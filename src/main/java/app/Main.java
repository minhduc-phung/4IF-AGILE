package app;

import controller.Controller;
import static java.awt.event.KeyEvent.VK_F1;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.User;
import view.Window;

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
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().getCode() == 10 ) {
                    System.out.println("F1");
                }
            }
        });        
        stage.setScene(scene);
        stage.show();
    }
}
