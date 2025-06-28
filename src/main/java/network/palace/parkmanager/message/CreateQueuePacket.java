package network.palace.parkmanager.message;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

/**
 * Represents the {@code CreateQueuePacket} class, which is used for creating new queues
 * in a message handling system. This packet contains the details required for queue creation,
 * such as the queue ID, queue name, holding area, and server information.
 *
 * <p>This class extends {@code MQPacket}, inheriting its core functionality and providing
 * additional attributes and methods specific to the "Create Queue" operation.</p>
 *
 * <p>Key features of the {@code CreateQueuePacket}:</p>
 * <ul>
 *   <li>Holds a unique identifier for the queue being created.</li>
 *   <li>Stores the name of the queue.</li>
 *   <li>Includes the holding area size, which defines the capacity or specific configurations
 *       related to the queue.</li>
 *   <li>Specifies the server on which the queue is being created.</li>
 * </ul>
 *
 * <p>The {@code CreateQueuePacket} provides two constructors:</p>
 * <ul>
 *   <li><b>JSON-based constructor</b>: Accepts a {@code JsonObject} to parse and set the
 *       necessary fields.</li>
 *   <li><b>Parameterized constructor</b>: Takes individual arguments (queue ID, queue name,
 *       holding area, and server) to initialize the packet.</li>
 * </ul>
 *
 * <h3>JSON Conversion</h3>
 * <p>The class overrides the {@code getJSON()} method to generate a {@code JsonObject}
 * containing:</p>
 * <ul>
 *   <li>{@code queueId}: The ID of the queue.</li>
 *   <li>{@code queueName}: The name of the queue.</li>
 *   <li>{@code holdingArea}: The defined holding area for the queue.</li>
 *   <li>{@code server}: The server associated with the queue.</li>
 * </ul>
 *
 * <p>The {@code getJSON()} method ensures the packet can be serialized into a structured JSON format
 * for transmission or logging purposes.</p>
 */
public class CreateQueuePacket extends MQPacket {

    /**
     * <p>Represents the unique identifier for a specific queue within the system.</p>
     *
     * <p>This variable serves as a distinguishing identifier to represent
     * and track a queue's unique identity throughout the system's
     * message queue framework.</p>
     *
     * <p>Key characteristics:</p>
     * <ul>
     *   <li>Ensures uniqueness for every queue, enabling proper identification
     *   and management.</li>
     *   <li>Utilized for creating a new queue, routing, or any
     *   operation where queue identification is necessary.</li>
     *   <li>Immutable and accessible via a getter method.</li>
     * </ul>
     *
     * <p>Typically, this identifier is paired with other attributes of a queue,
     * such as its name, server location, and holding area, to define a
     * complete queue configuration.</p>
     */
    @Getter private final String queueId;

    /**
     * <p>Represents the name of the queue being created or manipulated in a message queue system.</p>
     *
     * <p>The {@code queueName} is a unique identifier or descriptor for the queue, providing
     * a human-readable string representation. It plays a critical role in distinguishing one queue
     * from another and is typically used for tasks such as:</p>
     * <ul>
     *   <li>Identifying the queue within the system for operations like creation, updates, or removal.</li>
     *   <li>Facilitating communication within the message queue framework by associating messages
     *       or tasks with their respective queues.</li>
     *   <li>Enhancing the clarity and organization of the message routing or handling process.</li>
     * </ul>
     *
     * <p>Once assigned, this value is immutable and can be accessed using the getter method provided
     * by the {@code Lombok} library through the {@code @Getter} annotation.</p>
     */
    @Getter private final String queueName;

    /**
     * <p>Represents the server identifier for the queue creation packet.</p>
     *
     * <p>This field holds the name or address of the server related to the
     * queue creation process. It is utilized to precisely designate the server
     * that is involved in the operation of creating a queue within the
     * system's message queue infrastructure.</p>
     *
     * <p>Key characteristics of this field include:</p>
     * <ul>
     *    <li><b>Immutability:</b> This field is declared as final, ensuring that the
     *    server value remains constant once initialized.</li>
     *    <li><b>Usability:</b> Primarily used during serialization, logging,
     *    and communication processes where the server's identification is required.</li>
     * </ul>
     *
     * <p>Example scenarios where this field is significant include:</p>
     * <ul>
     *    <li>Tracking and accessing queue information tied to a specific server.</li>
     *    <li>Ensuring the correct server is targeted during the message queue processing lifecycle.</li>
     * </ul>
     */
    @Getter private final String server;

    /**
     * <p>Represents the {@code holdingArea} field, which specifies the numerical limit
     * or capacity allocated for a queue's holding area. This field indicates the
     * maximum number of entities or operations that can be accommodated within the
     * designated holding space.</p>
     *
     * <p>Key characteristics of the {@code holdingArea} field:</p>
     * <ul>
     *   <li>It is a final field, meaning its value cannot be modified after being
     *   initialized.</li>
     *   <li>It plays a critical role in defining the constraints or boundaries for
     *   queuing operations, ensuring controlled and efficient handling of resources.</li>
     *   <li>It can be accessed using a getter method provided by Lombok's <code>@Getter</code>
     *   annotation.</li>
     * </ul>
     *
     * <p>This field is particularly useful in scenarios involving the management of queues,
     * where defining a holding limit is critical for maintaining system stability,
     * managing resources, and avoiding overflows.</p>
     */
    @Getter private final int holdingArea;

    /**
     * Constructs a {@code CreateQueuePacket} instance using the provided JSON object.
     *
     * <p>This constructor initializes the new {@code CreateQueuePacket} with data extracted
     * from the given JSON object. The JSON object must contain the following fields:</p>
     *
     * <ul>
     *   <li><b>queueId</b>: A {@code String} representing the unique identifier of the queue.</li>
     *   <li><b>queueName</b>: A {@code String} representing the name of the queue.</li>
     *   <li><b>holdingArea</b>: An {@code int} representing the holding area capacity of the queue.</li>
     *   <li><b>server</b>: A {@code String} representing the server associated with the queue.</li>
     * </ul>
     *
     * @param object A {@link JsonObject} containing the required data to initialize the
     *               {@code CreateQueuePacket}.
     *               <p>The structure of the JSON object must include:</p>
     *               <ul>
     *                 <li><b>queueId</b>: The unique identifier of the queue (type: {@code String}).</li>
     *                 <li><b>queueName</b>: The name of the queue (type: {@code String}).</li>
     *                 <li><b>holdingArea</b>: The holding area capacity (type: {@code int}).</li>
     *                 <li><b>server</b>: The server information (type: {@code String}).</li>
     *               </ul>
     */
    public CreateQueuePacket(JsonObject object) {
        super(PacketID.Global.CREATE_QUEUE.getId(), object);
        this.queueId = object.get("queueId").getAsString();
        this.queueName = object.get("queueName").getAsString();
        this.holdingArea = object.get("holdingArea").getAsInt();
        this.server = object.get("server").getAsString();
    }

    /**
     * Constructs a {@code CreateQueuePacket} instance that represents a packet used for creating
     * a queue within the system, transmitting essential data to define the queue's attributes.
     *
     * <p>The packet is initialized with the following parameters:</p>
     * <ul>
     *   <li><b>queueId</b>: A unique identifier for the queue.</li>
     *   <li><b>queueName</b>: A descriptive name for the queue.</li>
     *   <li><b>holdingArea</b>: The capacity of the holding area for the queue, defined as an integer.</li>
     *   <li><b>server</b>: Information about the server associated with this queue.</li>
     * </ul>
     *
     * <p>This packet inherits properties from its superclass, connecting it to the messaging framework.
     * The associated type is defined by the {@link PacketID.Global#CREATE_QUEUE} identifier.</p>
     *
     * @param queueId The unique identifier for the queue.
     * @param queueName The descriptive name of the queue.
     * @param holdingArea The numeric capacity of the queue's holding area.
     * @param server The server associated with this queue.
     */
    public CreateQueuePacket(String queueId, String queueName, int holdingArea, String server) {
        super(PacketID.Global.CREATE_QUEUE.getId(), null);
        this.queueId = queueId;
        this.queueName = queueName;
        this.holdingArea = holdingArea;
        this.server = server;
    }

    /**
     * Retrieves the JSON representation of the current {@code CreateQueuePacket} instance.
     * This method constructs a {@link JsonObject} containing the packet's details such as
     * {@code queueId}, {@code queueName}, {@code holdingArea}, and {@code server} properties.
     *
     * <p>The returned JSON object encapsulates all relevant information about the queue creation
     * packet, which can later be serialized or transmitted as part of a messaging protocol.</p>
     *
     * <p>JSON structure:</p>
     * <ul>
     *   <li><b>queueId</b>: The unique identifier of the queue (as a {@code String}).</li>
     *   <li><b>queueName</b>: The name of the queue (as a {@code String}).</li>
     *   <li><b>holdingArea</b>: The holding area capacity of the queue (as an {@code int}).</li>
     *   <li><b>server</b>: The associated server information (as a {@code String}).</li>
     * </ul>
     *
     * @return A {@link JsonObject} representing the current state of the {@code CreateQueuePacket} instance.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("queueId", queueId);
        object.addProperty("queueName", queueName);
        object.addProperty("holdingArea", holdingArea);
        object.addProperty("server", server);
        return object;
    }
}
