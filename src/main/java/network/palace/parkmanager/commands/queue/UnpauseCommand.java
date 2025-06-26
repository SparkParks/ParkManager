package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;

/**
 * The {@code UnpauseCommand} class is a command used to resume the movement of a paused queue
 * within a specified park.
 * <p>
 * This command is executed by players or block-based command senders and requires the appropriate
 * queue ID to identify the queue to be unpaused. The command verifies if the sender is located inside
 * a park and if the specified queue exists before proceeding to unpause it.
 * </p>
 *
 * <p><b>Usage:</b></p>
 * <ul>
 *   <li>Command syntax: {@code /queue unpause [id]}</li>
 *   <li><b>[id]</b> - The unique identifier of the queue to be unpaused.</li>
 * </ul>
 *
 * <p><b>Command Execution for a Player:</b></p>
 * <ul>
 *   <li>If arguments are insufficient, an error message informs the user of the correct syntax and
 *       indicates how to obtain the queue ID.</li>
 *   <li>Checks if the player is within a park; if not, the command returns a failure message.</li>
 *   <li>Validates the queue ID against the specified park. If invalid, an error is displayed.</li>
 *   <li>Unpauses the identified queue and notifies the player with a success message.</li>
 * </ul>
 *
 * <p><b>Command Execution for a Block-Based Command Sender:</b></p>
 * <ul>
 *   <li>Similar to player execution, the sender must provide a valid queue ID and ensure the block
 *       is within a park.</li>
 *   <li>Queue validation and unpausing follow the same steps as for players.</li>
 *   <li>A success message indicating the status of the unpaused queue is sent back as feedback.</li>
 * </ul>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *   <li>Displays an error if insufficient arguments are provided.</li>
 *   <li>Ensures the sender is located inside a valid park, returning an error if not.</li>
 *   <li>Validates the queue ID to ensure it corresponds to a queue in the park, otherwise returns
 *       an error.</li>
 * </ul>
 */
@CommandMeta(description = "Un-pause movement of the queue")
public class UnpauseCommand extends CoreCommand {

    /**
     * <p>
     * The <code>UnpauseCommand</code> constructor initializes a new instance of the
     * command and sets the base name as "unpause". This command is used to handle
     * functionality related to unpausing a specific operation or state.
     * </p>
     *
     * <p>
     * It inherits relevant behavior and structure from its superclass, enabling
     * the command to integrate with the existing command handling system.
     * </p>
     *
     * <p>
     * Additional logic or configuration for the specific command behavior
     * is implemented in its associated methods.
     * </p>
     */
    public UnpauseCommand() {
        super("unpause");
    }

    /**
     * Handles the "unpause queue" command, allowing players to unpause a specific queue within a park.
     *
     * <p>This method processes the command arguments to unpause the queue with the specified ID.
     * The command must be executed from within a park and requires a valid queue ID.</p>
     *
     * <p>Players are provided feedback if:
     * <ul>
     *   <li>No arguments are passed.</li>
     *   <li>The player is not in a park.</li>
     *   <li>The specified queue ID does not exist.</li>
     * </ul>
     * </p>
     *
     * @param player The player executing the command. Must be actively in the game and must be located within a park.
     * @param args An array of strings representing command arguments. The first element should be the queue ID to unpause.
     * @throws CommandException If an error occurs while handling the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/queue unpause [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /queue list!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Queue queue = ParkManager.getQueueManager().getQueueById(args[0], park.getId());
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find a queue by id " + args[0] + "!");
            return;
        }
        queue.setPaused(false);
        player.sendMessage(queue.getName() + ChatColor.YELLOW + " has been un-paused!");
    }

    /**
     * Handles the execution of the "unpause" queue command for the specified park and queue.
     * <p>
     * This method checks the provided arguments, verifies the sender's location within a park,
     * and attempts to un-pause a specific queue by its ID. If the command fails any checks, appropriate
     * error messages are sent to the command sender.
     *
     * @param sender The {@link BlockCommandSender} who executes the command. This must be a block-based command source.
     * @param args   The command arguments. The first argument is expected to represent the queue ID to un-pause.
     * <ul>
     * <li>If {@code args.length < 1}, informs the sender about incorrect usage with guidance on retrieving the queue ID.</li>
     * <li>If the sender is not located in a park, an error message is sent stating that they must be inside a park.</li>
     * <li>If no queue corresponds to the provided ID, informs the sender that no such queue exists in the context of the current park.</li>
     * </ul>
     * @throws CommandException If there is an error in executing the command. This exception may not be thrown directly
     *                          in the current implementation but could be utilized for deeper error handling in extensions.
     */
    @Override
    protected void handleCommand(BlockCommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/queue unpause [id]");
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /queue list!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(sender.getBlock().getLocation());
        if (park == null) {
            sender.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Queue queue = ParkManager.getQueueManager().getQueueById(args[0], park.getId());
        if (queue == null) {
            sender.sendMessage(ChatColor.RED + "Could not find a queue by id " + args[0] + "!");
            return;
        }
        queue.setPaused(false);
        sender.sendMessage(queue.getName() + ChatColor.YELLOW + " has been un-paused!");
    }
}
