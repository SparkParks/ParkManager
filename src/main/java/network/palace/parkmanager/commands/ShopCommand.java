package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.shop.ItemCommand;
import network.palace.parkmanager.commands.shop.OutfitCommand;
import network.palace.parkmanager.commands.shop.*;

/**
 * The {@code ShopCommand} class is the main command handler for managing shops within the application.
 *
 * <p>
 * This command allows users to interact with a variety of shop-related functionalities through its subcommands.
 * The command is structured to handle only subcommands, providing a modular and expandable framework for shop-related operations.
 * The primary functionality is wrapped under these subcommands:
 * </p>
 *
 * <ul>
 *   <li><b>CreateCommand:</b> Used to create a new shop, specifying its ID, warp location, and name.</li>
 *   <li><b>ListCommand:</b> Lists all shops belonging to the park the user is currently in.</li>
 *   <li><b>ReloadCommand:</b> Reloads all shop configurations from the filesystem.</li>
 *   <li><b>RemoveCommand:</b> Removes an existing shop by specifying its ID.</li>
 *   <li><b>ItemCommand:</b> Manages shop items, supporting further subcommands to add, list, or remove items.</li>
 *   <li><b>OutfitCommand:</b> Manages shop outfits, supporting further subcommands to add, list, or remove outfits.</li>
 * </ul>
 *
 * <p>
 * This command is part of a larger command infrastructure and is designed to adhere to the modular,
 * hierarchical structure of a command framework.
 * </p>
 *
 * <p>
 * The {@code ShopCommand} is annotated with metadata such as description and rank requirements using the {@code @CommandMeta} annotation.
 * It ensures that the command and its subcommands are only accessible to users with the required rank.
 * </p>
 */
@CommandMeta(description = "Shop command", rank = Rank.CM)
public class ShopCommand extends CoreCommand {

    /**
     * Constructs the {@code ShopCommand} and sets up all its subcommands.
     *
     * <p>
     * The {@code ShopCommand} serves as the primary command for handling shop-related functionalities within the application.
     * It is built using a modular design, with each subcommand dedicated to specific shop operations. This constructor
     * initializes the command with its associated subcommands, as described below:
     * </p>
     *
     * <ul>
     *   <li><b>CreateCommand:</b> Allows the creation of new shops by specifying an ID, warp location, and shop name.</li>
     *   <li><b>ListCommand:</b> Displays a list of all shops available in the player's current park.</li>
     *   <li><b>ReloadCommand:</b> Reloads the shop data from the filesystem, allowing for configuration updates without requiring a restart.</li>
     *   <li><b>RemoveCommand:</b> Enables the removal of an existing shop, identified by its unique ID.</li>
     *   <li><b>ItemCommand:</b> Manages the items available in a shop. It offers subcommands to add, list, and remove items.</li>
     *   <li><b>OutfitCommand:</b> Handles shop outfits, providing further subcommands to add, list, and remove outfits.</li>
     * </ul>
     *
     * <p>
     * This constructor assigns "shop" as the main command's identifier and registers the subcommands.
     * Each subcommand is specifically implemented to encapsulate related functionalities, enhancing the modularity and
     * maintainability of the overall command structure.
     * </p>
     */
    public ShopCommand() {
        super("shop");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new RemoveCommand());
        registerSubCommand(new ItemCommand());
        registerSubCommand(new OutfitCommand());
    }

    /**
     * Determines if the command is designed to operate using subcommands only.
     *
     * <p>
     * This method is a part of the command framework and specifies whether the command
     * allows only the execution of its registered subcommands, without any additional
     * standalone logic. For this implementation, the method always returns {@code true},
     * indicating that the command does not have standalone functionality and is entirely
     * reliant on its subcommands for its operations.
     * </p>
     *
     * @return {@code true} if the command exclusively uses subcommands; otherwise {@code false}.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
