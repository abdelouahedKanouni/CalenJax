
package org.example.calenjax;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class ConnexionPage extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Chargement du fichier FXML
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ConnexionPage.fxml")));

        // Création de la scène
        Scene scene = new Scene(root);

        // Configuration de la scène principale
        primaryStage.setScene(scene);
        primaryStage.setTitle("Page de Connexion");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
