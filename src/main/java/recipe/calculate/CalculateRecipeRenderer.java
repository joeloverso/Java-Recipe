package recipe.calculate;

import recipe.common.ScrollableTerminalRenderer;
import recipe.common.Recipe;
import recipe.common.Ingredient;
import recipe.view.ViewRecipeRenderer;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;

public class CalculateRecipeRenderer {
  private final ScrollableTerminalRenderer renderer;
  private final ViewRecipeRenderer viewRenderer;
  
  public CalculateRecipeRenderer(ScrollableTerminalRenderer renderer) {
    this.renderer = renderer;
    this.viewRenderer = new ViewRecipeRenderer(renderer);
  }
  
  public void renderRecipeDetails(Recipe recipe) throws IOException {
    // Simply delegate to the view renderer
    viewRenderer.renderRecipeDetails(recipe);
  }
  public void renderScaledRecipeDetails(Recipe recipe, double scaleFactor) throws IOException {
    renderer.clear();
    
    // Title
    renderer.println("═══ " + recipe.getName() + " ═══", TextColor.ANSI.CYAN_BRIGHT);
    renderer.printBlankLine();

    // Show "Scaled: y" prefix before servings
    renderer.println(String.format("Scaled: %.2fx", scaleFactor), TextColor.ANSI.YELLOW);
    
    // Servings
    renderer.println(String.format("Servings: %.0f", recipe.getServings()));
    renderer.printBlankLine();

    // Ingredients
    renderer.println("INGREDIENTS:", TextColor.ANSI.YELLOW);
    renderer.println("─".repeat(11), TextColor.ANSI.YELLOW);
    
    // Draw ingredients table inline since we can't access the private method
    drawIngredientsTable(recipe);
    
    renderer.printBlankLine();

    // Recipe Notes (Instructions)
    drawRecipeNotes(recipe);
    
    renderer.printBlankLine();
  }
  
  public void renderPrimeIngredientPrompt(Recipe recipe, Ingredient primeIngredient) throws IOException {
    renderer.clear();
    
    // Recipe header (same as recipe details but without "(scaled)")
    renderer.println("═══ " + recipe.getName() + " ═══", TextColor.ANSI.CYAN_BRIGHT);
    renderer.printBlankLine();
    
    renderer.println("Prime ingredient found:", TextColor.ANSI.CYAN);
    renderer.println(String.format("Original amount: %.1f %s", 
        primeIngredient.getAmount(), primeIngredient.getUnit()), TextColor.ANSI.WHITE);
    renderer.printBlankLine();
  }
  
  public void renderNoPrimeIngredientError() throws IOException {
    renderer.printError("This recipe has no prime ingredients marked for scaling.");
    renderer.println("Prime ingredients are needed to calculate recipe scaling.", TextColor.ANSI.YELLOW);
    renderer.printBlankLine();
  }
  
  public void renderWelcome() throws IOException {
    viewRenderer.renderWelcome();
  }
  
  public void renderRecipeTable(java.util.List<Recipe> recipes, String searchQuery) throws IOException {
    viewRenderer.renderRecipeTable(recipes, searchQuery);
  }
  
  public void renderRecipeTableWithPagination(java.util.List<Recipe> recipes, String searchQuery, int currentPage, int totalPages, int startNumber) throws IOException {
    viewRenderer.renderRecipeTableWithPagination(recipes, searchQuery, currentPage, totalPages, startNumber);
  }
  
  public void renderNoRecipesMessage() throws IOException {
    viewRenderer.renderNoRecipesMessage();
  }
  
  public void renderSearchPrompt(int totalRecipes) throws IOException {
    viewRenderer.renderSearchPrompt(totalRecipes);
  }
  
  private void drawIngredientsTable(Recipe recipe) throws IOException {
    // Calculate column widths
    int ingredientWidth = calculateIngredientColumnWidth(recipe);
    int amountWidth = calculateAmountColumnWidth(recipe);
    int unitWidth = calculateUnitColumnWidth(recipe);
    
    // Print headers in magenta
    renderer.print(String.format("%-" + ingredientWidth + "s", "Ingredient"), TextColor.ANSI.MAGENTA);
    renderer.print("  "); // Spacing between columns
    renderer.print(String.format("%-" + amountWidth + "s", "Amount"), TextColor.ANSI.MAGENTA);
    renderer.print("  "); // Spacing between columns
    renderer.println(String.format("%-" + unitWidth + "s", "Unit"), TextColor.ANSI.MAGENTA);
    
    // Print ingredient rows
    for (int i = 0; i < recipe.getIngredients().size(); i++) {
      var ingredient = recipe.getIngredients().get(i);
      
      // Ingredient name in cyan
      renderer.print(String.format("%-" + ingredientWidth + "s", ingredient.getName()), TextColor.ANSI.CYAN);
      renderer.print("  "); // Spacing between columns
      
      // Amount in green
      String amountStr = String.format("%.1f", ingredient.getAmount()).replaceAll("\\.0$", "");
      renderer.print(String.format("%" + amountWidth + "s", amountStr), TextColor.ANSI.GREEN);
      renderer.print("  "); // Spacing between columns
      
      // Unit in yellow
      renderer.println(String.format("%-" + unitWidth + "s", ingredient.getUnit()), TextColor.ANSI.YELLOW);
    }
  }
  
  private int calculateIngredientColumnWidth(Recipe recipe) {
    int maxWidth = "Ingredient".length(); // Header width
    
    for (var ingredient : recipe.getIngredients()) {
      maxWidth = Math.max(maxWidth, ingredient.getName().length());
    }
    
    return maxWidth;
  }
  
  private int calculateAmountColumnWidth(Recipe recipe) {
    int maxWidth = "Amount".length(); // Header width
    
    for (var ingredient : recipe.getIngredients()) {
      String amountStr = String.format("%.1f", ingredient.getAmount()).replaceAll("\\.0$", "");
      maxWidth = Math.max(maxWidth, amountStr.length());
    }
    
    return maxWidth;
  }
  
  private int calculateUnitColumnWidth(Recipe recipe) {
    int maxWidth = "Unit".length(); // Header width
    
    for (var ingredient : recipe.getIngredients()) {
      maxWidth = Math.max(maxWidth, ingredient.getUnit().length());
    }
    
    return maxWidth;
  }
  
  private void drawRecipeNotes(Recipe recipe) throws IOException {
    // Calculate dynamic width based on content
    int contentWidth = calculateRecipeNotesWidth(recipe);
    String title = " Recipe Notes ";
    
    // Top border
    String topBorder = "╭─" + title + "─".repeat(contentWidth - title.length() - 1) + "╮";
    renderer.println(topBorder, TextColor.ANSI.BLUE);
    
    // Empty line after header
    String emptyLine = "│" + " ".repeat(contentWidth) + "│";
    renderer.println(emptyLine, TextColor.ANSI.BLUE);
    
    // Draw instructions
    for (int i = 0; i < recipe.getInstructions().size(); i++) {
      String instruction = String.format("%d. %s", i + 1, recipe.getInstructions().get(i));
      
      // Wrap text to fit within the content width (minus 4 for padding and borders)
      String[] wrappedLines = wrapText(instruction, contentWidth - 4);
      
      for (String line : wrappedLines) {
        renderer.print("│  ", TextColor.ANSI.BLUE);
        renderer.print(String.format("%-" + (contentWidth - 4) + "s", line), TextColor.ANSI.WHITE);
        renderer.println("  │", TextColor.ANSI.BLUE);
      }
    }
    
    // Empty line before bottom border
    renderer.println(emptyLine, TextColor.ANSI.BLUE);
    
    // Bottom border
    String bottomBorder = "╰" + "─".repeat(contentWidth) + "╯";
    renderer.println(bottomBorder, TextColor.ANSI.BLUE);
  }
  
  private int calculateRecipeNotesWidth(Recipe recipe) {
    int minWidth = 30; // Minimum width for "Recipe Notes" header
    int maxWidth = 80; // Maximum width to avoid overly wide boxes
    
    // Find the longest instruction line
    int maxInstructionLength = 0;
    for (int i = 0; i < recipe.getInstructions().size(); i++) {
      String instruction = String.format("%d. %s", i + 1, recipe.getInstructions().get(i));
      maxInstructionLength = Math.max(maxInstructionLength, instruction.length());
    }
    
    // Add padding for borders and internal spacing (4 chars for "│  " and "  │")
    int requiredWidth = maxInstructionLength + 4;
    
    // Apply bounds with 1 cell padding on the right
    return Math.max(minWidth, Math.min(maxWidth, requiredWidth)) + 1;
  }

  private String[] wrapText(String text, int maxWidth) {
    if (text.length() <= maxWidth) {
      return new String[]{text};
    }

    String[] words = text.split(" ");
    StringBuilder currentLine = new StringBuilder();
    StringBuilder result = new StringBuilder();

    for (String word : words) {
      if (currentLine.length() + word.length() + 1 <= maxWidth) {
        if (currentLine.length() > 0) {
          currentLine.append(" ");
        }
        currentLine.append(word);
      } else {
        if (result.length() > 0) {
          result.append("\n");
        }
        result.append(currentLine.toString());
        currentLine = new StringBuilder(word);
      }
    }

    if (currentLine.length() > 0) {
      if (result.length() > 0) {
        result.append("\n");
      }
      result.append(currentLine.toString());
    }

    return result.toString().split("\n");
  }
}