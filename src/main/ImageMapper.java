package main;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;

public class ImageMapper {
    private static Map<String, URL> imageMap = new HashMap<>();
    
    static {
        initializeImageMap();
    }
    
    private static void initializeImageMap() {
        File imagesDir = new File("res/images");
        
        // Check if the directory exists and is actually a directory
        if (imagesDir.exists() && imagesDir.isDirectory()) {
            // Loop through all files in the directory
            for (File file : imagesDir.listFiles()) {
                // Check if it's a file (not a directory) and has an image extension
                if (file.isFile() && (file.getName().endsWith(".jpg") || 
                    file.getName().endsWith(".jpeg") || 
                    file.getName().endsWith(".png"))) {
                    String fileName = file.getName();
                    // Get the filename without extension and convert to lowercase
                    // Example: "RedCar.jpg" becomes "redcar"
                    String key = fileName.substring(0, fileName.lastIndexOf('.')).toLowerCase();
                    
                    try {
                        // Convert the file to a URL that can be used by ImageIcon
                        URL imageUrl = file.toURI().toURL();
                        // Add the main mapping to the HashMap
                        imageMap.put(key, imageUrl);
                        
                        // Add any alternative names (aliases) for this image
                        addAliasesToImageMap(key, imageUrl);
                        
                    } catch (Exception e) {
                        System.err.println("Error loading image: " + fileName);
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.err.println("Error: 'res/images' directory not found.");
        }
    }
    
    // if "car" is the base word, it might add "automobile" pointing to the same image
    private static void addAliasesToImageMap(String baseWord, URL imageUrl) {
        // Check if this word has any aliases defined in ExpertSystem
        Set<String> aliases = ExpertSystem.WORD_ALIASES.get(baseWord);
        if (aliases != null) {
            // Add all aliases to the map, pointing to the same image
            for (String alias : aliases) {
                imageMap.put(alias.toLowerCase(), imageUrl);
            }
        }
        
        // Check if this word is itself an alias for another word
        for (Map.Entry<String, Set<String>> entry : ExpertSystem.WORD_ALIASES.entrySet()) {
            if (entry.getValue().contains(baseWord)) {
                String mainWord = entry.getKey();
                imageMap.put(mainWord.toLowerCase(), imageUrl);
            }
        }
    }

    // Method to get an ImageIcon object for a given object name
    public static ImageIcon getImageIcon(String objectName) {
        // First try to find the image directly using the provided name
        URL imgURL = imageMap.get(objectName.toLowerCase());
        
        // If not found directly, check if the name is an alias
        if (imgURL == null) {
            for (Map.Entry<String, Set<String>> entry : ExpertSystem.WORD_ALIASES.entrySet()) {
                if (entry.getValue().contains(objectName.toLowerCase())) {
                    imgURL = imageMap.get(entry.getKey().toLowerCase());
                    break;
                }
            }
        }
        
        // If we found an image URL, create and return an ImageIcon
        // Otherwise return null and log an error
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find image for: " + objectName);
            return null;
        }
    }
}