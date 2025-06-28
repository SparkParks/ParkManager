package network.palace.parkmanager.packs;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.events.CurrentPackReceivedEvent;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.resource.ResourceStatusEvent;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code PackManager} class is responsible for managing resource packs for players in a park environment.
 * This includes handling the settings for resource packs, managing configurations,
 * responding to player events, and interacting with the underlying file subsystem to save settings.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Handle the loading and saving of resource pack configurations.</li>
 *   <li>Respond to events related to resource packs, including when a pack is received
 *       or a player's resource pack status changes.</li>
 *   <li>Provide functionality to send appropriate resource packs to players based on their settings.</li>
 *   <li>Allow players to manage their resource pack settings through an in-game menu.</li>
 * </ul>
 *
 * <p><b>Event Handlers:</b></p>
 * <ul>
 *   <li>{@code onCurrentPackReceived}: Handles when a player receives a resource pack and updates their registry.</li>
 *   <li>{@code onResourceStatus}: Responds to the status of a player's resource pack, such as when it is accepted,
 *       loaded, or declined, as well as handling download failures.</li>
 * </ul>
 *
 * <p><b>Key Methods:</b></p>
 * <ul>
 *   <li>{@code handleJoin(CPlayer player, String s)}: Determines the player's pack setting upon join and sends
 *       the appropriate pack if required.</li>
 *   <li>{@code openMenu(CPlayer player)}: Opens a menu for the player to choose their resource pack setting.</li>
 *   <li>{@code setServerPack(String pack)}: Sets the server's active resource pack and saves it to the configuration file.</li>
 *   <li>{@code saveToFile()}: Saves the current server resource pack configuration to a file within the file subsystem.</li>
 *   <li>{@code changeSetting(CPlayer player, String setting)}: Updates the player's resource pack setting
 *       and sends the updated pack as necessary.</li>
 * </ul>
 *
 * <p>The class integrates with external systems and frameworks, such as the event management system,
 * player registry, and the file subsystem for persistence. It also utilizes an in-game menu system
 * to allow players to easily modify their settings.</p>
 */
public class PackManager implements Listener {
    /**
     * Represents the server resource pack identifier used in the application.
     * <p>
     * The {@code serverPack} variable holds the default server resource pack ID, which is typically used as a reference
     * for managing or validating the resource pack being used by players within the server.
     * </p>
     *
     * <ul>
     *     <li>Default Value: "WDW".</li>
     *     <li>Can be updated dynamically via corresponding methods in the {@code PackManager} class.</li>
     * </ul>
     *
     * <p>This variable is private and used internally within the {@code PackManager} class for handling resource pack-related logic.</p>
     */
    private String serverPack = "WDW";

    /**
     * Constructs a new instance of the <code>PackManager</code>. This constructor initializes
     * the internal state of the object by interacting with the file subsystem for storing
     * and retrieving configuration related to packs.
     *
     * <p>When invoked, it performs the following operations:
     * <ul>
     *   <li>Checks if a file subsystem named "packs" is already registered in the system.</li>
     *   <li>If the subsystem is not registered, it registers a new one for managing pack files.</li>
     *   <li>Attempts to retrieve and parse the JSON content of a file named "packs" within this subsystem.</li>
     *   <li>If the JSON file contains a "pack" property, it initializes the pack configuration using its value.</li>
     *   <li>If the file is invalid or lacks the "pack" property, it triggers a configuration save operation.</li>
     *   <li>Logs messages about the success or failure of loading the configuration.</li>
     * </ul>
     *
     * <p>Exceptions:
     * <ul>
     *   <li>Logs an error message and stack trace if an <code>IOException</code> occurs while accessing or reading the file.</li>
     * </ul>
     *
     * <p>This constructor ensures that the pack configuration is properly loaded and initialized
     * every time a new <code>PackManager</code> instance is created.
     */
    public PackManager() {
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("packs")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("packs");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("packs");
        }
        try {
            JsonElement element = subsystem.getFileContents("packs");
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                if (object.has("pack")) {
                    serverPack = object.get("pack").getAsString();
                } else {
                    saveToFile();
                }
            } else {
                saveToFile();
            }
            Core.logMessage("PackManager", "Loaded the PackManager config!");
        } catch (IOException e) {
            Core.logMessage("PackManager", "There was an error loading the PackManager config!");
            e.printStackTrace();
        }
    }

    /**
     * Handles the event triggered when a player receives the current resource pack.
     * <p>
     * This method updates the player's registry with the received pack and determines if the pack
     * should be sent directly to the player or if it should mark the player as needing the pack.
     * </p>
     *
     * @param event an instance of {@code CurrentPackReceivedEvent} containing information about the
     *              player and the received resource pack
     */
    @EventHandler
    public void onCurrentPackReceived(CurrentPackReceivedEvent event) {
        CPlayer player = event.getPlayer();
        player.getRegistry().addEntry("pack", event.getPack());
        if (player.getRegistry().hasEntry("packSetting")) {
            sendPack(player);
        } else {
            player.getRegistry().addEntry("needsPack", true);
        }
    }

    /**
     * Handles the `ResourceStatusEvent` triggered when a player's interaction with a resource pack is updated.
     * Responds to the player's choice and provides appropriate feedback or actions based on the resource status.
     * <p>
     * Possible statuses include:
     * <ul>
     *   <li><b>ACCEPTED</b>: The player accepts the resource pack, triggering a download message.</li>
     *   <li><b>LOADED</b>: The resource pack has been successfully loaded, informing the player.</li>
     *   <li><b>DECLINED</b>: The player denies the resource pack, sending an alert to them.</li>
     *   <li><b>DEFAULT</b>: Handles scenarios where downloading the pack fails, providing a manual download option or a help link.</li>
     * </ul>
     *
     * @param event the {@link ResourceStatusEvent} providing details about the resource pack status and the associated player.
     */
    @EventHandler
    public void onResourceStatus(ResourceStatusEvent event) {
        CPlayer player = event.getPlayer();
        switch (event.getStatus()) {
            case ACCEPTED:
                player.sendMessage(ChatColor.GREEN + "Resource Pack accepted! Downloading now...");
                break;
            case LOADED:
                player.sendMessage(ChatColor.GREEN + "Resource Pack loaded!");
                break;
            case DECLINED:
                player.sendMessage(ChatColor.RED + "You have declined the Resource Pack!");
                break;
            default: {
                if (player.getRegistry().hasEntry("packDownloadURL")) {
                    String url = (String) player.getRegistry().getEntry("packDownloadURL");
                    new FormattedMessage("Download failed! ").color(ChatColor.RED)
                            .then("You can download the pack manually by clicking ").color(ChatColor.AQUA)
                            .then("here").color(ChatColor.YELLOW).style(ChatColor.UNDERLINE).link(url).send(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Download failed!");
                    player.sendMessage(ChatColor.YELLOW + "For help with this, visit: " + ChatColor.AQUA +
                            "https://palnet.us/rphelp");
                }
            }
        }
    }

    /**
     * Sends a resource pack to a player based on their settings within the registry.
     * <p>
     * Depending on the player's current "packSetting" and the server's resource pack configuration,
     * this method determines whether a specific resource pack should be sent to the player.
     * </p>
     *
     * <ul>
     *     <li>If the player's "packSetting" is "enabled" and their current resource pack differs from the server's resource pack,
     *     the server's resource pack is sent.</li>
     *     <li>If the player's "packSetting" is "blank", a "blank" resource pack is sent.</li>
     * </ul>
     *
     * @param player The player receiving the resource pack. This object provides access to
     *               their resource pack preferences and current registry data.
     */
    private void sendPack(CPlayer player) {
        String packSetting = (String) player.getRegistry().getEntry("packSetting");
        if (packSetting.equals("enabled") && !player.getRegistry().getEntry("pack").equals(serverPack)) {
            Core.getResourceManager().sendPack(player, serverPack);
        } else if (packSetting.equals("blank")) {
            Core.getResourceManager().sendPack(player, "Blank");
        }
    }

    /**
     * Handles the join operation for a player. Based on the parameter <code>s</code>,
     * it either opens a menu, updates the player's registry, or sends a resource pack if needed.
     *
     * <p>This method reacts to specific values of the parameter <code>s</code>:</p>
     * <ul>
     *   <li>If the value is "ask", "yes", or "no", it schedules a task to open a menu for the player after a delay.</li>
     *   <li>If the value does not match the above, it saves the value in the player's registry under the "packSetting" key
     *       and checks the "needsPack" registry entry to determine if a resource pack should be sent to the player.</li>
     * </ul>
     *
     * @param player the player for whom the join operation is handled; cannot be <code>null</code>.
     * @param s a string indicating the action or setting to handle; expected values are "ask", "yes", "no", or other custom settings.
     */
    public void handleJoin(CPlayer player, String s) {
        if (s.equals("ask") || s.equals("yes") || s.equals("no")) {
            Core.runTaskLater(ParkManager.getInstance(), () -> openMenu(player), 20L);
            return;
        }
        player.getRegistry().addEntry("packSetting", s);
        if (player.getRegistry().hasEntry("needsPack") && (boolean) player.getRegistry().getEntry("needsPack")) {
            sendPack(player);
        }
    }

    /**
     * Opens the resource pack settings menu for the specified player.
     * This menu allows the player to configure their preference for
     * receiving resource packs (e.g., enable, disable, or blank).
     *
     * <p>
     * The menu contains the following buttons:
     * <ul>
     *   <li><b>Blank:</b> Sends a blank resource pack.</li>
     *   <li><b>Disabled:</b> No resource packs will be sent.</li>
     *   <li><b>Enabled:</b> All park resource packs will be sent.</li>
     * </ul>
     * </p>
     *
     * @param player The {@link CPlayer} instance representing the player for whom the menu is opened.
     *               This player will be presented with the menu interface and may interact
     *               with it to adjust their resource pack preferences.
     */
    public void openMenu(CPlayer player) {
        String setting;
        if (!player.getRegistry().hasEntry("packSetting")) {
            setting = "ask";
        } else {
            setting = (String) player.getRegistry().getEntry("packSetting");
        }
        String selected = ChatColor.YELLOW + " (SELECTED)";
        List<MenuButton> buttons = Arrays.asList(
                new MenuButton(10, ItemUtil.create(Material.STAINED_CLAY, 1, 3,
                        ChatColor.AQUA + "Blank" + (setting.equals("blank") ? selected : ""),
                        Arrays.asList(ChatColor.GRAY + "You will be sent a blank",
                                ChatColor.GRAY + "resource pack with this setting")),
                        ImmutableMap.of(ClickType.LEFT, p -> changeSetting(p, "blank"))),
                new MenuButton(13, ItemUtil.create(Material.CONCRETE, 1, 14,
                        ChatColor.RED + "Disabled" + (setting.equals("disabled") ? selected : ""),
                        Arrays.asList(ChatColor.GRAY + "You won't receive any",
                                ChatColor.GRAY + "resource packs with this setting")),
                        ImmutableMap.of(ClickType.LEFT, p -> changeSetting(p, "disabled"))),
                new MenuButton(16, ItemUtil.create(Material.CONCRETE, 1, 13,
                        ChatColor.GREEN + "Enabled" + (setting.equals("enabled") ? selected : ""),
                        Arrays.asList(ChatColor.GRAY + "You will be sent all park",
                                ChatColor.GRAY + "resource packs with this setting")),
                        ImmutableMap.of(ClickType.LEFT, p -> changeSetting(p, "enabled")))
        );

        new Menu(27, ChatColor.BLUE + "Pack Setting", player, buttons).open();
    }

    /**
     * Modifies the resource pack setting for a given player according to the specified option.
     * <p>
     * Depending on the value of the {@code setting} parameter, a corresponding message is sent to the player,
     * the player's registry is updated with the new setting, and the player's inventory is closed. Additionally,
     * the resource pack is sent to the player, and the updated setting is saved asynchronously to the database.
     * </p>
     *
     * @param player  The {@code CPlayer} instance representing the player whose setting is to be modified.
     * @param setting A {@code String} representing the new resource pack setting. Valid values include:
     *                <ul>
     *                  <li>{@code "enabled"} - Enables Park Resource Packs.</li>
     *                  <li>{@code "disabled"} - Disables Park Resource Packs.</li>
     *                  <li>{@code "blank"} - Sets the resource pack to a blank version.</li>
     *                </ul>
     */
    private void changeSetting(CPlayer player, String setting) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 2f);
        switch (setting.toLowerCase()) {
            case "enabled": {
                player.sendMessage(ChatColor.GREEN + "You've enabled Park Resource Packs!");
                player.getRegistry().addEntry("packSetting", "enabled");
                break;
            }
            case "disabled": {
                player.sendMessage(ChatColor.RED + "You've disabled Park Resource Packs!");
                player.getRegistry().addEntry("packSetting", "disabled");
                break;
            }
            case "blank": {
                player.sendMessage(ChatColor.DARK_AQUA + "You will be sent a " + ChatColor.AQUA + "blank " + ChatColor.DARK_AQUA + "resource pack in the Parks!");
                player.getRegistry().addEntry("packSetting", "blank");
                break;
            }
        }
        player.closeInventory();
        sendPack(player);
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setParkSetting(player.getUniqueId(), "pack", setting));
    }

    /**
     * Updates the server's current pack to the specified value and saves the update
     * to the configuration file.
     *
     * <p>
     * This method modifies the {@code serverPack} field to reflect the provided pack
     * name, ensuring the change is persistent by invoking the {@code saveToFile()}
     * method.
     * </p>
     *
     * @param pack The name of the new server pack to be set. This string represents
     *             the resource pack configuration to be applied to the server.
     */
    public void setServerPack(String pack) {
        this.serverPack = pack;
        saveToFile();
    }

    /**
     * Saves the current pack configuration to a file.
     *
     * <p>This method serializes the current server pack information into a JSON object
     * and writes it to a file within the "packs" subsystem. It utilizes the ParkManager's file
     * utility to perform the write operation.</p>
     *
     * <p>If an error occurs during the write process, an error message is logged, and the stack trace
     * of the exception is printed.</p>
     *
     * <p>The file storage system and handling of subsystems is managed by the ParkManager's file utility.</p>
     *
     * <ul>
     *   <li>Serializes the server pack configuration into a JSON format.</li>
     *   <li>Writes the serialized data into the file system under the "packs" subsystem.</li>
     *   <li>Handles potential <code>IOException</code> during the file operation.</li>
     *   <li>Logs errors and outputs the exception stack trace if the write operation fails.</li>
     * </ul>
     *
     * @throws IOException if an error occurs during writing to the file
     */
    public void saveToFile() {
        JsonObject object = new JsonObject();
        object.addProperty("pack", serverPack);
        try {
            ParkManager.getFileUtil().getSubsystem("packs").writeFileContents("packs", object);
        } catch (IOException e) {
            Core.logMessage("PackManager", "There was an error writing to the PackManager config!");
            e.printStackTrace();
        }
    }
}
