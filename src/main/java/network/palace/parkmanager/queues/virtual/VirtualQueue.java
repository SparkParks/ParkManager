package network.palace.parkmanager.queues.virtual;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.ComponentSerializer;
import network.palace.core.Core;
import network.palace.core.messagequeue.packets.SendPlayerPacket;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.message.PlayerQueuePacket;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

/**
 * Represents a virtual queue, allowing players to join, leave, and track their position.
 * Virtual queues are managed across servers and have designated locations, items, and signs
 * for tracking queue status and providing user feedback.
 *
 * <p>This class supports the following features:</p>
 * <ul>
 *     <li>Managing player entry to and removal from the queue.</li>
 *     <li>Maintaining a holding area for queued players.</li>
 *     <li>Updating visual indicators like signs and items to reflect queue state.</li>
 *     <li>Cross-server queue coordination and messaging.</li>
 * </ul>
 *
 * <p>Each VirtualQueue instance is identified by a unique ID, server association,
 * and positional details related to its hosting environment, such as queue locations and associated areas.</p>
 */
public class VirtualQueue {
    /**
     * <p>Represents the unique identifier of the virtual queue.</p>
     *
     * <p>The value of this variable is used to uniquely distinguish a queue instance
     * from other queues. It is expected to remain constant during the lifecycle of
     * the queue and is a key attribute within the system for various operations
     * including adding, removing, and managing queue-related activities.</p>
     *
     * <p>This field is primarily accessible through the getter method provided, ensuring
     * the integrity of the identifier.</p>
     */
    // id of the queue
    // name of the queue
    @Getter protected String id, /**
     * Represents the name of the virtual queue.
     * <p>
     * This field is used to identify the queue for both players and system operations.
     * It is typically a unique and descriptive string that defines the purpose or
     * location associated with the queue. For example, it may describe the name
     * of the server, the type of activity, or the function of the queue.
     * </p>
     * <p>
     * The value of this field is set during the creation of the {@code VirtualQueue}
     * object and may be referenced across various methods for display, logging,
     * or identification purposes.
     * </p>
     */
    name;

    /**
     * Represents the maximum number of players that can fit in the holding area
     * for the queue associated with this virtual queue system.
     *
     * <p>The holding area serves as a temporary staging zone for players who are
     * waiting to join the main queue. This allows for better organization and
     * proper flow within the virtual queue environment.</p>
     *
     * <p>Key characteristics of the holding area are:</p>
     * <ul>
     *     <li>The capacity of the holding area is defined by this variable.</li>
     *     <li>Once the holding area reaches its limit, no additional players can
     *     occupy it until space is created by movement or removal.</li>
     *     <li>Efficient management of the holding area is crucial to ensure a
     *     smooth user experience in the virtual queue system.</li>
     * </ul>
     */
    // number of players that fit in the holding area for the queue
    @Getter protected int holdingArea;

    /**
     * Represents the location of the holding area associated with the virtual queue.
     * This location serves as the designated area where players can wait before fully
     * joining the queue or being processed further.
     *
     * <p>The {@code holdingAreaLocation} is vital for coordinating player movements
     * and serves as a teleportation or staging point within the virtual queue system.
     * Its accurate configuration ensures players are correctly positioned while interacting
     * with the virtual queue.</p>
     *
     * <p>Use cases include:</p>
     * <ul>
     *     <li>Teleporting players to this location when they are added to the holding area.</li>
     *     <li>Ensuring that players leaving the queue are relocated to this holding area,
     *         if configured.</li>
     * </ul>
     */
    @Getter protected Location holdingAreaLocation;

    /**
     * Represents the location of the queue in the virtual queue system.
     * This location is used to define where queue-related actions or activities take place.
     *
     * <p>The {@code queueLocation} is critical for various operations, such as:
     * <ul>
     *     <li>Determining where players physically gather when joining the queue.</li>
     *     <li>Setting the spatial context for queue-related events and functions.</li>
     *     <li>Serving as a central point for teleportation or interaction within the queue system.</li>
     * </ul>
     *
     * <p>This variable is populated during the creation of the {@link VirtualQueue} instance
     * and remains fixed unless explicitly updated.</p>
     *
     * <p>Clients and methods that rely on queue operations may utilize this field to retrieve
     * or reference the physical location of the queue.</p>
     */
    @Getter protected Location queueLocation;
    /**
     * Represents the server where the virtual queue was created. This field identifies the host server
     * responsible for managing the queue and associated operations.
     *
     * <p>Key characteristics of this field include:</p>
     * <ul>
     *     <li>It is a {@code String} value representing the server's name or identifier.</li>
     *     <li>It is protected, meaning it is only accessible by the containing class or its subclasses.</li>
     *     <li>A {@link Getter} annotation is applied to provide read access to this field.</li>
     * </ul>
     *
     * <p>In the {@code VirtualQueue} class, this property is used to coordinate queue management and operations,
     * ensuring actions like admitting players to the queue or delegating actions across servers are directed to
     * the appropriate host.</p>
     */
    // server the queue was created on
    @Getter protected String server;

    /**
     * Represents a sign used within the virtual queue system to indicate the advancement
     * of players in the queue or display specific queue-related updates.
     *
     * <p>This field is primarily utilized to visually communicate queue progress or
     * state updates to players through the in-game environment.</p>
     *
     * <p>The {@code advanceSign} object can be updated dynamically to reflect real-time
     * changes in the queue, such as when a player's position advances or when specific
     * updates need to be displayed.</p>
     *
     * <p>Key functionalities include:</p>
     * <ul>
     *     <li>Displaying positional or progression messages to players in the queue.</li>
     *     <li>Reflecting state or configuration-specific information of the virtual queue.</li>
     *     <li>Serving as an interface to enhance interaction between players and the system.</li>
     * </ul>
     */
    @Getter @Setter protected Sign advanceSign;

    /**
     * Represents the current state of the virtual queue through a sign object.
     * The <code>stateSign</code> is used to display and update the status
     * of the queue visually to players. It can be interacted with or updated
     * dynamically based on the state of the queue.
     *
     * <p>The <code>stateSign</code> serves the following purposes:</p>
     * <ul>
     *     <li>Shows whether the queue is open or closed.</li>
     *     <li>Provides information about the number of players in the queue.</li>
     *     <li>Indicates additional instructions or related details about the queue.</li>
     * </ul>
     *
     * <p>This field is a protected property and can be modified or accessed
     * through its corresponding getter and setter methods.</p>
     */
    @Getter @Setter protected Sign stateSign;

    /**
     * Represents the unique identifier for an item within the virtual queue system.
     *
     * <p>This field is used to link an individual item to its associated functionality
     * and metadata within the queue. It is a final integer, ensuring immutability
     * once the VirtualQueue instance is created.</p>
     *
     * <p>Responsibilities of this identifier may include:</p>
     * <ul>
     *     <li>Relating items to specific queue events or actions.</li>
     *     <li>Assisting in the creation or retrieval of item data specific to the queue.</li>
     *     <li>Providing a reference for queue-related workflows or interactions.</li>
     * </ul>
     */
    private final int itemId;

    /**
     * Represents the current status of the virtual queue, indicating whether players
     * are allowed to join the queue at this time.
     *
     * <p>If set to {@code true}, the queue is open and players are permitted to join.
     * If set to {@code false}, the queue is closed and new players cannot join.</p>
     *
     * <p>This variable is managed primarily through {@link VirtualQueue#setOpen(boolean)},
     * which enables or disables the queue. The state of the queue can affect player
     * interactions and updates related to joining or leaving the queue.</p>
     */
    // whether players can join the queue
    @Getter private boolean open = false;

    /**
     * Represents the queue of players for the {@code VirtualQueue}.
     * This list maintains the {@link UUID} of each player currently in the queue.
     *
     * <p>Key uses and behaviors:</p>
     * <ul>
     *     <li>Tracks the players who are waiting to be processed by the queue mechanism.</li>
     *     <li>Enables operations such as adding, removing, and retrieving player positions.</li>
     *     <li>Acts as the primary data structure for queue management in {@code VirtualQueue}.</li>
     * </ul>
     *
     * <p>This list is primarily managed through other methods of the {@code VirtualQueue} class,
     * ensuring proper synchronization and updates to the queue's state.</p>
     */
    // the list of players in queue
    private final LinkedList<UUID> queue = new LinkedList<>();

    /**
     * A mapping of player IDs to timestamps representing the moment they were added to the
     * "joining to holding area" process in the virtual queue.
     *
     * <p>This map is primarily used to track players who are in transition from the main queue
     * to the holding area. The key represents the unique player identifier ({@link UUID}) while
     * the value represents the timestamp (in milliseconds) when the player started this transition.</p>
     *
     * <p>This information is utilized to manage and monitor the timing and flow of players being
     * moved to the holding area, ensuring efficient queue management and preventing contention
     * or bottlenecks.</p>
     *
     * <p>Relevant operations include:
     * <ul>
     *   <li>Adding a player to the map when they begin transitioning to the holding area.</li>
     *   <li>Removing a player from the map upon successful transition or cancellation of the process.</li>
     *   <li>Checking the timestamp to determine how long a player has been in the process.</li>
     * </ul>
     * </p>
     *
     * <p>This map is intended to support the virtual queue management system and ensure synchronization
     * of player flow between different states.</p>
     */
    private final HashMap<UUID, Long> joiningToHoldingArea = new HashMap<>();

    /**
     * Represents the timestamp of the last "advance" event in the virtual queue.
     *
     * <p>This variable is used to track when the most recent advance action
     * was performed in the queue system. It is stored as the number of milliseconds
     * since the Unix epoch (January 1, 1970, 00:00:00 GMT).</p>
     *
     * <p>The value of this field is updated whenever an advance occurs, which
     * may involve moving players within or out of the queue. It plays a role
     * in managing the state and actions of the queue to ensure proper
     * synchronization and timing when advancing players.</p>
     *
     * <ul>
     *     <li>Type: {@code long}</li>
     *     <li>Default value: {@code 0}</li>
     * </ul>
     *
     * <p>This variable is particularly useful for implementing time-based rules or
     * audit logs regarding queue advancements.</p>
     */
    @Getter @Setter private long lastAdvance = 0;

    /**
     * Indicates whether the state or configuration of the queue has been updated.
     * This flag is used to track changes that may require further processing, such as
     * updating signs, notifying players, or handling state transitions.
     *
     * <p>Possible values:</p>
     * <ul>
     *   <li><b>true</b> - The queue has been updated, signaling that dependent actions or processes may need to execute.</li>
     *   <li><b>false</b> - The queue is unchanged, and no immediate actions are required.</li>
     * </ul>
     *
     * <p>This variable is typically managed internally by the class and should
     * be updated whenever a significant modification occurs within the queue system.</p>
     */
    @Getter @Setter private boolean updated = false;

    /**
     * Constructs a new instance of the <code>VirtualQueue</code> class with the specified parameters.
     *
     * <p>The <code>VirtualQueue</code> represents a virtualized queue system where users can join,
     * leave, and advance through various stages. This constructor allows initialization of all necessary
     * details related to the queue, such as identifiers, locations, linked signs, and item data.</p>
     *
     * @param id A unique identifier for the virtual queue.
     * @param name The display name of the queue, often used for user-facing purposes.
     * @param holdingArea The number of users allocated to the holding area before being admitted to the queue's server.
     * @param holdingAreaLocation A {@link Location} object representing the physical position or teleport location of the holding area.
     * @param queueLocation A {@link Location} object representing the physical position or teleport location of the end of the queue.
     * @param server The server name or identifier where this queue is hosted.
     * @param advanceSign A {@link Sign} object used to handle queue advancement functionality.
     * @param stateSign A {@link Sign} object used to display or control the queue’s current state.
     * @param itemId An identifier for a specific item representation or metadata linked to the queue.
     */
    public VirtualQueue(String id, String name, int holdingArea, Location holdingAreaLocation, Location queueLocation, String server, Sign advanceSign, Sign stateSign, int itemId) {
        this.id = id;
        this.name = name;
        this.holdingArea = holdingArea;
        this.holdingAreaLocation = holdingAreaLocation;
        this.queueLocation = queueLocation;
        this.server = server;
        this.advanceSign = advanceSign;
        this.stateSign = stateSign;
        this.itemId = itemId;
    }

    /**
     * Grants admission to the next player in the virtual queue, if the current server is the host for the queue.
     * <p>
     * This method performs the following actions:
     * <lu>
     *   <li>Checks if the queue is empty; if so, it returns immediately without taking any action.</li>
     *   <li>If the current server is the host, processes the first player in the queue:</li>
     *   <ul>
     *     <li>Fetches the player's details using their unique identifier (UUID).</li>
     *     <li>Sends a success message to the player informing them they've reached the front of the queue.</li>
     *     <li>Removes the player from the "virtualQueueHoldingArea" and teleports them to the configured queue location.</li>
     *   </ul>
     *   <li>Removes the player from the front of the queue by invoking {@code leaveQueue(UUID)}.</li>
     *   <li>Updates virtual queue signs to reflect the latest queue state by invoking {@code updateSigns()}.</li>
     * </lu>
     * <p>
     * This method does nothing if the current server is not the host or if the queue is empty.
     *
     * @throws Exception if an error occurs during the operation, including but not limited to:
     *                   <ul>
     *                     <li>Issues with player retrieval.</li>
     *                     <li>Teleportation or queue update errors.</li>
     *                   </ul>
     */
    public void admit() throws Exception {
        if (queue.isEmpty()) return;
        if (isHost()) {
            UUID uuid = queue.getFirst();
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "You've made it to the front of the queue!");
                player.getRegistry().removeEntry("virtualQueueHoldingArea");
                player.teleport(queueLocation);
            }
        }
        leaveQueue(queue.getFirst());
        updateSigns();
    }

    /**
     * Sets the open state of the virtual queue. This method updates the state of the queue
     * and notifies all players in the queue about the change in its status. If the queue is
     * set to open, it will send positional updates to the players. If it is closed, players
     * are informed and warned about potentially losing their position if they leave.
     * <p>
     * Additional actions include updating signs that reflect the queue's state and notifying
     * the hosting system of the new status.
     *
     * @param open a boolean value representing the desired open state of the queue:
     *             <lu>
     *                 <li><code>true</code> to open the queue.</li>
     *                 <li><code>false</code> to close the queue.</li>
     *             </lu>
     */
    public void setOpen(boolean open) {
        if (this.open == open) return;
        this.open = open;
        updated = true;
        updateSigns();
        ListIterator<UUID> iterator = queue.listIterator();
        UUID uuid;
        int position = 1;
        String msg = open ? (ChatColor.GREEN + "The virtual queue " + name + ChatColor.GREEN + " has opened! You're in position #") :
                (ChatColor.AQUA + "The virtual queue " + name + ChatColor.AQUA + " has been closed! You're still in line, but you will lose your place if you leave the queue.");
        while (iterator.hasNext()) {
            uuid = iterator.next();
            position++;
            if (isHost()) ParkManager.getVirtualQueueManager().messagePlayer(uuid, open ? (msg + position) : msg);
        }
        if (isHost()) ParkManager.getVirtualQueueManager().setOpenStatus(this);
    }

    /**
     * Adds the specified player to the virtual queue.
     * Depending on whether the current instance is the host, either the player is added locally
     * or a message is sent to other hosts to handle the request.
     *
     * <p>If the current instance is the host, the player is immediately processed for joining the queue.
     * Otherwise, a {@link PlayerQueuePacket} is sent to other hosts to process the player's addition.</p>
     *
     * @param player The {@link CPlayer} instance representing the player to be added to the virtual queue.
     * @throws Exception If an error occurs while processing the player's addition to the queue.
     */
    public void joinQueue(CPlayer player) throws Exception {
        if (!isHost()) {
            Core.getMessageHandler().sendMessage(new PlayerQueuePacket(id, player.getUniqueId(), true), Core.getMessageHandler().permanentClients.get("all_parks"));
            return;
        }
        joinQueue(player.getUniqueId());
    }

    /**
     * Adds a user, identified by their UUID, to the virtual queue if certain conditions are met.
     * <p>
     * This method checks if the queue is open and whether the user is already present in the queue.
     * Based on the state of the queue and the user's position, the appropriate actions or messages
     * are triggered. If the user joins the queue, their position is updated and relevant notifications
     * are sent.
     * </p>
     *
     * @param uuid The unique identifier of the user attempting to join the virtual queue.
     * @throws Exception If an error occurs while sending the user to the server or updating their state.
     */
    public void joinQueue(UUID uuid) throws Exception {
        if (!open) {
            if (isHost())
                Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.RED + "The virtual queue " + name + ChatColor.RED + " is currently closed, sorry!", false);
            return;
        }
        if (getPosition(uuid) >= 1) {
            if (isHost()) {
                if (joiningToHoldingArea.containsKey(uuid)) {
                    joiningToHoldingArea.remove(uuid);
                    sendToServer(uuid);
                } else {
                    Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.RED + "You're already in the virtual queue " + name + "!", false);
                }
            }
            return;
        }
        queue.add(uuid);
        updated = true;
        updateSigns();
        if (isHost()) {
            Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.GREEN + "You joined the virtual queue " + name +
                    " in position #" + getPosition(uuid) + "!", false);
            ParkManager.getVirtualQueueManager().addQueueMember(this, uuid);
        }
        if (getPosition(uuid) <= holdingArea) {
            markAsSendingToServer(uuid);
        }
    }

    /**
     * Removes a player from the virtual queue. If the current server is the host of the queue, the player
     * is removed directly from the queue and their related data is updated accordingly. If the server
     * is not the host, a message is sent to the host server to handle the removal.
     *
     * <p>Upon successful removal, if the player was on the host server, they are also teleported
     * to a predefined location (e.g., "castle").
     *
     * @param player the {@code CPlayer} instance representing the player to be removed from the queue.
     *               This player must be part of the virtual queue identified by the current instance.
     *
     * @throws Exception if an error occurs while removing the player from the queue or if communication
     *                   with the host server fails.
     */
    public void leaveQueue(CPlayer player) throws Exception {
        if (!isHost()) {
            Core.getMessageHandler().sendMessage(new PlayerQueuePacket(id, player.getUniqueId(), false), Core.getMessageHandler().permanentClients.get("all_parks"));
            return;
        }
        if (leaveQueue(player.getUniqueId())) {
            player.getRegistry().removeEntry("virtualQueueHoldingArea");
            if (isHost()) player.performCommand("warp castle");
        }
    }

    /**
     * Removes a player, identified by their UUID, from the virtual queue. This method checks whether the player
     * is part of the queue and updates the system accordingly. If the server is not the host for this queue,
     * it sends a packet to the host server to handle the removal. If the player is successfully removed,
     * it updates the queue state and provides feedback to the player.
     *
     * <p>Actions performed by this method include:
     * <ul>
     *   <li>Sending a request to the host server if the local server is not the queue's host.</li>
     *   <li>Removing the player from a temporary holding area if applicable.</li>
     *   <li>Removing the player from the virtual queue if they are present.</li>
     *   <li>Updating the queue signs to reflect the current queue population.</li>
     *   <li>Notifying the player about the result of their attempt to leave the queue.</li>
     * </ul>
     *
     * @param uuid The unique identifier (UUID) of the player attempting to leave the queue.
     * @return {@code true} if the player was successfully removed from the queue,
     *         {@code false} if the player was not in the queue or the operation is delegated to another server.
     * @throws Exception if an error occurs while processing the request or updating the queue state.
     */
    public boolean leaveQueue(UUID uuid) throws Exception {
        if (!isHost()) {
            Core.getMessageHandler().sendMessage(new PlayerQueuePacket(id, uuid, false), Core.getMessageHandler().permanentClients.get("all_parks"));
            return false;
        }
        joiningToHoldingArea.remove(uuid);
        int position = queue.indexOf(uuid);
        if (position >= 0) {
            queue.remove(uuid);
            updated = true;
            updateSigns();
            ParkManager.getVirtualQueueManager().removeQueueMember(this, uuid);
            Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.GREEN + "You have left the virtual queue " + name + "!", false);
            return true;
        }
        Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.RED + "You aren't in the virtual queue " + name + "!", false);
        return false;
    }

    /**
     * Sends position messages to all players currently in the queue.
     * <p>
     * This method iterates through the list of UUIDs in the queue, determining
     * each player's position, and sends a message to each player informing them
     * of their current position in the virtual queue. The position starts at 1
     * for the first player in the queue.
     * </p>
     * <ul>
     * <li><strong>Queue Iteration:</strong> Uses a {@link ListIterator} to traverse through the list of UUIDs.</li>
     * <li><strong>Position Tracking:</strong> Maintains a counter starting at 1 to represent
     * each player's position, incrementing it for each subsequent player in the queue.</li>
     * <li><strong>Message Dispatch:</strong> Delegates the task of sending position messages
     * to the {@code sendPositionMessage(UUID uuid, int pos)} method for each UUID in the queue.</li>
     * </ul>
     * <p>
     * This method ensures equitable and sequential notification to all players about
     * their respective positions within the virtual queue.
     * </p>
     */
    public void sendPositionMessages() {
        ListIterator<UUID> iterator = queue.listIterator();
        UUID uuid;
        int position = 1;
        while (iterator.hasNext()) {
            uuid = iterator.next();
            sendPositionMessage(uuid, position++);
        }
    }

    /**
     * Sends a position message to the specified player, indicating their current position
     * in the virtual queue. This method retrieves the player's position using their unique
     * identifier and sends an appropriate message.
     *
     * <p>
     * If the player's position in the queue is valid (1 or greater), they will receive a
     * message indicating their position in the queue. This message includes the queue name
     * for better context.
     * </p>
     *
     * @param player The player to whom the position message should be sent. This must be a
     *               {@link CPlayer} instance representing the player currently in the queue.
     */
    private void sendPositionMessage(CPlayer player) {
        sendPositionMessage(player, getPosition(player.getUniqueId()));
    }

    /**
     * Sends a position message to the specified player, indicating their current position
     * in the virtual queue if the position is greater than or equal to 1.
     *
     * <p>
     * If the position is valid (1 or greater), the message will be displayed in green
     * with the player's position and the queue's name.
     * </p>
     *
     * @param player the {@link CPlayer} to whom the position message will be sent
     * @param pos    the current position of the player in the virtual queue
     */
    private void sendPositionMessage(CPlayer player, int pos) {
        if (pos >= 1)
            player.sendMessage(ChatColor.GREEN + "You are in position #" + pos + " in the virtual queue " + name + "!");
    }

    /**
     * Sends a position message to a player in a virtual queue using their unique identifier (UUID).
     * This message informs the player of their current position in the queue
     * if they are in the queue and the server is the host.
     *
     * <p>The message is sent only if the player's position is greater than or equal to 1
     * and this instance is the host server.</p>
     *
     * @param uuid The unique identifier (UUID) of the player to whom the message will be sent.
     * @param pos The position of the player in the virtual queue. Must be greater than or equal to 1
     *            to send the message.
     */
    private void sendPositionMessage(UUID uuid, int pos) {
        if (pos >= 1 && isHost())
            ParkManager.getVirtualQueueManager().messagePlayer(uuid, ChatColor.GREEN +
                    "You are in position #" + pos + " in the virtual queue " + name + "!");
    }

    /**
     * Retrieves the position of a specific UUID in the queue.
     * <p>
     * The method calculates the position of the specified UUID in the queue
     * (a 1-based index). If the UUID is not found within the queue, a position
     * of 0 is returned.
     *
     * @param uuid the UUID of the user whose position in the queue is to be retrieved
     *             <ul>
     *               <li>Must not be null.</li>
     *             </ul>
     * @return the position of the UUID in the queue as a 1-based index
     *         <ul>
     *           <li>Returns 0 if the UUID is not found in the queue.</li>
     *         </ul>
     */
    public int getPosition(UUID uuid) {
        return queue.indexOf(uuid) + 1;
    }

    /**
     * Updates the current queue with the specified list of UUIDs. This method clears the existing
     * queue and replaces it with the provided list, marking the queue as updated and refreshing
     * the associated signs.
     *
     * <p>If the provided list is {@code null}, the method will not perform any operations.
     *
     * @param queue A {@link List} of {@link UUID} objects representing the new queue to be set.
     *        If {@code null}, the operation is skipped.
     */
    public void updateQueue(List<UUID> queue) {
        if (queue == null) return;
        int size = this.queue.size();
        this.queue.clear();
        this.queue.addAll(queue);
        updated = true;
        updateSigns();
    }

    /**
     * Updates the state of the "advance" and "state" signs associated with the virtual queue. This method
     * ensures the signs display up-to-date queue information, including the queue size and whether the
     * queue is open or closed.
     * <p>
     * The method will only execute if the current instance is the host server. The updates made
     * to the signs are performed asynchronously to avoid blocking the main thread.
     * </p>
     *
     * <p><strong>Behavior:</strong></p>
     * <ul>
     *     <li>If the {@code advanceSign} is not null:
     *         <ul>
     *             <li>The first line shows "[Virtual Queue]" in aqua color.</li>
     *             <li>The second line displays the queue ID in blue color.</li>
     *             <li>The third line shows "Advance" in bold yellow color.</li>
     *             <li>The fourth line displays the current queue size in yellow color, appended with
     *                 "Player" or "Players" depending on the count.</li>
     *         </ul>
     *     </li>
     *     <li>If the {@code stateSign} is not null:
     *         <ul>
     *             <li>The first line shows "[Virtual Queue]" in aqua color.</li>
     *             <li>The second line displays the queue ID in blue color.</li>
     *             <li>The third line indicates whether the queue is "Open" (green and bold) or "Closed" (red and bold).</li>
     *             <li>The fourth line displays the current queue size in yellow color, appended with
     *                 "Player" or "Players" depending on the count.</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * <p><strong>Notes:</strong></p>
     * <ul>
     *     <li>The signs are only updated if the current instance matches the queue's host server.</li>
     *     <li>The method utilizes the {@link Core#runTask} functionality to handle sign updates asynchronously.</li>
     * </ul>
     */
    private void updateSigns() {
        if (!isHost()) return;
        Core.runTask(ParkManager.getInstance(), () -> {
            int size = this.queue.size();
            if (advanceSign != null) {
                advanceSign.setLine(0, ChatColor.AQUA + "[Virtual Queue]");
                advanceSign.setLine(1, ChatColor.BLUE + id);
                advanceSign.setLine(2, ChatColor.YELLOW + "" + ChatColor.BOLD + "Advance");
                advanceSign.setLine(3, ChatColor.YELLOW + "" + size + " Player" + TextUtil.pluralize(size));
                advanceSign.update();
            }
            if (stateSign != null) {
                stateSign.setLine(0, ChatColor.AQUA + "[Virtual Queue]");
                stateSign.setLine(1, ChatColor.BLUE + id);
                stateSign.setLine(2, (open ? ChatColor.GREEN : ChatColor.RED) + "" + ChatColor.BOLD + (open ? "Open" : "Closed"));
                stateSign.setLine(3, ChatColor.YELLOW + "" + size + " Player" + TextUtil.pluralize(size));
                stateSign.update();
            }
        });
    }

    /**
     * Determines if the current instance of the VirtualQueue is hosted on the same server
     * as the one specified during initialization.
     *
     * <p>This method compares the name of the current server, obtained via {@link Core#getInstanceName()},
     * to the server name specified in the {@code server} field.</p>
     *
     * @return {@code true} if the current server matches the server name of the VirtualQueue,
     *         {@code false} otherwise.
     */
    public boolean isHost() {
        return Core.getInstanceName().equals(server);
    }

    /**
     * Creates and returns an ItemStack representing the current state of the queue for the specified player.
     * <p>
     * This method generates an item with relevant information such as the player's position in the queue,
     * the number of players currently in the queue, and the queue's current status (open or closed). The
     * item also provides instructions for the player to either join or leave the queue.
     * </p>
     *
     * @param player The {@code CPlayer} for whom the item is being generated. This includes information
     *               necessary to compute the player's queue position and determine the appropriate lore.
     *
     * @return An {@link ItemStack} with the configured display name, material, and lore, representing
     *         the queue state and player's interaction options.
     */
    public ItemStack getItem(CPlayer player) {
        int pos = getPosition(player.getUniqueId());
        List<String> lore = new ArrayList<>(Arrays.asList(
                ChatColor.YELLOW + "Players: " + ChatColor.GREEN + getMembers().size(),
                ChatColor.YELLOW + "Status: " + (open ? ChatColor.GREEN + "Open" : ChatColor.RED + "Closed"),
                ChatColor.YELLOW + "Server: " + ChatColor.GREEN + server
        ));
        if (pos >= 1) {
            lore.addAll(0, Arrays.asList(ChatColor.RESET + "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Place in Line: " + ChatColor.AQUA + "" + ChatColor.BOLD + pos,
                    ChatColor.GREEN + "" + ChatColor.BOLD + "Right-Click to Leave the Queue", ChatColor.RESET + ""));
        } else {
            lore.addAll(Collections.singletonList(ChatColor.AQUA + "" + ChatColor.BOLD + "Right-Click to Join the Queue"));
        }
        return ItemUtil.create(Material.CONCRETE, 1, itemId, name, lore);
    }

    /**
     * Retrieves a list of members currently in the holding area of the virtual queue.
     * The holding area is composed of a specified number of players at the front of the queue.
     *
     * <p>The size of the holding area is determined by the value of the {@code holdingArea} field.
     * If the queue size is smaller than the holding area capacity, all players in the queue
     * are included in the returned list.</p>
     *
     * @return a {@link List} of {@link UUID} objects representing the members in the holding area.
     */
    public List<UUID> getHoldingAreaMembers() {
        return queue.subList(0, queue.size() < holdingArea ? queue.size() : holdingArea);
    }

    /**
     * Retrieves the list of unique identifiers (UUIDs) representing the members currently in the queue.
     *
     * <p>The returned list includes all members in their respective order from the queue.
     * Modifications to the returned list do not affect the original queue.</p>
     *
     * @return a {@link List} of {@link UUID} objects representing the members in the queue.
     */
    public List<UUID> getMembers() {
        return new ArrayList<>(queue);
    }

    /**
     * Retrieves the current mapping of players who are in the process of moving
     * to the holding area of the virtual queue.
     *
     * <p>This method returns a map where the keys are player UUIDs and the values
     * are timestamps represented as longs. The timestamps indicate when the player
     * was marked for transfer or related actions.</p>
     *
     * @return A {@code HashMap} containing the UUIDs of players and their
     *         corresponding timestamps for joining the holding area.
     */
    public HashMap<UUID, Long> getJoiningToHoldingArea() {
        return new HashMap<>(joiningToHoldingArea);
    }

    /**
     * Marks a player, identified by their unique UUID, as currently in the process of being sent to the server.
     * If the player is already joining the holding area or is present in the player manager, this method exits early.
     * <p>
     * If the player is not already in the queue and the current host matches the server identifier, a queued message
     * with interaction options is sent to the player.
     * </p>
     * <p>The player remains marked for 15 seconds before removal from the queue, unless further actions are taken.</p>
     *
     * @param uuid The unique identifier of the player to be marked as being sent to the server.
     * @throws Exception if an unexpected issue occurs during the process.
     */
    public void markAsSendingToServer(UUID uuid) throws Exception {
        if (joiningToHoldingArea.containsKey(uuid) || Core.getPlayerManager().getPlayer(uuid) != null) return;
        joiningToHoldingArea.put(uuid, System.currentTimeMillis() + 15000);
        if (isHost()) {
            BaseComponent[] components = new ComponentBuilder("You're almost at the front of the queue for ").color(net.md_5.bungee.api.ChatColor.GREEN)
                    .append(TextComponent.fromLegacyText(name + "! "))
                    .append("CLICK HERE IN THE NEXT 15 SECONDS").color(net.md_5.bungee.api.ChatColor.YELLOW).bold(true)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to advance in the queue!").color(net.md_5.bungee.api.ChatColor.AQUA).create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vqjoin " + id))
                    .append(" or you'll be removed from the queue!", ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.AQUA).create();
            Core.getMessageHandler().sendMessageToPlayer(uuid, ComponentSerializer.toString(components), true);
        }
    }

    /**
     * Sends a player to the server hosting the queue. This involves removing their
     * UUID from the holding area, notifying the player about the server transition,
     * and broadcasting a packet to transfer the player across proxies.
     *
     * <p>Handles exceptions gracefully and logs severe errors in the event of a failure.</p>
     *
     * @param uuid the unique identifier of the player being sent to the server
     */
    public void sendToServer(UUID uuid) {
        try {
            joiningToHoldingArea.remove(uuid);
            if (isHost())
                Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.GREEN + "Sending you to " + ChatColor.AQUA + server +
                        ChatColor.GREEN + " for the queue " + name + "...", false);
            Core.getMessageHandler().sendMessage(new SendPlayerPacket(uuid.toString(), Core.getInstanceName()), Core.getMessageHandler().ALL_PROXIES);
        } catch (Exception e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error sending player to host server", e);
        }
    }

    /**
     * Removes the specified UUID from the {@code joiningToHoldingArea} collection,
     * which represents the set of players currently transitioning to the holding area.
     *
     * <p>This method is typically used to manage or update the state of the queue by
     * removing players who are no longer in the intermediate joining-to-holding area state.</p>
     *
     * @param uuid the unique identifier of the player to be removed from the joining-to-holding area
     *        collection. Must not be {@code null}.
     */
    public void removeFromJoiningToHoldingArea(UUID uuid) {
        joiningToHoldingArea.remove(uuid);
    }
}
