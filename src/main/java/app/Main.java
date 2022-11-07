package app;

import controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.Window;

public class Main extends Application {
    Controller controller = new Controller();
    private Window window = new Window(controller);
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
        stage.setScene(new Scene(window, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }
}
