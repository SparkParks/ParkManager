package network.palace.parkmanager.commands.config.park;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * <p>
 * The {@code ReloadCommand} class is a command implementation for reloading
 * park configurations from the filesystem. It extends the {@code CoreCommand}
 * class and provides the logic necessary to refresh park information used in
 * the application.
 * </p>
 *
 * <p>
 * This command is executed by authorized users to ensure the park data is
 * up-to-date without restarting the application. When triggered, it
 * initializes all park configurations using the {@code ParkManager}'s utility
 * class.
 * </p>
 *
 * <h3>Command Description</h3>
 * <ul>
 * <li>Command keyword: {@code reload}</li>
 * <li>Functionality: Reloads all park data from the filesystem.</li>
 * <li>Feedback: Provides status messages to indicate the start and completion
 * of the reload process.</li>
 * </ul>
 *
 * <h3>Expected Behavior</h3>
 * <ul>
 * <li>Sends a confirmation message to the user when the reload process starts.</li>
 * <li>Triggers the reinitialization of park data via the {@code ParkManager}.</li>
 * <li>Sends a completion message upon successful completion of the reload
 * process.</li>
 * <li>Handles potential exceptions during command execution.</li>
 * </ul>
 */
@CommandMeta(description = "Reload parks from filesystem")
public class ReloadCommand extends CoreCommand {

    /**
     * <p>
     * Constructs a new {@code ReloadCommand} instance. This command is designed
     * to reload all park configurations from the filesystem, ensuring that the
     * latest data is reflected in the application without requiring a complete
     * server or application restart.
     * </p>
     *
     * <p>
     * By invoking this command, authorized users can reinitialize the park
     * configurations managed by the {@code ParkManager}. This is particularly
     * useful for scenarios where changes to park data have been made externally
     * and need to be applied dynamically.
     * </p>
     *
     * <h2>Key Characteristics:</h2>
     * <ul>
     *   <li>Initializes the command with the label <strong>"reload"</strong>.</li>
     *   <li>Extends the {@code CoreCommand} class to inherit core command-handling
     *       functionality.</li>
     *   <li>Prepares the infrastructure for executing the reload process.</li>
     * </ul>
     *
     * <h2>Behavior:</h2>
     * <ul>
     *   <li>Triggers the park reinitialization process.</li>
     *   <li>Provides feedback messages to indicate the start and completion of
     *       the reload process.</li>
     *   <li>Handles potential exceptions during the execution of the reload logic.</li>
     * </ul>
     */
    public ReloadCommand() {
        super("reload");
    }

    /**
     * Handles the execution of the reload command for reloading park data from the
     * filesystem. This method provides feedback to the player indicating the
     * start and completion of the reload process. The actual reinitialization
     * of park data is delegated to the {@code ParkManager}'s utility class.
     *
     * <p>
     * When invoked, this method performs the following tasks:
     * <ul>
     *   <li>Sends a "start reload" message to the player.</li>
     *   <li>Triggers the initialization of park data via {@code ParkManager.getParkUtil().initialize()}.</li>
     *   <li>Sends a "reload complete" message to the player.</li>
     * </ul>
     * </p>
     *
     * @param player The player who executed the command, used for displaying feedback messages.
     * @param args   The command arguments provided by the player.
     * @throws CommandException If an error occurs during execution of the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Reloading parks from filesystem...");
        ParkManager.getParkUtil().initialize();
        player.sendMessage(ChatColor.GREEN + "Finished reloading parks!");
    }
}
