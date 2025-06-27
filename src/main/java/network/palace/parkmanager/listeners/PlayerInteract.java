package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.handlers.sign.ServerSign;
import network.palace.parkmanager.magicband.BandInventory;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The <code>PlayerInteract</code> class implements the <code>Listener</code> interface to handle player interaction events in a Minecraft server.
 * <p>
 * This listener processes various actions performed by a player during interaction events. Specifically, it manages:
 * <ul>
 *   <li>Right-clicking signs to check for specific functionalities associated with the sign.</li>
 *   <li>Blocking access to certain interactable blocks for players without appropriate permissions or ranks.</li>
 *   <li>Handling inventory item interactions based on the player's held item slot.</li>
 * </ul>
 * </p>
 *
 * <p>The class is structured as follows:</p>
 * <ul>
 *   <li><b>onPlayerInteract:</b> The primary event handler that processes {@link PlayerInteractEvent}. It manages:
 *     <ul>
 *       <li>Interaction with signs to trigger associated functionality defined in <code>ServerSign</code>.</li>
 *       <li>Canceling interactions with specific blocks (e.g., workbench, furnace) if the player does not meet rank requirements.</li>
 *       <li>Handling specific item interaction slots for functionalities such as opening menus or achievements.</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>Conditions are checked to ensure interactions only occur with valid players and blocks/items are processed according to the player's build mode, rank, and inventory state.</
 * p>
 *
 * <p><b>Event Handling:</b></p>
 * <ul>
 *   <li>Right-clicking on blocks is processed for sign interactions to trigger custom behaviors based on the sign's header.</li>
 *   <li>For players with insufficient rank, interactions with specified block types are canceled.</li>
 *   <li>Item interactions in designated inventory slots trigger specific events, such as opening backpacks, accessing watch menus, or handling autograph-related actions.</li>
 * </ul>
 *
 * <p>Custom functionality integration is dynamically handled using methods from external manager classes like <code>ParkManager</code>, <code>Core</code>, and <code>Rank</code>.</
 * p>
 */
public class PlayerInteract implements Listener {

    /**
     * Handles the interaction event when a player interacts with blocks or items in the game world.
     * <p>
     * This method evaluates player interactions with blocks such as signs or other restricted blocks,
     * manages item use based on the player's held slot hand, and enforces rank-dependent restrictions.
     * It also integrates with various game mechanics like menus, achievements, and custom interactions
     * provided by supporting managers.
     * </p>
     *
     * @param event The {@link PlayerInteractEvent} that encapsulates details of the player's interaction,
     *              including clicked block or item, interaction type, and the player performing the action.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) {
            event.setCancelled(true);
            return;
        }
        Action action = event.getAction();

        //Check sign clicks
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Block b = event.getClickedBlock();
            if (b.getType().equals(Material.SIGN) || b.getType().equals(Material.WALL_SIGN) || b.getType().equals(Material.SIGN_POST)) {
                Sign s = (Sign) b.getState();
                ServerSign.SignEntry signEntry = ServerSign.getByHeader(s.getLine(0));
                if (signEntry != null) {
                    signEntry.getHandler().onInteract(player, s, event);
                }
                return;
            }
        }

        if (player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId() && (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_BLOCK))) {
            Block clicked = event.getClickedBlock();
            if (clicked != null) {
                switch (clicked.getType()) {
                    case FLOWER_POT:
                    case ANVIL:
                    case ENCHANTMENT_TABLE:
                    case WORKBENCH:
                    case FURNACE:
                    case BURNING_FURNACE:
                        event.setCancelled(true);
                        return;
                }
            }
        }

        //Handle inventory item click
        if (ParkManager.getBuildUtil().isInBuildMode(player)) {
            return;
        }
        ItemStack hand = event.getItem();
        if (hand == null || hand.getType() == null) return;
        int slot = player.getHeldItemSlot();
        boolean cancel = false;
        switch (slot) {
            case 5:
                cancel = true;
                ParkManager.getInventoryUtil().openMenu(player, MenuType.BACKPACK);
                break;
            case 6:
                //open watch menu
                ParkManager.getMagicBandManager().openInventory(player, BandInventory.TIMETABLE);
                player.giveAchievement(11);
                break;
            case 7:
                //autograph book
                if (!event.getAction().equals(Action.PHYSICAL)) {
                    cancel = true;
                    ParkManager.getAutographManager().handleInteract(player);
                }
                break;
            case 8:
                //open magicband
                ParkManager.getMagicBandManager().openInventory(player, BandInventory.MAIN);
                player.giveAchievement(2);
                break;
        }
        if (cancel) event.setCancelled(true);
    }
}
