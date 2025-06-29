package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.RideCount;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

import java.util.TreeMap;

/**
 * Utility class for managing and interacting with ride counters for players.
 *
 * <p>The {@code RideCounterUtil} class provides functionality to handle ride counting
 * operations for players, such as retrieving ride counters and logging new rides.
 * It works with external database systems to ensure persistent tracking of rides and
 * leverages caching mechanisms for efficient performance.</p>
 *
 * <p>Main features include:</p>
 * <ul>
 *   <li>Fetching and caching ride counters associated with a specific player.</li>
 *   <li>Logging new rides and updating the associated counters.</li>
 *   <li>Achievements are triggered based on the number of rides logged for a player.</li>
 * </ul>
 *
 * <p><strong>Implementation Notes:</strong></p>
 * <ul>
 *   <li>This class references external systems, such as a database, for retrieving
 *   and storing ride counter data.</li>
 *   <li>Caching is handled per player and updated dynamically during ride logging operations.</li>
 *   <li>Sound effects, achievements, and in-game notifications are triggered for the player
 *   and optionally for a sender, as part of the logging process.</li>
 * </ul>
 *
 * <p><strong>Concurrency Consideration:</strong></p>
 * <p>Methods performing database operations should not be called on the main thread
 * to avoid blocking the game loop.</p>
 */
public class RideCounterUtil {

    /**
     * Retrieves and calculates the ride counters for a specific player.
     *
     * <p>This method fetches the ride count data for a given player from the player's
     * registry cache if it exists. If it is not cached, it retrieves the data from
     * an external source, processes it, and stores it in the player's registry cache
     * for future use. The ride counters are represented as a {@code TreeMap} where the
     * keys are ride names and the values are {@link RideCount} objects.
     *
     * <p>The logic ensures that ride counts for the same ride name and server are
     * aggregated, and new {@link RideCount} instances are created only for distinct rides.
     *
     * @param player an instance of {@code CPlayer} representing the player whose ride
     *               counters are to be retrieved. This parameter must be non-null and
     *               identifies the individual player in the system.
     *
     * @return a {@code TreeMap<String, RideCount>} where:
     *         <ul>
     *           <li>The keys are strings representing the names of the rides.</li>
     *           <li>The values are {@link RideCount} objects that store the count information
     *               and associated server for each ride.</li>
     *         </ul>
     *         The returned map will either be retrieved from the player's cached data or
     *         constructed afresh by processing the external ride counter data.
     */
    public TreeMap<String, RideCount> getRideCounters(CPlayer player) {
        if (player.getRegistry().hasEntry("rideCounterCache"))
            return (TreeMap<String, RideCount>) player.getRegistry().getEntry("rideCounterCache");

        TreeMap<String, RideCount> rides = new TreeMap<>();
        for (Object o : Core.getMongoHandler().getRideCounterData(player.getUniqueId())) {
            Document doc = (Document) o;
            String name = doc.getString("name").trim();
            String server = doc.getString("server").replaceAll("[^A-Za-z ]", "");
            if (rides.containsKey(name) && rides.get(name).serverEquals(server)) {
                rides.get(name).addCount(1);
            } else {
                rides.put(name, new RideCount(name, server));
            }
        }
        player.getRegistry().addEntry("rideCounterCache", rides);

        return rides;
    }

    /**
     * Logs a new ride for the specified player and updates their ride counters.
     *
     * <p>The {@code logNewRide} method performs the following tasks:</p>
     * <ul>
     *   <li>Updates the player's ride count for the given {@code rideName}.</li>
     *   <li>Awards achievements to the player based on the number of distinct rides logged:
     *     <ul>
     *       <li>Achievement 12: 1 ride logged.</li>
     *       <li>Achievement 13: 10 rides logged.</li>
     *       <li>Achievement 14: 20 rides logged.</li>
     *       <li>Achievement 15: 30 rides logged.</li>
     *     </ul>
     *   </li>
     *   <li>Sends feedback to the player and the command sender about the updated ride count.</li>
     *   <li>Plays a sound effect for the player upon successfully logging the ride.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} object representing the player for whom the ride is being logged.
     * @param rideName A {@link String} representing the name of the ride being logged.
     * @param sender An optional {@link CommandSender} object representing the entity that initiated the ride log; it can be {@code null}.
     */
    public void logNewRide(CPlayer player, String rideName, CommandSender sender) {
        Core.getMongoHandler().logRideCounter(player.getUniqueId(), rideName);

        TreeMap<String, RideCount> rides = ParkManager.getRideCounterUtil().getRideCounters(player);
        if (rides.containsKey(rideName)) {
            rides.get(rideName).addCount(1);
        } else {
            rides.put(rideName, new RideCount(rideName, Core.getServerType()));
        }

        if (rides.size() >= 30) {
            player.giveAchievement(15);
        } else if (rides.size() >= 20) {
            player.giveAchievement(14);
        } else if (rides.size() >= 10) {
            player.giveAchievement(13);
        } else if (rides.size() >= 1) {
            player.giveAchievement(12);
        }

        if (sender != null && !(sender instanceof BlockCommandSender))
            sender.sendMessage(ChatColor.GREEN + "Added 1 to " + player.getName() + "'s counter for " + rideName);

        player.sendMessage(ChatColor.GREEN + "--------------" + ChatColor.GOLD + "" + ChatColor.BOLD +
                "Ride Counter" + ChatColor.GREEN + "-------------\n" + ChatColor.YELLOW +
                "Ride Counter for " + ChatColor.AQUA + rideName + ChatColor.YELLOW +
                " is now at " + ChatColor.AQUA + rides.get(rideName).getCount() +
                ChatColor.GREEN + "\n----------------------------------------");
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 100f, 0.75f);
    }
}
