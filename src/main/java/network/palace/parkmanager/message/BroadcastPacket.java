package network.palace.parkmanager.message;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

/**
 * Represents a broadcast packet within the message queue system. This packet is used to send
 * a broadcast message and contains information about the message sender and the message content.
 * <p>
 * The BroadcastPacket class provides capabilities to:
 * <ul>
 *     <li>Send a broadcast message with the sender and message content.</li>
 *     <li>Deserialize a {@link JsonObject} instance to create a BroadcastPacket
 *         with data extracted from the JSON object.</li>
 * </ul>
 * <p>
 * The class extends {@link MQPacket} and overrides the {@link MQPacket#getJSON()} method
 * to support serializing the broadcast packet details into a {@link JsonObject}.
 */
public class BroadcastPacket extends MQPacket {
    /**
     * Represents the identifier of the sender in a broadcast message.
     * <p>
     * The <code>sender</code> field holds the name or designation of the entity
     * initiating the broadcast message. This value is immutable and is established
     * when the {@link BroadcastPacket} is instantiated.
     * </p>
     * <p>
     * Primary usage:
     * <ul>
     *     <li>Identifies the origin of the message for display or processing purposes.</li>
     *     <li>Used during serialization to include sender information in the broadcast message's JSON representation.</li>
     * </ul>
     * </p>
     */
    @Getter private final String sender, /**
     * The content of the broadcast message to be sent to relevant receivers.
     * <p>
     * This variable stores the textual content of the message being broadcast
     * through the messaging system. It is used in conjunction with the {@code sender}
     * to format and deliver the broadcast packet.
     * </p>
     * <p>
     * Key characteristics of this variable:
     * <ul>
     *     <li>It represents the main content or body of the broadcast message.</li>
     *     <li>It is initialized via the constructor when creating a {@link BroadcastPacket} instance.</li>
     *     <li>It is serialized into JSON format when the {@link BroadcastPacket#getJSON()} method is called.</li>
     * </ul>
     * </p>
     */
    message;

    /**
     * Constructs a new {@code BroadcastPacket} instance by deserializing the provided JSON object.
     * The packet represents a broadcast message in the system, which includes information
     * about the sender of the message and the message content.
     *
     * <p>This constructor extracts the following fields from the provided {@code JsonObject}:
     * <ul>
     *     <li><b>sender</b>: The name or identifier of the entity sending the broadcast.</li>
     *     <li><b>message</b>: The content of the broadcast message.</li>
     * </ul>
     *
     * @param object the {@link JsonObject} containing the serialized data representing the broadcast packet.
     *               This object is expected to have the following keys:
     *               <ul>
     *                   <li><b>"sender"</b>: A {@code String} representing the sender.</li>
     *                   <li><b>"message"</b>: A {@code String} representing the message content.</li>
     *               </ul>
     *               If any expected key is missing or its value is not of the expected type,
     *               a {@link ClassCastException} or {@link NullPointerException} may be thrown.
     */
    public BroadcastPacket(JsonObject object) {
        super(PacketID.Global.BROADCAST.getId(), object);
        this.sender = object.get("sender").getAsString();
        this.message = object.get("message").getAsString();
    }

    /**
     * Constructs a new {@code BroadcastPacket} instance with the specified sender and message content.
     * <p>
     * This constructor initializes the broadcast packet with the specified sender and message values
     * and assigns the packet ID specific to the broadcast packet.
     * </p>
     *
     * @param sender The unique identifier or name of the sender who is sending the broadcast message.
     *               <p>This value should be a non-null string representation indicating the source of the message.</p>
     * @param message The content of the broadcast message being sent.
     *                <p>This value should be a non-null string containing the message that is intended
     *                for broadcasting to recipients.</p>
     */
    public BroadcastPacket(String sender, String message) {
        super(PacketID.Global.BROADCAST.getId(), null);
        this.sender = sender;
        this.message = message;
    }

    /**
     * Serializes the broadcast packet details into a {@link JsonObject}.
     * <p>
     * This method overrides {@link MQPacket#getJSON()} to include additional properties specific
     * to the broadcast packet:
     * <ul>
     *     <li><b>sender</b>: The name or identifier of the sender.</li>
     *     <li><b>message</b>: The content of the broadcast message.</li>
     * </ul>
     *
     * @return A {@link JsonObject} representation of this broadcast packet, including the sender and message properties.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("sender", sender);
        object.addProperty("message", message);
        return object;
    }
}
