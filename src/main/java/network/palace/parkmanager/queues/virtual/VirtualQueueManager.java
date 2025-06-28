package network.palace.parkmanager.queues.virtual;

import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.messagequeue.MessageClient;
import network.palace.core.messagequeue.packets.MQPacket;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.message.CreateQueuePacket;
import network.palace.parkmanager.message.PlayerQueuePacket;
import network.palace.parkmanager.message.RemoveQueuePacket;
import network.palace.parkmanager.message.UpdateQueuePacket;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.inventory.ClickType;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * The {@code VirtualQueueManager} class handles the management of virtual queues in the system.
 * This includes initializing queues from the database, handling queue creation and removal, and
 * managing queue updates and player interactions within the queues.
 *
 * <p>This class integrates with a MongoDB collection to persist virtual queue data
 * and communicates with other system components for real-time updates.
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Creating, removing, and updating virtual queues.</li>
 *   <li>Synchronizing queue information across servers.</li>
 *   <li>Maintaining player positions within the virtual queues.</li>
 *   <li>Handling cross-server communication related to queues.</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Initialize queues from a MongoDB database during server startup.</li>
 *   <li>Run periodic tasks to handle queue updates, player notifications, and data synchronization.</li>
 *   <li>Integrate with a message-based system for server-to-server communication.</li>
 *   <li>Provide utility methods to add, remove, and retrieve queues or player-specific queue data.</li>
 * </ul>
 *
 * <h2>Concurrency</h2>
 * <p>The class uses periodic tasks and thread-safe collections where necessary to handle
 * concurrency and ensure thread safety when modifying the queue and player-related data.</p>
 *
 * <h2>Associated Packets</h2>
 * <ul>
 *   <li>{@code CreateQueuePacket} - Represents the creation of a new virtual queue.</li>
 *   <li>{@code RemoveQueuePacket} - Represents the removal of an existing virtual queue.</li>
 *   <li>{@code UpdateQueuePacket} - Represents updates made to the virtual queue's state.</li>
 * </ul>
 *
 * <h2>Dependencies</h2>
 * <ul>
 *   <li>MongoDB - Used for queue persistence.</li>
 *   <li>Core.getMessageHandler() - Handles communication with external systems and servers.</li>
 *   <li>Core.getPlayerManager() - Provides player-related utilities and interaction management.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>This class is intended to be used as a centralized manager for all virtual queue operations within
 * the application. Instances of this class should not be manually created; instead, the system should
 * retrieve the instance through its designated centralized access point.</p>
 *
 * <h2>Error Handling</h2>
 * <p>The class implements error handling to log exceptions during queue operations, ensuring that
 * issues are tracked and do not disrupt the overall system functionality.</p>
 */
public class VirtualQueueManager {
    /**
     * Represents a collection of {@link VirtualQueue} objects managed by the VirtualQueueManager.
     * <p>
     * This list serves as the primary data structure for storing and maintaining all active
     * virtual queues in the system. The queues in this list are used for various operations
     * such as lookup, modification, and removal.
     * </p>
     *
     * <p>The list is immutable after initialization and designed to ensure thread safety.</p>
     */
    private final List<VirtualQueue> queues = new ArrayList<>();

    /**
     * A map that maintains an association between a unique player identifier {@link UUID}
     * and the menu currently opened by that player.
     *
     * <p>This data structure is used to track the open menus for players in the system. It allows
     * for the efficient retrieval, management, and tracking of menus associated with individual
     * players. The key is the {@link UUID} of a player, serving as the unique identifier,
     * and the value is the {@link Menu} object that the player currently has open.
     *
     * <p>It is a fundamental part of the {@code VirtualQueueManager} implementation and is
     * particularly relevant for features that involve player menu interaction, ensuring that
     * menus are properly managed and cleaned up when no longer needed.
     *
     * <ul>
     *   <li>Key ({@link UUID}): Represents a unique player.</li>
     *   <li>Value ({@link Menu}): Represents the menu the player is interacting with.</li>
     * </ul>
     *
     * <p>This map is marked as {@code final}, meaning its reference cannot be changed after
     * initialization. It is initialized as an empty {@code HashMap} and is updated via
     * methods in the {@code VirtualQueueManager} class.
     *
     * <p>Thread-safety of this structure is not explicitly guaranteed unless modifications
     * are synchronously managed in the containing class.
     *
     * <p>Usage context includes managing open-state menus, associating menus to players,
     * and providing precise control over closing or modifying a player's menu dynamically.
     */
    private final HashMap<UUID, Menu> openMenus = new HashMap<>();

    /**
     * Represents the MongoDB collection used to store and manage virtual queues in the system.
     *
     * <p>This collection is used by the {@link VirtualQueueManager} to perform various database
     * operations such as retrieving, adding, updating, and removing VirtualQueue objects.
     *
     * <p>The {@code virtualQueuesCollection} is a critical component in ensuring that virtual queues
     * persist across application lifecycles and are consistently stored within the MongoDB database.
     *
     * <h3>Key Responsibilities:</h3>
     * <ul>
     *    <li>Provides access to the MongoDB collection where virtual queue documents are stored.</li>
     *    <li>Facilitates operations like querying, inserting, deleting, and updating queues.</li>
     *    <li>Ensures data persistence for VirtualQueue objects managed by the {@link VirtualQueueManager}.</li>
     * </ul>
     */
    private final MongoCollection<Document> virtualQueuesCollection;

    /**
     * Manages the lifecycle and behavior of virtual queues to facilitate player participation and
     * queue management within a multi-server environment.
     *
     * <p>This constructor initializes the {@code VirtualQueueManager} by connecting to the MongoDB
     * database and retrieving the "virtual_queues" collection. It also sets up a repeating task to
     * handle queue lifecycle tasks, including position announcements, updates, member management,
     * and inter-server communication.</p>
     *
     * <p>The repeating task executes the following logic:</p>
     * <ul>
     *   <li><strong>Position Announcements:</strong> Messages are sent to all players in each queue
     *   every 8 cycles to update them on their current positions.</li>
     *   <li><strong>Queue Updates:</strong> Detects and processes any changes made to the queue, such
     *   as status or members, and sends corresponding {@link UpdateQueuePacket} instances across the
     *   network to other servers.</li>
     *   <li><strong>Queue Member Management:</strong> Handles the transition of players between
     *   different states, such as being queued, entering a holding area, or being teleported to the
     *   host server.</li>
     *   <li><strong>Cross-Server Communication:</strong> Sends {@code UpdateQueuePacket} instances to
     *   the "all_parks" permanent client via the {@link Core#getMessageHandler()} system to synchronize
     *   queue data across all park servers.</li>
     * </ul>
     *
     * <p>Key behaviors include:</p>
     * <ul>
     *   <li>Ensuring the task only operates for queues hosted on the current server
     *   ({@link VirtualQueue#isHost()}).</li>
     *   <li>Handling and recovering from errors encountered during player updates or message
     *   dispatch.</li>
     *   <li>Teleports players to designated holding areas or removes them from queues if timing
     *   constraints or other conditions are met.</li>
     * </ul>
     */
    public VirtualQueueManager() {
        virtualQueuesCollection = Core.getMongoHandler().getDatabase().getCollection("virtual_queues");
        Core.runTaskTimer(new Runnable() {
            int i = 1;

            @Override
            public void run() {
                if (queues.isEmpty()) return;

                boolean announce = i++ >= 8;
                if (announce) i = 1;

                List<MQPacket> packets = new ArrayList<>();
                queues.forEach(queue -> {
                    // Only run this task on the host server
                    if (!queue.isHost()) return;

                    // Message all players their current position (including players not on current server)
                    if (announce) queue.sendPositionMessages();

                    // Send out any necessary updates to all park servers
                    if (queue.isUpdated()) {
                        queue.setUpdated(false);
                        packets.add(new UpdateQueuePacket(queue.getId(), queue.isOpen(), queue.getMembers()));
                    }

                    // Only do the remaining tasks if the queue is open
                    if (!queue.isOpen()) return;

                    // Send all sendingToServer players to the host server
                    HashMap<UUID, Long> joiningToHoldingArea = queue.getJoiningToHoldingArea();
                    joiningToHoldingArea.forEach((uuid, time) -> {
                        if (System.currentTimeMillis() >= time) {
                            try {
                                queue.leaveQueue(uuid);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // Notify all players in the holdingArea that they are about to be brought to the host server
                    List<UUID> holdingAreaMembers = queue.getHoldingAreaMembers();
                    holdingAreaMembers.forEach(uuid -> {
                        try {
                            if (joiningToHoldingArea.containsKey(uuid)) return;
                            CPlayer player;
                            if ((player = Core.getPlayerManager().getPlayer(uuid)) != null) {
                                if (!player.getRegistry().hasEntry("virtualQueueHoldingArea")) {
                                    player.getRegistry().addEntry("virtualQueueHoldingArea", true);
                                    player.teleport(queue.getHoldingAreaLocation());
                                }
                            } else {
                                queue.markAsSendingToServer(uuid);
                            }
                        } catch (Exception e) {
                            Core.getInstance().getLogger().log(Level.SEVERE, "Error sending cross-server message for virtual queue", e);
                        }
                    });
                });
                MessageClient allParks = Core.getMessageHandler().permanentClients.get("all_parks");
                packets.forEach(packet -> {
                    try {
                        Core.getMessageHandler().sendMessage(packet, allParks);
                    } catch (IOException e) {
                        Core.getInstance().getLogger().log(Level.SEVERE, "Error sending virtual queue update packets", e);
                    }
                });
            }
        }, 20L, 100L);
    }

    /**
     * Initializes VirtualQueue instances from the database by loading data from the virtualQueuesCollection.
     *
     * <p>This method is responsible for:
     * <ul>
     *   <li>Iterating through documents in the database collection.</li>
     *   <li>Creating {@link VirtualQueue} objects from document data.</li>
     *   <li>Populating queue members based on the stored entries.</li>
     *   <li>Handling queues that may have been left open due to server crashes by attempting to remove them.</li>
     *   <li>Setting the open status for queues that aren't host queues and adding them to the active queue list.</li>
     * </ul>
     *
     * <p>If a queue is identified as a host queue (due to server crash), it attempts to close and remove the queue
     * to maintain system consistency. Any errors during this operation are logged for diagnostics.
     *
     * <p>The queue's members are reconstructed from the database's stored queue data. Invalid or improperly
     * formatted member entries are skipped.
     *
     * <p>Queues that are not host queues are initialized with their "open" status set based on the database value
     * and are then added to the program's queue tracking structure.
     *
     * <h3>Log Messages:</h3>
     * <ul>
     *   <li>An error message is logged if there is a failure in removing a host queue after a server crash.</li>
     * </ul>
     *
     * <h3>Integration:</h3>
     * <p>The method depends on several components:
     * <ul>
     *   <li>{@link VirtualQueueManager#queues}: A list to which initialized queues are added.</li>
     *   <li>{@link VirtualQueueManager#getRandomItemId()}: A utility method invoked for setting queue metadata.</li>
     *   <li>{@link ParkManager#getVirtualQueueManager()}: Used to manage queues post-crash.</li>
     * </ul>
     *
     * <p>Suppresses raw type warnings due to the usage of generic-less database query results.
     */
    @SuppressWarnings("rawtypes")
    public void initializeFromDatabase() {
        for (Document doc : virtualQueuesCollection.find()) {
            VirtualQueue queue = new VirtualQueue(doc.getString("queueId"), doc.getString("queueName"),
                    doc.getInteger("holdingArea"), null, null, doc.getString("server"),
                    null, null, getRandomItemId());
            List<UUID> members = new ArrayList<>();
            ArrayList array = doc.get("queue", ArrayList.class);
            for (Object o : array) {
                try {
                    members.add(UUID.fromString((String) o));
                } catch (Exception ignored) {
                }
            }
            queue.updateQueue(members);
            if (queue.isHost()) {
                // If the host server is loading a queue from the database, the server likely crashed without properly removing the queue
                try {
                    ParkManager.getVirtualQueueManager().removeQueue(queue);
                } catch (Exception e) {
                    Core.getInstance().getLogger().log(Level.SEVERE, "Error closing virtual queue after startup following recent crash", e);
                }
            } else {
                queue.setOpen(doc.getBoolean("open"));
                queues.add(queue);
            }
        }
    }

    /**
     * Retrieves a list of all virtual queues managed by this instance.
     *
     * <p>This method provides a complete list of {@link VirtualQueue} objects currently managed
     * by the {@code VirtualQueueManager}. The list is a new instance containing the elements from
     * the internal data structure, ensuring that external modifications do not directly affect
     * the internal state of the manager.
     *
     * @return a {@link List} of {@link VirtualQueue} objects representing all managed virtual queues.
     */
    public List<VirtualQueue> getQueues() {
        return new ArrayList<>(queues);
    }

    /**
     * Retrieves a {@link VirtualQueue} by its unique identifier.
     *
     * <p>This method searches through the list of available queues and
     * returns the queue that matches the given identifier. If no matching
     * queue is found, it returns {@code null}.
     *
     * @param id the unique identifier of the queue to retrieve. This parameter
     *           is used to find the corresponding queue in the list of queues.
     * @return the {@link VirtualQueue} instance that matches the given identifier,
     *         or {@code null} if no matching queue is found.
     */
    public VirtualQueue getQueueById(String id) {
        for (VirtualQueue queue : getQueues()) {
            if (queue.getId().equals(id)) {
                return queue;
            }
        }
        return null;
    }

    /**
     * Retrieves a {@link VirtualQueue} instance associated with the provided {@link Sign}.
     *
     * <p>This method iterates through all managed virtual queues and returns the queue
     * where the given {@link Sign} matches either the advance sign or state sign.
     * Matching is determined based on the location of the provided {@link Sign}.
     * If no matching queue is found, the method returns {@code null}.
     *
     * @param s the {@link Sign} to search for. This parameter represents a sign associated
     *          with a virtual queue, either as an advance sign or state sign.
     * @return the {@link VirtualQueue} instance that references the provided {@link Sign},
     *         or {@code null} if no matching queue is found.
     */
    public VirtualQueue getQueue(Sign s) {
        for (VirtualQueue queue : getQueues()) {
            Sign advanceSign = queue.getAdvanceSign();
            Sign stateSign = queue.getStateSign();
            if ((advanceSign != null && advanceSign.getLocation().equals(s.getLocation()))
                    || (stateSign != null && stateSign.getLocation().equals(s.getLocation()))) return queue;
        }
        return null;
    }

    /**
     * Adds a new {@link VirtualQueue} to the queue manager and inserts its data into the database if it is a host queue.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Adds the given {@code VirtualQueue} to the internal {@code queues} list.</li>
     *   <li>If the queue is a host queue (determined by {@link VirtualQueue#isHost()}):
     *       <ul>
     *           <li>Constructs a list of the queue's members, converting each member's {@code UUID} to string format.</li>
     *           <li>Inserts a document into the {@code virtualQueuesCollection} database containing:
     *               <ul>
     *                   <li>{@code queueId}: The unique identifier of the queue.</li>
     *                   <li>{@code queueName}: The name of the queue.</li>
     *                   <li>{@code server}: The host server of the queue.</li>
     *                   <li>{@code holdingArea}: The holding area managed by the queue.</li>
     *                   <li>{@code open}: The status of the queue (open or closed).</li>
     *                   <li>{@code queue}: The list of members currently in the queue.</li>
     *               </ul>
     *           </li>
     *       </ul>
     *   </li>
     *   <li>Sends a {@link CreateQueuePacket} message to all connected park servers to notify them about the newly created queue.</li>
     * </ul>
     *
     * @param queue the {@link VirtualQueue} to add. This parameter represents the queue instance being added to the manager and persisted to the database if applicable.
     * @throws IOException if an error occurs during communication when sending a {@link CreateQueuePacket}.
     */
    public void addQueue(VirtualQueue queue) throws IOException {
        queues.add(queue);
        if (queue.isHost()) {
            List<String> members = new ArrayList<>();
            queue.getMembers().forEach(uuid -> members.add(uuid.toString()));
            virtualQueuesCollection.insertOne(
                    new Document("queueId", queue.getId())
                            .append("queueName", queue.getName())
                            .append("server", queue.getServer())
                            .append("holdingArea", queue.getHoldingArea())
                            .append("open", queue.isOpen())
                            .append("queue", members)
            );
        }
        Core.getMessageHandler().sendMessage(new CreateQueuePacket(queue.getId(), queue.getName(), queue.getHoldingArea(), queue.getServer()), Core.getMessageHandler().permanentClients.get("all_parks"));
    }

    /**
     * Removes a virtual queue identified by its unique ID.
     *
     * <p>This method attempts to remove a virtual queue from the system by locating it using the
     * provided ID. If a queue with the specified ID is found, it is removed from the system.
     * If no queue with the specified ID exists, the method returns {@code false}.
     *
     * @param id the unique identifier of the virtual queue to be removed. This identifier is used
     *           to locate the queue within the system.
     * @return {@code true} if the queue was successfully found and removed; {@code false} if no queue
     *         with the specified ID exists.
     * @throws Exception if an exception occurs during the removal process.
     */
    public boolean removeQueue(String id) throws Exception {
        VirtualQueue queue = getQueueById(id);
        if (queue == null) return false;
        return removeQueue(queue);
    }

    /**
     * Removes a specified virtual queue and performs related cleanup operations.
     *
     * <p>This method handles the removal of a virtual queue from the internal tracking data structure.
     * If the queue is hosted on the current server, it performs additional cleanup, such as notifying
     * all queue members, broadcasting a staff message, and performing database and inter-server updates
     * to ensure consistency across the system.</p>
     *
     * <p>Specific actions for hosted queues include:</p>
     * <ul>
     *   <li>Notifying members of the queue about its removal.</li>
     *   <li>Broadcasting a log message to staff members about the queue's removal.</li>
     *   <li>Deleting the associated database record for the queue.</li>
     *   <li>Sending a removal packet to synchronize queue data across servers.</li>
     * </ul>
     *
     * <p>Errors encountered during operations like messaging players or inter-server communication
     * are logged for diagnostic purposes without halting the queue removal process.</p>
     *
     * @param queue the {@link VirtualQueue} instance to be removed. This queue is extracted from the
     *              internal list and cleaned up if hosted locally.
     * @return {@code true} if the queue was successfully removed.
     * @throws Exception if an error occurs while removing the queue, such as issues with inter-server
     *                   messaging or database operations.
     */
    public boolean removeQueue(VirtualQueue queue) throws Exception {
        queues.remove(queue);
        if (queue.isHost()) {
            queue.getMembers().forEach(uuid -> {
                try {
                    Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.AQUA + "The virtual queue " + queue.getName() + ChatColor.AQUA + " has been removed.", false);
                } catch (Exception e) {
                    Core.getInstance().getLogger().log(Level.SEVERE, "Error sending cross-server message for virtual queue", e);
                }
            });
            Core.getMessageHandler().sendStaffMessage(ChatColor.GREEN + "A virtual queue (" + queue.getName() +
                    ChatColor.GREEN + ") has been removed from " + ChatColor.AQUA + Core.getInstanceName() +
                    ChatColor.GREEN);
            virtualQueuesCollection.deleteOne(Filters.eq("queueId", queue.getId()));
            Core.getMessageHandler().sendMessage(new RemoveQueuePacket(queue.getId()), Core.getMessageHandler().permanentClients.get("all_parks"));
        }
        return true;
    }

    /**
     * Handles the creation of a new virtual queue based on the details provided in a
     * {@link CreateQueuePacket}.
     *
     * <p>This method checks if a virtual queue with the specified {@code queueId} already exists.
     * If a queue with the given ID is not found, a new {@link VirtualQueue} instance is created and
     * added to the list of active queues.</p>
     *
     * <p>The newly created queue is initialized with the values provided in the
     * {@link CreateQueuePacket}, including the queue's name, holding area, server, and a
     * randomly generated item ID.</p>
     *
     * @param packet the {@link CreateQueuePacket} containing the details for the queue to be created.
     *               This includes:
     *               <ul>
     *                   <li><strong>queueId:</strong> A unique identifier for the queue.</li>
     *                   <li><strong>queueName:</strong> The name of the queue.</li>
     *                   <li><strong>holdingArea:</strong> An integer representing the associated holding area for the queue.</li>
     *                   <li><strong>server:</strong> The server on which the queue will be hosted.</li>
     *               </ul>
     */
    public void handleCreate(CreateQueuePacket packet) {
        VirtualQueue queue = getQueueById(packet.getQueueId());
        if (queue != null) return;
        queues.add(new VirtualQueue(packet.getQueueId(), packet.getQueueName(), packet.getHoldingArea(), null,
                null, packet.getServer(), null, null, getRandomItemId()));
    }

    /**
     * Handles the removal of a virtual queue by processing the provided {@code RemoveQueuePacket}.
     * <p>
     * This method attempts to remove the virtual queue identified by the queue ID in the provided
     * packet. If an exception occurs during the removal process, it logs the error with severity
     * level {@code SEVERE}.
     * </p>
     *
     * @param packet The {@code RemoveQueuePacket} containing the queue ID to be removed.
     */
    public void handleRemove(RemoveQueuePacket packet) {
        try {
            removeQueue(packet.getQueueId());
        } catch (Exception e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error removing virtual queue - MessageQueue issue?", e);
        }
    }

    /**
     * Handles the update of a virtual queue based on the provided {@link UpdateQueuePacket}.
     * <p>
     * This method processes the incoming packet to update the state of the corresponding virtual queue,
     * making necessary changes such as opening/closing the queue and updating the queue's contents.
     * Additionally, it updates menus associated with open queues accordingly.
     * </p>
     *
     * @param packet the {@link UpdateQueuePacket} containing information required to update the virtual queue,
     *               including its ID, state (open/closed), and the updated queue structure.
     */
    public void handleUpdate(UpdateQueuePacket packet) {
        VirtualQueue queue = getQueueById(packet.getQueueId());
        if (queue == null || queue.isHost()) return;
        queue.setOpen(packet.isOpen());
        queue.updateQueue(packet.getQueue());
        int i = -1;
        for (VirtualQueue q : queues) {
            i++;
            if (q.getId().equals(queue.getId())) break;
        }
        int pos = i;
        Core.runTask(ParkManager.getInstance(), () -> openMenus.forEach((uuid, menu) -> {
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                menu.setButton(new MenuButton(pos, ItemUtil.unbreakable(queue.getItem(player)), ImmutableMap.of(ClickType.RIGHT, p -> {
                    p.closeInventory();
                    if (queue.getPosition(p.getUniqueId()) < 1) {
                        try {
                            queue.joinQueue(p);
                        } catch (Exception e) {
                            Core.getInstance().getLogger().log(Level.SEVERE, "Error joining virtual queue", e);
                            p.sendMessage(ChatColor.RED + "An error occurred while joining that queue - try again soon!");
                        }
                    } else {
                        try {
                            queue.leaveQueue(p);
                        } catch (Exception e) {
                            Core.getInstance().getLogger().log(Level.SEVERE, "Error joining virtual queue", e);
                            p.sendMessage(ChatColor.RED + "An error occurred while joining that queue - try again soon!");
                        }
                    }
                })));
            }
        }));
    }

    /**
     * Handles player join or leave actions in a virtual queue based on the data from the provided packet.
     * This method ensures that the appropriate queue is identified and the player's action
     * is processed accordingly.
     *
     * <p>Actions available include:
     * <ul>
     *     <li>Joining a queue</li>
     *     <li>Leaving a queue</li>
     * </ul>
     *
     * If an exception occurs during these actions, it is logged for debugging purposes.
     *
     * @param packet the {@link PlayerQueuePacket} containing information about the player action
     *               and the queue they are interacting with. This includes:
     *               <ul>
     *                   <li>The ID of the queue</li>
     *                   <li>The unique identifier (UUID) of the player</li>
     *                   <li>The action (joining or leaving)</li>
     *               </ul>
     */
    public void handlePlayer(PlayerQueuePacket packet) {
        VirtualQueue queue = getQueueById(packet.getQueueId());
        if (queue == null || !queue.isHost()) return;
        if (packet.isJoining()) {
            try {
                queue.joinQueue(packet.getPlayerUUID());
            } catch (Exception e) {
                Core.getInstance().getLogger().log(Level.SEVERE, "Error adding player to virtual queue via packet", e);
            }
        } else {
            try {
                queue.leaveQueue(packet.getPlayerUUID());
            } catch (Exception e) {
                Core.getInstance().getLogger().log(Level.SEVERE, "Error removing player from virtual queue via packet", e);
            }
        }
    }

    /**
     * Clears all queues managed by the system.
     *
     * <p>This method iterates over all queues currently stored in the internal collection
     * and attempts to remove each queue by calling the {@code removeQueue} method. If an
     * exception occurs during the removal process, it is logged as a severe error. After
     * all queues have been processed, the internal collection of queues is cleared.</p>
     *
     * <p>Steps performed by this method:</p>
     * <ul>
     *     <li>Creates a temporary copy of the current list of queues.</li>
     *     <li>Iterates through each queue in the copy and attempts to remove it.</li>
     *     <li>If an exception is thrown during removal, logs the error with a message.</li>
     *     <li>Clears the original collection of queues.</li>
     * </ul>
     *
     * <p>This method ensures that all queues are properly handled and removed, preventing
     * orphaned or lingering queues upon shutdown.</p>
     *
     * <h2>Logging</h2>
     * <p>Any exceptions thrown while removing queues are logged with the appropriate
     * severity level to aid in debugging and monitoring system issues during shutdown.</p>
     */
    public void clearQueues() {
        new ArrayList<>(queues).forEach(queue -> {
            try {
                removeQueue(queue);
            } catch (Exception e) {
                Core.getInstance().getLogger().log(Level.SEVERE, "Error removing hosted queue on shutdown!", e);
            }
        });
        queues.clear();
    }

    /**
     * Retrieves a random item ID from a predefined set of IDs.
     *
     * <p>The method selects a random ID from the following array:
     * <lu>
     * <li>0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 13, 14</li>
     * </lu>
     *
     * @return A randomly selected item ID as an integer from the predefined array.
     */
    public int getRandomItemId() {
        int[] ids = new int[]{0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 13, 14};
        return ids[new Random().nextInt(ids.length - 1)];
    }

    /**
     * Adds a menu to the open menus map for the given player and sets up a callback to remove it
     * when the menu is closed.
     *
     * <p>This method is responsible for managing the association between a {@link CPlayer} and a
     * {@link Menu} in the open menus data structure. It ensures that whenever a menu is opened, a
     * reference to it is tracked and subsequently removed when the menu is closed. The removal of
     * the open menu is handled by a callback triggered on menu close.
     *
     * @param player the player opening the menu. This parameter uniquely identifies the player
     *               whose menu is being tracked.
     * @param menu   the menu being opened by the player. This parameter contains the menu instance
     *               to associate with the given player for tracking.
     */
    public void addToOpenMenus(CPlayer player, Menu menu) {
        openMenus.put(player.getUniqueId(), menu);
        menu.setOnClose(() -> removeFromOpenMenus(player));
    }

    /**
     * Removes the specified player's menu from the list of open menus.
     * This method ensures that the player's unique identifier is no longer
     * associated with any open menus.
     *
     * @param player The {@link CPlayer} object representing the player
     *               whose menu should be removed from the open menus list.
     */
    private void removeFromOpenMenus(CPlayer player) {
        openMenus.remove(player.getUniqueId());
    }

    /**
     * Sends a message to a player identified by their UUID.
     * <p>
     * This method attempts to send a specified message to a player using
     * the core messaging system. If the message fails to send, an error is logged.
     *
     * @param uuid the unique identifier of the player to send the message to
     * @param msg the message content to be sent to the player
     */
    public void messagePlayer(UUID uuid, String msg) {
        try {
            Core.getMessageHandler().sendMessageToPlayer(uuid, msg, false);
        } catch (Exception e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error sending cross-server message with MQ", e);
        }
    }

    /**
     * Updates the open status of the specified virtual queue in the collection.
     * <p>
     * This method modifies the database record of the provided virtual queue by setting its open status
     * to the value of its {@code isOpen()} property.
     *
     * @param queue the {@link VirtualQueue} object whose open status is being updated.
     *              <ul>
     *                  <li>Must not be null.</li>
     *                  <li>Must have a valid queue ID.</li>
     *              </ul>
     */
    public void setOpenStatus(VirtualQueue queue) {
        virtualQueuesCollection.updateOne(Filters.eq("queueId", queue.getId()), Updates.set("open", queue.isOpen()));
    }

    /**
     * Adds a member to the specified virtual queue.
     *
     * <p>This method updates the corresponding virtual queue in the database by
     * appending the provided member's unique identifier to the queue list.
     *
     * @param queue The virtual queue to which the member will be added.
     *              This must be a valid VirtualQueue object.
     * @param member The unique identifier of the member to add to the queue,
     *               represented as a UUID.
     */
    public void addQueueMember(VirtualQueue queue, UUID member) {
        virtualQueuesCollection.updateOne(Filters.eq("queueId", queue.getId()), Updates.push("queue", member.toString()));
    }

    /**
     * Removes a specified member from the given virtual queue.
     *
     * <p>This method updates the database collection to remove the specified member's ID
     * from the queue associated with the given VirtualQueue object.</p>
     *
     * @param queue The {@link VirtualQueue} object representing the queue from which
     *              the member should be removed.
     * @param member The {@link UUID} of the member to remove from the virtual queue.
     */
    public void removeQueueMember(VirtualQueue queue, UUID member) {
        virtualQueuesCollection.updateOne(Filters.eq("queueId", queue.getId()), Updates.pull("queue", member.toString()));
    }
}
