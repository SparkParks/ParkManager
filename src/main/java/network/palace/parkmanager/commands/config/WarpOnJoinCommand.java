package network.palace.parkmanager.commands.config;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * Represents a command to configure the "warp-on-join" setting, allowing
 * players to enable or disable automatic warping of players when they join.
 * <p>
 * This command interacts with the configuration through the {@code ParkManager}.
 * </p>
 *
 * <p><b>Command Syntax:</b></p>
 * <lu>
 *   <li>{@code /spawn warponjoin [true/false]}</li>
 * </lu>
 *
 * <p><b>Responsibilities:</b></p>
 * <lu>
 *   <li>Sets the warp-on-join option to either true or false based on the input parameter.</li>
 *   <li>Notifies the player about the updated status of warp-on-join setting via a success or failure message.</li>
 * </lu>
 *
 * <p><b>Behavior:</b></p>
 * <lu>
 *   <li>If no argument is provided, a usage message is sent to the player.</li>
 *   <li>Updates the warp-on-join configuration and sends feedback to the player indicating whether warp-on-join is enabled or disabled.</li>
 * </lu>
 *
 * <p><b>Dependencies:</b></p>
 * <lu>
 *   <li>{@code ParkManager} - to access and update the warp-on-join configuration.</li>
 *   <li>{@code ChatColor} - to format the feedback messages sent to the player.</li>
 * </lu>
 *
 * <p><b>Player Interaction:</b></p>
 * <lu>
 *   <li>If invalid or no arguments are provided, the command will send a usage message.</li>
 *   <li>On valid input, displays whether the warp-on-join setting has been enabled or disabled.</li>
 * </lu>
 */
@CommandMeta(description = "Set the warp-on-join setting")
public class WarpOnJoinCommand extends CoreCommand {

    /**
     * Constructs a new <code>WarpOnJoinCommand</code> instance, initializing
     * the command with the identifier "warponjoin".
     *
     * <p>This constructor sets up the command which, when executed, allows
     * players to enable or disable the "warp-on-join" feature within the
     * server configuration. By default, this command registers with the
     * specified name and handles related operations invoked by players.</p>
     *
     * <p><b>Key Features:</b></p>
     * <ul>
     *   <li>Initializes the command with the name "warponjoin".</li>
     *   <li>Prepares the command framework to handle further execution logic
     *       defined in the appropriate handleCommand method.</li>
     * </ul>
     */
    public WarpOnJoinCommand() {
        super("warponjoin");
    }

    /**
     * Handles the command to configure the "warp-on-join" setting. This command allows
     * players to enable or disable the automatic warping functionality when they join.
     *
     * <p>Behavior:</p>
     * <ul>
     *   <li>If no arguments are provided or the arguments are invalid, the command sends a usage message to the player.</li>
     *   <li>On valid input, the warp-on-join configuration is updated, and the player is notified if it has been enabled or disabled.</li>
     * </ul>
     *
     * @param player The {@code CPlayer} who executed the command. This player will receive feedback messages.
     * @param args   The arguments provided by the player. The first argument should indicate whether "warp-on-join" should be enabled
     *               or disabled (as "true" or "false"). If absent, the command sends a usage message.
     *
     * @throws CommandException If any unexpected error occurs while processing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/spawn warponjoin [true/false]");
            return;
        }
        ParkManager.getConfigUtil().setWarpOnJoin(Boolean.parseBoolean(args[0]));
        if (ParkManager.getConfigUtil().isWarpOnJoin()) {
            player.sendMessage(ChatColor.GREEN + "Enabled warp-on-join!");
        } else {
            player.sendMessage(ChatColor.RED + "Disabled warp-on-join!");
        }
    }
}
