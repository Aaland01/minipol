package monopol;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MonopolApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.getIcons().add(new Image(MonopolApp.class.getResourceAsStream("Icon.png")));
        primaryStage.setTitle("MINIPOL");
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("Monopol.fxml"))));
        primaryStage.show();

    }
}
