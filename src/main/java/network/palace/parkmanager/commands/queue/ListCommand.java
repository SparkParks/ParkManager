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
 * Represents a command to list all queues within a park.
 * <p>
 * This command allows a player to retrieve and display a list of all queues
 * associated with the park they are currently located in. The park context
 * is determined based on the player's current location.
 * </p>
 *
 * <p>
 * If executed outside of a park, the command notifies the player that
 * they must be inside a park to use this command.
 * </p>
 *
 * <p>
 * For each queue available in the park:
 * <ul>
 *   <li>Displays the queue's identifier.</li>
 *   <li>Displays the queue's name.</li>
 *   <li>Provides the warp information to navigate to the queue.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Upon execution, output is sent to the player displaying details of the queues
 * in a formatted manner with styling applied using color codes.
 * </p>
 *
 * <p>
 * The {@code handleCommand} method is overridden to provide the required
 * implementation details for processing the "list" command associated with a
 * player.
 * </p>
 *
 * <p><b>Command Context:</b></p>
 * <ul>
 *   <li>Only works when the player is within a park.</li>
 *   <li>Uses {@link ParkManager} to retrieve park-related information.</li>
 *   <li>Iterates through the queues within the identified park.</li>
 * </ul>
 *
 * <p><b>Expected Output:</b> A list of queues with their respective information is sent
 * to the player using formatted messages.</p>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *   <li>Displays an error message if the player is not located in a park
 *       when the command is executed.</li>
 *   <li>Handles exceptions through the {@link CommandException} hierarchy.</li>
 * </ul>
 */
@CommandMeta(description = "List all queues")
public class ListCommand extends CoreCommand {

    /**
     * Initializes a new instance of the {@code ListCommand} class.
     * <p>
     * This constructor sets the command's name to "list", which is used to execute
     * the functionality associated with listing all queues within a park.
     * </p>
     *
     * <p>
     * The {@code ListCommand} is designed to handle player-initiated commands related
     * to queue structures in the context of a park. It ensures the command is registered
     * with the correct identifier for proper execution under the command framework.
     * </p>
     */
    public ListCommand() {
        super("list");
    }

    /**
     * Handles the command for listing and displaying all the queues available within a park.
     * <p>
     * This method retrieves the park context based on the player's location and, if successful,
     * lists all the queue identifiers, names, and warp information associated with that park.
     * If the player is not within any park when the command is executed, an error message
     * is displayed to the player.
     * </p>
     *
     * <p><b>Process Details:</b></p>
     * <ul>
     *   <li>Fetches the park instance using the player's current location.</li>
     *   <li>Notifies the player if they are not inside a park.</li>
     *   <li>Iterates through queues related to the identified park.</li>
     *   <li>For each queue, sends the details including queue ID, name, and warp to the player.</li>
     * </ul>
     *
     * @param player The player executing the command. This parameter is used to determine the
     *               player's current location and send messages back to the player.
     * @param args   An array of command arguments passed during execution. This command does
     *               not utilize any additional arguments directly.
     * @throws CommandException If an issue occurs handling the command, such as missing
     *                          or invalid context details.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + park.getId().getTitle() + ChatColor.GREEN + " Queues:");
        for (Queue queue : ParkManager.getQueueManager().getQueues(park.getId())) {
            player.sendMessage(ChatColor.AQUA + "- [" + queue.getId() + "] " + ChatColor.YELLOW + queue.getName() + ChatColor.GREEN + " at " + ChatColor.YELLOW + "/warp " + queue.getWarp());
        }
    }
}
