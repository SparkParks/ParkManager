package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.attractions.AttractionManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.queues.Queue;
import network.palace.parkmanager.queues.QueueManager;
import org.bukkit.ChatColor;

/**
 * Represents a command that opens an attraction within a player's current park.
 * <p>
 * The {@code OpenCommand} class extends the {@link CoreCommand} to implement
 * the functionality of opening an attraction. It verifies the presence of the player in a park
 * and ensures the attraction exists before marking it as open.
 * </p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Checks if the player is in a valid park before performing actions.</li>
 *   <li>Validates the attraction ID passed as an argument.</li>
 *   <li>Sets the specified attraction's state to "open".</li>
 *   <li>Attempts to open the linked queue, if applicable.</li>
 *   <li>Saves the modifications to persistent storage.</li>
 *   <li>Provides clear feedback to the player about the action performed or errors encountered.</li>
 * </ul>
 *
 * <p>This command interacts with:
 * <ul>
 *   <li>{@link ParkManager} - To retrieve park and attraction details.</li>
 *   <li>{@link AttractionManager} - For attraction-related operations.</li>
 *   <li>{@link QueueManager} - For handling linked queues.</li>
 *   <li>{@link CPlayer} - The player executing the command.</li>
 * </ul>
 *
 * <p><b>Command Syntax:</b></p>
 * <ul>
 *   <li>{@code /attraction open [id]}</li>
 * </ul>
 * <p>Where {@code [id]} represents the unique identifier of the attraction to be opened.</p>
 *
 * <p><b>Important Notes:</b></p>
 * <ul>
 *   <li>If the player is not located within a park, the command will not execute and display an error message.</li>
 *   <li>If no attraction ID is provided or if the provided ID does not correspond to an existing attraction, the command displays an error message.</li>
 * </ul>
 */
@CommandMeta(description = "Open an attraction")
public class OpenCommand extends CoreCommand {

    /**
     * Constructs a new instance of the <code>OpenCommand</code> class.
     * <p>
     * This command is used to handle the logic associated
     * with opening certain elements or functionality within the system.
     * </p>
     * <p>
     * The command name for this object will be initialized as <code>"open"</code>.
     * </p>
     */
    public OpenCommand() {
        super("open");
    }

    /**
     * Handles the command to open an attraction in a park. The command checks for proper input,
     * validates the player's location within a park, and updates the state of the specified attraction.
     * Additionally, linked queues associated with the attraction are opened if applicable.
     *
     *
     * @param player The player executing the command. This is used to determine the player's
     *               current location and send feedback messages.
     * @param args   The command arguments passed by the player. The first argument is expected
     *               to be the identifier of the attraction to open.
     * @throws CommandException If there is an error in executing the command, such as improper arguments
     *                          or issues related to attraction or park management.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/attraction open [id]");
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
        attraction.setOpen(true);
        boolean queueUpdated = false;
        if (attraction.getLinkedQueue() != null) {
            Queue queue = ParkManager.getQueueManager().getQueue(attraction.getLinkedQueue(), park.getId());
            if (queue != null) {
                queue.setOpen(true);
                queueUpdated = true;
            }
        }
        if (queueUpdated) ParkManager.getQueueManager().saveToFile();
        ParkManager.getAttractionManager().saveToFile();
        player.sendMessage(attraction.getName() + ChatColor.GREEN + " has been opened!");
    }
}
