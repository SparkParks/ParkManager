package network.palace.parkmanager.commands.config.park;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import org.bukkit.ChatColor;

/**
 * <p>
 * The {@code ListCommand} class provides a command that lists all local parks
 * available on the server. This command is intended for use by players to view
 * detailed information about parks, including their names, regions, and world locations.
 * </p>
 *
 * <p>
 * When executed, the command retrieves the list of parks through the {@code ParkManager} and
 * displays their details as formatted messages. Each park is displayed with its identifier,
 * title, region, and associated world.
 * </p>
 *
 * <p>
 * Command usage is tied to the base functionality defined in {@code CoreCommand}, and follows
 * its structure while overriding specific handling for this command.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Lists all registered parks on the server.</li>
 *   <li>Displays park details such as:
 *     <ul>
 *       <li><strong>Park ID</strong>: Unique identifier for the park.</li>
 *       <li><strong>Title</strong>: Descriptive name of the park.</li>
 *       <li><strong>Region</strong>: The in-game region where the park is located.</li>
 *       <li><strong>World</strong>: The game world containing the park.</li>
 *     </ul>
 *   </li>
 *   <li>Formats output using color codes for readability.</li>
 * </ul>
 *
 * <h2>Notes:</h2>
 * <ul>
 *   <li>This command is intended for use by players through the server's command interface.</li>
 *   <li>Access to the command may depend on player permissions managed by the server implementation.</li>
 * </ul>
 */
@CommandMeta(description = "List all local parks on this server")
public class ListCommand extends CoreCommand {

    /**
     * <p>
     * Constructs a new {@code ListCommand} instance. This command is used to list all
     * the available local parks on the server, providing details such as park identifier,
     * title, region, and world.
     * </p>
     *
     * <p>
     * The constructor initializes the command with a specific label ("list") and helps
     * set up the basic framework for executing the listing functionality. By extending the
     * {@code CoreCommand} class, this command inherits the necessary infrastructure for
     * player-command interaction and customization.
     * </p>
     *
     * <h2>Key Characteristics:</h2>
     * <ul>
     *   <li>Initializes a command labeled "list".</li>
     *   <li>Sets up a foundation for executing park list retrieval functionality.</li>
     *   <li>Relies on the structure and features provided by the base class {@code CoreCommand}.</li>
     * </ul>
     *
     * <h2>Usage:</h2>
     * <ul>
     *   <li>Used by players to view and explore a comprehensive list of parks registered
     *       on the server.</li>
     *   <li>Expected to be invoked via the command interface provided by the server implementation.</li>
     * </ul>
     */
    public ListCommand() {
        super("list");
    }

    /**
     * Handles the execution of the "list" command which displays a list of all registered parks
     * available on the server. This method retrieves park details through the {@code ParkManager}
     * and formats the information for display to the player.
     *
     * <p>The displayed information for each park includes:</p>
     * <ul>
     *   <li><strong>ID:</strong> The unique identifier of the park.</li>
     *   <li><strong>Title:</strong> A descriptive title of the park.</li>
     *   <li><strong>Region:</strong> The in-game region where the park is located.</li>
     *   <li><strong>World:</strong> The game world containing the park.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} executing the command. This parameter represents the player
     *               who will receive the formatted list of parks through in-game messages.
     * @param args   The command arguments provided by the player. This parameter may be empty
     *               as this command does not rely on additional arguments for execution.
     *
     * @throws CommandException If there is an error during command execution, such as issues retrieving
     *                          park details or insufficient permissions for the command caller.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Local Parks:");
        for (Park park : ParkManager.getParkUtil().getParks()) {
            player.sendMessage(ChatColor.AQUA + "- [" + park.getId().name() + "] " + ChatColor.YELLOW + park.getId().getTitle() +
                    ChatColor.GREEN + " in region " + ChatColor.YELLOW + park.getRegion() + ChatColor.GREEN + " on world " + ChatColor.YELLOW + park.getWorld().getName());
        }
    }
}
