package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The {@code ListCommand} class is responsible for listing all the virtual queues
 * managed within the system. It extends the {@code CoreCommand} class to provide
 * specific functionality for listing virtual queues to the sender.
 *
 * <p>This command outputs the details of each virtual queue including:
 * <ul>
 *   <li>The queue's unique identifier.</li>
 *   <li>The queue's name.</li>
 *   <li>The server on which the queue is active.</li>
 *   <li>The current state of the queue (open or closed).</li>
 *   <li>The number of members currently in the queue.</li>
 * </ul>
 *
 * <p>The information is displayed with specific formatting to enhance readability,
 * utilizing color codes for visual differentiation between various data points.
 *
 * <p><strong>Command Alias:</strong> "list"
 *
 * <p>This command should be executed by a {@code CommandSender}, and exceptions are
 * raised if command execution fails due to invalid state or input.
 */
@CommandMeta(description = "List all virtual queues")
public class ListCommand extends CoreCommand {

    /**
     * Constructs a new {@code ListCommand} instance.
     *
     * <p>This constructor initializes the {@code ListCommand} with the alias "list",
     * which serves as the command keyword for listing all currently managed virtual queues.
     *
     * <p>The command provides the sender with a detailed output of all the virtual queues,
     * including the following data points:
     * <ul>
     *   <li>Unique identifier of the queue.</li>
     *   <li>Name of the queue.</li>
     *   <li>The server hosting the queue.</li>
     *   <li>The current state of the queue (open or closed).</li>
     *   <li>The number of members currently in the queue.</li>
     * </ul>
     *
     * <p>The formatted output uses specific color codes to enhance readability and categorization of information.
     *
     * <p>This command is primarily intended for use by administrative or managing entities
     * within the system to monitor and manage virtual queues effectively.
     */
    public ListCommand() {
        super("list");
    }

    /**
     * Handles the 'list' command for displaying information about all active virtual queues.
     *
     * <p>This method retrieves all virtual queues managed by the system and sends detailed
     * information about each queue to the {@code CommandSender}. The message is formatted
     * with visual enhancements such as colors and styles to improve readability. The details
     * include the queue's unique identifier, name, server, status (open or closed), and the
     * number of players currently in the queue.
     *
     * @param sender the {@code CommandSender} who issued the command; this could be a player,
     *               console, or other entity able to send commands.
     * @param args the list of arguments provided with the command. This command does not expect
     *             specific arguments, but the array might still be populated.
     * @throws CommandException if an error occurs while executing the command, such as an invalid
     *                          state or failure in fetching queue information.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Virtual" + ChatColor.GREEN + " Queues:");
        for (VirtualQueue queue : ParkManager.getVirtualQueueManager().getQueues()) {
            sender.sendMessage(ChatColor.AQUA + "- [" + queue.getId() + "] " + ChatColor.YELLOW + queue.getName() +
                    ChatColor.GREEN + " on " + ChatColor.YELLOW + queue.getServer() + ChatColor.GREEN + " is " +
                    (queue.isOpen() ? ChatColor.GREEN + "open" : ChatColor.RED + "closed") + ChatColor.GREEN +
                    " with " + ChatColor.YELLOW + queue.getMembers().size() + " players" + ChatColor.GREEN + " in queue");
        }
    }
}
