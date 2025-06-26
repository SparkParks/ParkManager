package network.palace.parkmanager.commands.outfits;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * Represents the command functionality for removing a specific outfit.
 * This command allows users to remove outfits by providing their unique ID.
 * <p>
 * The command performs the following tasks:
 * <ul>
 *     <li>Validates the input arguments to ensure they are provided and represent a valid integer.</li>
 *     <li>Performs the deletion of the outfit asynchronously from the database.</li>
 *     <li>Notifies the player of the successful removal of the specified outfit.</li>
 * </ul>
 * <p>
 * If the input is invalid or insufficient, appropriate error messages are displayed to the user.
 *
 * <p><b>Command Syntax:</b></p>
 * <pre>
 * /outfit remove [id]
 * </pre>
 * <p>
 * Where <b>[id]</b> is the unique identifier of the outfit to be removed.
 *
 * <p><b>Implementation Details:</b></p>
 * <ul>
 *     <li>Validates the ID to ensure it's a valid integer.</li>
 *     <li>Uses asynchronous execution for database operations to prevent blocking the main thread.</li>
 *     <li>Updates to the outfit list on the server may require executing '/outfit reload'.</li>
 * </ul>
 */
@CommandMeta(description = "Remove an outfit")
public class RemoveCommand extends CoreCommand {

    /**
     * Constructs a new <code>RemoveCommand</code> instance.
     *
     * <p>
     * This constructor initializes the <code>RemoveCommand</code> with the identifier <code>"remove"</code>,
     * enabling users to invoke the command for removing a specific outfit from the system. The command is
     * designed to work with the subcommand structure of the outfit management system.
     * </p>
     *
     * <h3>Purpose</h3>
     * <p>
     * The <code>RemoveCommand</code> is part of the outfit management system, allowing players to remove
     * specific outfits by providing a unique identifier. It validates the input, processes the removal
     * asynchronously, and provides feedback to the player regarding the success or failure of the operation.
     * </p>
     *
     * <h3>Command Integration</h3>
     * <p>
     * This class is part of the subcommand system tied to outfit management functionality. It communicates
     * with the database to delete outfit records and ensures that the server remains responsive by performing
     * operations asynchronously.
     * </p>
     */
    public RemoveCommand() {
        super("remove");
    }

    /**
     * Handles the `/outfit remove` command, allowing a player to remove a specific outfit
     * by its unique ID. The method validates the input, executes the removal operation
     * asynchronously, and provides appropriate feedback to the player.
     *
     * @param player The {@link CPlayer} executing the command. Represents the player issuing the command.
     * @param args   The arguments provided by the player for this command. The first argument should
     *               be the outfit ID, which must be a valid integer.
     * @throws CommandException Thrown if there are issues executing the command, such as permission
     *                          restrictions or unexpected errors.
     * <p>
     * Key functionality:
     * <ul>
     *     <li>Validates that an ID is provided and is a valid integer.</li>
     *     <li>Performs an asynchronous removal of the outfit associated with the specified ID from the database.</li>
     *     <li>Sends appropriate success or error messages to the player based on the operation result.</li>
     * </ul>
     *
     * <p>Example command usage (not included in execution logic):</p>
     * <pre>
     * /outfit remove [id]
     * </pre>
     * Where <b>[id]</b> is the integer identifier of the outfit to remove.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/outfit remove [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the outfit id from /outfit list!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            player.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
            return;
        }
        int id = Integer.parseInt(args[0]);
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().deleteOutfit(id));
        player.sendMessage(ChatColor.GREEN + "Successfully removed that outfit! Reload with '/outfit reload' to update this server.");
    }
}
