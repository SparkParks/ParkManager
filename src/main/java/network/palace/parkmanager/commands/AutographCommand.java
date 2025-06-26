package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * Represents a command that facilitates interactions related to autograph books in the game.
 * This command allows players to manage autograph requests, accept or deny such requests,
 * remove autographs from their books, and send a request to other players to sign their books.
 *
 * <p><b>Command Syntax:</b></p>
 * <ul>
 *     <li><code>/autograph [user]</code>: Sends an autograph request to the specified player.
 *         This action requires the player to have at least VIP rank.</li>
 *     <li><code>/autograph accept</code>: Accepts a pending autograph signing request from another player.</li>
 *     <li><code>/autograph deny</code>: Denies a pending autograph signing request from another player.</li>
 *     <li><code>/autograph remove [Page Number]</code>: Removes a signature from the specified page in the player's book.</li>
 * </ul>
 *
 * <p>This command dynamically handles various subcommands based on the player's rank, the provided arguments,
 * and the current state of autograph-related requests.</p>
 *
 * <p><b>Usage Overview:</b></p>
 * <ul>
 *     <li>Players can initiate autograph requests or respond to requests using the relevant subcommands
 *         (<code>accept</code>, <code>deny</code>, or specifying a player username).</li>
 *     <li>Players must input both a command and any required supplemental arguments (e.g., <code>remove</code>
 *         requires a page number).</li>
 *     <li>Higher-ranked players (VIP+) gain access to additional functionality such as initiating requests to
 *         other players.</li>
 * </ul>
 */
@CommandMeta(description = "Request to sign a player's book", aliases = {"a", "auto"})
public class AutographCommand extends CoreCommand {

    /**
     * Constructs a new instance of the {@code AutographCommand}.
     *
     * <p>This command is used to handle autograph-related functionality within the system.</p>
     *
     * <p>Key characteristics of {@code AutographCommand} include:
     * <ul>
     *    <li>Inheriting functionality from its base class.</li>
     *    <li>Being registered with the command name "autograph".</li>
     *    <li>Potentially interacting with players or server mechanisms to manage autograph-specific tasks.</li>
     * </ul>
     * </p>
     *
     * <p>This constructor initializes the command by associating it with the "autograph" keyword.</p>
     */
    public AutographCommand() {
        super("autograph");
    }

    /**
     * Handles the "autograph" command functionality for a player.
     *
     * <p>This method processes various subcommands related to autograph management, such as:</p>
     * <ul>
     *   <li>Handling autograph signing requests.</li>
     *   <li>Accepting or denying incoming requests.</li>
     *   <li>Removing signatures from a player's autograph book.</li>
     * </ul>
     *
     * <p>Depending on the subcommand and provided arguments, it invokes the appropriate
     * operations via the {@code ParkManager}'s {@code AutographManager}.</p>
     *
     * @param player The {@code CPlayer} instance representing the player who executed the command.
     * @param args An array of strings containing the command arguments supplied by the player.
     *             <ul>
     *               <li>If the array is empty or missing required arguments, a help menu will be displayed.</li>
     *               <li>The first argument determines the main action (e.g., "accept", "deny", "remove").</li>
     *               <li>Additional arguments may be required by specific subcommands (e.g., page numbers for "remove").</li>
     *             </ul>
     * @throws CommandException If an error occurs while executing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            helpMenu(player);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "accept": {
                ParkManager.getAutographManager().requestResponse(player, true);
                return;
            }
            case "deny": {
                ParkManager.getAutographManager().requestResponse(player, false);
                return;
            }
            case "remove": {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "/autograph remove [Page Number]");
                    return;
                }
                Integer pageNum;
                try {
                    pageNum = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + args[1] + " is not a number!");
                    return;
                }
                ParkManager.getAutographManager().removeAutograph(player, pageNum);
                return;
            }
            default: {
                if (player.getRank().getRankId() < Rank.VIP.getRankId()) {
                    helpMenu(player);
                    return;
                }
                CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
                if (tp == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                if (tp.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You can't sign your own book!");
                    return;
                }
                ParkManager.getAutographManager().requestToSign(player, tp);
            }
        }
    }


    /**
     * Displays the help menu for the autograph commands to a specified player.
     *
     * <p>This method sends a series of messages to the player, detailing the available
     * commands related to autograph functionality. It includes subcommands for requesting,
     * accepting, denying, and removing autograph signatures. Additional functionality is
     * available to players with a rank equal to or higher than VIP.</p>
     *
     * <p>The displayed commands include:</p>
     * <ul>
     *   <li><code>/autograph [user]</code>: Requests to sign a player's autograph book (VIP+).</li>
     *   <li><code>/autograph accept</code>: Accepts a signing request from another player.</li>
     *   <li><code>/autograph deny</code>: Denies a signing request from another player.</li>
     *   <li><code>/autograph remove [Page Number]</code>: Removes a signature from the player's book.</li>
     * </ul>
     *
     * @param player The {@code CPlayer} instance representing the player receiving the help menu.
     */
    private void helpMenu(CPlayer player) {
        player.sendMessage(ChatColor.GREEN + "Autograph Book Commands:");
        CPlayer cp = Core.getPlayerManager().getPlayer(player.getUniqueId());
        if (cp.getRank().getRankId() >= Rank.VIP.getRankId()) {
            player.sendMessage(ChatColor.GREEN + "/autograph [user] " + ChatColor.AQUA +
                    "- Request to sign a player's book");
        }
        player.sendMessage(ChatColor.GREEN + "/autograph accept " + ChatColor.AQUA +
                "- Accepts signing request from a player");
        player.sendMessage(ChatColor.GREEN + "/autograph deny " + ChatColor.AQUA +
                "- Denies signing request from a player");
        player.sendMessage(ChatColor.GREEN + "/autograph remove [Page Number] " + ChatColor.AQUA +
                "- Remove a signature from your book");

    }
}