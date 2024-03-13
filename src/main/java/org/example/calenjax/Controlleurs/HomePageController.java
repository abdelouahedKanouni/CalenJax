package org.example.calenjax.Controlleurs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.example.calenjax.Event;
import javafx.event.ActionEvent;

import java.io.File;
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
    @FXML
    private TextField rechercheTextField;
    private LocalDateTime actualDate;
    private String mode = "week";

    private List<Button> eventButtons = new ArrayList<>();
    private List<String> formationsDisponibles = new ArrayList<>();
    private List<String> sallesDisponibles = new ArrayList<>();
    @FXML
    private ListView<String> formationsListView;

    public void initialize() {

        // Création des boutons de fond pour chaque cellule du calendrier
        int numRows = calendarWeek.getRowConstraints().size();
        int numCols = calendarWeek.getColumnConstraints().size();
        for (int row = 1; row < numRows; row++) {
            for (int col = 1; col < numCols; col++) {
                Button backgroundButton = createBackgroundButton(row, col);
                calendarWeek.getChildren().add(backgroundButton);
            }
        }

        // Initialisation de la date actuelle
        this.actualDate = LocalDateTime.now();

        // Récupération des événements à afficher
        String filePath = ""; // chemin du fichier à remplir
        if (!filePath.isEmpty()) {
            ICSParserController parser = new ICSParserController();
            List<Event> events = parser.parse(filePath, this.mode, actualDate);

            // Vérification si la liste d'événements n'est pas nulle avant de l'itérer
            if (events != null) {
                for (Event e : events) {
                    addEventButton(e);
                }
            } else {
                System.err.println("La liste des événements est nulle. Vérifiez le chemin du fichier.");
            }
        } else {
            System.err.println("Le chemin du fichier est vide.");
        }

        // Ajout d'un écouteur pour mettre à jour le TextField lors de la sélection d'une formation
        formationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                rechercheTextField.setText(newValue);
            }
        });
    }

    private void loadFormations() {
        // Effacer toutes les formations disponibles pour éviter les doublons
        formationsDisponibles.clear();

        // Charger les noms des formations disponibles depuis votre source de données (par exemple, le répertoire des formations)
        File repertoireFormations = new File("C:/Users/AbdelouahedKANOUNI/IdeaProjects/CalenJax/src/main/resources/org/example/calenjax/Formations/");
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


    @FXML
    private void handleButtonActionChercher(ActionEvent event) {
        String selectedItem = formationsListView.getSelectionModel().getSelectedItem();
        System.out.print("selected item: ");
        System.out.println(selectedItem);

        if (selectedItem != null) {
            String filePath;
            if (formationsDisponibles.contains(selectedItem)) {
                // Si l'élément sélectionné est une formation
                filePath = "C:/Users/AbdelouahedKANOUNI/IdeaProjects/CalenJax/src/main/resources/org/example/calenjax/Formations/" + selectedItem;
            } else if (sallesDisponibles.contains(selectedItem)) {
                // Si l'élément sélectionné est une salle
                filePath = "C:/Users/AbdelouahedKANOUNI/IdeaProjects/CalenJax/src/main/resources/org/example/calenjax/Salles/" + selectedItem;
            } else {
                // Si l'élément sélectionné n'est ni une formation ni une salle, vous pouvez gérer cela selon vos besoins
                System.err.println("Élément sélectionné non valide.");
                return;
            }

            // Parser le fichier correspondant à la formation ou la salle sélectionnée
            ICSParserController parser = new ICSParserController();
            List<Event> events = parser.parse(filePath, this.mode, this.actualDate);

            // Vérifier si la liste des événements est nulle avant de l'itérer
            if (events != null) {
                // Mettre à jour l'affichage de l'emploi du temps avec les informations récupérées
                calendarWeek.getChildren().removeAll(eventButtons);
                eventButtons.clear();

                for (Event e : events) {
                    addEventButton(e);
                }
            } else {
                System.err.println("La liste des événements est nulle. Vérifiez le fichier ICS spécifié.");
            }
        }
    }

    @FXML
    private void handleFormationButtonClick(ActionEvent event) {
        // Effacer toutes les salles actuellement affichées dans la ListView
        formationsListView.getItems().clear();
        // Effacer toutes les formations disponibles pour éviter les doublons
        formationsDisponibles.clear();

        // Charger les noms des formations disponibles depuis votre source de données (par exemple, le répertoire des formations)
        File repertoireFormations = new File("C:/Users/AbdelouahedKANOUNI/IdeaProjects/CalenJax/src/main/resources/org/example/calenjax/Formations/");
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
    @FXML
    private void handleSalleButtonClick(ActionEvent event) {
        // Effacer toutes les formations actuellement affichées dans la ListView
        formationsListView.getItems().clear();
        // Effacer toutes les salles disponibles pour éviter les doublons
        sallesDisponibles.clear();
        // Charger les noms des salles disponibles depuis votre source de données (par exemple, le répertoire des salles)
        File repertoireSalles = new File("C:/Users/AbdelouahedKANOUNI/IdeaProjects/CalenJax/src/main/resources/org/example/calenjax/Salles/");
        if (repertoireSalles.isDirectory()) {
            for (File fichierSalle : repertoireSalles.listFiles()) {
                if (fichierSalle.isFile()) {
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