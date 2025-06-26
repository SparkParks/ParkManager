package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Represents the command to provide a full stack of an item in the player's hand.
 * <p>
 * This command allows a player to fill the currently held item stack to its maximum stack size,
 * provided the item is not air or null. If the item does not support stacking,
 * its stack size will be set to 1.
 * </p>
 *
 * <p>
 * Usage of this command is typically restricted based on rank, with the required rank
 * being defined by the {@link Rank#CM} annotation value.
 * </p>
 *
 * <p><b>Command Information:</b></p>
 * <ul>
 *   <li><b>Name:</b> more</li>
 *   <li><b>Description:</b> Give a full stack of the item in the player's hand.</li>
 *   <li><b>Rank Required:</b> CM</li>
 * </ul>
 */
@CommandMeta(description = "Give a full stack of an item", rank = Rank.CM)
public class MoreCommand extends CoreCommand {

    /**
     * Initializes the {@code MoreCommand} instance.
     *
     * <p>
     * This constructor registers the command with the base name "more". The command
     * is designed to provide players with a full stack of the currently held item
     * in their inventory, adhering to any requirements defined in the command metadata.
     * </p>
     *
     * <p><b>Primary Behavior:</b></p>
     * <ul>
     *   <li>Checks the item in the player's main hand.</li>
     *   <li>Fills the item stack to its maximum stack size, if applicable.</li>
     *   <li>If the item is unstackable, sets its quantity to 1.</li>
     * </ul>
     *
     * <p><b>Restrictions:</b></p>
     * <ul>
     *   <li>The command requires the player to hold a non-air, non-null item in their hand.</li>
     *   <li>Command access is limited by the rank specified in the {@link Rank#CM} annotation.</li>
     * </ul>
     */
    public MoreCommand() {
        super("more");
    }

    /**
     * Handles the "more" command, which provides the player with a full stack of the item
     * currently held in their main hand. If the item in the player's hand is null or air,
     * an error message is sent, and the command does not proceed.
     *
     * <p>
     * This command retrieves the item stack in the player's main hand and adjusts its
     * quantity to either the maximum stack size allowed for that item or sets it to 1 if
     * the item cannot be stacked.
     * </p>
     *
     * @param player The player executing the command. This provides access to the player's inventory
     *               and allows sending feedback messages to the player.
     * @param args   The command arguments provided by the player, though this command does not
     *               make use of additional arguments.
     * @throws CommandException If an error occurs during the execution of the command, such as
     *                          issues with player data retrieval or inventory manipulation.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        PlayerInventory pi = player.getInventory();
        if (pi.getItemInMainHand() == null || pi.getItemInMainHand().getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "There is nothing in your hand!");
            return;
        }
        ItemStack stack = pi.getItemInMainHand();
        stack.setAmount(stack.getMaxStackSize() == -1 ? 1 : stack.getMaxStackSize());
    }
}