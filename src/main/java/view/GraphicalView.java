package view;

import controller.Service;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import model.Intersection;
import model.Map;
import model.Segment;
import model.User;
import observer.Observable;
import observer.Observer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class GraphicalView extends Pane implements Observer {
    private int viewHeight = 700;
    private final int viewWidth = 1050;
    private double scale;
    private MouseListener mouseListener;
    protected Intersection selectedIntersection = null;
    private ArrayList<Intersection> validatedIntersections = new ArrayList<>();


    public GraphicalView(Window window) {
        super();
        this.setLayoutX(20);
        this.setLayoutY(20);
        this.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
        this.setPrefSize(viewWidth, viewHeight);

        // draw a circle in the middle
        Circle circle = new Circle(50);
        circle.setFill(Color.RED);
        circle.setLayoutX(viewWidth / 2);
        circle.setLayoutY(viewHeight / 2);
        this.getChildren().add(circle);

        // Events
        this.setOnMouseMoved(mouseListener);
        this.setOnMouseClicked(mouseListener);

        window.getChildren().add(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub

    }

    public void drawMap(Map map){

        Double[] coords = map.getMinMaxCoordinates();
        //Print coords
        System.out.println("minY: " + coords[0]);
        System.out.println("minX: " + coords[1]);
        System.out.println("maxY: " + coords[2]);
        System.out.println("maxX: " + coords[3]);
        System.out.println("height: " + (coords[2]-coords[0]));
        System.out.println("width: " + (coords[3]-coords[1]));
        System.out.println(map.getListIntersection().values().size());


        scale = viewWidth / (coords[3] - coords[1]);
        viewHeight = (int) Math.ceil((coords[2] - coords[0]) * scale);


        // Segments
        for (Segment segment : map.getListSegment()) {
            System.out.println("Segment: " + segment.getName());
            Double posX1 = (segment.getOrigin().getLongitude() - coords[1]) * scale;
            Double posY1 = viewHeight - (segment.getOrigin().getLatitude() - coords[0]) * scale;
            Double posX2 = (segment.getDestination().getLongitude() - coords[1]) * scale;
            Double posY2 = viewHeight - (segment.getDestination().getLatitude() - coords[0]) * scale;
            // debug: print out the coordinates
            System.out.println("posX1: " + posX1);
            System.out.println("posY1: " + posY1);
            System.out.println("posX2: " + posX2);
            System.out.println("posY2: " + posY2);

            Line line = new Line(posX1, posY1, posX2, posY2);
            line.setStrokeWidth(2);
            line.setStroke(Color.WHITE);
            this.getChildren().add(line);
        }

        // Intersections
        for (Intersection intersection : map.getListIntersection().values()) {
            System.out.println("Intersection: " + intersection.getId());
            Double posX = (intersection.getLongitude() - coords[1]) * scale;
            Double posY = viewHeight - (intersection.getLatitude() - coords[0]) * scale;
            System.out.println("posX: " + posX + " posY: " + posY);
            Circle point = new Circle(posX, posY, 3);
            point.setFill(Color.WHITE);
            if (Objects.equals(intersection.getId(), map.getWarehouse().getId())) {
                point.setFill(Color.RED);
                point.setRadius(4);
            }
            this.getChildren().add(point);
        }
    }

    public void paintIntersection(Intersection intersection, Color color, Map map) {
        Double[] coords = map.getMinMaxCoordinates();
        Double posX = (intersection.getLongitude() - coords[1]) * scale;
        Double posY = viewHeight - (intersection.getLatitude() - coords[0]) * scale;
        Circle point = new Circle(posX, posY, 3);
        point.setFill(color);
        this.getChildren().add(point);
    }

    public void debug(){
        //print out every children of the pane
        for (int i = 0; i < this.getChildren().size(); i++) {
            System.out.println(this.getChildren().get(i));
        }
    }


}
