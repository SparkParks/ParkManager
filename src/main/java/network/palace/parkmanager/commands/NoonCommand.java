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
 * The {@code NoonCommand} class provides functionality to set the time in a Minecraft world to noon.
 * This command can be executed by various command senders including {@link CPlayer},
 * {@link BlockCommandSender}, or a generic {@link CommandSender}.
 * <p>
 * The time is set to 6000 ticks, which corresponds to noon in the Minecraft world.
 * When executed, a message will be sent to the command sender indicating that the time has been updated.
 * </p>
 * <p>
 * Supported command senders:
 * <ul>
 *     <li>{@link CPlayer} - Updates the time to noon in the sender's current world.</li>
 *     <li>{@link BlockCommandSender} - Updates the time to noon in the world of the block acting as the command source.</li>
 *     <li>{@link CommandSender} - Sets the time to noon in all available worlds.</li>
 * </ul>
 * </p>
 * <p>
 * This command is annotated with {@link CommandMeta} to include metadata details such as description and rank restrictions.
 * The annotation specifies the command's accessibility to users with a rank of {@code Rank.CM} or higher.
 * </p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *     <li>Sets the world time to exactly noon (6000 ticks).</li>
 *     <li>Provides feedback messages to the command sender upon successful execution.</li>
 *     <li>Handles execution logic for different types of command senders.</li>
 * </ul>
 *
 * <h3>Execution Modes:</h3>
 * <ul>
 *     <li><strong>Player:</strong> The time is updated only in the player's current world.</li>
 *     <li><strong>Block Command Sender:</strong> The time is updated only in the world of the originating block.</li>
 *     <li><strong>Generic Command Sender:</strong> The time is updated in all loaded worlds.</li>
 * </ul>
 */
@CommandMeta(description = "Set time to noon", rank = Rank.CM)
public class NoonCommand extends CoreCommand {

    /**
     * Constructs a new {@code NoonCommand} instance.
     * <p>
     * This command is used to set the time in the game world to noon.
     * <p>
     * The command is registered with the keyword <b>"noon"</b>.
     */
    public NoonCommand() {
        super("noon");
    }

    /**
     * Handles the command to set the time to noon in the player's current world.
     *
     * <p>
     * This method sets the time to 6000 ticks (noon) in the player's current world
     * and sends a confirmation message to the player.
     * </p>
     *
     * @param player The {@link CPlayer} who executed the command. The time will be set
     *               in the world that the player currently resides in.
     * @param args   The arguments provided with the command. This parameter is not used
     *               in this implementation but is required for the method signature.
     * @throws CommandException If the command encounters an error during execution,
     *                          such as issues with player permissions or world access.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.getWorld().setTime(6000);
        player.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Noon " + ChatColor.GRAY + "in world " +
                ChatColor.GREEN + player.getWorld().getName() + ".");
    }

    /**
     * Handles the execution of a command by a {@link BlockCommandSender}.
     * This method sets the time of the block sender's world to noon (6000 ticks)
     * and sends a confirmation message to the sender.
     *
     * @param commandSender The {@link BlockCommandSender} that is executing the command.
     *                       Represents the block that issued the command.
     * @param args          The arguments passed with the command. Although unused in this implementation,
     *                       this parameter may contain additional data depending on the command's context.
     * @throws CommandException If there is an error during the command execution.
     */
    @Override
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        commandSender.getBlock().getWorld().setTime(6000);
        commandSender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Noon " + ChatColor.GRAY + "in world " +
                ChatColor.GREEN + commandSender.getBlock().getWorld().getName() + ".");
    }

    /**
     * Handles a command that sets the time to noon in all worlds on the server.
     * Sends a confirmation message to the command sender for each world where the time is changed.
     *
     * @param sender the entity sending the command (e.g., a player or console).
     * @param args the arguments passed with the command. Arguments are unused in this implementation.
     * @throws CommandException if an exception occurs while processing the command.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(6000);
            sender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Noon " + ChatColor.GRAY + "in world " +
                    ChatColor.GREEN + world.getName() + ".");
        }
    }
}
