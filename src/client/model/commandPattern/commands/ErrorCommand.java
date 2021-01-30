package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import javafx.scene.text.TextFlow;

public class ErrorCommand extends BaseCommand {
    private static final String COMMAND_NAME = "ERROR";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) {
        String message = "Error al invocar el comando";
        writeError(out, message);
    }
}
