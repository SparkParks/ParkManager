package network.palace.parkmanager.commands.food;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.food.FoodLocation;
import network.palace.parkmanager.handlers.Park;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The {@code CreateCommand} class is a command class used to create a new food location
 * within a park in the system. This command allows players to set up a food location
 * by providing relevant details such as an identifier, warp point, and display name.
 * The item held in the player's hand at the time of execution will be used as the
 * representative item for the food location in the menu.
 *
 * <p><b>Command Syntax:</b></p>
 * <pre>/food create [id] [warp] [name]</pre>
 *
 * <p>Key requirements and restrictions:</p>
 * <ul>
 *   <li>Players must be located inside a park at the time the command is executed. The park
 *       is determined by the player's current location.</li>
 *   <li>The provided {@code id} must be unique within the park. If a food location
 *       with the given {@code id} already exists, the creation will be rejected.</li>
 *   <li>The player must hold an item in their main hand that will represent the food location.
 *       The display name of the item will be updated to reflect the name of the food location.</li>
 *   <li>The command requires a minimum of three arguments ({@code id}, {@code warp}, and {@code name})
 *       to be provided. An error message will be displayed if fewer than three arguments are supplied.</li>
 * </ul>
 *
 * <p>Upon successful execution, the food location will be created and registered, associating the
 * defined identifier, warp point, and display name with the park. A confirmation message will
 * be displayed to the player upon completion.</p>
 *
 * <p><b>Usage notes:</b></p>
 * <ul>
 *   <li>The argument {@code id} is a unique identifier for the food location.</li>
 *   <li>The argument {@code warp} represents the warp point associated with the food location.</li>
 *   <li>The {@code name} argument specifies the display name for the food location, which can
 *       include multiple words separated by spaces.</li>
 *   <li>The item the player is holding will serve as the visual icon or representative object
 *       for the food location in menus. Ensure the item is not {@code null} and is not an empty slot.</li>
 * </ul>
 *
 * <p><b>Error Messages:</b></p>
 * <ul>
 *   <li>If executed outside of a park, the player will receive a message indicating they must be inside a park.</li>
 *   <li>An error message will display if an existing food location with the same {@code id} already exists.</li>
 *   <li>If the item in the player's main hand is invalid (e.g., empty hand or invalid item type),
 *       they will be instructed to hold an appropriate item.</li>
 *   <li>If fewer arguments are given than required, the player will receive usage instructions for the command.</li>
 * </ul>
 */
@CommandMeta(description = "Create a new food location")
public class CreateCommand extends CoreCommand {

    /**
     * Constructs a new {@code CreateCommand} instance, which represents a subcommand
     * for creating food locations within a park. This command is part of the broader
     * set of commands under the "food" management system.
     *
     * <p>The {@code CreateCommand} is designed to handle the user action of adding new
     * food locations in the current park context. It provides the functionality to integrate
     * new entries into the park's existing list of food locations.</p>
     *
     * <p><b>Key Details:</b></p>
     * <ul>
     *     <li>The command keyword is "create".</li>
     *     <li>This subcommand must be used within the context of a registered park.</li>
     *     <li>The {@code CreateCommand} integrates with the system to populate new food location data.</li>
     * </ul>
     *
     * <p>This class contributes to the modular command structure, where its functionality
     * is encapsulated as a part of the "food" command system.</p>
     */
    public CreateCommand() {
        super("create");
    }

    /**
     * Handles the creation of a food location in a park based on the input arguments.
     * <p>
     * If the required arguments are not provided, or the player is not located inside a park,
     * an appropriate error message is sent to the player.
     * This method also updates the display name of the item in the player's main hand
     * and stores the created food location in the system.
     *
     * <p><b>Command Format:</b></p>
     * <pre>/food create [id] [warp] [name]</pre>
     * <p>
     * Ensure the following conditions:
     * <ul>
     *   <li>User holds an item in their main hand to represent the food location.</li>
     *   <li>User is located within a valid park when executing the command.</li>
     *   <li>The specified food location ID does not already exist within the park.</li>
     * </ul>
     *
     * @param player The player who executes the command. Used for locating the park, retrieving the item in hand,
     *               and sending feedback messages.
     * @param args   Command arguments to configure the food location. Must contain at least three elements:
     *               <ol>
     *                   <li><b>args[0]</b>: The ID of the food location.</li>
     *                   <li><b>args[1]</b>: The target warp location.</li>
     *                   <li><b>args[2...n]</b>: The display name of the food location (supports multiple words).</li>
     *               </ol>
     * @throws CommandException Throws this exception when a critical failure occurs during command processing.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "/food create [id] [warp] [name]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Also, hold the item for the food location in your hand!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "The name of the item will be changed to the name of the food location.");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        if (ParkManager.getFoodManager().getFoodLocation(args[0], park.getId()) != null) {
            player.sendMessage(ChatColor.RED + "A food location already exists with the id " + args[0] + "!");
            return;
        }
        ItemStack item = player.getItemInMainHand().clone();
        if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Hold the item in your hand that will represent the food location in the menu!");
            return;
        }
        StringBuilder name = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            name.append(args[i]).append(" ");
        }
        String displayName = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', name.toString().trim());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        ParkManager.getFoodManager().addFoodLocation(new FoodLocation(args[0], park.getId(), displayName, args[1], item));
        player.sendMessage(ChatColor.GREEN + "Created new food location " + displayName + ChatColor.GREEN + " at /warp " + args[0] + "!");
    }
}
