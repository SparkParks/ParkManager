package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.outfits.CreateCommand;
import network.palace.parkmanager.commands.outfits.ListCommand;
import network.palace.parkmanager.commands.outfits.ReloadCommand;
import network.palace.parkmanager.commands.outfits.RemoveCommand;

/**
 * Represents the main command for managing outfits within the application.
 * <p>
 * The <code>OutfitCommand</code> class serves as the parent command and provides
 * functionality to manage subcommands related to outfits, such as creating, listing, reloading,
 * and removing outfits. This command is exclusively subcommand-driven.
 * </p>
 *
 * <p>
 * <b>Subcommands include:</b>
 * <ul>
 *   <li><b>CreateCommand:</b> Enables the creation of new outfits by users.</li>
 *   <li><b>ListCommand:</b> Lists all existing outfits available to the user.</li>
 *   <li><b>ReloadCommand:</b> Reloads outfits from the database to ensure up-to-date data.</li>
 *   <li><b>RemoveCommand:</b> Removes an outfit by its identifier.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This command leverages the {@link CoreCommand} framework and sets {@link #isUsingSubCommandsOnly()}
 * to <code>true</code>, indicating that it is strictly subcommand-based.
 * </p>
 */
@CommandMeta(description = "Outfit command", rank = Rank.CM)
public class OutfitCommand extends CoreCommand {

    /**
     * Constructs a new <code>OutfitCommand</code> instance and registers all related subcommands.
     * <p>
     * This constructor initializes the command with the identifier <code>"outfit"</code> and adds the following subcommands:
     * </p>
     * <ul>
     *   <li><b>CreateCommand:</b> Allows users to create new outfits.</li>
     *   <li><b>ListCommand:</b> Lists all existing outfits available to the user.</li>
     *   <li><b>ReloadCommand:</b> Reloads outfits from the database to ensure the application has the latest information.</li>
     *   <li><b>RemoveCommand:</b> Enables users to remove an outfit by specifying its identifier.</li>
     * </ul>
     * <p>
     * Each subcommand handles a distinct functionality related to outfit management, leveraging the core command framework.
     * </p>
     */
    public OutfitCommand() {
        super("outfit");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new RemoveCommand());
    }

    /**
     * Determines whether this command exclusively utilizes subcommands.
     *
     * <p>
     * This method enforces that the command cannot function as a standalone command,
     * but instead only operates through its defined subcommands. This is particularly
     * useful in commands that serve as containers for modular functionality
     * divided into subcommands.
     * </p>
     *
     * @return <code>true</code> if the command is strictly subcommand-based; <code>false</code> otherwise.
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
