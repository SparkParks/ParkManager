package network.palace.parkmanager.food;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code FoodManager} class is responsible for managing food location data associated with parks.
 * This includes loading, saving, adding, removing, and retrieving food locations for parks.
 * The class interacts with file subsystems for persistent storage and operates on a list of {@code FoodLocation} objects.
 *
 * <p>Main features of the class include:</p>
 * <ul>
 *     <li>Load food location data from external storage.</li>
 *     <li>Save food location data to persistent storage.</li>
 *     <li>Add new food locations to the list.</li>
 *     <li>Remove existing food locations by their unique ID and park.</li>
 *     <li>Retrieve food location data for a specific park or an individual food location by ID.</li>
 * </ul>
 *
 * <p>The food location data for each park is loaded during initialization and saved after any modifications.</p>
 *
 * <p><b>Methods Overview:</b></p>
 * <ul>
 *     <li>{@link #initialize()}: Clears existing locations and reloads food location data from the storage system.</li>
 *     <li>{@link #getFoodLocations(ParkType)}: Retrieves a list of food locations specifically linked to a park type.</li>
 *     <li>{@link #getFoodLocation(String, ParkType)}: Retrieves a single food location by its ID and associated park type.</li>
 *     <li>{@link #addFoodLocation(FoodLocation)}: Adds a new food location to the list and updates persistent storage.</li>
 *     <li>{@link #removeFoodLocation(String, ParkType)}: Removes an existing food location by ID and park type from the list and updates storage.</li>
 *     <li>{@link #saveToFile()}: Saves the current state of the food locations to the persistent storage system.</li>
 * </ul>
 */
public class FoodManager {
    /**
     * A collection of {@link FoodLocation} objects managed by the {@link FoodManager} class.
     * <p>
     * This field serves as the primary storage for all the food locations available within the park or resort system. Each entry
     * in the list corresponds to a unique food location defined by its attributes, such as its identifier, park association, display name,
     * warp location, and corresponding {@link ItemStack}.
     * </p>
     *
     * <h3>Key Characteristics:</h3>
     * <ul>
     *     <li>
     *         Used to store, manage, and retrieve instances of {@link FoodLocation} within
     *         the {@link FoodManager} class.
     *     </li>
     *     <li>
     *         The content of the list is dynamically updated through methods such as
     *         {@code initialize()}, {@code addFoodLocation()}, and {@code removeFoodLocation(String, ParkType)}.
     *     </li>
     *     <li>
     *         Acts as the underlying data structure for operations involving food locations,
     *         including filtering by park type, querying by identifier, and saving to file.
     *     </li>
     * </ul>
     *
     * <h3>Usage:</h3>
     * <ul>
     *     <li>
     *         Supports retrieval of food locations via {@link FoodManager#getFoodLocations(ParkType)}
     *         and {@link FoodManager#getFoodLocation(String, ParkType)}.
     *     </li>
     *     <li>
     *         Handles CRUD (Create, Read, Update, Delete) operations for storing and
     *         modifying food location data dynamically during runtime.
     *     </li>
     *     <li>
     *         Ensures data persistence by being saved and loaded through the {@code saveToFile()}
     *         and {@code initialize()} methods, respectively.
     *     </li>
     * </ul>
     *
     * <h3>Relevant Operations:</h3>
     * <ul>
     *     <li>Adding new food locations to the list using {@link FoodManager#addFoodLocation(FoodLocation)}.</li>
     *     <li>Removing food locations based on identifiers and park association via {@link FoodManager#removeFoodLocation(String, ParkType)}.</li>
     *     <li>Retrieving all or filtered food locations via appropriate getter methods.</li>
     *     <li>Saving the complete list of food locations to a file for persistence.</li>
     * </ul>
     *
     * <p>
     * This field is initially populated through the {@code initialize()} method, where
     * data is loaded from configuration files. It is then dynamically updated during
     * runtime as new food locations are added or existing ones are modified or removed.
     * </p>
     */
    private List<FoodLocation> foodLocations = new ArrayList<>();

    /**
     * Constructs a new instance of the {@code FoodManager} class.
     *
     * <p>
     * This constructor initializes the {@code FoodManager} by invoking the {@link #initialize()} method
     * to set up and populate the necessary data structure for managing food locations. The initialization process
     * ensures that all configuration files related to food locations are loaded and processed, enabling
     * the {@code FoodManager} to manage, retrieve, and modify food locations within the park system effectively.
     * </p>
     *
     * <h3>Primary Responsibilities:</h3>
     * <ul>
     *     <li>Initializes the food locations data by clearing any existing entries.</li>
     *     <li>Registers and prepares the necessary file subsystems for loading configuration data related to food locations.</li>
     *     <li>Loads food locations for each park, parsing configuration data and instantiating {@link FoodLocation} objects.</li>
     *     <li>Performs error handling for missing or invalid configuration files.</li>
     * </ul>
     *
     * <p>
     * The {@code FoodManager} serves as the central point for managing food locations across multiple parks or resorts.
     * It interfaces with configuration files, park management systems, and other components to ensure consistency and accessibility
     * of food location data.
     * </p>
     */
    public FoodManager() {
        initialize();
    }

    /**
     * Initializes the food locations for all parks and loads corresponding data from the file subsystem.
     * <p>
     * This method performs the following actions:
     * <ul>
     *     <li>Clears the existing list of food locations.</li>
     *     <li>Checks if the "food" subsystem is registered in the file subsystem. If not, it registers a new "food" subsystem.</li>
     *     <li>Iterates through all parks obtained from the park utility and attempts to load food location data from the subsystem.</li>
     *     <li>For each park, parses the JSON data associated with the park's identifier and adds entries for food locations.</li>
     *     <li>Logs the number of successfully loaded food locations for each park.</li>
     *     <li>Handles any file I/O or JSON parsing exceptions by logging appropriate error messages.</li>
     * </ul>
     * After loading the data, this method also invokes {@code saveToFile()} to persist any changes.
     */
    public void initialize() {
        foodLocations.clear();
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("food")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("food");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("food");
        }
        for (Park park : ParkManager.getParkUtil().getParks()) {
            try {
                JsonElement element = subsystem.getFileContents(park.getId().name().toLowerCase());
                if (element.isJsonArray()) {
                    JsonArray array = element.getAsJsonArray();
                    for (JsonElement entry : array) {
                        JsonObject object = entry.getAsJsonObject();
                        String id;
                        if (object.has("id")) {
                            id = object.get("id").getAsString();
                        } else {
                            id = object.get("warp").getAsString().toLowerCase();
                        }
                        foodLocations.add(new FoodLocation(id, park.getId(), object.get("name").getAsString(),
                                object.get("warp").getAsString(), ItemUtil.getItemFromJsonNew(object.get("item").getAsJsonObject().toString())));
                    }
                }
                Core.logMessage("FoodManager", "Loaded " + foodLocations.size() + " food location" + TextUtil.pluralize(foodLocations.size()) + " for park " + park.getId().getTitle() + "!");
            } catch (IOException e) {
                Core.logMessage("FoodManager", "There was an error loading the FoodManager config for park " + park.getId().getTitle() + "!");
                e.printStackTrace();
            }
        }
        saveToFile();
    }

    /**
     * Retrieves a list of food locations that are associated with the specified park.
     * <p>
     * This method filters the {@link FoodLocation}s and returns only those that belong
     * to the provided {@link ParkType}. The filtering is based on the park attribute
     * of each food location.
     * </p>
     *
     * @param park The {@link ParkType} representing the park or resort for which food locations need to be retrieved.
     *             It determines which food locations belong to the specified park.
     * @return A {@link List} of {@link FoodLocation} objects that are associated with the specified park.
     *         If no food locations are found for the given park, an empty list is returned.
     */
    public List<FoodLocation> getFoodLocations(ParkType park) {
        return foodLocations.stream().filter(food -> food.getPark().equals(park)).collect(Collectors.toList());
    }

    /**
     * Retrieves a specific {@link FoodLocation} by its unique identifier within a specified park.
     * <p>
     * This method searches through available food locations in the provided park and
     * returns the food location matching the given identifier. If no match is found,
     * the method returns {@code null}.
     * </p>
     *
     * @param id The unique identifier of the food location to retrieve.
     *           <ul>
     *               <li>Must be a non-null and non-empty string.</li>
     *               <li>Represents the primary key for the {@link FoodLocation}.</li>
     *           </ul>
     * @param park The {@link ParkType} representing the park to search in.
     *             <ul>
     *                 <li>Used to filter the food locations within the associated park.</li>
     *                 <li>Determines the context of the search, restricting it to a specific park.</li>
     *             </ul>
     * @return The {@link FoodLocation} with the specified ID within the given park, or {@code null}
     *         if no matching food location is found.
     */
    public FoodLocation getFoodLocation(String id, ParkType park) {
        for (FoodLocation food : getFoodLocations(park)) {
            if (food.getId().equals(id)) {
                return food;
            }
        }
        return null;
    }

    /**
     * Adds a new food location to the list of managed food locations and persists the changes to a file.
     *
     * <p>
     * This method takes a {@link FoodLocation} object as input and adds it to the existing
     * collection of food locations managed by the {@code FoodManager}. After adding, the method
     * invokes {@link #saveToFile()} to ensure that the updated list is saved to the file system.
     * </p>
     *
     * @param food The {@link FoodLocation} object to be added.
     *             <ul>
     *                 <li>It should contain valid details such as name, location, and park association.</li>
     *                 <li>Must be non-null to avoid exceptions during the add operation.</li>
     *             </ul>
     */
    public void addFoodLocation(FoodLocation food) {
        foodLocations.add(food);
        saveToFile();
    }

    /**
     * Removes a food location from the system based on its unique identifier and associated park.
     *
     * <p>This method searches for the specified food location within the given park. If found,
     * it removes the food location from the internal data structure and saves the updated state
     * to the file system. If the food location does not exist, no changes are made, and the method
     * returns {@code false}.</p>
     *
     * @param id   The unique identifier of the {@link FoodLocation} to remove.
     *             <ul>
     *                 <li>Must be a non-null, non-empty string.</li>
     *                 <li>Represents the primary key of the food location.</li>
     *             </ul>
     * @param park The {@link ParkType} representing the park where the removal operation is to be performed.
     *             <ul>
     *                 <li>Restricts the search scope to the specified park's context.</li>
     *             </ul>
     * @return {@code true} if the food location was successfully found and removed;
     *         {@code false} otherwise (if not found or invalid parameters).
     */
    public boolean removeFoodLocation(String id, ParkType park) {
        FoodLocation food = getFoodLocation(id, park);
        if (food == null) return false;
        foodLocations.remove(food);
        saveToFile();
        return true;
    }

    /**
     * Persists the current list of food locations associated with each park into a structured JSON format,
     * saving it within the file subsystem for future retrieval.
     *
     * <p>This method performs the following actions:</p>
     * <ul>
     *     <li>Sorts the food locations alphabetically by their cleaned names (case-insensitive).</li>
     *     <li>Iterates through all parks managed by the application.</li>
     *     <li>Filters and organizes food locations by their associated park IDs.</li>
     *     <li>Constructs a JSON array for each park, where each food location is represented as a JSON object
     *         containing its properties such as ID, name, warp information, and item configuration.</li>
     *     <li>Writes the generated JSON array for each park into the "food" file subsystem.</li>
     * </ul>
     *
     * <p>If an error occurs during file writing, an error message is logged detailing the issue.</p>
     *
     * <h3>Operational Notes:</h3>
     * <ul>
     *     <li>The sorting process ensures consistent ordering of food locations, which aids in file readability and debugging.</li>
     *     <li>JSON structure contains:
     *         <ul>
     *             <li><code>id</code>: Unique identifier of the food location.</li>
     *             <li><code>name</code>: Display name of the food location.</li>
     *             <li><code>warp</*/
    public void saveToFile() {
        foodLocations.sort(Comparator.comparing(o -> ChatColor.stripColor(o.getName().toLowerCase())));
        for (Park park : ParkManager.getParkUtil().getParks()) {
            JsonArray array = new JsonArray();
            foodLocations.stream().filter(food -> food.getPark().equals(park.getId())).forEach(food -> {
                JsonObject object = new JsonObject();
                object.addProperty("id", food.getId());
                object.addProperty("name", food.getName());
                object.addProperty("warp", food.getWarp());
                object.add("item", ItemUtil.getJsonFromItemNew(food.getItem()));
                array.add(object);
            });
            try {
                ParkManager.getFileUtil().getSubsystem("food").writeFileContents(park.getId().name().toLowerCase(), array);
            } catch (IOException e) {
                Core.logMessage("FoodManager", "There was an error writing to the FoodManager config: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
