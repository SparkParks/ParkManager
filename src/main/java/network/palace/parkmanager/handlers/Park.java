package network.palace.parkmanager.handlers;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;

/**
 * Represents a park in a theme park or resort environment. This class contains information about the park type,
 * its associated world, and the protected region that defines its boundaries.
 *
 * <p>Each {@code Park} has the following attributes:
 * <ul>
 *   <li>{@code id} - The type of park, defined by {@link ParkType} enumeration.</li>
 *   <li>{@code world} - The world where the park is located, represented by a {@link World} instance.</li>
 *   <li>{@code region} - The protected region of the park, represented by a {@link ProtectedRegion} instance.</li>
 * </ul>
 *
 * <p>The {@code ParkType} enumeration provides details about different parks, including theme parks, water parks, resorts,
 * and other facilities, with additional attributes and utility methods to map identifiers and titles.
 * The {@link World} and {@link ProtectedRegion} classes are used to represent the park's physical or virtual location
 * and its boundaries, respectively.
 */
@Getter
@AllArgsConstructor
public class Park {
    /**
     * <p>Represents the type of park associated with the current instance of {@code Park}.</p>
     *
     * <p>This field utilizes the {@link ParkType} enumeration, which defines various types of parks,
     * such as theme parks, water parks, resorts, and other facilities. Each {@code ParkType} entry
     * is associated with a unique identifier and a descriptive title.</p>
     *
     * <p>Examples of park types include:</p>
     * <ul>
     *   <li>Magic Kingdom</li>
     *   <li>Epcot</li>
     *   <li>Animal Kingdom</li>
     *   <li>Typhoon Lagoon</li>
     *   <li>Various resort destinations</li>
     * </ul>
     *
     * <p>Utility methods provided by {@link ParkType}, such as {@code fromString} and {@code listIDs},
     * help facilitate identification and mapping of park types.</p>
     */
    private ParkType id;

    /**
     * Represents the world in which the park is located.
     *
     * <p>This variable holds a reference to a {@link World} instance,
     * which provides context and details about the physical or virtual
     * world associated with the park. This could include attributes such
     * as world dimensions, rules, and environmental properties.
     *
     * <ul>
     *   <li>Defines the world where the {@link Park} exists.</li>
     *   <li>Links park attributes to a specific world context.</li>
     * </ul>
     *
     * <p>The {@link World} class may include further details about the
     * environment, allowing for integration with broader systems handling
     * world-related operations.
     */
    private World world;

    /**
     * Represents a protected region within the boundaries of the park.
     *
     * <p>The {@code region} variable is used to define the restricted area within the park. It is an instance
     * of {@link ProtectedRegion} that typically includes information regarding the physical or virtual boundaries
     * of the park, permissions, and rules applicable to that region.
     *
     * <p>This ensures proper delineation of areas within the park and facilitates management of activities,
     * access control, and enforcement of region-specific policies.
     */
    private ProtectedRegion region;
}
