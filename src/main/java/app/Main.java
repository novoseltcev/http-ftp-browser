package app;

import java.io.IOException;


public class Main extends CustomApp {

    public Main() throws IOException {
        super("main-view.fxml", "Main", 600, 250, 600, 250);
    }

    public static void main(String[] args) throws Exception {
//        try {
        launch();
//        } catch (Exception e) {
//            System.out.println(Arrays.toString(e.getStackTrace()));
//            new app.NotifyApp(e, Alert.AlertType.ERROR);
//        }
    }

    @Override
    protected void show() {
        this.setPositionToCentral();
        this.stage.show();
    }
}