package network.palace.parkmanager.shows.schedule;

import com.google.common.collect.ImmutableMap;
import com.mongodb.Block;
import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * <p>
 * The {@code ScheduleManager} class is responsible for managing and updating the scheduled shows in the system.
 * It handles retrieving the data from a database, sorting the shows, updating their display items, and
 * providing functionalities to manage the schedule interactively.
 * </p>
 *
 * <p>
 * The manager also dynamically generates interface components such as {@code MenuButton}s based on
 * the scheduled shows and their attributes. It ensures proper synchronization to update visual representations
 * of the schedule efficiently.
 * </p>
 *
 * <h3>Features:</h3>
 * <ul>
 * <li>Fetch and update the scheduled shows from the database using asynchronous tasks.</li>
 * <li>Generate and manage {@code MenuButton}s for displaying shows and interactive scheduling.</li>
 * <li>Provide utility functions for formatting time and managing schedules.</li>
 * <li>Enable users to interact with the schedule via editable menu interfaces.</li>
 * </ul>
 *
 * <h3>Thread Safety:</h3>
 * <p>
 * The {@code ScheduleManager} ensures thread-safe updates by executing all scheduling-related tasks
 * either asynchronously or on the main task execution thread.
 * </p>
 */
public class ScheduleManager {
    /**
     * A list of scheduled shows managed by the {@code ScheduleManager}.
     * <p>
     * This collection contains {@link ScheduledShow} objects, each representing
     * individual scheduled shows with details such as type, day, raw time,
     * and formatted time.
     * </p>
     *
     * <ul>
     * <li>Used to maintain the current state of scheduled shows.</li>
     * <li>Can be updated dynamically through the {@link ScheduleManager#updateShows()} method.</li>
     * <li>Provides access to scheduled shows for further manipulation or viewing
     *    through the {@link ScheduleManager#getShows()} method.</li>
     * </ul>
     */
    private List<ScheduledShow> shows = new ArrayList<>();

    /**
     * <p>Represents a collection of scheduled show times used for managing
     * and displaying show schedules within the system.</p>
     *
     * <p>This list is used to store time entries, typically formatted as strings
     * (e.g., "12:00 PM", "06:30 PM"), which correlate to scheduled show times.</p>
     *
     * <p>The variable is primarily utilized within scheduling operations such as:</p>
     * <ul>
     *     <li>Updating the list of available show times.</li>
     *     <li>Managing and displaying formatted show times to users.</li>
     *     <li>Mapping show times to relevant show schedules.</li>
     * </ul>
     *
     * <p>This field is immutable and initialized as an empty list.</p>
     *
     * <p><b>Thread-safety:</b> As a private and final field, the list itself cannot be replaced.
     * However, external synchronization is required if accessed concurrently.</p>
     */
    private final List<String> times = new ArrayList<>();

    /**
     * <p>
     * Represents a list of {@link MenuButton} objects associated with the {@code ScheduleManager}.
     * These buttons are used for user interaction in scheduling or managing shows.
     * </p>
     *
     * <p>
     * The list is initialized as an empty {@link ArrayList} and is immutable (cannot be reassigned).
     * Modifications to the list should be performed using appropriate methods of {@link List}
     * or {@link ArrayList}.
     * </p>
     */
    @Getter private final List<MenuButton> buttons = new ArrayList<>();

    /**
     * Constructs a new {@code ScheduleManager} instance.
     * <p>
     * During the instantiation, this constructor schedules a repeating asynchronous task
     * that periodically invokes the {@code updateShows} method. The task starts immediately
     * and executes every 36,000 ticks.
     * </p>
     * <p>
     * This manager is primarily responsible for managing and updating scheduled shows in the system.
     * The schedule update process is performed asynchronously to optimize performance and prevent
     * blocking the main thread.
     * </p>
     */
    public ScheduleManager() {
        Core.runTaskTimerAsynchronously(ParkManager.getInstance(), this::updateShows, 0L, 36000L);
    }

    /**
     * Updates the list of scheduled shows and refreshes the associated UI elements.
     * <p>
     * The method retrieves scheduled shows from the database, processes and sorts them by show time and day,
     * and updates the internal state of the {@code shows} field. Finally, it schedules a task to update the
     * relevant UI components.
     *
     * <p><strong>Process:</strong></p>
     * <ol>
     *   <li>Retrieve the scheduled show data from the database via {@code Core.getMongoHandler().getScheduledShows()}.</li>
     *   <li>Convert each database document into a {@code ScheduledShow} object using its attributes:
     *       <ul>
     *         <li>Show type: Determined by {@code ShowType.fromString}.</li>
     *         <li>Show day: Determined by {@code ShowDay.fromString}.</li>
     *         <li>Show raw time.</li>
     *         <li>Formatted show time: Retrieved using {@code getTime}.</li>
     *       </ul>
     *   </li>
     *   <li>Sort the list of shows by raw time, and by day if times are identical.</li>
     *   <li>Schedule an asynchronous task to update the {@code shows} field and refresh the associated UI buttons.</li>
     * </ol>
     *
     * <p><strong>Threading:</strong></p>
     * <ul>
     *   <li>The creation and sorting of the {@code shows} list happens synchronously.</li>
     *   <li>The UI-related updates are delegated to a separate task, maintaining thread safety.</li>
     * </ul>
     *
     * <p><strong>Notes:</strong></p>
     * <ul>
     *   <li>If the data retrieval from the database fails, the {@code shows} list remains unchanged.</li>
     *   <li>The {@code updateButtons} method is invoked to refresh any related UI components in an appropriate thread.</li>
     * </ul>
     */
    public void updateShows() {
        List<ScheduledShow> shows = new ArrayList<>();
        Core.getMongoHandler().getScheduledShows().forEach((Block<? super Document>) doc ->
                shows.add(new ScheduledShow(ShowType.fromString(doc.getString("show")), ShowDay.fromString(doc.getString("day")), doc.getInteger("time"), getTime(doc.getInteger("time")))));
        shows.sort((o1, o2) -> {
            if (o1.getRawTime() == o2.getRawTime()) {
                return o1.getDay().ordinal() - o2.getDay().ordinal();
            }
            return o1.getRawTime() - o2.getRawTime();
        });
        Core.runTask(ParkManager.getInstance(), () -> {
            this.shows = shows;
            updateButtons();
        });
    }

    /**
     * Updates the internal button list to represent schedule-related UI components for each day of the week
     * and scheduled shows with their respective time slots.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Clears the current list of buttons to prepare for re-population.</li>
     *   <li>Creates seven buttons to represent days of the week from Monday to Sunday,
     *       each styled as a banner with unique patterns and labeled with the corresponding day name.</li>
     *   <li>Collects unique show times from the {@code shows} collection and generates corresponding
     *       time slot buttons represented as watches. Each button is positioned appropriately based on its
     *       corresponding time.</li>
     *   <li>Iterates through all scheduled shows and determines their placement on the interface:</li>
     *       <ul>
     *         <li>If the {@code ShowType} is a banner, a custom-patterned banner is created to represent the show.</li>
     *         <li>Otherwise, a generic item defined by the {@code ShowType} is used to represent the show.</li>
     *       </ul>
     * </ul>
     *
     * <p>The resulting button layout provides an interactive overview of weekly schedules
     * and allows each slot to correspond to a specific day, time, and show type.</p>
     *
     * <p>Note: Maximum button indexing ensures that no button slot index exceeds the maximum
     * inventory capacity of most supported systems (54 slots).</p>
     */
    private void updateButtons() {
        buttons.clear();

        ItemStack monday = new ItemStack(Material.BANNER);
        ItemStack tuesday = new ItemStack(Material.BANNER);
        ItemStack wednesday = new ItemStack(Material.BANNER);
        ItemStack thursday = new ItemStack(Material.BANNER);
        ItemStack friday = new ItemStack(Material.BANNER);
        ItemStack saturday = new ItemStack(Material.BANNER);
        ItemStack sunday = new ItemStack(Material.BANNER);
        BannerMeta bm = (BannerMeta) monday.getItemMeta();
        BannerMeta bt = (BannerMeta) tuesday.getItemMeta();
        BannerMeta bw = (BannerMeta) wednesday.getItemMeta();
        BannerMeta bth = (BannerMeta) thursday.getItemMeta();
        BannerMeta bf = (BannerMeta) friday.getItemMeta();
        BannerMeta bs = (BannerMeta) saturday.getItemMeta();
        BannerMeta bsu = (BannerMeta) sunday.getItemMeta();
        List<Pattern> m = new ArrayList<>();
        List<Pattern> t = new ArrayList<>();
        List<Pattern> w = new ArrayList<>();
        List<Pattern> f = new ArrayList<>();
        List<Pattern> s = new ArrayList<>();
        DyeColor lb = DyeColor.LIGHT_BLUE;
        DyeColor bl = DyeColor.BLACK;
        m.add(new Pattern(lb, PatternType.TRIANGLE_TOP));
        m.add(new Pattern(lb, PatternType.TRIANGLES_TOP));
        m.add(new Pattern(lb, PatternType.STRIPE_LEFT));
        m.add(new Pattern(lb, PatternType.STRIPE_RIGHT));
        t.add(new Pattern(lb, PatternType.STRIPE_CENTER));
        t.add(new Pattern(lb, PatternType.STRIPE_TOP));
        w.add(new Pattern(lb, PatternType.TRIANGLE_BOTTOM));
        w.add(new Pattern(bl, PatternType.TRIANGLES_BOTTOM));
        w.add(new Pattern(lb, PatternType.STRIPE_LEFT));
        w.add(new Pattern(lb, PatternType.STRIPE_RIGHT));
        f.add(new Pattern(lb, PatternType.STRIPE_MIDDLE));
        f.add(new Pattern(bl, PatternType.STRIPE_RIGHT));
        f.add(new Pattern(lb, PatternType.STRIPE_LEFT));
        f.add(new Pattern(lb, PatternType.STRIPE_TOP));
        s.add(new Pattern(lb, PatternType.TRIANGLE_TOP));
        s.add(new Pattern(lb, PatternType.TRIANGLE_BOTTOM));
        s.add(new Pattern(lb, PatternType.SQUARE_TOP_RIGHT));
        s.add(new Pattern(lb, PatternType.SQUARE_BOTTOM_LEFT));
        s.add(new Pattern(bl, PatternType.RHOMBUS_MIDDLE));
        s.add(new Pattern(lb, PatternType.STRIPE_DOWNRIGHT));
        bm.setPatterns(m);
        bt.setPatterns(t);
        bw.setPatterns(w);
        bth.setPatterns(t);
        bf.setPatterns(f);
        bs.setPatterns(s);
        bsu.setPatterns(s);
        bm.setDisplayName(ChatColor.GREEN + "Monday");
        bt.setDisplayName(ChatColor.GREEN + "Tuesday");
        bw.setDisplayName(ChatColor.GREEN + "Wednesday");
        bth.setDisplayName(ChatColor.GREEN + "Thursday");
        bf.setDisplayName(ChatColor.GREEN + "Friday");
        bs.setDisplayName(ChatColor.GREEN + "Saturday");
        bsu.setDisplayName(ChatColor.GREEN + "Sunday");
        monday.setItemMeta(bm);
        tuesday.setItemMeta(bt);
        wednesday.setItemMeta(bw);
        thursday.setItemMeta(bth);
        friday.setItemMeta(bf);
        saturday.setItemMeta(bs);
        sunday.setItemMeta(bsu);

        buttons.addAll(Arrays.asList(new MenuButton(1, monday), new MenuButton(2, tuesday), new MenuButton(3, wednesday),
                new MenuButton(4, thursday), new MenuButton(5, friday), new MenuButton(6, saturday),
                new MenuButton(7, sunday)));

        List<String> times = new ArrayList<>();
        shows.stream().filter(show -> !times.contains(show.getTime())).forEach(show -> times.add(show.getTime()));
        HashMap<String, Integer> timeMap = new HashMap<>();
        int i = 9;
        for (String st : times) {
            if (i >= 54) {
                break;
            }
            buttons.add(new MenuButton(i, ItemUtil.create(Material.WATCH, ChatColor.GREEN + st + " EST")));
            timeMap.put(st, i / 9);
            i += 9;
        }

        for (ScheduledShow show : shows) {
            ShowType type = show.getType();
            int place = getShowPos(show.getDay(), show.getTime(), timeMap);
            if (type.getType().equals(Material.BANNER)) {
                ItemStack banner = new ItemStack(Material.BANNER);
                BannerMeta bmeta = (BannerMeta) banner.getItemMeta();
                bmeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_SMALL));
                bmeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_SMALL));
                bmeta.addPattern(new Pattern(DyeColor.BLUE, PatternType.SQUARE_TOP_RIGHT));
                bmeta.addPattern(new Pattern(DyeColor.BLUE, PatternType.SQUARE_TOP_RIGHT));
                bmeta.addPattern(new Pattern(DyeColor.BLUE, PatternType.SQUARE_TOP_RIGHT));
                bmeta.setDisplayName(type.getName());
                banner.setItemMeta(bmeta);
                buttons.add(new MenuButton(place, banner));
                continue;
            }
            buttons.add(new MenuButton(place, ItemUtil.create(type.getType(), 1, type.getData(), type.getName(),
                    new ArrayList<>())));
        }
    }

    /**
     * Retrieves the current list of scheduled shows.
     *
     * <p>The list contains {@code ScheduledShow} objects, each representing
     * a show with its associated type, day, raw time (for sorting purposes),
     * and formatted time string.</p>
     *
     * <p>The returned list is a copy of the internal {@code shows} collection,
     * ensuring that modifications to the returned list do not affect the internal state
     * of the {@code ScheduleManager} instance.</p>
     *
     * <p><strong>Details:</strong></p>
     * <ul>
     *   <li>The list includes all shows currently scheduled in the system.</li>
     *   <li>Each {@code ScheduledShow} object provides structured information about
     *       the show, including:
     *       <ul>
     *         <li>{@code ShowType}: The type of the show.</li>
     *         <li>{@code ShowDay}: The day the show is scheduled.</li>
     *         <li>{@code rawTime}: Raw time used for sorting purposes.</li>
     *         <li>{@code time}: A formatted string representing the show time.</li>
     *       </ul>
     *   </li>
     *   <li>Note that updates to the internal {@code shows} collection
     *       are made asynchronously through the {@code updateShows} method.</li>
     * </ul>
     *
     * @return A {@code List} of {@code ScheduledShow} objects representing
     *         the current schedule of shows.
     */
    public List<ScheduledShow> getShows() {
        return new ArrayList<>(shows);
    }

    /**
     * Converts a 24-hour formatted time integer into a 12-hour clock format string with AM/PM notation.
     * <p>
     * The method accepts an integer representing time in the 24-hour format (e.g., 1300 for 1:00 PM)
     * and returns the formatted time in the 12-hour format with appropriate AM or PM designation.
     * </p>
     * <ul>
     *   <li>Time values from 0000 to 1159 are considered AM.</li>
     *   <li>Time values from 1200 to 2359 are considered PM.</li>
     *   <li>If the time value is exactly midnight (0000), it returns "12:00 AM".</li>
     * </ul>
     *
     * @param time An integer representing the time in 24-hour format (e.g., 0 for midnight or 1400 for 2:00 PM).
     *             The value should be a valid military time between 0000 and 2359.
     * @return A string representing the time in 12-hour format with AM/PM (e.g., "1:00 PM").
     */
    public String getTime(int time) {
        String am;
        if (time >= 1200) {
            am = "PM";
        } else {
            am = "AM";
        }
        if (time == 0) {
            return "12:00 AM";
        }
        return ((time / 100) % 12) + ":00 " + am;
    }

    /**
     * Computes the position index for a show based on the show day, time, and a predefined time mapping.
     * <p>
     * This method is used to calculate the slot index for where a show should appear
     * in a user interface or data structure, taking into account the sequential order of days
     * and time slots.
     * </p>
     *
     * @param day      The day of the week represented as a {@link ShowDay} enum. Each day has its ordinal value
     *                 used in the calculation of the position index.
     * @param time     A {@link String} representing the specific time of the show. The time must match
     *                 a key provided in the {@code timeMap}.
     * @param timeMap  A {@link HashMap} mapping time strings to integer values. Each time maps to a unique
     *                 incrementing integer used for calculating the positional index.
     *
     * @return An integer representing the computed position index for the show. The index is determined
     *         based on the provided {@code day}, {@code time}, and corresponding values from the {@code timeMap}.
     */
    private int getShowPos(ShowDay day, String time, HashMap<String, Integer> timeMap) {
        int i = 10;
        i += day.ordinal();
        i += (9 * (timeMap.get(time) - 1));
        return i;
    }

    /**
     * Opens a schedule editing interface for the specified player.
     * <p>
     * This method creates a menu with interactive elements, allowing the player
     * to modify the schedule's details. Non-editable items, such as certain materials
     * (e.g., WATCH, ARROW, and specific BANNER items), are excluded from modification. The
     * resulting menu is displayed with buttons that represent editable slots, and clicking
     * on an item initiates further editing processes.
     * </p>
     *
     * <p><strong>Functionality:</strong></p>
     * <ul>
     *   <li>Filters the buttons to exclude non-editable items based on their material type.</li>
     *   <li>Replaces editable buttons with customized interaction logic.</li>
     *   <li>Opens a menu titled "Edit Timetable" for the player to make modifications.</li>
     * </ul>
     *
     * @param player The player for whom the schedule editing interface is being opened.
     *               This parameter serves as the recipient of the menu and the
     *               associated edits.
     */
    public void editSchedule(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>(this.buttons);
        for (int i = 0; i < buttons.size(); i++) {
            MenuButton b = buttons.get(i);
            ItemStack item = b.getItemStack();
            if (item == null || item.getType() == null || item.getType().equals(Material.WATCH) ||
                    item.getType().equals(Material.ARROW) ||
                    (item.getType().equals(Material.BANNER) && item.getItemMeta().getDisplayName().startsWith(ChatColor.GREEN.toString())))
                continue;
            buttons.set(i, new MenuButton(b.getSlot(), getEditItem(b.getItemStack().clone()), ImmutableMap.of(ClickType.LEFT, p -> editShow(player, b.getSlot()))));
        }

        new Menu(54, ChatColor.BLUE + "Edit Timetable", player, buttons).open();
    }

    /**
     * Provides functionality to edit a show scheduled at a specific slot within the timetable.
     * <p>
     * This method identifies the show associated with the provided <code>slot</code> and constructs
     * an interactive menu to allow updates to the show's type. The menu displays available options,
     * allowing the user to make their selection and update the timetable accordingly.
     * </p>
     *
     * <p><strong>Features:</strong></p>
     * <ul>
     *   <li>Identifies the show corresponding to the provided slot in the schedule.</li>
     *   <li>Builds a menu interface for editing the selected show's type.</li>
     *   <li>Applies changes to the internal list of scheduled shows upon the player's confirmation.</li>
     *   <li>Updates UI elements and saves all modifications asynchronously to the database.</li>
     * </ul>
     *
     * <p><strong>Behavior:</strong></p>
     * <ul>
     *   <li>If no show exists at the given slot, the operation is canceled and no changes are made.</li>
     *   <li>The updated show list is stored and applied immediately, ensuring consistent behavior across all sessions.</li>
     * </ul>
     *
     * @param player The player attempting to edit the show via the user interface.
     * @param slot   The inventory slot corresponding to the show to be edited.
     */
    private void editShow(CPlayer player, int slot) {
        List<String> times = new ArrayList<>();
        shows.stream().filter(show -> !times.contains(show.getTime())).forEach(show -> times.add(show.getTime()));
        HashMap<String, Integer> timeMap = new HashMap<>();
        int i = 9;
        for (String st : times) {
            if (i >= 54) break;
            timeMap.put(st, i / 9);
            i += 9;
        }

        ScheduledShow show = null;
        int replace = 0;
        for (ScheduledShow s : getShows()) {
            if (getShowPos(s.getDay(), s.getTime(), timeMap) == slot) {
                show = s;
                break;
            }
            replace++;
        }
        if (show == null) return;

        i = 0;
        List<MenuButton> buttons = new ArrayList<>();
        int finalReplace = replace;
        ScheduledShow finalShow = show;
        for (ShowType type : ShowType.values()) {
            buttons.add(new MenuButton(i++, ItemUtil.create(type.getType(), 1, type.getData(), type.getName(),
                    Arrays.asList(ChatColor.GREEN + "Update the timetable entry", ChatColor.GREEN + "for " + ChatColor.AQUA + finalShow.getDay().name() + " at " + finalShow.getTime())),
                    ImmutableMap.of(ClickType.LEFT, p -> {
                        p.sendMessage(ChatColor.GREEN + "Set the show at " + ChatColor.AQUA + finalShow.getDay().name() + " at " + finalShow.getTime() + ChatColor.GREEN + " (" + finalReplace + ") to " + type.getName());
                        shows.set(finalReplace, new ScheduledShow(type, finalShow.getDay(), finalShow.getRawTime(), finalShow.getTime()));
                        updateButtons();
                        Core.runTaskAsynchronously(ParkManager.getInstance(), this::saveToDatabase);
                        editSchedule(p);
                    }))
            );
        }

        new Menu(54, ChatColor.BLUE + "Edit Show", player, buttons).open();
    }

    /**
     * Persists the current list of scheduled shows to a database.
     *
     * <p>This method retrieves all scheduled shows from the internal {@code shows} collection,
     * converts each show into a {@code Document} representation suitable for database storage,
     * and updates the corresponding collection in the database.</p>
     *
     * <p><strong>Process:</strong></p>
     * <ul>
     *   <li>Iterate through the list of scheduled shows obtained via {@code getShows()}.</li>
     *   <li>For each show, create a database document containing the following fields:
     *       <ul>
     *         <li><strong>day:</strong> The name of the day of the week (e.g., "monday"), derived from {@code show.getDay()}.</li>
     *         <li><strong>time:</strong> The raw time representation of the show, retrieved using {@code show.getRawTime()}.</li>
     *         <li><strong>show:</strong> The database-compatible name of the show type, obtained using {@code show.getType().getDBName()}.</li>
     *       </ul>
     *   </li>
     *   <li>Add each document to a temporary list representing the new state of the schedule in the database.</li>
     *   <li>Invoke {@code Core.getMongoHandler().updateScheduledShows()} to update the database with the new data.</li>
     * </ul>
     *
     * <p><strong>Database Operation:</strong></p>
     * <ul>
     *   <li>The {@code Core.getMongoHandler().updateScheduledShows(List<Document>)} method is responsible for
     *   performing the actual database update using the list of newly created documents.</li>
     *   <li>Existing data in the database's scheduled shows collection will be replaced with the new list, ensuring
     *   consistency between the in-memory representation and the stored data.</li>
     * </ul>
     *
     * <p><strong>Notes:</strong></p>
     * <ul>
     *   <li>This method does not handle or return confirmation of the database update; it is assumed that
     *   the {@code updateScheduledShows} method performs the appropriate database actions.</li>
     *   <li>Any changes to the collection of scheduled shows are not persisted until this method is called.</li>
     * </ul>
     */
    private void saveToDatabase() {
        List<Document> list = new ArrayList<>();
        for (ScheduledShow show : getShows()) {
            list.add(new Document("day", show.getDay().name().toLowerCase())
                    .append("time", show.getRawTime())
                    .append("show", show.getType().getDBName()));
        }
        Core.getMongoHandler().updateScheduledShows(list);
    }

    /**
     * Modifies the given {@link ItemStack} to include a specific lore message indicating
     * that it can be left-clicked to change a show.
     *
     * <p>The method retrieves and updates the {@link ItemMeta} of the provided {@link ItemStack},
     * setting a single lore line with green-colored text. The updated {@link ItemStack} is then returned.</p>
     *
     * @param i The {@link ItemStack} to be modified with a custom lore message.
     * @return The modified {@link ItemStack} with the updated lore message.
     */
    private ItemStack getEditItem(ItemStack i) {
        ItemMeta meta = i.getItemMeta();
        meta.setLore(Collections.singletonList(ChatColor.GREEN + "Left-Click to change this show"));
        i.setItemMeta(meta);
        return i;
    }

    /**
     * The {@code ScheduledShow} class represents a scheduled show with its type, day, and time details.
     *
     * <p>This class encapsulates information about a specific show including:</p>
     * <ul>
     *   <li>The type of show, represented by {@code ShowType}.</li>
     *   <li>The day on which the show is scheduled, represented by {@code ShowDay}.</li>
     *   <li>The raw time value of the show, represented by an integer.</li>
     *   <li>The human-readable formatted time of the show, represented by a {@code String}.</li>
     * </ul>
     *
     * <p>This class is immutable, and all its fields are declared as final. The {@code Getter}
     * annotation generates getter methods for all fields, allowing access to their values.</p>
     *
     * <p>The class is also annotated with {@code AllArgsConstructor}, ensuring that it can
     * be instantiated with values for all fields.</p>
     */
    @Getter
    @AllArgsConstructor
    public static class ScheduledShow {
        /**
         * Represents the type of a scheduled show.
         *
         * <p>This variable is an instance of the {@code ShowType} enum, which defines the various
         * categories and names of shows that can be scheduled. These types include regular shows,
         * projection shows, stage shows, parades, and special events, among others.</p>
         *
         * <p>The {@code type} field is:
         * <ul>
         *   <li>Immutable: Its value cannot be changed after initialization.</li>
         *   <li>Final: Ensures that the reference always points to the same {@code ShowType} instance.</li>
         * </ul>
         *
         * <p>This field allows determining the specific category or nature of the scheduled show.</p>
         */
        private final ShowType type;

        /**
         * Represents the day of the week when the show is scheduled to occur.
         *
         * <p>The {@code day} field is an enumeration of type {@link ShowDay}. It specifies
         * the exact day of the week a show is set to take place. This ensures that only
         * valid days (e.g., MONDAY through SUNDAY) can be assigned to the field.</p>
         *
         * <p>Key characteristics:</p>
         * <ul>
         *   <li>The value is immutable due to the {@code final} modifier.</li>
         *   <li>Strictly uses the {@link ShowDay} enum for type safety and predefined values.</li>
         *   <li>Links directly to scheduling logic by associating a show with a specific day.</li>
         * </ul>
         */
        private final ShowDay day;

        /**
         * Represents the raw, unformatted time of the scheduled show.
         *
         * <p>This field holds the time value in an integer format, typically
         * as a 24-hour representation (e.g., 1330 for 1:30 PM).
         * It is used internally to facilitate comparison, sorting, or manipulation
         * of time data within the context of the scheduled show.</p>
         *
         * <ul>
         *   <li>The {@code rawTime} is immutable and initialized when the {@code ScheduledShow} object is created.</li>
         *   <li>It serves as a core element in defining the timing of a show without being formatted for display.</li>
         * </ul>
         *
         * <p>To retrieve a user-friendly representation of the time, the {@code time} field can be used
         * after conversion from the {@code rawTime} value.</p>
         */
        private final int rawTime;

        /**
         * Represents the formatted, human-readable time of the scheduled show.
         *
         * <p>This field stores the time in a {@code String} format, intended to
         * offer a user-friendly representation of the show's scheduled time.</p>
         *
         * <ul>
         *   <li>It serves as a more intuitive visualization of the time compared to
         *       {@code rawTime}, which holds the time as an integer.</li>
         *   <li>This value is immutable and can only be set at the time of initialization.</li>
         * </ul>
         *
         * <p>Examples of possible values include formatted strings like "10:30 AM"
         * or "07:15 PM". The exact format may depend on the implementation or
         * context in which the {@code ScheduledShow} is used.</p>
         */
        private final String time;
    }

    /**
     * <p>The <code>ShowDay</code> enum represents the days of the week.</p>
     * <p>It provides a fixed set of constants for each day of the week:</p>
     * <ul>
     *     <li>MONDAY</li>
     *     <li>TUESDAY</li>
     *     <li>WEDNESDAY</li>
     *     <li>THURSDAY</li>
     *     <li>FRIDAY</li>
     *     <li>SATURDAY</li>
     *     <li>SUNDAY</li>
     * </ul>
     *
     * <p>The enum also offers a utility method to parse a <code>String</code>
     * representation of a day into its corresponding <code>ShowDay</code> constant.</p>
     */
    public enum ShowDay {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

        /**
         * Converts a string representation of the day of the week into its corresponding {@code ShowDay} enum constant.
         * <p>
         * The input string is case-insensitive and should match the name of one of the days of the week.
         * If the string does not match any valid day, the method returns {@code null}.
         * </p>
         *
         * @param s the string representation of the day of the week.
         *          <ul>
         *              <li>Acceptable inputs (case-insensitive): "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday".</li>
         *          </ul>
         * @return the corresponding {@code ShowDay} enum constant for the given string,
         *         or {@code null} if the input does not match any valid day.
         */
        public static ShowDay fromString(String s) {
            switch (s.toLowerCase()) {
                case "monday":
                    return MONDAY;
                case "tuesday":
                    return TUESDAY;
                case "wednesday":
                    return WEDNESDAY;
                case "thursday":
                    return THURSDAY;
                case "friday":
                    return FRIDAY;
                case "saturday":
                    return SATURDAY;
                case "sunday":
                    return SUNDAY;
            }
            return null;
        }
    }
}
