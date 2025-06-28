package network.palace.parkmanager.outline;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * The {@code OutlineManager} class manages the creation, storage, and manipulation of outline-related sessions
 * and points. It provides methods to handle user sessions, persist point data to files, and retrieve or modify
 * point collections in memory. The configuration is backed by a JSON file stored in the file system via the
 * {@link FileUtil} class.
 *
 * <p>Key responsibilities of {@code OutlineManager} include:</p>
 * <ul>
 *     <li>Handling {@code OutlineSession} instances for unique {@link UUID}s.</li>
 *     <li>Managing a collection of geographic points ({@link Point}) that are persisted and restored from storage.</li>
 *     <li>Saving and loading data through the registered file subsystem specific to "outline".</li>
 * </ul>
 *
 * <p>When initializing an {@code OutlineManager}, it attempts to load point data from the file system. If loading
 * fails or the data file is missing, it creates a new configuration file and stores existing points into it by default.</p>
 *
 * <h3>Important Operations:</h3>
 * <ul>
 *     <li><b>Session Management:</b> Create, retrieve, and remove sessions for unique identifiers ({@link UUID}).</li>
 *     <li><b>Point Management:</b> Retrieve all points, search for specific points by name, add new points, or remove
 *         points by name.</li>
 *     <li><b>Persistence:</b> Save changes to the collection of points into persistent storage.</li>
 * </ul>
 *
 * <h3>Thread Safety:</h3>
 * <p>This class is not thread-safe. Concurrent access to its methods or modification of its shared collections
 * should be externally synchronized if required.</p>
 */
public class OutlineManager {
    /**
     * A map that associates {@link UUID} keys with their corresponding {@link OutlineSession} values.
     * This map is used by the {@code OutlineManager} class to manage active outline sessions for unique users.
     *
     * <p>Key characteristics:
     * <ul>
     *   <li>Each {@code UUID} is intended to represent a unique identifier for a player or session.</li>
     *   <li>Each {@link OutlineSession} contains session-specific data such as the player's current session point,
     *       type of block being outlined, and undo states for modifications.</li>
     *   <li>Critical operations such as adding, retrieving, or removing sessions rely on this map.</li>
     * </ul>
     */
    private HashMap<UUID, OutlineSession> map = new HashMap<>();
    /**
     * <p>Stores a list of {@link Point} objects. This list represents the collection of points
     * managed by the {@code OutlineManager}.</p>
     *
     * <p>The {@link Point} objects in the list are used to define various geometric locations
     * and properties relevant to the outline operations provided by the {@code OutlineManager}
     * class. Each {@link Point} contains information such as its name, X-coordinate, and Z-coordinate.</p>
     *
     * <p>This field is initialized as an empty {@link ArrayList} and is modified by methods
     * such as {@code addPoint(Point)} and {@code removePoint(String)}, among others.</p>
     *
     * <ul>
     *   <li>It is private and can only be accessed directly within the {@code OutlineManager} class.</li>
     *   <li>Access to modify or retrieve the points is provided through public methods like
     *   {@code getPoints()}, {@code addPoint(Point)}, and {@code removePoint(String)}.</li>
     * </ul>
     */
    private List<Point> points = new ArrayList<>();

    /**
     * Constructs an instance of {@code OutlineManager} and initializes its state by loading point data
     * from the file system, or creating an empty configuration if the data does not exist or is invalid.
     *
     * <p>The constructor performs the following actions:
     * <ul>
     *     <li>Registers a file subsystem named "outline" using {@code FileUtil's} {@code registerSubsystem} method.</li>
     *     <li>Attempts to load the JSON content of the subsystem's "points" file.</li>
     *     <li>If the file content is a valid JSON array:
     *         <ul>
     *             <li>Iterates over each element in the JSON array and parses them as {@code Point} objects.</li>
     *             <li>Stores the parsed {@code Point} objects in the {@code points} map.</li>
     *         </ul>
     *     </li>
     *     <li>If the file does not exist or the JSON content is invalid, invokes {@code saveToFile()} to create or
     *     reset the configuration file.</li>
     *     <li>Logs an error message and outputs the exception stack trace if an {@code IOException} occurs during reading.</li>
     * </ul>
     *
     * <p>This implementation relies on a properly configured {@code FileUtil} for file-based storage and
     * retrieval of subsystem data. The "outline" directory serves as the storage location for the manager's configuration.
     *
     * <p><b>Note:</b> The method logs messages using the {@code Core.logMessage()} utility for error handling.
     * This may require the environment in which the {@code OutlineManager} operates to support or configure the
     * logging infrastructure accordingly.
     */
    public OutlineManager() {
        FileUtil.FileSubsystem subsystem = ParkManager.getFileUtil().registerSubsystem("outline");
        try {
            JsonElement element = subsystem.getFileContents("points");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();
                    points.add(new Point(object.get("name").getAsString(), object.get("x").getAsInt(), object.get("z").getAsInt()));
                }
            } else {
                saveToFile();
            }
        } catch (IOException e) {
            Core.logMessage("OutlineManager", "There was an error loading the OutlineManager config!");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the {@link OutlineSession} associated with the specified {@link UUID}.
     * If no session is found, a new {@link OutlineSession} is created, associated with the provided UUID,
     * and stored in the internal mapping.
     *
     * <p>The {@link OutlineSession} represents a session tied to a unique user and can be
     * used for various operations related to outlining and point management.</p>
     *
     * @param uuid the unique identifier of the user for which the session is to be retrieved or created.
     *        Must not be {@code null}.
     * @return the existing or newly created {@link OutlineSession} associated with the given {@link UUID}.
     */
    public OutlineSession getSession(UUID uuid) {
        OutlineSession session = map.get(uuid);
        if (session == null) {
            session = new OutlineSession(uuid);
            map.put(uuid, session);
        }
        return session;
    }

    /**
     * Removes a session associated with the specified UUID from the internal map.
     *
     * <p>This method is used to remove an existing {@link OutlineSession} that corresponds
     * to the given UUID. If no session is associated with the UUID, this method does nothing.
     *
     * @param uuid the unique identifier of the session to be removed
     */
    public void removeSession(UUID uuid) {
        map.remove(uuid);
    }

    /**
     * Retrieves a list of points managed by the OutlineManager.
     * <p>
     * This method returns a copy of the current points list to prevent modification of
     * the original collection. Each {@link Point} in the list contains information about
     * its name, x-coordinate, and z-coordinate.
     *
     * @return a {@code List<Point>} containing all points managed by the OutlineManager.
     *         The returned list is a copy and changes to it will not affect the original
     *         list within the manager.
     */
    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }

    /**
     * Adds a {@link Point} to the internal list of points and triggers saving the updated configuration
     * to the file system.
     *
     * <p>This method ensures that any new {@link Point} added to the {@link OutlineManager} is
     * persisted in the configuration by invoking {@code saveToFile()}. The added point will
     * also be immediately available for retrieval and use in memory.</p>
     *
     * @param point the {@link Point} object to be added. It must not be null and
     *              should contain a valid name, X-coordinate, and Z-coordinate.
     */
    public void addPoint(Point point) {
        points.add(point);
        saveToFile();
    }

    /**
     * Removes a {@link Point} with the given name from the list of points.
     * <p>
     * This method searches through the current list of points to find a
     * {@link Point} whose name matches the specified name, ignoring case.
     * If such a point is found, it is removed from the list, and the updated
     * list is saved to a file.
     *
     * @param name The name of the point to be removed. It is case-insensitive.
     * @return {@code true} if a point with the specified name was successfully
     *         removed, otherwise {@code false}.
     */
    public boolean removePoint(String name) {
        boolean removed = false;
        for (Point p : getPoints()) {
            if (p.getName().equalsIgnoreCase(name)) {
                removed = points.remove(p);
            }
        }
        saveToFile();
        return removed;
    }

    /**
     * Saves the list of {@code Point} objects to a file in JSON format.
     * <p>
     * This method retrieves all points from the system, converts them to a JSON array, and writes
     * the data to the appropriate configuration file for the "outline" subsystem. Each {@link Point}
     * object is serialized into a JSON object with the following structure:
     * <lu>
     *   <li>{@code name}: The name of the point (String).</li>
     *   <li>{@code x}: The x-coordinate of the point (integer).</li>
     *   <li>{@code z}: The z-coordinate of the point (integer).</li>
     * </lu>
     * <p>
     * If an error occurs during the writing process, the error is logged, and the stack trace is printed.
     *
     * <h3>Relevant Exceptions:</h3>
     * <ul>
     *     <li>{@link IOException}: Thrown if the file writing process fails.</li>
     * </ul>
     */
    private void saveToFile() {
        JsonArray array = new JsonArray();
        for (Point p : getPoints()) {
            JsonObject object = new JsonObject();
            object.addProperty("name", p.getName());
            object.addProperty("x", p.getX());
            object.addProperty("z", p.getZ());
            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getSubsystem("outline").writeFileContents("points", array);
        } catch (IOException e) {
            Core.logMessage("OutlineManager", "There was an error writing to the OutlineManager config!");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a {@link Point} object from the list of stored points whose name matches
     * the given string, ignoring case sensitivity.
     *
     * <p>The method iterates through all the {@link Point} objects returned by {@code getPoints()},
     * and compares their names to the provided string. If a match is found, the corresponding
     * {@link Point} is returned. If no match is found, {@code null} is returned.
     *
     * @param s the name of the {@link Point} to search for; case-insensitive
     * @return the {@link Point} object whose name matches the given string, or {@code null} if no match is found
     */
    public Point getPoint(String s) {
        for (Point p : getPoints()) {
            if (p.getName().equalsIgnoreCase(s)) {
                return p;
            }
        }
        return null;
    }
}
