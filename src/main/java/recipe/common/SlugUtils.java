package recipe.common;

import java.io.File;
import java.util.regex.Pattern;

public class SlugUtils {
    private static final Pattern INVALID_CHARS = Pattern.compile("[^a-zA-Z0-9\\s-]");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");
    
    /**
     * Converts a name to a URL-friendly slug format:
     * - Lowercase
     * - Replace spaces with hyphens
     * - Remove special characters
     * - Trim whitespace
     */
    public static String toSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "unnamed";
        }
        
        return name.trim()
                .toLowerCase()
                .replaceAll(INVALID_CHARS.pattern(), "") // Remove special chars
                .replaceAll(MULTIPLE_SPACES.pattern(), " ") // Normalize spaces
                .trim()
                .replace(" ", "-"); // Replace spaces with hyphens
    }
    
    /**
     * Generates a unique filename for a recipe, handling collisions
     * by adding numeric suffixes
     */
    public static String getUniqueRecipeFilename(String recipeName, String recipesDirectory) {
        String baseSlug = toSlug(recipeName);
        String filename = baseSlug + ".json";
        File recipesDir = new File(recipesDirectory);
        
        // Create directory if it doesn't exist
        if (!recipesDir.exists()) {
            recipesDir.mkdirs();
        }
        
        File file = new File(recipesDir, filename);
        
        // If no collision, return the original filename
        if (!file.exists()) {
            return filename;
        }
        
        // Handle collisions with numeric suffixes
        int counter = 2;
        do {
            filename = baseSlug + "-" + counter + ".json";
            file = new File(recipesDir, filename);
            counter++;
        } while (file.exists());
        
        return filename;
    }
    
    /**
     * Extracts the recipe slug from a filename
     */
    public static String filenameToSlug(String filename) {
        if (filename == null || !filename.endsWith(".json")) {
            return filename;
        }
        return filename.substring(0, filename.length() - 5); // Remove .json
    }
    
    /**
     * Validates that a name is not empty and contains valid characters
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String slug = toSlug(name);
        return !slug.isEmpty() && !slug.equals("unnamed");
    }
    
    /**
     * Normalizes a name for display (trims whitespace, capitalizes first letter, lowercase rest)
     */
    public static String normalizeDisplayName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Unnamed";
        }
        
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return "Unnamed";
        }
        
        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
    }
}