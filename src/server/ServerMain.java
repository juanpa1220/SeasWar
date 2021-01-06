package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/serverWindow.fxml"));
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.setResizable(false);
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
