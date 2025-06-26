package network.palace.parkmanager.commands.config;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Represents a command for setting or retrieving the message players receive when they join the server.
 *
 * <p>This command allows administrators to:
 * <ul>
 *   <li>View the current join message.</li>
 *   <li>Disable the join message by setting it to "none".</li>
 *   <li>Update the join message to a custom text, supporting alternate color codes via `&`.</li>
 * </ul>
 *
 * <p>The join message can be displayed in chat for all players upon joining the server.
 * If set to "none", no message will be shown to players when they join.
 *
 * <h2>Command Behavior:</h2>
 * <ul>
 *   <li>When executed with no arguments: Displays the current join message to the sender.</li>
 *   <li>When executed with the argument <code>none</code>: Disables the join message.</li>
 *   <li>When executed with other arguments: Sets a new join message with all provided arguments combined as text.</li>
 * </ul>
 */
@CommandMeta(description = "Set the message players are sent when they join")
public class JoinMessageCommand extends CoreCommand {

    /**
     * Constructor for the <code>JoinMessageCommand</code>, a sub-command for managing the customization
     * of the message that players see when they join the server.
     *
     * <p>This command is designed to provide server administrators with the ability to configure the
     * join message in the following ways:
     * <ul>
     *   <li>Display the current join message.</li>
     *   <li>Disable the join message by setting it to <code>"none"</code>.</li>
     *   <li>Set a new, custom join message using a given text with support for color codes.</li>
     * </ul>
     *
     * <p>The <code>JoinMessageCommand</code> is registered as a sub-command under higher-level
     * configuration commands, allowing for easier management of server settings related
     * to join events.
     */
    public JoinMessageCommand() {
        super("joinmessage");
    }

    /**
     * Handles the "join message" command logic, allowing the sender to view, disable, or update the join message displayed to players.
     *
     * <p>This method processes the input arguments and performs one of the following actions:
     * <ul>
     *   <li>If no arguments are provided, it displays the current join message to the sender.</li>
     *   <li>If the single argument "none" is provided, it disables the join message for the server.</li>
     *   <li>If one or more arguments are provided (excluding "none"), it sets a new join message using the provided text.</li>
     * </ul>
     *
     * <p>The join message may include alternate color codes, which can be specified via the `&` character.
     *
     * @param sender The entity or console executing the command. This can be a player or the console.
     * @param args The arguments provided with the command. These may include no arguments, "none", or a custom join message.
     *
     * @throws CommandException Thrown if an unexpected error occurs during command processing.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            String message = ParkManager.getConfigUtil().getJoinMessage();
            if (message.equals("none")) {
                sender.sendMessage(ChatColor.GREEN + "This server has no join message!");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Current join message:");
                sender.sendMessage(message);
            }
            return;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("none")) {
            ParkManager.getConfigUtil().setJoinMessage("none");
            sender.sendMessage(ChatColor.RED + "Disable the join message for this server!");
            return;
        }
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            msg.append(args[i]);
            if (i < (args.length - 1)) msg.append(" ");
        }
        String message = ChatColor.translateAlternateColorCodes('&', msg.toString());
        ParkManager.getConfigUtil().setJoinMessage(message);
        sender.sendMessage(ChatColor.GREEN + "Set the join message to:");
        sender.sendMessage(message);
    }
}
