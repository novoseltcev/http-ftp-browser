package app;

import javafx.stage.Modality;
import javafx.stage.Stage;


public class Browser extends CustomApp {
    public Browser() throws Exception {
        super("browser-html-view.fxml", "Browser");
        start(new Stage());
    }

    @Override
    protected void show() {
        this.stage.setFullScreen(true);
        this.setPositionToCentral();
        this.stage.initModality(Modality.WINDOW_MODAL);
        this.stage.showAndWait();
    }
}
