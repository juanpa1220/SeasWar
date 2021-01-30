package client.model.game;

import client.control.BoardController;
import client.control.ClientWindowController;
import client.model.BoardItem;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class KrakenRelease extends Warrior {
    public static final WarriorType TYPE = WarriorType.KRAKEN_RELEASE;

    public KrakenRelease(String name, int percentage, int fuerza, int resistencia, int sanidad, String image, ClientWindowController refWindowController) {
        super(name, percentage, fuerza, resistencia, sanidad, image, refWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    public KrakenRelease(ClientWindowController clientWindowController) {
        super(clientWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    @Override
    public void registerAttacks(Warrior warrior) {
        warrior.registerAttack("tentaculos", this::tentaclesAttack);
        warrior.registerAttack("kraken breath", this::krakenBreathAttack);
        warrior.registerAttack("kraken release", this::krakenReleaseAttack);
    }

    private void tentaclesAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                super.introAttack("Tentacles");
                for (int i = 0; i < 3; i++) {
                    Point position = new Point(new Random().nextInt(30), new Random().nextInt(20));
                    ArrayList<BoardItem> items = BoardController.getInstance().getQuadrant(1, position);
                    super.attackItems(items, "Tentacles", 100);
                }
                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " con el ataque de Tentacles del luchador Kraken Release.",
                        "Se recibió ataque  Tentacles de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void krakenBreathAttack(String[] args) {
        try {
            Point position = super.handleXYargs(args);

            if (position != null) {
                super.introAttack("Kraken Breath");
                int direction = new Random().nextInt(4);
                int length = new Random().nextInt(8) + 1;
                AtomicReference<String> dirString = new AtomicReference<>("");
                ArrayList<BoardItem> items = BoardController.getInstance().getLine(position, direction, length);
                super.attackItems(items, "Kraken Breath", 100);

                if (direction == 0) {
                    dirString.set("arriba");
                } else if (direction == 1) {
                    dirString.set("abajo");
                } else if (direction == 2) {
                    dirString.set("la izquierda");
                } else {
                    dirString.set("la derecha");
                }

                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " con el ataque de Kraken Breath del luchador Kraken Release, con dirección hacia " +
                                dirString + " y un largo de " + length + " casillas.",
                        "Se recibió ataque  Kraken Breath de parte de " +
                                this.refWindowController.refClient.clientThread.getCurrentTurn());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void krakenReleaseAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                if (!this.refWindowController.isKrakenControl()) {
                    super.introAttack("Kraken Release");

                    Point position = new Point(new Random().nextInt(30), new Random().nextInt(20));
                    int radio = new Random().nextInt(9) + 1;
//                int radio = 100;

                    ArrayList<BoardItem> items = BoardController.getInstance().getQuadrant(radio, position);
//                    ArrayList<BoardItem> items = this.refWindowController.getWarriors().get(1).army;

                    super.attackItems(items, "Kraken Release", 100);

                    super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                    " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                    " con el ataque de Kraken Release del luchador Kraken Release, con origen el la posición ("
                                    + (position.x + 1) + ", " + (position.y + 1) + ") y un radio de " + radio + " casillas.",
                            "Se recibió ataque  Kraken Release de parte de " +
                                    this.refWindowController.refClient.clientThread.getCurrentTurn());
                } else {
                    String opponent = refWindowController.refClient.clientThread.getCurrentTurn();

                    super.failAttack("Su ataque Kraken Release ha fallado, el oponente tenía un escudo",
                            this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                    " se ha defendido exitosamente del ataque Kraken Release de" + this.refWindowController.refClient.getName(),
                            " se ha activado el escudo del Kraken Control");
                    this.refWindowController.setKrakenControl(false);
                    this.refWindowController.writeSuccess("Su escudo de Kraken Control ha sido usado exitosamente " +
                            "y se le devuelto el ataque al enemigo " + opponent);

                    refWindowController.refClient.clientThread.writer.writeInt(6);
                    refWindowController.refClient.clientThread.writer.writeUTF(TYPE.toString());
                    refWindowController.refClient.clientThread.writer.writeUTF(opponent);
                    refWindowController.refClient.clientThread.writer.writeUTF("kraken release");
                    refWindowController.refClient.clientThread.writer.writeUTF("");
                    refWindowController.refClient.clientThread.writer.writeInt(0);
                    refWindowController.refClient.clientThread.writer.writeInt(super.colorId);
                    refWindowController.addAttackReport("Se ejecutó ataque Kraken Release contra el enemigo " + opponent);
                    this.refWindowController.refClient.clientThread.writer.writeInt(9);

                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}


