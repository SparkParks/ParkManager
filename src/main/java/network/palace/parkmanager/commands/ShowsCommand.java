package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.shows.ShowEntry;
import org.bukkit.ChatColor;

/**
 * The {@code ShowsCommand} class is responsible for handling the "shows" command
 * within the application. This command is primarily used by Shareholders to manage
 * and run shows, with additional permissions for higher-ranked users like Developers
 * and Staff Members.
 *
 * <p>Functionality includes:
 * <ul>
 *   <li>Opening the show menu and request menu for Shareholders and other ranks.</li>
 *   <li>Reloading the Shareholder Show Menu.</li>
 *   <li>Adding and removing shows from the Shareholder Show Menu.</li>
 *   <li>Listing all shows available in the menu.</li>
 *   <li>Enabling staff to manage show requests.</li>
 * </ul>
 *
 * <p>This command is designed to enforce rank-specific permissions:
 * <ul>
 *   <li>Only Shareholders or users with higher permissions (Community Manager and above) can use this command.</li>
 *   <li>Shareholders can add shows, remove shows, and run them in designated regions.</li>
 *   <li>Higher-ranked users (e.g., Developers) have access to request handling and configuration commands.</li>
 * </ul>
 *
 * <p>The command supports the following subcommands:
 * <ul>
 *   <li>{@code reload} - Reloads the Shareholder Show Menu.</li>
 *   <li>{@code add [ShowFile] [Region] [Display Name]} - Adds a new show to the menu with required details.</li>
 *   <li>{@code remove [ShowFile]} - Removes an existing show from the menu (without deleting the actual file).</li>
 *   <li>{@code list} - Lists all shows currently available in the menu.</li>
 *   <li>{@code menu} - Opens the Shareholder Show Menu UI.</li>
 *   <li>{@code requests} - Opens the request menu to handle show requests on the server.</li>
 * </ul>
 *
 * <p>Input parameters for adding a new show:
 * <ul>
 *   <li>{@code ShowFile} - The multishow command for the new show, with any spaces replaced by tildes (~).</li>
 *   <li>{@code Region} - Defines the region where the show can be activated. Shareholders must be in this region to run the show.</li>
 *   <li>{@code Display Name} - The display name of the show, which supports color codes.</li>
 * </ul>
 *
 * <p>Behavior Notes:
 * <ul>
 *   <li>Users below the Shareholder rank cannot access this command.</li>
 *   <li>Rank-specific checks ensure that only authorized users can perform sensitive actions like adding or managing shows.</li>
 *   <li>Color-coded messages are used to provide feedback and improve the user experience.</li>
 * </ul>
 */
@CommandMeta(description = "Allow Shareholders to run Shows with staff approval")
public class ShowsCommand extends CoreCommand {

    /**
     * Constructs a new instance of the <code>ShowsCommand</code> class.
     * <p>
     * The command associated with this class is initialized to "shows".
     * This class is typically used to handle the "shows" command functionality
     * and related logic within the application.
     */
    public ShowsCommand() {
        super("shows");
    }

    /**
     * Handles the execution of a specific command by verifying the player's rank and arguments,
     * and performing the associated actions within the Shareholder Show Menu system.
     *
     * <p> This method determines the player's eligibility to execute the command,
     * processes the command arguments, and performs one of the following actions:
     * <ul>
     *     <li>Opens menus based on the player's rank and permissions.</li>
     *     <li>Lists shows, reloads the menu, or manages show entries depending on the command.</li>
     *     <li>Processes addition or removal of show entries in the Shareholder Show Menu.</li>
     * </ul>
     *
     * @param player The player executing the command. Their rank and permissions are validated.
     * @param args An array of command arguments provided by the player. These dictate the action performed:
     *             <ul>
     *                 <li><code>"list"</code>: Lists all available shows.</li>
     *                 <li><code>"reload"</code>: Reloads the Shareholder Show Menu configuration.</li>
     *                 <li><code>"menu"</code>: Opens the Shareholder Show Menu.</li>
     *                 <li><code>"requests"</code>: Opens the request menu for show submissions.</li>
     *                 <li><code>"remove [show filename]"</code>: Removes a specific show from the menu.</li>
     *                 <li><code>"add [command] [file] [name]"</code>: Adds a new show entry with a specific command, file, and name.</li>
     *             </ul>
     * @throws CommandException If a validation issue or processing error occurs during command execution.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (player.getRank().getRankId() < Rank.SHAREHOLDER.getRankId()) {
            player.sendMessage(ChatColor.AQUA + "\nYou must be a " + Rank.SHAREHOLDER.getFormattedName() + ChatColor.AQUA +
                    " to use this! Find out more info at " + ChatColor.GREEN + "https://palnet.us/shareholder" + ChatColor.RESET + "\n");
            return;
        }
        if (player.getRank().getRankId() < Rank.CM.getRankId() && !player.getRank().equals(Rank.SHAREHOLDER)) {
            player.sendMessage(ChatColor.RED + "You can't use this command!");
            return;
        }
        if (player.getRank().equals(Rank.SHAREHOLDER)) {
            ParkManager.getShowMenuManager().openShowMenu(player);
            return;
        }
        if (player.getRank().getRankId() < Rank.DEVELOPER.getRankId()) {
            ParkManager.getShowMenuManager().openRequestMenu(player);
            return;
        }
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "list": {
                    ParkManager.getShowMenuManager().listShows(player);
                    return;
                }
                case "reload": {
                    player.sendMessage(ChatColor.BLUE + "Reloading Shareholder Show Menu...");
                    ParkManager.getShowMenuManager().initialize();
                    player.sendMessage(ChatColor.BLUE + "Reloaded Shareholder Show Menu!");
                    return;
                }
                case "menu": {
                    ParkManager.getShowMenuManager().openShowMenu(player);
                    return;
                }
                case "requests": {
                    ParkManager.getShowMenuManager().openRequestMenu(player);
                    return;
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            ShowEntry entry = ParkManager.getShowMenuManager().getShow(args[1]);
            if (entry == null) {
                player.sendMessage(ChatColor.RED + "A show isn't in the menu with the file '" + args[1] + "'!");
                return;
            }
            if (ParkManager.getShowMenuManager().removeShow(entry)) {
                player.sendMessage(ChatColor.GREEN + "Removed the show file '" + args[1] + "' from the Shareholder Show Menu!");
            } else {
                player.sendMessage(ChatColor.RED + "There was an error removing the show file '" + args[1] + "' from the Shareholder Show Menu!");
            }
            return;
        } else if (args.length >= 4 && args[0].equalsIgnoreCase("add")) {
            StringBuilder name = new StringBuilder();
            String multishowcommand = args[1].replaceAll("~", " ");
            for (int i = 3; i < args.length; i++) {
                name.append(args[i]).append(" ");
            }

            ShowEntry entry = new ShowEntry(multishowcommand, args[2], ChatColor.translateAlternateColorCodes('&', name.toString().trim()));
            ParkManager.getShowMenuManager().addShow(entry);
            player.sendMessage(ChatColor.GREEN + "Added a new command '" + entry.getCommand() + "' to the Shareholder Show Menu!");
            return;
        }
        helpMenu(player, args);
    }

    /**
     * Displays the help menu for the "shows" command, listing all available subcommands
     * and providing detailed usage instructions for each.
     *
     * <p>This menu guides players with the available commands in the Shareholder Show Menu system,
     * including operations like adding and removing shows, reloading the menu, viewing the show list,
     * or accepting/declining show requests.
     *
     * <p>Command highlights:
     * <ul>
     *     <li><code>/shows reload</code>: Reload the Shareholder Show Menu configuration.</li>
     *     <li><code>/shows add [ShowFile] [Region] [DisplayName]</code>: Add a new show.</li>
     *     <li><code>/shows remove [ShowFile]</code>: Remove an existing show from the menu.</li>
     *     <li><code>/shows list</code>: Display all shows in the menu.</li>
     *     <li><code>/shows menu</code>: Open the show menu interface.</li>
     *     <li><code>/shows requests</code>: Manage show requests on this server.</li>
     * </ul>
     *
     * @param player The {@code CPlayer} instance of the player viewing the help menu. Used for sending the help messages.
     * @param args Command arguments passed by the player. Not utilized in this method.
     */
    private void helpMenu(CPlayer player, String[] args) {
        player.sendMessage(ChatColor.GREEN + "Show Menu Commands:");
        player.sendMessage(ChatColor.GREEN + "/shows reload");
        player.sendMessage(ChatColor.AQUA + "- Reload the Shareholder Show Menu.");
        player.sendMessage(ChatColor.GREEN + "/shows add [ShowFile] [Region] [Display Name]");
        player.sendMessage(ChatColor.AQUA + "- Add a new show to the Shareholder Show Menu.");
        player.sendMessage(ChatColor.AQUA + "- [Command] is the multishow command to run (probably a pre-show) without the '/multishow start' part. Substitute spaces with a tilda (~). For example: 'PreIRoE10~dhsepcot~DHS/Epcot'");
        player.sendMessage(ChatColor.AQUA + "- Shareholders can only run the show when they're in the [Region].");
        player.sendMessage(ChatColor.AQUA + "- [Display Name] supports color codes.");
        player.sendMessage(ChatColor.GREEN + "/shows remove [ShowFile]");
        player.sendMessage(ChatColor.AQUA + "- Remove a show from the Shareholder Show Menu.");
        player.sendMessage(ChatColor.AQUA + "- This does " + ChatColor.RED + "" + ChatColor.ITALIC + "not " + ChatColor.AQUA + "delete the show file.");
        player.sendMessage(ChatColor.GREEN + "/shows list");
        player.sendMessage(ChatColor.AQUA + "- List all shows in the Shareholder Show Menu.");
        player.sendMessage(ChatColor.GREEN + "/shows menu");
        player.sendMessage(ChatColor.AQUA + "- Open the show menu.");
        player.sendMessage(ChatColor.GREEN + "/shows requests");
        player.sendMessage(ChatColor.AQUA + "- Accept/decline show requests on this server.");
    }
}
