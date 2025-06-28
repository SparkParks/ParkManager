package network.palace.parkmanager.queues;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.utils.FileUtil;
import network.palace.ridemanager.RideManager;
import network.palace.ridemanager.handlers.ride.Ride;
import network.palace.ridemanager.handlers.ride.RideType;
import network.palace.ridemanager.handlers.ride.file.FileRide;
import network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide;
import network.palace.ridemanager.handlers.ride.flat.CarouselRide;
import network.palace.ridemanager.handlers.ride.flat.TeacupsRide;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The {@code PluginQueue} class extends the {@code Queue} class. It represents a queue
 * in a theme park system with customized attributes and behaviors specific to the
 * plugins utilized for rides.
 *
 * <p>This class provides functionality to manage park-specific rides, handle different
 * queue types, and integrate custom ride configurations. Each queue can be associated
 * with a specific ride type, a currency system, and achievements.</p>
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Manage rides including {@code FileRide}, {@code TeacupsRide}, {@code CarouselRide},
 *   and {@code AerialCarouselRide}.</li>
 *   <li>Supports multiple ride configurations such as type, radius, angles, and other
 *   attributes depending on the ride type.</li>
 *   <li>Uses a countdown system for dispatching players to rides.</li>
 * </ul>
 *
 * <p><strong>Key Functionality:</strong></p>
 * <ul>
 *   <li>Overrides the {@code tick} method for custom ride scheduling and countdowns.</li>
 *   <li>Overrides the {@code handleSpawn} method to initiate specific behaviors based
 *   on the queue type when players are added to the queue.</li>
 *   <li>Determines and retrieves the appropriate queue type by overriding
 *   the {@code getQueueType} method.</li>
 * </ul>
 *
 * <p><strong>Behavior:</strong></p>
 * <ul>
 *   <li>If the queue is associated with flat rides (e.g., TeacupsRide or CarouselRide),
 *   the {@code flat} attribute is set to true.</li>
 *   <li>Duration of player dispatch is managed by a countdown sequence managed by
 *   {@code stationCountdown}.</li>
 *   <li>Achievements, honor, and currency rewards are integrated into the ride system.</li>
 * </ul>
 *
 * <p><strong>Components:</strong></p>
 * <ul>
 *   <li>{@code currencyType}: Represents the type of currency used within the ride queue.</li>
 *   <li>{@code ride}: Configured ride associated with the queue, which can be of different
 *   types such as file-based or carousel-based.</li>
 *   <li>{@code atStation}: List of players currently present at the station, waiting for dispatch.</li>
 *   <li>{@code stationCountdown}: Countdown timer for dispatching players to the ride.</li>
 * </ul>
 */
@Getter
public class PluginQueue extends Queue {
    /**
     * <p>Represents the type of currency required or utilized within the queue system.</p>
     *
     * <p>Used in the context of queue management to determine the type of currency
     * involved in transactions or requirements for queue access.</p>
     *
     *
     * <p>This field is immutable and must be defined during the initialization
     * of an instance of the containing class.</p>
     */
    private final CurrencyType currencyType;

    /**
     * Represents the amount of currency required or associated with a particular operation
     * or transaction within the system.
     *
     * <p>This value is a fixed integer that defines the quantity of the specific
     * {@code currencyType} required. It is immutable and set during the initialization
     * of the {@code PluginQueue} instance.</p>
     *
     * <ul>
     *   <li>Used for determining costs or rewards in relation to the queue.</li>
     *   <li>Ensures consistency in managing in-game currency amounts.</li>
     *   <li>Interacts with the {@code currencyType} field to specify the type of currency it represents.</li>
     * </ul>
     */
    private final int currencyAmount;

    /**
     * <p>The <code>honorAmount</code> variable represents the amount of in-game "honor"
     * required or awarded for a specific queueing system associated with a plugin in the
     * park manager system.</p>
     *
     * <p>This variable is declared as <code>final</code> to ensure that once initialized,
     * its value cannot be modified. This reflects a consistent value for honor-related
     * operations within the context of a queue instance.</p>
     *
     * <p>Usage may involve:</p>
     * <ul>
     *   <li>Defining the cost or reward in honor for participating in certain park activities.</li>
     *   <li>Contributing to decision-making processes involving player progression or achievements.</li>
     *   <li>Integration with other systems where honor metrics are needed.</li>
     * </ul>
     *
     * <p>The value is typically determined when initializing a queue instance and is used
     * throughout its lifecycle.</p>
     */
    private final int honorAmount;

    /**
     * <p>Represents the unique identifier for an achievement in the system.</p>
     *
     * <p>This identifier is associated with a specific achievement that can
     * be unlocked or tracked within the context of the queue's functionality.</p>
     *
     * <ul>
     *   <li>It is immutable and final once assigned during initialization.</li>
     *   <li>The value is used to look up or reference achievement-related data.</li>
     * </ul>
     */
    private final int achievementId;

    /**
     * Indicates whether the queue operates in a flat ride mode.
     * <p>
     * This flag determines specific behavior for the ride managed by the queue. When set to
     * <code>true</code>, the queue is configured for flat rides, which may have distinct
     * operational requirements or constraints compared to other ride types.
     */
    private boolean flat = false;

    /**
     * <p>Represents the ride associated with the current queue. This variable likely links
     * to a specific ride in the park system, enabling interaction, status tracking, or
     * operational management specific to the queue and its relationship to the ride.</p>
     *
     * <p>It may be used to:</p>
     * <ul>
     *   <li>Map a queue to its related ride entity.</li>
     *   <li>Access ride-specific configurations or data relevant to the queue.</li>
     *   <li>Support queue processing actions and ride operations.</li>
     * </ul>
     *
     * <p>This field is part of the internal mechanics within the <code>PluginQueue</code>
     * class to ensure that proper ride-queue associations exist as part of the park's system.</p>
     */
    private Ride ride;

    /**
     * <p>
     * Represents a list of players currently at the station within the queue system.
     * </p>
     *
     * <ul>
     * <li>Each player in this list is represented by a {@link CPlayer} object.</li>
     * <li>The station is typically a designated area where players await their turn
     * for further processing or actions in the queue.</li>
     * <li>This list is dynamically updated as players arrive at or leave the station.</li>
     * </ul>
     *
     * <p>
     * Managed as part of the {@link PluginQueue} system to ensure proper flow and
     * state tracking of players at the station.
     * </p>
     */
    private List<CPlayer> atStation = new ArrayList<>();

    /**
     * Represents the countdown timer for the station to indicate the time delay or waiting
     * period before the next operation or action is triggered at the station.
     *
     * <p>This variable is initialized to -1, typically representing a default or uninitialized
     * state. It is modified during the operational flow of the queue system to manage
     * station-specific timing behavior.</p>
     *
     * <p>This field is primarily used within the context of the queue system, facilitating
     * the control and synchronization of events or interactions related to the station
     * mechanism.</p>
     */
    private int stationCountdown = -1;

    /**
     * Constructs a new {@code PluginQueue} instance with the specified configuration and ride details.
     *
     * <p>This constructor initializes the {@code PluginQueue} object and sets up its associated {@code Ride}
     * based on the type of ride defined in the provided configuration.</p>
     *
     * @param id              The unique identifier for the queue.
     * @param park            The {@code ParkType} associated with the queue.
     * @param uuid            The unique {@code UUID} for this queue instance.
     * @param name            The name of the queue and ride.
     * @param warp            The warp location identifier for the queue.
     * @param groupSize       The maximum size of a group allowed in the queue.
     * @param delay           The delay, in ticks, between ride operations or group movements.
     * @param open            Whether the queue and ride are currently open for use.
     * @param station         The {@code Location} object representing the station's starting point in the queue.
     * @param signs           A {@code List} of {@code QueueSign} objects that are used to display information about the queue.
     * @param exit            The {@code Location} object representing the exit point for the ride.
     * @param currencyType    The {@code CurrencyType} used as payment for entering the queue.
     * @param currencyAmount  The amount of currency required to enter the queue.
     * @param honorAmount     The honor points required for the ride, if applicable.
     * @param achievementId   The achievement ID associated with completing the ride, if any.
     * @param rideConfig      A {@code JsonObject} containing the configuration details for the ride, including
     *                        ride type and any additional parameters needed for specific ride implementations.
     */
    public PluginQueue(String id, ParkType park, UUID uuid, String name, String warp, int groupSize, int delay, boolean open, Location station, List<QueueSign> signs,
                       Location exit, CurrencyType currencyType, int currencyAmount, int honorAmount, int achievementId, JsonObject rideConfig) {
        super(id, park, uuid, name, warp, groupSize, delay, open, station, signs);
        this.currencyType = currencyType;
        this.currencyAmount = currencyAmount;
        this.honorAmount = honorAmount;
        this.achievementId = achievementId;

        RideType type = RideType.fromString(rideConfig.get("rideType").getAsString());
        switch (type) {
            case FILE:
                ride = new FileRide(name, name, groupSize, delay, exit, rideConfig.get("file").getAsString(),
                        currencyType, currencyAmount, honorAmount, achievementId);
                break;
            case TEACUPS:
                ride = new TeacupsRide(name, name, delay, exit, FileUtil.getLocation(rideConfig.getAsJsonObject("center")),
                        currencyType, currencyAmount, honorAmount, achievementId);
                flat = true;
                break;
            case CAROUSEL:
                ride = new CarouselRide(name, name, delay, exit, FileUtil.getLocation(rideConfig.getAsJsonObject("center")),
                        currencyType, currencyAmount, honorAmount, achievementId);
                flat = true;
                break;
            case AERIAL_CAROUSEL:
                ride = new AerialCarouselRide(name, name, delay, exit, FileUtil.getLocation(rideConfig.getAsJsonObject("center")),
                        currencyType, currencyAmount, honorAmount, achievementId, rideConfig.get("aerialRadius").getAsDouble(),
                        rideConfig.get("supportRadius").getAsDouble(), rideConfig.get("small").getAsBoolean(),
                        rideConfig.get("supportAngle").getAsDouble(), rideConfig.get("height").getAsDouble(), rideConfig.get("movein").getAsDouble());
        }
        if (ride != null) RideManager.getMovementUtil().addRide(ride);
    }

    /**
     * Manages the countdown and dispatching process for the ride at the station.
     * <p>
     * This method performs the following tasks:
     * <ul>
     *     <li>If the countdown (`stationCountdown`) is negative, it exits the method, indicating no ride activity.</li>
     *     <li>If the countdown is greater than 0, it updates the players at the station with a countdown message displayed on their action bars.</li>
     *     <li>If the countdown reaches 0, it dispatches the ride, notifies the players, clears the station list, and resets the countdown.</li>
     * </ul>
     *
     * @param currentTime the current time in milliseconds, typically provided by a higher-level timer, to ensure consistency in timing operations.
     */
    @Override
    public void tick(long currentTime) {
        super.tick(currentTime);
        if (stationCountdown < 0) return;
        if (stationCountdown > 0) {
            //Countdown to dispatch
            atStation.forEach(p -> p.getActionBar().show(ChatColor.GREEN + "Ride starting in " + stationCountdown + " second" + TextUtil.pluralize(stationCountdown)));
        } else {
            //Dispatch
            atStation.forEach(p -> p.getActionBar().show(ChatColor.GREEN + "Ride starting now..."));
            ride.start(atStation);
            atStation.clear();
        }
        stationCountdown--;
    }

    /**
     * Handles the spawning process for a list of players in the queue system.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Resets the station countdown to 10.</li>
     *   <li>Clears the current list of players at the station and populates it with the provided list of players.</li>
     *   <li>Checks the {@link QueueType} of the ride. If it is of type {@code FILE}, calls the spawn method
     *       on the {@link FileRide} with a delay of 10,000 milliseconds.</li>
     * </ul>
     *
     * @param players a {@link List} of {@link CPlayer} objects representing the players to be spawned.
     *                This list is used to update the station with the current players and manage their interaction with the ride.
     */
    @Override
    protected void handleSpawn(List<CPlayer> players) {
        stationCountdown = 10;
        atStation.clear();
        atStation.addAll(players);
        if (getQueueType().equals(QueueType.FILE)) {
            ((FileRide) ride).spawn(10000);
        }
    }

    /**
     * Retrieves the queue type associated with the current ride.
     *
     * <p>This method determines the type of queue based on the specific ride instance
     * and returns the corresponding {@link QueueType} enumeration. The method evaluates
     * the ride instance to identify its type (e.g., a carousel, file-based ride, teacups,
     * or aerial carousel) and maps it to a predefined {@link QueueType}.</p>
     *
     * <p>Possible return values include:</p>
     * <ul>
     *   <li>{@link QueueType#CAROUSEL} - Returned if the ride instance is a {@code CarouselRide}.</li>
     *   <li>{@link QueueType#FILE} - Returned if the ride instance is a {@code FileRide}.</li>
     *   <li>{@link QueueType#TEACUPS} - Returned if the ride instance is a {@code TeacupsRide}.</li>
     *   <li>{@link QueueType#AERIAL_CAROUSEL} - Returned if the ride instance is an {@code AerialCarouselRide}.</li>
     *   <li>{@code null} - Returned if no matching ride type is found.</li>
     * </ul>
     *
     * @return the {@link QueueType} corresponding to the current ride instance, or {@code null}
     *         if the ride type does not match any predefined types.
     */
    @Override
    public QueueType getQueueType() {
        if (ride instanceof CarouselRide) {
            return QueueType.CAROUSEL;
        } else if (ride instanceof FileRide) {
            return QueueType.FILE;
        } else if (ride instanceof TeacupsRide) {
            return QueueType.TEACUPS;
        } else if (ride instanceof AerialCarouselRide) {
            return QueueType.AERIAL_CAROUSEL;
        }
        return null;
    }
}
