package network.palace.parkmanager.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.parkmanager.ParkManager;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Utility class to handle delays for scheduled block updates.
 * This class manages a list of delay entries and processes their updates every tick.
 * Blocks can be scheduled to change type after a specified delay, with an optional
 * additional action to reset the block to air after 20 ticks.
 * </p>
 *
 * <p>
 * The class uses an internal task scheduler to manage and process the list of delay
 * entries on a fixed schedule (1 tick interval). Each delay entry is processed to
 * determine if its associated action should be executed.
 * </p>
 *
 * <p>
 * Intended for use cases where block state changes are needed after a delayed time,
 * such as in minigames or other server-related tasks.
 * </p>
 *
 * <ul>
 *   <li>Blocks are set to the specified type after the given delay in ticks.</li>
 *   <li>If the delay entry originates from a command, the block will reset to air 20 ticks later.</li>
 * </ul>
 *
 * <p><b>Important:</b> This utility assumes the use of a server environment with proper scheduling mechanisms.</p>
 *
 * <h3>Methods Summary:</h3>
 * <ul>
 *   <li><b>logDelay(Location loc, long delay, Material type):</b> Log a delay entry to change a block's type.</li>
 * </ul>
 *
 * <h3>Nested Classes:</h3>
 * <ul>
 *   <li>
 *     <b>DelayEntry:</b> Represents a single scheduled delay entry for changing a block's state.
 *     Processes the countdown and applies the block change when the delay expires.
 *   </li>
 * </ul>
 */
public class DelayUtil {
    /**
     * A list that stores {@link DelayEntry} objects representing scheduled delay entries.
     *
     * <p>This list is used to manage blocks that will change state after a specified number of ticks.
     * Each {@link DelayEntry} contains the following information:
     * </p>
     * <ul>
     *     <li>Location of the block to be updated.</li>
     *     <li>Delay in ticks before the block changes state.</li>
     *     <li>New block type to be set after the delay.</li>
     *     <li>A flag indicating whether the entry was triggered by a command.</li>
     * </ul>
     *
     * <p>The list is dynamically populated as delays are logged through methods like {@code logDelay()},
     * and entries are managed by the {@code tick()} method within {@link DelayEntry}. Entries are removed
     * or modified as the ticks count down and their respective actions are executed.</p>
     */
    private List<DelayEntry> entries = new ArrayList<>();

    /**
     * Utility class to handle delayed tasks or timed processes.
     * <p>
     * The constructor initializes a repeating task using {@link Core#runTaskTimer},
     * which checks and processes a list of delay entries at regular intervals.
     * </p>
     *
     * <p>The primary operations include:</p>
     * <ul>
     *   <li>Creating a snapshot of current delay entries to avoid concurrent modification issues.</li>
     *   <li>Iterating through delay entries and invoking their {@code tick()} method.</li>
     *   <li>Removing entries from the active list once their delay has been completed.</li>
     * </ul>
     *
     * <p>The repeating task runs with an initial delay of 0 ticks and repeats every tick (20 times per second).</p>
     *
     * <p>This class relies on an external {@link ParkManager} instance to manage the scheduling of the task,
     * ensuring proper integration with the overall application lifecycle.</p>
     */
    public DelayUtil() {
        Core.runTaskTimer(ParkManager.getInstance(), () -> {
            List<DelayEntry> list = new ArrayList<>(entries);
            list.forEach(entry -> {
                if (entry.tick()) entries.remove(entry);
            });
        }, 0L, 1L);
    }

    /**
     * Logs a delay entry for a specific location with the given type and delay duration.
     * <p>
     * This method creates a {@code DelayEntry} instance and adds it to an internal collection.
     * The {@code DelayEntry} specifies a location, a duration (in ticks) for the delay,
     * and a type of material to be set after the delay.
     * </p>
     *
     * @param loc   the {@link Location} where the delay entry is applied.
     * @param delay the delay duration (in ticks).
     * @param type  the {@link Material} type to be applied after the delay.
     */
    public void logDelay(Location loc, long delay, Material type) {
        entries.add(new DelayEntry(loc, delay, type, true));
    }

    /**
     * Represents a delay entry for a specific location, type, and duration.
     * <p>
     * A DelayEntry encapsulates the concept of a delay action where a block's type
     * at a specific location is updated either immediately or after a specified number
     * of ticks.
     * </p>
     *
     * <p>Key properties include:</p>
     * <ul>
     *   <li>The {@code Location} where the delay action is applied.</li>
     *   <li>The {@code ticks} remaining before the action is executed.</li>
     *   <li>The {@code Material} type representing the block change.</li>
     *   <li>A boolean flag {@code fromCommand} to indicate if the action originated from a command.</li>
     * </ul>
     *
     * <p>This class is used internally within the {@code DelayUtil} class to manage
     * timed operations on block updates in a consistent and thread-safe manner.</p>
     */
    @Getter
    @AllArgsConstructor
    private class DelayEntry {
        /**
         * Represents the {@link Location} associated with the delay action applied
         * within the {@code DelayEntry} class.
         *
         * <p>The {@code loc} variable denotes the exact position in the Minecraft world where
         * the block update occurs, as defined by its world, coordinates (x, y, z), and
         * rotation (yaw, pitch).</p>
         *
         * <p><strong>Key Details:</strong></p>
         * <ul>
         *     <li>Used to specify the location where the material update happens after the delay.</li>
         *     <li>Determines the target block in the world for the delay action.</li>
         *     <li>Ensures precise location management in the context of scheduled block updates.</li>
         * </ul>
         *
         * <p>This variable is critical for enabling location-specific operations in the context
         * of the {@code DelayUtil} system, where delay timings and block changes are executed consistently.</p>
         */
        private Location loc;

        /**
         * Represents the number of ticks remaining before a specific action is executed.
         *
         * <p>Ticks are a fundamental unit of time measurement within the game, often used
         * to define delays, durations, or intervals. In this context, the {@code ticks} variable
         * typically stores the countdown until a delayed action is performed.</p>
         *
         * <p><strong>Key Characteristics:</strong></p>
         * <ul>
         *   <li>A tick generally refers to 1/20th of a second in standard game time, depending on server performance.</li>
         *   <li>The value decreases over time until it reaches zero, at which point the associated action may be executed.</li>
         *   <li>Used for time-based operations, such as block updates or command-based functionality.</li>
         * </ul>
         */
        private long ticks;

        /**
         * Represents the material type of a block to be used in a delay entry.
         * <p>
         * The {@code type} variable specifies the {@link Material} used to update
         * the block at a given {@link Location} after a defined number of ticks.
         * It indicates the new type of the block once the delay operation is executed.
         * </p>
         *
         * <p><strong>Key Points:</strong></p>
         * <ul>
         *     <li>Defines the type of material to set a block to.</li>
         *     <li>Used in conjunction with block location and tick delay to perform updates.</li>
         *     <li>Central to managing block transformations over time by this utility.</li>
         * </ul>
         */
        private Material type;

        /**
         * Indicates whether a delay action originates from a command or not.
         * <p>
         * This boolean flag is utilized within the {@link DelayEntry} class to distinguish
         * between delay actions triggered by a command and those initiated through other means.
         * <p>
         * <strong>Usage:</strong>
         * <ul>
         *   <li>If {@code true}, the delay action is associated with a command execution.</li>
         *   <li>If {@code false}, the delay action originates from another source.</li>
         * </ul>
         */
        private boolean fromCommand;

        /**
         * Executes a single "tick" of the delay logic for this {@code DelayEntry}.
         * <p>
         * This method decreases the remaining tick count and, if the delay has expired, applies
         * the specified block type to the associated location. If the delay was initiated by a command,
         * it schedules a secondary delay action to revert the block to air after 20 ticks.
         * </p>
         *
         * <ul>
         *   <li>If {@code ticks <= 0}:
         *     <ul>
         *       <li>The block at the {@code loc} is updated to the specified {@code type}.</li>
         *       <li>If the {@code fromCommand} flag is true, a new {@code DelayEntry} is created
         *       to set the block back to air after 20 ticks.</li>
         *       <li>Returns {@code true} as the delay action has been executed.</li>
         *     </ul>
         *   </li>
         *   <li>If {@code ticks > 0}, the tick count is decremented and no further action is performed.</li>
         * </ul>
         *
         * @return {@code true} if the delay has expired and the block update action has occurred;
         *         {@code false} if the delay is still active and no action was taken.
         */
        public boolean tick() {
            if (ticks <= 0) {
                if (fromCommand) entries.add(new DelayEntry(loc, 20, Material.AIR, false));
                loc.getBlock().setType(type);
                return true;
            }
            ticks--;
            return false;
        }
    }
}
