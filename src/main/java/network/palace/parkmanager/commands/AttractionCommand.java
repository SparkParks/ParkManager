package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.attractions.*;

/**
 * Represents the main command handler for managing attractions within a park.
 * The {@code AttractionCommand} class provides functionality for handling several
 * sub-commands related to the creation, modification, management, and querying of attractions.
 *
 * <p>This command serves as the entry point for attraction-related operations, such as:</p>
 * <ul>
 *     <li>Listing available attraction categories (via {@code CategoriesCommand}).</li>
 *     <li>Creating a new attraction (via {@code CreateCommand}).</li>
 *     <li>Opening, closing, and editing existing attractions (via {@code OpenCommand}, {@code CloseCommand}, and {@code EditCommand}).</li>
 *     <li>Linking and unlinking queues to/from attractions (via {@code LinkQueueCommand} and {@code UnlinkCommand}).</li>
 *     <li>Viewing a list of existing attractions (via {@code ListCommand}).</li>
 *     <li>Reloading attraction data (via {@code ReloadCommand}).</li>
 *     <li>Removing an attraction (via {@code RemoveCommand}).</li>
 * </ul>
 *
 * <p>By default, this class is configured to only utilize sub-commands for its operations,
 * ensuring a structured and organized command framework for all attraction-related features.</p>
 *
 * <p>The sub-commands registered within this class include:</p>
 * <ul>
 *     <li>{@code CategoriesCommand}</li>
 *     <li>{@code CloseCommand}</li>
 *     <li>{@code CreateCommand}</li>
 *     <li>{@code EditCommand}</li>
 *     <li>{@code LinkQueueCommand}</li>
 *     <li>{@code ListCommand}</li>
 *     <li>{@code OpenCommand}</li>
 *     <li>{@code ReloadCommand}</li>
 *     <li>{@code RemoveCommand}</li>
 *     <li>{@code UnlinkCommand}</li>
 * </ul>
 *
 */
@CommandMeta(description = "Attraction command", rank = Rank.CM)
public class AttractionCommand extends CoreCommand {

    /**
     * Constructs a new {@code AttractionCommand} instance and registers all related sub-commands.
     *
     * <p>The {@code AttractionCommand} serves as the main entry point for executing and handling
     * various operations related to attractions within a park. Upon initialization, this command
     * registers a set of sub-commands to handle specific functionalities.</p>
     *
     * <p>The sub-commands registered include:</p>
     * <ul>
     *     <li><b>CategoriesCommand</b>: Lists available attraction categories.</li>
     *     <li><b>CloseCommand</b>: Closes an attraction.</li>
     *     <li><b>CreateCommand</b>: Creates a new attraction.</li>
     *     <li><b>EditCommand</b>: Edits an existing attraction.</li>
     *     <li><b>LinkQueueCommand</b>: Links a queue to an attraction.</li>
     *     <li><b>ListCommand</b>: Displays a list of existing attractions.</li>
     *     <li><b>OpenCommand</b>: Opens an attraction.</li>
     *     <li><b>ReloadCommand</b>: Reloads attraction-related data from storage.</li>
     *     <li><b>RemoveCommand</b>: Removes an attraction.</li>
     *     <li><b>UnlinkCommand</b>: Unlinks a queue from an attraction.</li>
     * </ul>
     *
     * <p>Each registered sub-command provides functionality for a specific aspect of attraction
     * management, ensuring that all operations are modular and easy to maintain.</p>
     */
    public AttractionCommand() {
        super("attraction");
        registerSubCommand(new CategoriesCommand());
        registerSubCommand(new CloseCommand());
        registerSubCommand(new CreateCommand());
        registerSubCommand(new EditCommand());
        registerSubCommand(new LinkQueueCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new OpenCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new RemoveCommand());
        registerSubCommand(new UnlinkCommand());
    }

    /**
     * Indicates if the command relies exclusively on sub-commands for its functionality.
     *
     * <p>This method is typically overridden in commands that function purely as containers for
     * related sub-commands, delegating all operations to those sub-commands rather than
     * implementing any direct functionality themselves.</p>
     *
     * <p>When this method returns {@code true}, the command is expected to have registered
     * relevant sub-commands that handle all specific interactions or operations.</p>
     *
     * @return {@code true} if the command uses only sub-commands; {@code false} otherwise.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
