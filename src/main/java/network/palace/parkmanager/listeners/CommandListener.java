package network.palace.parkmanager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * The {@code CommandListener} class is responsible for handling server command events for specific
 * command sources, such as command blocks. It ensures that certain command patterns are recognized
 * and transformed into updated commands before being executed.
 *
 * <p>This class listens for the {@link ServerCommandEvent} and performs the following actions:</p>
 * <ul>
 *     <li>Checks if the command sender is a {@code BlockCommandSender}.</li>
 *     <li>Validates if the command issued from the command block starts with the prefix "magic rc add".</li>
 *     <li>Transforms the command by replacing the initial phrase and updates the block's command storage.</li>
 *     <li>Executes the updated command through the Bukkit dispatcher.</li>
 * </ul>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *     <li>Ensures handling of commands only from command blocks.</li>
 *     <li>Provides dynamic command transformation to adapt to prefix changes.</li>
 *     <li>Updates the stored command in the command block state to persist changes.</li>
 *     <li>Executes the transformed command programmatically after updates.</li>
 * </ul>
 *
 * <h2>Implementation Details:</h2>
 * <ul>
 *     <li>Uses Bukkit's {@code ServerCommandEvent} to capture server-side command executions.</li>
 *     <li>Checks the sender type to ensure that actions are applied exclusively to command blocks.</li>
 *     <li>The transformation logic ensures that only commands matching specific criteria are altered.</li>
 *     <li>Updates the command block state using {@code setCommand} and {@code update} methods.</li>
 * </ul>
 *
 * <p>This class provides a focused utility for developers looking to manage or adapt command block
 * behaviors programmatically within the server.</p>
 */
public class CommandListener implements Listener {

    /**
     * Handles the {@link ServerCommandEvent} to process and transform specific commands issued by
     * command blocks. This method ensures that commands starting with the prefix "magic rc add" are
     * modified and executed programmatically.
     *
     * <p>The method performs the following actions:</p>
     * <ul>
     *     <li>Identifies if the sender of the command is a {@link BlockCommandSender}.</li>
     *     <li>Checks if the command issued by the command block begins with the specific prefix "magic rc add".</li>
     *     <li>Transforms the command by replacing the recognized prefix with "rc".</li>
     *     <li>Updates the command stored in the block's state to persist the change.</li>
     *     <li>Executes the updated command using Bukkit's command dispatch system.</li>
     * </ul>
     *
     * @param event the {@link ServerCommandEvent} that represents the command issued within the server.
     *              It includes details such as the sender and the raw command string.
     */
    @EventHandler
    public void onCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();
        // only fix if command block
        if (!(sender instanceof BlockCommandSender)) return;
        CommandBlock block = (CommandBlock) ((BlockCommandSender) sender).getBlock().getState();
        String cmd = block.getCommand();
        if (cmd.toLowerCase().startsWith("magic rc add ")) {
            cmd = "rc " + cmd.substring(13);
            block.setCommand(cmd);
            block.update();
            Bukkit.dispatchCommand(sender, cmd);
        }
    }
}
