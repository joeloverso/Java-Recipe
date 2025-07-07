package recipe;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.TextColor;
import java.io.IOException;

public class MenuController {
    private final Terminal terminal;
    private final MenuModel model;
    private final MenuRenderer renderer;
    
    public MenuController(Terminal terminal, MenuModel model, MenuRenderer renderer) {
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
    
    private void processKeyStroke(KeyStroke key) throws IOException {
        KeyType keyType = key.getKeyType();
        Character character = key.getCharacter();
        
        if (keyType == KeyType.Escape || 
            (keyType == KeyType.Character && key.isCtrlDown() && character != null && character == 'c')) {
            System.exit(0);
        }
        
        if (keyType == KeyType.Character && character != null) {
            char ch = Character.toLowerCase(character);
            
            if (model.isValidKey(ch)) {
                executeAction(ch);
                return;
            }
            
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
        
        if (keyType == KeyType.Enter) {
            char selectedKey = model.getSelectedKey();
            executeAction(selectedKey);
            return;
        }
    }
    
    private void executeAction(char choice) throws IOException {
        switch (choice) {
            case 'c':
                renderer.renderStatusExample();
                waitForInput();
                break;
            case 'v':
                renderer.renderRecipeList();
                waitForInput();
                break;
            case 'n':
                renderer.renderMessage("NEW RECIPE Feature Coming Soon!", TextColor.ANSI.CYAN);
                waitForInput();
                break;
            case 'e':
                renderer.renderMessage("EDIT Recipe Feature Coming Soon!", TextColor.ANSI.CYAN);
                waitForInput();
                break;
            case 'd':
                renderer.renderMessage("DELETE RECIPE Feature Coming Soon!", TextColor.ANSI.CYAN);
                waitForInput();
                break;
            case 'q':
                renderer.renderMessage("Thanks for using Recipe Calculator!", TextColor.ANSI.GREEN);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.exit(0);
                break;
        }
    }
    
    private void waitForInput() throws IOException {
        KeyStroke keyStroke;
        do {
            keyStroke = terminal.readInput();
        } while (keyStroke.getKeyType() != KeyType.Enter && 
                 keyStroke.getKeyType() != KeyType.Escape);
    }
}