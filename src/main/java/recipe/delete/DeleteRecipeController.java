package recipe.delete;

import recipe.common.Terminal;
import recipe.common.ScrollableTerminalRenderer;
import recipe.common.PromptBasedController;
import recipe.common.RecipeFileManager;
import recipe.common.Recipe;
import recipe.view.ViewRecipeModel;
import recipe.view.ViewRecipeRenderer;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import java.util.List;

public class DeleteRecipeController {
  private final Terminal terminal;
  private final ScrollableTerminalRenderer renderer;
  private final PromptBasedController promptController;
  private final ViewRecipeModel viewModel;
  private final ViewRecipeRenderer viewRenderer;
  private final DeleteRecipeModel deleteModel;
  private final DeleteRecipeRenderer deleteRenderer;

  public DeleteRecipeController(Terminal terminal, RecipeFileManager fileManager) {
    this.terminal = terminal;
    this.renderer = new ScrollableTerminalRenderer(terminal);
    this.promptController = new PromptBasedController(terminal, renderer);
    this.viewModel = new ViewRecipeModel(fileManager);
    this.viewRenderer = new ViewRecipeRenderer(renderer);
    this.deleteModel = new DeleteRecipeModel(fileManager);
    this.deleteRenderer = new DeleteRecipeRenderer(renderer);
  }

  public void run() throws IOException {
    try {
      // Welcome message
      deleteRenderer.renderWelcome();

      // Check if there are any recipes
      if (viewModel.getRecipeCount() == 0) {
        deleteRenderer.renderNoRecipesMessage();
        promptController.waitForEnter();
        return;
      }

      // Recipe selection phase (inherited from ViewRecipeController)
      Recipe selectedRecipe = selectRecipe();
      if (selectedRecipe == null) {
        return; // User cancelled selection
      }

      // Multi-stage deletion confirmation
      if (handleDeletionProcess(selectedRecipe)) {
        deleteRenderer.renderSuccess("Recipe deleted successfully!");
      }
      promptController.waitForEnter();

    } catch (Exception e) {
      deleteRenderer.renderError("An error occurred: " + e.getMessage());
      promptController.waitForEnter();
    }
  }

  private Recipe selectRecipe() throws IOException {
    // Main interaction loop for recipe selection
    while (true) {
      // If more than 9 recipes, skip table and go directly to search
      if (viewModel.getRecipeCount() > 9) {
        renderer.clear();
        viewRenderer.renderSearchPrompt(viewModel.getRecipeCount());
        Recipe selected = handleSearch();
        if (selected != null) {
          return selected;
        }
        return null; // User chose to exit
      } else {
        // Display recipe table for 9 or fewer recipes
        viewRenderer.renderRecipeTable(viewModel.getRecipes(), viewModel.getSearchQuery());

        // Handle user input
        Recipe selected = handleUserInput();
        if (selected == null) {
          return null; // User chose to exit
        }
        return selected;
      }
    }
  }

  private Recipe handleUserInput() throws IOException {
    String prompt = buildPrompt();
    renderer.print(prompt, TextColor.ANSI.YELLOW);

    KeyStroke keyStroke = terminal.readInput();
    
    if (keyStroke.getKeyType() == KeyType.Escape) {
      return null; // User cancelled (ESC)
    }

    if (keyStroke.getKeyType() == KeyType.Character) {
      char ch = Character.toLowerCase(keyStroke.getCharacter());
      
      // Echo the character to show what was pressed
      renderer.println(String.valueOf(ch));
      
      // Handle quit
      if (ch == 'q') {
        return null;
      }

      // Handle search
      if (ch == 's' && viewModel.shouldShowSearch()) {
        return handleSearch();
      }

      // Handle recipe number selection (1-9)
      if (ch >= '1' && ch <= '9') {
        int recipeNumber = ch - '0';
        if (recipeNumber >= 1 && recipeNumber <= viewModel.getRecipeCount()) {
          Recipe selectedRecipe = viewModel.getRecipeByNumber(recipeNumber);
          if (selectedRecipe != null) {
            return selectedRecipe;
          }
        } else {
          renderer.printError("Invalid recipe number. Please try again.");
        }
      } else {
        // For recipes beyond 9, show search instruction
        if (viewModel.getRecipeCount() > 9) {
          renderer.printError("Invalid input. For recipes beyond 9, please use 's' to search, or 'q' to go back.");
        } else {
          renderer.printError("Invalid input. Please press a recipe number, 's' to search, or 'q' to go back.");
        }
      }
    }

    return null; // Continue loop by returning null
  }

  private String buildPrompt() {
    if (viewModel.shouldShowSearch()) {
      return String.format("Press recipe number (1-%d), 's' to search, or 'q' to go back: ",
          viewModel.getRecipeCount());
    } else {
      return String.format("Press recipe number (1-%d) or 'q' to go back: ",
          viewModel.getRecipeCount());
    }
  }

  private Recipe handleSearch() throws IOException {
    String searchQuery = promptController.promptForText("Enter recipe name to search (or 'v' to view all): ");

    if (searchQuery == null) {
      return null; // User pressed ESC, exit to main menu
    }

    if (searchQuery.trim().isEmpty()) {
      return selectRecipe(); // Continue to main loop
    }

    // Check if user wants to view all recipes
    if (searchQuery.trim().toLowerCase().equals("v")) {
      return handleViewAllRecipes();
    }

    // Search for all matching recipes
    List<Recipe> foundRecipes = viewModel.searchRecipesByName(searchQuery);

    if (foundRecipes.isEmpty()) {
      renderer.printError("Recipe not found: " + searchQuery);
      return selectRecipe(); // Continue to main loop
    }

    if (foundRecipes.size() == 1) {
      // Single match - return recipe directly
      viewModel.setSearchQuery(searchQuery);
      return foundRecipes.get(0);
    }

    // Multiple matches - show table and let user select
    return handleMultipleSearchResults(foundRecipes, searchQuery);
  }

  private Recipe handleViewAllRecipes() throws IOException {
    List<Recipe> allRecipes = viewModel.getRecipes();
    int totalRecipes = allRecipes.size();
    
    // Calculate recipes per page based on terminal height
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
      viewRenderer.renderRecipeTableWithPagination(pageRecipes, "", currentPage, totalPages, startIndex + 1);

      // Build prompt with navigation options
      String prompt = buildViewAllPrompt(currentPage, totalPages, pageRecipes.size());
      String input = promptController.promptForText(prompt);
      
      if (input == null) {
        return null; // User pressed ESC, exit to main menu
      }

      input = input.trim().toLowerCase();
      
      if (input.equals("q")) {
        return selectRecipe(); // Go back to search prompt
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
          viewModel.setSearchQuery(""); // Clear search query for all recipes view
          return selectedRecipe;
        } else {
          renderer.printError("Invalid recipe number. Please enter a number between 1 and " + pageRecipes.size() + " for this page.");
        }
      } catch (NumberFormatException e) {
        // Not a number, try searching by name
        List<Recipe> foundRecipes = viewModel.searchRecipesByName(input);
        
        if (foundRecipes.isEmpty()) {
          renderer.printError("Recipe not found: " + input);
          continue;
        }
        
        if (foundRecipes.size() == 1) {
          // Single match - return recipe directly
          viewModel.setSearchQuery("");
          return foundRecipes.get(0);
        }
        
        // Multiple matches - show table and let user select
        viewModel.setSearchQuery(input);
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

  private Recipe handleMultipleSearchResults(List<Recipe> foundRecipes, String searchQuery) throws IOException {
    // Limit to 9 results for table display
    List<Recipe> displayRecipes = foundRecipes.size() > 9 ? 
        foundRecipes.subList(0, 9) : foundRecipes;

    while (true) {
      // Clear console before showing search results
      renderer.clear();
      
      // Display search results table
      viewRenderer.renderRecipeTable(displayRecipes, searchQuery);

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
        return selectRecipe(); // Go back to main loop
      }

      if (keyStroke.getKeyType() == KeyType.Character) {
        char ch = Character.toLowerCase(keyStroke.getCharacter());
        
        // Echo the character to show what was pressed
        renderer.println(String.valueOf(ch));
        
        // Handle quit
        if (ch == 'q') {
          return selectRecipe(); // Go back to main loop
        }

        // Handle recipe number selection (1-9)
        if (ch >= '1' && ch <= '9') {
          int recipeNumber = ch - '0';
          if (recipeNumber >= 1 && recipeNumber <= displayRecipes.size()) {
            Recipe selectedRecipe = displayRecipes.get(recipeNumber - 1);
            viewModel.setSearchQuery(searchQuery);
            return selectedRecipe;
          } else {
            renderer.printError("Invalid recipe number. Please try again.");
          }
        } else {
          renderer.printError("Invalid input. Please press a recipe number or 'q' to go back.");
        }
      }
    }
  }

  private boolean handleDeletionProcess(Recipe recipe) throws IOException {
    // Show recipe details with WARNING and get confirmation
    renderer.clear();
    deleteRenderer.renderDeletionWarning(recipe);
    
    boolean confirm = promptController.promptForConfirmation("Are you sure you want to delete this recipe?", false);
    if (!confirm) {
      deleteRenderer.renderInfo("Deletion cancelled.");
      return false;
    }

    // Execute deletion
    try {
      boolean deleted = deleteModel.deleteRecipe(recipe);
      if (deleted) {
        return true;
      } else {
        deleteRenderer.renderError("Failed to delete recipe file. The recipe may not exist or be in use.");
        return false;
      }
    } catch (Exception e) {
      deleteRenderer.renderError("Error during deletion: " + e.getMessage());
      return false;
    }
  }
}