package recipe.edit;

import recipe.common.Recipe;
import recipe.common.Ingredient;
import recipe.common.RecipeFileManager;
import recipe.common.SlugUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for managing the state of a recipe being edited
 */
public class EditRecipeModel {
  private final RecipeFileManager fileManager;
  private Recipe originalRecipe;
  private Recipe currentRecipe;
  private boolean hasChanges;

  public EditRecipeModel(RecipeFileManager fileManager) {
    this.fileManager = fileManager;
    this.hasChanges = false;
  }

  /**
   * Loads a recipe for editing
   */
  public void loadRecipe(Recipe recipe) {
    this.originalRecipe = recipe;
    // Create a deep copy for editing
    this.currentRecipe = createRecipeCopy(recipe);
    this.hasChanges = false;
  }

  /**
   * Creates a deep copy of a recipe for editing
   */
  private Recipe createRecipeCopy(Recipe original) {
    Recipe copy = new Recipe(original.getName(), original.getServings());
    
    // Copy ingredients
    for (Ingredient ingredient : original.getIngredients()) {
      copy.addIngredient(new Ingredient(
          ingredient.getName(),
          ingredient.getAmount(),
          ingredient.getUnit(),
          ingredient.isPrime()
      ));
    }
    
    // Copy instructions
    for (String instruction : original.getInstructions()) {
      copy.addInstruction(instruction);
    }
    
    return copy;
  }

  /**
   * Gets the original recipe (read-only)
   */
  public Recipe getOriginalRecipe() {
    return originalRecipe;
  }

  /**
   * Gets the current recipe being edited
   */
  public Recipe getCurrentRecipe() {
    return currentRecipe;
  }

  /**
   * Checks if there are any changes
   */
  public boolean hasChanges() {
    return hasChanges;
  }

  /**
   * Updates the recipe name
   */
  public void setName(String name) {
    if (name != null && !name.trim().isEmpty()) {
      String normalizedName = SlugUtils.normalizeDisplayName(name);
      currentRecipe.setName(normalizedName);
      checkForChanges();
    }
  }

  /**
   * Updates the servings
   */
  public void setServings(double servings) {
    if (servings > 0) {
      currentRecipe.setServings(servings);
      checkForChanges();
    }
  }

  /**
   * Adds an ingredient to the recipe
   */
  public void addIngredient(Ingredient ingredient) {
    if (ingredient != null) {
      currentRecipe.addIngredient(ingredient);
      checkForChanges();
    }
  }

  /**
   * Removes an ingredient by index
   */
  public void removeIngredient(int index) {
    List<Ingredient> ingredients = currentRecipe.getIngredients();
    if (index >= 0 && index < ingredients.size()) {
      ingredients.remove(index);
      checkForChanges();
    }
  }

  /**
   * Updates an ingredient by index
   */
  public void updateIngredient(int index, Ingredient newIngredient) {
    List<Ingredient> ingredients = currentRecipe.getIngredients();
    if (index >= 0 && index < ingredients.size() && newIngredient != null) {
      ingredients.set(index, newIngredient);
      checkForChanges();
    }
  }

  /**
   * Gets ingredient by index
   */
  public Ingredient getIngredient(int index) {
    List<Ingredient> ingredients = currentRecipe.getIngredients();
    if (index >= 0 && index < ingredients.size()) {
      return ingredients.get(index);
    }
    return null;
  }

  /**
   * Gets the number of ingredients
   */
  public int getIngredientCount() {
    return currentRecipe.getIngredients().size();
  }

  /**
   * Adds an instruction to the recipe
   */
  public void addInstruction(String instruction) {
    if (instruction != null && !instruction.trim().isEmpty()) {
      currentRecipe.addInstruction(instruction.trim());
      checkForChanges();
    }
  }

  /**
   * Removes an instruction by index
   */
  public void removeInstruction(int index) {
    List<String> instructions = currentRecipe.getInstructions();
    if (index >= 0 && index < instructions.size()) {
      instructions.remove(index);
      checkForChanges();
    }
  }

  /**
   * Updates an instruction by index
   */
  public void updateInstruction(int index, String newInstruction) {
    List<String> instructions = currentRecipe.getInstructions();
    if (index >= 0 && index < instructions.size() && newInstruction != null && !newInstruction.trim().isEmpty()) {
      instructions.set(index, newInstruction.trim());
      checkForChanges();
    }
  }

  /**
   * Gets instruction by index
   */
  public String getInstruction(int index) {
    List<String> instructions = currentRecipe.getInstructions();
    if (index >= 0 && index < instructions.size()) {
      return instructions.get(index);
    }
    return null;
  }

  /**
   * Gets the number of instructions
   */
  public int getInstructionCount() {
    return currentRecipe.getInstructions().size();
  }

  /**
   * Checks if the current recipe has changes compared to the original
   */
  private void checkForChanges() {
    hasChanges = !recipesEqual(originalRecipe, currentRecipe);
  }

  /**
   * Compares two recipes for equality
   */
  private boolean recipesEqual(Recipe recipe1, Recipe recipe2) {
    if (recipe1 == null && recipe2 == null) {
      return true;
    }
    if (recipe1 == null || recipe2 == null) {
      return false;
    }

    // Compare basic properties
    if (!recipe1.getName().equals(recipe2.getName())) {
      return false;
    }
    if (recipe1.getServings() != recipe2.getServings()) {
      return false;
    }

    // Compare ingredients
    List<Ingredient> ingredients1 = recipe1.getIngredients();
    List<Ingredient> ingredients2 = recipe2.getIngredients();
    if (ingredients1.size() != ingredients2.size()) {
      return false;
    }
    for (int i = 0; i < ingredients1.size(); i++) {
      if (!ingredientsEqual(ingredients1.get(i), ingredients2.get(i))) {
        return false;
      }
    }

    // Compare instructions
    List<String> instructions1 = recipe1.getInstructions();
    List<String> instructions2 = recipe2.getInstructions();
    if (instructions1.size() != instructions2.size()) {
      return false;
    }
    for (int i = 0; i < instructions1.size(); i++) {
      if (!instructions1.get(i).equals(instructions2.get(i))) {
        return false;
      }
    }

    return true;
  }

  /**
   * Compares two ingredients for equality
   */
  private boolean ingredientsEqual(Ingredient ingredient1, Ingredient ingredient2) {
    if (ingredient1 == null && ingredient2 == null) {
      return true;
    }
    if (ingredient1 == null || ingredient2 == null) {
      return false;
    }

    return ingredient1.getName().equals(ingredient2.getName()) &&
           ingredient1.getAmount() == ingredient2.getAmount() &&
           ingredient1.getUnit().equals(ingredient2.getUnit()) &&
           ingredient1.isPrime() == ingredient2.isPrime();
  }

  /**
   * Gets a summary of changes made
   */
  public List<String> getChangesSummary() {
    List<String> changes = new ArrayList<>();
    
    if (!hasChanges) {
      changes.add("No changes made.");
      return changes;
    }

    // Check name changes
    if (!originalRecipe.getName().equals(currentRecipe.getName())) {
      changes.add("Name: '" + originalRecipe.getName() + "' → '" + currentRecipe.getName() + "'");
    }

    // Check servings changes
    if (originalRecipe.getServings() != currentRecipe.getServings()) {
      changes.add("Servings: " + originalRecipe.getServings() + " → " + currentRecipe.getServings());
    }

    // Check ingredient changes
    List<Ingredient> originalIngredients = originalRecipe.getIngredients();
    List<Ingredient> currentIngredients = currentRecipe.getIngredients();
    
    if (originalIngredients.size() != currentIngredients.size()) {
      changes.add("Ingredients: " + originalIngredients.size() + " → " + currentIngredients.size());
    } else {
      // Check for modified ingredients
      for (int i = 0; i < originalIngredients.size(); i++) {
        if (!ingredientsEqual(originalIngredients.get(i), currentIngredients.get(i))) {
          changes.add("Ingredient " + (i + 1) + " modified");
        }
      }
    }

    // Check instruction changes
    List<String> originalInstructions = originalRecipe.getInstructions();
    List<String> currentInstructions = currentRecipe.getInstructions();
    
    if (originalInstructions.size() != currentInstructions.size()) {
      changes.add("Instructions: " + originalInstructions.size() + " → " + currentInstructions.size());
    } else {
      // Check for modified instructions
      for (int i = 0; i < originalInstructions.size(); i++) {
        if (!originalInstructions.get(i).equals(currentInstructions.get(i))) {
          changes.add("Instruction " + (i + 1) + " modified");
        }
      }
    }

    return changes;
  }

  /**
   * Validates the current recipe
   */
  public boolean isValid() {
    return currentRecipe != null && currentRecipe.isValid();
  }

  /**
   * Gets validation errors for the current recipe
   */
  public List<String> getValidationErrors() {
    List<String> errors = new ArrayList<>();
    
    if (currentRecipe == null) {
      errors.add("No recipe loaded for editing");
      return errors;
    }
    
    if (currentRecipe.getName() == null || currentRecipe.getName().trim().isEmpty()) {
      errors.add("Recipe name is required");
    }
    
    if (currentRecipe.getServings() <= 0) {
      errors.add("Servings must be greater than 0");
    }
    
    if (currentRecipe.getIngredients().isEmpty()) {
      errors.add("At least one ingredient is required");
    }
    
    if (currentRecipe.getInstructions().isEmpty()) {
      errors.add("At least one instruction is required");
    }
    
    return errors;
  }

  /**
   * Resets the current recipe to the original state
   */
  public void resetToOriginal() {
    if (originalRecipe != null) {
      currentRecipe = createRecipeCopy(originalRecipe);
      hasChanges = false;
    }
  }
}