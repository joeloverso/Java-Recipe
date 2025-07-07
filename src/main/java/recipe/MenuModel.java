package recipe;

import com.googlecode.lanterna.TextColor;

public class MenuModel {
  private final String[] items;
  private final char[] itemKeys;
  private final String[] icons;
  private final TextColor.ANSI[] colors;
  private final String[] titleLines;
  private final String[] smallTitleLines;

  private int selectedIndex = 0;

  public MenuModel() {
    items = new String[] {
        "Calculate recipe", "View existing recipes", "New recipe",
        "Edit recipe", "Delete recipe", "Quit"
    };

    itemKeys = new char[] { 'c', 'v', 'n', 'e', 'd', 'q' };

    icons = new String[] { "∑", "☰", "✚", "✎", "✖", "⏻" };

    colors = new TextColor.ANSI[] {
        TextColor.ANSI.YELLOW,
        TextColor.ANSI.BLUE,
        TextColor.ANSI.GREEN,
        TextColor.ANSI.CYAN,
        TextColor.ANSI.RED,
        TextColor.ANSI.MAGENTA
    };

    titleLines = new String[] {
        "██████╗ ███████╗ ██████╗██╗██████╗ ███████╗       ██████╗ █████╗ ██╗",
        "██╔══██╗██╔════╝██╔════╝██║██╔══██╗██╔════╝      ██╔════╝██╔══██╗██║",
        "██████╔╝█████╗  ██║     ██║██████╔╝█████╗  █████╗██║     ███████║██║",
        "██╔══██╗██╔══╝  ██║     ██║██╔═══╝ ██╔══╝  ╚════╝██║     ██╔══██║██║",
        "██║  ██║███████╗╚██████╗██║██║     ███████╗      ╚██████╗██║  ██║███████╗",
        "╚═╝  ╚═╝╚══════╝ ╚═════╝╚═╝╚═╝     ╚══════╝       ╚═════╝╚═╝  ╚═╝╚══════╝"
    };

    smallTitleLines = new String[] {
        "RECIPE CALCULATOR",
    };
  }

  public String[] getItems() {
    return items;
  }

  public char[] getItemKeys() {
    return itemKeys;
  }

  public String[] getIcons() {
    return icons;
  }

  public TextColor.ANSI[] getColors() {
    return colors;
  }

  public String[] getTitleLines(boolean smallTerminal) {
    return smallTerminal ? smallTitleLines : titleLines;
  }

  public int getSmallTitleLinesHeight() {
    int smallTitleLinesHeight = smallTitleLines.length;
    return smallTitleLinesHeight;

  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  public void setSelectedIndex(int index) {
    if (index >= 0 && index < items.length) {
      selectedIndex = index;
    }
  }

  public void moveUp() {
    selectedIndex = (selectedIndex - 1 + items.length) % items.length;
  }

  public void moveDown() {
    selectedIndex = (selectedIndex + 1) % items.length;
  }

  public char getSelectedKey() {
    return itemKeys[selectedIndex];
  }

  public int getItemCount() {
    return items.length;
  }

  public int getItemMaxLength() {
    int itemsMaxLength = 0;
    for (String item : items) {
      if (item.length() > itemsMaxLength) {
        itemsMaxLength = item.length();
      }
    }
    return itemsMaxLength;
  }

  public int getKeyIndex(char key) {
    for (int i = 0; i < itemKeys.length; i++) {
      if (itemKeys[i] == key) {
        return i;
      }
    }
    return -1;
  }

  public boolean isValidKey(char key) {
    return getKeyIndex(key) != -1;
  }
}
