package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.queues.BlockQueue;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Represents a command for editing an existing queue in the park management system.
 * <p>
 * The <code>EditCommand</code> class allows players to perform updates and modifications
 * on an existing queue by specifying its current ID and relevant attributes to change.
 * This command supports a variety of operations, such as modifying the queue's ID, name,
 * warp location, group size, delay time, station location, and block location (for block queues).
 * </p>
 *
 * <p>
 * <b>Command structure:</b>
 * <ul>
 * <li><code>/queue edit [current id] id [new id]</code>: Updates the queue's ID.</li>
 * <li><code>/queue edit [id] name [name]</code>: Updates the display name of the queue.</li>
 * <li><code>/queue edit [id] warp [warp]</code>: Updates the warp point associated with the queue.</li>
 * <li><code>/queue edit [id] groupsize [groupSize]</code>: Sets the group size for the queue.</li>
 * <li><code>/queue edit [id] delay [delay]</code>: Configures the queue's delay time.</li>
 * <li><code>/queue edit [id] station</code>: Updates the station location to the player's current position.</li>
 * <li><code>/queue edit [id] blocklocation</code>: Sets the block spawn location for block-based queues.</li>
 * </ul>
 * </p>
 *
 * <p>
 * A help menu is provided when the command is executed incorrectly or with missing parameters.
 * </p>
 *
 * <h3>Development Notes</h3>
 * <ul>
 * <li>Validates the existence of the park and queue before making any modifications.</li>
 * <li>Ensures uniqueness of IDs when updating the queue's ID.</li>
 * <li>Performs input validation for numeric parameters (e.g., group size and delay).</li>
 * <li>Automatically saves changes to the queue configuration upon successful updates.</li>
 * <li>Supports only block-based queues for block location updates; throws an error otherwise.</li>
 * </ul>
 *
 * <p>
 * If the command is executed with insufficient or incorrect parameters, players are provided
 * with usage instructions and examples to guide them in using the command properly.
 * </p>
 */
@CommandMeta(description = "Edit an existing queue")
public class EditCommand extends CoreCommand {

    /**
     * <p>The <code>EditCommand</code> class represents the "edit" command in the park
     * management system, allowing players to make modifications to a park's queue
     * properties or configurations. This command helps streamline the editing process
     * by providing a direct interface for in-game queue editing tasks.</p>
     *
     * <p><b>Description:</b></p>
     * <ul>
     *   <li>Facilitates the modification of queue-related attributes within a park.</li>
     *   <li>Players can interact with this command to update queue details effectively.</li>
     *   <li>Ensures proper execution through relevant validations and feedback.</li>
     * </ul>
     *
     * <p><b>Responsibilities:</b></p>
     * <ul>
     *   <li>Acts as the trigger point for editing queue configurations.</li>
     *   <li>Integrates with other park management functionalities to apply changes.</li>
     * </ul>
     *
     * <p><b>Dependencies:</b></p>
     * <ul>
     *   <li>Relies on player context and system validations to execute successfully.</li>
     *   <li>Interacts with services such as queue management and park handling systems.</li>
     * </ul>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Leverages commands or subcommands to provide specific editing functionalities.</li>
     *   <li>Offers feedback to the player on actions initiated via the command.</li>
     * </ul>
     *
     * <p>This class is part of the larger command hierarchy and complements
     * other commands such as open, close, or list within the queue management system.</p>
     *
     * <p><b>Command Initialization:</b></p>
     * <ul>
     *   <li>Instantiates with the command identifier "edit".</li>
     *   <li>This identifier is registered and matched within the system's command framework.</li>
     * </ul>
     */
    public EditCommand() {
        super("edit");
    }

    /**
     * Handles the editing of queue details for a player, such as ID, name, warp, group size,
     * delay, station location, and block location.
     * <p>
     * This method is executed when a player issues the "/queue edit" command.
     * Depending on the arguments provided, it modifies specific attributes of
     * a queue in the system. If the arguments are invalid or incomplete,
     * a help menu is displayed to the player.
     * </p>
     *
     * @param player The {@link CPlayer} who issued the command.
     *               <p>The player must be located inside a valid park when the command is executed.</p>
     * @param args   A string array containing the command arguments.
     *               <ul>
     *                 <li><code>[0]</code>: The current queue ID to edit. Required for all actions.</li>
     *                 <li><code>[1]</code>: The action to perform (e.g., <code>id</code>, <code>name</code>, <code>warp</code>, <code>groupsize</code>, <code>delay</code>, <code
     * >station</code>, or <code>blocklocation</code>).</li>
     *                 <li><code>[2]</code>: Additional parameter(s) needed for the selected action (e.g., new ID, new name, etc.).</li>
     *               </ul>
     *               <p>If the arguments are incorrect, incomplete, or invalid for an action,
     *               the help menu is shown to guide the player on proper usage.</p>
     *
     * @throws CommandException Thrown if an error occurs while processing the command or
     *                          while making modifications to queues or parks.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            helpMenu(player);
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Queue queue = ParkManager.getQueueManager().getQueueById(args[0], park.getId());
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find an queue by id " + args[0] + "!");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "id": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                String newId = args[2];
                if (ParkManager.getQueueManager().getQueueById(args[2], park.getId()) != null) {
                    player.sendMessage(ChatColor.RED + "This id is already used by another queue!");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "See current queue ids with: " + ChatColor.YELLOW + "/queue list");
                    return;
                }

                player.sendMessage(ChatColor.GREEN + "Set " + queue.getName() + "'s " + ChatColor.GREEN +
                        "id to " + ChatColor.YELLOW + newId);

                queue.setId(newId);

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "name": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                StringBuilder name = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    name.append(args[i]).append(" ");
                }
                String displayName = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', name.toString().trim());

                player.sendMessage(ChatColor.GREEN + "Set " + queue.getName() + "'s " + ChatColor.GREEN +
                        "display name to " + ChatColor.YELLOW + displayName);

                queue.setName(displayName);

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "warp": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                queue.setWarp(args[2]);

                player.sendMessage(ChatColor.GREEN + "Set " + queue.getName() + "'s " + ChatColor.GREEN +
                        "warp to " + ChatColor.YELLOW + args[2]);

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "groupsize": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                if (!MiscUtil.checkIfInt(args[2])) {
                    player.sendMessage(ChatColor.RED + "'" + args[2] + "' is not an integer!");
                    return;
                }
                int groupSize = Integer.parseInt(args[2]);

                queue.setGroupSize(groupSize);

                player.sendMessage(ChatColor.GREEN + "Set " + queue.getName() + "'s " + ChatColor.GREEN +
                        "groupSize to " + ChatColor.YELLOW + groupSize);

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "delay": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                if (!MiscUtil.checkIfInt(args[2])) {
                    player.sendMessage(ChatColor.RED + "'" + args[2] + "' is not an integer!");
                    return;
                }
                int delay = Integer.parseInt(args[2]);

                queue.setDelay(delay);

                player.sendMessage(ChatColor.GREEN + "Set " + queue.getName() + "'s " + ChatColor.GREEN +
                        "delay to " + ChatColor.YELLOW + delay);

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "station": {
                queue.setStation(player.getLocation());

                player.sendMessage(ChatColor.GREEN + "Updated " + queue.getName() + "'s " + ChatColor.GREEN + "station location to where you're standing!");

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "blocklocation": {
                if (!queue.getQueueType().equals(QueueType.BLOCK)) {
                    player.sendMessage(ChatColor.RED + "This queue isn't a Block queue!");
                    return;
                }
                Location loc = player.getLocation();
                ((BlockQueue) queue).setBlockLocation(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

                player.sendMessage(ChatColor.GREEN + "Updated " + queue.getName() + "'s " + ChatColor.GREEN + "block spawn location to where you're standing!");

                ParkManager.getQueueManager().saveToFile();
                return;
            }
        }
        helpMenu(player);
    }

    /**
     * Displays the help menu for queue editing commands to the specified player.
     * <p>
     * This method sends a series of instructional messages to the player, outlining
     * the available commands for editing queue configurations.
     * <p>
     * The commands include:
     * <ul>
     * <li><code>/queue edit [current id] id [new id]</code> - Updates the queue ID.</li>
     * <li><code>/queue edit [id] name [name]</code> - Changes the queue name.</li>
     * <li><code>/queue edit [id] warp [warp]</code> - Sets the queue warp.</li>
     * <li><code>/queue edit [id] groupsize [groupSize]</code> - Adjusts the group size.</li>
     * <li><code>/queue edit [id] delay [delay]</code> - Modifies the delay setting.</li>
     * <li><code>/queue edit [id] station</code> - Adds or updates the associated station.</li>
     * <li><code>/queue edit [id] blocklocation</code> - Defines a block location.</li>
     * </ul>
     *
     * @param player the {@link CPlayer} to whom the help menu will be displayed
     */
    private void helpMenu(CPlayer player) {
        player.sendMessage(ChatColor.RED + "/queue edit [current id] id [new id]");
        player.sendMessage(ChatColor.RED + "/queue edit [id] name [name]");
        player.sendMessage(ChatColor.RED + "/queue edit [id] warp [warp]");
        player.sendMessage(ChatColor.RED + "/queue edit [id] groupsize [groupSize]");
        player.sendMessage(ChatColor.RED + "/queue edit [id] delay [delay]");
        player.sendMessage(ChatColor.RED + "/queue edit [id] station");
        player.sendMessage(ChatColor.RED + "Block Type:");
        player.sendMessage(ChatColor.RED + "/queue edit [id] blocklocation");
    }
}
