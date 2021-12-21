package app;

import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Notifier extends CustomApp {
    private final Exception exception;
    private final Alert.AlertType alertType;

    public Notifier(Exception exception, Alert.AlertType alertType) throws Exception {
        super("notify-view.fxml", alertType.name(), 380, 380, 280, 280);
        this.alertType = alertType;
        this.exception = exception;

        start(new Stage());
    }

    public Exception getException() {
        return exception;
    }

    @Override
    protected void show() {
        this.setPositionToCentral();

        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.showAndWait();
    }
}
