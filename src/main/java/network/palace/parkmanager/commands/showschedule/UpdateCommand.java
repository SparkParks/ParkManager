package network.palace.parkmanager.commands.showschedule;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * Represents the "update" command for reloading the show schedule from the database.
 * <p>
 * This command allows administrators or authorized users to refresh the current
 * show schedule by updating it from the database. It ensures that the most recent
 * schedule data is reflected in the system without requiring a server restart.
 *
 * <h3>Command Behavior:</h3>
 * <ul>
 *   <li>Sends a notification to the player indicating that the update operation has started.</li>
 *   <li>Triggers the show schedule update by invoking the related manager from the {@code ParkManager}.</li>
 *   <li>Sends a confirmation message upon successful completion of the update process.</li>
 * </ul>
 *
 * <h3>Permissions:</h3>
 * <p>
 * Ensure that the command is executed by an appropriate user with necessary permissions
 * to perform updates.
 *
 * <h3>Errors:</h3>
 * <p>
 * If the operation encounters issues such as database connectivity problems
 * or lack of permissions, a {@code CommandException} may be thrown.
 */
@CommandMeta(description = "Reload the show schedule from the database")
public class UpdateCommand extends CoreCommand {

    /**
     * Initializes a new instance of the {@code UpdateCommand} class. This command is part of the
     * {@code ShowScheduleCommand} system and is designed to update the show schedule by reloading
     * data from the database.
     *
     * <p>
     * This constructor registers the command with the name {@code "update"}. It ensures that
     * administrators or authorized users have the ability to reload the latest schedule
     * data without restarting the system.
     * </p>
     *
     * <h2>Usage Context</h2>
     * <ul>
     *   <li>Typically used by system administrators or users with appropriate permissions
     *       to ensure the schedule reflects the current database state.</li>
     *   <li>Operates as a sub-command within the {@code ShowScheduleCommand}, alongside
     *       other related commands like {@code EditCommand}.</li>
     * </ul>
     *
     * <h2>Command Details</h2>
     * <ul>
     *   <li><b>Command Name:</b> {@code update}</li>
     *   <li><b>Description:</b> Reloads the show schedule from the database and applies
     *       the changes to the system.</li>
     *   <li><b>Associated Functionality:</b> Triggers an update process in the {@code ScheduleManager}
     *       to fetch and apply the latest schedule.</li>
     * </ul>
     *
     * <h2>Execution Behavior</h2>
     * <ul>
     *   <li>Sends a notification to the user that the update has started.</li>
     *   <li>Performs the update by interaction with the system's {@code ScheduleManager}.</li>
     *   <li>Notifies the user upon successful completion of the update process.</li>
     * </ul>
     *
     * <h2>Implementation Notes</h2>
     * <ul>
     *   <li>This command is dependent on the {@code ParkManager}'s {@code ScheduleManager}
     *       for managing and applying updates to the schedule.</li>
     *   <li>Requires the user to have the appropriate permissions to execute this command;
     *       otherwise, a {@code CommandException} may be thrown.</li>
     * </ul>
     */
    public UpdateCommand() {
        super("update");
    }

    /**
     * Handles the "update" command to reload the show schedule from the database.
     * <p>
     * This method performs the following steps:
     * <ul>
     *   <li>Notifies the player that the update process has started.</li>
     *   <li>Invokes the show schedule update logic via the {@link ParkManager}'s {@code ScheduleManager}.</li>
     *   <li>Confirms to the player when the update process has completed successfully.</li>
     * </ul>
     *
     * @param player the player executing the command. This is the user who initiated
     *               the "update" command and will receive feedback messages about the
     *               operation.
     * @param args   an array of command arguments provided by the player. This method
     *               does not currently utilize these arguments but requires them as
     *               part of the command signature.
     * @throws CommandException if there is an issue during the execution of the command,
     *                          such as a failure to update the schedule (e.g., database
     *                          errors, lack of permissions, etc.).
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Updating the show schedule...");
        ParkManager.getScheduleManager().updateShows();
        player.sendMessage(ChatColor.GREEN + "Show schedule updated!");
    }
}
