package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The {@code SpeedCommand} class represents a command that allows players to set their movement speed or
 * the movement speed of another player. This includes walking or flying speeds, depending on the player's rank
 * and current state.
 * <p>
 * This command supports different ranks, with additional privileges granted to higher ranks. For example:
 * <ul>
 * <li>Players with proper permissions can adjust their own speed.</li>
 * <li>Higher ranks (e.g., {@code Rank.CM}) can adjust the speed of other players.</li>
 * <li>Players with a rank of {@code Rank.VIP} or higher and flying enabled will also have their flying speed adjusted.</li>
 * </ul>
 * <p>
 * The speed is inputted as a string and converted internally to a floating-point value (ranging between 0.0 and 10.0).
 * The resulting speed is scaled appropriately for Minecraft's movement-speed system.
 * <p>
 * Usage:
 * <ul>
 * <li>If issued by players, the command syntax is {@code /speed [speed]} to adjust their own speed.</li>
 * <li>For higher ranks, the syntax can include a target player, {@code /speed [speed] [player]}.</li>
 * <li>When executed by non-player entities (e.g., console commands), a target player must be specified.</li>
 * </ul>
 */
@CommandMeta(description = "Set the movement speed of a player", rank = Rank.TRAINEEBUILD)
public class SpeedCommand extends CoreCommand {

    /**
     * Constructs a new {@code SpeedCommand} instance.
     * <p>
     * This command allows manipulation of movement speed for players. Once initialized, it is
     * associated with the "speed" command keyword.
     * <p>
     * The {@code SpeedCommand} handles parsing and setting both walking and flying speed for either
     * the command sender or a specified target player, depending on the provided arguments.
     * <ul>
     *   <li>Supports adjusting speed within a defined range.</li>
     *   <li>Validates input values to prevent invalid speed configurations.</li>
     * </ul>
     */
    public SpeedCommand() {
        super("speed");
    }

    /**
     * Handles the {@code /speed} command for setting the movement speed of a player.
     *
     * <p>This command can adjust a player's walking or flying speed based on their
     * rank and the given input speed. Higher-ranked players may also specify a target player.</p>
     *
     * @param player The player executing the command.
     * @param args   The arguments passed with the command.
     *               <ul>
     *                 <li>If {@code args.length < 1}, provides usage instructions to the player.</li>
     *                 <li>The first argument is the speed to set, expressed as a string.</li>
     *                 <li>The second optional argument specifies the target player and is valid only if the command issuer's rank permits.</li>
     *               </ul>
     *
     * @throws CommandException If an invalid or unauthorized action is attempted in the command execution.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/speed [speed]" + (player.getRank().getRankId() >= Rank.CM.getRankId() ? " <player>" : ""));
            return;
        }
        CPlayer target;
        if (args.length > 1 && player.getRank().getRankId() >= Rank.CM.getRankId()) {
            target = Core.getPlayerManager().getPlayer(args[1]);
        } else {
            target = player;
        }
        setSpeed(player.getBukkitPlayer(), target, args[0]);
    }

    /**
     * Handles the execution of a command in a more general, unspecific manner.
     * This method specifically processes the <code>/speed</code> command to set the
     * walking or flying speed for a player or another target player.
     *
     * <p>The method validates the provided arguments and delegates the speed-setting
     * operation to a helper method after sufficient checks.</p>
     *
     * <ul>
     *   <li>If fewer than two arguments are provided, the sender is presented with
     *       usage instructions.</li>
     *   <li>The first argument represents the speed to set, and the second argument
     *       specifies the player whose movement speed is to be adjusted.</li>
     * </ul>
     *
     * @param sender The entity executing the command. This can be a player or the console.
     * @param args   The command arguments array:
     *               <ul>
     *                 <li><code>args[0]</code>: The desired movement speed, represented as a string.</li>
     *                 <li><code>args[1]</code>: The target player's name (optional, defaults to the sender).</li>
     *               </ul>
     * @throws CommandException If an error occurs during the command execution, such as
     *                          invalid arguments or unauthorized actions.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/speed [speed] [player]");
            return;
        }
        setSpeed(sender, Core.getPlayerManager().getPlayer(args[1]), args[0]);
    }

    /**
     * Sets the movement speed for a player, either walking or flying, depending on their rank and current status.
     *
     * <p>This method adjusts the speed of the specified target player, taking into account whether
     * the player is flying or walking. The speed value is parsed from the provided string input.</p>
     *
     * <p>If the target player is not found, an error message is sent to the command sender.
     * Otherwise, the speed is adjusted within the allowed range, and a success message is sent
     * indicating whose speed was modified and whether it applies to walking or flying.</p>
     *
     * @param sender      The entity (player, console, etc.) that issued the command to set the speed.
     *                    <ul>
     *                      <li>If the {@code sender} is a player and is the same as the target, the message will specify that it applies to "your" speed.</li>
     *                      <li>If the sender is different from the target or is non-player (e.g., console), the message will specify the target player's name.</li>
     *                    </ul>
     * @param target      The player whose speed is being set. If {@code null}, the method sends an error message
     *                    to the {@code sender} indicating the player could not be found.
     * @param speedString A string representing the desired speed. This value is parsed and converted to a float.
     *                    <ul>
     *                      <li>The speed value must be within the range of 0.0 to 10.0.</li>
     *                      <li>If the value is invalid or outside this range, a default speed is used.</li>
     *                    </ul>
     */
    private void setSpeed(CommandSender sender, CPlayer target, String speedString) {
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        boolean isFlying = target.getRank().getRankId() >= Rank.VIP.getRankId() && target.isFlying();
        float speed = getMoveSpeed(speedString);
        if (isFlying) {
            target.setFlySpeed(getRealMoveSpeed(speed, true));
        } else {
            target.setWalkSpeed(getRealMoveSpeed(speed, false));
        }
        sender.sendMessage(ChatColor.GREEN + "Set " +
                (((sender instanceof Player) && ((Player) sender).getUniqueId().equals(target.getUniqueId())) ? "your" : (target.getName() + "'s"))
                + " " + (isFlying ? "flying" : "walking") + " speed to " + speed);
    }

    /**
     * Parses and evaluates a given movement speed string, ensuring it adheres to defined constraints.
     *
     * <p>This method attempts to convert the provided string into a float value and ensures that
     * the result falls within an acceptable range. If the input cannot be parsed or is outside the
     * range of 0.0 to 10.0, a default speed is returned.</p>
     *
     * <ul>
     *   <li>Maximum allowed speed: 10.0</li>
     *   <li>Minimum allowed speed: 0.0</li>
     *   <li>Default speed on invalid input: 1.0</li>
     * </ul>
     *
     * @param moveSpeed A {@code String} containing the desired movement speed to be parsed.
     *                  <ul>
     *                    <li>If the value is valid and within the range of 0.0 to 10.0, it will be returned as a float.</li>
     *                    <li>Non-numerical or out-of-range inputs will result in a default value being returned.</li>
     *                  </ul>
     * @return A {@code float} representing the parsed movement speed.
     *         <ul>
     *           <li>Returns the parsed speed value if valid and within range (0.0 to 10.0).</li>
     *           <li>Returns 1.0 as a default speed for invalid inputs or non-numerical strings.</li>
     *         </ul>
     */
    private float getMoveSpeed(final String moveSpeed) {
        float userSpeed;
        try {
            userSpeed = Float.parseFloat(moveSpeed);
            if (userSpeed > 10f) {
                userSpeed = 10f;
            } else if (userSpeed < 0f) {
                userSpeed = 0f;
            }
        } catch (NumberFormatException e) {
            return 1;
        }
        return userSpeed;
    }

    /**
     * Calculates the actual movement speed based on a user's desired speed and their flying status.
     *
     * <p>The method determines the real speed by applying a default speed multiplier for values
     * below a specified threshold and a scaled ratio for higher values.
     * The default speed varies depending on whether the user is flying or walking.</p>
     *
     * @param userSpeed The desired speed specified by the user. Values below 1 are scaled by the
     *                  default speed, while values of 1 and higher are scaled linearly.
     * @param isFly     Indicates whether the user is flying. When true, a lower default speed is
     *                  applied compared to walking.
     *
     * @return A float representing the real movement speed, which is based on the user's input and
     *         adjusted according to their flying status. The return value ensures that the movement
     *         speed is within acceptable operational ranges.
     */
    private float getRealMoveSpeed(float userSpeed, boolean isFly) {
        final float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1f;

        if (userSpeed < 1f) {
            return defaultSpeed * userSpeed;
        } else {
            float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }
}
