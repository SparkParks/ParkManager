package network.palace.parkmanager.queues.virtual;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.util.logging.Level;

/**
 * <p>
 * The <code>VirtualQueueBuilder</code> class is used to interactively create and configure
 * virtual queues for managing players in an amusement park or similar environment. This class
 * extends the {@link VirtualQueue} class and facilitates step-by-step creation of a queue
 * through player input.
 * </p>
 *
 * <p>
 * The builder guides the player through multiple steps to set various properties of the virtual queue,
 * including:
 * </p>
 * <ul>
 *   <li><b>Queue ID:</b> A unique identifier for the virtual queue.</li>
 *   <li><b>Display Name:</b> A descriptive name for the queue, which supports color codes.</li>
 *   <li><b>Holding Area Size:</b> The number of players held in the front of the queue.</li>
 *   <li><b>Location Settings:</b> Specifies teleportation points for players in different stages of the queue,
 *   such as the holding area and the final position.</li>
 * </ul>
 *
 * <p>
 * The process ensures validation at each step, providing clear feedback to the player for errors or missing
 * input. The final step integrates the newly created virtual queue into the system.
 * </p>
 *
 * <p><b>Creation Steps:</b></p>
 * <ul>
 *   <li><b>Step 0:</b> Set a unique queue ID.</li>
 *   <li><b>Step 1:</b> Define a display name with support for color codes.</li>
 *   <li><b>Step 2:</b> Configure the size of the holding area.</li>
 *   <li><b>Step 3-6:</b> Define teleportation locations by the player's current position.</li>
 *   <li><b>Finalization:</b> Save and register the configured virtual queue in the system.</li>
 * </ul>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *   <li>Checks if the queue ID is already in use before allowing further configuration.</li>
 *   <li>Validates input formats, such as ensuring the holding area size is a positive integer.</li>
 *   <li>Logs errors in case of issues during the addition of the new virtual queue to the system.</li>
 * </ul>
 *
 * <p><b>Commands:</b></p>
 * <ul>
 *   <li>Prompts the player at every step with the command they need to run next.</li>
 *   <li>Utilizes player-provided input during execution, including location and looking direction.</li>
 *   <li>Displays descriptive messages and tips while guiding the player through the process.</li>
 * </ul>
 *
 * <p>This utility ensures a smooth creation flow with minimal manual intervention while maintaining flexibility
 * for customized configurations.</p>
 */
public class VirtualQueueBuilder extends VirtualQueue {

    /**
     * Constructs a new instance of the <code>VirtualQueueBuilder</code> class.
     *
     * <p>The <code>VirtualQueueBuilder</code> is a specialized implementation of the
     * <code>VirtualQueue</code> class. It is designed for creating and initializing a
     * virtual queue system with default configuration values. This constructor initializes
     * the superclass (<code>VirtualQueue</code>) with default or placeholder parameters.</p>
     *
     * <p>This constructor is typically used for creating instances for specific queue
     * scenarios where detailed customization is not immediately necessary. It serves as
     * a convenient entry point for building and managing virtual queues.</p>
     */
    public VirtualQueueBuilder() {
        super(null, null, 0, null, null, null, null, null, 0);
    }

    /**
     * Advances the process for creating a new virtual queue step-by-step based on the current state
     * and the provided arguments. This method guides a player through defining the required attributes
     * for a virtual queue, including ID, display name, holding area, and teleportation locations.
     *
     * <p>The method handles different creation steps as follows:
     * <ul>
     *   <li>Step 0: Set the queue ID.</li>
     *   <li>Step 1: Set the queue display name.</li>
     *   <li>Step 2: Define the holding area size.</li>
     *   <li>Step 5: Define the holding area location.</li>
     *   <li>Step 6: Finalize and define the teleportation location for the end of the queue.</li>
     * </ul>
     *
     * <p>If the required information for the current step is missing or invalid, it provides guidance
     * to the player via in-game messages. Once all steps are completed successfully, the queue is
     * finalized and added to the virtual queue manager.
     *
     * @param player the player interacting with the virtual queue creation process; responsible for
     *               issuing commands and acting as the point of reference for location-based steps.
     * @param args   an array of arguments supplied during the virtual queue creation process; used to
     *               specify details such as queue ID, name, and holding area size.
     */
    public void nextStep(CPlayer player, String[] args) {
        if (id == null) {
            //Step 0
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "/vqueue create [id]");
                return;
            }
            if (ParkManager.getVirtualQueueManager().getQueueById(args[0]) != null) {
                player.sendMessage(ChatColor.RED + "This id is already used by another queue! Try again: " + ChatColor.YELLOW + "/vqueue create [id]");
                player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "See current queue ids with: " + ChatColor.YELLOW + "/vqueue list");
                return;
            }
            this.id = args[0];
            player.sendMessage(ChatColor.GREEN + "Great! Now, let's give your queue a display name. Run " + ChatColor.YELLOW + "/vqueue create [name]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This name supports color codes! For example, '&aExample &dQueue' becomes '"
                    + ChatColor.GREEN + "Example " + ChatColor.LIGHT_PURPLE + "Queue" + ChatColor.DARK_AQUA + "'.");
            return;
        }
        if (name == null) {
            //Step 1
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "/vqueue create [name]");
                return;
            }
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                name.append(args[i]);
                if (i < (args.length - 1)) {
                    name.append(" ");
                }
            }
            this.name = ChatColor.translateAlternateColorCodes('&', name.toString());
            player.sendMessage(ChatColor.GREEN + "Next, let's define the holding area for this virtual queue. Run " + ChatColor.YELLOW + "/vqueue create [holdingAreaSize]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "The holding area is the number of players in the front of the line that are brought over to the server hosting the queue, before they're admitted.");
            return;
        }
        if (holdingArea == 0) {
            //Step 2
            if (args.length < 1 || !MiscUtil.checkIfInt(args[0]) || Integer.parseInt(args[0]) < 1) {
                player.sendMessage(ChatColor.RED + "/vqueue create [holdingAreaSize]");
                return;
            }
            this.holdingArea = Integer.parseInt(args[0]);
            player.sendMessage(ChatColor.GREEN + "Now, let's define where the \"holding area\" is. Run "
                    + ChatColor.YELLOW + "/vqueue create");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This command doesn't have any parameters because it uses where you're standing when you run it!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When running this, make sure you're in the exact position you want players to be teleported to.");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Note: " + ChatColor.DARK_AQUA + "" + ChatColor.ITALIC
                    + "Don't forget about where you're looking! Players will be looking exactly where you are when they're teleported in.");
            return;
        }
        if (holdingAreaLocation == null) {
            //Step 5
            this.holdingAreaLocation = player.getLocation();
            player.sendMessage(ChatColor.GREEN + "Now we need to define where players are teleported when they reach the end of the queue. Run "
                    + ChatColor.YELLOW + "/vqueue create");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This command doesn't have any parameters because it uses where you're standing when you run it!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When running this, make sure you're in the exact position you want players to be teleported to.");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Note: " + ChatColor.DARK_AQUA + "" + ChatColor.ITALIC
                    + "Don't forget about where you're looking! Players will be looking exactly where you are when they're teleported in.");
            return;
        }
        if (queueLocation == null) {
            //Step 6
            this.queueLocation = player.getLocation();
            player.sendMessage(ChatColor.YELLOW + "Great! Finalizing your Virtual Queue...");
            player.sendMessage(ChatColor.GREEN + "Your queue is all ready to go! It's closed by default, but you can change that with " + ChatColor.YELLOW + "/vqueue open");
            player.sendMessage(ChatColor.GREEN + "Create signs to control the queue:");
            player.sendMessage(ChatColor.AQUA + "[vqueue] " + ChatColor.GREEN + "on the first line, the vqueue id on the second line, and " + ChatColor.AQUA +
                    "advance " + ChatColor.GREEN + "or " + ChatColor.AQUA + "state " + ChatColor.GREEN + "on the third line.");
            player.getRegistry().removeEntry("vqueueBuilder");
            if (ParkManager.getVirtualQueueManager().getQueueById(this.id) != null) {
                player.sendMessage(ChatColor.RED + "This id is already used by another queue! Try again: " + ChatColor.YELLOW + "/vqueue create");
                player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "See current queue ids with: " + ChatColor.YELLOW + "/vqueue list");
                return;
            }
            try {
                ParkManager.getVirtualQueueManager().addQueue(new VirtualQueue(this.id, this.name, this.holdingArea, this.holdingAreaLocation,
                        this.queueLocation, Core.getInstanceName(), this.advanceSign, this.stateSign, ParkManager.getVirtualQueueManager().getRandomItemId()));
            } catch (IOException e) {
                Core.getInstance().getLogger().log(Level.SEVERE, "Error creating virtual queue", e);
                player.sendMessage(ChatColor.RED + "An error occurred while advancing that virtual queue, check console for details");
            }
        }
    }
}
