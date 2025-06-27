package network.palace.parkmanager.leaderboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.Core;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * The {@code LeaderboardSign} class represents a sign block that displays a leaderboard
 * for a specified ride within a Minecraft world. It interacts with a leaderboard system
 * to fetch and display leaderboard data on the in-game sign.
 *
 * <p>This class manages the following:
 * <ul>
 *   <li>Storing the location and world of the sign block</li>
 *   <li>Fetching and caching leaderboard data</li>
 *   <li>Updating the sign's displayed lines with leaderboard entries</li>
 *   <li>Serialization of the leaderboard sign's data to JSON</li>
 * </ul>
 *
 * <p><strong>Important Notes:</strong>
 * <ul>
 *   <li>The sign's location must reference a valid block that is of a sign type (e.g., Wall Sign or Standing Sign).</li>
 *   <li>If the block is not a valid sign, an exception will be thrown during construction.</li>
 * </ul>
 */
@Getter
public class LeaderboardSign {
    /**
     * <p>The name of the ride associated with the leaderboard sign.</p>
     *
     * <p>This variable is used to uniquely identify the specific ride for which
     * the leaderboard sign is created. It is a constant and cannot be modified
     * after the object is constructed.</p>
     *
     * <p>Properties:</p>
     * <ul>
     *   <li><b>Immutable:</b> The value of this variable does not change after its assignment during object creation.</li>
     *   <li><b>Accessibility:</b> This variable is private and final, meaning it is accessible only within the class and cannot be altered.</li>
     * </ul>
     */
    private final String rideName;

    /**
     * <p>
     * Represents the X coordinate of the location of the leaderboard sign.
     * This coordinate is part of a 3D spatial system defining the precise
     * position of the sign in the world environment.
     * </p>
     *
     * <p>
     * Key characteristics:
     * <ul>
     *     <li><b>Immutable:</b> Once assigned, the value of this field cannot be modified.</li>
     *     <li><b>Usage Context:</b> Primarily used in conjunction with the Y and Z coordinates,
     *     as well as the world information, to determine the placement of the leaderboard sign.</li>
     * </ul>
     * </p>
     */
    private final double x;

    /**
     * The Y-coordinate of the {@code LeaderboardSign}'s location within the world.
     *
     * <p>This variable represents the vertical axis in the coordinate system
     * used to define the position of the sign. It is immutable and set
     * during the construction of the {@code LeaderboardSign} object.</p>
     *
     * <p>The Y-coordinate is typically used in conjunction with the X and Z
     * coordinates to determine the exact placement of the sign in 3D space.</p>
     *
     * <ul>
     *   <li>Defined in the context of a specific {@code World}.</li>
     *   <li>Immutable after initialization.</li>
     *   <li>This value must be a valid {@code double} that corresponds to the
     *   desired vertical location.</li>
     * </ul>
     */
    private final double y;

    /**
     * <p>Represents the Z-coordinate of the leaderboard sign's location in the virtual world.</p>
     *
     * <p>This field is a part of the geographical positioning of the leaderboard sign and is used,
     * alongside the X and Y fields, to define its specific location. It remains immutable after
     * initialization and is fundamental in determining the placement of the sign within the world.</p>
     *
     * <p>The Z-coordinate typically represents the height or depth in the coordinate system
     * depending on the virtual world's axis conventions.</p>
     */
    private final double z;

    /**
     * Represents the world in which the leaderboard sign is located.
     *
     * <p>This variable holds the {@link World} object corresponding to the
     * Minecraft world where the leaderboard sign exists. It plays a crucial
     * role in determining the physical location of the sign within the game
     * environment.</p>
     *
     * <p>The {@code world} field is immutable and is set during the creation
     * of the {@link LeaderboardSign} object.</p>
     *
     * <ul>
     *   <li>Used in conjunction with coordinates ({@code x}, {@code y}, {@code z})
     *       to define the precise location of the sign.</li>
     *   <li>Required for constructing certain functionalities that depend on the
     *       sign's placement in the game world.</li>
     * </ul>
     */
    private final World world;

    /**
     * A map that caches associations between UUIDs and their corresponding integer values.
     * <p>
     * This map is primarily utilized to store and manage data related to leaderboard functionality,
     * where each unique identifier (UUID) maps to an integer, such as a score or rank, for efficient retrieval and updates.
     * </p>
     *
     * <ul>
     *   <li>The key of the map is a {@link UUID} representing a unique identifier for an entity.</li>
     *   <li>The value of the map is an {@link Integer} which serves as the associated data, typically a score or ranking.</li>
     * </ul>
     */
    private HashMap<UUID, Integer> cachedMap = new HashMap<>();

    /**
     * Constructs a new {@code LeaderboardSign} object representing a sign at the specified location
     * in the game world. Validates that the block at the specified coordinates is a sign, throwing an
     * exception if it is not.
     *
     * <p>This constructor ensures that the specified block coordinates point to a {@code Sign} block
     * (including {@code SIGN}, {@code SIGN_POST}, or {@code WALL_SIGN}) in the provided {@code World}.
     * If the block type does not match, an exception is thrown.</p>
     *
     * @param rideName The name of the ride associated with the leaderboard sign.
     * @param x The x-coordinate of the sign's location.
     * @param y The y-coordinate of the sign's location.
     * @param z The z-coordinate of the sign's location.
     * @param world The {@code World} object where the sign is located.
     * @throws Exception If the block at the specified coordinates is not a valid sign.
     */
    public LeaderboardSign(String rideName, double x, double y, double z, World world) throws Exception {
        this.rideName = rideName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        Block b = world.getBlockAt((int) x, (int) y, (int) z);
        Sign s = ((Sign) b.getState());
        if (s == null || (!b.getType().equals(Material.SIGN) &&
                !b.getType().equals(Material.SIGN_POST) &&
                !b.getType().equals(Material.WALL_SIGN))) {
            throw new Exception();
        }
    }

    /**
     * Retrieves the location of the leaderboard sign.
     *
     * <p>This method constructs and returns a {@link Location} object representing
     * the position of the leaderboard sign in the game world. The returned location
     * includes the world, and the X, Y, and Z coordinates of the sign.
     *
     * @return the {@link Location} of the leaderboard sign, which includes:
     * <ul>
     *   <li>The world where the sign is located.</li>
     *   <li>The X-coordinate of the sign.</li>
     *   <li>The Y-coordinate of the sign.</li>
     *   <li>The Z-coordinate of the sign.</li>
     * </ul>
     */
    public Location getLocation() {
        return new Location(world, x, y, z);
    }

    /**
     * Updates the leaderboard sign with the latest leaderboard data.
     * <p>
     * This method retrieves the top 10 entries for the ride leaderboard from a MongoDB collection.
     * It clears the current cached data and updates it with new values, mapping player UUIDs to their scores.
     * It then constructs the lines to be displayed on the leaderboard sign based on the top three entries.
     * <p>
     * Each line contains the player display name, formatted and truncated if necessary, along with their score.
     *
     * @return a {@link SignUpdate} instance containing the updated leaderboard sign and its associated lines.
     */
    public SignUpdate update() {
        List<Document> list = Core.getMongoHandler().getRideCounterLeaderboard(rideName, 10);
        cachedMap.clear();
        for (Document doc : list) {
            UUID uuid = UUID.fromString(doc.getString("uuid"));
            int amount = doc.getInteger("total");
            cachedMap.put(uuid, amount);
        }
        String[] lines = new String[4];
        lines[0] = ChatColor.BLUE + "[Leaderboard]";
        if (list.size() >= 1) {
            lines[1] = getLine(list.get(0));
        }
        if (list.size() >= 2) {
            lines[2] = getLine(list.get(1));
        }
        if (list.size() >= 3) {
            lines[3] = getLine(list.get(2));
        }
        return new SignUpdate(this, lines);
    }

    /**
     * Retrieves a formatted and truncated string representation of a given document's name.
     * <p>
     * This method formats the document name using the {@code LeaderboardManager.getFormattedName} method
     * and ensures that the returned string does not exceed 18 characters in length.
     *
     * @param doc the {@link Document} from which the name will be retrieved and formatted
     * @return a string containing the formatted and truncated name, up to 18 characters
     */
    private String getLine(Document doc) {
        String name = LeaderboardManager.getFormattedName(doc);
        return name.substring(0, Math.min(name.length(), 18));
    }

    /**
     * Converts the state of the {@code LeaderboardSign} object into a {@link JsonObject}.
     * <p>
     * The {@code JsonObject} contains the following properties:
     * <ul>
     *   <li><b>name:</b> The name of the ride associated with the sign.</li>
     *   <li><b>x:</b> The x-coordinate of the sign's location.</li>
     *   <li><b>y:</b> The y-coordinate of the sign's location.</li>
     *   <li><b>z:</b> The z-coordinate of the sign's location.</li>
     *   <li><b>world:</b> The name of the world where the sign is located.</li>
     * </ul>
     *
     * @return A {@link JsonObject} representing the current state of the {@code LeaderboardSign}.
     */
    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        object.addProperty("name", rideName);
        object.addProperty("x", x);
        object.addProperty("y", y);
        object.addProperty("z", z);
        object.addProperty("world", world.getName());
        return object;
    }
}
