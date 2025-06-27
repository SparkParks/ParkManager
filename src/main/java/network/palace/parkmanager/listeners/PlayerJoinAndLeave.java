package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.events.CorePlayerJoinedEvent;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

/**
 * The {@code PlayerJoinAndLeave} class implements the {@link Listener} interface and manages player
 * connections, including handling events such as pre-login, joining, quitting, and being kicked from the server.
 * This class interacts with various utility and manager classes to set up player data, teleportation, achievements,
 * and queue handling upon their connection or disconnection from the server.
 *
 * <p>It listens for and processes the following events:
 * <ul>
 *   <li>{@link AsyncPlayerPreLoginEvent} - Manages pre-login data retrieval and setup.</li>
 *   <li>{@link PlayerJoinEvent} - Sends join messages, clears inventory, and prepares the player environment.</li>
 *   <li>{@link CorePlayerJoinedEvent} - Handles full player initialization, including login data processing,
 *       build mode settings, teleportation, visibility, and park-specific features.</li>
 *   <li>{@link PlayerQuitEvent} - Handles player disconnection and queuing cleanup on quit.</li>
 *   <li>{@link PlayerKickEvent} - Manages disconnection and cleanup processes when the player is kicked.</li>
 * </ul>
 *
 * <p>This class also includes private helper methods to handle specific disconnection-related tasks.
 */
public class PlayerJoinAndLeave implements Listener {

    /**
     * Handles the {@link AsyncPlayerPreLoginEvent} to initialize and load
     * player-specific data prior to login.
     *
     * <p>This method is invoked asynchronously before a player fully connects
     * to the server. It retrieves and sets up necessary data, including
     * join-related configurations and friend list details, to prepare for the
     * player's session.</p>
     *
     * @param event The {@link AsyncPlayerPreLoginEvent} containing player
     *              connection details, such as their unique identifier (UUID).
     */
    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        ParkManager.getPlayerUtil().addLoginData(uuid,
                Core.getMongoHandler().getParkJoinData(uuid, "buildmode", "settings", "magicband",
                        "fastpass", "outfit", "outfitPurchases"),
                Core.getMongoHandler().getFriendList(uuid));
    }

    /**
     * Handles logic whenever a real player joins the game. This method clears the player's inventory
     * and sends a custom join message to the player if configured.
     *
     * <p>The join message is fetched from the configuration and sent to the player, provided it is not
     * set to "none". If the message is set to "none", no message will be sent.</p>
     *
     * @param event The PlayerJoinEvent triggered when a player joins the server. This event is
     *              provided by the Bukkit event system and contains details about the player joining.
     */
    @EventHandler
    public void onRealPlayerJoin(PlayerJoinEvent event) {
        String join = ParkManager.getConfigUtil().getJoinMessage();
        if (!join.equalsIgnoreCase("none")) event.getPlayer().sendMessage(join);
        event.getPlayer().getInventory().clear();
    }

    /**
     * Handles the actions that should be carried out when a player joins the server.
     * It retrieves player data, configures their settings, manages visibility settings, virtual queues,
     * and teleportation, and grants achievements based on the server type.
     *
     * <p>This method is triggered when a {@link CorePlayerJoinedEvent} occurs and ensures that the player
     * is properly initialized, kicked if an error occurs, or set up with their associated data and preferences.</p>
     *
     * @param event The {@link CorePlayerJoinedEvent} that contains information about the joining player,
     *              including their {@link CPlayer} instance and any related login data.
     */
    @EventHandler
    public void onPlayerJoin(CorePlayerJoinedEvent event) {
        CPlayer player = event.getPlayer();
        boolean buildMode = false;
        Document loginData = ParkManager.getPlayerUtil().removeLoginData(player.getUniqueId());
        if (loginData == null) {
            player.kick(ChatColor.RED + "An error occurred while you were joining, try again in a few minutes!");
            return;
        }

        if (loginData.containsKey("buildmode") && player.getRank().getRankId() >= Rank.CM.getRankId())
            //Only set to build if player is Mod+
            buildMode = loginData.getBoolean("buildmode");
        player.getRegistry().addEntry("friends", loginData.get("friends"));

        Document magicbandData = (Document) loginData.get("magicband");
        ParkManager.getMagicBandManager().handleJoin(player, magicbandData);

        ParkManager.getStorageManager().handleJoin(player, buildMode);

        Document settings = (Document) loginData.get("settings");
        String visibility;
        if (!settings.containsKey("visibility") || !(settings.get("visibility") instanceof String)) {
            visibility = "all";
        } else {
            visibility = settings.getString("visibility");
        }
        ParkManager.getVisibilityUtil().handleJoin(player, visibility);

        String pack;
        if (!settings.containsKey("pack") || !(settings.get("pack") instanceof String)) {
            pack = "ask";
        } else {
            pack = settings.getString("pack");
        }
        ParkManager.getPackManager().handleJoin(player, pack);
        ParkManager.getFastPassKioskManager().handleJoin(player, (Document) loginData.get("fastpass"));
        ParkManager.getWardrobeManager().handleJoin(player, loginData.getString("outfit"), loginData.get("outfitPurchases", ArrayList.class));

        boolean notInVirtualQueue = true;
        for (VirtualQueue queue : ParkManager.getVirtualQueueManager().getQueues()) {
            if (queue.getHoldingAreaLocation() != null && queue.getHoldingAreaMembers().contains(player.getUniqueId())) {
                player.teleport(queue.getHoldingAreaLocation());
                player.getRegistry().addEntry("virtualQueueHoldingArea", true);
                queue.removeFromJoiningToHoldingArea(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "You have been brought to the holding area for " + queue.getName() + "! " +
                        ChatColor.GREEN + "You'll be at the front of the queue soon.");
                notInVirtualQueue = false;
                break;
            }
        }

        if (notInVirtualQueue) {
            if (ParkManager.getConfigUtil().isSpawnOnJoin()) {
                player.teleport(ParkManager.getConfigUtil().getSpawn());
            } else if (ParkManager.getConfigUtil().isWarpOnJoin()) {
                Warp w = null;
                double distance = -1;
                for (Warp warp : new ArrayList<>(ParkWarp.getWarpUtil().getWarps())) {
                    if (warp.getWorld() == null ||
                            !warp.getWorld().equals(player.getWorld()) ||
                            !warp.getServer().equals(Core.getServerType()) ||
                            (warp.getRank() != null && player.getRank().getRankId() < warp.getRank().getRankId()))
                        continue;
                    if (distance == -1) {
                        w = warp;
                        distance = warp.distance(player.getLocation());
                        continue;
                    }
                    double d = warp.distance(player.getLocation());
                    if (d < distance) {
                        w = warp;
                        distance = d;
                    }
                }
                if (w == null) {
                    player.performCommand("spawn");
                    return;
                }
                player.teleport(w);
            }
        }

        player.giveAchievement(0);
        switch (Core.getServerType()) {
            case "MK":
                player.giveAchievement(3);
                break;
            case "Epcot":
                player.giveAchievement(4);
                break;
            case "DHS":
                player.giveAchievement(5);
                break;
            case "AK":
                player.giveAchievement(6);
                break;
            case "Typhoon":
                player.giveAchievement(7);
                break;
            case "DCL":
                player.giveAchievement(8);
                break;
            case "USO":
                player.giveAchievement(21);
                break;
        }
    }

    /**
     * Handles the {@link PlayerQuitEvent} that is triggered when a player leaves the server.
     * <p>
     * This method ensures that when a player disconnects, necessary cleanup operations
     * are performed to properly handle the player's disconnection from the system.
     * It utilizes the player's unique identifier to:
     * </p>
     * <ul>
     *     <li>Remove the player from the server's active storage and management systems.</li>
     *     <li>Ensure the player is removed from all active queues they might have joined.</li>
     * </ul>
     *
     * @param event The {@link PlayerQuitEvent} triggered when a player leaves the server.
     *              Contains information about the player leaving, including their {@code UUID}.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        handleDisconnect(event.getPlayer().getUniqueId());
    }

    /**
     * Handles the {@link PlayerKickEvent}, which is triggered when a player is kicked from the server.
     *
     * <p>This method ensures proper cleanup of the player data when they are disconnected
     * due to a kick. It delegates the handling of the player's disconnection to the {@code handleDisconnect(UUID uuid)} method,
     * which performs tasks such as logging the player out and removing them from any active queues.</p>
     *
     * @param event The {@link PlayerKickEvent} that contains details about the kicked player and the reason for the kick.
     *              <ul>
     *                  <li>The event provides access to the player being kicked using {@code event.getPlayer()}.</li>
     *                  <li>The player's unique identifier (UUID) is obtained through {@code event.getPlayer().getUniqueId()}.</li>
     *              </ul>
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {
        handleDisconnect(event.getPlayer().getUniqueId());
    }

    /**
     * Handles the disconnection of a player by removing their session, retrieving
     * player data, and ensuring they are removed from any active queues.
     *
     * <p>This method performs the following actions:
     * <ul>
     *   <li>Logs the player out via the storage manager.</li>
     *   <li>Retrieves the player using the player manager. If the player does not exist, the method exits.</li>
     *   <li>Instructs the queue manager to remove the player from all active queues.</li>
     * </ul>
     *
     * @param uuid The unique identifier of the player who has disconnected.
     */
    private void handleDisconnect(UUID uuid) {
        ParkManager.getStorageManager().logout(uuid);
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player == null) return;
        ParkManager.getQueueManager().leaveAllQueues(player);
    }
}
