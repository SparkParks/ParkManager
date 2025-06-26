package network.palace.parkmanager.commands.vqueue;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.ChatColor;

import java.util.logging.Level;

/**
 * The <code>AdvanceCommand</code> class handles the functionality for advancing players
 * in the virtual queue managed by the server. It allows the designated operator or
 * administrator to move the queue forward by processing the next individual or group inline.
 *
 * <p>
 * This command interacts with the virtual queue system, ensuring that only valid
 * queues can be advanced and enforces appropriate restrictions such as cooldown times
 * between successive advancements. Notifications are sent to the player invoking the command
 * to confirm the action taken or notify them of an error where applicable.
 * </p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *     <li>Validates the provided queue ID to ensure that it corresponds to an existing queue.</li>
 *     <li>Ensures that the queue is hosted on the current server before permitting advancement.</li>
 *     <li>Enforces a minimum delay of 10 seconds between successive advancements for a queue.</li>
 *     <li>Handles empty queues gracefully by notifying the player that no advancement is possible.</li>
 *     <li>Provides real-time feedback to the CommandExecutor regarding the status of the operation.</li>
 * </ul>
 *
 * <p><b>Restrictions:</b></p>
 * <ul>
 *     <li>Only queues hosted on the current server can be advanced using this command.</li>
 *     <li>Cooldown functionality prevents excessive or unintended queue advancements.</li>
 * </ul>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *     <li>Logs errors to the server console in case of unexpected behavior during advancement.</li>
 *     <li>Notifies the player with a user-friendly message in case of any issues or invalid inputs.</li>
 * </ul>
 *
 * <p>This command is part of a larger system that manages virtual queues for players within the server.</p>
 */
@CommandMeta(description = "Advance players in line for a virtual queue hosted on this server")
public class AdvanceCommand extends CoreCommand {

    /**
     * Constructs a new instance of the {@code AdvanceCommand} class.
     * <p>
     * This command is defined with the name "advance", intended to handle
     * functionality related to advancing actions or states in the context of virtual queues.
     * <p>
     * The specific behavior and the execution of this command are determined
     * by the overriding {@code handleCommand} method in its parent class.
     *
     * <p>
     * Usage of this constructor ensures proper registration and initialization
     * for the "advance" command within the command framework.
     */
    public AdvanceCommand() {
        super("advance");
    }

    /**
     * Handles the "advance" command for a virtual queue system, progressing the queue if all
     * requirements are met. This command interacts with a {@code VirtualQueue} to admit the next member
     * in line. It ensures that the command is issued in compliance with various constraints such as minimum
     * time between advances, valid queue ID, and server hosting validation.
     *
     * <p>If the command does not meet the required syntax or conditions, an appropriate message is
     * sent back to the player.</p>
     *
     * @param player The player executing the command. This is used to deliver feedback messages and validations.
     * @param args   The input arguments, where the expected usage is "/vqueue advance [id]".
     *               <ul>
     *                   <li>args[0]: The queue ID, used to identify the {@code VirtualQueue} to process.</li>
     *               </ul>
     *               If no argument or an invalid argument is provided, an error message will be sent to the player.
     * @throws CommandException If any unexpected error occurs during the command's execution. This handles
     *                          critical issues where the command logic encounters unrecoverable failures.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/vqueue advance [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /vqueue list!");
            return;
        }
        VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueueById(args[0]);
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find a queue by id " + args[0] + "!");
            return;
        }
        if (!queue.isHost()) {
            player.sendMessage(ChatColor.RED + "You can only do that on the server hosting the queue (" +
                    ChatColor.GREEN + queue.getServer() + ChatColor.RED + ")!");
            return;
        }
        if (System.currentTimeMillis() - queue.getLastAdvance() < 10000) {
            // If it's been less than 10 seconds since the last advance
            player.sendMessage(ChatColor.AQUA + "You must wait at least 10 seconds before advancing the queue!");
            return;
        }
        if (queue.getMembers().isEmpty()) {
            player.sendMessage(ChatColor.AQUA + "The queue is currently empty!");
            return;
        }
        queue.setLastAdvance(System.currentTimeMillis());

        try {
            queue.admit();
        } catch (Exception e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error advancing virtual queue", e);
            player.sendMessage(ChatColor.RED + "An error occurred while advancing that virtual queue, check console for details");
        }

        if (queue.getMembers().isEmpty()) {
            player.sendMessage(queue.getName() + ChatColor.GREEN + " has been advanced! The queue is now empty.");
        } else {
            player.sendMessage(queue.getName() + ChatColor.GREEN + " has been advanced! There are now " +
                    queue.getMembers().size() + " players in queue.");
        }
    }
}
