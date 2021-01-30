package client.model.game;

import client.control.BoardController;
import client.control.ClientWindowController;
import client.model.BoardItem;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SeaThunders extends Warrior {
    public static final WarriorType TYPE = WarriorType.SEA_THUNDERS;

    public SeaThunders(String name, int percentage, int fuerza, int resistencia, int sanidad, String image, ClientWindowController refWindowController) {
        super(name, percentage, fuerza, resistencia, sanidad, image, refWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    public SeaThunders(ClientWindowController clientWindowController) {
        super(clientWindowController);
        this.registerAttacks(this);
        super.setTYPE(TYPE);
    }

    @Override
    public void registerAttacks(Warrior warrior) {
        warrior.registerAttack("thunder rain", this::thunderRainAttack);
        warrior.registerAttack("poseidon thunders", this::poseidonThundersAttack);
        warrior.registerAttack("eel attack", this::eelAttackAttack);
    }

    public void thunderRainAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                super.introAttack("Thunder Rain");
                ArrayList<BoardItem> items = new ArrayList<>();
                int quantity = 100;
                for (int i = 0; i < quantity; i++) {
                    items.add(BoardController.getInstance().getItem(new Random().nextInt(30), new Random().nextInt(20)));
                }
                super.attackItems(items, "Thunder Rain", 20);

                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " con " + quantity + " rayos del ataque de Thunder Rain del luchador Sea Thunders.",
                        "Se recibió ataque Thunder Rain de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void poseidonThundersAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                super.introAttack("Poseidon Thunder");

                int quantity = new Random().nextInt(6) + 5;

                for (int i = 0; i < quantity; i++) {
                    ArrayList<BoardItem> items = new ArrayList<>(BoardController.getInstance().getQuadrant(
                            new Random().nextInt(9) + 2,
                            new Point(
                                    new Random().nextInt(30),
                                    new Random().nextInt(20)
                            )
                    ));
                    super.attackItems(items, "Poseidon Thunder", 100);

                }

                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " con " + quantity + " rayos del ataque de Poseidon Thunder del luchador Sea Thunders.",
                        "Se recibió ataque Poseidon Thunder de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void eelAttackAttack(String[] args) {
        try {
            if (super.handleNonArgs(args)) {
                super.introAttack("Eel Attack");
                ArrayList<BoardItem> items = new ArrayList<>();
                int quantity = new Random().nextInt(76) + 25;
                int descargas = new Random().nextInt(10) + 1;

                for (int i = 0; i < quantity; i++) {
                    items.add(BoardController.getInstance().getItem(new Random().nextInt(30), new Random().nextInt(20)));
                }
                super.attackItems(items, "Eel Attack", descargas * 10);
                super.successfulAttack(this.refWindowController.refClient.clientThread.getCurrentTurn() +
                                " ha atacado exitosamente a " + this.refWindowController.refClient.getName() +
                                " con " + quantity + " anguilas con " + descargas + " descargas cada una del ataque de Eel Attack del luchador Sea Thunders.",
                        "Se recibió ataque Eel Attack de parte de " + this.refWindowController.refClient.clientThread.getCurrentTurn()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
