package client.control;

import client.model.Client;
import client.model.Parser;
import client.model.Warrior;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.ArrayList;

public class ClientWindowController {
    public TextField commandInput;
    public TextFlow commandOutput;
    public AnchorPane anchorBoardPane;
    public Label lblTurn;
    public TilePane warriorsPane;
    private ObservableList tilePaneList;
    private Client refClient;
    public Parser parser;
    private boolean hasConnected = false;
    private ArrayList<Warrior> warriors = new ArrayList<>();

    public ClientWindowController() {
        this.parser = new Parser();

        this.parser.registerCommand("connect -s <arg1> -p <arg2> -n <arg3>", args -> {
            String server = args[0];
            int port = Integer.parseInt(args[1]);
            String name = args[2];
            if (!this.hasConnected) {
                Client client = new Client(this);
                this.refClient.setName(name);
                boolean connected = client.connect(server, port);
                if (connected) {
                    this.hasConnected = true;
                    try {
                        this.initBoard();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Text t1 = new Text(">> Conección exitosa\n");
                    t1.setStyle("-fx-fill: #008000;");
                    this.commandOutput.getChildren().addAll(t1);
                } else {
                    Text t1 = new Text(">> Conección denegada\n");
                    t1.setStyle("-fx-fill: RED;");
                    this.commandOutput.getChildren().addAll(t1);
                }
            } else {
                Text t2 = new Text(">> Ya se ha conectado\n");
                t2.setStyle("-fx-fill: RED;");
                this.commandOutput.getChildren().addAll(t2);
            }
        });

        this.parser.registerCommand("chat -m <arg1...>", args -> {
            String message = args[0];
            try {
                this.refClient.clientThread.writer.writeInt(2);
                this.refClient.clientThread.writer.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.parser.registerCommand("chat -p <arg1> -m <arg2...>", args -> {
            String privateTo = args[0];
            String message = args[1];
            try {
                this.refClient.clientThread.writer.writeInt(3);
                this.refClient.clientThread.writer.writeUTF(privateTo);
                this.refClient.clientThread.writer.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.parser.registerCommand("luchador -n <arg1> -p <arg2> -t <arg3> -f <arg4> -r <arg5> -s <arg6> -i <arg7>", args -> {
            String name = args[0];
            int percentage = Integer.parseInt(args[1]);
            String type = args[2];
            int fuerza = Integer.parseInt(args[3]);
            int resistencia = Integer.parseInt(args[4]);
            int sanidad = Integer.parseInt(args[5]);
            String image = args[6];
            Warrior warrior = new Warrior(name, percentage, type, fuerza, resistencia, sanidad, image);
            warriors.add(warrior);
            boolean approved = this.checkWarriors();
            if (approved) {
                System.out.println("ok");
//                try {
//                    this.refClient.clientThread.writer.writeInt(3);
////                this.refClient.clientThread.writer.writeUTF();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

        });

    }

    private boolean checkWarriors() {
        if (this.warriors.size() == 3) {
            int totalPercentage = 100;
            int totalAttributes = 3 * 100 + 3 * 75 + 3 * 50;
            for (Warrior warrior : warriors) {
                totalPercentage -= warrior.percentage;
                totalAttributes -= warrior.fuerza;
                totalAttributes -= warrior.resistencia;
                totalAttributes -= warrior.sanidad;
            }
            if (totalAttributes == 0 && totalPercentage == 0) {
                addWarriorToPane();
                return true;
            } else {
                this.writeError("La suma de los porcentajes no cierra, intentelo de nuevo.");
                warriors.clear();
                tilePaneList.clear();
                return false;
            }
        } else if (this.warriors.size() > 3) {
            this.writeError("Sólo se permiten tres luchadores");
            return false;
        } else {
            addWarriorToPane();
            return true;
        }
    }

    private void addWarriorToPane() {
        tilePaneList = this.warriorsPane.getChildren();
        tilePaneList.clear();
        warriorsPane.setHgap(280);
        warriorsPane.setStyle("-fx-alignment: TOP_CENTER");
//        warriorsPane.setVgap(20);
        this.warriors.forEach(warrior -> {
            Label lblImage = new Label();
            try {
                Image image = new Image(ClientWindowController.class.getResourceAsStream(warrior.image));
                lblImage.setGraphic(new ImageView(image));
            } catch (Exception ignored) {
            }
            Label lblPercentage = new Label("Porcentaje:" + warrior.percentage);
            Label lblName = new Label(warrior.name);
            Label lblType = new Label(warrior.type);
            Label lblPoder = new Label("Fuerza: " + warrior.fuerza);
            Label lblResistencia = new Label("Resistencia: " + warrior.resistencia);
            Label lblSanidad = new Label("Sanidad: " + warrior.sanidad);
            VBox temBox = new VBox(lblImage, lblPercentage, lblName, lblType, lblPoder, lblResistencia, lblSanidad, new Separator());
            temBox.setSpacing(10);
            temBox.setStyle("-fx-alignment: CENTER; -fx-font-family: Ayuthaya");
            tilePaneList.add(temBox);
        });
    }

    private void initBoard() throws IOException {
        AnchorPane boardPane = FXMLLoader.load(getClass().getResource("../view/board.fxml"));
        BoardController boardController = BoardController.getInstance();
        boardController.createBoard();
        this.anchorBoardPane.getChildren().setAll(boardPane);
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

        try {
            this.parser.parse(input);
        } catch (IllegalArgumentException e) {
            this.writeError("Comando inválido");

        }

    }

    public void printTurn(String turn) {
        this.lblTurn.setText("Turno: " + turn);
    }

    public void writeMassage(String message, boolean isPublic) {
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
}
