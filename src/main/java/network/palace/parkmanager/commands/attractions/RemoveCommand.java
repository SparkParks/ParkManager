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
 * Represents a command to remove an existing attraction within the player's current park.
 * <p>
 * This command allows players to remove an attraction by its ID. The command checks for
 * valid arguments and ensures that the player is located within a park. If the specified
 * attraction exists and is successfully removed, a confirmation message is displayed;
 * otherwise, error messages are presented in the event of invalid input or operational failure.
 * </p>
 *
 * <p><b>Command Overview:</b></p>
 * <ul>
 *   <li>Command Syntax: <code>/attraction remove [id]</code></li>
 *   <li>Players must be located within a park to execute this command.</li>
 *   <li>The attraction ID can be obtained using the <code>/attraction list</code> command.</li>
 * </ul>
 *
 * <p><b>Command Behavior:</b></p>
 * <ul>
 *   <li>If no ID is provided, an appropriate usage message is displayed.</li>
 *   <li>If the player is not inside a park, the command fails with an error message.</li>
 *   <li>If the specified attraction ID does not exist within the current park, an error
 *       message is shown to the player.</li>
 *   <li>If the attraction is successfully removed, a success message is displayed.</li>
 *   <li>If an unexpected error occurs during removal, an error message indicates the failure.</li>
 * </ul>
 */
@CommandMeta(description = "Remove an existing attraction")
public class RemoveCommand extends CoreCommand {

    /**
     * Constructs a new RemoveCommand instance.
     * <p>
     * The <code>RemoveCommand</code> class represents a command that allows players to remove
     * an existing attraction from their current park. It initiates the command logic with a
     * predefined name "remove," which will later be processed when the command is executed by a player.
     * </p>
     *
     * <p>This constructor initializes the command by calling the superclass constructor with the
     * specified name "remove."</p>
     *
     * <p><b>Features:</b></p>
     * <ul>
     *   <li>Sets the command identifier to "remove."</li>
     *   <li>Prepares the command for player execution in the context of park management.</li>
     * </ul>
     *
     * <p>See the documentation of the method <code>handleCommand</code> for detailed behavior
     * when the command is executed.</p>
     */
    public RemoveCommand() {
        super("remove");
    }

    /**
     * Handles the "remove" command to delete an attraction from the player's current park.
     * <p>
     * This method checks if the player has provided the required arguments, verifies that
     * the player is located within a park, and attempts to remove the specified attraction
     * by its ID. If successful, a confirmation message is sent to the player; otherwise,
     * appropriate error messages are displayed.
     * </p>
     *
     * @param player The {@link CPlayer} executing the command. The player's location is
     *               used to identify the current park context.
     * @param args   The command arguments provided by the player. The first argument should
     *               represent the ID of the attraction to be removed.
     *
     * @throws CommandException If a critical error occurs while processing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/attraction remove [id]");
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
        if (ParkManager.getAttractionManager().removeAttraction(args[0], park.getId())) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + attraction.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + attraction.getName() + "!");
        }
    }
}
