package network.palace.parkmanager.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * <p>The {@code ChatListener} class is a listener that intercepts and controls player chat events.</p>
 *
 * <p>This listener listens for the following event:</p>
 * <ul>
 *     <li>{@code AsyncPlayerChatEvent}</li>
 * </ul>
 *
 * <h2>Primary Functionality:</h2>
 * <ul>
 *     <li>Handles asynchronous player chat messages.</li>
 *     <li>Cancels the chat event upon invocation, preventing the message from being broadcasted.</li>
 * </ul>
 *
 * <h2>Implementation Details:</h2>
 * <ul>
 *     <li>The event is processed with the highest priority ({@code EventPriority.HIGHEST}), ensuring it is invoked
 *         after other listeners have completed processing the event unless explicitly ignored.</li>
 *     <li>The {@code ignoreCancelled} attribute is set to {@code true}, which ensures that the handler
 *         will not act on an already cancelled event.</li>
 * </ul>
 */
public class ChatListener implements Listener {

    /**
     * Handles the {@link AsyncPlayerChatEvent} that is triggered when a player sends a chat message.
     * This method cancels the chat event upon invocation, preventing the message from being sent to other players.
     *
     * <p>Key Details:</p>
     * <ul>
     *     <li>The event is processed with the highest priority ({@link EventPriority#HIGHEST}), ensuring it is
     *         invoked after other listeners unless explicitly ignored.</li>
     *     <li>The {@code ignoreCancelled} attribute is set to {@code true}, meaning this method will not run
     *         if the event has already been cancelled by another handler.</li>
     * </ul>
     *
     * @param event The {@link AsyncPlayerChatEvent} that occurs when a player sends a chat message.
     *              <ul>
     *                  <li>The event contains details such as the player who sent the message, the message content,
     *                      and the list of recipients.</li>
     *                  <li>Upon cancellation, the message will not be broadcasted to other players.</li>
     *              </ul>
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
    }
}
