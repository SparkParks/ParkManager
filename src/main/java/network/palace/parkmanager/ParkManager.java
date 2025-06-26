package network.palace.parkmanager;

import lombok.Getter;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.parkmanager.attractions.AttractionManager;
import network.palace.parkmanager.autograph.AutographManager;
import network.palace.parkmanager.commands.*;
import network.palace.parkmanager.food.FoodLocation;
import network.palace.parkmanager.food.FoodManager;
import network.palace.parkmanager.fpkiosk.FastPassKioskManager;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.leaderboard.LeaderboardManager;
import network.palace.parkmanager.listeners.*;
import network.palace.parkmanager.magicband.MagicBandManager;
import network.palace.parkmanager.outline.OutlineManager;
import network.palace.parkmanager.outline.OutlineSession;
import network.palace.parkmanager.packs.PackManager;
import network.palace.parkmanager.queues.QueueManager;
import network.palace.parkmanager.queues.virtual.VirtualQueueManager;
import network.palace.parkmanager.shop.ShopManager;
import network.palace.parkmanager.shows.ShowMenuManager;
import network.palace.parkmanager.shows.schedule.ScheduleManager;
import network.palace.parkmanager.storage.StorageManager;
import network.palace.parkmanager.utils.*;
import network.palace.parkmanager.wardrobe.WardrobeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

import java.util.Calendar;

/**
 * <p>
 * The {@code ParkManager} class represents the main plugin management class for a theme park plugin utilizing
 * dependencies for various features along with functionality to handle plugin enabling, disabling, command
 * registration, and listener registration.
 * </p>
 *
 * <p>
 * This plugin integrates a variety of utilities and managers essential for creating and running a virtual theme park
 * within a Minecraft environment.
 * </p>
 *
 * <h2>Dependencies</h2>
 * <p>
 * This class depends on several plugins and libraries to function properly, both as required and optional:
 * </p>
 * <ul>
 *     <li><strong>Required:</strong> Core, ProtocolLib, WorldEdit, Cosmetics, ParkWarp</li>
 *     <li><strong>Optional:</strong> RideManager, Show</li>
 * </ul>
 *
 * <h2>Main Responsibilities</h2>
 * <ul>
 *     <li>Manages the lifecycle of the plugin, including enabling and disabling.</li>
 *     <li>Initializes utilities and managers for various park functionalities.</li>
 *     <li>Registers custom commands and event listeners for interactive park features.</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <ul>
 *     <li>Support for attractions, queues, food vendors, shops, and fast passes.</li>
 *     <li>Control over player utilities, such as inventory and teleportation.</li>
 *     <li>Extensive handling for park-related events and gameplay mechanics.</li>
 *     <li>Integration with visual and interactive plugin features, such as outlines and wardrobe.</li>
 * </ul>
 *
 * <h2>Plugin Workflow</h2>
 * <p>
 * When enabled, this plugin:
 * </p>
 * <ul>
 *     <li>Initializes all utility classes and manager objects.</li>
 *     <li>Sets up commands for administrative interaction with park elements.</li>
 *     <li>Registers listeners for various events within the park environment.</li>
 * </ul>
 * <p>
 * On disable, it handles cleanup operations, such as clearing queues and removing entities like {@link Minecart}.
 * </p>
 *
 * <h2>Usage</h2>
 * <p>
 * This class acts as the entry point for the park management plugin. Developers use the provided utilities, managers,
 * and listeners by accessing their appropriate getters.
 * </p>
 *
 * <h2>Key Methods</h2>
 * <ul>
 *     <li>{@link #onPluginEnable()} - Invoked when the plugin is enabled. Initializes components and starts the park manager.</li>
 *     <li>{@link #onPluginDisable()} - Invoked when the plugin is disabled. Cleans up resources.</li>
 *     <li>{@link #registerCommands()} - Registers commands for managing the park.</li>
 *     <li>{@link #registerListeners()} - Sets up event listeners for handling park-related events.</li>
 *     <li>{@link #getResort()} - Returns the resort configuration managed by the plugin.</li>
 * </ul>
 */
@PluginInfo(name = "ParkManager", version = "3.3.9", depend = {"Core", "ProtocolLib", "WorldEdit", "Cosmetics", "ParkWarp"}, softdepend = {"RideManager", "Show"})
public class ParkManager extends Plugin {
    /**
     * <p>Represents the singleton instance of the {@code ParkManager} class.</p>
     *
     * <p>This variable holds the single, globally accessible instance of the
     * {@code ParkManager}, ensuring that only one instance of the class is
     * ever created throughout the lifecycle of the application.</p>
     *
     * <p><b>Key Features:</b></p>
     * <ul>
     *   <li>Ensures a single entry point for accessing core utility managers
     *       and services within the {@code ParkManager} framework.</li>
     *   <li>Facilitates centralized management of parks, attractions,
     *       and associated components.</li>
     *   <li>Lazy initialization or pre-instantiation mechanisms may be
     *       applied depending on the context of usage within the
     *       {@code ParkManager} class.</li>
     * </ul>
     *
     * <p><b>Thread Safety:</b> If the environment supports concurrency or multiple
     * threads, the implementation of this singleton must address concurrency
     * issues to ensure that the instance is consistently initialized in a
     * thread-safe manner.</p>
     */
    @Getter private static ParkManager instance;

    /**
     * <p>
     * The {@code fileUtil} variable provides access to a shared instance of the {@link FileUtil} class for managing
     * various file operations within the {@code ParkManager} plugin. This includes functionalities such as
     * handling subsystems, reading/writing JSON files, and maintaining the plugin's directory structure.
     * </p>
     *
     * <p>
     * <strong>Key Features:</strong>
     * <ul>
     *     <li>Allows registration and tracking of file subsystems for different components of the plugin.</li>
     *     <li>Supports creating and retrieving files/directories under subsystem-specific folders.</li>
     *     <li>Provides utility methods for interacting with JSON configurations.</li>
     *     <li>Ensures creation of the root plugin directory structure if not present.</li>
     * </ul>
     * </p>
     *
     * <p>
     * The {@code fileUtil} instance is initialized as a singleton and is accessed statically within the plugin
     * through {@link ParkManager}.
     * </p>
     *
     * <p>
     * This variable is leveraged heavily by other utility classes and managers in the plugin to store and retrieve
     * persistent data in an organized manner.
     * </p>
     */
    @Getter private static FileUtil fileUtil;

    /**
     * <p>The {@code parkUtil} is a static instance used to manage
     * and interact with the park-related functionality in the application.
     * It acts as a centralized utility for handling park operations,
     * such as retrieval, addition, and removal of parks, as well as
     * region validation and persistence of park data.</p>
     *
     * <p>Functionality provided by {@code parkUtil} includes:</p>
     * <ul>
     *   <li>Initialization of parks from a configuration file.</li>
     *   <li>Retrieval of park information based on identifiers or locations.</li>
     *   <li>Adding, removing, and maintaining a collection of parks.</li>
     *   <li>Saving parks data back to a persistent storage.</li>
     * </ul>
     *
     * <p>It leverages elements such as {@code ParkType}, {@code Park}, {@code World},
     * and {@code ProtectedRegion} to ensure proper functionality within the plugin
     * environment. The {@code parkUtil} is crucial for core park management operations.</p>
     *
     * <p>This instance is part of the {@link ParkManager} class and interacts with
     * other subsystems, including file handlers and region management plugins,
     * to maintain proper park lifecycle management.</p>
     */
    @Getter private static ParkUtil parkUtil;

    /**
     * <p>The <code>attractionManager</code> is a singleton instance within the <code>ParkManager</code> class, used for
     * managing interactions with park attractions in the system.</p>
     *
     * <p>This instance of <code>AttractionManager</code> is responsible for:</p>
     * <ul>
     *     <li>Loading attraction data from associated configuration files.</li>
     *     <li>Providing access to attraction details, including categories, linked queues, and park associations.</li>
     *     <li>Managing lifecycle operations such as adding, retrieving, and removing attractions.</li>
     *     <li>Persisting attraction data to external storage for long-term availability and consistency.</li>
     * </ul>
     *
     * <p>It interacts with other components, such as <code>ParkUtil</code>, <code>FileUtil</code>, and <code>ItemUtil</code>,
     * to perform data validation, storage operations, and data processing. This guarantees a reliable and consistent system
     * for managing park attractions across various park types.</p>
     *
     * <p>Being a static member, <code>attractionManager</code> ensures a single shared instance throughout
     * the application for centralized attraction management.</p>
     */
    @Getter private static AttractionManager attractionManager;

    /**
     * Represents the static instance of {@link AutographManager} within the
     * {@link ParkManager} class. This manager provides functionality specific
     * to handling autograph-related features within the park management system.
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Facilitates interaction with autograph-related operations.</li>
     *   <li>Acts as a centralized interface for managing autographs in the system.</li>
     *   <li>Ensures global access to autograph management capabilities.</li>
     * </ul>
     *
     * <p>This variable is designed to maintain a single, globally accessible instance
     * of {@link AutographManager} to streamline and standardize its usage throughout
     * the plugin.</p>
     */
    @Getter private static AutographManager autographManager;

    /**
     * <p>A static, singleton instance of {@link BuildUtil}, used to manage and control build-related functionalities within the <code>ParkManager</code> system.
     * This utility is responsible for handling operations such as toggling build mode or checking if a player is currently in build mode.</p>
     *
     * <p><b>Key responsibilities include:</b></p>
     * <ul>
     *   <li>Managing the build mode state of players.</li>
     *   <li>Validating whether specific players have the permissions to toggle build mode.</li>
     *   <li>Providing build utilities to interact with the underlying game mechanics and player inventories.</li>
     * </ul>
     *
     * <p>This instance is accessed statically and shared across the system to ensure consistent behavior and state management for build-related operations.</p>
     */
    @Getter private static BuildUtil buildUtil;

    /**
     * <p>The {@code configUtil} variable is a static instance of the {@link ConfigUtil} class. It is responsible
     * for managing the configuration-related operations of the application, such as loading, updating, and saving
     * configuration values to persistent storage. The configuration includes parameters related to player
     * management and server behavior, such as spawn location, join messages, and server-specific properties.</p>
     *
     * <p>The {@link ConfigUtil} class interacts with subsystem files using the {@link FileUtil} utility to persist
     * data in a JSON format. It ensures that the server settings are loaded during initialization and persists
     * any changes made to these settings.</p>
     *
     * <ul>
     *   <li>Handles configuration data such as spawn points, join messages, and server-specific attributes.</li>
     *   <li>Interacts with file subsystems for loading and saving JSON data.</li>
     *   <li>Provides methods for modifying configuration settings dynamically during runtime.</li>
     * </ul>
     *
     * <p>This instance is initialized during the server setup and configured via the plugin's runtime logic, ensuring
     * that the configuration data is consistent across server lifecycle events.</p>
     */
    @Getter private static ConfigUtil configUtil;

    /**
     * <p>Represents a static instance of {@link DelayUtil} responsible for managing and processing delay-related tasks.</p>
     *
     * <p>The {@code delayUtil} is initialized as a part of the {@code ParkManager} to handle block-related delays,
     * such as deferred actions for setting a block to a specific type at a given location after a certain time.
     * This utility works as a core component in handling gameplay mechanics that require timed effects.</p>
     *
     * <p><strong>Key Features:</strong></p>
     * <ul>
     *     <li>Schedules and processes delayed actions using {@code DelayEntry} structures.</li>
     *     <li>Allows blocks to transform into a given {@link Material} type after a specified number of ticks.</li>
     *     <li>Supports additional logic for setting blocks to air after a secondary delay when applicable.</li>
     * </ul>
     *
     * <p>This static instance can be accessed and utilized within the various systems in {@code ParkManager} and related classes
     * to create and manage timed gameplay effects in the virtual park environment.</p>
     */
    @Getter private static DelayUtil delayUtil;

    /**
     * Manages operations and functionalities of the Fast Pass kiosk system within the resort.
     *
     * <p>This singleton instance of {@code FastPassKioskManager} is responsible for handling
     * tasks specifically related to the fast pass system, including but not limited to:
     * <ul>
     *   <li>Registration and management of guests utilizing fast pass services.</li>
     *   <li>Integration with ride and attraction scheduling to provide accurate fast pass times.</li>
     *   <li>Ensuring synchronization with the resort's overall attraction and scheduling systems.</li>
     * </ul>
     *
     * <p>Accessible statically through the {@code ParkManager} class, this manager facilitates
     * centralized control and functionality for fast pass-related operations across the resort.
     */
    @Getter private static FastPassKioskManager fastPassKioskManager;

    /**
     * The {@code foodManager} instance is a static, globally accessible manager responsible for
     * handling operations related to food locations within the park system.
     * <p>
     * The {@code FoodManager} handles the initialization, storage, retrieval, addition, and removal
     * of food location data in the system. It also manages interaction with persistent storage to
     * save or load food-related configuration data.
     * </p>
     *
     * <p><b>Key Responsibilities:</b></p>
     * <ul>
     *   <li>Initializing and clearing the list of food locations.</li>
     *   <li>Interfacing with the {@code FileUtil} subsystem for reading and writing
     *       food-related JSON configuration files.</li>
     *   <li>Managing a collection of {@link FoodLocation} objects for parks.</li>
     *   <li>Querying food locations by ID and filtering by park.</li>
     *   <li>Updating the JSON configuration files when changes are made to the food locations.</li>
     * </ul>
     *
     * <p>The {@code foodManager} instance is initialized as part of the {@code ParkManager} lifecycle
     * and can be accessed statically throughout the system for managing food-related operations.</p>
     */
    @Getter private static FoodManager foodManager;

    /**
     * A static instance of {@code InventoryUtil} used for managing inventory-related
     * operations in the context of the application.
     * <p>
     * The {@code inventoryUtil} field provides centralized access to inventory
     * management utilities and facilitates operations such as handling item storage,
     * retrieval, and updates.
     * <p>
     * This field is part of the {@code ParkManager} class, which oversees various
     * aspects of park management functionalities.
     */
    @Getter private static InventoryUtil inventoryUtil;

    /**
     * <p>The <code>leaderboardManager</code> is a static instance of the {@link LeaderboardManager} class,
     * responsible for managing leaderboard data within the system. It handles tasks such as registering,
     * updating, and saving leaderboard signs, which display sorted information about rides, players, or related
     * entities in the park.</p>
     *
     * <p>This instance is initialized and managed by {@link ParkManager}, ensuring that leaderboard information
     * is updated periodically and synchronized with the configuration files. The <code>leaderboardManager</code>
     * facilitates consistent access to leaderboard functionality across the system.</p>
     *
     * <p>Main features of the <code>leaderboardManager</code> include:</p>
     * <ul>
     *   <li>Parsing leaderboard data from configuration files.</li>
     *   <li>Registering and maintaining leaderboard signs to display data in the game world.</li>
     *   <li>Updating leaderboard information periodically to reflect changes in relevant data.</li>
     *   <li>Sorting and formatting leaderboard entries for display.</li>
     *   <li>Saving leaderboard state to file for persistence.</li>
     * </ul>
     *
     * <p>Note: This instance is private to the {@link ParkManager} class and is accessed through static utilities where necessary.</p>
     */
    @Getter private static LeaderboardManager leaderboardManager;

    /**
     * <p>
     * The <code>magicBandManager</code> is a static instance of the {@link MagicBandManager} class,
     * which is used to manage and interact with functionalities related to the Magic Band system in the context
     * of the park's management operations.
     * </p>
     *
     * <p>
     * This instance is initialized as part of the park management system and provides access to features
     * like tracking, configuration, and operations related to Magic Bands. The <code>magicBandManager</code>
     * ensures centralized control and functionality accessibility for Magic Band-related tasks.
     * </p>
     *
     * <p>
     * This variable is part of the {@link ParkManager} class, designed for overseeing a wide range of
     * park management utilities and operations.
     * </p>
     *
     * <p><b>Note:</b> The variable is declared <code>private static</code> for singleton management
     * and is annotated with {@link lombok.Getter} to provide read-only access via a getter method.
     * </p>
     */
    @Getter private static MagicBandManager magicBandManager;

    /**
     * The {@code outlineManager} is a static instance of the {@link OutlineManager} class within the {@link ParkManager}.
     * It manages the creation, retrieval, and storage of outlines, which include defined points and sessions linked to users.
     * This component operates as a singleton within the {@link ParkManager} context, allowing centralized access and management.
     *
     * <p>Responsibilities of this manager include:
     * <ul>
     *   <li>Maintaining a list of configured outlines (points) within the park.</li>
     *   <li>Handling user-specific {@link OutlineSession} instances tied to unique user identifiers (UUIDs).</li>
     *   <li>Providing functionality to persist outline data to disk and load from configuration files.</li>
     *   <li>Facilitating the addition, retrieval, and deletion of points as part of outline management.</li>
     * </ul>
     *
     * <p>Access to {@code outlineManager} should be performed through the static reference contained in {@link ParkManager}.
     * This architecture enforces consistent use across all classes requiring outline-related functionality.
     */
    @Getter private static OutlineManager outlineManager;

    /**
     * Represents the static instance of the <code>PackManager</code> class used within the system.
     * <p>
     * <code>packManager</code> is a singleton that provides access to the core functionality for managing
     * resource packs for players, including loading configurations, handling player events, and adjusting settings.
     * </p>
     *
     * <p><b>Responsibilities:</b></p>
     * <ul>
     *   <li>Handles resource pack management, including sending the appropriate pack to players based on their settings.</li>
     *   <li>Processes user actions such as accepting, declining, or handling resource pack changes.</li>
     *   <li>Maintains server-wide resource pack configurations and synchronizes with player preferences.</li>
     *   <li>Supports events triggered by resource pack status changes or player interactions.</li>
     * </ul>
     *
     * <p><b>Usage:</b></p>
     * <p>This instance is primarily accessed by the containing <code>ParkManager</code> class or
     * other system components requiring resource pack management functionality. Utilize this variable
     * for managing park-related resource packs across the system's services.</p>
     */
    @Getter private static PackManager packManager;

    /**
     * Represents a singleton instance of {@link PlayerUtil}, utilized to manage player-related data within the
     * ParkManager plugin. This includes but is not limited to handling player login information and user cache.
     *
     * <p><b>Key Responsibilities:</b></p>
     * <ul>
     *     <li>Management of login data associated with players using {@code UUIDs} as keys.</li>
     *     <li>Tracking and updating the user cache containing mappings between player {@code UUIDs} and their respective usernames.</li>
     * </ul>
     *
     * <p>This static field ensures that a single instance of {@link PlayerUtil} is shared and accessible globally
     * within the plugin's runtime.</p>
     *
     * <p><b>Thread Safety:</b> This variable is designed for shared access but should be used cautiously in
     * multithreaded contexts to ensure data consistency.</p>
     */
    @Getter private static PlayerUtil playerUtil;

    /**
     * <p>The {@code queueManager} is a static instance of the {@link QueueManager} class utilized within
     * the context of the {@code ParkManager} to manage and maintain operations related to queuing systems.</p>
     *
     * <p><b>Key Characteristics:</b></p>
     * <ul>
     * <li>Serves as a centralized component for handling queue-related functionalities.</li>
     * <li>Ensures efficient management of operations associated with virtual and physical queues.</li>
     * </ul>
     *
     * <p>The {@code queueManager} instance is accessible across the application to facilitate queue-related
     * services and integrations with other park management components.</p>
     */
    @Getter private static QueueManager queueManager;

    /**
     * A singleton instance of the {@code RideCounterUtil} class used for managing ride counters in the park management system.
     *
     * <p>The {@code rideCounterUtil} is responsible for handling operations related to tracking and updating
     * ride statistics, including retrieving, caching, and logging ride data for players. The instance centralizes
     * all ride counter-related logic to ensure consistent behavior across the application.
     *
     * <p>Key responsibilities of the {@code RideCounterUtil} include:
     * <ul>
     *   <li>Retrieving ride counters for players from the database and caching them locally for performance.</li>
     *   <li>Logging new ride counters and immediately updating the cached data.</li>
     *   <li>Interfacing with external systems such as databases or messaging systems as needed.</li>
     * </ul>
     *
     * <p>This field is statically accessible and designed to support the centralized management of ride counter-related
     * operations, ensuring thread-safety and consistency throughout the application.
     */
    @Getter private static RideCounterUtil rideCounterUtil;

    /**
     * <p>Represents the singleton instance of the {@code ScheduleManager} within the system,
     * responsible for managing and coordinating scheduling-related functionality.</p>
     *
     * <p>This variable provides system-wide access to scheduling features, ensuring
     * centralized control over time-based operations and scheduling mechanisms.</p>
     *
     * <p>Usage of this field is managed internally by the application, leveraging
     * its static and immutable reference for maintaining consistency and avoiding redundant instances.</p>
     */
    @Getter private static ScheduleManager scheduleManager;

    /**
     * <p>The {@code shopManager} is a static instance of {@link ShopManager}
     * and is responsible for managing all shop-related functionalities within the system.
     * It provides centralized access to handle various operations related to shops.</p>
     *
     * <p>This instance is typically used to coordinate shop inventories, customer interactions,
     * transactions, and other shop-related tasks.</p>
     *
     * <p><strong>Class Context:</strong></p>
     * <ul>
     *   <li>Contained within the {@code ParkManager} class.</li>
     *   <li>Designed as a centralized static field for global access to shop functionalities.</li>
     * </ul>
     *
     * <p><strong>Usage Notes:</strong></p>
     * <ul>
     *   <li>Access to this field should be restricted to authorized modules within the application.</li>
     *   <li>Ensure thread safety when accessing and utilizing the {@code shopManager}.</li>
     * </ul>
     */
    @Getter private static ShopManager shopManager;

    /**
     * <p>Represents the manager responsible for handling the show's menu-related operations.</p>
     *
     * <p>This variable is a static instance, providing a centralized access point for
     * managing and interacting with functionalities related to show menus within the system.</p>
     *
     * <ul>
     *     <li>Accessing show-related menu items and configurations.</li>
     *     <li>Facilitating menu updates or retrieval within the context of the system.</li>
     * </ul>
     *
     * <p>This instance is intended to streamline operations involving show menu management
     * as part of the broader <code>ParkManager</code> functionality.</p>
     */
    @Getter private static ShowMenuManager showMenuManager;

    /**
     * A static instance of {@code StorageManager} used to manage storage-related functionalities
     * within the application.
     *
     * <p> This variable enables centralized access to functionalities and utilities provided
     * by the {@code StorageManager}. Designed to be used as part of the core operations
     * within the {@code ParkManager}, it ensures storage operations are handled consistently throughout.
     *
     * <p> Key Features:
     * <ul>
     *   <li> Acts as a manager for all storage operations.</li>
     *   <li> Provides streamlined performance for storage-intensive processes.</li>
     *   <li> Designed for access across the ParkManager class and potentially other utilities.</li>
     * </ul>
     */
    @Getter private static StorageManager storageManager;

    /**
     * A static instance of the {@link TeleportUtil} class, used for managing player teleportation,
     * location logging, and retrieval of past locations for the "/back" command functionality.
     *
     * <p>This utility serves as a core component for handling teleportation-related tasks, such as:
     * <ul>
     *     <li>Storing and managing player location data for enhanced navigation features.</li>
     *     <li>Logging players' previous positions for ease of backward teleportation.</li>
     *     <li>Retrieving and removing stored location data upon specific actions or events.</li>
     * </ul>
     */
    @Getter private static TeleportUtil teleportUtil;

    /**
     * A static instance of the {@link TimeUtil} class in the {@link ParkManager}.
     * <p>
     * This variable provides a centralized access point to utility methods for time-related
     * operations within the application. The {@link TimeUtil} class offers functionalities like
     * retrieving the current time, formatting time differences, and managing player interactions
     * with time displays.
     *
     * <p><b>Main Features:</b></p>
     * <ul>
     *   <li><b>Current Time Retrieval:</b> Provides the current time in EST with {@link TimeUtil#getCurrentTime()}.</li>
     *   <li><b>Custom Time Formatting:</b> Displays formatted time strings such as "hh:mm:ss AM/PM" via {@link TimeUtil#getWatchTimeText()}.</li>
     *   <li><b>Time Difference Calculation:</b> Allows formatting intervals between two dates via {@link TimeUtil#formatDateDiff(Calendar, Calendar)}.</li>
     *   <li><b>Player Watch Interaction:</b> Manages "watch viewers" for showing real-time time updates on player action bars.</li>
     *   <li><b>Time Representation:</b> Maps specific string time inputs (e.g., "6AM", "12PM") to game-internal time values using {@link TimeUtil#getTime(String)}.</li>
     * </ul>
     *
     * <p>This variable ensures a single instance of {@link TimeUtil} is utilized within the {@link ParkManager},
     * maintaining consistency and avoiding redundant instantiations.
     */
    @Getter private static TimeUtil timeUtil;

    /**
     * <p>The {@code virtualQueueManager} is a static instance of {@link VirtualQueueManager} that is
     * responsible for managing and coordinating virtual queues within the system.</p>
     *
     * <p>This manager implements functionality to handle the queuing of users or entities to specific
     * activities, attractions, or services, ensuring efficient queue management and optimized
     * user experience.</p>
     *
     * <p>The {@code virtualQueueManager} is accessible as a singleton and is initialized through the
     * lifecycle management of the encompassing {@code ParkManager} class.</p>
     *
     * <ul>
     *     <li>Performs operations related to virtual queue creation, maintenance, and resolution.</li>
     *     <li>Supports integration with other subsystems within the application for queueing processes.</li>
     *     <li>Ensures virtual queues adhere to configuration and operational rules.</li>
     * </ul>
     *
     * <p>Access to the {@code virtualQueueManager} instance should be performed via its getter method,
     * ensuring encapsulation and proper access rights.</p>
     */
    @Getter private static VirtualQueueManager virtualQueueManager;

    /**
     * Static instance of {@link VisibilityUtil} for managing player visibility within the ParkManager plugin.
     *
     * <p>This utility provides the following functionalities:
     * <ul>
     *   <li>Manages the visibility settings for players in the system.</li>
     *   <li>Allows toggling between different visibility settings such as visible to all, hidden, or visible only to friends/staff.</li>
     *   <li>Updates player visibility dynamically based on predefined settings or command inputs.</li>
     *   <li>Handles player join events and applies appropriate visibility configurations.</li>
     * </ul>
     *
     * <p>Visibility settings are enforced through the {@code VisibilityUtil.Setting} enum, which defines distinct modes such as:
     * <ul>
     *   <li>{@code ALL_VISIBLE}: All players are visible.</li>
     *   <li>{@code ONLY_STAFF_AND_FRIENDS}: Only staff members and friends are visible.</li>
     *   <li>{@code ONLY_FRIENDS}: Only friends are visible.</li>
     *   <li>{@code ALL_HIDDEN}: All players are hidden.</li>
     * </ul>
     *
     * <p>Access to this utility is limited to the {@code ParkManager} class through a singleton instance, ensuring centralized visibility management.
     */
    @Getter private static VisibilityUtil visibilityUtil;

    /**
     * <p>A static instance of the {@code WardrobeManager} used to manage wardrobe-related functionality
     * within the system. This variable is part of the {@code ParkManager} class and provides access
     * to utilities and operations related to wardrobe management.</p>
     *
     * <p>The {@code WardrobeManager} handles tasks such as:</p>
     * <ul>
     *   <li>Managing wardrobe customization and options for users.</li>
     *   <li>Interfacing with other system utilities to ensure seamless player experiences.</li>
     *   <li>Handling storage or retrieval of wardrobe configurations.</li>
     * </ul>
     *
     * <p>It operates as a singleton, ensuring there is only one centralized instance that manages
     * all wardrobe-related behaviors within the application.</p>
     */
    @Getter private static WardrobeManager wardrobeManager;

    /**
     * Initializes and enables the plugin by setting up various utilities and managers, registering commands,
     * and listeners necessary for the plugin's operation.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *     <li>Sets the plugin instance for access throughout the plugin.</li>
     *     <li>Instantiates utility and manager classes such as {@code FileUtil}, {@code ParkUtil}, {@code AttractionManager},
     *     and many others required for the plugin's functionality.</li>
     *     <li>Initializes important systems such as {@code StorageManager} and retrieves data from the database
     *     via {@code VirtualQueueManager}.</li>
     *     <li>Registers event listeners and commands to ensure that the plugin can handle in-game interactions and
     *     provide the required functionality.</li>
     * </ul>
     *
     * @throws Exception if any error occurs during the initialization process, such as database retrieval errors
     *                   or issues with setting up utilities.
     */
    @Override
    protected void onPluginEnable() throws Exception {
        instance = this;

        fileUtil = new FileUtil();
        parkUtil = new ParkUtil();

        attractionManager = new AttractionManager();
        autographManager = new AutographManager();
        buildUtil = new BuildUtil();
        configUtil = new ConfigUtil();
        delayUtil = new DelayUtil();
        fastPassKioskManager = new FastPassKioskManager();
        foodManager = new FoodManager();
        inventoryUtil = new InventoryUtil();
        leaderboardManager = new LeaderboardManager();
        magicBandManager = new MagicBandManager();
        outlineManager = new OutlineManager();
        packManager = new PackManager();
        playerUtil = new PlayerUtil();
        queueManager = new QueueManager();
        rideCounterUtil = new RideCounterUtil();
        scheduleManager = new ScheduleManager();
        showMenuManager = new ShowMenuManager();
        storageManager = new StorageManager();
        teleportUtil = new TeleportUtil();
        timeUtil = new TimeUtil();
        virtualQueueManager = new VirtualQueueManager();
        visibilityUtil = new VisibilityUtil();
        wardrobeManager = new WardrobeManager();
        shopManager = new ShopManager();

        storageManager.initialize();
        virtualQueueManager.initializeFromDatabase();

        registerListeners();
        registerCommands();
    }

    /**
     * Handles clean-up operations when the plugin is disabled.
     *
     * <p>This method performs the following actions on plugin shutdown:</p>
     * <ul>
     *     <li>Calls {@code clearQueues()} on the {@code virtualQueueManager} to remove all queues and free up resources.</li>
     *     <li>Iterates through all worlds in the Bukkit server and removes any {@code Minecart} entities from the game to
     *     ensure proper cleanup of leftover in-game entities.</li>
     * </ul>
     *
     * @throws Exception if an error occurs during the cleanup process, such as issues with removing entities or
     *                   clearing the virtual queues.
     */
    @Override
    protected void onPluginDisable() throws Exception {
        virtualQueueManager.clearQueues();
        Bukkit.getWorlds().forEach(world -> world.getEntities().stream().filter(e -> e instanceof Minecart).forEach(Entity::remove));
    }

    /**
     * Registers various commands for the plugin. This method is responsible for adding
     * all command instances utilized by the plugin, allowing players to interact with
     * the various functionalities provided by the ParkManager system.
     *
     * <p>The commands registered in this method include:</p>
     * <ul>
     *     <li><b>AddRideCounterCommand</b>: Command to add a ride counter for a specific player.</li>
     *     <li><b>AttractionCommand</b>: Command for managing attractions, such as creating or editing attractions.</li>
     *     <li><b>AutographCommand</b>: Command for managing autograph-related actions like signing or removing a signature.</li>
     *     <li><b>BackCommand</b>: Command allowing players to return to their previous location.</li>
     *     <li><b>BroadcastCommand</b>: Command for broadcasting a message to the entire server.</li>
     *     <li><b>BroadcastGlobalCommand</b>: Command for broadcasting messages globally to all servers.</li>
     *     <li><b>BuildCommand</b>: Provides access to build-related functionality for players.</li>
     *     <li><b>CosmeticsCommand</b>: Command related to managing and applying player cosmetics.</li>
     *     <li><b>DayCommand</b>: Command for switching the in-game time to daytime.</li>
     *     <li><b>DelayCommand</b>: Command for managing delays in various operations.</li>
     *     <li><b>FoodCommand</b>: Command for food-related features, such as meal preparation or consumption.</li>
     *     <li><b>HealCommand</b>: Command to fully heal a player.</li>
     *     <li><b>InvSeeCommand</b>: Enables players to view the inventory of other players.</li>
     *     <li><b>ItemCommand</b>: Command for retrieving or managing specific items.</li>
     *     <li><b>KioskCommand</b>: Command for interacting with FastPass or similar kiosks.</li>
     *     <li><b>LeaderboardCommand</b>: Command to view or interact with leaderboards.</li>
     *     <li><b>MoreCommand</b>: Command for increasing the stack count of items in a player's inventory.</li>
     *     <li><b>MuteChatCommand</b>: Allows toggling chat muting for the server or a specific player.</li>
     *     <li><b>NightCommand</b>: Command for switching the in-game time to nighttime.</li>
     *     <li><b>NightVisionCommand</b>: Provides night vision effects to a player.</li>
     *     <li><b>NoonCommand</b>: Command for setting the in-game time to midday.</li>
     *     <li><b>OutfitCommand</b>: Command to change or manage player outfits.</li>
     *     <li><b>OutlineCommand</b>: Command for toggling outlines or similar effects on blocks or players.</li>
     *     <li><b>PackCommand</b>: Handles the management or application of custom resource packs.</li>
     *     <li><b>ParkConfigCommand</b>: Command for configuring various park settings.</li>
     *     <li><b>PlayerTimeCommand</b>: Allows setting or customizing the time of day for specific players.</li>
     *     <li><b>PlayerWeatherCommand</b>: Allows setting or customizing the weather for specific players.</li>
     *     <li><b>QueueCommand</b>: Command for managing player queues, such as ride queues.</li>
     *     <li><b>SetSignCommand</b>: Command for setting or managing signs in the park.</li>
     *     <li><b>ShopCommand</b>: Command to manage or interact with in-game shops.</li>
     *     <li><b>ShowScheduleCommand</b>: Command to view or manage schedules for park shows.</li>
     *     <li><b>ShowsCommand</b>: Command for viewing or managing ongoing shows in the park.</li>
     *     <li><b>SignCommand</b>: Command related to general sign interactions or configurations.</li>
     *     <li><b>SpawnCommand</b>: Command for teleporting to or managing spawn locations.</li>
     *     <li><b>SpeedCommand</b>: Allows customization of player movement speeds.</li>
     *     <li><b>TeleportCommand</b>: Command for teleporting players to specific locations or other players.</li>
     *     <li><b>VirtualQueueCommand</b>: Command for managing virtual queue systems.</li>
     * </ul>
     *
     * <p>This method systematically registers commands essential for the functionality of
     * the plugin, ensuring players can execute needed operations through intuitive commands.</p>
     */
    public void registerCommands() {
        registerCommand(new AddRideCounterCommand());
        registerCommand(new AttractionCommand());
        registerCommand(new AutographCommand());
        registerCommand(new BackCommand());
        registerCommand(new BroadcastCommand());
        registerCommand(new BroadcastGlobalCommand());
        registerCommand(new BuildCommand());
        registerCommand(new CosmeticsCommand());
        registerCommand(new DayCommand());
        registerCommand(new DelayCommand());
        registerCommand(new FoodCommand());
        registerCommand(new HealCommand());
        registerCommand(new InvSeeCommand());
        registerCommand(new ItemCommand());
        registerCommand(new KioskCommand());
        registerCommand(new LeaderboardCommand());
        registerCommand(new MoreCommand());
        registerCommand(new MuteChatCommand());
        registerCommand(new NightCommand());
        registerCommand(new NightVisionCommand());
        registerCommand(new NoonCommand());
        registerCommand(new OutfitCommand());
        registerCommand(new OutlineCommand());
        registerCommand(new PackCommand());
        registerCommand(new ParkConfigCommand());
        registerCommand(new PlayerTimeCommand());
        registerCommand(new PlayerWeatherCommand());
        registerCommand(new QueueCommand());
        registerCommand(new SetSignCommand());
        registerCommand(new ShopCommand());
        registerCommand(new ShowScheduleCommand());
        registerCommand(new ShowsCommand());
        registerCommand(new SignCommand());
        registerCommand(new SpawnCommand());
        registerCommand(new SpeedCommand());
        registerCommand(new TeleportCommand());
        registerCommand(new VirtualQueueCommand());
    }

    /**
     * Registers all required event listeners for the `ParkManager` system.
     *
     * <p>This method adds multiple event handlers to ensure seamless interaction
     * between the players and the plugin's systems. These listeners cover a wide
     * range of events, such as block edits, player interactions, entity damages,
     * inventory changes, command handling, and more, enabling the functionality
     * provided by the `ParkManager`.</p>
     *
     * <p>The event listeners registered in this method include:</p>
     * <ul>
     *     <li><b>BlockEdit</b>: Handles modifications to blocks within the game world.</li>
     *     <li><b>ChatListener</b>: Manages player chat events and processes related interactions.</li>
     *     <li><b>CommandListener</b>: Intercepts player commands for custom functionalities.</li>
     *     <li><b>EntityDamage</b>: Tracks and manages damage events for entities.</li>
     *     <li><b>FoodLevel</b>: Monitors and handles changes to player food levels.</li>
     *     <li><b>InventoryListener</b>: Listens for and processes inventory-related actions.</li>
     *     <li><b>PacketListener</b>: Handles custom network packet interactions.</li>
     *     <li><b>PackManager</b>: Registers the plugin's custom resource pack-related functionalities.</li>
     *     <li><b>PlayerDropItem</b>: Tracks player item drop behaviors and applies related logic.</li>
     */
    public void registerListeners() {
        registerListener(new BlockEdit());
        registerListener(new ChatListener());
        registerListener(new CommandListener());
        registerListener(new EntityDamage());
        registerListener(new FoodLevel());
        registerListener(new InventoryListener());
        registerListener(new PacketListener());
        registerListener(packManager);
        registerListener(new PlayerDropItem());
        registerListener(new PlayerGameModeChange());
        registerListener(new PlayerInteract());
        registerListener(new PlayerJoinAndLeave());
        registerListener(new PlayerTeleport());
        registerListener(new RideListener());
        registerListener(new SignChange());
    }

    /**
     * Retrieves the current {@link Resort} configuration for the ParkManager system.
     *
     * <p>This method fetches the resort value using the {@code configUtil} utility, which
     * may be influenced by any active configuration settings or defaults defined
     * in the system.</p>
     *
     * @return the {@link Resort} currently configured, which could represent one of the following:
     * <ul>
     *     <li>WDW (Walt Disney World)</li>
     *     <li>DLR (Disneyland Resort)</li>
     *     <li>USO (Universal Studios Orlando)</li>
     * </ul>
     */
    public static Resort getResort() {
        return configUtil.getResort();
    }
}