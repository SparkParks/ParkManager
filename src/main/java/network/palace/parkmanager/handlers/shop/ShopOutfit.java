package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import network.palace.core.economy.currency.CurrencyType;

/**
 * Represents a specific type of {@code ShopEntry} in the shop system, primarily for outfits.
 *
 * <p>The {@code ShopOutfit} class extends the base {@code ShopEntry} class, adding
 * functionality tailored to outfits available in the shop. Each {@code ShopOutfit} includes
 * a unique identifier for the outfit it represents, in addition to the common properties
 * inherited from {@code ShopEntry}.
 *
 * <p>Key features:
 * <ul>
 *   <li><b>outfitId</b>: A unique identifier for the outfit associated with this shop entry.</li>
 *   <li>Inherits all properties from {@code ShopEntry}, including:
 *       <ul>
 *           <li>id: Unique identifier for the shop entry.</li>
 *           <li>cost: The cost associated with the shop entry.</li>
 *           <li>currencyType: The currency type to be used for the entry.</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p>This class is used to manage and display outfits that can be purchased in the shop
 * system, enhancing the shop's offerings with outfit-specific data.
 */
public class ShopOutfit extends ShopEntry {
    /**
     * Represents the unique identifier for a specific outfit associated with a shop entry.
     *
     * <p>The <code>outfitId</code> field is used to distinguish different outfits available
     * within the shop system, enabling outfit-specific operations and management. Each outfit
     * in the shop is assigned a unique integer value that is stored in this field.
     *
     * <p>Key characteristics:
     * <ul>
     *   <li>It is a unique identifier within the context of the shop system.</li>
     *   <li>Used to link the shop entry to its corresponding outfit data or assets.</li>
     *   <li>Populated through the constructor or other relevant initialization methods.</li>
     *   <li>Accessed using getter methods due to the @Getter annotation from Lombok.</li>
     * </ul>
     *
     * <p>This field is integral to the functionality of the <code>ShopOutfit</code> class,
     * ensuring outfits can be identified and managed efficiently in the shop's operations.
     */
    @Getter private int outfitId;

    /**
     * Constructs a new {@code ShopOutfit} instance with the specified properties.
     *
     * <p>This constructor initializes a shop entry specifically for outfits,
     * including a unique identifier for the outfit, along with the cost and
     * currency type inherited from the {@code ShopEntry} class.
     *
     * @param id The unique identifier for this shop entry.
     * @param outfitId The unique identifier for the outfit associated with this shop entry.
     * @param cost The cost associated with this shop entry, represented as an integer.
     */
    public ShopOutfit(int id, int outfitId, int cost) {
        super(id, cost, CurrencyType.TOKENS);
        this.outfitId = outfitId;
    }
}