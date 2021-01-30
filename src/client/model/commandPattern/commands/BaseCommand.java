package client.model.commandPattern.commands;

import client.control.ClientWindowController;
import client.model.commandPattern.ICommand;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public abstract class BaseCommand implements ICommand {

    @Override
    public abstract String getCommandName();

    @Override
    public abstract void execute(String[] args, TextFlow out, ClientWindowController refWindowController) throws IOException;

    public void writeError(TextFlow commandOutput, String message) {
        Text text = new Text(">> " + message + "\n");
        text.setStyle(" -fx-fill: #ff0000;");
        commandOutput.getChildren().add(text);
    }

    public void writeSuccess(TextFlow commandOutput, String message) {
        Text text = new Text(">> " + message + "\n");
        text.setStyle("-fx-fill: #008000;");
        commandOutput.getChildren().addAll(text);
    }
}
