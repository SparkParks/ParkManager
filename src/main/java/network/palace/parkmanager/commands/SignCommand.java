package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * <p>The <code>SignCommand</code> class is responsible for handling the "sign" command in the system.
 * This command allows a player to sign an autograph book with a custom message.
 * The command can only be executed by players of the <code>VIP</code> rank or higher.</p>
 *
 * <p><b>Command Details:</b></p>
 * <ul>
 *   <li><b>Name:</b> sign</li>
 *   <li><b>Description:</b> Sign an autograph book</li>
 *   <li><b>Aliases:</b> s</li>
 *   <li><b>Required Rank:</b> VIP</li>
 * </ul>
 *
 * <p>When executed, this command collects the arguments provided by the player as the signature message,
 * concatenates them into a single string, and processes the signing task asynchronously.</p>
 *
 * <p>Upon execution, a confirmation message is sent to the player, and the autograph manager is used
 * for the actual signing process.</p>
 */
@CommandMeta(description = "Sign an autograph book", aliases = "s", rank = Rank.VIP)
public class SignCommand extends CoreCommand {

    /**
     * Constructs a new instance of the <code>SignCommand</code> class, which registers
     * the "sign" command to the system.
     *
     * <p>The "sign" command allows players of the required rank to sign an autograph
     * book with a custom message, which is handled asynchronously when the command
     * is executed.</p>
     *
     * <p>This constructor invokes the parent class constructor, registering the
     * command with the name "sign". It is primarily responsible for initializing the
     * base functionality of the command structure within the command framework.</p>
     *
     * <ul>
     *   <li><b>Command Name:</b> sign</li>
     * </ul>
     */
    public SignCommand() {
        super("sign");
    }

    /**
     * Handles the "sign" command, allowing a player to sign an autograph book
     * with a custom message. The provided arguments are concatenated into a
     * single string, and the signing task is executed asynchronously.
     *
     * <p>This method ensures the player receives a confirmation message before
     * the signing operation is initiated.</p>
     *
     * @param player the player executing the command. This represents the sender of the command
     *               and the entity responsible for signing the autograph book.
     * @param args   the arguments provided by the player as the autograph message. Each element
     *               in the array represents a part of the message, which is concatenated into
     *               a complete single-line string.
     * @throws CommandException if an error occurs while processing the command,
     *                          including insufficient permissions or any unexpected issues.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            msg.append(args[i]);
            if (i < (args.length - 1)) {
                msg.append(" ");
            }
        }
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            player.sendMessage(ChatColor.GREEN + "Signing book...");
            ParkManager.getAutographManager().sign(player, msg.toString());
        });
    }
}
