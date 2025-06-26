package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;

/**
 * Represents a command to pause the movement of a specific queue within a park.
 * <p>
 * The {@code PauseCommand} is used to temporarily halt the operations of a queue in a park.
 * It can be executed by both a player and a block command sender.
 * This command requires a queue identifier ({@code id}) to specify the queue to be paused.
 * </p>
 *
 * <h3>Features:</h3>
 * <ul>
 *   <li>Pauses the specified queue in a park.</li>
 *   <li>Performs validations to ensure the command is executed inside a park.</li>
 *   <li>Notifies the sender of errors or successful command execution.</li>
 * </ul>
 *
 * <h3>Command Syntax:</h3>
 * <p>
 * <code>/queue pause [id]</code>
 * </p>
 * The {@code id} is expected to be the unique identifier of the queue, which can be obtained
 * via the {@code /queue list} command.
 *
 * <h3>Behavior:</h3>
 * <ul>
 *   <li>If no {@code id} parameter is provided, the command informs the sender of the proper syntax.</li>
 *   <li>The sender must be physically located within the bounds of a park to execute this command.</li>
 *   <li>If a queue corresponding to the given {@code id} cannot be found within the current park,
 *       an error notification is returned.</li>
 *   <li>Upon successfully pausing the queue, the sender will receive a confirmation message.</li>
 * </ul>
 */
@CommandMeta(description = "Pause movement of the queue")
public class PauseCommand extends CoreCommand {

    /**
     * Constructs a new instance of the {@code PauseCommand} class.
     * <p>
     * This command is designed to handle operations related to pausing a specific
     * system or functionality. It extends the base functionality provided by its
     * parent class and initializes the command with the name "pause".
     * <p>
     * Usage of this command may include scenarios such as:
     * <ul>
     * <li>Pausing an active process or service.</li>
     * <li>Interfacing with systems that support pause/resume workflows.</li>
     * </ul>
     * <p>
     * The command is to be executed within the defined system's context.
     */
    public PauseCommand() {
        super("pause");
    }

    /**
     * Handles the command to pause a specific queue within a player's current park.
     * <p>
     * The command requires a queue ID as an argument. The player must be inside a park when executing this command.
     * If the queue exists, it will be paused, and a confirmation message will be sent to the player.
     * If there are errors such as missing arguments, invalid park location, or non-existent queue ID,
     * appropriate error messages will be sent to the player.
     * </p>
     *
     * @param player The player executing the command. This provides context, such as the player's location and messaging capabilities.
     * @param args   The arguments passed with the command. It should include the queue ID to pause.
     * @throws CommandException If an error occurs when executing the command. This typically indicates a server-side problem.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/queue pause [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /queue list!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Queue queue = ParkManager.getQueueManager().getQueueById(args[0], park.getId());
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find a queue by id " + args[0] + "!");
            return;
        }
        queue.setPaused(true);
        player.sendMessage(queue.getName() + ChatColor.YELLOW + " has been paused!");
    }

    /**
     * Handles the `/queue pause` command to pause a queue in a park.
     * <p>
     * This method pauses a specific queue by its ID within a park, ensuring the command sender is inside a valid park
     * and the specified queue exists. If the command fails to meet these conditions, appropriate error messages
     * are sent back to the sender.
     * </p>
     *
     * @param sender The {@link BlockCommandSender} executing the command. This sender must be located within a park.
     * @param args   The command arguments provided. The first argument should be the ID of the queue to pause.
     *               <p>
     *               Expected usage: <lu>
     *               <li><code>/queue pause [id]</code></li>
     *               </lu>
     *               </p>
     *
     * @throws CommandException If an error occurs while executing the command.
     */
    @Override
    protected void handleCommand(BlockCommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/queue pause [id]");
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /queue list!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(sender.getBlock().getLocation());
        if (park == null) {
            sender.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Queue queue = ParkManager.getQueueManager().getQueueById(args[0], park.getId());
        if (queue == null) {
            sender.sendMessage(ChatColor.RED + "Could not find a queue by id " + args[0] + "!");
            return;
        }
        queue.setPaused(true);
        sender.sendMessage(queue.getName() + ChatColor.YELLOW + " has been paused!");
    }
}
