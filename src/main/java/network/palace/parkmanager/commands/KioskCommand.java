package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.kiosk.CreateCommand;
import network.palace.parkmanager.commands.kiosk.DeleteCommand;

/**
 * Represents the main command for managing kiosks in the application.
 * This command acts as a container for subcommands that allow users
 * to perform operations such as creating and deleting kiosks.
 *
 * <p>The subcommands registered under this command include:
 * <ul>
 *   <li><strong>CreateCommand:</strong> Creates a new kiosk at the player's location.</li>
 *   <li><strong>DeleteCommand:</strong> Deletes the kiosk closest to the player's location
 *   within a 3-block radius.</li>
 * </ul>
 *
 * <p>Users can invoke this command only through its subcommands, as it does not
 * handle any logic directly in its base implementation.
 */
@CommandMeta(description = "Default kiosk command", rank = Rank.CM)
public class KioskCommand extends CoreCommand {

    /**
     * Constructs the {@code KioskCommand}, which is the primary command for managing kiosks.
     *
     * <p>Upon instantiation, this command registers the following subcommands:
     * <ul>
     *   <li><strong>CreateCommand:</strong> Allows users to create a kiosk at their current location.</li>
     *   <li><strong>DeleteCommand:</strong> Enables users to delete the closest kiosk within a 3-block radius.</li>
     * </ul>
     *
     * <p>This class serves as a container for the subcommands and does not execute any actions directly by itself.
     */
    public KioskCommand() {
        super("kiosk");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new DeleteCommand());
    }

    /**
     * Determines whether the command relies exclusively on subcommands for its functionality.
     *
     * <p>This method indicates that the base command does not handle any execution
     * logic on its own and can only be triggered through its subcommands. It is
     * useful in cases where the command serves as a parent or wrapper for various
     * related subcommands.</p>
     *
     * @return <code>true</code> if the command uses only subcommands and has no
     *         standalone execution logic; <code>false</code> otherwise.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
