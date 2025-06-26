package network.palace.parkmanager.commands.config;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.commands.config.park.CreateCommand;
import network.palace.parkmanager.commands.config.park.ListCommand;
import network.palace.parkmanager.commands.config.park.ReloadCommand;
import network.palace.parkmanager.commands.config.park.RemoveCommand;

/**
 * <p>The <code>ParkCommand</code> class serves as a parent command
 * for managing parks in the system. It allows users to execute
 * various subcommands to manage parks effectively.</p>
 *
 * <p>This command is structured to work only through its registered subcommands
 * without directly handling commands itself.</p>
 *
 * <p>Subcommands include:</p>
 * <ul>
 *   <li><b>{@link CreateCommand}</b>: Creates a new park with a specific type, world, and region.</li>
 *   <li><b>{@link ListCommand}</b>: Lists all parks currently available on the server.</li>
 *   <li><b>{@link ReloadCommand}</b>: Reloads the parks from the filesystem configuration.</li>
 *   <li><b>{@link RemoveCommand}</b>: Removes an existing park by its unique id.</li>
 * </ul>
 *
 * <p>The <code>isUsingSubCommandsOnly</code> method is overridden to ensure
 * that this command does not handle commands directly,
 * instead relying on the subcommands listed above.</p>
 *
 * <p>The <code>CommandMeta</code> annotation describes the purpose of this
 * command: "Manage local parks".</p>
 */
@CommandMeta(description = "Manage local parks")
public class ParkCommand extends CoreCommand {

    /**
     * <p>Constructs a new <code>ParkCommand</code> instance, serving as the parent
     * command for managing various park-related operations within the system.</p>
     *
     * <p>This constructor initializes the <code>ParkCommand</code> by registering
     * several subcommands that handle specific tasks related to park management:</p>
     * <ul>
     *   <li><b>CreateCommand</b>: Allows the creation of a new park with specified attributes.</li>
     *   <li><b>ListCommand</b>: Lists all parks currently registered on the server.</li>
     *   <li><b>ReloadCommand</b>: Reloads the parks from the filesystem configuration, updating
     *   the system's park records.</li>
     *   <li><b>RemoveCommand</b>: Facilitates the removal of an existing park using its unique identifier.</li>
     * </ul>
     *
     * <p>The <code>ParkCommand</code> relies exclusively on these subcommands to
     * handle user operations, as indicated by the override of the
     * <code>isUsingSubCommandsOnly</code> method.</p>
     *
     * <p>This command is instrumental in organizing and managing parks through a
     * structured, hierarchical command system.</p>
     */
    public ParkCommand() {
        super("park");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new RemoveCommand());
    }

    /**
     * Determines if the command only supports execution through its registered subcommands.
     *
     * <p>This method is typically used to enforce the usage of subcommands
     * within a parent command, disallowing direct execution of the parent
     * command itself. It returns a boolean value indicating this behavior.</p>
     *
     * @return {@code true} if the command is intended to be used exclusively
     *         through its subcommands; {@code false} otherwise.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
