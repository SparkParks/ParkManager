package network.palace.parkmanager.message;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>The {@code UpdateQueuePacket} class represents a specific type of packet used for
 * managing and updating a queue within the system. This packet is designed to be sent
 * through a message queue framework for communication between system components.</p>
 *
 * <p>Key functionality includes:</p>
 * <ul>
 *   <li>Identifying the queue to be updated using a unique {@code queueId}.</li>
 *   <li>Specifying whether the queue is currently open or closed via the {@code open} flag.</li>
 *   <li>Providing the current state of the queue as a list of {@code UUID}s.</li>
 * </ul>
 *
 * <p>This class supports the creation of packets from a JSON object for incoming
 * data deserialization, as well as constructing packets programmatically for outgoing
 * data serialization.</p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Extends {@link MQPacket}, inheriting its fundamental packet features.</li>
 *   <li>Implements mechanisms for serializing the packet's data into a {@code JsonObject}
 *   for transmission.</li>
 *   <li>Automatically parses and validates the queue {@code UUID}s from the provided
 *   JSON object during instantiation.</li>
 * </ul>
 *
 * <p><b>JSON Structure:</b></p>
 * <ul>
 *   <li><b>queueId</b> (String): The unique identifier of the queue.</li>
 *   <li><b>open</b> (Boolean): Indicates whether the queue is open or closed.</li>
 *   <li><b>queue</b> (Array of Strings, optional): A list of {@code UUID}s representing
 *   the current state of the queue.</li>
 * </ul>
 *
 * <p><b>Primary Methods:</b></p>
 * <ul>
 *   <li>{@link #UpdateQueuePacket(JsonObject)}: Constructs the packet by parsing the
 *   provided {@code JsonObject}.</li>
 *   <li>{@link #UpdateQueuePacket(String, boolean, List)}: Constructs the packet
 *   programmatically using the given parameters.</li>
 *   <li>{@link #getJSON()}: Serializes the packet's data into a {@code JsonObject}
 *   for transmission or storage.</li>
 * </ul>
 *
 * <p><b>Use Cases:</b></p>
 * <ul>
 *   <li>Updating the state of a queue in a distributed system.</li>
 *   <li>Communicating queue status changes across system components.</li>
 *   <li>Synchronizing queue data between different services or nodes.</li>
 * </ul>
 *
 * <p>Note that when constructing a packet from a {@code JsonObject}, any invalid
 * {@code UUID} entries in the {@code queue} will be ignored, and the resulting
 * {@code queue} list will only include valid entries.</p>
 */
public class UpdateQueuePacket extends MQPacket {
    /**
     * <p>Represents the unique identifier associated with a message queue. The {@code queueId}
     * is a string that uniquely identifies a specific queue within the system.</p>
     *
     * <p>Key characteristics of {@code queueId}:</p>
     * <ul>
     *   <li>Acts as an identifier to distinguish between different message queues.</li>
     *   <li>Used during communication, serialization, and deserialization processes.</li>
     *   <li>Ensures that updates or actions are directed to the appropriate queue.</li>
     * </ul>
     *
     * <p>The identifier is immutable and intended to be used in scenarios involving
     * queue management, such as creating, updating, or removing a queue within a
     * message queue-based system.</p>
     */
    @Getter private final String queueId;

    /**
     * <p>Represents the state of whether the queue is open or closed.</p>
     *
     * <p>This field is a {@code boolean} value:</p>
     * <ul>
     *   <li><b>{@code true}</b>: Indicates that the queue is currently open and available for use.</li>
     *   <li><b>{@code false}</b>: Indicates that the queue is closed and not accessible.</li>
     * </ul>
     *
     * <p>This property is final, meaning its value is immutable once set. It is used to determine
     * and manage the active state of the queue within the system.</p>
     *
     * <p>The {@code open} field is often utilized in scenarios where operations or updates
     * depend on the availability of the queue, ensuring consistent control over its state.</p>
     */
    @Getter private final boolean open;

    /**
     * Represents the queue of UUIDs maintained within the {@code UpdateQueuePacket} class.
     *
     * <p>The {@code queue} variable is designed to store a list of universally unique identifiers (UUIDs),
     * which are typically used for representing entities or objects in a manner that ensures uniqueness
     * across systems and applications.</p>
     *
     * <p>Key Characteristics:</p>
     * <ul>
     *   <li>Immutable: The {@code queue} is marked as {@code final}, indicating that the reference
     *       to this list cannot be reassigned once initialized.</li>
     *   <li>Encapsulated: Access to the {@code queue} is managed through the getter method provided
     *       by the {@code @Getter} annotation, allowing read-only access from outside the class.</li>
     *   <li>Usage Context: It is primarily related to queue management in the context of the message
     *       or packet functional system, specifically within the {@code UpdateQueuePacket} class.</li>
     * </ul>
     *
     * <p>Purpose:</p>
     * <ul>
     *   <li>To maintain a collection of UUIDs that can represent a set of entities or participants
     *       associated with an update or operation within the system.</li>
     *   <li>To enable efficient and unique identification of objects during processing or
     *       transmission in the system's message queue.</li>
     * </ul>
     */
    @Getter private final List<UUID> queue;

    /**
     * Constructs a new {@code UpdateQueuePacket}, initializing it using the provided JSON object.
     * This packet represents an update to a server-side queue, including its current status and associated members.
     *
     * <p>The primary purpose of this constructor is to parse the JSON object and extract the required
     * data fields relevant to the update queue functionality.</p>
     *
     * <p>Key properties initialized within this constructor:</p>
     * <ul>
     *   <li>{@code queueId}: The unique identifier of the queue.</li>
     *   <li>{@code open}: A boolean value indicating whether the queue is open or closed.</li>
     *   <li>{@code queue}: A list of {@link UUID} objects representing the members in the queue.
     *   If the JSON object does not include a queue, this property is set to {@code null}.</li>
     * </ul>
     *
     * <p>During the initialization:</p>
     * <ul>
     *   <li>The {@code queueId} and {@code open} fields are directly retrieved from the JSON object.</li>
     *   <li>If a "queue" field is present, it is parsed as a JSON array, and each element is converted
     *   into a {@link UUID} object. Any parsing errors are silently ignored.</li>
     * </ul>
     *
     * @param object A {@link JsonObject} containing the data required to construct this packet.
     *               The object must include:
     *               <ul>
     *                 <li>{@code queueId}: A string representing the unique identifier of the queue.</li>
     *                 <li>{@code open}: A boolean indicating the queue's status.</li>
     *               </ul>
     *               Optionally, the object may include:
     *               <ul>
     *                 <li>{@code queue}: A JSON array of strings, where each string represents a {@link UUID}
     *                 of a member in the queue.</li>
     *               </ul>
     */
    public UpdateQueuePacket(JsonObject object) {
        super(PacketID.Global.UPDATE_QUEUE.getId(), object);
        this.queueId = object.get("queueId").getAsString();
        this.open = object.get("open").getAsBoolean();
        if (object.has("queue")) {
            queue = new ArrayList<>();
            JsonArray queueJSON = object.get("queue").getAsJsonArray();
            for (JsonElement e : queueJSON) {
                try {
                    queue.add(UUID.fromString(e.getAsString()));
                } catch (Exception ignored) {
                }
            }
        } else {
            queue = null;
        }
    }

    /**
     * Constructs an instance of the {@code UpdateQueuePacket} class, representing a packet
     * used to update the state of a queue in the system. This can include information
     * about whether the queue is open or closed, as well as the updated list of items
     * within the queue.
     *
     * <p>This packet is identified by the {@code UPDATE_QUEUE} packet ID defined
     * within the {@code PacketID.Global} enumeration.</p>
     *
     * @param queueId The unique identifier of the queue to be updated. This identifier
     *                is used to reference the specific queue within the system.
     * @param open    Specifies whether the queue is open ({@code true}) or closed ({@code false}).
     *                This determines the operational state of the queue.
     * @param queue   A list of {@link UUID} values representing the updated items
     *                or members in the queue. Each UUID corresponds to a unique entity
     *                in the queue.
     */
    public UpdateQueuePacket(String queueId, boolean open, List<UUID> queue) {
        super(PacketID.Global.UPDATE_QUEUE.getId(), null);
        this.queueId = queueId;
        this.open = open;
        this.queue = queue;
    }

    /**
     * Generates a {@link JsonObject} that represents the current state of the {@code UpdateQueuePacket}.
     * The JSON object includes details about the queue's identifier, open status, and its members.
     *
     * <p>The structure of the returned JSON object is as follows:</p>
     * <ul>
     *   <li>{@code queueId} (String): The unique ID of the queue.</li>
     *   <li>{@code open} (Boolean): Represents whether the queue is open ({@code true}) or closed ({@code false}).</li>
     *   <li>{@code queue} (Array of Strings): A JSON array of strings where each string is the UUID of a
     *   queue member. This field is only included if the queue is not {@code null}.</li>
     * </ul>
     *
     * <p>Note: If an error occurs while processing the queue field (e.g., serialization failure), the method
     * returns {@code null}. Ensure to handle null-checks when using this method.</p>
     *
     * @return A {@link JsonObject} containing the queue's metadata and member list, or {@code null} if
     * an error occurs during JSON generation.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("queueId", queueId);
        object.addProperty("open", this.open);
        if (queue != null) {
            try {
                Gson gson = new Gson();
                List<String> list = new ArrayList<>();
                for (UUID uuid : queue) {
                    list.add(uuid.toString());
                }
                object.add("queue", gson.toJsonTree(list).getAsJsonArray());
            } catch (Exception e) {
                return null;
            }
        }
        return object;
    }
}
