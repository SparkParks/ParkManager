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
 * The <code>RemoveCommand</code> is a command that allows users to remove an existing
 * food location within a park. The command ensures that the player is currently
 * inside a park and that the specified food location exists before attempting
 * to remove it.
 * </p>
 *
 * <h2>Command Syntax:</h2>
 * <pre>
 * /food remove [id]
 * </pre>
 * <p>
 * The <code>[id]</code> is the unique identifier for the food location to be removed.
 * Use the <code>/food list</code> command to retrieve the available food location IDs for the park.
 * </p>
 *
 * <h2>Functionality:</h2>
 * <ul>
 *    <li>Validates that the command is used inside a park.</li>
 *    <li>Checks if the food location with the given ID exists within the park.</li>
 *    <li>Attempts to remove the specified food location upon successful validation.</li>
 *    <li>Provides feedback to the player based on the success or failure of the removal operation.</li>
 * </ul>
 *
 * <h2>Permissions:</h2>
 * <p>
 * The command may require specific permissions to execute. Ensure the player has the necessary rights
 * to remove food locations in the targeted park.
 * </p>
 *
 * <h2>Error Handling:</h2>
 * <ul>
 *    <li>Displays a usage guide if the <code>[id]</code> argument is omitted.</li>
 *    <li>Notifies the player if they are not within a valid park region when executing the command.</li>
 *    <li>Informs the player if the supplied food location ID is invalid or does not exist.</li>
 *    <li>Returns a success or failure message based on whether the removal operation succeeded.</li>
 * </ul>
 */
@CommandMeta(description = "Remove an existing food location")
public class RemoveCommand extends CoreCommand {

    /**
     * <p>
     * Constructs a new instance of the <code>RemoveCommand</code>. This command is used to handle
     * the removal of a specific food location within a park. It sets the command name to "remove"
     * and prepares it for execution by the command handler.
     * </p>
     *
     * <h2>Behavior:</h2>
     * <ul>
     *    <li>Initializes the <code>RemoveCommand</code> with its default identifier "remove".</li>
     *    <li>Prepares the command for execution, allowing players to execute the command in-game to
     *        remove a food location.</li>
     *    <li>Relies on further implementation in the command handling logic to ensure proper execution.</li>
     * </ul>
     *
     * <h2>Usage Context:</h2>
     * <p>
     * This constructor is intended to be called when registering the "remove" command with the
     * system's command registry. It does not execute any functional logic but provides access
     * to the command functionality when invoked by the player.
     * </p>
     */
    public RemoveCommand() {
        super("remove");
    }

    /**
     * Handles the "remove" command to delete an existing food location within a park.
     * <p>
     * This method validates the command input, ensures the player is inside a valid park,
     * and attempts to remove the specified food location by its unique identifier. A success
     * or failure message is sent back to the player based on the operation result.
     * </p>
     * <h2>Command Workflow:</h2>
     * <ul>
     *    <li>Ensures the player provides the food location ID as the input argument.</li>
     *    <li>Validates that the player is currently located inside a park.</li>
     *    <li>Attempts to identify the food location by the provided ID within the park.</li>
     *    <li>Removes the food location if it exists and provides feedback on the operation's result.</li>
     * </ul>
     * <h2>Command Syntax:</h2>
     * <pre>
     * /food remove [id]
     * </pre>
     * <p>
     * If the usage is incorrect or an error occurs, an appropriate message is sent to the player.
     * </p>
     *
     * @param player The player executing the command.
     * @param args   The command arguments provided by the player. The first argument should
     *               be the unique ID of the food location to be removed.
     * @throws CommandException If an error occurs during the execution of the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/food remove [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the food location id from /food list!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        FoodLocation food = ParkManager.getFoodManager().getFoodLocation(args[0], park.getId());
        if (food == null) {
            player.sendMessage(ChatColor.RED + "Could not find a food location by id " + args[0] + "!");
            return;
        }
        if (ParkManager.getFoodManager().removeFoodLocation(args[0], park.getId())) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + food.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + food.getName() + "!");
        }
    }
}
