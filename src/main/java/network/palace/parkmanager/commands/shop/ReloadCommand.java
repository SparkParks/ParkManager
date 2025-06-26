package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * Represents a command used to reload shop configurations from the filesystem.
 *
 * <p>When executed, this command reloads the shop data by reinitializing the shop manager.
 * It provides feedback to the player indicating the start and completion of the reload process.</p>
 *
 * <h3>Features:</h3>
 * <ul>
 *   <li>Allows on-the-fly reloading of shop configurations without server restarts.</li>
 *   <li>Sends confirmation messages to the player regarding the reload process.</li>
 * </ul>
 *
 * <p>This class extends {@code CoreCommand} and overrides the {@code handleCommand} method to
 * perform its specific functionality.</p>
 *
 * <h3>Command Details:</h3>
 * <ul>
 *   <li>Trigger: The command can be invoked with the name "reload".</li>
 *   <li>Access: This command should be executed by an authorized in-game {@code CPlayer} entity.</li>
 * </ul>
 *
 * <p>Note: Proper permission handling should be implemented in the {@code handleCommand} method
 * if necessary to restrict usage to specific players or roles.</p>
 */
@CommandMeta(description = "Reload shops from filesystem")
public class ReloadCommand extends CoreCommand {

    /**
     * Constructs a new {@code ReloadCommand} instance.
     *
     * <p>The {@code ReloadCommand} is used to reload shop configurations from the filesystem.
     * This command is designed to allow administrators or authorized players to reinitialize
     * shop data dynamically without restarting the server.</p>
     *
     * <p>The command provides the following features:
     * <ul>
     *   <li>Reloads all shop configurations through the shop manager.</li>
     *   <li>Sends feedback messages to the player about the reload process, indicating its start
     *       and successful completion.</li>
     * </ul>
     * </p>
     *
     * <p>The command is registered with the identifier {@code "reload"}, aligning with its intended
     * functionality for reloading shop data. Proper permissions should be enforced to ensure that
     * only authorized players can execute this command.</p>
     */
    public ReloadCommand() {
        super("reload");
    }

    /**
     * Handles the execution of the command to reload shop configurations from the filesystem.
     *
     * <p>This method is invoked when the reload command is executed. It performs the following actions:</p>
     * <ul>
     *   <li>Sends a message to the player indicating the start of the reload process.</li>
     *   <li>Reinitializes the shop manager to reload the shop configurations from the filesystem.</li>
     *   <li>Sends a confirmation message to the player upon successful completion of the reload.</li>
     * </ul>
     *
     * <p>Exceptions may be thrown if an issue occurs during the execution of the command, which should
     * be handled appropriately by the calling context.</p>
     *
     * @param player The player executing the command. This is the in-game entity who initiated the action.
     * @param args   An array of command arguments passed during the invocation. This can be used for
     *               additional options or parameters required by the command.
     * @throws CommandException if an error occurs during the command's execution or shop reinitialization.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Reloading shops from filesystem...");
        ParkManager.getShopManager().initialize();
        player.sendMessage(ChatColor.GREEN + "Finished reloading shops!");
    }
}
