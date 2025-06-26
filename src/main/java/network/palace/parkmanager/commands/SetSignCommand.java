package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The {@code SetSignCommand} class provides functionality to modify the content of a sign
 * in a Minecraft world. This command allows users to set up to four lines of text on a
 * specific sign by providing coordinates and the desired text content.
 *
 * <p><strong>Command Syntax:</strong></p>
 * <p>{@code /setsign [x] [y] [z] line 1;text for line 2;text for line 3;text for line 4}</p>
 *
 * <h2>Command Features:</h2>
 * <ul>
 *   <li>The command requires at least four arguments: x, y, z coordinates, and text for the first line. The remaining lines are optional.</li>
 *   <li>The command supports relative coordinates using the "~" notation for x, y, and z when executed by a player or a command block.</li>
 *   <li>Any text lines omitted will remain unchanged or empty. For example, using double semicolons {@code ;;} will skip a line.</li>
 *   <li>Supports alternate color codes with "&" to add color to the sign's text.</li>
 * </ul>
 *
 * <h2>Execution Limitations:</h2>
 * <ul>
 *   <li>This command can only be executed by players or command blocks. Other entities cannot invoke it.</li>
 *   <li>The specified coordinates must point to a valid sign block; otherwise, an error message will be returned.</li>
 * </ul>
 *
 * <h2>Error Handling:</h2>
 * <ul>
 *   <li>Ensures that x, y, and z coordinates are integers and outputs an error if invalid values are provided.</li>
 *   <li>Validates the existence of a sign at the given coordinates before attempting modification.</li>
 *   <li>Handles cases where text input syntax is incorrect or inconsistent.</li>
 * </ul>
 *
 * <h2>Format Modifications:</h2>
 * <ul>
 *   <li>Lines exceeding the sign limit (4 lines) are truncated.</li>
 *   <li>Empty strings between semicolons (e.g., {@code line 1;;line 3}) will insert blank lines.</li>
 * </ul>
 *
 * <h2>Output Messages:</h2>
 * <ul>
 *   <li>Feedback is provided to the sender for each line updated, confirming the modifications.</li>
 *   <li>Displays the number of lines updated and the coordinates of the modified sign.</li>
 * </ul>
 */
@CommandMeta(description = "Set the lines of a sign", rank = Rank.CM)
public class SetSignCommand extends CoreCommand {

    /**
     * Constructs a new instance of the {@code SetSignCommand}.
     * <p>
     * This command initializes with the name "setsign", which can be used
     * for execution and mapping within the application.
     * <p>
     * Intended for use within a command-handling framework, this constructor
     * sets up the base name of the command. The command itself is expected
     * to perform actions related to setting or modifying signs in the
     * application environment.
     * <p>
     * Usage of this method typically involves the framework automatically
     * invoking this constructor when the command is initialized or registered.
     */
    public SetSignCommand() {
        super("setsign");
    }

    /**
     * Handles the "setsign" command, allowing the sender to update the lines of a sign at a specified location.
     * <p>
     * The command requires at least the x, y, and z coordinates followed by the text to set on the sign.
     * Partial lines are supported, and blank lines can be defined using two consecutive semicolons (";;").
     * The sender must be either a {@link Player} or {@link BlockCommandSender}.
     * </p>
     *
     * @param sender the entity or block executing the command. Must be a {@link Player} or {@link BlockCommandSender}.
     * @param args   an array of command arguments. Expected format:
     *               <ul>
     *                  <li><b>args[0]</b>: The x-coordinate of the target sign (relative if prefixed with "~").</li>
     *                  <li><b>args[1]</b>: The y-coordinate of the target sign (relative if prefixed with "~").</li>
     *                  <li><b>args[2]</b>: The z-coordinate of the target sign (relative if prefixed with "~").</li>
     *                  <li><b>args[3...n]</b>: Text to set on the sign, separated by semicolons (";").</li>
     *               </ul>
     * @throws CommandException if an internal command-related error occurs.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.AQUA + "Set sign lines:");
            sender.sendMessage(ChatColor.AQUA + "/setsign [x] [y] [z] line 1;line 2;line 3;line 4");
            sender.sendMessage(ChatColor.AQUA + "You need to provide text for the first line, but the rest are optional");
            sender.sendMessage(ChatColor.AQUA + "For example, '/setsign 1 1 1 hey;there' will skip the last two lines");
            sender.sendMessage(ChatColor.AQUA + "You can make a line blank as well, like '/setsign 1 1 1 hey;;there' (line two is blank)");
            return;
        }
        int x, y, z;
        boolean relativeX = false, relativeY = false, relativeZ = false;
        // If the command sender is a player or a command block, check for relative coordinates
        if (sender instanceof BlockCommandSender || sender instanceof Player) {
            if (args[0].contains("~")) {
                if (args[0].length() == 1) args[0] += "0";
                if (MiscUtil.checkIfInt(args[0].substring(1))) {
                    args[0] = args[0].substring(1);
                    relativeX = true;
                }
            }
            if (args[1].contains("~")) {
                if (args[1].length() == 1) args[1] += "0";
                if (MiscUtil.checkIfInt(args[1].substring(1))) {
                    args[1] = args[1].substring(1);
                    relativeY = true;
                }
            }
            if (args[2].contains("~")) {
                if (args[2].length() == 1) args[2] += "0";
                if (MiscUtil.checkIfInt(args[2].substring(1))) {
                    args[2] = args[2].substring(1);
                    relativeZ = true;
                }
            }
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            sender.sendMessage(ChatColor.RED + "The 'x' coordinate must be an integer!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[1])) {
            sender.sendMessage(ChatColor.RED + "The 'y' coordinate must be an integer!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[2])) {
            sender.sendMessage(ChatColor.RED + "The 'z' coordinate must be an integer!");
            return;
        }
        x = Integer.parseInt(args[0]);
        y = Integer.parseInt(args[1]);
        z = Integer.parseInt(args[2]);
        Location loc;
        if (sender instanceof BlockCommandSender) {
            loc = ((BlockCommandSender) sender).getBlock().getLocation();
        } else if (sender instanceof Player) {
            loc = ((Player) sender).getLocation();
        } else {
            sender.sendMessage(ChatColor.RED + "Only players and command blocks can use this command!");
            return;
        }
        if (relativeX || relativeY || relativeZ) {
            if (relativeX) x = loc.getBlockX() + x;
            if (relativeY) y = loc.getBlockY() + y;
            if (relativeZ) z = loc.getBlockZ() + z;
        }
        Block b = loc.getWorld().getBlockAt(x, y, z);
        if (!b.getType().equals(Material.SIGN) && !b.getType().equals(Material.SIGN_POST) && !b.getType().equals(Material.WALL_SIGN)) {
            sender.sendMessage(ChatColor.RED + "There is no sign at " + x + "," + y + "," + z);
            return;
        }
        Sign s = (Sign) b.getState();
        StringBuilder command = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            command.append(args[i]).append(" ");
        }
        String[] commandLines = command.toString().trim().replaceAll(";;", "; ;").split(";");
        int size = Math.min(commandLines.length, 4);
        sender.sendMessage(ChatColor.GREEN + "Updating " + size + " lines...");
        for (int i = 0; i < size; i++) {
            s.setLine(i, ChatColor.translateAlternateColorCodes('&', commandLines[i].trim()));
            sender.sendMessage(ChatColor.GREEN + "Set line " + (i + 1) + " to:" + ChatColor.RESET + s.getLine(i));
        }
        s.update();
        sender.sendMessage(ChatColor.GREEN + "Updated sign at " + x + "," + y + "," + z);
    }
}
