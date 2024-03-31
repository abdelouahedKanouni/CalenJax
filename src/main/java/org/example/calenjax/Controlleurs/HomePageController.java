package org.example.calenjax.Controlleurs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.calenjax.Event;
import org.example.calenjax.HelloApplication;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Properties;
import java.util.*;

public class HomePageController {
    public Button now;
    @FXML
    private GridPane calendarWeek;
    @FXML
    private Button addEvent;
    @FXML
    private Button formationButton;
    @FXML
    private Button roomButton;
    @FXML
    private Button personnalButton;
    @FXML
    private Button toggleModeButton;
    @FXML
    private GridPane calendarDay;
    @FXML
    private GridPane calendarMonth;
    @FXML
    private TextField rechercheTextField;
    private LocalDateTime actualDate;
    private String currentFilterValue;
    private String mode = "week";
    private String filePath = null;
    @FXML
    private Label labelDay;
    @FXML
    private ImageView homeImage;
    @FXML
    private VBox parameterBar;
    @FXML private ComboBox<String> filterComboBox;
    @FXML
    private VBox searchBar;
    @FXML
    private Label monthName;
    private int year;
    private boolean isDarkMode = false;
    private int month;
    private Stage primaryStage;
    private List<Button> eventButtons = new ArrayList<>();
    private List<String> formationsDisponibles = new ArrayList<>();
    private List<String> sallesDisponibles = new ArrayList<>();
    @FXML
    private ListView<String> formationsListView;
    private static String currentUser;
    private List<Button> selectedButtons = new ArrayList<>();
    private ICSParserController parser;
    private Scene scene;
    private String typeCalendar;
    @FXML
    private MenuItem buttonDay;
    @FXML
    private MenuItem buttonWeek;
    @FXML
    private MenuItem buttonMonth;

    private void setTypeCalendar(String newValue){
        this.typeCalendar = newValue;
        if (!this.typeCalendar.equals("personnal")){
            addEvent.setDisable(true);
            addEvent.setVisible(false);
            addEvent.setManaged(false);
        }
        else{
            addEvent.setDisable(false);
            addEvent.setVisible(true);
            addEvent.setManaged(true);
        }
    }

    public void setProperties(Stage primaryStage, Scene scene) {
        this.primaryStage = primaryStage;
        this.scene = scene;
        homeImage.fitWidthProperty().bind(this.primaryStage.widthProperty());

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN), formationButton::fire);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN), roomButton::fire);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.P, KeyCodeCombination.CONTROL_DOWN), personnalButton::fire);

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.D, KeyCodeCombination.CONTROL_DOWN), buttonDay::fire);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN), buttonWeek::fire);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.M, KeyCodeCombination.CONTROL_DOWN), buttonMonth::fire);
    }
    public void initialize() {

        setTypeCalendar("personnal");

        calendarDay.setVisible(false);
        calendarDay.setManaged(false);
        calendarMonth.setVisible(false);
        calendarMonth.setManaged(false);

        searchBar.setManaged(false);
        searchBar.setVisible(false);

        this.parser = new ICSParserController();

        addEvent.setOnAction(event -> openEventWindow());

        // Calendrier par semaine
        int numRows = calendarWeek.getRowConstraints().size();
        int numCols = calendarWeek.getColumnConstraints().size();

        calendarWeek.setPrefHeight(10000000);

        for (int col = 1; col < numCols; col++) {
            for (int row = 1; row < numRows; row++) {
                Button backgroundButton = createBackgroundButton(row, col);
                backgroundButton.setOnMouseClicked(event -> {
                    toggleSelectionBackgroundButton(backgroundButton);
                });
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
                backgroundButton.setOnMouseClicked(event -> {
                    toggleSelectionBackgroundButton(backgroundButton);
                });
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
        this.month = this.actualDate.getMonth().getValue() - 1;
        this.year = this.actualDate.getYear();

        handleButtonActionPersonnel(new ActionEvent());

        toggleModeButton.setOnAction(event -> toggleMode());

        // Ajout d'un écouteur pour mettre à jour le TextField lors de la sélection d'une formation
        formationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                rechercheTextField.setText(newValue);
            }
        });
    }

    private void toggleMode() {
        isDarkMode = !isDarkMode;

        Scene scene = Stage.getWindows().get(0).getScene();
        scene.getStylesheets().clear();

        if (isDarkMode) {
            scene.getStylesheets().add("dark.css");
        } else {
            scene.getStylesheets().add("light.css");
        }
    }

    private void toggleSelectionBackgroundButton
            (Button button) {

        if (selectedButtons.contains(button)) {
            if (canDeleteButton(button)) {
                selectedButtons.remove(button);
                button.setStyle("");
            }
        } else {
            if (selectedButtons.isEmpty() || isAdjacentVertically(button)) {
                selectedButtons.add(button);
                button.setStyle("-fx-background-color: lightblue;");
            }
        }
    }

    private boolean canDeleteButton(Button button) {
        int countAdjacentButtons = 0;
        int buttonRow = GridPane.getRowIndex(button);

        for (Button selectedButton : selectedButtons) {
            int selectedButtonRow = GridPane.getRowIndex(selectedButton);
            if (Math.abs(buttonRow - selectedButtonRow) == 1) {
                countAdjacentButtons++;
            }
        }

        return countAdjacentButtons <= 1;
    }

    @FXML
    private void handleFilterSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("filter-popup.fxml"));
            Parent root = loader.load();

            Stage filterPopup = new Stage();
            filterPopup.initModality(Modality.APPLICATION_MODAL);
            filterPopup.setResizable(false);
            // si on veut filtrer par matière sinon par enseignant
            if (filterComboBox.getValue().equals("Matière")){
                filterPopup.setTitle("Filtrer par matière");
            }
            else if(filterComboBox.getValue().equals("Groupe")){
                filterPopup.setTitle("Filtrer par Groupe");
            }else if(filterComboBox.getValue().equals("Salle")){
                filterPopup.setTitle("Filtrer par Salle");
            }
            else{
                filterPopup.setTitle("Filtrer par type de cours");
            }

            Scene scene = new Scene(root);
            if (isDarkMode) scene.getStylesheets().add("dark.css");
            else scene.getStylesheets().add("light.css");
            filterPopup.setScene(scene);
            filterPopup.show();
            Button filterButton = (Button) scene.lookup("#filterButton");
            filterButton.setOnAction(event -> {
                TextField filterText = (TextField) scene.lookup("#filterText");
                currentFilterValue = filterText.getText();
                String filterValue = filterText.getText();
                refreshDataCalendar("here", null,filterValue);

                filterPopup.close();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean isAdjacentVertically(Button button) {
        int buttonRow = GridPane.getRowIndex(button);
        int buttonCol = GridPane.getColumnIndex(button);
        if (buttonRow == 0) {
            return true;
        }

        for (Button selectedButton : selectedButtons) {
            int selectedButtonRow = GridPane.getRowIndex(selectedButton);
            int selectedButtonCol = GridPane.getColumnIndex(selectedButton);
            if (Math.abs(buttonRow - selectedButtonRow) == 1 && selectedButtonCol == buttonCol) {
                return true;
            }
        }
        return false;
    }



    @FXML
    private void handleButtonActionChercher(ActionEvent event) {
        String selectedItem = formationsListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            String filePath;
            if (formationsDisponibles.contains(selectedItem)) {
                // Si l'élément sélectionné est une formation
                String fileName = selectedItem.replace(" ", "_") + ".ics";
                filePath = "./src/main/resources/org/example/calenjax/Formations/" + fileName;
            } else if (sallesDisponibles.contains(selectedItem)) {
                // Si l'élément sélectionné est une salle
                String fileName = selectedItem.replace(" ", "_") + ".ics";
                filePath = "./src/main/resources/org/example/calenjax/Salles/" + fileName;
            } else {
                // Si l'élément sélectionné n'est ni une formation ni une salle, vous pouvez gérer cela selon vos besoins
                System.err.println("Élément sélectionné non valide.");
                return;
            }

            this.mode="week";
            // Parser le fichier correspondant à la formation ou la salle sélectionnée
            this.filePath = filePath;
            ICSParserController parser = new ICSParserController();
            refreshDataCalendar("now", null,"");
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
                    String formationName = fichierFormation.getName();
                    formationName = formationName.replace(".ics", "");
                    formationName = formationName.replace("_", " ");
                    formationsDisponibles.add(formationName);
                }
            }
        }

        // Mettre à jour la ListView des formations avec les formations disponibles
        formationsListView.getItems().addAll(formationsDisponibles);
    }
    @FXML
    private void handleImportButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer un fichier ICS");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers ICS", "*.ics"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            String userFileName = getCurrentUser() + ".ics";
            // Copier le fichier sélectionné dans le répertoire "Personnel"
            String destinationPath = "./src/main/resources/org/example/calenjax/Personnel/" + userFileName;
            try {
                Files.copy(selectedFile.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                // Gérer les erreurs de copie de fichier
                return;
            }
            // Parser le fichier ICS et mettre à jour l'emploi du temps de l'utilisateur
            ICSParserController parser = new ICSParserController();
            this.filePath = destinationPath;
            refreshDataCalendar("now", null,"");
        }
    }

    @FXML
    private void handleButtonActionPersonnel(ActionEvent event) {
        String currentUser = getCurrentUser();
        if (currentUser != null) {
            String userFileName = currentUser + ".ics";
            String filePath = "./src/main/resources/org/example/calenjax/Personnel/" + userFileName;

            File file = new File(filePath);
            if (file.exists()) {
                try {
                    ICSParserController parser = new ICSParserController();
                    this.filePath = filePath;
                    refreshDataCalendar("now", null,"");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Erreur lors du chargement de l'emploi du temps personnel de l'utilisateur " + currentUser + ".");
                }
            } else {
                System.out.println("Aucun fichier personnel trouvé pour l'utilisateur " + currentUser + ".");
            }
        } else {
            System.out.println("Utilisateur actuel non trouvé.");
        }
    }

    // Méthode pour récupérer l'utilisateur actuel
    private String getCurrentUser() {
        // Simuler un utilisateur connecté en utilisant une variable statique
        return currentUser;
    }

    // Méthode pour définir l'utilisateur actuel
    public static void setCurrentUser(String user) {
        currentUser = user;
    }
    private void handleBackgroundButtonClick(int col) {
        calendarWeek.setVisible(false);
        calendarWeek.setManaged(false);
        calendarMonth.setVisible(false);
        calendarMonth.setManaged(false);
        calendarDay.setVisible(true);
        calendarDay.setManaged(true);
        this.mode = "day";

        refreshDataCalendar("here", getDay(col),"");
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
                    String salleName = fichierSalle.getName();
                    salleName = salleName.replace(".ics", "");
                    salleName = salleName.replace("_", " ");
                    sallesDisponibles.add(salleName);
                }
            }
        }

        // Mettre à jour la ListView des salles avec les salles disponibles
        formationsListView.getItems().addAll(sallesDisponibles);
    }

    @FXML
    private void handleButtonActionRefresh(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        refreshDataCalendar(clickedButton.getId(), null,currentFilterValue);
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
        refreshDataCalendar("now", null,"");
    }

    public void refreshDataCalendar(String when, String date, String filterValue) {
        for (Node node : new ArrayList<>(calendarMonth.getChildren())) {
            Integer rowIndex = GridPane.getRowIndex(node);
            if (rowIndex != null && rowIndex > 0 && rowIndex < 6) {
                calendarMonth.getChildren().remove(node);
            }
        }
        calendarWeek.getChildren().removeAll(eventButtons);
        calendarDay.getChildren().removeAll(eventButtons);
        eventButtons.clear();
        calendarWeek.getChildren().removeAll(selectedButtons);
        calendarDay.getChildren().removeAll(selectedButtons);
        selectedButtons.clear();

        parameterBar.setManaged(true);
        parameterBar.setVisible(true);
        searchBar.setManaged(false);
        searchBar.setVisible(false);

        List<Event> events = new ArrayList<>();

        if (this.mode.equals("week")){
            switch (when) {
                case "previous" -> {
                    this.actualDate = this.actualDate.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    events = parser.parse(this.filePath, this.mode, this.actualDate, filterValue);
                }
                case "now" -> {
                    this.actualDate = LocalDateTime.now();
                    events = parser.parse(this.filePath, this.mode, this.actualDate, filterValue);
                }
                case "here" -> {
                    events = parser.parse(this.filePath, this.mode, this.actualDate, filterValue);
                }
                case "after" -> {
                    this.actualDate = this.actualDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));;
                    events = parser.parse(this.filePath, this.mode, this.actualDate, filterValue);
                }
            }
            for (int i = 1; i<6; i++ ){
                Button backgroundHeaderButton = createBackgroundHeaderButton(0, i);
                int numDay = this.actualDate.with(DayOfWeek.MONDAY).getDayOfMonth() + i-1;
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
                    events = parser.parse(this.filePath, this.mode, this.actualDate,filterValue);
                }
                case "now" -> {
                    this.actualDate =  LocalDateTime.now();
                    labelDay.setText(this.actualDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH)) + " " + this.actualDate.format(DateTimeFormatter.ofPattern("d", Locale.FRENCH)));
                    events = parser.parse(this.filePath, this.mode, this.actualDate,filterValue);
                }
                case "here" -> {
                    DayOfWeek desiredDayOfWeek;
                    if (date == null){
                        desiredDayOfWeek = this.actualDate.getDayOfWeek();
                    }
                    else{
                         desiredDayOfWeek = convertFrenchDayOfWeek(date);
                    }
                    LocalDate dateTmp = LocalDate.from(this.actualDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
                    this.actualDate = dateTmp.with(TemporalAdjusters.nextOrSame(desiredDayOfWeek)).atStartOfDay();
                    labelDay.setText(this.actualDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH)) + " " + this.actualDate.format(DateTimeFormatter.ofPattern("d", Locale.FRENCH)));
                    events = parser.parse(this.filePath, this.mode, this.actualDate,filterValue);
                }
                case "after" -> {
                    this.actualDate = this.actualDate.plusDays(1);
                    labelDay.setText(this.actualDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH)) + " " + this.actualDate.format(DateTimeFormatter.ofPattern("d", Locale.FRENCH)));
                    events = parser.parse(this.filePath, this.mode, this.actualDate,filterValue);
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
                    events = parser.parse(this.filePath, this.mode, this.actualDate,filterValue);
                }
                case "now" -> {
                    this.actualDate = LocalDateTime.now().withDayOfMonth(1);
                    events = parser.parse(this.filePath, this.mode, this.actualDate,filterValue);
                }
                case "after" -> {
                    this.actualDate = this.actualDate.plusMonths(1).withDayOfMonth(1);
                    events = parser.parse(this.filePath, this.mode, this.actualDate,filterValue);
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
        this.month = this.actualDate.getMonth().getValue() - 1;
        this.year = this.actualDate.getYear();


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
        Button eventButton = new Button(e.getSummary() + e.getLocation() + e.getDescription());

        if (e.getEnseignant() != null){
            eventButton.setOnAction(elem -> ouvrirEmail(e));
        }

        // classes des évènements
        if (e.getSummary().equals("Férié")){
            eventButton.getStyleClass().add("event-button-gray");
        }
        if (e.getUid() != null){
           String color = e.getUid().split("-")[0];
            switch (color) {
                case "blue" -> eventButton.getStyleClass().add("event-blue-button");
                case "red" -> eventButton.getStyleClass().add("event-red-button");
                case "green" -> eventButton.getStyleClass().add("event-green-button");
                default -> eventButton.getStyleClass().add("event-button");
            }
        }
        else {
            eventButton.getStyleClass().add("event-button");
        }

        // placement des évènements
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

    private void ouvrirEmail(Event e) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("send-email.fxml"));
            Parent root = loader.load();

            Stage sendEmail = new Stage();
            sendEmail.initModality(Modality.APPLICATION_MODAL);
            sendEmail.setResizable(false);

            sendEmail.setTitle("Envoie email");

            Scene scene = new Scene(root);
            if (isDarkMode) scene.getStylesheets().add("dark.css");
            else scene.getStylesheets().add("light.css");
            sendEmail.setScene(scene);
            sendEmail.show();

            TextField addressField = (TextField) scene.lookup("#address");
            String email = e.getEnseignant().toLowerCase();
            String[] parts = email.split(" ");
            String firstName = parts[0];
            String lastName = parts[1];
            email = firstName + "." + lastName + "@alumni.univ-avignon.fr";

            addressField.setText(email);

            TextField textField = (TextField) scene.lookup("#title");
            TextArea textArea = (TextArea) scene.lookup("#text");
            Button sendButton = (Button) scene.lookup("#send");

            sendButton.setOnAction(event -> {
                String address = addressField.getText();
                String title = textField.getText();
                String text = textArea.getText();
                sendEmail(address, title, text);
                sendEmail.close();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendEmail(String address, String title, String text) {
        final String username = "calenjax@gmail.com";
        final String password = "jzhy cbpg qkms iyyq";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress("calenjax@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("calenjax@gmail.com"));

            message.setSubject(title);
            message.setText(text);

            Transport.send(message);

            System.out.println("Email envoyé avec succès.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
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

    private void openEventWindow() {
        if (selectedButtons.isEmpty()) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("add-event.fxml"));
            Stage eventStage = new Stage();
            eventStage.initModality(Modality.APPLICATION_MODAL);
            eventStage.setResizable(false);
            eventStage.setTitle("Ajout Evenement");
            Scene scene = new Scene(loader.load());
            if (isDarkMode) scene.getStylesheets().add("dark.css");
            else scene.getStylesheets().add("light.css");
            eventStage.setScene(scene);
            eventStage.show();

            RadioButton blueRadioButton = (RadioButton) scene.lookup("#blue");
            RadioButton redRadioButton = (RadioButton) scene.lookup("#red");
            RadioButton greenRadioButton = (RadioButton) scene.lookup("#green");
            ToggleGroup toggleGroup = new ToggleGroup();
            blueRadioButton.setToggleGroup(toggleGroup);
            redRadioButton.setToggleGroup(toggleGroup);
            greenRadioButton.setToggleGroup(toggleGroup);

            TextField titleTextField = (TextField) scene.lookup("#title");
            TextField locationTextField = (TextField) scene.lookup("#location");
            TextArea descriptionTextArea = (TextArea) scene.lookup("#description");

            Button createButton = (Button) scene.lookup("#createButton");

            createButton.setOnMouseClicked(event -> {
                String title = titleTextField.getText() + "\n";
                String location = locationTextField.getText() + "\n";
                String description = descriptionTextArea.getText() + "\n";

                int startRow = Integer.MAX_VALUE;
                for (Button button : selectedButtons) {
                    int row = GridPane.getRowIndex(button);
                    if (row < startRow) {
                        startRow = row;
                    }
                }
                int startCol = GridPane.getColumnIndex(selectedButtons.get(0));
                int highestRow = 0;
                for (Button button : selectedButtons) {
                    int row = GridPane.getRowIndex(button);
                    if (row > highestRow) {
                        highestRow = row;
                    }
                }
                int rowSpan = highestRow - startRow + 1;
                String color = "";
                if (blueRadioButton.isSelected()) {
                    color = "blue";
                } else if (redRadioButton.isSelected()) {
                    color = "red";
                } else if (greenRadioButton.isSelected()) {
                    color = "green";
                }

                if (this.mode.equals("week")){
                    sendEvent(calendarWeek, startRow, startCol, rowSpan, title, location, description, color);
                }
                else{
                    sendEvent(calendarDay, startRow, startCol, rowSpan, title, location, description, color);
                }
                refreshDataCalendar("here", null,"");
                eventStage.close();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEvent(GridPane gridPane, int startRow, int startCol, int rowSpan, String title, String location, String description, String color){

        Button button;
        Label label;
        String day = "0";
        for (Node child : gridPane.getChildren()) {
            Integer r = GridPane.getRowIndex(child);
            Integer c = GridPane.getColumnIndex(child);
            int row = r == null ? 0 : r;
            int column = c == null ? 0 : c;
            if (row == 0 && column == startCol && (child instanceof Button) && this.mode.equals("week")) {
                button = (Button) child;
                day = button.getText().split(" ")[1];
            }
            if (row == 0 && column == startCol && (child instanceof Label) && this.mode.equals("day")) {
                label = (Label) child;
                day = label.getText().split(" ")[1];
            }
        }

        int minuteStart = 0;
        int hourStart = ((startRow + 16) / 2);
        startRow = startRow+1;
        System.out.println(startRow);
        if (startRow%2 == 1){
            minuteStart = 30;
            hourStart--;
        }

        int minuteEnd = 0;
        int hourEnd = ((startRow + rowSpan + 16) / 2 ) - 1;
        if ((startRow + rowSpan)%2 == 1){
            minuteEnd = 30;
        }

        Date DtStart = new Date(this.year - 1900, this.month, Integer.parseInt(day), hourStart - 1, minuteStart, 0);
        Date DtEnd = new Date(this.year - 1900, this.month, Integer.parseInt(day), hourEnd - 1, minuteEnd, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        String formattedDtStart = sdf.format(DtStart);
        String formattedDtEnd = sdf.format(DtEnd);
        System.out.println(day);
        System.out.println("start at : " + hourStart + "h" + minuteStart );
        System.out.println("end at : " + hourEnd + "h" + minuteEnd );
        System.out.println("DtStart : " + formattedDtStart );
        System.out.println("DtStart : " + formattedDtEnd );

        UUID uuid = UUID.randomUUID();
        String uid = color + "-" + uuid.toString().substring(0, 9);

        parser.addEvent(uid, formattedDtStart, formattedDtEnd, title, location, description);
    }

}