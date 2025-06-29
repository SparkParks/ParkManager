package network.palace.parkmanager.utils;

import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for managing player-related data, such as login information and user cache.
 * <p>
 * This class provides methods for adding, retrieving, and removing login data and user names
 * associated with player UUIDs.
 * </p>
 *
 * <h3>Features:</h3>
 * <ul>
 *   <li>Manage login data (with additional data like friends list).</li>
 *   <li>Cache player UUID to name mappings for quick lookup.</li>
 * </ul>
 */
public class PlayerUtil {
    /**
     * A mapping of player UUIDs to their associated login data represented as {@link Document}.
     * <p>
     * This variable is used to store specific information about players upon login, including but not
     * limited to the player's data and additional supplementary details such as their friends list.
     * The {@link Document} instance allows for flexible storage and retrieval of various player-related
     * attributes, serving as a central repository for login information.
     * </p>
     * <p>
     * Key characteristics of this variable:
     * </p>
     * <ul>
     *   <li>The key is a {@link UUID}, uniquely identifying a player.</li>
     *   <li>The value is a {@link Document}, containing structured metadata about the player.</li>
     *   <li>Designed for efficient management and lookup within the {@link PlayerUtil} utility class.</li>
     * </ul>
     * <p>
     * This variable is declared as {@code private final} to ensure encapsulated access and to maintain
     * data integrity during the application's lifecycle.
     * </p>
     */
    private final HashMap<UUID, Document> loginData = new HashMap<>();

    /**
     * A map that serves as an in-memory cache for associating player UUIDs with their corresponding names.
     * <p>
     * This cache is part of the {@code PlayerUtil} class and is primarily used to store and quickly retrieve
     * player names based on their unique identifier (UUID). It helps reduce the need for repetitive computation
     * or external service calls when player names are needed.
     * </p>
     *
     * <h3>Key Points:</h3>
     * <ul>
     *   <li>Keys: {@link UUID} - The unique identifier for a player.</li>
     *   <li>Values: {@link String} - The player's associated name.</li>
     *   <li>Provides efficient lookups for player name resolution.</li>
     *   <li>Can be populated and retrieved using methods within the {@code PlayerUtil} class.</li>
     * </ul>
     */
    @Getter private final HashMap<UUID, String> userCache = new HashMap<>();

    /**
     * Retrieves the login data associated with the specified UUID.
     * <p>
     * The login data is stored as a {@link Document} and may contain additional
     * information such as a list of friends or other metadata.
     * </p>
     *
     * @param uuid the unique identifier of the player whose login data is to be retrieved.
     *             Must not be {@code null}.
     * @return the {@link Document} containing the login data for the specified UUID,
     *         or {@code null} if no data is associated with the given UUID.
     */
    public Document getLoginData(UUID uuid) {
        return loginData.get(uuid);
    }

    /**
     * Adds login data for a specific player using their UUID, along with their
     * associated document and a list of friends.
     * <p>
     * This method stores the provided data in the internal login data map,
     * enabling further retrieval or modification as needed.
     * </p>
     *
     * @param uuid The unique identifier (UUID) of the player.
     * @param document A {@link Document} containing the player's login information.
     *                 This can include various metadata fields.
     * @param friends A list of UUIDs representing the friends of the player.
     *                This list is added to the provided document under the key "friends".
     */
    public void addLoginData(UUID uuid, Document document, List<UUID> friends) {
        document.put("friends", friends);
        loginData.put(uuid, document);
    }

    /**
     * Removes the login data associated with the specified UUID from the internal storage.
     * <p>
     * This method deletes the login data for the given UUID from the {@code loginData} map
     * and returns the removed data if it existed. If no data is associated with the UUID,
     * the method returns {@code null}.
     * </p>
     *
     * @param uuid the unique identifier of the player whose login data should be removed
     * @return the {@code Document} object representing the removed login data, or {@code null}
     *         if no data was associated with the specified UUID
     */
    public Document removeLoginData(UUID uuid) {
        return loginData.remove(uuid);
    }

    /**
     * Adds a UUID-to-name mapping to the user cache.
     * <p>
     * This method stores a player's UUID and corresponding name into the cache,
     * allowing quick retrieval of user names associated with their unique identifiers.
     * </p>
     *
     * @param uuid the unique identifier of the user to be added to the cache.
     *             Must not be <code>null</code>.
     * @param name the name of the user to be associated with the given UUID.
     *             Must not be <code>null</code> or empty.
     */
    public void addToUserCache(UUID uuid, String name) {
        userCache.put(uuid, name);
    }
}
