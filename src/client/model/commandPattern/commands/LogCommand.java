package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class LogCommand extends BaseCommand {
    public static final String COMMAND_NAME = "log";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) throws IOException {
        if (args == null || args.length < 1) {
            writeError(out, COMMAND_NAME + ": Número de argumentos inválidos");
        } else {
            switch (args[0]) {
                case "-all":
                    refWindowController.writeSuccess(refWindowController.getAttacksReport());
                    break;
                case "-resumen":
                    int fail = refWindowController.getFailAttacks();
                    int success = refWindowController.getSuccessAttacks();
                    int total = success + fail;
                    String resumen = "Resumen de Ataques:" +
                            "\n\t-Total de ataque realizados: " + total +
                            "\n\t-Total de ataque exitosos: " + success +
                            "\n\t-Total de ataque fallidos: " + fail +
                            "\n\t-Porcentaje de éxito: " + (total == 0 ? 100 : (success / total) * 100) + "%";
                    refWindowController.writeSuccess(resumen);
                    break;
                default:
                    writeError(out, COMMAND_NAME + ": Argumentos inválidos");
            }
        }
    }
}
