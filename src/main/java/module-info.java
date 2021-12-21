module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires javafx.web;
    requires commons.net;

    opens app to javafx.fxml;
    exports app;
    exports controller;
    opens controller to javafx.fxml;
}