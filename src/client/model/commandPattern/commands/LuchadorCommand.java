package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import client.model.game.*;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LuchadorCommand extends BaseCommand {
    public static final String COMMAND_NAME = "luchador";
    ClientWindowController refWindowController;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute(String[] args, TextFlow out, ClientWindowController refWindowController) {
        this.refWindowController = refWindowController;
        if (args == null || args.length < 14) {
            writeError(out, COMMAND_NAME + ": Número de argumentos inválidos");
        } else {
            AtomicReference<String> name = new AtomicReference<>("");
            AtomicInteger percentage = new AtomicInteger(0);
            AtomicReference<String> type = new AtomicReference<>("");
            AtomicInteger fuerza = new AtomicInteger(0);
            AtomicInteger resistencia = new AtomicInteger(0);
            AtomicInteger sanidad = new AtomicInteger(0);
            AtomicReference<String> image = new AtomicReference<>("");
            for (int i = 0; i < 14; i += 2) {
                switch (args[i].toLowerCase()) {
                    case "-n":
                        name.set(args[i + 1]);
                        break;
                    case "-p":
                        try {
                            percentage.set(Integer.parseInt(args[i + 1]));
                        } catch (NumberFormatException e) {
                            percentage.set(0);
                        }
                        break;
                    case "-t":
                        type.set(args[i + 1]);
                        break;
                    case "-f":
                        try {
                            fuerza.set(Integer.parseInt(args[i + 1]));
                        } catch (NumberFormatException e) {
                            fuerza.set(0);
                        }
                        break;
                    case "-r":
                        try {
                            resistencia.set(Integer.parseInt(args[i + 1]));
                        } catch (NumberFormatException e) {
                            resistencia.set(0);
                        }
                        break;
                    case "-s":
                        try {
                            sanidad.set(Integer.parseInt(args[i + 1]));
                        } catch (NumberFormatException e) {
                            sanidad.set(0);
                        }
                        break;
                    case "-i":
                        image.set(args[i + 1]);
                        break;
                    default:
                        writeError(out, COMMAND_NAME + ": Argumentos inválidos");
                }
            }

            if (!name.get().equals("") && percentage.get() != 0 && !type.get().equals("") && fuerza.get() != 0 &&
                    resistencia.get() != 0 && sanidad.get() != 0 && !image.get().equals("")) {

                WarriorType warriorType = WarriorType.getType(type.get());
                AtomicBoolean flag = new AtomicBoolean(true);

                if (warriorType == null) {
                    writeError(out, "El tipo de luchador ingresado es inválido");
                    flag.set(false);
                } else {
                    switch (warriorType) {
                        case FISH_TELEPATHY:
                            Warrior warrior = new FishTelepathy(name.get(), percentage.get(), fuerza.get(),
                                    resistencia.get(), sanidad.get(), image.get(), refWindowController);
                            refWindowController.getWarriors().add(warrior);
                            break;
                        case KRAKEN_RELEASE:
                            Warrior warrior2 = new KrakenRelease(name.get(), percentage.get(), fuerza.get(),
                                    resistencia.get(), sanidad.get(), image.get(), refWindowController);
                            refWindowController.getWarriors().add(warrior2);
                            break;
                        case POSEIDON_TRIDENT:
                            Warrior warrior3 = new PoseidonTrident(name.get(), percentage.get(), fuerza.get(),
                                    resistencia.get(), sanidad.get(), image.get(), refWindowController);
                            refWindowController.getWarriors().add(warrior3);
                            break;
                        case SEA_THUNDERS:
                            Warrior warrior4 = new SeaThunders(name.get(), percentage.get(), fuerza.get(),
                                    resistencia.get(), sanidad.get(), image.get(), refWindowController);
                            refWindowController.getWarriors().add(warrior4);
                            break;
                        case UNDERSEA_FIRE:
                            Warrior warrior5 = new UnderseaFire(name.get(), percentage.get(), fuerza.get(),
                                    resistencia.get(), sanidad.get(), image.get(), refWindowController);
                            refWindowController.getWarriors().add(warrior5);
                            break;
                        case WAVE_CONTROL:
                            Warrior warrior6 = new WaveControl(name.get(), percentage.get(), fuerza.get(),
                                    resistencia.get(), sanidad.get(), image.get(), refWindowController);
                            refWindowController.getWarriors().add(warrior6);
                            break;
                    }
                    boolean approved = this.checkWarriors();
                    if (approved && flag.get()) {
                        try {
                            refWindowController.refClient.clientThread.writer.writeInt(4);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                writeError(out, COMMAND_NAME + ": Argumentos inválidos");
            }
        }
    }

    private boolean checkWarriors() {
        if (refWindowController.getWarriors().size() == 3) {
            int totalPercentage = 100;
            int totalAttributes = 3 * 100 + 3 * 75 + 3 * 50;
            for (Warrior warrior : refWindowController.getWarriors()) {
                totalPercentage -= warrior.percentage;
                totalAttributes -= warrior.fuerza;
                totalAttributes -= warrior.resistencia;
                totalAttributes -= warrior.sanidad;
            }
            if (totalAttributes == 0 && totalPercentage == 0) {
                refWindowController.addWarriorToPane();
                refWindowController.writeSuccess("Todos los luchadores han sido agregados exitosamente");
                return true;
            } else {
                refWindowController.writeError("La suma de los porcentajes no cierra, intentelo de nuevo");
                refWindowController.getWarriors().clear();
                refWindowController.getWarriorsPaneList().clear();
                return false;
            }
        } else if (refWindowController.getWarriors().size() > 3) {
            refWindowController.writeError("Sólo se permiten tres luchadores");
            refWindowController.getWarriors().remove(3);
            return false;
        } else {
            refWindowController.addWarriorToPane();
            refWindowController.writeSuccess("Luchador agregado");
            return false;
        }
    }
}
