package view;

import controller.Controller;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import model.*;
import observer.Observable;
import observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static view.GraphicalView.IntersectionType.DP;

public class GraphicalView extends Pane implements Observer {
    public enum IntersectionType { UNSELECTED, SELECTED, LATE, ON_TIME, HOVERED, DP }
    private int viewHeight = 700;
    private final int viewWidth = 1000;
    private double scale;
    private Double minLongitude;
    private Double minLatitude;
    private Double maxLongitude;
    private Double maxLatitude;
    private List<Circle> circles = new ArrayList<>();

    private Intersection selectedIntersection = null;
    private Intersection hoveredIntersection = null;

    public GraphicalView(Window window, Controller controller) {
        super();
        this.setLayoutX(25);
        this.setLayoutY(25);
        this.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
        this.setPrefSize(viewWidth, viewHeight);
        MouseListener mouseListener = new MouseListener(controller);
        // Events
        this.setOnMouseMoved(mouseListener);
        this.setOnMouseClicked(mouseListener);
        this.setOnMouseExited(mouseListener);
        window.getChildren().add(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub
    }

    public void drawMap(Map map){
        this.getChildren().clear();
        minLatitude = map.getMinLatitude();
        minLongitude = map.getMinLongitude();
        maxLatitude = map.getMaxLatitude();
        maxLongitude = map.getMaxLongitude();
        // Calculate the height of the map to fit every intersection
        scale = viewWidth / (maxLongitude - minLongitude);
        viewHeight = (int) Math.ceil((maxLatitude - minLatitude) * scale);
        this.setPrefSize(viewWidth, viewHeight);


        // Segments
        for (Segment segment : map.getListSegment()) {
            Double posX1 = (segment.getOrigin().getLongitude() - minLongitude) * scale;
            Double posY1 = viewHeight - (segment.getOrigin().getLatitude() - minLatitude) * scale;
            Double posX2 = (segment.getDestination().getLongitude() - minLongitude) * scale;
            Double posY2 = viewHeight - (segment.getDestination().getLatitude() - minLatitude) * scale;

            Line line = new Line(posX1, posY1, posX2, posY2);
            line.setStrokeWidth(2);
            line.setStroke(Color.WHITE);
            this.getChildren().add(line);
        }

        // Intersections
        for (Intersection intersection : map.getListIntersection().values()) {
            Double posX = (intersection.getLongitude() - minLongitude) * scale;
            Double posY = viewHeight - (intersection.getLatitude() - minLatitude) * scale;
            Circle c = new Circle(posX, posY, 3.5, Color.WHITE);
            c.setId(intersection.getId().toString());
            circles.add(c);
            this.getChildren().add(c);
        }
        Intersection warehouse = map.getWarehouse();
        Double posX = (warehouse.getLongitude() - minLongitude) * scale;
        Double posY = viewHeight - (warehouse.getLatitude() - minLatitude) * scale;
        this.getChildren().add(new Circle(posX, posY, 6, Color.PURPLE));
    }

    public void clearSelection(){
        if(selectedIntersection != null){
            selectedIntersection = null;
        }
    }

    public void paintIntersection(Intersection intersection, IntersectionType type){
        for (Circle circle : circles) {
            if (Objects.equals(circle.getId(), intersection.getId().toString())) {
                switch (type){
                    case UNSELECTED:
                        circle.setFill(Color.WHITE);
                        circle.setRadius(3.5);
                        break;
                    case SELECTED:
                        circle.setFill(Color.BROWN);
                        circle.setRadius(5);
                        break;
                    case LATE:
                        circle.setFill(Color.RED);
                        circle.setRadius(7);
                        break;
                    case ON_TIME:
                        circle.setFill(Color.GREEN);
                        circle.setRadius(7);
                        break;
                    case HOVERED:
                        circle.setFill(Color.ORANGE);
                        circle.setRadius(5);
                        break;
                    case DP:
                        circle.setFill(Color.BLUE);
                        circle.setRadius(5);
                        break;
                }
                circle.toFront();
                break;
            }
        }
    }

    public void paintSegment(Segment segment, Color color, Map map) {
        Double[] coords = map.getMinMaxCoordinates();
        Double startX = (segment.getOrigin().getLongitude() - coords[1]) * scale;
        Double startY = viewHeight - (segment.getOrigin().getLatitude() - coords[0]) * scale;
        Double endX = (segment.getDestination().getLongitude() - coords[1]) * scale;
        Double endY = viewHeight - (segment.getDestination().getLatitude() - coords[0]) * scale;
        Integer arrowLength = 8;
        double slope = (startY - endY) / (startX - endX);
        double lineAngle = Math.atan(slope);
        double arrowAngle = startX > endX ? Math.toRadians(45) : -Math.toRadians(225);


        Line line = new Line(startX, startY, endX, endY);
        line.setStrokeWidth(2);
        line.setStroke(color);

        Line arrow1 = new Line();
        arrow1.setStartX(line.getEndX());
        arrow1.setStartY(line.getEndY());
        arrow1.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle - arrowAngle));
        arrow1.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle - arrowAngle));
        arrow1.setStrokeWidth(2);
        arrow1.setStroke(color);

        Line arrow2 = new Line();
        arrow2.setStartX(line.getEndX());
        arrow2.setStartY(line.getEndY());
        arrow2.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle + arrowAngle));
        arrow2.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle + arrowAngle));
        arrow2.setStrokeWidth(2);
        arrow2.setStroke(color);

        this.getChildren().add(arrow1);
        this.getChildren().add(arrow2);
        this.getChildren().add(line);
    }

    public Intersection getSelectedIntersection() {
        return selectedIntersection;
    }

    public double getScale() {
        return scale;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public Double getMinLatitude() {
        return minLatitude;
    }

    public Double getMaxLatitude() {
        return maxLatitude;
    }

    public Double getMaxLongitude() {
        return maxLongitude;
    }

    public Double getMinLongitude() {
        return minLongitude;
    }

    public Intersection getHoveredIntersection() {
        return hoveredIntersection;
    }

    public void setHoveredIntersection(Intersection hoveredIntersection) {
        this.hoveredIntersection = hoveredIntersection;
    }

    public void setSelectedIntersection(Intersection selectedIntersection) {
        this.selectedIntersection = selectedIntersection;
    }

    public void updateMap(Map map, Courier courier) {
    	this.getChildren().clear();
    	this.drawMap(map);
        for (DeliveryPoint deliveryPoint : courier.getCurrentDeliveryPoints()) {
            if (!Objects.equals(deliveryPoint.getId(), map.getWarehouse().getId())) paintIntersection(deliveryPoint, DP);
        }
    }
}
