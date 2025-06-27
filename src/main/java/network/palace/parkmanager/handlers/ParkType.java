package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration to represent different types of parks and associated facilities within a theme park or resort system.
 *
 * <p>The {@code ParkType} enum categorizes parks and facilities such as theme parks, water parks, resorts,
 * and other specialized destinations. Each park type is associated with a unique identifier (name) and
 * a descriptive title.</p>
 *
 * <p><b>Categories:</b></p>
 * <ul>
 *   <li><b>Theme Parks:</b> Represent major attractions like Magic Kingdom and Epcot, catering to general entertainment.</li>
 *   <li><b>Water Parks:</b> Include parks like Typhoon Lagoon, offering water-based attractions and activities.</li>
 *   <li><b>Resorts:</b> Represent accommodation destinations such as the Grand Floridian Resort & Spa and Polynesian Resort.</li>
 *   <li><b>Other:</b> Includes entities like Disney Cruise Line and miscellaneous seasonal offerings.</li>
 * </ul>
 *
 * <p>Each entry in the enumeration carries the following attributes:</p>
 * <ul>
 *   <li><b>title:</b> A descriptive name for the park or facility.</li>
 * </ul>
 *
 * <p><b>Utility Methods:</b></p>
 * <ul>
 *   <li>{@code getId()} - Returns the unique identifier for the park type, with special handling for certain types (e.g., "Epcot").</li>
 *   <li>{@code listIDs()} - Provides a comma-separated list of all park type identifiers in lowercase format.</li>
 *   <li>{@code fromString(String id)} - Maps a string to a {@code ParkType} instance, matching identifiers case-insensitively.</li>
 * </ul>
 *
 * <p>This enumeration is useful in scenarios that require categorizing and managing different park or resort types,
 * such as assignments in park management systems or integration with region and world contexts.</p>
 */
@Getter
@AllArgsConstructor
public enum ParkType {
    /* Walt Disney World */
    MK("Magic Kingdom"), EPCOT("Epcot"), DHS("Disney's Hollywood Studios"), AK("Animal Kingdom"),
    TYPHOON("Typhoon Lagoon"),
    /* Universal Orlando Resort */
    USF("Universal Studios Florida"), IOA("Islands of Adventure"),
    /* Resorts */
    CONTEMPORARY("Contemporary Resort"), POLYNESIAN("Polynesian Resort"), GRANDFLORIDIAN("Grand Floridian Resort & Spa"),
    ARTOFANIMATION("Art of Animation Resort"), POPCENTURY("Pop Century Resort"),
    /* Other */
    DCL("Disney Cruise Line"), SEASONAL("Seasonal");

    /**
     * <p>Represents the descriptive title of a specific {@code ParkType}.</p>
     *
     * <p>This field contains a human-readable name or title corresponding to a specific park type,
     * helping to provide clear and identifiable descriptions of the various {@link ParkType} entries.
     * Examples might include names like "Magic Kingdom", "Epcot", or "Animal Kingdom Resort".</p>
     *
     * <p>The {@code title} serves as a user-friendly counterpart to park identifiers and is typically used in
     * interfaces or applications where a readable label is preferred over technical identifiers.</p>
     *
     * <p>Key characteristics of the {@code title} field include:</p>
     * <ul>
     *   <li>Provides a concise, descriptive name for a specific {@code ParkType} entry.</li>
     *   <li>Acts as a mapping between a park's identifier and its corresponding readable name.</li>
     *   <li>Useful for display elements, logging, and other user-facing operations.</li>
     * </ul>
     *
     * <p>This field is typically initialized within the {@code ParkType} enumeration and remains constant for each entry.</p>
     */
    String title;

    /**
     * Retrieves the identifier for the current instance of {@code ParkType}.
     *
     * <p>This method returns a {@code String} representation of the park type. If the current instance
     * is {@code EPCOT}, the identifier will be the string <code>"Epcot"</code>. For all other park types,
     * the method will return the result of invoking {@code name()} on the instance, which provides the
     * Enum constant's name as a {@code String}.
     *
     * <p>This identifier can be used to uniquely distinguish {@code ParkType} instances when
     * processing or mapping park-related data.
     *
     * @return A {@code String} representing the identifier of the current park type.
     */
    public String getId() {
        return this.equals(EPCOT) ? "Epcot" : name();
    }

    /**
     * Generates a comma-separated list of park type identifiers in lowercase.
     *
     * <p>This method iterates over all the values of the {@link ParkType} enumeration,
     * extracts their names, converts them to lowercase, and concatenates them into a single string.
     * Each identifier is separated by a comma and a space.
     *
     * <p>For example, if the {@link ParkType} enum contains the values {@code MK, EPCOT, DHS},
     * this method would return the string {@code "mk, epcot, dhs"}.
     *
     * @return A {@link String} containing all {@link ParkType} identifiers in lowercase,
     * separated by commas and spaces.
     */
    public static String listIDs() {
        StringBuilder s = new StringBuilder();
        ParkType[] values = values();
        for (int i = 0; i < values.length; i++) {
            s.append(values[i].name().toLowerCase());
            if (i < (values.length - 1)) {
                s.append(", ");
            }
        }
        return s.toString();
    }

    /**
     * Converts a string representation of a park type to its corresponding {@code ParkType} enumeration constant.
     *
     * <p>This method performs a case-insensitive comparison to match the input string with the {@code name}
     * of a {@code ParkType} constant. If a match is found, the corresponding {@code ParkType} constant is returned.
     * If no match is found, the method returns {@code null}.
     *
     * @param id the string representation of the {@code ParkType} to be converted. This value should
     *           correspond to the name of one of the constants in the {@code ParkType} enumeration,
     *           regardless of case.
     *
     * @return the {@code ParkType} constant that matches the provided string, or {@code null} if no match is found.
     */
    public static ParkType fromString(String id) {
        for (ParkType type : values()) {
            if (type.name().equalsIgnoreCase(id)) return type;
        }
        return null;
    }
}
