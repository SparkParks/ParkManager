package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;

/**
 * <p>
 * The {@code EmptyCommand} class is used to empty a queue associated with a park.
 * </p>
 *
 * <p>
 * This command allows players to specify a queue by its ID and clear all its contents.
 * It enforces that the player executing the command is currently within a park.
 * If the park or queue is not found, appropriate error messages are sent to the player.
 * </p>
 *
 * <h3>Command Usage</h3>
 * <ul>
 * <li>Command name: {@code empty}</li>
 * <li>Arguments: {@code [queue-id]}</li>
 * </ul>
 * <p>
 * The command requires players to provide a valid queue ID, which can be retrieved
 * via the {@code /queue list} command. This ensures that the player knows which
 * queue they are trying to empty.
 * </p>
 *
 * <h3>Error Handling</h3>
 * <p>
 * Several checks and validations are built into the command:
 * </p>
 * <ul>
 * <li>If no queue ID is provided, the player is prompted with the correct usage format.</li>
 * <li>If the player is not within a park, an error message is displayed indicating
 * that the command must be run from within a park boundary.</li>
 * <li>If the specified queue ID is invalid or does not match any existing queue
 * within the park, the player is notified with an appropriate error message.</li>
 * </ul>
 *
 * <h3>Command Behavior</h3>
 * <p>
 * Upon successful execution:
 * </p>
 * <ul>
 * <li>The queue with the specified ID is emptied.</li>
 * <li>A success message is sent to the player indicating the cleared queue's name.</li>
 * </ul>
 *
 * <h3>Exception Handling</h3>
 * <p>
 * The command may throw a {@link CommandException} in case of internal command
 * handling errors or unexpected issues during its execution.
 * </p>
 */
@CommandMeta(description = "Empty a queue")
public class EmptyCommand extends CoreCommand {

    /**
     * <p>
     * Constructs an instance of the {@code EmptyCommand} class.
     * </p>
     *
     * <p>
     * This constructor initializes the command with its predefined name {@code "empty"}.
     * The command is designed to handle functionality related to clearing the content
     * of a specific queue within a park.
     * </p>
     *
     * <h3>Purpose of the Command</h3>
     * <ul>
     * <li>Associates the {@code empty} command with the corresponding functionality
     * of removing all individuals or entities from a designated queue.</li>
     * </ul>
     *
     * <p>
     * This constructor is typically used during the initialization of command
     * registration within the broader park management system.
     * </p>
     */
    public EmptyCommand() {
        super("empty");
    }

    /**
     * Handles the "empty" command to empty a specific queue in a park.
     *
     * <p>
     * The method validates player input, ensures the player is within a park,
     * and attempts to find the queue by the provided ID. If validation checks fail
     * or the queue is not found, appropriate error messages are returned to the player.
     * Upon successful execution, the queue is emptied, and the player is informed.
     * </p>
     *
     * @param player The {@link CPlayer} executing the command. The player's location and permissions
     *               may be used to determine eligibility for the command and its effects.
     * @param args   A {@code String[]} containing the command arguments. This includes the queue ID
     *               required to identify the specific queue to empty. If no ID is provided, a usage
     *               message is displayed to the player.
     *
     * @throws CommandException Thrown if any internal error or unexpected issue occurs during command
     *                          execution.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/queue empty [id]");
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
        queue.emptyQueue();
        player.sendMessage(ChatColor.GREEN + "The queue for " + queue.getName() + ChatColor.GREEN + " has been emptied!");
    }
}
