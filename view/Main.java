package view;

import controller.GraphController;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Arc;
import model.Graph;
import model.Node;
import controller.GraphController;


public class Main extends Application {
    public static double WINDOW_WIDTH = 900;
    public static double WINDOW_HEIGHT = 1600;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Graph editor");
        primaryStage.setScene(new Scene(new MainWindow(primaryStage).getVBox()));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
