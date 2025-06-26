package network.palace.parkmanager.commands.shop.item;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * <p>
 * The {@code AddCommand} class represents a command for adding a new item to a shop within a park.
 * This command is designed to be executed by players in the game and requires specific parameters
 * to successfully add an item to a shop.
 * </p>
 *
 * <p>
 * This command performs the following key actions:
 * </p>
 * <ul>
 *     <li>Validates the required input arguments such as shop ID, cost, currency type, and display name.</li>
 *     <li>Ensures the player is located within a park before proceeding with the command.</li>
 *     <li>Checks if the provided shop ID corresponds to an existing shop within the park.</li>
 *     <li>Retrieves the item held in the player's main hand and validates its existence and type.</li>
 *     <li>Applies a custom display name to the item using the provided input.</li>
 *     <li>Adds the newly configured item to the targeted shop within the park.</li>
 *     <li>Saves the updated shop data to a persistent storage file.</li>
 * </ul>
 *
 * <p>
 * This command requires the following usage format:
 * </p>
 * <code>/shop item add [shop id] [cost] [balance/tokens] [display name]</code>
 *
 * <p>
 * The command execution involves several checks and error-handling flows to ensure proper
 * input validation, such as:
 * </p>
 * <ul>
 *     <li>Verifying if the player provided the minimum required arguments.</li>
 *     <li>Ensuring the provided cost argument is an integer.</li>
 *     <li>Checking if the player is present in a valid park region.</li>
 *     <li>Confirming the provided shop ID belongs to an existing shop.</li>
 *     <li>Validating that the item in the player's main hand is not null or air.</li>
 * </ul>
 *
 * <p>
 * When executed successfully, this command:
 * </p>
 * <ul>
 *     <li>Creates a new {@link ShopItem} using the provided arguments and the player's held item.</li>
 *     <li>Applies the custom display name using color codes if applicable.</li>
 *     <li>Adds the item to the specified shop in the park.</li>
 *     <li>Provides feedback messages to the player indicating success or failure.</li>
 * </ul>
 *
 * <p>
 * If the execution fails due to missing or invalid input, the player is informed via error messages
 * that offer guidance on how to format the command correctly.
 * </p>
 *
 * <p>
 * <b>Note:</b> This command requires the player to hold the item to be added in their main hand
 * at the time of execution.
 * </p>
 */
@CommandMeta(description = "Add a new shop item")
public class AddCommand extends CoreCommand {

    /**
     * Constructs a new {@code AddCommand} instance.
     *
     * <p>The {@code AddCommand} is a subcommand designed to handle the addition of new items
     * to a specific shop within the game. This command is a logical component of the
     * {@code ItemCommand}, which is responsible for managing operations on shop items.</p>
     *
     * <p>Upon invocation, this command facilitates the creation and registration of a new
     * item in the target shop, enabling administrators to dynamically expand shop inventories.</p>
     *
     * <p>This constructor initializes the command with the identifier {@code "add"} to
     * distinguish it among other subcommands within the {@code ItemCommand} context.</p>
     */
    public AddCommand() {
        super("add");
    }

    /**
     * Handles the command to add an item to a shop. The command requires the player to be inside a park and
     * adhere to the expected format. Validates inputs, retrieves the corresponding shop, and adds the
     * specified item with its details to the shop.
     *
     * <p>Command format: <code>/shop item add [shop id] [cost] [balance/tokens] [display name]</code>.</p>
     * <p>The item to be added must be held in the player's main hand.</p>
     *
     * @param player The player issuing the command.
     * @param args The command arguments:
     * <ul>
     *     <li><code>args[0]</code>: The shop ID indicating the target shop.</li>
     *     <li><code>args[1]</code>: The cost of the item, which must be a valid integer.</li>
     *     <li><code>args[2]</code>: The currency type for the cost (<i>balance</i> or <i>tokens</i>).</li>
     *     <li><code>args[3]</code> and beyond: The display name of the item, formatted as a single string.</li>
     * </ul>
     * @throws CommandException If an error occurs during command processing.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "/shop item add [shop id] [cost] [balance/tokens] [display name]");
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
        ItemStack item = player.getItemInMainHand().clone();
        if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Hold the shop item in your hand!");
            return;
        }
        Shop shop = ParkManager.getShopManager().getShopById(args[0], park.getId());
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + args[0] + "!");
            return;
        }

        int cost = Integer.parseInt(args[1]);
        StringBuilder displayName = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            displayName.append(args[i]);
            if (i < (args.length - 1)) {
                displayName.append(" ");
            }
        }
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName.toString()));
        item.setItemMeta(meta);

        shop.addItem(new ShopItem(shop.nextId(), item, cost, CurrencyType.fromString(args[2])));
        ParkManager.getShopManager().saveToFile();

        player.sendMessage(ChatColor.GREEN + "Added a new item to " + shop.getName() + ChatColor.GREEN + " named " + ChatColor.translateAlternateColorCodes('&', displayName.toString()) + "!");
    }
}
