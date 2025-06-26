package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.message.BroadcastPacket;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * Represents a command that broadcasts a message globally across the network.
 * <p>
 * The <code>BroadcastGlobalCommand</code> allows command senders to send a broadcast
 * message to all servers or connected proxies in the network. The message is prefixed
 * with the sender's identity for context. This command is primarily designed for
 * administrative use and is restricted by rank.
 * <ul>
 *  <li><b>Rank Restriction:</b> Accessible only by users with the <code>Rank.CM</code>.</li>
 *  <li><b>Command Aliases:</b> The command responds to the alias "b".</li>
 * </ul>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *  <li>Allows administrators to send messages to all players or servers globally.</li>
 *  <li>Supports message formatting codes to modify display styles (e.g., color codes).</li>
 *  <li>Automatically identifies the source of the broadcast (e.g., console, player, or command blocks).</li>
 *  <li>Utilizes <code>BroadcastPacket</code> to encapsulate and transmit broadcast data.</li>
 * </ul>
 *
 * <p><b>Command Execution:</b></p>
 * <p>
 * This command requires the sender to supply a message as an argument. If no message
 * is provided, an error message informing the correct usage is displayed.
 * </p>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *  <li>Notifies the sender if the message parameter is missing.</li>
 *  <li>Logs exceptions to the console in case of transmission errors.</li>
 *  <li>Sends an error message to the sender if a broadcast fails to send.</li>
 * </ul>
 *
 * <p><b>Dependencies:</b></p>
 * <ul>
 *  <li>Relies on the core messaging system to dispatch messages via <code>BroadcastPacket</code>.</li>
 *  <li>Utilizes the <code>CommandMeta</code> annotation to define metadata like command description and rank restrictions.</li>
 *  <li>Integrates with the <code>Core</code> framework to identify the server instance and sender context.</li>
 * </ul>
 */
@CommandMeta(description = "Broadcast to the network", rank = Rank.CM)
public class BroadcastGlobalCommand extends CoreCommand {

    /**
     * Represents the global command to broadcast messages or execute global actions.
     * <p>
     * The <code>BroadcastGlobalCommand</code> is associated with the "b" command and
     * is utilized for broadcasting globally or performing global actions that are not command-specific.
     * This command serves as a foundation for handling commands with global effects or reach.
     * <p>
     * While the primary functionality is overridden in subclass methods, the constructor
     * establishes the command identifier, ensuring it is associated with the "b" string.
     *
     * <p><b>Features:</b></p>
     * <ul>
     *  <li>Sets up the "b" command for global execution purposes.</li>
     *  <li>Serves as part of a potentially larger command infrastructure.</li>
     *  <li>Supports command-specific implementations by extending the base functionality for handling global tasks.</li>
     * </ul>
     *
     * <p><b>Command Association:</b></p>
     * <ul>
     *  <li><b>Command Identifier:</b> "b"</li>
     * </ul>
     *
     * <p><b>Dependencies:</b></p>
     * <ul>
     *  <li>Relies on the inherited command handling infrastructure.</li>
     * </ul>
     *
     * <p><b>Error Handling:</b></p>
     * <ul>
     *  <li>Execution and error handling are customized and implemented in the command-specific methods.</li>
     * </ul>
     */
    public BroadcastGlobalCommand() {
        super("b");
    }

    /**
     * Handles the execution of an unspecified broadcast command.
     * <p>
     * This method is used to process and send a broadcast message across all proxies.
     * It determines the source of the command based on the sender type, formats the message,
     * and sends it to the message handler for broadcasting. The command is structured
     * differently depending on whether the sender is a player, console, or command block.
     * <p>
     * <b>Message Formatting:</b>
     * <ul>
     *  <li>For players: The player's name is used as the sender.</li>
     *  <li>For the console: The sender is identified as "Console on [InstanceName]".</li>
     *  <li>For command blocks: Includes the world name and block coordinates as part of the sender
     *      information.</li>
     * </ul>
     * <p>
     * <b>Error Handling:</b>
     * <ul>
     *  <li>If arguments are insufficient, an error message is sent to the sender.</li>
     *  <li>In case of an exception (e.g., during broadcasting), an error message is sent to the sender,
     *      and the error stack trace is printed to the console.</li>
     * </ul>
     *
     * @param sender the entity executing the command (can be a {@link Player}, {@link BlockCommandSender},
     *               or console).
     * @param args   the arguments for the command, where the first argument is the message to broadcast.
     * @throws CommandException if there is an issue processing the command.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/b [Message]");
            return;
        }
        String message = String.join(" ", args);
        String source;
        if (!(sender instanceof Player)) {
            if (sender instanceof BlockCommandSender) {
                Location loc = ((BlockCommandSender) sender).getBlock().getLocation();
                source = "" + Core.getInstanceName() + ", CMDBLK @ " + loc.getWorld().getName().toLowerCase() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
            } else {
                source = "Console on " + Core.getInstanceName();
            }
        } else {
            source = sender.getName();
        }
        try {
            Core.getMessageHandler().sendMessage(new BroadcastPacket(source, ChatColor.translateAlternateColorCodes('&', message)), Core.getMessageHandler().ALL_PROXIES);
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "An error occurred while sending that broadcast, check console for details.");
        }
    }
}
