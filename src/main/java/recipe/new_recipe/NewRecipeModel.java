package recipe.new_recipe;

import recipe.common.Recipe;
import recipe.common.Ingredient;
import recipe.common.SlugUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for managing the state of a new recipe being created
 */
public class NewRecipeModel {
  private String name;
  private String slug;
  private Double servings;
  private List<Ingredient> ingredients;
  private List<String> instructions;
  private boolean isComplete;

  public NewRecipeModel() {
    this.ingredients = new ArrayList<>();
    this.instructions = new ArrayList<>();
    this.isComplete = false;
  }

  // Recipe name methods
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = SlugUtils.normalizeDisplayName(name);
    this.slug = SlugUtils.toSlug(name);
  }

  public String getSlug() {
    return slug;
  }

  public boolean hasName() {
    return name != null && !name.trim().isEmpty();
  }

  // Servings methods
  public Double getServings() {
    return servings;
  }

  public void setServings(Double servings) {
    this.servings = servings;
  }

  public boolean hasServings() {
    return servings != null && servings > 0;
  }

  // Ingredient methods
  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public void addIngredient(Ingredient ingredient) {
    if (ingredient != null) {
      this.ingredients.add(ingredient);
    }
  }

  public void addIngredient(String name, double amount, String unit) {
    addIngredient(new Ingredient(name, amount, unit));
  }

  public void addIngredient(String name, double amount, String unit, boolean isPrime) {
    addIngredient(new Ingredient(name, amount, unit, isPrime));
  }

  public void removeIngredient(int index) {
    if (index >= 0 && index < ingredients.size()) {
      ingredients.remove(index);
    }
  }

  public Ingredient getIngredient(int index) {
    if (index >= 0 && index < ingredients.size()) {
      return ingredients.get(index);
    }
    return null;
  }

  public boolean hasIngredients() {
    return !ingredients.isEmpty();
  }

  public int getIngredientCount() {
    return ingredients.size();
  }

  public void setPrimeIngredient(int index) {
    if (index >= 0 && index < ingredients.size()) {
      // First, clear all prime flags
      for (Ingredient ingredient : ingredients) {
        ingredient.setPrime(false);
      }
      // Set this ingredient as prime
      ingredients.get(index).setPrime(true);
    }
  }

  public void addPrimeIngredient(int index) {
    if (index >= 0 && index < ingredients.size()) {
      ingredients.get(index).setPrime(true);
    }
  }

  public void removePrimeIngredient(int index) {
    if (index >= 0 && index < ingredients.size()) {
      ingredients.get(index).setPrime(false);
    }
  }

  public List<Ingredient> getPrimeIngredients() {
    return ingredients.stream()
        .filter(Ingredient::isPrime)
        .collect(java.util.stream.Collectors.toList());
  }

  public boolean hasPrimeIngredients() {
    return ingredients.stream().anyMatch(Ingredient::isPrime);
  }

  // Instructions methods
  public List<String> getInstructions() {
    return instructions;
  }

  public void addInstruction(String instruction) {
    if (instruction != null && !instruction.trim().isEmpty()) {
      this.instructions.add(instruction.trim());
    }
  }

  public void removeInstruction(int index) {
    if (index >= 0 && index < instructions.size()) {
      instructions.remove(index);
    }
  }

  public String getInstruction(int index) {
    if (index >= 0 && index < instructions.size()) {
      return instructions.get(index);
    }
    return null;
  }

  public boolean hasInstructions() {
    return !instructions.isEmpty();
  }

  public int getInstructionCount() {
    return instructions.size();
  }

  // Validation methods
  public boolean isValid() {
    return hasName() && hasServings() && hasIngredients() && hasInstructions();
  }

  public List<String> getValidationErrors() {
    List<String> errors = new ArrayList<>();

    if (!hasName()) {
      errors.add("Recipe name is required");
    }

    if (!hasServings()) {
      errors.add("Number of servings is required");
    }

    if (!hasIngredients()) {
      errors.add("At least one ingredient is required");
    }

    if (!hasInstructions()) {
      errors.add("At least one instruction is required");
    }

    return errors;
  }

  // Completion methods
  public boolean isComplete() {
    return isComplete;
  }

  public void setComplete(boolean complete) {
    isComplete = complete;
  }

  // Recipe conversion
  public Recipe toRecipe() {
    if (!isValid()) {
      throw new IllegalStateException("Cannot create recipe from invalid model");
    }

    Recipe recipe = new Recipe(name, servings);

    for (Ingredient ingredient : ingredients) {
      recipe.addIngredient(ingredient);
    }

    for (String instruction : instructions) {
      recipe.addInstruction(instruction);
    }

    return recipe;
  }

  // Reset methods
  public void reset() {
    this.name = null;
    this.slug = null;
    this.servings = null;
    this.ingredients.clear();
    this.instructions.clear();
    this.isComplete = false;
  }

  // Summary methods
  public String getSummary() {
    StringBuilder sb = new StringBuilder();
    sb.append("Recipe: ").append(name != null ? name : "Not set").append("\n");
    sb.append("Servings: ").append(servings != null ? servings : "Not set").append("\n");
    sb.append("Ingredients: ").append(ingredients.size()).append("\n");
    sb.append("Instructions: ").append(instructions.size()).append("\n");
    sb.append("Valid: ").append(isValid() ? "Yes" : "No");
    return sb.toString();
  }
}
