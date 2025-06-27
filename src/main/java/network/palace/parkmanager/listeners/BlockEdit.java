package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.sign.ServerSign;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * <p>The {@code BlockEdit} class is a listener responsible for handling block breaking
 * and block placing events in a controlled environment. This includes enforcing rules
 * such as rank restrictions and build mode requirements, as well as providing specific
 * behavior for interacting with certain types of blocks like signs.</p>
 *
 * <p>This class listens for the following events:</p>
 * <ul>
 *     <li>{@code BlockBreakEvent}</li>
 *     <li>{@code BlockPlaceEvent}</li>
 * </ul>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *     <li>Restricts block breaking or placing for players below a specific rank.</li>
 *     <li>Prevents block interaction for players not in build mode.</li>
 *     <li>Provides delay messaging for restricted actions.</li>
 *     <li>Handles special behavior for breaking signs linked to {@code ServerSign} entries.</li>
 * </ul>
 *
 * <h2>Implementation Details:</h2>
 * <ul>
 *     <li>Uses a {@code HashMap} to track delays between player warning messages.</li>
 *     <li>Determines player rank through the {@code CPlayer} entity.</li>
 *     <li>Ensures specific behaviors for signs by linking with {@code ServerSign} handlers.</li>
 *     <li>Build mode is verified using the {@code ParkManager#isInBuildMode} utility method.</li>
 * </ul>
 */
public class BlockEdit implements Listener {
    /**
     * A mapping that tracks delays associated with specific players or entities.
     * <p>
     * This variable is used to store delay times for certain actions, such as block-related events,
     * to prevent consecutive actions within a short time frame. The delay is recorded in milliseconds.
     * </p>
     *
     * <p>
     * Key details:
     * <ul>
     *   <li><b>Key (UUID):</b> The unique identifier of a player or entity for whom the delay is being tracked.</li>
     *   <li><b>Value (Long):</b> The timestamp of when the delay expires, measured in milliseconds since the epoch.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This is primarily used within event handling methods to enforce cooldown periods for actions like breaking or placing blocks.
     * </p>
     */
    private HashMap<UUID, Long> delay = new HashMap<>();

    /**
     * Handles the BlockBreakEvent where specific actions are performed based on player rank
     * and build mode status. Includes additional cases for managing interactions with signs.
     *
     * <p>This method first validates the player's rank and build mode access to determine
     * whether block breaking actions should be cancelled. Further, it manages custom behavior
     * related to sign-based interactions using the ServerSign utility.</p>
     *
     * @param event The BlockBreakEvent that occurs when a player breaks a block.
     *              <ul>
     *                  <li>The event's player is checked against rank and build mode permissions.</li>
     *                  <li>If the block is a Sign (or its related types), specific behaviors
     *                      are triggered through the ServerSign system.</li>
     *                  <li>Custom messages are sent to the player when block breaking is disallowed due to build mode.</li>
     *              </ul>
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId()) {
            event.setCancelled(true);
            return;
        } else if (!ParkManager.getBuildUtil().isInBuildMode(player.getUniqueId())) {
            event.setCancelled(true);

            if (delay.containsKey(player.getUniqueId()) && System.currentTimeMillis() >= delay.get(player.getUniqueId()))
                delay.remove(player.getUniqueId());

            player.sendMessage(ChatColor.RED + "You must be in Build Mode to break blocks!");
            delay.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
            return;
        }

        Material type = event.getBlock().getType();
        if (type.equals(Material.SIGN) || type.equals(Material.WALL_SIGN) || type.equals(Material.SIGN_POST)) {
            Sign s = (Sign) event.getBlock().getState();
            ServerSign.SignEntry signEntry = ServerSign.getByHeader(s.getLine(0));
            if (signEntry != null) signEntry.getHandler().onBreak(player, s, event);
        }

    }

    /**
     * Handles the event when a player attempts to place a block in the game.
     * The method enforces specific conditions based on the player's rank and build mode status.
     * <p>
     * If the player's rank is below a specified threshold or if the player is not in Build Mode,
     * the block placement will be canceled, and a relevant message will be sent to the player.
     *
     * @param event The {@link BlockPlaceEvent} triggered when a player places a block.
     *              Contains information about the block placement and the player involved.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId()) {
            event.setCancelled(true);
        } else if (!ParkManager.getBuildUtil().isInBuildMode(player.getUniqueId())) {
            event.setCancelled(true);

            if (delay.containsKey(player.getUniqueId()) && System.currentTimeMillis() >= delay.get(player.getUniqueId()))
                delay.remove(player.getUniqueId());

            player.sendMessage(ChatColor.RED + "You must be in Build Mode to place blocks!");
            delay.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
        }
    }
}