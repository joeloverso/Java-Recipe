package recipe.new_recipe;

import recipe.common.ScrollableTerminalRenderer;
import recipe.common.Ingredient;
import recipe.common.Recipe;
import com.googlecode.lanterna.TextColor;
import java.io.IOException;
import java.util.List;

/**
 * Renderer for the new recipe creation interface
 */
public class NewRecipeRenderer {
  private final ScrollableTerminalRenderer renderer;

  public NewRecipeRenderer(ScrollableTerminalRenderer renderer) {
    this.renderer = renderer;
  }

  /**
   * Renders the welcome message for new recipe creation
   */
  public void renderWelcome() throws IOException {
    // Force a complete terminal reset to ensure clean state
    renderer.clear();

    renderer.printHeader("Create New Recipe");
    renderer.printBlankLine();
  }

  /**
   * Renders a simple success message for adding ingredients
   */
  public void renderIngredientAdded(Ingredient ingredient) throws IOException {
    renderer.printSuccess("Added: " + String.format("%.2f %s %s",
        ingredient.getAmount(), ingredient.getUnit(), ingredient.getName()));
  }

  /**
   * Renders a simple success message for adding instructions
   */
  public void renderInstructionAdded(int stepNumber) throws IOException {
    renderer.printSuccess("Added step " + stepNumber);
  }

  /**
   * Renders a compact ingredient count (like the Python version)
   */
  public void renderIngredientCount(int count) throws IOException {
    if (count > 0) {
      renderer.println("(" + count + " ingredients added)", TextColor.ANSI.GREEN);
    }
  }

  /**
   * Renders a compact instruction count
   */
  public void renderInstructionCount(int count) throws IOException {
    if (count > 0) {
      renderer.println("(" + count + " instructions added)", TextColor.ANSI.GREEN);
    }
  }

  /**
   * Renders the recipe review section
   */
  public void renderReviewSection(NewRecipeModel model) throws IOException {
    renderer.printHeader("Recipe Preview");
    renderer.printBlankLine();

    // Recipe name
    renderer.print("Name: ", TextColor.ANSI.YELLOW);
    renderer.println(model.getName(), TextColor.ANSI.WHITE);
    renderer.printBlankLine();

    // Ingredients
    renderer.println("Ingredients:", TextColor.ANSI.YELLOW);
    printIngredientTable(model.getIngredients());
    renderer.printBlankLine();

    // Instructions
    renderer.println("Instructions:", TextColor.ANSI.YELLOW);
    for (String instruction : model.getInstructions()) {
      renderer.printListItem(instruction);
    }
    renderer.printBlankLine();

    // Prime ingredients info (optional, if still needed)
    List<Ingredient> primeIngredients = model.getPrimeIngredients();
    if (!primeIngredients.isEmpty()) {
      renderer.println("Prime ingredients (used for scaling):", TextColor.ANSI.MAGENTA);
      for (Ingredient ingredient : primeIngredients) {
        renderer.printListItem(ingredient.getName());
      }
      renderer.printBlankLine();
    }
  }

  private void printIngredientTable(List<Ingredient> ingredients) throws IOException {
    // Calculate column widths
    int nameWidth = "Ingredient".length();
    int quantityWidth = "Quantity".length();
    int unitWidth = "Unit".length();

    for (Ingredient ingredient : ingredients) {
      nameWidth = Math.max(nameWidth, ingredient.getName().length());
      quantityWidth = Math.max(quantityWidth, String.format("%.2f", ingredient.getAmount()).length());
      unitWidth = Math.max(unitWidth, ingredient.getUnit().length());
    }

    // Add padding
    nameWidth += 2;
    quantityWidth += 2;
    unitWidth += 2;

    // Print header
    renderer.println("┏" + "━".repeat(nameWidth) + "┳" + "━".repeat(quantityWidth) + "┳" + "━".repeat(unitWidth) + "┓");

    // Print header row with colored column titles
    renderer.print("┃ ", TextColor.ANSI.DEFAULT);
    renderer.print(padRight("Ingredient", nameWidth - 1), TextColor.ANSI.MAGENTA);
    renderer.print("┃ ", TextColor.ANSI.DEFAULT);
    renderer.print(padRight("Quantity", quantityWidth - 1), TextColor.ANSI.MAGENTA);
    renderer.print("┃ ", TextColor.ANSI.DEFAULT);
    renderer.print(padRight("Unit", unitWidth - 1), TextColor.ANSI.MAGENTA);
    renderer.println("┃", TextColor.ANSI.DEFAULT);

    renderer.println("┣" + "━".repeat(nameWidth) + "╋" + "━".repeat(quantityWidth) + "╋" + "━".repeat(unitWidth) + "┫");

    // Print ingredients with colored values
    for (Ingredient ingredient : ingredients) {
      renderer.print("┃ ", TextColor.ANSI.DEFAULT);
      renderer.print(padRight(ingredient.getName(), nameWidth - 1), TextColor.ANSI.CYAN);
      renderer.print("┃ ", TextColor.ANSI.DEFAULT);
      renderer.print(padRight(String.format("%.2f", ingredient.getAmount()), quantityWidth - 1), TextColor.ANSI.GREEN);
      renderer.print("┃ ", TextColor.ANSI.DEFAULT);
      renderer.print(padRight(ingredient.getUnit(), unitWidth - 1), TextColor.ANSI.YELLOW);
      renderer.println("┃", TextColor.ANSI.DEFAULT);
    }

    // Print footer
    renderer.println("┗" + "━".repeat(nameWidth) + "┻" + "━".repeat(quantityWidth) + "┻" + "━".repeat(unitWidth) + "┛");
  }

  private String padRight(String s, int n) {
    return String.format("%-" + n + "s", s);
  }

  public void renderSaveSection(String filename) throws IOException {
    renderer.printSubHeader("Save Recipe");
    renderer.println("Your recipe will be saved as: " + filename);
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
   * Renders a warning message
   */
  public void renderWarning(String message) throws IOException {
    renderer.printWarning(message);
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
   * Renders validation errors
   */
  public void renderValidationErrors(List<String> errors) throws IOException {
    renderer.println("Please fix the following issues:", TextColor.ANSI.RED);
    for (String error : errors) {
      renderer.printError(error);
    }
    renderer.printBlankLine();
  }

  /**
   * Renders the cancellation message
   */
  public void renderCancellation() throws IOException {
    renderer.printWarning("Recipe creation cancelled.");
    renderer.println("Returning to main menu...");
    renderer.printBlankLine();
  }

  /**
   * Renders the completion message
   */
  public void renderCompletion(String filename) throws IOException {
    renderer.printSuccess("Recipe created successfully!");
    renderer.println("Saved as: " + filename);
    renderer.printBlankLine();
    renderer.println("Returning to main menu...");
  }

  /**
   * Renders a custom formatted recipe (for final display)
   */
  public void renderFormattedRecipe(Recipe recipe) throws IOException {
    renderer.printHeader(recipe.getName());
    renderer.printBlankLine();

    renderer.println("Ingredients:", TextColor.ANSI.CYAN);
    printIngredientTable(recipe.getIngredients());
    renderer.printBlankLine();

    renderer.println("Instructions:", TextColor.ANSI.CYAN);
    for (String instruction : recipe.getInstructions()) {
      renderer.printListItem(instruction);
    }
    renderer.printBlankLine();
  }
}
