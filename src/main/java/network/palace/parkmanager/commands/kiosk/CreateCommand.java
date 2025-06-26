package network.palace.parkmanager.commands.kiosk;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;

/**
 * <p>
 * The <code>CreateCommand</code> class represents a command that allows a player
 * to create a new kiosk at their current in-game location. This is primarily used
 * in the context of a park management game to spawn kiosks for player interaction.
 * </p>
 *
 * <p>
 * This command executes the necessary functionality to initialize and place a FastPass kiosk
 * at the player's position. It ties directly to the game's backend systems, such as the
 * FastPassKioskManager, which facilitates the management of these kiosks.
 * </p>
 *
 * <p>
 * Features:
 * <ul>
 *   <li>Accepts no arguments when executed.</li>
 *   <li>Triggers the spawning process for a FastPass kiosk at the player's current location.</li>
 *   <li>Integrates with the game's park management module to ensure proper kiosk creation.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Upon execution, it invokes the relevant method in the <code>ParkManager</code> to handle
 * kiosk spawning, leveraging the <code>spawn</code> functionality specific to the player's
 * position and context.
 * </p>
 *
 * <p>
 * Note: Any exceptions encountered during execution will be handled as per the
 * <code>CommandException</code> error handling mechanism, ensuring proper feedback and control flow.
 * </p>
 */
@CommandMeta(description = "Create a new kiosk where you're standing")
public class CreateCommand extends CoreCommand {

    /**
     * Constructs a new instance of the <code>CreateCommand</code>.
     *
     * <p>The <code>CreateCommand</code> is designed to allow players to create a
     * new kiosk at their current in-game location. This operation is bound to
     * the command name "create", and its functionality integrates closely with
     * the park management system of the application.</p>
     *
     * <p>This constructor initializes the command by setting its identifier to
     * "create", ensuring it is properly registered and recognized by the system.</p>
     *
     * <p>Features and behavior:
     * <ul>
     *   <li>Registers the "create" subcommand in the <code>KioskCommand</code> container.</li>
     *   <li>Allows interaction with the <code>ParkManager</code> to facilitate kiosk creation.</li>
     *   <li>Does not accept any specific arguments during initialization.</li>
     * </ul>
     * </p>
     */
    public CreateCommand() {
        super("create");
    }

    /**
     * Handles the execution of the "create" command, allowing the player to spawn a FastPass kiosk
     * at their current in-game location. This functionality enables players to interact with
     * the park's management system by creating user-accessible kiosks.
     *
     * <p>
     * When called, this method triggers the <code>spawn</code> method from the game's
     * <code>FastPassKioskManager</code>. The kiosk is placed directly at the player's current
     * in-game coordinates, ensuring seamless integration into the surrounding environment.
     * </p>
     *
     * <p>
     * No additional arguments are required for this command beyond those associated
     * with the player's context within the game.
     * </p>
     *
     * <p>
     * Exceptions may be thrown if the command cannot be executed properly, such as
     * when game rules or constraints prevent kiosk placement. These exceptions are
     * managed through the <code>CommandException</code> mechanism.
     * </p>
     *
     * @param player The <code>CPlayer</code> initiating the command. This contains
     *               information about the player, such as their current in-game
     *               position and state.
     * @param args   The command arguments passed by the player. For this command, no
     *               arguments are expected; this parameter is included to match the
     *               standard command handler signature.
     * @throws CommandException If an error occurs during the execution of the command,
     *                          such as an invalid player state or failure to spawn the kiosk.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        ParkManager.getFastPassKioskManager().spawn(player);
    }
}
