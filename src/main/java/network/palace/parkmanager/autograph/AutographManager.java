package network.palace.parkmanager.autograph;

import com.google.common.collect.ImmutableMap;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.ReflectionUtils;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>The {@code AutographManager} class manages the core functionality for an autograph system,
 * including autograph collection, book operations, and interaction flows. It serves as the primary
 * component for handling autographs in a multi-player environment.</p>
 *
 * <p>This class provides methods for creating, organizing, and modifying Autograph Books, as well as
 * handling player interactions related to signing, viewing, and managing autographs. It also deals
 * with session-based tasks such as managing active sessions and handling logout scenarios.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *  <li>Retrieve a player's autographs, either as a list or sorted into books.</li>
 *  <li>Manage Autograph Books, including interactions, opening, and signature handling.</li>
 *  <li>Facilitate signing requests and manage responses between players.</li>
 *  <li>Session management and cache handling for autograph updates.</li>
 *  <li>Custom utility functions for internal operations like timer management and NMS item conversion.</li>
 * </ul>
 *
 */
public class AutographManager {
    /**
     * <p>The title of the Autograph Book, displayed with a customized color.</p>
     *
     * <p>This constant is used as a visual identifier for autograph books within the system, formatted
     * with {@link ChatColor#DARK_AQUA} for enhanced readability and aesthetic appeal.
     * It serves as a default title for all autograph books managed by the <code>AutographManager</code>.</p>
     *
     * <ul>
     *   <li>Color: {@link ChatColor#DARK_AQUA}</li>
     *   <li>Default Title: <code>"Autograph Book"</code></li>
     * </ul>
     */
    public static final String BOOK_TITLE = ChatColor.DARK_AQUA + "Autograph Book";

    /**
     * <p>Represents the maximum number of autographs that can be stored per autograph book.</p>
     *
     * <p>This constant defines the limit for how many {@link Signature} entries can be included in a
     * single {@link Book}. If the number of signatures exceeds this value, they are distributed
     * across multiple books.</p>
     *
     * <p>It is used within the {@link AutographManager} to manage the handling and distribution
     * of autograph entries when interacting with books.</p>
     */
    public static final int AUTOS_PER_BOOK = 30;

    /**
     * A constant representing the formatted and color-coded introductory page text
     * for the Autograph Book in the system.
     *
     * <p>The text provides the following details:
     * <ul>
     *     <li>The name of the Autograph Book series ("Palace Network").</li>
     *     <li>Instructions for collecting autographs from characters and staff members.</li>
     *     <li>The maximum number of autographs supported per book, dynamically included
     *         via the {@code AUTOS_PER_BOOK} value.</li>
     *     <li>Guidance for managing multiple books, including the ability to switch books
     *         while holding the shift key and clicking.</li>
     * </ul>
     *
     * <p>This is primarily utilized when generating the content of the first page in an
     * Autograph Book for a player and includes necessary formatting using
     * {@code ChatColor.translateAlternateColorCodes}.
     */
    public static final String FIRST_PAGE = ChatColor.translateAlternateColorCodes('&', "&d&lPalace Network\n&9&lAutograph Book\n\n&aMeet &9Characters &aand Staff Members to get your book signed!\n&eEach book holds up to " + AUTOS_PER_BOOK + " autographs. &9Hold shift and click to switch books.\n&a&nThis book contains:\n");

    //This contains the sender->target pair when sender is actively signing target's book
    /**
     * A map representing active autograph sessions.
     * <p>
     * This map maintains a relationship between players engaged in an autograph process,
     * where the key is the {@link UUID} of the player initiating the session (sender), and the value is
     * the {@link UUID} of the player receiving the request (target).
     * </p>
     * <p>
     * <strong>Usage:</strong>
     * <ul>
     *     <li>Tracks ongoing autograph sessions.</li>
     *     <li>Allows efficient lookup to handle interactions during signing requests.</li>
     *     <li>Enables session validation and handling session-specific behaviors.</li>
     * </ul>
     * </p>
     * <p>
     * The map is dynamically updated as autograph sessions are started, responded to, and concluded.
     * </p>
     */
    private HashMap<UUID, UUID> activeSessions = new HashMap<>();

    //Contains the taskID of the sender countdown task
    /**
     * <p>
     * Represents a map that keeps track of an autograph signing session for users. Each key
     * is a {@link UUID} that represents the user's unique identifier, and each value is an
     * {@link Integer} representing the state or status associated with that user's signing session.
     * </p>
     *
     * <p>
     * This map is primarily used in the context of managing autograph signing activities where
     * multiple users are involved at the same time. The exact purpose of the integer value may vary
     * (e.g., a timeout counter, session status, or state management within a signing process).
     * </p>
     *
     * <p>
     * For example:
     * <ul>
     * <li><strong>UUID:</strong> Represents the unique user this signing process is affiliated with.</li>
     * <li><strong>Integer:</strong> May represent session-related metrics, counters, or status codes.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This structure supports concurrency safely if accessed through thread-safe mechanisms,
     * though it is inherently not thread-safe.
     * </p>
     */
    private HashMap<UUID, Integer> signerMap = new HashMap<>();

    //Contains the taskID of the target countdown task
    /**
     * A map that associates a {@link UUID} (representing a unique player)
     * with an integer value that tracks the current state or number associated with the player.
     * <p>
     * This map is likely used to manage player-specific data within the autograph system,
     * such as tracking signing sessions, interactions, or state changes.
     * </p>
     * <ul>
     *     <li>The key is a {@link UUID}, uniquely identifying each player.</li>
     *     <li>The value is an {@link Integer}, representing a state, counter, or other numerical data tied to the player.</li>
     * </ul>
     * <p>
     * It is used internally by the {@code AutographManager} class for managing player data
     * during autograph-related interactions.
     * </p>
     */
    private HashMap<UUID, Integer> receiverMap = new HashMap<>();

    /**
     * A static reference to the {@link Method} instance used within the {@code AutographManager} class
     * for invoking certain internal operations. The exact method this represents is determined by
     * reflective access mechanisms.
     *
     * <p>
     * <b>Usage:</b>
     * <ul>
     *   <li>This variable is intended to be used internally by the {@code AutographManager} class.</li>
     *   <li>It likely facilitates interaction with methods or operations tied to autograph-related functionality.</li>
     * </ul>
     *
     * <p>
     * <b>Important Notes:</b>
     * <ul>
     *   <li>This variable is declared as {@code private static}, suggesting it is shared across all
     *       instances of the enclosing class but not accessible directly outside this class.</li>
     *   <li>It assumes a certain target method and parameters dynamically at runtime, potentially
     *       interacting with Minecraft server internals.</li>
     * </ul>
     */
    private static Method getHandle;

    /**
     * Represents a reflection-based reference to a method responsible for forcing a client to open a book in the game.
     *
     * <p>This method is likely utilized to interact with the Minecraft server's internal mechanics
     * (NMS), enabling actions such as opening a written book for a specific player programmatically.</p>
     *
     * <ul>
     *   <li>Declared as {@code private static Method}, which implies it is shared across all instances of
     *       the containing class and is used internally only.</li>
     *   <li>Serves as a utility within the {@code AutographManager} class to provide functionality tied
     *       to autograph book features.</li>
     * </ul>
     *
     * <p>Note: Reflection is used to access the method, hence it requires access to the specific server
     * implementation that supports this functionality.</p>
     */
    private static Method openBook;

    static {
        try {
            getHandle = ReflectionUtils.getMethod("CraftPlayer", ReflectionUtils.PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
            openBook = ReflectionUtils.getMethod("EntityPlayer", ReflectionUtils.PackageType.MINECRAFT_SERVER,
                    "a", ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ItemStack"),
                    ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("EnumHand"));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a list of signatures associated with a specific UUID.
     * <p>
     * This method queries the database for autograph data linked to the provided UUID
     * and transforms each result into a {@link Signature} object. Each signature contains
     * the signer, message, and timestamp of the autograph.
     *
     * @param uuid the unique identifier whose associated signatures are to be retrieved
     *             <ul>
     *                 <li>Must not be null.</li>
     *                 <li>Represents the entity for which autographs are stored.</li>
     *             </ul>
     * @return a {@link List} of {@link Signature} objects corresponding to the autographs
     *         associated with the given UUID
     *         <ul>
     *             <li>The list may be empty if no autographs are found.</li>
     *             <li>Each entry represents an autograph with signer details, message, and signing time.</li>
     *         </ul>
     */
    public List<Signature> getSignatures(UUID uuid) {
        List<Signature> list = new ArrayList<>();
        for (Object o : Core.getMongoHandler().getAutographs(uuid)) {
            Document doc = (Document) o;
            list.add(new Signature(doc.getString("author"), doc.getString("message"), doc.getLong("time")));
        }
        return list;
    }

    /**
     * Retrieves a list of books containing autographs associated with the given player.
     * <p>
     * This method processes the autographs from the player's registry to create one or more
     * {@link Book} objects. Each book contains a subset of autographs, determined by the
     * maximum number of entries allowed per book. The list is sorted by the signer's name
     * (case-insensitive) and then by the timestamp of the autographs. If no autographs are
     * associated with the player, a default book is created with no autographs.
     *
     * <ul>
     *     <li>If the player has autographs, they will be divided into books based on a predefined maximum signatures per book.</li>
     *     <li>If no autographs exist, a single book with an empty signatures list will be returned.</li>
     * </ul>
     *
     * @param player the player whose autograph books are being generated
     *               <ul>
     *                   <li>Must not be {@code null}.</li>
     *                   <li>Represents the owner of the signature data used to generate the books.</li>
     *               </ul>
     * @return a {@link List} of {@link Book} objects for the given player
     *         <ul>
     *             <li>Each book contains the player's UUID, name, and a collection of signatures.</li>
     *             <li>The list will always have at least one book, even if no autographs exist.</li>
     *         </ul>
     */
    public List<Book> getBooks(CPlayer player) {
        List<Signature> autographs = (List<Signature>) player.getRegistry().getEntry("autographs");
        if (autographs.isEmpty())
            return Collections.singletonList(new Book(1, player.getUniqueId(), player.getName(), new ArrayList<>()));
        autographs.sort((o1, o2) -> {
            if (o1.getSigner().equals(o2.getSigner())) {
                return (int) (o1.getTime() - o2.getTime());
            }
            return o1.getSigner().toLowerCase().compareTo(o2.getSigner().toLowerCase());
        });
        List<Book> books = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < (int) Math.ceil((double) autographs.size() / AUTOS_PER_BOOK); i++) {
            books.add(new Book(i + 1, player.getUniqueId(), player.getName(),
                    autographs.subList(start, autographs.size() - start > AUTOS_PER_BOOK ? start + AUTOS_PER_BOOK : autographs.size())));
            start += AUTOS_PER_BOOK;
        }
        return books;
    }

    /**
     * Gives a written book to the specified player.
     * <p>
     * This method checks if the player is in build mode. If not, it creates a written book
     * with a title and author set dynamically based on predefined or player-specific information.
     * The book is placed directly into the 7th inventory slot (index 6) of the player's inventory.
     * </p>
     *
     * @param player the player to whom the book will be given
     *               <ul>
     *                   <li>Must not be null.</li>
     *                   <li>Represents the player who will receive the book.</li>
     *               </ul>
     */
    public void giveBook(CPlayer player) {
        if (ParkManager.getBuildUtil().isInBuildMode(player)) return;
        Core.runTask(() -> {
            ItemStack book = ItemUtil.create(Material.WRITTEN_BOOK);
            BookMeta bm = (BookMeta) book.getItemMeta();
            bm.setTitle(BOOK_TITLE);
            bm.setAuthor(player.getName());
            book.setItemMeta(bm);
            player.setInventorySlot(7, book);
        });
    }

    /**
     * Handles player interaction for autograph-related actions.
     * <p>
     * This method processes interactions such as signing books, accessing autograph lists,
     * or initializing an autograph book creation. The specific behavior depends on the player's
     * current item and context:
     * <ul>
     *     <li>If the player is already in an autograph-related session, the method exits early.</li>
     *     <li>If the player holds no relevant item and is in the designated slot (slot 7),
     *     a new autograph book is given.</li>
     *     <li>If the player is sneaking, they can select from their available books via a menu.</li>
     *     <li>If the book in the player's hand has relevant metadata, it is opened for viewing.</li>
     *     <li>If no specific book context is available, the first available book is opened.</li>
     * </ul>
     *
     * @param player the player interacting with the system
     *               <ul>
     *                   <li>Must not be null.</li>
     *                   <li>Represents the player performing the interaction (e.g., signing or opening books).</li>
     *               </ul>
     */
    public void handleInteract(CPlayer player) {
        if (activeSessions.containsValue(player.getUniqueId())) {
            //Someone's signing their book, so do nothing
            return;
        }
        ItemStack item = player.getItemInMainHand();
        if (item == null || !item.getType().equals(Material.WRITTEN_BOOK)) {
            if (player.getHeldItemSlot() == 7) {
                giveBook(player);
            } else {
                return;
            }
        }
        if (player.isSneaking()) {
            //Player wants to select between their different books, so open the menu
            List<Book> books = getBooks(player);

            List<MenuButton> buttons = new ArrayList<>();
            for (int i = 0; i < books.size(); i++) {
                ItemStack bookItem = books.get(i).getBook();
                buttons.add(new MenuButton(i, bookItem, ImmutableMap.of(ClickType.LEFT, p -> openBook(p, bookItem))));
            }

            int size = books.size() < 10 ? 9 : (books.size() < 19 ? 18 : (books.size() < 28 ? 27 : (books.size() < 37 ? 36 : (books.size() < 46 ? 45 : 54))));
            new Menu(size, ChatColor.BLUE + "Choose an Autograph Book", player, buttons).open();
        } else if (item.getItemMeta().hasLore()) {
            //Player wants to open the book in their hand
            openBook(player, item);
        } else {
            //Player doesn't have a numbered book, so we need to give it to them
            List<Book> books = getBooks(player);
            openBook(player, getBooks(player).get(0).getBook());
        }
    }

    /**
     * Opens a written book for the specified player in their Minecraft client.
     *
     * <p>This method attempts to display a book to the player by simulating
     * the action of opening a written book in their inventory. It performs
     * the following steps:
     * <ul>
     *   <li>Checks if the given item is a written book; if not, the method returns immediately.</li>
     *   <li>Sets the player's held item slot to the 7th slot (index 6).</li>
     *   <li>If the book is not already held in the main hand, it assigns the book to the specified slot.</li>
     *   <li>Uses reflection to access Minecraft server internals for triggering the "open book" action.</li>
     * </ul>
     * If reflection fails due to an exception, the stack trace is printed.
     * </p>
     *
     * @param player the player for whom the book will be opened
     *               <ul>
     *                 <li>Must not be null.</li>
     *                 <li>Represents the player interacting with the book.</li>
     *               </ul>
     * @param book   the book item to be opened
     *               <ul>
     *                 <li>Must be of type {@link Material#WRITTEN_BOOK}.</li>
     *                 <li>Represents the written book that will be opened for the player.</li>
     *               </ul>
     */
    public void openBook(CPlayer player, ItemStack book) {
        if (!book.getType().equals(Material.WRITTEN_BOOK)) return;
        player.setHeldItemSlot(7);
        if (!player.getItemInMainHand().equals(book)) player.getInventory().setItem(7, book);

        try {
            Object entityPlayer = getHandle.invoke(player.getBukkitPlayer());
            Class<?> enumHand = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("EnumHand");
            Object[] enumArray = enumHand.getEnumConstants();
            openBook.invoke(entityPlayer, getItemStack(book), enumArray[0]);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an autograph request from one player to another, initiating a process
     * where the target player can accept or deny the request. The request has a timed duration
     * and automatically expires if no response is received.
     * <p>
     * The sender and target player's registries are updated with request-related information.
     * A formatted message is also displayed to both players, offering options to accept or deny the request.
     * If no action is taken, the request times out, and both players are notified.
     * </p>
     * <ul>
     *   <li>The sender is notified upon successful sending of the request.</li>
     *   <li>The target receives a clickable message to respond to the request.</li>
     *   <li>The request expires after a fixed duration (e.g., 400 ticks), with both players being informed of the timeout.</li>
     * </ul>
     *
     * @param sender the player sending the autograph request
     *               <ul>
     *                   <li>Must not be null.</li>
     *                   <li>Represents the initiator of the autograph request process.</li>
     *               </ul>
     * @param target the player receiving the autograph request
     *               <ul>
     *                   <li>Must not be null.</li>
     *                   <li>Represents the recipient of the autograph request.</li>
     *               </ul>
     */
    public void requestToSign(CPlayer sender, CPlayer target) {
        if (target.getRegistry().hasEntry("autographRequestFrom") && target.getRegistry().getEntry("autographRequestFrom") != null) {
            sender.sendMessage(ChatColor.RED + "That player already has an autograph request!");
            return;
        }
        target.getRegistry().addEntry("autographRequestFrom", sender.getUniqueId());
        sender.getRegistry().addEntry("autographRequestTo", target.getUniqueId());

        String coloredName = target.getRank().getTagColor() + target.getName();
        sender.sendMessage(ChatColor.GREEN + "Autograph Request sent to " + coloredName);

        new FormattedMessage(sender.getName()).color(sender.getRank().getTagColor())
                .then(" has sent you an ").color(ChatColor.GREEN)
                .then("Autograph Request. ").color(ChatColor.DARK_AQUA).style(ChatColor.BOLD)
                .then("Click here to Accept").color(ChatColor.YELLOW).command("/autograph accept").tooltip(ChatColor.GREEN + "Click to Accept!")
                .then(" - ").color(ChatColor.GREEN)
                .then("Click here to Deny").color(ChatColor.RED).command("/autograph deny").tooltip(ChatColor.RED + "Click to Deny!")
                .send(target);

        signerMap.put(sender.getUniqueId(), Core.runTaskLater(ParkManager.getInstance(), () -> {
            if (!target.getRegistry().hasEntry("autographRequestFrom") || !target.getRegistry().getEntry("autographRequestFrom").equals(sender.getUniqueId()))
                //If target does not have a pending request or if the pending request is for someone else, do nothing
                return;
            //Otherwise, time out the request

            target.getRegistry().removeEntry("autographRequestFrom");
            sender.getRegistry().removeEntry("autographRequestTo");

            sender.sendMessage(ChatColor.RED + "Your Autograph Request to " + coloredName + ChatColor.RED + " has timed out!");
            target.sendMessage(sender.getRank().getTagColor() + sender.getName() + "'s " + ChatColor.RED + "Autograph Request sent to you has timed out!");
        }, 400L));
        UUID uuid = sender.getUniqueId();
        receiverMap.put(sender.getUniqueId(), Core.runTaskTimer(ParkManager.getInstance(), new Runnable() {
            int i = 20;

            @Override
            public void run() {
                if (target == null || sender == null) {
                    Core.cancelTask(receiverMap.remove(uuid));
                    return;
                }
                if (i <= 0) {
                    target.getActionBar().show(ChatColor.RED + sender.getName() + "'s Autograph Request Expired!");
                    sender.getActionBar().show(ChatColor.RED + "Your Autograph Request to " + target.getName() +
                            " Expired!");
                    cancelTimer(sender.getUniqueId());
                    return;
                }
                target.getActionBar().show(ChatColor.AQUA + sender.getName() + "'s Autograph Request: " +
                        getTimerMessage(i) + " " + ChatColor.AQUA + i + "s");
                sender.getActionBar().show(ChatColor.GREEN + "Your Autograph Request to " + ChatColor.AQUA +
                        target.getName() + ": " + getTimerMessage(i) + " " + ChatColor.AQUA + i + "s");
                i--;
            }
        }, 0, 20L));
    }

    /**
     * Handles the response to an autograph request by a target player.
     * The response can either accept or deny the request. If accepted, a session is
     * created for signing; if denied, the request is declined and messaging is updated accordingly.
     *
     * <p>This method ensures that pending request entries are removed from both
     * the sender's and target's registries. It also cancels any associated timers and
     * updates the involved players with appropriate notifications.</p>
     *
     * @param target The player receiving the autograph request. It is their response
     *               that is being processed.
     * @param accept A boolean indicating whether the target accepts (<code>true</code>)
     *               or denies (<code>false</code>) the autograph request.
     */
    public void requestResponse(CPlayer target, boolean accept) {
        if (!target.getRegistry().hasEntry("autographRequestFrom")) {
            target.sendMessage(ChatColor.RED + "You don't have any pending Autograph Requests!");
            return;
        }

        CPlayer sender = Core.getPlayerManager().getPlayer((UUID) target.getRegistry().getEntry("autographRequestFrom"));

        if (sender == null) {
            target.sendMessage(ChatColor.RED + "You don't have any pending Autograph Requests!");
            return;
        }

        target.getRegistry().removeEntry("autographRequestFrom");
        sender.getRegistry().removeEntry("autographRequestTo");

        cancelTimer(sender.getUniqueId());

        final String senderName = sender.getRank().getTagColor() + sender.getName();
        final String targetName = target.getRank().getTagColor() + target.getName();

        if (accept) {
            target.getActionBar().show(ChatColor.GREEN + "You accepted " + senderName + "'s " + ChatColor.GREEN + "Autograph Request!");
            target.sendMessage(ChatColor.GREEN + "You accepted " + senderName + "'s " + ChatColor.GREEN + "Autograph Request!");
            sender.getActionBar().show(targetName + ChatColor.GREEN + " accepted your Autograph Request!");
            sender.sendMessage(targetName + ChatColor.GREEN + " accepted your Autograph Request! Sign with /sign " + ChatColor.YELLOW + "[message]");

            activeSessions.put(sender.getUniqueId(), target.getUniqueId());
            target.getInventory().setItem(7, new ItemStack(Material.AIR));
        } else {
            target.getActionBar().show(ChatColor.RED + "You denied " + senderName + "'s " + ChatColor.RED + "Autograph Request!");
            target.sendMessage(ChatColor.RED + "You denied " + senderName + "'s " + ChatColor.RED + "Autograph Request!");
            sender.getActionBar().show(targetName + ChatColor.RED + " denied your Autograph Request!");
            sender.sendMessage(targetName + ChatColor.RED + " denied your Autograph Request!");
        }
    }

    /**
     * Signs an autograph book for the specified target player during an active session.
     * <p>
     * The method checks if the sender player is engaged in an active signing session, signs the
     * target player's autograph book if a session exists, updates the autograph list,
     * and provides feedback to both the sender and the target. Sessions are removed post-signing.
     * </p>
     *
     * @param sender  The player attempting to sign another player's autograph book. This must be the player
     *                currently engaged in an active session.
     * @param message The custom message the sender wants to include in the autograph book.
     */
    public void sign(CPlayer sender, String message) {
        CPlayer tp = null;
        for (Map.Entry<UUID, UUID> entry : activeSessions.entrySet()) {
            if (entry.getKey().equals(sender.getUniqueId())) {
                tp = Core.getPlayerManager().getPlayer(entry.getValue());
                break;
            }
        }
        if (tp == null) {
            sender.sendMessage(ChatColor.RED + "You're not signing anyone's book right now!");
            return;
        }
        Core.getMongoHandler().signBook(tp.getUniqueId(), sender.getName(), message);
        updateAutographs(tp);
        giveBook(tp);
        tp.sendMessage(sender.getRank().getTagColor() + sender.getName() + ChatColor.GREEN + " has signed your Autograph Book!");
        tp.giveAchievement(1);
        sender.sendMessage(ChatColor.GREEN + "You signed " + tp.getName() + "'s Autograph Book!");
        activeSessions.remove(sender.getUniqueId());
    }

    /**
     * Removes an autograph from a player's autograph book.
     * <p>
     * This method ensures that the specified page of the autograph book is valid and checks additional constraints
     * such as if the player is in Build Mode or if there are active sessions involved. If all conditions are met,
     * the autograph will be removed from the specified page number.
     * </p>
     *
     * <p>Behavioral constraints:</p>
     * <ul>
     *     <li>Page number must be between 2 and 50.</li>
     *     <li>The player must not be in Build Mode.</li>
     *     <li>The player must have autographs recorded in their registry.</li>
     *     <li>An autograph cannot be removed while someone is signing the player's book.</li>
     *     <li>The autograph book must exist in the player's inventory at the designated slot and adhere to certain format rules.</li>
     * </ul>
     *
     * @param player The player attempting to remove the autograph. Includes all relevant states, such as inventory and registry data.
     * @param num    The page number of the autograph to be removed. Must be between 2 and 50, inclusive.
     */
    public void removeAutograph(CPlayer player, Integer num) {
        if (num < 2 || num > 50) {
            //Can't remove first page, and there can't be more than 50 pages
            player.sendMessage(ChatColor.RED + "You can't remove this page!");
            return;
        }
        if (ParkManager.getBuildUtil().isInBuildMode(player)) {
            player.sendMessage(ChatColor.RED + "You can't be in Build Mode while removing autographs!");
            return;
        }
        if (!player.getRegistry().hasEntry("autographs")) {
            player.sendMessage(ChatColor.RED + "There was an error removing the autograph!");
            return;
        }
        if (activeSessions.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't remove an autograph when someone's signing your book!");
            return;
        }

        ItemStack book = player.getInventory().getItem(7);
        if (book == null || !book.getType().equals(Material.WRITTEN_BOOK) || !((BookMeta) book.getItemMeta()).getTitle().contains("#")) {
            player.sendMessage(ChatColor.RED + "There's no book in your inventory!");
            player.setHeldItemSlot(7);
            handleInteract(player);
            return;
        }
        BookMeta meta = (BookMeta) book.getItemMeta();
        int bookNumber = Integer.parseInt(meta.getTitle().replaceAll(BOOK_TITLE + " #", ""));

        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            int pageNumber = (AUTOS_PER_BOOK * (bookNumber - 1)) + num;
            List<Signature> autographs = (List<Signature>) player.getRegistry().getEntry("autographs");
            if (pageNumber > (autographs.size() + 1)) {
                player.sendMessage(ChatColor.RED + "That page doesn't exist!");
                player.sendMessage(ChatColor.YELLOW + "Make sure you're using a page number from your " + ChatColor.ITALIC + "current book!");
                return;
            }
            autographs.sort((o1, o2) -> {
                if (o1.getSigner().equals(o2.getSigner())) {
                    return (int) (o1.getTime() - o2.getTime());
                }
                return o1.getSigner().toLowerCase().compareTo(o2.getSigner().toLowerCase());
            });
            if (getSignatures(player.getUniqueId()).size() != autographs.size()) {
                updateAutographs(player);
                giveBook(player);
                player.sendMessage(ChatColor.RED + "Your autograph page numbers just changed, make sure you're removing the correct page!");
                return;
            }
            Signature remove = autographs.get(pageNumber - 2);
            Core.getMongoHandler().deleteAutograph(player.getUniqueId(), remove.getSigner(), remove.getTime());
            updateAutographs(player);
            giveBook(player);
            player.sendMessage(ChatColor.GREEN + "You removed an autograph from " + ChatColor.BLUE + remove.getSigner() + ChatColor.GREEN + " from your Autograph Book");
        });
    }

    /**
     * Updates the autographs for a given player by retrieving their signatures
     * and adding them to the player's registry under the "autographs" entry.
     *
     * <p>This method ensures that a non-null player has their unique signatures
     * stored appropriately in their registry.</p>
     *
     * @param player the player whose autographs are to be updated. Must be non-null.
     *               If the player is null, the method will not perform any action.
     */
    public void updateAutographs(CPlayer player) {
        if (player != null) player.getRegistry().addEntry("autographs", getSignatures(player.getUniqueId()));
    }

    /**
     * Handles the logout process for a player. This method ensures that any ongoing
     * autograph sessions or requests involving the player are canceled and that the
     * appropriate notifications are sent to other players. It also clears any timers
     * associated with the player.
     *
     * <p>The following scenarios are handled:
     * <ul>
     *   <li>If the player has a pending autograph request from another player, the request is canceled and the other player is notified.</li>
     *   <li>If the player has a pending autograph request to another player, the request is canceled and the target player is notified.</li>
     *   <li>If the player is actively signing another player's book, the signing session is terminated, the book is returned, and the other player is notified.</li>
     *   <li>If the player is getting their book signed by another player, the session is terminated but the book remains with the requesting player. The signing player is notified
     *  of the disconnection.</li>
     * </ul>
     * <p>Additionally, any active timers associated with the player are canceled to free up resources.
     *
     * @param player The player who is logging out. This parameter must not be null. If null, the method will return immediately without performing any actions.
     */
    public void logout(CPlayer player) {
        if (player == null) return;
        if (player.getRegistry().hasEntry("autographRequestFrom")) {
            //Player has a pending autograph request from another player
            CPlayer sender = Core.getPlayerManager().getPlayer((UUID) player.getRegistry().getEntry("autographRequestFrom"));
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + player.getName() + " has disconnected, so your autograph request has timed out!");
                sender.getActionBar().show(ChatColor.RED + "Autograph Request Cancelled");
                cancelTimer(sender.getUniqueId());
            }
        } else if (player.getRegistry().hasEntry("autographRequestTo")) {
            //Player has a pending autograph request to another player
            CPlayer target = Core.getPlayerManager().getPlayer((UUID) player.getRegistry().getEntry("autographRequestTo"));
            if (target != null) {
                target.sendMessage(ChatColor.RED + player.getName() + " has disconnected, so their autograph request has timed out!");
                target.getActionBar().show(ChatColor.RED + "Autograph Request Cancelled");
                cancelTimer(target.getUniqueId());
            }
        } else if (activeSessions.containsKey(player.getUniqueId())) {
            //Player is currently signing another player's book, need to give the book back
            CPlayer target = Core.getPlayerManager().getPlayer(activeSessions.remove(player.getUniqueId()));
            if (target != null) {
                updateAutographs(target);
                giveBook(target);
                target.sendMessage(ChatColor.RED + player.getName() + " has disconnected, so they couldn't sign your Autograph Book!");
            }
        } else if (activeSessions.containsValue(player.getUniqueId())) {
            //Player is currently getting their book signed by another player, need to cancel request session but don't need to give book back
            CPlayer sender = null;
            for (Map.Entry<UUID, UUID> entry : activeSessions.entrySet()) {
                if (entry.getValue().equals(player.getUniqueId())) {
                    sender = Core.getPlayerManager().getPlayer(entry.getKey());
                    if (sender != null) {
                        activeSessions.remove(entry.getKey());
                        break;
                    }
                }
            }
            if (sender != null)
                sender.sendMessage(ChatColor.RED + player.getName() + " has disconnected, so you can't sign their Autograph Book!");
        }
        cancelTimer(player.getUniqueId());
    }

    /**
     * Cancels the timers associated with the given <code>UUID</code>.
     * <p>
     * This method attempts to cancel tasks that are stored in the <code>signerMap</code>
     * and <code>receiverMap</code> corresponding to the provided <code>UUID</code>.
     * If an exception occurs while cancelling a task, it is caught and ignored.
     * </p>
     *
     * @param uuid the unique identifier used to locate and cancel the associated timers
     */
    private void cancelTimer(UUID uuid) {
        try {
            Core.cancelTask(signerMap.remove(uuid));
        } catch (Exception ignored) {
        }
        try {
            Core.cancelTask(receiverMap.remove(uuid));
        } catch (Exception ignored) {
        }
    }

    /**
     * Converts a Bukkit {@link ItemStack} object into its corresponding NMS (Net Minecraft Server) representation.
     * <p>
     * This method uses reflection to access and invoke the CraftBukkit method "asNMSCopy",
     * which transforms a standard Bukkit {@link ItemStack} into its underlying NMS format.
     * This is commonly used for interactions or modifications that require the internal NMS class.
     *
     * @param item The {@link ItemStack} to be converted to its NMS representation. This must not be null.
     * @return The NMS representation of the given {@link ItemStack} if conversion is successful,
     *         or <code>null</code> if an error occurs during reflection or method invocation.
     */
    private static Object getItemStack(ItemStack item) {
        try {
            Method asNMSCopy = ReflectionUtils.getMethod(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"),
                    "asNMSCopy", ItemStack.class);
            return asNMSCopy.invoke(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"), item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generates a timer message consisting of colored blocks to visually represent a time value.
     * <p>
     * The output string includes:
     * <ul>
     *   <li><b>Dark green blocks</b>: Represent completed segments of the timer.</li>
     *   <li><b>Green block</b>: One partially completed segment, if applicable.</li>
     *   <li><b>Red blocks</b>: Represent remaining time segments.</li>
     * </ul>
     *
     * @param time The time value (in an integer format) that determines the composition of the colored blocks.
     *             It is assumed to be non-negative.
     * @return A formatted string consisting of colored blocks, using Minecraft color codes and Unicode block characters
     *         to represent the timer visually.
     */
    private String getTimerMessage(int time) {
        int darkGreenBlocks = Math.floorDiv(time, 2);
        int greenBlocks = time % 2 == 0 ? 0 : 1;
        int red = (10 - darkGreenBlocks) - greenBlocks;

        char block = '▉';

        StringBuilder s = new StringBuilder(ChatColor.DARK_GREEN + "");
        //dark green blocks
        s.append(String.valueOf(block).repeat(Math.max(0, darkGreenBlocks)));
        if (greenBlocks == 1) {
            s.append(ChatColor.GREEN).append(block);
        }
        s.append(ChatColor.RED);
        s.append(String.valueOf(block).repeat(Math.max(0, (10 - darkGreenBlocks) - greenBlocks)));
        return s.toString();
    }
}
