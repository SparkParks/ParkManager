package network.palace.parkmanager.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Resort;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.io.IOException;

/**
 * <p>Utility class for managing configuration settings related to a server environment.</p>
 *
 * <p>The {@code ConfigUtil} class is responsible for loading, saving, and providing access to
 * specific configuration settings stored in a JSON file. These settings determine various
 * properties and behaviors of the server, such as the spawn location, join message, and
 * server-specific preferences.</p>
 *
 * <p>The configuration is loaded from a dedicated subsystem named "config" using the
 * {@link FileUtil} API. If the subsystem or configuration file does not exist, it is
 * initialized with default values. Changes to configuration properties are persisted
 * back to the file automatically.</p>
 *
 * <p>Key features of this class:</p>
 * <ul>
 *   <li>Reads and writes configuration properties, including:
 *      <ul>
 *          <li>{@code spawn} - The default spawn location for players.</li>
 *          <li>{@code joinMessage} - A custom message displayed when a player joins.</li>
 *          <li>{@code spawnOnJoin} - Determines if players are teleported to spawn upon joining.</li>
 *          <li>{@code warpOnJoin} - Determines if a warp action is triggered upon joining.</li>
 *          <li>{@code resort} - A {@link Resort} enum representing the type of server (e.g., WDW, DLR, USO).</li>
 *      </ul>
 *   </li>
 *   <li>Supports automatic defaults if configuration properties are missing or invalid.</li>
 *   <li>Provides setters for properties that persist changes to the configuration file.</li>
 * </ul>
 *
 * <h3>Constructor Details:</h3>
 * <p>When an instance of {@code ConfigUtil} is created, it performs the following operations:</p>
 * <ul>
 *   <li>Initializes or retrieves the file subsystem named "config" using {@link FileUtil}.</li>
 *   <li>Loads the configuration file and parses relevant properties into fields.</li>
 *   <li>Applies default values if the configuration file is missing or incomplete.</li>
 *   <li>Persists any changes or default values back to the configuration file.</li>
 * </ul>
 *
 * <h3>Thread Safety:</h3>
 * <p>This class is not guaranteed to be thread-safe. Multiple threads modifying the configuration
 * simultaneously may result in inconsistent behavior. Appropriate synchronization or external
 * locking mechanisms are recommended if this class is accessed in a multithreaded context.</p>
 */
@Getter
public class ConfigUtil {
    /**
     * Represents the spawn location within the configuration utility.
     * <p>
     * The {@code spawn} field specifies a {@link Location} object that might be used
     * as the designated spawn point for players.
     * </p>
     *
     * <p><strong>Usage Context:</strong></p>
     * <ul>
     *     <li>Facilitates setting and retrieving the spawn location in the configuration.</li>
     *     <li>Used primarily for managing player spawn settings within the system.</li>
     * </ul>
     */
    private Location spawn;

    /**
     * Represents the join message that will be displayed when a player interacts with the system.
     * <p>
     * This variable is used to store a custom message that is displayed to players upon joining
     * a specific context or environment. It can be configured dynamically via the associated methods.
     * </p>
     *
     * <p><strong>Usage Examples:</strong></p>
     * <ul>
     *     <li>Setting a custom welcome message for players when they join a server or game.</li>
     *     <li>Retrieving the current join message for validation or modification.</li>
     * </ul>
     *
     * <p><strong>Behavior:</strong></p>
     * <ul>
     *     <li>This variable is typically updated via the associated setter method.</li>
     *     <li>The stored message will persist as part of the configuration utility.</li>
     * </ul>
     *
     * <p><strong>Note:</strong> Ensure to assign meaningful messages to enhance the user experience.</p>
     */
    private String joinMessage;

    /**
     * Indicates whether the player should be teleported to the spawn location upon joining the server.
     *
     * <p>This flag determines if the join event triggers an automatic teleportation to the predefined spawn
     * location. Useful for setups where a central spawn point is necessary for new or returning players.</p>
     *
     * <p><strong>Usage Considerations:</strong></p>
     * <ul>
     *     <li>If set to {@code true}, the player is teleported to the spawn location upon joining.</li>
     *     <li>If set to {@code false}, the player retains their previous location upon joining.</li>
     * </ul>
     */
    private boolean spawnOnJoin;

    /**
     * Represents whether a player should be automatically teleported to a pre-defined location
     * upon joining the game.
     *
     * <p>This variable is part of the configuration settings and determines if the warp functionality
     * is enabled to direct players to an assigned location when they join.</p>
     *
     * <p><strong>Usage:</strong></p>
     * <ul>
     *     <li>If set to {@code true}, players will be teleported to a specified warp location upon joining.</li>
     *     <li>If set to {@code false}, players will not be teleported upon joining.</li>
     * </ul>
     */
    private boolean warpOnJoin;

    /**
     * Represents a specific resort configuration setting for the application.
     *
     * <p>The {@code resort} field stores an instance of the {@link Resort} enum.
     * It defines the current resort setting, which is used throughout the application
     * to configure behaviors and resources specific to the selected resort.</p>
     *
     * <p>Key features of the {@code resort} field:</p>
     * <ul>
     *   <li>Stores the selected {@link Resort} enum constant, such as {@code WDW}, {@code DLR}, or {@code USO}.</li>
     *   <li>Aids in resolving resort-specific data or behavior, such as IDs, mappings, and associated settings.</li>
     *   <li>Can be used in conjunction with utility methods in {@link Resort} for validation or transformation.</li>
     * </ul>
     *
     * <p>The default value of this field depends on the initialization within the application context.</p>
     *
     * @see Resort
     */
    private Resort resort;

    /**
     * Constructs an instance of the {@code ConfigUtil} class while initializing and managing
     * the configuration settings for the application.
     *
     * <p>
     * The method performs the following tasks:
     * <ul>
     *     <li>Registers or retrieves a {@link FileUtil.FileSubsystem} named "config" to manage configuration files.</li>
     *     <li>Attempts to load the configuration data from the "config" JSON file within the subsystem.</li>
     *     <li>Parses the configuration for multiple settings, such as:
     *         <ul>
     *             <li><strong>Spawn location</strong>: Loaded from the "spawn" JSON object, defaults to {@code null} if not specified or the parsing fails.</li>
     *             <li><strong>Join message</strong>: Customizable welcome message loaded from "join-message". Defaults to a pre-defined message if not provided.</li>
     *             <li><strong>Spawn on join</strong>: Boolean property loaded from "spawn-on-join". Defaults to {@code false} if absent.</li>
     *             <li><strong>Warp on join</strong>: Boolean property loaded from "warp-on-join". Defaults to {@code true} if absent.</li>
     *             <li><strong>Resort</strong>: Specifies a resort setting (e.g., WDW). Defaults to {@link Resort#WDW} if not provided.</li>
     *         </ul>
     *     </li>
     *     <li>Saves the configuration back to the file system after loading.</li>
     *     <li>Logs messages to indicate successful or failed configuration loading.</li>
     * </ul>
     * </p>
     *
     * <p>
     * If the configuration file is missing, improperly formatted, or fails to load due to
     * an {@link IOException}, an error is logged, and the default values for the settings
     * are applied.
     * </p>
     *
     * <p><strong>Notes:</strong></p>
     * <ul>
     *     <li>The configuration file is expected to be a JSON file present in the "config" subsystem directory.</li>
     *     <li>Saving the configuration ensures that any missing or modified settings are updated in the file.</li>
     * </ul>
     *
     * <p><strong>Dependencies:</strong></p>
     * <ul>
     *     <li>{@link FileUtil}: For managing subsystems and loading JSON data.</li>
     *     <li>{@link Resort}: Enum representing the resort configuration type.</li>
     *     <li>{@link Core}: For logging messages and retrieving server types.</li>
     *     <li>{@link Bukkit}: For accessing {@link Location} details from JSON objects.</li>
     *     <li>{@link Gson}: Library for JSON parsing and serialization.</li>
     * </ul>
     *
     * <p><strong>Error Handling:</strong></p>
     * <ul>
     *     <li>If the "spawn" location JSON parsing fails, the value defaults to {@code null}.</li>
     *     <li>An {@link IOException} during the file loading process is caught and logged, and defaults are applied.</li>
     * </ul>
     */
    public ConfigUtil() {
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("config")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("config");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("config");
        }
        try {
            JsonElement element = subsystem.getFileContents("config");
            if (element.isJsonObject()) {
                JsonObject configObject = element.getAsJsonObject();
                try {
                    spawn = FileUtil.getLocation(configObject.getAsJsonObject("spawn"));
                } catch (Exception e) {
                    spawn = null;
                }
                if (configObject.has("join-message")) {
                    joinMessage = configObject.get("join-message").getAsString();
                } else {
                    joinMessage = ChatColor.GREEN + "Welcome to " + ChatColor.AQUA + "" + ChatColor.BOLD + Core.getServerType() + "!";
                }
                if (configObject.has("spawn-on-join")) {
                    spawnOnJoin = configObject.get("spawn-on-join").getAsBoolean();
                } else {
                    spawnOnJoin = false;
                }
                if (configObject.has("warp-on-join")) {
                    warpOnJoin = configObject.get("warp-on-join").getAsBoolean();
                } else {
                    warpOnJoin = true;
                }
                if (configObject.has("resort")) {
                    resort = Resort.fromString(configObject.get("resort").getAsString());
                } else {
                    resort = Resort.WDW;
                }
            }
            saveToFile();
            Core.logMessage("ConfigUtil", "Loaded config settings! This is a " + resort.name() + " server!");
        } catch (IOException e) {
            Core.logMessage("ConfigUtil", "There was an error loading the ConfigUtil config!");
            e.printStackTrace();
        }
    }

    /**
     * Sets the spawn location for the configuration and saves the changes to a file.
     * The provided location will be stored and made persistent.
     *
     * @param loc the {@link Location} object representing the new spawn location.
     */
    public void setSpawn(Location loc) {
        this.spawn = loc;
        saveToFile();
    }

    /**
     * Sets the join message for the configuration and saves the changes to the file.
     *
     * <p>The join message is displayed to users when they join the server,
     * and this method updates its value in both memory and the configuration file.</p>
     *
     * @param msg the join message to be set, represented as a {@code String}.
     *            It should describe the message displayed upon a player's joining.
     */
    public void setJoinMessage(String msg) {
        this.joinMessage = msg;
        saveToFile();
    }

    /**
     * Sets whether players should spawn at a designated location upon joining the server.
     * <p>
     * Enabling this feature ensures that players are teleported to a specific spawn point
     * set by the server when they log in. The updated configuration is automatically saved
     * to the configuration file.
     * </p>
     *
     * @param b true to enable spawning at the set spawn location on join, false to disable it.
     */
    public void setSpawnOnJoin(boolean b) {
        this.spawnOnJoin = b;
        saveToFile();
    }

    /**
     * Sets whether the player should be warped to a predefined location upon joining the game.
     * <p>
     * This method updates the internal configuration for the warp-on-join behavior,
     * which determines if players are automatically teleported to a specific location
     * when they join the server. The updated configuration is saved to the appropriate
     * file after modification.
     * </p>
     *
     * @param b {@code true} to enable the warp-on-join behavior, {@code false} to disable it.
     */
    public void setWarpOnJoin(boolean b) {
        this.warpOnJoin = b;
        saveToFile();
    }

    /**
     * Saves configuration data to a file in JSON format.
     * <p>
     * The method constructs a JSON object containing the following fields:
     * <ul>
     *   <li><strong>spawn:</strong> Contains serialized location data of the spawn point using {@link FileUtil#getJson(Location)}.</li>
     *   <li><strong>join-message:</strong> Contains a string representing the message shown to players upon joining.</li>
     *   <li><strong>spawn-on-join:</strong> A boolean value indicating whether players are teleported to the spawn point on joining.</li>
     *   <li><strong>warp-on-join:</strong> A boolean value indicating whether a warp action is performed on player join.</li>
     *   <li><strong>resort:</strong> Contains the name of the resort as a string.</li>
     * </ul>
     * </p>
     * <p>
     * The resultant JSON data is written to a subsystem file named "config"
     * through the {@link FileUtil.FileSubsystem#writeFileContents(String, JsonElement)} method.
     * </p>
     * <p>
     * Handles {@link IOException} during the file write operation by logging an error message
     * and printing the stack trace to the console.
     * </p>
     */
    private void saveToFile() {
        JsonObject object = new JsonObject();
        object.add("spawn", FileUtil.getJson(spawn));
        object.addProperty("join-message", joinMessage);
        object.addProperty("spawn-on-join", spawnOnJoin);
        object.addProperty("warp-on-join", warpOnJoin);
        object.addProperty("resort", resort.name());
        try {
            ParkManager.getFileUtil().getSubsystem("config").writeFileContents("config", object);
        } catch (IOException e) {
            Core.logMessage("ConfigUtil", "There was an error writing to the ConfigUtil config!");
            e.printStackTrace();
        }
    }
}
