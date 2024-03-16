package org.example.calenjax.Controlleurs;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.calenjax.Event;
import javafx.event.ActionEvent;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
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
    @FXML
    private TextField rechercheTextField;
    private LocalDateTime actualDate;
    private String mode = "week";
    private String filePath = "./src/main/resources/org/example/calenjax/personnal/test.ics";
    @FXML
    private Label labelDay;
    @FXML
    private ImageView homeImage;
    @FXML
    private VBox parameterBar;
    @FXML
    private VBox searchBar;
    @FXML
    private Label monthName;
    private Stage primaryStage;
    private List<Button> eventButtons = new ArrayList<>();
    private List<String> formationsDisponibles = new ArrayList<>();
    private List<String> sallesDisponibles = new ArrayList<>();
    @FXML
    private ListView<String> formationsListView;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        homeImage.fitWidthProperty().bind(this.primaryStage.widthProperty());
    }
    public void initialize() {

        calendarDay.setVisible(false);
        calendarDay.setManaged(false);
        calendarMonth.setVisible(false);
        calendarMonth.setManaged(false);

        searchBar.setManaged(false);
        searchBar.setVisible(false);

        // Calendrier par semaine
        // Création des boutons de fond pour chaque cellule du calendrier
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

        // Initialisation de la date actuelle
        this.actualDate = LocalDateTime.now();

        this.monthName.setText(this.actualDate.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH));
        this.monthName.setTextFill(Color.GRAY);
        this.monthName.setFont(new Font("Arial", 20));

        refreshDataCalendar("now", null);

        // Ajout d'un écouteur pour mettre à jour le TextField lors de la sélection d'une formation
        formationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                rechercheTextField.setText(newValue);
            }
        });
    }

    @FXML
    private void handleButtonActionChercher(ActionEvent event) {
        String selectedItem = formationsListView.getSelectionModel().getSelectedItem();
        System.out.print("selected item: ");
        System.out.println(selectedItem);

        if (selectedItem != null) {
            String filePath;
            if (formationsDisponibles.contains(selectedItem)) {
                // Si l'élément sélectionné est une formation
                filePath = "./src/main/resources/org/example/calenjax/Formations/" + selectedItem;
            } else if (sallesDisponibles.contains(selectedItem)) {
                // Si l'élément sélectionné est une salle
                filePath = "./src/main/resources/org/example/calenjax/Salles/" + selectedItem;
            } else {
                // Si l'élément sélectionné n'est ni une formation ni une salle, vous pouvez gérer cela selon vos besoins
                System.err.println("Élément sélectionné non valide.");
                return;
            }

            this.mode="week";
            // Parser le fichier correspondant à la formation ou la salle sélectionnée
            this.filePath = filePath;
            ICSParserController parser = new ICSParserController();
            refreshDataCalendar("now", null);
        }
    }

    @FXML
    private void handleFormationButtonClick(ActionEvent event) {

        parameterBar.setManaged(false);
        parameterBar.setVisible(false);
        searchBar.setManaged(true);
        searchBar.setVisible(true);

        // Effacer toutes les salles actuellement affichées dans la ListView
        formationsListView.getItems().clear();
        // Effacer toutes les formations disponibles pour éviter les doublons
        formationsDisponibles.clear();

        // Charger les noms des formations disponibles depuis votre source de données (par exemple, le répertoire des formations)
        File repertoireFormations = new File("./src/main/resources/org/example/calenjax/Formations/");
        if (repertoireFormations.isDirectory()) {
            for (File fichierFormation : repertoireFormations.listFiles()) {
                if (fichierFormation.isFile()) {
                    formationsDisponibles.add(fichierFormation.getName());
                }
            }
        }

        // Mettre à jour la ListView des formations avec les formations disponibles
        formationsListView.getItems().addAll(formationsDisponibles);
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
    private void handleSalleButtonClick(ActionEvent event) {

        parameterBar.setManaged(false);
        parameterBar.setVisible(false);
        searchBar.setManaged(true);
        searchBar.setVisible(true);

        // Effacer toutes les formations actuellement affichées dans la ListView
        formationsListView.getItems().clear();
        // Effacer toutes les salles disponibles pour éviter les doublons
        sallesDisponibles.clear();
        // Charger les noms des salles disponibles depuis votre source de données (par exemple, le répertoire des salles)
        File repertoireSalles = new File("./src/main/resources/org/example/calenjax/Salles/");
        if (repertoireSalles.isDirectory()) {
            for (File fichierSalle : repertoireSalles.listFiles()) {
                if (fichierSalle.isFile()) {
                    System.out.println(fichierSalle.getName());
                    sallesDisponibles.add(fichierSalle.getName());
                }
            }
        }

        // Mettre à jour la ListView des salles avec les salles disponibles
        formationsListView.getItems().addAll(sallesDisponibles);
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
        for (Node node : new ArrayList<>(calendarMonth.getChildren())) {
            Integer rowIndex = GridPane.getRowIndex(node);
            if (rowIndex != null && rowIndex > 0 && rowIndex < 6) {
                calendarMonth.getChildren().remove(node);
            }
        }
        calendarWeek.getChildren().removeAll(eventButtons);
        calendarDay.getChildren().removeAll(eventButtons);
        eventButtons.clear();

        parameterBar.setManaged(true);
        parameterBar.setVisible(true);
        searchBar.setManaged(false);
        searchBar.setVisible(false);

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
            for (int i = 1; i<6; i++ ){
                Button backgroundHeaderButton = createBackgroundHeaderButton(0, i);
                int numDay = Integer.parseInt(this.actualDate.format(DateTimeFormatter.ofPattern("d", Locale.FRENCH))) + i-1;
                backgroundHeaderButton.setText(backgroundHeaderButton.getText() + " " + numDay );
                int finalRow = i;
                backgroundHeaderButton.setOnAction(event -> handleBackgroundButtonClick(finalRow));
                backgroundHeaderButton.setId(String.valueOf(i));
                calendarWeek.getChildren().add(backgroundHeaderButton);
            }
            for ( Event e : events) {
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
            LocalDateTime firstDayOfMonth = actualDate.withDayOfMonth(1);
            int firstDay = firstDayOfMonth.getDayOfWeek().getValue() - 1;
            int daysInThisMonth = actualDate.toLocalDate().lengthOfMonth() - 1;
            for (int i=0; i<firstDay; i++){
                HBox buttonContainer = createBackgroundContainer(1, i, null);
                calendarMonth.getChildren().add(buttonContainer);
            }
            for (int i=daysInThisMonth-firstDay+1; i<5; i++){
                HBox buttonContainer = createBackgroundContainer(5, i, null);
                calendarMonth.getChildren().add(buttonContainer);
            }
            for (int i=firstDay; i<=daysInThisMonth + firstDay && i<35 ; i++ ){
                int row = (i+1) / 7 + 1;
                int col = (i) % 7;
                if (col < 5){
                    HBox backgroundHeaderButton;
                    if ( i-firstDay+1 == LocalDate.now().getDayOfMonth() && this.actualDate.getMonth() == LocalDate.now().getMonth()){
                        backgroundHeaderButton = createBackgroundContainer(row, col, "background-month-button-today");
                    }
                    else{
                        backgroundHeaderButton = createBackgroundContainer(row, col, "background-month-button-2");
                    }
                    int numDay = i - firstDay + 1;
                    Label label = new Label(numDay+"");
                    label.setTextFill(Color.DARKGREY);
                    label.setFont(new Font("Arial", 20));
                    backgroundHeaderButton.getChildren().add(label);
                    calendarMonth.getChildren().add(backgroundHeaderButton);
                }
            }
            for ( Event e : events) {
                addEventButtonMonthCalendar(e);
            }
        }

        this.monthName.setText(this.actualDate.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH));


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

    private HBox createBackgroundContainer(int row, int col, String classes) {
        HBox buttonContainer = new HBox();
        if (classes == null){classes = "background-button";}
        buttonContainer.getStyleClass().add(classes);
        GridPane.setRowIndex(buttonContainer, row);
        GridPane.setColumnIndex(buttonContainer, col);
        GridPane.setFillHeight(buttonContainer, true);
        GridPane.setFillWidth(buttonContainer, true);
        buttonContainer.setMaxHeight(Double.MAX_VALUE);
        buttonContainer.setMaxWidth(Double.MAX_VALUE);
        buttonContainer.setSpacing(10);
        buttonContainer.setPadding(new javafx.geometry.Insets(3));
        buttonContainer.setAlignment(Pos.BOTTOM_LEFT);

        return buttonContainer;
    }

    private void addEventButtonMonthCalendar(Event e) {
        Button eventButton = new Button();
        eventButton.getStyleClass().add("event-month-button");

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