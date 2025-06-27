package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * <p>The <code>EntityDamage</code> class is a listener that handles various entity-related events and applies custom restrictions
 * to certain in-game actions. This class is primarily used in the context of managing player interactions and damage
 * to entities and objects within the game, enforcing permissions and safeguards.</p>
 *
 * <p>Key functionality:</p>
 * <ul>
 *     <li>Prevents players and projectiles from damaging specific entities like players, minecarts, item frames, paintings, and armor stands.</li>
 *     <li>Imposes restrictions based on player rank, ensuring only authorized staff can perform certain actions.</li>
 *     <li>Requires staff to be in "Build Mode" for modifying entities.</li>
 *     <li>Handles custom behavior for interacting with item frames (e.g., clearing items from the frame due to game bugs).</li>
 * </ul>
 *
 * <p>Key Events Handled:</p>
 * <ul>
 *     <li><b>EntityDamageEvent:</b> Cancels damage to specific entities, e.g., players and entities damaged by projectiles.</li>
 *     <li><b>EntityDamageByEntityEvent:</b> Prevents unauthorized players from damaging certain entities like minecarts, item frames, and armor stands. Enforces rank and build mode
 *  checks for staff.</li>
 *     <li><b>PlayerInteractEntityEvent:</b> Restricts interaction with entities like item frames and paintings to authorized players in build mode.</li>
 *     <li><b>HangingBreakByEntityEvent:</b> Prevents the removal of hanging entities (item frames, paintings) by unauthorized players.</li>
 * </ul>
 *
 * <p>Custom Behavior:</p>
 * <ul>
 *     <li><b>onDamage:</b> Checks if the damage caused by a player should be canceled, based on player rank and build mode status.</li>
 * </ul>
 */
public class EntityDamage implements Listener {

    /**
     * Handles the {@link EntityDamageEvent} to enforce specific restrictions on damage events.
     * <p>
     * This method performs the following checks:
     * <ul>
     *     <li>Cancels all damage events targeting {@code PLAYER} entities.</li>
     *     <li>Cancels all damage events caused by projectiles.</li>
     * </ul>
     * <p>
     * The event is canceled when any of the above conditions are met.
     * </p>
     *
     * @param event The {@link EntityDamageEvent} triggered when an entity takes damage.
     *              <ul>
     *                  <li>{@code event.getEntityType()} is used to determine the type of entity (e.g., PLAYER).</li>
     *                  <li>{@code event.getCause()} is checked to identify the damage type (e.g., PROJECTILE).</li>
     *                  <li>If any conditions are met, {@code event.setCancelled(true)} is called to cancel the event.</li>
     *              </ul>
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        boolean cancel = false;
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            // Prevent all player damage
            cancel = true;
        } else if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            // Prevent damage caused by projectiles
            cancel = true;
        }
        if (cancel) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles the {@link EntityDamageByEntityEvent} to enforce specific behaviors for certain entity types
     * such as Minecarts, Item Frames, and Armor Stands when damaged by players or other entities.
     *
     * <p>The behavior includes:
     * <ul>
     * <li>Restricting players below a specific rank from damaging certain entities.</li>
     * <li>Cancelling damage events caused by arrows on Minecarts.</li>
     * <li>Requiring staff members to be in Build Mode to edit Armor Stands or other entities.</li>
     * </ul>
     *
     * @param event The {@link EntityDamageByEntityEvent} that provides information about the involved entities
     *              and allows cancellation or modification of the damage event.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        switch (event.getEntityType()) {
            case MINECART:
            case MINECART_CHEST:
            case MINECART_COMMAND:
            case MINECART_FURNACE:
            case MINECART_HOPPER:
            case MINECART_MOB_SPAWNER:
            case MINECART_TNT: {
                if (damager.getType().equals(EntityType.PLAYER)) {
                    CPlayer player = Core.getPlayerManager().getPlayer(damager.getUniqueId());
                    if (player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId()) {
                        // Non-staff can't destroy Minecarts
                        event.setCancelled(true);
                        return;
                    }
                } else if (damager.getType().equals(EntityType.ARROW)) {
                    // Cancel all damage done by arrows
                    event.setCancelled(true);
                    return;
                }
                break;
            }
            case ITEM_FRAME: {
                if (onDamage(event.getEntity(), damager)) event.setCancelled(true);
                break;
            }
            case ARMOR_STAND: {
                if (!damager.getType().equals(EntityType.PLAYER)) return;
                CPlayer player = Core.getPlayerManager().getPlayer(damager.getUniqueId());
                if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId()) {
                    if (!ParkManager.getBuildUtil().isInBuildMode(player)) {
                        // Staff can only edit entities when in Build mode
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You must be in Build Mode to break entities!");
                    }
                } else {
                    // Non-staff can't edit entities
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You are not allowed to break this!");
                }
                break;
            }
        }
    }

    /**
     * Handles interactions with specific entities (e.g., ITEM_FRAME and PAINTING) and applies
     * custom permissions and restrictions for players depending on their rank and build mode status.
     * This method ensures that only authorized players can interact with or modify certain entities.
     *
     * <p>Scenarios handled:</p>
     * <ul>
     *     <li>Non-staff players are prevented from editing specific entities.</li>
     *     <li>Staff players are only allowed to edit entities if they are in build mode.</li>
     * </ul>
     *
     * @param event the {@link PlayerInteractEntityEvent} triggered when a player interacts with an entity.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        switch (event.getRightClicked().getType()) {
            case ITEM_FRAME:
            case PAINTING: {
                CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
                if (player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId()) {
                    // Non-staff can't edit entities
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You are not allowed to edit this!");
                } else if (!ParkManager.getBuildUtil().isInBuildMode(player)) {
                    // Staff can only edit entities when in Build mode
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You must be in Build Mode to edit entities!");
                }
                break;
            }
        }
    }

    /**
     * Handles the {@link HangingBreakByEntityEvent} to manage the destruction of hanging entities
     * such as {@code ITEM_FRAME} and {@code PAINTING}.
     *
     * <p>This method checks the type of the entity being broken and invokes the {@code onDamage(Entity, Entity)}
     * method to determine whether the event should be cancelled.</p>
     *
     * <ul>
     *     <li>If the entity is of type {@code ITEM_FRAME} or {@code PAINTING}, further validation
     *         is performed through the {@code onDamage} method.</li>
     *     <li>If the {@code onDamage} method returns {@code true}, the hanging break event is cancelled.</li>
     * </ul>
     *
     * @param event The {@link HangingBreakByEntityEvent} triggered when a hanging entity
     *              is being broken by another entity.
     *              <ul>
     *                  <li>{@code event.getEntity()} retrieves the entity being broken.</li>
     *                  <li>{@code event.getRemover()} retrieves the entity responsible for the action.</li>
     *                  <li>{@code event.setCancelled(true)} prevents the entity from being destroyed.</li>
     *              </ul>
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        switch (event.getEntity().getType()) {
            case ITEM_FRAME:
            case PAINTING: {
                if (onDamage(event.getEntity(), event.getRemover())) event.setCancelled(true);
                break;
            }
        }
    }

    /**
     * Handles the logic for entity damage events triggered by another entity.
     * <p>
     * This method determines whether the damage is allowed based on the damager's type,
     * rank, and mode (e.g., Build Mode). Specific restrictions are applied to certain entities,
     * such as {@link ItemFrame}.
     * </p>
     *
     * @param entity The entity that is being damaged.
     * @param damager The entity that is causing the damage.
     *                It is evaluated to determine if the action is permitted based on its type and rank.
     * @return {@code true} if the damage event should be cancelled (preventing the damage);
     *         {@code false} otherwise.
     */
    public boolean onDamage(Entity entity, Entity damager) {
        if (!damager.getType().equals(EntityType.PLAYER)) return false;
        CPlayer player = Core.getPlayerManager().getPlayer(damager.getUniqueId());
        if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId()) {
            if (!ParkManager.getBuildUtil().isInBuildMode(player)) {
                // Staff can only edit entities when in Build mode
                player.sendMessage(ChatColor.RED + "You must be in Build Mode to break entities!");
                return true;
            } else {
                if (entity.getType().equals(EntityType.ITEM_FRAME)) {
                    ItemFrame frame = (ItemFrame) entity;
                    if (frame.getItem() != null) {
                        // We need to do this for now because of https://bugs.mojang.com/browse/MC-130558
                        frame.setItem(ItemUtil.create(Material.AIR));
                    }
                    return false;
                }
            }
        } else {
            // Non-staff can't edit entities
            player.sendMessage(ChatColor.RED + "You are not allowed to break this!");
            return true;
        }
        return false;
    }
}
