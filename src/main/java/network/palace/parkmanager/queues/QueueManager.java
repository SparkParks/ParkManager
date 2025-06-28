package network.palace.parkmanager.queues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.utils.FileUtil;
import network.palace.parkmanager.utils.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The <code>QueueManager</code> class is responsible for managing and handling operations related to ride queues.
 * It supports functionalities such as retrieving, adding, removing, and managing queues for different parks.
 * This class also provides utilities for queue-specific operations such as fast pass charging, displaying sign particles,
 * and file persistence for queues.
 *
 * <p>The <code>QueueManager</code> class is designed to manage a collection of queues
 * associated with various park types. Queues can be accessed by their ID, name, associated sign, or unique identifier (UUID).
 * It also enables players to leave all queues or interact with queue-related features.</p>
 *
 * <h3>Features:</h3>
 * <ul>
 *   <li>Initialize and maintain a collection of queues.</li>
 *   <li>Retrieve queues based on attributes such as ID, name, or park type.</li>
 *   <li>Add and remove queues to/from the managed collection.</li>
 *   <li>Allow players to leave all queues they are part of.</li>
 *   <li>Charge players for fast pass access to queues.</li>
 *   <li>Display visual effects on signs associated with specific queues.</li>
 *   <li>Save the state of queues to a file for persistence.</li>
 * </ul>
 *
 * <h3>Methods:</h3>
 * <ul>
 *   <li><code>QueueManager()</code>: Constructs an instance of the QueueManager.</li>
 *   <li><code>initialize()</code>: Initializes the internal structures of the queue manager.</li>
 *   <li><code>List&lt;Queue&gt; getQueues(ParkType park)</code>: Retrieves all queues associated with the specified park type.</li>
 *   <li><code>Queue getQueueById(String id, ParkType park)</code>: Retrieves a queue by its ID for the specified park type.</li>
 *   <li><code>Queue getQueue(UUID uuid, ParkType park)</code>: Fetches a queue using its unique identifier (UUID) and associated park type.</li>
 *   <li><code>void addQueue(Queue queue)</code>: Adds a new queue to the managed collection.</li>
 *   <li><code>boolean removeQueue(String id, ParkType park)</code>: Removes a queue identified by its ID from the specified park type.</li>
 *   <li><code>void leaveAllQueues(CPlayer player)</code>: Removes the specified player from all active queues.</li>
 *   <li><code>Queue getQueue(Sign s)</code>: Retrieves a queue associated with a specific sign.</li>
 *   <li><code>Queue getQueueByName(String name, ParkType park)</code>: Retrieves a queue by its name for the specified park type.</li>
 *   <li><code>void saveToFile()</code>: Saves the current state of the queues to a file for persistence.</li>
 *   <li><code>boolean chargeFastPass(CPlayer player)</code>: Charges a fast pass fee to the specified player.</li>
 *   <li><code>void displaySignParticles(CPlayer player, Sign s)</code>: Displays visual effects on a sign for the specified player.</li>
 * </ul>
 *
 * <p>This class is meant to ensure smooth queue management for parks, facilitating user engagement
 * and maintaining queue-related consistency across the system.</p>
 */
public class QueueManager {
    /**
     * A list of {@link Queue} objects managed by the {@code QueueManager}.
     * <p>
     * This field is used to store all the active queues within the system.
     * It is initialized as an {@code ArrayList} to dynamically accommodate
     * multiple queues and provide efficient operations for addition and
     * retrieval of queues.
     * </p>
     *
     * <p>
     * The associated methods in {@code QueueManager} allow interaction with
     * this list, enabling functionality such as:
     * </p>
     * <ul>
     *   <li>Retrieving specific queues by ID, UUID, or name.</li>
     *   <li>Adding or removing queues.</li>
     *   <li>Interacting with queues based on player or park type criteria.</li>
     *   <li>Saving queue details to persistent storage.</li>
     * </ul>
     *
     * <p>
     * This field is private to encapsulate the queue list and ensure that
     * all modifications to the list are controlled through the
     * methods provided in the {@code QueueManager}. This enhances
     * data integrity and consistency within the queue management system.
     * </p>
     */
    private List<Queue> queues = new ArrayList<>();

    /**
     * Initializes the QueueManager and sets up periodic tasks to manage queues in a theme park system.
     *
     * <p>This class is responsible for managing multiple queues and handling queue-related functionality
     * including ticking queues, updating signs, and saving or loading queue configurations. The constructor performs
     * the following steps:
     *
     * <ul>
     *   <li>Initiates the queue list by calling the {@code initialize()} method, which clears and sets up queues
     *       for each park based on saved configurations.</li>
     *   <li>Calculates the delay required for synchronizing queue updates with the system clock.</li>
     *   <li>Registers a timed task that executes every 20 ticks to update all queues. This ensures that:
     *       <ul>
     *           <li>Each queue's state is periodically updated by calling their {@code tick()} method.</li>
     *           <li>Signs associated with queues are updated as required.</li>
     *           <li>Exceptions occurring during updates are logged for debugging purposes.</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * <p>The task scheduling is designed to minimize desynchronization caused by discrepancies in system time,
     * ensuring smoother queue management.
     */
    public QueueManager() {
        initialize();
        long time = System.currentTimeMillis();
        long milliseconds = time - TimeUtil.getCurrentSecondInMillis(time);
        long delay = (long) Math.floor(20 - ((milliseconds * 20) / 1000f));
        Core.runTaskTimer(ParkManager.getInstance(), () -> {
            long currentTime = TimeUtil.getCurrentSecondInMillis();
            queues.forEach(queue -> {
                try {
                    queue.tick(currentTime);
                    queue.updateSigns();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }, delay, 20L);
    }

    /**
     * Initializes the state of the queue system for the application and loads queue configurations.
     *
     * <p>This method is responsible for resetting the queue system, clearing existing queues, and
     * loading queue data from the file system. It processes queue configuration data for multiple parks
     * and ensures that each queue is properly instantiated and registered in the application. Any invalid
     * or malformed data in the configuration is skipped to maintain the integrity of the system.
     *
     * <p>The following operations are performed during initialization:
     * <ul>
     *     <li>Clears all existing queues by calling {@code emptyQueue()} on each queue and performs
     *         additional cleanup specific to {@code PluginQueue} instances.</li>
     *     <li>Resets the internal queue list by clearing the {@code queues} collection.</li>
     *     <li>Loads configuration files from the registered {@code "queue"} file subsystem, creating the
     *         subsystem if it does not already exist.</li>
     *     <li>Iterates through all parks to retrieve their queue data and instantiates queue objects based
     *         on the configuration.</li>
     *     <li>Handles different queue types (e.g., {@code BLOCK}, {@code CAROUSEL}, {@code TEACUPS}) by creating
     *         appropriate queue objects.</li>
     *     <li>Checks for potential duplicate queue IDs within the same park and resolves conflicts by appending
     *         an incrementing numerical suffix to conflicting IDs.</li>
     *     <li>Logs the successful loading of queues or reports errors encountered during initialization.</li>
     *     <li>Saves the newly loaded queue data back to the file system for persistence.</li>
     * </ul>
     *
     * <p>Errors encountered during the loading of queue configurations are logged, but they do not prevent
     * the initialization process from continuing for the remaining parks.
     *
     * <p>Typical use cases for this method may include server startup, configuration reloads, or resetting
     * the queue system to a known default state.
     */
    public void initialize() {
        queues.forEach(q -> {
            q.emptyQueue();
            if (q instanceof PluginQueue) ((PluginQueue) q).getRide().despawn();
        });
        queues.clear();
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("queue")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("queue");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("queue");
        }
        for (Park park : ParkManager.getParkUtil().getParks()) {
            try {
                JsonElement element = subsystem.getFileContents(park.getId().name().toLowerCase());
                boolean duplicateIdCheck = false;
                if (element.isJsonArray()) {
                    JsonArray array = element.getAsJsonArray();
                    for (JsonElement entry : array) {
                        JsonObject object = entry.getAsJsonObject();

                        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                        String name = ChatColor.translateAlternateColorCodes('&', object.get("name").getAsString());
                        QueueType type = QueueType.fromString(object.get("type").getAsString());
                        if (type == null) continue;
                        Location station = FileUtil.getLocation(object.getAsJsonObject("station"));
                        List<QueueSign> signs = new ArrayList<>();

                        JsonArray signArray = object.getAsJsonArray("signs");
                        for (JsonElement signElement : signArray) {
                            JsonObject signObject = (JsonObject) signElement;
                            signs.add(new QueueSign(FileUtil.getLocation(signObject), name, signObject.has("fastpass") && signObject.get("fastpass").getAsBoolean(), 0));
                        }
                        String id;
                        if (object.has("id")) {
                            id = object.get("id").getAsString();
                        } else {
                            id = object.get("warp").getAsString().toLowerCase();
                            duplicateIdCheck = true;
                        }
                        switch (type) {
                            case BLOCK:
                                Location blockLocation = FileUtil.getLocation(object.getAsJsonObject("block-location"));
                                queues.add(new BlockQueue(id, park.getId(), uuid, name, object.get("warp").getAsString(),
                                        object.get("group-size").getAsInt(), object.get("delay").getAsInt(),
                                        object.get("open").getAsBoolean(), station, signs, blockLocation));
                                break;
                            case CAROUSEL:
                            case TEACUPS:
                            case AERIAL_CAROUSEL:
                            case FILE:
                                queues.add(new PluginQueue(id, park.getId(), uuid, name, object.get("warp").getAsString(),
                                        object.get("group-size").getAsInt(), object.get("delay").getAsInt(),
                                        object.get("open").getAsBoolean(), station, signs,
                                        FileUtil.getLocation(object.getAsJsonObject("exit")), CurrencyType.BALANCE,
                                        object.get("balance").getAsInt(), object.get("honor").getAsInt(),
                                        object.get("achievement").getAsInt(), object.getAsJsonObject("rideConfig")));
                                break;
                        }
                    }
                }
                if (duplicateIdCheck) {
                    for (Queue queue : queues) {
                        for (Queue search : getQueues(park.getId())) {
                            if (queue.getUuid().equals(search.getUuid())) continue;
                            if (queue.getId().equals(search.getId())) {
                                int id = 2;
                                while (getQueueById(search.getId() + id, park.getId()) != null) {
                                    id++;
                                }
                                search.setId(search.getId() + id);
                                break;
                            }
                        }
                    }
                }
                Core.logMessage("QueueManager", "Loaded " + queues.size() + " queue" + TextUtil.pluralize(queues.size()) + " for park " + park.getId().getTitle() + "!");
            } catch (Exception e) {
                Core.logMessage("QueueManager", "There was an error loading the QueueManager config for park " + park.getId().getTitle() + "!");
                e.printStackTrace();
            }
        }
        saveToFile();
    }

    /**
     * Retrieves a list of {@code Queue} objects associated with the specified {@code ParkType}.
     *
     * <p>This method filters the existing queues and returns only those that belong
     * to the provided {@code park} parameter.</p>
     *
     * @param park the {@code ParkType} for which the queues are to be retrieved.
     * @return a {@code List} of {@code Queue} objects associated with the specified {@code ParkType}.
     */
    public List<Queue> getQueues(ParkType park) {
        return queues.stream().filter(queue -> queue.getPark().equals(park)).collect(Collectors.toList());
    }

    /**
     * Retrieves a queue by its unique identifier within the specified park type.
     *
     * <p>This method iterates through the list of queues associated with the given park
     * type and returns the queue that matches the specified identifier. If no matching
     * queue is found, the method returns <code>null</code>.
     *
     * @param id The unique identifier of the queue to retrieve.
     * @param park The type of park to search for the queue.
     * @return The {@link Queue} object with the specified identifier, or <code>null</code> if no match is found.
     */
    public Queue getQueueById(String id, ParkType park) {
        for (Queue queue : getQueues(park)) {
            if (queue.getId().equals(id)) {
                return queue;
            }
        }
        return null;
    }

    /**
     * Retrieves a queue from the list of queues in the specified park that matches the given UUID.
     * <p>
     * If no match is found, the method returns {@code null}.
     * </p>
     *
     * @param uuid the unique identifier of the queue to retrieve
     * @param park the park type containing the queues to search
     * @return the {@link Queue} with the specified UUID in the given park, or {@code null} if no match is found
     */
    public Queue getQueue(UUID uuid, ParkType park) {
        for (Queue queue : getQueues(park)) {
            if (queue.getUuid().equals(uuid)) {
                return queue;
            }
        }
        return null;
    }

    /**
     * Adds a queue to the list of managed queues and persists the change to a file.
     *
     * <p>This method allows a new {@code Queue} object to be added to the internal
     * collection of queues managed by the {@code QueueManager} instance. After adding the
     * queue, the updated list is saved to a file to ensure data persistence.
     *
     * @param queue The {@code Queue} object to be added. It cannot be {@code null}.
     *              The queue must represent a valid entity to be stored and managed.
     */
    public void addQueue(Queue queue) {
        queues.add(queue);
        saveToFile();
    }

    /**
     * Removes a queue identified by its unique ID from the specified park, if it exists.
     *
     * <p>This method locates the queue associated with the given ID within the specified park.
     * If the queue is found, it performs cleanup operations, including despawning a ride
     * if the queue is a {@code PluginQueue}, removes the queue from the internal list of queues,
     * and saves the updated state. If the queue does not exist, no action is taken.</p>
     *
     * @param id the unique identifier of the queue to be removed. Must not be {@code null}.
     * @param park the {@link ParkType} where the queue is located. This specifies the context for the queue lookup.
     *
     * @return {@code true} if the queue was successfully removed, {@code false} if no matching queue was found.
     */
    public boolean removeQueue(String id, ParkType park) {
        Queue queue = getQueueById(id, park);
        if (queue == null) return false;
        if (queue instanceof PluginQueue) ((PluginQueue) queue).getRide().despawn();
        queues.remove(queue);
        saveToFile();
        return true;
    }

    /**
     * Removes the specified player from all queues managed by the system.
     *
     * <p>This method iterates through all registered queues and ensures that the player is removed from
     * each queue. The removal may be performed forcibly depending on the implementation in the underlying
     * queue logic. This action is performed for both standard queues and FastPass queues if applicable.</p>
     *
     * @param player the {@code CPlayer} instance representing the player to be removed from all queues
     */
    public void leaveAllQueues(CPlayer player) {
        queues.forEach(q -> q.leaveQueue(player, true));
    }

    /**
     * Retrieves the {@link Queue} associated with a given {@link Sign}.
     * <p>
     * This method iterates through the list of queues associated with the park
     * identified by the given sign's location and returns the queue that has a
     * matching {@link QueueSign} location. If no matching queue is found, the
     * method returns <code>null</code>.
     *
     * @param s The {@link Sign} object whose associated {@link Queue} is to be retrieved.
     *          Must not be <code>null</code>.
     * @return The {@link Queue} associated with the specified {@link Sign},
     *         or <code>null</code> if no associated queue is found.
     */
    public Queue getQueue(Sign s) {
        for (Queue queue : getQueues(ParkManager.getParkUtil().getPark(s.getLocation()).getId())) {
            for (QueueSign sign : queue.getSigns()) {
                if (sign.getLocation().equals(s.getLocation())) {
                    return queue;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a {@link Queue} object by matching its name (partially or fully) within the specified {@link ParkType}.
     * The method removes color codes from the provided name and the queue names for comparison purposes.
     *
     * <p>Iterates over all queues associated with the given park and checks if the cleaned name of any queue
     * starts with the provided (cleaned) name. If a match is found, it returns the queue; otherwise, returns {@code null}.
     *
     * @param name The (possibly colored) name used to search for a matching queue. Color codes will be stripped during the comparison.
     * @param park The {@link ParkType} that specifies which park's queues are being searched.
     * @return The matching {@link Queue} object if found, or {@code null} if no match is found.
     */
    public Queue getQueueByName(String name, ParkType park) {
        name = ChatColor.stripColor(name);
        for (Queue queue : getQueues(park)) {
            if (ChatColor.stripColor(queue.getName()).startsWith(name)) return queue;
        }
        return null;
    }

    /**
     * Saves the current state of queues associated with parks to a file for persistence.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Sorts the list of queues based on their names in a case-insensitive manner, ignoring any colors present in the names.</li>
     *   <li>Iterates through each park managed by the {@link ParkManager} and generates a JSON representation of all queues associated with the park.</li>
     *   <li>Extracts and formats the following attributes of each queue for serialization:
     *       <ul>
     *         <li>Queue ID, UUID, name (with color codes replaced), warp location.</li>
     *         <li>Station information, group size, delay, and operational status.</li>
     *         <li>List of associated signs and their locations, including fast-pass designation if applicable.</li>
     *         <li>Queue type and its specific configuration, which may differ for various ride types (e.g., BLOCK, CAROUSEL, TEACUPS, AERIAL_CAROUSEL).</li>
     *       </ul>
     *   </li>
     *   <li>Handles custom configurations for specific queue types:
     *       <ul>
     *         <li>For {@code BLOCK} queues, the block location is added.</li>
     *         <li>For {@code CAROUSEL}, {@code TEACUPS}, {@code AERIAL_CAROUSEL}, or {@code FILE}, additional ride configurations such as center point, radius, or file references
     *  are serialized.</li>
     *         <li>The {@code AERIAL_CAROUSEL} type also includes detailed parameters such as support radius, height, and movement attributes.</li>
     *       </ul>
     *   </li>
     *   <li>Writes the generated JSON data to the corresponding file for each park using the {@link FileUtil} subsystem.</li>
     * </ul>
     *
     * <p>If an error occurs during the file-writing process, an error message is logged, and the exception stack trace is printed to assist in debugging.</p>
     */
    public void saveToFile() {
        queues.sort(Comparator.comparing(o -> ChatColor.stripColor(o.getName().toLowerCase())));
        for (Park park : ParkManager.getParkUtil().getParks()) {
            JsonArray array = new JsonArray();
            queues.stream().filter(queue -> queue.getPark().equals(park.getId())).forEach(queue -> {
                JsonObject object = new JsonObject();
                object.addProperty("id", queue.getId());
                object.addProperty("uuid", queue.getUuid().toString());
                object.addProperty("name", queue.getName().replaceAll(ChatColor.COLOR_CHAR + "", "&"));
                object.addProperty("warp", queue.getWarp());
                object.add("station", FileUtil.getJson(queue.getStation()));
                object.addProperty("group-size", queue.getGroupSize());
                object.addProperty("delay", queue.getDelay());
                object.addProperty("open", queue.isOpen());

                JsonArray signArray = new JsonArray();
                for (QueueSign sign : queue.getSigns()) {
                    JsonObject signObject = FileUtil.getJson(sign.getLocation());
                    if (sign.isFastPassSign()) signObject.addProperty("fastpass", true);
                    signArray.add(signObject);
                }
                object.add("signs", signArray);
                object.addProperty("type", queue.getQueueType().name().toLowerCase());

                switch (queue.getQueueType()) {
                    case BLOCK:
                        object.add("block-location", FileUtil.getJson(((BlockQueue) queue).getBlockLocation()));
                        break;
                    case CAROUSEL: {
                        object.addProperty("balance", ((PluginQueue) queue).getCurrencyAmount());
                        object.addProperty("honor", ((PluginQueue) queue).getHonorAmount());
                        object.addProperty("achievement", ((PluginQueue) queue).getAchievementId());
                        object.add("exit", FileUtil.getJson(((PluginQueue) queue).getRide().getExit()));
                        JsonObject rideConfig = new JsonObject();
                        rideConfig.addProperty("rideType", "CAROUSEL");
                        rideConfig.add("center", FileUtil.getJson(((network.palace.ridemanager.handlers.ride.flat.CarouselRide) ((PluginQueue) queue).getRide()).getCenter()));
                        object.add("rideConfig", rideConfig);
                        break;
                    }
                    case TEACUPS: {
                        object.addProperty("balance", ((PluginQueue) queue).getCurrencyAmount());
                        object.addProperty("honor", ((PluginQueue) queue).getHonorAmount());
                        object.addProperty("achievement", ((PluginQueue) queue).getAchievementId());
                        object.add("exit", FileUtil.getJson(((PluginQueue) queue).getRide().getExit()));
                        JsonObject rideConfig = new JsonObject();
                        rideConfig.addProperty("rideType", "TEACUPS");
                        rideConfig.add("center", FileUtil.getJson(((network.palace.ridemanager.handlers.ride.flat.TeacupsRide) ((PluginQueue) queue).getRide()).getCenter()));
                        object.add("rideConfig", rideConfig);
                        break;
                    }
                    case AERIAL_CAROUSEL: {
                        object.addProperty("balance", ((PluginQueue) queue).getCurrencyAmount());
                        object.addProperty("honor", ((PluginQueue) queue).getHonorAmount());
                        object.addProperty("achievement", ((PluginQueue) queue).getAchievementId());
                        object.add("exit", FileUtil.getJson(((PluginQueue) queue).getRide().getExit()));

                        JsonObject rideConfig = new JsonObject();
                        rideConfig.addProperty("rideType", "AERIAL_CAROUSEL");
                        rideConfig.add("center", FileUtil.getJson(((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getCenter()));
                        rideConfig.addProperty("aerialRadius", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getAerialRadius());
                        rideConfig.addProperty("supportRadius", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getSupportRadius());
                        rideConfig.addProperty("small", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).isSmall());
                        rideConfig.addProperty("supportAngle", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getSupportAngle());
                        rideConfig.addProperty("height", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getHeight());
                        rideConfig.addProperty("movein", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getMovein());

                        object.add("rideConfig", rideConfig);
                        break;
                    }
                    case FILE: {
                        object.addProperty("balance", ((PluginQueue) queue).getCurrencyAmount());
                        object.addProperty("honor", ((PluginQueue) queue).getHonorAmount());
                        object.addProperty("achievement", ((PluginQueue) queue).getAchievementId());
                        object.add("exit", FileUtil.getJson(((PluginQueue) queue).getRide().getExit()));
                        JsonObject rideConfig = new JsonObject();
                        rideConfig.addProperty("rideType", "FILE");
                        String fileName = ((network.palace.ridemanager.handlers.ride.file.FileRide) ((PluginQueue) queue).getRide()).getRideFile().getName();
                        rideConfig.addProperty("file", fileName.substring(0, fileName.indexOf('.')));
                        object.add("rideConfig", rideConfig);
                        break;
                    }
                }
                array.add(object);
            });
            try {
                ParkManager.getFileUtil().getSubsystem("queue").writeFileContents(park.getId().name().toLowerCase(), array);
            } catch (IOException e) {
                Core.logMessage("QueueManager", "There was an error writing to the QueueManager config: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Charges a fast pass for the given player by decrementing their fastPassCount
     * in the player's registry. The method ensures the player has sufficient
     * fast passes before performing the charge operation.
     * <p>
     * If the fast pass count is valid, the operation is done both locally and
     * asynchronously persisted in the database.
     *
     * @param player the {@code CPlayer} who is being charged for the fast pass.
     *               It must contain a valid registry entry "fastPassCount" that
     *               represents the current number of available fast passes.
     * <p>
     * @return {@code true} if the fast pass was successfully charged (indicating the player had at least
     *         one fast pass available prior to the operation); {@code false} otherwise.
     */
    public boolean chargeFastPass(CPlayer player) {
        int newCount = ((int) player.getRegistry().getEntry("fastPassCount")) - 1;
        if (newCount < 0) return false;
        player.getRegistry().addEntry("fastPassCount", newCount);
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().chargeFastPass(player.getUniqueId(), 1));
        return true;
    }

    /**
     * Displays particles around a given sign location to create a visual effect for the player.
     * <p>
     * This method determines the attachment face of the provided sign and calculates the positions on
     * the edges of the sign. It then emits {@code Particle.SPELL_WITCH} particles in rows and columns
     * surrounding the sign to create a border effect.
     * </p>
     *
     * @param player the player to whom the particles should be displayed.
     *               The method ensures the visual effect is personalized for the specified player.
     * @param s the sign whose location and orientation determine the particle effect's position.
     *          The sign must not be attached to {@code BlockFace.DOWN}.
     */
    public void displaySignParticles(CPlayer player, Sign s) {
        org.bukkit.material.Sign sign = (org.bukkit.material.Sign) s.getData();
        BlockFace attached = sign.getAttachedFace();
        if (attached.equals(BlockFace.DOWN)) return;
        Location loc = s.getLocation(), c1, c2, c3, c4;
        switch (attached.getOppositeFace()) {
            case NORTH:
                c1 = loc.clone().add(0.95, 0.75, 0.9);
                c2 = loc.clone().add(0.05, 0.75, 0.9);
                c3 = loc.clone().add(0.95, 0.25, 0.9);
                c4 = loc.clone().add(0.05, 0.25, 0.9);
                for (int i = 0; i < 9; i++) {
                    // Top row
                    player.getParticles().send(c1.add(-0.1, 0, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Right side
                    player.getParticles().send(c2.add(0, -0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Left side
                    player.getParticles().send(c3.add(0, 0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 9; i++) {
                    // Bottom row
                    player.getParticles().send(c4.add(0.1, 0, 0), Particle.SPELL_WITCH, 1);
                }
                break;
            case EAST:
                c1 = loc.clone().add(0.1, 0.75, 0.95);
                c2 = loc.clone().add(0.1, 0.75, 0.05);
                c3 = loc.clone().add(0.1, 0.25, 0.95);
                c4 = loc.clone().add(0.1, 0.25, 0.05);
                for (int i = 0; i < 9; i++) {
                    // Top row
                    player.getParticles().send(c1.add(0, 0, -0.1), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Right side
                    player.getParticles().send(c2.add(0, -0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Left side
                    player.getParticles().send(c3.add(0, 0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 9; i++) {
                    // Bottom row
                    player.getParticles().send(c4.add(0, 0, 0.1), Particle.SPELL_WITCH, 1);
                }
                break;
            case SOUTH:
                c1 = loc.clone().add(0.05, 0.75, 0.1);
                c2 = loc.clone().add(0.95, 0.75, 0.1);
                c3 = loc.clone().add(0.05, 0.25, 0.1);
                c4 = loc.clone().add(0.95, 0.25, 0.1);
                for (int i = 0; i < 9; i++) {
                    // Top row
                    player.getParticles().send(c1.add(0.1, 0, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Right side
                    player.getParticles().send(c2.add(0, -0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Left side
                    player.getParticles().send(c3.add(0, 0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 9; i++) {
                    // Bottom row
                    player.getParticles().send(c4.add(-0.1, 0, 0), Particle.SPELL_WITCH, 1);
                }
                break;
            case WEST:
                c1 = loc.clone().add(0.9, 0.75, 0.05);
                c2 = loc.clone().add(0.9, 0.75, 0.95);
                c3 = loc.clone().add(0.9, 0.25, 0.05);
                c4 = loc.clone().add(0.9, 0.25, 0.95);
                for (int i = 0; i < 9; i++) {
                    // Top row
                    player.getParticles().send(c1.add(0, 0, 0.1), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Right side
                    player.getParticles().send(c2.add(0, -0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Left side
                    player.getParticles().send(c3.add(0, 0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 9; i++) {
                    // Bottom row
                    player.getParticles().send(c4.add(0, 0, -0.1), Particle.SPELL_WITCH, 1);
                }
                break;
        }
    }
}
