package client.model;

import client.control.ClientWindowController;
import client.model.commandPattern.CommandUtil;
import client.model.game.Warrior;
import client.model.game.WarriorType;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread {
    public DataInputStream reader;
    public DataOutputStream writer;

    private boolean running = true;
    private final ClientWindowController refClientWindow;
    private int id;
    private int currentTurnId = 0;
    private String currentTurn = "";
    private String name;

    public ClientThread(Socket socketRef, ClientWindowController refClientWindow) throws IOException {
        this.reader = new DataInputStream(socketRef.getInputStream());
        this.writer = new DataOutputStream(socketRef.getOutputStream());

        this.refClientWindow = refClientWindow;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // esperar hasta que reciba un entero
                int instructionId = reader.readInt();

                switch (instructionId) {
                    case 0:
                        boolean hasInit = reader.readBoolean();
                        if (hasInit) {
                            Platform.runLater(() -> this.refClientWindow.writeError("La partida ya ha iniciado"));
                        } else {
                            this.refClientWindow.setHasConnected(true);
//                            this.refClientWindow.setLeftCommands(1);
                            Platform.runLater(() -> {
                                this.refClientWindow.setLeftCommands(1);
                                this.refClientWindow.writeSuccess("Conección exitosa");
                            });
                        }
                        break;
                    case 1: // pasan un mensaje por el chat
                        this.id = reader.readInt();
                        this.name = reader.readUTF();
                        break;
                    case 2: // pasan un mensaje por el chat
                        String user = reader.readUTF();
                        String message = reader.readUTF();
                        Platform.runLater(() -> this.refClientWindow.writeMessage(user + " >> " + message + "\n", true));
                        break;
                    case 3: // pasan un mensaje privado por el chat
                        String privateUser = reader.readUTF();
                        String privateMessage = reader.readUTF();
                        Platform.runLater(() -> this.refClientWindow.writeMessage(privateUser + " >> " + privateMessage + "\n", false));
                        break;

                    case 4: // pasan un error
                        String errorMessage = reader.readUTF();
                        Platform.runLater(() -> this.refClientWindow.writeError(errorMessage));
                        break;
                    case 5: // init
                        this.currentTurnId = reader.readInt();
                        String turn = reader.readUTF();
                        this.currentTurn = turn;
                        Platform.runLater(() -> {
                            this.refClientWindow.printTurn(turn);
                            this.refClientWindow.writeSuccess("Partida iniciada");
                            this.refClientWindow.setLeftCommands(2);
                            try {
                                this.refClientWindow.initBoard();
                                this.refClientWindow.updateDetailsPane();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        break;
                    case 6: // pasan un mensaje privado por el chat
                        String warriorType = reader.readUTF();
                        String attack = reader.readUTF();
                        String args = reader.readUTF();
                        int warriorPower = reader.readInt();
                        int warriorId = reader.readInt();
                        Platform.runLater(() -> this.handleAttack(warriorType, attack, args, warriorPower, warriorId));
                        break;
                    case 7: // bitácora
                        String inform = reader.readUTF();
                        Platform.runLater(() -> this.refClientWindow.writeOnBitacora(inform));
                        break;
                    case 8: // pasan un success
                        String successMessage = reader.readUTF();
                        if (successMessage.equals("El ataque se ha ejecutado correctamente")) {
                            this.refClientWindow.increaseSuccessAttacks();
                        }
                        Platform.runLater(() -> this.refClientWindow.writeSuccess(successMessage));
                        break;
                    case 9: // next Turn
                        this.currentTurnId = reader.readInt();
                        this.currentTurn = reader.readUTF();
                        Platform.runLater(() -> {
                            this.refClientWindow.printTurn(currentTurn);
                            this.refClientWindow.updateDetailsPane();
                        });
                        break;
                    case 10: // attack log
                        String report = reader.readUTF();
                        Platform.runLater(() -> this.refClientWindow.writeOnAttackLog(report));
                        break;
                    case 11: //win
                        this.writer.writeInt(7);
                        this.writer.writeUTF(this.name + " ha ganado la partida");
                        Platform.runLater(() -> this.refClientWindow.writeSuccess("FELICITACIONES!!! HAS GANADO LA PARTIDA"));
                        break;
                    case 12: // remove commands
                        Platform.runLater(this.refClientWindow::removeLevelTwoCommands);
                        break;
                    case 13: // consult
                        int consultant = reader.readInt();
                        writer.writeInt(8);
                        writer.writeInt(consultant);
                        writer.writeUTF(this.refClientWindow.getSummary());
                        break;
                    case 14: // set power
                        this.refClientWindow.setWarriorPower(true);
                        break;
                    case 15:// traspasa poderes
                        int warriorId2 = reader.readInt();
                        String warriorType2 = reader.readUTF();
                        Platform.runLater(() -> this.refClientWindow.clonePowers(warriorId2, warriorType2));
                        break;
                    case 16:
                        this.refClientWindow.increaseFailAttacks();
                        break;
                }
            } catch (IOException ex) {
                System.out.println("El server se ha desconectado.");
                System.exit(0);
            }
        }
    }

    private void handleAttack(String type, String attack, String args, int warriorPower, int warriorId) {
        WarriorType warriorType = WarriorType.getType(type);
        String[] args2 = CommandUtil.tokenizerArgs(args);
        if (warriorType == null) {
            System.out.println("ERORRRRR AQUI");
        } else {
            this.refClientWindow.setEnemyPower(warriorPower);
            this.refClientWindow.setLastAttackerWarriorId(warriorId);
            for (Warrior enemy : this.refClientWindow.getEnemies()) {
                try {
                    enemy.getAttacks().get(attack).accept(args2);
                    this.refClientWindow.setEnemyPower(0);
                    return;
                } catch (Exception ignored) {
                }
            }

//            switch (warriorType) {
//                case FISH_TELEPATHY:
//                    for (Warrior enemy : this.refClientWindow.getEnemies()) {
//                        if (enemy.TYPE == WarriorType.FISH_TELEPATHY) {
//                            enemy.getAttacks().get(attack).accept(args2); // execute the attack and send the args by parameter
//                        }
//                    }
//                    break;
//                case KRAKEN_RELEASE:
//                    for (Warrior enemy : this.refClientWindow.getEnemies()) {
//                        if (enemy.TYPE == WarriorType.KRAKEN_RELEASE) {
//                            enemy.getAttacks().get(attack).accept(args2); // execute the attack and send the args by parameter
//                        }
//                    }
//                    break;
//                case POSEIDON_TRIDENT:
//                    for (Warrior enemy : this.refClientWindow.getEnemies()) {
//                        if (enemy.TYPE == WarriorType.POSEIDON_TRIDENT) {
//                            enemy.getAttacks().get(attack).accept(args2); // execute the attack and send the args by parameter
//                        }
//                    }
//                    break;
//                case SEA_THUNDERS:
//                    for (Warrior enemy : this.refClientWindow.getEnemies()) {
//                        if (enemy.TYPE == WarriorType.SEA_THUNDERS) {
//                            enemy.getAttacks().get(attack).accept(args2); // execute the attack and send the args by parameter
//                        }
//                    }
//                    break;
//                case UNDERSEA_FIRE:
//                    for (Warrior enemy : this.refClientWindow.getEnemies()) {
//                        if (enemy.TYPE == WarriorType.UNDERSEA_FIRE) {
//                            enemy.getAttacks().get(attack).accept(args2); // execute the attack and send the args by parameter
//                        }
//                    }
//                    break;
//                case WAVE_CONTROL:
//                    for (Warrior enemy : this.refClientWindow.getEnemies()) {
//                        if (enemy.TYPE == WarriorType.WAVE_CONTROL) {
//                            enemy.getAttacks().get(attack).accept(args2); // execute the attack and send the args by parameter
//                        }
//                    }
//                    break;
//            }
//            this.refClientWindow.setEnemyPower(0);
        }
    }


    public int getCurrentTurnId() {
        return currentTurnId;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public int getThreadId() {
        return this.id;
    }

    public String getClientName() {
        return this.name;
    }
}
