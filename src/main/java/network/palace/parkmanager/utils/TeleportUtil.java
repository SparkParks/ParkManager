package network.palace.parkmanager.utils;

import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Utility class for managing and handling teleportation actions, particularly
 * providing functionality for storing and retrieving players' locations for
 * teleportation commands such as /back.
 * <p>
 * This class maintains a record of players' last locations, enabling
 * functionalities such as teleporting back to a previous location.
 * <p>
 * Note: Only players with sufficient rank permissions are able to have
 * their locations logged.
 *
 * <p><b>Functionality:</b></p>
 * <ul>
 * <li>Log a player's current or specific location.</li>
 * <li>Remove a player's stored location upon logout.</li>
 * <li>Teleport a player to their previously saved location.</li>
 * </ul>
 */
public class TeleportUtil {
    /**
     * A collection that tracks the last known locations of players identified by their unique IDs.
     * <p>
     * This map is used to store and retrieve the most recent location associated with a player.
     * It serves as a core component for implementing teleportation commands, such as enabling
     * players to return to their previous position.
     * </p>
     * <p><b>Details:</b></p>
     * <ul>
     *   <li><code>UUID</code>: Associated with individual players, guaranteeing unique mapping for each player.</li>
     *   <li><code>Location</code>: Represents the player's saved position in the world.</li>
     * </ul>
     * <p>
     * This variable is managed exclusively by methods in the {@link TeleportUtil} class and typically
     * interacts with higher-rank players or those having specific permissions.
     * </p>
     */
    private HashMap<UUID, Location> locations = new HashMap<>();

    /**
     * Logs a player's current location for potential retrieval or teleportation purposes.
     * <p>
     * This method saves the player's current location if the player's rank
     * satisfies the required permission level to allow logging. Internally, it calls
     * {@link #log(CPlayer, Location)} with the player's current location as the argument.
     *
     * @param player The player whose current location is to be logged. Must have
     *               the appropriate rank to enable location logging.
     */
    public void log(CPlayer player) {
        log(player, player.getLocation());
    }

    /**
     * Logs the specified player's current or given location for potential
     * teleportation purposes. This method only logs the location if the
     * player's rank meets or exceeds the required threshold.
     *
     * <p><b>Usage Details:</b></p>
     * <ul>
     * <li>Rank requirement: Only players with a rank equal to or higher than
     * {@code Rank.CM} have their locations logged.</li>
     * <li>The location is stored using the player's unique identifier (UUID) as the key.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} whose location is to be logged.
     *               Only players with an adequate rank will have their
     *               locations stored.
     * @param location The {@link Location} to be recorded. This represents the
     *                 player's current or specified location that will be stored
     *                 for future reference.
     */
    public void log(CPlayer player, Location location) {
        if (player.getRank().getRankId() >= Rank.CM.getRankId()) locations.put(player.getUniqueId(), location);
    }

    /**
     * Removes a player's stored location from the location log when they log out.
     * This ensures that no stale or outdated location data remains after the
     * player leaves the session.
     *
     * <p><b>Functionality:</b></p>
     * <ul>
     * <li>Clears the stored location data for the specified player.</li>
     * <li>Keeps location records concise by removing unnecessary entries.</li>
     * </ul>
     *
     * @param player The player whose location data is to be removed from the log.
     */
    public void logout(Player player) {
        locations.remove(player.getUniqueId());
    }

    /**
     * Teleports the player to their previously saved location, if available, and logs their current location.
     * The teleportation also removes the stored location from the internal record.
     * <p>
     * This method is typically used to support "back" functionality for players.
     *
     * @param player The player requesting to be teleported to their previously saved location.
     *               Must be a valid player object with a unique identifier.
     * @return {@code true} if the player was successfully teleported to their saved location,
     *         {@code false} if no saved location was found for the player.
     */
    public boolean back(CPlayer player) {
        Location back = locations.remove(player.getUniqueId());
        if (back == null) return false;
        Location current = player.getLocation();
        player.teleport(back);
        log(player, current);
        return true;
    }
}