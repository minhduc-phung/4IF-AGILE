package app;

import controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Controller controller = new Controller();
    private final int WINDOW_WIDTH = 1500;
    private final int WINDOW_HEIGHT = 900;
    private final String TITLE = "Delivery Planner";
    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(TITLE);
//        this.setHeight(WINDOW_HEIGHT);
//        this.setTitle("Delivery Planner");
//        this.setResizable(false);
//        this.setOnCloseRequest(e -> System.exit(0));
        Scene scene = new Scene(controller.getWindow(), WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}
