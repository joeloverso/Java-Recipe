package recipe;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.*;

import java.io.IOException;

public class Menu {

  private static int iconX;
  private static int MenuStartY;
  private static final int MENU_ITEM_INCREMENT = 2;
  private static final String[] ITEMS = {
      "Calculate recipe", "View existing recipes", "New recipe",
      "Edit recipe", "Delete recipe", "Quit"
  };

  private static final char[] ITEM_KEYS = { 'c', 'v', 'n', 'e', 'd', 'q' };
  // Borders and decorations
  String hbar = "═";
  String vbar = "║";
  String topLeftBar = "╔";
  String topRightBar = "╗";
  String bottomLeftBar = "╚";
  String bottomRightBar = "╝";

  // Text Colors- in order for Main Menu Items
  TextColor.ANSI[] colors = {
      TextColor.ANSI.YELLOW,
      TextColor.ANSI.BLUE,
      TextColor.ANSI.GREEN,
      TextColor.ANSI.CYAN,
      TextColor.ANSI.RED,
      TextColor.ANSI.MAGENTA
  };

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
  public static int topThird(int contentHeight) {
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
    } while (keyStroke.getKeyType() != KeyType.Enter
        && keyStroke.getKeyType() != KeyType.Escape);
  }

  // Main menu
  public static void showMenu() throws IOException {
    clear();
    updateTerminalSize(); // Update size in case terminal was resized
    Menu menu = new Menu(); // Allows access to menu borders and decorations

    // Menu items
    String[] Items = { "Calculate recipe", "View existing recipes", "New recipe", "Edit recipe", "Delete recipe",
        "Quit" };

    // First menu item
    String firstMenuItem = ITEMS[0];

    // ASCII Art Title - split into lines for easier centering
    String[] titleLines = {
        "██████╗ ███████╗ ██████╗██╗██████╗ ███████╗       ██████╗ █████╗ ██╗",
        "██╔══██╗██╔════╝██╔════╝██║██╔══██╗██╔════╝      ██╔════╝██╔══██╗██║",
        "██████╔╝█████╗  ██║     ██║██████╔╝█████╗  █████╗██║     ███████║██║",
        "██╔══██╗██╔══╝  ██║     ██║██╔═══╝ ██╔══╝  ╚════╝██║     ██╔══██║██║",
        "\s\s\s\s██║  ██║███████╗╚██████╗██║██║     ███████╗      ╚██████╗██║  ██║███████╗",
        "\s\s\s ╚═╝  ╚═╝╚══════╝ ╚═════╝╚═╝╚═╝     ╚══════╝       ╚═════╝╚═╝  ╚═╝╚══════╝"
    };
    // For 3D Effect on second line
    // This piece of the line is to be cyan while the rest is yellow
    String secondLineBars = menu.hbar.repeat(6);

    // Calculate starting Y position to center the entire menu vertically
    int menuHeight = titleLines.length + 10; // Title + spacing + menu items + prompt
    //
    int startY = Math.max(1, topThird(menuHeight) - 2);

    // Menu items - centered
    int menuStartY = startY + titleLines.length + 1;
    // X position for all items will be based on the first item
    int menuItemsX = firstThirdX(firstMenuItem);

    // For Current Menu Items X position
    // Gap between menu items and icons
    int iconIncrement = 3;
    // Icon X plane is menuItemsX -3
    iconX = menuItemsX - iconIncrement;
    MenuStartY = menuStartY;

    // Menu border width based on the first menu item length
    double menuWidthFactor = firstMenuItem.length() * 4.35;
    // Cast MenuWidthFactor to int
    int menuWidth = (int) menuWidthFactor;
    // Menu border Left X position
    int menuBorderLX = menuItemsX - (menuWidth / 8);
    // Menu border Right X position
    int menuBorderRX = menuBorderLX + (menuWidth) + 5;

    // Top Border Frame for Menu Items
    printAt(menuBorderLX, menuStartY - 1, menu.topRightBar + menu.hbar.repeat(menuWidth + 4) + menu.topRightBar,
        TextColor.ANSI.CYAN_BRIGHT);

    // Left side Border
    for (int i = 0; i <= 12; i++) {
      printAt(menuBorderLX, menuStartY + i, menu.vbar, TextColor.ANSI.CYAN_BRIGHT);
    }

    // Right side Border
    for (int i = 0; i <= 12; i++) {
      printAt(menuBorderRX, menuStartY + i, menu.vbar, TextColor.ANSI.CYAN_BRIGHT);
    }

    // Bottom Border Frame for Menu Items
    printAt(menuBorderLX, menuStartY + 13, menu.bottomLeftBar + menu.hbar.repeat(menuWidth + 4) + menu.bottomRightBar,
        TextColor.ANSI.CYAN_BRIGHT);

    // Tippy top frame for Header Border RIGHT
    for (int i = -1; i >= -5; i--) {
      printAt(menuBorderRX, menuStartY, menu.vbar, TextColor.ANSI.CYAN_BRIGHT);
    }

    // Top Border Frame for Menu Items
    printAt(menuBorderLX, menuStartY - 6, menu.topLeftBar + menu.hbar.repeat(menuWidth - 2) + menu.topRightBar,
        TextColor.ANSI.CYAN_BRIGHT);

    // Draw the title ASCII art centered
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
    // Tippy top around the Title Right
    printAt(menuBorderLX + 70, startY + 1, menu.hbar.repeat(4) + menu.topRightBar, TextColor.ANSI.CYAN_BRIGHT);

    // Rest of tippy top RIGHT
    for (int i = -1; i >= -5; i--) {
      if (i == -2 || i == -3) {
        continue;
      }
      printAt(menuBorderRX, menuStartY + i, menu.vbar, TextColor.ANSI.CYAN_BRIGHT);
    }

    // Tippy top frame for Header Border LEFT
    for (int i = -1; i >= -5; i--) {
      printAt(menuBorderLX, menuStartY + i, menu.vbar, TextColor.ANSI.CYAN_BRIGHT);
    }
    // Array of colors corresponding to each menu item
    TextColor.ANSI[] colors = {
        TextColor.ANSI.YELLOW,
        TextColor.ANSI.BLUE,
        TextColor.ANSI.GREEN,
        TextColor.ANSI.CYAN,
        TextColor.ANSI.RED,
        TextColor.ANSI.MAGENTA
    };

    // Menu Items increment by 2 y to add a space between each item
    int menuItemIncrement = 2;
    int numItems = ITEMS.length;
    int itemY = menuStartY;

    for (int i = 0; i <= numItems - 1; i++) {
      // Calculate the Y position for each item
      itemY = menuStartY + 1 + i * menuItemIncrement;
      TextColor.ANSI color = colors[i % colors.length]; // Cycle through colors
      printAt(menuItemsX, itemY, ITEMS[i], color);
    }

    // Centered yellow prompt
    int centerMessageY = menuStartY + 13;
    printCentered(centerMessageY, "\sPress a key for your choice: ", TextColor.ANSI.YELLOW);
    printCentered(centerMessageY + 1, "OR Press ENTER on your choice ", TextColor.ANSI.YELLOW);

    // Display terminal size info in bottom corner for debugging reasons
    String sizeInfo = String.format("Terminal: %dx%d", getTerminalWidth(), getTerminalHeight());
    printAt(getTerminalWidth() - sizeInfo.length() - 1, getTerminalHeight() - 1, sizeInfo, TextColor.ANSI.WHITE);

    // Add unicode icons for funzies
    // IconX position is already declared and assigned
    // Icon 1- Calculate
    int icon1Y = menuStartY + 1;
    // Icon 2 - View
    int icon2Y = menuStartY + 3;
    // Icon 3 - Add Recipe
    int icon3Y = menuStartY + 5;
    // Icon 4 - Edit Recipe
    int icon4Y = menuStartY + 7;
    // Icon 5 - Delete Recipe
    int icon5Y = menuStartY + 9;
    // Icon 6 - Quit
    int icon6Y = menuStartY + 11;

    printAt(iconX, icon1Y, "∑", TextColor.ANSI.YELLOW);
    printAt(iconX, icon2Y, "☰", TextColor.ANSI.BLUE);
    printAt(iconX, icon3Y, "✚", TextColor.ANSI.GREEN);
    printAt(iconX, icon4Y, "✎", TextColor.ANSI.CYAN);
    printAt(iconX, icon5Y, "✖", TextColor.ANSI.RED);
    printAt(iconX, icon6Y, "⏻", TextColor.ANSI.MAGENTA);

    // Set the cursor on the first menu item
    terminal.setCursorPosition(iconX, menuStartY + 1);
    // Add key
    int keyX = menuBorderLX + menuWidth - 3;
    // Keys
    printAt(keyX, menuStartY + 1, "c", TextColor.ANSI.YELLOW);
    printAt(keyX, menuStartY + 3, "v", TextColor.ANSI.BLUE);
    printAt(keyX, menuStartY + 5, "n", TextColor.ANSI.GREEN);
    printAt(keyX, menuStartY + 7, "e", TextColor.ANSI.CYAN);
    printAt(keyX, menuStartY + 9, "d", TextColor.ANSI.RED);
    printAt(keyX, menuStartY + 11, "q", TextColor.ANSI.MAGENTA);

    // Set the cursor on the first menu item
    terminal.setCursorPosition(iconX, icon1Y);
    terminal.flush();

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
    int startY = topThird(10) - 2;

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
      // 1) Draw once…
      showMenu();
      terminal.setCursorVisible(true);

      int selected = 0;
      int n = ITEMS.length;
      repositionCursor(selected);

      // 2) …then loop forever, only moving cursor or firing actions
      while (true) {
        KeyStroke key = readKey();
        KeyType kt = key.getKeyType();
        Character ch = key.getCharacter(); // may be null

        // 2.1) Quit on ESC or Ctrl-C
        if (kt == KeyType.Escape ||
            (kt == KeyType.Character && key.isCtrlDown() && ch != null && ch == 'c')) {
          break;
        }

        // 2.2) If it's a printable character…
        if (kt == KeyType.Character && ch != null) {
          ch = Character.toLowerCase(ch);

          // 2.2a) Hot-keys (launch feature immediately)
          if (ch == 'c' || ch == 'v' || ch == 'n' ||
              ch == 'e' || ch == 'd' || ch == 'q') {
            chooseItem(ch);
            if (ch == 'q')
              return; // exit on quit
            showMenu(); // redraw menu after action
            selected = 0;
            repositionCursor(selected);
            continue;
          }

          // 2.2b) j/k navigation
          if (ch == 'j') {
            selected = (selected + 1) % n;
            repositionCursor(selected);
            continue;
          }
          if (ch == 'k') {
            selected = (selected - 1 + n) % n;
            repositionCursor(selected);
            continue;
          }

          // anything else: ignore
          continue;
        }

        // 2.3) Arrow-key navigation
        if (kt == KeyType.ArrowDown) {
          selected = (selected + 1) % n;
          repositionCursor(selected);
          continue;
        }
        if (kt == KeyType.ArrowUp) {
          selected = (selected - 1 + n) % n;
          repositionCursor(selected);
          continue;
        }

        // 2.4) Enter on highlighted item
        if (kt == KeyType.Enter) {
          char choice = ITEM_KEYS[selected];
          chooseItem(choice);
          if (choice == 'q')
            return;
          showMenu();
          selected = 0;
          repositionCursor(selected);
          continue;
        }

        // otherwise loop back
      }
    } finally {
      closeTerminal();
    }
  }

  // Moves the cursor to the icon column for the given menu index.
  private static void repositionCursor(int selected) throws IOException {
    terminal.setCursorPosition(
        iconX, // Curosr starts at iconX position
        MenuStartY + 1 + selected * MENU_ITEM_INCREMENT);
    terminal.flush();
  }

  // Runs the chosen method
  // then waits for Enter or Escape before returning to main menu.
  private static void chooseItem(char choice) throws IOException {
    switch (choice) {
      case 'c':
        showStatusExample();
        waitForEnter();
        break;
      case 'v':
        showRecipeList();
        waitForEnter();
        break;
      case 'n':
        clear();
        printCentered(getTerminalHeight() / 3,
            "NEW RECIPE Feature Coming Soon!",
            TextColor.ANSI.CYAN);
        waitForEnter();
        break;
      case 'e':
        clear();
        printCentered(getTerminalHeight() / 3,
            "EDIT Recipe Feature Coming Soon!",
            TextColor.ANSI.CYAN);
        waitForEnter();
        break;
      case 'd':
        clear();
        printCentered(getTerminalHeight() / 3,
            "DELETE RECIPE Feature Coming Soon!",
            TextColor.ANSI.CYAN);
        waitForEnter();
        break;
      case 'q':
        clear();
        printCentered(getTerminalHeight() / 3,
            "Thanks for using Recipe Calculator!",
            TextColor.ANSI.GREEN);
        newLine();
        break;
    }
  }
}
