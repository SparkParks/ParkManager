package network.palace.parkmanager.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.parkmanager.handlers.ParkType;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a food location within a theme park or resort.
 * <p>
 * This class is used to define attributes for a specific food-related location,
 * including its unique identifier, associated park, name, warp location, and
 * the corresponding {@link ItemStack} representation of food.
 * </p>
 * <p>
 * Instances of this class are primarily managed through {@link FoodManager},
 * which handles storage, retrieval, and CRUD operations for all food locations.
 * </p>
 * <h3>Key Fields:</h3>
 * <ul>
 *     <li><b>id:</b> The unique identifier for the food location.</li>
 *     <li><b>park:</b> The {@link ParkType} representing the park or resort where this food location resides.</li>
 *     <li><b>name:</b> The display name of the food location.</li>
 *     <li><b>warp:</b> A warp string associated with teleportation or location management.</li>
 *     <li><b>item:</b> The {@link ItemStack} representing the food item associated with this location.</li>
 * </ul>
 * <p>
 * Typically, {@link FoodManager} uses this class during operations such as:
 * <ul>
 *     <li>Loading food locations from configuration files.</li>
 *     <li>Filtering and retrieving food locations based on their associated park.</li>
 *     <li>Adding, updating, or deleting food locations dynamically.</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public class FoodLocation {

    /**
     * A unique identifier for the {@link FoodLocation}.
     * <p>
     * This field represents the primary key for identifying a specific food location within the system.
     * It is used for various operations, including:
     * <ul>
     *     <li>Retrieving a food location by its identifier in {@link FoodManager#getFoodLocation(String, ParkType)}.</li>
     *     <li>Ensuring uniqueness of food locations.</li>
     *     <li>Mapping data from configuration files to {@link FoodLocation} objects during initialization
     *     in {@link FoodManager#initialize()}.</li>
     * </ul>
     * <p>
     * Typically, this value is derived from the associated warp string or explicitly defined in the configuration files.
     *
     * <h3>Constraints:</h3>
     * <ul>
     *     <li>Must be unique within the context of the associated {@link ParkType}.</li>
     *     <li>Used as a reference when adding, updating, or removing food locations through {@link FoodManager} functionality.</li>
     * </ul>
     */
    private String id;

    /**
     * Represents the associated theme park or resort for this food location.
     * <p>
     * The {@code park} variable identifies where the food location is situated within a specific park or resort.
     * It is of type {@link ParkType}, which includes predefined enum values such as theme parks, water
     * parks, and resorts (e.g., "Magic Kingdom", "Epcot", or "Contemporary Resort").
     * </p>
     *
     * <h3>Usage:</h3>
     * <ul>
     *     <li>Used to filter or retrieve food locations by their associated park.</li>
     *     <li>Ensures that each food location is categorized under a specific park or resort.</li>
     *     <li>Serves in dynamic operations like loading, adding, or managing food locations for a park.</li>
     * </ul>
     *
     * <h3>Relevant Operations:</h3>
     * <ul>
     *     <li>In {@link FoodManager}, food locations are grouped, stored, and retrieved based on
     *     their {@link ParkType} through methods like {@code getFoodLocations(ParkType park)}.</li>
     *     <li>During initialization, this field links a food location to a park when loading
     *     configurations dynamically from files.</li>
     * </ul>
     */
    private ParkType park;

    /**
     * Represents the display name of the food location.
     * <p>
     * This field stores a human-readable name that is used to identify the food location
     * within the theme park or resort. The display name is primarily utilized for:
     * </p>
     * <ul>
     *     <li>User-facing displays or menus where a descriptive name is required.</li>
     *     <li>Filtering or searching operations to locate specific food locations by name.</li>
     *     <li>Configuration files where the name is stored alongside other metadata.</li>
     * </ul>
     * <p>
     * The value of this field is expected to be a non-null, non-empty string.
     * </p>
     */
    private String name;

    /**
     * Represents the warp location associated with a food location.
     * <p>
     * This variable stores a string identifier used to specify a teleportation
     * or location reference associated with the food location. It serves as a
     * link to external systems or tools that handle in-game navigation or
     * location management.
     * </p>
     *
     * <h3>Key Details:</h3>
     * <ul>
     *     <li>Used primarily by {@link FoodManager} for managing and referencing food locations.</li>
     *     <li>Mapped from configuration files or dynamically set during runtime operations.</li>
     *     <li>Acts as a connection between food locations and warp systems, ensuring accurate
     *         teleportation or navigation to the appropriate locations.</li>
     * </ul>
     *
     * <p>
     * The warp string must be unique within the scope of the associated park to avoid
     * conflicts or incorrect navigation. Modifications to this value should be handled
     * cautiously to ensure consistency across the system.
     * </p>
     */
    private String warp;

    /**
     * Represents the {@link ItemStack} associated with a specific food location in the park or resort.
     * <p>
     * This field stores the in-game item representation of the food offered at a given location.
     * It is primarily used to define the visual and functional characteristics of the food in the park system.
     * </p>
     *
     * <h3>Key Attributes:</h3>
     * <ul>
     *     <li>Defines the Minecraft item that symbolizes the food at the location.</li>
     *     <li>Can include attributes such as item type, display name, lore, and enchantments.</li>
     *     <li>Facilitates interactions and representations of food-related items within the game.</li>
     * </ul>
     */
    private ItemStack item;
}