package network.palace.parkmanager.commands.shop.outfit;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopOutfit;
import org.bukkit.ChatColor;

/**
 * Represents a command to remove a shop outfit from a specific shop in a park.
 *
 * <p>This command allows players to remove an existing shop outfit by providing the
 * shop ID and the shop outfit ID. It performs various validations such as:
 * <lu>
 *   <li>Ensuring the player is inside a park.</li>
 *   <li>Validating the shop ID exists within the park.</li>
 *   <li>Validating the shop outfit ID is an integer and exists in the specified shop.</li>
 * </lu>
 *
 * <p>If the operation is successful, the specified outfit is removed from the shop,
 * the changes are saved to file, and a success message is sent to the player.
 * The name of the removed outfit is included in the success message, if available.
 *
 * <h3>Command Syntax:</h3>
 * <ul>
 *   <li><code>/shop outfit remove [shop id] [shop outfit id]</code></li>
 * </ul>
 *
 * <p>Informational messages and errors are communicated to the player via in-game chat.
 *
 * <h3>Validation Steps:</h3>
 * <ul>
 *   <li>Checks if the player has provided the required arguments (shop ID and shop outfit ID).</li>
 *   <li>Ensures the player is inside a park when executing the command.</li>
 *   <li>Verifies that the shop ID exists within the current park.</li>
 *   <li>Ensures the shop outfit ID is a valid integer and corresponds to an existing outfit for the specified shop.</li>
 * </ul>
 *
 * <h3>Relevant Conditions:</h3>
 * <ul>
 *   <li>If insufficient arguments are provided, an error message is displayed with the correct command usage.</li>
 *   <li>If the player is not inside a park, the command cannot proceed, and an error is shown.</li>
 *   <li>If the specified shop does not exist within the park, an appropriate error message is displayed.</li>
 *   <li>If the specified shop outfit ID is invalid or does not exist, the operation is terminated with a corresponding error message.</li>
 * </ul>
 *
 * <p>This command is targeted at players who have permission to modify shop configurations
 * within their respective parks. Once the outfit is removed, the in-game changes are saved
 * for future reference.
 */
@CommandMeta(description = "Remove a shop outfit")
public class RemoveCommand extends CoreCommand {

    /**
     * Initializes a new instance of the <code>RemoveCommand</code> class.
     *
     * <p>This constructor sets up the "remove" subcommand to handle the removal of outfits
     * from shops within the park system. It is part of the subcommand structure aimed at
     * managing shop outfits efficiently.</p>
     *
     * <p>The "remove" subcommand is designed to complement other commands like "add," allowing
     * for seamless management of outfits associated with shops. By structuring commands in this
     * way, modularity and clarity are promoted in the command system.</p>
     *
     * <p>Subcommands such as <code>RemoveCommand</code> and others within this hierarchy contribute
     * to a well-organized framework for managing shop-related configurations in the park system.</p>
     */
    public RemoveCommand() {
        super("remove");
    }

    /**
     * Processes the "remove shop outfit" command in the context of the current player.
     * This involves validating input arguments, ensuring the player is within a park,
     * verifying shop and outfit existence, and removing a specified outfit from a shop.
     *
     * <p>The method expects at least two arguments: the shop ID and shop outfit ID.
     * It includes necessary checks to ensure the presence and validity of these arguments
     * and informs the player of any issues encountered during execution.</p>
     *
     * @param player The player executing the command. This parameter represents a {@code CPlayer} object
     *               and provides context related to the player issuing this command.
     * @param args   A string array of arguments passed along with the command.
     *               <ul>
     *                 <li>args[0] - The shop ID where the shop outfit is located.</li>
     *                 <li>args[1] - The ID of the shop outfit to be removed.</li>
     *               </ul>
     * @throws CommandException If a general issue occurs during command processing.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/shop outfit remove [shop id] [shop outfit id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the outfit id from /shop outfit list [shop id]!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[1])) {
            player.sendMessage(ChatColor.RED + args[1] + " is not an integer!");
            return;
        }
        Shop shop = ParkManager.getShopManager().getShopById(args[0], park.getId());
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + args[0] + "!");
            return;
        }

        int shopOutfitId = Integer.parseInt(args[1]);
        ShopOutfit shopOutfit = shop.getOutfit(shopOutfitId);
        if (shopOutfit == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop outfit by id " + shopOutfitId + "!");
            return;
        }

        shop.removeOutfit(shopOutfitId);
        ParkManager.getShopManager().saveToFile();
        Outfit outfit = ParkManager.getWardrobeManager().getOutfit(shopOutfit.getOutfitId());
        if (outfit == null) {
            player.sendMessage(ChatColor.GREEN + "Removed the outfit " + shopOutfitId + ChatColor.GREEN + " from " + shop.getName() + "!");
        } else {
            player.sendMessage(ChatColor.GREEN + "Removed the outfit " + outfit.getName() + ChatColor.GREEN + " from " + shop.getName() + "!");
        }
    }
}
