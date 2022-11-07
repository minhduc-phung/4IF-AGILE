package view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import observer.Observer;
import observer.Observable;

public class TextualView extends Label implements Observer {
    private String text;

    public TextualView(Window window){
        super();
        this.setStyle("ffx-border-color: black; -fx-border-width: 5");
        this.setTextAlignment(TextAlignment.CENTER);
        this.setAlignment(Pos.TOP_CENTER);
        window.getRoot().getChildren().add(this);
    }


    @Override
    public void update(Observable o, Object arg) {

    }


}
