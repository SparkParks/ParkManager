package network.palace.parkmanager.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.handlers.storage.ResortInventory;
import network.palace.parkmanager.handlers.storage.StorageData;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Utility class for managing inventory states and operations for players within the application.
 * The class centralizes inventory-related logic such as switching states, managing inventory
 * contents, and interacting with storage systems.
 * <p>
 * This class handles the following:
 * <ul>
 *     <li>Opening and managing specific inventory menu types (e.g., Backpack, Locker).</li>
 *     <li>Managing and transitioning between various inventory states such as Guest, Build, and Ride.</li>
 *     <li>Handling inventory and player setup on player join events.</li>
 *     <li>Extracting and formatting inventory data from external storage systems.</li>
 * </ul>
 */
public class InventoryUtil {

    /**
     * Opens a specific menu for the given player based on the provided menu type.
     *
     * <p>This method facilitates interaction with various storage-related menus such as
     * the backpack and locker. Depending on the specified {@code MenuType}, the appropriate
     * inventory is accessed and displayed to the player.
     *
     * <p><b>Behavior:</b>
     * <ul>
     *   <li>If the type is {@code BACKPACK}, the player's backpack inventory is opened.</li>
     *   <li>If the type is {@code LOCKER}, the player's locker inventory is opened.</li>
     *   <li>If the required data for the specified menu type is not found, the method returns without action.</li>
     * </ul>
     *
     * <p>This method is dependent on the {@code StorageData} entry being present in the player's registry
     * to manage and retrieve the required menu data.
     *
     * @param player the player for whom the menu is being opened. This parameter must not be {@code null}.
     * @param type the type of menu to be opened. Supported types include {@code MenuType.BACKPACK}
     *             and {@code MenuType.LOCKER}.
     */
    public void openMenu(CPlayer player, MenuType type) {
        switch (type) {
            case BACKPACK: {
                if (!player.getRegistry().hasEntry("storageData")) return;
                StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
                player.openInventory(data.getBackpack());
                break;
            }
            case LOCKER: {
                if (!player.getRegistry().hasEntry("storageData")) return;
                StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
                player.openInventory(data.getLocker());
                break;
            }
        }
    }

    /**
     * Retrieves the {@link InventoryState} of the specified player.
     * <p>
     * This method checks if the player's registry contains an entry for "inventoryState". If no such entry exists,
     * the default state {@link InventoryState#GUEST} is returned. Otherwise, the method retrieves and returns
     * the actual inventory state associated with the player.
     *
     * @param player the {@link CPlayer} whose {@link InventoryState} is to be retrieved.
     *               Must be a valid player instance with a registry.
     * @return the {@link InventoryState} of the given player. Returns {@link InventoryState#GUEST} if the
     *         inventory state is not found in the player's registry.
     */
    public InventoryState getInventoryState(CPlayer player) {
        if (!player.getRegistry().hasEntry("inventoryState")) return InventoryState.GUEST;
        return (InventoryState) player.getRegistry().getEntry("inventoryState");
    }

    /**
     * Switches the inventory state for a player, transitioning from the current state to the specified next state.
     * <p>
     * This method ensures that the player properly exits the current inventory state and enters the target inventory state.
     * It performs any necessary state-specific actions for both exiting the current state and entering the new state.
     * If the player is already in the target state, the method returns without performing any actions.
     *
     * @param player    The player whose inventory state is to be switched.
     *                  Must not be null.
     * @param nextState The target {@link InventoryState} to switch to.
     *                  Must not be null.
     */
    public void switchToState(CPlayer player, InventoryState nextState) {
        InventoryState currentState = getInventoryState(player);
        if (currentState.equals(nextState)) return;
        exitState(player, currentState);
        enterState(player, nextState);
    }

    /**
     * Updates the state of a player's inventory based on the provided {@code InventoryState}.
     * This method adjusts various player attributes and inventory contents depending on the
     * new state entered.
     *
     * <p>The states are defined as:
     * <ul>
     *   <li>{@code GUEST}: Places the player in a guest mode with restrictions and predefined items.</li>
     *   <li>{@code RIDE}: Configures the player for ride mode with a cleared inventory.</li>
     *   <li>{@code BUILD}: Grants the player creative mode and sets up their inventory for building.</li>
     * </ul>
     *
     * @param player The {@code CPlayer} whose state is being updated.
     * @param state  The {@code InventoryState} representing the new inventory state to enter.
     */
    private void enterState(CPlayer player, InventoryState state) {
        player.getRegistry().addEntry("inventoryState", state);
        PlayerInventory inv = player.getInventory();
        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
        switch (state) {
            case GUEST: {
                boolean flying = player.isFlying();
                player.setGamemode(player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId() ? GameMode.SURVIVAL : GameMode.ADVENTURE);

                if (player.getRank().getRankId() >= Rank.TRAINEE.getRankId()) {
                    player.setAllowFlight(true);
                    player.setFlying(flying);
                }

                ParkManager.getStorageManager().updateInventory(player, true);
                if (player.getHeldItemSlot() == 6) ParkManager.getTimeUtil().selectWatch(player);
                if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId())
                    Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setBuildMode(player.getUniqueId(), false));
                ParkManager.getWardrobeManager().setOutfitItems(player);
                break;
            }
            case RIDE:
                player.setGamemode(player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId() ? GameMode.SURVIVAL : GameMode.ADVENTURE);
                player.getInventory().clear();
                break;
            case BUILD: {
                player.setGamemode(GameMode.CREATIVE);
                ParkManager.getTimeUtil().unselectWatch(player);

                //Clear inventory and set basic build items
                inv.setContents(new ItemStack[]{ItemUtil.create(Material.COMPASS), ItemUtil.create(Material.WOOD_AXE)});

                ItemStack[] buildContents = data.getBuild();
                //Copy 'buildContents' items into main inventory offset by 2 for compass and WorldEdit wand
                for (int i = 0; i < buildContents.length; i++) {
                    inv.setItem(i + 2, buildContents[i]);
                }
                if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId())
                    Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setBuildMode(player.getUniqueId(), true));
                break;
            }
        }
    }

    /**
     * Handles the transition of a player exiting a specific inventory state. This method processes
     * the player's current inventory and updates the stored data to reflect items associated with the
     * state that is being exited (e.g., GUEST, RIDE, BUILD).
     *
     * <p>
     * Depending on the {@link InventoryState} being exited, specific inventory operations are executed:
     * <ul>
     *   <li><b>GUEST:</b> The player's current inventory (excluding reserved slots) is saved to
     *   the "base" inventory storage.</li>
     *   <li><b>BUILD:</b> Relevant player inventory items are stored in the "build" inventory
     *   storage.</li>
     *   <li><b>RIDE:</b> No actions are currently performed for this state.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} instance representing the player exiting the current state.
     * @param state The {@link InventoryState} indicating the inventory state the player is exiting.
     */
    private void exitState(CPlayer player, InventoryState state) {
        PlayerInventory inv = player.getInventory();
        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
        switch (state) {
            case GUEST: {
                ParkManager.getTimeUtil().unselectWatch(player);
                ItemStack[] invContents = inv.getStorageContents();
                ItemStack[] base = new ItemStack[36];
                //Store current inventory items (except reserved slots) into 'base' array
                for (int i = 0; i < invContents.length; i++) {
                    if (InventoryUtil.isReservedSlot(i)) continue;
                    base[i] = invContents[i];
                }
                data.setBase(base);
                break;
            }
            case RIDE:
                break;
            case BUILD: {
                ItemStack[] build = new ItemStack[34];
                ItemStack[] invContents = inv.getStorageContents();
                //Store current inventory items into 'build' array
                if (invContents.length - 2 >= 0) System.arraycopy(invContents, 2, build, 0, invContents.length - 2);
                data.setBuild(build);
                break;
            }
        }
    }

    /**
     * Handles the action of a player joining an inventory session in a specific state.
     * <p>
     * This method determines the player's inventory setup and behavior based on the provided
     * {@link InventoryState}. The associated inventory state is entered for the player.
     * </p>
     *
     * @param player the {@link CPlayer} instance representing the player who is joining
     *               the inventory session.
     * @param state  the {@link InventoryState} representing the state of the inventory
     *               session the player is joining, such as GUEST, RIDE, or BUILD.
     */
    public void handleJoin(CPlayer player, InventoryState state) {
        enterState(player, state);
    }

    /**
     * Determines whether a given slot index falls within the range of reserved slots.
     * <p>
     * Reserved slots are defined as those between index 5 (inclusive) and index 8 (inclusive).
     * </p>
     *
     * @param slot the index of the slot to be checked
     * @return {@code true} if the slot index is within the reserved range (5 to 8), otherwise {@code false}
     */
    public static boolean isReservedSlot(int slot) {
        return slot >= 5 && slot <= 8;
    }

    /**
     * Retrieves a {@link ResortInventory} instance by parsing the given document to extract
     * inventory details such as backpack, locker, base, and build sections.
     *
     * <p>The method processes various sections of the inventory from the {@code Document}
     * and generates corresponding JSON-like strings for each inventory type. Additionally, it computes
     * hash values for those sections and includes sizes like backpack and locker sizes in the result.
     *
     * @param uuid The unique identifier of the user associated with this inventory.
     * @param inv The {@code Document} containing the inventory information to process.
     * <ul>
     *   <li>Expected keys in the document: {@code "backpack"}, {@code "locker"}, {@code "base"}, {@code "build"}.</li>
     *   <li>Each key refers to an {@code ArrayList} of items, where each item is a {@code Document} with details like {@code type}, {@code data}, {@code amount}, and {@code tag}
     * .</li>
     *   <li>Also includes {@code backpacksize} and {@code lockersize} keys for inventory sizes.</li>
     * </ul>
     * @param resort The associated {@link Resort} for the inventory.
     * <p>This is used as a reference to associate the created inventory with a specific resort.
     *
     * @return A {@link ResortInventory} object containing parsed inventory data, including JSON-like strings
     * for four sections, their hashes, and sizes.
     */
    @SuppressWarnings("rawtypes")
    public ResortInventory getResortInventoryFromDocument(UUID uuid, Document inv, Resort resort) {
        StringBuilder backpack = new StringBuilder("[");
        ArrayList packcontents = inv.get("backpack", ArrayList.class);
        for (int i = 0; i < packcontents.size(); i++) {
            Document item = (Document) packcontents.get(i);
            if (!item.containsKey("amount") || !(item.get("amount") instanceof Integer)) {
                backpack.append("{}");
            } else {
                backpack.append("{type:'").append(item.getString("type"))
                        .append("',data:").append(item.getInteger("data"))
                        .append(",amount:").append(item.getInteger("amount"))
                        .append(",tag:'").append(item.getString("tag")).append("'}");
            }
            if (i < (packcontents.size() - 1)) {
                backpack.append(",");
            }
        }
        backpack.append("]");
        StringBuilder locker = new StringBuilder("[");
        ArrayList lockercontents = inv.get("locker", ArrayList.class);
        for (int i = 0; i < lockercontents.size(); i++) {
            Document item = (Document) lockercontents.get(i);
            if (!item.containsKey("amount") || !(item.get("amount") instanceof Integer)) {
                locker.append("{}");
            } else {
                locker.append("{type:'").append(item.getString("type"))
                        .append("',data:").append(item.getInteger("data"))
                        .append(",amount:").append(item.getInteger("amount"))
                        .append(",tag:'").append(item.getString("tag")).append("'}");
            }
            if (i < (lockercontents.size() - 1)) {
                locker.append(",");
            }
        }
        locker.append("]");
        StringBuilder base = new StringBuilder("[");
        ArrayList basecontents = inv.get("base", ArrayList.class);
        for (int i = 0; i < basecontents.size(); i++) {
            Document item = (Document) basecontents.get(i);
            if (!item.containsKey("amount") || !(item.get("amount") instanceof Integer)) {
                base.append("{}");
            } else {
                base.append("{type:'").append(item.getString("type"))
                        .append("',data:").append(item.getInteger("data"))
                        .append(",amount:").append(item.getInteger("amount"))
                        .append(",tag:'").append(item.getString("tag")).append("'}");
            }
            if (i < (basecontents.size() - 1)) {
                base.append(",");
            }
        }
        base.append("]");
        StringBuilder build = new StringBuilder("[");
        ArrayList buildcontents = inv.get("build", ArrayList.class);
        for (int i = 0; i < buildcontents.size(); i++) {
            Document item = (Document) buildcontents.get(i);
            if (!item.containsKey("amount") || !(item.get("amount") instanceof Integer)) {
                build.append("{}");
            } else {
                build.append("{type:'").append(item.getString("type"))
                        .append("',data:").append(item.getInteger("data"))
                        .append(",amount:").append(item.getInteger("amount"))
                        .append(",tag:'").append(item.getString("tag")).append("'}");
            }
            if (i < (buildcontents.size() - 1)) {
                build.append(",");
            }
        }
        build.append("]");
        int backpacksize = inv.getInteger("backpacksize");
        int lockersize = inv.getInteger("lockersize");
        return new ResortInventory(resort, backpack.toString(), generateHash(backpack.toString()), "", backpacksize,
                locker.toString(), generateHash(locker.toString()), "", lockersize,
                base.toString(), generateHash(base.toString()), "",
                build.toString(), generateHash(build.toString()), "");
    }

    /**
     * Generates a hashed string using the MD5 algorithm for the given inventory data.
     * If the input inventory is null, it will default to an empty string.
     *
     * <p>
     * The resulting hash is returned as a lowercase hexadecimal string.
     * In case of an error during the hashing process, logs the error and returns "null".
     * </p>
     *
     * @param inventory the inventory data to hash as a string. If null, it is treated as an empty string.
     * @return the MD5 hash of the inventory data in lowercase hexadecimal format.
     *         Returns "null" in case of an error.
     */
    private String generateHash(String inventory) {
        if (inventory == null) {
            inventory = "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(inventory.getBytes());
            return DatatypeConverter.printHexBinary(digest.digest()).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error generaing inventory hash", e);
            return "null";
        }
    }

    /**
     * Converts a JSON formatted string into a {@link BsonDocument}.
     * <p>
     * This method parses a JSON string, extracts specific fields, and constructs a BSON document
     * with the extracted values. If the input JSON does not contain the required "type" field,
     * an empty BSON document is returned. If parsing fails, the method logs an error and returns null.
     * </p>
     *
     * @param json A JSON formatted string. The input must include the following fields:
     *             <ul>
     *                <li><strong>type</strong>: A string indicating the type (required).</li>
     *                <li><strong>data</strong>: An integer value (required).</li>
     *                <li><strong>amount</strong>: An integer value (required).</li>
     *                <li><strong>tag</strong>: An optional string value.</li>
     *             </ul>
     *             If the "tag" field is absent, an empty string is used in its place.
     *
     * @return A {@link BsonDocument} representation of the parsed input JSON.
     *         Returns an empty BSON document if the "type" field is missing.
     *         Returns {@code null} if the JSON string cannot be parsed.
     */
    public static BsonDocument getBsonFromJson(String json) {
        JsonObject o = new JsonParser().parse(json).getAsJsonObject();
        if (!o.has("type")) {
            return new BsonDocument();
        }
        BsonDocument doc;
        try {
            doc = new BsonDocument("type", new BsonString(o.get("type").getAsString()))
                    .append("data", new BsonInt32(o.get("data").getAsInt()))
                    .append("amount", new BsonInt32(o.get("amount").getAsInt()))
                    .append("tag", o.get("tag") == null ? new BsonString("") : new BsonString(o.get("tag").getAsString()));
        } catch (IllegalArgumentException e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error converting Json to Bson", e);
            return null;
        }
        return doc;
    }

    /**
     * The {@code InventoryState} enum represents the various states of an inventory the user can interact with.
     *
     * <p>
     * This enumeration is used to define the context in which the inventory operates, and it can take one of
     * the following states:
     * <ul>
     *   <li>{@code GUEST} - Indicates the inventory state when in a guest mode or context.</li>
     *   <li>{@code RIDE} - Indicates the inventory state during a ride-related context.</li>
     *   <li>{@code BUILD} - Indicates the inventory state when in a building or construction context.</li>
     * </ul>
     * <p>
     * Each state defines a specific interaction or behavior of the inventory system.
     */
    public enum InventoryState {
        GUEST, RIDE, BUILD
    }
}
