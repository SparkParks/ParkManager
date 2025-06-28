package network.palace.parkmanager.queues;

import com.google.gson.JsonObject;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * The {@code QueueBuilder} class is responsible for managing and constructing queue processes
 * for a specific park type. It extends the {@code Queue} class to provide specialized
 * functionality for handling queues in the context of the park management system.
 *
 * <p>The class provides methods to get the type of queue, handle spawn events for players,
 * progress the queue steps for a specific player, and manage internal plugin-based queue logic.
 * It incorporates the following key features:</p>
 *
 * <ul>
 *   <li>Initialization of the queue builder with a specific park type.</li>
 *   <li>Overriding the default queue type retrieval logic.</li>
 *   <li>Handling spawn-related logic for a collection of players.</li>
 *   <li>Progressing individual players through their next queue step.</li>
 *   <li>Managing internal and plugin-specific queue operations.</li>
 * </ul>
 *
 * <p>This class is designed to streamline the process of managing queue flow within
 * the park system for complex scenarios and customized operations.</p>
 */
public class QueueBuilder extends Queue {
    /**
     * Represents the type of queue being constructed by the {@code QueueBuilder}.
     *
     * <p>This field holds an instance of {@link QueueType}, which defines the specific
     * type of queue or ride system. It allows {@code QueueBuilder} to configure and
     * manage the queue according to its unique characteristics.</p>
     *
     * <ul>
     *   <li>When initialized, this field is {@code null} and must be explicitly set
     *       before the {@code QueueBuilder} can function correctly.</li>
     *   <li>Possible values for this field are defined in the {@link QueueType} enum, which
     *       includes types such as {@code BLOCK}, {@code CAROUSEL}, {@code TEACUPS}, etc.</li>
     * </ul>
     *
     * <p>Type-specific behaviors, descriptions, and other configurations for the queue
     * can be accessed through methods within {@link QueueType}.</p>
     *
     * <p>This field plays a crucial role in determining the functionality and behavior
     * of the queues or rides managed by the {@code QueueBuilder} class.</p>
     */
    private QueueType type = null;

    /**
     * A <code>HashMap</code> that stores configuration or characteristic fields for
     * defining the behavior of a queue type in the context of the queue management system.
     * <p>
     * The keys in this map represent the names of the fields or attributes, while the values
     * represent the associated data or settings for those fields.
     * <p>
     * This member is private to ensure proper encapsulation and is utilized internally by
     * the <code>QueueBuilder</code> class to customize and define specific properties that affect
     * the operation or behavior of a queue.
     *
     * <ul>
     * <li>Key: <code>String</code> - Represents the field name or identifier.</li>
     * <li>Value: <code>Object</code> - Represents the associated value or configuration for the field.</li>
     * </ul>
     * <p>
     * This map plays an integral role in defining how the corresponding queue type operates
     * or interacts with other components of the queue management system.
     */
    private HashMap<String, Object> queueTypeFields = new HashMap<>();

    /**
     * Constructs a new {@code QueueBuilder} instance for managing queue creation and configuration within a
     * specific theme park or resort system.
     *
     * <p>This constructor initializes a {@code QueueBuilder} with default values inherited from the base
     * {@link Queue} class, associating the queue with a specific {@link ParkType}.</p>
     *
     * @param park the {@link ParkType} that this queue will be associated with. It defines the specific park
     *             or resort where the queue is implemented, such as Magic Kingdom, Epcot, or Animal Kingdom.
     */
    public QueueBuilder(ParkType park) {
        super(null, park, null, null, null, 0, 0, false, null, new ArrayList<>());
    }

    /**
     * Retrieves the {@link QueueType} associated with this queue.
     *
     * <p>This method returns the type of queue or ride that is currently in use or configured.
     * The {@link QueueType} determines the behavior, characteristics, and design of the queue system.</p>
     *
     * <p>Possible {@link QueueType} values include:</p>
     * <ul>
     *   <li>{@link QueueType#BLOCK} - Spawns a redstone block at a specific location for queued players.</li>
     *   <li>{@link QueueType#CAROUSEL} - A carousel with rotating horses.</li>
     *   <li>{@link QueueType#TEACUPS} - Spinning teacups arranged on revolving plates.</li>
     *   <li>{@link QueueType#AERIAL_CAROUSEL} - A vertically moving carousel.</li>
     *   <li>{@link QueueType#FILE} - A ride vehicle following a pre-determined path.</li>
     * </ul>
     *
     * <p><strong>Note:</strong> This method may return {@code null} if no {@link QueueType} is specified or available
     * for the current queue instance.</p>
     *
     * @return the {@link QueueType} representing the type of queue or ride, or {@code null} if none is set.
     */
    @Override
    public QueueType getQueueType() {
        return null;
    }

    /**
     * Handles the logic for spawning players into the queue.
     *
     * <p>This method processes a list of players and performs the necessary actions
     * to handle their entry into the queue system.</p>
     *
     * @param players the list of {@code CPlayer} objects representing the players
     *                to be spawned into the queue.
     */
    @Override
    protected void handleSpawn(List<CPlayer> players) {
    }

    /**
     * Processes and advances the configuration steps for creating a queue in a theme park system.
     * This method handles different setup stages such as setting an ID, display name, warp point, group size,
     * delay, station location, queue type, and further queue type-specific configurations.
     *
     * <p>Execution flow is determined by the current state of the configuration fields (e.g., {@code id}, {@code name}, {@code warp}).
     * As each field is configured, the process steps to the next, prompting the player through chat messages.
     *
     * <p>Steps to configure a queue:
     * <ul>
     *   <li>Step 0: Set the queue ID.</li>
     *   <li>Step 1: Set the queue display name.</li>
     *   <li>Step 2: Set the warp point name.</li>
     *   <li>Step 3: Define the group size.</li>
     *   <li>Step 4: Set the delay time between groups.</li>
     *   <li>Step 5: Define the station location.</li>
     *   <li>Step 6: Set the queue type and handle specific configurations.</li>
     * </ul>
     *
     * @param player The player running the command. Provides context for sending messages and obtaining location input.
     * @param args   An array of arguments provided through the command. Supplies input for each configuration step,
     *               depending on the current active step. Input types include:
     *               <ul>
     *                   <li>Queue ID: a unique identifier for the queue.</li>
     *                   <li>Display name: a customizable string, supports color codes.</li>
     *                   <li>Warp point: the name of the teleportation warp location.</li>
     *                   <li>Group size: an integer between 1 and 100.</li>
     *                   <li>Delay: an integer defining the interval (minimum of 5 seconds).</li>
     *                   <li>Type: the specific type of the queue, such as "block", "carousel", "teacups", or "aerial_carousel".</li>
     *               </ul>
     */
    public void nextStep(CPlayer player, String[] args) {
        if (id == null) {
            //Step 0
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "/queue create [id]");
                return;
            }
            if (ParkManager.getQueueManager().getQueueById(args[0], getPark()) != null) {
                player.sendMessage(ChatColor.RED + "This id is already used by another queue! Try again: " + ChatColor.YELLOW + "/queue create [id]");
                player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "See current queue ids with: " + ChatColor.YELLOW + "/queue list");
                return;
            }
            this.id = args[0];
            player.sendMessage(ChatColor.GREEN + "Great! Now, let's give your queue a display name. Run " + ChatColor.YELLOW + "/queue create [name]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This name supports color codes! For example, '&aExample &dQueue' becomes '"
                    + ChatColor.GREEN + "Example " + ChatColor.LIGHT_PURPLE + "Queue" + ChatColor.DARK_AQUA + "'.");
            return;
        }
        if (name == null) {
            //Step 1
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "/queue create [name]");
                return;
            }
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                name.append(args[i]);
                if (i < (args.length - 1)) {
                    name.append(" ");
                }
            }
            this.name = ChatColor.translateAlternateColorCodes('&', name.toString());
            player.sendMessage(ChatColor.GREEN + "Next, let's give your queue a warp. Run " + ChatColor.YELLOW + "/queue create [warp]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "The warp doesn't have to exist right now, but if it doesn't players won't be teleported anywhere!");
            return;
        }
        if (warp == null) {
            //Step 2
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "/queue create [warp]");
                return;
            }
            this.warp = args[0];
            player.sendMessage(ChatColor.GREEN + "Queues bring in a set number of players per group, so let's define that group size. Run "
                    + ChatColor.YELLOW + "/queue create [groupSize]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This number must be at least 1, but no more than 100.");
            return;
        }
        if (groupSize == 0) {
            //Step 3
            if (args.length < 1 || !MiscUtil.checkIfInt(args[0])) {
                player.sendMessage(ChatColor.RED + "/queue create [groupSize]");
                return;
            }
            int groupSize = Integer.parseInt(args[0]);
            if (groupSize < 1 || groupSize > 100) {
                player.sendMessage(ChatColor.RED + "/queue create [groupSize]");
                player.sendMessage(ChatColor.RED + "groupSize must be at least 1, but no more than 100! You entered: " + groupSize);
                return;
            }
            this.groupSize = groupSize;
            player.sendMessage(ChatColor.GREEN + "We need to define the number of seconds after bringing in a group to wait before we bring the next group in. Run "
                    + ChatColor.YELLOW + "/queue create [delay in seconds]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Delays must be at least 5, but have no upper limit.");
            return;
        }
        if (delay == 0) {
            //Step 4
            if (args.length < 1 || !MiscUtil.checkIfInt(args[0])) {
                player.sendMessage(ChatColor.RED + "/queue create [delay]");
                return;
            }
            int delay = Integer.parseInt(args[0]);
            if (delay < 5) {
                player.sendMessage(ChatColor.RED + "/queue create [delay]");
                player.sendMessage(ChatColor.RED + "delay must be at least 5! You entered: " + delay);
                return;
            }
            this.delay = delay;
            player.sendMessage(ChatColor.GREEN + "Now, let's define where exactly players are brought in, known as the \"station\". Run "
                    + ChatColor.YELLOW + "/queue create");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This command doesn't have any parameters because it uses where you're standing when you run it!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When running this, make sure you're in the exact position you want players to be teleported to.");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Note: " + ChatColor.DARK_AQUA + "" + ChatColor.ITALIC
                    + "Don't forget about where you're looking! Players will be looking exactly where you are when they're teleported in.");
            return;
        }
        if (station == null) {
            //Step 5
            this.station = player.getLocation();
            player.sendMessage(ChatColor.GREEN + "Now we need to define what " + ChatColor.ITALIC + "type " + ChatColor.GREEN + "of queue you want to make. The choices are:");
            for (QueueType qt : QueueType.values()) {
                player.sendMessage(ChatColor.YELLOW + "'" + qt.name().toLowerCase() + "' - " + ChatColor.GREEN + qt.getDescription());
            }
            player.sendMessage(ChatColor.GREEN + "Run " + ChatColor.YELLOW + "/queue create [type]");
            return;
        }
        if (type == null) {
            //Step 6
            if (args.length < 1 || QueueType.fromString(args[0]) == null) {
                player.sendMessage(ChatColor.RED + "/queue create [type]");
                player.sendMessage(ChatColor.GREEN + "The options for type are:");
                for (QueueType qt : QueueType.values()) {
                    player.sendMessage(ChatColor.YELLOW + "'" + qt.name().toLowerCase() + "' - " + ChatColor.GREEN + qt.getDescription());
                }
                return;
            }
            QueueType type = QueueType.fromString(args[0]);
            if (type == null) {
                player.sendMessage(ChatColor.RED + "That isn't a valid queue type!");
                player.sendMessage(ChatColor.RED + "/queue create [type]");
                player.sendMessage(ChatColor.GREEN + "The options for type are:");
                for (QueueType qt : QueueType.values()) {
                    player.sendMessage(ChatColor.YELLOW + "'" + qt.name().toLowerCase() + "' - " + ChatColor.GREEN + qt.getDescription());
                }
                return;
            }
            this.type = type;
            JsonObject rideConfig = new JsonObject();
            switch (type) {
                case BLOCK:
                    player.sendMessage(ChatColor.GREEN + "Great! All that's left is to set where the redstone block is spawned in.");
                    player.sendMessage(ChatColor.GREEN + "Stand where the redstone block should be placed, then run " + ChatColor.YELLOW + "/queue create");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This command doesn't have any parameters because it uses where you're standing when you run it!");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When running this, make sure you're in the exact position you want a redstone block to be placed.");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This block is placed 1 second after players are teleported in, then removed 1 second later.");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When the block is removed, it is set to "
                            + ChatColor.AQUA + "AIR" + ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + ", so make sure that doesn't interfere with any builds!");
                    break;
                case CAROUSEL:
                    player.sendMessage(ChatColor.GREEN + "Okay, now let's finish configuring your Carousel. Stand exactly where the center of the carousel should be and run " + ChatColor.YELLOW + "/queue create");
                    rideConfig.addProperty("rideType", "CAROUSEL");
                    queueTypeFields.put("rideConfig", rideConfig);
                    break;
                case TEACUPS:
                    player.sendMessage(ChatColor.GREEN + "Okay, now let's finish configuring your Teacups ride. Stand exactly where the center of the platform should be and run " + ChatColor.YELLOW + "/queue create");
                    rideConfig.addProperty("rideType", "TEACUPS");
                    queueTypeFields.put("rideConfig", rideConfig);
                    break;
                case AERIAL_CAROUSEL:
                    player.sendMessage(ChatColor.GREEN + "Okay, now let's finish configuring your Aerial Carousel. Stand exactly where the center of the carousel should be and run " + ChatColor.YELLOW + "/queue create");
                    rideConfig.addProperty("rideType", "AERIAL_CAROUSEL");
                    queueTypeFields.put("rideConfig", rideConfig);
                    break;
                case FILE:
                    player.sendMessage(ChatColor.GREEN + "Okay, now let's finish configuring your File ride. Run " + ChatColor.YELLOW + "/queue create [file]");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "[file] is the name of the ride file being used without the file extension");
                    rideConfig.addProperty("rideType", "FILE");
                    queueTypeFields.put("rideConfig", rideConfig);
                    break;
            }
            return;
        }
        switch (type) {
            case BLOCK:
                if (!queueTypeFields.containsKey("blockLocation")) {
                    //Step 7
                    player.getRegistry().removeEntry("queueBuilder");
                    Location loc = player.getLocation();
                    queueTypeFields.put("blockLocation", new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                    finish(player);
                }
                break;
            case CAROUSEL:
            case TEACUPS: {
                JsonObject rideConfig = (JsonObject) queueTypeFields.get("rideConfig");
                if (!rideConfig.has("center")) {
                    Location loc = player.getLocation();
                    rideConfig.add("center", FileUtil.getJson(new Location(loc.getWorld(), 0.5 * (Math.round(loc.getX() / 0.5)), loc.getBlockY(), 0.5 * (Math.round(loc.getZ() / 0.5)), 0, 0)));
                    player.sendMessage(ChatColor.GREEN + "Alright, next we're going to configure all of the standard plugin-ride settings.");
                }
                handlePluginQueue(player, args);
                break;
            }
            case AERIAL_CAROUSEL: {
                JsonObject rideConfig = (JsonObject) queueTypeFields.get("rideConfig");
                if (!rideConfig.has("center")) {
                    Location loc = player.getLocation();
                    rideConfig.add("center", FileUtil.getJson(new Location(loc.getWorld(), 0.5 * (Math.round(loc.getX() / 0.5)), loc.getBlockY(), 0.5 * (Math.round(loc.getZ() / 0.5)), 0, 0)));
                    player.sendMessage(ChatColor.GREEN + "Next, let's get the vehicle position values set. Run " + ChatColor.YELLOW + "/queue create [aerialRadius] [supportRadius] [small] [supportAngle] [height] [movein]");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "aerialRadius (rec. 6.5) is how far from the center vehicles rotate about");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "supportRadius (rec. 4.5) is how far from the center of the ride the center of the support is (usually about half aerialRadius)");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "small determines whether 12 or 16 vehicles are used");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "supportAngle (rec. 45) is the angle supports are at when the vehicles are on ground level");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "height (rec. 6) is the distance above ground level vehicles max out at (can't move any higher)");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "movein (rec. 0.9) is a value used to determine how fast vehicles move towards the center while ascending");
                    break;
                }
                if (!rideConfig.has("aerialRadius")) {
                    if (args.length < 6) {
                        player.sendMessage(ChatColor.RED + "/queue create [aerialRadius] [supportRadius] [small] [supportAngle] [height] [movein]");
                        break;
                    }
                    double aerialRadius, supportRadius, supportAngle, height, movein;
                    boolean small;
                    try {
                        aerialRadius = Double.parseDouble(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[0] + " isn't a valid double for aerialRadius!");
                        break;
                    }
                    try {
                        supportRadius = Double.parseDouble(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[1] + " isn't a valid double for supportRadius!");
                        break;
                    }
                    try {
                        small = Boolean.parseBoolean(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[2] + " isn't a valid boolean for small!");
                        break;
                    }
                    try {
                        supportAngle = Double.parseDouble(args[3]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[3] + " isn't a valid double for supportAngle!");
                        break;
                    }
                    try {
                        height = Double.parseDouble(args[4]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[4] + " isn't a valid double for height!");
                        break;
                    }
                    try {
                        movein = Double.parseDouble(args[5]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[5] + " isn't a valid double for movein!");
                        break;
                    }
                    rideConfig.addProperty("aerialRadius", aerialRadius);
                    rideConfig.addProperty("supportRadius", supportRadius);
                    rideConfig.addProperty("small", small);
                    rideConfig.addProperty("supportAngle", supportAngle);
                    rideConfig.addProperty("height", height);
                    rideConfig.addProperty("movein", movein);
                    player.sendMessage(ChatColor.GREEN + "Alright, next we're going to configure all of the standard plugin-ride settings.");
                }
                handlePluginQueue(player, args);
                break;
            }
            case FILE: {
                JsonObject rideConfig = (JsonObject) queueTypeFields.get("rideConfig");
                if (!rideConfig.has("file")) {
                    if (args.length < 1) {
                        player.sendMessage(ChatColor.RED + "/queue create [file]");
                        break;
                    }
                    rideConfig.addProperty("file", args[0]);
                    player.sendMessage(ChatColor.GREEN + "Alright, next we're going to configure all of the standard plugin-ride settings.");
                }
                handlePluginQueue(player, args);
                break;
            }
        }
    }

    /**
     * Handles the configuration of a plugin-based queue system by guiding the player
     * through setup steps like defining an exit location, assigning rewards, and optionally
     * specifying an achievement.
     *
     * <p>This method is used iteratively to set up a queue, calling different steps based on
     * the current state of the {@code queueTypeFields} map. It requests input and validates
     * configurations for properties such as the queue's exit location and rewards.</p>
     *
     * @param player the {@link CPlayer} instance representing the player executing the command.
     *               Used for receiving input and sending messages.
     * @param args   an array of {@link String} arguments provided by the player when executing the command.
     *               These may include values for honor points, monetary rewards, and optional achievement IDs.
     */
    private void handlePluginQueue(CPlayer player, String[] args) {
        if (!queueTypeFields.containsKey("exit")) {
            player.sendMessage(ChatColor.YELLOW + "First, we need the 'exit' location. This is where players are brought to when they exit the ride.");
            queueTypeFields.put("exit", null);
            return;
        }
        if (queueTypeFields.get("exit") == null) {
            Location loc = player.getLocation();
            queueTypeFields.put("exit", new Location(loc.getWorld(), 0.5 * (Math.round(loc.getX() / 0.5)), loc.getBlockY(), 0.5 * (Math.round(loc.getZ() / 0.5)), loc.getYaw(), loc.getPitch()));
            player.sendMessage(ChatColor.YELLOW + "Lastly, let's define the rewards a player gets from riding this ride.");
            player.sendMessage(ChatColor.YELLOW + "Run /queue create [honor points] [amount of money] <achievement id>");
            player.sendMessage(ChatColor.DARK_AQUA + "Honor points and a money reward is required for all rides.");
            player.sendMessage(ChatColor.DARK_AQUA + "An achievement is optional. If you don't want an achievement to be awarded, leave the field blank or put '0' for the id.");
            return;
        }
        if (!queueTypeFields.containsKey("currencyAmount")) {
            int honor;
            try {
                honor = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + args[0] + " isn't a valid integer for honor points!");
                player.sendMessage(ChatColor.YELLOW + "Run /queue create [honor points] [amount of money] <achievement id>");
                return;
            }
            int money;
            try {
                money = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + args[1] + " isn't a valid integer for money!");
                player.sendMessage(ChatColor.YELLOW + "Run /queue create [honor points] [amount of money] <achievement id>");
                return;
            }
            int achievementId;
            if (args.length > 2) {
                try {
                    achievementId = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + args[2] + " isn't a valid integer for achievement ID!");
                    player.sendMessage(ChatColor.YELLOW + "Run /queue create [honor points] [amount of money] <achievement id>");
                    return;
                }
            } else {
                achievementId = 0;
            }
            queueTypeFields.put("honorAmount", honor);
            queueTypeFields.put("currencyAmount", money);
            queueTypeFields.put("achievementId", achievementId);
            finish(player);
        }
    }

    /**
     * Finalizes the current queue creation process and registers the queue in the system.
     * This method creates a specific type of queue based on the {@code type} field and relevant
     * configuration attributes, notifying the player about the success or failure of the operation.
     *
     * <p>The queue type is determined using the {@code type} field, and the method handles each
     * type with its respective logic:</p>
     * <ul>
     *   <li>{@link QueueType#BLOCK}: Creates a {@link BlockQueue} with a redstone block location.</li>
     *   <li>{@link QueueType#CAROUSEL}, {@link QueueType#TEACUPS}, {@link QueueType#AERIAL_CAROUSEL},
     *       {@link QueueType#FILE}: Creates a {@link PluginQueue} with additional configuration fields
     *       such as exit location, currency type, and rewards.</li>
     * </ul>
     *
     * <p>In case of successful queue creation:</p>
     * <ul>
     *   <li>The queue is registered to the system via {@link QueueManager#addQueue}.</li>
     *   <li>The player is notified that the queue is ready and provided instructions to open it.</li>
     * </ul>
     *
     * <p>If queue creation fails due to missing or invalid configuration, the player is informed
     * of the error and prompted to contact a developer if the issue persists.</p>
     *
     * @param player the {@link CPlayer} executing the queue finalization command.
     *               Used for sending messages and context-specific feedback.
     */
    private void finish(CPlayer player) {
        player.sendMessage(ChatColor.YELLOW + "Great! Finalizing your " + type.name() + " Queue...");
        Queue finalQueue;
        switch (type) {
            case BLOCK:
                finalQueue = new BlockQueue(this.id, getPark(), UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', this.name),
                        this.warp, this.groupSize, this.delay, false, this.station, new ArrayList<>(), (Location) queueTypeFields.get("blockLocation"));
                break;
            case CAROUSEL:
            case TEACUPS:
            case AERIAL_CAROUSEL:
            case FILE:
                finalQueue = new PluginQueue(this.id, getPark(), UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', this.name),
                        this.warp, this.groupSize, this.delay, false, this.station, new ArrayList<>(), (Location) queueTypeFields.get("exit"),
                        CurrencyType.BALANCE, (int) queueTypeFields.get("currencyAmount"), (int) queueTypeFields.get("honorAmount"),
                        (int) queueTypeFields.get("achievementId"), (JsonObject) queueTypeFields.get("rideConfig"));
                break;
            default:
                finalQueue = null;
        }
        if (finalQueue == null) {
            player.sendMessage(ChatColor.RED + "Uh oh, looks like there was an error creating this queue! Try again and if this problem persists, contact a Developer.");
            return;
        }
        ParkManager.getQueueManager().addQueue(finalQueue);
        player.sendMessage(ChatColor.GREEN + "Your queue is all ready to go! It's closed by default, but you can change that with " + ChatColor.YELLOW + "/queue open");
    }
}
