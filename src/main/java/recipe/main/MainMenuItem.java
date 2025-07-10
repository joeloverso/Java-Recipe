package recipe.main;

import recipe.common.AbstractMenuItem;
import com.googlecode.lanterna.TextColor;

public class MainMenuItem extends AbstractMenuItem {
    public MainMenuItem(String text, char key, String icon, TextColor.ANSI color) {
        super(text, key, icon, color);
    }
}