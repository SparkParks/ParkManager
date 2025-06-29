package network.palace.parkmanager.wardrobe;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;
import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.outfits.Clothing;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.handlers.outfits.OutfitSlot;
import network.palace.parkmanager.magicband.BandInventory;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * The {@code WardrobeManager} class is responsible for managing outfits in the wardrobe system.
 * It provides functionality for initializing, retrieving, and modifying outfits as well as handling
 * wardrobe interactions for players.
 *
 * <p>Key responsibilities of the {@code WardrobeManager} class include:
 * <ul>
 *     <li>Loading and initializing outfit data from a database.</li>
 *     <li>Converting outdated outfit formats to newer supported formats.</li>
 *     <li>Providing access to individual or all outfits stored in the system.</li>
 *     <li>Managing outfit assignments and interactions for players.</li>
 *     <li>Synchronizing player outfit data with the database as needed.</li>
 * </ul>
 *
 * <p>The class is implemented to handle operations related to outfits in an asynchronous or synchronous manner
 * depending on usage, and it relies on other components such as {@code CPlayer}, {@code Outfit}, and database
 * utilities to function.
 */
public class WardrobeManager {
    /**
     * A mapping of outfit IDs to their corresponding {@link Outfit} instances.
     *
     * <p>The {@code outfits} field is a {@code HashMap} used to store and manage a collection
     * of {@link Outfit} objects. Each outfit is uniquely identified by an integer ID,
     * which serves as the key in the map. The value associated with each key is the
     * corresponding {@link Outfit} instance.
     *
     * <p>This field facilitates the retrieval, addition, and updating of outfits
     * within the system. It acts as a centralized storage mechanism for all
     * available outfits managed by the {@code WardrobeManager}.
     *
     * <p>Key characteristics of this field:
     * <ul>
     *     <li>It is immutable (declared {@code final}), ensuring the map reference cannot be reassigned.</li>
     *     <li>It is private to enforce encapsulation, with controlled access provided through class methods.</li>
     *     <li>The outfit data can be loaded, cleared, and updated through the logic defined in the class.</li>
     * </ul>
     */
    private final HashMap<Integer, Outfit> outfits = new HashMap<>();

    /**
     * Tracks whether the initialization process has started within the {@code WardrobeManager}.
     *
     * <p>The {@code initialStarted} variable is a flag used to determine if the initialization logic
     * has been executed. This prevents repeated initialization operations, ensuring that the process
     * only runs once.
     *
     * <ul>
     *     <li>Default value is {@code false}, indicating that initialization has not started.</li>
     *     <li>Set to {@code true} when the initialization process begins for the first time.</li>
     * </ul>
     *
     * <p>This variable is private and influences execution flow within the {@code initialize()} method of
     * the {@code WardrobeManager}.
     */
    private boolean initialStarted = false;

    /**
     * Constructs a new instance of the {@code WardrobeManager} class and initializes internal resources
     * for managing wardrobe-related functionality.
     *
     * <p>Upon instantiation, the constructor triggers an initialization process by invoking the
     * {@link #initialize()} method. This process ensures that outfit data is loaded, cached, and ready
     * for usage within the application.</p>
     *
     * <p>The initialization includes:</p>
     * <ul>
     *     <li>Loading outfit data from the database.</li>
     *     <li>Performing migrations or conversions for outfits requiring updates.</li>
     *     <li>Setting up async tasks to periodically synchronize outfit selections with player data.</li>
     *     <li>Ensuring the system is prepared to manage players' clothing and wardrobe interactions.</li>
     * </ul>
     *
     * <p>Once the manager is instantiated and initialized, it functions as the primary handler
     * for wardrobe interactions, including outfit retrieval, updates, and legacy data handling.</p>
     */
    public WardrobeManager() {
        initialize();
    }

    /**
     * Initializes the wardrobe manager by loading outfit data from a database, converting data formats
     * if necessary, and setting up asynchronous update tasks for player outfit selections.
     * <p>
     * This method performs the following operations:
     * <ul>
     *     <li>Clears the existing outfit data.</li>
     *     <li>Loads outfit information from the database and parses it into the appropriate format.</li>
     *     <li>Checks if the outfit data requires format conversion (legacy data handling).</li>
     *     <li>If conversion is needed, reprocesses legacy outfits and updates the database with a new format.</li>
     *     <li>Logs the total number of loaded outfits or conversion progress as appropriate.</li>
     *     <li>Starts an asynchronous task that periodically updates outfit information for online players,
     *         ensuring their selections are saved to the database.</li>
     * </ul>
     * <p>
     * The asynchronous task checks all online players and synchronizes their outfit data to the database
     * using a preset interval.
     */
    public void initialize() {
        outfits.clear();
        boolean convert = false;
        for (Document doc : Core.getMongoHandler().getOutfits(ParkManager.getResort().getId())) {
            if (!doc.containsKey("shirtJSON")) {
                Core.logMessage("WardrobeManager", "Converting outfits!");
                convert = true;
                break;
            }
            outfits.put(doc.getInteger("id"), new Outfit(doc.getInteger("id"),
                    ChatColor.translateAlternateColorCodes('&', doc.getString("name")),
                    ItemUtil.getItemFromJsonNew(doc.getString("headJSON")),
                    ItemUtil.getItemFromJsonNew(doc.getString("shirtJSON")),
                    ItemUtil.getItemFromJsonNew(doc.getString("pantsJSON")),
                    ItemUtil.getItemFromJsonNew(doc.getString("bootsJSON"))));
        }
        if (convert) {
            outfits.clear();
            for (Document doc : Core.getMongoHandler().getOutfits(ParkManager.getResort().getId())) {
                if (doc.containsKey("seq")) continue;
                Outfit outfit = getLegacyOutfit(doc);
                if (outfit == null) continue;
                outfits.put(outfit.getId(), outfit);
            }
            MongoCollection<Document> outfitsCollection = Core.getMongoHandler().getDatabase().getCollection("outfits");
            for (Outfit outfit : outfits.values()) {
                outfitsCollection.findOneAndUpdate(Filters.eq("id", outfit.getId()),
                        new Document("$set", new Document("headJSON", ItemUtil.getJsonFromItemNew(outfit.getHead()).toString())
                                .append("shirtJSON", ItemUtil.getJsonFromItemNew(outfit.getShirt()).toString())
                                .append("pantsJSON", ItemUtil.getJsonFromItemNew(outfit.getPants()).toString())
                                .append("bootsJSON", ItemUtil.getJsonFromItemNew(outfit.getBoots()).toString())
                        )
                );
            }
        } else {
            Core.logMessage("WardrobeManager", "Loaded " + outfits.size() + " outfits!");
        }
        if (!initialStarted) {
            initialStarted = true;
            Core.runTaskTimerAsynchronously(ParkManager.getInstance(), () -> {
                for (CPlayer player : Core.getPlayerManager().getOnlinePlayers()) {
                    if (!player.getRegistry().hasEntry("updateOutfitSelection")) continue;
                    player.getRegistry().removeEntry("updateOutfitSelection");
                    Clothing c = (Clothing) player.getRegistry().getEntry("clothing");
                    Core.getMongoHandler().setOutfitCode(player.getUniqueId(), c.getHeadID() +
                            "," + c.getShirtID() + "," + c.getPantsID() + "," + c.getBootsID());
                }
            }, 0L, 100L);
        }
    }

    /**
     * Retrieves an {@code Outfit} by its unique identifier.
     *
     * <p>This method allows for the retrieval of an {@code Outfit} instance from the internal collection
     * based on the provided ID. If an outfit with the specified ID exists in the collection, it returns
     * the corresponding {@code Outfit} object; otherwise, it returns {@code null}.
     *
     * @param id The unique identifier of the {@code Outfit} to retrieve.
     *           <ul>
     *               <li>An integer representing the ID of the desired outfit.</li>
     *           </ul>
     * @return The {@code Outfit} corresponding to the given ID, or {@code null} if no matching outfit is found.
     */
    public Outfit getOutfit(int id) {
        return outfits.get(id);
    }

    /**
     * Retrieves a list of all available outfits.
     *
     * <p>This method returns a collection of {@link Outfit} objects representing
     * all the outfits currently stored and managed by the wardrobe system.
     * Each outfit encapsulates details such as its unique identifier, name, and
     * associated items (e.g., headgear, shirt, pants, boots).
     *
     * <ul>
     *   <li>Encapsulates all registered outfits.</li>
     *   <li>Provides a read-only copy of the stored outfits, ensuring immutability
     *       of the internal outfit storage.</li>
     * </ul>
     *
     * <p>The outfits are returned in the form of a {@link List}, which is a copy
     * of the internal data structure storing the outfits.
     *
     * @return a {@link List} of {@link Outfit} objects, where each outfit includes
     *         its unique ID, name, and associated items.
     */
    public List<Outfit> getOutfits() {
        return new ArrayList<>(outfits.values());
    }

    /**
     * Handles the join operation for a player by processing their outfit code and purchase history.
     * This method assigns outfit items to the player along with recording outfit purchases.
     *
     * @param player The {@link CPlayer} instance representing the player joining.
     * @param outfitCode A comma-separated {@link String} representing the outfit identifiers to be processed.
     * @param arrayList An {@link ArrayList} of {@link Document} objects containing purchase records for parsing.
     */
    @SuppressWarnings("rawtypes")
    public void handleJoin(CPlayer player, String outfitCode, ArrayList arrayList) {
        List<Integer> purchases = new ArrayList<>();
        for (Object o : arrayList) {
            Document doc = (Document) o;
            int id = doc.getInteger("id");
            purchases.add(id);
        }
        player.getRegistry().addEntry("outfitPurchases", purchases);

        Clothing c = new Clothing();
        String[] list = outfitCode.split(",");
        int in = 0;
        for (String s : list) {
            try {
                int i = Integer.parseInt(s);
                if (!outfits.containsKey(i)) continue;
                Outfit o = getOutfit(i);
                switch (in) {
                    case 0:
                        c.setHead(ItemUtil.unbreakable(o.getHead()));
                        c.setHeadID(i);
                        break;
                    case 1:
                        c.setShirt(ItemUtil.unbreakable(o.getShirt()));
                        c.setShirtID(i);
                        break;
                    case 2:
                        c.setPants(ItemUtil.unbreakable(o.getPants()));
                        c.setPantsID(i);
                        break;
                    case 3:
                        c.setBoots(ItemUtil.unbreakable(o.getBoots()));
                        c.setBootsID(i);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            in++;
        }
        player.getRegistry().addEntry("clothing", c);
    }

    /**
     * Sets the outfit items for the provided player by equipping the appropriate
     * clothing pieces (head, shirt, pants, and boots) from the clothing registry entry.
     *
     * <p>The method retrieves the player's "clothing" entry from their registry,
     * and, if present, assigns its items to the player's inventory slots (helmet,
     * chestplate, leggings, boots). If no "clothing" entry exists, the method exits
     * without making any changes.
     *
     * @param player the player whose outfit items will be set. The method
     *               uses the player's registry to retrieve the clothing information
     *               and assigns the clothing items to the player's inventory slots.
     */
    public void setOutfitItems(CPlayer player) {
        Clothing c = (Clothing) player.getRegistry().getEntry("clothing");
        if (c == null) return;
        PlayerInventory inv = player.getInventory();
        inv.setHelmet(c.getHead());
        inv.setChestplate(c.getShirt());
        inv.setLeggings(c.getPants());
        inv.setBoots(c.getBoots());
    }

    /**
     * Updates the player's outfit by setting an item in the specified outfit slot
     * and validates ownership of the outfit.
     *
     * <p>This method modifies the player's appearance by equipping the specified outfit item
     * in the designated slot. It ensures that the player owns the outfit before equipping it
     * and provides feedback to the player if the equipping action is successful or not.
     *
     * @param player The {@code CPlayer} object representing the player whose outfit is being updated.
     * @param outfitId The ID of the outfit item to be equipped in the specified slot.
     * @param slot The {@code OutfitSlot} indicating which part of the outfit is being set (e.g., HEAD, SHIRT, PANTS, BOOTS).
     * @param page The wardrobe menu page that should be opened after the update.
     * @param owns A boolean indicating whether the player owns the specified outfit. If {@code false}, the outfit cannot be equipped.
     */
    private void setSlot(CPlayer player, int outfitId, OutfitSlot slot, int page, boolean owns) {
        if (!owns) {
            player.sendMessage(ChatColor.RED + "You don't own that!");
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
            return;
        }
        Clothing c = (Clothing) player.getRegistry().getEntry("clothing");
        PlayerInventory inv = player.getInventory();
        Outfit outfit = getOutfit(outfitId);
        boolean selected = false;
        switch (slot) {
            case HEAD:
                if (c.getHeadID() != outfitId) {
                    c.setHeadID(outfitId);
                    c.setHead(outfit == null ? null : ItemUtil.unbreakable(outfit.getHead()));
                    inv.setHelmet(c.getHead());
                    selected = true;
                } else if (outfitId != 0) {
                    player.sendMessage(ChatColor.RED + "You are already wearing that!");
                }
                break;
            case SHIRT:
                if (c.getShirtID() != outfitId) {
                    c.setShirtID(outfitId);
                    c.setShirt(outfit == null ? null : ItemUtil.unbreakable(outfit.getShirt()));
                    inv.setChestplate(c.getShirt());
                    selected = true;
                } else if (outfitId != 0) {
                    player.sendMessage(ChatColor.RED + "You are already wearing that!");
                }
                break;
            case PANTS:
                if (c.getPantsID() != outfitId) {
                    c.setPantsID(outfitId);
                    c.setPants(outfit == null ? null : ItemUtil.unbreakable(outfit.getPants()));
                    inv.setLeggings(c.getPants());
                    selected = true;
                } else if (outfitId != 0) {
                    player.sendMessage(ChatColor.RED + "You are already wearing that!");
                }
                break;
            case BOOTS:
                if (c.getBootsID() != outfitId) {
                    c.setBootsID(outfitId);
                    c.setBoots(outfit == null ? null : ItemUtil.unbreakable(outfit.getBoots()));
                    inv.setBoots(c.getBoots());
                    selected = true;
                } else if (outfitId != 0) {
                    player.sendMessage(ChatColor.RED + "You are already wearing that!");
                }
                break;
        }
        if (selected) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
            player.getRegistry().addEntry("updateOutfitSelection", true);
            openWardrobePage(player, page);
        }
    }

    /**
     * Sets the specified outfit for the player if they own it, updates the player's wardrobe,
     * and opens the wardrobe page.
     *
     * <p> This method validates whether a player owns the specified outfit and then updates the
     * player's outfit components (helmet, chestplate, leggings, boots) accordingly. If the player
     * is already wearing the given outfit, it sends a message indicating this and stops further execution.
     *
     * @param player The player for whom the outfit is being set.
     * @param outfitId The unique ID of the outfit being selected.
     * @param page The wardrobe page to open after setting the outfit.
     * @param owns Indicates whether the player owns the specified outfit.
     */
    private void setOutfit(CPlayer player, int outfitId, int page, boolean owns) {
        if (!owns) {
            player.sendMessage(ChatColor.RED + "You don't own that!");
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
            return;
        }
        Clothing c = (Clothing) player.getRegistry().getEntry("clothing");
        PlayerInventory inv = player.getInventory();
        Outfit outfit = getOutfit(outfitId);
        boolean selected = false;
        if (outfitId != 0 &&
                c.getHeadID() == outfitId &&
                c.getShirtID() == outfitId &&
                c.getPantsID() == outfitId &&
                c.getBootsID() == outfitId) {
            player.sendMessage(ChatColor.RED + "You are already wearing that!");
            return;
        }
        c.setHeadID(outfitId);
        c.setShirtID(outfitId);
        c.setPantsID(outfitId);
        c.setBootsID(outfitId);

        c.setHead(outfit == null ? null : ItemUtil.unbreakable(outfit.getHead()));
        c.setShirt(outfit == null ? null : ItemUtil.unbreakable(outfit.getShirt()));
        c.setPants(outfit == null ? null : ItemUtil.unbreakable(outfit.getPants()));
        c.setBoots(outfit == null ? null : ItemUtil.unbreakable(outfit.getBoots()));

        inv.setHelmet(c.getHead());
        inv.setChestplate(c.getShirt());
        inv.setLeggings(c.getPants());
        inv.setBoots(c.getBoots());
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
        player.getRegistry().addEntry("updateOutfitSelection", true);
        openWardrobePage(player, page);
    }

    /**
     * Opens the wardrobe interface for the specified player at the given page.
     * <p>
     * This method generates and displays a menu page in the wardrobe manager, allowing the player
     * to select, preview, or reset outfits. Navigation buttons for traversing pages (if applicable)
     * are also included.
     *
     * <p><strong>Menu Features:</strong></p>
     * <ul>
     * <li>Shows available outfits and whether they are owned by the player.</li>
     * <li>Provides reset functionality for individual outfit slots (head, shirt, pants, boots).</li>
     * <li>Allows navigation between wardrobe pages.</li>
     * </ul>
     *
     * @param player The player for whom the wardrobe page should be displayed.
     * @param page   The current page number to open in the wardrobe interface.
     */
    @SuppressWarnings("unchecked")
    public void openWardrobePage(CPlayer player, int page) {
        List<MenuButton> buttons = new ArrayList<>(Arrays.asList(
                new MenuButton(16, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Head"),
                        ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, 0, OutfitSlot.HEAD, page, true),
                                ClickType.RIGHT, p -> setOutfit(p, 0, page, true))),
                new MenuButton(25, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Shirt"),
                        ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, 0, OutfitSlot.SHIRT, page, true),
                                ClickType.RIGHT, p -> setOutfit(p, 0, page, true))),
                new MenuButton(34, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Pants"),
                        ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, 0, OutfitSlot.PANTS, page, true),
                                ClickType.RIGHT, p -> setOutfit(p, 0, page, true))),
                new MenuButton(43, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Boots"),
                        ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, 0, OutfitSlot.BOOTS, page, true),
                                ClickType.RIGHT, p -> setOutfit(p, 0, page, true))),
                ParkManager.getMagicBandManager().getBackButton(49, BandInventory.MAIN)
        ));

        List<Outfit> fullList = new ArrayList<>(outfits.values());
        List<Outfit> sublist = fullList.subList((page - 1) * 6, Math.min(page * 6, fullList.size()));

        List<Integer> purchases = (List<Integer>) player.getRegistry().getEntry("outfitPurchases");
        Clothing c = (Clothing) player.getRegistry().getEntry("clothing");

        int i = 0;
        for (Outfit outfit : sublist) {
            boolean owns = purchases.contains(outfit.getId());
            ItemStack head = ItemUtil.unbreakable(outfit.getHead().clone());
            ItemStack shirt = ItemUtil.unbreakable(outfit.getShirt().clone());
            ItemStack pants = ItemUtil.unbreakable(outfit.getPants().clone());
            ItemStack boots = ItemUtil.unbreakable(outfit.getBoots().clone());
            if (!owns) {
                ItemMeta hm = head.getItemMeta();
                hm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(hm.getDisplayName()));
                head.setItemMeta(hm);
                ItemMeta sm = shirt.getItemMeta();
                sm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(sm.getDisplayName()));
                shirt.setItemMeta(sm);
                ItemMeta pm = pants.getItemMeta();
                pm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(pm.getDisplayName()));
                pants.setItemMeta(pm);
                ItemMeta bm = boots.getItemMeta();
                bm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(bm.getDisplayName()));
                boots.setItemMeta(bm);
            }
            if (c.getHeadID() == outfit.getId()) head.addUnsafeEnchantment(Enchantment.LUCK, 1);
            buttons.add(new MenuButton(10 + i, head,
                    ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, outfit.getId(), OutfitSlot.HEAD, page, owns),
                            ClickType.RIGHT, p -> setOutfit(p, outfit.getId(), page, owns)))
            );
            if (c.getShirtID() == outfit.getId()) shirt.addUnsafeEnchantment(Enchantment.LUCK, 1);
            buttons.add(new MenuButton(19 + i, shirt,
                    ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, outfit.getId(), OutfitSlot.SHIRT, page, owns),
                            ClickType.RIGHT, p -> setOutfit(p, outfit.getId(), page, owns)))
            );
            if (c.getPantsID() == outfit.getId()) pants.addUnsafeEnchantment(Enchantment.LUCK, 1);
            buttons.add(new MenuButton(28 + i, pants,
                    ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, outfit.getId(), OutfitSlot.PANTS, page, owns),
                            ClickType.RIGHT, p -> setOutfit(p, outfit.getId(), page, owns)))
            );
            if (c.getBootsID() == outfit.getId()) boots.addUnsafeEnchantment(Enchantment.LUCK, 1);
            buttons.add(new MenuButton(37 + i, boots,
                    ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, outfit.getId(), OutfitSlot.BOOTS, page, owns),
                            ClickType.RIGHT, p -> setOutfit(p, outfit.getId(), page, owns)))
            );
            i++;
        }
        if (page > 1)
            buttons.add(new MenuButton(48, ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Last Page"),
                    ImmutableMap.of(ClickType.LEFT, p -> openWardrobePage(p, page - 1))));
        if ((page * 6) < fullList.size())
            buttons.add(new MenuButton(50, ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Next Page"),
                    ImmutableMap.of(ClickType.LEFT, p -> openWardrobePage(p, page + 1))));
        new Menu(54, ChatColor.BLUE + "Wardrobe Manager Page " + page, player, buttons).open();
    }

    /**
     * Retrieves a legacy outfit by parsing data from the provided {@code Document} object.
     *
     * <p>This method constructs an {@code Outfit} instance using the properties stored
     * in the given {@code Document}, including item data for headgear, chest wear,
     * leggings, and boots. The method also applies NBT tags and custom display names
     * to each item according to the parsed attributes.
     *
     * <p><strong>Warning:</strong> This method uses deprecated APIs internally and should
     * only be used for processing legacy data.
     *
     * @param doc The {@link Document} containing the necessary data to construct the outfit.
     *            The document is expected to include:
     *            <ul>
     *                <li>{@code id}: An integer representing the outfit's identifier.</li>
     *                <li>{@code name}: A string representing the outfit's display name.</li>
     *                <li>Item data for headgear, chest wear, leggings, and boots, including IDs,
     *                metadata, and NBT tags.</li>
     *            </ul>
     *
     * @return An {@link Outfit} object constructed from the legacy data in the {@link Document},
     *         or {@code null} if an error occurs during data parsing or outfit creation.
     */
    @SuppressWarnings("deprecation")
    private Outfit getLegacyOutfit(Document doc) {
        try {
            int id = doc.getInteger("id");
            String ht = doc.getString("head");
            String ct = doc.getString("chest");
            String lt = doc.getString("leggings");
            String bt = doc.getString("boots");
            ItemStack h = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("headID")),
                            1, (short) doc.getInteger("headData", 0))
            );
            if (!ht.equals("")) {
                NbtFactory.setItemTag(h, new NbtTextSerializer().deserializeCompound(ht));
            }
            ItemStack s = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("chestID")),
                            1, (short) doc.getInteger("chestData", 0))
            );
            if (!ct.equals("")) {
                NbtFactory.setItemTag(s, new NbtTextSerializer().deserializeCompound(ct));
            }
            ItemStack l = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("leggingsID")),
                            1, (short) doc.getInteger("leggingsData", 0))
            );
            if (!lt.equals("")) {
                NbtFactory.setItemTag(l, new NbtTextSerializer().deserializeCompound(lt));
            }
            ItemStack b = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("bootsID")),
                            1, (short) doc.getInteger("bootsData", 0))
            );
            if (!bt.equals("")) {
                NbtFactory.setItemTag(b, new NbtTextSerializer().deserializeCompound(bt));
            }
            String name = ChatColor.translateAlternateColorCodes('&', doc.getString("name"));
            ItemMeta hm = h.getItemMeta();
            hm.setDisplayName(name + " Head");
            h.setItemMeta(hm);
            ItemMeta shm = s.getItemMeta();
            shm.setDisplayName(name + " Shirt");
            s.setItemMeta(shm);
            ItemMeta pm = l.getItemMeta();
            pm.setDisplayName(name + " Pants");
            l.setItemMeta(pm);
            ItemMeta bm = b.getItemMeta();
            bm.setDisplayName(name + " Boots");
            b.setItemMeta(bm);
            return new Outfit(id, name, h, s, l, b);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
