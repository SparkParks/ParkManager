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
 * <p>The <code>AddCommand</code> class is responsible for adding a new outfit to a shop in the park system.</p>
 *
 * <p>This command is designed to be used by players within the game to associate an outfit with a specific shop,
 * requiring the shop ID, outfit ID, and the token cost of the outfit as input parameters.</p>
 *
 * <p>Command Syntax:</p>
 * <ul>
 *   <li><code>/shop outfit add [shop id] [outfit id] [cost in tokens]</code></li>
 * </ul>
 *
 * <p><strong>Expected Behavior:</strong></p>
 * <ul>
 *   <li>Validates the player is in a park while executing the command.</li>
 *   <li>Checks that input parameters are integers where necessary.</li>
 *   <li>Ensures the provided <code>shop id</code> and <code>outfit id</code> correspond to valid entities.</li>
 *   <li>Confirms the <code>cost</code> value is non-negative.</li>
 *   <li>If all validations pass, the outfit is added to the specified shop, and the shop data is saved.</li>
 *   <li>Feedback is provided to the player for success or validation errors.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *   <li>Throws a <code>CommandException</code> if an unexpected error occurs during execution.</li>
 * </ul>
 *
 * <p><strong>Parameters:</strong></p>
 * <ul>
 *   <li><code>player</code>: The player who executes the command.</li>
 *   <li><code>args</code>: An array containing command arguments in the following order:
 *       <ul>
 *           <li><code>args[0]</code>: The ID of the shop.</li>
 *           <li><code>args[1]</code>: The ID of the outfit to add.</li>
 *           <li><code>args[2]</code>: The cost of the outfit in tokens.</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p><strong>Requirements:</strong></p>
 * <ul>
 *   <li>The player must be located within a park.</li>
 *   <li>The specified shop and outfit must exist within the game system.</li>
 *   <li>Input must adhere to the expected format to avoid validation errors.</li>
 * </ul>
 */
@CommandMeta(description = "Add a new shop outfit")
public class AddCommand extends CoreCommand {

    /**
     * Initializes a new instance of the <code>AddCommand</code> class.
     *
     * <p>This constructor sets up the "add" subcommand for managing shop outfits. It is
     * part of the subcommand structure under the parent <code>OutfitCommand</code>, which
     * handles shop outfit management.</p>
     *
     * <p>The "add" subcommand is designed to provide functionality for adding new outfits
     * to a shop, ensuring modular command management and separation of concerns.</p>
     *
     * <p>Subcommands like <code>AddCommand</code>, <code>ListCommand</code>, and
     * <code>RemoveCommand</code> are utilized to streamline the operations within the
     * <code>OutfitCommand</code> hierarchy, promoting organized and clear command handling.</p>
     */
    public AddCommand() {
        super("add");
    }

    /**
     * Handles the "add" subcommand for adding an outfit to a shop within a park.
     * Validates input arguments and modifies the shop configuration accordingly.
     *
     * <p>If the command or arguments provided are invalid, an appropriate error
     * message will be sent to the player.</p>
     *
     * @param player The {@link CPlayer} instance representing the player issuing the command.
     *               This is used for sending feedback messages and determining location.
     * @param args An array of {@link String} arguments provided with the command by the player.
     *             Expected format:
     *             <lu>
     *               <li>{@code args[0]}: Shop ID</li>
     *               <li>{@code args[1]}: Outfit ID (integer)</li>
     *               <li>{@code args[2]}: Outfit cost in tokens (integer, non-negative)</li>
     *             </lu>
     *             The length of this array must be 3 or more for the command to proceed.
     * @throws CommandException If a critical error occurs while executing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "/shop outfit add [shop id] [outfit id] [cost in tokens]");
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
        if (!MiscUtil.checkIfInt(args[2])) {
            player.sendMessage(ChatColor.RED + args[2] + " is not an integer!");
            return;
        }
        Shop shop = ParkManager.getShopManager().getShopById(args[0], park.getId());
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + args[0] + "!");
            return;
        }

        int outfitId = Integer.parseInt(args[1]);
        Outfit outfit = ParkManager.getWardrobeManager().getOutfit(outfitId);
        if (outfit == null) {
            player.sendMessage(ChatColor.RED + "Couldn't find an outfit with id " + outfitId + "!");
            return;
        }

        int cost = Integer.parseInt(args[2]);
        if (cost < 0) {
            player.sendMessage(ChatColor.RED + "Cost cannot be negative!");
            return;
        }

        shop.addOutfit(new ShopOutfit(shop.nextId(), outfitId, cost));
        ParkManager.getShopManager().saveToFile();

        player.sendMessage(ChatColor.GREEN + "Added the " + outfit.getName() + ChatColor.GREEN + " outfit to " + shop.getName() + "!");
    }
}
