package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class SkipCommand extends BaseCommand {
    public static final String COMMAND_NAME = "skip";

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
                if (refWindowController.refClient.clientThread.getCurrentTurnId() == refWindowController.refClient.clientThread.getThreadId()) {
                    refWindowController.refClient.clientThread.writer.writeInt(9);

                    refWindowController.refClient.clientThread.writer.writeInt(7);
                    refWindowController.refClient.clientThread.writer.writeUTF(refWindowController.refClient.clientThread.getCurrentTurn() + " ha pasado el turno");

                    writeSuccess(out, "Has pasado de turno");

                } else {
                    writeError(out, "No es su turno");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
