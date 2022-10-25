package view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SimpleExampleData {
    private SimpleStringProperty timeWindow;
    private SimpleStringProperty deliveryPoint;
    private SimpleStringProperty arrivalTime;

    public SimpleExampleData(String timeWindow, String deliveryPoint, String arrivalTime) {
        this.timeWindow = new SimpleStringProperty(timeWindow);
        this.deliveryPoint = new SimpleStringProperty(deliveryPoint);
        this.arrivalTime = new SimpleStringProperty(arrivalTime);
    }

    public static ObservableList<SimpleExampleData> InitData(){
        SimpleExampleData A = new SimpleExampleData("08:00 - 09:00", "DP 1", "08:25");
        SimpleExampleData B = new SimpleExampleData("08:00 - 09:00", "DP 2", "08:28");
        SimpleExampleData C = new SimpleExampleData("08:00 - 09:00", "DP 3", "08:47");
        SimpleExampleData D = new SimpleExampleData("08:00 - 09:00", "DP 4", "09:03");
        SimpleExampleData E = new SimpleExampleData("09:00 - 10:00", "DP 5", "09:12");
        SimpleExampleData F = new SimpleExampleData("09:00 - 10:00", "DP 6", "09:27");
        return FXCollections.observableArrayList(A, B, C, D, E, F);
    }

    public String getArrivalTime() {
        return arrivalTime.get();
    }

    public String getDeliveryPoint() {
        return deliveryPoint.get();
    }

    public String getTimeWindow() {
        return timeWindow.get();
    }
}

