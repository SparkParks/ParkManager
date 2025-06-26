package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.queues.virtual.VirtualQueueBuilder;
import org.bukkit.ChatColor;

/**
 * The {@code CreateCommand} class handles the creation of new virtual queues hosted on the server.
 * It guides the player through a step-by-step process for configuring the virtual queue, including setting up its
 * ID, display name, holding area, and teleportation locations.
 * <p>
 * This command ensures that the virtual queue is properly configured before adding it to the system.
 * Players can exit the creation process anytime using the {@code /vqueue create exit} command.
 * </p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Initializes and manages the virtual queue creation process.</li>
 *   <li>Offers step-by-step guidance to players for setting up the queue parameters (ID, name, locations, etc.).</li>
 *   <li>Provides user feedback and error handling during the creation process.</li>
 *   <li>Supports exiting and re-entering the queue setup process with graceful handling of player session states.</li>
 * </ul>
 *
 * <h3>Detailed Steps of the Queue Creation Process:</h3>
 * <ol>
 *   <li>Assign an ID to the virtual queue (used for reference in commands).</li>
 *   <li>Configure a display name for the virtual queue, including support for color codes.</li>
 *   <li>Define the size of the "holding area," which determines how many players are brought to the hosting server
 *   before admission.</li>
 *   <li>Set the location of the holding area using the player's current position and orientation.</li>
 *   <li>Set the location where players are teleported after reaching the end of the queue.</li>
 *   <li>Finalize and validate the configuration before adding the queue to the system.</li>
 * </ol>
 *
 * <h3>Error Handling:</h3>
 * <ul>
 *   <li>Checks if the provided queue ID is unique and not already in use.</li>
 *   <li>Validates input formats and constraints, such as integer values for certain parameters.</li>
 *   <li>Ensures locations are set appropriately by validating the player's current position.</li>
 *   <li>Handles any unexpected issues during queue finalization, logging errors and notifying the player.</li>
 * </ul>
 *
 * <h3>Notes:</h3>
 * <ul>
 *   <li>The virtual queue is closed by default upon creation and can be opened later with another command.</li>
 *   <li>Players can use specific sign formats to control the queue post-creation.</li>
 *   <li>If the player exits the builder using {@code /vqueue create exit}, the creation session is terminated,
 *   and progress is not saved.</li>
 * </ul>
 */
@CommandMeta(description = "Create a new virtual queue hosted on this server")
public class CreateCommand extends CoreCommand {

    /**
     * Constructs a new instance of the {@code CreateCommand} class, initializing it with
     * the base command name "create".
     *
     * <p>This class is part of the command system within the application and is intended to
     * handle operations related to creating virtual queues or other associated features.
     *
     * <p>Key characteristics:
     * <ul>
     *   <li>Inherited functionalities from its superclass for command handling.</li>
     *   <li>Designed to work within the virtual queue management context.</li>
     *   <li>Links to the broader command system for integration with other functionalities,
     *       such as queue management operations.</li>
     * </ul>
     *
     * <p>This constructor is typically invoked automatically within the system
     * to register the command and associate it with its respective operations.
     */
    public CreateCommand() {
        super("create");
    }

    /**
     * Handles the execution of the virtual queue creation command. Facilitates the creation
     * of a new virtual queue by walking the user through a step-by-step process.
     *
     * <p>The command allows players to begin the creation of a virtual queue, exit the builder,
     * or proceed through the steps of defining the queue's properties such as ID, display name,
     * holding area size, and teleportation locations.</p>
     *
     * <p><b>Steps:</b></p>
     * <ul>
     *   <li><b>Step 0:</b> Define the queue's unique ID.</li>
     *   <li><b>Step 1:</b> Set a display name for the queue (supports color codes).</li>
     *   <li><b>Step 2:</b> Specify the size of the holding area (minimum of 1).</li>
     *   <li><b>Step 3:</b> Set the location for the holding area using the player's current position.</li>
     *   <li><b>Step 4:</b> Set the teleportation location for players at the end of the queue.</li>
     *   <li><b>Final Step:</b> Finalize and save the virtual queue definition.</li>
     * </ul>
     *
     * <p>If the "exit" argument is passed, the process is terminated and the builder instance
     * is removed.</p>
     *
     * @param player The player initiating the command. This parameter represents the player who
     *               executes the command to create or modify a virtual queue.
     * @param args   The command arguments provided by the player. The arguments determine the
     *               action to be performed:
     *               <ul>
     *                 <li>If "exit" is provided as the only argument, the builder exits.</li>
     *                 <li>Specific arguments are used to progress the queue creation process
     *                     (e.g., queue ID, name, etc.).</li>
     *               </ul>
     * @throws CommandException If any error occurs while processing or validating the command
     *                          arguments.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 1 && args[0].equalsIgnoreCase("exit")) {
            player.sendMessage(ChatColor.RED + "Exited the virtual queue builder!");
            player.getRegistry().removeEntry("vqueueBuilder");
            return;
        }
        if (player.getRegistry().hasEntry("vqueueBuilder")) {
            ((VirtualQueueBuilder) player.getRegistry().getEntry("vqueueBuilder")).nextStep(player, args);
            return;
        }
        VirtualQueueBuilder queue = new VirtualQueueBuilder();
        player.getRegistry().addEntry("vqueueBuilder", queue);
        player.sendMessage(ChatColor.GREEN + "You've started creating a new virtual queue! We're going to go step-by-step through this. " + ChatColor.YELLOW + "(Exit at any time with /vqueue create exit)");
        player.sendMessage(ChatColor.GREEN + "First, let's give your queue an id. This id is used to reference this queue in commands. Run " + ChatColor.YELLOW + "/vqueue create [id]");
    }
}
