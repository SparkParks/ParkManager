package network.palace.parkmanager.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import network.palace.core.Core;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.ParkType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The {@code ParkUtil} class is responsible for managing and interacting with
 * park-related data and functionalities in the system. It handles initialization,
 * retrieval, addition, removal, and persistence of parks. Parks are defined by
 * a {@link ParkType}, {@link World}, and {@link ProtectedRegion}.
 *
 * <p>This utility provides functionalities to:</p>
 * <ul>
 *   <li>Load park data from a configuration file during initialization.</li>
 *   <li>Retrieve a list of all parks or a specific park by type or location.</li>
 *   <li>Add new parks or remove existing parks from the collection.</li>
 *   <li>Persist park data back to a configuration file.</li>
 * </ul>
 *
 * <p>Each park is uniquely identified by a {@link ParkType}. The parks are stored
 * internally in a {@link HashMap} that maps {@link ParkType} to {@link Park} objects.
 * The class performs necessary file operations to read and write park data, as well as
 * initializing other related managers after saving data.</p>
 *
 * <p><b>File Operations:</b></p>
 * <ul>
 *   <li>Configuration data is read from a JSON file named "parks".</li>
 *   <li>The file contains an array of JSON objects where each object represents a park.</li>
 *   <li>The file is updated whenever parks are added, removed, or modified.</li>
 * </ul>
 *
 * <p><b>Key Functionalities:</b></p>
 * <ul>
 *   <li>{@link #initialize()} - Loads park data from the configuration file.</li>
 *   <li>{@link #getParks()} - Retrieves a list of all registered parks.</li>
 *   <li>{@link #getPark(ParkType)} - Retrieves a park by its associated {@link ParkType}.</li>
 *   <li>{@link #getPark(Location)} - Retrieves a park based on a given {@link Location}.</li>
 *   <li>{@link #addPark(Park)} - Adds a new park and saves it to the configuration file.</li>
 *   <li>{@link #removePark(ParkType)} - Removes a park by its type and updates the configuration file.</li>
 *   <li>{@link #saveToFile()} - Persists the current park data to the configuration file and
 *       initializes related managers.</li>
 * </ul>
 *
 * <p><b>Usage Considerations:</b></p>
 * <ul>
 *   <li>Parks are internally represented by {@link Park} objects, which must have valid
 *       {@link ParkType}, {@link World}, and {@link ProtectedRegion} data.</li>
 *   <li>Regions are handled using the WorldGuard API, and errors in region definitions are
 *       logged but do not stop the initialization process.</li>
 *   <li>If any file operations fail, appropriate error messages are logged via the core logging system.</li>
 *   <li>The utility is heavily reliant on JSON file operations and APIs (such as WorldGuard and Bukkit).</li>
 * </ul>
 */
public class ParkUtil {
    /**
     * <p>A collection to store and manage parks, categorized by their {@link ParkType}.</p>
     *
     * <p>This {@code HashMap} serves as a core data structure for handling associations between {@link ParkType},
     * which denotes different types of parks or facilities, and their corresponding {@link Park} instances.</p>
     *
     * <p><b>Key Characteristics:</b></p>
     * <ul>
     *   <li>Uses {@link ParkType} as the key to ensure proper categorization of parks.</li>
     *   <li>Stores instances of {@link Park}, representing specific park details such as name, location, or features.</li>
     *   <li>Allows efficient retrieval, update, and management of parks based on their type.</li>
     * </ul>
     *
     * <p>This collection is integral to the functionality of the {@code ParkUtil} class, enabling operations
     * like the addition, retrieval, and removal of parks, as well as serialization for persistence.</p>
     *
     * <p><b>Usage:</b> The {@code parks} collection is initialized as an empty {@code HashMap} within
     * the class and is populated or modified via methods such as {@code initialize}, {@code addPark}, or {@code removePark}.</p>
     */
    private HashMap<ParkType, Park> parks = new HashMap<>();

    /**
     * Constructor for the ParkUtil class.
     *
     * <p>This constructor is responsible for initializing the ParkUtil object
     * and preparing its internal state. It invokes the {@code initialize()} method
     * to load and configure parks from the underlying data source.
     *
     * <p>Responsibilities of this constructor include:
     * <ul>
     *   <li>Setting up the internal mapping of parks.</li>
     *   <li>Calling {@code initialize()} to load park data definitions, such as
     *   park types, regions, and corresponding worlds.</li>
     *   <li>Ensuring any necessary preconditions for park management are met after
     *   instantiation.</li>
     * </ul>
     *
     * <p>This class is intended for managing parks associated with specified
     * types, regions, and worlds, and for handling proper initialization when the
     * ParkUtil object is created.
     */
    public ParkUtil() {
        initialize();
    }

    /**
     * Initializes the {@code parks} collection by loading park data from a configuration file in the file subsystem.
     * <p>
     * The method attempts to read a JSON array from a file named "parks". Each array entry should be a JSON object
     * representing a park with its unique identifier, associated world, and a WorldGuard region name. It creates
     * and stores {@link Park} instances in the {@code parks} collection based on the configuration data.
     * <p>
     * If a JSON object is missing fields or contains incorrect or invalid data (e.g., invalid region name or world),
     * it logs an error message and skips the invalid park entry. Upon successful initialization, the configuration
     * is saved back to the file, and a summary of loaded parks is logged.
     *
     * <p>The JSON structure of the file is expected to be as follows:</p>
     * <pre>
     * [
     *     {
     *         "id": "<park_id>",
     *         "world": "<world_name>",
     *         "region": "<region_name>"
     *     },
     *     ...
     * ]
     * </pre>
     *
     * <p><b>Exceptions</b></p>
     * <ul>
     *     <li>If the "parks" file or its contents cannot be read, an {@link IOException} is caught, and an error
     *     message is logged.</li>
     *     <li>Invalid region names or missing configurations for parks are handled, and relevant error messages
     *     are logged for each invalid park entry.</li>
     * </ul>
     *
     * <p><b>Log Messages:</b></p>
     * <ul>
     *     <li>On successful load, logs the number of valid parks loaded.</li>
     *     <li>Logs specific error messages for invalid or missing data in individual park entries.</li>
     *     <li>Logs an error message if the configuration file cannot be loaded due to an {@link IOException}.</li>
     * </ul>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *     <li>Valid parks are created and stored in the {@code parks} map using their {@link ParkType} as the key.</li>
     *     <li>The configuration is saved after successful initialization using the {@link #saveToFile()} method.</li>
     * </ul>
     */
    public void initialize() {
        FileUtil.FileSubsystem subsystem = ParkManager.getFileUtil().getRootSubsystem();
        try {
            JsonElement element = subsystem.getFileContents("parks");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();

                    String id = object.get("id").getAsString();
                    World world = Bukkit.getWorld(object.get("world").getAsString());

                    ProtectedRegion region;
                    try {
                        region = WorldGuardPlugin.inst()
                                .getRegionManager(world)
                                .getRegion(object.get("region").getAsString());
                        if (region == null) {
                            throw new NullPointerException();
                        }
                    } catch (Exception e) {
                        Core.logMessage("ParkUtil", "There was an error loading the '" + id + "' park: Invalid region " +
                                object.get("region").getAsString() + " in world " + object.get("world").getAsString());
                        continue;
                    }

                    ParkType type = ParkType.fromString(id.toUpperCase());
                    parks.put(type, new Park(type, world, region));
                }
            }
            saveToFile();
            Core.logMessage("ParkUtil", "Loaded " + parks.size() + " park" + TextUtil.pluralize(parks.size()) + "!");
        } catch (IOException e) {
            Core.logMessage("ParkUtil", "There was an error loading the ParkUtil config!");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a list of all parks currently available in the system.
     *
     * <p>This method provides access to a collection of {@link Park} objects managed within the system.
     * Each park in the returned list represents a distinct entity, defined by its {@link ParkType},
     * associated {@link World}, and {@link ProtectedRegion} information.</p>
     *
     * <p>The returned list offers an unmodifiable snapshot of the parks, ensuring the integrity
     * of the internal data. Any modifications to the parks must be performed through designated
     * methods like {@code addPark(Park)} or {@code removePark(ParkType)}.</p>
     *
     * <p>Key characteristics of the returned list:</p>
     * <ul>
     *   <li>Contains all parks currently registered in the system.</li>
     *   <li>Encapsulates individual park details, including type, location, and boundaries.</li>
     *   <li>Provides a mechanism to iterate over or query the available parks.</li>
     * </ul>
     *
     * @return A {@link List} of {@link Park} objects representing all registered parks.
     */
    public List<Park> getParks() {
        return new ArrayList<>(parks.values());
    }

    /**
     * Retrieves a {@link Park} instance based on the specified {@link ParkType}.
     *
     * <p>The method iterates through the available list of parks and returns the park
     * whose identifier matches the provided {@link ParkType}. If no park with the
     * specified identifier is found, the method returns {@code null}.
     *
     * @param id the {@link ParkType} representing the type of park to retrieve.
     *           This parameter is used to identify the specific park within the collection.
     * @return the {@link Park} instance corresponding to the given {@link ParkType}, or {@code null}
     *         if no matching park is found.
     */
    public Park getPark(ParkType id) {
        for (Park park : getParks()) {
            if (park.getId().equals(id)) {
                return park;
            }
        }
        return null;
    }

    /**
     * Retrieves the {@link Park} corresponding to the given {@link Location}.
     *
     * <p>This method iterates through all parks and checks whether the provided {@code Location}
     * lies within the boundaries of a park's associated {@link ProtectedRegion}. It considers
     * both the world association and region containment to determine the correct park. If the
     * {@link ProtectedRegion} of the park has an ID of "__global__", it also matches the park
     * as long as it shares the same world as the provided {@link Location}.
     *
     * <p>If no matching park is found, {@code null} is returned.
     *
     * @param loc The {@link Location} to check, used to determine which park (if any)
     *            contains the specified location. The location includes world and
     *            coordinate details for precise matching.
     * @return The {@link Park} located at the given {@link Location}, or
     *         {@code null} if no park is found.
     */
    public Park getPark(Location loc) {
        for (Park park : getParks()) {
            if (!park.getWorld().getUID().equals(loc.getWorld().getUID())) continue;
            ProtectedRegion region = park.getRegion();
            if (region.getId().equals("__global__")) return park;
            if (region.contains(loc.getBlockX(), region.getMinimumPoint().getBlockY(), loc.getBlockZ())) return park;
        }
        return null;
    }

    /**
     * Adds a new park to the internal collection and persists the updated data to a file.
     *
     * <p>This method adds the specified {@link Park} instance to the internal mapping of parks,
     * associating it with its unique {@code id}. After adding the park, the updated park data
     * is saved to a file for long-term storage.
     *
     * @param park The {@link Park} object to be added. It must have a valid {@code id} and should represent
     *             a unique park that is not already present in the existing collection.
     */
    public void addPark(Park park) {
        parks.put(park.getId(), park);
        saveToFile();
    }

    /**
     * Removes a park from the system by its {@link ParkType} identifier.
     *
     * <p>This method removes the specified park from the internal storage of parks
     * and persists the changes to disk by invoking the {@code saveToFile()} method.
     * The park is identified using its {@link ParkType}, which should correspond
     * to one of the predefined park types in the system.</p>
     *
     * @param id the {@link ParkType} identifier of the park to be removed.
     *           This parameter specifies which park to remove from the system.
     *
     * @return {@code true} if the park was successfully removed and the change was saved.
     */
    public boolean removePark(ParkType id) {
        parks.remove(id);
        saveToFile();
        return true;
    }

    /**
     * Saves the current state of all registered parks to a file in JSON format.
     *
     * <p>This method iterates through all parks stored in the {@code parks} collection,
     * converting their data into a JSON representation that includes the following properties:
     * <ul>
     *   <li><b>id</b>: The identifier of the park, retrieved using {@link Park#getId()}.</li>
     *   <li><b>world</b>: The name of the world where the park is located, retrieved using {@link Park#getWorld()}.</li>
     *   <li><b>region</b>: The identifier of the protected region within the park, retrieved using {@link Park#getRegion()}.</li>
     * </ul>
     *
     * <p>The JSON data is then written to a file named {@code parks.json} through the application's file utility system.
     *
     * <p><b>Error Handling:</b>
     * <ul>
     *   <li>If an {@code IOException} occurs while writing to the file, an error message is logged, and the stack trace is printed.</li>
     *   <li>If a {@code NullPointerException} occurs during the initialization of attraction, food, queue, or shop managers, the exception is silently ignored.</li>
     * </ul>
     *
     * <p><b>Post-Save Initialization:</b>
     * After saving to the file, the method initializes the following subsystems:
     * <ul>
     *   <li>Attraction Manager</li>
     *   <li>Food Manager</li>
     *   <li>Queue Manager</li>
     *   <li>Shop Manager</li>
     * </ul>
     */
    public void saveToFile() {
        JsonArray array = new JsonArray();
        for (Park park : parks.values()) {
            JsonObject object = new JsonObject();
            object.addProperty("id", park.getId().name());
            object.addProperty("world", park.getWorld().getName());
            object.addProperty("region", park.getRegion().getId());
            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getRootSubsystem().writeFileContents("parks", array);
        } catch (IOException e) {
            Core.logMessage("ParkUtil", "There was an error writing to parks.json!");
            e.printStackTrace();
        }
        try {
            ParkManager.getAttractionManager().initialize();
            ParkManager.getFoodManager().initialize();
            ParkManager.getQueueManager().initialize();
            ParkManager.getShopManager().initialize();
        } catch (NullPointerException ignored) {
        }
    }
}
