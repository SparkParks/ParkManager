package network.palace.parkmanager.commands.config;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * Represents a command that sets the server's spawn location to the player's current location.
 * <p>
 * This command allows administrators or authorized users to define the default spawn location
 * for the server by executing the command at their desired position.
 * </p>
 * <ul>
 *     <li>The spawn location is saved through the server's configuration utility.</li>
 *     <li>Feedback is provided to the player upon successfully setting the spawn location.</li>
 * </ul>
 * <p>
 * Extends {@code CoreCommand} and overrides its {@code handleCommand} method to implement custom logic.
 * </p>
 */
@CommandMeta(description = "Set the spawn location")
public class SpawnCommand extends CoreCommand {

    /**
     * Constructs a new <code>SpawnCommand</code> instance, which is used to set
     * the server's spawn location to the player's current position.
     *
     * <p>This constructor initializes the <code>SpawnCommand</code> with the name
     * "setspawn". The command allows administrators or designated players to
     * define the default server spawn location for all players upon joining.</p>
     *
     * <ul>
     *     <li>The spawn location is saved in the server configuration using the configuration utility.</li>
     *     <li>Supports real-time feedback to the executing player upon successfully setting the spawn location.</li>
     * </ul>
     *
     * <p>The <code>SpawnCommand</code> extends the <code>CoreCommand</code> class, inheriting
     * its core functionality and behavior, while overriding the necessary methods
     * to implement custom logic for setting the spawn location.</p>
     */
    public SpawnCommand() {
        super("setspawn");
    }

    /**
     * Handles the command execution to set the server's spawn location to the player's current position.
     * <p>
     * This method updates the server configuration to save the spawn location and provides
     * feedback to the player upon successful execution.
     * </p>
     *
     * @param player The player executing the command. The spawn location is set to the player's current position.
     * @param args Additional arguments passed with the command. These are not utilized by this method.
     * @throws CommandException If an error occurs while processing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        ParkManager.getConfigUtil().setSpawn(player.getLocation());
        player.sendMessage(ChatColor.GRAY + "Set server spawn to where you're standing!");
    }
}
