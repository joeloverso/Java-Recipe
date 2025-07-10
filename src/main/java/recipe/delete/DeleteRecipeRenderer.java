package recipe.delete;

import recipe.common.ScrollableTerminalRenderer;
import recipe.common.Recipe;
import recipe.common.Ingredient;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Renderer for the delete recipe interface with warning-focused design
 */
public class DeleteRecipeRenderer {
  private final ScrollableTerminalRenderer renderer;

  public DeleteRecipeRenderer(ScrollableTerminalRenderer renderer) {
    this.renderer = renderer;
  }

  /**
   * Renders the welcome message for delete recipe
   */
  public void renderWelcome() throws IOException {
    renderer.clear();
    renderer.printHeader("Delete Recipe");
    renderer.printBlankLine();
    
    // Warning message about destructive operation
    renderer.println("⚠ WARNING: This operation will permanently delete a recipe ⚠", TextColor.ANSI.RED);
    renderer.printBlankLine();
    renderer.println("Select a recipe to delete:", TextColor.ANSI.CYAN);
    renderer.printBlankLine();
  }

  /**
   * Renders a message when no recipes are found
   */
  public void renderNoRecipesMessage() throws IOException {
    renderer.printError("No recipes found to delete.");
    renderer.println("Create some recipes first using the 'New Recipe' option from the main menu.");
    renderer.printBlankLine();
  }

  /**
   * Renders the deletion warning with recipe details
   */
  public void renderDeletionWarning(Recipe recipe) throws IOException {
    renderer.printHeader("⚠ DELETE RECIPE WARNING ⚠");
    renderer.printBlankLine();
    
    // Major warning message
    renderer.println("═══════════════════════════════════════════════", TextColor.ANSI.RED);
    renderer.println("             PERMANENT DELETION WARNING            ", TextColor.ANSI.RED);
    renderer.println("═══════════════════════════════════════════════", TextColor.ANSI.RED);
    renderer.printBlankLine();
    
    renderer.println("You are about to PERMANENTLY DELETE this recipe:", TextColor.ANSI.RED);
    renderer.printBlankLine();
    
    // Recipe details with dynamic red border
    renderRecipeDetailsWithRedBorder(recipe);
    
    // Warning messages
    renderer.println("⚠ THIS ACTION CANNOT BE UNDONE!", TextColor.ANSI.RED);
    renderer.println("⚠ The recipe file will be permanently deleted from your system.", TextColor.ANSI.RED);
    renderer.println("⚠ All recipe data will be lost forever.", TextColor.ANSI.RED);
    renderer.printBlankLine();
  }


  /**
   * Renders a success message
   */
  public void renderSuccess(String message) throws IOException {
    renderer.printBlankLine();
    renderer.println("✓ " + message, TextColor.ANSI.GREEN);
    renderer.printBlankLine();
  }

  /**
   * Renders an error message
   */
  public void renderError(String message) throws IOException {
    renderer.printBlankLine();
    renderer.println("✗ " + message, TextColor.ANSI.RED);
    renderer.printBlankLine();
  }

  /**
   * Renders an info message
   */
  public void renderInfo(String message) throws IOException {
    renderer.printBlankLine();
    renderer.println("ℹ " + message, TextColor.ANSI.CYAN);
    renderer.printBlankLine();
  }

  /**
   * Renders a warning message
   */
  public void renderWarning(String message) throws IOException {
    renderer.printBlankLine();
    renderer.println("⚠ " + message, TextColor.ANSI.YELLOW);
    renderer.printBlankLine();
  }

  /**
   * Renders the recipe ingredients in a compact format for deletion preview
   */
  public void renderIngredientsPreview(Recipe recipe) throws IOException {
    List<Ingredient> ingredients = recipe.getIngredients();
    
    if (ingredients.isEmpty()) {
      renderer.println("No ingredients", TextColor.ANSI.YELLOW);
      return;
    }
    
    renderer.println("Ingredients:", TextColor.ANSI.CYAN);
    for (int i = 0; i < Math.min(ingredients.size(), 3); i++) {
      Ingredient ingredient = ingredients.get(i);
      renderer.print("• ", TextColor.ANSI.WHITE);
      renderer.println(String.format("%.1f %s %s", 
          ingredient.getAmount(), ingredient.getUnit(), ingredient.getName()), 
          TextColor.ANSI.WHITE);
    }
    
    if (ingredients.size() > 3) {
      renderer.println("... and " + (ingredients.size() - 3) + " more", TextColor.ANSI.MAGENTA);
    }
  }

  /**
   * Renders the recipe instructions in a compact format for deletion preview
   */
  public void renderInstructionsPreview(Recipe recipe) throws IOException {
    List<String> instructions = recipe.getInstructions();
    
    if (instructions.isEmpty()) {
      renderer.println("No instructions", TextColor.ANSI.YELLOW);
      return;
    }
    
    renderer.println("Instructions:", TextColor.ANSI.CYAN);
    for (int i = 0; i < Math.min(instructions.size(), 2); i++) {
      String instruction = instructions.get(i);
      String truncated = instruction.length() > 50 ? instruction.substring(0, 47) + "..." : instruction;
      renderer.print((i + 1) + ". ", TextColor.ANSI.WHITE);
      renderer.println(truncated, TextColor.ANSI.WHITE);
    }
    
    if (instructions.size() > 2) {
      renderer.println("... and " + (instructions.size() - 2) + " more steps", TextColor.ANSI.MAGENTA);
    }
  }

  /**
   * Renders deletion cancelled message
   */
  public void renderDeletionCancelled() throws IOException {
    renderer.printBlankLine();
    renderer.println("✓ Deletion cancelled. Recipe is safe.", TextColor.ANSI.GREEN);
    renderer.println("Returning to main menu...", TextColor.ANSI.CYAN);
    renderer.printBlankLine();
  }

  /**
   * Renders recipe details with dynamic red border (adapted from ViewRecipeRenderer)
   */
  private void renderRecipeDetailsWithRedBorder(Recipe recipe) throws IOException {
    renderer.println("Recipe Details:", TextColor.ANSI.YELLOW);
    renderer.printBlankLine();
    
    // Title with red border
    String title = " " + recipe.getName() + " ";
    int titleWidth = Math.max(title.length() + 4, 40); // Minimum width for readability
    
    String topBorder = "┏━" + title + "━".repeat(titleWidth - title.length() - 1) + "┓";
    renderer.println(topBorder, TextColor.ANSI.RED);
    
    // Empty line after header
    String emptyLine = "┃" + " ".repeat(titleWidth) + "┃";
    renderer.println(emptyLine, TextColor.ANSI.RED);
    
    // Servings
    String servingsInfo = String.format("Servings: %.0f", recipe.getServings());
    renderer.print("┃  ", TextColor.ANSI.RED);
    renderer.print(String.format("%-" + (titleWidth - 4) + "s", servingsInfo), TextColor.ANSI.WHITE);
    renderer.println("  ┃", TextColor.ANSI.RED);
    
    // Empty line
    renderer.println(emptyLine, TextColor.ANSI.RED);
    
    // Ingredients count
    String ingredientsInfo = String.format("Ingredients: %d items", recipe.getIngredients().size());
    renderer.print("┃  ", TextColor.ANSI.RED);
    renderer.print(String.format("%-" + (titleWidth - 4) + "s", ingredientsInfo), TextColor.ANSI.WHITE);
    renderer.println("  ┃", TextColor.ANSI.RED);
    
    // Instructions count
    String instructionsInfo = String.format("Instructions: %d steps", recipe.getInstructions().size());
    renderer.print("┃  ", TextColor.ANSI.RED);
    renderer.print(String.format("%-" + (titleWidth - 4) + "s", instructionsInfo), TextColor.ANSI.WHITE);
    renderer.println("  ┃", TextColor.ANSI.RED);
    
    // Empty line before ingredients
    renderer.println(emptyLine, TextColor.ANSI.RED);
    
    // Ingredients table with red border
    drawIngredientsTableWithRedBorder(recipe, titleWidth);
    
    // Empty line before instructions
    renderer.println(emptyLine, TextColor.ANSI.RED);
    
    // Instructions with red border
    drawInstructionsWithRedBorder(recipe, titleWidth);
    
    // Empty line before bottom border
    renderer.println(emptyLine, TextColor.ANSI.RED);
    
    // Bottom border
    String bottomBorder = "┗" + "━".repeat(titleWidth) + "┛";
    renderer.println(bottomBorder, TextColor.ANSI.RED);
    renderer.printBlankLine();
  }

  /**
   * Draws ingredients table with red border (adapted from ViewRecipeRenderer)
   */
  private void drawIngredientsTableWithRedBorder(Recipe recipe, int containerWidth) throws IOException {
    if (recipe.getIngredients().isEmpty()) {
      renderer.print("┃  ", TextColor.ANSI.RED);
      renderer.print(String.format("%-" + (containerWidth - 4) + "s", "No ingredients"), TextColor.ANSI.YELLOW);
      renderer.println("  ┃", TextColor.ANSI.RED);
      return;
    }
    
    // Calculate column widths (adapted from ViewRecipeRenderer)
    int ingredientWidth = calculateIngredientColumnWidth(recipe);
    int amountWidth = calculateAmountColumnWidth(recipe);
    int unitWidth = calculateUnitColumnWidth(recipe);
    
    // Headers
    String headers = String.format("%-" + ingredientWidth + "s  %-" + amountWidth + "s  %-" + unitWidth + "s", 
        "Ingredient", "Amount", "Unit");
    renderer.print("┃  ", TextColor.ANSI.RED);
    renderer.print(String.format("%-" + (containerWidth - 4) + "s", headers), TextColor.ANSI.YELLOW);
    renderer.println("  ┃", TextColor.ANSI.RED);
    
    // Ingredient rows (show max 5 for deletion preview)
    int maxRows = Math.min(recipe.getIngredients().size(), 5);
    for (int i = 0; i < maxRows; i++) {
      Ingredient ingredient = recipe.getIngredients().get(i);
      
      String amountStr = String.format("%.1f", ingredient.getAmount()).replaceAll("\\.0$", "");
      String row = String.format("%-" + ingredientWidth + "s  %-" + amountWidth + "s  %-" + unitWidth + "s", 
          ingredient.getName(), amountStr, ingredient.getUnit());
      
      renderer.print("┃  ", TextColor.ANSI.RED);
      renderer.print(String.format("%-" + (containerWidth - 4) + "s", row), TextColor.ANSI.WHITE);
      renderer.println("  ┃", TextColor.ANSI.RED);
    }
    
    // Show "and X more" if there are more ingredients
    if (recipe.getIngredients().size() > 5) {
      String moreInfo = "... and " + (recipe.getIngredients().size() - 5) + " more";
      renderer.print("┃  ", TextColor.ANSI.RED);
      renderer.print(String.format("%-" + (containerWidth - 4) + "s", moreInfo), TextColor.ANSI.MAGENTA);
      renderer.println("  ┃", TextColor.ANSI.RED);
    }
  }

  /**
   * Draws instructions with red border (adapted from ViewRecipeRenderer)
   */
  private void drawInstructionsWithRedBorder(Recipe recipe, int containerWidth) throws IOException {
    if (recipe.getInstructions().isEmpty()) {
      renderer.print("┃  ", TextColor.ANSI.RED);
      renderer.print(String.format("%-" + (containerWidth - 4) + "s", "No instructions"), TextColor.ANSI.YELLOW);
      renderer.println("  ┃", TextColor.ANSI.RED);
      return;
    }
    
    // Show max 3 instructions for deletion preview
    int maxInstructions = Math.min(recipe.getInstructions().size(), 3);
    for (int i = 0; i < maxInstructions; i++) {
      String instruction = String.format("%d. %s", i + 1, recipe.getInstructions().get(i));
      
      // Wrap text to fit within the container width (minus 4 for padding and borders)
      String[] wrappedLines = wrapText(instruction, containerWidth - 4);
      
      for (String line : wrappedLines) {
        renderer.print("┃  ", TextColor.ANSI.RED);
        renderer.print(String.format("%-" + (containerWidth - 4) + "s", line), TextColor.ANSI.WHITE);
        renderer.println("  ┃", TextColor.ANSI.RED);
      }
    }
    
    // Show "and X more" if there are more instructions
    if (recipe.getInstructions().size() > 3) {
      String moreInfo = "... and " + (recipe.getInstructions().size() - 3) + " more steps";
      renderer.print("┃  ", TextColor.ANSI.RED);
      renderer.print(String.format("%-" + (containerWidth - 4) + "s", moreInfo), TextColor.ANSI.MAGENTA);
      renderer.println("  ┃", TextColor.ANSI.RED);
    }
  }

  /**
   * Calculate ingredient column width (adapted from ViewRecipeRenderer)
   */
  private int calculateIngredientColumnWidth(Recipe recipe) {
    int maxWidth = "Ingredient".length(); // Header width
    
    for (Ingredient ingredient : recipe.getIngredients()) {
      maxWidth = Math.max(maxWidth, ingredient.getName().length());
    }
    
    return maxWidth;
  }
  
  /**
   * Calculate amount column width (adapted from ViewRecipeRenderer)
   */
  private int calculateAmountColumnWidth(Recipe recipe) {
    int maxWidth = "Amount".length(); // Header width
    
    for (Ingredient ingredient : recipe.getIngredients()) {
      String amountStr = String.format("%.1f", ingredient.getAmount()).replaceAll("\\.0$", "");
      maxWidth = Math.max(maxWidth, amountStr.length());
    }
    
    return maxWidth;
  }
  
  /**
   * Calculate unit column width (adapted from ViewRecipeRenderer)
   */
  private int calculateUnitColumnWidth(Recipe recipe) {
    int maxWidth = "Unit".length(); // Header width
    
    for (Ingredient ingredient : recipe.getIngredients()) {
      maxWidth = Math.max(maxWidth, ingredient.getUnit().length());
    }
    
    return maxWidth;
  }

  /**
   * Wrap text to fit within specified width (adapted from ViewRecipeRenderer)
   */
  private String[] wrapText(String text, int maxWidth) {
    if (text.length() <= maxWidth) {
      return new String[]{text};
    }
    
    List<String> lines = new ArrayList<>();
    String[] words = text.split(" ");
    StringBuilder currentLine = new StringBuilder();
    
    for (String word : words) {
      if (currentLine.length() + word.length() + 1 <= maxWidth) {
        if (currentLine.length() > 0) {
          currentLine.append(" ");
        }
        currentLine.append(word);
      } else {
        if (currentLine.length() > 0) {
          lines.add(currentLine.toString());
          currentLine = new StringBuilder(word);
        } else {
          // Single word is too long, split it
          lines.add(word.substring(0, maxWidth));
          currentLine = new StringBuilder(word.substring(maxWidth));
        }
      }
    }
    
    if (currentLine.length() > 0) {
      lines.add(currentLine.toString());
    }
    
    return lines.toArray(new String[0]);
  }
}