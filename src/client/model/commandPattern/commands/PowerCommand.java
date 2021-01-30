package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import client.model.game.Warrior;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class PowerCommand extends BaseCommand {
    public static final String COMMAND_NAME = "poder";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) {
        if (args == null || args.length < 2) {
            writeError(out, COMMAND_NAME + ": Número de argumentos inválidos");
        } else {
            if (args[0].equals("-p")) {
                if (refWindowController.refClient.clientThread.getThreadId() == refWindowController.refClient.clientThread.getCurrentTurnId()) {
                    String power = args[1];
                    switch (power) {
                        case "sanidad":
                            refWindowController.getWarriors().forEach(Warrior::cure);
                            writeSuccess(out, "Ha aplicado el poder de la sanidad");
                            try {
                                refWindowController.refClient.clientThread.writer.writeInt(7);
                                refWindowController.refClient.clientThread.writer.writeUTF(
                                        refWindowController.refClient.getName() + " ha usado el poder de la sanación.");
                                refWindowController.refClient.clientThread.writer.writeInt(9);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "fuerza":
                            refWindowController.setWarriorPower(true);
                            writeSuccess(out, "Ha aplicado el poder de la fuerza");
                            try {
                                refWindowController.refClient.clientThread.writer.writeInt(7);
                                refWindowController.refClient.clientThread.writer.writeUTF(
                                        refWindowController.refClient.getName() + " ha usado el poder de la fuerza.");
                                refWindowController.refClient.clientThread.writer.writeInt(9);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "resistencia":
                            refWindowController.setResistance(true);
                            writeSuccess(out, "Ha aplicado el poder de la resistencia");
                            try {
                                refWindowController.refClient.clientThread.writer.writeInt(7);
                                refWindowController.refClient.clientThread.writer.writeUTF(
                                        refWindowController.refClient.getName() + " ha usado el poder de la resistencia.");
                                refWindowController.refClient.clientThread.writer.writeInt(9);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            writeError(out, COMMAND_NAME + ": Argumentos inválidos");
                            break;
                    }
                } else {
                    writeError(out, "No es su turno de jugar");
                }
            } else {
                writeError(out, COMMAND_NAME + ": Argumentos inválidos");
            }
        }
    }
}
