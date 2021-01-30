package client.model.game;

import client.control.BoardController;
import client.control.ClientWindowController;
import client.model.BoardItem;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class FishTelepathy extends Warrior {
    public static final WarriorType TYPE = WarriorType.FISH_TELEPATHY;

    public FishTelepathy(String name, int percentage, int fuerza, int resistencia, int sanidad, String image, ClientWindowController refWindowController) {
        super(name, percentage, fuerza, resistencia, sanidad, image, refWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    public FishTelepathy(ClientWindowController refWindowController) {
        super(refWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    @Override
    public void registerAttacks(Warrior warrior) {
        warrior.registerAttack("cardumen", this::cardumenAttack);
        warrior.registerAttack("shark attack", this::sharkAttack);
        warrior.registerAttack("pulp attack", this::pulpAttack);
    }

    public void cardumenAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                super.introAttack("Cardumen");
                ArrayList<BoardItem> items = new ArrayList<>();
                int quantity = new Random().nextInt(201) + 100;
                for (int i = 0; i < quantity; i++) {
                    items.add(BoardController.getInstance().getItem(new Random().nextInt(30), new Random().nextInt(20)));
                }
                super.attackItems(items, "Cardumen", 34);

                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " con " + quantity + " peces del ataque de Cardumen del luchador Fish Telepathy.",
                        "Se recibió ataque  Cardumen de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sharkAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                super.introAttack("Shark Attack");
                ArrayList<BoardItem> items = new ArrayList<>();

                items.addAll(BoardController.getInstance().getQuadrant(new Random().nextInt(10) + 1, new Point(0, 0)));
                items.addAll(BoardController.getInstance().getQuadrant(new Random().nextInt(10) + 1, new Point(29, 0)));
                items.addAll(BoardController.getInstance().getQuadrant(new Random().nextInt(10) + 1, new Point(0, 19)));
                items.addAll(BoardController.getInstance().getQuadrant(new Random().nextInt(10) + 1, new Point(29, 19)));


                super.attackItems(items, "Shark Attack", 100);

                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " con el ataque de Shark Attack del luchador Fish Telepathy.",
                        "Se recibió ataque Shark Attack de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pulpAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                super.introAttack("Pulp Attack");
                ArrayList<BoardItem> items = new ArrayList<>();

                int quantity = new Random().nextInt(31) + 20;
                for (int i = 0; i < quantity; i++) {
                    for (int j = 0; j < 8; j ++){
                        items.add(BoardController.getInstance().getItem(new Random().nextInt(30), new Random().nextInt(20)));
                    }
                }

                super.attackItems(items, "Pulp Attack", 25);

                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " con " + quantity + " pulpos del ataque de Pulp Attack del luchador Fish Telepathy.",
                        "Se recibió ataque  Pulp Attack de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
