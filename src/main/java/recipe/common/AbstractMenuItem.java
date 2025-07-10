package recipe.common;

import com.googlecode.lanterna.TextColor;

public abstract class AbstractMenuItem {
    protected final String text;
    protected final char key;
    protected final String icon;
    protected final TextColor.ANSI color;

    public AbstractMenuItem(String text, char key, String icon, TextColor.ANSI color) {
        this.text = text;
        this.key = key;
        this.icon = icon;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public char getKey() {
        return key;
    }

    public String getIcon() {
        return icon;
    }

    public TextColor.ANSI getColor() {
        return color;
    }

    @Override
    public String toString() {
        return String.format("%s{text='%s', key='%c', icon='%s', color=%s}", 
                           getClass().getSimpleName(), text, key, icon, color);
    }
}