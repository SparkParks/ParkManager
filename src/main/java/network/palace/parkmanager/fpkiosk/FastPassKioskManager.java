package network.palace.parkmanager.fpkiosk;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.ImmutableMap;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.MonthOfYear;
import network.palace.parkmanager.handlers.RewardData;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.ClickType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * The {@code FastPassKioskManager} class manages the operations and functionality for the FastPass kiosks
 * within a specific application or system. It includes methods to handle user interactions, manage kiosk menus,
 * and process claims related to FastPass tokens and rewards.
 *
 * <p>This class provides functionality such as:
 * <ul>
 *     <li>Updating and interacting with the kiosk menu.</li>
 *     <li>Handling player actions when they join the kiosk system.</li>
 *     <li>Processing token and FastPass claims for players based on their rank and reward data.</li>
 *     <li>Managing kiosk inventory and generating dynamic information such as cooldowns and claim statuses.</li>
 * </ul>
 *
 * <p>The following are primary configurable data points:
 * <ul>
 *     <li><b>menu</b>: Represents the kiosk menu interface.</li>
 *     <li><b>timeZone</b>: Configuration for the timezone used in time-based operations.</li>
 *     <li><b>websiteMessage</b>: A static or dynamic message to display on the kiosk regarding the website.</li>
 *     <li><b>voteMessage</b>: A static or dynamic message pertaining to voting rewards or information.</li>
 * </ul>
 *
 * <p>This class ensures seamless interaction between players and the kiosk system,
 * while encapsulating related backend processes such as cooldown tracking and updating player inventories.
 */
public class FastPassKioskManager {

    /**
     * <p>
     * Represents a list of unique identifiers used to manage the menu within the
     * FastPass kiosk system. This list is primarily utilized to track and manage
     * the unique menu items associated with the kiosks.
     * </p>
     *
     * <p>
     * The {@code menu} field is a collection of {@code UUID} objects. Each
     * {@code UUID} in this list correlates to a specific menu entry or entity
     * associated with the kiosk. The {@code menu} plays a central role in the
     * management of the kiosk menu's functionality and interactions.
     * </p>
     *
     * <p>
     * Key characteristics:
     * </p>
     * <ul>
     *   <li>Stores unique identifiers pertaining to the kiosk's menu.</li>
     *   <li>Facilitates menu interaction logic and operations.</li>
     *   <li>Designed for private access and internal management within the system.</li>
     * </ul>
     */
    private List<UUID> menu = new ArrayList<>();

    /**
     * The {@code timeZone} variable represents the time zone for the
     * FastPass Kiosk Manager, initialized to "America/New_York".
     *
     * <p>This time zone is primarily used to ensure consistent time calculations
     * and operations related to the kiosk system are localized to the specified
     * region's time setting.
     *
     * <p><b>Properties of this time zone:</b>
     * <ul>
     * <li>It adheres to the rules and offsets defined by the "America/New_York" region.</li>
     * <li>Handles daylight saving time variations automatically when applicable.</li>
     * <li>Useful for operations that involve time-sensitive interactions within the application.</li>
     * </ul>
     *
     * <p>As a {@code final} field, this variable is immutable after initialization,
     * ensuring the integrity of the time zone throughout the application's lifecycle.
     */
    private final ZoneId timeZone = ZoneId.of("America/New_York");

    /**
     * <p>
     * Represents a formatted message prompting users to visit a specified website.
     * The message is styled and interactive, providing a clickable link along with
     * a tooltip to guide users.
     * </p>
     *
     * <p>
     * The formatted message contains the following components:
     * </p>
     * <ul>
     *   <li>A clickable message prompting users to visit the website.</li>
     *   <li>The message is styled with yellow bold text and includes a hyperlink.</li>
     *   <li>A tooltip with additional instructions and information when hovered over.</li>
     *   <li>Links to <code>https://forums.palace.network</code> as the destination URL.</li>
     * </ul>
     *
     * <p>
     * This message is intended for use in an interactive interface, allowing users
     * to quickly navigate to external resources related to the application or service.
     * </p>
     */
    private final FormattedMessage websiteMessage = new FormattedMessage("\n")
            .then("Click here to visit our website!").color(ChatColor.YELLOW).style(ChatColor.BOLD)
            .link("https://forums.palace.network").tooltip(ChatColor.GREEN + "Click to visit " +
                    ChatColor.AQUA + "https://forums.palace.network")
            .then("\n");

    /**
     * Represents the pre-formatted message displayed to players, encouraging them to vote for the server
     * on Minecraft Server Lists. The message includes clickable text with styling and a tooltip for enhanced interaction.
     * <p>
     * The message format:
     * <ul>
     *   <li>Starts with a newline character for spacing.</li>
     *   <li>Highlights the clickable text: <strong>"Click here to vote for us on Minecraft Server Lists!"</strong>,
     *       styled in bold yellow.</li>
     *   <li>Contains a hyperlink redirecting players to the voting page: <a href="https://vote.palace.network/?1">
     *       https://vote.palace.network/?1</a>.</li>
     *   <li>Displays a tooltip with the text: <em>"Click to vote for us!"</em>, styled in green, upon hovering.</li>
     *   <li>Ends with another newline character.</li>
     * </ul>
     * <p>
     * This variable is intended to streamline the process of providing players with an intuitive and visually
     * appealing prompt for server voting.
     */
    private final FormattedMessage voteMessage = new FormattedMessage("\n")
            .then("Click here to vote for us on Minecraft Server Lists!").color(ChatColor.YELLOW).style(ChatColor.BOLD)
            .link("https://vote.palace.network/?1").tooltip(ChatColor.GREEN + "Click to vote for us!").then("\n");

    /**
     * Initializes the FastPassKioskManager and manages player interactions with in-game kiosks.
     * <p>
     * This constructor sets up packet listeners for player interactions with kiosks and schedules
     * periodic updates for the kiosk menus of players. It binds behavior to kiosk entities, ensuring
     * appropriate actions are executed when players interact with these entities.
     *
     * <p><b>Initialization:</b></p>
     * <lu>
     *   <li>Registers a packet listener for interactions with kiosks represented by armor stands.</li>
     *   <li>Kiosk interactions are identified and handled by checking entity IDs and kiosk-specific
     *       criteria.</li>
     *   <li>If a valid kiosk entity is interacted with, a menu is opened for the player.</li>
     * </lu>
     *
     * <p><b>Periodic Updates:</b></p>
     * <lu>
     *   <li>Schedules a recurring task that updates kiosk menus for players actively engaged with kiosks.</li>
     *   <li>Removes inactive or null players from the menu management list.</li>
     * </lu>
     *
     * <p><b>Error Handling:</b></p>
     * <lu>
     *   <li>Ignores exceptions during interaction processing to avoid disrupting the game environment.</li>
     * </lu>
     *
     * <p>This manager is critical for enabling "Fast Pass" system interactions, providing a seamless
     * experience for players engaging with kiosks in the game world.</p>
     */
    public FastPassKioskManager() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ParkManager.getInstance(), PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
                if (player == null || player.isInVehicle()) return;
                int id = event.getPacket().getIntegers().read(0);
                try {
                    for (ArmorStand stand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
                        if (isKiosk(stand) && stand.getEntityId() == id) {
                            event.setCancelled(true);
                            Core.runTask(ParkManager.getInstance(), () -> openMenu(player));
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        });
        Core.runTaskTimer(ParkManager.getInstance(), () -> {
            for (UUID uuid : new ArrayList<>(menu)) {
                CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                if (tp == null) {
                    menu.remove(uuid);
                    return;
                }
                updateKioskMenu(tp);
            }
        }, 0L, 20L);
    }

    /**
     * Opens the FastPass+ Kiosk menu for the specified player. This method sends a
     * message to the player to indicate the login process and schedules a task to
     * perform necessary setup for the menu after a delay.
     *
     * <p>This includes adding the player to the active menu list and updating their
     * registry to indicate that a kiosk inventory setup is required.</p>
     *
     * @param player the {@link CPlayer} who is accessing the FastPass+ Kiosk. This
     *               represents the player for whom the kiosk menu should be opened.
     */
    private void openMenu(CPlayer player) {
        player.sendMessage(ChatColor.BLUE + "Logging in to FastPass+ Kiosk...");
        Core.runTaskLater(ParkManager.getInstance(), () -> {
            menu.add(player.getUniqueId());
            player.getRegistry().addEntry("needsKioskInventory", true);
        }, 4L);
    }

    /**
     * Updates the player's kiosk menu depending on their registry status. If a kiosk menu
     * already exists in the player's registry, it utilizes it. Otherwise, it creates a new
     * kiosk menu and assigns it to the player, adding functionalities like logout handling
     * upon menu closure.
     *
     * <p>This method is responsible for ensuring the player has the correct kiosk menu
     * set up and visible, and may trigger the opening of the menu or cleanup when necessary.</p>
     *
     * @param player the player for whom the kiosk menu will be updated. The method interacts
     *               with the player's registry to determine if a menu needs to be created,
     *               removed, or updated.
     */
    private void updateKioskMenu(CPlayer player) {
        Menu menu;
        if (player.getRegistry().hasEntry("kioskInventory")) {
            menu = (Menu) player.getRegistry().getEntry("kioskInventory");
        } else if (player.getRegistry().hasEntry("needsKioskInventory")) {
            player.getRegistry().removeEntry("needsKioskInventory");
            menu = new Menu(27, ChatColor.GREEN + "FastPass+ Kiosk", player, new ArrayList<>());
            player.getRegistry().addEntry("kioskInventory", menu);
            menu.setOnClose(() -> {
                FastPassKioskManager.this.menu.remove(player.getUniqueId());
                player.getRegistry().removeEntry("kioskInventory");
                player.sendMessage(ChatColor.BLUE + "Logged out of FastPass+ Kiosk");
            });
            menu.open();
        } else {
            return;
        }
        if (menu != null) setInventory(player, menu);
    }

    /**
     * Handles the joining process for a player interacting with the FastPass system.
     * <p>
     * This method initializes and updates the player's registry with relevant
     * data from the provided {@code fastpassDocument}, ensuring required keys are
     * present. It also asynchronously fetches monthly reward data associated
     * with the player and updates their registry.
     *
     * <p><b>Initialization:</b></p>
     * <ul>
     *   <li>Ensures that the {@code fastpassDocument} contains keys for "lastClaimed"
     *       and "count", initializing them with default values if they are missing.</li>
     *   <li>Updates the player's registry with the values for "lastClaimed"
     *       and "count" from the {@code fastpassDocument}.</li>
     * </ul>
     *
     * <p><b>Reward Data Fetching:</b></p>
     * <ul>
     *   <li>Asynchronously retrieves monthly reward data for the player using their unique ID.</li>
     *   <li>If valid reward data is found, adds this information (such as settler, dweller, noble,
     *       majestic, and honorable rewards) to the player's registry.</li>
     *   <li>If no reward data is available or the required fields are missing, the process exits early.</li>
     * </ul>
     *
     * @param player the {@link CPlayer} representing the player who joined and whose registry
     *               should be updated with their FastPass-related data.
     * @param fastpassDocument the {@link Document} containing the player's FastPass data,
     *                         which will be checked for required keys ("lastClaimed"
     *                         and "count") and used to update the player's registry.
     */
    public void handleJoin(CPlayer player, Document fastpassDocument) {
        if (!fastpassDocument.containsKey("lastClaimed")) {
            fastpassDocument.put("lastClaimed", 0L);
        }
        if (!fastpassDocument.containsKey("count")) {
            fastpassDocument.put("count", 0);
        }
        player.getRegistry().addEntry("lastFastPassClaim", fastpassDocument.getLong("lastClaimed"));
        player.getRegistry().addEntry("fastPassCount", fastpassDocument.getInteger("count"));
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            Document rewardDocument = Core.getMongoHandler().getMonthlyRewards(player.getUniqueId());
            if (rewardDocument == null || !rewardDocument.containsKey("settler")) return;
            long settler = rewardDocument.getLong("settler");
            long dweller = (Long) rewardDocument.getOrDefault("dweller", 0L);
            long noble = (Long) rewardDocument.getOrDefault("noble", 0L);
            long majestic = (Long) rewardDocument.getOrDefault("majestic", 0L);
            long honorable = (Long) rewardDocument.getOrDefault("honorable", 0L);
            player.getRegistry().addEntry("kioskRewardData", new RewardData(settler, dweller, noble, majestic, honorable));
        });
    }

    /**
     * Sets up the player's inventory for the FastPass+ Kiosk menu. This method determines
     * the player's eligibility for specific rewards based on their registry data and
     * configures the menu with appropriate options and actions.
     *
     * <p><b>Functionality:</b></p>
     * <ul>
     *   <li>Verifies if the player has valid reward data in their registry.</li>
     *   <li>Checks the monthly claim status for various rank-based rewards:
     *       FastPass, Settler, Dweller, Noble, Majestic, and Honorable.</li>
     *   <li>Configures the menu with buttons representing these rewards,
     *       including their claim status and descriptive lore.</li>
     *   <li>Assigns actions to each menu button based on the player's interaction.</li>
     * </ul>
     *
     * <p>If the player does not have the required reward data registry, their inventory is
     * closed, and their menu session is terminated.</p>
     *
     * @param player the {@link CPlayer} whose inventory and menu should be updated. Represents
     *               the player interacting with the FastPass+ Kiosk.
     * @param menu   the {@link Menu} to be configured, representing the FastPass+ Kiosk interface
     *               displayed to the player.
     */
    private void setInventory(CPlayer player, Menu menu) {
        if (!player.getRegistry().hasEntry("kioskRewardData")) {
            this.menu.remove(player.getUniqueId());
            player.closeInventory();
            return;
        }
        RewardData rewardData = (RewardData) player.getRegistry().getEntry("kioskRewardData");
        boolean fpClaimed = monthlyClaim((long) player.getRegistry().getEntry("lastFastPassClaim"));
        boolean settler = monthlyClaim(rewardData.getSettler());
        boolean dweller = monthlyClaim(rewardData.getDweller());
        boolean noble = monthlyClaim(rewardData.getNoble());
        boolean majestic = monthlyClaim(rewardData.getMajestic());
        boolean honorable = monthlyClaim(rewardData.getHonorable());

        menu.setButton(new MenuButton(4, ItemUtil.create(fpClaimed ? Material.IRON_INGOT : Material.BRICK,
                ChatColor.GREEN + "Monthly FastPass", getClaimLore(fpClaimed,
                        Arrays.asList(ChatColor.GRAY + "Use this to skip the line", ChatColor.GRAY + "for any attraction!"))),
                ImmutableMap.of(ClickType.LEFT, this::claimFastPass)));
        menu.setButton(new MenuButton(12, ItemUtil.create(Material.NETHER_STAR, ChatColor.GREEN + "Join the discussion!",
                Arrays.asList(ChatColor.GRAY + "Check out our Forums at ", ChatColor.AQUA + "forums.palace.network",
                        ChatColor.GRAY + "for news, posts and more!")), ImmutableMap.of(ClickType.LEFT, websiteMessage::send)));
        menu.setButton(new MenuButton(14, ItemUtil.create(Material.NETHER_STAR, ChatColor.GREEN + "Vote for Palace!", Arrays.asList(ChatColor.GRAY +
                        "Vote for us on Minecraft Server", ChatColor.GRAY + "Lists! Voting rewards will be",
                ChatColor.GRAY + "returning soon!")), ImmutableMap.of(ClickType.LEFT, voteMessage::send)));
        menu.setButton(new MenuButton(18, ItemUtil.create(settler ? Material.IRON_INGOT : Material.DIAMOND,
                ChatColor.GRAY + "Monthly Tokens - Guest", getClaimLore(settler,
                        Arrays.asList(ChatColor.GRAY + "Claim your monthly 20 Tokens!",
                                ChatColor.GRAY + "Everyone can claim this prize!"))),
                ImmutableMap.of(ClickType.LEFT, p -> claimTokens(p, Rank.GUEST, rewardData))));
        menu.setButton(new MenuButton(20, ItemUtil.create(dweller ? Material.IRON_INGOT : Material.DIAMOND,
                ChatColor.DARK_AQUA + "Monthly Tokens - Passholder", getClaimLore(dweller,
                        Arrays.asList(ChatColor.GRAY + "Claim your monthly 20 Tokens!",
                                ChatColor.GRAY + "You must be " + Rank.PASSHOLDER.getFormattedName() + ChatColor.GRAY + " or above"))),
                ImmutableMap.of(ClickType.LEFT, p -> claimTokens(p, Rank.PASSHOLDER, rewardData))));
        menu.setButton(new MenuButton(22, ItemUtil.create(noble ? Material.IRON_INGOT : Material.DIAMOND,
                ChatColor.YELLOW + "Monthly Tokens - Premier Passport", getClaimLore(noble,
                        Arrays.asList(ChatColor.GRAY + "Claim your monthly 20 Tokens!",
                                ChatColor.GRAY + "You must be " + Rank.PASSPORT.getFormattedName() + ChatColor.GRAY + " or above"))),
                ImmutableMap.of(ClickType.LEFT, p -> claimTokens(p, Rank.PASSPORT, rewardData))));
        menu.setButton(new MenuButton(24, ItemUtil.create(majestic ? Material.IRON_INGOT : Material.DIAMOND,
                ChatColor.GOLD + "Monthly Tokens - DVC", getClaimLore(majestic,
                        Arrays.asList(ChatColor.GRAY + "Claim your monthly 20 Tokens!",
                                ChatColor.GRAY + "You must be " + Rank.DVC.getFormattedName() + ChatColor.GRAY + " or above"))),
                ImmutableMap.of(ClickType.LEFT, p -> claimTokens(p, Rank.DVC, rewardData))));
        menu.setButton(new MenuButton(26, ItemUtil.create(honorable ? Material.IRON_INGOT : Material.DIAMOND,
                ChatColor.DARK_RED + "Monthly Tokens - Club 33", getClaimLore(honorable,
                        Arrays.asList(ChatColor.GRAY + "Claim your monthly 20 Tokens!",
                                ChatColor.GRAY + "You must be " + Rank.CLUB.getFormattedName() + ChatColor.GRAY + " or above"))),
                ImmutableMap.of(ClickType.LEFT, p -> claimTokens(p, Rank.CLUB, rewardData))));
    }

    /**
     * Claims monthly tokens for the player based on their rank and reward eligibility.
     * <p>
     * This method checks the player's current rank against the required rank for token
     * eligibility and validates that a token claim has not already been made for the
     * current month. If the claim is valid, the player's token balance is updated and
     * the claim is recorded.
     * <p>
     * The following actions are performed during the token claiming process:
     * <ul>
     *   <li>Validation of player's rank against the required rank.</li>
     *   <li>Verification to ensure the reward for the month has not already been claimed.</li>
     *   <li>Updating the timestamp for the player's rank-specific claim in the reward data.</li>
     *   <li>Granting tokens to the player and persisting the reward data asynchronously.</li>
     *   <li>Notifying the player of the outcome through chat messages.</li>
     * </ul>
     *
     * @param player     the {@link CPlayer} attempting to claim the tokens. The method
     *                   checks the rank and reward state of this player.
     * @param rank       the {@link Rank} required for the player to claim the tokens. Defines
     *                   eligibility for the current reward.
     * @param rewardData the {@link RewardData} object containing the timestamps for the player's
     *                   token claims based on rank. This data is updated to reflect the new claim state.
     */
    private void claimTokens(CPlayer player, Rank rank, RewardData rewardData) {
        if (player.getRank().getRankId() < rank.getRankId()) {
            player.sendMessage(ChatColor.AQUA + "You must be " + rank.getFormattedName() + ChatColor.AQUA + " or above to claim these tokens!");
            return;
        }
        long lastClaim;
        switch (rank) {
            case GUEST:
                lastClaim = rewardData.getSettler();
                break;
            case PASSHOLDER:
                lastClaim = rewardData.getDweller();
                break;
            case PASSPORT:
                lastClaim = rewardData.getNoble();
                break;
            case DVC:
                lastClaim = rewardData.getMajestic();
                break;
            case CLUB:
                lastClaim = rewardData.getHonorable();
                break;
            default:
                player.sendMessage(ChatColor.RED + "There was an error claiming that reward!");
                return;
        }
        if (monthlyClaim(lastClaim)) {
            player.sendMessage(ChatColor.RED + "You've already claimed that reward for this month!");
            return;
        }
        long nextClaim = System.currentTimeMillis();
        switch (rank) {
            case GUEST:
                rewardData.setSettler(nextClaim);
                break;
            case PASSHOLDER:
                rewardData.setDweller(nextClaim);
                break;
            case PASSPORT:
                rewardData.setNoble(nextClaim);
                break;
            case DVC:
                rewardData.setMajestic(nextClaim);
                break;
            case CLUB:
                rewardData.setHonorable(nextClaim);
                break;
        }
        player.addTokens(20, "monthly " + rank.name().toLowerCase());
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().updateMonthlyRewardData(player.getUniqueId(),
                rewardData.getSettler(), rewardData.getDweller(), rewardData.getNoble(), rewardData.getMajestic(), rewardData.getHonorable()));
        player.sendMessage(ChatColor.YELLOW + "You've claimed your 20 monthly " + rank.getFormattedName() +
                ChatColor.YELLOW + " tokens!");
    }

    /**
     * Processes the claim for the monthly FastPass reward for the specified player.
     * This method ensures that the player can only claim the reward once per calendar month.
     * If the player has already claimed the reward this month, an appropriate message is sent
     * and the claim process is aborted. Otherwise, the reward is successfully claimed,
     * relevant registry entries are updated, and the changes are saved asynchronously in the database.
     *
     * <p><b>Functionality:</b></p>
     * <ul>
     *   <li>Checks the player's last FastPass claim date from their registry to verify eligibility.</li>
     *   <li>Notifies the player if the reward for the current month has already been claimed.</li>
     *   <li>Updates the player's registry with the new claim date and increments their FastPass claim count.</li>
     *   <li>Saves the updated claim data asynchronously to the database.</li>
     *   <li>Sends a confirmation message to the player upon successful reward claim.</li>
     * </ul>
     *
     * @param player the {@link CPlayer} representing the player attempting to claim the FastPass reward.
     *               The player's FastPass claim data in their registry will be accessed and updated accordingly.
     */
    private void claimFastPass(CPlayer player) {
        long lastClaim = (long) player.getRegistry().getEntry("lastFastPassClaim");
        if (monthlyClaim(lastClaim)) {
            player.sendMessage(ChatColor.RED + "You've already claimed that reward for this month!");
            return;
        }
        long nextClaim = System.currentTimeMillis();
        player.getRegistry().addEntry("lastFastPassClaim", nextClaim);
        player.getRegistry().addEntry("fastPassCount", (int) player.getRegistry().getEntry("fastPassCount") + 1);
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            Core.getMongoHandler().updateFastPassData(player.getUniqueId(), nextClaim);
            Core.getMongoHandler().addFastPass(player.getUniqueId(), 1);
        });
        player.sendMessage(ChatColor.YELLOW + "You've claimed your monthly FastPass!");
    }

    /**
     * Generates and returns a modified list of lore strings based on whether the
     * item has been claimed or not. This method appends specific messages to the
     * provided lore list depending on the claim status.
     *
     * <p>If the item is claimed, it adds the message indicating when the player
     * can next claim the item, formatted with time until the next opportunity.
     * If the item is not claimed, it adds a message prompting the user to claim it.</p>
     *
     * @param claimed a boolean indicating whether the item has been claimed
     *                (<code>true</code> if claimed, <code>false</code> otherwise).
     * @param lore the initial {@link List} of {@link String} values representing
     *             the current lore (descriptions or metadata) of the item.
     *
     * @return a new {@link List} of {@link String} objects representing the updated
     *         lore with messages added based on the claim status.
     */
    private List<String> getClaimLore(boolean claimed, List<String> lore) {
        lore = new ArrayList<>(lore);
        if (claimed) {
            lore.add(ChatColor.YELLOW + "Claim in " + ChatColor.GRAY + timeToNextMonth());
        } else {
            lore.add(ChatColor.YELLOW + "Right-Click to Claim!");
        }
        return lore;
    }

    /**
     * Checks if the provided timestamp for the last FastPass claim falls within the current month.
     *
     * <p>This method determines whether the claim associated with the given timestamp
     * occurred within the same year and month as the current date and time. It ensures
     * that users can only claim rewards once per calendar month.
     *
     * @param lastFastPassClaim the epoch timestamp in milliseconds representing the last
     *                          time a FastPass claim was made.
     * @return {@code true} if the last claim occurred in the current month, otherwise {@code false}.
     */
    private boolean monthlyClaim(long lastFastPassClaim) {
        ZonedDateTime lastFPClaim = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastFastPassClaim), timeZone);
        ZonedDateTime now = ZonedDateTime.ofInstant(Instant.now(), timeZone);
        return YearMonth.from(lastFPClaim).equals(YearMonth.from(now));
    }

    /**
     * Calculates the time remaining until the next month, expressed in days, hours,
     * minutes, and seconds. The computation accounts for leap years during February.
     *
     * <p>The method uses the current date and time to determine the number of days
     * in the current month, calculates the remaining days till the end of the month,
     * and additionally factors in the remaining hours, minutes, and seconds in the
     * current day. The returned value is formatted as a color-coded string.
     *
     * <p><b>Details:</b></p>
     * <ul>
     *   <li>Checks if the current month is February in a leap year and adjusts
     *       the total days of the month accordingly.</li>
     *   <li>Calculates the remaining time based on the current day, hour, minute,
     *       and second.</li>
     *   <li>Returns the formatted time string using a specific color coding.</li>
     * </ul>
     *
     * @return A formatted string indicating the time remaining until the next
     * month, expressed in the format: <code>"<gray_color>d<hours>h<minutes>m<seconds>s"</code>.
     */
    private String timeToNextMonth() {
        Calendar cur = Calendar.getInstance();
        cur.setTimeInMillis(new Date().getTime());
        MonthOfYear month = MonthOfYear.getFromNumber(cur.get(Calendar.MONTH));
        int days = month.getDays();
        if (month.equals(MonthOfYear.FEBRUARY) && cur.get(Calendar.YEAR) % 4 == 0) days += 1;

        int d = days - cur.get(Calendar.DAY_OF_MONTH);
        int h = 24 - (cur.get(Calendar.HOUR_OF_DAY) + 1);
        int m = 60 - (cur.get(Calendar.MINUTE) + 1);
        int s = 60 - (cur.get(Calendar.SECOND) + 1);
        return ChatColor.GRAY + "" + d + "d" + h + "h" + m + "m" + s + "s";
    }

    /**
     * Spawns a FastPass+ Kiosk at the player's current location.
     * <p>
     * This method ensures that the kiosk is placed at a valid location adhering to specific conditions:
     * <ul>
     *   <li>The player must be standing on solid ground (not above air).</li>
     *   <li>The kiosk is aligned to face the nearest cardinal or intercardinal direction (N/E/S/W or NE/SE/NW/SW).</li>
     *   <li>The kiosk's position is snapped to the nearest half-block for x and z coordinates.</li>
     * </ul>
     * An armor stand is used to represent the kiosk, with predefined properties such as invisibility, no gravity,
     * and additional tags for identification. The kiosk is equipped with a diamond sword as its helmet for display.
     * <p>
     * If the player is not standing on solid ground, an error message is sent to the player, and the kiosk is not spawned.
     *
     * @param player the {@link CPlayer} at whose location the kiosk will be spawned. Represents the player interacting
     *               with the system and triggering the kiosk creation process.
     */
    public void spawn(CPlayer player) {
        Location loc = player.getLocation();
        if (loc.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "You must be standing on the ground when creating a FastPass+ Kiosk!");
            return;
        }

        //Snap the kiosk to face the nearest cardinal/intercardinal direction (N/E/S/W or NE/SE/NW/SW)
        float adjustedYaw = 45 * (Math.round(loc.getYaw() / 45));

        //Similarly, snap the kiosk to the nearest half-block coordinate for x and z (center of the block or on the line)
        double x = 0.5 * (Math.round(loc.getX() / 0.5));
        double y = loc.getBlockY();
        double z = 0.5 * (Math.round(loc.getZ() / 0.5));

        Location realLoc = new Location(loc.getWorld(), x, y, z, adjustedYaw, 0);

        ArmorStand stand = lock(realLoc.getWorld().spawn(realLoc, ArmorStand.class));
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setArms(false);
        stand.setBasePlate(false);
        stand.setHelmet(ItemUtil.create(Material.DIAMOND_SWORD, 1, 6));
        stand.addScoreboardTag("kiosk");

        player.sendMessage(ChatColor.GREEN + "Spawned in a new FastPass+ Kiosk!");
    }

    /**
     * Determines whether the given entity is considered a kiosk.
     * <p>
     * A kiosk is identified as an entity of type {@code ARMOR_STAND}
     * that contains the scoreboard tag {@code "kiosk"}.
     *
     * @param e The entity to be checked.
     *           <ul>
     *               <li>Must be non-null.</li>
     *               <li>Expected to have a valid type and scoreboard tags.</li>
     *           </ul>
     * @return {@code true} if the entity is of type {@code EntityType.ARMOR_STAND}
     *         and contains the scoreboard tag {@code "kiosk"}, {@code false} otherwise.
     */
    public boolean isKiosk(Entity e) {
        return e.getType().equals(EntityType.ARMOR_STAND) && e.getScoreboardTags().contains("kiosk");
    }

    /**
     * Locks the provided ArmorStand to prevent certain interactions or modifications.
     * <p>
     * This method utilizes reflection to access and modify the internal properties of the ArmorStand
     * entity based on the current Minecraft server version.
     * It assigns a lock value to restrict specific behaviors. The functionality may vary across
     * different Minecraft server versions.
     *
     * @param stand The {@link ArmorStand} object that should be locked.
     *              <ul>
     *                  <li>Must not be null.</li>
     *                  <li>Should be a valid, existing ArmorStand instance in the game world.</li>
     *              </ul>
     *
     * @return The {@link ArmorStand} object provided as input, now modified to be locked.
     *         <ul>
     *             <li>If an exception occurs during reflection, it may not apply the lock as intended.</li>
     *         </ul>
     */
    public static ArmorStand lock(ArmorStand stand) {
        try {
            String lockField;
            switch (Core.getMinecraftVersion()) {
                case "v1_13_R1":
                case "v1_13_R2":
                    lockField = "bH";
                    break;
                case "v1_12_R1":
                    lockField = "bB";
                    break;
                default:
                    lockField = "bA";
                    break;
            }
            Field f = Class.forName("net.minecraft.server." + Core.getMinecraftVersion() + ".EntityArmorStand")
                    .getDeclaredField(lockField);
            if (f != null) {
                f.setAccessible(true);
                Object craftStand = Class.forName("org.bukkit.craftbukkit." + Core.getMinecraftVersion() +
                        ".entity.CraftArmorStand").cast(stand);
                Object handle = craftStand.getClass().getDeclaredMethod("getHandle").invoke(craftStand);
                f.set(handle, 2096896);
            }
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return stand;
    }
}
