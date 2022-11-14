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
import java.util.Objects;

public class GraphicalView extends Pane implements Observer {
    private int viewHeight = 700;
    private final int viewWidth = 1000;
    private double scale;
    private Double minLongitude;
    private Double minLatitude;
    private Double maxLongitude;
    private Double maxLatitude;

    private Intersection selectedIntersection = null;
    private Intersection hoveredIntersection = null;

    public GraphicalView(Window window, Controller controller) {
        super();
        this.setLayoutX(25);
        this.setLayoutY(25);
        this.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
        this.setPrefSize(viewWidth, viewHeight);
        MouseListener mouseListener = new MouseListener(controller);
        //KeyboardListener keyboardListener = new KeyboardListener(controller);
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
            this.getChildren().add(new Circle(posX, posY, 3.5, Color.WHITE));
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

    public void paintIntersection(Intersection intersection, Color color, Map map) {
        Double[] coords = map.getMinMaxCoordinates();
        Double posX = (intersection.getLongitude() - coords[1]) * scale;
        Double posY = viewHeight - (intersection.getLatitude() - coords[0]) * scale;
        this.getChildren().add(new Circle(posX, posY, 3.5, color));
    }

    public void paintSegment(Segment segment, Color color, Map map) {
        Double[] coords = map.getMinMaxCoordinates();
        Double posX1 = (segment.getOrigin().getLongitude() - coords[1]) * scale;
        Double posY1 = viewHeight - (segment.getOrigin().getLatitude() - coords[0]) * scale;
        Double posX2 = (segment.getDestination().getLongitude() - coords[1]) * scale;
        Double posY2 = viewHeight - (segment.getDestination().getLatitude() - coords[0]) * scale;

        Line line = new Line(posX1, posY1, posX2, posY2);
        line.setStrokeWidth(2);
        line.setStroke(color);
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
            if (!Objects.equals(deliveryPoint.getId(), map.getWarehouse().getId())) paintIntersection(deliveryPoint, Color.BLUE, map);
        }
    }
}
