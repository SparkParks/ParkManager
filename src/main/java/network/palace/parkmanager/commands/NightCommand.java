package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

/**
 * Represents a command implementation that sets the time of the targeted world(s) to night.
 * <p>
 * This command is primarily used to change the time of a player's current world, a block's world,
 * or all available worlds to night time.
 * </p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 * <li>Allows a player to set the time in their current world to night (time value: 16000).</li>
 * <li>Allows a block command sender to set the time in the respective world to night.</li>
 * <li>Allows the execution of the command to set all worlds' time to night when executed from an
 * unspecified sender.</li>
 * </ul>
 *
 * <p>The command can be executed by valid entities such as players, block command senders, and also
 * through an unspecified global sender.</p>
 *
 * <h3>Command Metadata:</h3>
 * <ul>
 * <li><b>Description:</b> "Set time to night".</li>
 * <li><b>Rank:</b> Requires a rank of {@link Rank#CM} or higher to execute the command.</li>
 * <li><b>Command Name:</b> "night".</li>
 * </ul>
 *
 * <h3>Handler Details:</h3>
 * <ul>
 * <li><b>Player-Specific Handling:</b> Sets the time of the player's current world to night and sends
 *      a confirmation message to the player.</li>
 * <li><b>Block Command Sender Handling:</b> Sets the time of the block's world to night and sends a
 *      confirmation message to the block sender.</li>
 * <li><b>General Command Sender Handling:</b> Iterates through all worlds available on the server,
 *      sets their time to night, and sends a confirmation message.</li>
 * </ul>
 *
 * <p>The command ensures user feedback by sending an informational message to the sender regarding the
 * time change in the targeted world(s).</p>
 */
@CommandMeta(description = "Set time to night", rank = Rank.CM)
public class NightCommand extends CoreCommand {

    /**
     * Constructs a new instance of the {@code NightCommand} class.
     * <p>
     * This command is intended to switch the in-game time to night, allowing server admins
     * or command block users to effortlessly set the world time. It is executed using the command
     * name <b>night</b>.
     * </p>
     * <p>
     * The {@code NightCommand} inherits from a base class that provides infrastructure for handling commands.
     * This constructor initializes the command name by passing it to the superclass.
     * </p>
     */
    public NightCommand() {
        super("night");
    }

    /**
     * Handles the execution logic for the "NightCommand" that sets the in-game time to night.
     * <p>
     * This method changes the time in the player's current world to night (time 16000) and
     * sends a confirmation message to the player.
     *
     * @param player The `CPlayer` object representing the player who executed the command.
     *               Must not be null and must have an associated world.
     * @param args   The arguments passed to the command. Not used in this implementation,
     *               but must be passed as part of the method signature.
     *
     * @throws CommandException Thrown if there is an issue executing the command. Typically
     *                          used for error handling when command execution fails.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.getWorld().setTime(16000);
        player.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Night " + ChatColor.GRAY + "in world " +
                ChatColor.GREEN + player.getWorld().getName() + ".");
    }

    /**
     * Handles the execution of a block command to set the time to night in the block's world.
     *
     * <p>This method adjusts the current world's time to a specific point (night) and sends
     * a feedback message to the command sender, indicating the operation's success. It is triggered
     * when the command is sent from a block-oriented context.</p>
     *
     * @param commandSender The sender of the command, represented as a {@code BlockCommandSender}.
     *                      This provides access to the block and its world in which the command
     *                      is executed.
     * @param args An array of {@code String} arguments passed to the command. These arguments are
     *             currently unused within this method but are included as part of the method's signature
     *             to conform to the broader command-handling system.
     *
     * @throws CommandException If an error occurs while processing the command. While this method does
     *                          not explicitly throw an exception during its current implementation, this
     *                          declaration ensures compatibility with parent methods and possible future changes.
     */
    @Override
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        commandSender.getBlock().getWorld().setTime(16000);
        commandSender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Night " + ChatColor.GRAY + "in world " +
                ChatColor.GREEN + commandSender.getBlock().getWorld().getName() + ".");
    }

    /**
     * Handles the execution of the command to set the in-game time to night across all worlds.
     *
     * <p>This method iterates through all loaded worlds in the server, setting their time to night
     * (time 16000), and notifies the command sender about the change for each world.</p>
     *
     * <p>It is intended to be a general-purpose handler for this command, applicable to any type of
     * {@code CommandSender}.</p>
     *
     * @param sender The sender of the command, represented as a {@code CommandSender} object.
     *               This can be a player, console, or any entity capable of sending commands.
     * @param args   An array of {@code String} arguments provided with the command.
     *               These arguments are not used in the current implementation but are required
     *               for compatibility with the command-handling system.
     *
     * @throws CommandException If an error occurs during command execution, ensuring consistency
     *                          with the command-handling framework.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(16000);
            sender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Night " + ChatColor.GRAY + "in world " +
                    ChatColor.GREEN + world.getName() + ".");
        }
    }
}
