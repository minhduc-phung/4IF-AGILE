package view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import model.DeliveryPoint;
import model.Map;
import observer.Observer;
import observer.Observable;

import java.util.ArrayList;

public class TextualView extends Pane implements Observer {
    private String text;
    private Map map;
    private Text selectedDelivery;
    private VBox textBox;

    private ArrayList<Text> deliveries;

    public TextualView (VBox textBox){
        this.textBox = textBox;
        deliveries = new ArrayList<Text>();

    }

    public void clearTheTextView(){
        textBox.getChildren().clear();
        selectedDelivery = null;
        deliveries.clear();
    }

    public void printDeliveryPoint (Map map, DeliveryPoint deliveryPoint){
        clearTheTextView();
        this.map = map;

    }

    @Override
    public void update(Observable o, Object arg) {

    }


}
