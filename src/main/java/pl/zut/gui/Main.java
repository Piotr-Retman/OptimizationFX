package pl.zut.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

        FXMLLoader fxmlLoader = new FXMLLoader();
        MyController myController = new MyController();

        fxmlLoader.setController(myController);
        Pane p = fxmlLoader.load(getClass().getResource("/fxml/main.fxml").openStream());
        Scene scene = new Scene(p, 1366, 768);

//        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Optimization");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
