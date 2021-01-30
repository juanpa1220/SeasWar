package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import javafx.scene.text.TextFlow;

public class ShowUnavailableCommand extends BaseCommand {
    public static final String COMMAND_NAME = "ocupados";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) {
        if (args == null || args.length != 0) {
            writeError(out, COMMAND_NAME + ": Número de argumentos inválidos");
        } else {
            refWindowController.showUnavailable();
        }
    }
}
