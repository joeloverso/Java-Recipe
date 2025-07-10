package recipe.view;

import recipe.common.Recipe;
import recipe.common.RecipeFileManager;
import recipe.common.SlugUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ViewRecipeModel {
  private final RecipeFileManager recipeFileManager;
  private List<Recipe> recipes;
  private String searchQuery = "";

  public ViewRecipeModel(RecipeFileManager recipeFileManager) {
    this.recipeFileManager = recipeFileManager;
    this.recipes = new ArrayList<>();
    loadRecipes();
  }

  private void loadRecipes() {
    try {
      recipes = recipeFileManager.listRecipes();

      // Sort recipes alphabetically by name
      recipes.sort((r1, r2) -> r1.getName().compareToIgnoreCase(r2.getName()));

    } catch (IOException e) {
      System.err.println("Error loading recipes: " + e.getMessage());
      recipes = new ArrayList<>();
    }
  }

  public List<Recipe> getRecipes() {
    return new ArrayList<>(recipes);
  }

  public boolean shouldShowSearch() {
    return recipes.size() > 9;
  }

  public String getSearchQuery() {
    return searchQuery;
  }

  public void setSearchQuery(String query) {
    this.searchQuery = query;
  }

  public Recipe searchRecipeByName(String name) {
    List<Recipe> matches = searchRecipesByName(name);
    return matches.isEmpty() ? null : matches.get(0);
  }

  public List<Recipe> searchRecipesByName(String name) {
    List<Recipe> matches = new ArrayList<>();
    
    if (name == null || name.trim().isEmpty()) {
      return matches;
    }

    // Ensure recipes are loaded
    if (recipes == null || recipes.isEmpty()) {
      loadRecipes();
    }

    // Sanitize input: trim, lowercase
    String searchTerm = name.trim().toLowerCase();
    
    // Try partial name matching - collect all matches
    for (Recipe recipe : recipes) {
      if (recipe == null) continue;
      
      String recipeName = recipe.getName();
      String recipeSlug = recipe.getSlug();
      
      if (recipeName == null || recipeSlug == null) continue;
      
      // Check if the search term appears in the recipe name (case insensitive)
      if (recipeName.toLowerCase().contains(searchTerm) || 
          recipeSlug.toLowerCase().contains(searchTerm)) {
        matches.add(recipe);
      }
    }

    return matches;
  }

  public Recipe getRecipeByNumber(int number) {
    if (number < 1 || number > recipes.size()) {
      return null;
    }
    return recipes.get(number - 1);
  }

  public int getRecipeCount() {
    return recipes.size();
  }

  public void refreshRecipes() {
    loadRecipes();
  }
}
