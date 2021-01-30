package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import client.model.game.Warrior;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class AttackCommand extends BaseCommand {
    public static final String COMMAND_NAME = "atacar";
    ClientWindowController refWindowController;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) {
        this.refWindowController = refWindowController;
        if (args.length < 6) {
            writeError(out, COMMAND_NAME + ": Número de argumentos inválidos");
            return;
        }
        AtomicReference<String> name = new AtomicReference<>("");
        AtomicReference<String> luchador = new AtomicReference<>("");
        AtomicReference<String> attack = new AtomicReference<>("");
        if (args[0].equals("-n")) {
            name.set(args[1]);
        } else {
            writeError(out, "Argumento -n (nombre) faltante o en posición inválida");
            return;
        }
        if (args[2].equals("-l")) {
            luchador.set(args[3]);
        } else {
            writeError(out, "Argumento -l (luchador) faltante o en posición inválida");
            return;
        }
        if (args[4].equals("-a")) {
            attack.set(args[5]);
        } else {
            writeError(out, "Argumento -a (ataque) faltante o en posición inválida");
            return;
        }
        String[] reduce = Arrays.copyOfRange(args, 6, args.length);

        this.checkAttack(out, name.get(), luchador.get(), attack.get(), reduce);
    }

    private void checkAttack(TextFlow out, String name, String luchador, String attack, String[] args) {
        for (Warrior warrior : this.refWindowController.getWarriors()) {
            if (warrior.name.equals(luchador.toLowerCase())) {
                for (String key : warrior.getAttacks().keySet()) {
                    if (key.equals(attack.toLowerCase())) {
                        if (key.equals("control the kraken")) {
                            this.refWindowController.setKrakenControl(true);
                            this.refWindowController.writeSuccess("Se ha activado un escudo para controlar el Kraken");
                            try {
                                this.refWindowController.refClient.clientThread.writer.writeInt(9);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                refWindowController.refClient.clientThread.writer.writeInt(6);
                                refWindowController.refClient.clientThread.writer.writeUTF(warrior.TYPE.toString());
                                refWindowController.refClient.clientThread.writer.writeUTF(name);
                                refWindowController.refClient.clientThread.writer.writeUTF(key);
                                refWindowController.refClient.clientThread.writer.writeUTF(this.argsToString(args));
                                if (refWindowController.isWarriorPower()) {
                                    refWindowController.refClient.clientThread.writer.writeInt(warrior.fuerza);
                                    refWindowController.setWarriorPower(false);
                                } else {
                                    refWindowController.refClient.clientThread.writer.writeInt(0);
                                }
                                refWindowController.refClient.clientThread.writer.writeInt(warrior.colorId);
                                refWindowController.addAttackReport("Se ejecutó ataque " + key + " contra el enemigo " + name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return;
                    }
                }
            }
        }

        writeError(out, "El luchador o ataque ingresado es incorrecto o no existe");
    }

    private String argsToString(String[] args) {
        StringBuilder result = new StringBuilder();
        for (String arg : args) {
            result.append(arg).append(" ");
        }

        return result.toString();
    }
}
