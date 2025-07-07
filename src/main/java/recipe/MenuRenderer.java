package recipe;

import com.googlecode.lanterna.TextColor;
import java.io.IOException;

public class MenuRenderer {
  private final Terminal terminal;
  private final MenuModel model;

  private static final int MENU_ITEM_INCREMENT = 2;
  private static final int MIN_TERMINAL_WIDTH = 63;
  private static final int MIN_TERMINAL_HEIGHT = 27;

  private final String hbar = "═";
  private final String vbar = "║";
  private final String tippyTopLeftBar = "╔";
  private final String topLeftBar = "╠";
  private final String tippyTopRightBar = "╗";
  private final String topRightBar = "╣";
  private final String bottomLeftBar = "╚";
  private final String bottomRightBar = "╝";

  // Y variables
  private boolean shortTerminal;
  private int topPadding;
  private int startY;
  private int menuStartY;
  private int menuBorderLX;

  // Y padding variables
  int tippyTopBorderAdj = 2;

  // X variables
  private boolean skinnyTerminal;
  private int menuBorderRX;
  private int iconX;
  private int menuItemsX;

  // X padding variables
  private int borderIconPadding = 5;
  // Icon padding from MenuItems X
  int iconMenuItemPadding = 3;
  // Padding from MenuItem to border
  int menuItemBorderPadding = 8;
  // Miscellanious
  private int menuWidth;

  public MenuRenderer(Terminal terminal, MenuModel model) {
    this.terminal = terminal;
    this.model = model;
  }

  public void render() throws IOException {
    terminal.clear();

    calculateLayout();
    drawMenuBorder();
    drawTitle();
    drawMenuItems();
    drawPrompt();
    drawDebugInfo();

    positionCursor();
    terminal.resetColors();
  }

  private boolean isTerminalSkinny() {
    skinnyTerminal = false;
    if (terminal.getWidth() < MIN_TERMINAL_WIDTH) {
      skinnyTerminal = true;
    }
    return skinnyTerminal;
  }

  private boolean isTerminalShort() {
    shortTerminal = false;
    if (terminal.getHeight() < MIN_TERMINAL_HEIGHT) {
      shortTerminal = true;
    }
    return shortTerminal;
  }

  private int getTopPadding(boolean shortTerminal) {
    if (shortTerminal) {
      topPadding = 2;
    } else {
      topPadding = terminal.getHeight() / 5;
    }
    return topPadding;
  }

  private int getTitleLinesMaxLength() {
    String[] titleLines = model.getTitleLines(terminal.getWidth() < MIN_TERMINAL_WIDTH);
    int maxTitleLine = titleLines[0].length();
    for (int i = 1; i < titleLines.length; i++) {
      if (titleLines[i].length() > maxTitleLine) {
        maxTitleLine = titleLines[i].length();
      }
    }
    maxTitleLine++;
    return maxTitleLine;
  }

  private void calculateLayout() {
    boolean skinnyTerminal = isTerminalSkinny();
    boolean shortTerminal = isTerminalShort();
    topPadding = getTopPadding(shortTerminal);
    startY = topPadding;
    String[] titleLines = model.getTitleLines(terminal.getWidth() < MIN_TERMINAL_WIDTH);
    menuStartY = startY + titleLines.length + 1;
    // Calculate max title line length
    int maxTitleLine = getTitleLinesMaxLength();
    ;
    // Set X values
    if (skinnyTerminal) {
      int borderTerminalPadding = 2;
      menuBorderLX = borderTerminalPadding;
      menuBorderRX = terminal.getWidth() - borderTerminalPadding;
      menuItemsX = menuBorderLX + menuItemBorderPadding;
      iconX = menuItemsX - iconMenuItemPadding;
    } else {
      menuBorderLX = centerX(maxTitleLine);
      menuBorderRX = menuBorderLX + maxTitleLine;
      iconX = menuBorderLX + borderIconPadding;
      menuItemsX = iconX + iconMenuItemPadding;
    }
    menuWidth = menuBorderRX - menuBorderLX;
  }

  private void drawTitle() throws IOException {
    String[] titleLines = model.getTitleLines(terminal.getWidth() < MIN_TERMINAL_WIDTH);
    boolean shortTerminal = isTerminalShort();
    int topPadding = getTopPadding(shortTerminal);
    int startY = topPadding;

    int maxTitleLine = getTitleLinesMaxLength() - 1;
    int borderTitlePadding = 2;
    int titleStartX = borderTitlePadding + menuBorderLX + ((menuWidth / 2) - (maxTitleLine / 2));

    for (int i = 0; i < titleLines.length; i++) {
      int y = startY + i;
      terminal.printAt(titleStartX, y, titleLines[i], TextColor.ANSI.YELLOW);

      // draw secondLineBars only on our nice big ASCII art between the two words;
      // RECIPE--CALCULATOR
      // on the second line for
      // an amazing, incredible 3D effect.

      if (i == 1 && titleLines[1].length() == 68) {
        String secondLineBars = hbar.repeat(6); // This represents the space betw
        int x = titleStartX + 43; // This number is hard-coded. That's just the way it is.
        terminal.printAt(x, y, secondLineBars, TextColor.ANSI.CYAN_BRIGHT);
      }
    }
  }

  private void drawSkinnyTippyTopBorder() throws IOException {
    terminal.printAt(menuBorderLX, menuStartY - model.getSmallTitleLinesHeight() - tippyTopBorderAdj,
        tippyTopLeftBar + hbar.repeat(menuWidth - 1) + tippyTopRightBar, TextColor.ANSI.CYAN_BRIGHT);
  }

  private void drawSkinnyTippyTopSideBorders() throws IOException {
    for (int i = 0; i <= model.getSmallTitleLinesHeight() + 1; i++) {
      terminal.printAt(menuBorderLX, startY + tippyTopBorderAdj - i, vbar, TextColor.ANSI.CYAN_BRIGHT);
      terminal.printAt(menuBorderRX, startY + tippyTopBorderAdj - i, vbar, TextColor.ANSI.CYAN_BRIGHT);

    }
  }

  private void drawTippyTopBorder() throws IOException {
    terminal.printAt(menuBorderLX, startY + 1,
        tippyTopLeftBar + hbar.repeat(menuWidth - 1) + tippyTopRightBar,
        TextColor.ANSI.CYAN_BRIGHT);
  }

  private void drawTippyTopSideBorders() throws IOException {
    String[] titleLines = model.getTitleLines(terminal.getWidth() < MIN_TERMINAL_WIDTH);
    for (int i = 0; i <= titleLines.length; i++) {
      terminal.printAt(menuBorderLX, startY + tippyTopBorderAdj + i, vbar, TextColor.ANSI.CYAN_BRIGHT);
      terminal.printAt(menuBorderRX, startY + tippyTopBorderAdj + i, vbar, TextColor.ANSI.CYAN_BRIGHT);
    }
  }

  private void drawMenuTopBorder() throws IOException {
    terminal.printAt(menuBorderLX, menuStartY - 1,
        topLeftBar +
            hbar.repeat(menuWidth - 1) + topRightBar,
        TextColor.ANSI.CYAN_BRIGHT);
  }

  private void drawMenuSideBorders() throws IOException {
    for (int i = 0; i <= model.getItemCount() * MENU_ITEM_INCREMENT; i++) {
      terminal.printAt(menuBorderLX, menuStartY + i, vbar, TextColor.ANSI.CYAN_BRIGHT);
      terminal.printAt(menuBorderRX, menuStartY + i, vbar, TextColor.ANSI.CYAN_BRIGHT);
    }
  }

  private void drawMenuBottomBorder() throws IOException {
    int menuBorderBottomY = menuStartY + 1 + (model.getItemCount() * MENU_ITEM_INCREMENT);
    terminal.printAt(menuBorderLX, menuBorderBottomY,
        bottomLeftBar + hbar.repeat(menuWidth - 1) + bottomRightBar,
        TextColor.ANSI.CYAN_BRIGHT);
  }

  private void drawMenuBorder() throws IOException {
    startY = topPadding;
    boolean skinnyTerminal = isTerminalSkinny();
    if (skinnyTerminal) {
      drawSkinnyTippyTopBorder();
      drawSkinnyTippyTopSideBorders();
    } else {
      drawTippyTopBorder();
      drawTippyTopSideBorders();
    }
    drawMenuTopBorder();
    drawMenuSideBorders();
    drawMenuBottomBorder();
  }

  private void drawMenuItems() throws IOException {
    String[] items = model.getItems();
    String[] icons = model.getIcons();
    char[] itemKeys = model.getItemKeys();
    TextColor.ANSI[] colors = model.getColors();

    // Menu item is hard coded to 8 spaces to the right of the left menu border
    int menuItemPadding = 8;
    int menuItemsX = menuBorderLX + menuItemPadding;
    // Menu key is hard coded 7 spaces to the left of the right border
    int keyPadding = 7;
    int keyX = menuBorderRX - keyPadding;

    // Draw Menu Items
    for (int i = 0; i < items.length; i++) {
      int itemY = menuStartY + 1 + i * MENU_ITEM_INCREMENT;
      // Text colors will never be out of bounds
      // Colors will auto cycle if there are more items than colors
      TextColor.ANSI color = colors[i % colors.length];

      terminal.printAt(menuItemsX, itemY, items[i], color);
      terminal.printAt(iconX, itemY, icons[i], color);
      terminal.printAt(keyX, itemY, String.valueOf(itemKeys[i]), color);
    }
  }

  private void drawPrompt() throws IOException {
    int menuBorderBottomY = menuStartY + 1 + (model.getItemCount() * MENU_ITEM_INCREMENT);

    String prompt = " Press a key for your choice: ";
    int promptLength = prompt.length();
    // We need to use a double to adjust for potential odd menu-widths and
    // promptlengths.
    // Add 1 to adjust for the something- the 0.5 is so that we round up.
    double promptXrounder = menuBorderLX + 1.5 + (menuWidth / 2) - (promptLength / 2);
    int promptX = (int) promptXrounder;

    terminal.printAt(promptX, menuBorderBottomY, prompt, TextColor.ANSI.YELLOW);
    terminal.printAt(promptX, menuBorderBottomY + 1, "OR Press ENTER on your choice ", TextColor.ANSI.YELLOW);
  }

  private void drawDebugInfo() throws IOException {
    String sizeInfo = String.format("Terminal: %dx%d", terminal.getWidth(), terminal.getHeight());
    terminal.printAt(terminal.getWidth() - sizeInfo.length() - 1, terminal.getHeight() - 1,
        sizeInfo, TextColor.ANSI.WHITE);
  }

  public void positionCursor() throws IOException {
    int cursorY = menuStartY + 1 + model.getSelectedIndex() * MENU_ITEM_INCREMENT;
    terminal.setCursorPosition(iconX, cursorY);
  }

  private int centerX(String text) {
    return Math.max(0, (terminal.getWidth() - text.length()) / 2);
  }

  private int centerX(int contentWidth) {
    return Math.max(0, (terminal.getWidth() - contentWidth) / 2);
  }

  private int topThirdY(int contentHeight) {
    return Math.max(0, (terminal.getHeight() - contentHeight) / 3);
  }

  public void renderStatusExample() throws IOException {
    terminal.clear();

    int centerY = terminal.getHeight() / 3;

    printCentered(centerY - 2, "Recipe Status Messages:", TextColor.ANSI.WHITE);
    printCentered(centerY, "✓ Recipe saved successfully!", TextColor.ANSI.GREEN);
    printCentered(centerY + 1, "⚠ Missing ingredient: Salt", TextColor.ANSI.YELLOW);
    printCentered(centerY + 2, "✗ Recipe not found!", TextColor.ANSI.RED);
    printCentered(centerY + 3, "ℹ 42 recipes loaded", TextColor.ANSI.BLUE);
    printCentered(centerY + 6, "Press Enter to continue...", TextColor.ANSI.WHITE);

    terminal.resetColors();
  }

  public void renderRecipeList() throws IOException {
    terminal.clear();

    int tableWidth = 45;
    int startX = centerX(tableWidth);
    int startY = topThirdY(10) - 2;

    terminal.printAt(startX, startY, "Your Recipes", TextColor.ANSI.CYAN);
    terminal.printAt(startX, startY + 1, "═".repeat(tableWidth), TextColor.ANSI.CYAN);

    terminal.printAt(startX, startY + 3, "Name", TextColor.ANSI.YELLOW);
    terminal.printAt(startX + 15, startY + 3, "Category", TextColor.ANSI.YELLOW);
    terminal.printAt(startX + 30, startY + 3, "Time", TextColor.ANSI.YELLOW);

    terminal.printAt(startX, startY + 4, "─".repeat(tableWidth), TextColor.ANSI.WHITE);

    terminal.printAt(startX, startY + 5, "Chocolate Cake", TextColor.ANSI.WHITE);
    terminal.printAt(startX + 15, startY + 5, "Dessert", TextColor.ANSI.GREEN);
    terminal.printAt(startX + 30, startY + 5, "45 min", TextColor.ANSI.BLUE);

    terminal.printAt(startX, startY + 6, "Pasta Carbonara", TextColor.ANSI.WHITE);
    terminal.printAt(startX + 15, startY + 6, "Main", TextColor.ANSI.GREEN);
    terminal.printAt(startX + 30, startY + 6, "20 min", TextColor.ANSI.BLUE);

    terminal.printAt(startX, startY + 7, "Caesar Salad", TextColor.ANSI.WHITE);
    terminal.printAt(startX + 15, startY + 7, "Appetizer", TextColor.ANSI.GREEN);
    terminal.printAt(startX + 30, startY + 7, "15 min", TextColor.ANSI.BLUE);

    printCentered(startY + 10, "Press Enter to continue...", TextColor.ANSI.YELLOW);
    terminal.resetColors();
  }

  public void renderMessage(String message, TextColor color) throws IOException {
    terminal.clear();
    printCentered(terminal.getHeight() / 3, message, color);
    terminal.resetColors();
  }

  private void printCentered(int y, String text, TextColor foreground) throws IOException {
    terminal.printAt(centerX(text), y, text, foreground);
  }
}
