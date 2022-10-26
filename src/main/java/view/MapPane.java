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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MapPane extends Pane {
    private final Double scale;
    private final Map map;
    private Intersection selectedIntersection = null;
    private ArrayList<Intersection> validatedIntersections = new ArrayList<>();
    MapPane(String mapPath) throws ParserConfigurationException, IOException, SAXException {
        super();
        this.setDisable(true);
        this.setLayoutX(50);
        this.setLayoutY(50);
        this.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));

        Service service = new Service();
        this.map = service.loadMapFromXML(mapPath);
        Double[] coords = this.map.getMinMaxCoordinates();
        /* Print coords
        System.out.println("minY: " + coords[0]);
        System.out.println("minX: " + coords[1]);
        System.out.println("maxY: " + coords[2]);
        System.out.println("maxX: " + coords[3]);
        System.out.println("height: " + (coords[2]-coords[0]));
        System.out.println("width: " + (coords[3]-coords[1]));
        System.out.println(map.getListIntersection().values().size());
         */
        scale = 900 / (coords[3] - coords[1]);


        // Segments
        for (Segment segment : map.getListSegment()) {
            Double posX1 = (segment.getOrigin().getLongitude() - coords[1]) * scale;
            Double posY1 = 650 - (segment.getOrigin().getLatitude() - coords[0]) * scale;
            Double posX2 = (segment.getDestination().getLongitude() - coords[1]) * scale;
            Double posY2 = 650 - (segment.getDestination().getLatitude() - coords[0]) * scale;
            Line line = new Line(posX1, posY1, posX2, posY2);
            line.setStrokeWidth(2);
            line.setStroke(Color.WHITE);
            this.getChildren().add(line);
        }

        // Intersections
        for (Intersection intersection : map.getListIntersection().values()) {
            Double posX = (intersection.getLongitude() - coords[1]) * scale;
            Double posY = 650 - (intersection.getLatitude() - coords[0]) * scale;
            //System.out.println("posX: " + posX + " posY: " + posY);
            Circle point = new Circle(posX, posY, 3);
            point.setFill(Color.WHITE);
            if (Objects.equals(intersection.getId(), map.getWarehouse().getId())) {
                point.setFill(Color.RED);
                point.setRadius(4);
            }
            this.getChildren().add(point);
        }

        // Events
        final Intersection[] nearestIntersection = new Intersection[1];
        this.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                nearestIntersection[0] = drawNearestPointToCursor(event, nearestIntersection[0]);
            }
        });

        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(selectedIntersection);
                if (selectedIntersection != null) {
                    drawIntersection(selectedIntersection, Color.WHITE);
                }
                selectedIntersection = nearestIntersection[0];

                System.out.println("Selected intersection: " + selectedIntersection.getId());
                drawIntersection(selectedIntersection, Color.YELLOW);
            }
        });
    }

    public void drawIntersection(Intersection intersection, Color color) {
        Double posX = (intersection.getLongitude() - map.getMinMaxCoordinates()[1]) * scale;
        Double posY = 650 - (intersection.getLatitude() - map.getMinMaxCoordinates()[0]) * scale;
        Circle point = new Circle(posX, posY, 3);
        point.setFill(color);
        this.getChildren().add(point);
    }

    public Intersection drawNearestPointToCursor(javafx.scene.input.MouseEvent e, Intersection oldNearestIntersection) {
        double x = e.getX();
        double y = e.getY();
        double minDistance = 1000000000;
        Intersection nearestIntersection = null;
        for (Intersection intersection : map.getListIntersection().values()) {
            if (Objects.equals(intersection.getId(), map.getWarehouse().getId())) {
                continue;
            }
            Double posX = (intersection.getLongitude() - map.getMinMaxCoordinates()[1]) * scale;
            Double posY = 650 - (intersection.getLatitude() - map.getMinMaxCoordinates()[0]) * scale;
            double distance = Math.sqrt(Math.pow(posX - x, 2) + Math.pow(posY - y, 2));
            if (distance < minDistance) {
                minDistance = distance;
                nearestIntersection = intersection;
            }
        }

        if (oldNearestIntersection != nearestIntersection) {
            // If it's the first time the mouse enter the map
            if (oldNearestIntersection == null) {
                drawIntersection(nearestIntersection, Color.ORANGE);
                //System.out.println("First time");
                //System.out.println("Nearest intersection: " + nearestIntersection.getId() + "at x: "+ point.getLayoutX() + "; y: " + point.getLayoutY());
            } else {
                if (oldNearestIntersection == selectedIntersection) {
                    drawIntersection(oldNearestIntersection, Color.YELLOW);
                }
                else if (validatedIntersections.contains(oldNearestIntersection)) {
                    drawIntersection(oldNearestIntersection, Color.GREEN);
                }  else {
                    drawIntersection(oldNearestIntersection, Color.WHITE);
                }
                // Remove warning "nearestIntersection might be null"
                assert nearestIntersection != null;
                drawIntersection(nearestIntersection,  Color.ORANGE);
                //System.out.println("Old nearest intersection: " + oldNearestIntersection.getId() + "at x: "+ oldPoint.getCenterX() + "; y: " + oldPoint.getCenterY());
                //System.out.println("Nearest intersection: " + nearestIntersection.getId() + " at x: "+ point.getCenterX() + "; y: " + point.getCenterY());
            }
        }
        return nearestIntersection;
    }

    public ArrayList<Intersection> getValidatedIntersections() {
        return validatedIntersections;
    }

    public Map getMap() {
        return map;
    }

    public Double getScale() {
        return scale;
    }

    public Intersection getSelectedIntersection() {
        return selectedIntersection;
    }

    public void addValidatedIntersection(Intersection intersection) {
        validatedIntersections.add(intersection);
    }

    public void removeValidatedIntersection(Intersection intersection) {
        validatedIntersections.remove(intersection);
    }

    public void setSelectedIntersection(Intersection selectedIntersection) {
        this.selectedIntersection = selectedIntersection;
    }
}
