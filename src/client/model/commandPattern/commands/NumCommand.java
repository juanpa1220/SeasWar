package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import client.model.commandPattern.CommandUtil;
import client.model.game.Warrior;
import javafx.scene.text.TextFlow;

import java.util.concurrent.atomic.AtomicInteger;

public class NumCommand extends BaseCommand {
    public static final String COMMAND_NAME = "num";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) {
        if (args == null || args.length > 3) {
            writeError(out, COMMAND_NAME + ": Número de argumentos inválidos");
        } else {
            AtomicInteger num1 = new AtomicInteger(-1);
            AtomicInteger num2 = new AtomicInteger(-1);
            AtomicInteger num3 = new AtomicInteger(-1);
            try {
                num1.set(Integer.parseInt(args[0]));
                num2.set(Integer.parseInt(args[1]));
                num3.set(Integer.parseInt(args[2]));
                if (num1.get() > -1 && num2.get() > -1 && num3.get() > -1) {
                    for (Warrior enemy : refWindowController.getEnemies()) {
                        try {
                            enemy.getAttacks().get("three numbers").accept(CommandUtil.tokenizerArgs(num1 + " " + num2 + " " + num3));
                            refWindowController.setEnemyPower(0);
                            return;
                        } catch (Exception ignored) {
                        }
                    }
                }
            } catch (Exception exception) {
                writeError(out, "Debe ingresar tres numeros como argumento");
            }
        }
    }
}
