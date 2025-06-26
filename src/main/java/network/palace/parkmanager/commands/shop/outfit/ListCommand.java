package network.palace.parkmanager.commands.shop.outfit;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopOutfit;
import org.bukkit.ChatColor;

/**
 * <p>
 * Represents a command to list all the outfits available in a specified shop within the player's current park.
 * This command is intended to be executed by a player, providing insights into the shop's available outfits,
 * along with their associated costs and currency types.
 * </p>
 *
 * <p>
 * Usage requires a shop ID as an argument and the player must be located within a park. If successful, the outfits
 * and corresponding details are displayed to the player.
 * </p>
 *
 * <ul>
 * <li>If insufficient arguments are provided, the player will be prompted with the correct command usage format.</li>
 * <li>If the player is not in a park, an appropriate error message will be displayed.</li>
 * <li>If the shop ID does not correspond to an existing shop in the player's current park, an error message will inform the player.</li>
 * </ul>
 *
 * <p>Functionality includes:</p>
 * <ul>
 * <li>Validating the player's current location within a park.</li>
 * <li>Retrieving a shop by its ID within the park.</li>
 * <li>Iterating through the shop's outfits and displaying their details to the player, including:
 *     <ul>
 *         <li>Outfit ID</li>
 *         <li>Outfit name</li>
 *         <li>Cost of the outfit</li>
 *         <li>Associated currency type</li>
 *     </ul>
 * </li>
 * </ul>
 *
 * <p>Behavior ensures comprehensive error handling such as missing arguments, invalid shop IDs, or the player being outside a park boundary.</p>
 */
@CommandMeta(description = "List shop outfits")
public class ListCommand extends CoreCommand {

    /**
     * <p>
     * Constructor for the {@code ListCommand} class, which initializes the command with the keyword "list".
     * </p>
     *
     * <p>
     * This command is designed to facilitate listing all outfits available in a specific shop within the player's
     * park. The command is registered under the "list" keyword and implemented to validate the player's location and
     * the shop's existence before processing further.
     * </p>
     *
     * <p>Functionality performed upon instantiation includes:</p>
     * <ul>
     * <li>Setting up a command keyword, "list", for execution within the system.</li>
     * <li>Prepare the structure to handle the listing functionality for shop outfits.</li>
     * </ul>
     *
     * <p>
     * This command works as part of the command framework to streamline interaction with in-game shops, enhancing
     * the player's user experience.
     * </p>
     */
    public ListCommand() {
        super("list");
    }

    /**
     * <p>
     * Processes a command to list all outfits available in a specific shop within the player's current park.
     * The command validates the player's location, retrieves the specified shop, and displays details
     * of each outfit available in that shop.
     * </p>
     *
     * <ul>
     * <li>If the arguments provided are insufficient, the player is prompted with the correct usage format.</li>
     * <li>Displays an error message if the player is not within a park.</li>
     * <li>Displays an error message if the specified shop ID does not correspond to an existing shop within the park.</li>
     * <li>Outputs a list of outfits with their details, including ID, name, cost, and currency type, for a valid shop.</li>
     * </ul>
     *
     * @param player The player executing the command.
     * @param args   The arguments provided to the command. The first argument is expected to contain the shop ID.
     * @throws CommandException If any error occurs during the command's execution that prevents normal processing.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/shop outfit list [shop id]");
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

        player.sendMessage(ChatColor.GREEN + "Shop Outfits:");
        for (ShopOutfit shopOutfit : shop.getOutfits()) {
            Outfit outfit = ParkManager.getWardrobeManager().getOutfit(shopOutfit.getOutfitId());
            if (outfit == null) continue;
            player.sendMessage(ChatColor.AQUA + "- [" + shopOutfit.getId() + "] " + ChatColor.YELLOW + outfit.getName() + " " + shopOutfit.getCurrencyType().getIcon() + shopOutfit.getCost());
        }
    }
}
