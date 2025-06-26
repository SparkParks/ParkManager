package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.queue.*;

/**
 * <p>
 * The {@code QueueCommand} class represents the main command handler for managing queues
 * in a theme park system. This command is further divided into multiple subcommands
 * to handle specific actions related to queue management.
 * </p>
 *
 * <p>
 * This class is designed to support a wide variety of operations for managing queues,
 * allowing fine-grained control for administrators or operators.
 * Each operation (e.g., closing a queue, creating a queue, editing properties) is
 * abstracted into specific subcommands for modular handling.
 * </p>
 *
 * <h2>Subcommands</h2>
 * <ul>
 * <li>{@code CloseCommand} – Used to close a currently open queue.</li>
 * <li>{@code CreateCommand} – Used to create a new queue.</li>
 * <li>{@code EditCommand} – Used to modify properties of an existing queue (e.g., ID, name, station).</li>
 * <li>{@code EmptyCommand} – Clears all entries in a queue.</li>
 * <li>{@code ListCommand} – Displays a list of queues with relevant details.</li>
 * <li>{@code OpenCommand} – Reopens a previously closed queue.</li>
 * <li>{@code PauseCommand} – Temporarily pauses processing for a queue.</li>
 * <li>{@code ReloadCommand} – Reloads the queue configuration from the data source.</li>
 * <li>{@code RemoveCommand} – Removes an existing queue from the system.</li>
 * <li>{@code UnpauseCommand} – Resumes a paused queue.</li>
 * </ul>
 *
 * <p>
 * This command and its subcommands interact with the park system, utilizing various
 * managers for queues, attractions, and parks to ensure proper functionality and data persistence.
 * </p>
 *
 * <h2>Behavior</h2>
 * <p>
 * {@link QueueCommand} serves as a parent command for all queue-related operations.
 * As a result, it enforces the use of subcommands to execute actions.
 * The command is specifically designed to leverage {@code isUsingSubCommandsOnly},
 * which restricts direct execution of the main command, ensuring all operations are managed
 * through subcommands.
 * </p>
 *
 * <h2>Command Registration</h2>
 * <p>
 * The {@code registerSubCommand} method is used to register all individual subcommands under
 * the {@code QueueCommand} namespace. This ensures modular handling of queue-related operations.
 * </p>
 */
@CommandMeta(description = "Queue command", rank = Rank.CM)
public class QueueCommand extends CoreCommand {

    /**
     * <p>
     * Constructs a new {@code QueueCommand} instance, representing the primary command
     * handler for managing queues in the system. This constructor initializes the {@code QueueCommand}
     * with a hierarchy of subcommands, each designed to handle a specific operation.
     * </p>
     *
     * <h2>Registered Subcommands</h2>
     * <ul>
     * <li>{@code CloseCommand} – Closes a currently open queue to block further entries.</li>
     * <li>{@code CreateCommand} – Initiates a step-by-step process for creating a new queue.</li>
     * <li>{@code EditCommand} – Provides options to update or modify an existing queue's properties.</li>
     * <li>{@code EmptyCommand} – Packs the functionality to clear all entries from a queue.</li>
     * <li>{@code ListCommand} – Displays a catalog of all queues with relevant details and statuses.</li>
     * <li>{@code OpenCommand} – Reopens a closed queue to allow new entries.</li>
     * <li>{@code PauseCommand} – Temporarily pauses the activity of a queue.</li>
     * <li>{@code ReloadCommand} – Reloads queue configurations from persistence storage.</li>
     * <li>{@code RemoveCommand} – Deletes an existing queue permanently from the system.</li>
     * <li>{@code UnpauseCommand} – Resumes operations for a paused queue.</li>
     * </ul>
     *
     * <p>
     * Within the {@code QueueCommand} structure, subcommands are registered via the
     * {@code registerSubCommand} method. These subcommands allow detailed control over
     * each aspect of queue management, ensuring modular and organized functionality.
     * </p>
     *
     * <h2>Behavior</h2>
     * <p>
     * By enforcing the {@code isUsingSubCommandsOnly} policy, {@code QueueCommand} enables
     * a streamlined approach to execution. This design mandates users to perform actions
     * exclusively through subcommands, optimizing the clarity and modular approach to queue operations.
     * </p>
     *
     * <h3>Purpose</h3>
     * <p>
     * The {@code QueueCommand} serves as a comprehensive parent command to facilitate
     * queue lifecycle management, making it essential for administering queues in the
     * associated system.
     * </p>
     */
    public QueueCommand() {
        super("queue");
        registerSubCommand(new CloseCommand());
        registerSubCommand(new CreateCommand());
        registerSubCommand(new EditCommand());
        registerSubCommand(new EmptyCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new OpenCommand());
        registerSubCommand(new PauseCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new RemoveCommand());
        registerSubCommand(new UnpauseCommand());
    }

    /**
     * <p>
     * Determines whether the command is restricted to using subcommands exclusively for
     * its operation. When this method returns {@code true}, the primary command does
     * not execute any functionality directly and delegates all operations to its
     * registered subcommands.
     * </p>
     *
     * <p>
     * This mechanism ensures that the command's functionality is modular, organized,
     * and strictly handled via predefined subcommands. It allows for consistent handling
     * and a streamlined approach to executing actions through the command hierarchy.
     * </p>
     *
     * @return {@code true} indicating that the command relies exclusively on
     * subcommands for execution, without allowing direct handling of tasks
     * within the primary command.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
