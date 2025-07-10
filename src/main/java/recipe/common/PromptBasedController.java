package recipe.common;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.TextColor;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

// A controller that handles conversational prompt-based user interaction
// Similar to Claude Code's interactive prompts
public class PromptBasedController {
  private final Terminal terminal;
  private final ScrollableTerminalRenderer renderer;
  private boolean shouldExit = false;

  public PromptBasedController(Terminal terminal, ScrollableTerminalRenderer renderer) {
    this.terminal = terminal;
    this.renderer = renderer;
  }

  // Prompts the user for text input with a given prompt message
  public String promptForText(String promptMessage) throws IOException {
    return promptForText(promptMessage, null);
  }

  // Prompts the user for text input with validation
  public String promptForText(String promptMessage, Predicate<String> validator) throws IOException {
    String input;

    do {
      // Use the renderer to print the prompt
      String fullPrompt = promptMessage + " ";
      renderer.print(fullPrompt, TextColor.ANSI.YELLOW);

      input = readLineInline(fullPrompt.length());

      if (input == null) {
        return null; // User cancelled
      }

      if (validator != null && !validator.test(input)) {
        renderer.printError("Invalid input. Please try again.");
        continue;
      }

      break;
    } while (true);

    return input;
  }

  // Prompts the user for a number input
  public Integer promptForNumber(String promptMessage) throws IOException {
    return promptForNumber(promptMessage, null, null);
  }

  // Prompts the user for a number input with min/max validation
  public Integer promptForNumber(String promptMessage, Integer min, Integer max) throws IOException {
    String input;

    do {
      String fullPrompt = promptMessage;
      if (min != null && max != null) {
        fullPrompt += " (" + min + "-" + max + ")";
      } else if (min != null) {
        fullPrompt += " (min: " + min + ")";
      } else if (max != null) {
        fullPrompt += " (max: " + max + ")";
      }
      fullPrompt += " ";

      renderer.print(fullPrompt, TextColor.ANSI.YELLOW);

      input = readLineInline(fullPrompt.length());

      if (input == null) {
        return null; // User cancelled
      }
      try {
        int number = Integer.parseInt(input.trim());
        if (min != null && number < min) {
          renderer.printError("Number must be at least " + min);
          continue;
        }
        if (max != null && number > max) {
          renderer.printError("Number must be at most " + max);
          continue;
        }
        return number;
      } catch (NumberFormatException e) {
        renderer.printError("Please enter a valid number.");
      }
    } while (true);
  }

  // Prompts the user for a decimal number input
  public Double promptForDouble(String promptMessage) throws IOException {
    return promptForDouble(promptMessage, null, null);
  }

  // Prompts the user for a decimal number input with min/max validation
  public Double promptForDouble(String promptMessage, Double min, Double max) throws IOException {
    String input;

    do {
      String fullPrompt = promptMessage;
      if (min != null && max != null) {
        fullPrompt += " (" + min + "-" + max + ")";
      } else if (min != null) {
        fullPrompt += " (min: " + min + ")";
      } else if (max != null) {
        fullPrompt += " (max: " + max + ")";
      }
      fullPrompt += " ";

      renderer.print(fullPrompt, TextColor.ANSI.YELLOW);

      input = readLineInline(fullPrompt.length());

      if (input == null) {
        return null; // User cancelled
      }

      try {
        double number = Double.parseDouble(input.trim());

        if (min != null && number < min) {
          renderer.printError("Number must be at least " + min);
          continue;
        }

        if (max != null && number > max) {
          renderer.printError("Number must be at most " + max);
          continue;
        }

        return number;

      } catch (NumberFormatException e) {
        renderer.printError("Please enter a valid number.");
      }
    } while (true);
  }

  // Prompts the user for a yes/no confirmation
  public boolean promptForConfirmation(String promptMessage) throws IOException {
    return promptForConfirmation(promptMessage, false);
  }

  // Prompts the user for a yes/no confirmation with default value
  public boolean promptForConfirmation(String promptMessage, boolean defaultValue) throws IOException {
    String input;
    String defaultText = defaultValue ? "Y/n" : "y/N";

    do {
      // Split the prompt to color the [Y/n] part differently
      renderer.print(promptMessage + " ", TextColor.ANSI.YELLOW);
      renderer.print("[" + defaultText + "]", TextColor.ANSI.MAGENTA);
      renderer.print(": ", TextColor.ANSI.WHITE);

      input = readLineInline(promptMessage.length() + defaultText.length() + 4); // " [Y/n]: "

      if (input == null) {
        return false; // User cancelled
      }

      input = input.trim().toLowerCase();

      if (input.isEmpty()) {
        return defaultValue;
      }

      if (input.equals("y") || input.equals("yes")) {
        return true;
      } else if (input.equals("n") || input.equals("no")) {
        return false;
      } else {
        renderer.printError("Please enter y/yes or n/no.");
      }
    } while (true);
  }

  // Prompts the user to select from a list of options
  public String promptForChoice(String promptMessage, List<String> options) throws IOException {
    renderer.println(promptMessage);

    for (int i = 0; i < options.size(); i++) {
      renderer.printNumberedItem(i + 1, options.get(i));
    }

    Integer choice = promptForNumber("Enter your choice", 1, options.size());

    if (choice == null) {
      return null; // User cancelled
    }

    return options.get(choice - 1);
  }

  // Prompts the user for multi-line text input
  public List<String> promptForMultiLineText(String promptMessage) throws IOException {
    renderer.println(promptMessage, TextColor.ANSI.YELLOW);
    renderer.print("Press Enter twice when finished, or type ", TextColor.ANSI.CYAN);
    renderer.print("'DONE'", TextColor.ANSI.GREEN);
    renderer.println(" on a line by itself", TextColor.ANSI.CYAN);

    List<String> lines = new java.util.ArrayList<>();
    while (true) {
      String line = readLineInline(0);
      if (line == null || line.equalsIgnoreCase("DONE")) {
        break;
      }
      if (line.isEmpty() && !lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
        lines.remove(lines.size() - 1);
        break;
      }
      lines.add(line);
    }
    return lines;
  }

  // Waits for the user to press Enter to continue
  public void waitForEnter() throws IOException {
    waitForEnter("Press Enter to continue...");
  }

  // Waits for the user to press Enter with custom message
  public void waitForEnter(String message) throws IOException {
    renderer.print(message + " ", TextColor.ANSI.YELLOW);

    KeyStroke keyStroke = terminal.readInput();
    
    if (keyStroke.getKeyType() == KeyType.Character) {
      char ch = Character.toLowerCase(keyStroke.getCharacter());
      if (ch == 'q') {
        System.out.println("q"); // Echo the character
      } else {
        System.out.println(ch); // Echo other characters
      }
    } else if (keyStroke.getKeyType() == KeyType.Escape) {
      System.out.println("ESC"); // Echo escape
    } else if (keyStroke.getKeyType() == KeyType.Enter) {
      System.out.println(""); // Just add newline for Enter
    } else {
      System.out.println(""); // Just add newline for other keys
    }
  }

  // Reads a line of input from the terminal at a specific position
  private String readLineAtPosition(int startX, int y) throws IOException {
    StringBuilder input = new StringBuilder();
    int currentX = startX;

    KeyStroke key;
    do {
      key = terminal.readInput();

      if (key.getKeyType() == KeyType.Escape) {
        return null; // User cancelled
      } else if (key.getKeyType() == KeyType.Enter) {
        break;
      } else if (key.getKeyType() == KeyType.Backspace) {
        if (input.length() > 0) {
          input.deleteCharAt(input.length() - 1);
          currentX--;
          // Clear the character on screen
          terminal.printAt(currentX, y, " ", TextColor.ANSI.DEFAULT);
          terminal.setCursorPosition(currentX, y);
        }
      } else if (key.getKeyType() == KeyType.Character) {
        char ch = key.getCharacter();
        if (ch >= 32 && ch <= 126) { // Printable characters
          input.append(ch);
          terminal.printAt(currentX, y, String.valueOf(ch), TextColor.ANSI.DEFAULT);
          currentX++;
        }
      }
    } while (true);

    return input.toString();
  }

  // Reads a line of input from the terminal (legacy method)
  private String readLine() throws IOException {
    return readLineAtPosition(1, terminal.getHeight() - 1);
  }

  // Reads a line of input using Lanterna but with natural display
  private String readLineInline(int promptLength) throws IOException {
    StringBuilder input = new StringBuilder();

    KeyStroke key;
    do {
      key = terminal.readInput();

      if (key.getKeyType() == KeyType.Escape) {
        return null; // User cancelled
      } else if (key.getKeyType() == KeyType.Enter) {
        break;
      } else if (key.getKeyType() == KeyType.Backspace) {
        if (input.length() > 0) {
          input.deleteCharAt(input.length() - 1);
          // Echo backspace to terminal
          System.out.print("\b \b");
          System.out.flush();
        }
      } else if (key.getKeyType() == KeyType.Character) {
        char ch = key.getCharacter();
        if (ch >= 32 && ch <= 126) { // Printable characters
          input.append(ch);
          // Echo character to terminal
          System.out.print(ch);
          System.out.flush();
        }
      }
    } while (true);

    // Print newline after input is complete and advance renderer line tracking
    System.out.println();
    renderer.advanceLineWithScroll();

    return input.toString();
  }

  /**
   * Checks if the user wants to exit
   */
  public boolean shouldExit() {
    return shouldExit;
  }

  /**
   * Sets the exit flag
   */
  public void setShouldExit(boolean shouldExit) {
    this.shouldExit = shouldExit;
  }
}
