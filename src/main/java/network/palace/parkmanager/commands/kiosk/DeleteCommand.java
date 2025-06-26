package network.palace.parkmanager.commands.kiosk;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

/**
 * The <code>DeleteCommand</code> class handles the deletion of the nearest kiosk within a
 * specified range of 3 blocks from the issuing player. This command is designed to be used
 * within the context of a system managing kiosks through armor stands.
 *
 * <p>This command is registered with the name "delete" and allows players to remove the
 * closest kiosk (virtual representation through an armor stand) if they are within proximity.</p>
 *
 * <p>The deletion process involves:</p>
 * <ul>
 *   <li>Determining the player's current location.</li>
 *   <li>Scanning for nearby armor stands in the same world as the player.</li>
 *   <li>Identifying the closest valid kiosk that is a maximum of 3 blocks away.</li>
 *   <li>Removing the identified kiosk if found.</li>
 * </ul>
 *
 * <p>If no kiosks are within range, the player will receive a message indicating the lack
 * of suitable targets for removal.</p>
 *
 * <p><b>Note:</b> A kiosk is determined to be valid using the
 * <code>ParkManager.getFastPassKioskManager().isKiosk()</code> method, which ensures only
 * registered kiosks are considered.</p>
 *
 * <h3>Command Workflow</h3>
 * <ol>
 *   <li>The player issues the "delete" command.</li>
 *   <li>The system calculates the distance to surrounding kiosks and identifies the closest one
 *       within 3 blocks.</li>
 *   <li>If a suitable kiosk is found, it is deleted, and the player receives a confirmation message.</li>
 *   <li>If no suitable kiosk is found, the player is notified of the failure to locate a nearby kiosk.</li>
 * </ol>
 *
 * <p><b>Exceptions:</b></p>
 * <ul>
 *   <li><code>CommandException</code> - if there is an issue executing the command.</li>
 * </ul>
 */
@CommandMeta(description = "Delete the kiosk closest to you (at most 3 blocks away)")
public class DeleteCommand extends CoreCommand {

    /**
     * Constructs a new instance of the <code>DeleteCommand</code>.
     *
     * <p>The <code>DeleteCommand</code> is designed to provide functionality for removing
     * the nearest kiosk within a range of 3 blocks from the player's current location.
     * This command is specifically registered under the name "delete" and operates in
     * the context of managing kiosks within the game.</p>
     *
     * <p>Key features and behavior:</p>
     * <ul>
     *   <li>Registers the "delete" command in the command system.</li>
     *   <li>Allows players to interactively remove kiosks close to their position.</li>
     *   <li>Handles proximity checks to ensure only kiosks within a 3-block range are deleted.</li>
     * </ul>
     *
     * <p>This constructor initializes the command and ensures it is properly recognized
     * and available for execution by the command system, enabling the integration of
     * kiosk deletion functionality within the player's interactive environment.</p>
     */
    public DeleteCommand() {
        super("delete");
    }

    /**
     * Handles the deletion of the closest kiosk (armor stand) within 3 blocks of the player's location.
     *
     * <p>The method performs the following tasks:</p>
     * <ul>
     *   <li>Retrieves the player's current location.</li>
     *   <li>Scans for armor stands in the player's world to determine potential kiosks.</li>
     *   <li>Identifies the nearest kiosk within a 3-block radius from the player's location.</li>
     *   <li>If a valid kiosk is found, it is removed, and the player is informed with a success message.</li>
     *   <li>If no valid kiosk is found, the player is notified with an error message.</li>
     * </ul>
     *
     * <b>Key Notes:</b>
     * <ul>
     *   <li>A kiosk is determined by calling <code>ParkManager.getFastPassKioskManager().isKiosk()</code>.</li>
     *   <li>The player's Y-coordinate is used to match the kiosk's height for distance calculation.</li>
     * </ul>
     *
     * @param player The <code>CPlayer</code> object representing the player executing the command.
     *               The player's location is used to calculate the nearest kiosk within range.
     * @param args   A string array of additional command arguments. This command currently does not
     *               utilize the provided arguments, but the array must be included for command parsing.
     * @throws CommandException If any issues occur during the command execution process.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Location loc = player.getLocation();
        ArmorStand closest = null;
        double distance = -1;
        for (ArmorStand stand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
            if (!ParkManager.getFastPassKioskManager().isKiosk(stand)) continue;
            Location standLoc = stand.getLocation();
            loc.setY(standLoc.getY());
            double dist = standLoc.distance(loc);
            if (dist < 3 && (dist < distance || distance == -1)) {
                closest = stand;
                distance = dist;
            }
        }
        if (closest == null) {
            player.sendMessage(ChatColor.RED + "Couldn't find a kiosk within 3 blocks from you, move closer to the one you're trying to remove!");
            return;
        }
        closest.remove();
        player.sendMessage(ChatColor.GREEN + "Deleted the closest kiosk to you!");
    }
}
