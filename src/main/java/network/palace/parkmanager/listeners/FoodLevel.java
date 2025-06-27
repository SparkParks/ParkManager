package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * The {@code FoodLevel} class implements the {@link Listener} interface to respond to
 * food level change events in the game. This class manages the behavior of food level
 * reduction for players based on their rank.
 *
 * <p>This class contains an event handler method that listens for the
 * {@link FoodLevelChangeEvent} and cancels the event for players with the required rank
 * or higher, effectively preventing reductions in the food level for specific players.
 *
 * <p><b>Event Handling:</b>
 * <ul>
 *   <li>Checks the rank of the player associated with the food level change event.</li>
 *   <li>Cancels the food level reduction if the player's rank is equal to or higher
 *       than the specified {@code CHARACTER} rank.</li>
 * </ul>
 *
 * <p>This ensures that specific ranked players are excluded from being affected by hunger
 * mechanics in the game.
 */
public class FoodLevel implements Listener {

    /**
     * Handles the food level change event for entities.
     *
     * <p>This method intercepts the {@link FoodLevelChangeEvent} triggered in the game and
     * determines whether the event should be canceled for certain players based on their rank.
     *
     * <p><b>Functionality:</b>
     * <ul>
     *   <li>Retrieves the player associated with the entity involved in the event.</li>
     *   <li>Checks the rank of the player to determine eligibility for excluding the hunger mechanic.</li>
     *   <li>If the player's rank is equal to or higher than the specified {@code CHARACTER} rank,
     *       the food level reduction event is canceled.</li>
     * </ul>
     *
     * @param event the {@link FoodLevelChangeEvent} triggered when an entity's food level changes
     */
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getEntity().getUniqueId());
        if (player != null && player.getRank().getRankId() >= Rank.CHARACTER.getRankId()) {
            event.setCancelled(true);
        }
    }
}
