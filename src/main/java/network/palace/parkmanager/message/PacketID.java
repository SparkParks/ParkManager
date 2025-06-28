package network.palace.parkmanager.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the {@code PacketID} class, which contains a collection of packet identifiers.
 * These identifiers are utilized across the system to uniquely identify different types
 * of packets sent within the message queue or network communication framework.
 *
 * <p>The PacketID class defines an internal {@code Global} enumeration, where each constant
 * corresponds to a specific functional packet type. Each constant holds an identifier value
 * represented as an integer. This identifier can be used to distinguish between different
 * packet types during processing or routing.</p>
 *
 * <p>Key characteristics of the {@code Global} enumeration:</p>
 * <ul>
 *   <li>Each constant maps to a distinct type of packet, serving a specific purpose within
 *   the system.</li>
 *   <li>Each constant contains an associated numerical {@code id} that represents the
 *   packet identifier.</li>
 *   <li>Packet identifiers are typically used during serialization and deserialization
 *   of packets, as well as for matching packet handlers to specific packet types.</li>
 * </ul>
 *
 * <p>Examples of defined packet types in the {@code Global} enumeration:</p>
 * <ul>
 *   <li><b>BROADCAST</b>: Represents a packet used for broadcasting messages with an id of {@code 1}.</li>
 *   <li><b>MESSAGEBYRANK</b>: Represents a packet for sending messages based on rank, with an id of {@code 2}.</li>
 *   <li><b>CHAT_MUTED</b>: Represents a packet for chat mute events, with an id of {@code 17}.</li>
 *   <li><b>SEND_PLAYER</b>: Represents a packet for sending players to a specific location, with an id of {@code 15}.</li>
 * </ul>
 *
 * <p>Each constant within the {@code Global} enumeration provides a method:</p>
 * <ul>
 *   <li>{@code getId()}: Retrieves the numerical identifier associated with the packet type.</li>
 * </ul>
 *
 * <p>The {@code PacketID} class plays a critical role in organizing and managing
 * packet communication within the system, ensuring that all packet types are uniquely
 * identified and accessible through a central location.</p>
 */
public class PacketID {

    /**
     * <p>The {@code Global} enumeration defines a collection of constants that represent different
     * packet types within the system. These constants are used to uniquely identify and process
     * specific functionalities related to packet communication and event handling.</p>
     *
     * <p>Each constant in the {@code Global} enumeration is associated with a unique integer
     * identifier ({@code id}) that serves as the packet's key within the system. This {@code id}
     * can be utilized during serialization, deserialization, or when routing packets across
     * various system components.</p>
     *
     * <p>Key characteristics of the {@code Global} enumeration include:</p>
     * <ul>
     *   <li>Highly organized and predefined list of packet identifiers for clear and efficient
     *   packet management.</li>
     *   <li>Facilitation of matching between packet definitions and their corresponding handlers.</li>
     *   <li>Support for system-wide communication through distinct functional packet types.</li>
     * </ul>
     *
     * <p>Representative examples of the constants in {@code Global}:</p>
     * <ul>
     *   <li><b>BROADCAST</b> ({@code id=1}): Used for broadcasting general messages.</li>
     *   <li><b>CHAT_MUTED</b> ({@code id=17}): Represents a packet type for handling chat mute events.</li>
     *   <li><b>CREATE_QUEUE</b> ({@code id=28}): Denotes a packet for creating update queues.</li>
     *   <li><b>SEND_PLAYER</b> ({@code id=15}): Used to designate sending a player to a specific destination.</li>
     *   <li><b>KICK_PLAYER</b> ({@code id=19}): Represents a packet for booting a player out of the system.</li>
     * </ul>
     *
     * <p>The {@code Global} enumeration provides the following method:</p>
     * <ul>
     *   <li><b>{@code getId()}</b>: Retrieves the integer {@code id} value of the packet type, which can
     *   then be used for comparing or identifying specific packet instances.</li>
     * </ul>
     *
     * <p>Overall, the {@code Global} enumeration contributes to maintaining a structured and
     * cohesive system for handling various packet operations within the application.</p>
     */
    @AllArgsConstructor
    enum Global {
        BROADCAST(1), MESSAGEBYRANK(2), PROXYRELOAD(3), DM(4), MESSAGE(5), COMPONENTMESSAGE(6),
        CLEARCHAT(7), CREATESERVER(8), DELETESERVER(9), MENTION(10), IGNORE_LIST(11), CHAT(12),
        CHAT_ANALYSIS(13), CHAT_ANALYSIS_RESPONSE(14), SEND_PLAYER(15), CHANGE_CHANNEL(16), CHAT_MUTED(17),
        MENTIONBYRANK(18), KICK_PLAYER(19), KICK_IP(20), MUTE_PLAYER(21), BAN_PROVIDER(22), FRIEND_JOIN(23),
        PARK_STORAGE_LOCK(24), REFRESH_WARPS(25), MULTI_SHOW_START(26), MULTI_SHOW_STOP(27), CREATE_QUEUE(28),
        REMOVE_QUEUE(29), UPDATE_QUEUE(30), PLAYER_QUEUE(31), BROADCAST_COMPONENT(32);

        /**
         * <p>Represents the unique identifier associated with a specific packet type within the {@code Global}
         * enumeration.</p>
         *
         * <p>This identifier is used to:
         * <ul>
         *   <li>Distinguish between various packet types within the system.</li>
         *   <li>Serialize and deserialize packets during communication processes.</li>
         *   <li>Facilitate efficient routing and handling of packets by matching them to their corresponding
         *   handlers or events.</li>
         * </ul>
         * </p>
         *
         * <p>Each constant in the {@code Global} enumeration is assigned a unique {@code id}, ensuring that
         * it can be easily recognized and processed in a consistent manner across different components
         * of the system.</p>
         *
         * <p>Example use cases include packet-related scenarios such as broadcasting messages,
         * muting chat, kick and ban operations, queue management, and more, all tied to their respective
         * unique identifiers.</p>
         */
        @Getter private final int id;
    }
}
