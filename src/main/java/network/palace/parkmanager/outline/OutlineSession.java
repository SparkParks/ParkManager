package network.palace.parkmanager.outline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.UUID;

/**
 * Represents a session that manages the creation and manipulation of outlines in the Minecraft world,
 * associated with a specific player identified by a unique UUID.
 * <p>
 * This class provides functionality to outline in a specified direction and undo the last
 * modification of blocks within the session. It uses a designated block type for outlining
 * and keeps track of previous block data for undo operations.
 * <p>
 * The outline manipulation is dependent on player-specific location and involves
 * workspace coordinates calculated using a given `Point` and directional parameters.
 *
 * <p><b>Key features:</b></p>
 * <ul>
 *   <li>Allows outlining blocks using specified parameters such as distance and angle.</li>
 *   <li>Maintains a history of the last block edit to support undo functionality.</li>
 *   <li>Uses a default material for outlining blocks with the ability to update the type of
 *       block being used.</li>
 * </ul>
 */
@Getter
@Setter
@RequiredArgsConstructor
@SuppressWarnings("deprecation")
public class OutlineSession {
    /**
     * A unique identifier for the session, represented as a {@link UUID}.
     * <p>
     * This UUID is used to uniquely associate the outline session with a specific player
     * in the Minecraft world. It serves as the primary method for identifying the session owner
     * and linking player-specific operations such as outlining and undoing blocks to the appropriate context.
     * <p>
     * The inclusion of this field ensures that each session is tied to a distinct player, allowing for
     * proper isolation and management of session-specific operations in a multiplayer environment.
     */
    private final UUID uuid;

    /**
     * Represents a point in the 2D coordinate system used within the session for outlining operations.
     * <p>
     * This variable stores the current session-specific coordinates and acts as the reference point
     * for all block outlining operations performed by the session. It holds both the horizontal
     * (x, z) position and is initialized as <code>null</code> when no point has been selected or
     * assigned yet in the session.
     *
     * <p><b>Characteristics:</b></p>
     * <ul>
     *   <li>Tracks the primary reference location for outline operations.</li>
     *   <li>Acts as the origin point for calculating positions based on direction and distance.</li>
     *   <li>Can be updated dynamically to shift the starting position for future operations.</li>
     *   <li>Stores an instance of {@link Point}, including its name and coordinates.</li>
     * </ul>
     *
     * <p>Set by the outlining logic and used as the basis for determining workspace
     * coordinates and directional calculations.</p>
     */
    private Point sessionPoint = null;

    /**
     * Specifies the material type to be used for outlining blocks in the session.
     * <p>
     * The default value is <code>Material.GOLD_BLOCK</code>. This material will be
     * used when invoking methods that modify the Minecraft world, such as creating
     * outlines or modifying block properties.
     * <p>
     * <b>Key Characteristics:</b>
     * <ul>
     *   <li>Represents the type of block material utilized for outlining actions.</li>
     *   <li>Can be updated or modified during the session, allowing dynamic changes to the block type.</li>
     *   <li>Used whenever blocks are set or manipulated by the session's methods.</li>
     * </ul>
     * <p>
     * This field is integral to the behavior of the {@link OutlineSession} class,
     * ensuring consistent and predictable modifications to the Minecraft world.
     */
    private Material type = Material.GOLD_BLOCK;

    /**
     * Stores the location of the last block modification made by the {@link OutlineSession#outline(double, double)}
     * method. This location is used by the {@link OutlineSession#undo()} method to revert the last block change.
     *
     * <p><b>Details:</b></p>
     * <ul>
     *   <li>Represents the {@link Location} where the last block was altered during an outline operation.</li>
     *   <li>If no outline operation has occurred, or the undo state has been reset, this value will be {@code null}.</li>
     *   <li>Used in conjunction with {@code undoType} and {@code undoData} to accurately revert modifications.</li>
     * </ul>
     *
     * <p>This field is set in the {@link OutlineSession#outline(double, double)} method and referenced in the
     * {@link OutlineSession#undo()} method for undo functionality.</p>
     */
    private Location undoLocation = null;

    /**
     * Stores the material type of the last modified block during the outlining operation.
     * <p>
     * This variable is used specifically for undo functionality within the {@link OutlineSession}
     * to restore the original block type after a modification. When an outline operation
     * is performed, the block's current material is saved in this variable before being
     * replaced with the outlining material.
     * </p>
     *
     * <p><b>Details:</b></p>
     * <ul>
     *   <li>Set during an outline operation to keep track of the previously existing block material.</li>
     *   <li>Used in conjunction with {@link OutlineSession#undoLocation} and {@link OutlineSession#undoData}
     *       to revert the last modification performed in the outline session.</li>
     *   <li>Defaults to <code>null</code> if no prior operations have been executed or if an undo
     *       operation resets the session state.</li>
     * </ul>
     */
    private Material undoType = null;

    /**
     * Represents the data value associated with the last modified block during an outlining operation
     * in a player's session.
     *
     * <p>This variable is used in conjunction with the <code>undo</code> function to ensure that the
     * block's metadata is restored to its original state when undoing a modification. This typically
     * includes specific block properties, such as orientation or other block-specific attributes,
     * represented as a byte.</p>
     *
     * <p><b>Usage context:</b></p>
     * <ul>
     *   <li>Stores the metadata of the block before it was overwritten during an outline operation.</li>
     *   <li>Enables restoration of the original block state, alongside <code>undoType</code>, when
     *       an undo operation is performed.</li>
     *   <li>Works in tandem with the <code>undoLocation</code> and <code>undoType</code> variables
     *       to ensure accuracy in reverting changes.</li>
     * </ul>
     *
     * <p><b>Value range:</b> 0 to 15, since it corresponds to Minecraft's block data values.</p>
     */
    private byte undoData = 0;

    /**
     * Creates an outline at a specified distance and heading from the player's current position.
     * <p>
     * The method calculates a new location based on the given length and heading, relative to the
     * session's specified `Point`. It sets the block at the calculated location to the session's
     * defined block type, while storing the previous block's type and data for potential undo operations.
     * <p>
     * If the player is not found or the session point is null, the method returns `null`.
     *
     * @param length  the distance from the session point to the target location, in blocks.
     * @param heading the direction to the target location, in degrees, where 0 points north.
     *                Positive values increase clockwise (e.g., 90 is east, 180 is south, 270 is west).
     * @return the newly outlined {@link Location}, or {@code null} if the player or session point is unavailable.
     */
    public Location outline(double length, double heading) {
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player == null || sessionPoint == null) return null;

        int y = player.getLocation().getBlockY();
        double radAngle = heading / 180.0D * Math.PI;
        int x = sessionPoint.getX() + (int) Math.round(length * Math.sin(radAngle));
        int z = sessionPoint.getZ() - (int) Math.round(length * Math.cos(radAngle));
        Location loc = new Location(player.getWorld(), x, y, z);

        Block b = loc.getBlock();
        undoLocation = loc.clone();
        undoType = b.getType();
        undoData = b.getData();
        b.setType(type);

        return loc;
    }

    /**
     * Reverts the last modification made to a block within the outline session.
     * <p>
     * The method restores the block at the stored undo location to its previous type and data,
     * effectively "undoing" the last block edit. After the operation, the current block type
     * and data are stored for potential future undo actions.
     * </p>
     *
     * <p><b>Conditions for Success:</b></p>
     * <ul>
     *   <li>The undo location must not be {@code null}.</li>
     *   <li>The undo type must not be {@code null}.</li>
     * </ul>
     *
     * <p><b>Side Effects:</b></p>
     * <ul>
     *   <li>The block at the undo location is reverted to its previous type and data.</li>
     *   <li>The current block type and data are stored for potential further undo operations.</li>
     * </ul>
     *
     * @return {@code true} if the undo operation successfully restores the block to its previous
     *         state; {@code false} if the undo operation fails due to missing data (e.g., {@code undoLocation} or {@code undoType} is {@code null}).
     */
    public boolean undo() {
        if (undoLocation == null || undoType == null) return false;
        Block b = undoLocation.getBlock();
        final Material preType = b.getType();
        final byte preData = b.getData();
        b.setType(undoType);
        b.setData(undoData);
        undoType = preType;
        undoData = preData;
        return true;
    }
}
