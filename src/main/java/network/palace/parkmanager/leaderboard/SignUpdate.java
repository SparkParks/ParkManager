package network.palace.parkmanager.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The {@code SignUpdate} class represents the update operation for a leaderboard sign,
 * encapsulating the sign to be updated and the lines of text that should appear on it.
 *
 * <p>This class is used to store and manage the details pertaining to a leaderboard sign
 * update operation, including the sign instance itself and the new text lines to display.
 *
 * <p><b>Fields:</b>
 * <ul>
 *   <li>{@code sign} - The {@link LeaderboardSign} instance representing the leaderboard sign to be updated.</li>
 *   <li>{@code lines} - An array of {@link String} representing the text lines to be displayed on the sign.</li>
 * </ul>
 *
 * <p>This class is immutable once instantiated.
 *
 * <p><b>Constructor Summary:</b>
 * <ul>
 *   <li>{@code SignUpdate(LeaderboardSign sign, String[] lines)}: Constructs a new {@code SignUpdate} object, initializing the sign and lines properties with the provided values
 * .</li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class SignUpdate {
    /**
     * Represents the {@link LeaderboardSign} instance associated with a leaderboard update operation.
     *
     * <p>This variable holds the specific leaderboard sign that is being updated with new text lines.
     *
     * <p><b>Details:</b>
     * <ul>
     *   <li><b>Type:</b> {@link LeaderboardSign}</li>
     *   <li><b>Visibility:</b> Private</li>
     * </ul>
     *
     * <p>Associated with the {@code SignUpdate} class, this field facilitates encapsulation
     * of the leaderboard sign being modified.
     */
    private LeaderboardSign sign;

    /**
     * An array of {@link String} representing the lines of text to be displayed on the leaderboard sign.
     *
     * <p>This field stores the new content that should appear on the sign after the update operation.
     * Each element in the array corresponds to one line of text on the sign.
     */
    private String[] lines;
}
