package recipe.main;

import recipe.common.AbstractMenuModel;
import com.googlecode.lanterna.TextColor;
import java.util.Arrays;

public class MainMenuModel extends AbstractMenuModel<MainMenuItem> {
  public MainMenuModel() {
    super(
        Arrays.asList(
            new MainMenuItem("Calculate recipe", 'c', "∑", TextColor.ANSI.YELLOW),
            new MainMenuItem("View existing recipes", 'v', "◯ ", TextColor.ANSI.BLUE),
            new MainMenuItem("New recipe", 'n', "✚", TextColor.ANSI.GREEN),
            new MainMenuItem("Edit recipe", 'e', "✎", TextColor.ANSI.CYAN),
            new MainMenuItem("Delete recipe", 'd', "✖", TextColor.ANSI.RED),
            new MainMenuItem("Quit", 'q', "⏻", TextColor.ANSI.MAGENTA)),
        new String[] {
            "██████╗ ███████╗ ██████╗██╗██████╗ ███████╗       ██████╗ █████╗ ██╗",
            "██╔══██╗██╔════╝██╔════╝██║██╔══██╗██╔════╝      ██╔════╝██╔══██╗██║",
            "██████╔╝█████╗  ██║     ██║██████╔╝█████╗  █████╗██║     ███████║██║",
            "██╔══██╗██╔══╝  ██║     ██║██╔═══╝ ██╔══╝  ╚════╝██║     ██╔══██║██║",
            "██║  ██║███████╗╚██████╗██║██║     ███████╗      ╚██████╗██║  ██║███████╗",
            "╚═╝  ╚═╝╚══════╝ ╚═════╝╚═╝╚═╝     ╚══════╝       ╚═════╝╚═╝  ╚═╝╚══════╝"
        },
        new String[] {
            "RECIPE CALCULATOR"
        },
        new String[] {
            " Press a key for your choice: ",
            " OR Press ↑ ↓ to navigate",
            " Press ENTER to select"
        });
  }
}
