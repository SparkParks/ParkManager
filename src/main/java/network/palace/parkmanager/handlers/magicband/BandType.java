package network.palace.parkmanager.handlers.magicband;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

/**
 * The {@code BandType} enum defines a collection of band types with associated
 * properties. Each band type has a specified color and a name,
 * offering a flexible representation for various applications.
 *
 * <p>This enum is typically used in scenarios where identifying or customizing
 * features based on specific band types is necessary, such as user selection or visual representation in an application.
 *
 * <p><b>Key Features:</b>
 * <ul>
 *   <li>Each band type is associated with a boolean value indicating a color property.</li>
 *   <li>Each band type has a display-friendly name, formed by combining text and colors.</li>
 *   <li>Provides utility methods for specific operations, such as obtaining a database-friendly name and
 *       resolving enum constants from string values.</li>
 * </ul>
 *
 * <p><b>Properties:</b>
 * <ul>
 *   <li>{@code color}: Indicates whether the band type has a color associated with it.</li>
 *   <li>{@code name}: A user-facing name for the band type, including color codes.</li>
 * </ul>
 *
 * <p><b>Utility Methods:</b>
 * <ul>
 *   <li>{@link #getDBName()}: Converts the enum constant to a lowercase string suitable for database storage.</li>
 *   <li>{@link #fromString(String)}: Resolves a {@code BandType} from a case-insensitive string. Defaults to {@code RED} if no match is found.</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum BandType {
    RED(true, ChatColor.RED + "Red"),
    ORANGE(true, ChatColor.GOLD + "Orange"),
    YELLOW(true, ChatColor.YELLOW + "Yellow"),
    GREEN(true, ChatColor.DARK_GREEN + "Green"),
    BLUE(true, ChatColor.BLUE + "Blue"),
    PURPLE(true, ChatColor.DARK_PURPLE + "Purple"),
    PINK(true, ChatColor.LIGHT_PURPLE + "Pink"),
    SORCERER_MICKEY(false, ChatColor.AQUA + "Sorcerer Mickey"),
    HAUNTED_MANSION(false, ChatColor.GRAY + "Haunted Mansion"),
    PRINCESSES(false, ChatColor.LIGHT_PURPLE + "Princesses"),
    BIG_HERO_SIX(false, ChatColor.RED + "Big Hero 6"),
    HOLIDAY(false, ChatColor.AQUA + "Holiday"),
    NOOKPHONE(false, ChatColor.GREEN + "Nook Phone");

    /**
     * Represents a boolean value indicating whether the {@code BandType} has an associated color property.
     *
     * <p>Key characteristics:
     * <ul>
     *   <li>A {@code true} value signifies that the {@code BandType} has a color assigned to it.</li>
     *   <li>A {@code false} value signifies the absence of an assigned color.</li>
     * </ul>
     *
     * <p>This property is used to distinguish between colorful and non-colorful {@code BandType} entries,
     * enabling conditional logic or specific behaviors based on the presence of a color attribute.
     */
    boolean color;

    /**
     * Represents the user-friendly display name of the band type.
     *
     * <p>The {@code name} field includes both text and color codes to provide
     * visual and contextual representation of the band type. This is typically
     * used for UI or display purposes in applications.
     *
     * <p><b>Key Characteristics:</b>
     * <ul>
     *   <li>Includes color codes (e.g., {@code ChatColor}) for visual representation.</li>
     *   <li>Formatted to provide a display-friendly name for users.</li>
     * </ul>
     *
     * <p><b>Example:</b>
     * <ul>
     *   <li>{@code ChatColor.RED + "Red"}</li>
     *   <li>{@code ChatColor.AQUA + "Sorcerer Mickey"}</li>
     * </ul>
     */
    String name;

    /**
     * Retrieves the database-friendly name of the enum constant by converting the enum's name to lowercase.
     *
     * <p>This method ensures that the enum name complies with common database naming conventions by
     * returning a lowercase string representation.</p>
     *
     * @return the name of the enum constant in lowercase.
     */
    public String getDBName() {
        return name().toLowerCase();
    }

    /**
     * Converts a string representation of a band type into the corresponding {@code BandType} enum constant.
     *
     * <p>This method performs a case-insensitive comparison of the input string against the names
     * of the {@code BandType} enum constants. If a match is found, the corresponding {@code BandType}
     * is returned. If no match is found, the method defaults to {@code BandType.RED}.
     *
     * <p><b>Usage Notes:</b>
     * <ul>
     *   <li>Input strings should ideally match the enum constant names, ignoring case sensitivity.</li>
     *   <li>Handles cases where the input string does not match any enum constant gracefully by defaulting to {@code RED}.</li>
     * </ul>
     *
     * @param type the string representation of a {@code BandType}, case-insensitive.
     *             If {@code null} or no match is found, the method will return {@code BandType.RED}.
     * @return the corresponding {@code BandType} enum constant, or {@code RED} if no match is found.
     */
    public static BandType fromString(String type) {
        for (BandType bandType : values()) {
            if (bandType.name().equalsIgnoreCase(type)) {
                return bandType;
            }
        }
        return RED;
    }
}
