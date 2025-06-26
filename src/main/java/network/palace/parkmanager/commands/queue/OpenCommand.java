package network.palace.parkmanager.commands.queue;

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
 * Represents a command to open a queue within a park.
 * This command allows players to activate a queue identified by its unique ID,
 * making it accessible within the game. It also updates the linked attractions
 * associated with the queue if necessary.
 *
 * <p><b>Command Usage:</b></p>
 * <ul>
 *   <li>Players must provide a valid queue ID as an argument.</li>
 *   <li>The player must be within a park region when attempting to execute the command.</li>
 * </ul>
 *
 * <p><b>Validation Steps:</b></p>
 * <ol>
 *   <li>Ensures the player provides at least one argument (the queue ID).</li>
 *   <li>Checks if the player is located inside a valid park region.</li>
 *   <li>Verifies if the specified queue ID exists within the park's system.</li>
 * </ol>
 *
 * <p><b>Behavior:</b></p>
 * <ul>
 *   <li>Marks the specified queue as open.</li>
 *   <li>If any attractions are linked to the queue, they are updated to an open state.</li>
 *   <li>Saves the updated queue and attraction data to file.</li>
 *   <li>Notifies the player about the success or failure of their action via messages.</li>
 * </ul>
 *
 * <p><b>Command Restrictions:</b></p>
 * <ul>
 *   <li>Players must issue this command while being inside a park.</li>
 *   <li>The provided queue ID must exist and correspond to a valid queue in the park.</li>
 * </ul>
 */
@CommandMeta(description = "Open a queue")
public class OpenCommand extends CoreCommand {

    /**
     * Constructs a new OpenCommand.
     *
     * <p><b>Description:</b></p>
     * <p>Initializes a command with the name "open" to be used in opening a queue
     * within the context of a park management system. This constructor sets up
     * the command meta-information required for the command execution framework.</p>
     */
    public OpenCommand() {
        super("open");
    }

    /**
     * Handles the "open queue" command, allowing a player to open a queue within a park.
     * This method verifies the player's input, ensures they are in a valid park,
     * validates the specified queue ID, and updates the state of the queue and its linked attractions.
     *
     * <p><b>Command Workflow:</b></p>
     * <ul>
     *   <li>Validates that the player has provided the required arguments.</li>
     *   <li>Checks if the player is located within a park.</li>
     *   <li>Verifies the existence of the queue using the provided ID within the detected park.</li>
     *   <li>Marks the queue and its linked attractions (if any) as open.</li>
     *   <li>Saves the changes persistently.</li>
     *   <li>Notifies the player regarding the status of the operation.</li>
     * </ul>
     *
     * @param player The player executing the command. Used to send feedback and determine their location.
     * @param args   The command arguments. This must include at least one argument, the queue ID to open.
     * @throws CommandException If there is an error during command execution, such as invalid input or missing queue/park.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/queue open [id]");
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
        queue.setOpen(true);
        boolean attractionUpdate = false;
        for (Attraction attraction : ParkManager.getAttractionManager().getAttractions(park.getId())) {
            if (attraction.getLinkedQueue() == null || !attraction.getLinkedQueue().equals(queue.getUuid())) continue;
            attraction.setOpen(true);
            attractionUpdate = true;
        }
        if (attractionUpdate) ParkManager.getAttractionManager().saveToFile();
        ParkManager.getQueueManager().saveToFile();
        player.sendMessage(queue.getName() + ChatColor.GREEN + " has been opened!");
    }
}
