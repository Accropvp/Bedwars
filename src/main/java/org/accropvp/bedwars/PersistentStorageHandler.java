package org.accropvp.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.*;

public class PersistentStorageHandler {
    private final File file;
    private final FileConfiguration config;

    public PersistentStorageHandler(File dataFolder) {
        this.file = new File(dataFolder, "bed_tags.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    // Save a location under a specific tag
    public void addLocationToTag(String tag, Location location) {
        List<String> locations = config.getStringList(tag);
        String locKey = locationToKey(location);

        if (!locations.contains(locKey)) {
            locations.add(locKey); // Add the location if it's not already present
        }

        config.set(tag, locations);
        saveFile();
    }

    // Remove a specific location from a tag
    public void removeLocationFromTag(String tag, Location location) {
        List<String> locations = config.getStringList(tag);
        String locKey = locationToKey(location);

        locations.remove(locKey); // Remove the location
        if (locations.isEmpty()) {
            config.set(tag, null); // Remove the tag if no locations remain
        } else {
            config.set(tag, locations);
        }

        saveFile();
    }

    // Get all locations associated with a tag
    public Set<Location> getLocationsForTag(String tag) {
        List<String> locationStrings = config.getStringList(tag);
        Set<Location> locations = new HashSet<>();

        for (String locString : locationStrings) {
            locations.add(keyToLocation(locString));
        }

        return locations;
    }

    // Helper: Convert a Location to a string
    private String locationToKey(Location location) {
        return location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
    }

    // Helper: Convert a string back to a Location
    private Location keyToLocation(String key) {
        String[] parts = key.split(",");
        if (parts.length != 4) return null;

        String worldName = parts[0];
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = Integer.parseInt(parts[3]);

        return new Location(org.bukkit.Bukkit.getWorld(worldName), x, y, z);
    }

    // Save a tag at a specific location
    public void saveTag(Location location, String tag) {
        String locKey = locationToKey(location);
        config.set(locKey, tag);
        saveFile();
    }

    // Get a tag from a specific location
    public String getTag(Location location) {
        String locKey = locationToKey(location);
        return config.getString(locKey);
    }

    // Remove a tag from a specific location
    public void removeTag(Location location) {
        String locKey = locationToKey(location);
        config.set(locKey, null);
        saveFile();
    }

    // Method to get the location of a bed by tag
    public Location getLocationByTag(String tag) {
        for (String key : config.getKeys(false)) {
            // If the tag matches
            if (Objects.equals(config.getString(key), tag)) {
                // Parse the key back into a Location
                String[] parts = key.split(",");
                if (parts.length == 4) {
                    String worldName = parts[0];
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    int z = Integer.parseInt(parts[3]);

                    return new Location(Bukkit.getWorld(worldName), x, y, z);
                }
            }
        }
        return null; // No bed with the given tag found
    }

    public Set<String> getAllKeys(){
        return config.getKeys(false);
    }

    public String getValue(String key){
        return config.getString(key);
    }

    public void removeKey(String key){
        config.set(key, null);
        saveFile();
    }

    public void addKeyValue(String key, String value){
        config.set(key, value);
        saveFile();
    }

    // Save the YAML file
    private void saveFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

