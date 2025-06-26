package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Represents the command that handles teleporting a player to the spawn location.
 * <p>
 * This class extends the {@link CoreCommand} class and is used to implement
 * the functionality for the "/spawn" command. When invoked, this command
 * teleports the player to the configured spawn location if it exists.
 * </p>
 *
 * <h3>Behavior:</h3>
 * <ul>
 *   <li>If a spawn location is configured, the player will be teleported to it
 *       and receive a confirmation message.</li>
 *   <li>If no spawn location is configured, an error message will be sent
 *       to the player.</li>
 * </ul>
 *
 * <h3>Command Metadata:</h3>
 * <ul>
 *   <li>Description: "Teleport to spawn"</li>
 *   <li>Command: "/spawn"</li>
 * </ul>
 *
 * <h3>Usage Notes:</h3>
 * <ul>
 *   <li>This command requires that a spawn location be previously set via
 *       configuration.</li>
 *   <li>If the spawn location is null, players will be notified of the absence
 *       of a configured spawn.</li>
 * </ul>
 *
 * <h3>Exceptions:</h3>
 * <ul>
 *   <li><b>CommandException:</b> Thrown when there is an issue handling the
 *   command logic.</li>
 * </ul>
 */
@CommandMeta(description = "Teleport to spawn")
public class SpawnCommand extends CoreCommand {

    /**
     * Constructs a new {@code SpawnCommand} instance and initializes it with
     * the command name "spawn".
     * <p>
     * This constructor sets up the base command structure needed for handling
     * the "/spawn" command, which teleports a player to the configured spawn
     * location. The command's metadata and logic are defined in the containing
     * class.
     * </p>
     *
     * <p><b>Purpose:</b> This constructor serves as the entry point for
     * initializing the "spawn" command, enabling the system to register and
     * process this command appropriately.</p>
     */
    public SpawnCommand() {
        super("spawn");
    }

    /**
     * Handles the "/spawn" command by teleporting the player to the configured spawn location.
     * <p>
     * This method performs the following steps:
     * <ul>
     *   <li>Retrieves the spawn location defined in the configuration.</li>
     *   <li>If the spawn location is not set, notifies the player with an error message.</li>
     *   <li>If the spawn location is valid, teleports the player to the location and
     *       shows a confirmation message.</li>
     * </ul>
     * </p>
     *
     * <p><b>Note:</b> A configured spawn location is required for this command to function properly.</p>
     *
     * @param player the player executing the command; cannot be null.
     * @param args the arguments provided with the command; may be empty or contain additional context.
     * @throws CommandException if there is an error executing the command logic.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Location spawn = ParkManager.getConfigUtil().getSpawn();
        if (spawn == null) {
            player.sendMessage(ChatColor.RED + "A spawn location hasn't been configured yet!");
            return;
        }
        player.sendMessage(ChatColor.GRAY + "Teleporting you to spawn...");
        player.teleport(spawn);
    }
}
