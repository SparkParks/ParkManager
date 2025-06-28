package network.palace.parkmanager.queues;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.utils.TimeUtil;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;

/**
 * This class represents a Queue system for managing players in a park-related environment.
 * <p>
 * The {@code Queue} handles the organization, scheduling, and processing of players who join
 * a standby or FastPass queue. It facilitates various queue-related behaviors such as adding players,
 * removing players, handling priorities, calculating wait times, and updating visual representations
 * like signs associated with the queue.
 * </p>
 * <p>
 * The queue also provides functionality to manage groups of players, handle timed events for group
 * processing, and incorporate extensible behavior for specific queue types through abstract methods.
 * </p>
 * <p>Key capabilities include:</p>
 * <ul>
 *     <li>Joining and leaving queue operations for both regular and FastPass players.</li>
 *     <li>Scheduling and bringing in groups of players according to timing logic.</li>
 *     <li>Providing wait time estimates based on the player's position in the queue.</li>
 *     <li>Support for marking the queue as open or closed and pausing/resuming queue operations.</li>
 *     <li>Updating associated display signs with queue status or details.</li>
 *     <li>Extensibility for defining additional behavior in specialized queue types.</li>
 * </ul>
 *
 * <p>To implement additional queue functionality, classes can extend this abstract {@code Queue} class
 * and override key methods such as {@code handleSpawn(List<CPlayer> players)}.
 * </p>
 */
@Getter
public abstract class Queue {
    /**
     * <p>Represents a unique identifier for the {@code Queue} instance.</p>
     *
     * <p>This field is used to uniquely distinguish a queue from other queues in the system.
     * The identifier is stored as a {@code String} and can be set or modified as needed.
     * This could be useful for naming, referencing, or logging purposes.</p>
     *
     * <p><b>Access Modifiers:</b></p>
     * <ul>
     *   <li>{@code protected}: Accessible within the same package or subclasses.</li>
     * </ul>
     *
     * <p><b>Annotations:</b></p>
     * <ul>
     *   <li>{@code @Setter}: Indicates Lombok will generate a setter method for this field, allowing modifications.</li>
     * </ul>
     */
    @Setter protected String id;

    /**
     * Represents the type of park this queue is associated with.
     * <p>
     * The park type defines the context or environment for the queue system, enabling
     * the association of specific settings, behavior, or configurations related to this queue.
     * </p>
     * <p>
     * This variable is used to determine the context of operations within the queue
     * system, ensuring alignment with park-specific rules or features.
     * </p>
     *
     * <p>Potential use cases include:</p>
     * <ul>
     *   <li>Customizing queue behavior based on park requirements.</li>
     *   <li>Differentiating queues operating in different types of parks.</li>
     *   <li>Providing park-specific data or functionality.</li>
     * </ul>
     */
    private ParkType park;

    /**
     * A unique identifier for the queue instance.
     * <p>
     * This variable is used to distinguish this specific queue from others
     * by assigning it a globally unique identifier.
     * </p>
     * <p>
     * The UUID is generated and provided during the creation of the queue
     * and remains immutable throughout the queue's lifecycle.
     * </p>
     * <p>
     * Key use cases of this variable within the containing class include:
     * <ul>
     *     <li>Identifying players when interacting with the queue.</li>
     *     <li>Tracking player positions and wait times within the queue.</li>
     *     <li>Ensuring uniqueness when managing multiple queues.</li>
     * </ul>
     * </p>
     */
    private final UUID uuid;

    /**
     * The name of the queue.
     * <p>
     * This field represents the display name or identifier for the queue.
     * It can be used for distinguishing among different queues in the system.
     * </p>
     *
     * <p>
     * Examples of its use might include:
     * <ul>
     *   <li>Showing the name of the queue on signage or user interfaces.</li>
     *   <li>Referencing the queue in external systems or configuration.</li>
     * </ul>
     * </p>
     */
    @Setter protected String name;

    /**
     * Represents the warp location associated with the queue.
     * <p>
     * The warp is typically used to specify the destination
     * where players will be teleported when they progress through the queue.
     * <ul>
     *   <li>It may refer to predefined locations within a park.</li>
     *   <li>Used during group management to bring players to their destination.</li>
     * </ul>
     * <p>
     * Accessible and modifiable within the class hierarchy due to its
     * {@code protected} access and the usage of the Lombok {@code @Setter} annotation.
     */
    @Setter protected String warp;

    /**
     * <p>Represents the number of players that can be processed or brought into the queue at a time.</p>
     *
     * <p>This variable is used to determine the maximum size of a single group in the queue workflow.
     * It affects operations such as scheduling groups, estimating wait times, and determining how many
     * players are brought to the station during the queue processing.</p>
     *
     * <ul>
     *     <li><b>Usage in Queue Scheduling:</b> Controls how many players are moved from the queue to the active
     *         group based on availability and queue length.</li>
     *     <li><b>Impact on Queue Players:</b> Any remaining players in the queue after a group is brought in may
     *         be notified of their progress by having moved up by this number of spaces.</li>
     *     <li><b>Minimum and Maximum Groups:</b> If the queue contains fewer players than this variable, then all players
     *         in the queue may still be brought in as a group.</li>
     * </ul>
     *
     * <p>Groups larger than the defined <code>groupSize</code> will not be allowed, ensuring consistent
     * and manageable workflow through the queue system.</p>
     */
    @Setter protected int groupSize;

    /**
     * Represents the duration in seconds between the processing of groups in the queue.
     * <p>
     * The {@code delay} variable determines the time interval used for scheduling and
     * allowing groups to enter from the queue. When this value is updated, it affects the
     * timing of how groups are processed, ensuring a controlled and consistent flow of players
     * within the system.
     * </p>
     *
     * <ul>
     *   <li>Used in scheduling groups during the regular processing of the queue.</li>
     *   <li>Impacts the timing of tasks such as {@code bringInNextGroup()}.</li>
     *   <li>Adjustable for reconfiguring the pacing of queue operations as needed.</li>
     * </ul>
     *
     * <p>
     * Setting an appropriate delay can help balance player throughput and ensure smooth transitions
     * in attractions or activities associated with the queue.
     * </p>
     */
    @Setter protected int delay;

    /**
     * Represents the location where a group of players in the queue is brought in.
     * <p>
     * This location is typically the destination where players are teleported when
     * it's their turn to proceed in the queue.
     * </p>
     * <p>
     * It is used in methods like {@link #bringInNextGroup()} to determine where queued
     * players are moved during the group handling process.
     * </p>
     * <ul>
     *     <li>Type: {@link Location}</li>
     *     <li>Access Modifier: <code>protected</code></li>
     *     <li>Setter: Available via Lombok's <code>@Setter</code></li>
     * </ul>
     */
    @Setter protected Location station;

    /**
     * <p>Represents the state of the queue being open or closed.</p>
     *
     * <p>This variable determines whether players are allowed to join the queue.
     * When set to <code>true</code>, the queue is considered open and players can enter.
     * When set to <code>false</code>, the queue is closed, and no new players are allowed.</p>
     *
     * <p>This variable can be modified using the {@link #setOpen(boolean)} method to
     * dynamically control access to the queue.</p>
     */
    protected boolean open;

    /**
     * A boolean flag that indicates whether the queue is currently paused or active.
     * <p>
     * If this variable is set to <code>true</code>, the queue is paused, meaning operations
     * such as adding or moving players in the queue might be temporarily suspended.
     * If set to <code>false</code>, the queue operates normally.
     * <p>
     * <b>Key behaviors:</b>
     * <ul>
     * <li>When paused, players cannot advance in the queue or join a new group.</li>
     * <li>When unpaused, the queue resumes normal operations such as group scheduling and advancement.</li>
     * </ul>
     */
    protected boolean paused = false;

    /**
     * A LinkedList that stores the UUIDs of players currently in the regular queue.
     *
     * <p>
     * This list contains the main queue members and manages the order of players
     * waiting to progress through the queue system.
     * </p>
     *
     * <p>
     * The queue operates in a FIFO (First-In-First-Out) manner where players added
     * first are processed before those added later.
     * </p>
     *
     * <ul>
     * <li>Players are added to this list when they join the main queue.</li>
     * <li>Players are removed when they are processed, leave the queue, or are
     * otherwise removed explicitly.</li>
     * <li>Interaction with this list directly occurs via queue-specific methods
     * such as {@code joinQueue}, {@code leaveQueue}, or {@code bringInNextGroup}.
     * </li>
     * </ul>
     *
     * <p>
     * This field directly corresponds to the main standby queue, separate from the
     * fast-pass queue mechanism.
     * </p>
     */
    private LinkedList<UUID> queueMembers = new LinkedList<>();

    /**
     * Represents a list of members with a FastPass in the queue.
     *
     * <p>The <code>fastPassMembers</code> variable keeps track of players
     * who have opted for the FastPass benefit, allowing them to bypass
     * the regular queue under certain conditions. This list is maintained
     * separately from the rest of the queue to give priority handling.
     *
     * <p>Key characteristics:
     * <ul>
     *   <li>Players in this list are identified using their unique <code>UUID</code>.</li>
     *   <li>The list is implemented using a <code>LinkedList</code> to facilitate
     *       efficient additions and removals during queue management operations.</li>
     *   <li>FastPass functionality is managed to ensure that eligible players
     *       benefit from reduced wait times while adhering to queue rules.</li>
     * </ul>
     */
    private LinkedList<UUID> fastPassMembers = new LinkedList<>();

    /**
     * A list of {@link QueueSign} objects associated with this queue.
     * <p>
     * Each {@link QueueSign} represents a physical sign that updates in real-time
     * to reflect the current state of the queue, such as its name, the number of
     * players, and the estimated wait time. Signs can represent either the main
     * queue or the FastPass queue.
     * </p>
     * <p>
     * This field is used to maintain and manage all associated signs for the queue,
     * ensuring they are updated to display accurate and relevant information.
     * </p>
     *
     * <ul>
     *     <li>Signs are linked to specific physical locations in the world.</li>
     *     <li>Each sign provides information such as the queue name, estimated wait time, and player count.</li>
     *     <li>FastPass signs are visually distinct and prioritize FastPass information.</li>
     * </ul>
     *
     * <p>
     * Modifiable via {@link #addSign(QueueSign)} and {@link #removeSign(QueueSign)}. Signs are also updated
     * dynamically via {@link #updateSigns()}.
     * </p>
     */
    private List<QueueSign> signs;

    /**
     * <p>
     * The variable <code>nextGroup</code> tracks the scheduled time for the next group
     * to be brought into the queue. This value is represented as a timestamp
     * (in milliseconds) relative to the system time.
     * </p>
     *
     * <p>
     * The scheduling is typically handled in methods like {@link #joinQueue(CPlayer)}
     * or {@link #tick(long)}. When new players join and the queue is empty, this variable
     * is set with a timestamp offset (e.g., 10 seconds from the current time) to allow
     * others to join within a brief window. It ensures that groups have adequate time for
     * formation if multiple players are joining collectively.
     * </p>
     *
     * <p>
     * When the queue is processed or the scheduled group is brought in using
     * {@link #bringInNextGroup()}, this value is reset or updated accordingly.
     * If the value is <code>0</code>, it indicates there is currently no scheduled
     * group.
     * </p>
     */
    private long nextGroup = 0;

    /**
     * Boolean flag indicating whether the FastPass queue should be brought in before
     * the normal queue.
     *
     * <p>This variable is used internally within the {@code Queue} class to determine
     * the prioritization of the FastPass queue when handling group scheduling. If set
     * to {@code true}, the FastPass queue will be processed before the regular queue
     * during group placement operations.</p>
     *
     * <p>Key points to consider:</p>
     * <ul>
     *   <li>When {@code bringInFastPass} is {@code true}, players in the FastPass queue
     *       are prioritized over players in the standard queue.</li>
     *   <li>This variable is typically modified during the queue operations based on
     *       system logic and cannot be publicly accessed directly.</li>
     *   <li>It plays a vital role in maintaining an enhanced experience for players
     *       who opt for FastPass services.</li>
     * </ul>
     *
     * <p>Default value: {@code false}</p>
     */
    private boolean bringInFastPass = false;

    /**
     * Constructs a new Queue for managing player lineups within a theme park system.
     * This class represents a queue that manages both standard and FastPass entries,
     * handles queue operations, and manages associated signs and player groupings.
     *
     * @param id         the unique identifier for this queue
     * @param park       the park type this queue is associated with
     * @param uuid       the globally unique identifier (UUID) for this queue
     * @param name       the name of the queue
     * @param warp       the warp point associated with the queue
     * @param groupSize  the number of players brought in per group
     * @param delay      the delay in seconds between bringing in groups
     * @param open       whether the queue is currently open for new players
     * @param station    the location where players are teleported upon joining the queue
     * @param signs      the list of {@link QueueSign} objects associated with this queue
     */
    public Queue(String id, ParkType park, UUID uuid, String name, String warp, int groupSize, int delay, boolean open, Location station, List<QueueSign> signs) {
        this.id = id;
        this.park = park;
        this.uuid = uuid;
        this.name = name;
        this.warp = warp;
        this.groupSize = groupSize;
        this.delay = delay;
        this.station = station;
        this.open = open;
        this.signs = signs;
    }

    /**
     * Adds the specified player to the current queue if it is open. Notifies the player
     * about their position in the queue and adjusts the queue's behavior based on the
     * current state (e.g., an empty queue).
     *
     * <p>If the queue is closed, the player will be notified and not added to the queue.</p>
     * <p>If the player successfully joins, they will be removed from all other queues.</p>
     * <p>If the player joins an empty queue, a delay is set to allow other players to join.</p>
     *
     * @param player the player attempting to join the queue
     * @return {@code true} if the player successfully joins the queue, {@code false}
     *         if the queue is closed or the player cannot be added
     */
    public boolean joinQueue(CPlayer player) {
        if (!open) {
            player.sendMessage(ChatColor.RED + "This queue is currently closed, check back soon!");
            return false;
        }
        ParkManager.getQueueManager().leaveAllQueues(player);
        queueMembers.add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You've joined the queue for " + name + ChatColor.GREEN +
                " at position #" + getPosition(player.getUniqueId()));
        if (queueMembers.size() == 1 && nextGroup == 0) {
            player.sendMessage(ChatColor.GREEN + "Since you joined an empty queue, you'll have a 10 second wait in case other players join.");
            nextGroup = TimeUtil.getCurrentSecondInMillis() + 10000;
        }
        return true;
    }

    /**
     * Allows a player to join the FastPass queue if certain conditions are met, such as the queue being open,
     * the standby queue not being empty, and the player having valid FastPass entries remaining. When the player
     * successfully joins the FastPass queue, a confirmation message is sent to them indicating their position
     * in the queue.
     *
     * <p>The method will return false, along with an appropriate message to the player, if any of the following
     * conditions occur:
     * <ul>
     *   <li>The queue is currently closed.</li>
     *   <li>The standby queue is empty.</li>
     *   <li>The player does not have a valid FastPass entry in their registry, or their available count is zero.</li>
     * </ul>
     *
     * @param player the player attempting to join the FastPass queue
     * @return {@code true} if the player successfully joins the FastPass queue, {@code false} otherwise
     */
    public boolean joinFastPassQueue(CPlayer player) {
        if (!open) {
            player.sendMessage(ChatColor.RED + "This queue is currently closed, check back soon!");
            return false;
        }
        if (queueMembers.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You can't join the FastPass queue when the standby queue is empty!");
            return false;
        }
        if (!player.getRegistry().hasEntry("fastPassCount")) {
            player.sendMessage(ChatColor.RED + "There was a problem redeeming your FastPass!");
            return false;
        }
        if (((int) player.getRegistry().getEntry("fastPassCount")) <= 0) {
            player.sendMessage(ChatColor.RED + "You do not have a FastPass to redeem, sorry!");
            return false;
        }
        fastPassMembers.add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You've joined the queue for " + name + ChatColor.GREEN +
                " at position #" + getPosition(player.getUniqueId()));
        return true;
    }

    /**
     * Removes a player from the queue. This method evaluates whether the player is present in the
     * main queue or the FastPass queue and removes them accordingly. A message is sent to the player
     * indicating whether they have been removed forcibly or voluntarily.
     *
     * <p>If the player is not found in either queue, no action is taken.</p>
     *
     * <p>The method also checks if the removal is performed forcefully or voluntarily, modifying the
     * messaging accordingly.</p>
     *
     * @param player the {@code CPlayer} instance representing the player to be removed from the queue
     * @param force  a {@code boolean} indicating if the removal is forceful
     */
    public void leaveQueue(CPlayer player, boolean force) {
        boolean mainQueue = queueMembers.remove(player.getUniqueId());
        boolean fpQueue = fastPassMembers.remove(player.getUniqueId());
        if (mainQueue || fpQueue) {
            if (force) {
                player.sendMessage(ChatColor.GREEN + "You've been removed from the " + (fpQueue ? "FastPass" : "") + " queue for " + name);
            } else {
                player.sendMessage(ChatColor.GREEN + "You've left the" + (fpQueue ? "FastPass" : "") + " queue for " + name);
            }
        }
    }

    /**
     * Removes all members from both the standard queue and the FastPass queue.
     *
     * <p>This method iterates over all queued players in both the standard
     * queue (`queueMembers`) and the FastPass queue (`fastPassMembers`)
     * and removes each player. If a player is successfully found, it invokes
     * the {@link #leaveQueue(CPlayer, boolean)} method to handle the removal process
     * and notify the player.
     *
     * <p>The removal process ensures that:
     * <ul>
     *   <li>Players are removed from their respective queues (`queueMembers` or `fastPassMembers`).</li>
     *   <li>Each player is notified about their removal if applicable.</li>
     * </ul>
     *
     * <p>This method is useful in scenarios where the queue needs to be
     * entirely reset, such as during operational shutdowns or queue reinitializations.
     *
     * @see #leaveQueue(CPlayer, boolean)
     */
    public void emptyQueue() {
        for (UUID uuid : new LinkedList<>(queueMembers)) {
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) leaveQueue(player, true);
        }
        for (UUID uuid : new LinkedList<>(fastPassMembers)) {
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) leaveQueue(player, true);
        }
    }

    /**
     * Retrieves the type of queue represented by this instance.
     *
     * <p>This method is intended to be implemented by subclasses to return the
     * specific {@link QueueType} associated with the queue, such as a standard or
     * FastPass queue type.</p>
     *
     * @return a {@link QueueType} representing the type of this queue
     */
    public abstract QueueType getQueueType();

    /**
     * Updates the open status of the queue and notifies all associated members.
     * <p>
     * When the queue is reopened or closed, all players in the queue (including
     * both standard and FastPass members) are sent a notification message.
     *
     * <ul>
     *   <li>If the queue is opened, all members are informed that the queue has reopened.</li>
     *   <li>If the queue is closed, members are informed that the queue is closed, but they may remain
     *   in their current position. However, they are warned that if they leave, they cannot rejoin
     *   until the queue reopens.</li>
     * </ul>
     *
     * @param b a boolean indicating whether to set the queue as open ({@code true})
     *          or closed ({@code false}).
     */
    public void setOpen(boolean b) {
        this.open = b;
        String msg;
        if (b) {
            msg = ChatColor.GREEN + "The queue for " + name + ChatColor.GREEN + " has just reopened!";
        } else {
            msg = ChatColor.GREEN + "The queue for " + name + ChatColor.GREEN + " has just been " + ChatColor.RED + "closed. "
                    + ChatColor.GREEN + "You can keep your place in line, but if you leave you can't rejoin until the queue reopens!";
        }
        queueMembers.forEach(id -> {
            CPlayer player = Core.getPlayerManager().getPlayer(id);
            if (player == null) return;
            player.sendMessage(msg);
        });
        fastPassMembers.forEach(id -> {
            CPlayer player = Core.getPlayerManager().getPlayer(id);
            if (player == null) return;
            player.sendMessage(msg);
        });
    }

    /**
     * Manages the scheduling and processing of players in a queue system for a game or event.
     * Updates the status of the queue, manages the timing for the arrival of groups, and provides
     * visual feedback to queued players and queue-related signs.
     *
     * <p>The method orchestrates the following functionalities:</p>
     * <ul>
     *   <li>Schedules or updates the timing for the next group based on the current state and conditions.</li>
     *   <li>Handles countdown updates for queued players, displaying their position and estimated wait time.</li>
     *   <li>Updates queue signs to reflect the current state of the queue, including the number of players
     *       and estimated wait times.</li>
     * </ul>
     *
     * @param currentTime The current system time in milliseconds, used for scheduling and timing calculations.
     */
    public void tick(long currentTime) {
        if (!open || (paused && nextGroup != 0)) {
            nextGroup += 1000;
        }
        if (nextGroup != 0) {
            //A group is scheduled
            if (nextGroup <= currentTime && open && !paused) {
                //Time to bring in the next group
                if (!queueMembers.isEmpty() || !fastPassMembers.isEmpty()) {
                    //There's more players after the previous group, so schedule another group
                    nextGroup = currentTime + (delay * 1000);
                    bringInNextGroup();
                } else {
                    //No players are in the queue, so don't schedule anything
                    nextGroup = 0;
                }
            } else {
                //Next group is scheduled but hasn't happened yet, so we're counting down
                queueMembers.forEach(uuid -> {
                    CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                    if (player != null)
                        player.getActionBar().show(ChatColor.GREEN + "You're #" + getPosition(uuid) + " in queue for " + name + ChatColor.YELLOW + " | " + "Wait: " + getWaitFor(uuid, currentTime));
                });
                fastPassMembers.forEach(uuid -> {
                    CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                    if (player != null)
                        player.getActionBar().show(ChatColor.GREEN + "You're #" + getPosition(uuid) + " in queue for " + name + ChatColor.YELLOW + " | " + "Wait: " + getWaitFor(uuid, currentTime));
                });
            }
        }
        signs.forEach(queueSign -> {
            queueSign.setAmount(queueSign.isFastPassSign() ? fastPassMembers.size() : queueMembers.size());
            queueSign.setWait(getWaitFor(null, currentTime));
            queueSign.updateSign();
        });
    }

    /**
     * Retrieves a wait time or status related to the specified {@code UUID} at the current time.
     * <p>
     * This method serves as a simple proxy for {@link #getWaitFor(UUID, long)} by passing the
     * current system time in milliseconds obtained via {@link TimeUtil#getCurrentSecondInMillis()}.
     *
     * @param uuid the UUID of the player whose wait time or status is being requested
     * @return a {@code String} representing the wait time or relevant status for the specified {@code UUID}
     */
    public String getWaitFor(UUID uuid) {
        return getWaitFor(uuid, TimeUtil.getCurrentSecondInMillis());
    }

    /**
     * Calculates and returns a string representing the wait time for a player
     * identified by the given UUID. The wait time is determined based on their
     * position in the queue and the current state of the system.
     *
     * <p>If the system is closed, this method returns "Closed" in a formatted
     * string indicating that no waiting is applicable. If the player is not
     * currently in the queue, the time is calculated as if they were appended
     * to the end of the queue.</p>
     *
     * @param uuid        The unique identifier of the player to determine
     *                    their wait time in the queue.
     * @param currentTime The current system time in milliseconds, used as the
     *                    reference to calculate the wait time.
     * @return A formatted string representing the player's wait time. The
     *         returned value will indicate one of the following states:
     *         <ul>
     *             <li>Closed: If the system is not open.</li>
     *             <li>No Wait: If there is no wait time for the player, or
     *                 the player's group is ready.</li>
     *             <li>A human-readable time difference indicating the
     *                 wait time.</li>
     *         </ul>
     *         Additionally, "Paused" is appended in yellow text
     *         if the queue is currently paused.
     */
    public String getWaitFor(UUID uuid, long currentTime) {
        if (!open) {
            return ChatColor.RED + "Closed";
        }
        if (nextGroup == 0) {
            return "No Wait" + (paused ? (ChatColor.YELLOW + " Paused") : "");
        }
        LinkedList<UUID> fullQueue = getFullQueue();
        int position = fullQueue.indexOf(uuid);
        if (position == -1) {
            //Not in queue, so get wait time as if they were after the last player in queue.
            position = fullQueue.size();
        }
        //The group the player is in, starting at 0
        int group = (int) Math.floor(((float) position) / groupSize);

        //Get time until player's group is up, plus time until current group is up
        int seconds = (int) Math.floor((delay * group) + ((nextGroup - currentTime) / 1000f));

        Calendar from = new GregorianCalendar();
        from.setTimeInMillis(currentTime);

        Calendar to = new GregorianCalendar();
        to.setTimeInMillis(currentTime + (seconds * 1000));

        String msg = TimeUtil.formatDateDiff(from, to);
        return (msg.equalsIgnoreCase("now") ? "No Wait" : msg) + (paused ? (ChatColor.YELLOW + " Paused") : "");
    }

    /**
     * Handles the logic to bring in the next group of players from a queue and teleports them to a predefined station,
     * processing charges for FastPass members if applicable.
     * <p>
     * This method alternates between prioritizing FastPass members and regular queue members, controlled by the
     * {@code bringInFastPass} flag. When invoked, it performs the following operations:
     * </p>
     * <ul>
     *     <li>Retrieves a defined number of players from the queue, based on {@code groupSize}.</li>
     *     <li>Removes processed players from the queue and updates their status (e.g., FastPass charges for eligible players).</li>
     *     <li>Teleports players in the group to the configured station location.</li>
     *     <li>Informs players still in the queue of their updated position.</li>
     * </ul>
     * <p>
     * If the queue is empty or no eligible players are found, the method returns early.
     * </p>
     *
     * <b>Steps performed:</b>
     * <ol>
     *     <li>Determine the next group to process, alternating queue member priority.</li>
     *     <li>Iterate through the queue to retrieve eligible players up to the group size.</li>
     *     <li>Remove players from the queue and, if applicable, deduct FastPass charges.</li>
     *     <li>Teleport players to the ride station.</li>
     *     <li>Notify remaining queue members of their new queue position.</li>
     * </ol>
     *
     * <b>Note:</b> FastPass players are charged using the {@code ParkManager.getQueueManager().chargeFastPass()} method.
     * If a player lacks a valid FastPass, they are notified and teleported back to a fallback warp location.
     *
     * <p>
     * Each player within the group is also handled through the {@code handleSpawn()} method, which processes additional
     * actions or logic as needed for the newly teleported group.
     * </p>
     */
    protected void bringInNextGroup() {
        List<CPlayer> players = new ArrayList<>();
        LinkedList<UUID> fullQueue = getFullQueue();
        bringInFastPass = !bringInFastPass;
        for (int i = 0; i < groupSize; i++) {
            UUID uuid;
            try {
                uuid = fullQueue.pop();
            } catch (NoSuchElementException e) {
                break;
            }
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) players.add(player);
        }
        if (players.isEmpty()) return;
        players.forEach(p -> {
            queueMembers.remove(p.getUniqueId());
            if (fastPassMembers.remove(p.getUniqueId())) {
                if (!ParkManager.getQueueManager().chargeFastPass(p)) {
                    // Player doesn't have a FastPass
                    p.sendMessage(ChatColor.RED + "You don't have a FastPass for this ride!");
                    Warp w = ParkWarp.getWarpUtil().findWarp(getWarp());
                    if (w != null) p.teleport(w);
                    return;
                }
                p.sendMessage(ChatColor.GREEN + "You have been charged " + ChatColor.AQUA + "1 " + ChatColor.GREEN + "FastPass!");
            }
            p.teleport(station);
        });
        handleSpawn(players);
        if (!fullQueue.isEmpty()) {
            ListIterator<UUID> iterator = fullQueue.listIterator(0);
            int pos = 1;
            while (iterator.hasNext()) {
                CPlayer player = Core.getPlayerManager().getPlayer(iterator.next());
                player.sendMessage(ChatColor.GREEN + "You've moved up " + players.size() + " places in queue for " + name
                        + ChatColor.GREEN + " to position #" + pos++);
            }
        }
    }

    /**
     * Handles the spawning logic for a list of players. This method must be implemented
     * by subclasses to define the specific spawn behavior for players.
     *
     * <p>
     * Implementations of this method should handle all necessary operations
     * related to spawning the provided players in the game.
     * </p>
     *
     * @param players A {@link List} of {@link CPlayer} objects representing the players
     *        to be spawned. The list should not be null and may contain one or more players.
     */
    protected abstract void handleSpawn(List<CPlayer> players);

    /**
     * Retrieves the position of a specified UUID in the queue.
     * <p>
     * This method checks the queue for the provided UUID and returns its position
     * (1-based index). If the UUID is not found in the queue, it returns -1.
     * </p>
     *
     * @param uuid the unique identifier to look for in the queue.
     *             It cannot be null.
     * @return the 1-based position of the UUID in the queue if present;
     *         otherwise, returns -1 if the UUID is not in the queue.
     */
    public int getPosition(UUID uuid) {
        int index = getFullQueue().indexOf(uuid);
        if (index == -1) return -1;
        return index + 1;
    }

    /**
     * Adds a new sign to the queue system and saves the updated list of signs to a file.
     *
     * <p>This method adds the provided {@link QueueSign} instance to the internal list of signs
     * and ensures that the current state of the list is persisted by invoking the {@code saveToFile} method
     * of the {@link ParkManager}'s queue manager.
     *
     * @param sign the {@link QueueSign} object to be added to the list of signs.
     */
    public void addSign(QueueSign sign) {
        signs.add(sign);
        ParkManager.getQueueManager().saveToFile();
    }

    /**
     * Removes the specified sign from the system and updates the queue manager by saving the changes to a file.
     *
     * <p>This method ensures that the provided {@code QueueSign} object is removed from the internal list of signs
     * and that the state of the queue manager is persisted after the removal.</p>
     *
     * @param sign the {@code QueueSign} object to be removed from the system
     */
    public void removeSign(QueueSign sign) {
        signs.remove(sign);
        ParkManager.getQueueManager().saveToFile();
    }

    /**
     * Removes a sign at the specified location if it exists.
     *
     * <p>This method iterates through the list of signs and checks if any sign matches
     * the provided location. If a match is found, the sign is removed.</p>
     *
     * @param location the location of the sign to be removed; must not be null
     */
    public void removeSign(Location location) {
        QueueSign toRemove = null;
        for (QueueSign sign : getSigns()) {
            if (sign.getLocation().equals(location)) {
                toRemove = sign;
                break;
            }
        }
        if (toRemove != null) removeSign(toRemove);
    }

    /**
     * Retrieves the current size of the queue.
     *
     * <p>This method returns the number of elements currently present in the queue.</p>
     *
     * @return an integer representing the number of elements in the queue.
     */
    public int getQueueSize() {
        return queueMembers.size();
    }

    /**
     * Retrieves the current size of the FastPass queue.
     *
     * <p>This method returns the total number of members currently in the FastPass queue,
     * which is typically used for giving priority access to certain members.</p>
     *
     * @return the number of members in the FastPass queue as an integer.
     */
    public int getFastPassQueueSize() {
        return fastPassMembers.size();
    }

    /**
     * Retrieves the full queue of members as a combined, ordered list of both standard queue members
     * and fast-pass members. The ordering alternates between adding members from the standard queue
     * and fast-pass members, if available.
     * <p>
     * The method processes the two groups of members, ensuring that fast-pass members are interleaved
     * with standard queue members based on a flag that determines the current processing order.
     * The process continues until all members from both groups are added to the full queue.
     *
     * @return a {@link LinkedList} of {@link UUID} objects representing the combined and ordered
     * full queue of both standard and fast-pass members.
     */
    public LinkedList<UUID> getFullQueue() {
        if (fastPassMembers.isEmpty()) return new LinkedList<>(queueMembers);

        LinkedList<UUID> list = new LinkedList<>();
        int standby = queueMembers.size(), fp = fastPassMembers.size();
        boolean bringInFP = bringInFastPass;

        while (standby > 0 || fp > 0) {
            if (bringInFP && fp > 0) {
                fp = processList(fp, fastPassMembers, list);
            } else if (standby > 0) {
                standby = processList(standby, queueMembers, list);
            }
            bringInFP = !bringInFP;
        }

        return list;
    }

    /**
     * Processes a given list by transferring elements from one list to another.
     * If the specified size is less than or equal to the groupSize, all elements are moved from the {@code fromList}
     * to the {@code toList}. Otherwise, a certain number of elements, determined by the {@code groupSize}, are moved.
     * <p>
     * The size of the remaining elements in the {@code fromList} after the transfer is updated and returned.
     * </p>
     *
     * @param size the number of elements to process from the {@code fromList}.
     *             If this value is less than or equal to the groupSize, all elements are transferred.
     * @param fromList the list from which elements are taken.
     * @param toList the list to which the elements are added.
     * @return the updated remaining size of elements to be processed after the transfer.
     */
    private int processList(int size, LinkedList<UUID> fromList, LinkedList<UUID> toList) {
        if (size <= groupSize) {
            toList.addAll(fromList);
            size = 0;
        } else {
            for (int i = 0; i < groupSize; i++) {
                toList.add(fromList.get(i));
            }
            size -= groupSize;
        }
        return size;
    }

    /**
     * Updates all signs in the collection.
     *
     * <p>This method iterates over the collection of signs and triggers the
     * {@code updateSign} method for each {@link QueueSign} instance. This ensures
     * that all signs reflect the latest state or display the most up-to-date
     * information.
     *
     * <p>Usage of this method is essential when the state associated with the
     * signs changes and requires immediate synchronization across all
     * {@code QueueSign} objects in the collection.
     */
    public void updateSigns() {
        signs.forEach(QueueSign::updateSign);
    }

    /**
     * Checks whether a specified player is currently present in either the regular queue or the fast pass queue.
     *
     * <p>This method verifies the player's presence by comparing their unique identifier against
     * the records in the queueMembers and fastPassMembers collections.</p>
     *
     * @param player the {@code CPlayer} instance to check for in the queue.
     *               Must not be {@code null}.
     * @return {@code true} if the player is present in either the regular queue or fast pass queue;
     *         {@code false} otherwise.
     */
    public boolean isInQueue(CPlayer player) {
        return queueMembers.contains(player.getUniqueId()) || fastPassMembers.contains(player.getUniqueId());
    }

    /**
     * Updates the paused state of the object.
     * <p>
     * This method sets the internal <code>paused</code> state, which can
     * be used to control the execution or behavior of the object depending
     * on whether it is in a paused state or not.
     * </p>
     *
     * @param paused <code>true</code> to set the object to a paused state,
     *               <code>false</code> to resume or unpause it.
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
