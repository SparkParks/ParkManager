package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.handlers.outfits.Outfit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a Shop that contains items and outfits available for purchase or interaction in a park system.
 *
 * <p>The {@code Shop} class is responsible for managing a collection of shop items and shop outfits.
 * It provides functionality to add, remove, retrieve, and sort the items and outfits in the shop.
 * The shop is associated with a specific park, has a unique identifier, and can be identified by its name, warp, and display item.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Stores a list of {@link ShopItem} objects representing individual items available in the shop.</li>
 *   <li>Stores a list of {@link ShopOutfit} objects representing outfits available for purchase or interaction.</li>
 *   <li>Maintains a unique ID for each item in the shop, ensuring each item's distinct identification within the shop.</li>
 *   <li>Enables adding, removing, and retrieving both {@link ShopItem} and {@link ShopOutfit} objects dynamically.</li>
 *   <li>Supports sorting of items by their display names and outfits by their associated names.</li>
 * </ul>
 *
 * <h2>Key Methods:</h2>
 * <ul>
 *   <li><b>addItem:</b> Adds a new {@link ShopItem} to the shop and automatically sorts the items list.</li>
 *   <li><b>removeItem:</b> Removes an existing {@link ShopItem} by its unique identifier.</li>
 *   <li><b>getItem:</b> Retrieves an existing {@link ShopItem} by its unique identifier.</li>
 *   <li><b>addOutfit:</b> Adds a new {@link ShopOutfit} to the shop and automatically sorts the outfits list.</li>
 *   <li><b>removeOutfit:</b> Removes an existing {@link ShopOutfit} by its unique identifier.</li>
 *   <li><b>getOutfit:</b> Retrieves an existing {@link ShopOutfit} by its unique identifier.</li>
 *   <li><b>nextId:</b> Generates the next unique item ID for use when creating new items in the shop.</li>
 * </ul>
 *
 * <h2>Sorting Mechanism:</h2>
 * <p>The shop ensures that its lists are consistently sorted to maintain a user-friendly display order:</p>
 * <ul>
 *   <li><b>Items:</b> Sorted alphabetically by the display name of the items.</li>
 *   <li><b>Outfits:</b> Sorted alphabetically by the names of the associated outfits. Invalid outfits (e.g., outfits that no longer exist) are automatically removed during the sorting
 *  process.</li>
 * </ul>
 *
 * <h2>Associated Classes:</h2>
 * <ul>
 *   <li>{@link ParkType} - Represents the park the shop is associated with.</li>
 *   <li>{@link ShopItem} - Represents individual items available in the shop.</li>
 *   <li>{@link ShopOutfit} - Represents outfits available for purchase or interaction.</li>
 *   <li>{@link ItemStack} - Represents the display item for the shop.</li>
 * </ul>
 *
 * <h2>Usage Notes:</h2>
 * <p>The {@code Shop} class is designed to be a central component for managing in-game shops within a park system.
 * Developers should ensure that the provided lists of items and outfits are properly populated during the shop's initialization.
 * Adding, removing, and retrieving items or outfits should be managed through the provided methods to ensure consistency and proper sorting.</p>
 */
public class Shop {
    /**
     * <p>Represents the next available ID for a new item being added to the shop.</p>
     *
     * <p>This variable is used to generate unique IDs for new {@code ShopItem} and
     * {@code ShopOutfit} instances added to the shop. The value increments automatically
     * as new items or outfits are added, ensuring that no two entries share the same ID.</p>
     *
     * <ul>
     *     <li>Scope: {@code private}</li>
     *     <li>Type: {@code int}</li>
     * </ul>
     */
    private int nextItemId;

    /**
     * <p>
     * Represents the unique identifier for this {@code Shop}.
     * </p>
     * <p>
     * This identifier is utilized to distinguish individual shops and maintain their uniqueness
     * in the system.
     * It serves as a key property for identifying shop instances in various operations such
     * as retrieval, modification, or deletion within the application.
     * </p>
     */
    @Getter private String id;

    /**
     * Represents the park type associated with the shop.
     *
     * <p>The {@code park} field categorizes the shop's association with a specific {@link ParkType}.
     * This information is used to link the shop to a theme park, resort, water park, or other destination.</p>
     *
     * <p>Key characteristics of this field include:</p>
     * <ul>
     *   <li>Acts as a categorization mechanism for the shop within the context of park management systems.</li>
     *   <li>Enables filtering, organizational management, or contextual operations based on the park type.</li>
     *   <li>Facilitates integration with other features like item and outfit management, linking them to specific park facilities.</li>
     * </ul>
     *
     * <p>This field is immutable for a specific {@link Shop} instance, ensuring that each shop remains
     * consistently linked to the park it was initially created for.</p>
     */
    @Getter private ParkType park;

    /**
     * Represents the name of the shop.
     * <p>
     * This field holds the display name of the shop, which is used to
     * identify the shop instance within the game or application. It may
     * also serve as a means of presenting the shop's label to players or users.
     * </p>
     */
    @Getter private String name;

    /**
     * Represents the warp location associated with the shop.
     *
     * <p>This value indicates the name or identifier of the warp point
     * used in conjunction with the shop functionality. It can denote
     * a specific teleport location within the system for directing users
     * or activities related to the shop.</p>
     *
     * <p><b>Access:</b></p>
     * <ul>
     *     <li>Read-Only: This value is immutable and can be accessed
     *     using the generated getter method.</li>
     * </ul>
     *
     * <p><b>Usage:</b> This is primarily intended to link the shop's operations
     * to a specific warp location in the system, enabling contextual teleportation
     * or alignment with certain features or zones.</p>
     */
    @Getter private String warp;

    /**
     * Represents the primary {@link ItemStack} associated with the shop. This item
     * serves as the main visual or functional representation of the shop in-game.
     *
     * <p>The item is commonly used to display shop information or interact with
     * players. It may include customization through item properties like names,
     * lore, or other meta attributes to align with the intended shop appearance.
     *
     * <p><strong>Usage:</strong>
     * <ul>
     *   <li>Facilitates UI representation of the shop.</li>
     *   <li>Defines the icon or symbolic reference for this shop in menus or inventories.</li>
     * </ul>
     */
    @Getter private ItemStack item;

    /**
     * <p>
     * Represents the collection of items available in the shop.
     * </p>
     *
     * <p>
     * Each item in this collection is an instance of {@link ShopItem}, which includes
     * the item's details such as its unique identifier, associated {@link ItemStack},
     * cost, and currency type.
     * </p>
     *
     * <p>
     * This list can be managed through various methods provided by the class, such as adding,
     * retrieving, or removing individual shop items. It serves as the primary storage for
     * the shop's inventory of items.
     * </p>
     *
     * <ul>
     * <li>Each {@link ShopItem} represents an individual product available in the shop.</li>
     * <li>Items can be sorted, retrieved, or removed using dedicated methods.</li>
     * </ul>
     *
     * @see ShopItem
     * @see Shop#addItem(ShopItem)
     * @see Shop#removeItem(int)
     * @see Shop#getItem(int)
     */
    @Getter private List<ShopItem> items;

    /**
     * <p>The <code>outfits</code> variable represents a collection of {@link ShopOutfit} objects
     * available in the current shop. These outfits are specific purchasable items or entities
     * that can be obtained by users in exchange for in-game currency.</p>
     *
     * <p>This list is generally managed by the shop, allowing addition, removal, and sorting
     * of outfits for efficient access. Each outfit encapsulated in the list contains details
     * such as its unique identifier and cost in a specific currency type.</p>
     *
     * <ul>
     *     <li><b>Type:</b> {@code List<ShopOutfit>}</li>
     *     <li><b>Accessibility:</b> Read-only (Getter provided)</li>
     *     <li><b>Managed by:</b> {@link Shop} class methods</li>
     * </ul>
     *
     * @see Shop
     * @see ShopOutfit
     */
    @Getter private List<ShopOutfit> outfits;

    /**
     * Constructs a new {@code Shop} instance with the specified details.
     *
     * <p>The {@code Shop} class represents a specific shop entity within a park or resort system.
     * It includes information about the shop's unique identifier, its associated park, display name,
     * warp point, display item, and the collection of items and outfits available in the shop.</p>
     *
     * <p>This constructor initializes the shop with the given information and also ensures that the
     * items list is sorted based on their display names upon creation.</p>
     *
     * @param id     The unique identifier for this shop.
     * @param park   The {@link ParkType} associated with this shop.
     * @param name   The name or display title of the shop.
     * @param warp   The location or warp point associated with the shop.
     * @param item   The {@link ItemStack} used to represent the shop visually.
     * @param items  A {@link List} of {@link ShopItem}s available in the shop.
     * @param outfits A {@link List} of {@link ShopOutfit}s available in the shop.
     */
    public Shop(String id, ParkType park, String name, String warp, ItemStack item, List<ShopItem> items, List<ShopOutfit> outfits) {
        nextItemId = items.size();
        this.id = id;
        this.park = park;
        this.name = name;
        this.warp = warp;
        this.item = item;
        this.items = items;
        this.outfits = outfits;
        sortItems();
    }

    /**
     * Adds a {@link ShopItem} to the shop's item collection and ensures the collection is sorted.
     *
     * <p>This method allows the addition of a new {@link ShopItem} instance to the internal list of
     * items maintained by the shop. Once added, the list is re-sorted based on the display names of
     * the items to maintain an appropriate ordering.</p>
     *
     * @param item The {@link ShopItem} instance to be added to the shop's item collection.
     *             This must represent a valid shop item with the necessary attributes such
     *             as cost and display information.
     */
    public void addItem(ShopItem item) {
        items.add(item);
        sortItems();
    }

    /**
     * Sorts the {@code items} list in ascending order based on the display names of the items.
     *
     * <p>The sorting is performed using a comparator that retrieves the item's display name via
     * the following chain of method calls:
     * {@link ShopItem#getItem()} → {@link ItemStack#getItemMeta()} →
     * {@link org.bukkit.inventory.meta.ItemMeta#getDisplayName()}.</p>
     *
     * <p>This method ensures that the {@code items} list is always sorted correctly, providing
     * an organized view of the shop items. It is called during shop initialization and whenever
     * new items are added to the shop.</p>
     *
     * <p><b>Note:</b> The {@code items} list must contain valid {@link ShopItem} objects,
     * and each item's {@code ItemStack} must have a non-null {@code ItemMeta} to avoid
     * potential {@link NullPointerException} during sorting.</p>
     */
    private void sortItems() {
        items.sort(Comparator.comparing(o -> o.getItem().getItemMeta().getDisplayName()));
    }

    /**
     * Retrieves a {@link ShopItem} from the shop's inventory based on its unique identifier.
     * <p>
     * This method iterates through the list of shop items and returns the first item
     * that matches the provided ID. If no such item is found, the method returns {@code null}.
     * </p>
     *
     * @param id The unique identifier of the shop item to retrieve.
     * @return The {@link ShopItem} with the specified ID, or {@code null} if no such item exists.
     */
    public ShopItem getItem(int id) {
        for (ShopItem item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    /**
     * Removes a {@link ShopItem} from the shop's item collection based on its unique identifier.
     *
     * <p>This method retrieves the {@link ShopItem} using the specified ID by calling {@link #getItem(int)}.
     * If the item is found, it is removed from the internal {@code items} list maintained by the shop.
     * If no item is associated with the provided ID, the method performs no action.</p>
     *
     * <p><b>Note:</b> The {@code items} list is expected to contain valid {@link ShopItem} objects,
     * and the removal of the item also ensures the item is no longer accessible in the shop's inventory.</p>
     *
     * @param id The unique identifier of the {@link ShopItem} to be removed.
     */
    public void removeItem(int id) {
        ShopItem item = getItem(id);
        if (item != null) items.remove(item);
    }

    /**
     * Adds a {@link ShopOutfit} to the shop's collection of outfits and ensures the collection is sorted.
     *
     * <p>This method enables the addition of a new {@link ShopOutfit} instance to the shop's internal
     * list of outfits. After the outfit is added, the list is re-sorted to maintain a consistent order,
     * which may be based on the associated outfit names or other criteria as defined in the {@code sortOutfits()} method.</p>
     *
     * @param outfit The {@link ShopOutfit} instance to be added to the shop's outfit collection.
     *               This must represent a valid shop outfit, with attributes such as {@code outfitId},
     *               {@code cost}, and {@code currencyType} properly defined.
     */
    public void addOutfit(ShopOutfit outfit) {
        outfits.add(outfit);
        sortOutfits();
    }

    /**
     * Sorts the list of {@code outfits} in ascending order based on their names and removes
     * any entries with invalid or non-existent outfit IDs.
     *
     * <p>This method performs two key operations on the {@code outfits} list:
     * <ul>
     *     <li>
     *         <b>Sorting:</b> The {@code outfits} list is sorted in ascending order by the name
     *         of the {@link Outfit} associated with each {@link ShopOutfit}. The name is retrieved
     *         using the {@link ParkManager#getWardrobeManager()} to access the {@link Outfit} object
     *         through its ID.
     *     </li>
     *     <li>
     *         <b>Removal of invalid outfits:</b> Any {@link ShopOutfit} that references an outfit
     *         ID which cannot be resolved (i.e., no corresponding {@link Outfit} exists) is removed
     *         from the list. Such unidentified outfits are tracked using a temporary removal list
     *         {@code toRemove}.
     *     </li>
     * </ul>
     *
     * <p><b>Implementation Details:</b>
     * <ul>
     *     <li>
     *         The {@code outfits} list is sorted using a comparator that retrieves the {@link Outfit}
     *         by its ID and evaluates the name of the outfit.
     *     </li>
     *     <li>
     *         If the outfit is not found (i.e., {@code null}), its ID is added to a list of IDs
     *         to be removed. The removal is performed after sorting to avoid issues with modifying
     *         the list during iteration.
     *     </li>
     *     <li>
     *         Invalid entries are filtered out using {@link List#removeIf(Predicate)} with a predicate
     *         to match IDs in the {@code toRemove} list.
     *     </li>
     * </ul>
     *
     * <p><b>Dependencies:</b>
     * This method relies on external components for proper functioning:
     * <ul>
     *     <li>{@link ParkManager#getWardrobeManager()} - Provides access to wardrobe management for fetching outfits.</li>
     *     <li>{@link Outfit} - Represents individual outfits and provides access to names for sorting.</li>
     * </ul>
     *
     * <p><b>Note:</b> This method assumes that the {@code outfits} list contains valid {@link ShopOutfit} objects
     * and that the {@link ParkManager#getWardrobeManager()} is correctly initialized and operational.
     */
    private void sortOutfits() {
        List<Integer> toRemove = new ArrayList<>();
        outfits.sort(Comparator.comparing(o -> {
            Outfit outfit = ParkManager.getWardrobeManager().getOutfit(o.getOutfitId());
            if (outfit != null) return outfit.getName();
            toRemove.add(o.getOutfitId());
            return null;
        }));
        outfits.removeIf(shopOutfit -> toRemove.contains(shopOutfit.getOutfitId()));
    }

    /**
     * Retrieves a {@link ShopOutfit} from the shop's collection of outfits based on its unique identifier.
     *
     * <p>This method iterates through the list of shop outfits and returns the first outfit
     * that matches the provided ID. If no such outfit is found, the method returns {@code null}.</p>
     *
     * @param id The unique identifier of the {@link ShopOutfit} to retrieve.
     *           <ul>
     *             <li>This ID should correspond to the ID assigned to a {@link ShopOutfit} within the shop.</li>
     *           </ul>
     * @return The {@link ShopOutfit} with the specified ID, or {@code null} if no such outfit exists.
     */
    public ShopOutfit getOutfit(int id) {
        for (ShopOutfit outfit : outfits) {
            if (outfit.getId() == id) {
                return outfit;
            }
        }
        return null;
    }

    /**
     * Removes a {@link ShopOutfit} with the specified ID from the shop's outfit collection.
     *
     * <p>This method locates the {@link ShopOutfit} in the shop's {@code outfits} list by its unique
     * identifier and removes it if found.</p>
     *
     * <p><b>Note:</b> If no outfit with the given ID exists in the shop's collection, the method
     * does nothing.</p>
     *
     * @param id The unique identifier of the {@link ShopOutfit} to be removed.
     */
    public void removeOutfit(int id) {
        ShopOutfit outfit = getOutfit(id);
        if (outfit != null) outfits.remove(outfit);
    }

    /**
     * Generates and returns the next unique identifier for a shop item or outfit.
     *
     * <p>This method increments and returns the value of the {@code nextItemId} field,
     * which maintains the next unique identifier for items or outfits in the shop.
     * The returned value can be used to assign a unique identifier to a new shop item
     * or outfit to ensure no duplication within the shop system.</p>
     *
     * @return The next unique identifier as an {@code int} for a shop item or outfit.
     */
    public int nextId() {
        return nextItemId++;
    }
}
