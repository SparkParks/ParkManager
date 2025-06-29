package network.palace.parkmanager.shows;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * <p>The {@code ShowMenuManager} class manages a menu system for viewing and requesting shows.
 * It provides functionality to load, list, add, remove, and request shows from a configurable data source.</p>
 *
 * <p>This class encapsulates show-related operations, including:</p>
 * <ul>
 *     <li>Loading shows and configuration limits from file.</li>
 *     <li>Managing a menu system for listing available shows and pending requests.</li>
 *     <li>Handling user requests to start shows or respond to requests.</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <ul>
 *     <li><b>Show Management:</b> Allows addition, removal, and retrieval of shows.</li>
 *     <li><b>Menu System:</b> Provides an interactive interface for users to view and request shows.</li>
 *     <li><b>Request Handling:</b> Handles user requests to run shows, including approval, denial, and related notifications.</li>
 *     <li><b>Configuration Management:</b> Automatically loads and persists configurations for shows and request limits.</li>
 * </ul>
 *
 * <h2>Usage Notes</h2>
 * <ul>
 *     <li>Ensure the required subsystem ("showmenu") is registered and accessible before using this class.</li>
 *     <li>This class interacts with system-specific utilities to log messages, manage configurations, and handle player commands.</li>
 *     <li>{@link #initialize()} should be called to ensure that the show data and configurations are loaded properly before using its other methods.</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is not thread-safe. Access to shared resources (e.g., {@code shows} and {@code requests} lists) should be externally synchronized in a multi-threaded environment
 * .</p>
 *
 * <h2>Error Handling</h2>
 * <ul>
 *     <li>Handles errors gracefully when loading configuration files or interacting with system utilities.</li>
 *     <li>Logs errors to assist debugging and proper system administration.</li>
 *     <li>Certain error scenarios (e.g., missing configurations or invalid files) might result in default configurations being applied.</li>
 * </ul>
 */
public class ShowMenuManager {
    /**
     * A collection of {@link ShowEntry} objects representing all available shows.
     *
     * <p>This list serves as the primary data structure for managing show entries
     * within the {@code ShowMenuManager} class.</p>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Stores instances of {@code ShowEntry}, each representing details about a specific show.</li>
     *   <li>Enables operations such as addition, removal, and listing of shows.</li>
     *   <li>Acts as the source for managing display and interaction functionality for the show menu.</li>
     * </ul>
     *
     * <p><b>Initialization:</b></p>
     * <ul>
     *   <li>Initialized as an empty {@link ArrayList} to allow dynamic management of {@code ShowEntry} objects.</li>
     *   <li>Final to ensure the reference to the list cannot be changed, preserving data integrity.</li>
     * </ul>
     */
    private final List<ShowEntry> shows = new ArrayList<>();

    /**
     * A list containing all current {@link ShowRequest} instances.
     * <p>
     * This list is used to manage pending or active show requests within the system. Each request
     * represents a player's attempt to initiate or interact with a specific show.
     * </p>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Stores and tracks all {@link ShowRequest} objects managed by the {@code ShowMenuManager}.</li>
     *   <li>Allows for operations such as adding, removing, or processing show requests.</li>
     *   <li>Enables interaction with pending requests through other methods within the class.</li>
     * </ul>
     *
     * <p>The list is initialized as an empty {@link ArrayList} and populated as {@link ShowRequest}
     * instances are created and managed.</p>
     *
     * <p><b>Characteristics:</b></p>
     * <ul>
     *   <li>Declared as {@code private} to encapsulate and protect the list from
     *       accidental external modification.</li>
     *   <li>Defined as {@code final} to ensure the reference to the list cannot be reassigned.</li>
     * </ul>
     */
    private final List<ShowRequest> requests = new ArrayList<>();

    /**
     * Represents a dedicated {@link FileUtil.FileSubsystem} instance utilized for managing
     * file operations specifically associated with the {@link ShowMenuManager} class.
     *
     * <p>This variable is responsible for providing a structured and organized means of handling
     * JSON files, directories, and subsystem-specific data related to the menu and request
     * management features in the system.</p>
     *
     * <p><strong>Key Characteristics:</strong></p>
     * <ul>
     *     <li>Acts as a link to a specific subsystem directory where relevant files are stored.</li>
     *     <li>Facilitates creation, reading, and writing of files within the associated subsystem directory.</li>
     *     <li>Ensures better separation of concerns by delegating file management tasks to the subsystem.</li>
     * </ul>
     *
     * <p>This subsystem is tightly integrated with the {@link FileUtil} architecture,
     * offering streamlined management of menu and request-related file data to ensure
     * a uniform application-level file structure.</p>
     */
    private FileUtil.FileSubsystem subsystem;

    /**
     * Constructs a new {@code ShowMenuManager} instance and initializes its state.
     *
     * <p>The constructor is responsible for preparing the manager to handle show-related
     * functionality. It performs the following actions:</p>
     * <ul>
     *   <li>Initializes any required data structures by calling the {@code initialize} method.</li>
     *   <li>Prepares the {@code ShowMenuManager} to interact with systems related to park show management.</li>
     * </ul>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Ensures that the necessary configurations for shows and request limits
     *       are properly loaded or created.</li>
     *   <li>Guarantees that the system is ready to manage interactions, such as
     *       adding shows, responding to requests, or managing UI menus for players.</li>
     * </ul>
     *
     * <p>Once constructed, this manager can be utilized to handle operations such as:</p>
     * <ul>
     *   <li>Adding, retrieving, or removing shows.</li>
     *   <li>Managing show requests and player interactions.</li>
     *   <li>Saving the current state to a configuration file.</li>
     * </ul>
     */
    public ShowMenuManager() {
        initialize();
    }

    /**
     * Initializes the ShowMenuManager by loading configuration data, setting up the relevant subsystem,
     * and preparing the internal structure for managing shows and their limits.
     * <p>
     * This method performs the following actions:
     * <ul>
     * <li>Clears the current list of shows.</li>
     * <li>Ensures the subsystem for the "showmenu" is registered. If not, it registers and initializes it.</li>
     * <li>Attempts to load show data from the subsystem's "shows" configuration file and populates the list of shows.</li>
     * <li>Saves the current state of the shows back to the "shows" file.</li>
     * <li>Logs the number of successfully loaded shows.</li>
     * <li>Loads or initializes the "limits" configuration file, ensuring it contains a valid structure for tracking users and shows.</li>
     * </ul>
     * <p>
     * In case of an error during file operations, this method logs the error details and attempts to continue loading where possible.
     * <p>
     * This method is designed to ensure that the ShowMenuManager is in a consistent and ready state for subsequent operations.
     */
    public void initialize() {
        shows.clear();
        if (ParkManager.getFileUtil().isSubsystemRegistered("showmenu")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("showmenu");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("showmenu");
        }
        try {
            JsonElement element = subsystem.getFileContents("shows");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();
                    shows.add(new ShowEntry(object.get("showFile").getAsString(),
                            object.get("region").getAsString(),
                            object.get("displayName").getAsString()));
                }
            }
            saveToFile();
            Core.logMessage("ShowMenu", "Loaded " + shows.size() + " show" + TextUtil.pluralize(shows.size()) + "!");
        } catch (IOException e) {
            Core.logMessage("ShowMenu", "There was an error loading the ShowMenu config!");
            e.printStackTrace();
        }
        try {
            JsonObject object = (JsonObject) subsystem.getFileContents("limits");
            if (object.has("users")) return;
            object.add("users", new JsonArray());
            object.add("shows", new JsonArray());
            subsystem.writeFileContents("limits", object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a specific show entry by its name.
     *
     * <p>This method iterates through the list of shows and searches for the {@code ShowEntry}
     * whose command matches the given {@code name}. If a match is found, the corresponding
     * {@code ShowEntry} is returned. If no match is found, {@code null} is returned.
     *
     * @param name the name of the show to search for; typically corresponds to the
     *             command or identifier of the show.
     * @return the {@code ShowEntry} object matching the specified name, or
     *         {@code null} if no matching entry is found.
     */
    public ShowEntry getShow(String name) {
        ShowEntry show = null;
        for (ShowEntry entry : shows) {
            if (entry.getCommand().equals(name)) {
                show = entry;
                break;
            }
        }
        return show;
    }

    /**
     * Adds a new show entry to the current list of shows and persists the changes
     * to the configuration file.
     *
     * <p>This method is part of the show management functionality and ensures that
     * a given {@code ShowEntry} is added to the internal list of managed shows.
     * After adding the entry, the state is immediately saved to the relevant file
     * to ensure consistency and durability of the data.</p>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>The provided {@code ShowEntry} is appended to the existing collection of shows.</li>
     *   <li>The system ensures that all changes are written to the persistent storage
     *       by invoking the {@code saveToFile} method.</li>
     * </ul>
     *
     * @param entry The show entry to be added. This must be a valid instance of {@code ShowEntry},
     *              representing the details of a new show that will be included in the show list.
     */
    public void addShow(ShowEntry entry) {
        shows.add(entry);
        saveToFile();
    }

    /**
     * Removes a specified show entry from the list of managed shows and updates the configuration file.
     *
     * <p>This method attempts to remove the specified {@code ShowEntry} from the underlying list
     * of shows. If the operation is successful, the current state of the list is persisted
     * to a file by invoking {@code saveToFile}. The removal status is then returned.
     *
     * @param entry the {@code ShowEntry} to be removed. It must not be {@code null}.
     *              This entry represents a specific show and all its associated details
     *              to be managed by the system.
     * @return {@code true} if the specified {@code ShowEntry} was successfully removed;
     *         {@code false} otherwise, typically indicating that the entry was not found in the list.
     */
    public boolean removeShow(ShowEntry entry) {
        boolean b = shows.remove(entry);
        saveToFile();
        return b;
    }

    /**
     * Displays a list of all available show entries to the specified player.
     * <p>
     * This method sends a formatted list of shows to the player's chat interface. Each show
     * entry includes details such as the associated command, the region, and the display name.
     * The shows are displayed in a consistent, color-coded format for better readability.
     *
     * @param player the {@code CPlayer} to whom the list of shows will be displayed. The player
     *               receives the messages in their chat, enumerating all available shows
     *               managed by the system.
     */
    public void listShows(CPlayer player) {
        player.sendMessage(ChatColor.GREEN + "Shareholder Show Menu Shows:");
        for (ShowEntry entry : shows) {
            player.sendMessage(ChatColor.AQUA + "- " + ChatColor.GREEN + entry.getCommand() + ChatColor.YELLOW + ", " +
                    ChatColor.GREEN + entry.getRegion() + ChatColor.YELLOW + ", " + ChatColor.GREEN + entry.getDisplayName());
        }
    }

    /**
     * Retrieves a {@link ShowRequest} based on the specified request identifier.
     *
     * <p>This method iterates through the list of existing {@code ShowRequest} objects
     * and searches for a match with the provided {@code requestId}. If a match is found,
     * the corresponding {@code ShowRequest} is returned. If no match exists, {@code null} is
     * returned.</p>
     *
     * @param requestId the unique identifier of the {@code ShowRequest} to retrieve. This is
     *                  used to search for a specific request within the list of requests.
     * @return the {@link ShowRequest} object associated with the specified {@code requestId},
     *         or {@code null} if no matching request is found.
     */
    private ShowRequest getRequest(UUID requestId) {
        for (ShowRequest request : requests) {
            if (request.getRequestId().equals(requestId)) {
                return request;
            }
        }
        return null;
    }

    /**
     * Opens the Show Menu for the specified player.
     *
     * <p>This method creates a new menu interface for the player, displaying a list of shows
     * that they can interact with. Each show is represented as a button within the menu, which,
     * when clicked, allows the player to proceed to a confirmation action or further interaction
     * specific to the selected show.</p>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Constructs a list of {@code MenuButton} instances from the available shows.</li>
     *   <li>Assigns click actions to each button (e.g., opening the show confirmation).</li>
     *   <li>Initializes a {@code Menu} object with these buttons and presents it to the player.</li>
     * </ul>
     *
     * <p>Note that this method does not handle permissions or other constraints; it assumes
     * the player is already allowed to access the Show Menu and related actions.</p>
     *
     * @param player The player for whom the Show Menu is being opened. The {@code CPlayer} instance
     *               represents the target player interacting with the menu system.
     */
    public void openShowMenu(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>();
        int i = 0;
        for (ShowEntry entry : shows) {
            buttons.add(new MenuButton(i++, entry.getItem(), ImmutableMap.of(ClickType.LEFT, p -> openShowConfirm(p, entry))));
        }
        new Menu(27, Rank.SHAREHOLDER.getTagColor() + "Shareholder Show Menu", player, buttons).open();
    }

    /**
     * Opens a request menu for the specified player, listing all pending show requests.
     *
     * <p>This method generates a menu for the given {@code CPlayer} to display show requests.
     * Each request is represented by an interactive menu button that includes details
     * about the requested show and the requester. Players can interact with these buttons
     * to respond to the requests.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Iterates through all available show requests.</li>
     *   <li>Creates a corresponding {@link MenuButton} for each request.</li>
     *   <li>Displays each request with its associated {@link ItemStack} details, including
     *       the show's display name and requester information.</li>
     *   <li>Assigns an interaction listener to each button so players can proceed with approving
     *       or denying the request.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} for whom the request menu should be opened. This player
     *               will interact with the menu to handle specific requests.
     */
    public void openRequestMenu(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>();
        int i = 0;
        for (ShowRequest request : requests) {
            CPlayer requester = Core.getPlayerManager().getPlayer(request.getUuid());
            if (requester == null) continue;

            ItemStack item = request.getShow().getItem();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(request.getShow().getDisplayName());
            meta.setLore(Collections.singletonList(ChatColor.GREEN + "Requested by: " + ChatColor.LIGHT_PURPLE + requester.getName()));
            item.setItemMeta(meta);

            buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> openRequestConfirm(p, request, item))));
        }
        new Menu(27, ChatColor.LIGHT_PURPLE + "Shareholder Show Requests", player, buttons).open();
    }

    /**
     * Opens a confirmation menu for the player to confirm or cancel a show request.
     *
     * <p>This method first checks whether the player can request the specified show entry
     * using the {@code canRequestShow} method. If the player cannot request the show, the
     * inventory is closed, and an error message is sent to the player.</p>
     *
     * <p>If the player is eligible to request the show, a confirmation menu is displayed
     * with options to cancel or confirm the request.</p>
     *
     * @param player the player initiating the show request
     * @param entry the show entry the player wants to request
     */
    public void openShowConfirm(CPlayer player, ShowEntry entry) {
        String s = canRequestShow(player, entry);
        if (s != null) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Uh oh! " + ChatColor.AQUA + s);
            return;
        }
        new Menu(27, Rank.SHAREHOLDER.getTagColor() + "Request Show Start", player, Arrays.asList(
                new MenuButton(4, entry.getItem()),
                new MenuButton(11, ItemUtil.create(Material.CONCRETE, ChatColor.RED + "Cancel", 14),
                        ImmutableMap.of(ClickType.LEFT, CPlayer::closeInventory)),
                new MenuButton(15, ItemUtil.create(Material.CONCRETE, ChatColor.GREEN + "Confirm", 13),
                        ImmutableMap.of(ClickType.LEFT, p -> handleShowRequest(p, entry)))
        )).open();
    }

    /**
     * Opens a confirmation menu for the player to respond to a specific request.
     * <p>
     * This method displays a menu with options to approve or deny the provided request.
     *
     * @param player  The player who is interacting with the menu.
     * @param request The request object the player is responding to.
     * @param item    The item displayed in the menu as part of the request details.
     */
    public void openRequestConfirm(CPlayer player, ShowRequest request, ItemStack item) {
        new Menu(27, Rank.SHAREHOLDER.getTagColor() + "Respond To Request", player, Arrays.asList(
                new MenuButton(4, item),
                new MenuButton(11, ItemUtil.create(Material.CONCRETE, ChatColor.RED + "Deny", 14),
                        ImmutableMap.of(ClickType.LEFT, p -> handleRequestResposne(p, request, false))),
                new MenuButton(15, ItemUtil.create(Material.CONCRETE, ChatColor.GREEN + "Approve", 13),
                        ImmutableMap.of(ClickType.LEFT, p -> handleRequestResposne(p, request, true)))
        )).open();
    }

//    public void handlePacket(PacketShowRequestResponse packet) {
//        ShowRequest request = getRequest(packet.getRequestId());
//        if (request == null) return;
//        request.setCanBeApproved(true);
//    }

    /**
     * Handles a player's show request by processing and notifying the staff.
     * <p>
     * This method performs the following actions:
     * <ul>
     *   <li>Closes the player's inventory.</li>
     *   <li>Sends a confirmation message to the player indicating that their request is being processed.</li>
     *   <li>Generates a unique request ID and adds the show request to the internal request tracking system.</li>
     *   <li>Sends a notification message to the staff with the details of the player's request.</li>
     * </ul>
     * If an error occurs during the staff notification, the exception stack trace is printed.
     *
     * @param player The player who is requesting to see the show. Cannot be {@code null}.
     * @param entry The show entry that the player is requesting. Cannot be {@code null}.
     */
    private void handleShowRequest(CPlayer player, ShowEntry entry) {
        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "Processing request...");
        UUID requestId = UUID.randomUUID();
        requests.add(new ShowRequest(requestId, player.getUniqueId(), entry));
        try {
            Core.getMessageHandler().sendStaffMessage(ChatColor.GREEN + "A shareholder " + player.getName() + " has requested the show " + entry.getDisplayName() + "! " + ChatColor.GREEN + "To accept/deny this request, head to " + Core.getInstanceName() + " and run /shows!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the request response for running a show. This includes approving or declining
     * the request from a player, updating necessary data, and notifying the requester of the outcome.
     *
     * <p>This method performs the following actions:</p>
     * <ul>
     *     <li>Closes the requesting player's inventory.</li>
     *     <li>Removes the request from the active request list.</li>
     *     <li>If approved, executes the specified command associated with the show request,
     *     updates the player's request limits and persists relevant data in the database.</li>
     *     <li>If declined, sends a notification to the requester with the decline message.</li>
     * </ul>
     *
     * @param player   The player handling the response to the show request.
     * @param request  The {@link ShowRequest} object containing details about the requested show.
     * @param approve  A boolean indicating whether the request is approved or declined.
     */
    public void handleRequestResposne(CPlayer player, ShowRequest request, boolean approve) {
        player.closeInventory();
        requests.remove(request);
        CPlayer requester = Core.getPlayerManager().getPlayer(request.getUuid());
        if (approve) {
            String cmd = "multishow start " + request.getCommand();
            Core.logMessage("Shareholder Show", cmd);
            player.performCommand(cmd);
//            ShowPlugin.startShow(request.getShow().getShowFile(), new Show(ParkManager.getInstance(), file));
            updateLimitsFile(player, request);
            Core.getMongoHandler().getDatabase().getCollection("players")
                    .updateOne(Filters.eq("uuid", player.getUniqueId().toString()),
                            Updates.set("showRequests", new Document("lastShow", request.getShow().getCommand())
                                    .append("lastRan", System.currentTimeMillis() / 1000)));
        } else {
            requester.sendMessage(ChatColor.RED + "A staff member has declined your request to run a show. Please try again soon!");
        }
    }

    /**
     * Determines if a player can request to run a specific show based on usage limits from
     * a configuration file. The method checks if the player or other users have previously run
     * the show within defined time constraints.
     *
     * <p>The method evaluates:
     * <ul>
     *   <li>Whether the show was executed by the player within the last 48 hours.</li>
     *   <li>Whether the show was run by any other user categorized as a Shareholder within the last 12 hours.</li>
     * </ul>
     *
     * <p>If the player or other users have run the show within the set timeframes, a corresponding
     * message is returned indicating how long the player must wait to execute the show again.
     * Otherwise, the request may proceed.
     *
     * @param player The player requesting to run the show. The player's unique ID is used for identification and matching in the configuration file.
     * @param entry The requested show entry that contains the command identifying the specific show.
     * @return A message indicating why the show cannot be run yet, or {@code null} if the player is allowed to run the show.
     */
    public String canRequestShow(CPlayer player, ShowEntry entry) {
        try {
            JsonElement element = subsystem.getFileContents("limits");
            if (element.isJsonObject()) {
                JsonObject object = (JsonObject) element;
                JsonArray users = object.getAsJsonArray("users");
                for (JsonElement e : users) {
                    JsonObject o = (JsonObject) e;

                    UUID uuid = UUID.fromString(o.get("uuid").getAsString());
                    if (!uuid.equals(player.getUniqueId())) continue;

                    JsonArray shows = o.getAsJsonArray("shows");
                    for (JsonElement e2 : shows) {
                        JsonObject show = (JsonObject) e2;

                        String showFile = show.get("name").getAsString();
                        if (showFile.equals(entry.getCommand())) {
                            if (show.get("lastRan").getAsLong() > ((System.currentTimeMillis() / 1000) - 172800)) {
                                return "You've already run this show within the last 48 hours, try again soon!";
                            } else {
                                break;
                            }
                        }
                    }
                    break;
                }
                JsonArray shows = object.getAsJsonArray("shows");
                for (JsonElement e : shows) {
                    JsonObject show = (JsonObject) e;

                    String showFile = show.get("name").getAsString();
                    if (showFile.equals(entry.getCommand())) {
                        if (show.get("lastRan").getAsLong() > ((System.currentTimeMillis() / 1000) - 43200)) {
                            return "This show was run by a Shareholder within the last 12 hours, try again soon!";
                        } else {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the "limits" file with information about the specified player's executed show
     * and the current timestamp. This method modifies both the player's show-specific record
     * and global show limits data.
     *
     * <p>The method performs the following steps:
     * <ul>
     *   <li>Retrieves the existing contents of the "limits" file as a JSON object.</li>
     *   <li>Updates or adds the data for the specified player's show, including the last run
     *       timestamp.</li>
     *   <li>Updates or adds the global entry for the specified show.</li>
     *   <li>Writes the updated JSON back to the "limits" file.</li>
     * </ul>
     *
     * @param player  The player whose data will be updated in the "limits" file. This parameter
     *                includes the player's unique identifier, which is used to locate or create
     *                their entry in the file.
     * @param request The show request containing details about the show being executed, such as
     *                the command representing the show.
     */
    private void updateLimitsFile(CPlayer player, ShowRequest request) {
        try {
            JsonObject object = (JsonObject) subsystem.getFileContents("limits");

            JsonObject show = new JsonObject();
            show.addProperty("name", request.getShow().getCommand());
            show.addProperty("lastRan", System.currentTimeMillis() / 1000);

            JsonArray users = object.getAsJsonArray("users");
            JsonArray userShows = new JsonArray();
            for (JsonElement e : users) {
                JsonObject o = (JsonObject) e;

                UUID uuid = UUID.fromString(o.get("uuid").getAsString());
                if (!uuid.equals(player.getUniqueId())) continue;

                userShows = o.getAsJsonArray("shows");
                users.remove(o);
                break;
            }
            for (JsonElement e : userShows) {
                JsonObject s = (JsonObject) e;

                String showFile = s.get("name").getAsString();
                if (showFile.equals(request.getShow().getCommand())) userShows.remove(s);
            }
            userShows.add(show);

            JsonObject user = new JsonObject();
            user.addProperty("uuid", player.getUniqueId().toString());
            user.add("shows", userShows);
            users.add(user);

            JsonArray shows = object.getAsJsonArray("shows");
            for (JsonElement e : shows) {
                JsonObject s = (JsonObject) e;

                String showFile = s.get("name").getAsString();
                if (showFile.equals(request.getShow().getCommand())) shows.remove(s);
            }
            shows.add(show);

            subsystem.writeFileContents("limits", object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the list of show entries to a file in JSON format.
     *
     * <p>This method retrieves the list of shows, sorts them alphabetically by their display name (case-insensitive,
     * ignoring color codes), and constructs a JSON array. Each show entry is represented as a JSON object containing
     * the following properties:
     * <ul>
     *   <li><b>showFile</b>: The command associated with the show.</li>
     *   <li><b>region</b>: The region assigned to the show.</li>
     *   <li><b>displayName</b>: The display name of the show.</li>
     * </ul>
     * The JSON array is then written to a specified subsystem's file using the defined file utility.
     *
     * <p>If an {@link IOException} occurs during the file write process, an error message is logged to the console.
     *
     * <p><b>Note:</b> This method assumes the existence of a "showmenu" subsystem and proper configurations for file
     * writing through the ParkManager file utility.
     *
     * @throws IOException If there is an error writing the JSON data to the file.
     */
    public void saveToFile() {
        JsonArray array = new JsonArray();
        shows.sort(Comparator.comparing(showEntry -> ChatColor.stripColor(showEntry.getDisplayName().toLowerCase())));
        for (ShowEntry entry : shows) {
            JsonObject object = new JsonObject();
            object.addProperty("showFile", entry.getCommand());
            object.addProperty("region", entry.getRegion());
            object.addProperty("displayName", entry.getDisplayName());
            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getSubsystem("showmenu").writeFileContents("shows", array);
        } catch (IOException e) {
            Core.logMessage("ShowMenu", "There was an error writing to the ShowMenu config!");
            e.printStackTrace();
        }
    }
}
