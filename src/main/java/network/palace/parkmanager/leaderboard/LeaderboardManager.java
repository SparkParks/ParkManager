package network.palace.parkmanager.leaderboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages leaderboard signs in the game world. This class is responsible for loading,
 * saving, updating, and registering leaderboard signs as well as processing leaderboard information.
 * The leaderboard signs display player rankings and associated data from the game's leaderboard system.
 * <p>
 * The class integrates with external components like {@code ParkManager}, {@code Core}, and MongoDB to handle
 * player data and sign-related operations.
 * </p>
 *
 * <h3>Features:</h3>
 * <ul>
 *    <li>Loads leaderboard configuration from a file system and initializes leaderboard signs.</li>
 *    <li>Regularly updates leaderboard signs to reflect the latest player rankings.</li>
 *    <li>Provides methods for registering, deleting, and retrieving leaderboard signs.</li>
 *    <li>Ensures persistent storage of leaderboard signs by saving them to the filesystem.</li>
 *    <li>Formats leaderboard information for display on signs.</li>
 * </ul>
 *
 * <h3>Threading:</h3>
 * <p>
 *   Certain operations, such as updates and file saving, are performed asynchronously to prevent
 *   blocking the main game thread. Timers are utilized to schedule periodic updates of leaderboard signs.
 * </p>
 *
 * <h3>Usage Notes:</h3>
 * <p>
 *   Leaderboard signs are uniquely identified by their location and ride name. Avoid registering duplicate
 *   signs with the same ride name. Use appropriate methods to update or remove signs when needed.
 * </p>
 *
 * <h3>Error Handling:</h3>
 * <ul>
 *    <li>Logs errors during initialization and updates for easier debugging.</li>
 *    <li>Handles invalid file inputs by creating default configurations.</li>
 *    <li>Catches and logs exceptions where applicable to ensure program stability.</li>
 * </ul>
 *
 * <h3>Core Functionality:</h3>
 * <dl>
 *    <dt>{@link #registerLeaderboardSign(String[], Block)}</dt>
 *    <dd>Registers a new leaderboard sign in the specified location.</dd>
 *    <dt>{@link #deleteSign(Location)}</dt>
 *    <dd>Deletes a leaderboard sign at the specified location.</dd>
 *    <dt>{@link #update()}</dt>
 *    <dd>Updates all leaderboard signs to reflect the latest data.</dd>
 *    <dt>{@link #saveToFile()}</dt>
 *    <dd>Saves all leaderboard signs to persistent storage.</dd>
 *    <dt>{@link #getSign(Location)}</dt>
 *    <dd>Retrieves a leaderboard sign at a specified location, if one exists.</dd>
 *    <dt>{@link #sortLeaderboardMessages(List)}</dt>
 *    <dd>Static method that sorts leaderboard rankings in descending order.</dd>
 *    <dt>{@link #getFormattedName(UUID, int)}</dt>
 *    <dd>Static method that formats the leaderboard entry's display name and rank.</dd>
 * </dl>
 */
public class LeaderboardManager {
    /**
     * <p>
     * Represents a list of leaderboard signs managed by the {@link LeaderboardManager}.
     * Each {@link LeaderboardSign} in this list is a registered sign that displays
     * leaderboard information in the game world.
     * </p>
     * <p>
     * <b>Properties:</b>
     * <lu>
     * <li>The list is immutable (`final`) and cannot be reassigned after initialization.</li>
     * <li>The list is stored in-memory for quick access and manipulation.</li>
     * </lu>
     * <p>
     * <b>Modifications:</b>
     * <lu>
     * <li>To add or remove leaderboard signs, see the methods provided in {@link LeaderboardManager},
     * such as {@code registerLeaderboardSign} and {@code deleteSign}.</li>
     * </lu>
     * </p>
     */
    private final List<LeaderboardSign> signs = new ArrayList<>();

    /**
     * A list that stores pending updates to leaderboard signs.
     *
     * <p>This list holds instances of {@link SignUpdate}, each of which contains a
     * {@link LeaderboardSign} and the new lines of text to be displayed on the leaderboard sign.
     * These updates are processed to keep the leaderboard signs synchronized with the most
     * recent data.
     *
     * <p>Key details:
     * <lu>
     *   <li>Each {@link SignUpdate} associates a {@link LeaderboardSign} with its updated text content.</li>
     *   <li>The list is maintained in memory and is immutable as it is declared {@code final}.</li>
     *   <li>New updates can be added or processed as part of sign update scheduling.</li>
     * </lu>
     *
     * <p>Intended Usage:
     * <ul>
     *   <li>Track updates to leaderboard signs that need to be applied.</li>
     *   <li>Ensure leaderboard signs reflect real-time changes in the leaderboard data.</li>
     * </ul>
     */
    private final List<SignUpdate> signUpdates = new ArrayList<>();

    /**
     * <p>The constructor for the {@code LeaderboardManager} class, responsible for loading leaderboard configurations,
     * initializing leaderboard signs from existing storage, and scheduling periodic update tasks.</p>
     *
     * <p>This constructor performs the following actions:</p>
     * <ul>
     *   <li>Registers the "leaderboard" subsystem using the {@link FileUtil} to manage leaderboard-related files.</li>
     *   <li>Attempts to load leaderboard information from the "leaderboards" configuration file and initializes
     *       {@link LeaderboardSign} instances for each valid entry.</li>
     *   <li>If the configuration file is missing or invalid, a default configuration is saved to ensure persistent state.</li>
     *   <li>Schedules periodic tasks:
     *     <ul>
     *       <li>One task updates leaderboard signs at regular intervals based on pending changes.</li>
     *       <li>Another asynchronous task refreshes leaderboard data periodically.</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p>Error handling is included to manage issues like invalid JSON data, missing worlds, or general file I/O errors.
     * These errors are logged to provide relevant diagnostics.</p>
     *
     * @throws IOException if an I/O error occurs while registering the subsystem or accessing configuration files.
     */
    public LeaderboardManager() throws IOException {
        FileUtil.FileSubsystem subsystem = ParkManager.getFileUtil().registerSubsystem("leaderboard");
        try {
            JsonElement element = subsystem.getFileContents("leaderboards");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject leaderboardObject = (JsonObject) entry;
                    String name = leaderboardObject.get("name").getAsString();
                    double x = leaderboardObject.get("x").getAsDouble();
                    double y = leaderboardObject.get("y").getAsDouble();
                    double z = leaderboardObject.get("z").getAsDouble();
                    String worldName = leaderboardObject.get("world").getAsString();
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) continue;
                    LeaderboardSign sign;
                    try {
                        sign = new LeaderboardSign(name, x, y, z, world);
                    } catch (Exception e) {
                        continue;
                    }
                    signs.add(sign);
                }
            } else {
                saveToFile();
            }
        } catch (IOException e) {
            Core.logMessage("LeaderboardManager", "There was an error loading the LeaderboardManager config!");
            e.printStackTrace();
        }
        Core.runTaskTimer(() -> new ArrayList<>(signUpdates).forEach(update -> {
            try {
                LeaderboardSign sign = update.getSign();
                Location loc = sign.getLocation();
                Sign s = (Sign) loc.getBlock().getState();
                String[] lines = update.getLines();
                for (int i = 0; i < lines.length; i++) {
                    s.setLine(i, lines[i]);
                }
                s.update();
            } catch (Exception e) {
                deleteSign(update.getSign().getLocation());
            }
            signUpdates.remove(update);
        }), 0L, 600L);
        Core.runTaskTimerAsynchronously(ParkManager.getInstance(), this::update, 400L, 10 * 60 * 20L);
    }

    /**
     * Retrieves a list of leaderboard signs currently stored.
     * <p>
     * This method returns a copy of the internal list of {@code LeaderboardSign} objects
     * to ensure encapsulation and avoid accidental modifications to the original list.
     * </p>
     *
     * @return a {@code List} of {@code LeaderboardSign} objects representing the leaderboard signs.
     */
    private List<LeaderboardSign> getSigns() {
        return new ArrayList<>(signs);
    }

    /**
     * Retrieves a {@link LeaderboardSign} instance based on the provided {@link Location}.
     * If there is a {@link LeaderboardSign} at the specified location, it is returned;
     * otherwise, {@code null} is returned.
     *
     * <p>
     * The method checks each {@link LeaderboardSign} in the list maintained by the
     * {@code LeaderboardManager} to find a match with the provided location.
     * </p>
     *
     * @param loc the {@link Location} to search for a corresponding {@link LeaderboardSign};
     *            must not be {@code null}.
     * @return the {@link LeaderboardSign} at the specified {@link Location}, or {@code null}
     *         if no sign exists at the given location.
     */
    public LeaderboardSign getSign(Location loc) {
        for (LeaderboardSign sign : getSigns()) {
            if (sign.getLocation().equals(loc)) {
                return sign;
            }
        }
        return null;
    }

    /**
     * Registers a leaderboard sign at the specified block location using the provided sign text.
     * <p>
     * This method validates the uniqueness of the leaderboard sign based on the concatenated text
     * from {@code lines[1]}, {@code lines[2]}, and {@code lines[3]}. If the sign is unique, it is
     * asynchronously added to the list of leaderboard signs and persisted for future use.
     * </p>
     *
     * <p>If an error occurs while creating the {@code LeaderboardSign} object or if the
     * provided block is invalid, the registration will fail.</p>
     *
     * @param lines an array of strings representing the text to be displayed on the sign.
     *              <ul>
     *                  <li>{@code lines[0]} is typically ignored, while the values from {@code lines[1]},
     *                  {@code lines[2]}, and {@code lines[3]} are concatenated and trimmed to create
     *                  a unique sign identifier.</li>
     *              </ul>
     * @param block the {@link Block} where the sign will be located; must represent a valid sign state.
     *              If not, the operation will fail.
     * @return {@code true} if the sign is successfully registered and added to the leaderboard system,
     *         or {@code false} if the sign already exists, the block is invalid, or an error occurs
     *         while creating the new leaderboard sign.
     */
    public boolean registerLeaderboardSign(String[] lines, Block block) {
        Sign sign = (Sign) block.getState();
        String name = (lines[1] + " " + lines[2] + " " + lines[3]).trim();
        for (LeaderboardSign s : getSigns()) {
            if (s.getRideName().equals(name)) return false;
        }
        Location loc = sign.getLocation();
        LeaderboardSign signObject;
        try {
            signObject = new LeaderboardSign(name, loc.getX(), loc.getY(), loc.getZ(), loc.getWorld());
        } catch (Exception e) {
            return false;
        }
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            SignUpdate update = signObject.update();
            if (update != null) signUpdates.add(update);
            signs.add(signObject);
            saveToFile();
        });
        return true;
    }

    /**
     * Deletes a leaderboard sign located at the specified location.
     *
     * <p>
     * This method iterates through the list of stored {@code LeaderboardSign} objects
     * and removes the sign that matches the given location. After removing the
     * relevant sign from the internal list, the changes are persisted by
     * calling {@link #saveToFile()}.
     * </p>
     *
     * <p>
     * Note that if no sign exists at the specified location, the method performs
     * no actions.
     * </p>
     *
     * @param loc the {@link Location} of the leaderboard sign to be deleted; must not be {@code null}.
     */
    public void deleteSign(Location loc) {
        for (LeaderboardSign sign : getSigns()) {
            if (!sign.getLocation().equals(loc)) continue;
            signs.remove(sign);
        }
        saveToFile();
    }

    /**
     * Sorts the leaderboard messages in descending order based on the numeric value
     * present at the beginning of each message.
     * <p>
     * Messages are expected to begin with a numeric value followed by a colon (e.g., "10: Player").
     * The method strips color codes from the messages, extracts the numeric portion, and
     * sorts the messages based on this value in descending order.
     * </p>
     *
     * @param messages a {@link List} of {@link String} representing leaderboard messages;
     *                 each message should include a numeric value as the prefix.
     */
    public static void sortLeaderboardMessages(List<String> messages) {
        messages.sort((o1, o2) -> {
            String nocolor1 = ChatColor.stripColor(o1);
            String nocolor2 = ChatColor.stripColor(o2);
            int i1 = Integer.parseInt(nocolor1.substring(0, nocolor1.indexOf(":")));
            int i2 = Integer.parseInt(nocolor2.substring(0, nocolor2.indexOf(":")));
            return i2 - i1;
        });
    }

    /**
     * Retrieves a formatted name string based on the provided document.
     *
     * <p>This method extracts a UUID and a numeric total value from the given {@code Document}.
     * These values are then used to generate a formatted name in combination with user and rank data.
     * If the user information is cached, it is retrieved directly from the cache; otherwise, it is fetched
     * from the database and added to the cache.</p>
     *
     * <p>The resulting formatted name is structured as: {@code total: <rank_tag_color><username>}.</p>
     *
     * @param doc the {@link Document} object containing user-related data.
     *            <ul>
     *               <li>{@code uuid} - a {@code String} representing the user's unique identifier.</li>
     *               <li>{@code total} - an {@code Integer} representing the leaderboard value.</li>
     *            </ul>
     * @return a {@code String} containing the formatted name with rank and total score details.
     */
    public static String getFormattedName(Document doc) {
        return getFormattedName(UUID.fromString(doc.getString("uuid")), doc.getInteger("total"));
    }

    /**
     * Generates a formatted name string for a user, including their rank and a numeric identifier.
     * <p>
     * This method retrieves the player's username and rank based on the provided UUID.
     * If the username is not cached, it fetches the name from the database and updates the cache.
     * The formatted string includes the provided total as a prefix, the player's rank tag color,
     * and their username.
     * </p>
     *
     * @param uuid  the UUID of the player whose name and rank are to be retrieved; must not be {@code null}.
     * @param total an integer representing the number or identifier to prefix the formatted name; must be non-negative.
     * @return a formatted string in the format "{total}: {rankTagColor}{username}" representing the player's details.
     */
    public static String getFormattedName(UUID uuid, int total) {
        String name;
        if (ParkManager.getPlayerUtil().getUserCache().containsKey(uuid)) {
            name = ParkManager.getPlayerUtil().getUserCache().get(uuid);
        } else {
            name = Core.getMongoHandler().uuidToUsername(uuid);
            ParkManager.getPlayerUtil().addToUserCache(uuid, name);
        }
        Rank rank = Core.getMongoHandler().getRank(uuid);
        return total + ": " + rank.getTagColor() + name;
    }

    /**
     * Updates the state of all leaderboard signs managed by the {@code LeaderboardManager}.
     *
     * <p>
     * This method first checks if there are any leaderboard signs to update. If no signs
     * are present, the method exits immediately. Otherwise, it performs the following steps:
     * </p>
     *
     * <ul>
     *   <li>Logs a message indicating the start of the update process.</li>
     *   <li>Iterates over all stored leaderboard signs retrieved via {@link #getSigns()}:
     *     <ul>
     *       <li>For each sign, calls its {@code update()} method to retrieve a {@code SignUpdate}
     *       object.</li>
     *       <li>If the returned {@code SignUpdate} is non-null, it is added to the internal list
     *       of sign updates.</li>
     *     </ul>
     *   </li>
     *   <li>Logs a message indicating the completion of the update process.</li>
     * </ul>
     *
     * <p>
     * This method ensures that the leaderboard signs reflect the most up-to-date data
     * and prepares sign update objects for further handling, such as applying changes
     * to the physical signs or saving updates to persistent storage.
     * </p>
     */
    public void update() {
        if (signs.isEmpty()) return;
        Core.logMessage("LeaderboardManager", "Updating ride counter leaderboards...");
        getSigns().forEach(sign -> {
            SignUpdate update = sign.update();
            if (update != null) signUpdates.add(update);
        });
        Core.logMessage("LeaderboardManager", "Finished updating ride counter leaderboards!");
    }

    /**
     * Persists the current state of leaderboard signs into a file.
     * <p>
     * This method converts all {@code LeaderboardSign} objects from the {@code signs} collection
     * into a JSON array and saves it to the file system. It utilizes the file utility subsystem
     * for writing the serialized JSON data to the designated file.
     * </p>
     *
     * <p><b>Process Overview:</b></p>
     * <lu>
     *   <li>Iterates over each {@code LeaderboardSign} in the {@code signs} collection.</li>
     *   <li>Converts each {@code LeaderboardSign} into a JSON object and adds it to a JSON array.</li>
     *   <li>Attempts to write the JSON array to the file via the leaderboard file subsystem.</li>
     *   <li>Logs an error message and prints the stack trace if an {@code IOException} occurs during the write process.</li>
     * </lu>
     *
     * <p><b>Exceptions:</b></p>
     * <lu>
     *   <li>If an {@code IOException} is thrown during writing, it will be caught and handled
     *       with a log message and stack trace output to assist with debugging.</li>
     * </lu>
     */
    public void saveToFile() {
        JsonArray array = new JsonArray();
        for (LeaderboardSign sign : signs) {
            array.add(sign.toJsonObject());
        }
        try {
            ParkManager.getFileUtil().getSubsystem("leaderboard").writeFileContents("leaderboards", array);
        } catch (IOException e) {
            Core.logMessage("LeaderboardManager", "There was an error writing to the LeaderboardManager config!");
            e.printStackTrace();
        }
    }
}
