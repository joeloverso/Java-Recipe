package recipe.calculate;

import recipe.common.Terminal;
import recipe.common.ScrollableTerminalRenderer;
import recipe.common.PromptBasedController;
import recipe.common.RecipeFileManager;
import recipe.common.Recipe;
import recipe.common.Ingredient;
import recipe.view.ViewRecipeController;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import java.util.List;

public class CalculateRecipeController {
  private final Terminal terminal;
  private final ScrollableTerminalRenderer renderer;
  private final PromptBasedController promptController;
  private final CalculateRecipeModel calculateModel;
  private final CalculateRecipeRenderer calculateRenderer;
  private final ViewRecipeController viewController;
  
  public CalculateRecipeController(Terminal terminal, RecipeFileManager fileManager) {
    this.terminal = terminal;
    this.renderer = new ScrollableTerminalRenderer(terminal);
    this.promptController = new PromptBasedController(terminal, renderer);
    this.calculateModel = new CalculateRecipeModel(fileManager);
    this.calculateRenderer = new CalculateRecipeRenderer(renderer);
    this.viewController = new ViewRecipeController(terminal, fileManager);
  }
  
  public void run() throws IOException {
    try {
      // Welcome message
      calculateRenderer.renderWelcome();

      // Check if there are any recipes
      if (calculateModel.getRecipeCount() == 0) {
        calculateRenderer.renderNoRecipesMessage();
        renderer.print("Press Enter to continue: ", TextColor.ANSI.YELLOW);
        
        KeyStroke keyStroke = terminal.readInput();
        if (keyStroke.getKeyType() == KeyType.Character) {
          char ch = Character.toLowerCase(keyStroke.getCharacter());
          if (ch == 'q') {
            renderer.println("q");
          } else {
            renderer.println(String.valueOf(ch));
          }
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
          renderer.println("ESC");
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
          renderer.println("");
        } else {
          renderer.println("");
        }
        
        return;
      }

      // Main interaction loop
      while (true) {
        // If more than 9 recipes, skip table and go directly to search
        if (calculateModel.getRecipeCount() > 9) {
          renderer.clear();
          calculateRenderer.renderSearchPrompt(calculateModel.getRecipeCount());
          if (!handleSearch()) {
            break;
          }
        } else {
          // Display recipe table for 9 or fewer recipes
          calculateRenderer.renderRecipeTable(calculateModel.getRecipes(), calculateModel.getSearchQuery());

          // Handle user input
          if (!handleUserInput()) {
            break;
          }
        }
      }

    } catch (Exception e) {
      renderer.printError("An error occurred: " + e.getMessage());
      renderer.print("Press Enter to continue: ", TextColor.ANSI.YELLOW);
      
      KeyStroke keyStroke = terminal.readInput();
      if (keyStroke.getKeyType() == KeyType.Character) {
        char ch = Character.toLowerCase(keyStroke.getCharacter());
        if (ch == 'q') {
          renderer.println("q");
        } else {
          renderer.println(String.valueOf(ch));
        }
      } else if (keyStroke.getKeyType() == KeyType.Escape) {
        renderer.println("ESC");
      } else if (keyStroke.getKeyType() == KeyType.Enter) {
        renderer.println("");
      } else {
        renderer.println("");
      }
    }
  }
  
  private boolean handleUserInput() throws IOException {
    String prompt = buildPrompt();
    renderer.print(prompt, TextColor.ANSI.YELLOW);

    KeyStroke keyStroke = terminal.readInput();
    
    if (keyStroke.getKeyType() == KeyType.Escape) {
      return false; // User cancelled (ESC)
    }

    if (keyStroke.getKeyType() == KeyType.Character) {
      char ch = Character.toLowerCase(keyStroke.getCharacter());
      
      // Echo the character to show what was pressed
      renderer.println(String.valueOf(ch));
      
      // Handle quit
      if (ch == 'q') {
        return false;
      }

      // Handle search
      if (ch == 's' && calculateModel.shouldShowSearch()) {
        return handleSearch();
      }

      // Handle recipe number selection (1-9)
      if (ch >= '1' && ch <= '9') {
        int recipeNumber = ch - '0';
        if (recipeNumber >= 1 && recipeNumber <= calculateModel.getRecipeCount()) {
          Recipe selectedRecipe = calculateModel.getRecipeByNumber(recipeNumber);
          if (selectedRecipe != null) {
            return handleRecipeView(selectedRecipe);
          }
        } else {
          renderer.printError("Invalid recipe number. Please try again.");
        }
      } else {
        // For recipes beyond 9, show search instruction
        if (calculateModel.getRecipeCount() > 9) {
          renderer.printError("Invalid input. For recipes beyond 9, please use 's' to search, or 'q' to go back.");
        } else {
          renderer.printError("Invalid input. Please press a recipe number, 's' to search, or 'q' to go back.");
        }
      }
    }

    return true; // Continue loop
  }

  private String buildPrompt() {
    if (calculateModel.shouldShowSearch()) {
      return String.format("Press recipe number (1-%d), 's' to search, or 'q' to go back: ",
          calculateModel.getRecipeCount());
    } else {
      return String.format("Press recipe number (1-%d) or 'q' to go back: ",
          calculateModel.getRecipeCount());
    }
  }

  private boolean handleSearch() throws IOException {
    String searchQuery = promptController.promptForText("Enter recipe name to search (or 'v' to view all): ");

    if (searchQuery == null) {
      return false; // User pressed ESC, exit to main menu
    }

    if (searchQuery.trim().isEmpty()) {
      return true; // Continue to main loop
    }

    // Check if user wants to view all recipes
    if (searchQuery.trim().toLowerCase().equals("v")) {
      return handleViewAllRecipes();
    }

    // Search for all matching recipes
    List<Recipe> foundRecipes = calculateModel.searchRecipesByName(searchQuery);

    if (foundRecipes.isEmpty()) {
      renderer.printError("Recipe not found: " + searchQuery);
      return true; // Continue to main loop
    }

    if (foundRecipes.size() == 1) {
      // Single match - show recipe details directly
      calculateModel.setSearchQuery(searchQuery);
      return handleRecipeView(foundRecipes.get(0));
    }

    // Multiple matches - show table and let user select
    return handleMultipleSearchResults(foundRecipes, searchQuery);
  }

  private boolean handleViewAllRecipes() throws IOException {
    List<Recipe> allRecipes = calculateModel.getRecipes();
    int totalRecipes = allRecipes.size();
    
    // Calculate recipes per page based on terminal height
    // Leave space for header, pagination info, and prompt (approximately 8 lines)
    int recipesPerPage = Math.max(5, terminal.getHeight() - 10);
    int totalPages = (int) Math.ceil((double) totalRecipes / recipesPerPage);
    int currentPage = 1;
    
    while (true) {
      // Calculate current page recipes
      int startIndex = (currentPage - 1) * recipesPerPage;
      int endIndex = Math.min(startIndex + recipesPerPage, totalRecipes);
      List<Recipe> pageRecipes = allRecipes.subList(startIndex, endIndex);
      
      // Clear console and show current page of recipes
      renderer.clear();
      calculateRenderer.renderRecipeTableWithPagination(pageRecipes, "", currentPage, totalPages, startIndex + 1);

      // Build prompt with navigation options
      String prompt = buildViewAllPrompt(currentPage, totalPages, pageRecipes.size());
      String input = promptController.promptForText(prompt);
      
      if (input == null) {
        return false; // User pressed ESC, exit to main menu
      }

      input = input.trim().toLowerCase();
      
      if (input.equals("q")) {
        return true; // Go back to search prompt
      }

      if (input.isEmpty()) {
        continue; // Try again
      }

      // Handle navigation
      if (input.equals("n") && currentPage < totalPages) {
        currentPage++;
        continue;
      }
      
      if (input.equals("p") && currentPage > 1) {
        currentPage--;
        continue;
      }

      // Try to parse as recipe number first (relative to current page)
      try {
        int recipeNumber = Integer.parseInt(input);
        int globalIndex = startIndex + recipeNumber - 1;
        
        if (recipeNumber >= 1 && recipeNumber <= pageRecipes.size()) {
          Recipe selectedRecipe = allRecipes.get(globalIndex);
          calculateModel.setSearchQuery(""); // Clear search query for all recipes view
          return handleRecipeView(selectedRecipe);
        } else {
          renderer.printError("Invalid recipe number. Please enter a number between 1 and " + pageRecipes.size() + " for this page.");
        }
      } catch (NumberFormatException e) {
        // Not a number, try searching by name (search across all recipes, not just current page)
        List<Recipe> foundRecipes = calculateModel.searchRecipesByName(input);
        
        if (foundRecipes.isEmpty()) {
          renderer.printError("Recipe not found: " + input);
          continue;
        }
        
        if (foundRecipes.size() == 1) {
          // Single match - show recipe details directly
          calculateModel.setSearchQuery("");
          return handleRecipeView(foundRecipes.get(0));
        }
        
        // Multiple matches - show table and let user select (same as main search)
        calculateModel.setSearchQuery(input);
        return handleMultipleSearchResults(foundRecipes, input);
      }
    }
  }

  private String buildViewAllPrompt(int currentPage, int totalPages, int recipesOnPage) {
    StringBuilder prompt = new StringBuilder();
    prompt.append("Enter recipe number (1-").append(recipesOnPage).append(")");
    prompt.append(", search by name");
    
    if (currentPage > 1) {
      prompt.append(", 'p' for previous page");
    }
    
    if (currentPage < totalPages) {
      prompt.append(", 'n' for next page");
    }
    
    prompt.append(", or 'q' to go back: ");
    return prompt.toString();
  }

  private boolean handleMultipleSearchResults(List<Recipe> foundRecipes, String searchQuery) throws IOException {
    // Limit to 9 results for table display
    List<Recipe> displayRecipes = foundRecipes.size() > 9 ? 
        foundRecipes.subList(0, 9) : foundRecipes;

    while (true) {
      // Clear console before showing search results
      renderer.clear();
      
      // Display search results table
      calculateRenderer.renderRecipeTable(displayRecipes, searchQuery);

      // Show additional message if there are more than 9 results
      if (foundRecipes.size() > 9) {
        renderer.println(String.format("Showing first 9 of %d matching recipes. Refine your search for more specific results.", 
            foundRecipes.size()), TextColor.ANSI.YELLOW);
        renderer.printBlankLine();
      }

      // Prompt for selection
      String prompt = String.format("Select recipe number (1-%d) or 'q' to go back: ", 
          displayRecipes.size());
      renderer.print(prompt, TextColor.ANSI.YELLOW);

      KeyStroke keyStroke = terminal.readInput();
      
      if (keyStroke.getKeyType() == KeyType.Escape) {
        return true; // Go back to main loop
      }

      if (keyStroke.getKeyType() == KeyType.Character) {
        char ch = Character.toLowerCase(keyStroke.getCharacter());
        
        // Echo the character to show what was pressed
        renderer.println(String.valueOf(ch));
        
        // Handle quit
        if (ch == 'q') {
          return true; // Go back to main loop
        }

        // Handle recipe number selection (1-9)
        if (ch >= '1' && ch <= '9') {
          int recipeNumber = ch - '0';
          if (recipeNumber >= 1 && recipeNumber <= displayRecipes.size()) {
            Recipe selectedRecipe = displayRecipes.get(recipeNumber - 1);
            calculateModel.setSearchQuery(searchQuery);
            return handleRecipeView(selectedRecipe);
          } else {
            renderer.printError("Invalid recipe number. Please try again.");
          }
        } else {
          renderer.printError("Invalid input. Please press a recipe number or 'q' to go back.");
        }
      }
    }
  }

  private boolean handleRecipeView(Recipe recipe) throws IOException {
    // Check if recipe has prime ingredients
    Ingredient primeIngredient = calculateModel.getFirstPrimeIngredient(recipe);
    if (primeIngredient == null) {
      calculateRenderer.renderNoPrimeIngredientError();
      renderer.print("Press Enter to continue: ", TextColor.ANSI.YELLOW);
      
      KeyStroke keyStroke = terminal.readInput();
      if (keyStroke.getKeyType() == KeyType.Character) {
        char ch = Character.toLowerCase(keyStroke.getCharacter());
        if (ch == 'q') {
          renderer.println("q");
        } else {
          renderer.println(String.valueOf(ch));
        }
      } else if (keyStroke.getKeyType() == KeyType.Escape) {
        renderer.println("ESC");
      } else if (keyStroke.getKeyType() == KeyType.Enter) {
        renderer.println("");
      } else {
        renderer.println("");
      }
      
      return true;
    }
    
    // Show prime ingredient info and prompt for new amount
    calculateRenderer.renderPrimeIngredientPrompt(recipe, primeIngredient);
    
    String prompt = String.format("How much %s will you be using? (in %s): ", 
        primeIngredient.getName(), primeIngredient.getUnit());
    
    PromptBasedController promptController = new PromptBasedController(terminal, renderer);
    String newAmountInput = promptController.promptForText(prompt);
    
    if (newAmountInput == null) {
      return false; // User pressed ESC, exit to main menu
    }
    
    if (newAmountInput.trim().isEmpty()) {
      return true; // Continue to main loop
    }
    
    // Parse new amount
    double newAmount;
    try {
      newAmount = Double.parseDouble(newAmountInput.trim());
      if (newAmount <= 0) {
        renderer.printError("Amount must be greater than 0.");
        return true;
      }
    } catch (NumberFormatException e) {
      renderer.printError("Please enter a valid number.");
      return true;
    }
    
    // Calculate scale factor and create scaled recipe
    double scaleFactor = calculateModel.calculateScaleFactor(primeIngredient.getAmount(), newAmount);
    Recipe scaledRecipe = calculateModel.createScaledRecipe(recipe, scaleFactor);
    
    // Display scaled recipe
    calculateRenderer.renderScaledRecipeDetails(scaledRecipe, scaleFactor);
    
    // Wait for user input to continue
    renderer.print("Press Enter to continue: ", TextColor.ANSI.YELLOW);
    
    KeyStroke keyStroke = terminal.readInput();
    
    if (keyStroke.getKeyType() == KeyType.Character) {
      char ch = Character.toLowerCase(keyStroke.getCharacter());
      
      if (ch == 'q') {
        renderer.println("q");
      } else {
        renderer.println(String.valueOf(ch));
      }
    } else if (keyStroke.getKeyType() == KeyType.Escape) {
      renderer.println("ESC");
    } else if (keyStroke.getKeyType() == KeyType.Enter) {
      renderer.println("");
    } else {
      renderer.println("");
    }

    // Clear console before returning to recipe list
    renderer.clear();
    
    return true;
  }
}