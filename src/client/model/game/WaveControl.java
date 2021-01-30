package client.model.game;

import client.control.BoardController;
import client.control.ClientWindowController;
import client.model.BoardItem;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class WaveControl extends Warrior {
    public static final WarriorType TYPE = WarriorType.WAVE_CONTROL;


    public WaveControl(String name, int percentage, int fuerza, int resistencia, int sanidad, String image, ClientWindowController refWindowController) {
        super(name, percentage, fuerza, resistencia, sanidad, image, refWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    public WaveControl(ClientWindowController clientWindowController) {
        super(clientWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    @Override
    public void registerAttacks(Warrior warrior) {
        warrior.registerAttack("swirl raising", this::swirlRaisingAttack);
        warrior.registerAttack("human garbage", this::humanGarbageAttack);
        warrior.registerAttack("radioactive rush", this::radioactiveRushAttack);
    }

    public void swirlRaisingAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                super.introAttack("Swirl Raising");

                Point position = new Point(new Random().nextInt(30), new Random().nextInt(20));
                int radio = new Random().nextInt(9) + 2;

                ArrayList<BoardItem> items = BoardController.getInstance().getQuadrant(radio, position);
                items.forEach(item -> {
                            item.setSwirl(true);
                            item.setSwirlRadio(radio);
                        }
                );

                super.attackItems(items, "Swirl Raising", 100);

                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " con el ataque de Swirl Raising del luchador Wave Control, con origen el la posición ("
                                + (position.x + 1) + ", " + (position.y + 1) + ") y un radio de " + radio + " casillas.",
                        "Se recibió ataque Swirl Raising de parte de " +
                                this.refWindowController.refClient.clientThread.getCurrentTurn());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void humanGarbageAttack(String[] args) {
        try {
            Point position = super.handleXYargs(args);
            if (position != null) {
                if (BoardController.getInstance().getItem(position.x, position.y).isSwirl()) {
                    super.introAttack("Human Garbage");
                    ArrayList<BoardItem> items = new ArrayList<>();
                    int radio = BoardController.getInstance().getItem(position.x, position.y).getSwirlRadio();
                    int numGarbage = radio * 10;

                    for (int i = 0; i < numGarbage; i++) {
                        BoardItem item = BoardController.getInstance().getItem(new Random().nextInt(30), new Random().nextInt(20));
                        item.setGarbage(new Random().nextBoolean());
                        item.setGarbageValue(numGarbage);
                        items.add(item);
                    }

                    super.attackItems(items, "Human Garbage", 25);

                    super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                    " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                    " con " + numGarbage + " toneladas de basura del ataque de Human Garbage del luchador Wave Control.",
                            "Se recibió ataque Human Garbage de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                    );

                } else {
                    super.attackError("La casilla seleccionada no posee remolino");
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void radioactiveRushAttack(String[] args) {
        try {
            ArrayList<BoardItem> items = new ArrayList<>();
            AtomicInteger value = new AtomicInteger(1);
            ArrayList<BoardItem> boardItems = BoardController.getInstance().getBoard();
            boardItems.forEach(item -> {
                if (item.isGarbage()) {
                    items.add(item);
                    value.set(item.getGarbageValue());
                }
            });
            if (items.size() > 0) {
                super.introAttack("Radioactive Rush");
                int seg = new Random().nextInt(10) + 1;
                int damage = seg * value.get();

                super.attackItems(items, "Radioactive Rush", damage);

                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " con daño de " + value.get() +
                                " toneladas de basura por " + seg + " segundos del ataque de Radioactive Rush del luchador Wave Control.",
                        "Se recibió ataque Radioactive Rush de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                );

            } else {
                super.attackError("la civilización del contrincante no posee basura radioactiva");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
