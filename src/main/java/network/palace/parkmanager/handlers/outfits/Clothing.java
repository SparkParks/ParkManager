package network.palace.parkmanager.handlers.outfits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

/**
 * The {@code Clothing} class represents an outfit system for a player,
 * consisting of various pieces of wearable items such as a head, shirt, pants, and boots.
 * Each piece of clothing also associates with its respective unique ID.
 * <p>
 * This class is used to manage and hold the player's current clothing items
 * and their IDs for persistence or outfit handling purposes.
 * </p>
 *
 * <p><b>Structure and Fields:</b></p>
 * <ul>
 *   <li><b>head:</b> Represents the headwear {@link ItemStack} worn by the player.</li>
 *   <li><b>headID:</b> Unique identifier associated with the headwear.</li>
 *   <li><b>shirt:</b> Represents the shirt {@link ItemStack} worn by the player.</li>
 *   <li><b>shirtID:</b> Unique identifier associated with the shirt.</li>
 *   <li><b>pants:</b> Represents the pants {@link ItemStack} worn by the player.</li>
 *   <li><b>pantsID:</b> Unique identifier associated with the pants.</li>
 *   <li><b>boots:</b> Represents the boots {@link ItemStack} worn by the player.</li>
 *   <li><b>bootsID:</b> Unique identifier associated with the boots.</li>
 * </ul>
 *
 * <p><b>Usage:</b></p>
 * <p>
 * An instance of this class is used to manage the outfit configuration of a player.
 * The clothing items can be retrieved, updated, and persisted within the framework
 * for further gameplay functionalities. This is particularly utilized in outfit selection
 * and inventory synchronization mechanisms.
 * </p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Supports modification of each clothing item and its associated ID.</li>
 *   <li>Provides an efficient structure for tracking outfit states.</li>
 * </ul>
 *
 * <p><b>Annotations:</b></p>
 * <ul>
 *   <li><b>@Getter:</b> Automatically generates getters for the fields.</li>
 *   <li><b>@Setter:</b> Automatically generates setters for the fields.</li>
 *   <li><b>@AllArgsConstructor:</b> Generates a constructor initializing all fields.</li>
 *   <li><b>@NoArgsConstructor:</b> Generates a no-argument constructor.</li>
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Clothing {
    /**
     * Represents the headwear item equipped by the player.
     * This is an {@link ItemStack} object, holding the appearance, type, and associated
     * attributes of the headwear.
     *
     * <p><b>Usage Details:</b></p>
     * <ul>
     *   <li>This field is part of the player's outfit system, managed by the {@code Clothing} class.</li>
     *   <li>Null indicates no headwear is currently equipped.</li>
     *   <li>Can be updated or retrieved to reflect changes in the player's appearance or equipment.</li>
     * </ul>
     */
    private ItemStack head = null;

    /**
     * Represents the unique identifier associated with the headwear item equipped
     * by a player.
     *
     * <p>
     * This ID is primarily used to track and reference the specific headwear
     * associated with the player. It allows easy management and persistence
     * of the player's headwear within the outfit system.
     * </p>
     *
     * <p><b>Constraints and Usage:</b></p>
     * <ul>
     *   <li>Must be a non-negative integer.</li>
     *   <li>Serves as a unique key or lookup reference for the associated headwear item.</li>
     *   <li>Can be utilized in inventory synchronization, outfit management, or gameplay logic.</li>
     * </ul>
     */
    private int headID = 0;

    /**
     * Represents the {@link ItemStack} for the shirt piece of clothing worn by a player.
     * <p>
     * This variable is part of the {@code Clothing} class, which manages a player's wearable items.
     * It holds the {@code ItemStack} for the player's shirt, or {@code null} if no shirt is worn.
     * </p>
     *
     * <p><b>Key Characteristics:</b></p>
     * <ul>
     *   <li>Stores the {@link ItemStack} for the shirt outfit item.</li>
     *   <li>Can be updated to reflect the current shirt worn by the player.</li>
     *   <li>Initialized as {@code null}, indicating no shirt is equipped by default.</li>
     * </ul>
     */
    private ItemStack shirt = null;

    /**
     * Represents the unique identifier associated with the shirt item equipped by a player.
     * <p>
     * This ID is used to track and manage the specific shirt associated with the player
     * within the outfit system. It allows for efficient referencing and persistence of
     * the shirt selection for gameplay and inventory functionalities.
     * </p>
     *
     * <p><b>Usage Details:</b></p>
     * <ul>
     *   <li>Acts as an identifier for the shirt item, facilitating outfit management.</li>
     *   <li>Used in synchronization of player appearance with other game components.</li>
     *   <li>Stores a non-negative integer, with {@code 0} as the default value indicating no shirt equipped.</li>
     * </ul>
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>The value should be a valid integer representing an existing shirt ID.</li>
     *   <li>Changes to this ID should correspond with updates to the player's shirt selection.</li>
     * </ul>
     */
    private int shirtID = 0;

    /**
     * Represents the {@link ItemStack} for the pants piece of clothing worn by a player.
     * <p>
     * This variable is part of the {@code Clothing} class, which manages a player's wearable items.
     * It holds the {@code ItemStack} for the player's pants, or {@code null} if no pants are worn.
     * </p>
     *
     * <p><b>Key Characteristics:</b></p>
     * <ul>
     *   <li>Stores the {@link ItemStack} representing the pants outfit item.</li>
     *   <li>Initialized as {@code null}, indicating no pants are equipped by default.</li>
     *   <li>Can be updated to reflect the pants currently equipped by the player.</li>
     * </ul>
     *
     * <p>
     * This field plays a crucial role in the player's outfit configuration within
     * the outfit management system, providing a way to track, retrieve, and modify
     * the pants being worn.
     * </p>
     */
    private ItemStack pants = null;

    /**
     * Represents the unique identifier associated with the pants item worn by a player.
     * <p>
     * This variable is part of the {@code Clothing} class, used to manage and track the player's
     * pants within the outfit system. It serves as a reference ID for persistence and outfit handling.
     * </p>
     *
     * <p><b>Key Characteristics:</b></p>
     * <ul>
     *   <li>Stores an integer that uniquely identifies the pants item.</li>
     *   <li>Used to synchronize or reference pants-related data in the outfit system.</li>
     *   <li>Default value is 0, indicating that no pants are assigned by default.</li>
     * </ul>
     *
     * <p><b>Constraints and Usage:</b></p>
     * <ul>
     *   <li>This ID should be a non-negative integer.</li>
     *   <li>Can be updated to reflect changes in the player's equipped pants.</li>
     *   <li>Commonly utilized for gameplay features such as outfit saving, inventory updates,
     *       and outfit retrieval logic.</li>
     * </ul>
     */
    private int pantsID = 0;

    /**
     * Represents the boots item of the clothing set for a player or character.
     * <p>
     * This variable is used to store the {@link ItemStack} instance representing
     * the boots equipped as part of an outfit.
     * <p>
     * The boots can be part of a complete set of clothing items, including
     * <ul>
     * <li>head</li>
     * <li>shirt</li>
     * <li>pants</li>
     * <li>boots</li>
     * </ul>
     * <p>
     * Initially set to <code>null</code> to indicate that no boots are equipped.
     */
    private ItemStack boots = null;

    /**
     * <p>Represents the unique identifier for the boots associated with this clothing instance.</p>
     *
     * <ul>
     *   <li>Used to identify a specific pair of boots in the context of a clothing set or outfit.</li>
     *   <li>Integral to distinguishing individual outfit components.</li>
     * </ul>
     */
    private int bootsID = 0;
}