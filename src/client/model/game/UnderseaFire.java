package client.model.game;

import client.control.BoardController;
import client.control.ClientWindowController;
import client.model.BoardItem;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class UnderseaFire extends Warrior {
    public static final WarriorType TYPE = WarriorType.UNDERSEA_FIRE;

    public UnderseaFire(String name, int percentage, int fuerza, int resistencia, int sanidad, String image, ClientWindowController refWindowController) {
        super(name, percentage, fuerza, resistencia, sanidad, image, refWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    public UnderseaFire(ClientWindowController clientWindowController) {
        super(clientWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    @Override
    public void registerAttacks(Warrior warrior) {
        warrior.registerAttack("volcano raising", this::volcanoRaisingAttack);
        warrior.registerAttack("volcano explosion", this::volcanoExplosionAttack);
        warrior.registerAttack("thermal rush", this::thermalRushAttack);
    }

    public void volcanoRaisingAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                super.introAttack("Volcano Raising");

                Point position = new Point(new Random().nextInt(30), new Random().nextInt(20));
                int radio = new Random().nextInt(9) + 1;

                ArrayList<BoardItem> items = BoardController.getInstance().getQuadrant(radio, position);
                items.forEach(
                        item -> {
                            item.setVolcano(true);
                            item.setVolcanoRadio(radio);
                        }
                );

                super.attackItems(items, "Volcano Raising", 100);

                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " con el ataque de Volcano Raising del luchador Undersea Fire, con origen el la posición ("
                                + (position.x + 1) + ", " + (position.y + 1) + ") y un radio de " + radio + " casillas.",
                        "Se recibió ataque Volcano Raising de parte de " +
                                this.refWindowController.refClient.clientThread.getCurrentTurn());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void volcanoExplosionAttack(String[] args) {
        try {
            Point position = super.handleXYargs(args);
            if (position != null) {
                if (BoardController.getInstance().getItem(position.x, position.y).isVolcano()) {
                    super.introAttack("Volcano Explosion");
                    ArrayList<BoardItem> items = new ArrayList<>();
                    int numRocks = BoardController.getInstance().getItem(position.x, position.y).getVolcanoRadio() *
                            BoardController.getInstance().getItem(position.x, position.y).getVolcanoRadio() * 10;

                    for (int i = 0; i < numRocks; i++) {
                        items.add(BoardController.getInstance().getItem(new Random().nextInt(30), new Random().nextInt(20)));
                    }

                    super.attackItems(items, "Volcano Explosion", 20);

                    super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                    " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                    " con " + numRocks + " piedras del ataque de Volcano Explosion del luchador Undersea Fire.",
                            "Se recibió ataque Volcano Explosion de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                    );

                } else {
                    super.attackError("La casilla seleccionada no posee volcán");
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void thermalRushAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                super.introAttack("Thermal Rush");
                ArrayList<BoardItem> items = new ArrayList<>();
                ArrayList<BoardItem> board = BoardController.getInstance().getBoard();
                AtomicInteger value = new AtomicInteger(1);

                for (BoardItem item : board) {
                    if (item.isVolcano()) {
                        value.set(item.getVolcanoRadio());
                        ArrayList<BoardItem> items2 = BoardController.getInstance().getQuadrant(value.get(), new Point(item.getCol(), item.getRow()));
                        for (BoardItem item2 : items2) {
                            if (item2.getLife() > 0 && !items.contains(item2)) {
                                items.add(item2);
                            }
                        }
                    }
                }

                if (items.size() > 0) {
                    int seg = new Random().nextInt(1) + 5;
                    super.attackItems(items, "Thermal Rush", seg * value.get());


                    super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                    " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                    " con daño de " + value.get() +
                                    " por " + seg + " segundos del ataque de Thermal Rush del luchador Undersea Fire.",
                            "Se recibió ataque Thermal Rush de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                    );

                } else {
                    super.attackError("El enemigo no posee volcanes");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
