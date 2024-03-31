package org.example.calenjax.Controlleurs;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class ConnexionPageController {

    @FXML private Button connecterButton;
    @FXML private TextField identifiantField;
    @FXML private PasswordField motDePasseField;
    @FXML private RadioButton etudiantButton;
    @FXML private RadioButton enseignantButton;
    @FXML private Text errorMessage;

    private String type = "etudiant";
    public void connexion() {

        if (!etudiantButton.isSelected() && !enseignantButton.isSelected()) {
            errorMessage.setText("Veuillez sélectionner étudiant ou enseignant.");
            playErrorAnimation(errorMessage);
            return;
        }
        String identifiant = identifiantField.getText();
        String motDePasse = motDePasseField.getText();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode utilisateurs;
            if (type.equals("etudiant")) {
                utilisateurs = objectMapper.readTree(ConnexionPageController.class.getResourceAsStream
                        ("/etudiants.json"));

            } else if (type.equals("enseignant")){
                utilisateurs = objectMapper.readTree(ConnexionPageController.class.getResourceAsStream
                        ("/enseignants.json"));
            } else {
                throw new Exception("Type inconnu");
            }

            for (JsonNode utilisateur : utilisateurs) {
                if (utilisateur.get("identifiant").asText().equals(identifiant) && utilisateur.get("motDePasse").asText().equals(motDePasse)) {
                    try {
                        HomePageController.setCurrentUser(utilisateur.get("identifiant").asText());
                        HomePageController.setTypeCurrentUser(this.type);
                        // Charger la page d'accueil
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/calenjax/home-page.fxml"));
                        Scene scene = new Scene(fxmlLoader.load(), 1400, 700);
                        Scene currentScene = connecterButton.getScene();
                        if (utilisateur.get("isDark").asBoolean()){
                            scene.getStylesheets().add("dark.css");
                        }
                        else{
                            scene.getStylesheets().add("light.css");
                        }
                        Stage stage = (Stage) currentScene.getWindow();
                        stage.setScene(scene);
                        stage.setTitle("Accueil");

                        HomePageController controller = fxmlLoader.getController();
                        controller.setProperties(stage, scene);

                        stage.show();
                        System.out.println("Connexion réussie");

                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            errorMessage.setText("Identifiant ou mot de passe incorrect.");
            playErrorAnimation(errorMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void typeEtudiant() {
        type = "etudiant";
    }

    public void typeEnseignant() {
        type = "enseignant";
    }

    public void setErrorMessage(String message) {
        errorMessage.setText(message);
    }

    public  void playErrorAnimation(Text errorMessage) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), errorMessage);
        translateTransition.setFromY(-20);
        translateTransition.setToY(20);
        translateTransition.setCycleCount(4);
        translateTransition.setAutoReverse(true);
        translateTransition.play();
    }
}
