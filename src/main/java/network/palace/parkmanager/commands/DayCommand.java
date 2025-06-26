package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

/**
 * Represents a command that sets the time to day in one or more worlds.
 * <p>
 * The <code>DayCommand</code> adjusts the time to day (tick 1000) in a specific
 * world or across all worlds, depending on the command executor context. This command
 * can be executed by players, command blocks, or as an unspecified sender.
 * <p>
 * The command is restricted by rank and can only be executed by players or entities
 * with the <code>Rank.CM</code> permission.
 *
 * <ul>
 *  <li><b>Rank Restriction:</b> This command is restricted to users with the <code>Rank.CM</code>.</li>
 *  <li><b>Command Execution:</b> The valid command alias for this feature is "day".</li>
 * </ul>
 *
 * <p><b>Behaviors:</b></p>
 * <ul>
 *  <li>When executed by a player, it sets the time to day in the world the player is currently in.</li>
 *  <li>When executed by a command block, it sets the time to day in the command block's world.</li>
 *  <li>When executed by an unspecified sender, it sets the time to day in all loaded worlds on the server.</li>
 * </ul>
 *
 * <p><b>Feedback:</b></p>
 * <ul>
 *  <li>Displays a confirmation message to the executor indicating the world where the time was set to day.</li>
 *  <li>For global operations (unspecific sender), feedback is provided for each world affected.</li>
 * </ul>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *  <li>Relies on <code>CommandException</code> for handling potential command execution errors.</li>
 * </ul>
 */
@CommandMeta(description = "Set time to day", rank = Rank.CM)
public class DayCommand extends CoreCommand {

    /**
     * Constructs a new instance of the DayCommand.
     * <p>
     * The <code>DayCommand</code> is bound to the "day" command string, allowing
     * command executors to set the time to day (tick 1000) in a specific context.
     * This command is restricted to players or entities with the <code>Rank.CM</code>.
     * <p>
     * Upon execution, the behavior of the command varies based on the type of the sender:
     * <ul>
     *  <li><b>Player Sender:</b> Sets the time to day in the current world the player is in.</li>
     *  <li><b>Command Block Sender:</b> Sets the time to day in the command block's world.</li>
     *  <li><b>Unspecified Sender:</b> Sets the time to day in all loaded worlds on the server.</li>
     * </ul>
     *
     * <p><b>Features:</b></p>
     * <ul>
     *  <li>Provides confirmation messages to the executor about the time change in the affected world(s).</li>
     *  <li>For global executions (unspecified sender), feedback covers each affected world individually.</li>
     * </ul>
     *
     * <p><b>Error Handling:</b></p>
     * <ul>
     *  <li>Relies on <code>CommandException</code> for managing execution errors.</li>
     * </ul>
     */
    public DayCommand() {
        super("day");
    }

    /**
     * Handles the execution of the "day" command for a player.
     * <p>
     * This method sets the time to day (tick 1000) in the world the player is currently in.
     * Upon successful execution, a confirmation message is sent to the player indicating
     * the updated time and the name of the affected world.
     * <p>
     * The command is restricted to users with the appropriate rank (<code>Rank.CM</code>).
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Sets the time to day in the world the player is currently located.</li>
     *   <li>Notifies the player of the time update with appropriate feedback.</li>
     * </ul>
     *
     * <p><b>Error Handling:</b></p>
     * <ul>
     *   <li>Relies on <code>CommandException</code> to handle command execution errors.</li>
     * </ul>
     *
     * @param player the player executing the command. Represents the entity whose current world
     *               time will be updated to day.
     * @param args   the additional arguments passed with the command. Not utilized in this method.
     * @throws CommandException if there is an issue processing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.getWorld().setTime(1000);
        player.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Day " + ChatColor.GRAY + "in world " +
                ChatColor.GREEN + player.getWorld().getName() + ".");
    }

    /**
     * Handles the "day" command execution when triggered by a command block sender.
     * <p>
     * This method sets the time to day (tick 1000) in the world where the command block
     * is located. Upon successful execution, a confirmation message is sent to the command
     * block sender, including the updated time and the name of the affected world.
     * <p>
     * This command is specifically designed to handle scenarios where the sender is a
     * command block, rather than a player or other sender types.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Sets the time to day (tick 1000) in the command block's world.</li>
     *   <li>Sends a feedback message to the command block sender with the updated time
     *       and the world name.</li>
     * </ul>
     *
     * <p><b>Error Handling:</b></p>
     * <ul>
     *   <li>Relies on <code>CommandException</code> to handle errors during execution.</li>
     * </ul>
     *
     * @param commandSender the command block executing the command. Represents the entity
     *                      whose associated world's time will be updated to day.
     * @param args          additional arguments passed with the command. Not utilized in this method.
     * @throws CommandException if there is an issue processing the command.
     */
    @Override
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        commandSender.getBlock().getWorld().setTime(1000);
        commandSender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Day " + ChatColor.GRAY + "in world " +
                ChatColor.GREEN + commandSender.getBlock().getWorld().getName() + ".");
    }

    /**
     * Handles the execution of the "day" command in the context of an unspecified sender.
     * <p>
     * This method sets the time to day (tick 1000) in all loaded worlds on the server.
     * Upon successful execution, it sends confirmation messages to the sender for each world
     * individually, indicating the updated time and the name of the affected world.
     * <p>
     * <b>Features:</b>
     * <ul>
     *   <li>Sets the time to day in all loaded worlds.</li>
     *   <li>Provides detailed feedback to the sender about the change in each world.</li>
     * </ul>
     *
     * <p><b>Error Handling:</b></p>
     * <ul>
     *   <li>Throws <code>CommandException</code> if there is an issue during command execution.</li>
     * </ul>
     *
     * @param sender the entity that executed the command. Can represent a player, a command block, or the console.
     * @param args   the additional arguments passed with the command. Not utilized in this method.
     * @throws CommandException if an error occurs while processing the command.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(1000);
            sender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Day " + ChatColor.GRAY + "in world " +
                    ChatColor.GREEN + world.getName() + ".");
        }
    }
}
