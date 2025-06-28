package network.palace.parkmanager.magicband;

import com.google.common.collect.ImmutableMap;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.HeadUtil;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.food.FoodLocation;
import network.palace.parkmanager.handlers.*;
import network.palace.parkmanager.handlers.magicband.BandType;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.storage.StorageData;
import network.palace.parkmanager.handlers.storage.StorageSize;
import network.palace.parkmanager.queues.Queue;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import network.palace.parkmanager.utils.VisibilityUtil;
import org.apache.commons.lang.WordUtils;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;

/**
 * <p>The <code>MagicBandManager</code> class is responsible for managing the functionality and
 * customization of MagicBands in a theme park system. MagicBands provide players with
 * access to various in-game features such as profiles, customizations, and attraction menus.</p>
 *
 * <p>This class includes methods for handling MagicBand interactions, customization,
 * and inventory management:</p>
 *
 * <ul>
 *   <li>Opening and managing MagicBand inventories.</li>
 *   <li>Handling customization features such as type, name color, and material.</li>
 *   <li>Managing player interactions with theme park features through the MagicBand.</li>
 *   <li>Fetching and generating MagicBand-related inventory items or components.</li>
 * </ul>
 *
 * <p>The MagicBandManager integrates with various in-game aspects such as ride counters,
 * park menus, and customization interfaces to provide a seamless player experience.</p>
 *
 * <h3>Methods Overview</h3>
 * <ul>
 *   <li><b><code>openInventory(CPlayer player, BandInventory inventory)</code></b>
 *     <p>Opens a MagicBand inventory for a given player based on the inventory type.</p>
 *   </li>
 *   <li><b><code>currentParkOrOpenParkMenu(CPlayer player)</code></b>
 *     <p>Determines the park a player is currently in. If the player is not in a park, this method
 *     opens the park selection menu.</p>
 *   </li>
 *   <li><b><code>openRideCounterPage(CPlayer player, int page)</code></b>
 *     <p>Opens the ride counter screen for the specified player, displaying data for the given page number.</p>
 *   </li>
 *   <li><b><code>handleJoin(CPlayer player, Document doc)</code></b>
 *     <p>Handles player initialization when joining, with functionality such as loading their MagicBand settings.</p>
 *   </li>
 *   <li><b><code>getMagicBandItem(CPlayer player)</code></b>
 *     <p>Returns an <code>ItemStack</code> representing the MagicBand assigned to the specified player.</p>
 *   </li>
 *   <li><b><code>getMagicBandItem(String type, String color)</code></b>
 *     <p>Generates and returns a <code>ItemStack</code> MagicBand based on the specified type and color.</p>
 *   </li>
 *   <li><b><code>getBackButton(int slot, BandInventory inv)</code></b>
 *     <p>Creates a back button for MagicBand inventory menus, assigning it to the specified slot.</p>
 *   </li>
 * </ul>
 *
 * <p>Private helper methods are utilized for additional functionalities such as setting
 * MagicBand type, name color, and material.</p>
 */
@SuppressWarnings("DuplicatedCode")
public class MagicBandManager {

    /**
     * Opens a specified inventory for the given player in the game.
     * <p>
     * This method displays various interactive menus and allows users to navigate
     * through different options such as managing profiles, finding food locations,
     * viewing attractions, customizing items, and much more.
     * </p>
     * <ul>
     *     <li>Handles different inventory types like {@code MAIN}, {@code FOOD}, {@code SHOWS}, etc.</li>
     *     <li>Dynamically updates menu content based on player information, game state,
     *         and park-specific features.</li>
     *     <li>Includes interactive features such as toggling visibility, joining virtual queues,
     *         and upgrading storage.</li>
     * </ul>
     *
     * @param player   The {@link CPlayer} object representing the player for whom the inventory is being opened. Cannot be {@code null}.
     * @param inventory The {@link BandInventory} enumeration specifying the type of inventory to open.
     *                  Supported values include {@code MAIN}, {@code FOOD}, {@code SHOWS}, and others,
     *                  each corresponding to a unique menu or feature.
     */
    public void openInventory(CPlayer player, BandInventory inventory) {
        // switch the BandInventory variable to a specific enumeration group member
        switch (inventory) {
            case MAIN: { // should the inventory be MAIN
                // get the player's visibility settings from the VisibilityUtil
                VisibilityUtil.Setting setting = ParkManager.getVisibilityUtil().getSetting(player);
                // get the color of the setting based on what it is set to:
                // (All visible, only staff/friends, only friends, or all hidden)
                ChatColor color = setting.getColor();

                // get the player's head from the utility file method to act as the item to showcase a
                // player's profile item lore
                ItemStack profile = HeadUtil.getPlayerHead(player.getTextureValue(), ChatColor.AQUA + "My Profile");
                // get the item meta of the Item serving as the player head
                ItemMeta meta = profile.getItemMeta();
                // set the lore (item description) to loading -- of which data will be gathered
                // from the player's MongoDB Database document
                meta.setLore(Arrays.asList("", ChatColor.GREEN + "Loading...", ""));
                // set the profile item meta to what we have created (meta variable with the lore added)
                profile.setItemMeta(meta);

                // this is the list of buttons players will see when they open their magic band to the main menu
                List<MenuButton> buttons = new ArrayList<>(Arrays.asList(
                        // this is the player's profile, and the item is the player's head we previously defined
                        new MenuButton(4, profile, ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PROFILE))),
                        // this is a button to help players find a food source in the park or server they are in
                        new MenuButton(10, ItemUtil.create(Material.POTATO_ITEM, ChatColor.AQUA + "Find Food",
                                Arrays.asList(ChatColor.GREEN + "Visit a restaurant", ChatColor.GREEN + "to get some food!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.FOOD))),
                        // this is a button to open the menu to show upcoming show times and events for the server,
                        // such as the next showing of Happily Ever After fireworks
                        new MenuButton(11, ItemUtil.create(Material.FIREWORK, ChatColor.AQUA + "Shows and Events",
                                Arrays.asList(ChatColor.GREEN + "Watch stage shows, nighttime", ChatColor.GREEN + "spectaculars, and much more!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SHOWS))),
                        // this button will take players to the menu page of all available attractions in the server or park
                        new MenuButton(12, ItemUtil.create(Material.MINECART, ChatColor.AQUA + "Attractions",
                                Arrays.asList(ChatColor.GREEN + "View all of our available", ChatColor.GREEN + "theme park attractions!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.ATTRACTION_MENU))),
                        // this button will take players to the menu to select a new park to warp to
                        new MenuButton(13, ItemUtil.create(Material.NETHER_STAR, ChatColor.AQUA + "Park Menu",
                                Arrays.asList(ChatColor.GREEN + "Travel to another one", ChatColor.GREEN + "of our theme parks!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PARKS))),
                        // this button will bring players to the SHOP menu to purchase in-game/server merchandise, etc.
                        new MenuButton(14, ItemUtil.create(Material.GOLD_BOOTS, ChatColor.AQUA + "Shop",
                                Arrays.asList(ChatColor.GREEN + "Purchase souveniers and", ChatColor.GREEN + "all kinds of collectibles!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SHOP))),
                        // this button will bring players to the menu to change their outfit/wardrobe
                        new MenuButton(15, ItemUtil.create(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Wardrobe Manager",
                                Arrays.asList(ChatColor.GREEN + "Change your outfit to make you", ChatColor.GREEN + "look like your favorite characters!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.WARDROBE))),
                        // this button, when clicked, will allow players to set other Guest visibility to
                        // show them or hide them. If they left-click the button instead right-clicking,
                        // it takes players to a new menu for more visibility options. Once set to a player's liking,
                        // they are brought back to the main menu of their magicband
                        new MenuButton(16, ItemUtil.create(setting.getBlock(), 1, setting.getData(), ChatColor.AQUA + "Guest Visibility " +
                                        ChatColor.GOLD + "➠ " + setting.getColor() + setting.getText(),
                                Arrays.asList(ChatColor.YELLOW + "Right-Click to " + (setting.equals(VisibilityUtil.Setting.ALL_HIDDEN) ? "show" : "hide") + " all players",
                                        ChatColor.YELLOW + "Left-Click for more options")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.VISIBILITY), ClickType.RIGHT, p -> {
                                    if (ParkManager.getVisibilityUtil().toggleVisibility(player)) {
                                        openInventory(p, BandInventory.MAIN);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 2);
                                    }
                                })),
                        // this button, when clicked, will take players to a menu to expand their storage space to
                        // gain more item slots to store items they get from in-game/server shops, etc.
                        new MenuButton(6, ItemUtil.create(Material.CHEST, ChatColor.AQUA + "Storage Upgrade",
                                Arrays.asList(ChatColor.GREEN + "Expand the space available", ChatColor.GREEN + "in your backpack and locker")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.STORAGE_UPGRADE)))
                ));

                // if there are virtual queues, then add a new item (a Book) to the main menu to allow players to click
                // and see what virtual queues are available to join
                if (!ParkManager.getVirtualQueueManager().getQueues().isEmpty()) {
                    // create the item to act as the button for players to click on
                    ItemStack queueBook = ItemUtil.create(Material.BOOK, ChatColor.GREEN + "Virtual Queues",
                            Arrays.asList(ChatColor.AQUA + "There's a virtual queue to join!", ChatColor.YELLOW + "Click to see what's available!"));
                    // add an effect to the item
                    queueBook.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    // add the item, as a button, to the ArrayList of buttons for the main menu
                    buttons.add(new MenuButton(0, queueBook, ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.VIRTUAL_QUEUES))));
                }

                // if the player is not at Universal Studios Resort,
                // then add a button for players to customize their Disney MagicBand
                if (!ParkManager.getResort().equals(Resort.USO)) {
                    // get the player's magic band item
                    ItemStack band = getMagicBandItem(player);
                    // get the item's meta
                    meta = band.getItemMeta();
                    // set the display name for the item to equal what the player can do when they click on it
                    meta.setDisplayName(ChatColor.AQUA + "Customize your MagicBand");
                    // set the lore (description) of the item on what the player can customize for their MagicBand
                    meta.setLore(Arrays.asList("", ChatColor.GREEN + "Choose from a variety of MagicBand",
                            ChatColor.GREEN + "designs and customize the color",
                            ChatColor.GREEN + "of the name for your MagicBand!"));
                    // Hide all item flags for the player's magic band item
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    // set the band's meta to what was set above this comment
                    band.setItemMeta(meta);
                    // add the band as a new button on the main menu gui when player's open the magic band main menu
                    buttons.add(new MenuButton(22, band, ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.CUSTOMIZE_BAND))));
                }

                // create the inventory GUI with the size being 27 slots, the title being "Your MagicBand" or
                // "Your Power Pass", the player associated with the new menu,
                // and finally add in the list of buttons created for the main menu the players will first see
                Menu menu = new Menu(27, ChatColor.BLUE + "Your " + (ParkManager.getResort().equals(Resort.USO) ? "Power Pass" : "MagicBand"), player, buttons);
                // if a player's rank ID  is greater than or equal to PASSPORT,
                // then add a new button for those players to change the time in-game to what it actually is in real life
                if (player.getRank().getRankId() >= Rank.PASSPORT.getRankId()) {
                    // set a new button to be added for players to click to change from going
                    // through in-game time to real life time based on the park they are in
                    menu.setButton(new MenuButton(2, ItemUtil.create(Material.WATCH, ChatColor.AQUA + "Player Time",
                            Arrays.asList(ChatColor.GREEN + "Change the time of day you see", ChatColor.GREEN + "for the park you're currently in!")),
                            ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PLAYER_TIME))));
                } else { // if they are not eqal to or above PASSPORT, have the same button
                    // but create a formatted message for players telling them they can purchase this ability using /store
                    menu.setButton(new MenuButton(2, ItemUtil.create(Material.WATCH, ChatColor.AQUA + "Player Time",
                            Arrays.asList(ChatColor.GREEN + "Purchase " + Rank.PASSPORT.getFormattedName() + ChatColor.GREEN + "at",
                                    ChatColor.YELLOW + "/store" + ChatColor.GREEN + "to use this!"))));
                }
                // open the new menu when the player right click's their magicband item in their inventory hot bar
                menu.open();
                // run an asynchronous task to load in the profile data based on who the player is from the CPlayer methods
                Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                    // clone the profile item previosuly created
                    ItemStack updatedProfile = profile.clone();
                    // get the item clone's meta
                    ItemMeta menuMeta = updatedProfile.getItemMeta();
                    // add in the player's data into the cloned item's lore (description)
                    menuMeta.setLore(Arrays.asList(
                            ChatColor.GREEN + "Name: " + ChatColor.YELLOW + player.getName(),
                            ChatColor.GREEN + "Rank: " + player.getRank().getFormattedName(),
                            ChatColor.GREEN + "Balance: " + ChatColor.YELLOW + "$" + player.getBalance(),
                            ChatColor.GREEN + "Tokens: " + ChatColor.YELLOW + "✪ " + player.getTokens(),
                            ChatColor.GREEN + "FastPass: " + ChatColor.YELLOW + player.getRegistry().getEntry("fastPassCount")
                    ));
                    // set the clone item's meta to the item meta we created
                    updatedProfile.setItemMeta(menuMeta);

                    // overwrite the original profile item in slot 4 with the new updated profile item that shows a player's data
                    menu.setButton(new MenuButton(4, updatedProfile, ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PROFILE))));
                });
                break;
            }
            case FOOD: { // should the menu the player is looking be food
                // get the current park the player is in,
                // or have it open a park selection for the player to go to in order to use this menu
                ParkType currentPark = currentParkOrOpenParkMenu(player);
                // if the current park the player is in does not exist, return and go back to the main menu
                if (currentPark == null) return;
                // create a new Array List called buttons (like main menu in order to create a list of buttons
                // to add to the menu at the very end)
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                // for every food location in the current park,
                // create a new item for each location and add it as a new button to the buttons list
                for (FoodLocation food : ParkManager.getFoodManager().getFoodLocations(currentPark)) {
                    // create the food location place holder item
                    ItemStack item = food.getItem();
                    // get the item meta
                    ItemMeta meta = item.getItemMeta();
                    // set the lore (description) to the warp location of the food location
                    meta.setLore(Arrays.asList("", ChatColor.YELLOW + "/warp " + food.getWarp()));
                    // set the item meta to what we created
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    // add all the food location item placeholders as a new button players can left click on
                    // and it would warp them to said location
                    buttons.add(new MenuButton(i++, ItemUtil.unbreakable(item), ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + food.getWarp());
                        p.closeInventory();
                    })));
                }
                // if the buttons list is empty, add a new non-clickable button that informs the player
                // there are no food locations in their current park
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Food Locations",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no food locations in this park!"))));
                }
                // add a "go back" button to the end of the row of buttons that brings players back to the main menu
                buttons.add(getBackButton(size - 5, BandInventory.MAIN));
                // create the new food menu with the slot size being the variable "size," the title as coded,
                // the player associated to the menu, and add the list of buttons.
                // Finally, open the menu when a player clicks on the item button from the main menu
                new Menu(size, ChatColor.BLUE + "Food Locations (" + currentPark.getId() + ")", player, buttons).open();
                break;
            }
            case SHOWS: { // if the opened menu is shows and events
                // create a new, 27-slot, menu for the player that shows them a timetable of upcoming shows
                // Buttons created are shows that the server offers. When clicked, it warps players to the location of the show
                // A timetable button is shows players the next times of each show that is offered on the server
                new Menu(27, ChatColor.BLUE + "Shows and Events", player, Arrays.asList(
                        new MenuButton(8, ItemUtil.create(Material.BOOK, ChatColor.AQUA + "Show Timetable"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.TIMETABLE))),
                        // this button warps players to the Symphony of the Stars show location
                        new MenuButton(10, ItemUtil.create(Material.DIAMOND_SWORD, ChatColor.RED + "Symphony in the Stars"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp sits");
                                    p.closeInventory();
                                })),
                        // this button warps players to the Fantasmic! show location
                        new MenuButton(11, ItemUtil.create(Material.DIAMOND_HELMET, ChatColor.BLUE + "Fantasmic!"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp fant");
                                    p.closeInventory();
                                })),
                        // this button teleports players to the location of Wishes!
                        new MenuButton(12, ItemUtil.create(Material.BLAZE_ROD, ChatColor.AQUA + "Wishes!"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp castle");
                                    p.closeInventory();
                                })),
                        // this button teleports players to Illuminations location
                        new MenuButton(13, ItemUtil.create(Material.EGG, ChatColor.GREEN + "Illuminations: Reflections of Earth"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp iroe");
                                    p.closeInventory();
                                })),
                        // this button teleports players to FoF's location
                        new MenuButton(14, ItemUtil.create(Material.INK_SACK, ChatColor.DARK_AQUA + "Festival of Fantasy", 12),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp fof");
                                    p.closeInventory();
                                })),
                        // this button teleports players to Main Street for the electrical parade
                        new MenuButton(15, ItemUtil.create(Material.GLOWSTONE_DUST, ChatColor.YELLOW + "Main Street Electrical Parade"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp msep");
                                    p.closeInventory();
                                })),
                        // this button teleports players to the finding nemo location
                        new MenuButton(16, ItemUtil.create(Material.RAW_FISH, ChatColor.GOLD + "Finding Nemo: The Musical", 2),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp fntm");
                                    p.closeInventory();
                                })),
                        // add the back button
                        getBackButton(22, BandInventory.MAIN))).open();
                break;
            }
            case ATTRACTION_MENU: // if the players open the Attraction menu
                // create a new menu that will showcase available attractions and their wait times
                new Menu(27, ChatColor.BLUE + "Attractions Menu", player, Arrays.asList(
                        // When clicked, it opens a list of attractions the players can warp to
                        new MenuButton(11, ItemUtil.create(Material.MINECART, ChatColor.AQUA + "Attractions List",
                                Arrays.asList(ChatColor.GREEN + "View all of our available", ChatColor.GREEN + "theme park attractions")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.ATTRACTION_LIST))),
                        // when clicked, it shows wait times for all rides on the server
                        new MenuButton(15, ItemUtil.create(Material.WATCH, ChatColor.AQUA + "Wait Times",
                                Arrays.asList(ChatColor.GREEN + "View the wait times for all", ChatColor.GREEN + "queues on this server")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.WAIT_TIMES))),
                        // add the badck button
                        getBackButton(22, BandInventory.MAIN))).open();
                break;
            case STORAGE_UPGRADE: { // shows the menu if players click the chest button on the main menu
                // create a new array list to add buttons to
                List<MenuButton> buttons = new ArrayList<>();
                // get a player's storage data from their database document
                StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
                // if their backpack size is small, then add a button for players to increase their backpack size
                if (data.getBackpackSize().equals(StorageSize.SMALL)) {
                    buttons.add(
                            // this button allows players to click to expand their backpack size if they have a small storage size
                            new MenuButton(11, ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Expand Backpack",
                                    Arrays.asList(
                                            ChatColor.YELLOW + "3 rows ➠ 6 rows", ChatColor.GRAY + "Purchase a backpack",
                                            ChatColor.GRAY + "upgrade for " + ChatColor.GREEN + "$250"
                                    )),
                                    ImmutableMap.of(ClickType.LEFT, p -> ParkManager.getStorageManager().buyUpgrade(p, Material.CHEST)))
                    );
                } else { // if their backpack storage size is not equal to small, show the player a button that informs them they cannot expand their backpack
                    buttons.add(
                            // this is a non-clickable button that tells players they cannot expand their current backpack size
                            new MenuButton(11, ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Expand Backpack",
                                    Arrays.asList(
                                            ChatColor.RED + "You already own this!",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "3 rows ➠ 6 rows",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "Purchase a backpack",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "upgrade for $250"
                                    )))
                    );
                }
                // if the player's locker size is equal to SMALL,
                // then show a button show allowing them (the player) to request to expand their locker size
                if (data.getLockerSize().equals(StorageSize.SMALL)) {
                    buttons.add(
                            // this button allows players to click to expand their locker size if they have a small storage size
                            new MenuButton(15, ItemUtil.create(Material.ENDER_CHEST, ChatColor.GREEN + "Expand Locker",
                                    Arrays.asList(
                                            ChatColor.YELLOW + "3 rows ➠ 6 rows", ChatColor.GRAY + "Purchase a locker",
                                            ChatColor.GRAY + "upgrade for " + ChatColor.GREEN + "$250"
                                    )),
                                    ImmutableMap.of(ClickType.LEFT, p -> ParkManager.getStorageManager().buyUpgrade(p, Material.ENDER_CHEST)))
                    );
                } else { // if their locker storage size is not equal to small, show the player a button that informs them they cannot expand their locker
                    buttons.add(
                            // this is a non-clickable button that tells players they cannot expand their current locker size
                            new MenuButton(15, ItemUtil.create(Material.ENDER_CHEST, ChatColor.GREEN + "Expand Locker",
                                    Arrays.asList(
                                            ChatColor.RED + "You already own this!",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "3 rows ➠ 6 rows",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "Purchase a locker",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "upgrade for $250"
                                    )))
                    );
                }
                // add the "go back" button to the buttons list
                buttons.add(getBackButton(22, BandInventory.MAIN));
                // create the new menu for the player with all the buttons from the Array list
                // and open the menu when the item button is clicked from the main menu
                new Menu(27, ChatColor.BLUE + "Storage Upgrade", player, buttons).open();
                break;
            }
            case VIRTUAL_QUEUES: { // what shows when the player opens the virtual queues menu from the main menu
                // get a list of queues from the manager
                List<VirtualQueue> queues = ParkManager.getVirtualQueueManager().getQueues();
                // if the list is empty, return and don't show this menu?
                if (queues.isEmpty()) return;
                // create a new array list to add buttons to
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                // for every vqueue in the queues list, add it as a click-able button to join that queue
                for (VirtualQueue queue : queues) {
                    // get the item connected to the player in the virtual queue
                    ItemStack item = queue.getItem(player);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    // add a new button to the buttons array with the item we got
                    buttons.add(new MenuButton(i++, ItemUtil.unbreakable(item), ImmutableMap.of(ClickType.RIGHT, p -> {
                        // close the inventory gui
                        p.closeInventory();
                        // if the player's position in the virtual queue is less than one,
                        // then try having the player join the queue.
                        // If the player cannot join the queue, inform the player to try again soon
                        if (queue.getPosition(p.getUniqueId()) < 1) {
                            try {
                                // try allowing the player to join the queue
                                queue.joinQueue(p);
                            } catch (Exception e) { // catch any error that might happen
                                // send a logged message to the server console a severe error happened with the player joining the queue
                                Core.getInstance().getLogger().log(Level.SEVERE, "Error joining virtual queue", e);
                                // send the player a message stating that the error occured and they need to try again soon
                                p.sendMessage(ChatColor.RED + "An error occurred while joining that queue - try again soon!");
                            }
                        } else { // if they are already in the queue, and their position is not less than one,
                                // have the player leave the queue and teleports them to a designated location if applicable
                            try {
                                // try removing the player from the queue
                                queue.leaveQueue(p);
                            } catch (Exception e) { // catch any error that might happen
                                // send a logged message to the server console a severe error happened with the player joining the queue
                                Core.getInstance().getLogger().log(Level.SEVERE, "Error joining virtual queue", e);
                                // send the player a message stating that the error occured and they need to try again soon
                                p.sendMessage(ChatColor.RED + "An error occurred while joining that queue - try again soon!");
                            }
                        }
                    })));
                }
                // if the buttons list is empty (no virtual queues available to join),
                // then open the main menu screen of the magic band
                if (buttons.isEmpty()) {
                    openInventory(player, BandInventory.MAIN);
                    break;
                }
                // add the "go back" button to the list and have its slot be the end of the content
                buttons.add(getBackButton(size - 5, BandInventory.MAIN));
                // create the new menu for the player with the buttons list
                Menu menu = new Menu(size, ChatColor.BLUE + "Virtual Queues", player, buttons);
                // open the menu when the item is clicked
                menu.open();
                ParkManager.getVirtualQueueManager().addToOpenMenus(player, menu);
                break;
            }
            case ATTRACTION_LIST: { // when a player opens this menu, it opens the list of available attractions a player can warp to
                // get the current park the player is in,
                // or have it open a park selection for the player to go to in order to use this menu
                ParkType currentPark = currentParkOrOpenParkMenu(player);
                // if the currentPark is null, then return? (return to what?)
                if (currentPark == null) return;
                // create a new array list to add buttons to
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                // for every attraction in the currentPark, give it an item button and put it into the array list
                for (Attraction attraction : ParkManager.getAttractionManager().getAttractions(currentPark)) {
                    // get the item the attraction is set to
                    ItemStack item = attraction.getItem();
                    // get the attraction item meta to change
                    ItemMeta meta = item.getItemMeta();
                    // create a new singleton array list that is blank
                    List<String> lore = new ArrayList<>(Collections.singletonList(""));
                    // create a string array of the attraction description that wraps the attraction description to be readable
                    String[] descriptionList = WordUtils.wrap(attraction.getDescription(), 30).split("\n");
                    // for every string in the description list array, add it to the item lore (description)
                    for (String s : descriptionList) {
                        // add the strings with the DARK_AQUA color
                        lore.add(ChatColor.DARK_AQUA + s);
                    }
                    // add all information to the description of the item as well
                    // (warp location, status -- closed or open, and categories the attraction is part of)
                    lore.addAll(Arrays.asList("", ChatColor.GREEN + "Warp: " + ChatColor.YELLOW + "/warp " + attraction.getWarp(),
                            "", ChatColor.GREEN + "Status: " + (attraction.isOpen() ? "OPEN" : ChatColor.RED + "CLOSED"),
                            "", ChatColor.GREEN + "Categories:"));
                    // if the attraction has a linked queue, then get the queue associated to that attraction in the current park
                    if (attraction.getLinkedQueue() != null) {
                        // the queue that is linked to the attraction in the current park
                        Queue queue = ParkManager.getQueueManager().getQueue(attraction.getLinkedQueue(), currentPark);
                        // if the queue is not null, then add the wait for the attraction in the attraction item lore (description)
                        if (queue != null)
                            lore.addAll(5 + descriptionList.length, Arrays.asList("", ChatColor.GREEN + "Wait: " + ChatColor.YELLOW + queue.getWaitFor(null)));
                    }
                    // for every attraction category in the attraction's category list, add it to the item lore (description)
                    for (AttractionCategory category : attraction.getCategories()) {
                        lore.add(ChatColor.AQUA + "- " + ChatColor.YELLOW + category.getFormattedName());
                    }
                    // add the lore to the item meta
                    meta.setLore(lore);
                    // set the item meta to the meta we created
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    // add a new button for each attraction, when clicked: it warps the player to said attraction and closes the inventory gui
                    buttons.add(new MenuButton(i++, ItemUtil.unbreakable(item), ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + attraction.getWarp());
                        p.closeInventory();
                    })));
                }
                // if the buttons list is empty, then inform the player with a non-clickable button titled "No attractions."
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Attractions",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no attractions on this server!"))));
                }
                // add the "go back" button to the buttons list
                buttons.add(getBackButton(size - 5, BandInventory.ATTRACTION_MENU));
                // create the new menu for the player with all the buttons for their current park and open the gui
                new Menu(size, ChatColor.BLUE + "Attractions List (" + currentPark.getId() + ")", player, buttons).open();
                break;
            }
            case WAIT_TIMES: { // when a player opens this menu, it opens the list of attractions' wait times
                // get the current park the player is in,
                // or have it open a park selection for the player to go to in order to use this menu
                ParkType currentPark = currentParkOrOpenParkMenu(player);
                // if the currentPark is null, then return? (return to what?)
                if (currentPark == null) return;
                // create a new array list to add buttons to
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                // for every queue in the current park, add it as an item a player can hover to see the attraction's wait time
                for (Queue queue : ParkManager.getQueueManager().getQueues(currentPark)) {
                    // the wait time item -- a sign
                    ItemStack item = ItemUtil.create(Material.SIGN);
                    // the meta of the sign item
                    ItemMeta meta = item.getItemMeta();
                    // set the display name of the item to the queue's name
                    meta.setDisplayName(queue.getName());
                    // create the lore Array list containing the wait, warp location, and status
                    List<String> lore = new ArrayList<>(Arrays.asList("", ChatColor.GREEN + "Wait: " + ChatColor.YELLOW + queue.getWaitFor(null),
                            "", ChatColor.GREEN + "Warp: " + ChatColor.YELLOW + "/warp " + queue.getWarp(),
                            "", ChatColor.GREEN + "Status: " + (queue.isOpen() ? "OPEN" : ChatColor.RED + "CLOSED")));
                    // set the meta lore as the array list we made
                    meta.setLore(lore);
                    // set the item meta to what we created
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    // add a new button of each queue item, when clicked, it warps the player to that location
                    // then closes the inventory gui
                    buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + queue.getWarp());
                        p.closeInventory();
                    })));
                }
                // if the buttons list is empty, then then inform the player with a non-clickable button titled "No queues."
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Queues",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no queues on this server!"))));
                }
                // add the "go back" button to the end of the content size
                buttons.add(getBackButton(size - 5, BandInventory.ATTRACTION_MENU));
                // create the new menu for the player showing attraction wait times and open the menu
                new Menu(size, ChatColor.BLUE + "Wait Times (" + currentPark.getId() + ")", player, buttons).open();
                new Menu(size, ChatColor.BLUE + "Wait Times (" + currentPark.getId() + ")", player, buttons).open();
                break;
            }
            case PARKS: { // when a player opens this menu, it opens the list of parks a player can warp to
                // create a new array list to add buttons to
                List<MenuButton> buttons = Arrays.asList(
                        // This button opens a server list for players to select a different park server
                        new MenuButton(0, ItemUtil.create(Material.END_CRYSTAL, ChatColor.AQUA + "Park Servers",
                                Arrays.asList(ChatColor.GREEN + "Transfer to a different park server", "",
                                        ChatColor.YELLOW + "Current Server: " + ChatColor.GREEN + Core.getInstanceName(),
                                        ChatColor.AQUA + "" + ChatColor.ITALIC + "Coming Soon - use /f and /p for now")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SERVER_LIST))),
                        // this button, when clicked, warps players to Walt Disney World Resort and closes the inventory gui
                        new MenuButton(4, ItemUtil.create(Material.EMPTY_MAP, ChatColor.AQUA + "Walt Disney World Resort", Collections.singletonList(ChatColor.GREEN + "/warp WDW")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    // perform the command as if the player typed it in chat
                                    p.performCommand("warp wdw");
                                    // close the opened inventory screen
                                    p.closeInventory();
                                })),
                        // this button will warp players to Magic Kingdom Park when clicked
                        new MenuButton(11, ItemUtil.create(Material.DIAMOND_HOE, ChatColor.AQUA + "Magic Kingdom", Collections.singletonList(ChatColor.GREEN + "/warp MK")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    // perform the command as if the player typed it in chat
                                    p.performCommand("warp mk");
                                    // close the opened inventory screen
                                    p.closeInventory();
                                })),
                        // this button, when clicked, warps players to Epcot
                        new MenuButton(12, ItemUtil.create(Material.SNOW_BALL, ChatColor.AQUA + "Epcot", Collections.singletonList(ChatColor.GREEN + "/warp Epcot")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    // perform the command as if the player typed it in chat
                                    p.performCommand("warp epcot");
                                    // close the opened inventory screen
                                    p.closeInventory();
                                })),
                        // this button will warp players, when clicked, to Disney's Hollywood Studios
                        new MenuButton(13, ItemUtil.create(Material.JUKEBOX, ChatColor.AQUA + "Disney's Hollywood Studios", Collections.singletonList(ChatColor.GREEN + "/warp DHS")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    // perform the command as if the player typed it in chat
                                    p.performCommand("warp dhs");
                                    // close the opened inventory screen
                                    p.closeInventory();
                                })),
                        // this button, when clicked, will warp players to Disney's Animal Kingdom
                        new MenuButton(14, ItemUtil.create(Material.SAPLING, 1, 5, ChatColor.AQUA + "Animal Kingdom", Collections.singletonList(ChatColor.GREEN + "/warp AK")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    // perform the command as if the player typed it in chat
                                    p.performCommand("warp ak");
                                    // close the opened inventory screen
                                    p.closeInventory();
                                })),
                        // this button will warp players to Typhoon Lagoon
                        new MenuButton(15, ItemUtil.create(Material.WATER_BUCKET, ChatColor.AQUA + "Typhoon Lagoon", Collections.singletonList(ChatColor.GREEN + "/warp Typhoon")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    // perform the command as if the player typed it in chat
                                    p.performCommand("warp typhoon");
                                    // close the opened inventory screen
                                    p.closeInventory();
                                })),
                        // this button will warp players to the Seasonal park
                        new MenuButton(28, ItemUtil.create(Material.EMPTY_MAP, ChatColor.AQUA + "Seasonal", Collections.singletonList(ChatColor.GREEN + "/warp Seasonal")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    // perform the command as if the player typed it in chat
                                    p.performCommand("warp seasonal");
                                    // close the opened inventory screen
                                    p.closeInventory();
                                })),
                        // this button will warp players to Universal Studios Resort
                        new MenuButton(31, ItemUtil.create(Material.EMPTY_MAP, ChatColor.AQUA + "Universal Orlando Resort", Collections.singletonList(ChatColor.GREEN + "/warp UOR")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    // perform the command as if the player typed it in chat
                                    p.performCommand("warp uso");
                                    // close the opened inventory screen
                                    p.closeInventory();
                                })),
                        // this button will warp players to Disney's Cruise Line
                        new MenuButton(34, ItemUtil.create(Material.BOAT, ChatColor.AQUA + "Disney Cruise Line", Collections.singletonList(ChatColor.GREEN + "/warp DCL")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    // perform the command as if the player typed it in chat
                                    p.performCommand("warp dcl");
                                    // close the opened inventory screen
                                    p.closeInventory();
                                })),
                        // this button will warp players to Universal Studios Florida Park
                        new MenuButton(39, ItemUtil.create(Material.JUKEBOX, ChatColor.AQUA + "Universal Studios Florida", Collections.singletonList(ChatColor.GREEN + "/warp USF")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    // perform the command as if the player typed it in chat
                                    p.performCommand("warp usf");
                                    // close the opened inventory screen
                                    p.closeInventory();
                                })),
                        // this button will warp players to Islands of Adventure
                        new MenuButton(41, ItemUtil.create(Material.JUKEBOX, ChatColor.AQUA + "Islands of Adventure", Collections.singletonList(ChatColor.GREEN + "/warp IOA")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    // perform the command as if the player typed it in chat
                                    p.performCommand("warp ioa");
                                    // close the opened inventory screen
                                    p.closeInventory();
                                })),
                        // add the "go back" button
                        getBackButton(49, BandInventory.MAIN)
                );
                // create the new menu with the button array list we just created and open the menu
                new Menu(54, ChatColor.BLUE + "Park Menu", player, buttons).open();
                break;
            }
            // when a player opens this menu, it showcases shops players can go into for purchasing in-game items
            case SHOP: {
                // get the current park the player is in,
                // or have it open a park selection for the player to go to in order to use this menu
                ParkType currentPark = currentParkOrOpenParkMenu(player);
                // if the currentPark does not exist, return null
                if (currentPark == null) return;
                // button array list to add a range of buttons
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                // for every shop in the currentPark, get the item representing the shop and add custom item meta
                // and add it to a MenuButton that is being added to the buttons array list
                for (Shop shop : ParkManager.getShopManager().getShops(currentPark)) {
                    // get the item representing the shop
                    ItemStack item = shop.getItem();
                    // get the shop item meta
                    ItemMeta meta = item.getItemMeta();
                    // add lore (description) to the item telling the player the warp command to go to the shop
                    meta.setLore(Arrays.asList("", ChatColor.YELLOW + "/warp " + shop.getWarp()));
                    // set the shop item meta to the custom meta we created
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    // add a new menu button for every unbreakable shop item to the array list
                    buttons.add(new MenuButton(i++, ItemUtil.unbreakable(item), ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + shop.getWarp());
                        p.closeInventory();
                    })));
                }
                // if the buttons array list is empty, then place a non-clickable button with information
                // telling the player there are no shops on the server they are on
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Shops",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no shops on this server!"))));
                }
                // add the "go back" button to the buttons array list
                buttons.add(getBackButton(size - 5, BandInventory.MAIN));
                // create the new menu with the specific number of slots, and the buttons array list. Open the gui
                new Menu(size, ChatColor.BLUE + "Shop List (" + currentPark.getId() + ")", player, buttons).open();
                break;
            }
            case WARDROBE: { // a player opening this menu is brought a new menu with their wardrobe options
                // open the player's first wardrobe page
                ParkManager.getWardrobeManager().openWardrobePage(player, 1);
                break;
            }
            case PROFILE: { // this is the player's profile menu
                // create a new menu for the player having buttons for the server website, store page, player's locker,
                // ride counters, photopass settings, achievements, resource packs, and server discord
                new Menu(27, ChatColor.BLUE + "My Profile", player, Arrays.asList(
                        // this button holds the website link. When clicked, it will bring up the server website in the player's web browser
                        new MenuButton(1, ItemUtil.create(Material.NETHER_STAR, ChatColor.AQUA + "Website",
                                Collections.singletonList(ChatColor.GREEN + "Visit our website!")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    new FormattedMessage("\n")
                                            .then("Click here to visit our website")
                                            .color(ChatColor.YELLOW).style(ChatColor.UNDERLINE)
                                            .tooltip(ChatColor.GREEN + "Click to visit " + ChatColor.YELLOW + "https://palace.network")
                                            .link("https://palace.network").then("\n").send(p);
                                    p.closeInventory();
                                })),
                        // this button will send players a web link to open in their web browser.
                        new MenuButton(10, ItemUtil.create(Material.DIAMOND, ChatColor.AQUA + "Store",
                                Collections.singletonList(ChatColor.GREEN + "Visit our store!")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    new FormattedMessage("\n")
                                            .then("Click here to visit our store")
                                            .color(ChatColor.YELLOW).style(ChatColor.UNDERLINE)
                                            .tooltip(ChatColor.GREEN + "Click to visit " + ChatColor.YELLOW + "https://store.palace.network")
                                            .link("https://store.palace.network").then("\n").send(p);
                                    p.closeInventory();
                                })),
                        // this button will open their locker storage menu
                        new MenuButton(3, ItemUtil.create(Material.ENDER_CHEST, ChatColor.AQUA + "Locker",
                                Collections.singletonList(ChatColor.GREEN + "Click to view your Locker")),
                                ImmutableMap.of(ClickType.LEFT, p -> ParkManager.getInventoryUtil().openMenu(p, MenuType.LOCKER))),
                        // this button will allow the player to view the number of times they have been on different rides
                        new MenuButton(12, ItemUtil.create(Material.GOLD_INGOT, ChatColor.AQUA + "Ride Counters",
                                Arrays.asList(ChatColor.GREEN + "View the number of times you've",
                                        ChatColor.GREEN + "been on different theme park rides")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.RIDE_COUNTERS))),
                        // this button opens a new menu to allow players to change settings in regards to Photopass
                        new MenuButton(5, ItemUtil.create(Material.CLAY_BRICK, ChatColor.AQUA + "PhotoPass Settings",
                                Arrays.asList(ChatColor.GREEN + "Manage Settings for", ChatColor.GREEN + "Ride Photos and Photo Spots!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.RIDE_PHOTOS))),
                        // this button allows players to see all of the achievements they have collected
                        new MenuButton(14, ItemUtil.create(Material.EMERALD, ChatColor.AQUA + "Achievements", Arrays.asList(ChatColor.GREEN +
                                        "You've earned " + ChatColor.YELLOW + player.getAchievementManager().getAchievements().size() + ChatColor.GREEN + " achievements!",
                                ChatColor.GREEN + "There are " + ChatColor.YELLOW + Core.getAchievementManager().getAchievements().size() + ChatColor.GREEN + " total to earn",
                                ChatColor.GRAY + "Click to view all of your achievements")),
                                ImmutableMap.of(ClickType.LEFT, p -> Core.getCraftingMenu().openAchievementPage(p, 1))),
                        // this menu button allows players to manage their serve resource pack settings
                        new MenuButton(7, ItemUtil.create(Material.NOTE_BLOCK, ChatColor.AQUA + "Resource Packs",
                                Collections.singletonList(ChatColor.GREEN + "Manage your Resource Pack settings")),
                                ImmutableMap.of(ClickType.LEFT, p -> ParkManager.getPackManager().openMenu(p))),
                        // this button will take players to an invite of the server discord
                        new MenuButton(16, ItemUtil.create(Material.COMPASS, ChatColor.AQUA + "Discord",
                                Collections.singletonList(ChatColor.GREEN + "Join the conversation on our Discord!")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    new FormattedMessage("\n")
                                            .then("Click here to join our Discord")
                                            .color(ChatColor.YELLOW).style(ChatColor.UNDERLINE)
                                            .tooltip(ChatColor.GREEN + "Click to run " + ChatColor.YELLOW + "/discord")
                                            .command("/discord").then("\n").send(p);
                                    p.closeInventory();
                                })),
                        // get the "go back a page" button
                        getBackButton(22, BandInventory.MAIN)
                )).open();
                break;
            }
            // when clicked from the main menu or in the player's profile,
            // it will open the counter for all the rides a player has been on
            case RIDE_COUNTERS: {
                // open the ride counter menu page
                openRideCounterPage(player, 1);
                break;
            }
            case RIDE_PHOTOS: { // this is the menu for players to change photopass settings
                // create the new menu to showcase the settings a player can edit for photopass
                new Menu(27, ChatColor.BLUE + "PhotoPass Settings", player, Arrays.asList(
                        // this button, depending on if it is toggled ON or not, will determine if ride photos are allowed for the player
                        new MenuButton(13, ItemUtil.create(Material.LEVER, ChatColor.AQUA + "Toggle RidePhotos",
                                Collections.singletonList(ChatColor.GREEN + "Toggle RidePhotos On/Off")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("pp toggle");
                                    p.closeInventory();
                                })),
                        getBackButton(22, BandInventory.PARKS)
                )).open();
                break;
            }
            case VISIBILITY: { // when clicked open, it brings up a new menu for players to change player visibility settings
                // get the player visibility settings from the database
                VisibilityUtil.Setting setting = ParkManager.getVisibilityUtil().getSetting(player);
                // create an item representing the "visible" option
                ItemStack visible = ItemUtil.create(VisibilityUtil.Setting.ALL_VISIBLE.getBlock(), 1,
                        VisibilityUtil.Setting.ALL_VISIBLE.getData(),
                        VisibilityUtil.Setting.ALL_VISIBLE.getColor() + VisibilityUtil.Setting.ALL_VISIBLE.getText()
                                + (setting.equals(VisibilityUtil.Setting.ALL_VISIBLE) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show all players"));
                // create an item representing the "staff and friends" option
                ItemStack staffFriends = ItemUtil.create(VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getBlock(), 1,
                        VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getData(),
                        VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getColor() + VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getText()
                                + (setting.equals(VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show only staff and friends"));
                // create an item representing the "friends only" option
                ItemStack friends = ItemUtil.create(VisibilityUtil.Setting.ONLY_FRIENDS.getBlock(), 1,
                        VisibilityUtil.Setting.ONLY_FRIENDS.getData(),
                        VisibilityUtil.Setting.ONLY_FRIENDS.getColor() + VisibilityUtil.Setting.ONLY_FRIENDS.getText()
                                + (setting.equals(VisibilityUtil.Setting.ONLY_FRIENDS) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show only friends"));
                // create an item representing the "none visible" option
                ItemStack none = ItemUtil.create(VisibilityUtil.Setting.ALL_HIDDEN.getBlock(), 1,
                        VisibilityUtil.Setting.ALL_HIDDEN.getData(),
                        VisibilityUtil.Setting.ALL_HIDDEN.getColor() + VisibilityUtil.Setting.ALL_HIDDEN.getText()
                                + (setting.equals(VisibilityUtil.Setting.ALL_HIDDEN) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Hide all players"));
                // the item meta to be gathered for each item setting placeholder
                ItemMeta meta;
                switch (setting) { // switch the setting variable with each visibility enumeration member
                    case ALL_VISIBLE: // the setting being all players visible
                        // add a luck enchantment to show it was selected
                        visible.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        // get the item's meta data
                        meta = visible.getItemMeta();
                        // remove any enchants
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        // set the item meta data
                        visible.setItemMeta(meta);
                        break;
                    case ONLY_STAFF_AND_FRIENDS:
                        // add a luck enchantment to show it was selected
                        staffFriends.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        // get the item's meta data
                        meta = staffFriends.getItemMeta();
                        // remove any enchants
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        // set the item meta data
                        staffFriends.setItemMeta(meta);
                        break;
                    case ONLY_FRIENDS:
                        // add a luck enchantment to show it was selected
                        friends.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        // get the item's meta data
                        meta = friends.getItemMeta();
                        // remove any enchants
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        // set the item meta data
                        friends.setItemMeta(meta);
                        break;
                    case ALL_HIDDEN:
                        // add a luck enchantment to show it was selected
                        none.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        // get the item's meta data
                        meta = none.getItemMeta();
                        // remove any enchants
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        // set the item meta data
                        none.setItemMeta(meta);
                        break;
                }
                // array list of buttons to add to the menu
                List<MenuButton> buttons = Arrays.asList(
                        // this button is for the all players visible setting. When clicked, a sound is played telling the player it worked
                        new MenuButton(10, visible,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ALL_VISIBLE, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 2);
                                    }
                                })),
                        // this button is for the staff and friends visible setting. When clicked, a sound is played telling the player it worked
                        new MenuButton(12, staffFriends,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 2);
                                    }
                                })),
                        // this button is for the friends only visible setting. When clicked, a sound is played telling the player it worked
                        new MenuButton(14, friends,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ONLY_FRIENDS, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 2);
                                    }
                                })),
                        // this button is for the all players are hidden visible setting. When clicked, a sound is played telling the player it worked
                        new MenuButton(16, none,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ALL_HIDDEN, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 2);
                                    }
                                })),
                        getBackButton(22, BandInventory.MAIN)
                );
                // create the new menu and open it showcasing the set of settings to choose from
                new Menu(27, ChatColor.BLUE + "Visibility Settings", player, buttons).open();
                break;
            }
            case CUSTOMIZE_BAND: { // when opened, players are presented a new menu with options on customizing their MagicBand
                // this array list contains all the buttons to be added for the menu
                List<MenuButton> buttons = Arrays.asList(
                        // this button, when clicked, will bring up the menu to customize the magic band type
                        new MenuButton(11, ItemUtil.create(getMaterial(BandType.SORCERER_MICKEY), ChatColor.GREEN + "Customize MagicBand Type"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.CUSTOMIZE_BAND_TYPE))),
                        // this button will change the menu screen to another one where players change the magic band color
                        new MenuButton(15, ItemUtil.create(Material.JUKEBOX, ChatColor.GREEN + "Customize MagicBand Name Color"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.CUSTOMIZE_BAND_NAME))),
                        // add the "go back" button
                        getBackButton(22, BandInventory.MAIN)
                );
                // create the new menu for the player with the buttons and open it
                new Menu(27, ChatColor.BLUE + "Customize MagicBand", player, buttons).open();
                break;
            }
            case CUSTOMIZE_BAND_TYPE: { // this menu allows players to customize the band type of their magic band
                // get the red magicband item
                ItemStack red = getMagicBandItem("red", (String) player.getRegistry().getEntry("bandNameColor"));
                // the band item meta, this is being updated with every item
                ItemMeta meta = red.getItemMeta();
                // set the display name to the RED name from BandType
                meta.setDisplayName(BandType.RED.getName());
                // set the item meta to our custom meta
                red.setItemMeta(meta);
                // get the orange magicband item
                ItemStack orange = getMagicBandItem("orange", (String) player.getRegistry().getEntry("bandNameColor"));
                // the orange item meta
                meta = orange.getItemMeta();
                // set the meta display name of the item to Orange's enum name
                meta.setDisplayName(BandType.ORANGE.getName());
                // set the orange item meta
                orange.setItemMeta(meta);
                // get the yellow magicband item
                ItemStack yellow = getMagicBandItem("yellow", (String) player.getRegistry().getEntry("bandNameColor"));
                // get yellow's item meta
                meta = yellow.getItemMeta();
                // set the meta display name of the item to yellow's enum name
                meta.setDisplayName(BandType.YELLOW.getName());
                // set the item meta
                yellow.setItemMeta(meta);
                // get the green magicband item
                ItemStack green = getMagicBandItem("green", (String) player.getRegistry().getEntry("bandNameColor"));
                // get green's item meta
                meta = green.getItemMeta();
                // set the meta display name of the item to green's enum name
                meta.setDisplayName(BandType.GREEN.getName());
                // set the item meta
                green.setItemMeta(meta);
                // get the blue magicband item
                ItemStack blue = getMagicBandItem("blue", (String) player.getRegistry().getEntry("bandNameColor"));
                // get the item meta of the blue item band
                meta = blue.getItemMeta();
                // set the meta display name of the item to blue's enum name
                meta.setDisplayName(BandType.BLUE.getName());
                // set the item meta
                blue.setItemMeta(meta);
                // get the purple magicband item
                ItemStack purple = getMagicBandItem("purple", (String) player.getRegistry().getEntry("bandNameColor"));
                // get the item meta for purple
                meta = purple.getItemMeta();
                // set the meta display name of the item to purple's enum name
                meta.setDisplayName(BandType.PURPLE.getName());
                // set the item meta
                purple.setItemMeta(meta);
                // get the pink magicband item
                ItemStack pink = getMagicBandItem("pink", (String) player.getRegistry().getEntry("bandNameColor"));
                // the pink item meta
                meta = pink.getItemMeta();
                // set the meta display name of the item to pink's enum name
                meta.setDisplayName(BandType.PINK.getName());
                // set the item meta
                pink.setItemMeta(meta);
                // create a array list to add the items to a button to be clicked on
                List<MenuButton> buttons = Arrays.asList(
                        // the red band item button, when clicked it will set the player's band color to red
                        new MenuButton(1, red, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.RED.getDBName()))),
                        // the orange band item button, when clicked it will set the player's band color to orange
                        new MenuButton(2, orange, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.ORANGE.getDBName()))),
                        // the yellow band item button, when clicked it will set the player's band color to yellow
                        new MenuButton(3, yellow, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.YELLOW.getDBName()))),
                        // the green band item button, when clicked it will set the player's band color to green
                        new MenuButton(4, green, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.GREEN.getDBName()))),
                        // the blue band item button, when clicked it will set the player's band color to blue
                        new MenuButton(5, blue, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.BLUE.getDBName()))),
                        // the purple band item button, when clicked it will set the player's band color to purple
                        new MenuButton(6, purple, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.PURPLE.getDBName()))),
                        // the pink band item button, when clicked it will set the player's band color to pink
                        new MenuButton(7, pink, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.PINK.getDBName()))),

                        // these buttons are of non-color bands, in other words, these are of special band types

                        // this will change the player's band to the sorcerer mickey band type
                        new MenuButton(11, ItemUtil.unbreakable(ItemUtil.create(getMaterial(BandType.SORCERER_MICKEY), BandType.SORCERER_MICKEY.getName())),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.SORCERER_MICKEY.getDBName()))),
                        // this will change the player's band to the haunted mansion band type
                        new MenuButton(12, ItemUtil.unbreakable(ItemUtil.create(getMaterial(BandType.HAUNTED_MANSION), BandType.HAUNTED_MANSION.getName())),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.HAUNTED_MANSION.getDBName()))),
                        // this will change the player's band to the princesses band type
                        new MenuButton(13, ItemUtil.unbreakable(ItemUtil.create(getMaterial(BandType.PRINCESSES), BandType.PRINCESSES.getName())),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.PRINCESSES.getDBName()))),
                        // this will change the player's band to the big hero six band type
                        new MenuButton(14, ItemUtil.unbreakable(ItemUtil.create(getMaterial(BandType.BIG_HERO_SIX), BandType.BIG_HERO_SIX.getName())),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.BIG_HERO_SIX.getDBName()))),
                        // this will change the player's band to the holiday band type
                        new MenuButton(15, ItemUtil.unbreakable(ItemUtil.create(getMaterial(BandType.HOLIDAY), BandType.HOLIDAY.getName())),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.HOLIDAY.getDBName()))),

                        // this will change the player's band to the Nookphone type
                        new MenuButton(22, ItemUtil.unbreakable(ItemUtil.create(getMaterial(BandType.NOOKPHONE), BandType.NOOKPHONE.getName())),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.NOOKPHONE.getDBName()))),
                        // go back button
                        getBackButton(31, BandInventory.CUSTOMIZE_BAND)
                );
                // create the new menu with all buttons and open it
                new Menu(36, ChatColor.BLUE + "Customize MagicBand Type", player, buttons).open();
                break;
            }
            case CUSTOMIZE_BAND_NAME: { // this menu allows players to customize the magic band name based on the color of their choosing
                // this is an array list of buttons to allow players to select a new band name for their magic band
                List<MenuButton> buttons = Arrays.asList(
                        // this will change the player's band name to red
                        new MenuButton(10, ItemUtil.create(Material.CONCRETE, ChatColor.RED + "Red", 14),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "red"))),
                        // this will change the player's band name to orange
                        new MenuButton(11, ItemUtil.create(Material.CONCRETE, ChatColor.GOLD + "Orange", 1),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "orange"))),
                        // this will change the player's band name to yellow
                        new MenuButton(12, ItemUtil.create(Material.CONCRETE, ChatColor.YELLOW + "Yellow", 4),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "yellow"))),
                        // this will change the player's band name to green
                        new MenuButton(13, ItemUtil.create(Material.CONCRETE, ChatColor.DARK_GREEN + "Green", 13),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "green"))),
                        // this will change the player's band name to blue
                        new MenuButton(14, ItemUtil.create(Material.CONCRETE, ChatColor.BLUE + "Blue", 11),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "blue"))),
                        // this will change the player's band name to purple
                        new MenuButton(15, ItemUtil.create(Material.CONCRETE, ChatColor.DARK_PURPLE + "Purple", 10),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "purple"))),
                        // this will change the player's band name to pink
                        new MenuButton(16, ItemUtil.create(Material.CONCRETE, ChatColor.LIGHT_PURPLE + "Pink", 6),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "pink"))),
                        // go back button
                        getBackButton(22, BandInventory.CUSTOMIZE_BAND)
                );
                // create the new menu with the buttons and open it
                new Menu(27, ChatColor.BLUE + "Customize MagicBand Name Color", player, buttons).open();
                break;
            }
            case TIMETABLE: { // when opened, a menu showing the show and events timetable is displayed
                // get the schedule buttons predefined in the Schedule Manager
                List<MenuButton> buttons = ParkManager.getScheduleManager().getButtons();
                // add the go back button to the list
                buttons.add(getBackButton(49, BandInventory.SHOWS));
                // create the new menu with the buttons list and open it
                new Menu(54, ChatColor.BLUE + "Show Timetable", player, buttons).open();
                break;
            }
            case PLAYER_TIME: { // player time is toggled if players wish to follow real life time for the park they are in,
                                // or just follow in-game time
                // early morning time in the minecraft world
                long time = player.getBukkitPlayer().getPlayerTime() % 24000;
                // get the player's current selection as a singleton list
                List<String> current = Collections.singletonList(ChatColor.YELLOW + "Currently Selected!");
                // a new list of what is not selected the player has the ability to select
                List<String> not = Collections.singletonList(ChatColor.GRAY + "Click to Select!");
                // create the new menuy with the buttons player's use to adjust their time and open the menu at the end
                new Menu(27, ChatColor.BLUE + "Player Time", player, Arrays.asList(
                        // this button will reset their in-game time to match the park time
                        new MenuButton(9, ItemUtil.create(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "Reset",
                                Collections.singletonList(ChatColor.GREEN + "Match Park Time")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.sendMessage(ChatColor.GREEN + "You " + ChatColor.AQUA + "reset " + ChatColor.GREEN + "your Player Time!");
                                    p.getBukkitPlayer().resetPlayerTime();
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        // this button will set a player's time to 0600 (6am)
                        new MenuButton(10, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "6AM", time == 0 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(0, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "6AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        // this button will set a player's time to 0900 (9am)
                        new MenuButton(11, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "9AM", time == 3000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(3000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "9AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        // this button will set a player's time to 1200 (12pm)
                        new MenuButton(12, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "12PM", time == 6000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(6000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "12PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        // this button will set a player's time to 1500 (3pm)
                        new MenuButton(13, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "3PM", time == 9000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(9000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "3PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        // this button will set a player's time to 1800 (6pm)
                        new MenuButton(14, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "6PM", time == 12000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(12000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "6PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        // this button will set a player's time to 2100 (9pm)
                        new MenuButton(15, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "9PM", time == 15000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(15000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "9PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        // this button will set a player's time to 0000 (12am)
                        new MenuButton(16, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "12AM", time == 18000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(18000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "12AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        // this button will set a player's time to 0300 (3am)
                        new MenuButton(17, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "3AM", time == 21000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(21000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "3AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        // go back button
                        getBackButton(22, BandInventory.MAIN)
                )).open();
                break;
            }
        }
    }

    /**
     * Determines the current park based on a player's location or opens a park selection menu if no park is found.
     *
     * <p>This method first attempts to retrieve the park associated with the player's current location.
     * If a park is found, it returns the {@code ParkType} identifier for that park. If no park is found,
     * it opens the park selection menu for the player to choose a park and returns {@code null}.</p>
     *
     * @param player The {@link CPlayer} object representing the player whose location is being considered.
     *               Cannot be {@code null}.
     *
     * @return The {@link ParkType} corresponding to the player's current park if found; otherwise, {@code null}.
     */
    private ParkType currentParkOrOpenParkMenu(CPlayer player) {
        // get the current park location the player is in
        Park p = ParkManager.getParkUtil().getPark(player.getLocation());
        // if the park is not null, then return the park's ID
        if (p != null) return p.getId();
        // open the inventory gui for selecting a park to go to
        openInventory(player, BandInventory.PARKS);
        return null;
    }

    /**
     * Opens the Ride Counter page for the specified player, displaying a paginated list of rides
     * the player has recorded. Each ride includes details such as the name, ride count, and associated park.
     * <p>
     * This method dynamically populates the menu with rides data, provides navigation between pages,
     * and includes options to return to the previous menu.
     * </p>
     * <ul>
     *     <li>The list of rides is sorted alphabetically by server and then by ride name.</li>
     *     <li>If the total rides exceed 45, pagination controls such as "Next Page" and "Last Page" are displayed.</li>
     *     <li>If there are fewer than 46 rides, pagination is disabled and only the available rides are shown.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} object representing the player for whom the Ride Counter page will be opened. Cannot be {@code null}.
     * @param page   The page number to display in the Ride Counter menu. Must be a positive integer. If the provided page number
     *               exceeds the available pages based on the total number of rides, the method automatically adjusts the page value.
     */
    @SuppressWarnings("unchecked")
    public void openRideCounterPage(CPlayer player, int page) {
        List<MenuButton> buttons = new ArrayList<>();
        TreeMap<String, RideCount> data = (TreeMap<String, RideCount>) player.getRegistry().getEntry("rideCounterCache");

        List<RideCount> rides = new ArrayList<>(data.values());
        rides.sort((o1, o2) -> {
            if (o1.getServer().equals(o2.getServer())) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            } else {
                return o1.getServer().toLowerCase().compareTo(o2.getServer().toLowerCase());
            }
        });
        int size = rides.size();
        if (size < 46) {
            page = 1;
        } else if (size < (45 * (page - 1) + 1)) {
            page -= 1;
        }
        List<RideCount> list = rides.subList(page > 1 ? (45 * (page - 1)) : 0, (size - (45 * (page - 1))) > 45 ? (45 * page) : size);
        int pos = 0;
        for (RideCount ride : list) {
            if (pos >= 45) break;
            buttons.add(new MenuButton(pos++, ItemUtil.create(Material.MINECART, ChatColor.GREEN + ride.getName(),
                    Arrays.asList(ChatColor.YELLOW + "Rides: " + ride.getCount(), ChatColor.YELLOW + "Park: " + ride.getServer()))));
        }
        int finalPage = page;
        if (page > 1) {
            buttons.add(new MenuButton(48, ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Last Page"),
                    ImmutableMap.of(ClickType.LEFT, p -> openRideCounterPage(p, finalPage - 1))));
        }
        int maxPage = 1;
        int n = size;
        while (true) {
            if (n - 45 > 0) {
                n -= 45;
                maxPage += 1;
            } else {
                break;
            }
        }
        if (size > 45 && page < maxPage) {
            buttons.add(new MenuButton(50, ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Next Page"),
                    ImmutableMap.of(ClickType.LEFT, p -> openRideCounterPage(p, finalPage + 1))));
        }
        buttons.add(getBackButton(49, BandInventory.PROFILE));
        new Menu(54, ChatColor.GREEN + "Ride Counter Page " + page, player, buttons).open();
    }

    /**
     * Sets the band type for the given player and updates relevant properties.
     * <p>
     * This method updates the player's MagicBand type, modifies related registry
     * entries, updates the inventory, and synchronizes the change with the storage
     * system. A message is sent to the player notifying them of the updated band type.
     * </p>
     *
     * <ul>
     *   <li>Updates the player's registry with the new band type in lowercase.</li>
     *   <li>Synchronizes changes with the asynchronous storage system.</li>
     *   <li>Notifies the player about the updated MagicBand type and closes their inventory.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} object representing the player whose MagicBand type is being set. Cannot be {@code null}.
     * @param type   A {@code String} specifying the new band type to assign to the player. The value will be stored in lowercase.
     */
    private void setBandType(CPlayer player, String type) {
        // add an entry to the player's registry for the band type name
        player.getRegistry().addEntry("bandType", type.toLowerCase());
        // update the player's storage inventory
        ParkManager.getStorageManager().updateInventory(player);
        // send the player a message of the successful band change
        player.sendMessage(ChatColor.GREEN + "You've changed to a " + BandType.fromString(type).getName() + ChatColor.GREEN + " MagicBand!");
        // close the inventory
        player.closeInventory();
        // run a background task that changes the player's mongo data
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setMagicBandData(player.getUniqueId(), "bandtype", type.toLowerCase()));
    }

    /**
     * Sets the color of the MagicBand's name for the specified player.
     *
     * <p>This method modifies the player's MagicBand name color in the system, updates the storage,
     * and notifies the player about the change. The new color is stored in lowercase and is applied
     * asynchronously to ensure database synchronization.</p>
     *
     * <ul>
     *   <li>Updates the player's registry to include the new name color.</li>
     *   <li>Saves the updated name color to the storage system asynchronously.</li>
     *   <li>Notifies the player about the change and closes their inventory.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} object representing the player whose MagicBand name color is being modified. Cannot be {@code null}.
     * @param color  A {@code String} specifying the new color to apply to the MagicBand name. The input is converted to lowercase before being stored.
     */
    private void setBandNameColor(CPlayer player, String color) {
        // add an entry to the player registry of their band's color
        player.getRegistry().addEntry("bandNameColor", color.toLowerCase());
        // update the player's storage inventory
        ParkManager.getStorageManager().updateInventory(player);
        // send the player a message of the successful band color change
        player.sendMessage(ChatColor.GREEN + "You've set your MagicBand's name color to " + getNameColor(color) + color + "!");
        // close the inventory
        player.closeInventory();
        // run a background task that changes the player's mongo data
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setMagicBandData(player.getUniqueId(), "namecolor", color.toLowerCase()));
    }

    /**
     * Handles the actions performed when a player joins the system, including setting default or customized
     * MagicBand properties and loading ride counter data asynchronously.
     *
     * <p>This method performs the following tasks:</p>
     * <ul>
     *     <li>Checks the provided document for "bandtype" and "namecolor" keys. If absent, sets default values.</li>
     *     <li>Registers the player's MagicBand type and name color in the player's registry.</li>
     *     <li>Asynchronously loads the player's ride counter data from the database and stores it in the player's registry.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} object representing the player who joined. Cannot be {@code null}.
     * @param doc    The {@link Document} containing player's data, which may include MagicBand-related properties
     *               such as "bandtype" and "namecolor". If these keys are missing, default values are assigned.
     */
    public void handleJoin(CPlayer player, Document doc) {
        String bandtype, namecolor;
        // if the player mongo document does not contain band type or name color, then set it to the default settings
        if (!doc.containsKey("bandtype") || !doc.containsKey("namecolor")) {
            bandtype = "red";
            namecolor = "gold";
        } else { // if the document does contain the keys, get the strings from said keys
            bandtype = doc.getString("bandtype");
            namecolor = doc.getString("namecolor");
        }
        // add an entry to the player's registry of the band type
        player.getRegistry().addEntry("bandType", bandtype);
        // add an entry to the player's registry of the band color
        player.getRegistry().addEntry("bandNameColor", namecolor);
        // run a task in the background to add ride counter data to the player document
        Core.runTaskAsynchronously(() -> {
            TreeMap<String, RideCount> data = new TreeMap<>();
            for (Object o : Core.getMongoHandler().getRideCounterData(player.getUniqueId())) {
                Document d = (Document) o;
                String name = d.getString("name").trim();
                String server = d.getString("server").replaceAll("[^A-Za-z ]", "");
                if (data.containsKey(name) && data.get(name).serverEquals(server)) {
                    data.get(name).addCount(1);
                } else {
                    data.put(name, new RideCount(name, server));
                }
            }
            player.getRegistry().addEntry("rideCounterCache", data);
        });
    }

    /**
     * Retrieves a {@link ItemStack} representing the MagicBand item for the specified player.
     * <p>
     * This method first checks the player's registry for entries specifying the MagicBand type
     * and name color. If these entries are not available, a default MagicBand item with a red type
     * and gold color is returned. If valid entries are found, a custom MagicBand item is created
     * using the specified type and color.
     * </p>
     *
     * @param player The {@link CPlayer} object representing the player for whom the MagicBand item
     *               is being retrieved. Cannot be {@code null}.
     * @return An {@link ItemStack} representing the player's MagicBand.
     *         If the player's registry has missing entries, a default {@link ItemStack} is provided.
     */
    public ItemStack getMagicBandItem(CPlayer player) {
        if (!player.getRegistry().hasEntry("bandType") || !player.getRegistry().hasEntry("bandNameColor")) {
            return getMagicBandItem("red", "gold");
        }
        return getMagicBandItem((String) player.getRegistry().getEntry("bandType"), (String) player.getRegistry().getEntry("bandNameColor"));
    }

    /**
     * Generates a MagicBand item or Power Pass item based on the specified type and color.
     * <p>
     * This method creates a customized {@code ItemStack} representing a MagicBand or Power Pass
     * with visual and functional attributes as determined by the given parameters. The item may
     * include special effects or metadata related to the selected type or color. If an invalid
     * type is provided, a default red MagicBand with gold coloring is returned.
     * </p>
     *
     * <p>The MagicBand types include:</p>
     * <ul>
     *     <li>RED</li>
     *     <li>ORANGE</li>
     *     <li>YELLOW</li>
     *     <li>GREEN</li>
     *     <li>BLUE</li>
     *     <li>PURPLE</li>
     *     <li>PINK</li>
     *     <li>SORCERER_MICKEY</li>
     *     <li>HAUNTED_MANSION</li>
     *     <li>PRINCESSES</li>
     *     <li>BIG_HERO_SIX</li>
     *     <li>HOLIDAY</li>
     * </ul>
     *
     * <p>The availability of the MagicBand may vary depending on the resort. If the resort is
     * {@code USO}, this method instead generates a Power Pass item.</p>
     *
     * @param type  The {@code String} representation of the MagicBand type. Must correspond
     *              to a value defined in the {@link BandType} enumeration. If the type is
     *              invalid, a default value is used.
     * @param color The {@code String} representation of the color applied to the MagicBand
     *              name. This affects the display name's color using {@link ChatColor}.
     *
     * @return The {@code ItemStack} representing the configured MagicBand or Power Pass item.
     */
    public ItemStack getMagicBandItem(String type, String color) {
        if (ParkManager.getResort().equals(Resort.USO))
            return ItemUtil.create(Material.PAPER, getNameColor(color) + "Power Pass " + ChatColor.GRAY + "(Right-Click)");
        BandType bandType = BandType.fromString(type);
        ItemStack item;
        switch (bandType) {
            case RED:
            case ORANGE:
            case YELLOW:
            case GREEN:
            case BLUE:
            case PURPLE:
            case PINK: {
                item = ItemUtil.create(Material.FIREWORK_CHARGE, getNameColor(color) + "MagicBand " +
                        ChatColor.GRAY + "(Right-Click)");
                FireworkEffectMeta meta = (FireworkEffectMeta) item.getItemMeta();
                meta.setEffect(FireworkEffect.builder().withColor(getBandColor(bandType)).build());
                item.setItemMeta(meta);
                break;
            }
            case SORCERER_MICKEY:
            case HAUNTED_MANSION:
            case PRINCESSES:
            case BIG_HERO_SIX:
            case HOLIDAY:
                item = ItemUtil.create(getMaterial(bandType), getNameColor(color) + "MagicBand " +
                        ChatColor.GRAY + "(Right-Click)");
                break;
            default:
                return getMagicBandItem("red", "gold");
        }
        return item;
    }

    /**
     * Retrieves the appropriate material based on the given band type.
     * <p>
     * The method determines the material either based on the resort or the specific band type.
     * If the resort is "USO," it directly returns a {@code Material.PAPER}. Otherwise, the material is matched
     * to the provided {@code BandType} through a switch case.
     * </p>
     *
     * @param type the {@link BandType} representing the band for which the material is to be retrieved.
     *             <ul>
     *               <li>{@code SORCERER_MICKEY} returns {@code Material.DIAMOND_BARDING}</li>
     *               <li>{@code HAUNTED_MANSION} returns {@code Material.GOLD_BARDING}</li>
     *               <li>{@code PRINCESSES} returns {@code Material.GHAST_TEAR}</li>
     *               <li>{@code BIG_HERO_SIX} returns {@code Material.IRON_BARDING}</li>
     *               <li>{@code HOLIDAY} returns {@code Material.PAPER}</li>
     *               <li>{@code NOOKPHONE} returns {@code Material.RABBIT_FOOT}</li>
     *               <li>For any other type, {@code Material.FIREWORK_CHARGE} is returned</li>
     *             </ul>
     * @return the {@link Material} associated with the given {@code BandType} or the resort.
     */
    private Material getMaterial(BandType type) {
        if (ParkManager.getResort().equals(Resort.USO)) return Material.PAPER;
        switch (type) {
            case SORCERER_MICKEY:
                return Material.DIAMOND_BARDING;
            case HAUNTED_MANSION:
                return Material.GOLD_BARDING;
            case PRINCESSES:
                return Material.GHAST_TEAR;
            case BIG_HERO_SIX:
                return Material.IRON_BARDING;
            case HOLIDAY:
                return Material.PAPER;
            case NOOKPHONE:
                return Material.RABBIT_FOOT;
            default:
                return Material.FIREWORK_CHARGE;
        }
    }

    /**
     * Retrieves the color associated with the provided {@link BandType}.
     * <p>
     * Depending on the {@link BandType}, this method returns a {@link Color} instance
     * corresponding to a specific RGB color value.
     * If the provided {@code type} does not match any predefined band types, the method
     * defaults to returning a red color.
     * </p>
     *
     * @param type the {@link BandType} whose corresponding color is to be retrieved.
     *             <ul>
     *                 <li><b>ORANGE</b> returns RGB(247, 140, 0)</li>
     *                 <li><b>YELLOW</b> returns RGB(239, 247, 0)</li>
     *                 <li><b>GREEN</b> returns RGB(0, 192, 13)</li>
     *                 <li><b>BLUE</b> returns RGB(41, 106, 255)</li>
     *                 <li><b>PURPLE</b> returns RGB(176, 0, 220)</li>
     *                 <li><b>PINK</b> returns RGB(246, 120, 255)</li>
     *                 <li><b>Default (Other)</b> returns RGB(255, 40, 40) (red)</li>
     *             </ul>
     *
     * @return a {@link Color} object representing the RGB color associated with the given {@link BandType}.
     */
    private Color getBandColor(BandType type) {
        switch (type) {
            case ORANGE:
                return Color.fromRGB(247, 140, 0);
            case YELLOW:
                return Color.fromRGB(239, 247, 0);
            case GREEN:
                return Color.fromRGB(0, 192, 13);
            case BLUE:
                return Color.fromRGB(41, 106, 255);
            case PURPLE:
                return Color.fromRGB(176, 0, 220);
            case PINK:
                return Color.fromRGB(246, 120, 255);
            default:
                //Red
                return Color.fromRGB(255, 40, 40);
        }
    }

    /**
     * Returns a {@link ChatColor} based on the provided color name as a string.
     * <p>
     * This method maps specific color names:
     * <ul>
     *   <li>"red" to {@link ChatColor#RED}</li>
     *   <li>"yellow" to {@link ChatColor#YELLOW}</li>
     *   <li>"green" to {@link ChatColor#DARK_GREEN}</li>
     *   <li>"blue" to {@link ChatColor#BLUE}</li>
     *   <li>"purple" to {@link ChatColor#DARK_PURPLE}</li>
     *   <li>"pink" to {@link ChatColor#LIGHT_PURPLE}</li>
     *   <li>Any other input defaults to {@link ChatColor#GOLD}</li>
     * </ul>
     *
     * @param color A string representing the desired color. It is case-insensitive.
     * @return The corresponding {@link ChatColor}. Defaults to {@link ChatColor#GOLD} if no match is found.
     */
    private ChatColor getNameColor(String color) {
        switch (color.toLowerCase()) {
            case "red":
                return ChatColor.RED;
            case "yellow":
                return ChatColor.YELLOW;
            case "green":
                return ChatColor.DARK_GREEN;
            case "blue":
                return ChatColor.BLUE;
            case "purple":
                return ChatColor.DARK_PURPLE;
            case "pink":
                return ChatColor.LIGHT_PURPLE;
            default:
                //Gold
                return ChatColor.GOLD;
        }
    }

    /**
     * Creates and returns a "Back" button for the menu.
     *
     * <p>The button will be associated with the specified slot in the inventory
     * and will allow players to navigate back when clicked.</p>
     *
     * @param slot the slot position in the inventory where the back button will be placed.
     * @param inv the BandInventory instance representing the inventory to navigate back to.
     * @return a {@code MenuButton} configured as a "Back" button with specific click functionality.
     */
    public MenuButton getBackButton(int slot, BandInventory inv) {
        return new MenuButton(slot, ItemUtil.create(Material.ARROW, ChatColor.GRAY + "Back"),
                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, inv)));
    }
}
