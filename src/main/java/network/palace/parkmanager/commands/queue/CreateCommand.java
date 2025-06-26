package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.queues.QueueBuilder;
import org.bukkit.ChatColor;

/**
 * The <code>CreateCommand</code> class represents a command responsible for initiating
 * the creation of a new queue in the system. This command is executed by players
 * and provides a step-by-step guided process for creating custom queues.
 *
 * <p>This command requires the player to be within a valid park. It also supports
 * the ability to exit the queue-building process at any time using a specific
 * command argument.</p>
 *
 * <p><b>Key features of this command:</b></p>
 * <ul>
 *   <li>Initiates the queue-building process using <code>QueueBuilder</code>.</li>
 *   <li>Allows a player to specify an identifier (<code>id</code>) for the queue during creation.</li>
 *   <li>Handles scenarios where a player opts to exit the builder process mid-way.</li>
 *   <li>Ensures that the command is only executed within the bounds of a valid park.</li>
 * </ul>
 *
 * <p><b>When the command is executed:</b>
 * <ul>
 *   <li>If the player provides the "exit" argument, the queue-building process is canceled, and the player is notified.</li>
 *   <li>If the player is already in an active queue-building session, the next step of the builder is prompted.</li>
 *   <li>If no queue-building session exists for the player, a new session is initiated, allowing the creation of a new queue tied to the park the player is currently in.</li>
 * </ul>
 * </p>
 *
 * <p><b>Dependencies:</b>
 * <ul>
 *   <li><code>QueueBuilder</code>: Manages the step-by-step process of constructing a queue.</li>
 *   <li><code>ParkManager</code>: Provides utility functions to fetch the current park context for the player.</li>
 *   <li><code>CPlayer</code>: Represents the player entity interacting with the command.</li>
 * </ul>
 * </p>
 *
 * <p>This command extends the <code>CoreCommand</code> class and follows
 * the structure of the core command-handling system by overriding the <code>handleCommand</code>
 * method.</p>
 */
@CommandMeta(description = "Create a new queue")
public class CreateCommand extends CoreCommand {

    /**
     * Constructs a new instance of the <code>CreateCommand</code> class.
     * <p>
     * The command is registered with the keyword "create."
     * </p>
     * <p>
     * This command is intended to facilitate the creation of queues within a
     * specified park environment.
     * </p>
     * <ul>
     *     <li>Integrates into a command-handling framework for ease of usage.</li>
     *     <li>Operates within the context of a player and their current park location.</li>
     * </ul>
     * <p>
     * Further functionality and behavioral specifics of this command are defined
     * within any overridden methods in the class.
     * </p>
     */
    public CreateCommand() {
        super("create");
    }

    /**
     * Handles the command given by the player to manage queue creation.
     * This method processes the player input and either initializes the queue
     * creation process, proceeds with the next step in an ongoing queue creation,
     * or ends the queue creation process if specified by the input arguments.
     *
     * <p>This command execution ensures the following:</p>
     * <ul>
     *   <li>Allows a player to exit the queue builder with the "exit" command.</li>
     *   <li>Proceeds with the next step if the player is already in a queue creation process.</li>
     *   <li>Validates if the player is inside a park before starting a new queue builder.</li>
     *   <li>Initializes and registers a new queue builder for the player.</li>
     * </ul>
     *
     * @param player The player executing the command. Necessary for context about the
     *               command's origin and modifications to the player's state or registry.
     * @param args   The arguments passed along with the command. Validates command
     *               options such as "exit" or parameters for queue creation steps.
     * @throws CommandException If command execution encounters an issue, such as a
     *                          missing park context or unexpected errors in the process.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 1 && args[0].equalsIgnoreCase("exit")) {
            player.sendMessage(ChatColor.RED + "Exited the queue builder!");
            player.getRegistry().removeEntry("queueBuilder");
            return;
        }
        if (player.getRegistry().hasEntry("queueBuilder")) {
            ((QueueBuilder) player.getRegistry().getEntry("queueBuilder")).nextStep(player, args);
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        QueueBuilder queue = new QueueBuilder(park.getId());
        player.getRegistry().addEntry("queueBuilder", queue);
        player.sendMessage(ChatColor.GREEN + "You've started creating a new queue! We're going to go step-by-step through this. " + ChatColor.YELLOW + "(Exit at any time with /queue create exit)");
        player.sendMessage(ChatColor.GREEN + "First, let's give your queue an id. This id is used to reference this queue in commands. Run " + ChatColor.YELLOW + "/queue create [id]");
    }
}
