package network.palace.parkmanager.commands.food;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.food.FoodManager;
import org.bukkit.ChatColor;

/**
 * Represents a command to reload food locations from the filesystem.
 * <p>
 * This command is used to reinitialize the food location data by loading it from
 * the filesystem. It ensures that the latest updates or changes to the food
 * locations are reflected without requiring a full system restart.
 * </p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Reloads the food locations by invoking the {@code initialize} method of the
 *   {@link FoodManager}.</li>
 *   <li>Sends feedback messages to the player indicating the start and completion
 *   of the reload process.</li>
 *   <li>Extends the {@code CoreCommand} class to provide base command functionality.</li>
 * </ul>
 *
 * <p><strong>Command Syntax:</strong></p>
 * <ul>
 *   <li>Command name: {@code reload}</li>
 * </ul>
 */
@CommandMeta(description = "Reload food locations from filesystem")
public class ReloadCommand extends CoreCommand {

    /**
     * Constructs a new <code>ReloadCommand</code> instance.
     * <p>
     * Initializes the command with the identifier <strong>"reload"</strong>, which is passed to the parent
     * <code>CoreCommand</code> class. This command is designed to reload the food location data from the filesystem,
     * ensuring that the latest updates or modifications to the data set are reflected in the system.
     * </p>
     *
     * <h2>Purpose:</h2>
     * <p>
     * The <code>ReloadCommand</code> aims to provide a straightforward mechanism for reinitializing food locations
     * without requiring a system restart. It allows for updating data dynamically during runtime.
     * </p>
     *
     * <h2>Usage:</h2>
     * <ul>
     *     <li>Allows administrators or authorized users to reload food location data from the filesystem.</li>
     *     <li>Improves system flexibility by applying data updates in real-time.</li>
     * </ul>
     *
     * <h2>Key Behavior:</h2>
     * <ul>
     *     <li>Associates the command name <strong>"reload"</strong> with its functionality.</li>
     *     <li>Provides the starting point for executing the reload process.</li>
     * </ul>
     */
    public ReloadCommand() {
        super("reload");
    }

    /**
     * Handles the execution of the "reload" command.
     * <p>
     * This method reloads the food locations data from the filesystem by
     * invoking the initialization method of the {@code FoodManager}. During this process,
     * it provides feedback to the player about the reload status.
     * </p>
     *
     * @param player The player executing the command. This parameter is used
     *               to send feedback messages regarding the command execution process.
     * @param args   The arguments provided with the command.
     *               These are currently unused in this implementation.
     * @throws CommandException If an error occurs during command execution
     *                          (e.g., issues with filesystem access).
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Reloading food locations from filesystem...");
        ParkManager.getFoodManager().initialize();
        player.sendMessage(ChatColor.GREEN + "Finished reloading food locations!");
    }
}
