package network.palace.parkmanager.attractions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.AttractionCategory;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The {@code AttractionManager} class is responsible for managing the attractions
 * within a theme park system. It provides functionality for initialization,
 * retrieval, addition, removal, and persistence of attractions.
 *
 * <p>The system is organized to handle multiple theme parks, with each attraction
 * linked to a specific park. The class supports operations to manage the attraction
 * catalog by reading and writing data to persistent storage, as well as filtering
 * based on park associations.
 *
 * <p><b>Key Responsibilities:</b>
 * <ul>
 *   <li>Initializing and loading attractions from configuration files.</li>
 *   <li>Providing access to attractions filtered by park or ID.</li>
 *   <li>Adding or removing attractions and updating the associated data store.</li>
 *   <li>Maintaining compatibility with park utilities and the file subsystem.</li>
 * </ul>
 *
 * <p><b>Features:</b>
 * <ul>
 *   <li>Ability to serialize and persist attraction data in JSON format for
 *       long-term storage.</li>
 *   <li>Integration with {@code ParkManager} for park-related metadata and file I/O.</li>
 *   <li>Structured data retrieval for categories, queue linkage, and attraction details.</li>
 * </ul>
 *
 * <p>This class is designed to work seamlessly with other components of the park
 * management system, ensuring efficient and reliable management of attractions.
 */
public class AttractionManager {
    /**
     * <p>A list containing instances of {@link Attraction} objects managed
     * by the {@code AttractionManager}.</p>
     *
     * <p>This list serves as the primary data structure for storing
     * attractions associated with specific parks. It is initialized as an
     * empty {@link ArrayList} and is populated or modified through various
     * methods within the {@code AttractionManager} class.</p>
     *
     * <p>Each {@link Attraction} encapsulates information pertaining
     * to an individual attraction within a park, and this list provides
     * storage and quick access for retrieval, addition, and removal tasks.</p>
     *
     * <p><b>Note:</b> This list does not directly handle any synchronization
     * and must be used with appropriate caution in a multithreaded environment.</p>
     *
     * <p>Key operations involving this list:</p>
     * <ul>
     *   <li>Adding a new attraction using {@code addAttraction(Attraction attraction)}</li>
     *   <li>Retrieving attractions based on criteria through methods like
     *       {@code getAttractions(ParkType park)}</li>
     *   <li>Removing specific attractions using
     *       {@code removeAttraction(String id, ParkType park)}</li>
     * </ul>
     */
    private List<Attraction> attractions = new ArrayList<>();

    /**
     * Default constructor for the <code>AttractionManager</code> class.
     * <p>
     * This constructor initializes the <code>AttractionManager</code> by invoking
     * the {@link #initialize()} method. The initialization process involves
     * clearing existing attractions, retrieving and registering necessary file subsystems,
     * and loading attraction data from configuration files for each park.
     *
     * <p>Behavior details:
     * <ul>
     *   <li>Clears the current attractions data to prepare for re-initialization.</li>
     *   <li>Ensures the required file subsystem for attractions is registered.</li>
     *   <li>Processes configuration files to load and populate the attractions list
     *       with parsed data, ensuring compatibility with park and attraction file structures.</li>
     *   <li>Logs success or failure messages for each park's data loading process.</li>
     *   <li>Saves the updated configuration to the file system.</li>
     * </ul>
     *
     * <p>This constructor is typically invoked during the instantiation of the <code>AttractionManager</code> class
     * to set up and manage the operations related to attractions.
     *
     * @see #initialize()
     */
    public AttractionManager() {
        initialize();
    }

    /**
     * Initializes the data and state for the <code>AttractionManager</code>.
     * <p>
     * This method is responsible for performing a series of operations necessary to prepare
     * attractions data and maintain consistency across the system. The key steps include:
     * </p>
     *
     * <ul>
     *   <li>Clearing the existing list of attractions to ensure outdated data is removed.</li>
     *   <li>Ensuring the file subsystem for attractions is appropriately registered in the
     *   {@link FileUtil} system, which manages storage and retrieval operations for attractions.</li>
     *   <li>Iterating over all parks retrieved from the <code>ParkUtil</code> utility and loading
     *   corresponding attraction data from configuration files, if available.</li>
     *   <li>Parsing JSON data for each attraction, including categories, linked queues, attraction
     *   IDs, warps, item details, descriptions, and other attributes, and then creating appropriate
     *   {@link Attraction} objects.</li>
     *   <li>Adding the loaded attractions to the internal attractions list and logging the
     *   operation results (e.g., number of attractions loaded).</li>
     *   <li>Logging error messages in case of any issues, such as failure to retrieve or parse
     *   attraction configuration files for specific parks.</li>
     *   <li>Saving the updated attractions configuration to the file system by invoking the
     *   {@link #saveToFile()} method.</li>
     * </ul>
     *
     * <p>This method is typically invoked during the initialization or re-initialization of attraction
     * data and is essential for ensuring that attractions are correctly loaded, managed, and updated
     * for all parks in the system.</p>
     */
    public void initialize() {
        attractions.clear();
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("attraction")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("attraction");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("attraction");
        }
        for (Park park : ParkManager.getParkUtil().getParks()) {
            int c = 0;
            try {
                JsonElement element = subsystem.getFileContents(park.getId().name().toLowerCase());
                if (element.isJsonArray()) {
                    JsonArray array = element.getAsJsonArray();
                    for (JsonElement entry : array) {
                        JsonObject object = entry.getAsJsonObject();

                        JsonArray categories = object.getAsJsonArray("categories");
                        List<AttractionCategory> categoryList = new ArrayList<>();
                        categories.forEach(e -> categoryList.add(AttractionCategory.fromString(e.getAsString())));
                        UUID linkedQueue;
                        if (object.has("linked-queue")) {
                            linkedQueue = UUID.fromString(object.get("linked-queue").getAsString());
                        } else {
                            linkedQueue = null;
                        }
                        String id;
                        if (object.has("id")) {
                            id = object.get("id").getAsString();
                        } else {
                            id = object.get("warp").getAsString().toLowerCase();
                        }
                        attractions.add(new Attraction(id, park.getId(), object.get("name").getAsString(), object.get("warp").getAsString(),
                                object.get("description").getAsString(), categoryList, object.get("open").getAsBoolean(),
                                ItemUtil.getItemFromJsonNew(object.get("item").getAsJsonObject().toString()), linkedQueue));
                        c++;
                    }
                }
                Core.logMessage("AttractionManager", "Loaded " + c + " attraction" + TextUtil.pluralize(c) + " for park " + park.getId().getTitle() + "!");
            } catch (IOException e) {
                Core.logMessage("AttractionManager", "There was an error loading the AttractionManager config for park " + park.getId().getTitle() + "!");
                e.printStackTrace();
            }
        }
        saveToFile();
    }

    /**
     * Retrieves a list of attractions that belong to the specified park.
     * <p>
     * This method filters and collects the attractions associated with the provided
     * <code>ParkType</code>. It checks each attraction's park property and returns
     * only those that match the given park instance.
     * </p>
     *
     * @param park the <code>ParkType</code> instance used to filter attractions.
     *             Only attractions belonging to this park will be included.
     * @return a <code>List</code> of <code>Attraction</code> objects associated
     *         with the specified park. If no attractions match the given park,
     *         an empty list is returned.
     */
    public List<Attraction> getAttractions(ParkType park) {
        return attractions.stream().filter(attr -> attr.getPark().equals(park)).collect(Collectors.toList());
    }

    /**
     * Retrieves an attraction based on its unique identifier within a specific park type.
     * <p>
     * This method iterates through the list of attractions for the specified park and
     * returns the attraction matching the provided identifier. If no matching attraction
     * is found, the method returns <code>null</code>.
     * </p>
     *
     * @param id The unique identifier for the attraction to be retrieved.
     *           The identifier should match the ID of one of the attractions within
     *           the specified park type.
     * @param park The park type where the attraction is located. This parameter determines
     *             which list of attractions will be searched.
     *
     * @return The {@link Attraction} object corresponding to the specified ID within
     *         the given park type. If no matching attraction is found,
     *         the method returns <code>null</code>.
     */
    public Attraction getAttraction(String id, ParkType park) {
        for (Attraction attraction : getAttractions(park)) {
            if (attraction.getId().equals(id)) {
                return attraction;
            }
        }
        return null;
    }

    /**
     * Adds a new attraction to the list of managed attractions and saves the updated list to the file system.
     * <p>
     * This method is responsible for adding an instance of {@link Attraction} to the internal data collection
     * of attractions and subsequently triggering the {@link #saveToFile()} method to persist the changes.
     * It ensures that the addition is reflected both in memory and in the saved configuration file.
     * </p>
     *
     * @param attraction The {@link Attraction} object to be added to the system. This parameter represents
     *                   the new attraction to be included in the list of managed attractions.
     */
    public void addAttraction(Attraction attraction) {
        attractions.add(attraction);
        saveToFile();
    }

    /**
     * Removes an attraction identified by its unique ID from the specified park.
     * <p>
     * This method searches for an attraction with the given ID within the specified
     * park's data. If the attraction is found, it is removed from the list of
     * attractions, the changes are persisted by invoking {@link #saveToFile()}, and
     * the method returns <code>true</code>. If no matching attraction is found, the
     * method returns <code>false</code>.
     * </p>
     *
     * @param id the unique identifier of the attraction to be removed. This ID
     *           should correspond to an existing attraction within the specified park.
     * @param park the <code>ParkType</code> instance representing the park where
     *             the attraction is located. Only attractions associated with this
     *             park are considered for removal.
     * @return <code>true</code> if the attraction was successfully removed;
     *         <code>false</code> if no matching attraction is found.
     */
    public boolean removeAttraction(String id, ParkType park) {
        Attraction attraction = getAttraction(id, park);
        if (attraction == null) return false;
        attractions.remove(attraction);
        saveToFile();
        return true;
    }

    /**
     * Saves the current state of attractions to their respective park configuration files.
     * <p>
     * This method organizes and serializes all attractions associated with each park into
     * a JSON format, ensuring that attraction details are properly updated in the file system.
     * It sorts the attractions alphabetically by their name (ignoring color codes), groups them
     * by park, and handles the file writing process for each park's configuration file.
     * </p>
     *
     * <p>Key steps performed by this method:</p>
     * <ul>
     *   <li>Sorts the list of attractions alphabetically by their name, ignoring color codes.</li>
     *   <li>Iterates through all parks retrieved from the {@link ParkManager#getParkUtil()}.</li>
     *   <li>Filters the attractions belonging to each park and converts their details into
     *       a JSON object format, including:
     *     <ul>
     *       <li>ID, name, warp, and description of the attraction.</li>
     *       <li>Categories associated with the attraction, added as a JSON array.</li>
     *       <li>State of the attraction (open or closed).</li>
     *       <li>Item details represented in JSON using {@link ItemUtil#getJsonFromItemNew}.</li>
     *       <li>Linked queue information, if applicable.</li>
     *     </ul>
     *   </li>
     *   <li>Writes the JSON data for all attractions of a park to the corresponding configuration file
     *       within the "attraction" subsystem, using the park's identifier as the filename.</li>
     *   <li>Handles potential {@code IOException}s during the file writing process and logs errors
     *       if file writing fails.</li>
     * </ul>
     *
     * <p>This method ensures that attraction data is consistently stored for each park and can
     * be retrieved later as needed. It identifies and logs any errors encountered during the
     * file writing process, providing necessary debugging information.</p>
     */
    public void saveToFile() {
        attractions.sort(Comparator.comparing(o -> ChatColor.stripColor(o.getName().toLowerCase())));
        for (Park park : ParkManager.getParkUtil().getParks()) {
            JsonArray array = new JsonArray();
            attractions.stream().filter(attr -> attr.getPark().equals(park.getId())).forEach(attraction -> {
                JsonObject object = new JsonObject();
                object.addProperty("id", attraction.getId());
                object.addProperty("name", attraction.getName());
                object.addProperty("warp", attraction.getWarp());
                object.addProperty("description", attraction.getDescription());

                JsonArray categories = new JsonArray();
                attraction.getCategories().forEach(c -> categories.add(c.getShortName()));
                object.add("categories", categories);

                object.addProperty("open", attraction.isOpen());
                object.add("item", ItemUtil.getJsonFromItemNew(attraction.getItem()));

                if (attraction.getLinkedQueue() != null) {
                    object.addProperty("linked-queue", attraction.getLinkedQueue().toString());
                }
                array.add(object);
            });
            try {
                ParkManager.getFileUtil().getSubsystem("attraction").writeFileContents(park.getId().name().toLowerCase(), array);
            } catch (IOException e) {
                Core.logMessage("AttractionManager", "There was an error writing to the AttractionManager config: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
