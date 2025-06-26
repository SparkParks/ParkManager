package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.handlers.AttractionCategory;
import org.bukkit.ChatColor;

/**
 * The {@code CategoriesCommand} class is responsible for handling the "categories" command,
 * which displays a list of available attraction categories.
 *
 * <p>This command retrieves all available attraction categories and sends them to the
 * player, formatted for readability. The categories are displayed using their short names.
 *
 * <p>Key functionalities include:
 * <ul>
 *     <li>Lists all predefined attraction categories.</li>
 *     <li>Displays each category with consistent formatting and color coding for better clarity.</li>
 * </ul>
 *
 * <p><b>Command Usage</b>: Executing this command will provide users with an overview of
 * the available attractions grouped by category, assisting them in understanding
 * or selecting relevant options within the system.
 */
@CommandMeta(description = "List available attraction categories")
public class CategoriesCommand extends CoreCommand {

    /**
     * Constructs a new {@link CategoriesCommand} instance.
     *
     * <p>This command is used to display a list of currently available attraction categories
     * to the player. It initializes the command with the specified name "categories"
     * to handle the listing functionality properly.
     *
     * <p>Key responsibilities include:
     * <ul>
     *     <li>Providing a user-friendly and formatted list of attraction categories.</li>
     *     <li>Working as part of an attraction management system to help players navigate
     *     available options effectively.</li>
     * </ul>
     *
     * <p>All categories displayed by this command are aligned with the system's predefined
     * {@link AttractionCategory} enumeration.
     */
    public CategoriesCommand() {
        super("categories");
    }

    /**
     * Handles the "categories" command, providing a list of available attraction categories to the player.
     *
     * <p>This method retrieves all the predefined {@link AttractionCategory} values,
     * formats them using color codes for clarity, and sends the formatted list to the player.
     *
     * @param player the {@link CPlayer} executing the command. This is the player who will receive
     *               the list of attraction categories.
     * @param args   the command arguments passed by the player. These arguments are currently unused
     *               since the command only displays information and does not rely on additional input.
     * @throws CommandException if an error occurs while executing the command. This exception could
     *                          arise if there are issues related to permissions or command execution.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "List of attraction categories:");
        for (AttractionCategory category : AttractionCategory.values()) {
            player.sendMessage(ChatColor.AQUA + "- " + ChatColor.YELLOW + category.getShortName());
        }
    }
}
