package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.cosmetics.Cosmetics;

/**
 * Represents a command to open the cosmetics inventory for a player.
 * <p>
 * The <code>CosmeticsCommand</code> class allows players to access the cosmetics inventory
 * via the "cosmetics" command. This command is designed to provide a user-friendly way to
 * access and manage cosmetic options in the game.
 *
 * <p><b>Features:</b></p>
 * <ul>
 *  <li>Provides a single, straightforward command to open the cosmetics inventory.</li>
 *  <li>Integrates seamlessly with the <code>Cosmetics</code> feature implementation.</li>
 *  <li>Executes directly for the player issuing the command.</li>
 * </ul>
 *
 * <p><b>Usage Restrictions:</b></p>
 * <ul>
 *  <li>Command availability and permissions are managed through the implementation of the
 *      <code>CoreCommand</code> framework.</li>
 *  <li>This is a player-exclusive command and can only be initiated by an in-game player.</li>
 * </ul>
 *
 * <p><b>Behavior:</b></p>
 * <ul>
 *  <li>Upon execution, the player's cosmetics inventory is opened using the <code>Cosmetics</code>
 *      system.</li>
 *  <li>Incorporates error handling for cases where the action cannot be completed as expected.</li>
 * </ul>
 */
public class CosmeticsCommand extends CoreCommand {

    /**
     * Constructs a new instance of the <code>CosmeticsCommand</code> class.
     * <p>
     * This constructor initializes the <code>CosmeticsCommand</code> with the command identifier
     * "cosmetics". The command is designed to allow players to open and manage their cosmetics
     * inventory in the game. It integrates with the <code>CoreCommand</code> framework for
     * command handling.
     *
     * <p><b>Key Features:</b></p>
     * <ul>
     *  <li>Associates the "cosmetics" command string with the <code>CosmeticsCommand</code>.</li>
     *  <li>Enables seamless interaction with the cosmetics management system.</li>
     *  <li>Executes via the <code>CoreCommand</code> framework.</li>
     * </ul>
     *
     * <p><b>Execution Context:</b></p>
     * <ul>
     *  <li>Primarily intended for use by in-game players.</li>
     *  <li>Permissions and availability are managed by the <code>CoreCommand</code> framework.</li>
     * </ul>
     */
    public CosmeticsCommand() {
        super("cosmetics");
    }

    /**
     * Handles the execution of the "cosmetics" command for a player.
     * <p>
     * This method opens the cosmetics inventory for the specified player, enabling
     * them to view and manage their cosmetic options. It utilizes the <code>Cosmetics</code>
     * system to provide access to the inventory interface.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *     <li>Directly opens the cosmetics inventory for the executing player.</li>
     *     <li>Relies on the <code>Cosmetics</code> system singleton for functionality.</li>
     * </ul>
     *
     * <p><b>Requirements:</b></p>
     * <ul>
     *     <li>This command must be executed by an in-game player.</li>
     * </ul>
     *
     * @param player the player executing the command. Represents the in-game entity
     *               for which the cosmetics inventory will be opened.
     * @param args   additional arguments passed with the command. Not utilized in this method.
     * @throws CommandException if an issue occurs while processing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Cosmetics.getInstance().openCosmeticsInventory(player);
    }
}
