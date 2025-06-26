package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Represents the command to broadcast messages to the entire server.
 * <p>
 * The <code>BroadcastCommand</code> enables players with the required rank (e.g., <code>Rank.CM</code>)
 * to send server-wide broadcast messages. These messages are formatted with a prefix and can include
 * color codes for customization.
 * <p>
 * By design, the command ensures that only appropriate users can access this feature
 * and provides feedback when the command parameters are incorrect.
 *
 * <p><b>Features:</b></p>
 * <ul>
 *  <li>Sends a server-wide broadcast message formatted with a prefix.</li>
 *  <li>Supports color codes using the '&' character for enhanced message formatting.</li>
 *  <li>Restricted to users with a <code>Rank.CM</code> or higher.</li>
 * </ul>
 *
 * <p><b>Command Details:</b></p>
 * <ul>
 *  <li><b>Command Alias:</b> "bc".</li>
 *  <li><b>Permissions:</b> Users must have at least <code>Rank.CM</code> to execute this command.</li>
 * </ul>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *  <li>If no message is provided, players will receive an error message prompting proper usage.</li>
 * </ul>
 *
 * <p><b>Formatting:</b></p>
 * The broadcast message includes the "[Information]" prefix followed by the user’s message in green text.
 * Color codes are supported using the '&' character and are automatically converted.
 */
@CommandMeta(description = "Broadcast to the server", rank = Rank.CM)
public class BroadcastCommand extends CoreCommand {

    /**
     * Constructs a new BroadcastCommand instance.
     * <p>
     * The <code>BroadcastCommand</code> is associated with the "bc" command string and is designed
     * to send server-wide broadcast messages to all players. The command is restricted to users
     * with the appropriate rank, specifically <code>Rank.CM</code> or higher.
     *
     * <p><b>Features:</b></p>
     * <ul>
     *  <li>Sends a broadcast message globally to all players on the server.</li>
     *  <li>Messages are formatted with a prefix to indicate that they are announcements.</li>
     *  <li>Supports color codes using the '&' symbol to enhance message readability and style.</li>
     *  <li>Restricts command execution to users with <code>Rank.CM</code> or higher.</li>
     * </ul>
     *
     * <p><b>Command Details:</b></p>
     * <ul>
     *  <li><b>Command Alias:</b> "bc".</li>
     *  <li><b>Permissions:</b> The command can only be executed by users with at least
     *      <code>Rank.CM</code>.</li>
     * </ul>
     *
     * <p><b>Error Handling:</b></p>
     * <ul>
     *  <li>If the command is executed without any message arguments, the user receives an error
     *      message indicating the correct usage.</li>
     * </ul>
     *
     * <p><b>Design:</b></p>
     * The broadcast message uses a consistent format that includes the "[Information]" prefix
     * and the message in a distinct color format. Color codes within the message are converted
     * to their visual representation using the '&' delimiter.
     */
    public BroadcastCommand() {
        super("bc");
    }

    /**
     * Handles the execution logic of an unspecified command version, providing
     * support for broadcasting a custom message to all players on the server.
     * <p>
     * This method validates the provided arguments and broadcasts a global message
     * if the arguments meet the criteria. It ensures that the message is correctly
     * formatted, supports color codes using the '&' symbol, and includes a prefixed
     * "[Information]" tag to provide context for the broadcast.
     * <p>
     * <b>Error Handling:</b>
     * <ul>
     *   <li>If the method is executed with zero arguments (<code>args.length &lt; 1</code>), it sends
     *   an error message back to the sender indicating correct usage in red text.</li>
     * </ul>
     *
     * @param sender the entity or player sending the command. It represents the command's executor.
     *               Commonly, this includes players or console executors.
     * @param args   the arguments provided with the command, representing parts of the broadcast
     *               message. These arguments are concatenated to form the final global message.
     * @throws CommandException if there is an issue processing the command. Specific causes
     *         vary based on command restrictions or runtime behavior.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/bc [Message]");
            return;
        }
        StringBuilder message = new StringBuilder();
        for (String s : args) {
            message.append(s).append(" ");
        }
        Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.AQUA + "Information" + ChatColor.WHITE + "] " +
                ChatColor.GREEN + ChatColor.translateAlternateColorCodes('&', message.toString().trim()));
    }
}
