package view;

import controller.Controller;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.scene.layout.Pane;
import model.Courier;
import model.DeliveryPoint;
import model.User;
import observer.Observer;
import observer.Observable;

/**
 *this class define the table view, update and delete the table view data and define the layout of the window.
 */
public class TextualView extends Pane implements Observer {

    /**
     * this attribute is defined to handle Mouse events
     */
    private MouseListener mouseListener;
    /**
     * This attribute represents the table view
     */
    private TableView<Map> tableView;
    /**
     * This attribute represents the text in the index column of the table view
     */
    public static final String IndexMapKey = "Index";
    /**
     * This attribute represents the text in the TimeWindow column of the table view
     */
    public static final String TimeWindowMapKey = "TimeWindow";
    /**
     * this attribute represents the text in Estimatedarrival column of the table view
     */
    public static final String EstArrivalMapKey = "EstArrival";
    /**
     * This attribute represents the delivery point that will be selected in the table view
     */
    private DeliveryPoint selectedDeliveryPoint;
    /**
     * This attribute represents the width of the table view
     */
    private int width = 350;
    /**
     * This attribute represents the height of the table view
     */
    private int height = 250;
    public TextualView (Window window, Controller controller){
        super();
        setLayoutX(1075);
        setLayoutY(375);
        setPrefSize(width, height);
        TableColumn<Map, String> indexColumn = new TableColumn<>("Number");
        indexColumn.setMinWidth(25);
        indexColumn.setCellValueFactory(new MapValueFactory<>(IndexMapKey));

        TableColumn<Map, String> timeWindowColumn = new TableColumn<>("Time-window");
        timeWindowColumn.setMinWidth(150);
        timeWindowColumn.setCellValueFactory(new MapValueFactory<>(TimeWindowMapKey));

        TableColumn<Map, String> estArrivalTimeColumn = new TableColumn<>("Estimated arrival");
        estArrivalTimeColumn.setMinWidth(125);
        estArrivalTimeColumn.setCellValueFactory(new MapValueFactory<>(EstArrivalMapKey));

        tableView = new TableView<>();
        tableView.getColumns().addAll(indexColumn, timeWindowColumn, estArrivalTimeColumn);
        tableView.setEditable(true);
        tableView.setPrefSize(width, height);
        tableView.getSortOrder().add(estArrivalTimeColumn);
        mouseListener = new MouseListener(controller);
        // get the selected row
        tableView.setRowFactory( tv -> {
            TableRow<Map> row = new TableRow<>();
            // color the row if the estimated arrival time is after the time window
            BooleanBinding late = new BooleanBinding() {
                {
                    super.bind(row.itemProperty());
                }
                @Override
                protected boolean computeValue() {
                    if (row.getItem() == null) {
                        return false;
                    }
                    String timeWindowString = row.itemProperty().get().get(TimeWindowMapKey).toString();
                    Integer timeWindow = controller.getUser().getTimeWindows().get(timeWindowString);
                    if (Objects.equals(row.itemProperty().get().get(EstArrivalMapKey).toString(), "")) {
                        return false;
                    }
                    String estArrivalHourString = row.itemProperty().get().get(EstArrivalMapKey).toString().substring(0, 2);
                    Integer estArrivalHour = Integer.parseInt(estArrivalHourString);
                    return estArrivalHour > timeWindow;
                }
            };
            row.styleProperty().bind(Bindings.when(late).then("-fx-background-color: #ff9aa2;").otherwise(""));
            row.setOnMouseClicked(mouseListener);
            return row ;
        });

        getChildren().add(tableView);
        window.getChildren().add(this);
    }

    /**
     * this method put data into the table view
     *@param user is the one who use this application
     *@param idCourier id of a courier for a specific user
     *@return all the data that should be put in the table view
     */
    private ObservableList<Map> generateData(User user, Long idCourier) {
        ObservableList<Map> data = FXCollections.observableArrayList();
        Courier c = user.getCourierById(idCourier);
        List<DeliveryPoint> deliveryPoints = new ArrayList<>(c.getCurrentDeliveryPoints());
        for (int i = 1; i < deliveryPoints.size(); i++) {
            DeliveryPoint dp = deliveryPoints.get(i);
            Map<String, String> dataRow = new HashMap<>();
            String index = Integer.toString(i);
            String timeWindow = dp.getTimeWindowString();
            String estArrival = "";
            if (dp.getEstimatedDeliveryTime()!= null) {
                estArrival = dp.getEstimatedDeliveryTimeString();
            }
            dataRow.put(IndexMapKey, index);
            dataRow.put(TimeWindowMapKey, timeWindow);
            dataRow.put(EstArrivalMapKey, estArrival);
            data.add(dataRow);
        }
        return data;
    }

    /**
     *this method update the data of a selected item in the table view
     *@param user is the one who use this application
     *@param idCourier id of a courier for a specific user
     */
    public void updateData(User user, Long idCourier) {
        tableView.setItems(generateData(user, idCourier));
    }

    /**
     * this method is the getter for the selectedDeliveryPoint attribute
     * @return the delivery point selected by the user
     * @see view.TextualView#selectedDeliveryPoint
     */
    public DeliveryPoint getSelectedDeliveryPoint() {
        return selectedDeliveryPoint;
    }

    /**
     * this method is the setter for the selectedDeliveryPoint attribute, it updates the selected Delivery point.
     * @param dp the new value of the selected delivery point
     * @see view.TextualView#selectedDeliveryPoint
     */
    public void setSelectedDeliveryPoint(DeliveryPoint dp) {
        this.selectedDeliveryPoint = dp;
    }

    /**
     * this method cancel the selection of a delivery point
     */
    public void clearSelection() {
        tableView.getSelectionModel().clearSelection();
        selectedDeliveryPoint = null;
    }

    public TableView<Map> getTableView() {
        return tableView;
    }

    @Override
    public void update(Observable o, Object arg) {

    }


}
