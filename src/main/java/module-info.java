module org.example.calenjax {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires ical4j.core;
        requires com.fasterxml.jackson.databind;

        opens org.example.calenjax to javafx.fxml;
    exports org.example.calenjax;
        exports org.example.calenjax.Controlleurs;
        opens org.example.calenjax.Controlleurs to javafx.fxml;
        exports org.example.calenjax.models;
        opens org.example.calenjax.models to javafx.fxml;
    }