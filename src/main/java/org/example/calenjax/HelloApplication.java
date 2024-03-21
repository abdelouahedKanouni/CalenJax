package org.example.calenjax;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.calenjax.Controlleurs.HomePageController;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 700);

        HomePageController controller = fxmlLoader.getController();
        controller.setProperties(stage, scene);

        scene.getStylesheets().add("light.css");
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

//FXMLLoader loader = new FXMLLoader(getClass().getResource("home-page.fxml"));
//Parent root = loader.load();
//
//// Get the controller from the loader
//HomePageController homePageController = loader.getController();
//
//// Call any initialization method in your controller, if needed
//        homePageController.initialize();
//
//// Set up the primary stage
//Scene scene = new Scene(root, 1600, 900);
//        stage.setTitle("Calandax!");
//        stage.setScene(scene);
//        stage.show();