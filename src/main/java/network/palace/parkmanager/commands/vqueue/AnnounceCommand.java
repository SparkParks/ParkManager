package network.palace.parkmanager.commands.vqueue;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.ComponentSerializer;
import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.messagequeue.packets.BroadcastComponentPacket;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Provides the ability to announce a virtual queue hosted on the server.
 * <p>
 * The AnnounceCommand allows authorized senders to broadcast a clickable announcement
 * for a specific virtual queue. This announcement will be visible to all players connected
 * to the server network, and others can click on the announcement to join the queue.
 * </p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Broadcasts announcements with customization support.</li>
 *   <li>Creates clickable announcements allowing players to join the queue directly.</li>
 *   <li>Supports multiple command sender types such as players, command blocks, and console.</li>
 * </ul>
 *
 * <p><b>Command Syntax:</b></p>
 * <ul>
 *   <li><code>/vqueue announce [id] [Announcement Text]</code> - Announces the specified queue identified by <code>[id]</code>
 *       along with an optional <code>[Announcement Text]</code> to all servers.</li>
 * </ul>
 *
 * <p><b>Behavior:</b></p>
 * <ul>
 *   <li>Ensures the specified queue exists and is hosted on the current server before broadcasting an announcement.</li>
 *   <li>Supports dynamic sender identification, including player, console, and command block senders.</li>
 *   <li>Sends error messages to the sender if validation or broadcasting fails.</li>
 *   <li>Handles message building and formatting, ensuring compatibility with clickable and hoverable Minecraft chat components.</li>
 * </ul>
 *
 * <p><b>Error Handling:</b></p>
 * <ul>
 *   <li>If the queue ID is invalid or not found, an error message is displayed to the sender.</li>
 *   <li>Ensures announcements can only be made on the hosting server for the specified queue.</li>
 *   <li>Logs detailed errors to the console in case of packet sending issues.</li>
 * </ul>
 */
@CommandMeta(description = "Announce a virtual queue hosted on this server")
public class AnnounceCommand extends CoreCommand {

    /**
     * Constructor for the AnnounceCommand class.
     *
     * <p>This class is responsible for handling the "announce" command. It is meant
     * to integrate with the command-handling system of the application. By invoking
     * this constructor, a command instance is initialized and registered under the
     * name "announce".
     *
     * <p>Usage of this command is dependent on the specific implementation of its
     * {@code handleCommandUnspecific} method, which determines the behavior and
     * response when the command is executed.
     *
     * <p>Key characteristics:
     * <ul>
     *   <li>Extends functionality from its superclass.</li>
     *   <li>Specifically tailored for handling "announce" operations.</li>
     * </ul>
     */
    public AnnounceCommand() {
        super("announce");
    }

    /**
     * Handles the execution of an unspecified command scenario for announcing a virtual queue.
     * <p>
     * This method processes the announcement message for a specified queue, validates input arguments,
     * identifies the sender, and sends formatted announcements to all servers in the network.
     * The announcement allows players to directly join a virtual queue by interacting with the provided text.
     * </p>
     *
     * @param sender The {@link CommandSender} who issued the command. This can be a player,
     *               console, or a block command sender (e.g., Command Block).
     * @param args   An array of {@link String} arguments supplied to the command.
     *               The first argument should be the queue ID, and subsequent arguments compose the announcement text.
     *               If insufficient arguments are provided, a usage message will be sent to the sender.
     *
     * @throws CommandException If any errors occur during the execution of the command, such as
     *                          invalid queue ID or failure in broadcasting the announcement.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/vqueue announce [id] [Announcement Text]");
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /vqueue list!");
            sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Clicking the Announcement Text will let players join the queue!");
            return;
        }
        VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueueById(args[0]);
        if (queue == null) {
            sender.sendMessage(ChatColor.RED + "Could not find a queue by id " + args[0] + "!");
            return;
        }
        if (!queue.isHost()) {
            sender.sendMessage(ChatColor.RED + "You can only do that on the server hosting the queue (" +
                    ChatColor.GREEN + queue.getServer() + ChatColor.RED + ")!");
            return;
        }
        String senderName;
        if (!(sender instanceof Player)) {
            if (sender instanceof BlockCommandSender) {
                Location loc = ((BlockCommandSender) sender).getBlock().getLocation();
                senderName = "" + Core.getInstanceName() + ", CMDBLK @ " + loc.getWorld().getName().toLowerCase() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
            } else {
                senderName = "Console on " + Core.getInstanceName();
            }
        } else {
            senderName = sender.getName();
        }
        StringBuilder s = new StringBuilder(ChatColor.GREEN + "");
        for (int i = 1; i < args.length; i++) {
            s.append(args[i]).append(" ");
        }
        BaseComponent[] components = new ComponentBuilder("")
                .append(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', s.toString().trim())))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(ChatColor.GREEN + "Click to join the virtual queue " + queue.getName() + "!")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vqjoin " + queue.getId()))
                .create();
        BroadcastComponentPacket packet = new BroadcastComponentPacket(senderName, ComponentSerializer.toString(components));
        try {
            Core.getMessageHandler().sendMessage(packet, Core.getMessageHandler().ALL_PROXIES);
        } catch (IOException e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error sending queue announcement", e);
            sender.sendMessage(ChatColor.RED + "An error occurred while sending that virtual queue announcement, check console for details.");
        }
        sender.sendMessage(ChatColor.GREEN + "Your announcement has been sent!");
    }
}
