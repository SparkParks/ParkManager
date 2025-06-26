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
 * <p>
 * The <code>CreateCommand</code> class provides functionality for creating a new attraction
 * within a specified park in the system. This command requires the player to be inside a park
 * and holding an item that will represent the attraction. The attraction is associated with
 * the specified categories, warp location, and a unique identifier.
 * </p>
 *
 * <p>
 * When the command is executed, it performs the following operations:
 * </p>
 * <ul>
 *   <li>Validates the number of arguments provided to ensure the required information for
 *       the attraction is included.</li>
 *   <li>Checks that the issuing player is located inside a park.</li>
 *   <li>Validates that the specified attraction ID does not already exist within the current park.</li>
 *   <li>Ensures that the player is holding a valid item to represent the attraction in their hand.</li>
 *   <li>Parses and validates the category list specified in the command arguments.</li>
 *   <li>Sets the display name of the item to the specified attraction name and prepares it
 *       as the menu representation for the attraction.</li>
 *   <li>Creates a new attraction object and registers it within the system.</li>
 *   <li>Confirms the successful creation of the attraction by sending a feedback message to the player.</li>
 * </ul>
 *
 * <p>
 * The command syntax for creating a new attraction is as follows:
 * </p>
 * <ul>
 *   <li><code>/attraction create [id] [warp] [category1,category2] [name]</code></li>
 * </ul>
 *
 * <p>
 * Additional notes about the operation:
 * </p>
 * <ul>
 *   <li>The item held in the player's hand will be used to represent the attraction in the menu simulation.</li>
 *   <li>The item name will be set to the display name of the attraction.</li>
 *   <li>Use <code>/attraction categories</code> to get a list of valid categories available for attractions.</li>
 * </ul>
 *
 * <p>
 * If any validation fails, appropriate error messages will be provided to the player.
 * </p>
 */
@CommandMeta(description = "Create a new attraction")
public class CreateCommand extends CoreCommand {

    /**
     * Creates a new instance of the {@code CreateCommand} class.
     * <p>
     * This command is used to handle the creation of new entities within the
     * application. It extends the functionality of the core command framework
     * and initializes the command with the keyword "create".
     * </p>
     *
     * <p>
     * The {@code CreateCommand} class may be invoked in situations where a new
     * item, entity, or object needs to be registered or created.
     * </p>
     *
     * <ul>
     *     <li>Inherits core command functionality.</li>
     *     <li>Associated with the keyword <code>"create"</code>.</li>
     *     <li>Designed to integrate into the command infrastructure for processing
     *     user-issued "create" operations.</li>
     * </ul>
     */
    public CreateCommand() {
        super("create");
    }

    /**
     * Handles the creation of a new attraction for a specified park.
     * <p>
     * The command allows players to create an attraction by specifying its unique ID,
     * corresponding warp point, applicable categories, and name. Additionally, the player
     * must hold an item in-hand that represents the attraction in the menu.
     * <p>
     * If the specified conditions are not met or the command is misused, appropriate error
     * messages will be sent to the player.
     *
     * @param player The player executing the command.
     * @param args   Command arguments provided by the player. Expected structure:
     *               <ul>
     *                  <li><code>[id]</code> - Unique identifier for the attraction.</li>
     *                  <li><code>[warp]</code> - Warp location associated with the attraction.</li>
     *                  <li><code>[category1,category2,...]</code> - Comma-separated list of categories for the attraction.</li>
     *                  <li><code>[name]</code> - Display name of the attraction.</li>
     *               </ul>
     *
     * @throws CommandException If there is an error during command execution or processing.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "/attraction create [id] [warp] [category1,category2] [name]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Also, hold the item for the attraction in your hand!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "The name of the item will be changed to the name of the attraction.");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "For a list of categories, run /attraction categories");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        if (ParkManager.getAttractionManager().getAttraction(args[0], park.getId()) != null) {
            player.sendMessage(ChatColor.RED + "An attraction already exists with the id " + args[0] + "!");
            return;
        }
        ItemStack item = player.getItemInMainHand().clone();
        if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Hold the item in your hand that will represent the attraction in the menu!");
            return;
        }
        StringBuilder name = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            name.append(args[i]).append(" ");
        }
        String displayName = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', name.toString().trim());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);

        List<AttractionCategory> categories = new ArrayList<>();
        for (String s : args[2].split(",")) {
            AttractionCategory category = AttractionCategory.fromString(s);
            if (category == null) {
                player.sendMessage(ChatColor.RED + "Unknown category '" + s + "'!");
                continue;
            }
            categories.add(category);
        }

        ParkManager.getAttractionManager().addAttraction(new Attraction(args[0], park.getId(), displayName, args[1], "",
                categories, true, item, null));
        player.sendMessage(ChatColor.GREEN + "Created new attraction " + displayName + ChatColor.GREEN + " at /warp " + args[1] + "!");
    }
}
