package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.food.CreateCommand;
import network.palace.parkmanager.commands.food.ListCommand;
import network.palace.parkmanager.commands.food.ReloadCommand;
import network.palace.parkmanager.commands.food.RemoveCommand;

/**
 * Represents the Food command in the application, which serves as the main entry point for managing food locations.
 * This command allows users to execute a variety of subcommands that include functionalities such as creating, listing,
 * reloading, and removing food locations.
 *
 * <p><b>Overview of Subcommands:</b></p>
 * <ul>
 *     <li><b>CreateCommand:</b> Creates a new food location. Requires the user to provide an ID, warp, name,
 *     and hold the item representing the food location.</li>
 *     <li><b>ListCommand:</b> Lists all food locations for the park the user is currently in.</li>
 *     <li><b>ReloadCommand:</b> Reloads all food locations from the filesystem to refresh or update
 *     the data without restarting the application.</li>
 *     <li><b>RemoveCommand:</b> Removes an existing food location by its unique ID in a specific park.</li>
 * </ul>
 *
 * <p>This command operates only with its registered subcommands and does not allow direct execution without a subcommand.</p>
 *
 * <p><b>Command Registration:</b></p>
 * <ul>
 *     <li><code>CreateCommand</code></li>
 *     <li><code>ListCommand</code></li>
 *     <li><code>ReloadCommand</code></li>
 *     <li><code>RemoveCommand</code></li>
 * </ul>
 *
 * <p><b>Usage:</b> The command is executed in the format <code>/food &lt;subcommand&gt; [arguments]</code>, where each
 * subcommand handles its own specific task along with its required arguments.</p>
 *
 * <p><b>Command Metadata:</b></p>
 * <ul>
 *     <li><b>Description:</b> Food location management command</li>
 *     <li><b>Rank Required:</b> CM</li>
 * </ul>
 */
@CommandMeta(description = "Food location command", rank = Rank.CM)
public class FoodCommand extends CoreCommand {

    /**
     * Constructs a new FoodCommand instance, representing the main command for managing food locations
     * within the application. This command is designed to serve as a parent command, providing access
     * to a set of subcommands for specific tasks.
     *
     * <p><b>Subcommands Registered:</b></p>
     * <ul>
     *     <li><b>CreateCommand:</b> Allows users to create a new food location within a park.</li>
     *     <li><b>ListCommand:</b> Provides a listing of all food locations in the current park.</li>
     *     <li><b>ReloadCommand:</b> Reloads food locations from the filesystem, ensuring the data is up-to-date.</li>
     *     <li><b>RemoveCommand:</b> Facilitates the removal of an existing food location by its ID.</li>
     * </ul>
     *
     * <p>The FoodCommand operates as a parent command that delegates functionality to its subcommands.
     * Direct usage of the parent command alone is not permitted; users must utilize one of the registered
     * subcommands.</p>
     *
     * <p><b>Key Details:</b></p>
     * <ul>
     *     <li>The command keyword is "food".</li>
     *     <li>This command supports only subcommand-based execution.</li>
     * </ul>
     */
    public FoodCommand() {
        super("food");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new RemoveCommand());
    }

    /**
     * Indicates whether this command is designed to operate exclusively using subcommands.
     *
     * <p>This method specifies that the current command does not support direct execution,
     * and all functionality must be accessed through defined subcommands.</p>
     *
     * <p><b>Key Details:</b></p>
     * <ul>
     *     <li>This ensures that the command works strictly as a parent command.</li>
     *     <li>All functionality is delegated to specific subcommands.</li>
     * </ul>
     *
     * @return {@code true} if the command supports only subcommand-based execution; otherwise {@code false}.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
