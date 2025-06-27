package network.palace.parkmanager.handlers.outfits;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The {@code OutfitItem} class represents an individual item used in an outfit.
 *
 * <p>This class is primarily used to link specific items to their associated outfits
 * by referencing an {@code outfitId}. It also includes a {@code cost} field to define
 * the cost or value associated with this item.
 *
 * <ul>
 *     <li>{@code outfitId} - A unique identifier linking the item to a specific outfit.</li>
 *     <li>{@code cost} - The numerical value or cost associated with the item.</li>
 * </ul>
 *
 * <p>Instances of {@code OutfitItem} serve as utility representations of individual
 * outfit components which can include pricing and categorization data for usage
 * in various systems such as outfit management, inventory control, or cosmetic customization.</p>
 */
@Getter
@AllArgsConstructor
public class OutfitItem {
    /**
     * A unique identifier for a specific outfit.
     *
     * <p>The {@code outfitId} field is an integer that is used to associate
     * an individual outfit component, represented by an {@code OutfitItem},
     * with a specific outfit. Each {@code outfitId} should correspond to
     * a unique {@code Outfit} instance within the system.</p>
     *
     * <p>Key characteristics of {@code outfitId}:</p>
     * <ul>
     *     <li>Acts as a linking mechanism between an {@code OutfitItem}
     *     and a corresponding {@code Outfit}.</li>
     *     <li>Should be unique for distinct {@code Outfit} instances.</li>
     *     <li>Used as an identifier to manage and reference outfits within
     *     the application or game environment.</li>
     * </ul>
     */
    private int outfitId;

    /**
     * Represents the cost associated with an individual outfit item.
     *
     * <p>This field stores a numerical value that denotes the cost or value linked to a specific
     * item in an outfit. The {@code cost} can be utilized to determine pricing, categorization,
     * or valuation of the item in various systems such as inventory management or customization configurations.</p>
     *
     * <p>Key characteristics of this field:
     * <ul>
     *     <li>Quantifies the item's cost in integer format.</li>
     *     <li>Primarily used in defining pricing models for outfit components.</li>
     *     <li>Supports outfit-related functionalities by including cost-value data.</li>
     * </ul>
     */
    private int cost;
}
