package recipe.common;

import com.googlecode.lanterna.TextColor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A renderer that provides scrollable terminal output with line-by-line display
 * Similar to Claude Code's conversational interface
 */
public class ScrollableTerminalRenderer {
  private final Terminal terminal;
  private final List<String> outputBuffer;
  private final List<TextColor> colorBuffer;
  private int currentLine;
  private int maxLines;

  public ScrollableTerminalRenderer(Terminal terminal) {
    this.terminal = terminal;
    this.outputBuffer = new ArrayList<>();
    this.colorBuffer = new ArrayList<>();
    this.currentLine = 0;
    this.maxLines = terminal.getHeight() - 2; // Leave space for input prompt
  }

  // Adds a line of text to the output buffer and displays it
  public void println(String text) throws IOException {
    println(text, TextColor.ANSI.DEFAULT);
  }

  // Adds a colored line of text to the output buffer and displays it
  public void println(String text, TextColor color) throws IOException {
    if (text == null) {
      text = "";
    }

    // Use System.out for natural terminal scrolling behavior
    String colorCode = getAnsiColorCode(color);
    System.out.println("\033[" + colorCode + "m" + text + "\033[0m");
    System.out.flush();
  }

  // Adds a blank line
  public void printBlankLine() throws IOException {
    println("");
  }

  // Prints text on the same line without advancing (for prompts)
  public void print(String text) throws IOException {
    print(text, TextColor.ANSI.DEFAULT);
  }

  // Prints colored text on the same line without advancing
  public void print(String text, TextColor color) throws IOException {
    if (text == null) {
      text = "";
    }

    // Use System.out for natural terminal behavior
    String colorCode = getAnsiColorCode(color);
    System.out.print("\033[" + colorCode + "m" + text + "\033[0m");
    System.out.flush();
  }

  // Prints a header with a distinctive style
  public void printHeader(String text) throws IOException {
    println("");
    print("═".repeat(Math.min(text.length() + 4, terminal.getWidth() - 2)), TextColor.ANSI.CYAN);
    print("  " + text + " ", TextColor.ANSI.CYAN_BRIGHT);
    print("═".repeat(Math.min(text.length() + 4, terminal.getWidth() - 2)), TextColor.ANSI.CYAN);
    println("");
  }

  // Prints a sub-header
  public void printSubHeader(String text) throws IOException {
    println("");
    println("── " + text + " ──", TextColor.ANSI.CYAN);
    println("");
  }

  // Prints a success message
  public void printSuccess(String text) throws IOException {
    println("✓ " + text, TextColor.ANSI.GREEN);
  }

  // Prints an error message
  public void printError(String text) throws IOException {
    println("✗ " + text, TextColor.ANSI.RED);
  }

  // Prints a warning message
  public void printWarning(String text) throws IOException {
    println("⚠ " + text, TextColor.ANSI.YELLOW);
  }

  // Prints an info message
  public void printInfo(String text) throws IOException {
    println("ℹ " + text, TextColor.ANSI.BLUE);
  }

  // Prints a bulleted list item
  public void printListItem(String text) throws IOException {
    println("  • " + text, TextColor.ANSI.DEFAULT);
  }

  // Prints a numbered list item
  public void printNumberedItem(int number, String text) throws IOException {
    println("  " + number + ". " + text, TextColor.ANSI.DEFAULT);
  }

  // Clears the terminal and resets the output buffer
  public void clear() throws IOException {
    terminal.clear();
    outputBuffer.clear();
    colorBuffer.clear();
    currentLine = 0;
    updateMaxLines();
  }

  // Scrolls the display up by one line
  private void scrollUp() throws IOException {
    // Shift all lines up by one
    for (int i = 1; i < maxLines; i++) {
      int bufferIndex = outputBuffer.size() - maxLines + i;
      if (bufferIndex >= 0 && bufferIndex < outputBuffer.size()) {
        String line = outputBuffer.get(bufferIndex);
        TextColor color = colorBuffer.get(bufferIndex);
        terminal.printAt(1, i, line, color);
      }
    }

    // Clear the bottom line
    terminal.printAt(1, maxLines, " ".repeat(terminal.getWidth() - 2), TextColor.ANSI.DEFAULT);
    currentLine = maxLines - 1;
  }

  // Wraps text to fit within the terminal width
  private List<String> wrapText(String text, int maxWidth) {
    List<String> lines = new ArrayList<>();

    if (text.length() <= maxWidth) {
      lines.add(text);
      return lines;
    }

    String[] words = text.split(" ");
    StringBuilder currentLine = new StringBuilder();

    for (String word : words) {
      if (currentLine.length() + word.length() + 1 <= maxWidth) {
        if (currentLine.length() > 0) {
          currentLine.append(" ");
        }
        currentLine.append(word);
      } else {
        if (currentLine.length() > 0) {
          lines.add(currentLine.toString());
          currentLine = new StringBuilder(word);
        } else {
          // Word is too long, truncate it
          lines.add(word.substring(0, maxWidth));
          if (word.length() > maxWidth) {
            lines.addAll(wrapText(word.substring(maxWidth), maxWidth));
          }
        }
      }
    }

    if (currentLine.length() > 0) {
      lines.add(currentLine.toString());
    }

    return lines;
  }

  // Updates max lines based on current terminal size
  private void updateMaxLines() {
    this.maxLines = terminal.getHeight() - 2; // Leave space for input prompt
  }

  // Positions cursor for input prompt at the bottom of the terminal
  public void positionForInput() throws IOException {
    terminal.setCursorPosition(1, terminal.getHeight() - 1);
  }

  // Advances to the next line (used after getting input)
  public void advanceLine() {
    currentLine++;
    if (currentLine >= maxLines) {
      currentLine = maxLines - 1;
    }
  }

  // Advances to the next line and ensures proper scrolling
  public void advanceLineWithScroll() throws IOException {
    // No-op for natural scrolling approach
    // The terminal handles scrolling naturally with System.out
  }

  // Gets the current line number
  public int getCurrentLine() {
    return 0; // Not tracked in natural scrolling mode
  }

  // Gets the total number of lines in the output buffer
  public int getTotalLines() {
    return outputBuffer.size();
  }

  // Converts Lanterna TextColor to ANSI escape code
  private String getAnsiColorCode(TextColor color) {
    if (color == TextColor.ANSI.RED)
      return "31";
    if (color == TextColor.ANSI.GREEN)
      return "32";
    if (color == TextColor.ANSI.YELLOW)
      return "33";
    if (color == TextColor.ANSI.BLUE)
      return "34";
    if (color == TextColor.ANSI.MAGENTA)
      return "35";
    if (color == TextColor.ANSI.CYAN)
      return "36";
    if (color == TextColor.ANSI.WHITE)
      return "37";
    if (color == TextColor.ANSI.CYAN_BRIGHT)
      return "96";
    return "0"; // Default
  }
}
