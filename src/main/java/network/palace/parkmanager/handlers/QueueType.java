package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents different types of theme park queues or rides, each with unique characteristics and descriptions.
 *
 * <p>{@code QueueType} is an enumeration used to categorize various queue or ride systems in a theme park.
 * Each enum value represents a specific type of ride or queue, with a description indicating its features.</p>
 *
 * <p>Enum constants include:</p>
 * <ul>
 *   <li>{@code BLOCK} - A type of queue that spawns a redstone block at a specific location when players are brought in.</li>
 *   <li>{@code CAROUSEL} - A carousel with 24 horses that rotate around a central location.</li>
 *   <li>{@code TEACUPS} - Features 18 spinning teacups arranged across three plates, revolving around a central location.</li>
 *   <li>{@code AERIAL_CAROUSEL} - A carousel variant with vertical movement (up-and-down rotation).</li>
 *   <li>{@code FILE} - A ride vehicle that follows a pre-determined path with integrated show elements and speed variations.</li>
 * </ul>
 *
 * <p>Each queue type includes a description that provides details about the queue's design or functionality. The descriptions
 * can be accessed or modified using the provided getter and setter methods for the {@code description} field.</p>
 *
 * <p>This enumeration also provides a utility method:</p>
 * <ul>
 *   <li>{@code fromString(String s)} - Attempts to retrieve a {@code QueueType} based on a case-insensitive string match
 *   with the name of the enum constant. Returns {@code null} if no match is found.</li>
 * </ul>
 *
 * <p>{@code QueueType} is primarily used to organize and identify different ride or queue formats within a theme park
 * management system. Each constant corresponds to a specific ride or queue behavior.</p>
 */
@AllArgsConstructor
public enum QueueType {
    BLOCK("This type of queue spawns in a redstone block at a specified location when players are brought in"),
    CAROUSEL("A carousel with 24 horses that rotate around a central location"),
    TEACUPS("18 teacups spin on three plates around a central location"),
    AERIAL_CAROUSEL("Like a carousel, but you go up and down too"),
    FILE("The ride vehicle will follow a pre-determined path of actions along with added show elements and speed changes");

    /**
     * Represents a textual description of a {@code QueueType}.
     *
     * <p>This field provides a human-readable explanation of the characteristics and
     * functionality of the associated queue type. Descriptions are used to inform or
     * document details about specific rides or queue systems, such as their behavior,
     * design, and unique features.</p>
     *
     * <p>For example, a {@code QueueType} might describe ride elements like rotation,
     * movement patterns, or interactive mechanics unique to the type.</p>
     *
     * <p>The value of {@code description} can vary depending on the queue type and
     * is configurable through the associated getter and setter methods.</p>
     */
    @Getter @Setter String description;

    /**
     * Converts a string representation of a {@code QueueType} to its corresponding enum constant.
     *
     * <p>This method performs a case-insensitive match to find a {@link QueueType} constant
     * whose name matches the provided string. If a match is found, the corresponding
     * enum constant is returned. Otherwise, {@code null} is returned.</p>
     *
     * <p>Usage notes:</p>
     * <ul>
     *   <li>The input string should not be {@code null} for predictable behavior.</li>
     *   <li>Ensure the string matches a valid {@code QueueType} constant name, ignoring case.</li>
     *   <li>If no match is found, the method will return {@code null}.</li>
     * </ul>
     *
     * @param s the string representation of the {@link QueueType} to be converted.
     *          This parameter is case-insensitive and should correspond to the name
     *          of a valid enum constant.
     * @return the {@link QueueType} that matches the input string, or {@code null}
     *         if no match is found.
     */
    public static QueueType fromString(String s) {
        for (QueueType type : values()) {
            if (type.name().equalsIgnoreCase(s)) {
                return type;
            }
        }
        return null;
    }
}
