package org.example.calenjax.Controlleurs;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.calenjax.Event;
import javafx.event.ActionEvent;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomePageController {
    public Button now;
    @FXML
    private GridPane calendarWeek;
    @FXML
    private GridPane calendarDay;
    @FXML
    private GridPane calendarMonth;
    private LocalDateTime actualDate;
    private String mode = "week";
    private String filePath = "C:/Users/romai/Documents/M1/semestre2/serveur_app/CalenJax/src/main/resources/org/example/calenjax/test.ics";
    @FXML
    private Label labelDay;
    @FXML
    private ImageView homeImage;
    private Stage primaryStage;

    private List<Button> eventButtons = new ArrayList<>();

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        homeImage.fitWidthProperty().bind(this.primaryStage.widthProperty());
    }
    public void initialize() {

        calendarDay.setVisible(false);
        calendarDay.setManaged(false);
        calendarMonth.setVisible(false);
        calendarMonth.setManaged(false);


        // Calendrier par semaine
        int numRows = calendarWeek.getRowConstraints().size();
        int numCols = calendarWeek.getColumnConstraints().size();

        calendarWeek.setPrefHeight(10000000);

        for (int col = 1; col < numCols; col++) {
            for (int row = 1; row < numRows; row++) {
                Button backgroundButton = createBackgroundButton(row, col);
                calendarWeek.getChildren().add(backgroundButton);
            }
        }

        // Calendrier par jour
        numRows = calendarDay.getRowConstraints().size();
        numCols = calendarDay.getColumnConstraints().size();

        calendarDay.setPrefHeight(10000000);

        for (int row = 1; row < numRows; row++) {
            for (int col = 1; col < numCols; col++) {
                Button backgroundButton = createBackgroundButton(row, col);
                calendarDay.getChildren().add(backgroundButton);
            }
        }

        // Calendrier par mois
        numRows = calendarMonth.getRowConstraints().size();
        numCols = calendarMonth.getColumnConstraints().size();

        calendarMonth.setPrefHeight(10000000);

        for (int row = 1; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                HBox buttonContainer = createBackgroundContainer(row, col);
                calendarMonth.getChildren().add(buttonContainer);
            }
        }

        this.actualDate = LocalDateTime.now();


        refreshDataCalendar("now", null);
    }

    private void handleBackgroundButtonClick(int col) {
        calendarWeek.setVisible(false);
        calendarWeek.setManaged(false);
        calendarMonth.setVisible(false);
        calendarMonth.setManaged(false);
        calendarDay.setVisible(true);
        calendarDay.setManaged(true);
        this.mode = "day";

        refreshDataCalendar("here", getDay(col));
    }

    @FXML
    private void handleButtonActionRefresh(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        refreshDataCalendar(clickedButton.getId(), null);
    }

    @FXML
    private void handleButtonActionCalendar(ActionEvent event) {
        MenuItem clickedButton = (MenuItem) event.getSource();
        String buttonId = clickedButton.getId();

        switch (buttonId) {
            case "buttonDay" -> {
                this.mode = "day";
                calendarWeek.setVisible(false);
                calendarWeek.setManaged(false);
                calendarMonth.setVisible(false);
                calendarMonth.setManaged(false);
                calendarDay.setVisible(true);
                calendarDay.setManaged(true);
            }
            case "buttonWeek" -> {
                this.mode = "week";
                calendarWeek.setVisible(true);
                calendarWeek.setManaged(true);
                calendarMonth.setVisible(false);
                calendarMonth.setManaged(false);
                calendarDay.setVisible(false);
                calendarDay.setManaged(false);
            }
            case "buttonMonth" -> {
                this.mode = "month";
                calendarWeek.setVisible(false);
                calendarWeek.setManaged(false);
                calendarMonth.setVisible(true);
                calendarMonth.setManaged(true);
                calendarDay.setVisible(false);
                calendarDay.setManaged(false);
            }
        }
        refreshDataCalendar("now", null);
    }

    public void refreshDataCalendar(String when, String date) {
        clearButtonsFromGridPane(calendarMonth);
        calendarWeek.getChildren().removeAll(eventButtons);
        calendarDay.getChildren().removeAll(eventButtons);
        eventButtons.clear();

        ICSParserController parser = new ICSParserController();
        List<Event> events = new ArrayList<>();

        if (this.mode.equals("week")){
            switch (when) {
                case "previous" -> {
                    this.actualDate = this.actualDate.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    events = parser.parse(this.filePath, this.mode, this.actualDate);
                }
                case "now" -> {
                    this.actualDate = LocalDateTime.now();
                    events = parser.parse(this.filePath, this.mode, this.actualDate);
                }
                case "after" -> {
                    this.actualDate = this.actualDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));;
                    events = parser.parse(this.filePath, this.mode, this.actualDate);
                }
            }
            for ( Event e : events) {
                for (int i = 1; i<6; i++ ){
                    Button backgroundHeaderButton = createBackgroundHeaderButton(0, i);
                    int numDay = Integer.parseInt(this.actualDate.format(DateTimeFormatter.ofPattern("d", Locale.FRENCH))) + i-1;
                    backgroundHeaderButton.setText(backgroundHeaderButton.getText() + " " + numDay );
                    int finalRow = i;
                    backgroundHeaderButton.setOnAction(event -> handleBackgroundButtonClick(finalRow));
                    backgroundHeaderButton.setId(String.valueOf(i));
                    calendarWeek.getChildren().add(backgroundHeaderButton);

                }
                addEventButtonWeekCalendar(e);
            }
        }
        else if (this.mode.equals("day")){
            switch (when) {
                case "previous" -> {
                    this.actualDate = this.actualDate.minusDays(1);
                    labelDay.setText(this.actualDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH)) + " " + this.actualDate.format(DateTimeFormatter.ofPattern("d", Locale.FRENCH)));
                    events = parser.parse(this.filePath, this.mode, this.actualDate);
                }
                case "now" -> {
                    this.actualDate =  LocalDateTime.now();
                    labelDay.setText(this.actualDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH)) + " " + this.actualDate.format(DateTimeFormatter.ofPattern("d", Locale.FRENCH)));
                    events = parser.parse(this.filePath, this.mode, this.actualDate);
                }
                case "here" -> {
                    DayOfWeek desiredDayOfWeek = convertFrenchDayOfWeek(date);
                    LocalDate dateTmp = LocalDate.from(this.actualDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
                    this.actualDate = dateTmp.with(TemporalAdjusters.nextOrSame(desiredDayOfWeek)).atStartOfDay();
                    labelDay.setText(this.actualDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH)) + " " + this.actualDate.format(DateTimeFormatter.ofPattern("d", Locale.FRENCH)));
                    events = parser.parse(this.filePath, this.mode, this.actualDate);
                }
                case "after" -> {
                    this.actualDate = this.actualDate.plusDays(1);
                    labelDay.setText(this.actualDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH)) + " " + this.actualDate.format(DateTimeFormatter.ofPattern("d", Locale.FRENCH)));
                    events = parser.parse(this.filePath, this.mode, this.actualDate);
                }
            }
            for ( Event e : events) {
                addEventButtonDayCalendar(e);
            }
        }
        else if (this.mode.equals("month")){
            switch (when) {
                case "previous" -> {
                    this.actualDate = this.actualDate.minusMonths(1).withDayOfMonth(1);
                    events = parser.parse(this.filePath, this.mode, this.actualDate);
                }
                case "now" -> {
                    this.actualDate = LocalDateTime.now().withDayOfMonth(1);
                    events = parser.parse(this.filePath, this.mode, this.actualDate);
                }
                case "after" -> {
                    this.actualDate = this.actualDate.plusMonths(1).withDayOfMonth(1);
                    events = parser.parse(this.filePath, this.mode, this.actualDate);
                }
            }
            for ( Event e : events) {
                addEventButtonMonthCalendar(e);
            }
        }

    }

    private void clearButtonsFromGridPane(GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                hbox.getChildren().removeIf(child -> child instanceof Button);

            }
        }

    }

    private static DayOfWeek convertFrenchDayOfWeek(String dayOfWeekString) {
        return switch (dayOfWeekString.toLowerCase()) {
            case "lundi" -> DayOfWeek.MONDAY;
            case "mardi" -> DayOfWeek.TUESDAY;
            case "mercredi" -> DayOfWeek.WEDNESDAY;
            case "jeudi" -> DayOfWeek.THURSDAY;
            case "vendredi" -> DayOfWeek.FRIDAY;
            case "samedi" -> DayOfWeek.SATURDAY;
            case "dimanche" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Jour de la semaine non valide : " + dayOfWeekString);
        };
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

    private Button createBackgroundHeaderButton(int row, int col) {
        Button backgroundButton = new Button();
        backgroundButton.setText(getDay(col));
        backgroundButton.getStyleClass().add("background-header-button");
        GridPane.setRowIndex(backgroundButton, row);
        GridPane.setColumnIndex(backgroundButton, col);
        GridPane.setFillHeight(backgroundButton, true);
        GridPane.setFillWidth(backgroundButton, true);
        backgroundButton.setMaxHeight(Double.MAX_VALUE);
        backgroundButton.setMaxWidth(Double.MAX_VALUE);
        return backgroundButton;
    }

    private HBox createBackgroundContainer(int row, int col) {
        HBox buttonContainer = new HBox();
        buttonContainer.getStyleClass().add("background-button");
        GridPane.setRowIndex(buttonContainer, row);
        GridPane.setColumnIndex(buttonContainer, col);
        GridPane.setFillHeight(buttonContainer, true);
        GridPane.setFillWidth(buttonContainer, true);
        buttonContainer.setMaxHeight(Double.MAX_VALUE);
        buttonContainer.setMaxWidth(Double.MAX_VALUE);
        buttonContainer.setSpacing(10);
        buttonContainer.setAlignment(Pos.BOTTOM_LEFT);

        return buttonContainer;
    }

    private void addEventButtonMonthCalendar(Event e) {
        Button eventButton = new Button();
        eventButton.getStyleClass().add("event-month-button");
        eventButton.setMaxHeight(5);
        eventButton.setMaxWidth(5);

        Tooltip tooltip = new Tooltip(e.getDescription());
        tooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(eventButton, tooltip);

        HBox hbox = getOrCreateVBox(e.getStartRow(), e.getStartCol());
        hbox.getChildren().add(eventButton);

        eventButtons.add(eventButton);
    }

    private HBox getOrCreateVBox(int rowIndex, int colIndex) {
        for (Node node : calendarMonth.getChildren()) {
            if (node instanceof HBox && GridPane.getRowIndex(node) == rowIndex && GridPane.getColumnIndex(node) == colIndex) {
                return (HBox) node;
            }
        }

        HBox newHBox = new HBox();
        newHBox.getStyleClass().add("background-button");
        GridPane.setFillHeight(newHBox, true);
        GridPane.setFillWidth(newHBox, true);
        newHBox.setMaxHeight(Double.MAX_VALUE);
        newHBox.setMaxWidth(Double.MAX_VALUE);
        return newHBox;
    }

    private void addEventButtonWeekCalendar(Event e) {
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

        eventButtons.add(eventButton);
    }

    private void addEventButtonDayCalendar(Event e) {
        Button eventButton = new Button(e.getSummary() + e.getDescription());
        if (e.getSummary().equals("Férié")){
            eventButton.getStyleClass().add("event-button-gray");
        }
        else {
            eventButton.getStyleClass().add("event-button");
        }
        GridPane.setRowIndex(eventButton, e.getStartRow());
        GridPane.setColumnIndex(eventButton, 1);
        GridPane.setRowSpan(eventButton, e.getRowSpan());
        GridPane.setFillHeight(eventButton, true);
        GridPane.setFillWidth(eventButton, true);
        eventButton.setMaxHeight(Double.MAX_VALUE);
        eventButton.setMaxWidth(Double.MAX_VALUE);
        calendarDay.getChildren().add(eventButton);

        eventButtons.add(eventButton);
    }
    private String getDay(int num){
        switch (num){
            case 1 -> {
                return "Lundi";
            }
            case 2 -> {
                return "Mardi";
            }
            case 3 -> {
                return "Mercredi";
            }
            case 4 -> {
                return "Jeudi";
            }
            case 5 -> {
                return "Vendredi";
            }
        }
        return "Lundi";
    }

}