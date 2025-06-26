package network.palace.parkmanager.attractions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import network.palace.parkmanager.handlers.AttractionCategory;
import network.palace.parkmanager.handlers.ParkType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Represents an attraction within a park. An attraction has various properties including a unique identifier,
 * the park it belongs to, its name, warp point, description, categories, operational status, an associated item,
 * and an optional linked queue.
 *
 * <p>Instances of this class are typically managed by the {@link AttractionManager} for purposes such as
 * initialization, addition, removal, and data persistence within the park management system.</p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *     <li><b>ID:</b> A unique identifier for the attraction.</li>
 *     <li><b>Park:</b> The {@link ParkType} that the attraction is associated with.</li>
 *     <li><b>Name:</b> The display name of the attraction, which can be updated.</li>
 *     <li><b>Warp:</b> A warp identifier for teleporting to the attraction, which can be updated.</li>
 *     <li><b>Description:</b> A descriptive text about the attraction, which can be updated.</li>
 *     <li><b>Categories:</b> A list of {@link AttractionCategory} values describing the attraction's features, which can be updated.</li>
 *     <li><b>Open:</b> A flag indicating whether the attraction is currently operational, which can be updated.</li>
 *     <li><b>Item:</b> An {@link ItemStack} representing the attraction in a visual or interactive manner, which can be updated.</li>
 *     <li><b>Linked Queue:</b> An optional {@link UUID} representing a linked queue system for the attraction, which can be updated.</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public class Attraction {

    /**
     * A unique identifier for the attraction.
     *
     * <p>This variable is used to uniquely distinguish each attraction within the park management system.
     * It serves as a key for identifying and managing specific attractions in the system.</p>
     *
     * <p><b>Key Characteristics:</b></p>
     * <ul>
     *     <li>Used in various operations such as retrieval, addition, and removal of attractions.</li>
     *     <li>Stored and managed in conjunction with the {@link AttractionManager} for persistence and look-up.</li>
     *     <li>Must be unique across all attractions within the system.</li>
     * </ul>
     *
     * <p><b>Usage:</b></p>
     * <ul>
     *     <li>Assigned either explicitly or derived from other attraction properties during initialization.</li>
     *     <li>Utilized in methods such as {@code getAttraction(String id, ParkType park)} and {@code removeAttraction(String id, ParkType park)}
     *     within {@link AttractionManager}.</li>
     * </ul>
     */
    private String id;

    /**
     * Represents the type of park associated with an attraction.
     *
     * <p>The <code>park</code> variable stores a {@link ParkType}, indicating which specific park
     * the attraction belongs to. This can include theme parks, resorts, water parks, or other categories
     * of destinations managed within the system.</p>
     *
     * <p><b>Usage:</b></p>
     * <ul>
     *     <li>Determines the park category relevant to the attraction.</li>
     *     <li>Used for data persistence and filtering attractions by their associated park.</li>
     *     <li>Supports identification and retrieval of attractions grouped by different park types.</li>
     * </ul>
     *
     * <p>Relevant operations, including initialization, addition, and deletion of attractions for a specific park,
     * are typically handled by the {@link AttractionManager}.</p>
     *
     * @see ParkType
     */
    private ParkType park;

    /**
     * <p>Represents the name of an attraction within a park.</p>
     *
     * <p><b>Description:</b></p>
     * <ul>
     *     <li>The name serves as the display name of this attraction.</li>
     *     <li>This value can be updated dynamically during runtime to reflect any changes made to the attraction's title.</li>
     *     <li>This name is utilized throughout the park management system for identifying and displaying the attraction.</li>
     * </ul>
     *
     * <p><b>Use Cases:</b></p>
     * <ul>
     *     <li>To display the attraction's name in user-facing interfaces.</li>
     *     <li>To allow managers or systems to modify the attraction's name for branding or descriptive updates.</li>
     *     <li>To enable searches and categorization of attractions across the park management system using their names.</li>
     * </ul>
     */
    @Setter private String name;

    /**
     * <p>The <code>warp</code> variable represents the warp location associated with the attraction.
     * It is used to identify or teleport players to specific locations within a park attraction.</p>
     *
     * <p>This variable is expected to hold a unique, string-based identifier for the warp, which is
     * typically defined within the configuration of each attraction. It may also serve as a fallback
     * identifier if no specific ID is provided for the attraction.</p>
     *
     * <p>The <code>warp</code> value is essential for mapping and managing in-game attractions, allowing
     * seamless integration between the Attraction system and other systems such as park navigation and
     * queue management.</p>
     */
    @Setter private String warp;

    /**
     * <p>Represents the description of an attraction in the park system.</p>
     *
     * <p>This variable contains a textual description providing additional information
     * or details about the specific attraction for purposes such as user-facing
     * explanations, metadata for attraction filtering, or documentation purposes within
     * the park management system.</p>
     *
     * <ul>
     *   <li>Used for displaying detailed information about the attraction.</li>
     *   <li>Persisted and stored as part of attraction management configurations.</li>
     *   <li>Loaded from or saved to configuration files within the system.</li>
     * </ul>
     */
    @Setter private String description;

    /**
     * <p>
     * Represents a collection of attraction categories that are associated
     * with an attraction. These categories represent various characteristics
     * or classifications that describe the attraction.
     * </p>
     *
     * <p>
     * The categories can include different types of rides, shows, or experiences,
     * and they help in organizing or filtering attractions within a park system.
     * </p>
     *
     * <p>
     * Common categories may include:
     * <ul>
     *   <li>BIG_DROPS - Attractions featuring significant drops.</li>
     *   <li>SMALL_DROPS - Attractions with minor drops.</li>
     *   <li>SLOW_RIDE - Slow-paced rides suitable for all ages.</li>
     *   <li>THRILL_RIDE - High-energy, adrenaline-inducing attractions.</li>
     *   <li>WATER_RIDE - Attractions involving water features or elements.</li>
     *   <li>SPINNING - Spinning-based attractions.</li>
     *   <li>DARK - Attractions taking place in low-light or dark environments.</li>
     *   <li>LOUD - Attractions known for loud sounds or music.</li>
     *   <li>SCARY - Attractions with scary themes or elements.</li>
     *   <li>CLASSIC - Timeless, iconic attractions.</li>
     *   <li>ANIMAL_ENCOUNTERS - Attractions featuring interactions with animals.</li>
     *   <li>INDOOR - Indoor-only attractions.</li>
     *   <li>INTERACTIVE - Attractions with interactive elements.</li>
     *   <li>STAGE_SHOW - Live stage performances.</li>
     *   <li>AUDIO_SERVER - Attractions relying on in-depth audio experiences.</li>
     *   <li>PHOTOPASS - Attractions providing photography features.</li>
     * </ul>
     * </p>
     */
    @Setter private List<AttractionCategory> categories;

    /**
     * Represents the operational state of an attraction.
     * <p>
     * This field determines whether the attraction is currently open or closed.
     * Attractions marked as open are available for visitors, while closed attractions
     * are not accessible.
     * </p>
     *
     * <ul>
     *     <li><b>true:</b> The attraction is open and operational.</li>
     *     <li><b>false:</b> The attraction is closed and not operational.</li>
     * </ul>
     *
     * <p>
     * This variable is expected to be updated based on operational conditions,
     * such as maintenance schedules, operating hours, or unexpected closures.
     * </p>
     *
     * <p>
     * The state of this variable may influence other*/
    @Setter private boolean open;

    /**
     * Represents the in-game item associated with the attraction.
     * <p>
     * This item may be used to visually represent or interact with the attraction
     * in the game, such as displaying it in a GUI or utilizing it as a marker or tool.
     **/
    @Setter private ItemStack item;

    /**
     * Represents the unique identifier of a linked queue associated with the attraction.
     * <p>
     * This UUID corresponds to a queue system that may be linked to the specific
     * attraction, allowing for integration and management of guest flow within the park.
     * If the attraction does not have an associated queue, this field can be null.
     * </p>
     *
     * <p>Key notes:</p>
     * <ul>
     *   <li>Acts as a reference for the queue system connected to the attraction.</li>
     *   <li>Used in saving and loading attraction data*/
    @Setter private UUID linkedQueue;
}