package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a type of resort and provides utility methods for mapping and identification.
 *
 * <p>The {@code Resort} enum defines specific resorts and their unique identifiers.
 * Each resort is assigned a unique integer ID that allows for easy identification and mapping
 * between string or integer inputs and their corresponding resorts.</p>
 *
 * <p>Available resorts include:</p>
 * <ul>
 *   <li>{@code WDW} - Walt Disney World</li>
 *   <li>{@code DLR} - Disneyland Resort</li>
 *   <li>{@code USO} - Universal Studios Orlando</li>
 * </ul>
 *
 * <p>Key features of this enum:</p>
 * <ul>
 *   <li>Provides a utility method {@link #fromString(String)} for mapping a string to the matching {@code Resort} enum constant.</li>
 *   <li>Defines a utility method {@link #fromId(int)} for resolving a {@code Resort} enum constant based on its ID.</li>
 *   <li>Assigns a unique ID to each resort for quick resolution and comparison.</li>
 * </ul>
 *
 * <p>The default resort, if no match is found or if the input is invalid, is {@code WDW}.</p>
 */
@AllArgsConstructor
public enum Resort {
    WDW(0), DLR(1), USO(2);

    /**
     * <p>Represents the unique identifier for a specific {@code Resort}.</p>
     *
     * <p>This field defines a numerical ID for each resort, which allows for
     * efficient identification, mapping, and comparison of {@code Resort} constants.</p>
     *
     * <p>Key features of the {@code id} field:</p>
     * <ul>
     *   <li>Each {@code Resort} constant is assigned a distinct integer ID.</li>
     *   <li>Facilitates mapping of inputs (e.g., integers or strings) to their corresponding {@code Resort} instance.</li>
     *   <li>Used internally in utility methods such as {@link #fromId(int)} for resolving resorts based on their ID.</li>
     * </ul>
     *
     * <p>The {@code id} field is immutable and can only be retrieved through its getter method.</p>
     */
    @Getter private final int id;

    /**
     * Converts a string representation of a resort into its corresponding {@code Resort} enum constant.
     *
     * <p>This method takes a case-insensitive string input and resolves it to one of the predefined
     * {@code Resort} constants. If the input string is {@code null} or does not match
     * any recognized resort identifiers, the default {@code WDW} (Walt Disney World) is returned.</p>
     *
     * <p>Valid string representations include:</p>
     * <ul>
     *   <li>{@code "wdw"} - Resolves to {@code WDW} (Walt Disney World).</li>
     *   <li>{@code "dlr"} - Resolves to {@code DLR} (Disneyland Resort).</li>
     *   <li>{@code "uso"} - Resolves to {@code USO} (Universal Studios Orlando).</li>
     * </ul>
     *
     * @param s the string representation of a resort, case-insensitive. Valid values include "wdw", "dlr", and "uso".
     *          If {@code null} or unrecognized, the default {@code WDW} is returned.
     * @return the {@code Resort} enum constant corresponding to the given string. Returns {@code WDW} if the input is {@code null} or invalid.
     */
    public static Resort fromString(String s) {
        if (s == null) return WDW;
        switch (s.toLowerCase()) {
            case "wdw":
                return WDW;
            case "dlr":
                return DLR;
            case "uso":
                return USO;
        }
        return WDW;
    }

    /**
     * Maps an integer ID to its corresponding {@code Resort} enum constant.
     *
     * <p>This method searches through all defined constants of the {@code Resort} enum
     * and returns the one that matches the given ID. If no match is found, the default
     * resort {@code WDW} is returned.</p>
     *
     * @param id an integer representing the unique identifier of a {@code Resort} constant.
     *           Valid values are the IDs assigned to each constant in the {@code Resort} enum.
     * @return the {@code Resort} enum constant that matches the provided ID. If no match
     *         is found, the method returns {@code WDW}.
     */
    public static Resort fromId(int id) {
        for (Resort type : values()) {
            if (type.getId() == id) return type;
        }
        return WDW;
    }
}
