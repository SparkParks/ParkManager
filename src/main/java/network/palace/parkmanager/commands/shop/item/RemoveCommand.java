package network.palace.parkmanager.commands.shop.item;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopItem;
import org.bukkit.ChatColor;

/**
 * The {@code RemoveCommand} class handles the removal of an item from a specified shop
 * in the game. This command requires the player to provide valid shop and item identifiers
 * and verifies that the player is within the park where the shop exists.
 *
 * <p><b>Command syntax:</b></p>
 * <ul>
 *   <li>{@code /shop item remove [shop id] [shop item id]}</li>
 * </ul>
 * <p><b>Command prerequisites:</b></p>
 * <ul>
 *   <li>The player must be located within a park when executing this command.</li>
 *   <li>The shop must exist and match the provided shop identifier.</li>
 *   <li>The shop item must exist and match the provided item identifier.</li>
 * </ul>
 *
 * <p><b>Behavior:</b></p>
 * <ul>
 *   <li>If the arguments are insufficient or invalid, appropriate error messages are sent to the player.</li>
 *   <li>If the shop or shop item cannot be found, the player is notified with an error message.</li>
 *   <li>Upon successful removal of the shop item, the change is saved, and the player receives a confirmation message.</li>
 * </ul>
 *
 * <p><b>Notes:</b></p>
 * <ul>
 *   <li>The item to be removed must be identified using its unique shop item identifier,
 *       which can be retrieved using {@code /shop item list [shop id]}.</li>
 *   <li>Any changes to the shop are saved persistently after successful execution of this command.</li>
 * </ul>
 */
@CommandMeta(description = "Remove a shop item")
public class RemoveCommand extends CoreCommand {

    /**
     * <p>
     * The {@code RemoveCommand} class represents a command for removing an item from a shop within a park.
     * This command is designed to be executed by players in the game to specifically remove items
     * from shop inventories.
     * </p>
     *
     * <p>
     * This command performs the following key actions:
     * </p>
     * <ul>
     *     <li>Validates the required input arguments necessary to identify the item and shop.</li>
     *     <li>Ensures the player is within a valid park before proceeding with the removal process.</li>
     *     <li>Locates the specified shop from which the item needs to be removed.</li>
     *     <li>Validates that the identified item exists within the targeted shop.</li>
     *     <li>Removes the targeted item from the shop inventory.</li>
     *     <li>Saves the updated shop data to a persistent storage file.</li>
     * </ul>
     *
     * <p>
     * This command is critical for managing and maintaining the integrity of shop inventories by providing
     * park administrators the ability to revoke outdated or misplaced items. Command execution ensures
     * proper input validation and informs the player of success or failure through feedback messages.
     * </p>
     *
     * <p>
     * <b>Note:</b> The {@code RemoveCommand} must be invoked with proper parameters identifying
     * the shop and the target item to be removed. If the prerequisites are not met, the command
     * alerts the player with an error message and terminates further execution.
     * </p>
     *
     * <p>
     * This command inherits its functionalities and registration logic from the {@link CoreCommand}
     * superclass, and it is a core component of the {@code ItemCommand} system.
     * </p>
     *
     * <p>
     * Upon construction, {@code RemoveCommand} initializes itself with the identifier {@code "remove"},
     * distinguishing it from other subcommands within the {@code ItemCommand} framework.
     * </p>
     */
    public RemoveCommand() {
        super("remove");
    }

    /**
     * Handles the "remove" command for shop items. This command removes a specified item
     * from a shop within a park that the player is currently in. The shop and item IDs should
     * be provided as arguments.
     *
     * <p>The command ensures the following:</p>
     * <ul>
     *   <li>Validates the number of arguments provided.</li>
     *   <li>Checks that the player is located inside a park.</li>
     *   <li>Verifies the shop ID and shop item ID exist and are valid integers.</li>
     *   <li>Performs the removal of the item from the shop if validation is successful.</li>
     *   <li>Saves the updated shop configuration to a file after item removal.</li>
     *   <li>Sends the appropriate feedback/messages to the player based on the outcome.</li>
     * </ul>
     *
     * <p>The command will trigger error messages for different scenarios such as invalid
     * input, missing shops, or missing shop items.</p>
     *
     * @param player The {@link CPlayer} executing the command. The location of the player
     *               is used to verify that they are inside a park.
     * @param args   The arguments provided for the command. Should include:
     *               <ul>
     *                 <li>args[0]: The shop ID.</li>
     *                 <li>args[1]: The shop item ID.</li>
     *               </ul>
     *               Both IDs are mandatory for successful execution.
     * @throws CommandException If an internal error occurs during command processing.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/shop item remove [shop id] [shop item id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the shop item id from /shop item list [shop id]!");
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

        int itemId = Integer.parseInt(args[1]);
        ShopItem item = shop.getItem(itemId);
        if (item == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop item by id " + itemId + "!");
            return;
        }

        shop.removeItem(itemId);
        ParkManager.getShopManager().saveToFile();

        player.sendMessage(ChatColor.GREEN + "Removed the item " + item.getItem().getItemMeta().getDisplayName()
                + ChatColor.GREEN + " (" + item.getItem().getType().name().toLowerCase() + ") from " + shop.getName() + "!");
    }
}
