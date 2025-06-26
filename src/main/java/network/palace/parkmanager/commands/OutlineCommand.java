package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.outline.OutlineSession;
import network.palace.parkmanager.outline.Point;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

/**
 * Represents a command used to manage and interact with outlines. The command
 * enables players to create, manipulate, and manage outline points and related sessions.
 *
 * <p>The {@code OutlineCommand} provides various functionalities, including:
 * <ul>
 *   <li>Creating outline points with coordinates and names</li>
 *   <li>Listing all saved outline points on the server</li>
 *   <li>Setting a session-specific starting outline point</li>
 *   <li>Removing existing outline points</li>
 *   <li>Placing blocks at specific outline locations based on length and heading</li>
 *   <li>Undoing the most recent outline operation</li>
 *   <li>Configuring the block type used for outlines in a session</li>
 * </ul>
 *
 * <p>Each player has their own {@link OutlineSession}, which defines properties
 * such as the current starting point, block type, and operations history. The
 * session allows seamless customization and management of outline interactions.
 *
 * <p>This command is registered under the default name {@code "outline"}
 * and supports the alias {@code "out"}.
 *
 * <p>The following are the available subcommands and their usage:
 * <ul>
 *   <li><b>/outline point list</b> - Lists all configured outline points.</li>
 *   <li><b>/outline point create [name] [x,z]</b> - Creates a new outline point with the specified name and coordinates.</li>
 *   <li><b>/outline point remove [name]</b> - Removes an outline point with the specified name.</li>
 *   <li><b>/outline point [name]</b> - Sets the specified outline point as the starting point for the current session.</li>
 *   <li><b>/outline [length] [heading]</b> - Places a block at the computed location based on the length and heading from the starting point.</li>
 *   <li><b>/outline undo</b> - Reverts the most recent block placement operation in the current session.</li>
 *   <li><b>/outline setblock [type]</b> - Sets the block type for the current session. Defaults to {@code GOLD_BLOCK}.</li>
 * </ul>
 *
 * <p>If insufficient input or invalid arguments are provided, the appropriate
 * help menu is displayed to the player with specific guidance.
 */
@CommandMeta(description = "Outline command", aliases = "out", rank = Rank.CM)
public class OutlineCommand extends CoreCommand {

    /**
     * Constructs a new {@code OutlineCommand} instance.
     * <p>
     * This command is registered under the name "outline". It is designed
     * to handle specific operations related to outlining functionality in the system.
     * </p>
     * <p>
     * The constructor initializes the command with the default name "outline"
     * by invoking the superclass constructor with the same name.
     * </p>
     */
    public OutlineCommand() {
        super("outline");
    }

    /**
     * Handles various outline-related commands for the player. Commands
     * allow the player to list, create, remove, and manage outline points,
     * as well as create outlines, undo changes, and set block types.
     *
     * <p>The method parses the provided arguments to determine the command
     * to execute and performs the appropriate operations based on the input.
     *
     * @param player The {@link CPlayer} executing the command.
     *               This is the player interacting with the outline system.
     * @param args   An array of {@link String} arguments representing the commands and parameters.
     *               <ul>
     *                  <li>When empty, displays the help menu for 'main'.</li>
     *                  <li>When of length 1, handles commands such as "undo" or displays specific help menus.</li>
     *                  <li>When of length 2, handles commands like setting a block type, selecting a session point,
     *                      or creating an outline at a given length and heading.</li>
     *                  <li>When of length 3, handles the removal of a point.</li>
     *                  <li>When of length 4, allows for the creation of a new named point at specified coordinates.</li>
     *               </ul>
     * @throws CommandException If there are errors during the execution of a command or if invalid arguments are provided.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 0) {
            helpMenu("main", player);
            return;
        }
        OutlineSession session = ParkManager.getOutlineManager().getSession(player.getUniqueId());
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("undo")) {
                if (session.undo()) {
                    player.sendMessage(ChatColor.GREEN + "Undo successful!");
                } else {
                    player.sendMessage(ChatColor.RED + "Error undoing! (Maybe you have nothing to undo?)");
                }
                return;
            }
            helpMenu(args[0], player);
            return;
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "point": {
                    if (args[1].equalsIgnoreCase("list")) {
                        List<Point> points = ParkManager.getOutlineManager().getPoints();
                        if (points.isEmpty()) {
                            player.sendMessage(ChatColor.RED + "No points exist on this server!");
                            return;
                        }
                        player.sendMessage(ChatColor.GREEN + "Outline Points:");
                        for (Point p : points) {
                            player.sendMessage(ChatColor.AQUA + "- " + p.getName() + " x: " + p.getX() + ", z: " + p.getZ());
                        }
                        return;
                    }
                    Point p = ParkManager.getOutlineManager().getPoint(args[1]);
                    if (p == null) {
                        helpMenu("point", player);
                        return;
                    }
                    session.setSessionPoint(p);
                    player.sendMessage(ChatColor.GREEN + "Your session point is now " + ChatColor.AQUA + p.getName());
                    return;
                }
                case "setblock": {
                    Material type = Material.valueOf(args[1]);
                    if (type == null) {
                        player.sendMessage(ChatColor.RED + "No block type '" + args[1] + "'!");
                        return;
                    }
                    session.setType(type);
                    player.sendMessage(ChatColor.GREEN + "Set your block type to " + ChatColor.AQUA + type);
                    return;
                }
                default: {
                    try {
                        double length = Double.parseDouble(args[0]);
                        double heading = Double.parseDouble(args[1]);
                        Location loc = session.outline(length, heading);
                        if (loc == null) {
                            player.sendMessage(ChatColor.RED + "There was an error creating that outline! Do you have a starting point selected!");
                            return;
                        }
                        player.sendMessage(ChatColor.GREEN + "Placed a block at " + loc.getBlockX() + "," +
                                loc.getBlockY() + "," + loc.getBlockZ());
                    } catch (NumberFormatException e) {
                        helpMenu("main", player);
                    }
                    return;
                }
            }
        }
        if (args.length == 3) {
            if (!args[0].equalsIgnoreCase("point") || !args[1].equalsIgnoreCase("remove")) {
                helpMenu("main", player);
                return;
            }
            if (ParkManager.getOutlineManager().removePoint(args[2])) {
                player.sendMessage(ChatColor.GREEN + "Point removed successfully!");
            } else {
                player.sendMessage(ChatColor.RED + "No point exists with the name '" + args[2] + "'!");
            }
            return;
        }
        if (args.length == 4) {
            if (!args[0].equalsIgnoreCase("point") || !args[1].equalsIgnoreCase("create")) {
                helpMenu("main", player);
                return;
            }
            try {
                if (ParkManager.getOutlineManager().getPoint(args[2]) != null) {
                    player.sendMessage(ChatColor.RED + "A point already exists named '" + args[2] + "'!");
                    return;
                }
                String[] list = args[3].split(",");
                int x = Integer.parseInt(list[0]);
                int z = Integer.parseInt(list[1]);
                Point p = new Point(args[2], x, z);
                ParkManager.getOutlineManager().addPoint(p);
                player.sendMessage(ChatColor.GREEN + "Point added successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "There was an error while creating that point!");
            }
            return;
        }
        helpMenu("main", player);
    }

    /**
     * Displays the help menu for outline commands to the player.
     *
     * <p>This method provides detailed information and instructions on how to use
     * the available commands for managing outline points and outline operations
     * based on the specified menu type.</p>
     *
     * <p>If the specified menu is <code>"point"</code>, it outlines the commands
     * related to managing individual points. Otherwise, it provides general outline
     * commands.</p>
     *
     * @param menu The type of help menu to display. Acceptable values:
     *             <ul>
     *                 <li><code>"point"</code> - Shows commands related to managing outline points.</li>
     *                 <li>Any other value - Shows general outline commands.</li>
     *             </ul>
     * @param player The {@link CPlayer} to whom the help menu will be sent.
     *               Represents the player interacting with the outline system.
     */
    public static void helpMenu(String menu, CPlayer player) {
        if (menu.equals("point")) {
            player.sendMessage(ChatColor.GREEN + "Point Commands:");
            player.sendMessage(ChatColor.GREEN + "/outline point list " + ChatColor.AQUA +
                    "- List all configured outline points");
            player.sendMessage(ChatColor.GREEN + "/outline point create [name] [x,z] " + ChatColor.AQUA +
                    "- Save a new outline point");
            player.sendMessage(ChatColor.GREEN + "/outline point remove [name] " + ChatColor.AQUA +
                    "- Remove a saved point");
            player.sendMessage(ChatColor.GREEN + "/outline point [name] " + ChatColor.AQUA +
                    "- Select a starting point for your session");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Outline Commands:");
        player.sendMessage(ChatColor.GREEN + "/outline point " + ChatColor.AQUA +
                "- Commands for managing starting points");
        player.sendMessage(ChatColor.GREEN + "/outline [length] [heading]" + ChatColor.AQUA +
                "- Place a block at this outline location");
        player.sendMessage(ChatColor.GREEN + "/outline undo" + ChatColor.AQUA +
                "- Undo your latest outline");
        player.sendMessage(ChatColor.GREEN + "/outline setblock [type] " + ChatColor.AQUA +
                "- Set the block type for your session (optional, default is gold block)");
    }
}
