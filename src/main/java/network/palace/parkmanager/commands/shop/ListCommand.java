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
 * Represents the command for listing all shops within a park. This command is used for players
 * to retrieve and display information about available shops in the park they are currently located in.
 * <p>
 * When executed, the command identifies the park based on the player's current location and lists
 * all shops associated with that park. Each shop entry includes its name, unique identifier,
 * and the warp command associated with it.
 * <p>
 * Key functionality:
 * <ul>
 *   <li>Ensures the player is inside a valid park before proceeding.</li>
 *   <li>Provides details of all shops in the park, formatted with color-coded messages.</li>
 * </ul>
 * <p>
 * Usage requires correct implementation of the {@code ParkManager}, {@code ShopManager}, and related
 * underlying infrastructure to retrieve relevant park and shop data.
 */
@CommandMeta(description = "List all shops")
public class ListCommand extends CoreCommand {

    /**
     * Constructs a new {@code ListCommand} instance.
     *
     * <p>The {@code ListCommand} is used to list all shops available within a park. This command
     * is designed to be executed by players, and it retrieves information about all the shops
     * associated with the park the player is currently located in.</p>
     *
     * <p>The command provides the following functionality:
     * <ul>
     *   <li>Identifies the current park the player is inside using {@code ParkManager} utilities.</li>
     *   <li>Fetches and displays a list of shops within that park.</li>
     * </ul>
     * </p>
     *
     * <p>Each shop entry includes the shop's name, unique identifier, and its associated warp command,
     * formatted with appropriate color-coded messages for clarity.</p>
     *
     * <p>The {@code ListCommand} is registered with the identifier {@code "list"} and serves as a
     * key utility for players to quickly gain visibility into available shops within a park.</p>
     *
     * <p>Note: If executed outside of a valid park, the command informs the player with a
     * relevant error message.</p>
     */
    public ListCommand() {
        super("list");
    }

    /**
     * Handles the execution of the command to list all shops available within the park
     * where the player is currently located. This method retrieves and displays information
     * about shops such as their identifiers, names, and warp locations.
     * <p>
     * If the player is not inside a valid park, an error message is sent to the player.
     * <p>
     * Key features of this method:
     * <ul>
     *   <li>Verifies that the player is within a valid park.</li>
     *   <li>Retrieves and lists all shops associated with the park.</li>
     *   <li>Displays shop details in a color-coded format for better readability.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} executing the command. This object includes
     *               necessary player data and their current location.
     * @param args   An array of command arguments passed by the player. Although the
     *               method does not utilize arguments directly, the parameter is necessary
     *               to conform with inherited method requirements.
     * @throws CommandException Thrown if an error occurs during command execution.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + park.getId().getTitle() + ChatColor.GREEN + " Shops:");
        for (Shop shop : ParkManager.getShopManager().getShops(park.getId())) {
            player.sendMessage(ChatColor.AQUA + "- [" + shop.getId() + "] " + ChatColor.YELLOW + shop.getName() + ChatColor.GREEN + " at " + ChatColor.YELLOW + "/warp " + shop.getWarp());
        }
    }
}
