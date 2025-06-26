package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * <p>The <code>PlayerWeatherCommand</code> class is a custom server command that allows players
 * or administrators to set weather conditions on a per-player basis. This command modifies the
 * weather experienced by an individual player, leaving the global server weather unchanged.</p>
 *
 * <p>The command can be used to set the weather for a player to one of the following options:</p>
 * <ul>
 *   <li><b>rain</b>: Sets the player's weather to raining.</li>
 *   <li><b>sun</b>: Sets the player's weather to clear.</li>
 *   <li><b>reset</b>: Resets the player's weather, synchronizing it back with the server's global weather.</li>
 * </ul>
 *
 * <p><b>Usage:</b></p>
 * <ul>
 *   <li><b>/pweather [rain/sun/reset]</b>: Changes the executing player's personal weather.</li>
 *   <li><b>/pweather [rain/sun/reset] [Username]</b>: Changes the specified player's personal weather. Requires appropriate permissions.</li>
 * </ul>
 *
 * <p><b>Behavior:</b></p>
 * <ul>
 *   <li>If the command is executed without arguments or with invalid arguments, an error message will be sent to the executor.</li>
 *   <li>If a target player is not found, an appropriate error message will be displayed.</li>
 *   <li>Weather settings are applied to players individually, offering a personalized experience.</li>
 * </ul>
 */
@CommandMeta(description = "Set player weather", rank = Rank.CM)
public class PlayerWeatherCommand extends CoreCommand {

    /**
     * Constructs a new instance of the {@code PlayerWeatherCommand} class.
     * <p>
     * This command is used to manage and manipulate player-specific weather settings in the game.
     * The primary purpose of this constructor is to initialize the command with the identifier "pweather".
     *
     * <p><b>Usage:</b> This constructor is invoked when creating an instance of {@code PlayerWeatherCommand}.
     *
     * <p><b>Properties:</b>
     * <ul>
     *     <li>Super constructor is called with the argument "pweather" to register the command's name.</li>
     * </ul>
     */
    public PlayerWeatherCommand() {
        super("pweather");
    }

    /**
     * Handles the player command to change or reset the weather for a specific player
     * or themselves.
     *
     * <p>The command expects arguments to set the personal weather for a player. If no
     * username is provided, it defaults to setting the weather for the command sender.
     * Supported weather types are "rain", "sun", and "reset".</p>
     *
     * <ul>
     *   <li>If the weather type or username is invalid, appropriate error messages are sent to the player.</li>
     *   <li>If the command aims to set weather for a target player but the target is not found,
     * an error message is displayed to the command sender.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} executing the command. This player will either have their
     *               own weather updated or be sending the command on behalf of another player.
     * @param args   A {@link String} array containing the command arguments.
     *               <ul>
     *                 <li>args[0]: The weather type ("rain", "sun", "reset").</li>
     *                 <li>args[1]: (Optional) The target player username.</li>
     *               </ul>
     * @throws CommandException If an error occurs during the execution of the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/pweather [rain/sun/reset] [Username]");
            return;
        }
        if (args.length < 2) {
            setPlayerWeather(player.getBukkitPlayer(), player, args[0]);
        } else {
            CPlayer target = Core.getPlayerManager().getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            setPlayerWeather(player.getBukkitPlayer(), target, args[0]);
        }
    }

    /**
     * Handles the execution of the `/pweather` command with minimal specificity requirements.
     *
     * <p>This command allows the sender to alter or reset the personal weather setting of a target player.
     * The sender must provide a valid weather type ("rain", "sun", "reset") and a target player's username.</p>
     *
     * <ul>
     *   <li>If insufficient arguments are provided, the sender receives an error message with usage instructions.</li>
     *   <li>If the specified target player cannot be found, an error message is sent to the sender.</li>
     *   <li>The method delegates setting or resetting the weather to the {@code setPlayerWeather()} method.</li>
     * </ul>
     *
     * @param sender The {@link CommandSender} executing the command. This can be the console or a player.
     * @param args   A {@link String} array containing the command arguments:
     *               <ul>
     *                 <li>args[0]: The desired weather type ("rain", "sun", "reset").</li>
     *                 <li>args[1]: The username of the target player whose weather is to be modified.</li>
     *               </ul>
     * @throws CommandException If an error occurs while executing the command, such as an invalid argument.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/pweather [rain/sun/reset] [Username]");
            return;
        }
        CPlayer target = Core.getPlayerManager().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        setPlayerWeather(sender, target, args[0]);
    }

    /**
     * Sets the personal weather for a specific player or resets it to match the server's weather.
     *
     * <p>This method allows the command sender to change the weather for a target player
     * or themselves based on the specified weather type. The supported weather types are:
     * <ul>
     *     <li><b>sun</b>: Sets clear weather.</li>
     *     <li><b>rain</b>: Sets rainy weather.</li>
     *     <li><b>reset</b>: Resets the player's weather to match the global server weather.</li>
     * </ul>
     * If an invalid weather type is provided, an error message is sent to the command sender.
     *
     * @param sender The {@link CommandSender} executing the command. This can be the player whose weather is being
     *               updated or a player modifying another user's weather.
     * @param target The {@link CPlayer} whose weather is being modified. If the weather type is "reset", the target's
     *               weather will be synchronized with the server's weather.
     * @param s      A {@link String} representing the desired weather type. It must be one of the following:
     *               <ul>
     *                   <li>"sun" for clear weather.</li>
     *                   <li>"rain" for rainy weather.</li>
     *                   <li>"reset" to revert to the server's weather.</li>
     *               </ul>
     */
    private void setPlayerWeather(CommandSender sender, CPlayer target, String s) {
        boolean same = (sender instanceof Player) && ((Player) sender).getUniqueId().equals(target.getUniqueId());
        WeatherType type;
        switch (s.toLowerCase()) {
            case "sun": {
                type = WeatherType.CLEAR;
                break;
            }
            case "rain": {
                type = WeatherType.DOWNFALL;
                break;
            }
            case "reset": {
                type = null;
                break;
            }
            default: {
                sender.sendMessage(ChatColor.RED + "/pweather [rain/sun/reset] [Username]");
                return;
            }
        }
        if (type == null) {
            target.getBukkitPlayer().resetPlayerWeather();
            sender.sendMessage(ChatColor.DARK_AQUA + (same ? "Your" : (target.getName() + "'s")) + ChatColor.GREEN +
                    " weather now matches the server.");
        } else {
            target.getBukkitPlayer().setPlayerWeather(type);
            sender.sendMessage(ChatColor.DARK_AQUA + (same ? "Your" : (target.getName() + "'s")) + ChatColor.GREEN +
                    " weather has been set to " + ChatColor.DARK_AQUA + type.name().toLowerCase() + ChatColor.GREEN + "!");
        }
    }
}
