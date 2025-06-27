package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import network.palace.core.economy.currency.CurrencyType;

/**
 * Represents a base class for entries in a shop system with common properties and functionality.
 * This class serves as a foundation for specific shop entry types, such as items or outfits.
 *
 * <p> Each shop entry includes:
 * <ul>
 *   <li><b>id</b>: A unique identifier for the shop entry.</li>
 *   <li><b>cost</b>: The cost of the shop entry, represented as an integer.</li>
 *   <li><b>currencyType</b>: The type of currency used for the cost of the entry.</li>
 * </ul>
 *
 * <p> Subclasses should extend this class to implement additional specific properties and behaviors for different types of shop entries.
 */
@Getter
public abstract class ShopEntry {
    /**
     * A unique identifier for the shop entry.
     *
     * <p>This identifier is used to distinguish each shop entry within the system, ensuring that
     * each entry can be uniquely referenced and managed. It is typically assigned at the time
     * of creation and remains consistent for the lifecycle of the shop entry.
     */
    private int id;

    /**
     * Represents the cost of the shop entry.
     *
     * <p>This variable stores the cost as an integer value, which indicates
     * the amount required for purchasing the corresponding shop entry. The
     * cost should align with the currency type defined for the entry.</p>
     *
     * <p>Key points:</p>
     * <ul>
     *   <li>The cost is specific to the shop entry it belongs to.</li>
     *   <li>This value is defined during the creation of the shop entry.</li>
     *   <li>The cost is expected to remain consistent unless the shop entry is updated.</li>
     * </ul>
     */
    private int cost;

    /**
     * Represents the type of currency associated with the cost of a shop entry.
     *
     * <p> This variable specifies the denomination or category of currency used
     * to determine the payment required for the item represented by this shop entry.
     *
     *
     * <p> The <code>currencyType</code> works in conjunction with the <code>cost</code>
     * field to fully define the pricing details of a shop entry.
     */
    private CurrencyType currencyType;

    /**
     * Constructs a new {@code ShopEntry} instance with the specified properties.
     *
     * <p> This constructor initializes a shop entry with a unique identifier, its cost,
     * and the type of currency that should be used for the transaction.
     *
     * @param id The unique identifier for this shop entry.
     * @param cost The cost associated with this shop entry, represented as an integer.
     * @param currencyType The type of currency used for the cost of this entry.
     */
    public ShopEntry(int id, int cost, CurrencyType currencyType) {
        this.id = id;
        this.cost = cost;
        this.currencyType = currencyType;
    }
}
