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
 * <p>The <code>ListCommand</code> class is responsible for handling the "list" command,
 * which provides a list of all attractions available within the park where the command is executed.</p>
 *
 * <p>This command is context-sensitive and requires the player to be within a park region.
 * When executed successfully, the player receives a formatted list of attractions,
 * including each attraction's ID, name, and warp location.</p>
 *
 * <h3>Command Behavior:</h3>
 * <ul>
 *     <li>Validates that the player is within a park region.
 *         If the player is not within a park, an error message is sent.</li>
 *     <li>Retrieves all attractions associated with the park the player is in.</li>
 *     <li>Sends a message to the player listing each attraction in the park,
 *         formatted with details such as ID, name, and warp location.</li>
 * </ul>
 *
 * <h3>Command Details:</h3>
 * <ul>
 *     <li><b>Command Name:</b> list</li>
 *     <li><b>Context Requirement:</b> Player must be within a valid park.</li>
 *     <li><b>Permissions:</b> The player must have appropriate permissions to execute commands.</li>
 * </ul>
 *
 * <h3>Possible Exceptions:</h3>
 * <ul>
 *     <li><b>CommandException:</b> Raised in cases where command execution encounters issues.</li>
 * </ul>
 *
 * <h3>Output:</h3>
 * <ul>
 *     <li>A success message listing all attractions in the park, formatted with details.</li>
 *     <li>A failure message if the player is not in a park when the command is executed.</li>
 * </ul>
 */
@CommandMeta(description = "List all attractions")
public class ListCommand extends CoreCommand {

    /**
     * Constructs a new instance of the <code>ListCommand</code> class.
     * <p>
     * This constructor initializes the command with the name "list".
     * The "list" command is used to display a detailed list of attractions available
     * within the park where the command is executed.
     * </p>
     *
     * <p>This initialization is integral to registering the command for execution.
     * Once registered, the command can be executed to retrieve formatted
     * information about park attractions.</p>
     *
     * <h3>Initialization Details:</h3>
     * <ul>
     *     <li><b>Command Name:</b> list</li>
     *     <li>The command name is defined as a parameter passed to the superclass constructor.</li>
     *     <li><b>Purpose:</b> Prepares the command for use by setting its primary identifier as "list".</li>
     * </ul>
     */
    public ListCommand() {
        super("list");
    }

    /**
     * Handles the "list" command by providing a list of attractions in the park where the player is located.
     * <p>
     * This method checks if the player is inside a park and retrieves all attractions associated with that park.
     * If the player is not in a park, an error message is sent. Otherwise, a list of attractions is sent to the player,
     * including each attraction's ID, name, and warp location.
     * </p>
     *
     * @param player The player executing the command. The player must be in a park to successfully view the list of attractions.
     * @param args   An array of command arguments provided by the player. These arguments are unused in this method.
     * @throws CommandException If an error occurs during the command execution that prevents proper handling.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + park.getId().getTitle() + ChatColor.GREEN + " Attractions:");
        for (Attraction attraction : ParkManager.getAttractionManager().getAttractions(park.getId())) {
            player.sendMessage(ChatColor.AQUA + "- [" + attraction.getId() + "] " + ChatColor.YELLOW + attraction.getName() + ChatColor.GREEN + " at " + ChatColor.YELLOW + "/warp " + attraction.getWarp());
        }
    }
}
