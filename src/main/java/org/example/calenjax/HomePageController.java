package org.example.calenjax;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class HomePageController {
    @FXML
    private GridPane calendarWeek;

    public void initialize() {

        // Iterate through cells and add background buttons
        int numRows = calendarWeek.getRowConstraints().size();
        int numCols = calendarWeek.getColumnConstraints().size();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Button backgroundButton = createBackgroundButton(row, col);
                calendarWeek.getChildren().add(backgroundButton);
            }
        }

        // Appeler la fonction parse du ICSParserController pour obtenir les données du calendrier
        ICSParserController parser = new ICSParserController();
         parser.parse("");

        Button eventButton = new Button("Evenement");
        eventButton.getStyleClass().add("event-button");
        GridPane.setRowIndex(eventButton, 2);
        GridPane.setColumnIndex(eventButton, 1);
        GridPane.setRowSpan(eventButton, 6);
        GridPane.setFillHeight(eventButton, true);
        GridPane.setFillWidth(eventButton, true);
        eventButton.setMaxHeight(Double.MAX_VALUE);
        eventButton.setMaxWidth(Double.MAX_VALUE);
        calendarWeek.getChildren().add(eventButton);
    }

    private Button createBackgroundButton(int row, int col) {
        Button backgroundButton = new Button();
        backgroundButton.getStyleClass().add("background-button");
        GridPane.setRowIndex(backgroundButton, row);
        GridPane.setColumnIndex(backgroundButton, col);
        GridPane.setFillHeight(backgroundButton, true);
        GridPane.setFillWidth(backgroundButton, true);
        backgroundButton.setMaxHeight(Double.MAX_VALUE);
        backgroundButton.setMaxWidth(Double.MAX_VALUE);
        return backgroundButton;
    }

    private void addEventButton(Event event) {
        Button eventButton = new Button(event.getSummary());
        eventButton.getStyleClass().add("event-button");
        GridPane.setRowIndex(eventButton, event.getStartRow());
        GridPane.setColumnIndex(eventButton, event.getStartCol());
        GridPane.setRowSpan(eventButton, event.getRowSpan());
        GridPane.setFillHeight(eventButton, true);
        GridPane.setFillWidth(eventButton, true);
        eventButton.setMaxHeight(Double.MAX_VALUE);
        eventButton.setMaxWidth(Double.MAX_VALUE);

        // Ajoutez des événements ou des styles supplémentaires selon vos besoins
        // eventButton.setOnAction(e -> handleEventButtonClick(event));

        calendarWeek.getChildren().add(eventButton);
    }


}