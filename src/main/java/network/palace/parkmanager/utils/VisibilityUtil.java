package network.palace.parkmanager.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

/**
 * Utility class for managing player visibility within the game.
 * <p>
 * This class provides methods for toggling, updating, and handling player visibility settings,
 * allowing players to manage who they can and cannot see in the game world.
 * </p>
 * <p>
 * Visibility settings are controlled through the {@code Setting} enum, which defines the
 * scope of visibility for a player.
 * </p>
 */
public class VisibilityUtil {

    /**
     * Updates the visibility status of a player in relation to other online players.
     * This method determines whether the specified player should see other players
     * or hide them based on their visibility settings.
     *
     * <p>The visibility of each player is decided using the {@code shouldSee} method.
     * If a player should be visible to the given player, the player is shown.
     * Otherwise, the player is hidden.
     *
     * @param player the player for whom visibility updates are being made. This player’s
     *               visibility of all other online players will be adjusted.
     */
    private void updatePlayerVisibility(CPlayer player) {
        for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
            if (shouldSee(player, tp)) {
                player.showPlayer(ParkManager.getInstance(), tp);
            } else {
                player.hidePlayer(ParkManager.getInstance(), tp);
            }
        }
    }

    /**
     * Determines whether {@code player1} should "see" or be able to interact with {@code player2}
     * based on their visibility settings and relationships.
     *
     * <p>This method evaluates the visibility setting of {@code player1} and checks various
     * conditions like rank, friendship status, and personal preferences to determine
     * visibility.</p>
     *
     * <p>The visibility behaviors are as follows:
     * <ul>
     *     <li>{@code ONLY_STAFF_AND_FRIENDS}: {@code player2} must have a rank of VIP or higher,
     *        or be a friend of {@code player1}.</li>
     *     <li>{@code ONLY_FRIENDS}: {@code player2} must be a friend of {@code player1}.</li>
     *     <li>{@code ALL_HIDDEN}: {@code player1} will not see {@code player2} regardless of other conditions.</li>
     *     <li>All other cases default to visible.</li>
     * </ul>
     * </p>
     *
     * @param player1 the player whose visibility setting is being evaluated.
     * @param player2 the player whose visibility relative to {@code player1} is being checked.
     * @return {@code true} if {@code player1} can see {@code player2}, {@code false} otherwise.
     */
    private boolean shouldSee(CPlayer player1, CPlayer player2) {
        if (player1.getUniqueId().equals(player2.getUniqueId())) return false;
        Setting setting = getSetting(player1);
        switch (setting) {
            case ONLY_STAFF_AND_FRIENDS:
                if (player2.getRank().getRankId() >= Rank.VIP.getRankId()) return true;
            case ONLY_FRIENDS:
                return isFriend(player1, player2);
            case ALL_HIDDEN:
                return false;
        }
        return true;
    }

    /**
     * Determines whether two players are friends by checking if the unique ID of one player
     * is present in the friends registry entry of the other player.
     *
     * @param player1 The first player whose friends registry is checked.
     * @param player2 The second player whose unique ID is looked for in the friends registry of player1.
     * @return {@code true} if player2 is listed as a friend in the friends registry of player1;
     *         {@code false} otherwise.
     */
    private boolean isFriend(CPlayer player1, CPlayer player2) {
        return ((List<UUID>) player1.getRegistry().getEntry("friends")).contains(player2.getUniqueId());
    }

    /**
     * Toggles the visibility setting of a player between visible to all and hidden from all.
     * If the player toggles too quickly (within 5 seconds), an error message is sent, and the toggle is not performed.
     * This method interacts with the player's registry to manage visibility settings and toggle delays.
     *
     * <p>
     * The visibility toggle works between the following states:
     * <ul>
     *   <li>{@code ALL_HIDDEN} - The player is completely hidden.</li>
     *   <li>{@code ALL_VISIBLE} - The player is visible to everyone.</li>
     * </ul>
     * If the current state is {@code ALL_HIDDEN}, the state will change to {@code ALL_VISIBLE}, and vice versa.
     * </p>
     *
     * @param player The player whose visibility setting is being toggled. The player's registry is accessed to manage
     *               current visibility state, toggle delays, and to send feedback messages.
     * @return {@code true} if the visibility toggle was successful; {@code false} if the toggle cooldown period (5 seconds)
     *         has not elapsed.
     */
    public boolean toggleVisibility(CPlayer player) {
        if (player.getRegistry().hasEntry("visibilityDelay") && (System.currentTimeMillis() < ((long) player.getRegistry().getEntry("visibilityDelay")))) {
            player.sendMessage(ChatColor.RED + "You must wait 5s between changing visibility settings!");
            return false;
        }
        player.getRegistry().addEntry("visibilityDelay", System.currentTimeMillis() + 5000);
        Setting currentSetting = getSetting(player);
        if (currentSetting.equals(Setting.ALL_HIDDEN)) {
            setSetting(player, Setting.ALL_VISIBLE, true);
        } else {
            setSetting(player, Setting.ALL_HIDDEN, true);
        }
        return true;
    }

    /**
     * Retrieves the visibility setting for the specified player. If the player does not
     * have a pre-existing visibility setting stored in their registry, a default value of
     * {@code Setting.ALL_VISIBLE} is added and returned.
     *
     * <p>The visibility setting defines the scope of visibility preferences the player has,
     * such as "all visible", "only friends", etc.
     *
     * @param player the {@code CPlayer} whose visibility setting is to be retrieved.
     *               This parameter must not be null.
     * @return the {@code Setting} associated with the provided player.
     *         If no setting exists, the default {@code Setting.ALL_VISIBLE} is stored
     *         and returned.
     */
    public Setting getSetting(CPlayer player) {
        if (!player.getRegistry().hasEntry("visibilitySetting"))
            player.getRegistry().addEntry("visibilitySetting", Setting.ALL_VISIBLE);
        return (Setting) player.getRegistry().getEntry("visibilitySetting");
    }

    /**
     * Updates the visibility setting for a player and applies any necessary delay restrictions.
     * <p>
     * This method allows a player to set their visibility preferences, which determines how other
     * players can interact or see them. If {@code delayBypass} is set to {@code false}, a five-second delay
     * restriction is enforced between updates to prevent rapid changes. The visibility settings are stored
     * in the player's registry and the player's visibility is updated accordingly.
     *
     * @param player       The {@code CPlayer} instance whose setting is being updated.
     * @param setting      The {@code Setting} value to be applied to the specified player.
     *                      Possible values are defined in the {@code Setting} enum, including
     *                      visibility states such as ALL_VISIBLE, ONLY_STAFF_AND_FRIENDS, and more.
     * @param delayBypass  A {@code boolean} indicating if the delay restriction should be bypassed.
     *                      If {@code true}, the delay is not enforced.
     *
     * @return {@code true} if the setting was successfully updated. Returns {@code false} if the update
     *         could not be processed due to the delay restriction being enforced.
     */
    public boolean setSetting(CPlayer player, Setting setting, boolean delayBypass) {
        if (!delayBypass) {
            if (player.getRegistry().hasEntry("visibilityDelay") && (System.currentTimeMillis() < ((long) player.getRegistry().getEntry("visibilityDelay")))) {
                player.sendMessage(ChatColor.RED + "You must wait 5s between changing visibility settings!");
                return false;
            }
            player.getRegistry().addEntry("visibilityDelay", System.currentTimeMillis() + 5000);
        }
        player.getRegistry().addEntry("visibilitySetting", setting);
        updatePlayerVisibility(player);
        return true;
    }

    /**
     * Handles the visibility settings for a player upon joining and ensures that other players
     * can or cannot see them based on these settings.
     *
     * <p>This method updates the player's visibility setting, applies it, and hides the player
     * from those who are not permitted to see them according to their visibility preferences.
     *
     * @param player The player who has joined and whose visibility settings are being handled.
     * @param visibility A string representing the visibility setting to be applied.
     *                   Valid values may include:
     *                   <ul>
     *                     <li>"all" - All players can see them.</li>
     *                     <li>"staff_friends" - Only staff and friends can see them.</li>
     *                     <li>"friends" - Only friends can see them.</li>
     *                     <li>"none" - The player is hidden from everyone.</li>
     *                   </ul>
     */
    public void handleJoin(CPlayer player, String visibility) {
        setSetting(player, Setting.fromString(visibility), false);
        Core.getPlayerManager().getOnlinePlayers().stream().filter(p -> !shouldSee(p, player)).forEach(p -> p.hidePlayer(player));
    }

    /**
     * Represents the visibility settings for players. This enumeration defines the
     * different levels of visibility a player can choose for interacting with other players.
     *
     * <p>Each visibility setting comes with an associated display name, color,
     * material representation, and data value used for various visual representations
     * in the application.</p>
     *
     * <p>The defined visibility settings include:</p>
     * <ul>
     *     <li>{@code ALL_VISIBLE} - The player is visible to all other players.</li>
     *     <li>{@code ONLY_STAFF_AND_FRIENDS} - The player is visible only to staff
     *         members and their friends.</li>
     *     <li>{@code ONLY_FRIENDS} - The player is visible only to their friends.</li>
     *     <li>{@code ALL_HIDDEN} - The player is hidden from everyone.</li>
     * </ul>
     *
     * <p>This enum also provides utility methods to handle conversions:</p>
     * <ul>
     *     <li>{@code toString()} - Converts the visibility setting to a
     *         corresponding string value.</li>
     *     <li>{@code fromString(String visibility)} - Returns the appropriate
     *         {@code Setting} instance based on the provided string value. Defaults
     *         to {@code ALL_VISIBLE} if the input is invalid or null.</li>
     * </ul>
     */
    @Getter
    @AllArgsConstructor
    public enum Setting {
        ALL_VISIBLE("Visible", ChatColor.GREEN, Material.STAINED_CLAY, 13),
        ONLY_STAFF_AND_FRIENDS("Staff & Friends", ChatColor.YELLOW, Material.STAINED_CLAY, 4),
        ONLY_FRIENDS("Friends", ChatColor.GOLD, Material.STAINED_CLAY, 1),
        ALL_HIDDEN("Hidden", ChatColor.RED, Material.STAINED_CLAY, 14);

        String text;
        ChatColor color;
        Material block;
        int data;

        public String toString() {
            switch (this) {
                case ALL_VISIBLE:
                    return "all";
                case ONLY_STAFF_AND_FRIENDS:
                    return "staff_friends";
                case ONLY_FRIENDS:
                    return "friends";
            }
            return "none";
        }

        public static Setting fromString(String visibility) {
            if (visibility == null) return ALL_VISIBLE;
            switch (visibility.toLowerCase()) {
                case "staff_friends":
                    return ONLY_STAFF_AND_FRIENDS;
                case "friends":
                    return ONLY_FRIENDS;
                case "none":
                    return ALL_HIDDEN;
                default:
                    return ALL_VISIBLE;
            }
        }
    }
}
