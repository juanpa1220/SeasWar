package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import client.model.Client;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ConnectCommand extends BaseCommand {
    public static final String COMMAND_NAME = "connect";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) {
        if (args == null || args.length < 6) {
            writeError(out, COMMAND_NAME + ": Número de argumentos inválidos");
        } else {
            AtomicInteger port = new AtomicInteger(0);
            AtomicReference<String> server = new AtomicReference<>("");
            AtomicReference<String> name = new AtomicReference<>("");
            for (int i = 0; i < 6; i += 2) {
                switch (args[i].toLowerCase()) {
                    case "-s":
                        server.set(args[i + 1]);
                        break;
                    case "-p":
                        try {
                            port.set(Integer.parseInt(args[i + 1]));
                        } catch (NumberFormatException e) {
                            port.set(0);
                        }
                        break;
                    case "-n":
                        name.set(args[i + 1]);
                        break;
                    default:
                        writeError(out, COMMAND_NAME + ": Argumentos inválidos");
                }
            }
            if (port.get() != 0 && !server.get().equals("") && !name.get().equals("")) {
                if (!refWindowController.isHasConnected()) {
                    Client client = new Client(refWindowController);
                    refWindowController.refClient.setName(name.get());
                    boolean connected = client.connect(server.get(), port.get());
                    if (connected) {
                        try {
                            refWindowController.refClient.clientThread.writer.writeInt(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        writeError(out, "Conección denegada");
                    }
                } else {
                    writeError(out, "Ya se ha conectado");
                }
            } else {
                writeError(out, COMMAND_NAME + ": Argumentos inválidos");
            }
        }
    }
}