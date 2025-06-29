package network.palace.parkmanager.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;

/**
 * Utility class for managing files and directories related to subsystems within a plugin directory.
 * <p>
 * This class facilitates the creation, registration, and management of subsystems,
 * each represented by a unique directory under the main plugin directory, as well as
 * utility methods for handling JSON files stored within these directories.
 * </p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *     <li>Automatic creation of a root plugin directory if it does not exist.</li>
 *     <li>Support for registering subsystems, each corresponding to a unique directory.</li>
 *     <li>Methods for accessing, creating, and working with files inside subsystem directories.</li>
 *     <li>Convenient handling of JSON data within subsystem files.</li>
 * </ul>
 */
public class FileUtil {
    /**
     * The base directory path for the ParkManager plugin.
     *
     * <p>This constant represents the root folder where all plugin-related files and subsystems
     * are stored. It is primarily used as the starting point for organizing and managing
     * plugin data such as subsystem directories or configuration files.</p>
     *
     * <p>Example:
     * <ul>
     *   <li>A subsystem named 'outline' would resolve to 'plugins/ParkManager/outline'.</li>
     *   <li>Configuration or data files would also be located within this directory or its subdirectories.</li>
     * </ul>
     * </p>
     *
     * <p>Note: This path is hardcoded and assumes the structure of your plugin's directory
     * remains consistent.</p>
     */
    public static final String mainPath = "plugins/ParkManager";

    /**
     * <p>The {@code subsystems} variable maintains a collection of {@link FileSubsystem} objects,
     * each representing a distinct file-based subsystem. This map allows subsystems
     * to be registered, accessed, and managed within the overarching file management structure.</p>
     *
     * <p>Key Characteristics:</p>
     * <ul>
     *     <li>Uses a {@code HashMap} for efficient retrieval of subsystems by their associated names.</li>
     *     <li>The key is a {@code String} representing the subsystem's identifier, typically corresponding
     *     to the name of its directory within the file structure.</li>
     *     <li>The value is a {@link FileSubsystem} instance, containing logic for handling JSON
     *     file operations within the subsystem's directory.</li>
     * </ul>
     *
     * <p>Usage Context:</p>
     * <ul>
     *     <li>This variable is primarily utilized by methods such as {@code registerSubsystem},
     *     {@code getSubsystem}, and {@code isSubsystemRegistered} within the {@link FileUtil} class
     *     to manage the lifecycle of subsystems.</li>
     *     <li>Provides a dynamic and flexible structure to accommodate multiple subsystems
     *     with distinct purposes and data storage requirements.</li>
     * </ul>
     */
    private HashMap<String, FileSubsystem> subsystems = new HashMap<>();

    /**
     * Represents the root {@link FileSubsystem} utilized as the primary directory
     * within the file system structure for managing and organizing subsystem-related
     * files. This is the base subsystem that provides fundamental functionality for
     * reading, writing, and interacting with JSON files and directories.
     *
     * <p><strong>Features:</strong>
     * <ul>
     *     <li>Acts as the root-level {@code FileSubsystem} in the file structure.</li>
     *     <li>Manages JSON file operations within the designated base directory.</li>
     *     <li>Serves as the central access point for other subsystems registered via {@link FileUtil}.</li>
     * </ul>
     *
     * <p>Usage of this variable typically involves file operations within the root of the
     * plugin's storage structure. It is initialized and managed internally within the
     * {@link FileUtil} class.
     */
    @Getter private FileSubsystem rootSubsystem;

    /**
     * Constructs a new {@code FileUtil} instance.
     * <p>
     * Initializes the root subsystem and ensures that the main plugin directory
     * exists. If the directory does not exist, it will be created.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *   <li>Checks if the main plugin directory exists at the specified {@code mainPath}.</li>
     *   <li>If the directory does not exist, it creates the required directories.</li>
     *   <li>Sets up the {@code rootSubsystem} as an instance of {@code RootFileSubsystem},
     *       which designates this main directory for file operations.</li>
     * </ul>
     */
    public FileUtil() {
        File pluginDirectory = new File(mainPath);
        if (!pluginDirectory.exists()) {
            pluginDirectory.mkdirs();
        }
        rootSubsystem = new RootFileSubsystem();
    }

    /**
     * Registers a subsystem with the given name and ensures that its directory exists within the file structure.
     * <p>
     * The method performs the following steps:
     * <ul>
     *   <li>Checks if a subsystem with the specified name is already registered. If yes, an {@code IllegalArgumentException} is thrown.</li>
     *   <li>Creates a new {@code FileSubsystem} instance with the specified name.</li>
     *   <li>Ensures that the directory for the subsystem exists, creating it if necessary.</li>
     *   <li>Stores the new subsystem in the {@code subsystems} map.</li>
     * </ul>
     *
     * @param name the name of the subsystem to be registered. This name also determines the directory for the subsystem.
     * @return the newly created {@code FileSubsystem} instance.
     * @throws IllegalArgumentException if a subsystem with the given name is already registered.
     */
    public FileSubsystem registerSubsystem(String name) throws IllegalArgumentException {
        if (isSubsystemRegistered(name))
            throw new IllegalArgumentException("A subsystem already exists by the name '" + name + "'!");
        FileSubsystem subsystem = new FileSubsystem(name);
        File dir = subsystem.getDirectory();
        if (!dir.exists()) dir.mkdirs();
        subsystems.put(name, subsystem);
        return subsystem;
    }

    /**
     * Retrieves a {@link FileSubsystem} by its name.
     *
     * <p>If the name provided is "root", the root subsystem is returned.
     * Otherwise, it attempts to fetch a registered subsystem matching the given name.
     *
     * @param name The name of the subsystem to retrieve. This can either be "root"
     *             or the identifier of a previously registered subsystem.
     * @return The {@link FileSubsystem} corresponding to the specified name.
     *         Returns {@code null} if no subsystem with the given name exists.
     */
    public FileSubsystem getSubsystem(String name) {
        if (name.equals("root")) return rootSubsystem;
        return subsystems.get(name);
    }

    /**
     * Checks whether a subsystem with the specified name is registered.
     *
     * <p>This method verifies if the subsystem identified by the given
     * {@code name} exists in the internal registry.
     *
     * @param name the name of the subsystem to check for registration.
     *             Must not be {@code null}.
     * @return {@code true} if the subsystem is registered,
     *         {@code false} otherwise.
     */
    public boolean isSubsystemRegistered(String name) {
        return subsystems.containsKey(name);
    }

    /**
     * The {@code FileSubsystem} class represents a subsystem directory within the file structure.
     * It provides functionality to work with JSON files within this directory, including reading,
     * writing, and file creation. This class is designed to be extended or instantiated for specific
     * file organization purposes.
     *
     * <p>Key Features:
     * <ul>
     *     <li>Ability to define a specific directory for the subsystem.</li>
     *     <li>Automatic creation of files if they do not exist.</li>
     *     <li>Reading and parsing the contents of JSON files.</li>
     *     <li>Writing JSON content to files within the subsystem.</li>
     * </ul>
     * <p><strong>Note:</strong> The {@code mainPath} variable must be defined within the containing
     * context to determine the base directory.
     */
    public static class FileSubsystem {
        /**
         * Represents the name of the file subsystem.
         * <p>
         * This variable is used to store the name associated with a specific file subsystem instance.
         * It is typically used in conjunction with other methods in the class to manage files and
         * directories within the subsystem.
         */
        private String name;

        /**
         * Constructs a new {@code FileSubsystem} instance with the specified name.
         *
         * <p>This constructor initializes a file subsystem with a unique identifier, which can be used
         * to manage and access files and folders related to this subsystem.</p>
         *
         * @param name the name of the file subsystem. This name acts as an identifier for the subsystem
         *             and is used internally for file and folder management.
         */
        FileSubsystem(String name) {
            this.name = name;
        }

        /**
         * Retrieves the directory associated with the current <code>FileSubsystem</code>.
         *
         * <p>
         * This method constructs a {@link File} object corresponding to the
         * directory derived from the combination of the main path and the subsystem's name.
         * </p>
         *
         * @return A {@link File} object representing the directory.
         */
        public File getDirectory() {
            return new File(mainPath + "/" + name);
        }

        /**
         * Retrieves a file with the specified name from the subsystem's directory.
         * If the file does not already exist, it will be created.
         *
         * <p>The file will have a ".json" extension appended to the specified name
         * and will be located in the directory returned by {@link #getDirectory()}.</p>
         *
         * @param name the name of the file (without the extension) to retrieve or create.
         * @return the {@code File} object representing the file with the specified name.
         * @throws IOException if an I/O error occurs when creating the file or accessing the directory.
         */
        public File getFile(String name) throws IOException {
            File file = new File(getDirectory().getPath() + "/" + name + ".json");
            if (!file.exists()) file.createNewFile();
            return file;
        }

        /**
         * Retrieves the contents of a JSON file with the specified name and parses it into a {@link JsonElement}.
         *
         * <p>This method reads the file's content, converts it to a JSON format using the <code>Gson</code> library,
         * and returns it as a {@link JsonElement}. If the JSON content is invalid or empty,
         * an empty {@link JsonObject} is returned.</p>
         *
         * <p>The file is located in the directory managed by the class's file subsystem and must have a ".json" extension.
         * Refer to the {@link #getFile(String)} method for file retrieval logic.</p>
         *
         * @param name The name of the file (without the ".json" extension) to retrieve and parse.
         *             This corresponds to a JSON file located within the associated file subsystem.
         * @return A {@link JsonElement}, which could be a {@link JsonObject}, {@link JsonArray}, or another JSON type,
         *         representing the contents of the file. Returns an empty {@link JsonObject} if the content is invalid.
         * @throws IOException If there is an error reading the file.
         * @throws JsonSyntaxException If the file content cannot be parsed as valid JSON.
         */
        public JsonElement getFileContents(String name) throws IOException, JsonSyntaxException {
            File file = getFile(name);
            StringBuilder json = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }
            JsonElement element = new Gson().fromJson(json.toString(), JsonElement.class);
            if (element == null) {
                return new JsonObject();
            } else {
                return element;
            }
        }

        /**
         * Writes the JSON representation of the given {@link JsonElement} to a file within the subsystem's directory.
         *
         * <p>Creates or overwrites the file associated with the specified name in the subsystem's directory,
         * and writes the stringified JSON content of the provided {@link JsonElement} into the file.
         * The file will be created if it does not already exist.</p>
         *
         * @param name the name of the file (without the extension) where the JSON content will be written.
         * @param element the {@link JsonElement} whose JSON content is to be written to the file.
         * @throws IOException if an I/O error occurs when writing to the file or accessing the directory.
         */
        public void writeFileContents(String name, JsonElement element) throws IOException {
            Files.write(Paths.get(getFile(name).toURI()), Collections.singletonList(element.toString()), StandardCharsets.UTF_8);
        }
    }

    /**
     * The {@code RootFileSubsystem} class is a specialized implementation of the {@code FileSubsystem}.
     * It represents the root directory of the file structure and provides access to this directory.
     *
     * <p><b>Purpose:</b></p>
     * <p>The root file subsystem acts as the primary directory for file operations in the system.
     * It is generally used to manage files and directories at the top level of the file structure.</p>
     *
     * <p><b>Key Features:</b></p>
     * <ul>
     *     <li>Provides a concrete implementation for accessing the root directory.</li>
     *     <li>Overrides the {@link FileSubsystem#getDirectory()} method to return the
     *     root directory as defined by the {@code mainPath} variable.</li>
     *     <li>Configured to have an empty subsystem name, as it is designed to operate
     *     at the root level of the file structure.</li>
     * </ul>
     *
     * <p><b>Usage:</b></p>
     * <p>This class is typically instantiated and managed by the {@code FileUtil} class to ensure
     * seamless interaction with the root directory. It is not intended for direct external instantiation
     * but is instead used as a central point of operation for root-level file management.</p>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *     <li>When the {@link #getDirectory()} method is invoked, it returns the {@code File}
     *     object that corresponds to the root directory defined by the base {@code mainPath}.</li>
     * </ul>
     */
    public static class RootFileSubsystem extends FileSubsystem {

        /**
         * Constructs a new {@code RootFileSubsystem} instance.
         *
         * <p>This constructor initializes the root-level file subsystem, extending the {@code FileSubsystem} base
         * class. The {@code RootFileSubsystem} is designed specifically to represent and manage the root directory
         * of the file structure. It provides centralized access to the file system at the root level.</p>
         *
         * <p><b>Behavior:</b></p>
         * <ul>
         *     <li>Initializes the base {@code FileSubsystem} with an empty string as its name,
         *     aligning with the characteristic of the root directory being unnamed.</li>
         * </ul>
         *
         * <p><b>Intended Use:</b></p>
         * <p>This constructor is used internally within the system to create a root-level file subsystem
         * for managing and organizing file-based resources. It pairs with subsystem functionalities
         * related to file and directory operations at the primary level of the file hierarchy.</p>
         */
        RootFileSubsystem() {
            super("");
        }

        /**
         * Retrieves the root directory associated with this file subsystem.
         *
         * <p>This method provides access to the root directory as a {@link File} object,
         * which allows for further operations on the directory, such as reading, writing,
         * or querying its contents. The directory is defined based on the {@code mainPath}
         * variable, which specifies the path to the root level of the file structure.</p>
         *
         * <p><b>Key Notes:</b></p>
         * <ul>
         *     <li>The returned {@link File} object represents the root directory.</li>
         *     <li>The {@code mainPath} variable specifies the path to the directory.</li>
         * </ul>
         *
         * @return A {@link File} object representing the root directory of the file subsystem.
         */
        @Override
        public File getDirectory() {
            return new File(mainPath);
        }
    }

    /**
     * Converts a {@link JsonObject} into a {@link Location} object.
     * <p>
     * This method parses the provided {@code JsonObject} to extract the world,
     * x, y, z coordinates, yaw, and pitch to construct a {@link Location} instance.
     * If the provided {@code JsonObject} is {@code null}, this method returns {@code null}.
     * <p>
     * Assumes that the {@code JsonObject} contains the following keys:
     * <ul>
     *   <li>{@code world} - A string representing the name of the world.</li>
     *   <li>{@code x} - A double representing the x-coordinate.</li>
     *   <li>{@code y} - A double representing the y-coordinate.</li>
     *   <li>{@code z} - A double representing the z-coordinate.</li>
     *   <li>{@code yaw} - A float representing the yaw value.</li>
     *   <li>{@code pitch} - A float representing the pitch value.</li>
     * </ul>
     *
     * @param object the {@link JsonObject} representing the location data. Must not be {@code null}.
     *               The object should contain the keys {@code world}, {@code x}, {@code y}, {@code z},
     *               {@code yaw}, and {@code pitch}.
     * @return a {@link Location} object constructed with the data from the {@code JsonObject},
     *         or {@code null} if the {@code JsonObject} is {@code null}.
     */
    public static Location getLocation(JsonObject object) {
        if (object == null) return null;
        return new Location(Bukkit.getWorld(object.get("world").getAsString()), object.get("x").getAsDouble(),
                object.get("y").getAsDouble(), object.get("z").getAsDouble(), object.get("yaw").getAsFloat(), object.get("pitch").getAsFloat());
    }

    /**
     * Converts a {@link Location} object into a JSON representation.
     *
     * <p>This method creates a {@code JsonObject} with the following properties:
     * <ul>
     *   <li><b>x:</b> The X coordinate of the {@code Location}.</li>
     *   <li><b>y:</b> The Y coordinate of the {@code Location}.</li>
     *   <li><b>z:</b> The Z coordinate of the {@code Location}.</li>
     *   <li><b>yaw:</b> The yaw rotation of the {@code Location}.</li>
     *   <li><b>pitch:</b> The pitch rotation of the {@code Location}.</li>
     *   <li><b>world:</b> The name of the world of the {@code Location}.</li>
     * </ul>
     * If the provided {@code Location} is {@code null}, an empty {@code JsonObject} is returned.
     *
     * @param location the {@link Location} to convert to a JSON object. Can be {@code null}.
     * @return a {@link JsonObject} representing the {@code Location}, or an empty JSON object if {@code location} is {@code null}.
     */
    public static JsonObject getJson(Location location) {
        JsonObject object = new JsonObject();
        if (location != null) {
            object.addProperty("x", location.getX());
            object.addProperty("y", location.getY());
            object.addProperty("z", location.getZ());
            object.addProperty("yaw", location.getYaw());
            object.addProperty("pitch", location.getPitch());
            object.addProperty("world", location.getWorld().getName());
        }
        return object;
    }
}
