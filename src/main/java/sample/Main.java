package sample;

import classes.restclient.MyRestClientConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.logging.LogManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        ApplicationContext context = new AnnotationConfigApplicationContext(MyRestClientConfig.class);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root = (Parent)fxmlLoader.load();
//        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        Controller controller = fxmlLoader.<Controller>getController();
        controller.setContext(context);
        primaryStage.setTitle("InclPoll");
        primaryStage.setScene(new Scene(root, 990, 768));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
