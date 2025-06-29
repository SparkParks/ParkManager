package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * <p>Utility class for managing and toggling build mode status for players.</p>
 *
 * <p>This class provides functionality to check if a player is in build mode,
 * toggle their build mode status, and determine if a player is eligible to
 * toggle build mode based on their rank.</p>
 */
public class BuildUtil {

    /**
     * Determines whether the player associated with the specified UUID is currently in Build Mode.
     * <p>
     * This method retrieves the player instance from the {@code PlayerManager} using the given UUID
     * and checks their inventory state to determine if they are in Build Mode.
     * </p>
     *
     * @param uuid the unique identifier of the player whose Build Mode status is to be verified
     * @return {@code true} if the player is in Build Mode, {@code false} otherwise
     */
    public boolean isInBuildMode(UUID uuid) {
        return isInBuildMode(Core.getPlayerManager().getPlayer(uuid));
    }

    /**
     * Checks if the specified player is currently in Build Mode.
     * <p>
     * This method verifies whether the given player's inventory state
     * is set to {@code InventoryUtil.InventoryState.BUILD}.
     * </p>
     *
     * @param player the {@code CPlayer} whose Build Mode status is to be checked
     * @return {@code true} if the player is in Build Mode, {@code false} otherwise
     */
    public boolean isInBuildMode(CPlayer player) {
        return ParkManager.getInventoryUtil().getInventoryState(player).equals(InventoryUtil.InventoryState.BUILD);
    }

    /**
     * Toggles the Build Mode status for the specified player.
     *
     * <p>
     * This method checks the current inventory state of the player and either enables or disables
     * Build Mode accordingly. If the player is currently on a ride, they are not allowed to
     * toggle Build Mode, and the method will return {@code false}.
     * </p>
     *
     * <p>
     * Build Mode allows the player to modify and build structures within the game. Switching to
     * Build Mode changes the player's inventory state and provides them with the appropriate tools
     * for building. Exiting Build Mode reverts the player's state back to Guest mode.
     * </p>
     *
     * @param player the {@code CPlayer} whose Build Mode status is to be toggled
     * @return {@code true} if Build Mode was successfully toggled, {@code false} if the player
     *         is ineligible to toggle Build Mode (e.g., they are on a ride)
     */
    public boolean toggleBuildMode(CPlayer player) {
        InventoryUtil.InventoryState state = ParkManager.getInventoryUtil().getInventoryState(player);
        if (state.equals(InventoryUtil.InventoryState.RIDE)) {
            player.sendMessage(ChatColor.RED + "You cannot toggle Build Mode while on a ride!");
            return false;
        }
        if (state.equals(InventoryUtil.InventoryState.BUILD)) {
            ParkManager.getInventoryUtil().switchToState(player, InventoryUtil.InventoryState.GUEST);
            player.sendMessage(ChatColor.YELLOW + "You have exited Build Mode");
        } else {
            ParkManager.getInventoryUtil().switchToState(player, InventoryUtil.InventoryState.BUILD);
            player.sendMessage(ChatColor.YELLOW + "You have entered Build Mode");
        }
        return true;
    }

    /**
     * Determines whether the specified player can toggle Build Mode.
     * <p>
     * This method evaluates if the player's rank permits them to enable or disable Build Mode.
     * A player must have a rank greater than or equal to {@code Rank.TRAINEEBUILD}
     * to toggle Build Mode.
     * </p>
     *
     * @param player the {@code CPlayer} whose eligibility to toggle Build Mode is to be checked
     * @return {@code true} if the player's rank is sufficient to toggle Build Mode,
     *         {@code false} otherwise
     */
    public boolean canToggleBuildMode(CPlayer player) {
        return player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId();
    }
}
