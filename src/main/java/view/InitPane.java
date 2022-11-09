//package view;
//
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.layout.Pane;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Font;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Paths;
//
//public class InitPane extends Pane {
//    public InitPane(Stage stage){
//        super();
//        this.setPrefSize(1500, 900);
//        this.setLayoutX(0);
//        this.setLayoutY(0);
//
//        // The frame
//        Rectangle rectangle = new Rectangle(1500, 900);
//        rectangle.setStroke(Color.BLACK);
//        rectangle.setStrokeWidth(6);
//        rectangle.setFill(Color.WHITE);
//        this.getChildren().add(rectangle);
//
//        // Label
//        Label label = new Label("Welcome! Please open a map file to begin.");
//        // Center the label manually
//        label.layoutXProperty().bind(this.widthProperty().subtract(label.widthProperty()).divide(2));
//        // Change the font size
//        label.setFont(new Font(30));
//        // Bold
//        label.setStyle("-fx-font-weight: bold");
//        label.setLayoutY(70);
//
//
//
//        // Buttons
//        Button exitButton = new Button("Exit");
//        exitButton.setLayoutX(120);
//        exitButton.setLayoutY(800);
//        exitButton.setPrefSize(200,50);
//        exitButton.setStyle("-fx-background-color: #ff9aa2; ");
//        exitButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                stage.close();
//            }
//        });
//
//        Button loadButton = new Button("Load");
//        loadButton.setLayoutX(1180);
//        loadButton.setLayoutY(800);
//        loadButton.setPrefSize(200,50);
//        loadButton.setStyle("-fx-background-color: #b5ead7; ");
//        loadButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//
//                // Map
//                Main.mapPane = new MapPane();
//                try {
//                    Main.setPanesVisible();
//                } catch (ParserConfigurationException | SAXException | IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//
//
//
//        // FileChooser
//        Button chooseFileButton = new Button("Browse...");
//        chooseFileButton.layoutXProperty().bind(this.widthProperty().subtract(chooseFileButton.widthProperty()).divide(2));
//        chooseFileButton.setLayoutY(300);
//        chooseFileButton.setPrefSize(650,45);
//        chooseFileButton.setStyle("-fx-background-color: #C6D6D6; ");
//        chooseFileButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                final FileChooser fileChooser = new FileChooser();
//                fileChooser.setTitle("Load a map");
//                String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
//                fileChooser.setInitialDirectory(new File(currentPath));
//                final File selectedFile = fileChooser.showOpenDialog(stage);
//
//                if (selectedFile != null) {
//                    if (selectedFile.exists()) {
//                        Main.mapXMLPath = selectedFile.getPath();
//                        chooseFileButton.setText(Main.mapXMLPath);
//                    }
//                }
//            }
//        });
//
//
//        this.getChildren().add(exitButton);
//        this.getChildren().add(loadButton);
//        this.getChildren().add(chooseFileButton);
//        this.getChildren().add(label);
//    }
//
//}
