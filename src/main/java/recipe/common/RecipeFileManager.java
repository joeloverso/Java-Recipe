package recipe.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages recipe file operations including save, load, list, and delete
 */
public class RecipeFileManager {
    private final String recipesDirectory;
    private static final String RECIPES_DIR_NAME = "recipes";
    
    public RecipeFileManager() {
        this.recipesDirectory = RECIPES_DIR_NAME;
        ensureRecipesDirectoryExists();
    }
    
    public RecipeFileManager(String recipesDirectory) {
        this.recipesDirectory = recipesDirectory;
        ensureRecipesDirectoryExists();
    }
    
    /**
     * Ensures the recipes directory exists
     */
    private void ensureRecipesDirectoryExists() {
        File dir = new File(recipesDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Saves a recipe to a JSON file
     */
    public String saveRecipe(Recipe recipe) throws IOException {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null");
        }
        
        if (!recipe.isValid()) {
            throw new IllegalArgumentException("Recipe is not valid");
        }
        
        String filename = SlugUtils.getUniqueRecipeFilename(recipe.getName(), recipesDirectory);
        File file = new File(recipesDirectory, filename);
        
        recipe.saveToFile(file);
        
        return filename;
    }
    
    /**
     * Loads a recipe from a JSON file by filename
     */
    public Recipe loadRecipe(String filename) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        
        File file = new File(recipesDirectory, filename);
        
        if (!file.exists()) {
            throw new IOException("Recipe file not found: " + filename);
        }
        
        return Recipe.fromFile(file);
    }
    
    /**
     * Loads a recipe by its slug (name without .json extension)
     */
    public Recipe loadRecipeBySlug(String slug) throws IOException {
        if (slug == null || slug.trim().isEmpty()) {
            throw new IllegalArgumentException("Slug cannot be null or empty");
        }
        
        String filename = slug.endsWith(".json") ? slug : slug + ".json";
        return loadRecipe(filename);
    }
    
    /**
     * Lists all available recipe files
     */
    public List<String> listRecipeFiles() {
        File dir = new File(recipesDirectory);
        
        if (!dir.exists() || !dir.isDirectory()) {
            return new ArrayList<>();
        }
        
        File[] files = dir.listFiles((file, name) -> name.endsWith(".json"));
        
        if (files == null) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(files)
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Lists all available recipe slugs (names without .json extension)
     */
    public List<String> listRecipeSlugs() {
        return listRecipeFiles().stream()
                .map(SlugUtils::filenameToSlug)
                .collect(Collectors.toList());
    }
    
    /**
     * Lists all available recipes (loads them from files)
     */
    public List<Recipe> listRecipes() throws IOException {
        List<Recipe> recipes = new ArrayList<>();
        List<String> filenames = listRecipeFiles();
        
        for (String filename : filenames) {
            try {
                Recipe recipe = loadRecipe(filename);
                recipes.add(recipe);
            } catch (IOException e) {
                // Skip invalid recipe files and continue
                System.err.println("Warning: Could not load recipe from " + filename + ": " + e.getMessage());
            }
        }
        
        return recipes;
    }
    
    /**
     * Deletes a recipe file by filename
     */
    public boolean deleteRecipe(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        File file = new File(recipesDirectory, filename);
        return file.exists() && file.delete();
    }
    
    /**
     * Deletes a recipe by its slug
     */
    public boolean deleteRecipeBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }
        
        String filename = slug.endsWith(".json") ? slug : slug + ".json";
        return deleteRecipe(filename);
    }
    
    /**
     * Checks if a recipe exists by filename
     */
    public boolean recipeExists(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        File file = new File(recipesDirectory, filename);
        return file.exists() && file.isFile();
    }
    
    /**
     * Checks if a recipe exists by slug
     */
    public boolean recipeExistsBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }
        
        String filename = slug.endsWith(".json") ? slug : slug + ".json";
        return recipeExists(filename);
    }
    
    /**
     * Gets the full path to a recipe file
     */
    public String getRecipeFilePath(String filename) {
        return new File(recipesDirectory, filename).getAbsolutePath();
    }
    
    /**
     * Gets the recipes directory path
     */
    public String getRecipesDirectory() {
        return recipesDirectory;
    }
    
    /**
     * Gets the number of recipes
     */
    public int getRecipeCount() {
        return listRecipeFiles().size();
    }
    
    /**
     * Searches for recipes by name (case-insensitive)
     */
    public List<Recipe> searchRecipesByName(String searchTerm) throws IOException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase();
        
        return listRecipes().stream()
                .filter(recipe -> recipe.getName().toLowerCase().contains(lowerSearchTerm) ||
                               recipe.getSlug().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }
    
    /**
     * Searches for recipes by ingredient (case-insensitive)
     */
    public List<Recipe> searchRecipesByIngredient(String ingredientName) throws IOException {
        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerIngredientName = ingredientName.toLowerCase();
        
        return listRecipes().stream()
                .filter(recipe -> recipe.getIngredients().stream()
                        .anyMatch(ingredient -> 
                            ingredient.getName().toLowerCase().contains(lowerIngredientName) ||
                            ingredient.getSlug().toLowerCase().contains(lowerIngredientName)))
                .collect(Collectors.toList());
    }
}