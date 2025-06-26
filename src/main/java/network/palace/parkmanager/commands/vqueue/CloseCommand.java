package network.palace.parkmanager.commands.vqueue;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.message.UpdateQueuePacket;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.ChatColor;

import java.util.logging.Level;

/**
 * Represents the command to close a virtual queue hosted on the current server.
 *
 * <p>This class extends {@link CoreCommand} and is responsible for handling the "close" command,
 * which is used to close a specific virtual queue based on its ID. The command requires the
 * ID of the queue to be specified as an argument and validates conditions before performing the
 * action.
 *
 * <p><b>Command Usage:</b>
 * <ul>
 *   <li><code>/vqueue close [id]</code>: Closes the virtual queue with the specified ID.</li>
 * </ul>
 *
 * <p><b>Behavior:</b>
 * <ul>
 *   <li>If the ID argument is missing, a warning message will be shown to the player.</li>
 *   <li>If the specified virtual queue ID cannot be found, a message indicating this will be sent to the player.</li>
 *   <li>If the queue is not hosted on the server where the command is executed, an error message will be shown.</li>
 *   <li>If the queue is already closed, the command will inform the player and take no further action.</li>
 *   <li>When successfully closed, an update notification will be sent to other servers in the network, and a message
 *       will be broadcasted to staff members.</li>
 * </ul>
 *
 * <p><b>Error Handling:</b>
 * <ul>
 *   <li>If an exception occurs while closing the queue, the error will be logged, and the player will receive
 *       an error message indicating the issue.</li>
 * </ul>
 *
 * <p>This command is intended for use by authorized players or staff with sufficient permissions to manage
 * virtual queues on the server.
 */
@CommandMeta(description = "Close a virtual queue hosted on this server")
public class CloseCommand extends CoreCommand {

    /**
     * Constructs a new CloseCommand instance to handle the "close" command functionality.
     * <p>
     * This command is designed to manage the closure of a virtual queue within the system.
     * The exact implementation details and command handling are defined in the
     * overridden method in the appropriate context.
     * <p>
     * <b>Features:</b>
     * <ul>
     *   <li>Initialization of the command with the identifier "close".</li>
     *   <li>Acts as part of the command framework to modify the state of virtual queues.</li>
     * </ul>
     */
    public CloseCommand() {
        super("close");
    }

    /**
     * Handles the command to close a virtual queue by its ID. Ensures the queue exists,
     * is hosted on the correct server, and is currently open before initiating the close action.
     * Sends appropriate feedback to the player in case of any issues.
     *
     * <p>This method updates the state of the targeted queue, propagates the update to all
     * parks via an {@link UpdateQueuePacket}, and notifies staff of the closure.</p>
     *
     * @param player The {@link CPlayer} who issued the command. This represents the user attempting to close the queue.
     * @param args A {@link String} array containing the command arguments. The first argument must be the queue ID.
     *             If no arguments are provided or if the queue ID is invalid, appropriate feedback is sent to the player.
     *
     * @throws CommandException If the command encounters issues during execution, such as problems
     *                          propagating the queue state update.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/vqueue close [id]");
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
        if (!queue.isOpen()) {
            player.sendMessage(ChatColor.RED + "This queue is already closed!");
            return;
        }
        queue.setOpen(false);
        try {
            Core.getMessageHandler().sendMessage(new UpdateQueuePacket(queue.getId(), queue.isOpen(), null), Core.getMessageHandler().permanentClients.get("all_parks"));
            Core.getMessageHandler().sendStaffMessage(ChatColor.GREEN + "A virtual queue (" + queue.getName() +
                    ChatColor.GREEN + ") has been " + (queue.isOpen() ? "opened" : "closed"));
        } catch (Exception e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error removing virtual queue", e);
            player.sendMessage(ChatColor.RED + "An error occurred while closing that virtual queue, check console for details");
        }
    }
}
