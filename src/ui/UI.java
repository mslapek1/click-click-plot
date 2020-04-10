package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class UI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static String applicationName() {
        return "Click-Click Plot";
    }

    
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Ui2.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
        stage.setTitle(applicationName());
        try {
            stage.getIcons().add(new Image(UI.class.getResourceAsStream("logo.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
