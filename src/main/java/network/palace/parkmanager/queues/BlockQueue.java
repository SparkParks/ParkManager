package network.palace.parkmanager.queues;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.handlers.QueueType;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

/**
 * Represents a queue type that is tied to a specific block location.
 * <p>
 * The {@code BlockQueue} class extends the generic {@code Queue} class, adding functionality
 * specific to queues associated with a block location. It includes methods to handle queue type
 * identification and spawning of players with respect to a block's location.
 * </p>
 *
 * <p>Key features of this class include:</p>
 * <ul>
 * <li>Maintaining a specific block location for queue-processing.</li>
 * <li>Overriding behavior for handling queue-specific operations such as player spawning.</li>
 * </ul>
 *
 * <p>Typical use cases involve associating queues with physical game constructs or locations
 * represented by blocks in the game world.</p>
 *
 * <b>Class Properties:</b>
 * <ul>
 * <li>{@code blockLocation}: Represents the {@code Location} of a block tied to the queue.</li>
 * </ul>
 */
public class BlockQueue extends Queue {
    /**
     * Represents the specific {@link Location} of a block tied to the queue.
     * <p>
     * This property is used to associate the queue with a physical game block in the world. The block location
     * serves as a reference point for various queue-related operations such as spawning players or processing events
     * at a specific location.
     * </p>
     *
     * <b>Key Features:</b>
     * <ul>
     * <li>Acts as the positional reference for the queue within the game world.</li>
     * <li>Used in operations that depend on the physical location of the associated block.</li>
     * </ul>
     *
     * <p>Modifiable via getter and setter methods, this property allows dynamic updates
     * to the block location when needed.</p>
     *
     * <b>Usage Notes:</b>
     * <ul>
     * <li>Should be set to a valid {@link Location} that corresponds to an actual block in the game world.</li>
     * <li>Changes to this property may affect queue-specific behavior that relies on the block's position.</li>
     * </ul>
     */
    @Getter @Setter private Location blockLocation;

    /**
     * Constructs a new {@code BlockQueue} object tied to a specific block location.
     *
     * <p>The {@code BlockQueue} class represents a queue type within a park system that is
     * linked to a physical block location. This constructor initializes a new instance
     * using the provided parameters, including the block location and other details
     * necessary to set up and manage the queue.</p>
     *
     * @param id         The unique identifier for this queue.
     * @param park       The park type associated with this queue.
     * @param uuid       The universally unique identifier (UUID) for the queue.
     * @param name       The display name of the queue.
     * @param warp       The warp or teleport location associated with the queue.
     * @param groupSize  The number of players allowed in the queue group.
     * @param delay      The delay in queue processing, often used to manage spawn intervals.
     * @param open       A boolean flag indicating whether the queue is currently open or closed.
     * @param station    The location of the station tied to the queue.
     * @param signs      A list of {@code QueueSign} objects associated with the queue, typically used for display or interaction purposes.
     * @param blockLocation The {@code Location} object representing the block location associated with this queue.
     */
    public BlockQueue(String id, ParkType park, UUID uuid, String name, String warp, int groupSize, int delay, boolean open, Location station, List<QueueSign> signs, Location blockLocation) {
        super(id, park, uuid, name, warp, groupSize, delay, open, station, signs);
        this.blockLocation = blockLocation;
    }

    /**
     * Retrieves the type of queue associated with this instance.
     *
     * <p>This method provides the {@link QueueType} that represents the specific
     * type of queue. The queue type defines the behavior and characteristics
     * tied to this queue instance, such as its functionality, mechanics, or
     * interaction with game elements.</p>
     *
     * <p>In the case of the {@code BlockQueue} implementation, this method always
     * returns {@link QueueType#BLOCK}, signifying that the queue is tied to a specific
     * block location and involves the spawning of a redstone block at that location.</p>
     *
     * @return the {@link QueueType} associated with this queue, indicating its specific type.
     */
    @Override
    public QueueType getQueueType() {
        return QueueType.BLOCK;
    }

    /**
     * Handles the spawning of players associated with this {@code BlockQueue}.
     * <p>
     * This method performs operations specific to player spawning in the context of
     * the associated block location. It utilizes the {@code ParkManager}'s delay utility
     * to log a spawning delay at the location tied to this queue.
     * </p>
     *
     * @param players a {@code List} of {@code CPlayer} objects representing the players
     *                that are being spawned in the context of this queue.
     */
    @Override
    protected void handleSpawn(List<CPlayer> players) {
        ParkManager.getDelayUtil().logDelay(blockLocation, 20, Material.REDSTONE_BLOCK);
    }
}
