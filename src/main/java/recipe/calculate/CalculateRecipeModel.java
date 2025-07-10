package recipe.calculate;

import recipe.common.Recipe;
import recipe.common.Ingredient;
import recipe.view.ViewRecipeModel;
import recipe.common.RecipeFileManager;

import java.util.List;

public class CalculateRecipeModel extends ViewRecipeModel {
  
  public CalculateRecipeModel(RecipeFileManager recipeFileManager) {
    super(recipeFileManager);
  }
  
  public Ingredient getFirstPrimeIngredient(Recipe recipe) {
    List<Ingredient> primeIngredients = recipe.getPrimeIngredients();
    if (primeIngredients.isEmpty()) {
      return null;
    }
    return primeIngredients.get(0);
  }
  
  public double calculateScaleFactor(double originalAmount, double newAmount) {
    if (originalAmount <= 0) {
      return 1.0;
    }
    return newAmount / originalAmount;
  }
  
  public Recipe createScaledRecipe(Recipe originalRecipe, double scaleFactor) {
    return originalRecipe.scaled(scaleFactor);
  }
}