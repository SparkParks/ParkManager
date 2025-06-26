package network.palace.parkmanager.commands.showschedule;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * <p>
 * The {@code EditCommand} class represents a command for editing the show schedule
 * in the application. It extends the {@code CoreCommand} class and is responsible
 * for providing an interface to modify the show schedule.
 * </p>
 *
 * <p>
 * When executed, this command opens the show schedule edit menu for the player,
 * allowing them to make changes to the schedule.
 * </p>
 *
 * <h2>Command Details</h2>
 * <ul>
 *   <li><b>Name:</b> {@code edit}</li>
 *   <li><b>Description:</b> Edit the show schedule.</li>
 * </ul>
 *
 * <h2>Functional Behavior</h2>
 * <ul>
 *   <li>Notifies the player that the edit menu is being opened using a message.</li>
 *   <li>Invokes the {@code editSchedule()} method from the {@code ScheduleManager}
 *       to provide the editing functionality.</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * <ul>
 *   <li>If there are issues with the command execution, a {@code CommandException} may be thrown.</li>
 * </ul>
 */
@CommandMeta(description = "Edit the show schedule")
public class EditCommand extends CoreCommand {

    /**
     * <p>
     * Initializes a new instance of the {@code EditCommand} class. This command allows
     * players to modify the current show schedule through an editing interface.
     * </p>
     *
     * <h2>Command Information</h2>
     * <ul>
     *   <li><b>Command Name:</b> {@code edit}</li>
     *   <li><b>Description:</b> Opens the show schedule edit menu for modification.</li>
     * </ul>
     *
     * <p>
     * This constructor registers the command with the key {@code "edit"}. It is used as a sub-command
     * of {@code ShowScheduleCommand} to provide editing capabilities for the show schedule.
     * </p>
     *
     * <h2>Usage Context</h2>
     * <ul>
     *   <li>Typically used by authorized players to manage or modify the show's schedule.</li>
     *   <li>Part of the overall {@code ShowScheduleCommand} structure, leveraging sub-commands
     *       to organize functionality.</li>
     * </ul>
     */
    public EditCommand() {
        super("edit");
    }

    /**
     * Handles the execution of the "edit" command, which allows the player to access
     * the show schedule edit menu.
     *
     * <p>
     * This method performs the following actions:
     * <ul>
     *   <li>Sends a notification to the player indicating that the edit menu is being opened.</li>
     *   <li>Invokes the appropriate method in {@code ScheduleManager} to open the edit menu.</li>
     * </ul>
     * </p>
     *
     * @param player the player executing the command. Must not be {@code null}.
     * @param args   the arguments provided with the command. Can be an empty array but must not be {@code null}.
     *
     * @throws CommandException if there is any issue during the execution of the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Opening show schedule edit menu...");
        ParkManager.getScheduleManager().editSchedule(player);
    }
}
