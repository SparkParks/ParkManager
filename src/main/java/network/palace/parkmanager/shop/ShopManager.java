package network.palace.parkmanager.shop;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopItem;
import network.palace.parkmanager.handlers.shop.ShopOutfit;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles the management of shops, including loading, saving, and providing various operations
 * such as adding, removing, and retrieving shops. Also supports interaction with shop inventories
 * and purchasing items or outfits using in-game currency.
 *
 * <p>This class interacts with various subsystems and utilizes JSON configuration files to store
 * and retrieve shop data. It provides functionality for different operations related to shops and
 * their items or outfits.
 *
 * <p>Shops are associated with specific parks, and each shop contains unique items and outfits
 * available for purchase by players.
 */
@SuppressWarnings("unchecked")
public class ShopManager {
    /**
     * A list that stores all the available shops managed by the {@code ShopManager}.
     * <p>
     * This list holds instances of {@code Shop}, allowing various operations such as
     * adding, removing, and retrieving shops to be performed. The shops in this collection may
     * correspond to specific parks and can be filtered or accessed based on associated criteria.
     * <p>
     * Key functionalities supported by this field include:
     * <ul>
     *   <li>Initialization of shop data</li>
     *   <li>Storage and management of shop instances</li>
     *   <li>Interaction with specific shops via implemented methods</li>
     * </ul>
     *
     * <p>This field is initialized as an empty {@code ArrayList} by default.
     */
    private List<Shop> shops = new ArrayList<>();

    /**
     * Constructs a new instance of {@code ShopManager} and initializes the shop management system.
     *
     * <p>This constructor is responsible for setting up the shop system by calling the internal
     * {@code initialize} method. The initialization process prepares the necessary data structures
     * and loads shop configurations from storage, ensuring that all shops and associated data
     * are properly loaded for use within the system.</p>
     */
    public ShopManager() {
        initialize();
    }

    /**
     * Initializes and loads the shop management system for all parks.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *     <li>Clears the existing list of shops to ensure a fresh state.</li>
     *     <li>Obtains the "shop" file subsystem from the file utility. If the subsystem does not exist, it registers a new one.</li>
     *     <li>Iterates through all parks retrieved from the park utility.</li>
     *     <li>For each park:
     *         <ul>
     *             <li>Attempts to read the shop configuration file from the file subsystem.</li>
     *             <li>Parses the JSON configuration to load individual shop data, including:
     *                 <ul>
     *                     <li>Shop ID</li>
     *                     <li>Items available in the shop, along with their cost and currency type</li>
     *                     <li>Outfits available in the shop, linking to their respective configurations</li>
     *                 </ul>
     *             </li>
     *             <li>Logs the number of shops loaded for each park.</li>
     *             <li>Handles any errors during the loading process and logs appropriate error messages.</li>
     *         </ul>
     *     </li>
     *     <li>Saves the shop data to the file system.</li>
     * </ul>
     *
     * <p>This method ensures that all shops and related data are properly initialized and ready for use.</p>
     *
     * <p>Note: Any previous shop data will be cleared and replaced with the data loaded from the configuration files.</p>
     */
    public void initialize() {
        // clear the array list to start fresh
        shops.clear();
        // this is the sub system to find the shop file folder
        FileUtil.FileSubsystem subsystem;
        // if the subsystem registered contains "shop," then get the subsystem called "shop"
        if (ParkManager.getFileUtil().isSubsystemRegistered("shop")) {
            // set the subsystem equaled to "shop"
            subsystem = ParkManager.getFileUtil().getSubsystem("shop");
        } else { // if it does not contain it, then register it
            // register the "shop" subsystem
            subsystem = ParkManager.getFileUtil().registerSubsystem("shop");
        }
        // for every park in the parks config file created from ParkUtil, try to get the shops from the parks
        for (Park park : ParkManager.getParkUtil().getParks()) {
            try {
                // get the park name element to all lowercase
                JsonElement element = subsystem.getFileContents(park.getId().name().toLowerCase());
                // if the element is a JSON array, then create a new shop
                if (element.isJsonArray()) {
                    // setting the element as a JSON array
                    JsonArray array = element.getAsJsonArray();
                    // for every JSON entry in the array, get the object id and warp location as a String
                    for (JsonElement entry : array) {
                        // the JSON object acting as the entry serving as a JSON object
                        JsonObject object = entry.getAsJsonObject();
                        // the JSON ID
                        String id;
                        // if the object contains "id," then set the ID variable to the ID in the object
                        if (object.has("id")) {
                            // set the String id to the id from the object
                            id = object.get("id").getAsString();
                        } else { // if it is not in the object, set id to the warp as a String
                            id = object.get("warp").getAsString().toLowerCase();
                        }

                        // get the shop items from the object as a JSON Array
                        JsonArray shopItems = object.getAsJsonArray("items");
                        // create a new array list of shop items
                        List<ShopItem> items = new ArrayList<>();
                        int nextId = 0;
                        // for every item element in the JSON Array, shopItems, add it to the items Array list
                        for (JsonElement itemElement : shopItems) {
                            // create a item object serving as the item element
                            JsonObject itemObject = (JsonObject) itemElement;
                            // add a new shop item to the `items` array
                            items.add(new ShopItem(nextId++, ItemUtil.getItemFromJsonNew(itemObject.getAsJsonObject("item").toString()),
                                    itemObject.get("cost").getAsInt(),
                                    CurrencyType.fromString(itemObject.get("currency").getAsString())));
                        }

                        JsonArray shopOutfits = object.getAsJsonArray("outfits");
                        List<ShopOutfit> outfits = new ArrayList<>();
                        nextId = 0;
                        for (JsonElement outfitElement : shopOutfits) {
                            JsonObject outfitObject = (JsonObject) outfitElement;
                            Outfit outfit = ParkManager.getWardrobeManager().getOutfit(outfitObject.get("outfit-id").getAsInt());
                            if (outfit != null) {
                                outfits.add(new ShopOutfit(nextId++, outfitObject.get("outfit-id").getAsInt(), outfitObject.get("cost").getAsInt()));
                            }
                        }

                        shops.add(new Shop(id, park.getId(), ChatColor.translateAlternateColorCodes('&', object.get("name").getAsString()),
                                object.get("warp").getAsString(), ItemUtil.getItemFromJsonNew(object.getAsJsonObject("item").toString()), items, outfits));
                    }
                }
                Core.logMessage("ShopManager", "Loaded " + shops.size() + " shop" + TextUtil.pluralize(shops.size()) + " for park " + park.getId().getTitle() + "!");
            } catch (IOException e) {
                Core.logMessage("ShopManager", "There was an error loading the ShopManager config for park " + park.getId().getTitle() + "!");
                e.printStackTrace();
            }
        }
        saveToFile();
    }

    /**
     * Retrieves a list of shops filtered by the specified park type.
     *
     * <p>This method returns shops that are associated with the given {@code park} parameter,
     * allowing clients to filter and access shops specific to a particular park.</p>
     *
     * @param park The {@link ParkType} representing the park for which the shops need to be retrieved.
     *             It is used to filter the shops based on their park association.
     * @return A {@link List} of {@link Shop} objects that belong to the specified park.
     *         Returns an empty list if no shops match the given park type.
     */
    public List<Shop> getShops(ParkType park) {
        return shops.stream().filter(shop -> shop.getPark().equals(park)).collect(Collectors.toList());
    }

    /**
     * Retrieves a shop by its unique identifier within a specified park.
     *
     * <p>This method searches through the list of shops associated with the provided park type
     * and returns the shop that matches the given ID. If no shop is found with the specified
     * ID, the method returns {@code null}.
     *
     * @param id The unique identifier of the shop to be retrieved. This value must not be {@code null}.
     * @param park The park type used to retrieve the shop. This determines the scope of the search.
     *             Must not be {@code null}.
     * @return The {@link Shop} object matching the specified ID within the given park, or {@code null}
     *         if no such shop exists.
     */
    public Shop getShopById(String id, ParkType park) {
        for (Shop shop : getShops(park)) {
            if (shop.getId().equals(id)) {
                return shop;
            }
        }
        return null;
    }

    /**
     * Retrieves a shop by its name from the specified park.
     *
     * <p>This method searches through all shops available in the given park
     * and returns the first shop whose name contains the specified search string.
     * If no matching shop is found, the method returns <code>null</code>.</p>
     *
     * @param s the name or partial name of the shop to search for.
     *          This is a case-sensitive search and must not be <code>null</code>.
     * @param park the park in which to search for the shop. It determines
     *             the context of the search among the shops available
     *             for that specific park.
     * @return the shop matching the specified name search string, or <code>null</code>
     *         if no such shop is found.
     */
    public Shop getShopByName(String s, ParkType park) {
        for (Shop shop : getShops(park)) {
            if (shop.getName().contains(s)) {
                return shop;
            }
        }
        return null;
    }

    /**
     * Adds a new shop to the shop management system.
     *
     * <p>This method appends the provided {@code Shop} object to the internal list of shops
     * managed by this system and persists the changes by saving the updated shop data to a file.</p>
     *
     * <p>It ensures that new shops can be dynamically added to the system, allowing for
     * subsequent retrieval, edits, and usage within the application.</p>
     *
     * @param shop the {@code Shop} instance to be added to the shop management system.
     *             <ul>
     *                 <li>The {@code Shop} instance should be properly constructed and contain valid
     *                     data such as shop ID and the items it offers.</li>
     *                 <li>It must not duplicate the ID of an existing shop in the system to avoid conflicts.</li>
     *             </ul>
     */
    public void addShop(Shop shop) {
        shops.add(shop);
        saveToFile();
    }

    /**
     * Removes a shop identified by its unique ID from the specified park.
     *
     * <p>This method searches for a shop with the given {@code id} within the {@code park}.
     * If the shop is found, it is removed from the internal list of shops, and the changes
     * are persisted by saving the updated data to a file. If no shop with the specified ID
     * exists, the method returns {@code false}.</p>
     *
     * @param id The unique identifier of the shop to be removed. Must not be {@code null}.
     * @param park The {@link ParkType} from which the shop is to be removed. Determines the
     *             context in which the shop will be searched.
     * @return {@code true} if the shop was successfully removed; {@code false} if no shop
     *         with the given ID is found in the specified park or removal fails.
     */
    public boolean removeShop(String id, ParkType park) {
        Shop shop = getShopById(id, park);
        if (shop == null) return false;
        shops.remove(shop);
        saveToFile();
        return true;
    }

    /**
     * Opens the inventory interface for a specified shop and populates it with the shop's items and outfits.
     *
     * <p>This method is responsible for creating and displaying a graphical interface for the player,
     * where they can browse and potentially purchase items or outfits from the specified shop.
     * The inventory UI includes features such as a divider between items and outfits (if both are present)
     * and includes relevant item metadata, such as cost and currency type.</p>
     *
     * <ul>
     * <li>Shop items are displayed first, followed by an optional divider, and then outfits.</li>
     * <li>The number of displayed items and outfits is capped to prevent overcrowding in the UI.</li>
     * <li>Allows the player to interact with items and outfits to confirm purchases.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} instance representing the player for whom the shop inventory
     *               is being opened. Must not be {@code null}.
     * @param shop   The {@link Shop} containing the items and outfits to display in the inventory.
     *               Must not be {@code null}.
     */
    public void openShopInventory(CPlayer player, Shop shop) {
        List<MenuButton> buttons = new ArrayList<>();
        List<ShopItem> shopItems = shop.getItems();
        List<ShopOutfit> shopOutfits = shop.getOutfits();

        boolean divider = !shopItems.isEmpty() && !shopOutfits.isEmpty();
        int pos = 0;

        int itemSize = 9;
        for (ShopItem shopItem : shopItems) {
            ItemStack item = shopItem.getItem();
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.setLore(Arrays.asList("", ChatColor.YELLOW + "Cost: " + shopItem.getCurrencyType().getIcon() + shopItem.getCost()));
            item.setItemMeta(meta);

            if (pos != 0 && pos % 9 == 0) itemSize += 9;

            buttons.add(new MenuButton(pos++, item, ImmutableMap.of(ClickType.LEFT, p -> openConfirmItemPurchase(p, item, shopItem.getCost(), shopItem.getCurrencyType()))));
            if (itemSize > 27) {
                itemSize = 27;
                break;
            }
        }

        if (divider) {
            ItemStack dividerItem = ItemUtil.create(Material.STAINED_GLASS_PANE, ChatColor.RESET + "");
            pos = itemSize;
            for (int i = 0; i < 9; i++) {
                buttons.add(new MenuButton(pos++, dividerItem));
            }
        }

        int outfitSize = 9;
        int initialPos = pos;
        for (ShopOutfit shopOutfit : shopOutfits) {
            int outfitId = shopOutfit.getOutfitId();
            Outfit outfit = ParkManager.getWardrobeManager().getOutfit(outfitId);
            if (outfit == null) continue;

            ItemStack item = outfit.getHead().clone();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(outfit.getName());
            meta.setLore(Arrays.asList("", ChatColor.YELLOW + "Cost: " + CurrencyType.TOKENS.getIcon() + shopOutfit.getCost()));
            item.setItemMeta(meta);

            if (pos != initialPos && pos % 9 == 0) outfitSize += 9;

            buttons.add(new MenuButton(pos++, item, ImmutableMap.of(ClickType.LEFT, p -> openConfirmOutfitPurchase(p, item, outfitId, shopOutfit.getCost()))));
            if (outfitSize > 18) {
                outfitSize = 18;
                break;
            }
        }

        int size = itemSize + outfitSize + (divider ? 9 : 0);
        new Menu(size, shop.getName(), player, buttons).open();
    }

    /**
     * Opens a confirmation menu for purchasing a specific outfit for a player.
     * The method verifies if the player already owns the outfit or has sufficient tokens
     * to make the purchase. It provides options to confirm or decline the purchase.
     *
     * <p>
     * The confirmation menu contains three main buttons:
     * <ul>
     *     <li>A display button for the outfit being purchased.</li>
     *     <li>A decline button, allowing the player to cancel the purchase.</li>
     *     <li>A confirm button to finalize the purchase, deduct tokens, and register the outfit ownership.</li>
     * </ul>
     *
     * @param player   The {@code CPlayer} instance representing the player attempting the purchase.
     * @param item     The {@code ItemStack} representing the visual representation of the outfit.
     * @param outfitId The unique ID of the outfit to be purchased.
     * @param cost     The token cost required to purchase the outfit.
     */
    private void openConfirmOutfitPurchase(CPlayer player, ItemStack item, int outfitId, int cost) {
        List<Integer> currentPurchases = (List<Integer>) player.getRegistry().getEntry("outfitPurchases");
        if (currentPurchases.contains(outfitId)) {
            player.sendMessage(ChatColor.RED + "You already own this outfit!");
            return;
        }

        int tokens = player.getTokens();
        if (tokens < cost) {
            player.sendMessage(ChatColor.RED + "You cannot afford that outfit!");
            return;
        }

        List<MenuButton> buttons = Arrays.asList(
                new MenuButton(4, item),
                new MenuButton(11, ItemUtil.create(Material.CONCRETE, ChatColor.RED + "Decline Purchase", 14),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.closeInventory();
                            p.sendMessage(ChatColor.RED + "You cancelled the purchase");
                        })),
                new MenuButton(15, ItemUtil.create(Material.CONCRETE, 1, 13,
                        ChatColor.GREEN + "Confirm Purchase", Arrays.asList(ChatColor.GRAY + "You agree you will buy",
                                ChatColor.GRAY + "this shop outfit for " + ChatColor.AQUA + CurrencyType.TOKENS.getIcon() + cost)),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.closeInventory();
                            p.sendMessage(ChatColor.GREEN + "Processing your payment...");
                            Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                                p.removeTokens(cost, "Park Store " + Core.getInstanceName());
                                p.sendMessage(ChatColor.GREEN + "Payment has been processed!");
                                List<Integer> purchases = (List<Integer>) player.getRegistry().getEntry("outfitPurchases");
                                purchases.add(outfitId);
                                Core.getMongoHandler().purchaseOutfit(p.getUniqueId(), outfitId);
                            });
                        }))
        );

        new Menu(27, ChatColor.BLUE + "Confirm Purchase", player, buttons).open();
    }

    /**
     * Opens a confirmation menu to allow the player to confirm the purchase
     * of an item from the store. The method ensures that the player has enough
     * funds and an available slot in their inventory before proceeding with the purchase.
     *
     * <p>If the player confirms the purchase, the item is added to their inventory,
     * and the specified cost is deducted from their balance or tokens, depending
     * on the currency type.</p>
     *
     * @param player         The player attempting to purchase the item.
     * @param item           The item the player is attempting to purchase.
     * @param cost           The cost of the item, specified in the selected currency type.
     * @param currencyType   The currency type used for the transaction (e.g., balance or tokens).
     */
    private void openConfirmItemPurchase(CPlayer player, ItemStack item, int cost, CurrencyType currencyType) {
        boolean openSlot = false;
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < 5; i++) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                openSlot = true;
                break;
            }
        }

        if (!openSlot) {
            player.sendMessage(ChatColor.RED + "You don't have an open inventory slot for this item! Clear up some space in your hotbar before you buy it.");
            return;
        }

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        meta.setLore(Collections.emptyList());
        item.setItemMeta(meta);

        if (currencyType.equals(CurrencyType.BALANCE)) {
            int balance = player.getBalance();
            if (balance < cost) {
                player.sendMessage(ChatColor.RED + "You cannot afford that item!");
                return;
            }
        } else {
            int tokens = player.getTokens();
            if (tokens < cost) {
                player.sendMessage(ChatColor.RED + "You cannot afford that item!");
                return;
            }
        }

        List<MenuButton> buttons = Arrays.asList(
                new MenuButton(4, item),
                new MenuButton(11, ItemUtil.create(Material.CONCRETE, ChatColor.RED + "Decline Purchase", 14),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.closeInventory();
                            p.sendMessage(ChatColor.RED + "You cancelled the purchase");
                        })),
                new MenuButton(15, ItemUtil.create(Material.CONCRETE, 1, 13,
                        ChatColor.GREEN + "Confirm Purchase", Arrays.asList(ChatColor.GRAY + "You agree you will buy",
                                ChatColor.GRAY + "this shop item for " + ChatColor.AQUA + currencyType.getIcon() + cost)),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.closeInventory();
                            p.sendMessage(ChatColor.GREEN + "Processing your payment...");
                            Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                                if (currencyType.equals(CurrencyType.BALANCE)) {
                                    p.removeBalance(cost, "Park Store " + Core.getInstanceName());
                                } else {
                                    p.removeTokens(cost, "Park Store " + Core.getInstanceName());
                                }
                                p.sendMessage(ChatColor.GREEN + "Payment has been processed!");
                                Core.runTask(ParkManager.getInstance(), () -> p.getInventory().addItem(item));
                            });
                        }))
        );

        new Menu(27, ChatColor.BLUE + "Confirm Purchase", player, buttons).open();
    }

    /**
     * Saves the current state of shops to individual JSON configuration files, categorized by parks.
     * <p>
     * The method performs the following operations:
     * <lu>
     *   <li>Sorts the list of shops alphabetically by their name (ignoring case and color codes).</li>
     *   <li>Iterates over all parks retrieved from the park utility.</li>
     *   <li>For each park, filters the list of shops associated with the park and constructs
     *       a JSON representation of the shop's data, including:
     *       <ul>
     *         <li>Shop name and warp location.</li>
     *         <li>Details about the shop's main item.</li>
     *         <li>List of available shop items, including each item's cost and currency.</li>
     *         <li>List of shop outfits, including each outfit's ID and cost.</li>
     *       </ul>
     *   </li>
     *   <li>Writes the generated JSON data for each park to its corresponding file under the "shop" subsystem.</li>
     * </lu>
     * <p>
     * <b>File Names:</b> The files are named using the lowercase representation of each park ID.
     * </p>
     *
     * <p>
     * If an error occurs during the file writing process, an error message is logged, and the exception stack trace
     * is printed to the console.
     * </p>
     */
    public void saveToFile() {
        shops.sort(Comparator.comparing(o -> ChatColor.stripColor(o.getName().toLowerCase())));
        for (Park park : ParkManager.getParkUtil().getParks()) {
            JsonArray array = new JsonArray();
            shops.stream().filter(shop -> shop.getPark().equals(park.getId())).forEach(shop -> {
                JsonObject object = new JsonObject();
                object.addProperty("name", shop.getName());
                object.addProperty("warp", shop.getWarp());
                object.add("item", ItemUtil.getJsonFromItemNew(shop.getItem()));

                JsonArray items = new JsonArray();
                for (ShopItem item : shop.getItems()) {
                    JsonObject shopItem = new JsonObject();
                    shopItem.add("item", ItemUtil.getJsonFromItemNew(item.getItem()));
                    shopItem.addProperty("cost", item.getCost());
                    shopItem.addProperty("currency", item.getCurrencyType().name().toLowerCase());
                    items.add(shopItem);
                }
                object.add("items", items);

                JsonArray outfits = new JsonArray();
                for (ShopOutfit outfit : shop.getOutfits()) {
                    JsonObject shopOutfit = new JsonObject();
                    shopOutfit.addProperty("outfit-id", outfit.getOutfitId());
                    shopOutfit.addProperty("cost", outfit.getCost());
                    outfits.add(shopOutfit);
                }
                object.add("outfits", outfits);

                array.add(object);
            });
            try {
                ParkManager.getFileUtil().getSubsystem("shop").writeFileContents(park.getId().name().toLowerCase(), array);
            } catch (IOException e) {
                Core.logMessage("ShopManager", "There was an error writing to the ShopManager config!");
                e.printStackTrace();
            }
        }
    }
}
