package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;

/**
 * Represents a command to close an attraction in a park.
 * <p>
 * This command allows players to mark an attraction as closed within a park.
 * It also handles any linked queue associated with the attraction and updates their status accordingly.
 * The attraction's status and corresponding queue details are saved to their respective files after execution.
 * </p>
 *
 * <p>
 * <b>Command Structure:</b>
 * <ul>
 * <li>Command name: <code>close</code></li>
 * <li>Usage: <code>/attraction close [id]</code></li>
 * <li>The <code>[id]</code> parameter refers to the unique ID of the attraction to be closed.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Behavior:</b>
 * <ul>
 * <li>Validates the input parameters.</li>
 * <li>Ensures that the player is located within a valid park.</li>
 * <li>Marks the specified attraction as closed and updates its status in the system.</li>
 * <li>If the attraction has a linked queue, closes the queue and persists the updated information.</li>
 * <li>Sends appropriate feedback messages to the player for error handling or successful execution.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Parameter Details:</b>
 * <ul>
 * <li><code>player</code> - The player executing the command.</li>
 * <li><code>args</code> - The arguments passed with the command. Requires at least one argument specifying the attraction ID.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Error Scenarios:</b>
 * <ul>
 * <li>If no attraction ID is provided, a usage message is sent to the player.</li>
 * <li>If the player is not inside a park, an error message is sent informing them to execute the command within a park.</li>
 * <li>If the attraction ID does not match any existing attraction in the park, an error message is sent specifying the issue.</li>
 * </ul>
 * </p>
 */
@CommandMeta(description = "Close an attraction")
public class CloseCommand extends CoreCommand {

    /**
     * Constructs a new instance of the {@code CloseCommand}.
     * <p>
     * This command is responsible for closing an attraction. It is used in a system where
     * attractions can be managed and toggled between open and closed states.
     * <p>
     * <b>Command Parameters:</b>
     * <ul>
     *     <li><strong>Command Name:</strong> "close" - The name used to invoke the command in the system.</li>
     *     <li><strong>Parent System:</strong> Integrated with the attraction and park management systems.</li>
     * </ul>
     * <p>
     * The command utilizes underlying park and attraction services for its functionality. It assumes
     * the presence of a valid system state and the necessary supporting data to execute the purpose
     * of closing an attraction.
     *
     * <p>For details on the execution or further context, refer to the relevant attraction management
     * documentation.
     */
    public CloseCommand() {
        super("close");
    }

    /**
     * Handles the "close" command for attractions in a park. This method is invoked when a player
     * executes a command to close a specific attraction by its ID. The attraction is identified
     * within the player's current park vicinity and its operational status is updated accordingly.
     * If the attraction has an associated queue, the queue is also closed.
     *
     * <p>Users must invoke this command while located inside a park. The attraction ID
     * is required as a command argument. An appropriate error message is shown if the
     * command is not valid, the player is not inside a park, or the attraction ID does not exist.
     *
     * @param player the player executing the command
     * @param args an array of command arguments, where the first element is expected to be the attraction ID
     * @throws CommandException if an error occurs while processing the command
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/attraction close [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the attraction id from /attraction list!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Attraction attraction = ParkManager.getAttractionManager().getAttraction(args[0], park.getId());
        if (attraction == null) {
            player.sendMessage(ChatColor.RED + "Could not find an attraction by id " + args[0] + "!");
            return;
        }
        attraction.setOpen(false);
        boolean queueUpdated = false;
        if (attraction.getLinkedQueue() != null) {
            Queue queue = ParkManager.getQueueManager().getQueue(attraction.getLinkedQueue(), park.getId());
            if (queue != null) {
                queue.setOpen(false);
                queueUpdated = true;
            }
        }
        if (queueUpdated) ParkManager.getQueueManager().saveToFile();
        ParkManager.getAttractionManager().saveToFile();
        player.sendMessage(attraction.getName() + ChatColor.RED + " has been closed!");
    }
}
