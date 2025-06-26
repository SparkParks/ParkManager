package network.palace.parkmanager.commands.vqueue;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.ChatColor;

import java.util.logging.Level;

/**
 * Represents a command for removing a virtual queue hosted on the server.
 * <p>
 * The {@code RemoveCommand} is used to delete an existing virtual queue by its unique identifier.
 * This command ensures that the queue meets specific criteria before removal:
 * <ul>
 *   <li>The queue must exist and be identified by a valid ID.</li>
 *   <li>The queue must be hosted on the current server.</li>
 *   <li>The queue must be closed before it can be removed.</li>
 * </ul>
 *
 * <p>
 * <strong>Inputs:</strong>
 * <ul>
 *   <li>The command expects at least one argument: the unique ID of the virtual queue to be removed.</li>
 * </ul>
 *
 * <p>
 * <strong>Error Handling:</strong>
 * <ul>
 *   <li>If no ID is provided, an error message prompts the user to supply a queue ID.</li>
 *   <li>If the specified queue does not exist, an error message is displayed indicating invalid ID.</li>
 *   <li>If the queue is hosted on a different server, the command notifies the user with appropriate details.</li>
 *   <li>If the queue is still open, users are informed that it must be closed for removal.</li>
 *   <li>If an error occurs during the removal process, detailed error information is logged to the console.</li>
 * </ul>
 *
 * <p>
 * <strong>Queue Updates:</strong>
 * <ul>
 *   <li>Upon successful removal, queue signage elements (if present) are updated to indicate removal.</li>
 * </ul>
 *
 * <p>
 * This command is designed to streamline the process of managing virtual queues while ensuring data consistency.
 */
@CommandMeta(description = "Remove a virtual queue hosted on this server")
public class RemoveCommand extends CoreCommand {

    /**
     * Constructs a new {@code RemoveCommand} instance.
     *
     * <p>This constructor initializes the {@code RemoveCommand} with the alias "remove",
     * which serves as the command keyword for removing specific entities or items within the system.
     *
     * <p>The {@code RemoveCommand} is intended to handle functionality related to the removal
     * of objects, players, or data as defined by the specific implementation. Details about
     * what is removed and how it is processed are managed within the corresponding override
     * or handler implementation for this command class.
     *
     * <p>When executed, this command is expected to interact with the system state or
     * related entities to perform the requested removal operation in a safe and consistent manner.
     *
     * <p><strong>Command Alias:</strong> "remove"
     */
    public RemoveCommand() {
        super("remove");
    }

    /**
     * Handles the "remove" subcommand for virtual queues. This command removes a virtual queue
     * based on its ID, provided certain conditions are met, such as the queue being closed and
     * the command being executed on the hosting server.
     *
     * <p>
     * The command arguments and behavior are as follows:
     * <ul>
     * <li>If no arguments are provided, an error message will prompt the user to supply a queue ID.</li>
     * <li>If the queue ID is invalid or does not correspond to an existing queue, an error message
     *     will notify the user.</li>
     * <li>If the queue is still open or hosted by another server, it cannot be removed.</li>
     * <li>If the specified queue exists and meets the criteria, it will be removed, and any
     *     associated signs will be updated to reflect the removal.</li>
     * </ul>
     * </p>
     *
     * @param player The player who executed the command. This object is used to send feedback
     *               messages to the player.
     * @param args   The arguments provided with the command. The first argument should specify
     *               the ID of the queue to remove.
     * @throws CommandException If an error occurs while executing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/vqueue remove [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /vqueue list!");
            return;
        }
        VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueueById(args[0]);
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find a queue by id " + args[0] + "!");
            return;
        }
        if (!queue.isHost()) {
            player.sendMessage(ChatColor.RED + "You can only do that on the server hosting the queue (" +
                    ChatColor.GREEN + queue.getServer() + ChatColor.RED + ")!");
            return;
        }
        if (queue.isOpen()) {
            player.sendMessage(ChatColor.RED + "Virtual Queues must be closed before they can be removed!");
            return;
        }
        if (queue.getAdvanceSign() != null) {
            queue.getAdvanceSign().setLine(1, ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + queue.getId());
            queue.getAdvanceSign().update();
        }
        if (queue.getStateSign() != null) {
            queue.getStateSign().setLine(1, ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + queue.getId());
            queue.getStateSign().update();
        }
        try {
            ParkManager.getVirtualQueueManager().removeQueue(args[0]);
        } catch (Exception e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error removing virtual queue", e);
            player.sendMessage(ChatColor.RED + "An error occurred while removing that virtual queue, check console for details");
        }
    }
}
