package network.palace.parkmanager.commands.outfits;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * <p>
 * The {@code ReloadCommand} class is a command implementation allowing
 * players to reload outfits from the database. It extends the {@code CoreCommand}
 * base class and provides functionality to refresh data for wardrobes in the system.
 * </p>
 *
 * <p>
 * When the command is executed, it triggers the initialization of
 * {@code WardrobeManager} via the {@code ParkManager} class, ensuring
 * the wardrobe data is updated. Feedback is provided to the player during
 * the process through in-game messages.
 * </p>
 *
 * <p>
 * This command is commonly used by administrators or operators to refresh
 * outfit or wardrobe data without requiring a server restart.
 * </p>
 *
 * <ul>
 * <li><b>Command:</b> {@code reload}</li>
 * <li><b>Description:</b> Reloads outfit data from the database.</li>
 * </ul>
 */
@CommandMeta(description = "Reload outfits from the database")
public class ReloadCommand extends CoreCommand {

    /**
     * Constructs a new <code>ReloadCommand</code> instance.
     *
     * <p>
     * This constructor initializes the <code>ReloadCommand</code> with the command name <code>"reload"</code>,
     * enabling users to execute the command for refreshing outfit-related data in the system.
     * </p>
     *
     * <h3>Purpose</h3>
     * <p>
     * The <code>ReloadCommand</code> is designed to allow administrators or operators to reload
     * outfits from the database. This is useful for applying changes to wardrobe configurations
     * without restarting the server, ensuring a smooth and responsive experience for players.
     * </p>
     *
     * <h3>Command Integration</h3>
     * <p>
     * This class integrates with the <code>WardrobeManager</code> via the <code>ParkManager</code>,
     * triggering a re-initialization of relevant data. It is part of the outfit management system and
     * focuses specifically on updating and synchronizing data from the database to the game environment.
     * </p>
     *
     * <h3>Command Behavior</h3>
     * <ul>
     *   <li>This command is invoked with the name <code>"reload"</code>.</li>
     *   <li>On execution, it communicates with the <code>WardrobeManager</code> to refresh outfit data.</li>
     *   <li>Sends feedback messages to the user indicating the start and completion of the reload process.</li>
     * </ul>
     */
    public ReloadCommand() {
        super("reload");
    }

    /**
     * Handles the {@code reload} command to reload outfit data from the database.
     * <p>
     * This method sends feedback to the command executor to indicate the
     * start and completion of the wardrobe data reinitialization process.
     * It interacts with the {@code WardrobeManager} through {@code ParkManager}
     * to refresh the data and ensure the latest information is loaded.
     * </p>
     *
     * @param player The player executing the command. The player receives feedback
     *               messages during the process.
     * @param args   An array of arguments passed with the command. This command
     *               does not require any additional arguments.
     * @throws CommandException If an error occurs while processing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Reloading outfits from the database...");
        ParkManager.getWardrobeManager().initialize();
        player.sendMessage(ChatColor.GREEN + "Finished reloading outfits!");
    }
}
