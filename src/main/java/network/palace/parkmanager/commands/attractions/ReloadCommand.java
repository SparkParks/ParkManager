package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.AttractionManager;
import org.bukkit.ChatColor;

/**
 * Represents a command to reload attractions from the filesystem.
 * <p>
 * This command is used to reinitialize attraction data in the system by
 * loading it again from the filesystem. Users executing this command
 * will receive status feedback during the reload process.
 * </p>
 * <p>
 * Key features:
 * <ul>
 *   <li>Triggers reloading of attraction data via {@code ParkManager}'s {@code AttractionManager}.</li>
 *   <li>Provides feedback messages to the executing player.</li>
 *   <li>Ensures that all attraction data is refreshed in the application during runtime.</li>
 * </ul>
 * </p>
 * <p>
 * Permission to use this command and relevant exception handling
 * should be implemented as part of the overall system design.
 * </p>
 */
@CommandMeta(description = "Reload attractions from filesystem")
public class ReloadCommand extends CoreCommand {

    /**
     * Creates a new instance of the {@code ReloadCommand}.
     * <p>
     * This command is responsible for reloading the attraction data from the filesystem,
     * ensuring that the system has the most up-to-date information available during
     * runtime. It is typically called when an update or reinitialization of the attraction
     * data is required.
     * </p>
     * <p>
     * The command's behavior includes:
     * <ul>
     *   <li>Triggering the attraction data reload using the appropriate data manager.</li>
     *   <li>Providing feedback to the user about the initiation and completion of the reload process.</li>
     * </ul>
     * </p>
     * <p>
     * Proper permissions should be enforced for players executing this command, alongside any relevant
     * exception handling mechanisms, to ensure the command can only be used by authorized users.
     * </p>
     */
    public ReloadCommand() {
        super("reload");
    }

    /**
     * Handles the execution of the "reload" command, which reloads attraction data from the filesystem.
     * <p>
     * This method interacts with the {@link ParkManager}'s {@link AttractionManager}
     * to reinitialize all attraction data. It provides status updates to the executing player.
     * Use this command to update the application's runtime data related to attractions.
     * </p>
     *
     * @param player the player executing the command; receives feedback messages during the process
     * @param args   the arguments provided with the command; not expected to contain additional parameters here
     * @throws CommandException if there is an issue executing the command, such as missing rights or internal errors
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Reloading attractions from filesystem...");
        ParkManager.getAttractionManager().initialize();
        player.sendMessage(ChatColor.GREEN + "Finished reloading attractions!");
    }
}
