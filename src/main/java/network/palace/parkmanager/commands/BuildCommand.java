package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * Represents a command used to toggle build mode for players.
 * <p>
 * This command grants players the ability to enable or disable their build mode,
 * which is primarily used in scenarios where players need to construct or modify
 * game elements. The command ensures that a cooldown period is respected to
 * prevent excessive toggling.
 * <p>
 * Features:
 * <ul>
 *     <li>Allows toggling build mode for players.</li>
 *     <li>Enforces a 1-second cooldown between mode toggles.</li>
 *     <li>Validates player permissions and conditions before toggling mode.</li>
 * </ul>
 *
 * <p><b>Command Details:</b></p>
 * <ul>
 *     <li><b>Name:</b> build</li>
 *     <li><b>Description:</b> Toggle build mode</li>
 *     <li><b>Required Rank:</b> TRAINEEBUILD</li>
 * </ul>
 *
 * <p>Behavior:</p>
 * <ul>
 *     <li>If the player attempts to execute the command within 1 second of a
 *         previous execution, they are shown a warning message.</li>
 *     <li>The build mode toggle is processed only if the player meets the
 *         necessary conditions provided by the {@code ParkManager}'s utilities.</li>
 *     <li>On a successful toggle, the player's build mode status is updated
 *         and a timestamp is saved to enforce the cooldown.</li>
 * </ul>
 */
@CommandMeta(description = "Toggle build mode", rank = Rank.TRAINEEBUILD)
public class BuildCommand extends CoreCommand {

    /**
     * Constructs a new BuildCommand instance.
     * <p>
     * The <code>BuildCommand</code> is associated with the "build" command string, enabling players
     * to toggle their build mode on or off. This command is primarily used in scenarios where players
     * need to construct or modify game elements, while adhering to specific conditions and restrictions.
     * <p>
     * When executed, the command ensures:
     * <ul>
     *     <li>A cooldown period of 1 second between command executions to prevent excessive toggling.</li>
     *     <li>The player meets all the conditions required for toggling build mode.</li>
     *     <li>The build mode status is updated successfully on valid toggles.</li>
     * </ul>
     *
     * <p><b>Features:</b></p>
     * <ul>
     *     <li>Toggles a player's build mode when executed.</li>
     *     <li>Enforces a 1-second cooldown between toggles to prevent spam.</li>
     *     <li>Validates player permissions and conditions using utility methods from <code>ParkManager</code>.</li>
     * </ul>
     *
     * <p><b>Command Details:</b></p>
     * <ul>
     *     <li><b>Command Name:</b> build</li>
     *     <li><b>Required Rank:</b> <code>Rank.TRAINEEBUILD</code></li>
     *     <li><b>Description:</b> Toggle build mode on and off.</li>
     * </ul>
     *
     * <p><b>Error Handling:</b></p>
     * <ul>
     *     <li>Players attempting to toggle build mode within the cooldown period will receive a warning message.</li>
     *     <li>If the build mode toggle fails due to unmet conditions, no changes are applied, and the command is safely terminated.</li>
     * </ul>
     */
    public BuildCommand() {
        super("build");
    }

    /**
     * Handles the execution of the build mode toggle command for a player.
     * <p>
     * This method ensures that the player meets all conditions for toggling their
     * build mode and enforces a cooldown of 1 second between consecutive toggles.
     * On successful toggling, the player's build mode status is updated and the
     * cooldown timer is recorded.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *     <li>If the command is executed within the 1-second cooldown period,
     *     an error message is shown, and the command execution is halted.</li>
     *     <li>Conditions for toggling the build mode are validated using the {@code ParkManager}'s utilities.</li>
     *     <li>Upon successfully toggling the build mode, a timestamp is saved in
     *     the player's registry to enforce the cooldown.</li>
     * </ul>
     *
     * @param player The player executing the command. This includes information
     *               about the player's current state, permission level, and registry.
     * @param args   The arguments provided with the command. Currently, this method
     *               does not utilize any arguments.
     *
     * @throws CommandException If there is an error during the command execution,
     *                          such as missing permissions or an internal failure.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (player.getRegistry().hasEntry("buildModeTimeout") && (System.currentTimeMillis() - (long) player.getRegistry().getEntry("buildModeTimeout")) < 1000) {
            player.sendMessage(ChatColor.RED + "You must wait at least 1s before doing this again!");
            return;
        }
        if (!ParkManager.getBuildUtil().canToggleBuildMode(player)) return;
        if (ParkManager.getBuildUtil().toggleBuildMode(player))
            player.getRegistry().addEntry("buildModeTimeout", System.currentTimeMillis());
    }
}
