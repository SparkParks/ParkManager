package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.handlers.storage.StorageData;
import org.bukkit.ChatColor;

/**
 * The {@code InvSeeCommand} class represents a command that allows an authorized player
 * to view the inventory of another player. This command offers options to view either
 * the target player's main inventory, backpack, or locker, depending on the arguments provided.
 * <p>
 * This class is intended for use by players with the appropriate rank, as specified in the
 * {@code @CommandMeta} annotation.
 * <p>
 * <b>Command format:</b>
 * <lu>
 *   <li>{@code /invsee [username] [main/backpack/locker]}</li>
 * </lu>
 * If the player or the selected inventory type is invalid, appropriate error messages
 * are sent to guide the user.
 * <p>
 * <strong><b>Key features of the command:</strong></b>
 * <lu>
 *   <li>Allows viewing another player's inventory in one of three categories:
 *       <lu>
 *         <li>Main: The player's main inventory</li>
 *         <li>Backpack: The player's backpack, if available</li>
 *         <li>Locker: The player's locker, if available</li>
 *       </lu>
 *   </li>
 *   <li>Error handling for invalid targets or unavailable inventory types.</li>
 * </lu>
 */
@CommandMeta(description = "Look into a player's inventory", rank = Rank.CM)
public class InvSeeCommand extends CoreCommand {

    /**
     * Constructs a new instance of the {@code InvSeeCommand} class.
     * <p>
     * This constructor initializes the command with the base name {@code "invsee"}, attaching it
     * to the command processing framework. When executed, this command allows an authorized player
     * to view another player's inventory, with options to view specific sections such as the main inventory,
     * backpack, or locker, as supported.
     * <p>
     * <b>Details:</b>
     * <lu>
     *   <li><b>Command Name:</b> {@code invsee}</li>
     *   <li><b>Command Purpose:</b> Enable inspection of another player's inventory.</li>
     *   <li><b>Options:</b> The command accepts additional arguments to specify the
     *       inventory section to view:
     *       <lu>
     *         <li>{@code main} - Represents the player's main inventory.</li>
     *         <li>{@code backpack} - Represents the player's backpack, if they have one configured.</li>
     *         <li>{@code locker} - Represents the player's locker, if one exists.</li>
     *       </lu>
     *   </li>
     *   <li><b>Requirement:</b> Requires the executing player to have the rank {@code Rank.CM}
     *       or higher to use this command.</li>
     * </lu>
     */
    public InvSeeCommand() {
        super("invsee");
    }

    /**
     * Handles the execution of the {@code /invsee} command, allowing a player to access and view
     * specific inventories of the target player. This command requires appropriate arguments to
     * specify the target player and the inventory type.
     * <p>
     * The supported inventory types are:
     * <ul>
     *   <li>{@code main} - The main inventory of the target player.</li>
     *   <li>{@code backpack} - The backpack inventory of the target player (if available).</li>
     *   <li>{@code locker} - The locker inventory of the target player (if available).</li>
     * </ul>
     * If any errors are encountered, such as invalid arguments, a non-existent target player,
     * or an unavailable inventory type, appropriate error messages are displayed to the executing player.
     *
     * @param player The {@code CPlayer} executing the command.
     * @param args   The command arguments. The first argument must be the username of the target player,
     *               and the second argument must specify the inventory type ({@code main/backpack/locker}).
     * @throws CommandException if an error occurs during the execution of the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/invsee [username] [main/backpack/locker]");
            return;
        }
        CPlayer target = Core.getPlayerManager().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "main": {
                player.openInventory(target.getInventory());
                return;
            }
            case "backpack": {
                if (!target.getRegistry().hasEntry("storageData")) {
                    player.sendMessage(ChatColor.RED + "There was an error opening " + target.getName() + "'s backpack!");
                    return;
                }
                StorageData data = (StorageData) target.getRegistry().getEntry("storageData");
                player.openInventory(data.getBackpack());
                return;
            }
            case "locker": {
                if (!target.getRegistry().hasEntry("storageData")) {
                    player.sendMessage(ChatColor.RED + "There was an error opening " + target.getName() + "'s locker!");
                    return;
                }
                StorageData data = (StorageData) target.getRegistry().getEntry("storageData");
                player.openInventory(data.getLocker());
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "/invsee [username] [main/backpack/locker]");
    }
}
