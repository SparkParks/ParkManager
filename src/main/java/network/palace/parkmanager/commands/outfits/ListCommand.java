package network.palace.parkmanager.commands.outfits;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.outfits.Outfit;
import org.bukkit.ChatColor;

/**
 * Represents a command to list all available outfits for a player.
 *
 * <p>
 * This command retrieves the list of outfits from the wardrobe manager and displays them
 * to the player with their respective IDs and names.
 * </p>
 *
 * <h3>Functionality</h3>
 * <ul>
 *   <li>Provides the player with a list of available outfits.</li>
 *   <li>Displays each outfit along with its unique ID and name.</li>
 * </ul>
 *
 * <h3>Command Behavior</h3>
 * <ul>
 *   <li>This command is invoked with the name "list".</li>
 *   <li>It sends a formatted message to the player showing available outfits in their wardrobe.</li>
 * </ul>
 *
 * <h3>Error Handling</h3>
 * <ul>
 *   <li>Throws a {@link CommandException} if an issue arises during command execution.</li>
 * </ul>
 */
@CommandMeta(description = "List all outfits")
public class ListCommand extends CoreCommand {

    /**
     * Constructs a new <code>ListCommand</code> instance.
     *
     * <p>
     * This constructor initializes the <code>ListCommand</code> with the identifier <code>"list"</code>,
     * enabling users to view a list of available outfits using the command `/outfit list`.
     * </p>
     *
     * <h3>Purpose</h3>
     * <p>
     * The <code>ListCommand</code> is part of the outfit management system, designed to provide players with
     * a summary of all outfits currently stored in the wardrobe manager. Each outfit is displayed with its unique
     * identifier and name, formatted for readability.
     * </p>
     *
     * <h3>Command Integration</h3>
     * <p>
     * This class is part of the subcommand system under the larger <code>OutfitCommand</code>, allowing modular management
     * of outfits. The <code>ListCommand</code> particularly focuses on retrieving and formatting a list of outfits
     * for display to the player.
     * </p>
     */
    public ListCommand() {
        super("list");
    }

    /**
     * Handles the "list" command to display all available outfits to the specified player.
     *
     * <p>
     * This method retrieves the list of outfits from the wardrobe manager and sends a
     * message to the player detailing each outfit's unique ID and name.
     * </p>
     *
     * <h3>Functionality</h3>
     * <ul>
     *   <li>Iterates through the available outfits from the wardrobe manager.</li>
     *   <li>Sends a message to the player listing each outfit with its corresponding ID and name.</li>
     * </ul>
     *
     * <h3>Error Handling</h3>
     * <ul>
     *   <li>Throws a {@link CommandException} in case of a command execution issue.</li>
     * </ul>
     *
     * @param player the player executing the command; the list of outfits will be displayed to this player.
     * @param args an array of arguments passed along with the command; not used in this implementation.
     * @throws CommandException if an error occurs while executing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Outfits:");
        for (Outfit outfit : ParkManager.getWardrobeManager().getOutfits()) {
            player.sendMessage(ChatColor.AQUA + "- [" + outfit.getId() + "] " + ChatColor.YELLOW + outfit.getName());
        }
    }
}
