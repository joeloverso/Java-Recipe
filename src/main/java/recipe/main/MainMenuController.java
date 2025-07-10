package recipe.main;

import recipe.common.AbstractMenuController;
import recipe.common.RecipeFileManager;
import recipe.common.Terminal;
import recipe.new_recipe.NewRecipeController;
import recipe.view.ViewRecipeController;
import recipe.calculate.CalculateRecipeController;
import recipe.edit.EditRecipeController;
import recipe.delete.DeleteRecipeController;
import com.googlecode.lanterna.TextColor;
import java.io.IOException;

public class MainMenuController extends AbstractMenuController<MainMenuItem> {
  public MainMenuController(Terminal terminal, MainMenuModel model, MainMenuRenderer renderer) {
    super(terminal, model, renderer);
  }

  @Override
  protected void executeAction(char choice) throws IOException {
    switch (choice) {
      case 'c':
        try {
          // Clear screen before transitioning to calculate recipe menu
          terminal.clear();
          RecipeFileManager recipeFileManager = new RecipeFileManager();
          CalculateRecipeController calculateController = new CalculateRecipeController(terminal, recipeFileManager);
          calculateController.run();
          // After calculating recipes, re-render the main menu
          renderer.render();
        } catch (Exception e) {
          renderer.renderMessage("Error calculating recipe: " + e.getMessage(), TextColor.ANSI.RED);
          waitForInput();
        }
        break;
      case 'v':
        try {
          // Clear screen before transitioning to view recipe menu
          terminal.clear();
          RecipeFileManager recipeFileManager = new RecipeFileManager();
          ViewRecipeController viewController = new ViewRecipeController(terminal, recipeFileManager);
          viewController.run();
          // After viewing recipes, re-render the main menu
          renderer.render();
        } catch (Exception e) {
          renderer.renderMessage("Error viewing recipes: " + e.getMessage(), TextColor.ANSI.RED);
          waitForInput();
        }
        break;
      case 'n':
        try {
          // Clear screen before transitioning to new recipe menu
          terminal.clear();
          NewRecipeController newRecipeController = new NewRecipeController(terminal);
          newRecipeController.createNewRecipe();
          // After recipe creation, re-render the main menu
          renderer.render();
        } catch (Exception e) {
          renderer.renderMessage("Error creating recipe: " + e.getMessage(), TextColor.ANSI.RED);
          waitForInput();
        }
        break;
      case 'e':
        try {
          // Clear screen before transitioning to edit recipe menu
          terminal.clear();
          RecipeFileManager recipeFileManager = new RecipeFileManager();
          EditRecipeController editController = new EditRecipeController(terminal, recipeFileManager);
          editController.run();
          // After editing recipes, re-render the main menu
          renderer.render();
        } catch (Exception e) {
          renderer.renderMessage("Error editing recipe: " + e.getMessage(), TextColor.ANSI.RED);
          waitForInput();
        }
        break;
      case 'd':
        try {
          // Clear screen before transitioning to delete recipe menu
          terminal.clear();
          RecipeFileManager recipeFileManager = new RecipeFileManager();
          DeleteRecipeController deleteController = new DeleteRecipeController(terminal, recipeFileManager);
          deleteController.run();
          // After deleting recipes, re-render the main menu
          renderer.render();
        } catch (Exception e) {
          renderer.renderMessage("Error deleting recipe: " + e.getMessage(), TextColor.ANSI.RED);
          waitForInput();
        }
        break;
      case 'q':
        renderer.renderMessage("Thanks for using Recipe Calculator!", TextColor.ANSI.MAGENTA);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        shouldExit = true;
        break;
    }
  }
}
