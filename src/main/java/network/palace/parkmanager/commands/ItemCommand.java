package network.palace.parkmanager.commands;

import com.google.common.base.Joiner;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

/**
 * <p>The {@code ItemCommand} class represents a command that allows players to obtain specific items in the game.</p>
 *
 * <p>This command supports specifying the item type, the amount, and optional NBT data for customizing the item attributes.</p>
 *
 * <h3>Command Details:</h3>
 * <ul>
 *   <li><strong>Command Name:</strong> item</li>
 *   <li><strong>Description:</strong> Get an item</li>
 *   <li><strong>Rank:</strong> TRAINEEBUILD</li>
 *   <li><strong>Aliases:</strong> i</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * <ul>
 *   <li><strong>Command Format:</strong> {@code /i [item] <amount> <NBT Data>}</li>
 *   <li><strong>Example:</strong> {@code /i cooked_porkchop 1 {display:{Name:"\"Turkey Leg\""}}}</li>
 * </ul>
 *
 * <p>When invoked, this command does the following:</p>
 * <ul>
 *   <li>Parses the input arguments to determine the item, its amount, and optional NBT data.</li>
 *   <li>Checks if the specified item is valid, using a predefined list of alternative names if necessary.</li>
 *   <li>Validates and converts the specified amount. Defaults to the item's maximum stack size if not provided or invalid.</li>
 *   <li>Adds the specified item(s) to the player’s inventory.</li>
 *   <li>Displays a confirmation or error message to the player.</li>
 * </ul>
 *
 * <h3>Special Features:</h3>
 * <ul>
 *   <li>Allows alternative names for certain items through an internal map.</li>
 *   <li>Includes support for parsing raw NBT data for custom item metadata.</li>
 * </ul>
 *
 * <p><strong>Note:</strong> This command involves potentially unsafe operations when handling item metadata.</p>
 */
@CommandMeta(description = "Get an item", rank = Rank.TRAINEEBUILD, aliases = "i")
public class ItemCommand extends CoreCommand {
    /**
     * <p>
     * A static and final <code>HashMap</code> used to store mappings between item names (represented as strings)
     * and their corresponding <code>Material</code> types. This collection is primarily utilized for
     * alternative item lookups within the command handling process.
     * </p>
     *
     * <p>
     * The key in the map is a <code>String</code> representing the name or identifier of an item,
     * while the value is the respective <code>Material</code> associated with that name.
     * </p>
     *
     * <ul>
     * <li>The map is initialized as an empty <code>HashMap</code>.</li>
     * <li>It is declared <code>private</code> to restrict visibility to within the encapsulating class.</li>
     * <li>The map is <code>static</code> indicating a single shared instance among all objects of the class.</li>
     * <li>The map is <code>final</code> ensuring its reference cannot be reassigned.</li>
     * </ul>
     */
    private static final HashMap<String, Material> alternatives = new HashMap<>();

    static {
        alternatives.put("CMD", Material.COMMAND);
        alternatives.put("ENDERCHEST", Material.ENDER_CHEST);
        alternatives.put("ARMORSTAND", Material.ARMOR_STAND);
        alternatives.put("FIREWORK", Material.FIREWORK);
    }

    /**
     * Constructs a new instance of the <code>ItemCommand</code> class.
     * <p>
     * This constructor initializes the command with a predefined name,
     * <code>"item"</code>. It is typically used as part of a framework
     * or command system to handle item-related functionality.
     */
    public ItemCommand() {
        super("item");
    }

    /**
     * Handles the processing of a command related to giving an item to a player.
     * <p>
     * This command allows a player to receive an item in their inventory. The command
     * can optionally include the quantity and NBT data of the item. If invalid
     * arguments are provided, appropriate error messages will be sent to the player.
     * </p>
     *
     * @param player the {@code CPlayer} instance representing the player executing the command.
     * @param args an array of {@code String} arguments provided with the command:
     *             <ul>
     *                 <li>args[0]: A {@code String} representing the material/item name.</li>
     *                 <li>args[1] (optional): An {@code int} representing the quantity of the item. If not provided or invalid, defaults to the item's maximum stack size.</li>
     *                 <li>args[2] (optional): A {@code String} containing NBT data for the item in JSON format.</li>
     *             </ul>
     * @throws CommandException if there is an error in processing the command.
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/i [item] <amount> <NBT Data>");
            player.sendMessage(ChatColor.RED + "Ex: /i cooked_porkchop 1 {display:{Name:\"\\\"Turkey Leg\\\"\"}}");
            return;
        }
        String itemString = args[0];
        Material type = getMaterial(itemString);
        if (type == null) {
            player.sendMessage(ChatColor.RED + "Unknown item '" + itemString + "'!");
            return;
        }
        int amount;
        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                amount = type.getMaxStackSize();
            }
        } else {
            amount = type.getMaxStackSize();
        }
        if (amount < 1) amount = 1;
        ItemStack item = new ItemStack(type, amount);
        if (args.length > 2 && args[2].startsWith("{")) {
            try {
                ParkManager.getInstance().getServer().getUnsafe().modifyItemStack(item, Joiner.on(' ').join(Arrays.asList(args).subList(2, args.length)));
            } catch (NullPointerException npe) {
                player.sendMessage(ChatColor.RED + "The provided item meta is invalid: '" + args[2] + "'");
                return;
            }
        }
        player.getInventory().addItem(item);
        player.sendMessage(ChatColor.GRAY + "Given " + amount + " " + type.name().toLowerCase());
    }

    /**
     * Retrieves a {@code Material} object based on the provided string.
     * <p>
     * The method first checks if a case-insensitive alternative mapping of the string exists
     * in the {@code alternatives} map. If found, it returns the corresponding {@code Material}
     * from the map. Otherwise, it attempts to match the string with a material using
     * {@code Material.matchMaterial}.
     *
     * @param s The name of the material as a string. It can be in any case and may have
     *          a corresponding alternative in the {@code alternatives} map.
     * @return The corresponding {@code Material} object if a match or alternative is found,
     *         otherwise {@code null}.
     */
    private Material getMaterial(String s) {
        if (alternatives.containsKey(s.toUpperCase())) {
            return alternatives.get(s.toUpperCase());
        } else {
            return Material.matchMaterial(s);
        }
    }
}
