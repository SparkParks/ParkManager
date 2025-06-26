package network.palace.parkmanager.commands.config;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * <p>
 * The {@code SpawnOnJoinCommand} class is a command implementation extending {@link CoreCommand},
 * which allows toggling the "spawn-on-join" feature. This command enables or disables
 * players being teleported to a spawn location upon joining the server.
 * </p>
 *
 * <p>
 * The command can be executed by providing a single argument, either {@code true} or {@code false}:
 * <lu>
 *   <li>{@code true} - Enables the spawn-on-join feature.</li>
 *   <li>{@code false} - Disables the spawn-on-join feature.</li>
 * </lu>
 * </p>
 *
 * <p>
 * If the argument is invalid or missing, the command will provide feedback to the executor about
 * the correct usage of the command.
 * </p>
 *
 * <p>
 * Upon successful execution of the command, an appropriate message will be sent to the player to
 * confirm the updated status of the spawn-on-join setting.
 * </p>
 */
@CommandMeta(description = "Set the spawn-on-join setting")
public class SpawnOnJoinCommand extends CoreCommand {

    /**
     * Constructs a new <code>SpawnOnJoinCommand</code> instance, which provides
     * functionality to toggle the "spawn-on-join" feature in the server.
     *
     * <p>The "spawn-on-join" feature determines whether players should be teleported
     * to the spawn location upon joining the server. Executing this command allows
     * authorized users to enable or disable this functionality.</p>
     *
     * <ul>
     *   <li><b>Command Name</b>: spawnonjoin</li>
     *   <li><b>Purpose</b>: Toggles the status of the spawn-on-join feature.</li>
     *   <li><b>Expected Arguments</b>: {@code true} or {@code false}</li>
     *   <li><b>Feedback</b>: Provides confirmation messages to the player upon successful execution.</li>
     * </ul>
     *
     * <p>
     * This command extends the <code>CoreCommand</code> class and overrides
     * the <code>handleCommand</code> method to implement custom logic for updating
     * the feature's status in the server configuration.
     * </p>
     */
    public SpawnOnJoinCommand() {
        super("spawnonjoin");
    }

    /**
     * Handles the "spawn-on-join" command to toggle whether players are teleported to a spawn
     * location upon joining the server. This method expects a single argument: {@code true} to
     * enable or {@code false} to disable the feature.
     *
     * <p>
     * If no argument or an invalid argument is provided, the player will receive an error message
     * indicating the correct usage of the command.
     * </p>
     *
     * <p>
     * After successfully changing the setting, the method sends feedback to the player indicating
     * the updated status.
     * </p>
     *
     * @param player The {@link CPlayer} executing the command. This is the player whose action triggered
     *               the command handler.
     * @param args   The command arguments provided by the player. Expected to include a single argument:
     *               {@code "true"} or {@code "false"} to toggle the spawn-on-join feature.
     * @throws CommandException Thrown if an error occurs while executing the command, such as a restriction
     *                          on the player's permission or any internal command issue.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/spawn spawnonjoin [true/false]");
            return;
        }
        ParkManager.getConfigUtil().setSpawnOnJoin(Boolean.parseBoolean(args[0]));
        if (ParkManager.getConfigUtil().isSpawnOnJoin()) {
            player.sendMessage(ChatColor.GREEN + "Enabled spawn-on-join!");
        } else {
            player.sendMessage(ChatColor.RED + "Disabled spawn-on-join!");
        }
    }
}
