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

    /**
     * this is the method that launch the javaFX application
     * @param args
     */
    public static void main(String[] args) {
        launch(args);

    }

    /**
     * Displays everything inside the primary window of our JavaFX application, and shows it to the user.
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(TITLE);
        stage.setResizable(true);
        Scene scene = new Scene(controller.getWindow(), WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}
