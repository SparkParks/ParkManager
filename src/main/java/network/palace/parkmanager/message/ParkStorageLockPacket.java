package network.palace.parkmanager.message;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

import java.util.UUID;

/**
 * Represents the {@code ParkStorageLockPacket} class, which is used to manage the lock status
 * of a park's storage on a specific server. This packet is communicated within a message queue
 * system to convey the necessary information about the lock state.
 *
 * <p>This class extends the {@code MQPacket} base class and provides the required functionality
 * to handle packets with the global packet identifier {@code PARK_STORAGE_LOCK}.</p>
 *
 * <p>The packet contains the following key data fields:</p>
 * <ul>
 *   <li><b>uuid</b>: A {@code UUID} identifying the unique entity for which the storage lock
 *   applies.</li>
 *   <li><b>serverName</b>: A {@code String} representing the name of the server where the
 *   lock operation is performed.</li>
 *   <li><b>storageLock</b>: A {@code boolean} value indicating whether the storage should be
 *   locked ({@code true}) or unlocked ({@code false}).</li>
 * </ul>
 *
 * <p>There are two constructors available:</p>
 * <ul>
 *   <li>The first constructor initializes the packet from a pre-existing {@code JsonObject}.
 *   It deserializes and extracts the necessary fields from the given {@code object}.</li>
 *   <li>The second constructor initializes the packet directly using provided parameters such
 *   as {@code uuid}, {@code serverName}, and {@code storageLock} values.</li>
 * </ul>
 *
 * <p>This class also includes an overridden {@code getJSON()} method, which serializes the packet
 * data into a {@code JsonObject}. The resulting JSON object can then be utilized during communication
 * processes.</p>
 *
 * <p>Typical operations the class is involved in include:</p>
 * <ul>
 *   <li>Identifying the target server for a storage lock operation.</li>
 *   <li>Storing and transmitting the status of the storage lock.</li>
 *   <li>Serializing and deserializing the packet contents for transport within the system's
 *   messaging infrastructure.</li>
 * </ul>
 *
 * <p>Overall, this packet class is part of a system that ensures proper management of park storage
 * lock states across multiple servers in a synchronized and efficient manner.</p>
 */
public class ParkStorageLockPacket extends MQPacket {
    /**
     * <p>Represents the universally unique identifier (UUID) for an instance of the
     * {@code ParkStorageLockPacket} class.</p>
     *
     * <p>Key characteristics of the {@code uuid} field:</p>
     * <ul>
     *   <li>Serves as a globally unique identifier for distinguishing between different
     *   instances of {@code ParkStorageLockPacket}.</li>
     *   <li>Immutable and thread-safe by design, ensuring consistency in behavior
     *   across various system operations.</li>
     *   <li>Useful for serialization and deserialization processes where consistent
     *   identification of packets is required.</li>
     *   <li>Can be used for tracing or debugging individual packet instances
     *   within the system.</li>
     * </ul>
     *
     * <p>The {@code uuid} field is essential for ensuring that each packet instance
     * can be uniquely identified and managed, especially in distributed systems or
     * scenarios requiring reliable message differentiation.</p>
     */
    @Getter private final UUID uuid;

    /**
     * <p>Represents the name of the server associated with this packet.</p>
     *
     * <p>This variable holds a string that uniquely identifies the server in the system.
     * It is utilized in communication packets to refer to or interact with a specific server.</p>
     *
     * <p>Key characteristics:</p>
     * <ul>
     *   <li>Uniquely identifies a server within the network or system.</li>
     *   <li>Serves as an essential link for operations targeting or referencing the server.</li>
     *   <li>Immutable once set, ensuring data consistency and reliability.</li>
     * </ul>
     */
    @Getter private final String serverName;

    /**
     * <p>The {@code storageLock} variable represents a flag indicating the storage lock state
     * for a park storage system.</p>
     *
     * <p>Key characteristics of the {@code storageLock} variable:</p>
     * <ul>
     *   <li>It is a {@code boolean} value that determines whether the storage lock is active.</li>
     *   <li>When the {@code storageLock} is set to {@code true}, it implies that the storage for the
     *   park is currently locked and restricted from modifications.</li>
     *   <li>When the {@code storageLock} is set to {@code false}, it indicates that the storage
     *   is unlocked and available for access or changes.</li>
     * </ul>
     *
     * <p>This variable is immutable and is initialized during object construction of the
     * {@code ParkStorageLockPacket} class. As part of the packet data, it is used to
     * define or relay the state of the park's storage lock in the system.</p>
     */
    @Getter private final boolean storageLock;

    /**
     * Constructs a new {@code ParkStorageLockPacket} object for managing storage lock packets
     * within the system. This packet is used to handle the locking mechanism of park storage
     * by associating a unique identifier (UUID), a server name where the lock applies, and the
     * lock state.
     *
     * <p>The constructor initializes the packet with the packet identifier specific to
     * PARK_STORAGE_LOCK and populates its properties using the provided {@code JsonObject}.
     * </p>
     *
     * @param object A {@code JsonObject} containing the following information:
     * <ul>
     *   <li><b>uuid</b>: A {@code String} representing the unique identifier associated
     *   with the lock. This value is parsed into a {@code UUID}.</li>
     *   <li><b>serverName</b>: A {@code String} indicating the name of the server where
     *   this lock operation is taking place.</li>
     *   <li><b>storageLock</b>: A {@code boolean} flag specifying the lock state,
     *   where {@code true} represents that the storage is locked, and {@code false}
     *   indicates it is unlocked.</li>
     * </ul>
     */
    public ParkStorageLockPacket(JsonObject object) {
        super(PacketID.Global.PARK_STORAGE_LOCK.getId(), object);
        this.uuid = UUID.fromString(object.get("uuid").getAsString());
        this.serverName = object.get("serverName").getAsString();
        this.storageLock = object.get("storageLock").getAsBoolean();
    }

    /**
     * Constructs a {@code ParkStorageLockPacket} object to handle the state of a storage lock
     * for a specific server and UUID combination.
     *
     * <p>This packet is used to modify or query the storage lock status within the system. It
     * represents an action that targets a particular server and user identified by the UUID,
     * with functionality to either enable or disable the storage lock.</p>
     *
     * @param uuid        The unique identifier ({@code UUID}) associated with the user or entity
     *                    targeted by this packet.
     * @param serverName  The name of the server where the storage lock action is being applied.
     * @param storageLock A boolean indicating the desired state of the storage lock:
     *                    <ul>
     *                      <li>{@code true} to enable the storage lock.</li>
     *                      <li>{@code false} to disable the storage lock.</li>
     *                    </ul>
     */
    public ParkStorageLockPacket(UUID uuid, String serverName, boolean storageLock) {
        super(PacketID.Global.PARK_STORAGE_LOCK.getId(), null);
        this.uuid = uuid;
        this.serverName = serverName;
        this.storageLock = storageLock;
    }

    /**
     * Constructs a JSON representation of the packet, including its unique identifier,
     * the server name, and the storage lock status.
     *
     * <p>The generated JSON object includes:</p>
     * <ul>
     *   <li><b>uuid</b>: A string representation of the packet's unique identifier (UUID).</li>
     *   <li><b>serverName</b>: The name of the server associated with this packet.</li>
     *   <li><b>storageLock</b>: A boolean value representing whether storage locking is enabled.</li>
     * </ul>
     *
     * @return a {@link JsonObject} containing the serialized data of the packet.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("serverName", serverName);
        object.addProperty("storageLock", storageLock);
        return object;
    }
}