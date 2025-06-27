package network.palace.parkmanager.listeners;

import com.google.gson.JsonObject;
import network.palace.core.events.IncomingMessageEvent;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.message.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

/**
 * Provides a packet listener implementation for handling incoming message events.
 * Implements the {@link Listener} interface.
 * <p>
 * This class listens for {@link IncomingMessageEvent} instances triggered within the
 * application and processes the associated {@link JsonObject} packet based on its unique
 * identifier (`id`). Depending on the `id` value, this class delegates the packet's
 * handling to appropriate components or managers.
 * <p>
 * Packet processing flow:
 * <ul>
 * <li>If the packet does not have an `id`, it is ignored.</li>
 * <li>For packets with specific `id` values:
 *   <ul>
 *     <li>24: Processes a {@link ParkStorageLockPacket} to handle late joins in the park storage system.</li>
 *     <li>28: Processes a {@link CreateQueuePacket} to create a new virtual queue.</li>
 *     <li>29: Processes a {@link RemoveQueuePacket} to remove an existing virtual queue.</li>
 *     <li>30: Processes an {@link UpdateQueuePacket} to update data for an existing virtual queue.</li>
 *     <li>31: Processes a {@link PlayerQueuePacket} to handle player-related queue operations.</li>
 *   </ul>
 * </li>
 * </ul>
 * <p>
 * This implementation leverages the {@link ParkManager} to delegate task-specific actions
 * to corresponding storage or queue management subsystems.
 */
public class PacketListener implements Listener {

    /**
     * Handles an {@link IncomingMessageEvent} by processing its associated {@link JsonObject} packet.
     * The method validates the presence of an "id" field in the packet and, based on its value,
     * delegates functionality to respective subsystems for further processing.
     *
     * <p>The method handles specific packet types based on the "id" field:
     * <ul>
     *   <li><b>id 24:</b> Processes a {@link ParkStorageLockPacket} to manage late joins
     *       in the park's storage system.</li>
     *   <li><b>id 28:</b> Processes a {@link CreateQueuePacket} to create a new virtual queue.</li>
     *   <li><b>id 29:</b> Processes a {@link RemoveQueuePacket} to remove an existing virtual queue.</li>
     *   <li><b>id 30:</b> Processes an {@link UpdateQueuePacket} to update an existing virtual queue.</li>
     *   <li><b>id 31:</b> Processes a {@link PlayerQueuePacket} to handle player interactions
     *       within a queue.</li>
     * </ul>
     * <p>If the packet does not contain a valid "id" field, the event is ignored.
     *
     * @param event the {@link IncomingMessageEvent} containing the JSON packet to be processed
     */
    @EventHandler
    public void onIncomingMessage(IncomingMessageEvent event) {
        JsonObject object = event.getPacket();
        if (!object.has("id")) return;
        int id = object.get("id").getAsInt();
        switch (id) {
            case 24: {
                ParkStorageLockPacket packet = new ParkStorageLockPacket(object);
                UUID uuid = packet.getUuid();
                ParkManager.getStorageManager().joinLate(uuid);
                break;
            }
            case 28: {
                CreateQueuePacket packet = new CreateQueuePacket(object);
                ParkManager.getVirtualQueueManager().handleCreate(packet);
                break;
            }
            case 29: {
                RemoveQueuePacket packet = new RemoveQueuePacket(object);
                ParkManager.getVirtualQueueManager().handleRemove(packet);
                break;
            }
            case 30: {
                UpdateQueuePacket packet = new UpdateQueuePacket(object);
                ParkManager.getVirtualQueueManager().handleUpdate(packet);
                break;
            }
            case 31: {
                PlayerQueuePacket packet = new PlayerQueuePacket(object);
                ParkManager.getVirtualQueueManager().handlePlayer(packet);
                break;
            }
        }
    }
}
