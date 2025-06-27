package network.palace.parkmanager.handlers.outfits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * The {@code Outfit} class represents an outfit that can include various wearable items,
 * such as headgear, shirt, pants, and boots. Each outfit is uniquely identified by an ID and has a name.
 *
 * <p>The {@code Outfit} class is designed to encapsulate a set of items representing a full outfit,
 * allowing for storage and retrieval of related clothing or cosmetic items.
 *
 * <ul>
 *     <li>{@code id} - A unique identifier for the outfit.</li>
 *     <li>{@code name} - The name of the outfit.</li>
 *     <li>{@code head} - The item worn on the head.</li>
 *     <li>{@code shirt} - The item worn as a shirt.</li>
 *     <li>{@code pants} - The item worn as pants.</li>
 *     <li>{@code boots} - The item worn as boots.</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public class Outfit {
    /**
     * A unique identifier for the outfit.
     *
     * <p>This field represents a numeric ID that is used to uniquely identify an
     * {@code Outfit} instance. Each outfit within the application should have a
     * distinct value for {@code id}.
     */
    private int id;

    /**
     * Represents the name of the outfit.
     *
     * <p>This field stores a human-readable name that identifies the outfit.
     * The name can be used to describe or label the outfit for display or selection purposes.</p>
     */
    private String name;

    /**
     * Represents the item worn on the head as part of an {@code Outfit}.
     *
     * <p>This field stores the {@code ItemStack} that defines the headgear
     * associated with the outfit. It can represent various types of headwear,
     * such as hats, helmets, or other head accessories.
     */
    private ItemStack head;

    /**
     * Represents the shirt item of an {@code Outfit}.
     *
     * <p>This variable is a part of the {@code Outfit} class and holds the {@code ItemStack}
     * that represents the shirt worn in the outfit. It can be used to retrieve or modify
     * the shirt associated with this outfit.</p>
     *
     * <p>The value of this variable is expected to correspond to an item stack representing
     * a shirt in a specific application or game context. It can represent various visual
     * appearances or functional attributes tied to the outfit's shirt.</p>
     */
    private ItemStack shirt;

    /**
     * Represents the pants item in an outfit.
     *
     * <p>The {@code pants} variable holds an {@code ItemStack} object, which specifies the item or cosmetic
     * piece designated as pants within the {@code Outfit} instance. This item is part of the clothing or
     * armor elements that together define the full outfit.
     *
     * <p>This field is private and can be accessed through the corresponding getter method.
     */
    private ItemStack pants;

    /**
     * Represents the boots item that is part of an outfit.
     *
     * <p>This field is used to store the {@link ItemStack} object corresponding
     * to the boots worn in the outfit. The boots are an optional component of
     * an {@code Outfit}, and they can represent cosmetic or functional footwear
     * in the context of the outfit system.
     *
     * <p>Key characteristics of this field:
     * <ul>
     *     <li>Encapsulates the information about the boots item.</li>
     *     <li>Allows retrieval or manipulation of the boots item in an {@code Outfit}.</li>
     * </ul>
     */
    private ItemStack boots;
}
