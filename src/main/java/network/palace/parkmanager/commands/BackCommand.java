package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * Represents the command to teleport a player back to their previous location.
 * <p>
 * The <code>BackCommand</code> allows players to return to their last saved location
 * by executing the command. If there is no previous location available,
 * an appropriate message will be sent to the player.
 * <p>
 * The command is restricted by rank and can only be executed by players with the
 * proper permissions.
 * <ul>
 *  <li><b>Rank Restriction:</b> This command is restricted to players with the <code>Rank.CM</code>.</li>
 *  <li><b>Command Aliases:</b> The command responds to "back".</li>
 * </ul>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *  <li>If no previous location is available, a message will notify the player.</li>
 * </ul>
 *
 * <p><b>Dependencies:</b></p>
 * <ul>
 *  <li>Relies on <code>ParkManager</code> and its teleport utility to process the back action.</li>
 *  <li>Uses <code>CPlayer</code> to retrieve player-specific information.</li>
 *  <li>Handles exceptions by throwing <code>CommandException</code> when necessary.</li>
 * </ul>
 */
@CommandMeta(description = "Go to your previous location", rank = Rank.CM)
public class BackCommand extends CoreCommand {

    /**
     * Constructs a new BackCommand instance.
     * <p>
     * The <code>BackCommand</code> is associated with the "back" command string, allowing
     * players to return to their last saved location. This command is restricted to players
     * with the appropriate rank, specifically <code>Rank.CM</code>.
     * <p>
     * When executed, it attempts to teleport the player back to their previous location
     * using the teleport utility from <code>ParkManager</code>. If no previous location is
     * available, an appropriate message is sent to the player.
     *
     * <p><b>Features:</b></p>
     * <ul>
     *  <li>Allows players to teleport back to their last saved location.</li>
     *  <li>Sends feedback to the player if no previous location is available.</li>
     *  <li>Integrates with <code>ParkManager</code> for teleportation functionality.</li>
     *  <li>Command execution is restricted to users with the <code>Rank.CM</code>.</li>
     * </ul>
     *
     * <p><b>Error Handling:</b></p>
     * <ul>
     *  <li>Notifies the player if there is no recorded previous location.</li>
     *  <li>Relies on exceptions, like <code>CommandException</code>, for handling execution errors.</li>
     * </ul>
     */
    public BackCommand() {
        super("back");
    }

    /**
     * Handles the execution of the "back" command for a player.
     * <p>
     * This method attempts to teleport the player back to their previous saved location
     * using the teleport utility from <code>ParkManager</code>. If no previous location
     * exists, an informational message is sent to the player notifying them of the absence
     * of a saved location.
     * <p>
     * <b>Error Handling:</b> If no previous location is available, the player is informed
     * via a message. This message is displayed in gray text.
     *
     * @param player the player executing the command. Represents the entity attempting
     *               to teleport back to their previous location.
     * @param args   additional arguments passed with the command. Not utilized in this method.
     * @throws CommandException if there is an issue processing the command or teleportation.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (!ParkManager.getTeleportUtil().back(player)) {
            player.sendMessage(ChatColor.GRAY + "No location to teleport back to!");
        }
    }
}