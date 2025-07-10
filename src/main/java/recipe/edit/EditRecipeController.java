package recipe.edit;

import recipe.common.Terminal;
import recipe.common.ScrollableTerminalRenderer;
import recipe.common.PromptBasedController;
import recipe.common.RecipeFileManager;
import recipe.common.Recipe;
import recipe.common.Ingredient;
import recipe.common.SlugUtils;
import recipe.view.ViewRecipeModel;
import recipe.view.ViewRecipeRenderer;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import java.util.List;

public class EditRecipeController {
  private final Terminal terminal;
  private final ScrollableTerminalRenderer renderer;
  private final PromptBasedController promptController;
  private final ViewRecipeModel viewModel;
  private final ViewRecipeRenderer viewRenderer;
  private final EditRecipeModel editModel;
  private final EditRecipeRenderer editRenderer;
  private final RecipeFileManager fileManager;

  public EditRecipeController(Terminal terminal, RecipeFileManager fileManager) {
    this.terminal = terminal;
    this.renderer = new ScrollableTerminalRenderer(terminal);
    this.promptController = new PromptBasedController(terminal, renderer);
    this.viewModel = new ViewRecipeModel(fileManager);
    this.viewRenderer = new ViewRecipeRenderer(renderer);
    this.editModel = new EditRecipeModel(fileManager);
    this.editRenderer = new EditRecipeRenderer(renderer);
    this.fileManager = fileManager;
  }

  public void run() throws IOException {
    try {
      // Welcome message
      editRenderer.renderWelcome();

      // Check if there are any recipes
      if (viewModel.getRecipeCount() == 0) {
        editRenderer.renderNoRecipesMessage();
        promptController.waitForEnter();
        return;
      }

      // Recipe selection phase (inherited from ViewRecipeController)
      Recipe selectedRecipe = selectRecipe();
      if (selectedRecipe == null) {
        return; // User cancelled selection
      }

      // Load recipe into edit model
      editModel.loadRecipe(selectedRecipe);

      // Show edit confirmation
      editRenderer.renderEditConfirmation(selectedRecipe);
      boolean confirmEdit = promptController.promptForConfirmation("Edit this recipe?", true);
      if (!confirmEdit) {
        return;
      }

      // Main edit loop
      handleEditMenu();

    } catch (Exception e) {
      editRenderer.renderError("An error occurred: " + e.getMessage());
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

  private void handleEditMenu() throws IOException {
    while (true) {
      renderer.clear();
      editRenderer.renderEditMenu(editModel);

      String choice = promptController.promptForText("Select option (1-6) or 'q' to cancel: ");
      if (choice == null || choice.equals("q")) {
        return; // User cancelled
      }

      switch (choice) {
        case "1":
          handleEditName();
          break;
        case "2":
          handleEditServings();
          break;
        case "3":
          handleEditIngredients();
          break;
        case "4":
          handleEditInstructions();
          break;
        case "5":
          handleReviewChanges();
          break;
        case "6":
          if (handleSaveChanges()) {
            return; // Successfully saved, exit
          }
          break;
        default:
          renderer.printError("Invalid option. Please try again.");
          promptController.waitForEnter();
      }
    }
  }

  private void handleEditName() throws IOException {
    String currentName = editModel.getCurrentRecipe().getName();
    editRenderer.renderCurrentValue("Recipe Name", currentName);
    
    String newName = promptController.promptForText("Enter new recipe name (or press Enter to keep current): ");
    if (newName != null && !newName.trim().isEmpty() && !newName.equals(currentName)) {
      // Check for duplicate names
      if (isRecipeNameTaken(newName, editModel.getOriginalRecipe().getName())) {
        return; // Error message already shown
      }
      editModel.setName(newName);
      editRenderer.renderSuccess("Recipe name updated to: " + newName);
    } else {
      editRenderer.renderInfo("Recipe name unchanged.");
    }
    promptController.waitForEnter();
  }

  private void handleEditServings() throws IOException {
    double currentServings = editModel.getCurrentRecipe().getServings();
    editRenderer.renderCurrentValue("Servings", String.valueOf(currentServings));
    
    Double newServings = promptController.promptForDouble("Enter new number of servings (or press Enter to keep current): ");
    if (newServings != null && newServings != currentServings) {
      editModel.setServings(newServings);
      editRenderer.renderSuccess("Servings updated to: " + newServings);
    } else {
      editRenderer.renderInfo("Servings unchanged.");
    }
    promptController.waitForEnter();
  }

  private void handleEditIngredients() throws IOException {
    while (true) {
      renderer.clear();
      editRenderer.renderIngredientsEdit(editModel);
      
      String choice = promptController.promptForText("Choose option (a/e/d/q): ");
      if (choice == null || choice.equals("q")) {
        return; // Back to main edit menu
      }
      
      switch (choice.toLowerCase()) {
        case "a":
          handleAddIngredient();
          break;
        case "e":
          handleEditIngredient();
          break;
        case "d":
          handleDeleteIngredient();
          break;
        default:
          editRenderer.renderError("Invalid option. Please try again.");
          promptController.waitForEnter();
      }
    }
  }

  private void handleAddIngredient() throws IOException {
    String name = promptController.promptForText("Ingredient name:");
    if (name == null || name.trim().isEmpty()) {
      return; // User cancelled
    }

    String unit = promptController.promptForText("Unit of measurement for " + name + " (e.g., cups, tbsp, lbs):");
    if (unit == null || unit.trim().isEmpty()) {
      return; // User cancelled
    }

    Double amount = promptController.promptForDouble("Quantity of " + name + " in " + unit + ":");
    if (amount == null || amount <= 0) {
      return; // User cancelled or invalid amount
    }

    Ingredient ingredient = new Ingredient(name, amount, unit);
    editModel.addIngredient(ingredient);
    editRenderer.renderSuccess("Added ingredient: " + String.format("%.2f %s %s", amount, unit, name));
    promptController.waitForEnter();
  }

  private void handleEditIngredient() throws IOException {
    if (editModel.getIngredientCount() == 0) {
      editRenderer.renderError("No ingredients to edit.");
      promptController.waitForEnter();
      return;
    }

    String indexStr = promptController.promptForText("Enter ingredient number to edit (1-" + editModel.getIngredientCount() + "):");
    if (indexStr == null) {
      return; // User cancelled
    }

    try {
      int index = Integer.parseInt(indexStr) - 1;
      Ingredient ingredient = editModel.getIngredient(index);
      if (ingredient == null) {
        editRenderer.renderError("Invalid ingredient number.");
        promptController.waitForEnter();
        return;
      }

      // Show current ingredient
      editRenderer.renderCurrentValue("Current Ingredient", 
          String.format("%.2f %s %s", ingredient.getAmount(), ingredient.getUnit(), ingredient.getName()));

      // Edit each field
      String newName = promptController.promptForText("New name (or Enter to keep '" + ingredient.getName() + "'):");
      if (newName == null) return; // User cancelled
      if (newName.trim().isEmpty()) newName = ingredient.getName();

      String newUnit = promptController.promptForText("New unit (or Enter to keep '" + ingredient.getUnit() + "'):");
      if (newUnit == null) return; // User cancelled
      if (newUnit.trim().isEmpty()) newUnit = ingredient.getUnit();

      Double newAmount = promptController.promptForDouble("New amount (or Enter to keep " + ingredient.getAmount() + "):");
      if (newAmount == null) return; // User cancelled
      if (newAmount <= 0) newAmount = ingredient.getAmount();

      // Update ingredient
      Ingredient updatedIngredient = new Ingredient(newName, newAmount, newUnit, ingredient.isPrime());
      editModel.updateIngredient(index, updatedIngredient);
      editRenderer.renderSuccess("Ingredient updated successfully.");
      promptController.waitForEnter();

    } catch (NumberFormatException e) {
      editRenderer.renderError("Invalid number format.");
      promptController.waitForEnter();
    }
  }

  private void handleDeleteIngredient() throws IOException {
    if (editModel.getIngredientCount() == 0) {
      editRenderer.renderError("No ingredients to delete.");
      promptController.waitForEnter();
      return;
    }

    String indexStr = promptController.promptForText("Enter ingredient number to delete (1-" + editModel.getIngredientCount() + "):");
    if (indexStr == null) {
      return; // User cancelled
    }

    try {
      int index = Integer.parseInt(indexStr) - 1;
      Ingredient ingredient = editModel.getIngredient(index);
      if (ingredient == null) {
        editRenderer.renderError("Invalid ingredient number.");
        promptController.waitForEnter();
        return;
      }

      boolean confirm = promptController.promptForConfirmation("Delete '" + ingredient.getName() + "'?", false);
      if (confirm) {
        editModel.removeIngredient(index);
        editRenderer.renderSuccess("Ingredient deleted successfully.");
      } else {
        editRenderer.renderInfo("Deletion cancelled.");
      }
      promptController.waitForEnter();

    } catch (NumberFormatException e) {
      editRenderer.renderError("Invalid number format.");
      promptController.waitForEnter();
    }
  }

  private void handleEditInstructions() throws IOException {
    while (true) {
      renderer.clear();
      editRenderer.renderInstructionsEdit(editModel);
      
      String choice = promptController.promptForText("Choose option (a/e/d/q): ");
      if (choice == null || choice.equals("q")) {
        return; // Back to main edit menu
      }
      
      switch (choice.toLowerCase()) {
        case "a":
          handleAddInstruction();
          break;
        case "e":
          handleEditInstruction();
          break;
        case "d":
          handleDeleteInstruction();
          break;
        default:
          editRenderer.renderError("Invalid option. Please try again.");
          promptController.waitForEnter();
      }
    }
  }

  private void handleAddInstruction() throws IOException {
    String instruction = promptController.promptForText("Enter new instruction:");
    if (instruction == null || instruction.trim().isEmpty()) {
      return; // User cancelled
    }

    editModel.addInstruction(instruction);
    editRenderer.renderSuccess("Added instruction: " + instruction);
    promptController.waitForEnter();
  }

  private void handleEditInstruction() throws IOException {
    if (editModel.getInstructionCount() == 0) {
      editRenderer.renderError("No instructions to edit.");
      promptController.waitForEnter();
      return;
    }

    String indexStr = promptController.promptForText("Enter instruction number to edit (1-" + editModel.getInstructionCount() + "):");
    if (indexStr == null) {
      return; // User cancelled
    }

    try {
      int index = Integer.parseInt(indexStr) - 1;
      String instruction = editModel.getInstruction(index);
      if (instruction == null) {
        editRenderer.renderError("Invalid instruction number.");
        promptController.waitForEnter();
        return;
      }

      // Show current instruction
      editRenderer.renderCurrentValue("Current Instruction", instruction);

      // Edit instruction
      String newInstruction = promptController.promptForText("New instruction (or Enter to keep current):");
      if (newInstruction == null) return; // User cancelled
      if (newInstruction.trim().isEmpty()) newInstruction = instruction;

      // Update instruction
      editModel.updateInstruction(index, newInstruction);
      editRenderer.renderSuccess("Instruction updated successfully.");
      promptController.waitForEnter();

    } catch (NumberFormatException e) {
      editRenderer.renderError("Invalid number format.");
      promptController.waitForEnter();
    }
  }

  private void handleDeleteInstruction() throws IOException {
    if (editModel.getInstructionCount() == 0) {
      editRenderer.renderError("No instructions to delete.");
      promptController.waitForEnter();
      return;
    }

    String indexStr = promptController.promptForText("Enter instruction number to delete (1-" + editModel.getInstructionCount() + "):");
    if (indexStr == null) {
      return; // User cancelled
    }

    try {
      int index = Integer.parseInt(indexStr) - 1;
      String instruction = editModel.getInstruction(index);
      if (instruction == null) {
        editRenderer.renderError("Invalid instruction number.");
        promptController.waitForEnter();
        return;
      }

      boolean confirm = promptController.promptForConfirmation("Delete instruction '" + instruction + "'?", false);
      if (confirm) {
        editModel.removeInstruction(index);
        editRenderer.renderSuccess("Instruction deleted successfully.");
      } else {
        editRenderer.renderInfo("Deletion cancelled.");
      }
      promptController.waitForEnter();

    } catch (NumberFormatException e) {
      editRenderer.renderError("Invalid number format.");
      promptController.waitForEnter();
    }
  }

  private void handleReviewChanges() throws IOException {
    renderer.clear();
    editRenderer.renderChangesReview(editModel);
    promptController.waitForEnter();
  }

  private boolean handleSaveChanges() throws IOException {
    if (!editModel.hasChanges()) {
      editRenderer.renderInfo("No changes to save.");
      promptController.waitForEnter();
      return false;
    }

    renderer.clear();
    editRenderer.renderChangesReview(editModel);
    
    boolean confirmSave = promptController.promptForConfirmation("Save these changes?", false);
    if (!confirmSave) {
      return false;
    }

    try {
      // Save the edited recipe
      Recipe editedRecipe = editModel.getCurrentRecipe();
      
      // Delete the original recipe file
      String originalSlug = editModel.getOriginalRecipe().getSlug();
      fileManager.deleteRecipeBySlug(originalSlug);
      
      // Save the new recipe
      String newFilename = fileManager.saveRecipe(editedRecipe);
      
      editRenderer.renderSuccess("Recipe saved successfully as: " + newFilename);
      promptController.waitForEnter();
      return true;
      
    } catch (Exception e) {
      editRenderer.renderError("Failed to save recipe: " + e.getMessage());
      promptController.waitForEnter();
      return false;
    }
  }

  private boolean isRecipeNameTaken(String name, String originalName) {
    if (name == null || name.trim().isEmpty()) {
      return false;
    }
    
    // If the new name is the same as the original (case-insensitive), it's not taken
    if (name.trim().equalsIgnoreCase(originalName.trim())) {
      return false;
    }
    
    String slug = SlugUtils.toSlug(name);
    boolean exists = fileManager.recipeExistsBySlug(slug);
    
    if (exists) {
      try {
        editRenderer.renderError("A recipe with the name '" + name + "' already exists. Please choose a different name.");
      } catch (IOException e) {
        System.err.println("Error rendering message: " + e.getMessage());
      }
    }
    
    return exists;
  }
}