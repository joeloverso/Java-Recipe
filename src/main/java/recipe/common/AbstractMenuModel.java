package recipe.common;

import java.util.List;

public abstract class AbstractMenuModel<T extends AbstractMenuItem> {
    protected final List<T> menuItems;
    protected final String[] titleLines;
    protected final String[] smallTitleLines;
    protected final String[] prompt;
    protected int selectedIndex = 0;

    public AbstractMenuModel(List<T> menuItems, String[] titleLines, String[] smallTitleLines, String[] prompt) {
        this.menuItems = menuItems;
        this.titleLines = titleLines;
        this.smallTitleLines = smallTitleLines;
        this.prompt = prompt;
    }

    public List<T> getMenuItems() {
        return menuItems;
    }

    public String[] getTitleLines(boolean smallTerminal) {
        return smallTerminal ? smallTitleLines.clone() : titleLines.clone();
    }

    public int getSmallTitleLinesHeight() {
        return smallTitleLines.length;
    }

    public String[] getPrompt() {
        return prompt.clone();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < menuItems.size()) {
            selectedIndex = index;
        }
    }

    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + menuItems.size()) % menuItems.size();
    }

    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % menuItems.size();
    }

    public char getSelectedKey() {
        return menuItems.get(selectedIndex).getKey();
    }

    public int getItemCount() {
        return menuItems.size();
    }

    public int getItemMaxLength() {
        return menuItems.stream()
            .mapToInt(item -> item.getText().length())
            .max()
            .orElse(0);
    }

    public int getKeyIndex(char key) {
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getKey() == key) {
                return i;
            }
        }
        return -1;
    }

    public boolean isValidKey(char key) {
        return getKeyIndex(key) != -1;
    }

    // Backward compatibility methods
    public String[] getItems() {
        return menuItems.stream().map(AbstractMenuItem::getText).toArray(String[]::new);
    }

    public char[] getItemKeys() {
        char[] keys = new char[menuItems.size()];
        for (int i = 0; i < menuItems.size(); i++) {
            keys[i] = menuItems.get(i).getKey();
        }
        return keys;
    }

    public String[] getIcons() {
        return menuItems.stream().map(AbstractMenuItem::getIcon).toArray(String[]::new);
    }

    public com.googlecode.lanterna.TextColor.ANSI[] getColors() {
        return menuItems.stream().map(AbstractMenuItem::getColor).toArray(com.googlecode.lanterna.TextColor.ANSI[]::new);
    }
}