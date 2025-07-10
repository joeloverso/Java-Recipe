package recipe.common;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.io.IOException;

public abstract class AbstractMenuController<T extends AbstractMenuItem> {
    protected final Terminal terminal;
    protected final AbstractMenuModel<T> model;
    protected final AbstractMenuRenderer<T> renderer;
    protected boolean shouldExit = false;

    public AbstractMenuController(Terminal terminal, AbstractMenuModel<T> model, AbstractMenuRenderer<T> renderer) {
        this.terminal = terminal;
        this.model = model;
        this.renderer = renderer;
    }

    public void handleInputWithTimeout() throws IOException {
        KeyStroke key = terminal.readInputWithTimeout();
        if (key == null) {
            return;
        }
        processKeyStroke(key);
    }

    public void handleInput() throws IOException {
        KeyStroke key = terminal.readInput();
        processKeyStroke(key);
    }

    protected void processKeyStroke(KeyStroke key) throws IOException {
        KeyType keyType = key.getKeyType();
        Character character = key.getCharacter();

        // Handle exit keys
        if (keyType == KeyType.Escape ||
            (keyType == KeyType.Character && key.isCtrlDown() && character != null && character == 'c')) {
            shouldExit = true;
            return;
        }

        // Handle character keys
        if (keyType == KeyType.Character && character != null) {
            char ch = Character.toLowerCase(character);

            if (model.isValidKey(ch)) {
                executeAction(ch);
                return;
            }

            // Vim-style navigation
            if (ch == 'j') {
                model.moveDown();
                renderer.positionCursor();
                return;
            }

            if (ch == 'k') {
                model.moveUp();
                renderer.positionCursor();
                return;
            }
        }

        // Handle arrow keys
        if (keyType == KeyType.ArrowDown) {
            model.moveDown();
            renderer.positionCursor();
            return;
        }

        if (keyType == KeyType.ArrowUp) {
            model.moveUp();
            renderer.positionCursor();
            return;
        }

        // Handle enter key
        if (keyType == KeyType.Enter) {
            char selectedKey = model.getSelectedKey();
            executeAction(selectedKey);
            return;
        }
    }

    protected abstract void executeAction(char choice) throws IOException;

    public boolean shouldExit() {
        return shouldExit;
    }

    public void setShouldExit(boolean shouldExit) {
        this.shouldExit = shouldExit;
    }

    protected void waitForInput() throws IOException {
        KeyStroke keyStroke = terminal.readInput();
        
        if (keyStroke.getKeyType() == KeyType.Character) {
            char ch = Character.toLowerCase(keyStroke.getCharacter());
            if (ch == 'q') {
                System.out.println("q"); // Echo the character
                return;
            } else {
                System.out.println(ch); // Echo other characters
                return;
            }
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            System.out.println("ESC"); // Echo escape
            return;
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            System.out.println(""); // Just add newline for Enter
            return;
        } else {
            System.out.println(""); // Just add newline for other keys
            return;
        }
    }
}