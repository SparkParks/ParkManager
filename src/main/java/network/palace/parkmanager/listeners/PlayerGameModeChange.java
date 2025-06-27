package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 * Listener class that handles the PlayerGameModeChangeEvent to enforce specific game mode rules.
 * <p>
 * This class listens to game mode change events and restricts the player's ability to change
 * their game mode based on their role, build status, and rank within the system.
 * </p>
 *
 * <h3>Behavior:</h3>
 * <ul>
 *   <li>If the player is in build mode, only <code>CREATIVE</code> and <code>SPECTATOR</code>
 *   modes are allowed. Other game modes are cancelled.</li>
 *   <li>If the player is not in build mode:
 *     <ul>
 *       <li>Players with ranks equal to or higher than <code>TRAINEEBUILD</code> are allowed
 *       to use only the <code>SURVIVAL</code> game mode. Other game modes are cancelled.</li>
 *       <li>Players with ranks lower than <code>TRAINEEBUILD</code> are restricted to
 *       <code>ADVENTURE</code> game mode. Any other game mode is cancelled.</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>This class is designed to integrate with the <code>CPlayer</code> and rank management
 * system to enforce these rules. Any attempt to bypass these restrictions is programmatically
 * overridden by cancelling the event.</p>
 */
public class PlayerGameModeChange implements Listener {

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        GameMode gameMode = event.getNewGameMode();
        if (ParkManager.getBuildUtil().isInBuildMode(player)) {
            if (!gameMode.equals(GameMode.CREATIVE) && !gameMode.equals(GameMode.SPECTATOR)) {
                event.setCancelled(true);
            }
        } else {
            if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId()) {
                if (!gameMode.equals(GameMode.SURVIVAL)) {
                    event.setCancelled(true);
                }
            } else if (!gameMode.equals(GameMode.ADVENTURE)) {
                event.setCancelled(true);
            }
        }
    }
}
