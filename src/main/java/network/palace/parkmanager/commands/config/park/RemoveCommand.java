package network.palace.parkmanager.commands.config.park;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.ParkType;
import org.bukkit.ChatColor;

/**
 * Represents the command used to remove an existing park. This command allows the user
 * to delete a park from the system by specifying its ID.
 *
 * <p>The <b>RemoveCommand</b> extends the {@code CoreCommand} class and provides functionality
 * to handle the removal of parks. It verifies the validity of the park identifier provided
 * by the user, checks if the specified park exists, and then attempts to remove it.
 *
 * <p><b>Command Syntax:</b>
 * <ul>
 *   <li><code>/park remove [id]</code> - Removes an existing park with the specified ID.</li>
 * </ul>
 *
 * <p>If the ID is invalid or no park exists with the specified ID, appropriate error messages
 * are sent to the user.
 *
 * <p><b>Behavior Details:</b>
 * <ul>
 *   <li>Requires an ID parameter to identify the park to be removed.</li>
 *   <li>Validates the provided ID against a list of available park IDs.</li>
 *   <li>Checks if the park exists in the current system by querying the {@code ParkManager}.</li>
 *   <li>If the park is found, attempts to remove it using {@code ParkManager.getParkUtil().removePark()}.</li>
 *   <li>Displays success or error messages to the user based on the operation outcome.</li>
 * </ul>
 *
 * <p><b>Error Responses:</b>
 * <ul>
 *   <li><i>Invalid Command Usage:</i> If no ID is provided, displays the correct command usage to the user.</li>
 *   <li><i>Invalid ID:</i> If the provided ID is not recognized as a valid park ID, notifies the user accordingly.</li>
 *   <li><i>Nonexistent Park:</i> If no park with the specified ID exists, informs the user that the park is missing.</li>
 *   <li><i>Removal Error:</i> If an error occurs during the removal process, an error message is shown.</li>
 * </ul>
 */
@CommandMeta(description = "Remove an existing park")
public class RemoveCommand extends CoreCommand {

    /**
     * <p>
     * Constructs a new {@code RemoveCommand} instance. This command is designed
     * to handle the removal of specified entities or objects within the context
     * of the implemented system. It sets up the foundation for command execution
     * by initializing the command with a specific label ("remove").
     * </p>
     *
     * <h2>Key Characteristics:</h2>
     * <ul>
     *   <li>Initializes a command labeled "remove".</li>
     *   <li>Provides the necessary setup for handling removal operations.</li>
     *   <li>Extends the functionality of its parent class, relying on its
     *       structure to implement behavior specific to removal operations.</li>
     * </ul>
     *
     * <p>
     * The {@code RemoveCommand} is meant to integrate with the broader command handling system,
     * enabling interaction with players or users in specific scenarios where removal
     * functionality is required.
     * </p>
     */
    public RemoveCommand() {
        super("remove");
    }

    /**
     * Handles the removal of a park by the specified identifier.
     * <p>
     * This method is invoked when the user executes the command to remove
     * a park. It validates the input arguments, checks the existence of the
     * park, and attempts to remove it. If successful, a success message is
     * displayed to the player; otherwise, an error message is shown.
     * </p>
     *
     * @param player The player who executed the command.
     *               <ul>
     *                  <li>The {@code player} object contains methods to communicate with the player (e.g., sending messages).</li>
     *               </ul>
     * @param args   The arguments supplied with the command.
     *               <ul>
     *                  <li>The first argument, {@code args[0]}, should be the identifier of the park to be removed.</li>
     *                  <li>If no identifier is provided or it's invalid, an appropriate error message is shown to the player.</li>
     *               </ul>
     * @throws CommandException Thrown if there is an error executing the command
     *                          due to invalid input or unexpected issues.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/park remove [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the park id from /parkconfig park list!");
            return;
        }
        String id = args[0];
        ParkType type = ParkType.fromString(id);
        if (type == null) {
            player.sendMessage(ChatColor.RED + "That isn't a valid park id!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(type);
        if (park == null) {
            player.sendMessage(ChatColor.RED + "A park doesn't exist on this server with the id " + id + "!");
            return;
        }
        if (ParkManager.getParkUtil().removePark(type)) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + park.getId() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + park.getId() + "!");
        }
    }
}
