package org.example.calenjax.Controlleurs;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import com.fasterxml.jackson.databind.JsonNode;

public class ConnexionPageController {

    @FXML private Button connecterButton;
    @FXML private TextField identifiantField;
    @FXML private PasswordField motDePasseField;
    @FXML private RadioButton etudiantButton;
    @FXML private RadioButton enseignantButton;

    private String type = "etudiant";
    public void connexion() {

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
                        System.out.println("Connexion réussie");
                        return;
                    }
                }
                System.out.println("Connexion échouée");

            } catch (Exception e) {
                e.printStackTrace();
            }

    }
    public void type(){
        if (etudiantButton.isSelected()){
            type = "etudiant";
        } else if (enseignantButton.isSelected()){
            type = "enseignant";
        }

    }
}
