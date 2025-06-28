package network.palace.parkmanager.message;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

import java.util.UUID;

/**
 * Represents a packet used to manage player queue operations within a system.
 * <p>
 * The {@code PlayerQueuePacket} class extends {@link MQPacket} and provides functionality
 * for handling requests related to player queueing operations. It is associated with a specific
 * packet type identifier defined within the {@link PacketID.Global} enumeration.
 * </p>
 *
 * <p>Main Features:</p>
 * <ul>
 *   <li>Stores the queue identifier ({@code queueId}) to specify the target queue.</li>
 *   <li>Maintains the unique identifier of the player ({@code playerUUID}) involved in the operation.</li>
 *   <li>Tracks whether the player is joining or leaving the queue ({@code joining}).</li>
 * </ul>
 *
 * <p>This class provides the following constructors:</p>
 * <ul>
 *   <li>{@link #PlayerQueuePacket(JsonObject)}: Initializes the packet from a {@link JsonObject},
 *   typically used during deserialization.</li>
 *   <li>{@link #PlayerQueuePacket(String, UUID, boolean)}: Initializes the packet with explicit parameters,
 *   specifying the queue, player, and operation type.</li>
 * </ul>
 *
 * <p>Data Serialization:</p>
 * <p>The {@link #getJSON()} method allows serialization of the packet data into a {@link JsonObject}, which
 * may be utilized for communication or storage:
 * <ul>
 *   <li>Includes the {@code queueId} representing the queue target.</li>
 *   <li>Stores the {@code playerUUID} to identify the relevant player.</li>
 *   <li>Indicates the operation type with {@code joining}, specifying if the player is joining or leaving the queue.</li>
 * </ul>
 * </p>
 *
 * <p>Usage:</p>
 * <p>The {@code PlayerQueuePacket} is primarily intended for usage in message processing systems,
 * enabling actions such as queue modifications for players. It ensures reliable data handling
 * and communication within distributed or real-time systems.</p>
 */
public class PlayerQueuePacket extends MQPacket {
    /**
     * Represents the identifier for a specific queue targeted by the {@code PlayerQueuePacket}.
     * <p>
     * This variable is used to determine the queue associated with the operation being
     * performed, such as player joining or leaving the queue. It is an essential part of
     * the {@link PlayerQueuePacket} data structure and serves as a reference to ensure
     * correct queue-related functionalities.
     * </p>
     *
     * <p>Key Characteristics:</p>
     * <ul>
     *   <li>Immutable: The value is set during the initialization of the {@code PlayerQueuePacket}
     *   instance and cannot be changed thereafter.</li>
     *   <li>Non-null: Ensures a valid and existing queue is always specified for operations.</li>
     *   <li>Serialized: Included in the packet's JSON serialization for communication or storage purposes.</li>
     * </ul>
     *
     * <p>Primary Usage:</p>
     * <ul>
     *   <li>Used to identify the target queue for operations such as adding or removing a player.</li>
     *   <li>Included in message payloads sent to processing systems to facilitate queue management.</li>
     * </ul>
     */
    @Getter private final String queueId;

    /**
     * The {@code playerUUID} variable represents the unique identifier associated with a specific player.
     * <p>
     * This field is integral to identifying the player involved in the operation defined by the {@link PlayerQueuePacket}.
     * It is commonly used to track and manage player-specific actions within a queue system.
     * </p>
     *
     * <p>Main Characteristics:</p>
     * <ul>
     *   <li><b>Immutable:</b> Once set during the packet construction, the value of {@code playerUUID} cannot be changed.</li>
     *   <li><b>Global Uniqueness:</b> This value is globally unique for individual players, ensuring accurate identification
     *       across systems.</li>
     *   <li><b>Serialization Friendly:</b> Included as part of the packet's JSON representation for communication or data persistence.</li>
     * </ul>
     *
     * <p>Purpose:</p>
     * <p>The {@code playerUUID} is crucial for systems requiring precise identification of players, such as queue management,
     * real-time updates, and player-specific operations.</p>
     */
    @Getter private final UUID playerUUID;

    /**
     * Indicates whether a player is joining or leaving a queue.
     *
     * <p>This field stores a boolean value that signifies the type of operation being performed
     * in the context of player queue interactions.</p>
     *
     * <ul>
     *   <li>If {@code true}, the player is joining the queue.</li>
     *   <li>If {@code false}, the player is leaving the queue.</li>
     * </ul>
     *
     * <p>This information is critical for processing queue-related requests within the
     * {@code PlayerQueuePacket} system, allowing proper handling of player actions.</p>
     */
    @Getter private final boolean joining;

    /**
     * Constructs a {@code PlayerQueuePacket} object to represent a player's interaction with a queue.
     * The packet encapsulates information about the player's unique ID, queue ID, and whether
     * the player is joining or leaving the queue.
     *
     * @param object A {@link JsonObject} that contains the following required fields:
     *               <ul>
     *                 <li><b>queueId</b>: A {@code String} representing the unique identifier of the queue.</li>
     *                 <li><b>playerUUID</b>: A {@code String} representing the UUID of the player. This must be
     *                 a valid UUID that can be parsed by {@link UUID#fromString(String)}.</li>
     *                 <li><b>joining</b>: A {@code Boolean} indicating whether the player is joining ({@code true})
     *                 or leaving ({@code false}) the queue.</li>
     *               </ul>
     */
    public PlayerQueuePacket(JsonObject object) {
        super(PacketID.Global.PLAYER_QUEUE.getId(), object);
        this.queueId = object.get("queueId").getAsString();
        this.playerUUID = UUID.fromString(object.get("playerUUID").getAsString());
        this.joining = object.get("joining").getAsBoolean();
    }

    /**
     * Constructs an instance of {@code PlayerQueuePacket}, representing a message packet
     * designed for managing player queue interactions. This packet communicates whether
     * a player is joining or leaving a specific queue within the system.
     *
     * <p>This constructor initializes the packet with the target queue ID, the player's
     * UUID, and the action they are performing (e.g., joining or leaving the queue). The
     * packet is assigned a unique packet ID, represented by {@code PacketID.Global.PLAYER_QUEUE},
     * to facilitate identification and routing within the system.</p>
     *
     * @param queueId A {@link String} representing the unique identifier of the queue
     *                the player is interacting with.
     * @param playerUUID A {@link UUID} representing the unique identifier of the player
     *                   involved in the queue interaction.
     * @param joining A {@code boolean} value indicating the player's action in the queue:
     *                <ul>
     *                  <li><code>true</code>: The player is joining the queue.</li>
     *                  <li><code>false</code>: The player is leaving the queue.</li>
     *                </ul>
     */
    public PlayerQueuePacket(String queueId, UUID playerUUID, boolean joining) {
        super(PacketID.Global.PLAYER_QUEUE.getId(), null);
        this.queueId = queueId;
        this.playerUUID = playerUUID;
        this.joining = joining;
    }

    /**
     * Generates a JSON representation of this {@code PlayerQueuePacket}, including
     * the queue ID, player UUID, and whether the player is joining or leaving the queue.
     *
     * <p>The JSON object contains the following properties:</p>
     * <ul>
     *     <li><b>queueId</b>: A {@code String} representing the unique identifier of the queue.</li>
     *     <li><b>playerUUID</b>: A {@code String} representation of the player's UUID.</li>
     *     <li><b>joining</b>: A {@code Boolean} indicating whether the player is joining (true) or
     *     leaving (false) the queue.</li>
     * </ul>
     *
     * @return A {@link JsonObject} containing the serialized representation of the {@code PlayerQueuePacket}.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("queueId", queueId);
        object.addProperty("playerUUID", playerUUID.toString());
        object.addProperty("joining", joining);
        return object;
    }
}
