package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * Represents a command to manage and open the pack settings menu for players.
 * <p>
 * This class extends {@link CoreCommand} and allows players to interact with and manage
 * server pack settings. The command also includes functionality for privileged users
 * to set the server's pack directly.
 * </p>
 *
 * <h2>Command Functionality</h2>
 * <ul>
 *   <li>Opens the pack settings menu for players.</li>
 *   <li>Allows privileged users (with sufficient rank) to set the server's pack using the "setpack" subcommand.</li>
 * </ul>
 *
 * <p>
 * The primary command is <b>/pack</b>, with the optional subcommand:
 * </p>
 * <ul>
 *   <li><code>/pack setpack [pack]</code>: Sets the server's current pack to the specified value.
 *       This operation is limited to users with an adequate rank.</li>
 * </ul>
 *
 * <p>
 * Usage behavior:
 * <ul>
 *   <li>If no arguments are provided, or if the player does not have the required rank,
 *       the pack settings menu will be opened.</li>
 *   <li>If the "setpack" argument is provided without specifying a pack name, the player
 *       will receive detailed usage instructions.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The player must have a rank greater than or equal to {@link Rank#CM} to utilize the "setpack" subcommand.
 * </p>
 */
@CommandMeta(description = "Open the Pack settings menu")
public class PackCommand extends CoreCommand {

    /**
     * Constructs the <code>PackCommand</code> instance and initializes the command with the base keyword "pack".
     * <p>
     * This command allows players to interact with pack-related settings on the server.
     * By default, executing the command will open the pack settings menu. Players with adequate permissions
     * can also invoke specific subcommands to configure the server's pack settings.
     * </p>
     *
     * <h2>Command Registration</h2>
     * <p>
     * The command is registered under the name "pack". Additional subcommands such as "setpack"
     * are managed internally within the command logic.
     * </p>
     *
     * <h2>Command Purpose</h2>
     * <ul>
     *   <li>Allows players to access the pack settings menu.</li>
     *   <li>Grants privileged users the ability to set the server's active pack.</li>
     * </ul>
     *
     * <h2>Permissions</h2>
     * <p>
     * Players must have a rank greater than or equal to {@link Rank#CM} to access certain subcommands
     * like setting the server pack.
     * </p>
     */
    public PackCommand() {
        super("pack");
    }

    /**
     * Handles the execution of the "/pack" command for players, determining behavior
     * based on the provided arguments and the player's rank.
     *
     * <p>
     * This method processes the following use cases:
     * <ul>
     *   <li>If no arguments are provided or the player's rank is insufficient, it will open the pack settings menu.</li>
     *   <li>If the "setpack" subcommand is provided by a player with an adequate rank but no pack is specified, it displays usage instructions.</li>
     *   <li>If the "setpack" subcommand and a valid pack name are provided, it updates the server's pack setting to the specified pack.</li>
     * </ul>
     * </p>
     *
     * @param player The {@link CPlayer} instance representing the player executing the command.
     *               This object is used to check the player's rank and send messages or interfaces.
     * @param args   An array containing the command arguments. The first argument determines
     *               the action (e.g., "setpack"), and subsequent arguments provide additional details.
     * @throws CommandException If an unexpected issue occurs during command execution.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1 || player.getRank().getRankId() < Rank.CM.getRankId()) {
            ParkManager.getPackManager().openMenu(player);
            return;
        }
        if (args.length < 2 || !args[0].equalsIgnoreCase("setpack")) {
            player.sendMessage(ChatColor.AQUA + "/pack setpack [pack] - Set the server's pack");
            return;
        }
        ParkManager.getPackManager().setServerPack(args[1]);
        player.sendMessage(ChatColor.GREEN + "Set this server's pack setting to " + ChatColor.YELLOW + args[1] + "!");
    }
}
