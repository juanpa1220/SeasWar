package client.model.game;

import client.control.BoardController;
import client.control.ClientWindowController;
import client.model.BoardItem;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PoseidonTrident extends Warrior {
    public static final WarriorType TYPE = WarriorType.POSEIDON_TRIDENT;

    public PoseidonTrident(String name, int percentage, int fuerza, int resistencia, int sanidad, String image, ClientWindowController refWindowController) {
        super(name, percentage, fuerza, resistencia, sanidad, image, refWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    public PoseidonTrident(ClientWindowController clientWindowController) {
        super(clientWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    @Override
    public void registerAttacks(Warrior warrior) {
        warrior.registerAttack("three lines", this::threeLinesAttack);
        warrior.registerAttack("three numbers", this::threeNumbersAttack);
        warrior.registerAttack("control the kraken", this::krakenControlAttack);
    }

    private void threeLinesAttack(String[] args) {
        try {
            if (args.length == 12) {
                Point position1 = super.handleXYargs(Arrays.copyOfRange(args, 0, 4));
                Point position2 = super.handleXYargs(Arrays.copyOfRange(args, 4, 8));
                Point position3 = super.handleXYargs(Arrays.copyOfRange(args, 8, 12));
                ArrayList<Point> points = new ArrayList<>();
                points.add(position1);
                points.add(position2);
                points.add(position3);
                if (position1 != null && position2 != null && position3 != null) {
                    super.introAttack("three lines");
                    points.forEach(position -> {
                        int direction = new Random().nextInt(4);
                        int length = new Random().nextInt(4) + 1;
                        ArrayList<BoardItem> items = BoardController.getInstance().getLine(position, direction, length);
                        try {
                            super.attackItems(items, "three lines", 100);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                    " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                    " con el ataque de Three Lines del luchador Poseidon Trident, en las posiciones (" +
                                    position1.x + ", " + position1.y + "), (" + position2.x + ", " + position2.y +
                                    ") y (" + position3.x + ", " + position3.y + ")",
                            "Se recibió ataque Three Lines de parte de " +
                                    this.refWindowController.refClient.clientThread.getCurrentTurn());
                }

            } else {
                super.attackError("Se esperan 3 pares de valores -x y -y");
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void threeNumbersAttack(String[] args) {
        try {
            ArrayList<Integer> tem = new ArrayList<>();
            tem.add(Integer.parseInt(args[0]));
            tem.add(Integer.parseInt(args[1]));
            tem.add(Integer.parseInt(args[2]));
            AtomicBoolean flag = new AtomicBoolean(false);

            for (int i : tem) {
                for (int j : this.refWindowController.threeNumbers) {
                    if (j == i) {
                        flag.set(true);
                    }
                }
            }

            if (flag.get()) {
                ArrayList<BoardItem> items = new ArrayList<>();
                AtomicInteger quantity = new AtomicInteger(1);
                this.refWindowController.threeNumbers.forEach(integer -> quantity.updateAndGet(v -> v * integer));

                for (int i = 0; i < quantity.get(); i++) {
                    items.add(BoardController.getInstance().getItem(new Random().nextInt(30), new Random().nextInt(20)));
                }

                super.attackItems(items, "Three Numbers", 100);
                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " a " + quantity + " casillas con el ataque de Three Numbers del luchador Poseidon Trindent.",
                        "Se recibió ataque Three Numbers de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                );

            } else {
                String opponent = refWindowController.refClient.clientThread.getCurrentTurn();
                super.failAttack("Su ataque Three Numbers ha fallado, el oponente no ha atinado a ningún número",
                        this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " se ha defendido exitosamente del ataque Three Numbers de" + this.refWindowController.refClient.getName(),
                        " no ha atinado a ningún número");

                this.refWindowController.writeSuccess("Ha defendido el ataque de Three Numbers del oponente " + opponent + " satisfactoriamente");
            }
            this.refWindowController.threeNumbers.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void krakenControlAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                super.introAttack("Control the Kraken");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
