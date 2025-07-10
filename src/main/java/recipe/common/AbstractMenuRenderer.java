package recipe.common;

import com.googlecode.lanterna.TextColor;
import java.io.IOException;

public abstract class AbstractMenuRenderer<T extends AbstractMenuItem> {
    protected final Terminal terminal;
    protected final AbstractMenuModel<T> model;

    // Layout constants - can be overridden by subclasses
    protected static final int MENU_ITEM_INCREMENT = 2;
    protected static final int MIN_TERMINAL_WIDTH = 63;
    protected static final int MIN_TERMINAL_HEIGHT = 27;

    // Border characters
    protected final String hbar = "═";
    protected final String vbar = "║";
    protected final String tippyTopLeftBar = "╔";
    protected final String topLeftBar = "╠";
    protected final String tippyTopRightBar = "╗";
    protected final String topRightBar = "╣";
    protected final String bottomLeftBar = "╚";
    protected final String bottomRightBar = "╝";

    // Layout variables
    protected boolean shortTerminal;
    protected boolean skinnyTerminal;
    protected int topPadding;
    protected int startY;
    protected int menuStartY;
    protected int menuBorderLX;
    protected int menuBorderRX;
    protected int iconX;
    protected int menuItemsX;
    protected int menuWidth;

    // Padding variables
    protected int tippyTopBorderAdj = 2;
    protected int borderIconPadding = 5;
    protected int iconMenuItemPadding = 3;
    protected int menuItemBorderPadding = 8;

    public AbstractMenuRenderer(Terminal terminal, AbstractMenuModel<T> model) {
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

    protected abstract void calculateLayout();
    protected abstract void drawTitle() throws IOException;

    protected boolean isTerminalSkinny() {
        skinnyTerminal = terminal.getWidth() < MIN_TERMINAL_WIDTH;
        return skinnyTerminal;
    }

    protected boolean isTerminalShort() {
        shortTerminal = terminal.getHeight() < MIN_TERMINAL_HEIGHT;
        return shortTerminal;
    }

    protected int getTopPadding(boolean shortTerminal) {
        return shortTerminal ? 2 : terminal.getHeight() / 5;
    }

    protected int centerX(String text) {
        return Math.max(0, (terminal.getWidth() - text.length()) / 2);
    }

    protected int centerX(int contentWidth) {
        return Math.max(0, (terminal.getWidth() - contentWidth) / 2);
    }

    protected void drawMenuItems() throws IOException {
        // Menu item is hard coded to 8 spaces to the right of the left menu border
        int menuItemPadding = 8;
        int menuItemsX = menuBorderLX + menuItemPadding;
        // Menu key is hard coded 7 spaces to the left of the right border
        int keyPadding = 7;
        int keyX = menuBorderRX - keyPadding;

        // Draw Menu Items
        for (int i = 0; i < model.getMenuItems().size(); i++) {
            T item = model.getMenuItems().get(i);
            int itemY = menuStartY + 1 + i * MENU_ITEM_INCREMENT;

            terminal.printAt(menuItemsX, itemY, item.getText(), item.getColor());
            terminal.printAt(iconX, itemY, item.getIcon(), item.getColor());
            terminal.printAt(keyX, itemY, String.valueOf(item.getKey()), item.getColor());
        }
    }

    protected void drawPrompt() throws IOException {
        int menuBorderBottomY = menuStartY + 1 + (model.getItemCount() * MENU_ITEM_INCREMENT);
        String[] prompt = model.getPrompt();
        int promptLength = prompt[0].length();
        // We need to use a double to adjust for potential odd menu-widths and
        // promptlengths.
        // Add 1 to adjust for the something- the 0.5 is so that we round up.
        double promptXrounder = menuBorderLX + 1.5 + (menuWidth / 2) - (promptLength / 2);
        int promptX = (int) promptXrounder;

        for (int i = 0; i < prompt.length; i++) {
            terminal.printAt(promptX, menuBorderBottomY + i, prompt[i], TextColor.ANSI.YELLOW);
        }
    }

    protected void drawDebugInfo() throws IOException {
        String sizeInfo = String.format("Terminal: %dx%d", terminal.getWidth(), terminal.getHeight());
        terminal.printAt(terminal.getWidth() - sizeInfo.length() - 1, terminal.getHeight() - 1,
            sizeInfo, TextColor.ANSI.WHITE);
    }

    public void positionCursor() throws IOException {
        int cursorY = menuStartY + 1 + model.getSelectedIndex() * MENU_ITEM_INCREMENT;
        terminal.setCursorPosition(iconX, cursorY);
    }

    protected void drawMenuBorder() throws IOException {
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

    protected void drawSkinnyTippyTopBorder() throws IOException {
        terminal.printAt(menuBorderLX, menuStartY - model.getSmallTitleLinesHeight() - tippyTopBorderAdj,
            tippyTopLeftBar + hbar.repeat(menuWidth - 1) + tippyTopRightBar, TextColor.ANSI.CYAN_BRIGHT);
    }

    protected void drawSkinnyTippyTopSideBorders() throws IOException {
        for (int i = 0; i <= model.getSmallTitleLinesHeight() + 1; i++) {
            terminal.printAt(menuBorderLX, startY + tippyTopBorderAdj - i, vbar, TextColor.ANSI.CYAN_BRIGHT);
            terminal.printAt(menuBorderRX, startY + tippyTopBorderAdj - i, vbar, TextColor.ANSI.CYAN_BRIGHT);
        }
    }

    protected void drawTippyTopBorder() throws IOException {
        terminal.printAt(menuBorderLX, startY + 1,
            tippyTopLeftBar + hbar.repeat(menuWidth - 1) + tippyTopRightBar,
            TextColor.ANSI.CYAN_BRIGHT);
    }

    protected void drawTippyTopSideBorders() throws IOException {
        String[] titleLines = model.getTitleLines(terminal.getWidth() < MIN_TERMINAL_WIDTH);
        for (int i = 0; i <= titleLines.length; i++) {
            terminal.printAt(menuBorderLX, startY + tippyTopBorderAdj + i, vbar, TextColor.ANSI.CYAN_BRIGHT);
            terminal.printAt(menuBorderRX, startY + tippyTopBorderAdj + i, vbar, TextColor.ANSI.CYAN_BRIGHT);
        }
    }

    protected void drawMenuTopBorder() throws IOException {
        terminal.printAt(menuBorderLX, menuStartY - 1,
            topLeftBar + hbar.repeat(menuWidth - 1) + topRightBar,
            TextColor.ANSI.CYAN_BRIGHT);
    }

    protected void drawMenuSideBorders() throws IOException {
        for (int i = 0; i <= model.getItemCount() * MENU_ITEM_INCREMENT; i++) {
            terminal.printAt(menuBorderLX, menuStartY + i, vbar, TextColor.ANSI.CYAN_BRIGHT);
            terminal.printAt(menuBorderRX, menuStartY + i, vbar, TextColor.ANSI.CYAN_BRIGHT);
        }
    }

    protected void drawMenuBottomBorder() throws IOException {
        int menuBorderBottomY = menuStartY + 1 + (model.getItemCount() * MENU_ITEM_INCREMENT);
        terminal.printAt(menuBorderLX, menuBorderBottomY,
            bottomLeftBar + hbar.repeat(menuWidth - 1) + bottomRightBar,
            TextColor.ANSI.CYAN_BRIGHT);
    }

    // Utility methods for subclasses
    protected void printCentered(int y, String text, TextColor foreground) throws IOException {
        terminal.printAt(centerX(text), y, text, foreground);
    }

    public void renderMessage(String message, TextColor color) throws IOException {
        terminal.clear();
        printCentered(terminal.getHeight() / 3, message, color);
        terminal.resetColors();
    }
}