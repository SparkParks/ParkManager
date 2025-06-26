package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.handlers.Park;
import org.bukkit.ChatColor;

/**
 * Represents a command used to unlink a queue from an attraction within a park.
 * <p>
 * This command allows players to unlink a previously linked queue from a specific attraction
 * within their current park. The player must execute this command while inside a valid park.
 * <p>
 * If the provided attraction ID does not exist within the player's park, or if the player
 * is outside of a park, error messages will be sent to the player. Once the attraction is
 * successfully unlinked, the attraction configuration is saved, and a success message is
 * displayed to the player.
 *
 * <p><b>Command Syntax:</b></p>
 * <ul>
 *   <li><code>/attraction unlink [attraction-id]</code></li>
 * </ul>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Validates that the player is located within a park.</li>
 *   <li>Checks for the existence of the attraction associated with the given ID.</li>
 *   <li>Unlinks the queue linked to the specified attraction.</li>
 *   <li>Saves modifications to persistent storage after unlinking.</li>
 * </ul>
 *
 * <p><b>Player Feedback:</b></p>
 * <ul>
 *   <li>Sends an error if the player is not inside a park.</li>
 *   <li>Sends an error if the specified attraction ID does not exist.</li>
 *   <li>Sends a confirmation message upon successful unlinking of the queue.</li>
 * </ul>
 */
@CommandMeta(description = "Unlink a queue from an attraction")
public class UnlinkCommand extends CoreCommand {

    /**
     * Initializes a new instance of the <code>UnlinkCommand</code>.
     * <p>
     * This constructor sets up the command with the identifier <code>"unlink"</code>,
     * which facilitates the execution of the unlink operation related to attractions
     * within a player's park.
     * </p>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Associates the command with its intended usage, allowing players to unlink queues from attractions.</li>
     *   <li>Prepares the command to handle specific logic when executed by a player.</li>
     * </ul>
     */
    public UnlinkCommand() {
        super("unlink");
    }

    /**
     * Handles the unlinking of a queue from a specific attraction within the player's current park.
     * <p>
     * This method validates the player's location to ensure they are within a park, verifies the
     * existence of the attraction using the provided ID, and proceeds to unlink the queue if all
     * conditions are met. A success or error message is sent to the player based on the outcome
     * of the command execution.
     *
     * <p><b>Command Usage:</b> <code>/attraction unlink [attraction-id]</code></p>
     *
     * <p><b>Steps performed:</b></p>
     * <ol>
     *   <li>Checks if the player supplied valid arguments.</li>
     *   <li>Validates that the player is located within a valid park.</li>
     *   <li>Attempts to find the attraction with the specified ID within the park.</li>
     *   <li>If found, unlinks the attraction's queue and saves the changes.</li>
     *   <li>Sends feedback messages to the player based on success or errors encountered.</li>
     * </ol>
     *
     * @param player the player executing the command, whose location and permission to perform the action are evaluated.
     * @param args the command arguments provided by the player. The first argument should be the attraction ID to unlink.
     * @throws CommandException if an error occurs during command execution or validation.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/attraction unlink [attraction-id]");
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
        attraction.setLinkedQueue(null);
        ParkManager.getAttractionManager().saveToFile();
        player.sendMessage(ChatColor.GREEN + "Successfully unlinked " + attraction.getName() + "'s " + ChatColor.GREEN + "queue!");
    }
}
