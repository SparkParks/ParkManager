package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.InventoryUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * The <code>PlayerDropItem</code> class is a listener that handles the event
 * when a player attempts to drop an item. It enforces specific rules based on
 * the player's rank, build mode, and reserved inventory slots.
 * <p>
 * Implements the {@link Listener} interface to allow handling of Bukkit
 * events related to item dropping by players.
 *
 * <p><b>Functionalities:</b></p>
 * <ul>
 *   <li>Checks the player's rank and prevents players below a certain rank
 *   from dropping items.</li>
 *   <li>Allows item drops if the player is in build mode.</li>
 *   <li>Prevents item drops if the dropped item is in a reserved inventory slot.</li>
 * </ul>
 *
 * <p><b>Event Handling:</b></p>
 * <ul>
 *   <li>The event priority is set to {@link EventPriority#HIGHEST}, ensuring this listener
 *   executes late in the event process.</li>
 *   <li>The event is canceled under specific conditions, stopping further processing
 *   of the drop item event.</li>
 * </ul>
 */
public class PlayerDropItem implements Listener {

    /**
     * Handles the {@link PlayerDropItemEvent} when a player attempts to drop an item.
     * This method enforces specific restrictions based on the player's rank, build mode status,
     * and inventory reserved slots.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Prevents players below a certain rank from dropping items.</li>
     *   <li>Allows item drops if the player is in build mode.</li>
     *   <li>Prevents item drops from reserved inventory slots.</li>
     *   <li>Cancels the event when the above conditions are met to prevent the default behavior.</li>
     * </ul>
     *
     * @param event the {@link PlayerDropItemEvent} object triggered when a player attempts to drop an item.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (player.getRank().getRankId() < Rank.CM.getRankId()) {
            // Non-mods can't drop items
            event.setCancelled(true);
            return;
        }
        if (ParkManager.getBuildUtil().isInBuildMode(player.getUniqueId())) return;

        if (InventoryUtil.isReservedSlot(player.getHeldItemSlot())) event.setCancelled(true);
    }
}