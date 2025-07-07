package recipe;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Terminal {
    private final com.googlecode.lanterna.terminal.Terminal terminal;
    private TerminalSize terminalSize;
    private TerminalSize previousSize;
    private final AtomicBoolean sizeChanged = new AtomicBoolean(false);
    private Thread resizeWatcher;
    private volatile boolean watchingResize = false;
    
    private static final int MIN_TERMINAL_WIDTH = 98;
    private static final int MIN_TERMINAL_HEIGHT = 24;
    
    public Terminal() throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setInitialTerminalSize(new TerminalSize(98, 24));
        
        terminal = factory.createTerminal();
        terminal.enterPrivateMode();
        
        updateTerminalSize();
        previousSize = terminalSize;
        
        startResizeWatcher();
    }
    
    public void startResizeWatcher() {
        watchingResize = true;
        resizeWatcher = new Thread(() -> {
            while (watchingResize) {
                try {
                    TerminalSize currentSize = terminal.getTerminalSize();
                    if (currentSize != null && !currentSize.equals(terminalSize)) {
                        terminalSize = currentSize;
                        sizeChanged.set(true);
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (IOException e) {
                    
                }
            }
        });
        resizeWatcher.setDaemon(true);
        resizeWatcher.start();
    }
    
    public void stopResizeWatcher() {
        watchingResize = false;
        if (resizeWatcher != null) {
            resizeWatcher.interrupt();
        }
    }
    
    public boolean hasTerminalSizeChanged() {
        return sizeChanged.getAndSet(false);
    }
    
    public void updateTerminalSize() throws IOException {
        terminalSize = terminal.getTerminalSize();
    }
    
    public int getWidth() {
        return terminalSize != null ? terminalSize.getColumns() : 98;
    }
    
    public int getHeight() {
        return terminalSize != null ? terminalSize.getRows() : 24;
    }
    
    public boolean isTooSmall() {
        return getWidth() < MIN_TERMINAL_WIDTH || getHeight() < MIN_TERMINAL_HEIGHT;
    }
    
    public void clear() throws IOException {
        terminal.clearScreen();
        terminal.flush();
    }
    
    public void setCursorPosition(int x, int y) throws IOException {
        terminal.setCursorPosition(x, y);
    }
    
    public void setCursorVisible(boolean visible) throws IOException {
        terminal.setCursorVisible(visible);
    }
    
    public void printAt(int x, int y, String text) throws IOException {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            return;
        }
        
        if (x + text.length() > getWidth()) {
            text = text.substring(0, Math.max(0, getWidth() - x));
        }
        
        terminal.setCursorPosition(x, y);
        terminal.putString(text);
        terminal.flush();
    }
    
    public void printAt(int x, int y, String text, TextColor foreground) throws IOException {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            return;
        }
        
        if (x + text.length() > getWidth()) {
            text = text.substring(0, Math.max(0, getWidth() - x));
        }
        
        terminal.setCursorPosition(x, y);
        terminal.setForegroundColor(foreground);
        terminal.putString(text);
        terminal.flush();
    }
    
    public void printAt(int x, int y, String text, TextColor foreground, TextColor background) throws IOException {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            return;
        }
        
        if (x + text.length() > getWidth()) {
            text = text.substring(0, Math.max(0, getWidth() - x));
        }
        
        terminal.setCursorPosition(x, y);
        terminal.setForegroundColor(foreground);
        terminal.setBackgroundColor(background);
        terminal.putString(text);
        terminal.flush();
    }
    
    public void resetColors() throws IOException {
        terminal.resetColorAndSGR();
        terminal.flush();
    }
    
    public KeyStroke readInputWithTimeout() throws IOException {
        return terminal.pollInput();
    }
    
    public KeyStroke readInput() throws IOException {
        return terminal.readInput();
    }
    
    public void close() throws IOException {
        stopResizeWatcher();
        terminal.exitPrivateMode();
        terminal.close();
    }
}