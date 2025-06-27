package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import network.palace.core.economy.currency.CurrencyType;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a shop item available for purchase.
 *
 * <p> This class extends {@link ShopEntry} to include a specific item that can be bought
 * in a shop. It inherits the basic properties of a shop entry such as ID, cost, and
 * {@link CurrencyType}, and adds an additional property to represent the item itself.
 *
 * <p> Key properties of a {@code ShopItem}:
 * <ul>
 *   <li><b>id</b>: A unique identifier for the shop item, inherited from {@link ShopEntry}.</li>
 *   <li><b>cost</b>: The cost of the shop item, inherited from {@link ShopEntry}.</li>
 *   <li><b>currencyType</b>: The currency type associated with the cost, inherited from {@link ShopEntry}.</li>
 *   <li><b>item</b>: The {@link ItemStack} object representing the item being sold.</li>
 * </ul>
 *
 * <p> This class is typically used in shop systems to represent individual items that can
 * be purchased by users. It provides a clear structure for specifying the cost and
 * description of the item.
 *
 * <p> Instances of {@code ShopItem} are immutable after creation, meaning all its properties
 * are assigned during construction and cannot be changed later.
 *
 * <p> Example usage:
 * <ul>
 *   <li>Construct a new {@code ShopItem} with a unique ID, associated {@code ItemStack}, cost, and currency type.</li>
 *   <li>Retrieve its {@code item} property to access the specific item being sold.</li>
 * </ul>
 *
 * <p> This class relies on the {@code @Getter} annotation to provide read-only access
 * to its properties, ensuring encapsulation and immutability.
 */
public class ShopItem extends ShopEntry {
    /**
     * Represents the {@link ItemStack} associated with this shop item.
     *
     * <p>This property holds the actual item being sold in the shop, providing a way to access
     * its attributes and functionality. The {@link ItemStack} object typically includes
     * information such as the type of item, quantity, and any special metadata or properties
     * associated with it.
     *
     * <p>Key features of this property:
     * <ul>
     *   <li>Encapsulates the item that can be purchased.</li>
     *   <li>Read-only access via the {@code @Getter} annotation, ensuring immutability
     *       for this property after the {@code ShopItem} has been constructed.</li>
     *   <li>Designed to work seamlessly as part of the shop system's representation of
     *       purchasable items.</li>
     * </ul>
     *
     * <p>This field is initialized through the {@code ShopItem} constructor and cannot
     * be modified once assigned.
     */
    @Getter private ItemStack item;

    /**
     * Constructs a new {@code ShopItem} instance with the specified properties.
     *
     * <p> This constructor initializes a shop item with a unique ID, the associated
     * {@link ItemStack} object representing the item to be sold, the cost of the item,
     * and the currency type used for the transaction. It extends the functionality of
     * the {@link ShopEntry} class by adding a specific {@code item} to the shop entry.
     *
     * @param id The unique identifier for this shop item.
     * @param item The {@link ItemStack} object representing the item for sale.
     * @param cost The cost associated with this shop item, represented as an integer.
     * @param currencyType The type of currency used for the cost of this item, as a {@link CurrencyType}.
     */
    public ShopItem(int id, ItemStack item, int cost, CurrencyType currencyType) {
        super(id, cost, currencyType);
        this.item = item;
    }
}