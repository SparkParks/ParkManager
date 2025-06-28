package network.palace.parkmanager.queues;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.utils.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Represents a sign associated with a queue in a system, including its location,
 * name, and other attributes.
 * <p>
 * This class provides functionality to manage queue signs and update their state
 * in a Minecraft-like environment.
 * </p>
 *
 * <p>
 * The queue sign can be designated as a "FastPass" sign or a regular queue sign,
 * and displays information such as the queue name, number of players,
 * and wait time on the sign block.
 * </p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *     <li>Stores the physical location of the sign.</li>
 *     <li>Tracks the associated queue name.</li>
 *     <li>Supports designating the sign as a "FastPass" or regular queue sign.</li>
 *     <li>Tracks the number of players currently in the queue.</li>
 *     <li>Displays a wait time dynamically on the sign.</li>
 * </ul>
 *
 * <h2>Functional Behavior:</h2>
 * <p>
 * The {@link #updateSign()} method updates the appearance of the sign block in the
 * game world. It validates the block type to ensure it is a compatible sign type
 * before updating. The text displayed on the sign reflects the current properties
 * of the {@code QueueSign}, such as {@code queueName}, {@code amount}, and {@code wait}.
 * </p>
 *
 * <h2>Constructor:</h2>
 * <p>
 * The constructor initializes the {@code QueueSign} with its location, queue name,
 * "FastPass" designation, the number of players, and a default wait time of "No Wait."
 * </p>
 */
@Getter
public class QueueSign {
    /**
     * Represents the geographical location associated with the {@code QueueSign}.
     * <p>
     * This variable stores the position data where the queue sign is placed.
     * </p>
     * <p>
     * The location is utilized to determine the physical coordinates or setting of the sign within a theme park or other relevant setting.
     * </p>
     */
    private Location location;

    /**
     * Represents the name of the queue associated with this sign.
     *
     * <p>This variable stores the identifier or title of the queue and is used
     * for distinguishing different queues within the system or environment.
     *
     * <p>Key characteristics:
     * <ul>
     *   <li>Mutable: The value can be updated as required via the setter method.</li>
     *   <li>Serves as a critical property of the {@code QueueSign} class.</li>
     * </ul>
     *
     * <p>Common use cases may involve labeling or organizational purposes
     * specific to queue management processes.
     */
    @Setter private String queueName;

    /**
     * A boolean value indicating whether the associated queue allows fast pass access or not.
     * <p>
     * This field is part of the {@code QueueSign} class and determines whether visitors
     * with fast pass privileges can use the queue. When set to {@code true}, fast pass
     * access is enabled; when set to {@code false}, fast pass access is not available.
     * </p>
     */
    private boolean fastPassSign;

    /**
     * Represents the current capacity or count relevant to the queue system.
     * <p>
     * The <code>amount</code> variable is designed to track the number of
     * entities, such as people or items, for the associated <code>QueueSign</code>.
     * This variable is mutable and can be updated to reflect real-time changes.
     * </p>
     *
     * <ul>
     *   <li>
     *     A positive value signifies the count or capacity directly associated
     *     with the queue.
     *   </li>
     *   <li>
     *     A value of zero or less (if applicable) could indicate an empty or
     *     unavailable state.
     *   </li>
     * </ul>
     */
    @Setter private int amount;

    /**
     * Represents the wait time for the queue associated with the QueueSign.
     * <p>
     * This variable stores the wait time in textual format, typically displayed
     * on the associated queue sign to inform users about the estimated time
     * they may need to wait in line.
     * </p>
     *
     * <p>Key Characteristics:</p>
     * <ul>
     *   <li>Modifiable using the associated setter method.</li>
     *   <li>Provides information crucial for user experience in queue management systems.</li>
     * </ul>
     */
    @Setter private String wait;

    /**
     * Constructs a new QueueSign object, initializing its location, queue name,
     * fast pass status, number of available entries, and default wait time.
     *
     * @param location      the location where the queue sign is placed
     * @param queueName     the name of the queue associated with this sign
     * @param fastPassSign  {@code true} if the sign is for a fast pass queue, {@code false} otherwise
     * @param amount        the number of available entries or spots for the queue
     */
    public QueueSign(Location location, String queueName, boolean fastPassSign, int amount) {
        this.location = location;
        this.queueName = queueName;
        this.fastPassSign = fastPassSign;
        this.amount = amount;
        this.wait = "No Wait";
    }

    /**
     * Updates the sign at the stored location to display the relevant queue information.
     *
     * <p>This method checks if the block at the associated location is a valid type of sign
     * (e.g., a standing sign, wall sign, or sign post). If the block is not a sign, the method
     * terminates without making changes.</p>
     *
     * <p>If the block is a sign, it updates the text of the sign to reflect the current queue
     * information. The content displayed depends on whether the queue is marked as "FastPass"
     * or a regular queue:</p>
     *
     * <ul>
     *   <li>If <code>fastPassSign</code> is enabled, the sign shows labels relevant to FastPass with an appropriate header.</li>
     *   <li>Otherwise, it displays information for a regular queue with a header.</li>
     * </ul>
     *
     * <p>The updated text contains:</p>
     * <ul>
     *   <li>A header indicating the queue type (<code>[FastPass]</code> or <code>[Queue]</code>).</li>
     *   <li>The name of the queue (<code>queueName</code>).</li>
     *   <li>The number of players in the queue along with pluralization adjustments.</li>
     *   <li>The current wait time (<code>wait</code>).</li>
     * </ul>
     *
     * <p>If any of the lines on the sign have changed, the sign state is updated to reflect
     * the new content.</p>
     */
    public void updateSign() {
        Block b = location.getBlock();
        if (!b.getType().equals(Material.SIGN) &&
                !b.getType().equals(Material.WALL_SIGN) &&
                !b.getType().equals(Material.SIGN_POST))
            return;
        Sign s = (Sign) b.getState();
        String[] lines;
        if (fastPassSign) {
            lines = new String[]{ChatColor.BLUE + "[FastPass]", queueName, amount
                    + " Player" + TextUtil.pluralize(amount), wait};
        } else {
            lines = new String[]{ChatColor.BLUE + "[Queue]", queueName, amount
                    + " Player" + TextUtil.pluralize(amount), wait};
        }
        boolean updated = false;
        for (int i = 0; i < lines.length; i++) {
            if (!s.getLine(i).equals(lines[i])) {
                s.setLine(i, lines[i]);
                updated = true;
            }
        }
        if (updated) s.update();
    }
}
