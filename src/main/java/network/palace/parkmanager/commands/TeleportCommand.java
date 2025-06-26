package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MathUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The {@code TeleportCommand} class handles the teleportation of players in the game.
 * <p>
 * This command allows both players and the console to execute teleportation functions with various arguments,
 * such as specifying coordinates, direction (yaw and pitch), and optional target world. It also supports
 * teleporting players to other players.
 *
 * <p><b>Features:</b></p>
 * <ul>
 *     <li>Teleporting a player to another player.</li>
 *     <li>Teleporting a player to specific coordinates with optional yaw and pitch.</li>
 *     <li>Offering detailed command feedback when arguments are invalid or targets are not found.</li>
 *     <li>Handling teleport commands issued by both players and the console.</li>
 * </ul>
 *
 * <p>This command enforces permission and rank restrictions to prevent unauthorized access.
 * The teleportation logic integrates with existing player and location management systems.
 *
 * <p><b>Command Syntax:</b></p>
 * <ul>
 *     <li>{@code /tp [player] <target>} – Teleport a player to another player.</li>
 *     <li>{@code /tp [x] [y] [z]} – Teleport to coordinates.</li>
 *     <li>{@code /tp [player] [x] [y] [z]} – Teleport a target player to coordinates.</li>
 *     <li>{@code /tp [player] [x] [y] [z] <yaw> <pitch>} – Teleport with optional orientation.</li>
 *     <li>{@code /tp [player] [x] [y] [z] <yaw> <pitch> <world>} – Teleport specifying the target world.</li>
 * </ul>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *     <li>Displays a message when players or coordinates are not found.</li>
 *     <li>Handles malformed input with detailed feedback for the sender.</li>
 * </ul>
 *
 * <p>This command is primarily intended for server administrators or players with the appropriate {@code Rank.TRAINEE}
 * or higher, as specified in the {@link CommandMeta} annotation.</p>
 */
@CommandMeta(description = "Teleport command", rank = Rank.TRAINEE)
public class TeleportCommand extends CoreCommand {

    /**
     * Constructs a new {@code TeleportCommand} instance, initializing the command
     * with the alias "tp".
     * <p>
     * This command is used to handle teleportation functionalities within the game.
     * It can process teleportation requests for players, either between players
     * or to specific locations.
     * <p>
     * The {@code TeleportCommand} facilitates the following functionality:
     * <ul>
     *   <li>Teleporting a player to another player.</li>
     *   <li>Teleporting a player to a specific set of coordinates or location.</li>
     * </ul>
     */
    public TeleportCommand() {
        super("tp");
    }

    /**
     * Handles the teleport command, allowing players to teleport themselves or others to specified players or locations.
     * The method processes various argument configurations to perform the appropriate teleportation logic.
     *
     * <p>The following use cases are supported based on the arguments provided:</p>
     * <ul>
     *     <li><b>Case 1:</b> Teleport the sender to another player.</li>
     *     <li><b>Case 2:</b> Teleport one player to another player.</li>
     *     <li><b>Case 3 or 5:</b> Teleport the sender to a location, specified as coordinates (x, y, z) optionally including yaw and pitch.</li>
     *     <li><b>Case 4, 6, or 7:</b> Teleport a player to a specified location, optionally including yaw, pitch, and world.</li>
     * </ul>
     * <p>Outputs error messages for invalid arguments or teleportation failures.</p>
     *
     * @param player the {@code CPlayer} instance executing the command, representing the sender of the teleport command.
     * @param args   an array of {@code String} arguments supplied to the command. The arguments are interpreted as follows:
     *               <ul>
     *                   <li>When the array contains a single argument, it is treated as the target player's name.</li>
     *                   <li>With two arguments, both are treated as player names to teleport one to the other.</li>
     *                   <li>With three or five arguments, they are parsed as coordinates:
     *                       <ul>
     *                           <li><b>[x, y, z]:</b> Basic teleportation to specific coordinates.</li>
     *                           <li><b>[x, y, z, yaw, pitch]:</b> Advanced teleportation including orientation.</li>
     *                       </ul>
     *                   </li>
     *                   <li>With four, six, or seven arguments:
     *                       <ul>
     *                           <li><b>[player, x, y, z]:</b> Teleports the specified player to the coordinates.</li>
     *                           <li><b>[player, x, y, z, yaw, pitch]:</b> Teleports the player with orientation.</li>
     *                           <li><b>[player, x, y, z, yaw, pitch, world]:</b> Teleports the player to a specific world.</li>
     *                       </ul>
     *                   </li>
     *               </ul>
     * @throws CommandException if an error occurs during command execution, such as invalid arguments or unavailable players.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        switch (args.length) {
            case 1: {
                CPlayer target = Core.getPlayerManager().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    player.sendMessage(ChatColor.RED + "/tp " + ChatColor.BOLD + "[player]");
                    return;
                }
                teleport(player.getBukkitPlayer(), player, target, false);
                return;
            }
            case 2: {
                teleport(player.getBukkitPlayer(),
                        Core.getPlayerManager().getPlayer(args[0]),
                        Core.getPlayerManager().getPlayer(args[1]),
                        false);
                return;
            }
            case 3:
            case 5:
                try {
                    Location target;
                    if (args.length == 3) {
                        Location playerLoc = player.getLocation();
                        target = new Location(player.getWorld(),
                                getDouble(player.getBukkitPlayer(), args[0], "x"),
                                getDouble(player.getBukkitPlayer(), args[1], "y"),
                                getDouble(player.getBukkitPlayer(), args[2], "z"),
                                playerLoc.getYaw(),
                                playerLoc.getPitch());
                    } else {
                        target = new Location(player.getWorld(),
                                getDouble(player.getBukkitPlayer(), args[0], "x"),
                                getDouble(player.getBukkitPlayer(), args[1], "y"),
                                getDouble(player.getBukkitPlayer(), args[2], "z"),
                                (float) getDouble(player.getBukkitPlayer(), args[3], "yaw"),
                                (float) getDouble(player.getBukkitPlayer(), args[4], "pitch"));
                    }
                    teleport(player.getBukkitPlayer(), player, target);
                } catch (NumberFormatException e) {
                    player.sendMessage(e.getMessage());
                }
                return;
            case 4:
            case 6:
            case 7: {
                CPlayer moving = Core.getPlayerManager().getPlayer(args[0]);
                if (moving == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    player.sendMessage(ChatColor.RED + "/tp " + ChatColor.BOLD + "[player] " + ChatColor.RED
                            + "[x] [y] [z] <yaw> <pitch> <world>");
                    return;
                }
                try {
                    teleport(player.getBukkitPlayer(), moving, getLocation(args, player.getBukkitPlayer(), moving));
                } catch (NumberFormatException e) {
                    player.sendMessage(e.getMessage());
                }
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "/tp [player] <target>");
        player.sendMessage(ChatColor.RED + "/tp [x] [y] [z] <yaw> <pitch>");
        player.sendMessage(ChatColor.RED + "/tp [player] [x] [y] [z] <yaw> <pitch>");
        player.sendMessage(ChatColor.RED + "/tp [player] [x] [y] [z] <yaw> <pitch> <world>");
    }

    /**
     * Handles the teleportation logic for various command configurations based on the arguments
     * provided by the sender. This method processes commands for teleporting players either to another
     * player or to specified coordinates, possibly including orientation and world information.
     * <p>
     * The supported configurations and their respective behaviors are:
     * <ul>
     *     <li><b>Case 2 arguments:</b> Teleports one player to another player.</li>
     *     <li><b>Case 4, 6, or 7 arguments:</b> Teleports a player to specific coordinates
     *         (<i>x</i>, <i>y</i>, <i>z</i>), optionally including yaw, pitch, and world.</li>
     * </ul>
     * <p>Error messages are sent to the sender in cases of invalid input or teleportation failure.</p>
     *
     * @param sender the {@link CommandSender} executing the command. Typically, this represents the entity
     *               (player or console) issuing the teleport command.
     * @param args   an array of {@code String} arguments supplied to the command. The expected arguments are:
     *               <ul>
     *                   <li><b>2 arguments:</b> Player names of the teleporting player and target player.</li>
     *                   <li><b>4 arguments:</b> Player name followed by coordinates (<i>x</i>, <i>y</i>, <i>z</i>).</li>
     *                   <li><b>6 arguments:</b> Player name, coordinates, and orientation (<i>yaw</i>, <i>pitch</i>).</li>
     *                   <li><b>7 arguments:</b> Player name, coordinates, orientation, and specific world.</li>
     *               </ul>
     * @throws CommandException if an error occurs during command execution, such as invalid arguments,
     *                          a missing player, or an unavailable target world.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        switch (args.length) {
            case 2: {
                teleport(sender, Core.getPlayerManager().getPlayer(args[0]),
                        Core.getPlayerManager().getPlayer(args[1]), true);
                return;
            }
            case 4:
            case 6:
            case 7: {
                CPlayer moving = Core.getPlayerManager().getPlayer(args[0]);
                if (moving == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    sender.sendMessage(ChatColor.RED + "/tp " + ChatColor.BOLD + "[player] " + ChatColor.RED
                            + "[x] [y] [z] <yaw> <pitch> <world>");
                    return;
                }
                try {
                    teleport(sender, moving, getLocation(args, sender, moving));
                } catch (NumberFormatException e) {
                    sender.sendMessage(e.getMessage());
                }
                return;
            }
        }
        sender.sendMessage(ChatColor.RED + "/tp [player] [target]");
        sender.sendMessage(ChatColor.RED + "/tp [player] [x] [y] [z] <yaw> <pitch>");
        sender.sendMessage(ChatColor.RED + "/tp [player] [x] [y] [z] <yaw> <pitch> <world>");
    }

    /**
     * Constructs a {@link Location} using the provided arguments, which specify the coordinates,
     * orientation (yaw and pitch), and the world for the location. If some arguments (e.g., yaw, pitch, world)
     * are not provided, they will be derived from the target player's current location and world.
     *
     * <p>Coordinates (x, y, z) are parsed from the arguments, and optional orientation (yaw, pitch)
     * and world name may also be processed. If optional values are missing, defaults will be used.
     *
     * @param args   an array of {@code String} arguments representing the coordinates and optional yaw, pitch, and world:
     *               <ul>
     *                   <li>args[1]: The x-coordinate.</li>
     *                   <li>args[2]: The y-coordinate.</li>
     *                   <li>args[3]: The z-coordinate.</li>
     *                   <li>args[4] (optional): The yaw value, defining orientation (only if args.length >= 6).</li>
     *                   <li>args[5] (optional): The pitch value, defining orientation (only if args.length >= 6).</li>
     *                   <li>args[6] (optional): The world name (only if args.length == 7).</li>
     *               </ul>
     * @param sender the {@code CommandSender} issuing the command. May be used to provide feedback or determine information such as sender's current location.
     * @param target the {@code CPlayer} acting as the reference point for obtaining fallback or default values (e.g., current location or world).
     *
     * @return a {@link Location} object constructed using the parsed and/or default data, including
     *         position, yaw, pitch, and world.
     *
     * @throws NumberFormatException if any numerical argument (x, y, z, yaw, pitch) cannot be parsed to a valid number.
     */
    private Location getLocation(String[] args, CommandSender sender, CPlayer target) throws NumberFormatException {
        double x = getDouble(sender, args[1], "x"),
                y = getDouble(sender, args[2], "y"),
                z = getDouble(sender, args[3], "z");

        float yaw, pitch;
        if (args.length == 6) {
            yaw = (float) getDouble(sender, args[4], "yaw");
            pitch = (float) getDouble(sender, args[5], "pitch");
        } else {
            Location playerLoc = target.getLocation();
            yaw = playerLoc.getYaw();
            pitch = playerLoc.getPitch();
        }

        World world;
        if (args.length == 7) {
            world = Bukkit.getWorld(args[6]);
        } else {
            world = target.getWorld();
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Teleports a player to another player's location.
     * <p>
     * The method performs the following checks before executing the teleport:
     * <ul>
     *     <li>Ensures the source player ({@code player}) exists and is valid.</li>
     *     <li>Ensures the target player ({@code target}) exists and is valid.</li>
     *     <li>Checks if the source player is in spectator mode or if the target is in a vehicle, preventing teleportation if conditions are not met.</li>
     * </ul>
     *
     * Additionally, appropriate error messages will be sent to the {@code messenger} in case of invalid inputs or teleportation restrictions.
     *
     * @param messenger the {@code CommandSender} responsible for sending feedback or error messages to the command initiator.
     * @param player the {@code CPlayer} instance representing the source player to be teleported.
     * @param target the {@code CPlayer} instance representing the target player to whom the source player is teleported.
     * @param consoleSender a boolean indicating whether the command was initiated from the console.
     */
    private void teleport(CommandSender messenger, CPlayer player, CPlayer target, boolean consoleSender) {
        if (player == null) {
            messenger.sendMessage(ChatColor.RED + "First player not found!");
            messenger.sendMessage(ChatColor.RED + "/tp " + ChatColor.BOLD + "[player] " + ChatColor.RED +
                    (consoleSender ? "[target]" : "<target>"));
            return;
        }
        if (target == null) {
            messenger.sendMessage(ChatColor.RED + "Second player not found!");
            messenger.sendMessage(ChatColor.RED + "/tp [player] " + ChatColor.BOLD +
                    (consoleSender ? "[target]" : "<target>"));
            return;
        }
        if (!player.getGamemode().equals(GameMode.SPECTATOR) && target.isInVehicle()) {
            messenger.sendMessage(ChatColor.RED + "Can't teleport to " + target.getName() + ", they're on a ride!");
            return;
        }
        teleport(null, player, target.getLocation());
        messenger.sendMessage(ChatColor.GRAY + "Teleported " +
                ((messenger instanceof Player) && ((Player) messenger).getUniqueId().equals(player.getUniqueId()) ? "you" : player.getName())
                + " to " + target.getName() + "!");
    }

    /**
     * Teleports a player to a specified location and logs the action. Optionally, it can send a message to the
     * initiating {@code CommandSender} about the action performed.
     *
     * <p>This method handles teleportation requests where a {@code CPlayer} is sent to a specific {@code Location}.
     * The action is logged, and the location is rounded for precision. If a messenger is supplied, a message is
     * sent to confirm the teleportation details.</p>
     *
     * @param messenger the {@code CommandSender} who initiated the teleportation. May be {@code null} in cases where no
     *                  message is required to be sent back to the initiator, such as server-side calls.
     * @param player    the {@code CPlayer} to be teleported. This is the player entity to move to the specified location.
     * @param loc       the target {@code Location} where the {@code CPlayer} will be teleported. Must not be {@code null},
     *                  as the method will return immediately if this is the case.
     */
    private void teleport(CommandSender messenger, CPlayer player, Location loc) {
        if (loc == null) return;
        ParkManager.getTeleportUtil().log(player);
        player.teleport(loc);
        MathUtil.round(loc, 4);
        if (messenger != null) {
            messenger.sendMessage(ChatColor.GRAY + "Teleported " +
                    ((messenger instanceof Player) && ((Player) messenger).getUniqueId().equals(player.getUniqueId()) ? "you" : player.getName())
                    + " to [" + loc.getX() + "," + loc.getY() + "," + loc.getZ()
                    + " | " + loc.getYaw() + "," + loc.getPitch() + "]");
        }
    }

    /**
     * Parses a string to return a double value based on the input and sender's context.
     * If the input starts with "~", the method interprets it as a relative coordinate
     * and calculates the value based on the sender's current location or orientation.
     *
     * <p>This method supports several arguments:</p>
     * <ul>
     *   <li><b>"x"</b>: Gets the sender's X-coordinate.</li>
     *   <li><b>"y"</b>: Gets the sender's Y-coordinate.</li>
     *   <li><b>"z"</b>: Gets the sender's Z-coordinate.</li>
     *   <li><b>"yaw"</b>: Gets the sender's yaw (rotation angle around Y-axis).</li>
     *   <li><b>"pitch"</b>: Gets the sender's pitch (tilt angle).</li>
     * </ul>
     *
     * <p>If the string starts with "~" followed by a number, this additional value is added to
     * the base value derived from the sender's context.</p>
     *
     * @param sender The command sender, which could be a {@link Player} or {@link BlockCommandSender},
     *               used to retrieve the location or orientation details.
     * @param s      The input string to parse, which could include a relative indicator ("~")
     *               or a direct numeric value.
     * @param arg    The argument specifying which location or orientation property to retrieve
     *               (e.g., "x", "y", "z", "yaw", or "pitch").
     * @return A double value representing the parsed number or the calculated coordinate/orientation detail.
     * @throws NumberFormatException If the input string cannot be parsed into a valid number or
     *                               if the "~" syntax is improperly formatted.
     */
    private double getDouble(CommandSender sender, String s, String arg) throws NumberFormatException {
        try {
            if (s.startsWith("~")) {
                Location loc = null;
                if (sender instanceof Player) {
                    loc = ((Player) sender).getLocation();
                } else if (sender instanceof BlockCommandSender) {
                    loc = ((BlockCommandSender) sender).getBlock().getLocation().add(0.5, 0, 0.5);
                }
                if (loc != null) {
                    double value;
                    switch (arg) {
                        case "x":
                            value = loc.getX();
                            break;
                        case "y":
                            value = loc.getY();
                            break;
                        case "z":
                            value = loc.getZ();
                            break;
                        case "yaw":
                            value = loc.getYaw();
                            break;
                        case "pitch":
                            value = loc.getPitch();
                            break;
                        default:
                            return 0;
                    }
                    if (s.length() > 1) {
                        try {
                            double addition = Double.parseDouble(s.substring(1));
                            return value + addition;
                        } catch (NumberFormatException ignored) {
                        }
                    } else {
                        return value;
                    }
                }
            }
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(ChatColor.RED + "Couldn't parse [" + arg + "] number: " + s);
        }
    }
}