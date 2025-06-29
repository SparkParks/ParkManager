package network.palace.parkmanager.shows.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Represents the various show types available in the system. Each show type is
 * associated with a name, a material representation, and an optional data byte.
 *
 * <p>This enumeration supports multiple categories of shows, including but not
 * limited to:</p>
 * <ul>
 *   <li>No shows or placeholder events.</li>
 *   <li>Normal shows such as fireworks and nighttime spectaculars.</li>
 *   <li>Projection shows with specific themes or concepts.</li>
 *   <li>Stage shows performed live, with various unique presentations.</li>
 *   <li>Parades with floats and characters.</li>
 *   <li>Seasonal and holiday shows, such as those for Halloween, Christmas,
 *       or Independence Day celebrations.</li>
 * </ul>
 *
 * <p>Each show type can be retrieved by its associated display name (using
 * {@link #fromString(String)}) or by converting its name into a formatted
 * database-compatible name (using {@link #getDBName()}).</p>
 */
@Getter
@AllArgsConstructor
public enum ShowType {
    NO_SHOW(ChatColor.GRAY + "No Show Scheduled", Material.STAINED_GLASS_PANE, (byte) 7),
    TBA(ChatColor.LIGHT_PURPLE + "To Be Announced", Material.BARRIER),
    //Normal Shows
    HEA(ChatColor.YELLOW + "Happily Ever After", Material.GLOWSTONE_DUST),
    WISHES(ChatColor.AQUA + "Wishes", Material.BLAZE_ROD),
    EPCOT(ChatColor.DARK_PURPLE + "Epcot Forever", Material.SNOW_BALL),
    IROE(ChatColor.GREEN + "Illuminations: Reflections of Earth", Material.NETHER_STAR),
    SITS(ChatColor.GOLD + "Symphony in the Stars", Material.DIAMOND_SWORD),
    SPECIAL(ChatColor.DARK_PURPLE + "Special Event", Material.DIAMOND),
    //Projection Shows
    OUAT(ChatColor.YELLOW + "Once Upon A Time", Material.BOOK),
    //Stage Shows
    FANTASMIC(ChatColor.BLUE + "Fantasmic", Material.DIAMOND_HELMET),
    FOTLK(ChatColor.YELLOW + "Festival of the Lion King", Material.INK_SACK, (byte) 3),
    FNTM(ChatColor.BLUE + "Finding Nemo: The Musical", Material.RAW_FISH, (byte) 2),
    JEDI(ChatColor.BLUE + "Jedi Training", Material.IRON_SWORD),
    MRFF(ChatColor.GOLD + "Mickey’s Royal Friendship Faire", Material.INK_SACK),
    GRINCHMAS(ChatColor.GREEN + "Grinchmas Wholiday Spectacular", Material.QUARTZ),
    //Parades
    FOF(ChatColor.DARK_AQUA + "Festival of Fantasy Parade", Material.INK_SACK, (byte) 12),
    MSEP(ChatColor.YELLOW + "Main Street Electrical Parade", Material.BLAZE_POWDER),
    MISIP(ChatColor.GREEN + "Move It! Shake It! MousekeDance It!", Material.SUGAR),
    MAGICHAPPENS(ChatColor.DARK_GREEN + "Magic Happens", Material.END_CRYSTAL),
    //Fourth of July
    CA(ChatColor.RED + "Celebrate " + ChatColor.BLUE + "America", Material.BANNER),
    //Halloween
    HALLOWISHES(ChatColor.GOLD + "Happy HalloWishes", Material.JACK_O_LANTERN),
    NOT_SO_SPOOKY(ChatColor.GOLD + "Not So Spooky Spectacular", Material.JACK_O_LANTERN),
    HOCUSPOCUS(ChatColor.GOLD + "Hocus Pocus Villain Spelltacular", Material.CAULDRON_ITEM),
    BOOTOYOU(ChatColor.GOLD + "Mickey's Boo To You Halloween Parade", Material.ROTTEN_FLESH),
    //Christmas
    FITS(ChatColor.BLUE + "Fantasy in the Sky", Material.DIAMOND),
    FHW(ChatColor.AQUA + "Frozen Holiday Wish", Material.QUARTZ),
    HOLIDAYWISHES(ChatColor.AQUA + "Holiday Wishes", Material.SNOW),
    CHRISTMASTIME_FIREWORKS(ChatColor.LIGHT_PURPLE + "Minnie's Wonderful Christmastime Fireworks", Material.SNOW),
    MERRIEST_CELEBRATION(ChatColor.RED + "Mickey's Most Merriest Celebration", Material.MELON),
    OUACTP(ChatColor.AQUA + "Once Upon A Christmastime Parade", Material.SNOW_BALL),
    JBJB(ChatColor.GREEN + "Jingle Bell, Jingle BAM!", Material.RECORD_5),
    PTN(ChatColor.DARK_AQUA + "Paint The Night", Material.STONE_SWORD);

    /**
     * <p>The <code>name</code> field represents the string identifier associated
     * with a specific show type. This value is typically used to map and retrieve
     * data related to a show type in the system.</p>
     *
     * <p>Key attributes of the <code>name</code> field:</p>
     * <ul>
     *   <li><b>Accessibility:</b> Private access to ensure encapsulation within the class.</li>
     *   <li><b>Type:</b> String, representing textual data for the name of the show type.</li>
     * </ul>
     */
    private String name;

    /**
     * Represents the material or fabric type associated with an instance of a show.
     * This variable specifies a category or type that can be used to differentiate
     * between various materials related to the show types within the enumeration.
     *
     * <p>Usage includes specifying the fundamental material attribute for a show
     * during initialization or interaction within the system.
     *
     * <p><strong>Note:</strong> This field is private and immutable after the
     * instance has been created, ensuring the material type remains consistent
     * for a given show type.
     */
    private Material type;

    /**
     * <p>The variable <code>data</code> represents an internal field used for storing
     * a specific byte of information pertaining to the <code>ShowType</code>
     * enumeration. It is intended for internal use within the context of the class
     * functionality.</p>
     *
     * <ul>
     *   <li>This variable is private and cannot be accessed directly from outside the class.</li>
     *   <li>Its presence supports specific functionality encapsulated in <code>ShowType</code>.</li>
     *   <li>The information stored is generally auxiliary and directly relevant
     *       to the associated enum instances.</li>
     * </ul>
     */
    private byte data;

    /**
     * Constructor for the <code>ShowType</code> class, which initializes a show type with a name and material type.
     *
     * <p>This constructor allows the creation of a new <code>ShowType</code> instance with a specified name and
     * associated type. The <code>data</code> field is initialized to <code>0</code>.</p>
     *
     * @param name the name of the show type. It represents the display name or identifier for this type of show.
     * @param type the material type associated with the show. It typically represents specific visual or thematic
     *             properties tied to the type.
     */
    ShowType(String name, Material type) {
        this.name = name;
        this.type = type;
        this.data = 0;
    }

    /**
     * Converts a string to its corresponding {@code ShowType} enumeration value.
     * <p>
     * This method maps a string representation to an appropriate {@code ShowType} enum,
     * based on a case-insensitive comparison with the value returned by {@link #getDBName()}
     * for each enumeration constant.
     * </p>
     *
     * @param name the string representation of the desired {@code ShowType}.
     *             This parameter is case-insensitive and must match the database-style
     *             name of a {@code ShowType}.
     * @return the corresponding {@code ShowType} constant if a match is found;
     *         otherwise, it returns {@code NO_SHOW}.
     */
    public static ShowType fromString(String name) {
        for (ShowType type : values()) {
            if (name.equalsIgnoreCase(type.getDBName())) {
                return type;
            }
        }
        return NO_SHOW;
    }

    /**
     * Retrieves the database-compatible name of the current enum constant.
     * <p>
     * This method transforms the enum constant's name to a lowercase string
     * and replaces all underscores ("_") with an empty string.
     *
     * <ul>
     *  <li>Enum constant names are converted to lowercase letters.</li>
     *  <li>All underscores ("_") in the original name are removed.</li>
     * </ul>
     *
     * @return A <code>String</code> representing the database-compatible name of the enum constant.
     */
    public String getDBName() {
        return name().toLowerCase().replaceAll("_", "");
    }
}
