package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import javafx.scene.text.TextFlow;

public class NotFoundCommand extends BaseCommand {
    private static final String COMMAND_NAME = "NOT FOUND";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) {
        writeError(out, "Comando no encontrado");
    }
}