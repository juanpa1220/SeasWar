package client.model.game;

import client.control.ClientWindowController;
import client.model.BoardItem;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public abstract class Warrior {
    public String name;
    public int percentage;
    public int fuerza;
    public int resistencia;
    public int sanidad;
    public String image;
    public final ClientWindowController refWindowController;
    private final Map<String, Consumer<String[]>> attacks;
    public WarriorType TYPE;
    public ArrayList<BoardItem> army;
    public int colorId;

    public Warrior(String name, int percentage, int fuerza, int resistencia, int sanidad, String image, ClientWindowController refWindowController) {
        this.name = name;
        this.percentage = percentage;
        this.fuerza = fuerza;
        this.resistencia = resistencia;
        this.sanidad = sanidad;
        this.image = image;
        this.refWindowController = refWindowController;
        this.attacks = new HashMap<>();
        this.army = new ArrayList<>();
    }

    public Warrior(ClientWindowController refWindowController) {
        this.refWindowController = refWindowController;
        this.attacks = new HashMap<>();
    }

    public abstract void registerAttacks(Warrior warrior);

    public void registerAttack(String attackName, Consumer<String[]> func) {
        attacks.put(attackName, func);
    }

    public Map<String, Consumer<String[]>> getAttacks() {
        return attacks;
    }

    public int getNumArmyAlive() {
        int counter = 0;
        for (BoardItem item : this.army) {
            if (item.getLife() > 0) {
                counter++;
            }
        }
        return counter;
    }

    public int getNumArmy() {
        return this.army.size();
    }

    public int getSumArmyLife() {
        int counter = 0;
        for (BoardItem item : this.army) {
            counter += item.getLife();
        }
        return counter;
    }

    public void attackItems(ArrayList<BoardItem> items, String attack, int value) throws IOException {
        for (BoardItem item : items) {
            AtomicInteger temValue = new AtomicInteger(0);
            AtomicReference<String> temReport = new AtomicReference<>("");
            if (this.refWindowController.isResistance()) {
                Warrior temWarrior = refWindowController.getWarriors().get(item.getId());
                int tem = temWarrior.resistencia - value;
                if (tem < 0) {
                    temValue.set(Math.abs(tem));
                }
                temReport.set(", tenía resistencia de " + temWarrior.resistencia);
            } else {
                temValue.set(value);
            }

            AtomicReference<String> temReport2 = new AtomicReference<>("");
            if (this.refWindowController.getEnemyPower() > 0) {
                temValue.set(temValue.get() + this.refWindowController.getEnemyPower());
                if (temValue.get() > 100) {
                    temValue.set(100);
                }
                temReport2.set(", tenía poder de fuerza activado de +" + this.refWindowController.getEnemyPower());

            }

            if (item.getLife() > 0) {
                int lifeBefore = item.getLife();
                item.attack(temValue.get());
                this.refWindowController.refClient.clientThread.writer.writeInt(10);
                this.refWindowController.refClient.clientThread.writer.writeInt(this.refWindowController.refClient.clientThread.getCurrentTurnId());
                AtomicReference<String> reference = new AtomicReference<>("");
                if (attack.equals("Swirl Raising") && temValue.get() > 0) {
                    reference.set(" y colocó remolino ");
                }
                if (attack.equals("Volcano Raising") && temValue.get() > 0) {
                    reference.set(" y colocó volcán ");
                }
                String report = "Atacando casilla (" + (item.getCol() + 1) + ", " + (item.getRow() + 1) +
                        "): pasó de " + lifeBefore + " a " + item.getLife() + reference.get() + temReport.get() + temReport2.get();
                String report2 = "Recibió ataque " + attack + " de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                        + ", pasó de " + lifeBefore + " a " + item.getLife() + reference.get() + temReport.get() + temReport2.get();
                item.addActivityReport(report2);
                this.refWindowController.refClient.clientThread.writer.writeUTF(report);
                this.refWindowController.writeOnAttackLog2(report);
            } else {
                this.refWindowController.refClient.clientThread.writer.writeInt(10);
                this.refWindowController.refClient.clientThread.writer.writeInt(this.refWindowController.refClient.clientThread.getCurrentTurnId());
                String report = "Atacando casilla (" + item.getCol() + ", " + item.getRow() +
                        "): ya estaba sin vida";
                String report2 = "Recibió ataque " + attack + " de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                        + ", ya estaba sin vida";
                item.addActivityReport(report2);
                this.refWindowController.refClient.clientThread.writer.writeUTF(report);
                this.refWindowController.writeOnAttackLog2(report);
            }
        }
        this.refWindowController.refClient.clientThread.writer.writeInt(10);
        this.refWindowController.refClient.clientThread.writer.writeInt(this.refWindowController.refClient.clientThread.getCurrentTurnId());
        this.refWindowController.refClient.clientThread.writer.writeUTF(" ");
        this.refWindowController.writeOnAttackLog2(" ");
    }

    public void introAttack(String attackName) throws IOException {
        this.refWindowController.refClient.clientThread.writer.writeInt(10);
        this.refWindowController.refClient.clientThread.writer.writeInt(this.refWindowController.refClient.clientThread.getCurrentTurnId());
        this.refWindowController.refClient.clientThread.writer.writeUTF("Ataque " + attackName + " ejecutándose...");
        this.refWindowController.writeOnAttackLog2("Recibiendo ataque " + attackName + " de parte de: " +
                this.refWindowController.refClient.clientThread.getCurrentTurn());
    }

    public void successfulAttack(String report, String attackReport) throws IOException {
        this.refWindowController.refClient.clientThread.writer.writeInt(7);
        this.refWindowController.refClient.clientThread.writer.writeUTF(report);

        this.refWindowController.refClient.clientThread.writer.writeInt(8);
        this.refWindowController.refClient.clientThread.writer.writeInt(this.refWindowController.refClient.clientThread.getCurrentTurnId());
        this.refWindowController.refClient.clientThread.writer.writeUTF("El ataque se ha ejecutado correctamente");

        this.refWindowController.setLastAttackerId(this.refWindowController.refClient.clientThread.getCurrentTurnId());

        this.refWindowController.refClient.clientThread.writer.writeInt(9);
        this.refWindowController.setResistance(false);
        this.refWindowController.addAttackReport(attackReport);
    }

    public void failAttack(String message, String report, String attackReport) throws IOException {
        this.refWindowController.refClient.clientThread.writer.writeInt(11);
        this.refWindowController.refClient.clientThread.writer.writeInt(this.refWindowController.refClient.clientThread.getCurrentTurnId());
        this.refWindowController.refClient.clientThread.writer.writeUTF(message);


        this.refWindowController.refClient.clientThread.writer.writeInt(7);
        this.refWindowController.refClient.clientThread.writer.writeUTF(report);

        this.refWindowController.refClient.clientThread.writer.writeInt(15);
        this.refWindowController.refClient.clientThread.writer.writeInt(this.refWindowController.refClient.clientThread.getCurrentTurnId());

        this.refWindowController.refClient.clientThread.writer.writeInt(9);
        this.refWindowController.addAttackReport(attackReport);
    }

    public void attackError(String message) throws IOException {
        this.refWindowController.refClient.clientThread.writer.writeInt(11);
        this.refWindowController.refClient.clientThread.writer.writeInt(this.refWindowController.refClient.clientThread.getCurrentTurnId());
        this.refWindowController.refClient.clientThread.writer.writeUTF(message);
    }

    public boolean handleNonArgs(String[] args) throws IOException {
        if (args.length > 0) {
            this.attackError("Argumentos no identificados.");
            return false;
        }
        return true;
    }


    public Point handleXYargs(String[] xyArgs) throws IOException {
        if (xyArgs.length == 4) {
            Point position = new Point();
            if (xyArgs[0].equals("-x")) {
                try {
                    int x = Integer.parseInt(xyArgs[1]) - 1;
                    if (x >= 0 && x < 30) {
                        position.x = x;
                    } else {
                        this.attackError("Error en los argumetos de la posición. El valor del argumento -x debe ser un número entero válido");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    this.attackError("Error en los argumetos de la posición. El valor del argumento -x debe ser un número entero válido");
                    return null;
                }

            } else {
                this.attackError("Error en los argumetos de la posición. Bebe ingresar un -x y un -y");
                return null;
            }
            if (xyArgs[2].equals("-y")) {
                try {
                    int y = Integer.parseInt(xyArgs[3]) - 1;
                    if (y >= 0 && y < 20) {
                        position.y = y;
                    } else {
                        this.attackError("Error en los argumetos de la posición. El valor del argumento -y debe ser un número entero válido");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    this.attackError("Error en los argumetos de la posición. El valor del argumento -y debe ser un número entero válido");
                    return null;
                }
            } else {
                this.attackError("Error en los argumetos de la posición. Bebe ingresar un valores válidos para -x y -y");
                return null;
            }
            return position;
        } else {
            this.attackError("Error en los argumetos de la posición. Bebe ingresar un -x y un -y");
            return null;
        }
    }

    public void cure() {
        this.army.forEach(boardItem -> {
            int temLife = boardItem.getLife();
            if (temLife > 0 && temLife != 100) {
                boardItem.cure(this.sanidad);
                String report = "Curando casilla (" + boardItem.getCol() + ", " + boardItem.getRow() +
                        "): pasó de " + temLife + " a " + boardItem.getLife();
                String report2 = "Recibió curación, pasó de " + temLife + " a " + boardItem.getLife();
                boardItem.addActivityReport(report2);
                this.refWindowController.writeOnAttackLog(report);
            }
        });
        this.refWindowController.writeOnAttackLog("");
    }

    public void setTYPE(WarriorType TYPE) {
        this.TYPE = TYPE;
    }
}

