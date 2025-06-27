package network.palace.parkmanager.handlers.outfits;

/**
 * The {@code OutfitSlot} enum represents the different wearable slots available for customizable outfits.
 *
 * <p>Each slot corresponds to a specific part of an outfit and is used in conjunction with the {@code Outfit}
 * class to define the items worn in those slots. This enumeration makes it possible to categorize and
 * reference individual outfit components.
 *
 * <p>Available slots:
 * <ul>
 *     <li>{@code HEAD} - Represents the headgear slot.</li>
 *     <li>{@code SHIRT} - Represents the shirt or upper body slot.</li>
 *     <li>{@code PANTS} - Represents the pants or lower body slot.</li>
 *     <li>{@code BOOTS} - Represents the footwear or boots slot.</li>
 * </ul>
 */
public enum OutfitSlot {
    HEAD, SHIRT, PANTS, BOOTS
}
