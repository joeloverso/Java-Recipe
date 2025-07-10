package recipe.edit;

import recipe.common.ScrollableTerminalRenderer;
import recipe.common.Recipe;
import recipe.common.Ingredient;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.List;

/**
 * Renderer for the edit recipe interface
 */
public class EditRecipeRenderer {
  private final ScrollableTerminalRenderer renderer;

  public EditRecipeRenderer(ScrollableTerminalRenderer renderer) {
    this.renderer = renderer;
  }

  /**
   * Renders the welcome message for edit recipe
   */
  public void renderWelcome() throws IOException {
    renderer.clear();
    renderer.printHeader("Edit Recipe");
    renderer.printBlankLine();
    renderer.println("Select a recipe to edit:", TextColor.ANSI.CYAN);
    renderer.printBlankLine();
  }

  /**
   * Renders a message when no recipes are found
   */
  public void renderNoRecipesMessage() throws IOException {
    renderer.printError("No recipes found to edit.");
    renderer.println("Create some recipes first using the 'New Recipe' option from the main menu.");
    renderer.printBlankLine();
  }

  /**
   * Renders confirmation message for editing a recipe
   */
  public void renderEditConfirmation(Recipe recipe) throws IOException {
    renderer.clear();
    renderer.printHeader("Edit Recipe");
    renderer.printBlankLine();
    
    renderer.println("Selected recipe:", TextColor.ANSI.YELLOW);
    renderer.println("  Name: " + recipe.getName(), TextColor.ANSI.CYAN);
    renderer.println("  Servings: " + recipe.getServings(), TextColor.ANSI.CYAN);
    renderer.println("  Ingredients: " + recipe.getIngredients().size(), TextColor.ANSI.CYAN);
    renderer.println("  Instructions: " + recipe.getInstructions().size(), TextColor.ANSI.CYAN);
    renderer.printBlankLine();
  }

  /**
   * Renders the main edit menu
   */
  public void renderEditMenu(EditRecipeModel model) throws IOException {
    Recipe recipe = model.getCurrentRecipe();
    
    renderer.printHeader("Edit: " + recipe.getName());
    renderer.printBlankLine();

    // Show change indicator
    if (model.hasChanges()) {
      renderer.println("● Changes pending", TextColor.ANSI.YELLOW);
      renderer.printBlankLine();
    }

    // Menu options
    renderer.println("Edit Options:", TextColor.ANSI.CYAN);
    renderer.printBlankLine();
    
    renderer.print("1. ", TextColor.ANSI.GREEN);
    renderer.print("Recipe Name", TextColor.ANSI.WHITE);
    renderer.println(" (currently: " + recipe.getName() + ")", TextColor.ANSI.MAGENTA);
    
    renderer.print("2. ", TextColor.ANSI.GREEN);
    renderer.print("Servings", TextColor.ANSI.WHITE);
    renderer.println(" (currently: " + recipe.getServings() + ")", TextColor.ANSI.MAGENTA);
    
    renderer.print("3. ", TextColor.ANSI.GREEN);
    renderer.print("Ingredients", TextColor.ANSI.WHITE);
    renderer.println(" (currently: " + recipe.getIngredients().size() + " items)", TextColor.ANSI.MAGENTA);
    
    renderer.print("4. ", TextColor.ANSI.GREEN);
    renderer.print("Instructions", TextColor.ANSI.WHITE);
    renderer.println(" (currently: " + recipe.getInstructions().size() + " steps)", TextColor.ANSI.MAGENTA);
    
    renderer.printBlankLine();
    
    renderer.print("5. ", TextColor.ANSI.GREEN);
    renderer.println("Review Changes", TextColor.ANSI.WHITE);
    
    renderer.print("6. ", TextColor.ANSI.GREEN);
    renderer.println("Save Changes", TextColor.ANSI.WHITE);
    
    renderer.printBlankLine();
  }

  /**
   * Renders the current value of a field being edited
   */
  public void renderCurrentValue(String fieldName, String currentValue) throws IOException {
    renderer.printSubHeader("Edit " + fieldName);
    renderer.println("Current value: " + currentValue, TextColor.ANSI.CYAN);
    renderer.printBlankLine();
  }

  /**
   * Renders a review of all changes made
   */
  public void renderChangesReview(EditRecipeModel model) throws IOException {
    renderer.printHeader("Review Changes");
    renderer.printBlankLine();

    if (!model.hasChanges()) {
      renderer.println("No changes have been made.", TextColor.ANSI.YELLOW);
      renderer.printBlankLine();
      return;
    }

    renderer.println("Changes Summary:", TextColor.ANSI.CYAN);
    renderer.printBlankLine();

    List<String> changes = model.getChangesSummary();
    for (String change : changes) {
      renderer.print("• ", TextColor.ANSI.GREEN);
      renderer.println(change, TextColor.ANSI.WHITE);
    }
    
    renderer.printBlankLine();
    
    // Show before and after comparison
    renderRecipeComparison(model);
  }

  /**
   * Renders a side-by-side comparison of original and current recipe
   */
  private void renderRecipeComparison(EditRecipeModel model) throws IOException {
    Recipe original = model.getOriginalRecipe();
    Recipe current = model.getCurrentRecipe();
    
    renderer.println("Before and After Comparison:", TextColor.ANSI.CYAN);
    renderer.printBlankLine();
    
    // Name comparison
    renderer.print("Name: ", TextColor.ANSI.YELLOW);
    if (!original.getName().equals(current.getName())) {
      renderer.print(original.getName(), TextColor.ANSI.RED);
      renderer.print(" → ", TextColor.ANSI.WHITE);
      renderer.println(current.getName(), TextColor.ANSI.GREEN);
    } else {
      renderer.println(current.getName(), TextColor.ANSI.WHITE);
    }
    
    // Servings comparison
    renderer.print("Servings: ", TextColor.ANSI.YELLOW);
    if (original.getServings() != current.getServings()) {
      renderer.print(String.valueOf(original.getServings()), TextColor.ANSI.RED);
      renderer.print(" → ", TextColor.ANSI.WHITE);
      renderer.println(String.valueOf(current.getServings()), TextColor.ANSI.GREEN);
    } else {
      renderer.println(String.valueOf(current.getServings()), TextColor.ANSI.WHITE);
    }
    
    // Ingredients comparison
    renderer.print("Ingredients: ", TextColor.ANSI.YELLOW);
    if (original.getIngredients().size() != current.getIngredients().size()) {
      renderer.print(String.valueOf(original.getIngredients().size()), TextColor.ANSI.RED);
      renderer.print(" → ", TextColor.ANSI.WHITE);
      renderer.println(String.valueOf(current.getIngredients().size()), TextColor.ANSI.GREEN);
    } else {
      renderer.println(String.valueOf(current.getIngredients().size()), TextColor.ANSI.WHITE);
    }
    
    // Instructions comparison
    renderer.print("Instructions: ", TextColor.ANSI.YELLOW);
    if (original.getInstructions().size() != current.getInstructions().size()) {
      renderer.print(String.valueOf(original.getInstructions().size()), TextColor.ANSI.RED);
      renderer.print(" → ", TextColor.ANSI.WHITE);
      renderer.println(String.valueOf(current.getInstructions().size()), TextColor.ANSI.GREEN);
    } else {
      renderer.println(String.valueOf(current.getInstructions().size()), TextColor.ANSI.WHITE);
    }
    
    renderer.printBlankLine();
  }

  /**
   * Renders ingredients list for editing
   */
  public void renderIngredientsEdit(EditRecipeModel model) throws IOException {
    renderer.printSubHeader("Edit Ingredients");
    renderer.printBlankLine();

    List<Ingredient> ingredients = model.getCurrentRecipe().getIngredients();
    
    if (ingredients.isEmpty()) {
      renderer.println("No ingredients found.", TextColor.ANSI.YELLOW);
    } else {
      renderer.println("Current ingredients:", TextColor.ANSI.CYAN);
      renderer.printBlankLine();
      
      for (int i = 0; i < ingredients.size(); i++) {
        Ingredient ingredient = ingredients.get(i);
        renderer.print((i + 1) + ". ", TextColor.ANSI.GREEN);
        renderer.print(String.format("%.2f %s %s", 
            ingredient.getAmount(), ingredient.getUnit(), ingredient.getName()), 
            TextColor.ANSI.WHITE);
        if (ingredient.isPrime()) {
          renderer.print(" ★", TextColor.ANSI.YELLOW);
        }
        renderer.println("");
      }
    }
    
    renderer.printBlankLine();
    renderIngredientEditOptions();
  }

  /**
   * Renders ingredient editing options
   */
  private void renderIngredientEditOptions() throws IOException {
    renderer.println("Options:", TextColor.ANSI.CYAN);
    renderer.println("a. Add new ingredient", TextColor.ANSI.WHITE);
    renderer.println("e. Edit ingredient by number", TextColor.ANSI.WHITE);
    renderer.println("d. Delete ingredient by number", TextColor.ANSI.WHITE);
    renderer.println("q. Back to main edit menu", TextColor.ANSI.WHITE);
    renderer.printBlankLine();
  }

  /**
   * Renders instructions list for editing
   */
  public void renderInstructionsEdit(EditRecipeModel model) throws IOException {
    renderer.printSubHeader("Edit Instructions");
    renderer.printBlankLine();

    List<String> instructions = model.getCurrentRecipe().getInstructions();
    
    if (instructions.isEmpty()) {
      renderer.println("No instructions found.", TextColor.ANSI.YELLOW);
    } else {
      renderer.println("Current instructions:", TextColor.ANSI.CYAN);
      renderer.printBlankLine();
      
      for (int i = 0; i < instructions.size(); i++) {
        renderer.print((i + 1) + ". ", TextColor.ANSI.GREEN);
        renderer.println(instructions.get(i), TextColor.ANSI.WHITE);
      }
    }
    
    renderer.printBlankLine();
    renderInstructionEditOptions();
  }

  /**
   * Renders instruction editing options
   */
  private void renderInstructionEditOptions() throws IOException {
    renderer.println("Options:", TextColor.ANSI.CYAN);
    renderer.println("a. Add new instruction", TextColor.ANSI.WHITE);
    renderer.println("e. Edit instruction by number", TextColor.ANSI.WHITE);
    renderer.println("d. Delete instruction by number", TextColor.ANSI.WHITE);
    renderer.println("q. Back to main edit menu", TextColor.ANSI.WHITE);
    renderer.printBlankLine();
  }

  /**
   * Renders a success message
   */
  public void renderSuccess(String message) throws IOException {
    renderer.printSuccess(message);
    renderer.printBlankLine();
  }

  /**
   * Renders an error message
   */
  public void renderError(String message) throws IOException {
    renderer.printError(message);
    renderer.printBlankLine();
  }

  /**
   * Renders an info message
   */
  public void renderInfo(String message) throws IOException {
    renderer.printInfo(message);
    renderer.printBlankLine();
  }

  /**
   * Renders a warning message
   */
  public void renderWarning(String message) throws IOException {
    renderer.printWarning(message);
    renderer.printBlankLine();
  }

  /**
   * Renders validation errors
   */
  public void renderValidationErrors(List<String> errors) throws IOException {
    renderer.println("Please fix the following issues:", TextColor.ANSI.RED);
    for (String error : errors) {
      renderer.printError(error);
    }
    renderer.printBlankLine();
  }
}