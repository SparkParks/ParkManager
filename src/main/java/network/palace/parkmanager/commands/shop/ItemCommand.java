package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.commands.shop.item.AddCommand;
import network.palace.parkmanager.commands.shop.item.ListCommand;
import network.palace.parkmanager.commands.shop.item.RemoveCommand;

/**
 * Represents the main command for managing shop items in the game.
 *
 * <p>This command is a parent command that delegates functionality to its subcommands.
 * The following subcommands are registered:
 * <ul>
 *     <li><b>AddCommand</b>: Allows adding a new shop item to a shop.</li>
 *     <li><b>ListCommand</b>: Lists all items within a particular shop.</li>
 *     <li><b>RemoveCommand</b>: Removes a specific item from a shop.</li>
 * </ul>
 * </p>
 *
 * <p>The command operates exclusively through its subcommands and does not implement
 * functionality directly.</p>
 */
@CommandMeta(description = "Manage shop items")
public class ItemCommand extends CoreCommand {

    /**
     * Constructs a new {@code ItemCommand} instance.
     *
     * <p>The {@code ItemCommand} serves as the primary command for managing shop items
     * in the game. It implements functionality by registering and delegating to the following
     * subcommands:
     * <ul>
     *   <li><b>AddCommand</b>: Adds a new item to a specified shop.</li>
     *   <li><b>ListCommand</b>: Lists all items in a specified shop.</li>
     *   <li><b>RemoveCommand</b>: Removes an item from a specified shop.</li>
     * </ul>
     * </p>
     *
     * <p>By design, this command only handles subcommands, thus its own functionality
     * is limited to acting as a container for its operational components.</p>
     *
     * <p>This constructor initializes the command with the identifier {@code "item"}
     * and registers its subcommands.</p>
     */
    public ItemCommand() {
        super("item");
        registerSubCommand(new AddCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new RemoveCommand());
    }

    /**
     * Determines whether the command operates exclusively using subcommands.
     *
     * <p>This implementation returns <code>true</code>, indicating that the command
     * does not perform any functionality directly and instead delegates all
     * operations to its registered subcommands.</p>
     *
     * @return <code>true</code> if the command uses only subcommands;
     *         <code>false</code> otherwise.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
