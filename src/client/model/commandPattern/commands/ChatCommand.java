package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class ChatCommand extends BaseCommand {
    public static final String COMMAND_NAME = "chat";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) {
        if (args == null || !(args.length == 2 || args.length == 4)) {
            writeError(out, COMMAND_NAME + ": Número de argumentos inválidos");
        } else {
            AtomicReference<String> privateTo = new AtomicReference<>("");
            AtomicReference<String> message = new AtomicReference<>("");
            try {
                for (int i = 0; i < args.length; i += 2) {
                    switch (args[i].toLowerCase()) {
                        case "-p":
                            privateTo.set(args[i + 1]);
                            break;
                        case "-m":
                            message.set(args[i + 1]);
                            break;
                        default:
                            writeError(out, COMMAND_NAME + ": Argumentos inválidos");
                    }
                }
            } catch (Exception exception) {
                writeError(out, COMMAND_NAME + ": Argumentos inválidos");
            }

            if (!message.get().equals("") && privateTo.get().equals("")) {
                try {
                    refWindowController.refClient.clientThread.writer.writeInt(2);
                    refWindowController.refClient.clientThread.writer.writeUTF(message.get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!message.get().equals("") && !privateTo.get().equals("")) {
                try {
                    refWindowController.refClient.clientThread.writer.writeInt(3);
                    refWindowController.refClient.clientThread.writer.writeUTF(privateTo.get());
                    refWindowController.refClient.clientThread.writer.writeUTF(message.get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                writeError(out, COMMAND_NAME + ": Argumentos inválidos");
            }
        }
    }
}
