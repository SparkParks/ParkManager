package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

/**
 * The {@code NightVisionCommand} class is a command implementation that toggles
 * the Night Vision potion effect for a player. It allows a player with the
 * appropriate command permission to enable or disable the Night Vision effect
 * on themselves.
 *
 * <p>
 * <b>Command Metadata:</b>
 * <ul>
 *  <li><b>Description:</b> Provides the ability to toggle Night Vision status for the player.</li>
 *  <li><b>Rank Required:</b> Trainee</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>When the command is executed:</b>
 * <lu>
 *  <li>If the player currently has the Night Vision potion effect, it will be removed,
 *  and they will be notified via a message.</li>
 *  <li>If the player does not have the Night Vision potion effect, it will be applied
 *  for an extended duration with no ambient particle effects, and they will be notified via a message.
 * .</li>
 * </lu>
 * </p>
 *
 * <p>
 * The command is initialized with the alias {@code "nv"} and can be executed by players
 * who meet the rank requirement specified in the {@code @CommandMeta} annotation.
 * </p>
 *
 * <p>
 * If the command execution encounters an error, an appropriate {@code CommandException}
 * will be thrown.
 * </p>
 */
@CommandMeta(description = "Night vision", rank = Rank.TRAINEE)
public class NightVisionCommand extends CoreCommand {

    /**
     * Constructs a new {@code NightVisionCommand} instance.
     *
     * <p>
     * This constructor initializes the command with the alias {@code "nv"}.
     * The {@code NightVisionCommand} is designed to toggle the Night Vision potion
     * effect on the executing player. The alias enables concise execution of the
     * command by authorized users.
     * </p>
     *
     * <p>
     * The {@code NightVisionCommand} requires players to meet the rank
     * requirement, as defined in the {@link CommandMeta} annotation.
     * </p>
     */
    public NightVisionCommand() {
        super("nv");
    }

    /**
     * Handles the execution of the Night Vision command.
     * Toggles the Night Vision potion effect on the player who executes the command.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *  <li>If the player has the Night Vision potion effect, it will be removed,
     *  and a corresponding message will be sent to the player.
     *  </li>
     *  <li>If the player does not have the Night Vision potion effect, it will be applied
     *  for an extended duration with no ambient particle effects, and a corresponding message
     *  will be sent to the player.
     *  </li>
     * </ul>
     * </p>
     *
     * @param player the player executing the command. Must not be {@code null}.
     * @param args any additional arguments passed with the command. Can be empty or {@code null}.
     * @throws CommandException if the command execution encounters an error, such as insufficient permissions or invalid arguments.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Collection<PotionEffect> effects = player.getActivePotionEffects();
        boolean contains = false;
        for (PotionEffect e : effects) {
            if (e.getType().equals(PotionEffectType.NIGHT_VISION)) {
                contains = true;
                break;
            }
        }
        if (contains) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendMessage(ChatColor.GRAY + "You no longer have Night Vision!");
        } else {
            PotionEffect effect = new PotionEffect(PotionEffectType.NIGHT_VISION, 200000, 0, true, false);
            player.addPotionEffect(effect);
            player.sendMessage(ChatColor.GRAY + "You now have Night Vision!");
        }
    }
}
