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
    public static final String mainPath = "plugins/ParkManager";
    private HashMap<String, FileSubsystem> subsystems = new HashMap<>();
    @Getter private FileSubsystem rootSubsystem;

    public FileUtil() {
        File pluginDirectory = new File(mainPath);
        if (!pluginDirectory.exists()) {
            pluginDirectory.mkdirs();
        }
        rootSubsystem = new RootFileSubsystem();
    }

    /**
     * Creates (or does nothing if exists) a directory for the subsystem within the plugin directory
     * ex: 'plugins/ParkManager/outline' for OutlineManager
     *
     * @param name the name of the directory, preferably lowercase with only letters/numbers
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
     * Get a registered subsystem
     *
     * @param name the name
     * @return FileSubsystem if it exists, null if not
     */
    public FileSubsystem getSubsystem(String name) {
        if (name.equals("root")) return rootSubsystem;
        return subsystems.get(name);
    }

    /**
     * Check if a subsystem is registered by name
     *
     * @param name the name
     * @return true if a subsystem has been registered by that name
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
        private String name;

        FileSubsystem(String name) {
            this.name = name;
        }

        public File getDirectory() {
            return new File(mainPath + "/" + name);
        }

        /**
         * Get a file within the subsystem's directory
         * If it doesn't exist, attempt to create it
         *
         * @param name the name of the file
         * @return the File
         * @throws IOException if there was an error creating the file
         */
        public File getFile(String name) throws IOException {
            File file = new File(getDirectory().getPath() + "/" + name + ".json");
            if (!file.exists()) file.createNewFile();
            return file;
        }

        /**
         * Get the JSON contents of a subsystem file
         *
         * @param name the name of the file
         * @return JsonElement with the contents of the file
         * @throws IOException         if there was an error reading the file
         * @throws JsonSyntaxException if there was an error parsing the file as JSON
         * @see #getFile(String)
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
         * Write JSON content to a subsystem file
         *
         * @param name    the name of the file
         * @param element the JSON data being written to the file
         * @throws IOException if there was an error writing to the file
         * @see #getFile(String)
         */
        public void writeFileContents(String name, JsonElement element) throws IOException {
            Files.write(Paths.get(getFile(name).toURI()), Collections.singletonList(element.toString()), StandardCharsets.UTF_8);
        }
    }

    public static class RootFileSubsystem extends FileSubsystem {

        RootFileSubsystem() {
            super("");
        }

        @Override
        public File getDirectory() {
            return new File(mainPath);
        }
    }

    public static Location getLocation(JsonObject object) {
        if (object == null) return null;
        return new Location(Bukkit.getWorld(object.get("world").getAsString()), object.get("x").getAsDouble(),
                object.get("y").getAsDouble(), object.get("z").getAsDouble(), object.get("yaw").getAsFloat(), object.get("pitch").getAsFloat());
    }

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
