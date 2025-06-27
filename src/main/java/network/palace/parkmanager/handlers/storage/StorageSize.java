package network.palace.parkmanager.handlers.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The {@code StorageSize} enum represents different predefined sizes for storage systems.
 * <p>
 * Each storage size is characterized by:
 * <ul>
 *   <li>Number of rows</li>
 *   <li>Dimension size</li>
 *   <li>A name that describes the size</li>
 * </ul>
 * <p>
 * This enum also provides utility methods for calculating the total number of slots and
 * obtaining a {@code StorageSize} instance based on an integer representation.
 */
@Getter
@AllArgsConstructor
public enum StorageSize {
    SMALL(3, 0, "Small"), LARGE(6, 1, "Large");

    /**
     * Represents the number of rows in the storage configuration.
     * <p>
     * This variable is used to determine the height (row count) of the storage grid. Each row typically contains
     * a fixed number of slots, and the total storage capacity is derived as {@code rows * slots per row}.
     * </p>
     *
     * <ul>
     *   <li>Defines a key characteristic of the storage size.</li>
     *   <li>Used in calculations to determine total storage space.</li>
     * </ul>
     */
    private final int rows;

    /**
     * Represents the dimension size associated with a specific {@code StorageSize}.
     * <p>
     * The {@code size} variable is used to identify a unique integer representation
     * of the storage dimension. It differentiates between various predefined sizes
     * in the {@code StorageSize} enum, such as Small and Large.
     * <p>
     * <ul>
     *   <li>Serves as an internal identifier for each storage size option.</li>
     *   <li>Can be utilized in methods to map from an integer representation
     *       back to a corresponding {@code StorageSize}.</li>
     * </ul>
     */
    private final int size;

    /**
     * The {@code name} field represents the human-readable name associated with the storage size.
     * <p>
     * This field is used to identify and describe the size of the storage in a descriptive manner,
     * such as "Small" or "Large". It helps in providing clear and meaningful representation
     * for the different storage sizes.
     * <p>
     * <ul>
     *   <li>Immutable and final</li>
     *   <li>Stores a descriptive name as a {@code String}</li>
     * </ul>
     */
    private final String name;

    /**
     * Calculates the total number of slots based on the number of rows.
     * <p>
     * Each row consists of 9 slots, and the result is determined by multiplying
     * the number of rows by 9.
     *
     * @return the total number of slots as an integer, computed as {@code rows * 9}.
     */
    public int getSlots() {
        return rows * 9;
    }

    /**
     * Retrieves a {@code StorageSize} instance based on an integer representation.
     * <p>
     * This method maps integer values to specific {@code StorageSize} constants:
     * <ul>
     *   <li>Returns {@code SMALL} for the value {@code 0}</li>
     *   <li>Returns {@code LARGE} for the value {@code 1}</li>
     *   <li>Defaults to {@code SMALL} for any other value</li>
     * </ul>
     *
     * @param i the integer representation of a storage size
     * @return the corresponding {@code StorageSize} instance, or {@code SMALL} if the integer
     *         does not match a predefined representation
     */
    public static StorageSize fromInt(int i) {
        switch (i) {
            case 0:
                return SMALL;
            case 1:
                return LARGE;
        }
        return SMALL;
    }
}