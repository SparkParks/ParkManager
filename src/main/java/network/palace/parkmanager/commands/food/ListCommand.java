package network.palace.parkmanager.commands.food;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.food.FoodLocation;
import network.palace.parkmanager.handlers.Park;
import org.bukkit.ChatColor;

/**
 * <p>
 * Represents the command that lists all food locations within the player's current park.
 * </p>
 *
 * <h2>Functionality:</h2>
 * <p>
 * This command retrieves and displays all food locations inside the park where the player is currently located.
 * When executed, it checks if the player is within a park. If not, a message is sent to inform the player
 * that the command requires them to be inside a park. Otherwise, it lists information about each food location,
 * including its ID, name, and corresponding warp command.
 * </p>
 *
 * <h2>Usage Flow:</h2>
 * <ul>
 *     <li>Determines the park the player currently resides in based on their location.</li>
 *     <li>If the player is outside of a park, they are alerted with an error message.</li>
 *     <li>If inside a park, all food locations in that park are iterated and displayed to the player.</li>
 * </ul>
 *
 * <h2>Expected Output:</h2>
 * <p>
 * When the command is executed successfully, the player receives a message in chat listing all food locations
 * with details formatted using colors.
 * </p>
 *
 * <h2>Command Attributes:</h2>
 * <p>
 * The command's identifier is <strong>"list"</strong>, which is passed to the parent <code>CoreCommand</code> class
 * during instantiation.
 * </p>
 *
 * <h2>Key Conditions:</h2>
 * <ul>
 *     <li>The player must be located inside a valid park for the command to operate.</li>
 *     <li>The command fetches food location data from the relevant park's ID through the <code>ParkManager</code>.</li>
 * </ul>
 */
@CommandMeta(description = "List all food locations")
public class ListCommand extends CoreCommand {

    /**
     * Constructs a new <code>ListCommand</code> instance.
     * <p>
     * Initializes the command with the identifier <strong>"list"</strong>, which is passed to the parent
     * <code>CoreCommand</code> class for further processing and command management.
     * </p>
     *
     * <h2>Purpose:</h2>
     * <p>
     * This constructor sets up the <code>ListCommand</code> to list all food locations in the park where the player
     * is currently located. The identifier "list" associates this command with its functionality in the game's command list.
     * </p>
     *
     * <h2>Usage:</h2>
     * <ul>
     *     <li>Instantiates a command to retrieve and display food locations within the park.</li>
     *     <li>Can be executed by players when they wish to view nearby food places.</li>
     *     <li>Name "list" is required to invoke this command.</li>
     * </ul>
     *
     * <h2>Key Behavior:</h2>
     * <ul>
     *     <li>Ensures the player must be inside a park for the command to operate effectively.</li>
     *     <li>Provides the starting point for retrieving and formatting the requested details about food locations.</li>
     * </ul>
     */
    public ListCommand() {
        super("list");
    }

    /**
     * <p>
     * Handles the "list" command to display all food locations within the current park the player is in.
     * The command ensures the player is located within a park before listing the food locations. If the player
     * is not inside a park, an error message is displayed.
     * </p>
     *
     * <h2>Functionality:</h2>
     * <ul>
     *     <li>Determines the player's current park based on their location.</li>
     *     <li>Checks if the player is outside of any park and notifies them if true.</li>
     *     <li>If the player is inside a park, retrieves all food locations for that park and displays them in a formatted message.</li>
     * </ul>
     *
     * <h2>Output:</h2>
     * <p>
     * Sends messages containing a list of food locations inside the park, including the following details for each:
     * <ul>
     *     <li><strong>ID:</strong> The identifier of the food location.</li>
     *     <li><strong>Name:</strong> The display name of the food location.</li>
     *     <li><strong>Warp Command:</strong> A warp command to navigate to the food location.</li>
     * </ul>
     * </p>
     *
     * @param player The player who issued the command. Their location is used to determine the park.
     * @param args   The command arguments. Not used in this command but may be available for potential extensions.
     * @throws CommandException If an issue occurs during the command's execution related to permissions or command logic.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + park.getId().getTitle() + ChatColor.GREEN + " Food locations:");
        for (FoodLocation food : ParkManager.getFoodManager().getFoodLocations(park.getId())) {
            player.sendMessage(ChatColor.AQUA + "- [" + food.getId() + "] " + ChatColor.YELLOW + food.getName() + ChatColor.GREEN + " at " + ChatColor.YELLOW + "/warp " + food.getWarp());
        }
    }
}
