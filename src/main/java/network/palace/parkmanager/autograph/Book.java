package network.palace.parkmanager.autograph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.utils.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Represents a book entity that can be signed by multiple individuals and provides details
 * about its autographs.
 *
 * <p>This class provides methods for accessing and managing the book's metadata,
 * including its unique identifier, name, list of signatures, and its displayable content as a custom item.</p>
 *
 * <p>The book contains:
 * <ul>
 *   <li>A unique integer <code>id</code>, representing its identifier in the catalog.</li>
 *   <li>A <code>uuid</code> (Universally Unique Identifier) for additional identification.</li>
 *   <li>A <code>name</code>, representing the book's title or creation reference.</li>
 *   <li>A list of <code>Signature</code> objects capturing details about individuals who signed the book.</li>
 * </ul>
 * </p>
 *
 * <h3>Functionality:</h3>
 * <ul>
 *   <li><code>getFirstName()</code>:
 *   Retrieves the name of the first signer in the book. Returns "null" if no signatures are present.</li>
 *   <li><code>getLastName()</code>:
 *   Retrieves the name of the last signer in the book. Returns "null" if no signatures are present.</li>
 *   <li><code>getBook()</code>:
 *   Constructs and returns a customized item representation of the book as a <code>ItemStack</code>.
 *   This item includes all necessary metadata (e.g., author, title, signatures) and designed content (e.g., formatted display pages).</li>
 * </ul>
 *
 * <h3>Behavior of <code>getBook()</code>:</h3>
 * <p>The generated book item includes:
 * <ul>
 *   <li>A formatted title combining a default title prefix and the book's <code>id</code>.</li>
 *   <li>A customized author field using the book's <code>name</code>.</li>
 *   <li>An overview of its autographs (e.g., "No Autographs" if there are none, or a range from the first to the last signer).</li>
 *   <li>Individual pages for each signature, displaying the signer, message, and styled details.</li>
 * </ul>
 * </p>
 */
@Getter
@AllArgsConstructor
public class Book {

    /**
     * <p>Represents the unique identifier for the book.</p>
     *
     * <p>This identifier is used primarily to distinguish this book
     * from other books in the system. Each book should have a unique ID.</p>
     */
    private int id;

    /**
     * Represents the universally unique identifier (UUID) used to uniquely identify this specific Book instance.
     *
     * <p>This variable is essential for distinguishing each Book object from others,
     * providing a unique identifying value that ensures no two Book instances are conflated.</p>
     *
     * <p>The UUID is immutable and should be assigned at the time of object creation.</p>
     */
    private UUID uuid;

    /**
     * Represents the name of the book.
     * <p>
     * This variable is used to store the name or title of the book
     * and serves as a core attribute to differentiate books from
     * one another.
     */
    private String name;

    /**
     * A private collection of {@link Signature} objects associated with a {@link Book}.
     * <p>
     * This field stores a list of signatures, where each {@link Signature} represents
     * a specific individual's sign-off or message on the book.
     * </p>
     * <p>
     * Each {@link Signature} includes:
     * <ul>
     *   <li>The name of the signer</li>
     *   <li>The message left by the signer</li>
     *   <li>The time the signature was added</li>
     * </ul>
     * </p>
     */
    private List<Signature> signatures;

    /**
     * Retrieves the first name of the signer from the list of signatures.
     * <p>
     * This method accesses the first {@code Signature} in the {@code signatures} list and returns the
     * {@code signer} associated with it. If the {@code signatures} list is empty, the method returns the string "null".
     *
     * @return A {@code String} representing the first signer's name, or "null" if no signatures are present.
     */
    public String getFirstName() {
        return signatures.isEmpty() ? "null" : signatures.get(0).getSigner();
    }

    /**
     * Retrieves the last signer's name from the list of signatures.
     * <p>
     * If the {@code signatures} list is empty, this method will return {@code "null"}.
     * Otherwise, it retrieves the {@link Signature#getSigner()} value of the last element
     * in the list.
     *
     * @return a {@link String} representing the last signer's name, or {@code "null"}
     *         if the list is empty.
     */
    public String getLastName() {
        return signatures.isEmpty() ? "null" : signatures.get(signatures.size() - 1).getSigner();
    }

    /**
     * Creates and returns a book {@link ItemStack} that represents an autographed book
     * with relevant metadata, including its title, author, pages, and contents based
     * on the associated signatures.
     *
     * <p>The book's metadata is dynamically built from the current state of the
     * instance, such as its title, author name, autograph details, and signature
     * messages.</p>
     *
     * <p>If no signatures are present, the book will display an appropriate message.
     * If there is one signature, it will show the author's name. If there are multiple
     * signatures, it will include the first and last names based on a given logic.</p>
     *
     * <ul>
     * <li>Title: Derived from a predefined constant and an identifier.</li>
     * <li>Author: Uses the name field of the instance.</li>
     * <li>Pages: Contains details about the autographs, the first page showing
     * metadata and subsequent pages showing formatted signature messages.</li>
     * <li>Lore: Provides a summary of the autographs contained in the book.</li>
     * </ul>
     *
     * @return A {@link ItemStack} representing a customized written book with
     *         metadata and autograph data.
     */
    public ItemStack getBook() {
        ItemStack book = ItemUtil.create(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(AutographManager.BOOK_TITLE + " #" + id);
        String extra;
        if (signatures.isEmpty()) {
            extra = "\n" + ChatColor.RED + "" + ChatColor.ITALIC + "No Autographs";
        } else if (signatures.size() == 1) {
            extra = ChatColor.BLUE + "" + ChatColor.ITALIC + getFirstName();
        } else {
            extra = ChatColor.BLUE + "" + ChatColor.ITALIC + getFirstName() + ChatColor.GREEN +
                    " to " + ChatColor.BLUE + "" + ChatColor.ITALIC + getLastName();
        }
        meta.addPage(AutographManager.FIRST_PAGE + extra);
        meta.setAuthor(name);
        meta.setLore(Arrays.asList("", ChatColor.GREEN + "Contains:", "", extra.replaceAll("\n", "")));
        for (Signature signature : signatures) {
            meta.addPage(ChatColor.translateAlternateColorCodes('&', signature.getMessage()) +
                    ChatColor.YELLOW + "\n- " + ChatColor.BLUE + "" + ChatColor.ITALIC + "" + signature.getSigner());
        }
        book.setItemMeta(meta);
        return book;
    }
}
