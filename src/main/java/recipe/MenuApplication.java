package recipe;

import java.io.IOException;

public class MenuApplication {
  private final Terminal terminal;
  private final MenuModel model;
  private final MenuRenderer renderer;
  private final MenuController controller;

  public MenuApplication() throws IOException {
    terminal = new Terminal();
    model = new MenuModel();
    renderer = new MenuRenderer(terminal, model);
    controller = new MenuController(terminal, model, renderer);
  }

  public void run() throws IOException {
    try {
      renderer.render();
      terminal.setCursorVisible(true);

      while (true) {
        if (terminal.hasTerminalSizeChanged()) {
          renderer.render();
        }
        controller.handleInputWithTimeout();
      }
    } finally {
      terminal.close();
    }
  }

  public static void main(String[] args) throws IOException {
    MenuApplication app = new MenuApplication();
    app.run();
  }
}
