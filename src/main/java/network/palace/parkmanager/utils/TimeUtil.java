package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * The {@code TimeUtil} class provides various utility methods related to
 * time, such as formatting the current time, calculating differences between dates,
 * and other functionality associated with time management. It includes features
 * for setting up and managing watches for players in a specific context.
 *
 * <p>Key functionalities include:
 * <ul>
 *     <li>Displaying the current time in a specific time zone (EST).</li>
 *     <li>Managing player-specific watch views and real-time updates.</li>
 *     <li>Formatting date differences in a human-readable format.</li>
 *     <li>Returning pre-defined time values based on specific string inputs (e.g., "6AM").</li>
 *     <li>Calculating precise millisecond representation of the current second.</li>
 * </ul>
 */
public class TimeUtil {
    /**
     * A list of unique identifiers representing players who are currently watching
     * or viewing a specific watch-related context in the application.
     *
     * <p>The list is maintained to keep track of the viewers interacting with
     * the watch feature. Each entry in the list corresponds to the UUID of a player,
     * ensuring unique identification of participants.</p>
     *
     * <ul>
     *   <li>Used to manage and access watcher information for certain operations.</li>
     *   <li>Handles dynamic addition and removal of players viewing the watch.</li>
     *   <li>Ensures no duplication as UUIDs are inherently unique.</li>
     * </ul>
     */
    private List<UUID> watchViewers = new ArrayList<>();

    /**
     * Default constructor for the TimeUtil class.
     * <p>
     * This constructor initializes a task that updates watch viewers with the
     * current watch time through an action bar update mechanism. The task is
     * executed periodically on a defined timing schedule.
     *
     * <p>
     * Functionality:
     * <ul>
     * <li>Calculates the delay required to align task execution with 20 ticks per second.</li>
     * <li>Schedules a repeating task that fetches the current watch time text and displays
     * it to all players in the `watchViewers` collection.</li>
     * <li>Ensures that only valid players are processed while updating the action bar.</li>
     * </ul>
     *
     * <p>
     * Side Effects:
     * <ul>
     * <li>Starts a scheduled task that runs indefinitely at a frequency of 20 ticks.</li>
     * <li>Displays text related to the current time on the action bar for selected viewers.</li>
     * </ul>
     *
     * <p>
     * Note: Uses the {@code Core.runTaskTimer} method to set up the periodic task and the
     * {@code getWatchTimeText()} method to retrieve the watch time to be displayed.
     */
    public TimeUtil() {
        long milliseconds = System.currentTimeMillis() - ((System.currentTimeMillis() / 1000) * 1000);
        long delay = (long) Math.floor(20 - ((milliseconds * 20) / 1000.0));
        Core.runTaskTimer(ParkManager.getInstance(), () -> {
            String watchText = getWatchTimeText();
            watchViewers.forEach(uuid -> {
                CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                if (player == null) return;
                player.getActionBar().show(watchText);
            });
        }, delay + 100, 20L);
    }

    /**
     * Adds the provided player to the list of watch viewers if they are not already in it
     * and shows the current watch time on their action bar.
     * <p>
     * This method checks if the given player is already a viewer of the watch. If not,
     * their UUID is added to the list of {@code watchViewers}, and the current watch
     * time is displayed to the player via their action bar.
     *
     * @param player The {@code CPlayer} object that represents the player who is selecting the watch.
     */
    public void selectWatch(CPlayer player) {
        if (!watchViewers.contains(player.getUniqueId()) && watchViewers.add(player.getUniqueId()))
            player.getActionBar().show(getWatchTimeText());
    }

    /**
     * Removes the specified player from the watch list, if present, and clears their action bar message.
     *
     * <p>This method unselects the watch status of the given player by removing their unique ID
     * from the watch viewers and hides their action bar text.
     *
     * @param player The {@code CPlayer} instance representing the player to be unselected from the watch list.
     */
    public void unselectWatch(CPlayer player) {
        if (watchViewers.remove(player.getUniqueId())) player.getActionBar().show("");
    }

    /**
     * Retrieves the current date and time in the "America/New_York" time zone.
     *
     * <p>This method obtains the current time using the system's default clock,
     * adjusts it to the "America/New_York" time zone using {@link ZonedDateTime},
     * and returns the result.
     *
     * @return the current date and time as a {@link ZonedDateTime} object,
     *         adjusted to the "America/New_York" time zone.
     */
    public static ZonedDateTime getCurrentTime() {
        return LocalDateTime.now().atZone(TimeZone.getTimeZone("America/New_York").toZoneId());
    }

    /**
     * Generates a formatted string representing the current time in Eastern Standard Time (EST),
     * including hour, minute, second, and AM/PM designation.
     *
     * <p>This method retrieves the current time using {@code TimeUtil.getCurrentTime()}, formats it
     * appropriately, and applies color codes for use in contexts such as chat or user interfaces.
     *
     * <p>The time is displayed in a 12-hour format, with leading zeroes added to minutes and seconds
     * when values are less than 10.
     *
     * <p>Output example (colors not rendered in plain text):
     * <ul>
     *     <li><b>Current time in EST:</b> 3:05:07 PM</li>
     * </ul>
     *
     * @return A string containing the formatted current time in EST, with specific color codes for styling.
     */
    public static String getWatchTimeText() {
        ZonedDateTime current = TimeUtil.getCurrentTime();

        int hour = current.getHour();
        int minute = current.getMinute();
        int second = current.getSecond();

        return ChatColor.YELLOW + "" + ChatColor.BOLD + "Current time in EST: " + ChatColor.GREEN +
                ((hour > 12 ? hour - 12 : (hour < 1 ? 12 : hour)) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second) + " " + (hour >= 12 ? "PM" : "AM"));
    }

    /**
     * Calculates the start of the next second in milliseconds based on the given time.
     * <p>
     * This method takes an input time in milliseconds and computes the millisecond
     * value of the start of the next second.
     *
     * @param time The input time in milliseconds.
     * <p>
     * <ul>
     *   <li>Must be a positive long value representing time in milliseconds.</li>
     *   <li>Represents the elapsed time since the epoch (January 1, 1970, 00:00:00 GMT).</li>
     * </ul>
     *
     * @return The start of the next second in milliseconds as a {@code long}.
     */
    public static long getCurrentSecondInMillis(long time) {
        return ((time / 1000) + 1) * 1000;
    }

    /**
     * Returns the current time rounded up to the next nearest second in milliseconds.
     * <p>
     * This method calculates the current system time in milliseconds and rounds
     * it up to the next second boundary in milliseconds.
     * </p>
     *
     * @return The current time, rounded to the nearest second in milliseconds.
     */
    public static long getCurrentSecondInMillis() {
        return getCurrentSecondInMillis(System.currentTimeMillis());
    }

    /**
     * Converts a string representation of a specific time to its corresponding
     * numerical value in milliseconds.
     *
     * <p>The method maps specific time strings (e.g., "6AM", "9AM") to predefined
     * millisecond values. If an unrecognized string is provided, the method returns -1.</p>
     *
     * @param s A {@code String} representing a specific time. Expected values include:
     * <ul>
     *   <li>"6AM"</li>
     *   <li>"9AM"</li>
     *   <li>"12PM"</li>
     *   <li>"3PM"</li>
     *   <li>"6PM"</li>
     *   <li>"9PM"</li>
     *   <li>"12AM"</li>
     *   <li>"3AM"</li>
     * </ul>
     * Any other input will result in a return value of -1.
     *
     * @return A {@code long} representing the corresponding time in milliseconds:
     * <ul>
     *   <li>0 for "6AM"</li>
     *   <li>3000 for "9AM"</li>
     *   <li>6000 for "12PM"</li>
     *   <li>9000 for "3PM"</li>
     *   <li>12000 for "6PM"</li>
     *   <li>15000 for "9PM"</li>
     *   <li>18000 for "12AM"</li>
     *   <li>21000 for "3AM"</li>
     * </ul>
     * Returns -1 if the input string does not match any of the predefined values.
     */
    public static long getTime(String s) {
        switch (s) {
            case "6AM":
                return 0;
            case "9AM":
                return 3000;
            case "12PM":
                return 6000;
            case "3PM":
                return 9000;
            case "6PM":
                return 12000;
            case "9PM":
                return 15000;
            case "12AM":
                return 18000;
            case "3AM":
                return 21000;
        }
        return -1;
    }

    /**
     * Formats the difference between two Calendar instances into a readable string.
     * The output will provide the time difference in terms of years, months, days,
     * hours, minutes, and/or seconds, based on the level of difference between
     * the two dates.
     *
     * <p>This method determines whether the second date occurs before or after
     * the first. The output is constructed based on the time difference in order of
     * significance (years to seconds), up to a maximum accuracy of two levels.
     * For instance, the result could include "1 Year 2 Months" or "3 Hours 15 Minutes".
     * If the dates are equal, it will return "Now".
     *
     * @param fromDate the starting date, represented as a {@link Calendar}.
     *                 This is the date from which the difference is calculated.
     * @param toDate   the target date, represented as a {@link Calendar}.
     *                 This is the date to which the difference is calculated.
     *
     * @return a string describing the difference between the two dates.
     *         The string will indicate the time difference in a human-readable form,
     *         such as "2 Days 4 Hours". Returns "Now" if the dates are equal.
     */
    public static String formatDateDiff(Calendar fromDate, Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate)) {
            return "Now";
        }
        if (toDate.after(fromDate)) {
            future = true;
        }
        StringBuilder sb = new StringBuilder();
        int[] types = {1, 2, 5, 11, 12, 13};

        String[] names = {"Years", "Years", "Months", "Months", "Days",
                "Days", "hr", "hr", "min", "min", "s",
                "s"};

        int accuracy = 0;
        for (int i = 0; i < types.length; i++) {
            if (accuracy > 2) {
                break;
            }
            int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0) {
                accuracy++;
                sb.append(" ").append(diff).append(names[(i * 2)]);
            }
        }
        if (sb.length() == 0) {
            return "Now";
        }
        return sb.toString().trim();
    }

    /**
     * Calculates the difference in a specified unit of time between two given calendar dates.
     * The difference is computed as the number of the specified units (e.g., days, months, years)
     * between the starting date and the ending date.
     *
     * <p>If the <code>future</code> parameter is set to true, the method calculates the difference
     * moving forward in time from the <code>fromDate</code> to the <code>toDate</code>.
     * If it is set to false, the method calculates the difference moving backward in time.</p>
     *
     * @param type the type of the calendar field to calculate the difference for.
     *             Values are based on {@link Calendar} constants such as
     *             <code>Calendar.DATE</code>, <code>Calendar.MONTH</code>, or <code>Calendar.YEAR</code>.
     * @param fromDate the starting <code>Calendar</code> date for the calculation.
     * @param toDate the ending <code>Calendar</code> date for the calculation.
     * @param future a boolean indicating the direction of calculation;
     *               <code>true</code> for forward in time, <code>false</code> for backward.
     * @return the difference in the specified time unit between the two given dates.
     */
    private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future) && (!fromDate.after(toDate)) || (!future)
                && (!fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }
}
