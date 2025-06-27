package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.QueueManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * The {@code PlayerTeleport} class implements the {@link Listener} interface
 * to manage player teleportation events and execute specific actions when a player
 * teleports using a command.
 *
 * <p>This listener is triggered by the {@link PlayerTeleportEvent} with the {@code MONITOR}
 * priority, ensuring it listens to events after other handlers have been executed. It only
 * processes the event if the teleport cause equals {@code COMMAND}.
 *
 * <ul>
 *   <li>Retrieves the {@link CPlayer} instance associated with the player involved in the event.</li>
 *   <li>Logs the player's current location using the {@code TeleportUtil} in {@link ParkManager}.</li>
 *   <li>Ensures the player leaves all ongoing queues managed by the {@link QueueManager} in {@link ParkManager}.</li>
 * </ul>
 *
 * <p>This listener aims to provide enhanced management and logging for player-related activities
 * when teleportation commands are used.
 */
public class PlayerTeleport implements Listener {

    /**
     * Handles the {@link PlayerTeleportEvent} when a player teleports using a command.
     *
     * <p>This method listens for the {@link PlayerTeleportEvent} and executes specific actions
     * if the cause of the teleport is {@link PlayerTeleportEvent.TeleportCause#COMMAND}.
     *
     * <ul>
     *   <li>Retrieves the {@link CPlayer} instance associated with the player involved in the event.</li>
     *   <li>Logs the player's current location using {@code TeleportUtil} in {@link ParkManager}.</li>
     *   <li>Ensures the player leaves all queues they are part of via the {@link QueueManager} in {@link ParkManager}.</li>
     * </ul>
     *
     * @param event the {@link PlayerTeleportEvent} triggered when a player teleports. This event contains
     *              information about the teleport's cause, the player involved, and their locations.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!event.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND)) return;
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        ParkManager.getTeleportUtil().log(player, player.getLocation());
        ParkManager.getQueueManager().leaveAllQueues(player);
    }
}
