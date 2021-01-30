package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class InitCommand extends BaseCommand {
    public static final String COMMAND_NAME = "init";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) {
        if (args == null || args.length != 0) {
            writeError(out, COMMAND_NAME + ": Número de argumentos inválidos");
        } else {
            try {
                if (!refWindowController.isHasInit()) {
                    refWindowController.refClient.clientThread.writer.writeInt(5);
                } else {
                    writeError(out, "Ya el juego ha empezado");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
