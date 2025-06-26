package network.palace.parkmanager.commands.config.park;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.commands.config.ParkCommand;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.ParkType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

/**
 * Represents a command to create a new park.
 * <p>
 * This class extends the {@link CoreCommand} class to handle the creation of new parks.
 * It validates the input arguments for the type of park, the world, and the region, and if valid,
 * it creates a new park by adding it to the park manager. The creation process involves:
 * </p>
 * <ul>
 *   <li>Checking if the provided park type is valid.</li>
 *   <li>Ensuring no park with the same ID already exists on the server.</li>
 *   <li>Validating the specified world.</li>
 *   <li>Validating the specified region within the chosen world.</li>
 * </ul>
 * <p>On successful creation, a success message is sent to the player.</p>
 *
 * <p>If the input arguments are incorrect or invalid, an error message is sent to the player.
 * This command makes use of {@link ParkType}, {@link ParkManager}, {@link WorldGuardPlugin},
 * among other components of the system.
 * </p>
 *
 * <p><b>Command Syntax:</b></p>
 * <ul>
 *   <li><code>/parkconfig park create [type] [world] [region]</code></li>
 * </ul>
 *
 * <p><b>Error Messages:</b></p>
 * <ul>
 *   <li>If insufficient arguments are provided, a message is sent with the correct syntax and
 *       available park types obtained from {@link ParkType#listIDs()}.</li>
 *   <li>If the park type is invalid, the player is notified that the provided type is incorrect.</li>
 *   <li>If a park with the same ID already exists, the player is informed of the conflict.</li>
 *   <li>If the specified world is invalid, the player is notified of the discrepancy.</li>
 *   <li>If the specified region is invalid or not found within the chosen world, the player receives an error message.</li>
 * </ul>
 */
@CommandMeta(description = "Create a new park")
public class CreateCommand extends CoreCommand {

    /**
     * Constructs a new {@code CreateCommand} instance, which represents a subcommand
     * within the park management system for creating parks.
     *
     * <p>This command is designed to handle the addition of new parks by leveraging
     * user-provided input for configuration. The {@code CreateCommand} operates as
     * part of the hierarchical command structure under the parent {@code ParkCommand}.</p>
     *
     * <p>Key characteristics of this command include:</p>
     * <ul>
     *   <li>Serves as a subcommand of {@link ParkCommand}, focused solely on park creation.</li>
     *   <li>Registers itself with the parent command system using the identifier "create".</li>
     *   <li>Works in conjunction with other related subcommands, such as {@code ListCommand},
     *   {@code ReloadCommand}, and {@code RemoveCommand}, to provide comprehensive park management functionality.</li>
     * </ul>
     *
     * <p>By utilizing this command, users can create customized parks within the system,
     * enabling a more dynamic and organized structure for park-related operations.</p>
     */
    public CreateCommand() {
        super("create");
    }

    /**
     * Handles the "park create" command to create a new park of a specified type in a defined world and region.
     * <p>
     * This method validates the provided input arguments, ensures the park type is valid, checks that no other park
     * with the same ID exists, and verifies the existence of a specified world and region before creating the park.
     * If any input validation fails, an appropriate error message is sent to the player.
     * Upon successful creation, a success message is sent to the player.
     *
     * @param player the player executing the command; used for sending feedback about the command's execution.
     * @param args   the input arguments for the command, represented as follows:
     *               <ul>
     *               <li><b>args[0]</b>: The type of the park (e.g., "mk", "epcot", "usf"). This value must match a valid {@link ParkType}.</li>
     *               <li><b>args[1]</b>: The world in which the park should be created. Must correspond to a valid world name.</li>
     *               <li><b>args[2]</b>: The WorldGuard region ID where the park will be located. The region must exist in the specified world.</li>
     *               </ul>
     * @throws CommandException if the command execution fails due to unavailable external resources or logical errors.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/parkconfig park create [type] [world] [region]");
            player.sendMessage(ChatColor.AQUA + "Types: " + ParkType.listIDs());
            return;
        }
        ParkType type = ParkType.fromString(args[0].toUpperCase());
        if (type == null) {
            player.sendMessage(ChatColor.RED + args[0] + " isn't a valid park id!");
            return;
        }
        if (ParkManager.getParkUtil().getPark(type) != null) {
            player.sendMessage(ChatColor.RED + "A park already exists on this server with the id " + args[0] + "!");
            return;
        }
        World world = Bukkit.getWorld(args[1]);
        if (world == null) {
            player.sendMessage(ChatColor.RED + args[1] + " isn't a valid world for this server!");
            return;
        }
        ProtectedRegion region;
        try {
            region = WorldGuardPlugin.inst()
                    .getRegionManager(world)
                    .getRegion(args[2]);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Invalid region " + args[2] + " in world " + args[1]);
            return;
        }
        ParkManager.getParkUtil().addPark(new Park(type, world, region));
        player.sendMessage(ChatColor.GREEN + "Created a new park " + type.getTitle() + " with the id " + type.name() +
                " in region " + region.getId() + " on world " + world.getName());
    }
}
