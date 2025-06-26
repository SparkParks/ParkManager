package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.shop.Shop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Represents a command to create a new shop within a park.
 *
 * <p> This command allows players to create a new shop by providing an ID, warp location, and a name.
 * The player must hold the item they want to represent the shop in their hand, and the item's display
 * name will be modified to reflect the shop name. The shop will only be created if the player is
 * currently located within a park's boundaries and the shop ID is unique within that park.
 *
 * <p> Command Syntax:
 * <ul>
 *   <li><code>/shop create [id] [warp] [name]</code></li>
 * </ul>
 *
 * <p> Additional Information:
 * <ul>
 *   <li>The item held in the player's hand will be used to represent the shop in the menu.</li>
 *   <li>The shop name can use color codes specified with the <code>&</code> character.</li>
 *   <li>The command will not proceed if the player is not inside a park or if an existing shop with
 *       the specified ID already exists in the same park.</li>
 * </ul>
 *
 * <p> Usage Constraints:
 * <ul>
 *   <li>The player holding an empty hand or no item will result in the command being rejected.</li>
 *   <li>If the player is outside a park or provides insufficient arguments, they will receive error
 *       messages.</li>
 * </ul>
 *
 * <p> Exceptions:
 * <ul>
 *   <li><code>CommandException</code>: Thrown in cases where command execution fails for an unforeseen error.</li>
 * </ul>
 */
@CommandMeta(description = "Create a new shop")
public class CreateCommand extends CoreCommand {

    /**
     * Constructs the {@code CreateCommand} instance and initializes it with the "create" identifier.
     *
     * <p>
     * The {@code CreateCommand} serves as a subcommand under {@code ShopCommand}, designed to facilitate
     * the creation of new shops. This command expects specific arguments to define the shop's characteristics, such as
     * its unique ID, warp location, and shop name.
     * </p>
     *
     * <p>
     * This constructor sets "create" as the command's identifier, enabling it to be invoked as a subcommand.
     * It operates within the context of the park management system, working alongside other subcommands to provide
     * a modular and extensible framework for managing shops and their related functionalities.
     * </p>
     *
     * <ul>
     *   <li><b>Context of Usage:</b> Invoked as part of the {@code ShopCommand} hierarchy to allow administrators
     *       to create shops in the player's current park.</li>
     *   <li><b>Dependency:</b> Relies on the broader command system to handle parsing, execution, and error
     *       handling during the command execution flow.</li>
     *   <li><b>Related Commands:</b> {@code ListCommand}, {@code RemoveCommand}, and {@code ReloadCommand}, which
     *       complement this subcommand for shop management purposes.</li>
     * </ul>
     */
    public CreateCommand() {
        super("create");
    }

    /**
     * Handles the "create" shop command issued by a player. This method allows players to create a new shop
     * within a park they are currently located in. Shops must have a unique ID, a warp point, and a display
     * name. The item held in the player's hand will represent the shop in the menu.
     *
     * <p><strong>Preconditions:</strong>
     * <lu>
     * <li>The player must provide at least 3 arguments: shop ID, warp name, and shop name.</li>
     * <li>The player must be inside a park to execute the command.</li>
     * <li>The player must hold an item in their hand to represent the shop.</li>
     * </lu>
     *
     * <p><strong>Steps Performed by the Method:</strong>
     * <lu>
     * <li>Checks for valid number of arguments and sends appropriate error messages if insufficient.</li>
     * <li>Ensures the player is inside a park and that the shop ID is unique within the park.</li>
     * <li>Validates the item held in the player's main hand.</li>
     * <li>Assigns a display name to the shop, applying color codes if necessary.</li>
     * <li>Registers the new shop with the park's shop manager.</li>
     * <li>Sends confirmation of the shop's creation to the player.</li>
     * </lu>
     *
     * @param player The {@code CPlayer} who issued the command.
     * @param args An array of {@code String} arguments provided with the command:
     * <lu>
     * <li>args[0] - The shop's unique ID.</li>
     * <li>args[1] - The warp point associated with the shop.</li>
     * <li>args[2..] - The display name for the shop.</li>
     * </lu>
     *
     * @throws CommandException Thrown if an error occurs while processing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "/shop create [id] [warp] [name]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Also, hold the item for the shop in your hand!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "The name of the item will be changed to the name of the shop.");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        if (ParkManager.getShopManager().getShopById(args[0], park.getId()) != null) {
            player.sendMessage(ChatColor.RED + "A shop already exists with the id " + args[0] + "!");
            return;
        }
        ItemStack item = player.getItemInMainHand().clone();
        if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Hold the item in your hand that will represent the shop in the menu!");
            return;
        }
        StringBuilder name = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            name.append(args[i]).append(" ");
        }
        String displayName = ChatColor.translateAlternateColorCodes('&', name.toString().trim());
        if (!displayName.startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
            displayName = ChatColor.AQUA + displayName;
        }
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        ParkManager.getShopManager().addShop(new Shop(args[0], park.getId(), displayName, args[1], item, new ArrayList<>(), new ArrayList<>()));
        player.sendMessage(ChatColor.GREEN + "Created new shop " + displayName + ChatColor.GREEN + " at /warp " + args[1] + "!");
    }
}
