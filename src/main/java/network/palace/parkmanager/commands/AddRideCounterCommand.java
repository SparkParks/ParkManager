package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The {@code AddRideCounterCommand} class represents a command to add a ride counter
 * for a specified player in the system. This command allows staff members with the
 * appropriate rank to log a ride the player is currently on.
 *
 * <p>This command is used in systems where players interact with vehicles in a
 * virtual environment (e.g., amusement parks or simulations). Staff members can
 * input the player's username and the name of the ride to log the ride for tracking purposes.
 *
 * <h3>Command Usage:</h3>
 * <p>The command follows the syntax: <code>/rc [username] [ride name]</code>.</p>
 *
 * <h3>Behavior:</h3>
 * <ul>
 *   <li>Validates the arguments to ensure the username and ride name are provided.</li>
 *   <li>Retrieves the player object for the specified username.</li>
 *   <li>Checks if the player is currently in a rideable vehicle.</li>
 *   <li>Constructs the ride name from the input and logs the ride entry asynchronously,
 *       using the relevant utility class.</li>
 * </ul>
 *
 * <h3>Error Handling:</h3>
 * <ul>
 *   <li>If an insufficient number of arguments are provided, the user is informed of the proper usage.</li>
 *   <li>If the specified player is not found or is invalid, an error message is sent to the command sender.</li>
 *   <li>If the targeted player is not in a vehicle, the command sender is notified of this condition.</li>
 * </ul>
 *
 * <h3>Command Permissions:</h3>
 * <p>The command is restricted to users with a rank of {@code Rank.CM} or higher. This ensures
 * only authorized staff members can log ride entries.</p>
 */
@CommandMeta(description = "Add a ride counter for a player", rank = Rank.CM)
public class AddRideCounterCommand extends CoreCommand {

    /**
     * Constructs an {@code AddRideCounterCommand} object to initialize the
     * command with the identifier "rc".
     *
     * <p>This command is designed to allow authorized staff members to add a ride counter
     * for a specified player, ensuring proper tracking of ride usage in the system.
     * The command requires at least two arguments: the player's username and the
     * name of the ride being logged.</p>
     *
     * <h3>Key Features:</h3>
     * <ul>
     *   <li>Registers the command shorthand identifier as "rc".</li>
     *   <li>Ensures it is accessible only to staff members with a rank of {@code Rank.CM} or higher.</li>
     *   <li>Centralizes functionality for logging rides in the overall park management system.</li>
     * </ul>
     *
     * <p>The addition of this command to the system supports features like player
     * activity tracking, analytics, and ride management in virtual environments.</p>
     */
    public AddRideCounterCommand() {
        super("rc");
    }

    /**
     * Handles the unspecific command logic for adding a ride counter for a specified player.
     * <p>
     * This method processes the command to log a ride for a given player by validating
     * input arguments, ensuring the player is available and currently in a vehicle, and
     * asynchronously delegating the ride logging task.
     * </p>
     * <p>
     * If the command arguments are invalid or the player conditions are not met, the
     * appropriate error messages are sent back to the command sender.
     * </p>
     *
     * @param sender The {@code CommandSender} who executed the command. This can be a player or console.
     * @param args   The arguments passed with the command, where:
     *               <ul>
     *               <li>args[0] is expected to be the username of the targeted player.</li>
     *               <li>args[1] (and subsequent) represent the name of the ride to be logged.</li>
     *               </ul>
     *
     * @throws CommandException if an error occurs that is not related to standard input validation or
     *                          logical flow within the command handler.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/rc [username] [ride name]");
            return;
        }
        CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
        if (tp == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        if (!tp.isInVehicle()) {
            sender.sendMessage(ChatColor.RED + "That player is not in a vehicle!");
            return;
        }
        StringBuilder rideName = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            rideName.append(args[i]);
            rideName.append(" ");
        }
        String finalRideName = rideName.toString().trim();
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> ParkManager.getRideCounterUtil().logNewRide(tp, finalRideName, sender));
    }
}
