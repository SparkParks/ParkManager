package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.sign.ServerSign;
import network.palace.parkmanager.leaderboard.LeaderboardManager;
import network.palace.parkmanager.leaderboard.LeaderboardSign;
import network.palace.parkmanager.queues.Queue;
import network.palace.parkmanager.queues.QueueSign;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The {@code SignChange} class is responsible for registering and handling
 * various types of signs with specific functionalities relevant to a themed
 * park's operational mechanics. These functionalities encompass disposal,
 * leaderboards, server switching, warps, queue management, fast-pass systems,
 * and wait times.
 *
 * <p>This class implements the {@link Listener} interface to handle events
 * related to signs in the park, providing seamless interactivity for players
 * through various commands, actions, and visual updates on the signs.
 *
 * <p>Key Responsibilities:
 * <ul>
 *   <li>Registers custom signs with predefined types.</li>
 *   <li>Defines behaviors for each type of sign when they are changed,
 *       interacted with, or broken.</li>
 *   <li>Handles player interactions with signs, including actions like
 *       joining a queue, viewing wait times, managing leaderboards, using
 *       warps, and switching servers.</li>
 * </ul>
 *
 * <p>Supported Sign Types:
 * <ul>
 *   <li><b>[Disposal]</b>: Allows players to open a disposal inventory.</li>
 *   <li><b>[Leaderboard]</b>: Displays and manages ride leaderboard data.</li>
 *   <li><b>[Server]</b>: Allows players to switch to another server.</li>
 *   <li><b>[Warp]</b>: Provides functionality to teleport to predefined warp
 *       locations.</li>
 *   <li><b>[Queue]</b>: Manages interaction with queues, including joining,
 *       leaving, and displaying wait times.</li>
 *   <li><b>[FastPass]</b>: Provides functionality for fast-pass queue
 *       interaction and management.</li>
 *   <li><b>[Wait Times]</b>: Displays the estimated wait time for a specific
 *       queue in a park.</li>
 * </ul>
 *
 * <p>Technical Details:
 * <ul>
 *   <li>Each sign type is registered through the {@link ServerSign#registerSign}
 *       method with an associated {@link ServerSign.SignHandler} implementation
 *       for defining specific behaviors.</li>
 *   <li>Handles events such as {@link SignChangeEvent}, {@link PlayerInteractEvent},
 *       and {@link BlockBreakEvent} to manage the lifecycle and interaction of
 *       signs.</li>
 *   <li>Utilizes various park management utilities like {@link ParkManager},
 *       {@link Queue}, {@link QueueSign}, and {@link LeaderboardManager}
 *       for executing park-specific operations.</li>
 * </ul>
 *
 * <p>Usage Notes:
 * <ul>
 *   <li>Events triggered on these signs require proper player permissions and
 *       context to perform actions.</li>
 *   <li>All relevant data is dynamically fetched and processed asynchronously
 *       where applicable, such as leaderboard data retrieval.</li>
 *   <li>Some actions, like breaking specific sign types, require the player
 *       to hold a specific tool (e.g., a golden axe).</li>
 * </ul>
 */
public class SignChange implements Listener {

    /**
     * The <code>SignChange</code> class is a utility for handling interactions with custom server signs.
     * This includes registering multiple types of signs with distinct handlers for sign creation, interaction,
     * and destruction. Each sign type performs specific actions that enhance player interactions within the park system.
     *
     * <p>This class is an extension of a server-side functionality, bundling customizable features, such as
     * opening inventories, displaying leaderboards, teleporting to warps, queue management, and related actions.
     *
     * <p>Key responsibilities of <code>SignChange</code> include:
     * <ul>
     *   <li>Registering various custom sign types using the <code>ServerSign.registerSign</code> method.</li>
     *   <li>Defining event-handler logic for each sign type:
     *   <ul>
     *     <li><b>onSignChange:</b> Handles initialization and visual modifications when a sign is created by a player.</li>
     *     <li><b>onInteract:</b> Defines the player interaction behavior when clicking on the sign, often triggering
     *         server logic or game features.</li>
     *     <li><b>onBreak:</b> Handles cleanup or restrictions when signs are destroyed, ensuring proper state management.</li>
     *   </ul>
     *   </li>
     *   <li>Seamlessly integrating with core park management systems to provide enhanced gameplay mechanics.</li>
     * </ul>
     *
     * <p>Sign types implemented in <code>SignChange</code>:
     * <ul>
     *   <li><b>[Disposal]:</b> Opens a disposable inventory for quick item cleanup.</li>
     *   <li><b>[Leaderboard]:</b> Displays and interacts with ride leaderboards, showcasing performance and scores.</li>
     *   <li><b>[Server]:</b> Allows players to click and join different servers within the network.</li>
     *   <li><b>[Warp]:</b> Provides teleportation functionality to predefined locations in the park system.</li>
     *   <li><b>[Queue]:</b> Handles park queue interactions, such as joining, leaving, and managing lines.</li>
     *   <li><b>[FastPass]:</b> Manages premium queue interactions for expedited access to park features.</li>
     *   <li><b>[Wait Times]:</b> Displays estimated wait times for queues within the park.</li>
     * </ul>
     *
     * <p>By using the <code>SignChange</code> class, the park system enhances player engagement and
     * interactivity, adding value through a range of custom server-side capabilities.
     */
    public SignChange() {
        ServerSign.registerSign("[Disposal]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                event.setLine(1, "");
                event.setLine(2, ChatColor.BLACK + "" + ChatColor.BOLD + "Trash");
                event.setLine(3, "");
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                player.openInventory(Bukkit.createInventory(player.getBukkitPlayer(), 36, ChatColor.BLUE + "Disposal"));
            }
        });
        ServerSign.registerSign("[Leaderboard]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                if (!ParkManager.getLeaderboardManager().registerLeaderboardSign(event.getLines(), event.getBlock())) {
                    player.sendMessage(ChatColor.RED + "There was a problem creating that leaderboard sign! This usually happens when a leaderboard sign already exists for this ride.");
                    return;
                }
                event.setLine(1, "");
                event.setLine(2, ChatColor.AQUA + "Updating...");
                event.setLine(3, "");
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                LeaderboardSign leaderboard = ParkManager.getLeaderboardManager().getSign(event.getClickedBlock().getLocation());
                if (leaderboard == null) return;
                String rideName = leaderboard.getRideName();
                player.sendMessage(ChatColor.AQUA + "Gathering leaderboard data for " + rideName + "...");
                Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                    List<String> messages = new ArrayList<>();
                    for (Map.Entry<UUID, Integer> entry : leaderboard.getCachedMap().entrySet()) {
                        messages.add(ChatColor.BLUE + LeaderboardManager.getFormattedName(entry.getKey(), entry.getValue()));
                    }
                    LeaderboardManager.sortLeaderboardMessages(messages);
                    player.sendMessage(ChatColor.BLUE + "Ride Counter Leaderboard for " + ChatColor.GOLD + rideName + ":");
                    messages.forEach(player::sendMessage);
                });
            }

            @Override
            public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
                ParkManager.getLeaderboardManager().deleteSign(s.getLocation());
            }
        });
        ServerSign.registerSign("[Server]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                String name = event.getLine(1);
                event.setLine(1, "Click to join");
                event.setLine(2, name);
                event.setLine(3, "");
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                player.sendToServer(s.getLine(2));
            }
        });
        ServerSign.registerSign("[Warp]", new ServerSign.SignHandler() {
            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Warp warp = ParkWarp.getWarpUtil().findWarp(ChatColor.stripColor(s.getLine(1)));
                if (warp == null) {
                    player.sendMessage(ChatColor.RED + "That warp does not exist, sorry!");
                    return;
                }
                player.getBukkitPlayer().chat("/warp " + warp.getName());
            }
        });
        ServerSign.registerSign("[Queue]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                Park currentPark = ParkManager.getParkUtil().getPark(event.getBlock().getLocation());
                if (currentPark == null) {
                    player.sendMessage(ChatColor.RED + "This sign must be made within a park!");
                    return;
                }
                String id = event.getLine(1);
                Queue queue = ParkManager.getQueueManager().getQueueById(id, currentPark.getId());
                if (queue == null) {
                    player.sendMessage(ChatColor.RED + "Couldn't find a queue with id " + id + "!");
                    return;
                }
                if (!event.getLine(2).isEmpty()) {
                    if (event.getLine(2).equalsIgnoreCase("wait")) {
                        event.setLine(0, ChatColor.BLUE + "[Wait Times]");
                        event.setLine(1, ChatColor.DARK_AQUA + "Click for the");
                        event.setLine(2, ChatColor.DARK_AQUA + "wait time for");
                        event.setLine(3, ChatColor.DARK_AQUA + queue.getName());
                    } else if (event.getLine(2).equalsIgnoreCase("fp")) {
                        queue.addSign(new QueueSign(event.getBlock().getLocation(), queue.getName(), true, queue.getQueueSize()));
                        event.setCancelled(true);
                        queue.updateSigns();
                    }
                } else {
                    queue.addSign(new QueueSign(event.getBlock().getLocation(), queue.getName(), false, queue.getQueueSize()));
                    event.setCancelled(true);
                    queue.updateSigns();
                }
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Queue queue = ParkManager.getQueueManager().getQueue(s);
                if (queue == null) return;
                if (queue.isInQueue(player)) {
                    queue.leaveQueue(player, false);
                } else if (queue.joinQueue(player)) {
                    ParkManager.getQueueManager().displaySignParticles(player, s);
                }
            }

            @Override
            public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
                Queue queue = ParkManager.getQueueManager().getQueue(s);
                if (queue == null) return;
                if (!player.getMainHand().getType().equals(Material.GOLD_AXE)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.GREEN + "In order to break a [Queue] sign, you must be holding a "
                            + ChatColor.GOLD + "Golden Axe!");
                    return;
                }
                queue.removeSign(s.getLocation());
                player.sendMessage(ChatColor.GREEN + "You removed a queue sign for " + queue.getName());
            }
        });
        ServerSign.registerSign("[FastPass]", new ServerSign.SignHandler() {
            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Queue queue = ParkManager.getQueueManager().getQueue(s);
                if (queue == null) return;
                if (queue.isInQueue(player)) {
                    queue.leaveQueue(player, false);
                } else if (queue.joinFastPassQueue(player)) {
                    ParkManager.getQueueManager().displaySignParticles(player, s);
                }
            }

            @Override
            public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
                Queue queue = ParkManager.getQueueManager().getQueue(s);
                if (queue == null) return;
                if (!player.getMainHand().getType().equals(Material.GOLD_AXE)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.GREEN + "In order to break a [FastPass] sign, you must be holding a "
                            + ChatColor.GOLD + "Golden Axe!");
                    return;
                }
                queue.removeSign(s.getLocation());
                player.sendMessage(ChatColor.GREEN + "You removed a queue sign for " + queue.getName());
            }
        });
        ServerSign.registerSign("[Wait Times]", new ServerSign.SignHandler() {
            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Park currentPark = ParkManager.getParkUtil().getPark(event.getClickedBlock().getLocation());
                if (currentPark == null) {
                    player.sendMessage(ChatColor.RED + "This sign must be used within a park!");
                    return;
                }
                Queue queue = ParkManager.getQueueManager().getQueueByName(s.getLine(3), currentPark.getId());
                if (queue == null) return;
                if (!queue.isOpen()) {
                    player.sendMessage(ChatColor.GREEN + "This queue is currently " + ChatColor.RED + "closed!");
                } else {
                    String wait = queue.getWaitFor(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "The estimated wait time for " + queue.getName() +
                            ChatColor.GREEN + " is " + ChatColor.AQUA + wait);
                }
            }

            @Override
            public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
                Park currentPark = ParkManager.getParkUtil().getPark(event.getBlock().getLocation());
                if (currentPark == null) return;
                Queue queue = ParkManager.getQueueManager().getQueueByName(s.getLine(3), currentPark.getId());
                if (queue == null) return;
                if (!player.getMainHand().getType().equals(Material.GOLD_AXE)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.GREEN + "In order to break a [Wait Times] sign, you must be holding a "
                            + ChatColor.GOLD + "Golden Axe!");
                    return;
                }
                player.sendMessage(ChatColor.GREEN + "You removed a wait time sign for " + queue.getName());
            }
        });
        ServerSign.registerSign("[Shop]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                Park currentPark = ParkManager.getParkUtil().getPark(event.getBlock().getLocation());
                if (currentPark == null) {
                    player.sendMessage(ChatColor.RED + "This sign must be used within a park!");
                    return;
                }
                String id = event.getLine(1);
                Shop shop = ParkManager.getShopManager().getShopById(id, currentPark.getId());
                if (shop == null) {
                    player.sendMessage(ChatColor.RED + "Couldn't find a shop with id " + id + "!");
                    return;
                }
                event.setLine(1, shop.getName());
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Park currentPark = ParkManager.getParkUtil().getPark(event.getClickedBlock().getLocation());
                if (currentPark == null) {
                    player.sendMessage(ChatColor.RED + "This sign must be used within a park!");
                    return;
                }
                Shop shop = ParkManager.getShopManager().getShopByName(ChatColor.stripColor(s.getLine(1)), currentPark.getId());
                if (shop == null) {
                    player.sendMessage(ChatColor.RED + "Could not find a shop named " + s.getLine(1) + "!");
                    return;
                }
                ParkManager.getShopManager().openShopInventory(player, shop);
            }
        });
        ServerSign.registerSign("[vqueue]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                event.setLine(0, ChatColor.AQUA + "[Virtual Queue]");
                String id = event.getLine(1);
                VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueueById(id);
                if (queue == null) {
                    player.sendMessage(ChatColor.RED + "Couldn't find a queue with id " + id + "!");
                    return;
                }
                event.setLine(1, ChatColor.BLUE + id);
                event.setLine(3, ChatColor.YELLOW + "" + queue.getMembers().size() + " Player" +
                        TextUtil.pluralize(queue.getMembers().size()));
                if (event.getLine(2).equalsIgnoreCase("advance")) {
                    event.setLine(2, ChatColor.YELLOW + "" + ChatColor.BOLD + "Advance");
                    queue.setAdvanceSign((Sign) event.getBlock().getState());
                } else if (event.getLine(2).equalsIgnoreCase("state")) {
                    event.setLine(2, (queue.isOpen() ? ChatColor.GREEN : ChatColor.RED) + "" + ChatColor.BOLD +
                            (queue.isOpen() ? "Open" : "Closed"));
                    queue.setStateSign((Sign) event.getBlock().getState());
                }
            }
        });
        ServerSign.registerSign("[Virtual Queue]", new ServerSign.SignHandler() {
            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueue(s);
                if (queue == null) return;
                if (s.getLine(2).startsWith(ChatColor.YELLOW.toString())) {
                    player.performCommand("vq advance " + queue.getId());
                } else {
                    player.performCommand("vq " + (queue.isOpen() ? "close " : "open ") + queue.getId());
                }
            }

            @Override
            public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
                VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueue(s);
                if (queue == null) return;
                event.setCancelled(true);
                player.sendMessage(ChatColor.AQUA + "You can only destroy this sign once the queue is removed!");
            }
        });
    }

    /**
     * Handles the event triggered when a sign's text is changed by a player.
     * This method processes the text on the sign, applying color codes and checking
     * for a specific header to determine further custom event handling.
     *
     * <p>Features include:</p>
     * <ul>
     *     <li>Applying color translations using alternate color codes.</li>
     *     <li>Validating the first line of the sign against predefined headers.</li>
     *     <li>Triggering additional handling through a related {@code ServerSign.SignEntry} if applicable.</li>
     * </ul>
     *
     * @param event The {@link SignChangeEvent} triggered when a player changes the text on a sign.
     *              Contains information about the player, the sign, and its contents.
     */
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        Block b = event.getBlock();

        for (int i = 0; i < 4; i++) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
        }
        String line1 = event.getLine(0);

        ServerSign.SignEntry signEntry = ServerSign.getByHeader(line1);

        if (signEntry != null) {
            event.setLine(0, ChatColor.BLUE + signEntry.getHeader());
            signEntry.getHandler().onSignChange(player, event);
        }
    }
}
