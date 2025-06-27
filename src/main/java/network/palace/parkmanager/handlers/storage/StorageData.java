package network.palace.parkmanager.handlers.storage;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * The {@code StorageData} class represents the storage details and data for an inventory system.
 * This class includes information about backpack and locker inventories, their sizes, content hashes,
 * and other relevant details for management and synchronization.
 *
 * <p>
 * Main functionalities of this class include:
 * <ul>
 *   <li>Storing and managing data for a backpack inventory (contents, size, and hash).</li>
 *   <li>Storing and managing data for a locker inventory (contents, size, and hash).</li>
 *   <li>Providing item storage for base items and build items, with respective content hashes.</li>
 *   <li>Tracking if an update is required for the inventory data.</li>
 *   <li>Maintaining a timestamp for the last inventory update.</li>
 * </ul>
 *
 * <p>
 * Each inventory (backpack and locker) is associated with a {@link StorageSize}, which defines its dimensions
 * and capacity. The class uses hashes to identify the state of the inventories and determine changes or updates
 * required.
 *
 * <p>
 * Usage of this class is intended for systems that involve tracking and managing player or entity inventories
 * in a structured storage-based system where synchronization and updates are critical.
 *
 * <p>
 * Components:
 * <ul>
 *   <li>{@code backpack}: Represents the inventory data for the backpack.</li>
 *   <li>{@code backpackSize}: Represents the size configuration of the backpack.</li>
 *   <li>{@code backpackHash}: A hash string representing the contents of the backpack.</li>
 *   <li>{@code dbBackpackSize}: Represents the stored database size of the backpack.</li>
 *   <li>{@code locker}: Represents the inventory data for the locker.</li>
 *   <li>{@code lockerSize}: Represents the size configuration of the locker.</li>
 *   <li>{@code lockerHash}: A hash string representing the contents of the locker.</li>
 *   <li>{@code dbLockerSize}: Represents the stored database size of the locker.</li>
 *   <li>{@code base}: Represents basic items or contents stored, with their corresponding hash.</li>
 *   <li>{@code build}: Represents build items or contents stored, with their corresponding hash.</li>
 *   <li>{@code needsUpdate}: A flag indicating if the inventory data requires synchronization or update.</li>
 *   <li>{@code lastInventoryUpdate}: A timestamp representing the last update time for the inventory.</li>
 * </ul>
 *
 * <p>
 * Note: This class uses Lombok annotations ({@code @Getter}, {@code @Setter}, {@code @RequiredArgsConstructor}, and {@code @NonNull})
 * for boilerplate code reduction, such as getters, setters, and constructor generation.
 */
@Getter
@RequiredArgsConstructor
public class StorageData {
    /**
     * <p>Represents the player's backpack inventory. This variable is used within the
     * {@code StorageData} class to manage the inventory system associated with the
     * player's storage. The {@code backpack} is a non-null reference to the player's
     * inventory object, ensuring that it is always initialized and accessible.</p>
     *
     * <p>The structure represented by {@code backpack} is responsible for holding
     * items specific to the player's backpack storage. It is tightly coupled with
     * other fields and operations within {@code StorageData} to maintain consistency
     * and manage the inventory system effectively.</p>
     *
     * <p>Features of the backpack inventory include:</p>
     * <ul>
     *     <li>Used to store items exclusively pertaining to the player's backpack.</li>
     *     <li>Acts as a central component for managing storage in the system.</li>
     *     <li>Interacts with other storage-related fields such as {@code backpackSize}
     *     and {@code backpackHash} for validation and synchronization.</li>
     * </ul>
     */
    @NonNull private final Inventory backpack;

    /**
     * Represents the size of the player's backpack in the storage system.
     * This variable is used to determine the capacity of the backpack based on predefined sizes.
     *
     * <p>The {@link StorageSize} enumeration provides the following predefined sizes:</p>
     * <ul>
     *   <li><strong>SMALL</strong>: A small backpack with a limited number of slots.</li>
     *   <li><strong>LARGE</strong>: A large backpack with an extended number of slots.</li>
     * </ul>
     *
     * <p>Each size in the {@link StorageSize} enum offers corresponding metadata, such as
     * number of rows, size identifier, and a human-readable name.</p>
     *
     * <p>This variable is annotated with {@code @NonNull}, which ensures that it cannot
     * be null. Use the {@code @Setter} annotation to update the value when necessary.</p>
     *
     * <p>See {@link StorageSize} for more details about available sizes and their properties.</p>
     */
    @NonNull @Setter private StorageSize backpackSize;

    /**
     * Represents the hash value associated with the backpack's data.
     * <p>
     * This hash is utilized to identify and ensure the integrity of the
     * stored backpack information. It may be used in operations such as
     * comparison, validation, or synchronization of backpack data.
     * <p>
     * The variable is non-null and uses a setter to allow controlled
     * modification of its content.
     */
    @NonNull @Setter private String backpackHash;

    /**
     * Represents the number of slots in the backpack as persisted in the database.
     * <p>
     * This value is used to synchronize and store the size of the user's backpack
     * between the application and the database.
     * </p>
     * <p>
     * The size is typically derived from an enumeration such as {@link StorageSize}
     * to ensure consistency in backpack dimensions.
     * </p>
     * <ul>
     *   <li><strong>Getter</strong>: Retrieves the current database representation of the backpack size.</li>
     *   <li><strong>Setter</strong>: Updates the database representation of the backpack size.
     *   This might require validation or post-update logic depending on its use-case in the application.</li>
     * </ul>
     */
    @NonNull @Setter private int dbBackpackSize;

    /**
     * Represents the locker inventory storage for the {@code StorageData} class.
     * <p>
     * The locker is a {@link Inventory} instance that is non-null and holds the
     * contents designated to the locker storage. This field is managed and updated
     * as part of the game storage system.
     * <p>
     * The locker size and related attributes, such as hash and database references,
     * are managed alongside this field to ensure consistency and accurate data handling.
     */
    @NonNull private final Inventory locker;

    /**
     * Represents the size of the locker in the storage system.
     * <p>
     * This field defines the {@link StorageSize} allocated for a locker, which determines
     * its capacity and the number of available slots. The locker size can
     * either be {@code SMALL} or {@code LARGE}, and each option corresponds to
     * a different capacity.
     * </p>
     *
     * <p>
     * The {@link StorageSize} enum provides:
     * <ul>
     *   <li>Number of rows for the locker</li>
     *   <li>Total available slots (calculated as <code>rows * 9</code>)</li>
     *   <li>A readable name for the size</li>
     * </ul>
     * </p>
     *
     * <p>
     * This variable is annotated with {@link NonNull}, which ensures that
     * null values are not permitted, and {@link Setter}, which allows
     * for modification of this field.
     * </p>
     */
    @NonNull @Setter private StorageSize lockerSize;

    /**
     * <p>Represents a unique hash identifier associated with the locker in the storage system.</p>
     *
     * <p>This hash is used to track and differentiate specific lockers within the storage data.</p>
     *
     * <ul>
     *   <li>It is annotated with {@code @NonNull}, meaning it cannot be null and must always have a value.</li>
     *   <li>It is mutable via the setter provided by the {@code @Setter} annotation.</li>
     * </ul>
     */
    @NonNull @Setter private String lockerHash;

    /**
     * Represents the size of the locker as stored in the database.
     * <p>
     * This field is used to persist and retrieve the locker size within the storage system.
     * The value corresponds to one of the available {@link StorageSize} types.
     * </p>
     *
     * <ul>
     *   <li>It is not null to ensure that a valid locker size is always set.</li>
     *   <li>Changes to this value are updated using the provided setter method.</li>
     *   <li>Values are expected to align with the sizes defined in the {@link StorageSize} enum.</li>
     * </ul>
     */
    @NonNull @Setter private int dbLockerSize;

    /**
     * Represents the base storage or inventory within the {@code StorageData} system.
     * <p>
     * This array holds {@link ItemStack} objects that constitute the base structure
     * of a storage unit. The slots within this base determine the foundation for
     * any further inventory-related operations or configurations.
     * <p>
     * Characteristics:
     * <ul>
     * <li>The array is non-null, ensuring that it always has a valid reference.</li>
     * <li>Each element in the array corresponds to an {@code ItemStack}, defining
     * the stored items or states within the base storage level.</li>
     * </ul>
     * <p>
     * Modifications to this property are controlled through the setter,
     * allowing sanctioned updates to the storage base.
     */
    @NonNull @Setter private ItemStack[] base;

    /**
     * Represents the hash value associated with the base storage in the system.
     * <p>
     * This variable is used to track and compare the current state of the base storage
     * for integrity or consistency purposes. It is expected to be a unique and non-null
     * hash value that corresponds to the state of the base storage data.
     * </p>
     * <p>
     * <b>Constraints:</b>
     * <lu>
     *   <li> Must not be null. </li>
     *   <li> Should be managed carefully to ensure consistency. </li>
     * </lu>
     */
    @NonNull @Setter private String baseHash;

    /**
     * Represents the build inventory storage of the {@code StorageData}.
     * <p>
     * This array stores {@link ItemStack} objects, typically representing items
     * or blocks, which are part of the player's build inventory.
     * <p>
     * Key characteristics of this field:
     * <ul>
     *   <li>Annotated with {@code @NonNull}, indicating that the array cannot be null.</li>
     *   <li>Modifiable via the {@code @Setter}, allowing updates to the array reference.</li>
     * </ul>
     */
    @NonNull @Setter private ItemStack[] build;

    /**
     * Represents the unique hash value associated with the build data.
     *
     * <p>This hash value serves as an identifier for the build's current state, ensuring that the
     * contents of the build can be monitored or tracked for changes.</p>
     *
     * <p><b>Annotations:</b></p>
     * <ul>
     *    <li><code>@NonNull</code> - Ensures that this value cannot be null.</li>
     *    <li><code>@Setter</code> - Allows setting a value for this field.</li>
     * </ul>
     *
     * <p>Modification of this field directly reflects updates to the build's unique identifier.</p>
     */
    @NonNull @Setter private String buildHash;

    /**
     * Indicates whether the storage data requires an update.
     *
     * <p>This flag is used to track whether any changes to the storage data
     * (including backpack, locker, or base) have been made that necessitate
     * an update to ensure consistency with external systems or persisted state.</p>
     *
     * <p>Users of the {@code StorageData} class should set this field to
     * {@code true} whenever modifications are performed, and a subsequent
     * operation should handle the update process if required.</p>
     *
     * <p>Default value: {@code false}.</p>
     */
    @Setter private boolean needsUpdate = false;

    /**
     * Represents the timestamp of the most recent inventory update.
     * <p>
     * This field is initialized to the current system time in milliseconds when the object is created.
     * It can be used to track when the inventory was last modified or synchronized.
     * </p>
     *
     * <p>
     * <b>Important Notes:</b>
     * <ul>
     *   <li>This timestamp is represented in milliseconds since the Unix epoch (January 1, 1970, 00:00:00 GMT).</li>
     *   <li>Regular updates to this field should ensure accurate tracking of inventory update times.</li>
     *   <li>The timestamp value may be compared to other timestamps for calculating elapsed time or other time-based operations.</li>
     * </ul>
     * </p>
     */
    @Getter @Setter private long lastInventoryUpdate = System.currentTimeMillis();
}
