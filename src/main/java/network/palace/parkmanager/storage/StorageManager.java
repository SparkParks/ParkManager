package network.palace.parkmanager.storage;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.events.IncomingMessageEvent;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.messagequeue.ConnectionType;
import network.palace.core.messagequeue.MessageClient;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.handlers.storage.ResortInventory;
import network.palace.parkmanager.handlers.storage.StorageData;
import network.palace.parkmanager.handlers.storage.StorageSize;
import network.palace.parkmanager.message.ParkStorageLockPacket;
import network.palace.parkmanager.utils.HashUtil;
import network.palace.parkmanager.utils.InventoryUtil;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;

/**
 * The <code>StorageManager</code> class handles storage-related functionality for players,
 * including inventory updates, database synchronization, and management of banned items.
 * This class manages player inventories, processes join and logout events, and provides
 * static utility methods for manipulating inventory data.
 *
 * <p>Key responsibilities of <code>StorageManager</code> include:</p>
 * <ul>
 *   <li>Initializing storage and inventory systems for players.</li>
 *   <li>Handling player join and logout events, ensuring database synchronization.</li>
 *   <li>Managing updates to player inventories based on gameplay states such as build mode.</li>
 *   <li>Providing static utility methods for inventory manipulation and data transformation.</li>
 * </ul>
 *
 * <p>The class includes fields to manage banned items, players waiting for inventory updates,
 * joined players, and objects related to player backpack storage.</p>
 */
public class StorageManager {
    /**
     * A predefined list of {@link Material} objects representing items that are banned
     * from being included in player inventories within the system.
     * <p>
     * These items will be filtered out or removed when handling inventories, ensuring
     * players are restricted from using or accessing any banned materials.
     * </p>
     * <p>
     * Contains the following banned materials:
     * <ul>
     *   <li>{@link Material#MINECART}</li>
     *   <li>{@link Material#SNOW_BALL}</li>
     *   <li>{@link Material#ARROW}</li>
     * </ul>
     * </p>
     * <p>
     * This list remains static throughout the application's runtime and cannot be modified.
     * </p>
     */
    private static final List<Material> bannedItems = Arrays.asList(Material.MINECART, Material.SNOW_BALL, Material.ARROW);
    /**
     * A map used to store information about players who require their inventory to be set
     * after joining. This is especially useful for managing the state of players within the game.
     * The map uses the player's {@link UUID} as the key, while the value is a {@link Boolean}
     * signifying whether the player should join in build mode.
     *
     * <p>Key characteristics:</p>
     * <ul>
     *     <li><b>Key ({@link UUID}):</b> Represents the unique identifier of the player.</li>
     *     <li><b>Value ({@link Boolean}):</b> Indicates whether the player is in build mode
     *         (<code>true</code> if in build mode, <code>false</code> otherwise).</li>
     * </ul>
     *
     * <p>
     * This variable is primarily utilized for managing player states upon login and ensuring
     * their inventories match their required configurations.
     * </p>
     *
     * <p>Thread Safety: As this map is marked as <code>private final</code>, it can only be
     * accessed or modified inside the class that declares it. However, it is not inherently
     * thread-safe. If used in a multi-threaded environment, synchronization methods should be
     * considered to avoid race conditions.</p>
     *
     * <p>Usage Context:</p>
     * <ul>
     *     <li>Assign the appropriate inventory to players upon joining.</li>
     *     <li>Track and handle build mode configuration for specific players.</li>
     *     <li>Ensure game mechanics related to player inventory updates are properly synchronized.</li>
     * </ul>
     */
    // Map used to store players that need their inventory set after joining
    // Boolean represents build mode
    private final HashMap<UUID, Boolean> joinList = new HashMap<>();
    /**
     * A data structure that stores players who are waiting for their inventory data
     * to finish uploading from a previous server.
     *
     * <p>The map uses the player's UUID as a key, and the corresponding value is the timestamp
     * (in milliseconds) when they were added to the queue.</p>
     *
     * <p>Players who remain in this queue for longer than 5 seconds should be prompted
     * to disconnect and reconnect to help resolve the delay.</p>
     *
     * <ul>
     *   <li>Key: {@code UUID} - The unique identifier of the player.</li>
     *   <li>Value: {@code Long} - The time the player was added to the queue, in milliseconds.</li>
     * </ul>
     *
     * <p>This field is immutable and should only be modified within the {@code StorageManager} class to
     * ensure proper synchronization and functionality.</p>
     */
    // List of players waiting for previous server to finish uploading inventory
    // If this takes more than 5 seconds, request player to disconnect and reconnect
    private final HashMap<UUID, Long> waitingForInventory = new HashMap<>();

    /**
     * Represents the backpack item stack used in the player's inventory management system.
     * <p>
     * This variable is stored as part of the {@code StorageManager} class and is used to manage
     * the contents and interactions with a player's backpack. It is immutable once initialized.
     * </p>
     * <p>
     * The {@code backpack} may include player-specific items or storage data crucial for
     * gameplay functionality. It works in conjunction with other inventory systems
     * to provide consistent tracking and synchronization.
     * </p>
     * <ul>
     *    <li>Immutable: This ensures thread safety as it cannot be modified after initialization.</li>
     *    <li>Core component of inventory management: Used in methods handling inventory updates,
     *        synchronization, and database interaction.</li>
     * </ul>
     */
    private final ItemStack backpack;

    /**
     * Constructs a new instance of the {@code StorageManager} class.
     * <p>
     * This constructor initializes the player's backpack based on the current resort specified
     * in the application. It sets different item properties for the backpack depending on whether
     * the resort is Walt Disney World (WDW) or not.
     * <p>
     * <strong>Key Initialization Behavior:</strong>
     * <ul>
     * <li>If the resort is {@code Resort.WDW}, the backpack is initialized as a {@code DIAMOND_HOE}
     * with a custom name and specific item properties, such as being unbreakable and hiding the unbreakable state.</li>
     * <li>If the resort is not {@code Resort.WDW}, the backpack is initialized as a {@code CHEST}
     * with a custom name.</li>
     * </ul>
     */
    public StorageManager() {
        if (ParkManager.getResort().equals(Resort.WDW)) {
            backpack = ItemUtil.create(Material.DIAMOND_HOE, ChatColor.GREEN + "Backpack " + ChatColor.GRAY + "(Right-Click)", 47);
            ItemMeta meta = backpack.getItemMeta();
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            backpack.setItemMeta(meta);
        } else {
            backpack = ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Backpack " + ChatColor.GRAY + "(Right-Click)");
        }
    }

    /**
     * Initializes the {@code StorageManager} by setting up periodic tasks and message handling mechanisms.
     *
     * <p>This method performs the following actions:
     * <ul>
     *     <li>Schedules a periodic task to update cached inventories for online players every 60 seconds.</li>
     *     <li>Schedules another task to process join requests and handle delayed inventory loading issues every 0.5 seconds:
     *         <ul>
     *             <li>Processes and clears the {@code joinList} queue, invoking inventory handling for players based on their designated state.</li>
     *             <li>Monitors {@code waitingForInventory} for players experiencing prolonged inventory loading, notifying affected players and updating their status.</li>
     *         </ul>
     *     </li>
     *     <li>Establishes a message client for asynchronous communication, using a "fanout" exchange named {@code all_parks}:
     *         <ul>
     *             <li>Registers a consumer to listen for incoming messages, process them, and trigger {@code IncomingMessageEvent} instances.</li>
     *             <li>Handles errors during message processing and delivery gracefully.</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * <p>Any exceptions encountered during initialization (e.g., I/O errors or message client setup failures) are caught and logged.
     */
    public void initialize() {
        Core.runTaskTimer(ParkManager.getInstance(), () -> Core.getPlayerManager().getOnlinePlayers().forEach(this::updateCachedInventory), 0L, 1200L);
        Core.runTaskTimer(ParkManager.getInstance(), () -> {
            if (!joinList.isEmpty()) {
                HashMap<UUID, Boolean> map = new HashMap<>(joinList);
                joinList.clear();
                map.forEach((uuid, build) -> {
                    CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                    if (player == null) return;
                    ParkManager.getInventoryUtil().handleJoin(player, build ? InventoryUtil.InventoryState.BUILD : InventoryUtil.InventoryState.GUEST);
                });
            }
            if (!waitingForInventory.isEmpty()) {
                List<UUID> toRemove = new ArrayList<>();
                List<UUID> toUpdate = new ArrayList<>();
                for (Map.Entry<UUID, Long> entry : waitingForInventory.entrySet()) {
                    if (System.currentTimeMillis() - entry.getValue() >= 5000) {
                        // If it's taking more than 5 seconds to load their inventory, suggest they reconnect
                        CPlayer player = Core.getPlayerManager().getPlayer(entry.getKey());
                        if (player == null) {
                            toRemove.add(entry.getKey());
                        } else {
                            player.sendMessage(" ");
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.YELLOW + "We're having some trouble loading your inventory items.\n" +
                                    ChatColor.LIGHT_PURPLE + "Please disconnect and reconnect to Palace Network to fix this.\n" +
                                    ChatColor.AQUA + "We apologize for the inconvenience. If you encounter any further issues, reach out to a staff member with " + ChatColor.GREEN + "/helpme.");
                            player.sendMessage(" ");
                            player.sendMessage(" ");
                            toUpdate.add(entry.getKey());
                        }
                    }
                }
                toRemove.forEach(waitingForInventory::remove);
                toUpdate.forEach(uuid -> waitingForInventory.put(uuid, System.currentTimeMillis() + 5000));
            }
        }, 0L, 10L);
        try {
            MessageClient all_parks = new MessageClient(ConnectionType.PUBLISHING, "all_parks", "fanout");
            Core.getMessageHandler().permanentClients.put("all_parks", all_parks);
            Core.getMessageHandler().registerConsumer("all_parks", "fanout", "", (consumerTag, delivery) -> {
                try {
                    JsonObject object = Core.getMessageHandler().parseDelivery(delivery);
                    Core.debugLog(object.toString());
                    int id = object.get("id").getAsInt();
                    try {
                        new IncomingMessageEvent(id, object).call();
                    } catch (Exception e) {
                        Core.logMessage("MessageHandler", "Error processing IncomingMessageEvent for incoming packet " + object.toString());
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Core.getMessageHandler().handleError(consumerTag, delivery, e);
                }
            }, t -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the process of a player joining the system and manages their inventory state and settings.
     * <p>
     * This method checks whether the player's inventory storage lock is handled correctly while transitioning
     * between server instances. If the player's inventory is ready, it initializes their data and sets up
     * relevant parameters. Otherwise, it marks the player as waiting for their inventory to be available and
     * schedules a subsequent check.
     *
     * @param player The player object representing the individual joining the system.
     * @param buildMode A flag indicating whether the player is in build mode.
     */
    public void handleJoin(CPlayer player, boolean buildMode) {
        Object o = Core.getMongoHandler().getOnlineDataValue(player.getUniqueId(), "parkStorageLock");
        String parkStorageLock = o == null ? "" : (String) o;
        if (parkStorageLock.isEmpty() || parkStorageLock.equals(Core.getInstanceName())) {
            // Player is either not coming from another park server, or that park server has finished saving the player's inventory
            Core.getMongoHandler().setOnlineDataValue(player.getUniqueId(), "parkStorageLock", Core.getInstanceName());
            StorageData data = getStorageDataFromDatabase(player.getUniqueId());
            player.getRegistry().addEntry("storageData", data);
            joinList.put(player.getUniqueId(), buildMode);
        } else {
            // Player is coming from another park server which is still saving the player's inventory
            player.getRegistry().addEntry("waitingForInventory", true);
            player.getRegistry().addEntry("waitingForInventory_BuildSetting", buildMode);
            waitingForInventory.put(player.getUniqueId(), System.currentTimeMillis() + 5000);
        }
    }

    /**
     * Allows a player who joined late to complete the necessary setup for accessing storage.
     * <p>
     * This method handles players in a "waiting for inventory" state, updating their records and cache
     * to ensure they are properly initialized. It adjusts the necessary data in the player's registry
     * and sets their storage preferences based on previous configurations.
     *
     * @param uuid The unique identifier (UUID) of the player who joined late and needs storage setup completion.
     */
    public void joinLate(UUID uuid) {
        waitingForInventory.remove(uuid);
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player == null || !player.getRegistry().hasEntry("waitingForInventory")) return;
        Core.getMongoHandler().setOnlineDataValue(player.getUniqueId(), "parkStorageLock", Core.getInstanceName());
        StorageData data = getStorageDataFromDatabase(player.getUniqueId());
        player.getRegistry().addEntry("storageData", data);
        player.getRegistry().removeEntry("waitingForInventory");
        boolean build = (boolean) player.getRegistry().removeEntry("waitingForInventory_BuildSetting");
        joinList.put(player.getUniqueId(), build);
    }

    /**
     * Retrieves and constructs a {@code StorageData} object from the database
     * for the specified user UUID.
     * <p>
     * This method interacts with the database to fetch and parse storage-related
     * data for a given UUID. If no current storage data exists, it attempts to
     * retrieve legacy data or initializes the storage with default values.
     *
     * @param uuid The unique identifier of the user whose storage data is being requested.
     *             This identifier is used to query the database for storing and retrieving
     *             the user's storage-related data.
     *
     * @return A {@code StorageData} object containing the storage details for the user.
     *         This object is generated either from existing data in the database or
     *         from default values if no valid data is found.
     */
    public StorageData getStorageDataFromDatabase(UUID uuid) {
        Document storage = Core.getMongoHandler().getParkData(uuid, ParkManager.getResort().name().toLowerCase() + "_storage");

        ResortInventory resortInventory;
        if (storage == null) {
            FindIterable<Document> oldStorageDocuments = Core.getMongoHandler().getOldStorageDocuments(uuid);
            Document doc = oldStorageDocuments.first();
            if (doc == null || !doc.containsKey(ParkManager.getResort().name().toLowerCase())) {
                resortInventory = new ResortInventory(ParkManager.getResort(), "", "", "", StorageSize.SMALL.getSize(), "", "", "", StorageSize.SMALL.getSize(), "", "", "", "", "", "");
            } else {
                resortInventory = ParkManager.getInventoryUtil().getResortInventoryFromDocument(uuid, doc.get(ParkManager.getResort().name().toLowerCase(), Document.class), ParkManager.getResort());
            }
        } else {
            resortInventory = ParkManager.getInventoryUtil().getResortInventoryFromDocument(uuid, storage, ParkManager.getResort());
        }

        return resortInventory.toStorageData();
    }

    /**
     * Updates the cached inventory for a given player. This method simply delegates the task to
     * {@link #updateCachedInventory(CPlayer, boolean)} with a `disconnect` parameter set to {@code false}.
     *
     * <p>The method ensures that the player's stored inventory data is kept up to date,
     * ensuring proper synchronization between player state and storage during the application lifecycle.
     *
     * @param player The {@link CPlayer} whose cached inventory will be updated. This player must have valid storage
     *               data associated with their registry for the update to proceed. If the registry does not contain
     *               storage-related data, the operation will be skipped.
     */
    public void updateCachedInventory(CPlayer player) {
        updateCachedInventory(player, false);
    }

    /**
     * Updates the cached inventory for a given player, with optional handling for player disconnection.
     * <p>
     * This method is designed to ensure the player's inventory data is synchronized with the backend
     * system by comparing hash values and determining if an update is required. It also saves data
     * asynchronously to prevent main-thread blocking. Additionally, it handles special actions
     * necessary during player disconnection.
     * </p>
     *
     * @param player    The player whose cached inventory needs to be updated.
     *                  Must have valid storage data in their registry.
     * @param disconnect A flag to indicate whether the update is being triggered due to
     *                   the player's disconnection. If true, specific cleanup and finalization
     *                   operations will be performed.
     */
    public void updateCachedInventory(CPlayer player, boolean disconnect) {
        if (!player.getRegistry().hasEntry("storageData")) return;
        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");

        if (!disconnect && System.currentTimeMillis() - data.getLastInventoryUpdate() < (5 * 60 * 1000)) return;
        long currentTime = System.currentTimeMillis();

        Inventory backpackInventory = data.getBackpack();
        ItemStack[] base;
        ItemStack[] build;

        PlayerInventory inv = player.getInventory();
        ItemStack[] invContents = inv.getStorageContents();

        switch (ParkManager.getInventoryUtil().getInventoryState(player)) {
            case GUEST: {
                base = new ItemStack[36];
                //Store current inventory items (except reserved slots) into 'base' array
                for (int i = 0; i < invContents.length; i++) {
                    if (InventoryUtil.isReservedSlot(i)) continue;
                    base[i] = invContents[i];
                }
                build = data.getBuild();
                break;
            }
            case BUILD: {
                build = new ItemStack[34];
                //Store current inventory items into 'build' array
                if (invContents.length - 2 >= 0) System.arraycopy(invContents, 2, build, 0, invContents.length - 2);
                base = data.getBase();
                break;
            }
            default: {
                base = data.getBase();
                build = data.getBuild();
                break;
            }
        }

        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            String backpackJson = ItemUtil.getJsonFromInventoryNew(backpackInventory).toString();
            String backpackHash = HashUtil.generateHash(backpackJson);
            int backpackSize = data.getBackpackSize().getSize();

            String lockerJson = ItemUtil.getJsonFromInventoryNew(data.getLocker()).toString();
            String lockerHash = HashUtil.generateHash(lockerJson);
            int lockerSize = data.getLockerSize().getSize();

            String baseJson = ItemUtil.getJsonFromArrayNew(base).toString();
            String baseHash = HashUtil.generateHash(baseJson);

            String buildJson = ItemUtil.getJsonFromArrayNew(build).toString();
            String buildHash = HashUtil.generateHash(buildJson);

            data.setLastInventoryUpdate(System.currentTimeMillis());

            if (!disconnect && backpackHash.isEmpty() && lockerHash.isEmpty() && baseHash.isEmpty() && buildHash.isEmpty() && backpackSize == -1 && lockerSize == -1) {
                Core.logInfo("Skipped updating " + player.getName() + "'s inventory, no change!");
                return;
            }

            UpdateData updateData = getDataFromJson(backpackJson, backpackSize, lockerJson, lockerSize, baseJson, buildJson);

            Document doc = new Document("backpack", updateData.getPack()).append("backpacksize", updateData.getPackSize())
                    .append("locker", updateData.getLocker()).append("lockersize", updateData.getLockerSize())
                    .append("base", updateData.getBase()).append("build", updateData.getBuild())
                    .append("last-updated", System.currentTimeMillis());
            Core.getMongoHandler().setParkStorage(player.getUniqueId(), ParkManager.getResort().name().toLowerCase() + "_storage", doc);
            if (disconnect) {
                Core.getMongoHandler().setOnlineDataValueConcurrentSafe(player.getUniqueId(), "parkStorageLock", null, Core.getInstanceName());
                try {
                    Core.getMessageHandler().sendMessage(new ParkStorageLockPacket(player.getUniqueId(), Core.getInstanceName(), false), Core.getMessageHandler().permanentClients.get("all_parks"));
                } catch (Exception e) {
                    Core.getInstance().getLogger().log(Level.SEVERE, "Error while sending ParkStorageLockPacket to all_parks", e);
                }
            }

            Core.logInfo("Inventory packet for " + player.getName() + " generated and sent in " +
                    (System.currentTimeMillis() - currentTime) + "ms");
        });
    }

    /**
     * Updates the inventory of a given player.
     * <p>
     * This method serves as a convenience wrapper to update the player's inventory
     * without performing a storage check. It internally delegates to {@code updateInventory(player, false)}.
     * </p>
     *
     * @param player The {@link CPlayer} whose inventory is being updated. Cannot be {@code null}.
     */
    public void updateInventory(CPlayer player) {
        updateInventory(player, false);
    }

    /**
     * Updates the player's inventory based on their storage data and rank, while also
     * handling special functionalities like adding items, updating player outfits, and
     * managing access to restricted actions such as flight.
     *
     * <p>This method performs critical inventory management tasks by clearing the current
     * inventory, repopulating it with rank-specific items, and ensuring the inventory
     * conforms to the player's storage data and permissions.</p>
     *
     * @param player        The {@code CPlayer} whose inventory is being updated.
     *                      Must have valid storage data in the player's registry if
     *                      {@code storageCheck} is true.
     * @param storageCheck  A boolean flag indicating whether the method should check
     *                      for storage data in the player's registry before proceeding.
     *                      If {@code false}, the method skips this check.
     */
    public void updateInventory(CPlayer player, boolean storageCheck) {
        long time = System.currentTimeMillis();
        if ((!storageCheck && !player.getRegistry().hasEntry("storageData")) || ParkManager.getBuildUtil().isInBuildMode(player))
            return;

        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");

        PlayerInventory inv = player.getInventory();
        inv.clear();
        ItemStack compass = player.getRank().getRankId() >= Rank.CM.getRankId() ? ItemUtil.create(Material.COMPASS) : null;
        ItemStack[] contents = new ItemStack[]{
                compass, null, null, null, null, backpack,
                ItemUtil.create(Material.WATCH, ChatColor.GREEN + "Watch " + ChatColor.GRAY + "(Right-Click)",
                        Arrays.asList(ChatColor.GRAY + "Right-Click to open", ChatColor.GRAY + "the Show Timetable")),
                null, ParkManager.getMagicBandManager().getMagicBandItem(player),
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null
        };

        if (data != null) {
            ItemStack[] base = data.getBase();
            for (int i = player.getRank().getRankId() >= Rank.CM.getRankId() ? 1 : 0; i < base.length; i++) {
                if (InventoryUtil.isReservedSlot(i)) continue;
                contents[i] = base[i];
            }
        }
        inv.setContents(contents);
        ParkManager.getWardrobeManager().setOutfitItems(player);
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            ParkManager.getAutographManager().updateAutographs(player);
            ParkManager.getAutographManager().giveBook(player);
        });

        if (player.getRank().getRankId() >= Rank.TRAINEE.getRankId())
            Core.runTask(ParkManager.getInstance(), () -> player.setAllowFlight(true));
    }

    /**
     * Logs out a player by removing them from the inventory waiting list and updating cached inventory data.
     *
     * <p>This method performs the following actions:
     * <ul>
     *   <li>Removes the player identified by the unique identifier from the {@code waitingForInventory} queue.</li>
     *   <li>Retrieves the player's session using the player manager. If the player does not exist, the method exits.</li>
     *   <li>Updates the player's cached inventory and handles inventory persistence as necessary.</li>
     * </ul>
     *
     * @param uuid The unique identifier of the player being logged out.
     */
    public void logout(UUID uuid) {
        waitingForInventory.remove(uuid);
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player == null) return;
        updateCachedInventory(player, true);
    }

    /**
     * Filters an array of {@link ItemStack} objects by removing items that are banned.
     * <p>
     * This method iterates through the provided array of {@link ItemStack} objects and sets
     * any element to {@code null} if its type is included in the list of banned items.
     * </p>
     *
     * @param items an array of {@link ItemStack} objects to be filtered. Items that match
     *              the banned criteria will be removed (set to {@code null}).
     */
    public static void filterItems(ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            if (bannedItems.contains(items[i].getType())) {
                items[i] = null;
            }
        }
    }

    /**
     * Fills the provided {@link Inventory} with the given {@code ItemStack} array, adjusting the contents
     * to match the size of the specified {@link StorageSize}.
     * <p>
     * If the number of items in the {@code items} array matches the total slots provided by the {@link StorageSize},
     * the method will directly set all contents. If the sizes don't match, the method will copy as many items
     * as possible into a new {@code ItemStack} array that matches the {@link StorageSize}'s slot count and then
     * set the resulting array as the inventory contents. Empty slots will remain unfilled.
     * </p>
     *
     * @param inventory the {@link Inventory} to be filled
     * @param size      the {@link StorageSize} that defines the storage capacity
     *                  and the number of slots in the {@code inventory}
     * @param items     the array of {@link ItemStack} objects to populate into the {@code inventory}.
     *                  If fewer items than the available slots are provided, the remaining slots
     *                  will be left empty.
     */
    public static void fillInventory(Inventory inventory, StorageSize size, ItemStack[] items) {
        if (items.length == size.getSlots()) {
            inventory.setContents(items);
        } else {
            ItemStack[] arr = new ItemStack[size.getSlots()];

            System.arraycopy(items, 0, arr, 0, Math.min(items.length, size.getSlots()));

            inventory.setContents(arr);
        }
    }

    /**
     * Handles the purchase of a storage upgrade, either for a backpack or a locker, based on the specified material type.
     * Presents the player with a confirmation menu to proceed with the upgrade.
     *
     * <p>The upgrade increases the storage size from 3 rows to 6 rows and deducts the cost from the player's balance.
     * If the player does not have sufficient balance, the transaction is canceled.</p>
     *
     * @param player The player attempting to purchase the storage upgrade.
     * @param type   The type of material representing the upgrade (either a backpack or a locker).
     */
    public void buyUpgrade(CPlayer player, Material type) {
        boolean backpack = type.equals(Material.CHEST);
        if (!backpack && !type.equals(Material.ENDER_CHEST)) return;
        new Menu(27, ChatColor.BLUE + "Confirm Upgrade", player, Arrays.asList(
                new MenuButton(4,
                        ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Storage Upgrade", Arrays.asList(
                                ChatColor.YELLOW + "3 rows ➠ 6 rows", ChatColor.GRAY + "Purchase a " + (backpack ? "backpack" : "locker"),
                                ChatColor.GRAY + "upgrade for " + ChatColor.GREEN + "$250"
                        ))
                ),
                new MenuButton(11,
                        ItemUtil.create(Material.STAINED_CLAY, ChatColor.GREEN + "Decline", 14),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.sendMessage(ChatColor.RED + "Cancelled transaction!");
                            p.closeInventory();
                        })
                ),
                new MenuButton(15,
                        ItemUtil.create(Material.STAINED_CLAY, ChatColor.GREEN + "Confirm", 13),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.sendMessage(ChatColor.GREEN + "Processing transaction...");
                            if (p.getBalance() < 250) {
                                p.sendMessage(ChatColor.RED + "You cannot afford this upgrade!");
                                p.closeInventory();
                            } else {
                                Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                                    p.addBalance(-250, (backpack ? "Backpack" : "Locker") + " Upgrade");
                                    StorageData data = (StorageData) p.getRegistry().getEntry("storageData");
                                    if (backpack) {
                                        data.setBackpackSize(StorageSize.LARGE);
                                    } else {
                                        data.setLockerSize(StorageSize.LARGE);
                                    }
                                    Core.runTask(ParkManager.getInstance(), () -> {
                                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        p.closeInventory();
                                        p.sendMessage(ChatColor.GREEN + "Successfully processed storage upgrade!");
                                        Inventory newInv = Bukkit.createInventory(null, StorageSize.LARGE.getSlots(), ChatColor.BLUE + "Your " + (backpack ? "Backpack" : "Locker"));
                                        newInv.setContents((backpack ? data.getBackpack() : data.getLocker()).getContents());
                                        StorageData newData = new StorageData(
                                                backpack ? newInv : data.getBackpack(),
                                                backpack ? StorageSize.LARGE : data.getBackpackSize(),
                                                data.getBackpackHash(),
                                                data.getDbBackpackSize(),
                                                backpack ? data.getLocker() : newInv,
                                                backpack ? data.getLockerSize() : StorageSize.LARGE,
                                                data.getLockerHash(),
                                                data.getDbLockerSize(),
                                                data.getBase(),
                                                data.getBaseHash(),
                                                data.getBuild(),
                                                data.getBuildHash()
                                        );
                                        p.getRegistry().addEntry("storageData", newData);
                                    });
                                });
                            }
                        })
                )
        )).open();
    }

    /**
     * Converts JSON strings representing inventory data into an {@link UpdateData} object.
     * This method takes JSON strings for various inventory sections, parses them into BSON arrays,
     * and packages them with their respective sizes into an {@link UpdateData} object for further processing.
     *
     * @param backpackJSON The JSON string representing the contents of the backpack inventory.
     * @param backpackSize The size (capacity) of the backpack inventory.
     * @param lockerJSON The JSON string representing the contents of the locker inventory.
     * @param lockerSize The size (capacity) of the locker inventory.
     * @param baseJSON The JSON string representing the base inventory contents.
     * @param buildJSON The JSON string representing the build inventory contents.
     *
     * @return An {@link UpdateData} object encapsulating parsed inventory data (as BSON arrays)
     *         along with their respective sizes.
     */
    public static UpdateData getDataFromJson(String backpackJSON, int backpackSize, String lockerJSON, int lockerSize, String baseJSON, String buildJSON) {
        BsonArray pack = jsonToArray(backpackJSON);
        BsonArray locker = jsonToArray(lockerJSON);
        BsonArray base = jsonToArray(baseJSON);
        BsonArray build = jsonToArray(buildJSON);

        return new UpdateData(pack, backpackSize, locker, lockerSize, base, build);
    }

    /**
     * Converts a JSON string representation of an array into a {@link BsonArray}.
     * <p>
     * This method parses the input JSON string, expecting it to represent a JSON array.
     * Each element in the JSON array is converted into a {@link BsonDocument} and added
     * to the resulting {@link BsonArray}.
     * </p>
     *
     * @param json A JSON string representing an array. This string must be properly formatted
     *             as a JSON array, otherwise the output will be an empty {@link BsonArray}.
     *             If the input string is {@code null} or empty, an empty {@link BsonArray}
     *             will be returned.
     *
     * @return A {@link BsonArray} containing the elements parsed from the input JSON array.
     *         If the input is {@code null}, empty, or not a valid JSON array, an empty
     *         {@link BsonArray} will be returned.
     */
    public static BsonArray jsonToArray(String json) {
        BsonArray array = new BsonArray();
        if (json == null || json.isEmpty()) return array;
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonArray()) {
            JsonArray baseArray = element.getAsJsonArray();

            int i = 0;
            for (JsonElement e2 : baseArray) {
                JsonObject o = e2.getAsJsonObject();
                BsonDocument item = InventoryUtil.getBsonFromJson(o.toString());
                array.add(item);
                i++;
            }
        }
        return array;
    }

    /**
     * The {@code UpdateData} class is a container for data used in updating or
     * processing operations involving BSON arrays. It holds multiple collections
     * of BSON data and their associated size attributes.
     *
     * <p>This class includes:
     * <ul>
     *   <li>Pack: A BSON array used for packing operations and its size.</li>
     *   <li>Locker: A BSON array potentially used for locking mechanisms and its size.</li>
     *   <li>Base: A BSON array representing the base data set.</li>
     *   <li>Build: A BSON array representing the build or related operational set.</li>
     * </ul>
     *
     * <p>Instances of this class are immutable and constructed with all components
     * explicitly initialized, ensuring consistency and thread safety.
     */
    @Getter
    @AllArgsConstructor
    public static class UpdateData {
        private final BsonArray pack;
        private final int packSize;
        private final BsonArray locker;
        private final int lockerSize;
        private final BsonArray base;
        private final BsonArray build;
    }
}
