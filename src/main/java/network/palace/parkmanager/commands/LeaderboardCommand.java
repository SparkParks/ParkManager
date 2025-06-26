package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.leaderboard.LeaderboardManager;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code LeaderboardCommand} class is responsible for managing and displaying leaderboard rankings
 * related to ride counters. This command allows developers to interact with ride leaderboards and
 * retrieve statistical data asynchronously. It includes functionalities such as updating leaderboard
 * data and retrieving top-ranked users for specific rides.
 *
 * <p><strong><b>Usage:</b></strong></p>
 * <p>The command provides handlers for different arguments:</p>
 * <ul>
 *   <li><code>/leaderboard [update]</code>: Updates the ride leaderboards asynchronously.</li>
 *   <li><code>/leaderboard [top #] [ride]</code>: Retrieves the leaderboard data for a given ride
 *   with the specified number of top entries.</li>
 * </ul>
 *
 * <p><strong><b>Error Handling:</b></strong></p>
 * <ul>
 *   <li>If no arguments are provided or if the sender is an instance of {@link BlockCommandSender},
 *       the command shows the valid usage options.</li>
 *   <li>If the argument <code>#</code> is not a valid number, an error message is displayed.</li>
 *   <li>If an unsupported argument structure is provided, the command prompts for the expected format.</li>
 * </ul>
 *
 * <p><strong><b>Key Functionalities:</b></strong></p>
 * <ul>
 *   <li>Asynchronous tasks are used to update leaderboard data or fetch leaderboard details without
 *       blocking the main thread.</li>
 *   <li>Formatting of leaderboard entries is handled by the {@code LeaderboardManager} utility class.</li>
 * </ul>
 *
 * <p>This command is intended for use by developers with sufficient rank, as specified in the
 * {@link Rank#DEVELOPER} annotation.
 */
@CommandMeta(description = "Get top ride leaderboards", rank = Rank.DEVELOPER)
public class LeaderboardCommand extends CoreCommand {

    /**
     * Constructs an instance of the {@code LeaderboardCommand}.
     * <p>
     * This command is typically used to retrieve and display the leaderboard,
     * showing rankings or statistics related to players or participants.
     * </p>
     * <p>
     * The command keyword for triggering this command is "leaderboard".
     * </p>
     * <p>
     * This constructor initializes the command and sets its associated keyword.
     * </p>
     */
    public LeaderboardCommand() {
        super("leaderboard");
    }

    /**
     * Handles unspecific leaderboard command inputs provided by the command sender.
     * <p>
     * This method processes various leaderboard-related commands such as updating the leaderboard or
     * fetching and displaying the top rankings for a specified ride. Invalid commands or insufficient
     * arguments are appropriately handled by providing usage instructions or error responses.
     * </p>
     *
     * @param sender The sender of the command, which could be a player, console, or command block.
     *               If the sender is a BlockCommandSender, the command will return usage instructions.
     * @param args   The arguments provided with the command. Valid inputs can include:
     *               <ul>
     *                  <li>An "update" command to refresh the ride counter leaderboards.</li>
     *                  <li>Arguments specifying the top number of players and the ride name to display leaderboards.</li>
     *               </ul>
     * @throws CommandException If an error occurs while processing the command.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0 || sender instanceof BlockCommandSender) {
            sender.sendMessage(ChatColor.RED + "/leaderboard [update]");
            sender.sendMessage(ChatColor.RED + "/leaderboard [top #] [ride]");
            return;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("update")) {
                sender.sendMessage(ChatColor.GREEN + "Updating Ride Counter Leaderboards...");
                Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                    ParkManager.getLeaderboardManager().update();
                    sender.sendMessage(ChatColor.GREEN + "Leaderboards updated!");
                });
                return;
            }
            sender.sendMessage(ChatColor.RED + "/leaderboard [update]");
            sender.sendMessage(ChatColor.RED + "/leaderboard [top #] [ride]");
            return;
        }
        if (args.length > 1) {
            int top;
            try {
                top = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a number!");
                return;
            }
            StringBuilder name = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                name.append(args[i]).append(" ");
            }
            String rideName = name.toString().trim();
            sender.sendMessage(ChatColor.AQUA + "Gathering leaderboard data for " + rideName + "...");
            Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {

                List<Document> list = Core.getMongoHandler().getRideCounterLeaderboard(rideName, top);

                List<String> messages = new ArrayList<>();
                for (Document doc : list) {
                    messages.add(ChatColor.BLUE + LeaderboardManager.getFormattedName(doc));
                }
                LeaderboardManager.sortLeaderboardMessages(messages);

                sender.sendMessage(ChatColor.BLUE + "Ride Counter Leaderboard for " + ChatColor.GOLD + rideName + ":");
                messages.forEach(sender::sendMessage);
            });
        }
    }
}
