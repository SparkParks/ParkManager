package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.config.SpawnCommand;
import network.palace.parkmanager.commands.config.*;

/**
 * <p>
 * The <code>ParkConfigCommand</code> class provides an entry point for managing various
 * park configuration settings in the application. It is a core command that operates
 * primarily through sub-commands, which handle specific features and configuration tasks.
 * </p>
 *
 * <p>
 * This class registers the following sub-commands for managing park-related configurations:
 * </p>
 * <ul>
 *   <li><b>JoinMessageCommand:</b> Handle the configuration and modification of the message
 *   players receive when they join the server.</li>
 *   <li><b>ParkCommand:</b> Manage park settings, including park creation, listing, reloading,
 *   and removal.</li>
 *   <li><b>SpawnCommand:</b> Set the spawn location for the server.</li>
 *   <li><b>SpawnOnJoinCommand:</b> Configure whether players will spawn at the server's spawn
 *   location upon joining the server.</li>
 *   <li><b>WarpOnJoinCommand:</b> Configure whether players will be warped to a specific location
 *   upon joining the server.</li>
 * </ul>
 *
 * <p>
 * This class overrides the <code>isUsingSubCommandsOnly</code> method to ensure that the primary
 * <code>ParkConfigCommand</code> can only be executed via its sub-commands. Direct execution without
 * a sub-command is not supported.
 * </p>
 *
 * <p>
 * The <code>ParkConfigCommand</code> class provides a modular approach to managing various
 * park configuration tasks within the server environment, separating concerns and allowing
 * for scalability and clear command management.
 * </p>
 *
 * <p><strong>Primary Command Name:</strong> "parkconfig"</p>
 */
@CommandMeta(description = "Manage park config settings", rank = Rank.CM)
public class ParkConfigCommand extends CoreCommand {

    /**
     * <p>
     * Constructs the <code>ParkConfigCommand</code> instance and registers sub-commands
     * related to park configuration management. These sub-commands allow for fine-grained
     * control of various park-related features within the server environment.
     * </p>
     *
     * <p>
     * The following sub-commands are registered by this constructor:
     * </p>
     * <ul>
     *   <li><b>JoinMessageCommand:</b> Manages the customization of the join message
     *   displayed to players when they enter the server.</li>
     *   <li><b>ParkCommand:</b> Provides functionality for managing parks, including creation,
     *   removal, listing, and reloading park configurations.</li>
     *   <li><b>SpawnCommand:</b> Allows administrators to set the server's default spawn location.</li>
     *   <li><b>SpawnOnJoinCommand:</b> Enables or disables the functionality to spawn
     *   players at the server's spawn location when they join.</li>
     *   <li><b>WarpOnJoinCommand:</b> Enables or disables the feature to warp players
     *   to a designated location upon joining the server.</li>
     * </ul>
     *
     * <p>
     * This ensures that the <code>ParkConfigCommand</code> acts solely as a base command,
     * delegating its functionality to the respective sub-commands for better modularity
     * and maintainability.
     * </p>
     */
    public ParkConfigCommand() {
        super("parkconfig");
        registerSubCommand(new JoinMessageCommand());
        registerSubCommand(new ParkCommand());
        registerSubCommand(new SpawnCommand());
        registerSubCommand(new SpawnOnJoinCommand());
        registerSubCommand(new WarpOnJoinCommand());
    }

    /**
     * Determines whether this command is restricted to execution via sub-commands only.
     *
     * <p>
     * This method ensures that the primary command cannot be executed directly and must be
     * invoked through one of its registered sub-commands. This is useful when the main command
     * serves as an entry point to a set of distinct functionalities, each implemented as
     * separate sub-commands.
     * </p>
     *
     * @return <code>true</code> if the command is restricted to sub-command usage only;
     *         <code>false</code> otherwise.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
