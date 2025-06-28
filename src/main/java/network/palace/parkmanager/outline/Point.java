package network.palace.parkmanager.outline;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The {@code Point} class represents a point in a 2D space with a name and its coordinates.
 *
 * <p>Objects of this class are immutable. Each {@code Point} is defined by:
 * <ul>
 *   <li>{@code name}: the name of the point.</li>
 *   <li>{@code x}: the x-coordinate of the point.</li>
 *   <li>{@code z}: the z-coordinate of the point.</li>
 * </ul>
 *
 * <p>This class provides read-only access to its properties and ensures immutability.
 */
@Getter
@AllArgsConstructor
public class Point {
    /**
     * The name of the point.
     *
     * <p>This field holds a descriptive name or label that identifies the point within a 2D space.
     * It is immutable and set during the creation of the {@code Point} instance.
     */
    private final String name;

    /**
     * Represents the x-coordinate of the {@code Point} in a 2D space.
     *
     * <p>This field defines the horizontal position of the point
     * and is immutable once the {@code Point} object is created.
     */
    private final int x;

    /**
     * The {@code z} field represents the z-coordinate of the {@code Point} in 2D space.
     *
     * <p>This field is a part of the immutable state of the {@code Point} object.
     * It is a required value and is initialized during the creation of the {@code Point}.
     *
     * <p>Key characteristics:
     * <ul>
     *   <li>Immutable after initialization.</li>
     *   <li>Represents the vertical or depth-related positional value of the point.</li>
     * </ul>
     */
    private final int z;
}
