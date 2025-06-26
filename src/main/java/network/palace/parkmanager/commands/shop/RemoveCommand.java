package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.shop.Shop;
import org.bukkit.ChatColor;

/**
 * <p>
 * The {@code RemoveCommand} class represents a command for removing an existing shop.
 * This command allows players to delete a shop that has been previously created
 * within the boundaries of a park.
 * </p>
 *
 * <p>
 * This command requires the player to be inside a park and to specify a valid shop
 * ID. The shop ID can be retrieved using the <code>/shop list</code> command. If
 * the specified shop ID is not found or the command is executed improperly,
 * appropriate error messages will be displayed to the player.
 * </p>
 *
 * <h3>Command Description:</h3>
 * <ul>
 *  <li>Command syntax: <code>/shop remove [id]</code></li>
 *  <li>The <code>[id]</code> parameter represents the unique ID of the shop to be removed.</li>
 * </ul>
 *
 * <h3>Behavior:</h3>
 * <ul>
 *  <li>Players must be located within a park to execute this command.</li>
 *  <li>An error message is displayed if the shop ID is not provided or is invalid.</li>
 *  <li>On successful removal, a confirmation message is sent to the player.</li>
 *  <li>If an error occurs during the removal process, an appropriate error message is shown.</li>
 * </ul>
 *
 * <h3>Error Scenarios:</h3>
 * <ul>
 *  <li>If the player is not within a park, an error message is displayed.</li>
 *  <li>If the shop ID does not exist within the park, an error message is displayed.</li>
 *  <li>If an internal issue occurs during the shop removal, an error message is displayed.</li>
 * </ul>
 */
@CommandMeta(description = "Remove an existing shops")
public class RemoveCommand extends CoreCommand {

    /**
     * Constructs a new {@code RemoveCommand} instance.
     *
     * <p>The {@code RemoveCommand} is a subcommand of the shop management system that
     * allows players to remove an existing shop within a park. This command ensures
     * that the player is inside a park and provides the necessary validation for the
     * specified shop ID to enable its removal.</p>
     *
     * <h3>Features and Behavior:</h3>
     * <ul>
     *   <li>Executes the {@code /shop remove [id]} command to delete a shop.</li>
     *   <li>Verifies that the player is located within a park before proceeding.</li>
     *   <li>Validates the provided shop ID and checks for its existence in the current park.</li>
     *   <li>Provides success confirmation upon successful removal of a shop.</li>
     *   <li>Displays error messages for invalid shop IDs, absence of a park, or removal failures.</li>
     * </ul>
     *
     * <h3>Command Requirements:</h3>
     * <ul>
     *   <li>Players must specify a valid shop ID (<code>[id]</code>) when using this command.</li>
     *   <li>The shop ID must refer to a shop that exists in the park the player is currently in.</li>
     *   <li>The command cannot be executed outside of park boundaries.</li>
     * </ul>
     */
    public RemoveCommand() {
        super("remove");
    }

    /**
     * Handles the command to remove an existing shop from the system.
     *
     * <p>
     * This method allows a player to delete a shop within the boundaries of a park.
     * The player must provide a valid shop ID as an argument and must also be located within
     * a park for this command to execute successfully. The shop ID can be retrieved using
     * the command <code>/shop list</code>.
     * </p>
     *
     * <h3>Execution Flow:</h3>
     * <ul>
     *  <li>If no arguments are provided, an error message is shown with the correct syntax for the command.</li>
     *  <li>The command checks if the player is inside a park. If not, an error message is displayed.</li>
     *  <li>The shop associated with the provided ID is retrieved. If no match is found, an error message is displayed.</li>
     *  <li>If a valid shop is found, the system attempts to remove it. A success or error message is then displayed based on the removal result.</li>
     * </ul>
     *
     * <p>Error handling is managed at each stage to ensure the player receives appropriate feedback.</p>
     *
     * @param player The player executing the command. Represents the user who wants to remove a shop.
     * @param args The command arguments provided by the player. The first argument is expected to be the shop ID.
     *             <ul>
     *                 <li><code>args[0]</code> - The unique identifier of the specific shop to be removed.</li>
     *             </ul>
     * @throws CommandException If an issue occurs during the execution of the command or its associated operations.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/shop remove [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the shop id from /shop list!");
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
        if (ParkManager.getShopManager().removeShop(args[0], park.getId())) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + shop.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + shop.getName() + "!");
        }
    }
}
