package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.vqueue.*;

/**
 * Represents a command that manages virtual queues on a server. This command groups subcommands
 * to perform actions such as creating, opening, closing, listing, advancing, or announcing virtual queues.
 *
 * <p>The {@code VirtualQueueCommand} is used to handle the primary {@code /vqueue} command and delegates
 * functionality to multiple subcommands. This structure allows administrators or authorized users
 * to manage virtual queues effectively.
 *
 * <p>Key functionalities available through subcommands include:
 * <lu>
 *   <li>{@code CreateCommand} - To create a new virtual queue.</li>
 *   <li>{@code OpenCommand} - To open a specific virtual queue for users.</li>
 *   <li>{@code CloseCommand} - To close an already opened virtual queue.</li>
 *   <li>{@code AnnounceCommand} - To broadcast a virtual queue announcement.</li>
 *   <li>{@code AdvanceCommand} - To advance participants in the virtual queue.</li>
 *   <li>{@code ListCommand} - To list all available virtual queues on the server.</li>
 *   <li>{@code RemoveCommand} - To remove or delete a virtual queue from the system.</li>
 * </lu>
 *
 * <p>This command only operates through its registered subcommands and does not handle commands directly,
 * as indicated by the {@code isUsingSubCommandsOnly()} override.
 */
@CommandMeta(rank = Rank.CM, aliases = "vq")
public class VirtualQueueCommand extends CoreCommand {

    /**
     * Constructs a new instance of the {@code VirtualQueueCommand}, registering all associated subcommands
     * used for managing virtual queues on a server.
     *
     * <p>The {@code VirtualQueueCommand} serves as the parent command for the {@code /vqueue} command. It
     * organizes and delegates functionality to multiple subcommands, enabling efficient and modular
     * management of virtual queues.</p>
     *
     * <p>Upon construction, the following subcommands are registered:</p>
     * <ul>
     *   <li>{@code CreateCommand} - Allows the creation of new virtual queues.</li>
     *   <li>{@code OpenCommand} - Facilitates opening an existing virtual queue for user participation.</li>
     *   <li>{@code CloseCommand} - Enables closing an open virtual queue.</li>
     *   <li>{@code AnnounceCommand} - Handles broadcasting announcements about a virtual queue.</li>
     *   <li>{@code AdvanceCommand} - Progresses users in the virtual queue.</li>
     *   <li>{@code ListCommand} - Fetches a list of all available virtual queues.</li>
     *   <li>{@code RemoveCommand} - Removes or deletes a specified virtual queue.</li>
     * </ul>
     *
     * <p>These subcommands are designed to provide administrators or authorized users with a comprehensive
     * toolkit for managing virtual queues efficiently.</p>
     */
    public VirtualQueueCommand() {
        super("vqueue");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new OpenCommand());
        registerSubCommand(new CloseCommand());
        registerSubCommand(new AnnounceCommand());
        registerSubCommand(new AdvanceCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new RemoveCommand());
    }

    /**
     * Determines if the command exclusively relies on subcommands for operation.
     *
     * <p>This method indicates whether the base command itself performs any actions or
     * depends entirely on subcommands to implement functionality. If this method returns
     * {@code true}, the command only operates through its associated subcommands.
     *
     * @return {@code true} if the command uses subcommands exclusively; {@code false} otherwise.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
