package app;

import controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Controller controller = new Controller();
    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        String TITLE = "Delivery Planner";
        stage.setTitle(TITLE);
        int WINDOW_HEIGHT = 900;
        int WINDOW_WIDTH = 1500;
        stage.setScene(new Scene(controller.getWindow(), WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }
}
