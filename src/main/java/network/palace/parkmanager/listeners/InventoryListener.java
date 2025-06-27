package network.palace.parkmanager.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.utils.InventoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The {@code InventoryListener} class implements event handling for inventory-related actions
 * in a Minecraft server. It ensures specific behaviors or restrictions for players interacting
 * with inventories and items, depending on their state or rank.
 * <p>
 * This class listens to multiple events, including inventory interactions, item swaps, slot
 * selection, and packet events for block actions. It enforces rules such as prohibiting
 * interactions with ender chests, restricting specific types of inventory access,
 * or managing custom gameplay functionality for certain item slots.
 * <p>
 * Key functionalities of the {@code InventoryListener}:
 * <ul>
 *     <li>Monitors specific block actions and cancels events for certain block types like ender chests.</li>
 *     <li>Handles inventory clicks, imposing restrictions based on player's inventory state
 *     (e.g., BUILD, RIDE, etc.), clicked inventory, reserved slots, and actions like swapping items.</li>
 *     <li>Cancels inventory opening for ender chests, redirecting the player to a custom menu system.</li>
 *     <li>Regulates offhand swaps, enforcing rules based on player's rank and build mode status.
 *     Specific logic is applied when interacting with map items.</li>
 *     <li>Performs custom actions when players switch slots, triggering specific behaviors like
 *     enabling or disabling a "watch" functionality on a designated slot.</li>
 * </ul>
 * <p>
 * This listener heavily relies on supporting utilities and managers provided by the server, such as:
 * <ul>
 *     <li>{@code ProtocolLibrary} for packet management.</li>
 *     <li>{@code ParkManager}, a central manager for various server utilities and states.</li>
 *     <li>{@code InventoryUtil}, which encapsulates logic for managing inventory states and reserved slots.</li>
 *     <li>{@code Core.getPlayerManager()}, which provides player-related information.</li>
 * </ul>
 * <p>
 * <strong>Event Listeners:</strong>
 * <ul>
 *     <li>{@code InventoryClickEvent}: Controls player actions within inventories, including restrictions on reserved slots or
 *     specific conditions like build or ride mode.</li>
 *     <li>{@code InventoryOpenEvent}: Prevents access to ender chests and redirects to a specialized custom menu.</li>
 *     <li>{@code PlayerSwapHandItemsEvent}: Manages offhand swaps, enforcing restrictions or swapping logic based on rank and build mode.</li>
 *     <li>{@code PlayerItemHeldEvent}: Triggers actions when specific item slots are selected or unselected, enabling behaviors like
 *     time or watch-related mechanics.</li>
 * </ul>
 */
public class InventoryListener implements Listener {

    /**
     * Constructs an instance of the InventoryListener class and registers a packet listener
     * to intercept and handle specific server-block action packets related to Ender Chests.
     *
     * <p>This listener monitors outgoing packets of type {@code PacketType.Play.Server.BLOCK_ACTION}
     * sent by the server. If the packet affects an Ender Chest block, the event is cancelled,
     * preventing the packet from being sent to the player. This is achieved by identifying the
     * block locations within the packet and verifying if the corresponding blocks in the world
     * are Ender Chests.</p>
     *
     * <p>Key responsibilities:</p>
     * <ul>
     *   <li>Intercepts and processes outgoing block action packets before they reach the player.</li>
     *   <li>Prevents server-client communication related to Ender Chests by cancelling the packet.</li>
     *   <li>Ensures players do not receive updates for actions on Ender Chest blocks.</li>
     * </ul>
     *
     * <p>This constructor automatically registers the packet listening behavior using the ProtocolLib
     * library and associates it with the main plugin instance.</p>
     */
    public InventoryListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ParkManager.getInstance(),
                PacketType.Play.Server.BLOCK_ACTION) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                StructureModifier<BlockPosition> modifier = packet.getBlockPositionModifier();
                List<BlockPosition> list = modifier.getValues();
                for (BlockPosition pos : list) {
                    if (event.getPlayer().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getType()
                            .equals(Material.ENDER_CHEST)) {
                        event.setCancelled(true);
                    }
                }
            }
        });
    }

    /**
     * Handles the {@link InventoryClickEvent} triggered when a player interacts with an inventory.
     * <p>
     * This method processes the specific actions a player performs in inventory
     * and takes necessary actions based on the player's inventory state.
     *
     * <p>
     * The method behaves as follows:
     * <ul>
     *     <li>If the player is in build mode, or if they interact with an inventory that
     *         is not their personal inventory and are not using the number key for interaction,
     *         the event is ignored.</li>
     *     <li>If the player is in ride mode, interacts with armor slots, reserved slots,
     *         or attempts a number key swap involving reserved slots,
     *         the event will be cancelled to prevent those actions.</li>
     *     <li>In other cases (when the player is in guest mode and clicks valid slots),
     *         the method allows the event to continue.</li>
     * </ul>
     *
     * @param event The inventory click event triggered by the player's interaction.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getWhoClicked().getUniqueId());
        if (player == null) return;
        InventoryUtil.InventoryState state = ParkManager.getInventoryUtil().getInventoryState(player);
        if (state.equals(InventoryUtil.InventoryState.BUILD) ||
                (event.getClickedInventory() != null &&
                        !event.getClickedInventory().equals(player.getInventory()) &&
                        !event.getClick().equals(ClickType.NUMBER_KEY)))
            //if player is in build mode or they're clicking on an inventory other than their personal inventory, skip
            return;
        if (state.equals(InventoryUtil.InventoryState.RIDE) ||
                event.getSlotType().equals(InventoryType.SlotType.ARMOR) ||
                InventoryUtil.isReservedSlot(event.getSlot()) ||
                (event.getClick().equals(ClickType.NUMBER_KEY) && InventoryUtil.isReservedSlot(event.getHotbarButton()))) {
            //if player is in ride mode, or is clicking on reserved/armor slots, or is swapping items to reserved slots, cancel
            event.setCancelled(true);
        }
        //otherwise (player is in guest mode, clicking on personal inventory slots not reserved), continue
    }

    /**
     * Handles the {@link InventoryOpenEvent} to control player access to certain inventory types
     * such as Ender Chests, and redirects players to custom menus if applicable.
     *
     * <p>This method ensures certain inventories are not directly accessible and,
     * in specific cases like the Ender Chest, redirects players to a custom locker menu.
     *
     * @param event the {@link InventoryOpenEvent} triggered when a player attempts to open an inventory.
     *              <ul>
     *                  <li>If the player is not recognized, the event is cancelled entirely.</li>
     *                  <li>If the opened inventory type is an Ender Chest, the event is cancelled
     *                      and the player is redirected to the Locker menu.</li>
     *              </ul>
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (player == null) {
            event.setCancelled(true);
            return;
        }
        Inventory inv = event.getInventory();
        if (inv.getType().equals(org.bukkit.event.inventory.InventoryType.ENDER_CHEST)) {
            event.setCancelled(true);
            ParkManager.getInventoryUtil().openMenu(player, MenuType.LOCKER);
        }
    }

    /**
     * Handles the {@link PlayerSwapHandItemsEvent} when a player attempts to swap items
     * between their main hand and offhand. This method enforces specific behaviors to
     * restrict or allow the swap based on the player's rank, build mode status, and the
     * types of items being swapped.
     *
     * <p>If the player does not meet the required conditions or performs an invalid swap,
     * the event is cancelled. Otherwise, certain swaps, such as with maps under specific
     * circumstances, are permitted.
     *
     * @param event the {@link PlayerSwapHandItemsEvent} triggered when a player attempts to
     *              swap items between their main hand and offhand.
     *              <ul>
     *                <li>Will be cancelled if the player does not have build permissions.</li>
     *                <li>Allows swapping of maps when certain conditions are satisfied.</li>
     *                <li>Sends appropriate messages to players for invalid actions.</li>
     *              </ul>
     */
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null || player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId()) return;
        if (ParkManager.getBuildUtil().isInBuildMode(player)) {
            ItemStack toMainhand = event.getMainHandItem();
            ItemStack toOffhand = event.getOffHandItem();
            if (toMainhand != null && toMainhand.getType().equals(Material.MAP)) {
                if (toOffhand != null && !toOffhand.getType().equals(Material.AIR)) {
                    player.sendMessage(ChatColor.RED + "You can only swap hands if your main hand is empty!");
                } else {
                    event.setCancelled(false);
                }
                return;
            }
            if (toOffhand != null && toOffhand.getType().equals(Material.MAP)) {
                event.setCancelled(false);
                return;
            }
        }
        player.performCommand("build");
    }

    /**
     * Handles the event when a player changes the currently held item slot in their inventory.
     * <p>
     * This method performs specific actions when the player moves to or from slot 6. If the player
     * selects slot 6, certain functionality related to a "watch" is activated; if they move away
     * from slot 6, that functionality is deactivated.
     * <p>
     * The method ensures it does not execute if the player is in build mode or if the player's
     * custom player data cannot be retrieved.
     *
     * @param event the {@link PlayerItemHeldEvent} that contains details about the item slot
     *              change, such as the previous and new slots, and the player involved.
     */
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null || ParkManager.getBuildUtil().isInBuildMode(player)) return;
        if (event.getNewSlot() == 6) {
            ParkManager.getTimeUtil().selectWatch(player);
        } else if (event.getPreviousSlot() == 6) {
            ParkManager.getTimeUtil().unselectWatch(player);
        }
    }
}