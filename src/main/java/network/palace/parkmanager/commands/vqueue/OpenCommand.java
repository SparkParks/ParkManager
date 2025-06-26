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
 * Command to open a virtual queue hosted on the server. This command allows players
 * to activate a queue identified by its unique ID. Once a queue is open, players can
 * interact with it through the queueing system.
 *
 * <p>When executed, this command will:</p>
 * <ul>
 *     <li>Check if the required queue ID is provided as an argument.</li>
 *     <li>Validate if the specified queue exists on the server.</li>
 *     <li>Ensure the queue is hosted on the current server.</li>
 *     <li>Confirm that the queue is not already open.</li>
 *     <li>Set the queue to an "open" state.</li>
 * </ul>
 *
 * <p>If the operation is successful, it broadcasts the updated state of the queue
 * to relevant message handlers and notifies the staff about the change.
 * In case of errors during execution, appropriate error messages will be
 * logged and sent to the initiating player.</p>
 *
 * <p>Usage of this command is dependent on specific server hosting and queue configurations.
 * Always ensure the queue identifier is obtained beforehand using relevant commands
 * such as <code>/vqueue list</code>.</p>
 *
 * <p><strong>Validation Notes:</strong></p>
 * <ul>
 *     <li>Queues must be hosted on the current server to be manipulated.</li>
 *     <li>ID should correspond to a valid, existing virtual queue.</li>
 * </ul>
 */
@CommandMeta(description = "Open a virtual queue hosted on this server")
public class OpenCommand extends CoreCommand {

    /**
     * Constructs a new {@code OpenCommand} instance.
     *
     * <p>This constructor initializes the {@code OpenCommand} with the alias "open",
     * which serves as the command keyword for opening specific functionality
     * or resources within the associated system.
     *
     * <p>The {@code OpenCommand} is intended to be used as part of the command
     * framework within the system. It should be extended to implement specific
     * behaviors when the "open" command is invoked.
     */
    public OpenCommand() {
        super("open");
    }

    /**
     * Handles the command to open a virtual queue. This method ensures the queue exists, verifies
     * the hosting server, checks the current state of the queue, and then opens the queue if applicable.
     * It also notifies the appropriate clients and staff members about the change.
     *
     * <p>In case of missing or invalid arguments, the method sends appropriate error messages to the
     * player. If an issue occurs during the process of opening the queue, an error message is sent, and
     * the exception is logged.</p>
     *
     * @param player The {@link CPlayer} who executed the command.
     * @param args   The command arguments passed by the player. Expected arguments:
     *               <ul>
     *                   <li><code>args[0]</code>: The ID of the virtual queue to be opened.</li>
     *               </ul>
     *               If no arguments are provided, or if the arguments are invalid, the method will notify
     *               the player with usage instructions or error messages.
     * @throws CommandException If an error occurs while processing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/vqueue open [id]");
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
            player.sendMessage(ChatColor.RED + "This queue is already open!");
            return;
        }
        queue.setOpen(true);
        try {
            Core.getMessageHandler().sendMessage(new UpdateQueuePacket(queue.getId(), queue.isOpen(), null), Core.getMessageHandler().permanentClients.get("all_parks"));
            Core.getMessageHandler().sendStaffMessage(ChatColor.GREEN + "A virtual queue (" + queue.getName() +
                    ChatColor.GREEN + ") has been " + (queue.isOpen() ? "opened" : "closed"));
        } catch (Exception e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error opening virtual queue", e);
            player.sendMessage(ChatColor.RED + "An error occurred while opening that virtual queue, check console for details");
        }
    }
}
