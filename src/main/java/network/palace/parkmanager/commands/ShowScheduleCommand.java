package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.showschedule.EditCommand;
import network.palace.parkmanager.commands.showschedule.UpdateCommand;

/**
 * <p>
 * The <code>ShowScheduleCommand</code> class represents a command used to manage and display
 * the schedule of shows in the system. It supports sub-commands to perform specific actions
 * on the show schedule, such as editing and updating it.
 * </p>
 *
 * <h3>Sub-Commands:</h3>
 * <ul>
 *   <li><b>EditCommand:</b> Allows authorized users to edit the show schedule.</li>
 *   <li><b>UpdateCommand:</b> Reloads the show schedule from the database to ensure accuracy.</li>
 * </ul>
 *
 * <p>
 * This command is only functional through its sub-commands and does not perform any operation
 * directly when invoked.
 * </p>
 *
 * <h3>Behavior:</h3>
 * <ul>
 *   <li>This command is registered with the key <code>"showschedule"</code>.</li>
 *   <li>All related functionality is handled exclusively through the registered sub-commands.</li>
 * </ul>
 *
 * <p>
 * Users with the appropriate rank and permissions can execute the sub-commands to manage
 * the show schedule.
 * </p>
 */
@CommandMeta(description = "Show schedule command", rank = Rank.CM)
public class ShowScheduleCommand extends CoreCommand {

    /**
     * <p>
     * Constructs a new instance of the <code>ShowScheduleCommand</code>. This command is designed
     * to handle operations related to managing and displaying a show's schedule. It is registered
     * with the command key <code>"showschedule"</code>.
     * </p>
     *
     * <h3>Sub-Commands:</h3>
     * <ul>
     *   <li><b>EditCommand:</b> Enables authorized users to edit the current show schedule,
     *   providing a way to access and modify the schedule directly.</li>
     *   <li><b>UpdateCommand:</b> Updates the show's schedule by reloading the data from
     *   the database, ensuring the schedule is accurate and up to date.</li>
     * </ul>
     *
     * <p>
     * The <code>ShowScheduleCommand</code> itself does not execute any operations when used.
     * Instead, it relies entirely on its dedicated sub-commands to provide functionality.
     * </p>
     *
     * <p>
     * By initializing this command, the associated sub-commands (<code>EditCommand</code>
     * and <code>UpdateCommand</code>) are automatically registered and made available for use.
     * </p>
     */
    public ShowScheduleCommand() {
        super("showschedule");
        registerSubCommand(new EditCommand());
        registerSubCommand(new UpdateCommand());
    }

    /**
     * Indicates whether this command operates exclusively through sub-commands.
     *
     * <p>
     * This method returns a boolean value that specifies if the current command
     * does not perform any direct operation and instead delegates all functionality
     * to its sub-commands. This is useful for commands that serve as containers
     * for grouped functionalities organized under sub-commands.
     * </p>
     *
     * @return <code>true</code> if the command relies solely on sub-commands to carry
     *         out its functions, <code>false</code> otherwise.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
