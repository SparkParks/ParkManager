package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;

/**
 * <p>A command implementation that allows players to heal themselves, another player, or
 * all players on the server. The healing process restores the player's health, food level,
 * extinguishes any active fire, and removes all active potion effects.</p>
 *
 * <p>This command is restricted to players with at least {@link Rank#TRAINEE} rank as
 * specified in the {@code @CommandMeta} annotation.</p>
 *
 * <h3>Command Syntax</h3>
 * <ul>
 *   <li><b>/heal:</b> Heals the player who issued the command.</li>
 *   <li><b>/heal &lt;target&gt;:</b> Heals the specified target player. The target can be a specific
 *   player's username or <code>**</code> to heal all online players.</li>
 * </ul>
 *
 * <h3>Features</h3>
 * <ul>
 *   <li>Restores the health of the player(s) to their maximum health value.</li>
 *   <li>Resets the food level of the player(s) to full (20).</li>
 *   <li>Extinguishes any fire effects on the player(s).</li>
 *   <li>Clears all active potion effects from the player(s).</li>
 *   <li>Sends a confirmation message to both the healed player(s) and the command sender.</li>
 * </ul>
 *
 * <p>The command seamlessly supports execution by both players and non-player command senders,
 * such as the server console. If the target player is not found, or the proper arguments are
 * not provided, the sender is notified with an appropriate message.</p>
 */
@CommandMeta(description = "Heal a player", rank = Rank.TRAINEE)
public class HealCommand extends CoreCommand {

    /**
     * A command class that allows players or command senders to issue a "heal" command.
     * <p>
     * The "heal" command provides functionality to restore the health of players or entities.
     * </p>
     * <p>
     * This constructor initializes the command with the specified command name.
     * </p>
     * <ul>
     *     <li>It is intended to be executed by players or command senders who have the ability to issue healing commands.</li>
     *     <li>The constructor sets the underlying structure for handling the "heal" command.</li>
     * </ul>
     */
    public HealCommand() {
        super("heal");
    }

    /**
     * Handles the command logic to heal players. Depending on the input arguments, this method
     * will either heal a specific player or multiple players.
     *
     * <p>If arguments are provided, the method attempts to heal the specified player.
     * If no arguments are provided, it heals the executing player.</p>
     *
     * @param player The <code>CPlayer</code> executing or associated with the command.
     * @param args   An array of <code>String</code> arguments. The first argument, if present,
     *               should specify the target player to heal. If the argument is omitted, the
     *               executing player is healed.
     *
     * @throws CommandException If the command cannot be executed or fails due to invalid input.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length > 0) {
            heal(player.getBukkitPlayer(), args[0]);
        } else {
            healPlayers(player);
        }
    }

    /**
     * Handles the "heal" command when issued without specifying a player context.
     * <p>
     * If command arguments are provided, it attempts to heal the specified player.
     * If no arguments are provided, the method sends a usage message to the sender.
     * </p>
     *
     * @param sender The entity (player, console, or other) that executed the command.
     * @param args   An array of <code>String</code> arguments associated with the command.
     *               <ul>
     *                   <li>If the first argument specifies a player's name, the target player is healed.</li>
     *                   <li>If no arguments are provided, the sender is informed of the correct usage.</li>
     *               </ul>
     *
     * @throws CommandException If the command execution encounters an issue, such as invalid input or internal errors.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            heal(sender, args[0]);
        } else {
            sender.sendMessage(ChatColor.RED + "/heal [target]");
        }
    }

    /**
     * Heals a single player or all online players based on the provided input.
     *
     * <p>If the string parameter is equal to <code>"**"</code>, all online players
     * are healed. Otherwise, the method attempts to find and heal the specified player
     * by name.</p>
     *
     * <ul>
     *     <li>Players healed will have their health, food level, and status effects restored.</li>
     *     <li>If the specified player is not found, the sender is notified with an error message.</li>
     *     <li>Messages are sent to inform both the sender and the healed players about the action.</li>
     * </ul>
     *
     * @param sender The entity or console that issued the heal command. Must be
     *               capable of receiving messages.
     * @param s      A string representing either the player to heal or the keyword
     *               <code>"**"</code> to heal all online players.
     */
    private void heal(CommandSender sender, String s) {
        if (s.equals("**")) {
            healPlayers(Core.getPlayerManager().getOnlinePlayers().toArray(new CPlayer[0]));
            sender.sendMessage(ChatColor.GRAY + "Healed all players!");
        } else {
            CPlayer tp = Core.getPlayerManager().getPlayer(s);
            if (tp == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            healPlayers(tp);
            sender.sendMessage(ChatColor.GRAY + "You healed " + tp.getName());
        }
    }

    /**
     * Heals the specified players by restoring their health, food level, and
     * removing all active potion effects. Additionally, any fire ticks are reset.
     *
     * <p>Each player receives a system message notifying them that they have been healed.</p>
     *
     * <ul>
     *   <li>Player health is set to their maximum health based on their attributes.</li>
     *   <li>The food level is restored to maximum (20).</li>
     *   <li>All potion effects applied to the players are removed.</li>
     *   <li>If a player is on fire, their fire ticks are reset.</li>
     * </ul>
     *
     * @param players The array of <code>CPlayer</code> objects to heal. Can include
     *                multiple players or none. If <code>null</code> values are
     *                present in the array, they are ignored.
     */
    private void healPlayers(CPlayer... players) {
        Arrays.asList(players).forEach(p -> {
            if (p == null) return;
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            p.setFoodLevel(20);
            p.getBukkitPlayer().getMaxFireTicks();
            p.setFireTicks(0);
            for (PotionEffect effect : p.getActivePotionEffects()) {
                p.removePotionEffect(effect.getType());
            }
            p.sendMessage(ChatColor.GRAY + "You have been healed.");
        });
    }
}