package client.model.commandPattern;

import client.control.ClientWindowController;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public interface ICommand {
    String getCommandName();

    void execute(String[] args, TextFlow commandOutput, ClientWindowController refWindowController) throws IOException;
}
