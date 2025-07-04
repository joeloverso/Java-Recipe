package recipe;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.*;

import java.io.IOException;

public class Menu {

  // The X plane for the icons
  private static int iconX;
  // Where the top bar of the ITEM menu starts
  // Menu item 1 is MenuStartY + 1
  private static int MenuStartY;
  // The space between each menu item
  private static final int MENU_ITEM_INCREMENT = 2;
  // The list of all items
  private static final String[] ITEMS = {
      "Calculate recipe", "View existing recipes", "New recipe",
      "Edit recipe", "Delete recipe", "Quit"
  };
  // c is for 'Calculate recipe', v is for 'View estisting recipes' and so on
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
  private static TerminalSize previousSize; // Track previous size for terminal resize detection.

  public static void initTerminal() throws IOException {
    DefaultTerminalFactory factory = new DefaultTerminalFactory();

    // Try to maximize the terminal window
    factory.setInitialTerminalSize(new TerminalSize(98, 24)); // Fallback size

    terminal = factory.createTerminal();
    terminal.enterPrivateMode();

    // Get actual terminal size after initialization
    updateTerminalSize();
    previousSize = terminalSize;
  }

  public static void updateTerminalSize() throws IOException {
    terminalSize = terminal.getTerminalSize();
  }

  // Method to check if terminal size changed
  private static boolean hasTerminalSizeChanged() {
    if (previousSize == null)
      return true;
    return terminalSize.getColumns() != previousSize.getColumns() ||
        terminalSize.getRows() != previousSize.getRows();
  }

  public static void closeTerminal() throws IOException {
    terminal.exitPrivateMode();
    terminal.close();
  }

  // Get terminal width
  public static int getTerminalWidth() {
    return terminalSize != null ? terminalSize.getColumns() : 98;
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
    } while (keyStroke.getKeyType() != KeyType.Enter
        && keyStroke.getKeyType() != KeyType.Escape);
  }

  // New method that waits for enter while handling resizes with redraw callback
  private static void waitForEnterWithResize(Runnable redrawCallback) throws IOException {
    KeyStroke keyStroke;
    TerminalSize currentSize = terminalSize;

    do {
      updateTerminalSize();
      if (currentSize.getColumns() != terminalSize.getColumns() ||
          currentSize.getRows() != terminalSize.getRows()) {
        // Terminal was resized - redraw
        redrawCallback.run();
        currentSize = terminalSize;
      }
      keyStroke = terminal.readInput();
    } while (keyStroke.getKeyType() != KeyType.Enter
        && keyStroke.getKeyType() != KeyType.Escape);
  }

  // Main menu
  public static void showMenu() throws IOException {
    clear();
    updateTerminalSize(); // Update size in case terminal was resized
    Menu menu = new Menu(); // Allows access to menu borders and decorations

    // First menu item
    String firstMenuItem = ITEMS[0];

    // ASCII Art Title - split into lines for easier centering

    String[] titleLines = {
        "██████╗ ███████╗ ██████╗██╗██████╗ ███████╗       ██████╗ █████╗ ██╗",
        "██╔══██╗██╔════╝██╔════╝██║██╔══██╗██╔════╝      ██╔════╝██╔══██╗██║",
        "██████╔╝█████╗  ██║     ██║██████╔╝█████╗  █████╗██║     ███████║██║",
        "██╔══██╗██╔══╝  ██║     ██║██╔═══╝ ██╔══╝  ╚════╝██║     ██╔══██║██║",
        "██║  ██║███████╗╚██████╗██║██║     ███████╗      ╚██████╗██║  ██║███████╗",
        "╚═╝  ╚═╝╚══════╝ ╚═════╝╚═╝╚═╝     ╚══════╝       ╚═════╝╚═╝  ╚═╝╚══════╝"
    };

    // For 3D Effect on second line
    // This piece of the line is to be cyan while the rest is yellow
    String secondLineBars = menu.hbar.repeat(6);
    /*
     * String[] titleLines = {
     * "▄▖    ▘      ▄▖  ▜",
     * "▙▘█▌▛▘▌▛▌█▌  ▌ ▀▌▐",
     * "▌▌▙▖▙▖▌▙▌▙▖  ▙▖█▌▐",
     * "▌"
     * };
     */

    // Calculate starting Y position to center the entire menu vertically
    // top padding is 1/5th height of terminal
    int topPaddingFactor = 5;
    int topPadding = getTerminalHeight() / topPaddingFactor;
    // int menuHeight = titleLines.length + topPadding; // Title + spacing + menu
    // items + prompt
    //
    // int startY = Math.max(1, topThirdY(menuHeight));
    int startY = topPadding;

    // Menu items - centered
    MenuStartY = startY + titleLines.length + 1;
    // X position for all items will be based on the first item
    int menuItemsX = firstThirdX(firstMenuItem);

    // For Current Menu Items X position
    // Gap between menu items and icons
    int iconIncrement = 3;
    // Icon X plane is menuItemsX -3
    iconX = menuItemsX - iconIncrement;

    // Menu border width based on the first menu item length
    // double menuWidthFactor = firstMenuItem.length() * 4.35;
    // Cast MenuWidthFactor to int
    // int menuWidth = (int) menuWidthFactor;
    // Menu border Left X position
    //
    int menuBorderLX = iconX - 5;
    int maxTitleLine = titleLines[0].length();
    // Menu border Right X position
    for (int i = 1; i <= titleLines.length - 2; i++) {
      if (titleLines[i].length() > titleLines[i - 1].length()) {
        maxTitleLine = titleLines[i].length();
      }
    }
    // Add one to maxTitleLine so the border doesn't overlap with the title.
    maxTitleLine++;
    int menuBorderRX;
    boolean isTitleBiggerThanFrame;
    if (menuBorderLX + maxTitleLine > menuBorderLX + (ITEMS[0].length() * 4)) {
      isTitleBiggerThanFrame = true;
    } else {
      isTitleBiggerThanFrame = false;
    }
    // Define menuBorderRX based on whether maxTitleLine is bigger than Menu Item 1
    // * 4 (taking the larger)
    if (isTitleBiggerThanFrame) {
      menuBorderRX = menuBorderLX + maxTitleLine;
    } else {
      menuBorderRX = menuBorderLX + (ITEMS[0].length() * 4);
    }

    // menu width is the space between the left and right border
    int menuWidth = menuBorderRX - menuBorderLX;

    // Tippy top TOP border frame
    // printAt(menuBorderLX, startY + 1, menu.topLeftBar +
    // menu.hbar.repeat(menuWidth - 3) + menu.topRightBar,
    // TextColor.ANSI.CYAN_BRIGHT);

    // Top Border Frame for Menu Items
    printAt(menuBorderLX, MenuStartY - 1, menu.topRightBar + menu.hbar.repeat(menuWidth - 1) + menu.topRightBar,
        TextColor.ANSI.CYAN_BRIGHT);

    // Left side Border
    for (int i = 0; i <= 12; i++) {
      printAt(menuBorderLX, MenuStartY + i, menu.vbar, TextColor.ANSI.CYAN_BRIGHT);
    }

    // Right side Border
    for (int i = 0; i <= 12; i++) {
      printAt(menuBorderRX, MenuStartY + i, menu.vbar, TextColor.ANSI.CYAN_BRIGHT);
    }

    // Bottom Border Frame for Menu Items
    int menuBorderBottomY = MenuStartY + 1 + ((ITEMS.length) * MENU_ITEM_INCREMENT);
    printAt(menuBorderLX, menuBorderBottomY,
        menu.bottomLeftBar + menu.hbar.repeat(menuWidth - 1) + menu.bottomRightBar,
        TextColor.ANSI.CYAN_BRIGHT);

    // Tippy top frame VERTICAL for Header Border RIGHT
    for (int i = -1; i >= -5; i--) {
      printAt(menuBorderRX, MenuStartY, menu.vbar, TextColor.ANSI.CYAN_BRIGHT);
    }

    // Tippy top border frame HORIZONTAL
    printAt(menuBorderLX, startY + 1,
        menu.topLeftBar + menu.hbar.repeat(menuWidth - 1) + menu.topRightBar,
        TextColor.ANSI.CYAN_BRIGHT);

    // Tippy top frame for Header Border LEFT
    int tippyTopBorderAdj = 2;
    for (int i = 0; i <= titleLines.length; i++) {
      printAt(menuBorderLX, startY + tippyTopBorderAdj + i, menu.vbar, TextColor.ANSI.CYAN_BRIGHT);
    }
    // Tippy top frame for Header Border RIGHT
    for (int i = 0; i <= titleLines.length; i++) {
      printAt(menuBorderRX, startY + tippyTopBorderAdj + i, menu.vbar, TextColor.ANSI.CYAN_BRIGHT);
    }

    // Draw the title ASCII art justified left or centered
    int y;
    int titleStartX;
    if (isTitleBiggerThanFrame) {
      titleStartX = menuBorderLX + 2; // Offset from left menu border
    } else {
      titleStartX = menuBorderLX + ((menuWidth / 2) - (maxTitleLine / 2));
    }
    for (int i = 0; i < titleLines.length; i++) {
      y = startY + i;
      // always draw the yellow ASCII text
      printAt(titleStartX, y, titleLines[i], TextColor.ANSI.YELLOW);
      // if it's the second line, add the cyan overlay
      if (i == 1) {
        // Find the position of the end of the 'E' on the second line.
        int x = titleStartX + 43; // hard-coded offset- It's a magic number forgive me.
        printAt(x, y, secondLineBars, TextColor.ANSI.CYAN_BRIGHT);
      }
    }

    // Tippy top around the Title Right horizontal + corner
    // printAt(menuBorderLX + menuWidth - 4, startY + 1, menu.hbar.repeat(4) +
    // menu.topRightBar,
    // TextColor.ANSI.CYAN_BRIGHT);

    // Rest of tippy top RIGHT vertical
    /*
     * for (int i = -1; i >= MenuStartY - startY; i--) {
     * if (i == -2 || i == -3) {
     * continue;
     * }
     * printAt(menuBorderRX, MenuStartY + i, menu.vbar, TextColor.ANSI.CYAN_BRIGHT);
     * }
     */

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
    int itemY = MenuStartY;

    for (int i = 0; i <= numItems - 1; i++) {
      // Calculate the Y position for each item
      itemY = MenuStartY + 1 + i * menuItemIncrement;
      TextColor.ANSI color = colors[i % colors.length]; // Cycle through colors
      printAt(menuItemsX, itemY, ITEMS[i], color);
    }

    // Centered yellow prompt
    String prompt = "\sPress a key for your choice: ";
    int promptLength = prompt.length();
    int promptX = menuBorderLX + (menuWidth / 2) - (promptLength / 2);
    printAt(promptX, menuBorderBottomY, prompt, TextColor.ANSI.YELLOW);
    printAt(promptX, menuBorderBottomY + 1, "OR Press ENTER on your choice ", TextColor.ANSI.YELLOW);

    // Display terminal size info in bottom corner for debugging reasons
    String sizeInfo = String.format("Terminal: %dx%d", getTerminalWidth(), getTerminalHeight());
    printAt(getTerminalWidth() - sizeInfo.length() - 1, getTerminalHeight() - 1, sizeInfo, TextColor.ANSI.WHITE);

    // Add unicode icons for funzies
    // IconX position is already declared and assigned
    int[] iconY = new int[ITEMS.length];
    for (int i = 0; i < ITEMS.length; i++) {
      iconY[i] = MenuStartY + 1 + (i * 2);
    }
    printAt(iconX, iconY[0], "∑", TextColor.ANSI.YELLOW);
    printAt(iconX, iconY[1], "☰", TextColor.ANSI.BLUE);
    printAt(iconX, iconY[2], "✚", TextColor.ANSI.GREEN);
    printAt(iconX, iconY[3], "✎", TextColor.ANSI.CYAN);
    printAt(iconX, iconY[4], "✖", TextColor.ANSI.RED);
    printAt(iconX, iconY[5], "⏻", TextColor.ANSI.MAGENTA);

    // Set the cursor on the first menu item
    terminal.setCursorPosition(iconX, MenuStartY + 1);
    // Add key
    // Padding from Right Menu Border
    int keyPadding = 7;
    int keyX = menuBorderLX + menuWidth - keyPadding;
    // Keys
    printAt(keyX, iconY[0], "c", TextColor.ANSI.YELLOW);
    printAt(keyX, iconY[1], "v", TextColor.ANSI.BLUE);
    printAt(keyX, iconY[2], "n", TextColor.ANSI.GREEN);
    printAt(keyX, iconY[3], "e", TextColor.ANSI.CYAN);
    printAt(keyX, iconY[4], "d", TextColor.ANSI.RED);
    printAt(keyX, iconY[5], "q", TextColor.ANSI.MAGENTA);

    // Set the cursor on the first menu item
    terminal.setCursorPosition(iconX, iconY[0]);
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
      // Draw menu once…
      showMenu();
      terminal.setCursorVisible(true);

      // selected is place of cursor in relation to Menu Items
      int selected = 0;
      // n = number of Menu Items
      int n = ITEMS.length;
      repositionCursor(selected);

      // …then loop forever, only moving cursor or firing actions
      while (true) {
        updateTerminalSize();
        if (hasTerminalSizeChanged()) {
          showMenu();
          repositionCursor(selected);
          previousSize = terminalSize;
        }
        KeyStroke key = readKey();
        KeyType kt = key.getKeyType();
        Character ch = key.getCharacter(); // may be null

        // Quit on ESC or Ctrl-C
        if (kt == KeyType.Escape ||
            (kt == KeyType.Character && key.isCtrlDown() && ch != null && ch == 'c')) {
          break;
        }

        // If it's a printable character…
        if (kt == KeyType.Character && ch != null) {
          ch = Character.toLowerCase(ch);

          // Hot-keys (launch feature immediately)
          if (ch == 'c' || ch == 'v' || ch == 'n' ||
              ch == 'e' || ch == 'd' || ch == 'q') {
            chooseItem(ch);
            if (ch == 'q')
              return; // exit on quit
            showMenu(); // redraw menu after action
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

        // Arrow-key navigation
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

        // Enter on highlighted item
        if (kt == KeyType.Enter) {
          char choice = ITEM_KEYS[selected];
          chooseItem(choice);
          if (choice == 'q')
            return;
          showMenu();
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
