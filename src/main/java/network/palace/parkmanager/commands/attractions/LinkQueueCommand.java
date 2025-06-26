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
 * <p>The {@code LinkQueueCommand} class is responsible for handling the command logic
 * to link a queue to an attraction within a park. This command associates an attraction
 * with a specific queue, enabling better management of queues for attractions in a park.</p>
 *
 * <p>The command format is:
 * <code>/attraction linkqueue [attraction-id] [queue-id]</code></p>
 *
 * <p>The required parameters are:</p>
 * <ul>
 *   <li><b>attraction-id</b>: The unique identifier for the attraction to link.</li>
 *   <li><b>queue-id</b>: The unique identifier for the queue to link to the attraction.</li>
 * </ul>
 *
 * <p><b>Key functionalities:</b></p>
 * <ul>
 *   <li>Validates that the player is inside a park before executing the command.</li>
 *   <li>Checks if the provided attraction ID is valid and exists within the current park.</li>
 *   <li>Checks if the provided queue ID is valid and exists within the current park.</li>
 *   <li>Links the specified attraction to the specified queue.</li>
 *   <li>Saves the updates to the configuration or data file after linking.</li>
 *   <li>Provides feedback messages to the player regarding the success or failure of the operation.</li>
 * </ul>
 *
 * <p><b>Failure conditions:</b></p>
 * <ul>
 *   <li>If insufficient arguments are provided, a usage message is sent to the player.</li>
 *   <li>If the command is executed outside of a park, an error message is sent to the player.</li>
 *   <li>If the specified attraction ID does not exist, an error message is sent to the player.</li>
 *   <li>If the specified queue ID does not exist, an error message is sent to the player.</li>
 * </ul>
 *
 * <p>This class extends {@code CoreCommand}, inheriting its behavior and command framework.</p>
 */
@CommandMeta(description = "Link a queue to an attraction")
public class LinkQueueCommand extends CoreCommand {

    /**
     * Constructs a new instance of the <code>LinkQueueCommand</code> class.
     *
     * <p>This constructor initializes the command with the identifier <code>"linkqueue"</code>.
     * It is typically used to manage linking operations or queues associated with a specific
     * command structure.</p>
     *
     * <p>The class should be utilized in scenarios where a custom command
     * implementation is required. Further behavior and context are defined
     * in the overridden command handling methods of the containing class.</p>
     *
     * <ul>
     *     <li>Initializes with a default command identifier: <code>"linkqueue"</code>.</li>
     * </ul>
     */
    public LinkQueueCommand() {
        super("linkqueue");
    }

    /**
     * Handles the command to link a queue to an attraction within a park.
     * <p>
     * This method processes the provided arguments to identify an attraction and a queue,
     * and links the two entities together if valid. It ensures the player is inside a park
     * during the execution of the command and provides feedback if any validation fails.
     * </p>
     *
     * @param player The {@link CPlayer} executing the command. This represents the player issuing the command.
     * @param args   An array of {@link String} that holds the command arguments.
     *               <ul>
     *                  <li><strong>args[0]</strong>: The ID of the attraction to link.</li>
     *                  <li><strong>args[1]</strong>: The ID of the queue to link to the attraction.</li>
     *               </ul>
     *               At least two arguments must be provided for the command to execute successfully.
     *
     * @throws CommandException If an error occurs while processing the command or if command execution fails.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/attraction linkqueue [attraction-id] [queue-id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get attraction-id from /attraction list!");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get queue-id from /queue list!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        String attractionID = args[0];
        Attraction attraction = ParkManager.getAttractionManager().getAttraction(attractionID, park.getId());
        if (attraction == null) {
            player.sendMessage(ChatColor.RED + "Could not find an attraction by id " + attractionID + "!");
            return;
        }
        String queueID = args[1];
        Queue queue = ParkManager.getQueueManager().getQueueById(queueID, park.getId());
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find a queue by id " + queueID + "!");
            return;
        }
        attraction.setLinkedQueue(queue.getUuid());
        ParkManager.getAttractionManager().saveToFile();
        player.sendMessage(ChatColor.GREEN + "Successfully linked the attraction " + attraction.getName() +
                ChatColor.GREEN + " to the queue " + queue.getName() + "!");
    }
}
