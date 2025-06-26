package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * <p>The {@code ReloadCommand} class represents a command for reloading queues
 * from the filesystem. It is a subclass of the {@code CoreCommand} class and
 * is designed to be invoked by the player.</p>
 *
 * <p>This command performs the following actions:</p>
 * <ul>
 *   <li>Sends a feedback message to the player, indicating the start of the reloading process.</li>
 *   <li>Invokes the {@code initialize} method on the {@code QueueManager} instance
 *       from the {@code ParkManager} to reload the data from the filesystem.</li>
 *   <li>Sends a feedback message to the player, confirming the completion of the reloading process.</li>
 * </ul>
 *
 * <p>The command keyword for invoking this action is <b>"reload"</b>.</p>
 */
@CommandMeta(description = "Reload queues from filesystem")
public class ReloadCommand extends CoreCommand {

    /**
     * <p>Constructs a new {@code ReloadCommand} instance, initializing the command
     * with the keyword <b>"reload"</b>.</p>
     *
     * <p>This constructor sets up the command to facilitate the reloading of
     * queues from the filesystem using functionality provided within the
     * {@code ParkManager} class.</p>
     */
    public ReloadCommand() {
        super("reload");
    }

    /**
     * Handles the "reload" command issued by a player. This command reloads queues
     * from the filesystem by reinitializing the {@code QueueManager}.
     * <p>
     * The method sends feedback messages to the player when the process begins and
     * completes, providing a user-friendly response.
     * </p>
     *
     * @param player The player executing the command. The {@code CPlayer} object represents
     *               the sender of the command and is used to send feedback messages.
     * @param args   An array of arguments provided with the command. This method does not
     *               process any arguments and expects none.
     *
     * @throws CommandException If any error occurs during the execution of the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Reloading queues from filesystem...");
        ParkManager.getQueueManager().initialize();
        player.sendMessage(ChatColor.GREEN + "Finished reloading queues!");
    }
}
