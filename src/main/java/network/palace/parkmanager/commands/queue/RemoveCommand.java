package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;

/**
 * <p>The <code>RemoveCommand</code> class represents a command used to remove an existing queue from a park.
 * This command is executed by a player and requires the player to be within a park region while providing a
 * valid queue ID. The queue ID can be obtained using <code>/queue list</code>.</p>
 *
 * <p><b>Usage:</b></p>
 * <ul>
 *   <li><code>/queue remove [id]</code> - Removes the specified queue identified by <code>[id]</code>
 *       from the park the player is currently located in.</li>
 * </ul>
 *
 * <p><b>Conditions:</b></p>
 * <ul>
 *   <li>The player must be located within a valid park region to execute this command.</li>
 *   <li>The command requires at least one argument, which is the queue ID to be removed.</li>
 *   <li>If the provided queue ID does not match any existing queue, an error message is shown.</li>
 *   <li>Successful removal of a queue will display a confirmation message to the player.</li>
 *   <li>In case of removal failure due to an error, a failure message is displayed to the player.</li>
 * </ul>
 *
 * <p><b>Implementation Notes:</b></p>
 * <ul>
 *   <li>The class extends <code>CoreCommand</code>, inheriting core functionalities for command handling.</li>
 *   <li>Data for parks and queues is managed through the <code>ParkManager</code>, which facilitates park
 *       and queue lookups and modifications.</li>
 *   <li>Error handling is implemented to ensure the command runs safely and provides meaningful feedback
 *       to the player in case issues arise (e.g., missing arguments or invalid queue IDs).</li>
 * </ul>
 *
 * <p>This command is annotated with <code>@CommandMeta</code>, which provides metadata about its functionality.
 * Specifically, it is described as "Remove an existing queue".</p>
 */
@CommandMeta(description = "Remove an existing queue")
public class RemoveCommand extends CoreCommand {

    /**
     * Constructs a new {@code RemoveCommand} instance.
     * <p>
     * This command is used to remove a certain entity or perform a specific removal operation as defined in the
     * implementing class functionality.
     * </p>
     * <p>
     * The command keyword for this operation is set as "remove".
     * </p>
     */
    public RemoveCommand() {
        super("remove");
    }

    /**
     * Handles the command for removing a queue based on the provided queue ID.
     * <p>
     * This method is invoked when a player executes the appropriate remove queue command.
     * It performs the following actions:
     * <ul>
     * <li>Validates if the player has provided the required arguments.</li>
     * <li>Ensures that the player is inside a park.</li>
     * <li>Checks if a queue with the specified ID exists.</li>
     * <li>Attempts to remove the queue if it exists.</li>
     * <li>Sends feedback to the player about the success or failure of the operation.</li>
     * </ul>
     *
     * @param player The player executing the command.
     * @param args   The arguments provided with the command. The first argument must be the ID of
     *               the queue to remove.
     * @throws CommandException If an error occurs during command execution, such as invalid arguments or
     *                          unexpected issues during queue removal.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/queue remove [id]");
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
        if (ParkManager.getQueueManager().removeQueue(args[0], park.getId())) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + queue.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + queue.getName() + "!");
        }
    }
}
