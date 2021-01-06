package server.control;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import server.model.Server;

public class ServerWindowController {
    public TextArea textArea;
    public Button btnStart;
    public ScrollPane scrollPane;
    public AnchorPane anchorPane;

    public void addLogMessage(String message) {
        this.textArea.appendText(message + "\n");
    }

    public void startServer() {
        this.scrollPane.setDisable(false);
        this.anchorPane.getChildren().remove(btnStart);
        Server server = new Server(this);
        server.start();
    }
}
