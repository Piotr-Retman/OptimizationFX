package pl.zut.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(new MyController());
        Pane p = fxmlLoader.load(getClass().getResource("sample.fxml").openStream());
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
