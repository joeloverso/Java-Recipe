package recipe.view;

import recipe.common.ScrollableTerminalRenderer;
import recipe.common.Recipe;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.List;

public class ViewRecipeRenderer {
  private final ScrollableTerminalRenderer renderer;

  public ViewRecipeRenderer(ScrollableTerminalRenderer renderer) {
    this.renderer = renderer;
  }

  public void renderWelcome() throws IOException {
    renderer.clear();
    renderer.printHeader("View Existing Recipes");
    renderer.printBlankLine();
  }

  public void renderRecipeTable(List<Recipe> recipes, String searchQuery) throws IOException {
    renderRecipeTableWithPagination(recipes, searchQuery, 0, 0, 1);
  }

  public void renderRecipeTableWithPagination(List<Recipe> recipes, String searchQuery, int currentPage, int totalPages, int startNumber) throws IOException {
    // Status line with pagination info
    String statusLine;
    if (searchQuery != null && !searchQuery.trim().isEmpty()) {
      statusLine = String.format("Showing %d recipe(s) matching '%s':", 
          recipes.size(), searchQuery);
    } else if (totalPages > 1) {
      statusLine = String.format("Showing %d recipe(s): (Page %d of %d)", 
          recipes.size(), currentPage, totalPages);
    } else {
      statusLine = String.format("Showing %d recipe(s):", recipes.size());
    }
    renderer.println(statusLine);
    renderer.printBlankLine();

    // Calculate dynamic table dimensions
    int numberWidth = 3;  // "# " - accounts for up to 999 recipes
    int createdWidth = 12; // "Created " - YYYY-MM-DD format
    int nameWidth = calculateOptimalNameWidth(recipes);
    
    // Table header
    String topBorder = "┏" + "━".repeat(numberWidth + 2) + "┳" + "━".repeat(nameWidth + 2) + "┳" + "━".repeat(createdWidth + 2) + "┓";
    String separatorRow = "┡" + "━".repeat(numberWidth + 2) + "╇" + "━".repeat(nameWidth + 2) + "╇" + "━".repeat(createdWidth + 2) + "┩";
    
    renderer.println(topBorder);
    
    // Header row with magenta headers
    renderer.print("┃ ");
    renderer.print(String.format("%-" + numberWidth + "s", "#"), TextColor.ANSI.MAGENTA);
    renderer.print(" ┃ ");
    renderer.print(String.format("%-" + nameWidth + "s", "Recipe Name"), TextColor.ANSI.MAGENTA);
    renderer.print(" ┃ ");
    renderer.print(String.format("%-" + createdWidth + "s", "Created"), TextColor.ANSI.MAGENTA);
    renderer.println(" ┃");
    
    renderer.println(separatorRow);

    // Table rows
    for (int i = 0; i < recipes.size(); i++) {
      Recipe recipe = recipes.get(i);
      // Show page-relative numbers for user input consistency
      String number = String.valueOf(i + 1);
      String name = truncateString(recipe.getName(), nameWidth);
      String created = formatDate(recipe.getCreated());
      
      // Row with green index numbers
      renderer.print("│ ");
      renderer.print(String.format("%-" + numberWidth + "s", number), TextColor.ANSI.GREEN);
      renderer.print(" │ ");
      renderer.print(String.format("%-" + nameWidth + "s", name));
      renderer.print(" │ ");
      renderer.print(String.format("%-" + createdWidth + "s", created));
      renderer.println(" │");
    }

    // Table footer
    String bottomBorder = "└" + "─".repeat(numberWidth + 2) + "┴" + "─".repeat(nameWidth + 2) + "┴" + "─".repeat(createdWidth + 2) + "┘";
    renderer.println(bottomBorder);
    renderer.printBlankLine();
  }
  
  private int calculateOptimalNameWidth(List<Recipe> recipes) {
    if (recipes.isEmpty()) {
      return 12; // Minimum width for "Recipe Name" header
    }
    
    // Find the longest recipe name
    int maxNameLength = recipes.stream()
        .mapToInt(recipe -> recipe.getName().length())
        .max()
        .orElse(12);
    
    // Ensure minimum width for the header "Recipe Name" (11 characters)
    int minWidth = Math.max(11, maxNameLength);
    
    // Set reasonable bounds - minimum 15, maximum 50 characters
    return Math.max(15, Math.min(50, minWidth));
  }

  public void renderRecipeDetails(Recipe recipe) throws IOException {
    renderer.clear();
    
    // Title
    renderer.println("═══ " + recipe.getName() + " ═══", TextColor.ANSI.CYAN_BRIGHT);
    renderer.printBlankLine();

    // Servings
    renderer.println(String.format("Servings: %.0f", recipe.getServings()));
    renderer.printBlankLine();

    // Ingredients
    renderer.println("INGREDIENTS:", TextColor.ANSI.YELLOW);
    renderer.println("─".repeat(11), TextColor.ANSI.YELLOW);
    
    drawIngredientsTable(recipe);
    
    renderer.printBlankLine();

    // Recipe Notes (Instructions)
    drawRecipeNotes(recipe);
    
    renderer.printBlankLine();
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

  private String truncateString(String str, int maxLength) {
    if (str.length() <= maxLength) {
      return str;
    }
    return str.substring(0, maxLength - 3) + "...";
  }

  private String formatDate(String isoDateString) {
    if (isoDateString == null || isoDateString.isEmpty()) {
      return "Unknown";
    }

    try {
      // Extract date part from ISO string (YYYY-MM-DD from YYYY-MM-DDTHH:MM:SS.sssZ)
      if (isoDateString.contains("T")) {
        return isoDateString.substring(0, 10);
      }
      return isoDateString;
    } catch (Exception e) {
      return "Unknown";
    }
  }

  public void renderNoRecipesMessage() throws IOException {
    renderer.println("No recipes found!", TextColor.ANSI.YELLOW);
    renderer.printBlankLine();
    renderer.println("Add some recipes first using the main menu.", TextColor.ANSI.WHITE);
    renderer.printBlankLine();
  }

  public void renderSearchPrompt(int totalRecipes) throws IOException {
    renderer.println(String.format("Found %d recipes. Too many to display in numbered list.", totalRecipes));
    renderer.printBlankLine();
    renderer.println("Please search for a recipe by name to view it.", TextColor.ANSI.YELLOW);
    renderer.println("Or enter 'v' to view all recipes in a numbered list.", TextColor.ANSI.YELLOW);
    renderer.printBlankLine();
  }
}