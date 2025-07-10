package recipe.delete;

import recipe.common.Recipe;
import recipe.common.RecipeFileManager;

import java.io.IOException;

/**
 * Model for managing recipe deletion operations
 */
public class DeleteRecipeModel {
  private final RecipeFileManager fileManager;
  private Recipe selectedRecipe;
  private boolean deletionSuccessful;
  private String lastError;

  public DeleteRecipeModel(RecipeFileManager fileManager) {
    this.fileManager = fileManager;
    this.deletionSuccessful = false;
    this.lastError = null;
  }

  /**
   * Sets the recipe to be deleted
   */
  public void setSelectedRecipe(Recipe recipe) {
    this.selectedRecipe = recipe;
    this.deletionSuccessful = false;
    this.lastError = null;
  }

  /**
   * Gets the currently selected recipe
   */
  public Recipe getSelectedRecipe() {
    return selectedRecipe;
  }

  /**
   * Deletes the selected recipe
   */
  public boolean deleteRecipe(Recipe recipe) {
    if (recipe == null) {
      this.lastError = "No recipe specified for deletion";
      this.deletionSuccessful = false;
      return false;
    }

    try {
      // Use the slug-based deletion method which is more reliable
      boolean deleted = fileManager.deleteRecipeBySlug(recipe.getSlug());
      
      if (deleted) {
        this.deletionSuccessful = true;
        this.lastError = null;
        this.selectedRecipe = null; // Clear selected recipe after successful deletion
        return true;
      } else {
        this.lastError = "Recipe file not found or could not be deleted";
        this.deletionSuccessful = false;
        return false;
      }
    } catch (Exception e) {
      this.lastError = "Error during deletion: " + e.getMessage();
      this.deletionSuccessful = false;
      return false;
    }
  }

  /**
   * Checks if the last deletion was successful
   */
  public boolean wasLastDeletionSuccessful() {
    return deletionSuccessful;
  }

  /**
   * Gets the last error message, if any
   */
  public String getLastError() {
    return lastError;
  }

  /**
   * Validates that a recipe can be deleted
   */
  public boolean canDeleteRecipe(Recipe recipe) {
    if (recipe == null) {
      this.lastError = "No recipe specified";
      return false;
    }

    if (recipe.getSlug() == null || recipe.getSlug().trim().isEmpty()) {
      this.lastError = "Recipe has invalid slug";
      return false;
    }

    // Check if the recipe file exists
    boolean exists = fileManager.recipeExistsBySlug(recipe.getSlug());
    if (!exists) {
      this.lastError = "Recipe file does not exist";
      return false;
    }

    return true;
  }

  /**
   * Gets validation errors for the specified recipe
   */
  public String getValidationError(Recipe recipe) {
    if (canDeleteRecipe(recipe)) {
      return null;
    }
    return lastError;
  }

  /**
   * Resets the model state
   */
  public void reset() {
    this.selectedRecipe = null;
    this.deletionSuccessful = false;
    this.lastError = null;
  }

  /**
   * Gets a summary of the deletion operation
   */
  public String getDeletionSummary() {
    if (deletionSuccessful) {
      return "Recipe deleted successfully";
    } else if (lastError != null) {
      return "Deletion failed: " + lastError;
    } else {
      return "No deletion operation performed";
    }
  }
}