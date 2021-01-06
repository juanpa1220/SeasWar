package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {
    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/clientWindow.fxml"));
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.setResizable(false);
        primaryStage.show();

        ClientMain.primaryStage = primaryStage;
    }


    public static void main(String[] args) {
        launch(args);
    }
}