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
 * <p>The <code>CloseCommand</code> class handles the "close" command in the game,
 * enabling players to close a specific queue associated with a park.
 * This command ensures that the queue and any linked attractions are updated accordingly.</p>
 *
 * <p><b>Description:</b></p>
 * <ul>
 *   <li>Used to mark a queue as closed.</li>
 *   <li>Also disables any attractions linked to the specified queue.</li>
 * </ul>
 *
 * <p><b>Usage:</b></p>
 * <ul>
 *   <li>Players must specify the queue ID as an argument.</li>
 *   <li>Command will validate the player's location to ensure they are inside a park.</li>
 *   <li>If the queue or park is not found, appropriate error messages are sent to the player.</li>
 * </ul>
 *
 * <p><b>Behavior:</b></p>
 * <ul>
 *   <li>If no arguments are provided, the player receives instructions on how to use the command.</li>
 *   <li>Closes the queue identified by the supplied ID within the park the player is located in.</li>
 *   <li>All attractions linked to the closed queue will also be marked as closed, if any exist.</li>
 *   <li>Updates and saves the relevant data to ensure persistence of changes.</li>
 *   <li>Provides feedback to the player about the status of the operation.</li>
 * </ul>
 *
 * <p><b>Dependencies:</b></p>
 * <ul>
 *   <li>Requires the player to be located within a park (validated by <code>ParkManager</code>).</li>
 *   <li>Queue and attraction information is managed through <code>QueueManager</code> and <code>AttractionManager</code>.</li>
 * </ul>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *   <li>If no queue ID is provided, the player receives a usage hint message.</li>
 *   <li>If the player is not in a park, an appropriate error message is shown.</li>
 *   <li>If the queue ID does not match any existing queue in the park, an error message is displayed.</li>
 * </ul>
 */
@CommandMeta(description = "Close a queue")
public class CloseCommand extends CoreCommand {

    /**
     * Constructs a new CloseCommand instance with the command identifier "close".
     * <p>
     * This command is expected to be used in the context of managing or interacting
     * with queues within a park management system.
     * <p>
     * This constructor initializes the command by invoking the parent class's constructor
     * and passing the command's identifier as an argument.
     */
    public CloseCommand() {
        super("close");
    }

    /**
     * Handles the "close queue" command, allowing players to close a queue within a park.
     * If the queue is linked to an attraction, the attraction will also be closed.
     * Command updates are persisted to the necessary files.
     *
     * <p>If the command is incorrectly formatted or the park/queue cannot be found, error messages
     * will be sent to the player.</p>
     *
     * @param player The player executing the command.
     * @param args   The command arguments.
     *               <ul>
     *               <li>args[0] - The ID of the queue to be closed.</li>
     *               </ul>
     * @throws CommandException If an error occurs while executing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/queue close [id]");
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
        queue.setOpen(false);
        boolean attractionUpdate = false;
        for (Attraction attraction : ParkManager.getAttractionManager().getAttractions(park.getId())) {
            if (attraction.getLinkedQueue() == null || !attraction.getLinkedQueue().equals(queue.getUuid())) continue;
            attraction.setOpen(false);
            attractionUpdate = true;
        }
        if (attractionUpdate) ParkManager.getAttractionManager().saveToFile();
        ParkManager.getQueueManager().saveToFile();
        player.sendMessage(queue.getName() + ChatColor.RED + " has been closed!");
    }
}
