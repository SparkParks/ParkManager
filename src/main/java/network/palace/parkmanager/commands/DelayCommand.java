package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;

/**
 * Represents a command that delays the placement of a redstone block at a specified location
 * after a provided time interval. This command is intended to be executed by command blocks.
 *
 * <p>The command syntax is:
 * <ul>
 *   <li><code>/delay [delay in seconds] x y z</code></li>
 * </ul>
 *
 * <p><b>Key functionality:</b>
 * <ul>
 *   <li>Calculates a delay time in seconds provided by the user.</li>
 *   <li>Identifies the target location coordinates (x, y, z) within the world.</li>
 *   <li>Logs a request to place a redstone block at the specified location after the delay.</li>
 *   <li>Handles the case where the target chunk is not loaded and ensures it is loaded before proceeding.</li>
 *   <li>Sends appropriate feedback to the command sender if arguments are incorrect or invalid.</li>
 * </ul>
 *
 * <p><b>Usage restrictions:</b>
 * <ul>
 *   <li>The command is only executable by command blocks, not by players.</li>
 *   <li>Requires the argument list to contain exactly four arguments in the following format:
 *       <code>[delay in seconds] x y z</code>.</li>
 *   <li>All arguments must be numeric values. Non-numeric values will result in an error message.</li>
 * </ul>
 *
 * <p><b>Error handling:</b>
 * <ul>
 *   <li>If the command is run by players, they will receive a message indicating that the command
 *       can only be run by command blocks.</li>
 *   <li>If the argument count is incorrect or contains non-numeric values, an error message will
 *       be sent back to the command block sender.</li>
 * </ul>
 */
@CommandMeta(description = "Delay placing a redstone block", rank = Rank.CM)
public class DelayCommand extends CoreCommand {

    /**
     * Constructs a new {@code DelayCommand} instance with the default command name set to "delay".
     * <p>
     * This command is designed to introduce a delay or handle time-sensitive operations
     * within its containing logic. The implementation is specific to the environment where
     * it is executed.
     */
    public DelayCommand() {
        super("delay");
    }

    /**
     * <p>Handles the "/delay" command execution for players.</p>
     * <p>This method informs the player that the command can only be executed via command blocks and provides usage instructions.</p>
     *
     * @param player The player executing the command.
     * @param args   The arguments passed with the command.
     * @throws CommandException If an error occurs while handling the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "This command can only be run by command blocks");
        player.sendMessage(ChatColor.RED + "/delay [delay in seconds] x y z");
    }

    /**
     * <p>Handles the "/delay" command when executed from a command block.</p>
     * <p>This method checks the validity of the command arguments, interprets them, and sets up a delay operation based on the input parameters.</p>
     * <p><b>Expected behavior:</b></p>
     * <ul>
     *     <li>The first argument represents the delay duration in seconds.</li>
     *     <li>The remaining three arguments represent the x, y, and z coordinates of a location.</li>
     *     <li>A redstone block is placed at the specified location after the given delay duration.</li>
     * </ul>
     * <p>If the arguments are invalid, an error message will be sent to the sender.</p>
     *
     * @param sender The {@code BlockCommandSender} executing the command. Must be a command block sender.
     * @param args   The array of command arguments. It should contain exactly four elements:
     *               <ul>
     *                   <li>args[0]: A double value representing the delay duration in seconds.</li>
     *                   <li>args[1]: A double value representing the x-coordinate of the location.</li>
     *                   <li>args[2]: A double value representing the y-coordinate of the location.</li>
     *                   <li>args[3]: A double value representing the z-coordinate of the location.</li>
     *               </ul>
     * @throws CommandException If an error occurs while processing or executing the command.
     */
    @Override
    protected void handleCommand(BlockCommandSender sender, String[] args) throws CommandException {
        if (args.length != 4) {
            sender.sendMessage(ChatColor.RED + "Incorrect amount of arguments!");
            return;
        }
        if (MiscUtil.checkIfDouble(args[0]) && MiscUtil.checkIfDouble(args[1]) && MiscUtil.checkIfDouble(args[2]) && MiscUtil.checkIfDouble(args[3])) {
            int x = (int) Double.parseDouble(args[1]);
            int y = (int) Double.parseDouble(args[2]);
            int z = (int) Double.parseDouble(args[3]);
            Location loc = new Location(sender.getBlock().getWorld(), x, y, z);
            long delay = (long) (20 * (Double.parseDouble(args[0])));
            if (!loc.getChunk().isLoaded()) loc.getChunk().load();

            ParkManager.getDelayUtil().logDelay(loc, delay, Material.REDSTONE_BLOCK);
            return;
        }
        sender.sendMessage(ChatColor.RED + "/delay [delay in seconds] x y z");
    }
}
