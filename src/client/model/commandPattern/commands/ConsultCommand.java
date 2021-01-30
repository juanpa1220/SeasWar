package client.model.commandPattern.commands;

import client.control.BoardController;
import client.control.ClientWindowController;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ConsultCommand extends BaseCommand {
    public static final String COMMAND_NAME = "consultar";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) {
        if (args == null || args.length < 2) {
            writeError(out, COMMAND_NAME + ": Número de argumentos inválidos");
        } else {
            AtomicInteger x = new AtomicInteger(0);
            AtomicInteger y = new AtomicInteger(0);
            AtomicReference<String> name = new AtomicReference<>("");
            for (int i = 0; i < args.length; i += 2) {
                switch (args[i].toLowerCase()) {
                    case "-x":
                        try {
                            int temX = Integer.parseInt(args[i + 1]);
                            if (temX > 0 && temX <= 30) {
                                x.set(temX);
                            } else {
                                writeError(out, COMMAND_NAME + ": El valor del argumento -x debe ser un número entero entre 1 y 30");
                            }
                        } catch (NumberFormatException e) {
                            writeError(out, COMMAND_NAME + ": El valor del argumento -x debe ser un número entero entre 1 y 30");
                        }
                        break;
                    case "-y":
                        try {
                            int temY = Integer.parseInt(args[i + 1]);
                            if (temY > 0 && temY <= 20) {
                                y.set(temY);
                            } else {
                                writeError(out, COMMAND_NAME + ": El valor del argumento -y debe ser un número entero entre 1 y 20");
                            }
                        } catch (NumberFormatException e) {
                            writeError(out, COMMAND_NAME + ": El valor del argumento -y debe ser un número entero entre 1 y 20");
                        }
                        break;
                    case "-n":
                        try {
                            name.set(args[i + 1]);
                        } catch (Exception exception) {
                            writeError(out, COMMAND_NAME + ": Argumentos inválidos");
                        }
                        break;
                    default:
                        writeError(out, COMMAND_NAME + ": Argumentos inválidos");
                }
            }
            if (x.get() != 0 && y.get() != 0 && name.get().equals("")) {
                String report = BoardController.boardController.consultBoard(x.get() - 1, y.get() - 1);
                writeSuccess(out, report);
            } else if (x.get() == 0 && y.get() == 0 && !name.get().equals("")) {
                try {
                    refWindowController.refClient.clientThread.writer.writeInt(13);
                    refWindowController.refClient.clientThread.writer.writeUTF(name.get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                writeError(out, COMMAND_NAME + ": Argumentos inválidos");
            }
        }
    }
}
