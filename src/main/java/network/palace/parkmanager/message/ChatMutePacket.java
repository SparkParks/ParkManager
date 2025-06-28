package network.palace.parkmanager.message;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

/**
 * <p>The {@code ChatMutePacket} class represents a packet for handling chat mute events.
 * This packet is used to indicate whether a specific channel or server has been muted or unmuted.</p>
 *
 * <p>This packet extends the {@link MQPacket} class and utilizes a {@link JsonObject} to serialize
 * and deserialize its data fields.</p>
 *
 * <p>Key properties:</p>
 * <ul>
 *   <li><b>channel</b>: The chat channel or server name associated with the mute event.</li>
 *   <li><b>source</b>: The name or identifier of the entity (e.g., user, system) triggering the mute event.</li>
 *   <li><b>muted</b>: A boolean value indicating whether the associated channel or server has been muted
 *   ({@code true}) or unmuted ({@code false}).</li>
 * </ul>
 *
 * <p>This class provides two constructors:</p>
 * <ul>
 *   <li>A constructor that initializes the object from a {@link JsonObject} containing the packet data.</li>
 *   <li>A constructor that allows the packet to be created with explicit {@code channel}, {@code source}, and
 *   {@code muted} values.</li>
 * </ul>
 *
 * <p>The {@link #getJSON()} method is overridden to serialize the packet into a JSON representation,
 * which includes the fields {@code channel}, {@code source}, and {@code muted}, along with its base properties
 * inherited from {@link MQPacket}.</p>
 *
 * <p>Packet ID: {@code PacketID.Global.CHAT_MUTED.getId()}</p>
 */
public class ChatMutePacket extends MQPacket {
    /**
     * <p>Represents the chat channel or server name associated with the mute event.</p>
     *
     * <p>This value identifies the target chat channel or server where the mute action
     * is applied. It may refer to a specific chat room, group, or server in a messaging
     * or communication platform.</p>
     *
     * <p>Key characteristics:</p>
     * <ul>
     *   <li>Immutable: Once initialized, the value cannot be changed.</li>
     *   <li>Utilized in conjunction with the {@code source} and {@code muted} fields of
     *   {@code ChatMutePacket} to link the mute/unmute action to a specific channel.</li>
     * </ul>
     */
    // ParkChat, or server name
    @Getter private final String channel, /**
     * <p>Represents the name or identifier of the entity initiating the mute event.</p>
     *
     * <p>This could typically be the username of a user, or a system identifier if triggered by the system.
     * This variable helps in identifying the origin of the action taken on the chat channel or server.</p>
     */
    source;

    /**
     * <p>Indicates whether the associated chat channel or server is muted.</p>
     *
     * <p>This variable holds a boolean value that reflects the mute status:</p>
     * <ul>
     *   <li><b>{@code true}</b>: The chat channel or server is muted, meaning notifications and messages
     *   related to this channel are temporarily disabled.</li>
     *   <li><b>{@code false}</b>: The chat channel or server is not muted, allowing notifications
     *   and messages to be visible as usual.</li>
     * </ul>
     *
     * <p>This property is immutable and is assigned a value during the object's construction.
     * It is primarily intended for determining the state of a mute event as represented
     * by a {@code ChatMutePacket}.</p>
     */
    @Getter private final boolean muted;

    /**
     * Constructs a new {@code ChatMutePacket} object by deserializing data from the provided {@link JsonObject}.
     * The {@code JsonObject} is expected to contain the necessary fields to populate the properties
     * of this packet, including {@code channel}, {@code source}, and {@code muted}.
     *
     * <p>This constructor is typically used when receiving a {@code ChatMutePacket} from an external source
     * or when deserializing a packet from JSON.</p>
     *
     * @param object The {@link JsonObject} containing the data used to initialize the {@code ChatMutePacket}.
     *               <ul>
     *                 <li><b>channel</b>: A {@code String} representing the chat channel or server name.</li>
     *                 <li><b>source</b>: A {@code String} representing the entity (e.g., user or system)
     *                     responsible for the mute event.</li>
     *                 <li><b>muted</b>: A {@code boolean} indicating whether the channel or server is muted
     *                     (<code>true</code>) or unmuted (<code>false</code>).</li>
     *               </ul>
     *               This parameter must not be {@code null}, and the provided {@link JsonObject} must contain
     *               valid values for the expected fields.
     */
    public ChatMutePacket(JsonObject object) {
        super(PacketID.Global.CHAT_MUTED.getId(), object);
        this.channel = object.get("channel").getAsString();
        this.source = object.get("source").getAsString();
        this.muted = object.get("muted").getAsBoolean();
    }

    /**
     * Constructs a {@code ChatMutePacket} object with the specified channel, source, and muted state.
     *
     * <p>This constructor allows explicit creation of a chat mute packet by specifying the chat channel
     * or server name, the source responsible for the mute event, and whether the channel is muted.</p>
     *
     * @param channel the name of the chat channel or server associated with the mute event.
     * @param source the identifier of the entity (e.g., user or system) triggering the mute event.
     * @param muted {@code true} if the channel is muted; {@code false} if it is unmuted.
     */
    public ChatMutePacket(String channel, String source, boolean muted) {
        super(PacketID.Global.CHAT_MUTED.getId(), null);
        this.channel = channel;
        this.source = source;
        this.muted = muted;
    }

    /**
     * Serializes the {@code ChatMutePacket} instance to a JSON representation.
     *
     * <p>This method overrides the {@code getJSON()} method of the {@link MQPacket} base class.
     * It includes the fields specific to this packet:</p>
     * <ul>
     *   <li><b>channel</b>: The name of the chat channel or server associated with the mute event.</li>
     *   <li><b>source</b>: The entity responsible for triggering the mute action.</li>
     *   <li><b>muted</b>: A boolean flag indicating whether the channel or server is muted
     *   ({@code true}) or unmuted ({@code false}).</li>
     * </ul>
     *
     * <p>The returned JSON object also contains any fields from the base {@link MQPacket} class.</p>
     *
     * @return A {@link JsonObject} containing the serialized data for this {@code ChatMutePacket}.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("channel", channel);
        object.addProperty("source", source);
        object.addProperty("muted", muted);
        return object;
    }
}
