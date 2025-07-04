package recipe;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Arrays;

/**
 * A terminal-based recipe menu with a black background and colored text using
 * Lanterna GUI2.
 */
public class RecipeMenuGUI {
  public static void main(String[] args) {
    DefaultTerminalFactory factory = new DefaultTerminalFactory();
    try {
      Terminal terminal = factory.createTerminal();
      Screen screen = new TerminalScreen(terminal);
      screen.startScreen();
      screen.doResizeIfNecessary();

      // Create the GUI; black background filler
      WindowBasedTextGUI gui = new MultiWindowTextGUI(
          screen,
          new DefaultWindowManager(),
          new EmptySpace(TextColor.ANSI.BLACK));

      // Main window
      BasicWindow window = new BasicWindow("Recipe Calculator");
      window.setHints(Arrays.asList(Window.Hint.CENTERED));

      // Panel for layout
      Panel panel = new Panel();
      panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
      panel.setBackgroundColor(TextColor.ANSI.BLACK);

      // Add header label
      Label header = new Label("Recipe Calculator");
      header.setForegroundColor(TextColor.ANSI.WHITE);
      panel.addComponent(header);
      panel.addComponent(new EmptySpace(new TerminalSize(0, 1))); // spacer

      // Add menu buttons
      panel.addComponent(createButton("Calculate recipe", TextColor.ANSI.YELLOW,
          () -> MessageDialog.showMessageDialog(gui, "Calculate", "Recipe calculation coming soon.",
              MessageDialogButton.OK)));
      panel.addComponent(createButton("View existing recipes", TextColor.ANSI.BLUE,
          () -> MessageDialog.showMessageDialog(gui, "Recipes", "Chocolate Cake\nPasta Carbonara\nCaesar Salad",
              MessageDialogButton.OK)));
      panel.addComponent(createButton("New recipe", TextColor.ANSI.GREEN,
          () -> MessageDialog.showMessageDialog(gui, "New Recipe", "Feature coming soon.", MessageDialogButton.OK)));
      panel.addComponent(createButton("Edit recipe", TextColor.ANSI.CYAN,
          () -> MessageDialog.showMessageDialog(gui, "Edit Recipe", "Feature coming soon.", MessageDialogButton.OK)));
      panel.addComponent(createButton("Delete recipe", TextColor.ANSI.RED,
          () -> MessageDialog.showMessageDialog(gui, "Delete Recipe", "Feature coming soon.", MessageDialogButton.OK)));
      panel.addComponent(createButton("Quit", TextColor.ANSI.MAGENTA,
          window::close));

      // Attach panel to window and display
      window.setComponent(panel.withBorder(Borders.singleLine("Menu")));
      gui.addWindowAndWait(window);

      screen.stopScreen();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Button createButton(String title, TextColor color, Runnable action) {
    Button button = new Button(title, action);
    button.setRenderer(new ColoredButtonRenderer(color));
    return button;
  }

  private static class ColoredButtonRenderer extends ButtonRenderer {
    private final TextColor color;

    ColoredButtonRenderer(TextColor color) {
      this.color = color;
    }

    @Override
    public void drawComponent(TextGraphics graphics, Component component) {
      graphics.setForegroundColor(color);
      super.drawComponent(graphics, component);
    }
  }
}
