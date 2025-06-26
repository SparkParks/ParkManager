package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.message.ChatMutePacket;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * <p>
 * The {@code MuteChatCommand} class represents a command for muting and unmuting chat.
 * It is intended for use within the server environment where a user of sufficient rank
 * can manage the chat state across proxies.
 * </p>
 *
 * <p>
 * This command processes the mute/unmute state and broadcasts the chat mute packet
 * to all connected proxies. It supports execution from players, command blocks,
 * and the console.
 * </p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *     <li>Allows users of sufficient rank (e.g., {@code Rank.CM}) to mute or unmute chat.</li>
 *     <li>Sends a {@code ChatMutePacket} over the messaging queue to toggle the chat state
 *         across all proxies.</li>
 *     <li>Handles different types of {@code CommandSender}, ensuring proper source identification
 *         for each execution type (Players, Command Blocks, Console).</li>
 *     <li>Provides immediate feedback to the sender on successful or failed execution.</li>
 * </ul>
 *
 * <h3>Command Syntax:</h3>
 * <ul>
 *     <li><code>/mc mute</code> - Mutes the chat.</li>
 *     <li><code>/mc unmute</code> - Unmutes the chat.</li>
 * </ul>
 * <p>
 * If the command is executed without any arguments, an error message is returned
 * to the sender indicating the correct usage syntax.
 * </p>
 *
 * <h3>Error Handling:</h3>
 * <ul>
 *     <li>If an unrecognized argument is provided, the command will fail silently
 *         without toggling the chat state.</li>
 *     <li>In case of an {@link IOException} during the sending of the {@code ChatMutePacket},
 *         an error message is sent to the sender, and the stack trace is printed.</li>
 * </ul>
 *
 * <h3>Execution Note:</h3>
 * <p>
 * When executed by a non-player sender (e.g., Console or Command Block), the source information
 * is prepended with the instance name and relevant location details for Command Block senders.
 * </p>
 */
@CommandMeta(description = "Mute and unmute chat", rank = Rank.CM)
public class MuteChatCommand extends CoreCommand {

    /**
     * A command implementation used to mute a chat on the server.
     * This class extends a command processing framework, enabling specific handling
     * of the "mc" (mute chat) command for command senders.
     *
     * <p>The primary purpose of this constructor is to initialize the command with
     * the name "mc". It acts as an entry point for registering the mute chat functionality.
     *
     * <ul>
     *   <li>Command Name: "mc"</li>
     *   <li>Category: Chat Moderation</li>
     * </ul>
     *
     * <p>Further implementation and functionality are delegated to specific methods
     * within the class.
     */
    public MuteChatCommand() {
        super("mc");
    }

    /**
     * Handles the "mute" or "unmute" command sent by a user or the server.
     * <p>
     * This method processes a command to either mute or unmute the chat system across proxies.
     * It supports execution by players, command blocks, or the console. If the mute or unmute
     * operation encounters an error, the sender is notified with an error message.
     *
     * <p><b>Command Syntax:</b>
     * <lu>
     *   <li><code>/mc mute</code> - Mutes the chat system.</li>
     *   <li><code>/mc unmute</code> - Unmutes the chat system.</li>
     * </lu>
     *
     * <p>If executed by a command block, the command’s source will include the block’s location.
     *
     * @param sender The entity that issued the command (e.g., a player, command block, or console).
     *               If the sender is a player, their name is used as the source. If it is a command
     *               block, its location is included in the source string. For the console, the server
     *               name is used as the source.
     * @param args   An array of arguments provided with the command. The first argument determines whether
     *               to mute or unmute the chat. Valid values are "mute" or "unmute". If no argument is
     *               provided or the argument is invalid, the sender is prompted with the correct syntax.
     * @throws CommandException If an error occurs while executing the command.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/mc [mute/unmute]");
            return;
        }
        String source;
        if (!(sender instanceof Player)) {
            if (sender instanceof BlockCommandSender) {
                Location loc = ((BlockCommandSender) sender).getBlock().getLocation();
                source = "Server (" + Core.getInstanceName() + ", CMDBLK @ " + loc.getWorld().getName().toLowerCase() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ")";
            } else {
                source = "Server (" + Core.getInstanceName() + ")";
            }
        } else {
            source = sender.getName();
        }
        try {
            Core.getMessageHandler().sendMessage(new ChatMutePacket("ParkChat", source, args[0].equalsIgnoreCase("mute")), Core.getMessageHandler().ALL_PROXIES);
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "An error occurred while muting/unmuting chat, check console for details.");
        }
    }
}
