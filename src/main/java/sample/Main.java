package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.LogManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("InclPoll");
        primaryStage.setScene(new Scene(root, 990, 768));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
