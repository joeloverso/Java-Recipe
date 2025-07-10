package recipe.new_recipe;

import recipe.common.Terminal;
import recipe.common.ScrollableTerminalRenderer;
import recipe.common.PromptBasedController;
import recipe.common.RecipeFileManager;
import recipe.common.Recipe;
import recipe.common.Ingredient;
import recipe.common.SlugUtils;

import java.io.IOException;
import java.util.List;

// Controller for the new recipe creation flow
public class NewRecipeController {
  private final Terminal terminal;
  private final ScrollableTerminalRenderer renderer;
  private final PromptBasedController promptController;
  private final NewRecipeModel model;
  private final NewRecipeRenderer recipeRenderer;
  private final RecipeFileManager fileManager;

  public NewRecipeController(Terminal terminal) {
    this.terminal = terminal;
    this.renderer = new ScrollableTerminalRenderer(terminal);
    this.promptController = new PromptBasedController(terminal, renderer);
    this.model = new NewRecipeModel();
    this.recipeRenderer = new NewRecipeRenderer(renderer);
    this.fileManager = new RecipeFileManager();
  }

  // Runs the complete new recipe creation flow
  public void createNewRecipe() throws IOException {
    try {
      // Welcome message
      recipeRenderer.renderWelcome();

      // Get recipe name
      if (!promptForRecipeName()) {
        recipeRenderer.renderCancellation();
        promptController.waitForEnter();
        return;
      }
      renderer.printBlankLine();

      // Get number of servings
      if (!promptForServings()) {
        recipeRenderer.renderCancellation();
        promptController.waitForEnter();
        return;
      }
      renderer.printBlankLine();

      // Get instructions
      if (!promptForInstructions()) {
        recipeRenderer.renderCancellation();
        promptController.waitForEnter();
        return;
      }
      renderer.printBlankLine();

      // Get ingredients
      if (!promptForIngredients()) {
        recipeRenderer.renderCancellation();
        promptController.waitForEnter();
        return;
      }

      // Review and save
      if (!reviewAndSave()) {
        recipeRenderer.renderCancellation();
        promptController.waitForEnter();
        return;
      }

    } catch (Exception e) {
      recipeRenderer.renderError("An error occurred: " + e.getMessage());
      promptController.waitForEnter();
    }
  }

  // Prompts for recipe name
  private boolean promptForRecipeName() throws IOException {
    String name = promptController.promptForText("Enter recipe name: ",
        input -> SlugUtils.isValidName(input) && !isRecipeNameTaken(input));

    if (name == null) {
      return false; // User cancelled
    }

    model.setName(name);

    return true;
  }

  // Checks if a recipe with the given name already exists
  private boolean isRecipeNameTaken(String name) {
    if (name == null || name.trim().isEmpty()) {
      return false;
    }
    
    String slug = SlugUtils.toSlug(name);
    boolean exists = fileManager.recipeExistsBySlug(slug);
    
    if (exists) {
      try {
        recipeRenderer.renderError("A recipe with the name '" + name + "' already exists. Please choose a different name.");
      } catch (IOException e) {
        // If we can't render the error, we still need to return that the name is taken
        System.err.println("Error rendering message: " + e.getMessage());
      }
    }
    
    return exists;
  }

  // Prompts for number of servings
  private boolean promptForServings() throws IOException {
    Double servings = promptController.promptForDouble("Number of servings: ");

    if (servings == null) {
      return false; // User cancelled
    }

    model.setServings(servings);

    return true;
  }

  // Prompts for ingredients
  private boolean promptForIngredients() throws IOException {
    renderer.printSubHeader("Now let's add ingredients:");
    boolean firstIngredient = true;

    while (true) {
      // Show current ingredient count
      if (!firstIngredient) {
        recipeRenderer.renderIngredientCount(model.getIngredientCount());
        renderer.printBlankLine();
        boolean addAnother = promptController.promptForConfirmation("Add another ingredient?", true);
        if (!addAnother) {
          break;
        }
      }

      // Get ingredient details
      String name = promptController.promptForText("Ingredient name:");

      if (name == null) {
        return false; // User cancelled
      }

      String unit = promptController.promptForText("Unit of measurement for " + name + " (e.g., cups, tbsp, lbs):");

      if (unit == null) {
        return false; // User cancelled
      }

      Double amount = promptController.promptForDouble("Quantity of " + name + " in " + unit + ":");

      if (amount == null) {
        return false; // User cancelled
      }

      // Create ingredient
      Ingredient ingredient = new Ingredient(name, amount, unit);

      // Mark first ingredient as prime
      if (firstIngredient) {
        ingredient.setPrime(true);
      }

      model.addIngredient(ingredient);
      recipeRenderer.renderIngredientAdded(ingredient);

      firstIngredient = false;
    }

    if (!model.hasIngredients()) {
      recipeRenderer.renderError("At least one ingredient is required.");
      return false;
    }

    return true;
  }

  // Prompts for instructions
  private boolean promptForInstructions() throws IOException {
    List<String> instructions = promptController.promptForMultiLineText("Enter recipe notes/instructions:");

    if (instructions == null) {
      return false; // User cancelled
    }

    for (String instruction : instructions) {
      model.addInstruction(instruction);
    }

    return true;
  }

  // Reviews the recipe and saves it
  private boolean reviewAndSave() throws IOException {
    // Show review
    recipeRenderer.renderReviewSection(model);

    // Validate the model
    if (!model.isValid()) {
      List<String> errors = model.getValidationErrors();
      recipeRenderer.renderValidationErrors(errors);
      return false;
    }

    // Confirm save
    boolean shouldSave = promptController.promptForConfirmation("Save this recipe?", true);
    renderer.printBlankLine();

    if (!shouldSave) {
      return false;
    }

    try {
      // Convert to Recipe object
      Recipe recipe = model.toRecipe();

      // Save to file
      String filename = fileManager.saveRecipe(recipe);

      // Show success message
      recipeRenderer.renderCompletion(filename);

      // Show final formatted recipe
      recipeRenderer.renderFormattedRecipe(recipe);

      promptController.waitForEnter();

      return true;

    } catch (Exception e) {
      recipeRenderer.renderError("Failed to save recipe: " + e.getMessage());
      return false;
    }
  }
}
