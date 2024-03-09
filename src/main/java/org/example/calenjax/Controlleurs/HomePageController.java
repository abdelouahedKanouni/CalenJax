package org.example.calenjax.Controlleurs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.example.calenjax.Event;
import javafx.event.ActionEvent;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class HomePageController {
    public Button now;
    @FXML
    private GridPane calendarWeek;
    @FXML
    private GridPane calendarDay;
    private LocalDateTime actualDate;
    private String mode = "week";

    private List<Button> eventButtons = new ArrayList<>();

    public void initialize() {

        int numRows = calendarWeek.getRowConstraints().size();
        int numCols = calendarWeek.getColumnConstraints().size();

        for (int row = 1; row < numRows; row++) {
            for (int col = 1; col < numCols; col++) {
                Button backgroundButton = createBackgroundButton(row, col);
                calendarWeek.getChildren().add(backgroundButton);
            }
        }

        this.actualDate = LocalDateTime.now();


        ICSParserController parser = new ICSParserController();
        List<Event> events = parser.parse("", this.mode, actualDate);

        for ( Event e : events) {
            addEventButton(e);
        }

    }

    @FXML
    private void handleButtonActionRefresh(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        refreshDataCalendar(clickedButton.getId());
    }

    public void refreshDataCalendar(String when) {
        calendarWeek.getChildren().removeAll(eventButtons);
        eventButtons.clear();

        ICSParserController parser = new ICSParserController();
        List<Event> events = new ArrayList<>();

        if (this.mode.equals("week")){
            switch (when) {
                case "previous" -> {
                    this.actualDate = this.actualDate.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));;
                    events = parser.parse("", this.mode, this.actualDate);
                }
                case "now" -> {
                    this.actualDate =  LocalDateTime.now();
                    events = parser.parse("", this.mode, this.actualDate);
                }
                case "after" -> {
                    this.actualDate = this.actualDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));;
                    events = parser.parse("", this.mode, this.actualDate);
                }
            }
        }

        for ( Event e : events) {
            addEventButton(e);
        }
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

    private void addEventButton(Event e) {
        Button eventButton = new Button(e.getSummary() + e.getDescription());
        if (e.getSummary().equals("Férié")){
            eventButton.getStyleClass().add("event-button-gray");
        }
        else {
            eventButton.getStyleClass().add("event-button");

        }
        GridPane.setRowIndex(eventButton, e.getStartRow());
        GridPane.setColumnIndex(eventButton, e.getStartCol());
        GridPane.setRowSpan(eventButton, e.getRowSpan());
        GridPane.setFillHeight(eventButton, true);
        GridPane.setFillWidth(eventButton, true);
        eventButton.setMaxHeight(Double.MAX_VALUE);
        eventButton.setMaxWidth(Double.MAX_VALUE);
        calendarWeek.getChildren().add(eventButton);


        // Ajoutez des événements ou des styles supplémentaires selon vos besoins
        // eventButton.setOnAction(e -> handleEventButtonClick(event));

        eventButtons.add(eventButton);
    }


}