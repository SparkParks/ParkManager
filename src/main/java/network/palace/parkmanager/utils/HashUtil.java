package network.palace.parkmanager.utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * The {@code HashUtil} class provides utility methods for generating hash values.
 * It specifically includes functionality for generating an MD5 hash from a given
 * input string, with a use case in hashing inventory JSON data.
 * </p>
 *
 * <p>
 * This utility is built on the {@link MessageDigest} class, and it
 * ensures a consistent hashing process using the MD5 algorithm. In case the
 * MD5 algorithm is not available, an error is logged during initialization.
 * </p>
 *
 * <p>
 * Usage notes:
 * <ul>
 *     <li>The class is designed to work with strings. Input is typically encoded to bytes before hashing.</li>
 *     <li>The hashing algorithm used is fixed to MD5, which is not suitable for cryptographic security.</li>
 *     <li>Output is returned as a lowercase hexadecimal string to maintain uniformity.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Limitations:
 * <ul>
 *     <li>MD5 is considered weak for cryptographic purposes. Use stronger algorithms for secure applications.</li>
 *     <li>The method is static and dependent on the singleton nature of {@code MessageDigest}. Concurrent access may lead to unexpected results.</li>
 * </ul>
 * </p>
 */
public class HashUtil {

    /**
     * <p>
     * A static instance of {@link MessageDigest} used for generating hash values
     * using the MD5 algorithm.
     * </p>
     *
     * <p>
     * This instance is initialized once when the class is loaded and is used
     * across all method calls to generate hash values. It helps maintain
     * consistency and avoids creating new instances of {@link MessageDigest}.
     * </p>
     *
     * <p>Characteristics:</p>
     * <ul>
     *   <li>Uses the MD5 hash algorithm, which is deterministic and produces a
     *       fixed-length (128-bit) hash value.</li>
     *   <li>Thread safety is not guaranteed, as {@link MessageDigest} instances
     *       are not thread-safe. Use synchronization or alternative approaches
     *       in a multithreaded environment.</li>
     *   <li>If the MD5 algorithm is unavailable during class initialization, an
     *       exception is logged to indicate the issue.</li>
     * </ul>
     *
     * <p>Limitations:</p>
     * <ul>
     *   <li>As MD5 is not cryptographically secure, this field is not suitable
     *       for secure hashing purposes.</li>
     *   <li>This instance is singleton in nature, and any shared usage should
     *       ensure proper synchronization to avoid data corruption.</li>
     * </ul>
     */
    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("NO MD5?");
        }
    }

    /**
     * <p>
     * Generates an MD5 hash of the provided input string and returns it
     * as a lowercase hexadecimal string.
     * </p>
     *
     * <p>
     * The method takes the input string, converts it into a byte array, and
     * processes it to generate a hash using the MD5 algorithm. The resulting
     * hash is encoded in lowercase hexadecimal format for uniformity.
     * </p>
     *
     * <p><strong>Note:</strong> This method depends on the singleton {@code MessageDigest}
     * instance and the MD5 hashing algorithm. It is not suitable for cryptographically
     * secure applications.</p>
     *
     * @param inventory the input string to be hashed, typically expected to represent
     *                  inventory data in JSON format. Must not be {@code null}.
     * @return the generated MD5 hash as a lowercase hexadecimal string.
     */
    public static String generateHash(String inventory) {
        digest.update(inventory.getBytes());
        return DatatypeConverter.printHexBinary(digest.digest()).toLowerCase();
    }
}
