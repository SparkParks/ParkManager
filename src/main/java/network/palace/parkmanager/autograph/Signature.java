package network.palace.parkmanager.autograph;

import lombok.Getter;

/**
 * Represents a signature with a signer's name, message, and timestamp.
 *
 * <p>The {@code Signature} class provides a structure for storing
 * information about an individual's signature, including:
 * <ul>
 *   <li>{@code signer}: The name of the individual who signed.</li>
 *   <li>{@code message}: The message left by the signer.</li>
 *   <li>{@code time}: The timestamp representing when the signature was created.</li>
 * </ul>
 * </p>
 *
 * <p>The {@code signer} field undergoes formatting during object construction, where:
 * <ul>
 *   <li>All underscores {@code _} are replaced with spaces.</li>
 *   <li>Extra spaces are removed.</li>
 *   <li>Leading and trailing spaces are trimmed.</li>
 * </ul>
 * </p>
 */
@Getter
public class Signature {

    /**
     * The name of the individual who signed.
     *
     * <p>This field stores the formatted name of the signer. During object construction,
     * the following formatting rules are applied:
     * <ul>
     *   <li>All underscores ({@code _}) in the input are replaced with spaces.</li>
     *   <li>Consecutive spaces are condensed into a single space.</li>
     *   <li>Leading and trailing spaces are trimmed.</li>
     * </ul>
     * </p>
     *
     * <p>The value of this field is immutable and cannot be modified after initialization.</p>
     */
    private final String signer;

    /**
     * Represents the message left by the signer in a signature.
     *
     * <p>The {@code message} field holds the textual content associated with the signature.
     * It is intended to store any notes, thoughts, or declarations provided by the signer.</p>
     *
     * <p><strong>Key Characteristics:</strong></p>
     * <ul>
     *   <li>Immutable: The value of this field cannot be changed once the object is created.</li>
     *   <li>Can be of arbitrary length, depending on the user's input.</li>
     *   <li>May be optional, depending on the usage context of the {@code Signature} class.</li>
     * </ul>
     */
    private final String message;

    /**
     * Represents the timestamp at which the signature was created.
     *
     * <p>The {@code time} field is stored as a {@code long} and typically
     * represents the epoch time in milliseconds. This value indicates
     * the precise moment the signature was created.</p>
     *
     * <p>It is used to:
     * <ul>
     *   <li>Track when the signature was made.</li>
     *   <li>Facilitate time-based operations, such as sorting or comparison.</li>
     *   <li>Provide audit or logging information.</li>
     * </ul>
     * </p>
     */
    private final long time;

    /**
     * Constructs a {@code Signature} instance with the specified signer, message, and timestamp.
     * <p>
     * The {@code signer} parameter undergoes the following transformations:
     * <ul>
     *   <li>All underscores ({@code _}) are replaced with spaces.</li>
     *   <li>Multiple consecutive spaces are reduced to a single space.</li>
     *   <li>Leading and trailing spaces are trimmed.</li>
     * </ul>
     * </p>
     *
     * @param signer the name of the individual who signed; it will be formatted to ensure proper spacing.
     * @param message the message or note associated with the signature.
     * @param time the timestamp of when the signature was created, represented in milliseconds since the epoch.
     */
    public Signature(String signer, String message, long time) {
        this.signer = signer.replaceAll("_", " ").replaceAll(" {2}", " ").trim();
        this.message = message;
        this.time = time;
    }
}
