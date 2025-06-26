package network.palace.parkmanager.commands.outfits;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * <p>
 * Represents a command for creating a new outfit in-game. The {@code CreateCommand}
 * allows a player to save their current worn armor (helmet, chestplate, leggings,
 * and boots) as a named outfit, which is stored for future use.
 * </p>
 *
 * <h3>Command Behavior</h3>
 * <p>
 * When executed, this command performs the following:
 * </p>
 * <ul>
 *     <li>Checks if the player has provided a name for the outfit. If not, an error message
 *         is shown with usage instructions.</li>
 *     <li>Ensures that the player is wearing all required armor pieces: helmet, chestplate,
 *         leggings, and boots. If any slot is missing an item, it notifies the player.</li>
 *     <li>Generates display names for each armor piece by incorporating the outfit name.</li>
 *     <li>Saves the outfit data asynchronously to the database in JSON format.</li>
 *     <li>Informs the player upon successful saving of the outfit and prompts them to reload
 *         outfits on the server to view it.</li>
 * </ul>
 *
 * <h3>Command Requirements</h3>
 * <ul>
 *     <li>Players must be wearing an item in all armor slots (helmet, chestplate, leggings, and boots).</li>
 *     <li>A valid outfit name must be provided as an argument.</li>
 * </ul>
 *
 * <h3>Internal Logic</h3>
 * <ul>
 *     <li>The command concatenates all arguments into a single string to form the outfit name.</li>
 *     <li>Item metadata, such as display names, is customized for each armor piece using the outfit name.</li>
 *     <li>Outfit JSON data is created using an {@code ItemUtil} helper class and stored
 *         asynchronously in a database via the {@code MongoHandler}.</li>
 * </ul>
 *
 * <h3>Player Notifications</h3>
 * <ul>
 *     <li>Usage instructions are displayed if the player provides insufficient arguments.</li>
 *     <li>Specific warnings are displayed if any armor slot is empty.</li>
 *     <li>Success messages are displayed once the outfit is stored in the database.</li>
 * </ul>
 */
@CommandMeta(description = "Create a new outfit")
public class CreateCommand extends CoreCommand {

    /**
     * Constructs a new <code>CreateCommand</code> instance.
     * <p>
     * This constructor initializes the <code>CreateCommand</code> with the identifier <code>"create"</code>,
     * enabling users to create new outfits within the application. It serves as part of the subcommand
     * system under the parent <code>OutfitCommand</code>.
     * </p>
     *
     * <p>
     * The <code>CreateCommand</code> is one of the subcommands registered in the <code>OutfitCommand</code> and
     * contributes to modular outfit management by handling the creation process.
     * </p>
     */
    public CreateCommand() {
        super("create");
    }

    /**
     * Handles the `/outfit create [name]` command by creating a new outfit based on the items currently worn by the player.
     * The outfit is created and stored in the database, with custom display names assigned to each piece of the outfit.
     * <p>
     * The command requires the player to wear items in all armor slots (helmet, chestplate, leggings, and boots).
     * If any slot is empty, the command notifies the player to ensure all slots are filled before proceeding.
     * The outfit is then asynchronously saved in the database, and the player is notified upon success.
     *
     * @param player The {@link CPlayer} executing the command. Represents the player who initiates the outfit creation process.
     * @param args   The arguments passed with the command. Expected to include the name of the outfit. If no arguments are provided,
     *               the player will be reminded of the correct command syntax and prerequisites.
     *
     * @throws CommandException Thrown when an error occurs in the command execution process.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/outfit create [name]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Also, put on the outfit items before running this command!");
            return;
        }

        StringBuilder name = new StringBuilder();
        for (String arg : args) {
            name.append(arg).append(" ");
        }
        String displayName = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', name.toString().trim());

        PlayerInventory inv = player.getInventory();

        if (inv.getHelmet() == null) {
            player.sendMessage(ChatColor.RED + "You're not wearing anything in your helmet slot!");
            return;
        }
        if (inv.getChestplate() == null) {
            player.sendMessage(ChatColor.RED + "You're not wearing anything in your chestplate slot!");
            return;
        }
        if (inv.getLeggings() == null) {
            player.sendMessage(ChatColor.RED + "You're not wearing anything in your leggings slot!");
            return;
        }
        if (inv.getBoots() == null) {
            player.sendMessage(ChatColor.RED + "You're not wearing anything in your boots slot!");
            return;
        }

        ItemStack head = inv.getHelmet().clone();
        ItemStack shirt = inv.getChestplate().clone();
        ItemStack pants = inv.getLeggings().clone();
        ItemStack boots = inv.getBoots().clone();

        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(displayName + " Head");
        head.setItemMeta(meta);

        meta = shirt.getItemMeta();
        meta.setDisplayName(displayName + " Shirt");
        shirt.setItemMeta(meta);

        meta = pants.getItemMeta();
        meta.setDisplayName(displayName + " Pants");
        pants.setItemMeta(meta);

        meta = boots.getItemMeta();
        meta.setDisplayName(displayName + " Boots");
        boots.setItemMeta(meta);

        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            Core.getMongoHandler().createOutfitNew(name.toString().trim(),
                    ItemUtil.getJsonFromItemNew(head).toString(),
                    ItemUtil.getJsonFromItemNew(shirt).toString(),
                    ItemUtil.getJsonFromItemNew(pants).toString(),
                    ItemUtil.getJsonFromItemNew(boots).toString(),
                    ParkManager.getResort().getId());
            player.sendMessage(ChatColor.GREEN + "Created new outfit in the database! Reload with '/outfit reload' to view it on this server.");
        });
    }
}
