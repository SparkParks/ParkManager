package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.handlers.AttractionCategory;
import network.palace.parkmanager.handlers.Park;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command used to edit an existing attraction within a park.
 *
 * <p>The {@code EditCommand} class allows players to modify various attributes of
 * an attraction, including its name, warp, description, categories, and associated item.
 * The command requires the player to be inside a park and provides detailed feedback
 * for invalid inputs or insufficient arguments.</p>
 *
 * <p>Supported attributes for editing include:</p>
 * <ul>
 *   <li><b>Name:</b> Updates the display name of the attraction, with support for
 *       color codes.</li>
 *   <li><b>Warp:</b> Sets a warp location identifier for the attraction.</li>
 *   <li><b>Description:</b> Changes the detailed description of the attraction.</li>
 *   <li><b>Categories:</b> Updates the categories or tags associated with the attraction.</li>
 *   <li><b>Item:</b> Assigns a new item to visually represent the attraction in menus.</li>
 * </ul>
 *
 * <p>Upon successful edits, the command saves the updated attraction data
 * to the persistence layer.</p>
 *
 * <p>If the command is entered incorrectly or with missing arguments, a help menu
 * is displayed to guide the player on proper usage.</p>
 *
 * <h2>Command Structure:</h2>
 * <p>Players must provide at least an attraction ID and an attribute to edit. Additional
 * input may be required depending on the specific attribute being modified.</p>
 *
 * <p><b>Note:</b> Attempting to edit an attraction outside of a park or using an invalid
 * attraction ID will result in an error message being displayed to the player.</p>
 */
@CommandMeta(description = "Edit an existing attraction")
public class EditCommand extends CoreCommand {

    /**
     * Constructs a new {@code EditCommand} instance.
     * <p>
     * The {@code EditCommand} is responsible for handling the "edit" command functionality
     * within the application. This command is intended to provide edit-related operations
     * for attractions within the park management system.
     * <p>
     * This constructor initializes the command with the name "edit".
     *
     * <ul>
     * <li>The command is tied to an underlying command handling structure.</li>
     * <li>Specific behaviors of this command are expected to be defined in the
     * {@code handleCommand} method, which may handle player interactions and specific parameters.</li>
     * </ul>
     */
    public EditCommand() {
        super("edit");
    }

    /**
     * Handles editing commands for attractions within parks. This method allows the player to modify various
     * attributes of an attraction, such as its name, warp, description, categories, or item representation.
     * <p>
     * If insufficient or invalid arguments are provided, the player will be presented with the help menu showing
     * the correct usage of the command.
     * </p>
     *
     * <p><b>Supported subcommands:</b></p>
     * <ul>
     *    <li><b>name:</b> Sets or updates the display name of the attraction.</li>
     *    <li><b>warp:</b> Sets or updates the warp location associated with the attraction.</li>
     *    <li><b>description:</b> Updates the attraction's description.</li>
     *    <li><b>categories:</b> Assigns one or more categories to the attraction.</li>
     *    <li><b>item:</b> Updates the item representing the attraction in the menu.</li>
     * </ul>
     * <p>
     * Commands must be executed while the player is inside the park containing the attraction being modified.
     * </p>
     *
     * @param player The player executing the command.
     *               This parameter provides the context of the player, including location and permissions.
     * @param args   The array of command arguments:
     *               <ul>
     *                   <li><code>args[0]</code> - The unique identifier of the attraction being edited.</li>
     *                   <li><code>args[1]</code> - The subcommand specifying the attribute to modify. Valid values include:
     *                       <ul>
     *                           <li><code>name</code> - Updates the attraction's display name.</li>
     *                           <li><code>warp</code> - Updates the warp location.</li>
     *                           <li><code>description</code> - Updates the description.</li>
     *                           <li><code>categories</code> - Updates the attraction's categories.</li>
     *                           <li><code>item</code> - Updates the menu item representation.</li>
     *                       </ul>
     *                   </li>
     *                   <li><code>args[2...]</code> - Additional data required for specific subcommands (e.g., a new name, warp location, or categories).</li>
     *               </ul>
     * @throws CommandException If an error occurs during command execution, such as missing arguments, invalid input,
     *                          or if the player is not within a park or the specified attraction cannot be found.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            helpMenu(player);
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Attraction attraction = ParkManager.getAttractionManager().getAttraction(args[0], park.getId());
        if (attraction == null) {
            player.sendMessage(ChatColor.RED + "Could not find an attraction by id " + args[0] + "!");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "name": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                StringBuilder name = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    name.append(args[i]).append(" ");
                }
                String displayName = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', name.toString().trim());

                player.sendMessage(ChatColor.GREEN + "Set " + attraction.getName() + "'s " + ChatColor.GREEN +
                        "display name to " + ChatColor.YELLOW + displayName);

                attraction.setName(displayName);

                ItemStack item = attraction.getItem();
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(displayName);
                item.setItemMeta(meta);

                ParkManager.getAttractionManager().saveToFile();
                return;
            }
            case "warp": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                attraction.setWarp(args[2]);

                player.sendMessage(ChatColor.GREEN + "Set " + attraction.getName() + "'s " + ChatColor.GREEN +
                        "warp to " + ChatColor.YELLOW + args[2]);

                ParkManager.getAttractionManager().saveToFile();
                return;
            }
            case "description": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                StringBuilder description = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    description.append(args[i]).append(" ");
                }
                player.sendMessage(ChatColor.GREEN + "Set " + attraction.getName() + "'s " + ChatColor.GREEN +
                        "description to " + ChatColor.DARK_AQUA + description.toString());

                attraction.setDescription(description.toString());

                ParkManager.getAttractionManager().saveToFile();
                return;
            }
            case "categories": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                List<AttractionCategory> categories = new ArrayList<>();
                StringBuilder list = new StringBuilder();
                for (String s : args[2].split(",")) {
                    AttractionCategory category = AttractionCategory.fromString(s);
                    if (category == null) {
                        player.sendMessage(ChatColor.RED + "Unknown category '" + s + "'!");
                        continue;
                    }
                    categories.add(category);
                    list.append(category.getShortName()).append(",");
                }
                attraction.setCategories(categories);

                player.sendMessage(ChatColor.GREEN + "Set " + attraction.getName() + "'s " + ChatColor.GREEN +
                        "attraction categories to " + ChatColor.YELLOW + list.substring(0, list.length() - 1));

                ParkManager.getAttractionManager().saveToFile();
                return;
            }
            case "item": {
                ItemStack item = player.getItemInMainHand().clone();
                if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
                    player.sendMessage(ChatColor.RED + "Hold the item in your hand that will represent the attraction in the menu!");
                    return;
                }
                attraction.setItem(item);

                player.sendMessage(ChatColor.GREEN + "Updated " + attraction.getName() + "'s " + ChatColor.GREEN + "item!");

                ParkManager.getAttractionManager().saveToFile();
                return;
            }
        }
        helpMenu(player);
    }

    /**
     * Displays the help menu for "edit" commands related to attractions.
     * <p>
     * This method provides the player with detailed information on how to use
     * the available attraction edit commands, including their syntax and options.
     * The displayed commands allow modifications such as updating the name, warp,
     * description, categories, and item properties of an attraction.
     * </p>
     *
     * <p><b>Displayed commands:</b></p>
     * <ul>
     *   <li><code>/attraction edit [id] name [name]</code> - Updates the name of the attraction.</li>
     *   <li><code>/attraction edit [id] warp [warp]</code> - Sets or updates the attraction's warp location.</li>
     *   <li><code>/attraction edit [id] description [description]</code> - Updates the description of the attraction.
     *       <br>(Note: <i>Color codes are not supported in descriptions.</i>)</li>
     *   <li><code>/attraction edit [id] categories [category1,category2]</code> - Assigns or updates categories for the attraction.</li>
     *   <li><code>/attraction edit [id] item</code> - Updates the item representation of the attraction.</li>
     * </ul>
     *
     * @param player The player to whom the help menu is being displayed. This player instance
     *               contains information about the player's context, such as their permissions
     *               and current location.
     */
    private void helpMenu(CPlayer player) {
        player.sendMessage(ChatColor.RED + "/attraction edit [id] name [name]");
        player.sendMessage(ChatColor.RED + "/attraction edit [id] warp [warp]");
        player.sendMessage(ChatColor.RED + "/attraction edit [id] description [description] (Color codes are "
                + ChatColor.ITALIC + "not " + ChatColor.RED + "supported in descriptions!)");
        player.sendMessage(ChatColor.RED + "/attraction edit [id] categories [category1,category2]");
        player.sendMessage(ChatColor.RED + "/attraction edit [id] item");
    }
}
