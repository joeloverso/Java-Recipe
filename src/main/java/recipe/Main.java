package recipe;

import recipe.common.Terminal;
import recipe.main.MainMenuModel;
import recipe.main.MainMenuRenderer;
import recipe.main.MainMenuController;
import java.io.IOException;

public class Main {
  private final Terminal terminal;
  private final MainMenuModel model;
  private final MainMenuRenderer renderer;
  private final MainMenuController controller;

  public Main() throws IOException {
    terminal = new Terminal();
    model = new MainMenuModel();
    renderer = new MainMenuRenderer(terminal, model);
    controller = new MainMenuController(terminal, model, renderer);
    
    // Add shutdown hook to ensure terminal cleanup
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        terminal.close();
      } catch (IOException e) {
        // Ignore errors during shutdown
      }
    }));
  }

  public void run() throws IOException {
    try {
      // Clear the console for a clean interface
      terminal.clear();
      renderer.render();
      terminal.setCursorVisible(true);

      while (!controller.shouldExit()) {
        if (terminal.hasTerminalSizeChanged()) {
          renderer.render();
        }
        controller.handleInputWithTimeout();
      }
    } finally {
      terminal.clear();
      terminal.close();
    }
  }

  public static void main(String[] args) throws IOException {
    Main app = new Main();
    app.run();
  }
}
