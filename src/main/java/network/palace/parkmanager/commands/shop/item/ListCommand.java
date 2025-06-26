package network.palace.parkmanager.commands.shop.item;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * The {@code ListCommand} class is responsible for listing all items available in a specified shop.
 * It extends the {@code CoreCommand} and is utilized within the context of a park.
 *
 * <p>This command retrieves shop details, validates the location of the player
 * (to ensure they are within a park), and displays a categorized list of items
 * available for purchase, along with their details.
 *
 * <p>Command Usage: <code>/shop item list [shop id]</code>
 * <ul>
 *   <li>If no arguments are provided, an error message is displayed, along with the correct usage.</li>
 *   <li>The player must be inside a park to execute this command. Attempting to run the command outside
 *       a park will result in an error message.</li>
 *   <li>If a shop with the specified ID does not exist within the park, an error message
 *       is displayed.</li>
 * </ul>
 *
 * <p>On successful execution:
 * <ul>
 *   <li>The command lists the items available in the specified shop, along with:
 *       <ul>
 *          <li>Item ID</li>
 *          <li>Item Display Name</li>
 *          <li>Item Type</li>
 *          <li>Cost</li>
 *          <li>Currency Type</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p>Each item is listed in a readable format using color-coded messages for better clarity.
 */
@CommandMeta(description = "List shop items")
public class ListCommand extends CoreCommand {

    /**
     * Default constructor for the {@code ListCommand} class.
     *
     * <p>Initializes the command with the identifier "list" to associate it with
     * the functionality of listing items available in a shop. This constructor
     * delegates to the parent {@code CoreCommand} class for further setup and
     * processing of the command structure and behavior.
     *
     * <p>The command is designed to:
     * <ul>
     *   <li>Validate preconditions required for command execution, such as the player
     *       being inside a park.</li>
     *   <li>Retrieve and display information from a specific shop based on user input.</li>
     *   <li>Format and present data about shop items, including details like
     *       cost, type, and display names, in a player-friendly format.</li>
     * </ul>
     */
    public ListCommand() {
        super("list");
    }

    /**
     * Processes the "/shop item list" command, validating the player's location
     * and arguments, retrieving the shop, and displaying a list of available
     * items in the shop with their details.
     *
     * <p>This method performs the following actions:
     * <ul>
     *   <li>Checks if the required command arguments are provided. If not, displays an error message.</li>
     *   <li>Ensures the player is located within a park. If the player is outside a park, an error is shown.</li>
     *   <li>Attempts to retrieve the shop by its ID and validates its existence. If no shop is found, displays
     *       an error message.</li>
     *   <li>If all validations are successful, lists all items in the shop, displaying the following for each item:
     *       <ul>
     *          <li>Item ID</li>
     *          <li>Item Display Name</li>
     *          <li>Item Type</li>
     *          <li>Cost</li>
     *          <li>Currency Type</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * <p>Color-coded messages are used to improve readability of command output.
     *
     * @param player the player executing the command. This is used to retrieve the player's location and to send
     *               feedback messages.
     * @param args   the command arguments. The first argument must specify the shop ID to retrieve items from.
     *               If no arguments are provided, or the shop ID is invalid, an error message is returned.
     * @throws CommandException if there is an error processing the command, such as missing or invalid data.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/shop item list [shop id]");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Shop shop = ParkManager.getShopManager().getShopById(args[0], park.getId());
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + args[0] + "!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Shop Items:");
        for (ShopItem shopItem : shop.getItems()) {
            ItemStack item = shopItem.getItem();
            player.sendMessage(ChatColor.AQUA + "- [" + shopItem.getId() + "] " + ChatColor.YELLOW + item.getItemMeta().getDisplayName() +
                    " (" + item.getType().name().toLowerCase() + ") " + shopItem.getCurrencyType().getIcon() + shopItem.getCost());
        }
    }
}
