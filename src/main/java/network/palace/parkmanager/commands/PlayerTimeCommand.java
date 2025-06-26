package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The {@code PlayerTimeCommand} class represents a command that allows server operators
 * or authorized players to modify the in-game time visible to a specified player or themselves.
 * This includes setting the time to specific pre-defined values, custom values, or resetting
 * it to match the server's actual time.
 * <p>
 * This command is registered under the alias "ptime" and can be used by players with the
 * appropriate rank.
 * <p>
 * <b>Command Syntax:</b>
 * <ul>
 *     <li><code>/ptime [day/noon/night/1000/reset] [Username]</code></li>
 * </ul>
 *
 * <p>
 * <b>Expected Arguments:</b>
 * <ul>
 *     <li><b>time</b>: Specifies the in-game time for the player. This can be:
 *         <ul>
 *             <li><code>day</code> - Sets the player's time to "day".</li>
 *             <li><code>noon</code> - Sets the player's time to "noon".</li>
 *             <li><code>night</code> - Sets the player's time to "night".</li>
 *             <li>Custom numerical value (e.g., <code>1000</code>).</li>
 *             <li><code>reset</code> - Resets the player's time to match the server time.</li>
 *         </ul>
 *     </li>
 *     <li><b>Username</b> (optional): Specifies the target player's username. If omitted,
 *     the command applies to the player executing the command.</li>
 * </ul>
 *
 * <p>
 * <b>Features:</b>
 * <ul>
 *     <li>Allows both pre-defined times and custom numeric time values.</li>
 *     <li>Supports resetting player-specific times to match the server time.</li>
 *     <li>Handles cases where the target player is not found.</li>
 * </ul>
 *
 * <p>
 * <b>Permissions:</b>
 * The command uses a rank-based permission system defined by the associated {@code Rank.CM}.
 *
 * <p>
 * <b>Implementation Notes:</b>
 * The command processes input arguments to determine the desired action and provides
 * feedback messages based on the outcome of the requested operation. If no target is
 * specified, the command defaults to applying the action to the command executor.
 */
@CommandMeta(description = "Set player time", rank = Rank.CM)
public class PlayerTimeCommand extends CoreCommand {

    /**
     * Constructs a new PlayerTimeCommand instance and initializes it with the command name "ptime".
     *
     * <p>This command allows players or command senders to manipulate the time setting for individual players.
     * It is part of the game's custom command framework and is intended to handle time-related operations
     * specific to player experiences.</p>
     *
     * <p><b>Note:</b> This command should properly interact with the game's player and time management systems to
     * ensure expected behavior during execution.</p>
     */
    public PlayerTimeCommand() {
        super("ptime");
    }

    /**
     * Handles the execution of a time-related player command, allowing players to modify their own
     * in-game time or the in-game time of a specified target player.
     *
     * <p>This method validates the input arguments and invokes the appropriate logic to set or reset
     * the target player's time using the internal {@code setPlayerTime} method.</p>
     *
     * <p>Supported time arguments include:</p>
     * <ul>
     *   <li><strong>day</strong> - Set the time to day (1000 ticks).</li>
     *   <li><strong>noon</strong> - Set the time to noon (6000 ticks).</li>
     *   <li><strong>night</strong> - Set the time to night (16000 ticks).</li>
     *   <li><strong>reset</strong> - Reset the player's time to match the server.</li>
     *   <li><strong>[ticks]</strong> - Set the time to the specified tick value.</li>
     * </ul>
     *
     * <p>If the target player argument is not provided, the time change is applied to the player
     * executing the command.</p>
     *
     * <p>If the specified target player does not exist, an error message is displayed to the
     * command sender.</p>
     *
     * @param player  The {@link CPlayer} executing the command. Cannot be {@code null}.
     * @param args    An array of {@link String} arguments provided with the command:
     *                <ul>
     *                  <li><strong>args[0]</strong>: The time argument (e.g., "day", "noon", "reset", or a tick value).</li>
     *                  <li><strong>args[1]</strong> (optional): The username of the target player.</li>
     *                </ul>
     *                Must not be {@code null}.
     *
     * @throws CommandException If an error occurs during command execution. Specific conditions,
     * such as invalid arguments or player not found, result in user-friendly messages
     * being sent to the command sender.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/ptime [day/noon/night/1000/reset] [Username]");
            return;
        }
        if (args.length < 2) {
            setPlayerTime(player.getBukkitPlayer(), player, args[0]);
        } else {
            CPlayer target = Core.getPlayerManager().getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            setPlayerTime(player.getBukkitPlayer(), target, args[0]);
        }
    }

    /**
     * Handles an unspecific command for setting a player's time environment.
     * <p>
     * This command allows setting the time for a specific player using predefined options
     * such as "day", "noon", "night", "reset", or a custom time value. If the command is malformed
     * or the target player is invalid, the sender will receive an error message.
     * </p>
     *
     * @param sender The sender of the command, which could be either a player or the console.
     * @param args   The arguments provided for the command, where:
     *               <ul>
     *                 <li>args[0] represents the time value (e.g., "day", "noon", "night", "1000", or "reset").</li>
     *                 <li>args[1] represents the target player's username whose time is being modified.</li>
     *               </ul>
     * @throws CommandException If any error occurs while processing the command.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/ptime [day/noon/night/1000/reset] [Username]");
            return;
        }
        CPlayer target = Core.getPlayerManager().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        setPlayerTime(sender, target, args[0]);
    }

    /**
     * Sets the in-game time for a specific player or resets their time to match the server.
     *
     * <p>This method supports setting predefined times such as "day", "noon", or "night", as well as
     * custom tick-based times and resetting the player's time to match the global server time.</p>
     *
     * <p>Upon successful execution:</p>
     * <ul>
     *   <li>The target player's in-game time is modified according to the provided value.</li>
     *   <li>A confirmation message is sent to the command sender.</li>
     * </ul>
     *
     * <p>If the provided time value is invalid, an error message is sent to the command sender.</p>
     *
     * @param sender The entity executing the command. This can be a {@link Player} or the console.
     *               If the sender is a {@link Player} and they target themselves, the confirmation
     *               message will reference the sender appropriately.
     *
     * @param target The {@link CPlayer} whose in-game time is to be modified. Cannot be {@code null}.
     *
     * @param s The time argument provided by the sender. Acceptable values include:
     *          <ul>
     *            <li><strong>"day"</strong>: Sets the time to 1000 ticks.</li>
     *            <li><strong>"noon"</strong>: Sets the time to 6000 ticks.</li>
     *            <li><strong>"night"</strong>: Sets the time to 16000 ticks.</li>
     *            <li><strong>"reset"</strong>: Resets the player's time to match the server.</li>
     *            <li><strong>Tick value (e.g., "1000")</strong>: Sets the time to a specific tick-based value.</li>
     *          </ul>
     *          If an invalid value is provided, an error message is displayed to the sender.
     */
    private void setPlayerTime(CommandSender sender, CPlayer target, String s) {
        boolean same = (sender instanceof Player) && ((Player) sender).getUniqueId().equals(target.getUniqueId());
        long time;
        switch (s.toLowerCase()) {
            case "day": {
                time = 1000;
                break;
            }
            case "noon": {
                time = 6000;
                break;
            }
            case "night": {
                time = 16000;
                break;
            }
            case "reset": {
                time = -1;
                break;
            }
            default: {
                try {
                    time = Long.parseLong(s);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "/ptime [day/noon/night/1000/reset] [Username]");
                    return;
                }
                break;
            }
        }
        if (time == -1) {
            target.getBukkitPlayer().resetPlayerTime();
            sender.sendMessage(ChatColor.DARK_AQUA + (same ? "Your" : (target.getName() + "'s")) + ChatColor.GREEN +
                    " time now matches the server.");
        } else {
            target.getBukkitPlayer().setPlayerTime(time, false);
            sender.sendMessage(ChatColor.DARK_AQUA + (same ? "Your" : (target.getName() + "'s")) + ChatColor.GREEN +
                    " time has been set to " + ChatColor.DARK_AQUA + time + ChatColor.GREEN + "!");
        }
    }
}
