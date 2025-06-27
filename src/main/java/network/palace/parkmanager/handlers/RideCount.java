package network.palace.parkmanager.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Represents the count of rides for a specific entity, identified by a name and server.
 *
 * <p>The {@code RideCount} class is designed to track and update the count of rides
 * performed or associated with a specific name and server combination. This class
 * provides methods to modify the ride count and compare server identifiers.</p>
 *
 * <p>Key features include:</p>
 * <ul>
 *   <li>Immutable {@code name} and {@code server} fields that act as identifiers.</li>
 *   <li>A mutable {@code count} field initialized to {@code 1}.</li>
 *   <li>Methods to increment the count and validate server equality.</li>
 * </ul>
 *
 * <p>The class leverages annotations from Lombok to generate boilerplate code, such as
 * getters for fields and a required-args constructor to ensure proper initialization
 * of the final fields {@code name} and {@code server}.</p>
 *
 * <p>Usage overview:</p>
 * <ul>
 *   <li>{@link #addCount(int)}: Increment the current ride count by a specified value.</li>
 *   <li>{@link #serverEquals(String)}: Check if the current server matches a given string.</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public class RideCount {
    /**
     * Represents the name associated with the specific instance of {@code RideCount}.
     *
     * <p>This field serves as a unique identifier or label, linking the ride count information
     * to a particular entity (e.g., an attraction, user, or session) within the context of the
     * {@code RideCount} class. The value of this field is immutable once set, ensuring consistent
     * identification throughout the lifecycle of the object.</p>
     *
     * <p>Key characteristics:</p>
     * <ul>
     *   <li>Immutable and cannot be modified after initialization.</li>
     *   <li>Acts as a primary identifier within the {@code RideCount} class.</li>
     * </ul>
     *
     * <p>Its immutability is enforced via the {@code final} keyword, and the value must be
     * supplied at the time of object construction.</p>
     */
    private final String name;

    /**
     * Represents the server identifier associated with the {@code RideCount} instance.
     *
     * <p>This variable serves as an immutable identifier for the specific server
     * where the rides are counted. It is intended to differentiate ride counts
     * across multiple servers in a distributed system.</p>
     *
     * <p>Key characteristics:</p>
     * <ul>
     *   <li>Immutable and final once initialized via the constructor.</li>
     *   <li>Used alongside the {@code name} variable to uniquely identify the entity
     *   associated with the ride count.</li>
     *   <li>Subject to validation and comparison through the {@link #serverEquals(String)} method.</li>
     * </ul>
     */
    private final String server;

    /**
     * Represents the count of rides for a specific entity or instance.
     *
     * <p>The {@code count} field tracks the number of rides associated with the
     * identified entity. This variable is mutable and initialized to {@code 1} by default,
     * serving as the starting point for tracking ride counts. The value of this field
     * can be updated as needed using the provided setter method.</p>
     *
     * <p>Key characteristics include:</p>
     * <ul>
     *   <li>Mutable: The value of {@code count} can be dynamically updated.</li>
     *   <li>Default initialization: Starts with a value of {@code 1}.</li>
     *   <li>Tracks the cumulative count of rides associated with the specific name and server combination.</li>
     * </ul>
     */
    @Setter private int count = 1;

    /**
     * Adds a specified value to the current count.
     *
     * <p>This method increments the internal {@code count} field of the class by the
     * provided integer value. It is useful for tracking or accumulating values related
     * to the class's context (e.g., number of rides, actions, or interactions).</p>
     *
     * @param i the value to be added to the count. This should be an integer representing
     *          the amount by which the count needs to be increased.
     */
    public void addCount(int i) {
        this.count += i;
    }

    /**
     * Compares the provided string with the server's name to determine if they are equal.
     *
     * <p>The comparison is case-insensitive. Additionally, the method checks if the
     * provided string, after removing all non-alphabetic characters, matches the server name.</p>
     *
     * @param s the string to compare against the server name. It can contain non-alphabetic characters or be differently cased.
     * @return {@code true} if the provided string matches the server name (directly or after processing); {@code false} otherwise.
     */
    public boolean serverEquals(String s) {
        if (server.equalsIgnoreCase(s)) return true;
        return s.replaceAll("[^A-Za-z ]", "").equalsIgnoreCase(s);
    }
}