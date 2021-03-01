package airhockey;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GameApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Air Hockey Ultimate");
        Scene scene = new Scene(FXMLLoader.load(GameApp.class.getResource("Game.fxml")));
        primaryStage.setScene(scene);

        primaryStage.show();
    }


    public static void main(String[] args) {
        GameApp.launch(args);
    }
}

