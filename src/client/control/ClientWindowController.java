package client.control;

import client.model.Client;
import client.model.commandPattern.CommandManager;
import client.model.commandPattern.CommandUtil;
import client.model.commandPattern.ICommand;
import client.model.game.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientWindowController implements Serializable {
    public TextField commandInput;
    public TextFlow commandOutput;
    public TextFlow bitacoraOutput;
    public TextFlow attackLogOutput;
    public AnchorPane anchorBoardPane;
    public Label lblTurn;
    public TilePane warriorsPane;
    public TilePane detailsPane;
    public Client refClient;
    private ObservableList<Node> warriorsPaneList;
    private ObservableList<Node> detailsPaneList;
    private boolean hasConnected = false;
    private final ArrayList<Warrior> warriors = new ArrayList<>();
    private final ArrayList<Warrior> enemies = new ArrayList<>();
    private final CommandManager commandManager;
    private boolean unavailableFlag;
    private boolean percentageFlag;
    private boolean aliveFlag;
    private String stateSummary;
    private boolean isResistance = false;
    private boolean isWarriorPower = false;
    private int enemyPower = 0;
    private int successAttacks;
    private int failAttacks;
    private String attacksReport;

    private int lastAttacker;
    private int lastAttackerWarriorId;
    private boolean isKrakenControl;
    private boolean hasInit;


    public ClientWindowController() {
        this.commandManager = CommandManager.getInstance();
        this.setUpEnemies();
        this.unavailableFlag = true;
        this.percentageFlag = true;
        this.aliveFlag = true;
        this.stateSummary = "";
        this.successAttacks = 0;
        this.failAttacks = 0;
        this.attacksReport = "Reporte de Ataques:\n";
        this.lastAttacker = -1;
        this.lastAttackerWarriorId = -1;
        this.isKrakenControl = false;
        this.hasInit = false;
    }

    public void setLeftCommands(int level) {
        // sólo los que están conectados y jugando tienen derecho a estos commands
        this.commandManager.setLeftCommands(level);

//         just for test
        if (level == 1) {
            this.commandInput.setText("luchador -n \"nombre\" -p 20 -t \"kraken release\" -f 100 -r 75 -s 50 -i \"../imgs/crush.png\"");
            this.onEnterAction();
            this.commandInput.setText("luchador -n \"nombre1\" -p 20 -t \"poseidon trident\" -f 50 -r 100 -s 75 -i \"../imgs/crush.png\"");
            this.onEnterAction();
            this.commandInput.setText("luchador -n \"nombre2\" -p 60 -t \"wave control\" -f 75 -r 50 -s 100 -i \"../imgs/crush.png\"");
            this.onEnterAction();
        }
    }

    public void removeLevelTwoCommands() {
        this.commandManager.removeLevelTwoCommands();
    }

    public void initBoard() throws IOException {
        this.hasInit = true;
        AnchorPane boardPane = FXMLLoader.load(getClass().getResource("../view/board.fxml"));
        BoardController boardController = BoardController.getInstance();
        boardController.createBoard();
        this.anchorBoardPane.getChildren().setAll(boardPane);
        boardController.printBoard(this.warriors);
    }

    public void setRefClient(Client refClient) {
        this.refClient = refClient;
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            this.onEnterAction();
        }
    }

    public void onEnterAction() {
        String input = this.commandInput.getText();
        Text response = new Text(">> " + input + "\n");
        response.setStyle(" -fx-fill: #000000;");
        this.commandOutput.getChildren().addAll(response);
        this.commandInput.setText("");

        String[] commands = CommandUtil.tokenizerArgs(input);
        if (commands.length > 0) {
            String commandName = commands[0];
            String[] commandArgs;

            if (commands.length > 1) {
                commandArgs = Arrays.copyOfRange(commands, 1, commands.length);
            } else {
                commandArgs = new String[0];
            }

            ICommand command = this.commandManager.getCommand(commandName);
            try {
                command.execute(commandArgs, this.commandOutput, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void printTurn(String turn) {
        this.lblTurn.setText("  Turno: " + turn);
    }

    public void writeMessage(String message, boolean isPublic) {
        Text text = new Text(message);
        if (isPublic) {
            text.setStyle(" -fx-fill: #0047d0;");
        } else {
            text.setStyle(" -fx-fill: #5900ac;");
        }
        this.commandOutput.getChildren().add(text);
    }

    public void writeError(String message) {
        Text text = new Text(">> " + message + "\n");
        text.setStyle(" -fx-fill: #ff0000;");
        this.commandOutput.getChildren().add(text);
    }

    public void writeSuccess(String message) {
        Text text = new Text(">> " + message + "\n");
        text.setStyle("-fx-fill: #008000;");
        this.commandOutput.getChildren().addAll(text);
        this.setWarriorPower(false);
    }

    public void writeOnBitacora(String message) {
        Text text = new Text(".: " + message + "\n");
        text.setStyle(" -fx-fill: #5900ac;");
        this.bitacoraOutput.getChildren().addAll(text);
    }

    public void writeOnAttackLog(String message) {
        Text text = new Text(".: " + message + "\n");
        text.setStyle(" -fx-fill: #0047d0;");
        this.attackLogOutput.getChildren().addAll(text);
    }

    public void writeOnAttackLog2(String message) {
        Text text = new Text(".: " + message + "\n");
        text.setStyle(" -fx-fill: #912f62;");
        this.attackLogOutput.getChildren().addAll(text);
    }


    public void setHasConnected(boolean hasConnected) {
        this.hasConnected = hasConnected;
    }

    private void checkWarriors() {
        ArrayList<Warrior> tem = new ArrayList<>();
        for (Warrior warrior : this.warriors) {
            if (warrior.getNumArmyAlive() <= 0) {
                this.writeError("Su luchador " + warrior.name + " ha sido eliminado");
                tem.add(warrior);
            }
        }

        tem.forEach(warrior -> {
            try {
                this.refClient.clientThread.writer.writeInt(7);
                this.refClient.clientThread.writer.writeUTF("El luchador " + warrior.name + " del jugador " +
                        this.refClient.clientThread.getClientName() + " ha sido vencido");

                this.refClient.clientThread.writer.writeInt(14); // traspasa poderes
                this.refClient.clientThread.writer.writeInt(this.lastAttacker);
                this.refClient.clientThread.writer.writeInt(this.lastAttackerWarriorId);
                this.refClient.clientThread.writer.writeUTF(warrior.TYPE.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.warriors.remove(warrior);
        });

        if (this.warriors.size() == 0) {
            this.writeError("Todos sus luchadores han sido eliminados. Has perdido la partida");
            try {
                this.refClient.clientThread.writer.writeInt(7);
                this.refClient.clientThread.writer.writeUTF(this.refClient.clientThread.getClientName() + " ha perdido la partida");
                this.refClient.clientThread.writer.writeInt(12);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void updateDetailsPane() {
        this.checkWarriors();
        this.detailsPaneList = this.detailsPane.getChildren();
        this.detailsPaneList.clear();
        this.detailsPane.setHgap(280);
        this.detailsPane.setStyle("-fx-alignment: TOP_CENTER");
        int totalAlive = 0;
        int totalDead = 0;
        int lifePercentage = 0;
        int i = 0;
        for (Warrior warrior : this.warriors) {
            int temAlive = warrior.getNumArmyAlive();
            int temNumTotalArmy = warrior.getNumArmy();
            totalAlive += temAlive;
            totalDead += temNumTotalArmy - temAlive;
            if (temNumTotalArmy != 0) {
                lifePercentage += warrior.getSumArmyLife() / temNumTotalArmy;
                i++;
            }
        }
        if (i != 0) {
            lifePercentage /= i;
        }
        Label total = new Label("RESUMEN GENERAL");
        Label life = new Label("Vida: " + lifePercentage + "%");
        Label alive = new Label("Casillas vivas: " + totalAlive);
        Label dead = new Label("Casillas muertas: " + totalDead);
        VBox temBox = new VBox(total, life, alive, dead, new Separator());
        temBox.setSpacing(10);
        temBox.setStyle("-fx-alignment: CENTER; -fx-font-family: Ayuthaya;");
        this.detailsPaneList.add(temBox);

        this.stateSummary = "Estado de " + this.refClient.getName() + ":\n" +
                "\tVida: " + lifePercentage + "%\n" +
                "\tCasillas vivas: " + totalAlive + "\n" +
                "\tCasillas muertas: " + totalDead;

        this.warriors.forEach(warrior -> {
            Label name = new Label("Luchador: " + warrior.name);
            int tem = warrior.getSumArmyLife();
            int tem2 = warrior.getNumArmy();
            int tem3 = warrior.getNumArmyAlive();
            int warriorLife = 0;
            if (tem2 != 0) {
                warriorLife = tem / tem2;
            }
            Label warriorLifeLbl = new Label("Vida: " + warriorLife + "%");
            Label warriorTotalLbl = new Label("Total: " + tem2);
            Label warriorAliveLbl = new Label("Vivos: " + tem3);
            Label warriorDeathLbl = new Label("Muertos: " + (tem2 - tem3));
            if (warrior.colorId == 0) {
                name.setStyle("-fx-text-fill:  #b5622f;");
            } else if (warrior.colorId == 1) {
                name.setStyle("-fx-text-fill:  #3074c1;");
            } else {
                name.setStyle("-fx-text-fill:  #2fc36a;");
            }
            VBox temBox2 = new VBox(name, warriorLifeLbl, warriorTotalLbl, warriorAliveLbl, warriorDeathLbl, new Separator());
            temBox2.setSpacing(10);
            temBox2.setStyle("-fx-alignment: CENTER; -fx-font-family: Ayuthaya;");
            this.detailsPaneList.add(temBox2);
        });

    }

    public void addWarriorToPane() {
        this.warriorsPaneList = this.warriorsPane.getChildren();
        this.warriorsPaneList.clear();
        this.warriorsPane.setHgap(280);
        this.warriorsPane.setStyle("-fx-alignment: TOP_CENTER");
        this.warriors.forEach(warrior -> {
            Label lblImage = new Label();
            try {
                Image image = new Image(ClientWindowController.class.getResourceAsStream(warrior.image));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(150);
                imageView.setFitWidth(150);
                lblImage.setGraphic(imageView);
            } catch (Exception ignored) {
                this.writeError("No se pudo insertar la imagen");
            }
            Label lblPercentage = new Label("Porcentaje:" + warrior.percentage);
            Label lblName = new Label(warrior.name);
            Label lblType = new Label(warrior.TYPE.toString());
            Label lblPoder = new Label("Fuerza: " + warrior.fuerza);
            Label lblResistencia = new Label("Resistencia: " + warrior.resistencia);
            Label lblSanidad = new Label("Sanidad: " + warrior.sanidad);
            VBox temBox = new VBox(lblImage, lblPercentage, lblName, lblType, lblPoder, lblResistencia, lblSanidad, new Separator());
            temBox.setSpacing(10);
            temBox.setStyle("-fx-alignment: CENTER; -fx-font-family: Ayuthaya");
            this.warriorsPaneList.add(temBox);
        });
    }

    public boolean isHasConnected() {
        return hasConnected;
    }

    public ArrayList<Warrior> getWarriors() {
        return warriors;
    }

    public ArrayList<Warrior> getEnemies() {
        return enemies;
    }

    public ObservableList<Node> getWarriorsPaneList() {
        return warriorsPaneList;
    }

    private void setUpEnemies() {
        this.enemies.add(new FishTelepathy(this));
        this.enemies.add(new KrakenRelease(this));
        this.enemies.add(new PoseidonTrident(this));
        this.enemies.add(new SeaThunders(this));
        this.enemies.add(new UnderseaFire(this));
        this.enemies.add(new WaveControl(this));
    }

    public void showPercentages() {
        if (this.percentageFlag) {
            BoardController.boardController.showPercentages();
            this.percentageFlag = false;
        } else {
            BoardController.boardController.hidePercentages();
            this.percentageFlag = true;
        }
    }

    public void showAlive() {
        if (this.aliveFlag) {
            BoardController.boardController.showAlive();
            this.aliveFlag = false;
        } else {
            BoardController.boardController.hideAlive();
            this.aliveFlag = true;
        }
    }

    public void showUnavailable() {
        if (this.unavailableFlag) {
            BoardController.boardController.showUnavailable();
            this.unavailableFlag = false;
        } else {
            BoardController.boardController.hideUnavailable();
            this.unavailableFlag = true;
        }
    }

    public String getSummary() {
        return this.stateSummary;
    }

    public void setResistance(boolean b) {
        this.isResistance = b;
        if (b) {
            this.warriors.forEach(warrior -> warrior.army.forEach(boardItem -> {
                if (boardItem.getLife() > 0) {
                    String report = "Recibió escudo de resistencia";
                    boardItem.addActivityReport(report);
                }
            }));
        }
    }

    public void clonePowers(int warriorId, String warriorType) {
        WarriorType warriorT = WarriorType.getType(warriorType); // poder para transferir
        try {
            for (Warrior enemy : enemies) {
                if (enemy.TYPE == warriorT) {
                    enemy.registerAttacks(this.warriors.get(warriorId));
                    this.writeSuccess("Se han transferido los poderes del luchador " + warriorType + " a su guerrero "
                            + this.warriors.get(warriorId).name + " por haberlo vencido");
                    return;
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public boolean isResistance() {
        return isResistance;
    }

    public boolean isWarriorPower() {
        return isWarriorPower;
    }

    public void setWarriorPower(boolean warriorPower) {
        isWarriorPower = warriorPower;
    }

    public void setEnemyPower(int enemyPower) {
        this.enemyPower = enemyPower;
    }

    public int getEnemyPower() {
        return enemyPower;
    }

    public void increaseSuccessAttacks() {
        this.successAttacks++;
    }

    public void increaseFailAttacks() {
        this.failAttacks++;
    }

    public void addAttackReport(String report) {
        this.attacksReport += "\t- " + report + "\n";
    }

    public String getAttacksReport() {
        return attacksReport;
    }

    public int getSuccessAttacks() {
        return successAttacks;
    }

    public int getFailAttacks() {
        return failAttacks;
    }

    public void setLastAttackerId(int lastAttacker) {
        this.lastAttacker = lastAttacker;
    }

    public void setLastAttackerWarriorId(int lastAttackerWarriorId) {
        this.lastAttackerWarriorId = lastAttackerWarriorId;
    }

    public void setKrakenControl(boolean b) {
        this.isKrakenControl = b;
    }

    public boolean isKrakenControl() {
        return isKrakenControl;
    }

    public boolean isHasInit() {
        return hasInit;
    }
}
