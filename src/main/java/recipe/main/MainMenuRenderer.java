package recipe.main;

import recipe.common.AbstractMenuRenderer;
import recipe.common.Terminal;
import com.googlecode.lanterna.TextColor;
import java.io.IOException;

public class MainMenuRenderer extends AbstractMenuRenderer<MainMenuItem> {
    public MainMenuRenderer(Terminal terminal, MainMenuModel model) {
        super(terminal, model);
    }

    @Override
    protected void calculateLayout() {
        boolean skinnyTerminal = isTerminalSkinny();
        boolean shortTerminal = isTerminalShort();
        topPadding = getTopPadding(shortTerminal);
        startY = topPadding;
        String[] titleLines = model.getTitleLines(terminal.getWidth() < MIN_TERMINAL_WIDTH);
        menuStartY = startY + titleLines.length + 1;
        
        // Calculate max title line length
        int maxTitleLine = getTitleLinesMaxLength();
        
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

    @Override
    protected void drawTitle() throws IOException {
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

    private int topThirdY(int contentHeight) {
        return Math.max(0, (terminal.getHeight() - contentHeight) / 3);
    }
}