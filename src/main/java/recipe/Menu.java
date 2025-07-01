package recipe;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.*;
import java.io.IOException;

public class Menu {

  private static Terminal terminal;
  private static TerminalSize terminalSize;

  public static void initTerminal() throws IOException {
    DefaultTerminalFactory factory = new DefaultTerminalFactory();

    // Try to maximize the terminal window
    factory.setInitialTerminalSize(new TerminalSize(120, 40)); // Fallback size

    terminal = factory.createTerminal();
    terminal.enterPrivateMode();

    // Get actual terminal size after initialization
    updateTerminalSize();
  }

  public static void updateTerminalSize() throws IOException {
    terminalSize = terminal.getTerminalSize();
  }

  public static void closeTerminal() throws IOException {
    terminal.exitPrivateMode();
    terminal.close();
  }

  // Get terminal width
  public static int getTerminalWidth() {
    return terminalSize != null ? terminalSize.getColumns() : 80;
  }

  // Get terminal height
  public static int getTerminalHeight() {
    return terminalSize != null ? terminalSize.getRows() : 24;
  }

  // Center text horizontally
  public static int centerX(String text) {
    return Math.max(0, (getTerminalWidth() - text.length()) / 2);
  }

  // Justify text horizontally to the first Third
  public static int firstThirdX(String text) {
    return Math.max(0, (getTerminalWidth() - text.length()) / 3);
  }

  // Center text horizontally with custom width
  public static int centerX(int contentWidth) {
    return Math.max(0, (getTerminalWidth() - contentWidth) / 2);
  }

  // Center text vertically at the top third of the terminal
  public static int topThirdY(int contentHeight) {
    return Math.max(0, (getTerminalHeight() - contentHeight) / 3);
  }

  // Print centered text at specific Y position
  public static void printCentered(int y, String text) throws IOException {
    printAt(centerX(text), y, text);
  }

  // Print centered colored text at specific Y position
  public static void printCentered(int y, String text, TextColor foreground) throws IOException {
    printAt(centerX(text), y, text, foreground);
  }

  // Print centered colored text with background at specific Y position
  public static void printCentered(int y, String text, TextColor foreground, TextColor background) throws IOException {
    printAt(centerX(text), y, text, foreground, background);
  }

  // Print colored text at current position
  public static void printColored(String text, TextColor foreground) throws IOException {
    terminal.setForegroundColor(foreground);
    terminal.putString(text);
    terminal.flush();
  }

  // Print colored text with background at current position
  public static void printColored(String text, TextColor foreground, TextColor background) throws IOException {
    terminal.setForegroundColor(foreground);
    terminal.setBackgroundColor(background);
    terminal.putString(text);
    terminal.flush();
  }

  // Print text at specific position
  public static void printAt(int x, int y, String text) throws IOException {
    terminal.setCursorPosition(x, y);
    terminal.putString(text);
    terminal.flush();
  }

  // Print colored text at specific position
  public static void printAt(int x, int y, String text, TextColor foreground) throws IOException {
    terminal.setCursorPosition(x, y);
    terminal.setForegroundColor(foreground);
    terminal.putString(text);
    terminal.flush();
  }

  // Print colored text with background at specific position
  public static void printAt(int x, int y, String text, TextColor foreground, TextColor background) throws IOException {
    terminal.setCursorPosition(x, y);
    terminal.setForegroundColor(foreground);
    terminal.setBackgroundColor(background);
    terminal.putString(text);
    terminal.flush();
  }

  // Clear screen
  public static void clear() throws IOException {
    terminal.clearScreen();
    terminal.flush();
  }

  // Move to next line
  public static void newLine() throws IOException {
    terminal.putString("\n");
    terminal.flush();
  }

  // Reset colors to default
  public static void resetColors() throws IOException {
    terminal.resetColorAndSGR();
    terminal.flush();
  }

  // Read a single key press
  public static KeyStroke readKey() throws IOException {
    return terminal.readInput();
  }

  // Wait for Enter key
  public static void waitForEnter() throws IOException {
    KeyStroke keyStroke;
    do {
      keyStroke = terminal.readInput();
    } while (keyStroke.getKeyType() != KeyType.Enter && keyStroke.getKeyType() != KeyType.Escape);
  }

  // Enhanced menu with centering
  public static void showMenu() throws IOException {
    clear();
    updateTerminalSize(); // Update size in case terminal was resized

    // ASCII Art Title - split into lines for easier centering
    String[] titleLines = {
        "██████╗ ███████╗ ██████╗██╗██████╗ ███████╗       ██████╗ █████╗ ██╗",
        "██╔══██╗██╔════╝██╔════╝██║██╔══██╗██╔════╝      ██╔════╝██╔══██╗██║",
        "██████╔╝█████╗  ██║     ██║██████╔╝█████╗  █████╗██║     ███████║██║",
        "██╔══██╗██╔══╝  ██║     ██║██╔═══╝ ██╔══╝  ╚════╝██║     ██╔══██║██║",
        "\s\s\s\s██║  ██║███████╗╚██████╗██║██║     ███████╗      ╚██████╗██║  ██║███████╗",
        "\s\s\s ╚═╝  ╚═╝╚══════╝ ╚═════╝╚═╝╚═╝     ╚══════╝       ╚═════╝╚═╝  ╚═╝╚══════╝"
    };
    String secondLineBars = "══════";

    // Calculate starting Y position to center the entire menu vertically
    int menuHeight = titleLines.length + 10; // Title + spacing + menu items + prompt
    int startY = Math.max(1, topThirdY(menuHeight) - 2);

    // Display centered title
    for (int i = 0; i < titleLines.length; i++) {
      printCentered(startY + i, titleLines[i], TextColor.ANSI.YELLOW);
    }
    // Menu items - centered
    int menuStartY = startY + titleLines.length + 1;
    // X position for all items will be based on the first item
    String firstMenuItem = "Calculate recipe";
    int menuX = firstThirdX(firstMenuItem);
    double menuWidthFactor = firstMenuItem.length() * 4.35;
    int menuWidth = (int) menuWidthFactor;
    // Menu border X position
    int menuBorderLX = menuX - (menuWidth / 8);
    int menuBorderRX = menuBorderLX + (menuWidth) + 5;

    // Top Border Frame for Menu Items
    printAt(menuBorderLX, menuStartY - 1, "╔" + "═".repeat(menuWidth + 4) + "╗", TextColor.ANSI.CYAN_BRIGHT);

    // Left side Border
    printAt(menuBorderLX, menuStartY - 0, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 1, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 2, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 3, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 4, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 5, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 6, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 7, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 8, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 9, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 10, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 11, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY + 12, "║", TextColor.ANSI.CYAN_BRIGHT);

    // Right side Border
    printAt(menuBorderRX, menuStartY - 0, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 1, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 2, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 3, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 4, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 5, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 6, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 7, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 8, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 9, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 10, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 11, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY + 12, "║", TextColor.ANSI.CYAN_BRIGHT);

    // Bottom Border Frame for Menu Items
    printAt(menuBorderLX, menuStartY + 13, "╚" + "═".repeat(menuWidth + 4) + "╝", TextColor.ANSI.CYAN_BRIGHT);

    // Tippy top frame for Header Border RIGHT
    printAt(menuBorderRX, menuStartY - 1, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY - 2, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY - 3, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY - 4, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderRX, menuStartY - 5, "║", TextColor.ANSI.CYAN_BRIGHT);

    // Top Border Frame for Menu Items
    printAt(menuBorderLX, menuStartY - 6, "╔" + "═".repeat(menuWidth - 2) + "╗", TextColor.ANSI.CYAN_BRIGHT);

    for (int i = 0; i < titleLines.length; i++) {
      int y = startY + i;

      // always draw the yellow ASCII text
      printCentered(y, titleLines[i], TextColor.ANSI.YELLOW);

      // if it's the second line, add the cyan overlay
      if (i == 1) {
        int x = centerX(titleLines[i]) + 43; // hard-coded offset
        printAt(x, y, secondLineBars, TextColor.ANSI.CYAN_BRIGHT);
      }
    }
    // Tip top Header Border RIGHT
    printAt(menuBorderLX + 70, startY + 1, "═".repeat(4) + "╗", TextColor.ANSI.CYAN_BRIGHT);

    // Tippy top frame for Header Border LEFT
    printAt(menuBorderLX, menuStartY - 1, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY - 2, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY - 3, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY - 4, "║", TextColor.ANSI.CYAN_BRIGHT);
    printAt(menuBorderLX, menuStartY - 5, "║", TextColor.ANSI.CYAN_BRIGHT);

    // Menu Items increment by 2 y to add a space between each item
    printAt(menuX, menuStartY + 1, firstMenuItem, TextColor.ANSI.YELLOW);
    printAt(menuX, menuStartY + 3, "View existing recipes", TextColor.ANSI.BLUE);
    printAt(menuX, menuStartY + 5, "New recipe", TextColor.ANSI.GREEN);
    printAt(menuX, menuStartY + 7, "Edit recipe", TextColor.ANSI.CYAN);
    printAt(menuX, menuStartY + 9, "Delete recipe", TextColor.ANSI.RED);
    printAt(menuX, menuStartY + 11, "Quit", TextColor.ANSI.MAGENTA);

    // Centered prompt
    printCentered(menuStartY + 13, "\sPress a key for your choice: ", TextColor.ANSI.YELLOW);
    printCentered(menuStartY + 14, "OR Press ENTER on your choice ", TextColor.ANSI.YELLOW);

    // Display terminal size info in bottom corner for debugging
    String sizeInfo = String.format("Terminal: %dx%d", getTerminalWidth(), getTerminalHeight());
    printAt(getTerminalWidth() - sizeInfo.length() - 1, getTerminalHeight() - 1, sizeInfo, TextColor.ANSI.WHITE);

    // Add unicode icons for funzies
    printAt(menuX - 3, menuStartY + 1, "∑", TextColor.ANSI.YELLOW);
    printAt(menuX - 3, menuStartY + 3, "☰", TextColor.ANSI.BLUE);
    printAt(menuX - 3, menuStartY + 5, "✚", TextColor.ANSI.GREEN);
    printAt(menuX - 3, menuStartY + 7, "✎", TextColor.ANSI.CYAN);
    printAt(menuX - 3, menuStartY + 9, "✖", TextColor.ANSI.RED);
    printAt(menuX - 3, menuStartY + 11, "⏻", TextColor.ANSI.MAGENTA);

    // Add key
    int keyX = menuBorderLX + menuWidth - 3;
    // Keys
    printAt(keyX, menuStartY + 1, "c", TextColor.ANSI.YELLOW);
    printAt(keyX, menuStartY + 3, "v", TextColor.ANSI.BLUE);
    printAt(keyX, menuStartY + 5, "n", TextColor.ANSI.GREEN);
    printAt(keyX, menuStartY + 7, "e", TextColor.ANSI.CYAN);
    printAt(keyX, menuStartY + 9, "d", TextColor.ANSI.RED);
    printAt(keyX, menuStartY + 11, "q", TextColor.ANSI.MAGENTA);

    resetColors();
  }

  // Enhanced status example with centering
  public static void showStatusExample() throws IOException {
    clear();
    updateTerminalSize();

    int centerY = getTerminalHeight() / 3;

    printCentered(centerY - 2, "Recipe Status Messages:", TextColor.ANSI.WHITE);

    // Status messages centered
    printCentered(centerY, "✓ Recipe saved successfully!", TextColor.ANSI.GREEN);
    printCentered(centerY + 1, "⚠ Missing ingredient: Salt", TextColor.ANSI.YELLOW);
    printCentered(centerY + 2, "✗ Recipe not found!", TextColor.ANSI.RED);
    printCentered(centerY + 3, "ℹ 42 recipes loaded", TextColor.ANSI.BLUE);

    printCentered(centerY + 6, "Press Enter to continue...", TextColor.ANSI.WHITE);
    resetColors();
  }

  // Enhanced recipe list with centering
  public static void showRecipeList() throws IOException {
    clear();
    updateTerminalSize();

    // Calculate positions for centered table
    int tableWidth = 45;
    int startX = centerX(tableWidth);
    int startY = topThirdY(10) - 2;

    // Header
    printAt(startX, startY, "Your Recipes", TextColor.ANSI.CYAN);
    printAt(startX, startY + 1, "═".repeat(tableWidth), TextColor.ANSI.CYAN);

    // Table headers
    printAt(startX, startY + 3, "Name", TextColor.ANSI.YELLOW);
    printAt(startX + 15, startY + 3, "Category", TextColor.ANSI.YELLOW);
    printAt(startX + 30, startY + 3, "Time", TextColor.ANSI.YELLOW);

    // Divider
    printAt(startX, startY + 4, "─".repeat(tableWidth), TextColor.ANSI.WHITE);

    // Recipe data
    printAt(startX, startY + 5, "Chocolate Cake", TextColor.ANSI.WHITE);
    printAt(startX + 15, startY + 5, "Dessert", TextColor.ANSI.GREEN);
    printAt(startX + 30, startY + 5, "45 min", TextColor.ANSI.BLUE);

    printAt(startX, startY + 6, "Pasta Carbonara", TextColor.ANSI.WHITE);
    printAt(startX + 15, startY + 6, "Main", TextColor.ANSI.GREEN);
    printAt(startX + 30, startY + 6, "20 min", TextColor.ANSI.BLUE);

    printAt(startX, startY + 7, "Caesar Salad", TextColor.ANSI.WHITE);
    printAt(startX + 15, startY + 7, "Appetizer", TextColor.ANSI.GREEN);
    printAt(startX + 30, startY + 7, "15 min", TextColor.ANSI.BLUE);

    printCentered(startY + 10, "Press Enter to continue...", TextColor.ANSI.YELLOW);
    resetColors();
  }

  public static void main(String[] args) throws IOException {
    initTerminal();

    try {
      while (true) {
        showMenu();

        // Read user input using Lanterna's input method
        KeyStroke keyStroke = readKey();

        // Handle Ctrl+C for graceful exit
        if (keyStroke.isCtrlDown() && keyStroke.getCharacter() != null && keyStroke.getCharacter() == 'c') {
          break;
        }

        char choice = ' ';
        if (keyStroke.getCharacter() != null) {
          choice = Character.toLowerCase(keyStroke.getCharacter());
        }

        switch (choice) {
          case '1':
          case 'c':
            showStatusExample();
            waitForEnter();
            break;
          case '2':
          case 'v':
            showRecipeList();
            waitForEnter();
            break;
          case '3':
          case 'n':
            clear();
            updateTerminalSize();
            printCentered(getTerminalHeight() / 3, "NEW RECIPE Feature Coming Soon!", TextColor.ANSI.CYAN);
            printCentered(getTerminalHeight() / 3 + 1, "Press Enter to continue...", TextColor.ANSI.WHITE);
            waitForEnter();
            break;
          case '4':
          case 'e':
            clear();
            updateTerminalSize();
            printCentered(getTerminalHeight() / 3, "EDIT Recipe Feature Coming Soon!", TextColor.ANSI.CYAN);
            printCentered(getTerminalHeight() / 3 + 1, "Press Enter to continue...", TextColor.ANSI.WHITE);
            waitForEnter();
            break;
          case '5':
          case 'd':
            clear();
            updateTerminalSize();
            printCentered(getTerminalHeight() / 3, "DELETE RECIPE Feature Coming Soon!", TextColor.ANSI.CYAN);
            printCentered(getTerminalHeight() / 3 + 1, "Press Enter to continue...", TextColor.ANSI.WHITE);
            waitForEnter();
            break;
          case 'q':
            clear();
            updateTerminalSize();
            printCentered(getTerminalHeight() / 3, "Thanks for using Recipe Calculator!", TextColor.ANSI.GREEN);
            newLine();
            return;
          default:
            clear();
            updateTerminalSize();
            int errorY = getTerminalHeight() / 3;
            printCentered(errorY, "Invalid choice. Please try again.", TextColor.ANSI.RED);
            printCentered(errorY + 1, "Valid choices: 1, 2, 3, 4, Q", TextColor.ANSI.WHITE);
            printCentered(errorY + 2, "Press Enter to continue...", TextColor.ANSI.WHITE);
            waitForEnter();
        }
      }
    } finally {
      closeTerminal();
    }
  }
}
