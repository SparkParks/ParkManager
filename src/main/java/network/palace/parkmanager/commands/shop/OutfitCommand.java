package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.commands.shop.outfit.AddCommand;
import network.palace.parkmanager.commands.shop.outfit.ListCommand;
import network.palace.parkmanager.commands.shop.outfit.RemoveCommand;

/**
 * Represents the main command for managing shop outfits in the game.
 *
 * <p>This command serves as a parent command and delegates functionality
 * to its subcommands. The following subcommands are registered:
 * <ul>
 *     <li><b>AddCommand</b>: Adds a new outfit to a shop.</li>
 *     <li><b>ListCommand</b>: Lists all outfits within a particular shop.</li>
 *     <li><b>RemoveCommand</b>: Removes a specific outfit from a shop.</li>
 * </ul>
 * </p>
 *
 * <p>The command operates exclusively through its subcommands and does not
 * implement functionality directly.</p>
 */
@CommandMeta(description = "Manage shop outfits")
public class OutfitCommand extends CoreCommand {

    /**
     * The default constructor for the <code>OutfitCommand</code> class.
     *
     * <p>This constructor initializes the "outfit" command and registers its
     * subcommands to handle specific functionalities related to shop outfit management.
     * The subcommands include:</p>
     * <ul>
     *     <li><b>AddCommand</b>: Provides the ability to add a new outfit to a shop.</li>
     *     <li><b>ListCommand</b>: Lists all outfits within a specified shop.</li>
     *     <li><b>RemoveCommand</b>: Removes a specified outfit from a shop.</li>
     * </ul>
     *
     * <p>Each subcommand handles the corresponding functionality, ensuring modularity
     * and clear separation of responsibilities. The parent <code>OutfitCommand</code>
     * utilizes these subcommands exclusively and delegates all functionality to them.</p>
     */
    public OutfitCommand() {
        super("outfit");
        registerSubCommand(new AddCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new RemoveCommand());
    }

    /**
     * Indicates whether this command operates solely using subcommands.
     *
     * <p>This method is used to specify if the functionality of the parent command
     * is entirely delegated to its subcommands. When the method returns <code>true</code>,
     * it implies that the parent command itself does not implement any direct functionality
     * and exists solely as a container for its subcommands.</p>
     *
     * <p>In this implementation, the method unequivocally returns <code>true</code>,
     * confirming that this command exclusively utilizes subcommands for all operations.</p>
     *
     * @return <code>true</code> if the command relies solely on subcommands;
     *         <code>false</code> otherwise.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
