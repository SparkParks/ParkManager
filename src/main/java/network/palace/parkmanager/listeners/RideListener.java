package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.ridemanager.events.RideEndEvent;
import network.palace.ridemanager.handlers.ride.Ride;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

/**
 * The {@code RideListener} class implements the {@code Listener} interface and is responsible
 * for handling ride-related events for a park or amusement system.
 *
 * <p>This class listens for the {@link RideEndEvent} to perform actions such as logging rides for players
 * when a ride has successfully ended. The actual operations are handled asynchronously to avoid
 * blocking the main thread.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 *     <li>Monitors the {@code RideEndEvent} to detect when a ride ends.</li>
 *     <li>Fetches the ride details and cleans the ride name of formatting or color codes.</li>
 *     <li>Logs ride details for each participating player using the {@code RideCounterUtil}.</li>
 * </ul>
 *
 * <p><b>Threading:</b>
 * <ul>
 *     <li>Uses asynchronous operations to ensure player data logging does not interfere with the main thread.</li>
 * </ul>
 */
public class RideListener implements Listener {

    /**
     * Handles the {@link RideEndEvent} to perform actions when a ride in the park has successfully ended.
     *
     * <p>Upon completion of a ride, this method:
     * <ul>
     *     <li>Retrieves the ride details from the event.</li>
     *     <li>Removes any formatting or color codes from the ride name for standardized logging.</li>
     *     <li>Asynchronously logs the ride details for each participating player.</li>
     * </ul>
     *
     * <p><b>Threading:</b> This operation is handled asynchronously to prevent blocking
     * the main thread while processing the list of players.
     *
     * @param event The {@link RideEndEvent} triggered when a ride ends, containing the ride
     *              information and the list of participating players.
     */
    @EventHandler
    public void onRideEnd(RideEndEvent event) {
        Ride ride = event.getRide();
        ParkManager parkManager = ParkManager.getInstance();
        String finalRideName = ChatColor.stripColor(ride.getName());
        Core.runTaskAsynchronously(() -> {
            UUID[] players = event.getPlayers();
            for (UUID uuid : players) {
                CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                if (tp == null) continue;
                ParkManager.getRideCounterUtil().logNewRide(tp, finalRideName, null);
            }
        });
    }

}
