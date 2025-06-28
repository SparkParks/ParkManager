package network.palace.parkmanager.message;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

/**
 * Represents the {@code RemoveQueuePacket}, a subclass of {@code MQPacket} used for removing a queue
 * within the system using its unique identifier.
 *
 * <p>This packet type is identified by the {@code PacketID.Global.REMOVE_QUEUE} identifier, ensuring
 * the packet is recognized and processed as a request to remove a specific queue.</p>
 *
 * <p>Instances of this class can be created using either a {@code JsonObject} containing the queue's
 * identifier or directly using a string representation of the {@code queueId}. The class provides
 * serialization functionality to extract the packet's data as a {@code JsonObject}.</p>
 *
 * <p>Key functionality of the {@code RemoveQueuePacket}:</p>
 * <ul>
 *   <li>Holds the unique {@code queueId} of the queue to be removed.</li>
 *   <li>Supports serialization into a JSON format, including base packet data and the queue identifier.</li>
 *   <li>Utilized in scenarios where a queue needs to be programmatically removed from the system.</li>
 * </ul>
 *
 * <p>The following constructors are provided:</p>
 * <ul>
 *   <li>A constructor that accepts a {@code JsonObject}, extracting the queue identifier from the input.</li>
 *   <li>A constructor that directly accepts the {@code queueId} as a string.</li>
 * </ul>
 *
 * <p>The packet includes the following additional operations:</p>
 * <ul>
 *   <li>{@code getJSON()}: Serializes the packet data into a {@code JsonObject}, including the unique
 *   {@code queueId} and base packet data from its superclass.</li>
 * </ul>
 *
 * <p>This class plays a role in the message queue or network communication framework, allowing efficient
 * and structured management of queue removal requests.</p>
 */
public class RemoveQueuePacket extends MQPacket {
    /**
     * Represents the unique identifier of the queue to be removed.
     *
     * <p>This field is used within the {@code RemoveQueuePacket} to specify the target queue
     * that should be removed from the system. The value of {@code queueId} uniquely identifies
     * a queue instance, ensuring that the removal operation is performed on the correct queue.</p>
     *
     * <p>Key characteristics:</p>
     * <ul>
     *   <li>It is a {@code String} representing the unique identifier of the queue.</li>
     *   <li>It is final, ensuring the identifier cannot be changed after initialization.</li>
     *   <li>Accessible via a getter method for read-only access.</li>
     * </ul>
     */
    @Getter private final String queueId;

    /**
     * Constructs an instance of {@code RemoveQueuePacket} using a {@code JsonObject}.
     *
     * <p>This constructor initializes the packet with a {@code queueId} extracted
     * from the provided {@code JsonObject}. The {@code queueId} represents the
     * unique identifier of the queue that needs to be removed.</p>
     *
     * @param object A {@code JsonObject} containing the key "queueId", which specifies
     *               the unique identifier of the queue to be removed.
     */
    public RemoveQueuePacket(JsonObject object) {
        super(PacketID.Global.REMOVE_QUEUE.getId(), object);
        this.queueId = object.get("queueId").getAsString();
    }

    /**
     * Constructs a new {@code RemoveQueuePacket} with the specified queue identifier.
     *
     * <p>This constructor initializes a {@code RemoveQueuePacket} instance to represent a request for
     * removing a queue from the system. The unique identifier of the queue to be removed should be
     * specified as the {@code queueId} parameter.</p>
     *
     * @param queueId The unique identifier of the queue to be removed. This value must not be null
     *                and should match the identifier of an existing queue.
     */
    public RemoveQueuePacket(String queueId) {
        super(PacketID.Global.REMOVE_QUEUE.getId(), null);
        this.queueId = queueId;
    }

    /**
     * Serializes the current instance of the {@code RemoveQueuePacket} into a {@code JsonObject}.
     * <p>
     * This method includes:
     * <ul>
     *   <li>The base JSON data inherited from the superclass.</li>
     *   <li>The {@code queueId} property representing the unique identifier of the queue being removed.</li>
     * </ul>
     *
     * @return A {@code JsonObject} containing serialized data of the packet, including the base properties
     * and the {@code queueId}.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("queueId", queueId);
        return object;
    }
}
