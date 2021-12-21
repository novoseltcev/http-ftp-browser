package app;

import controller.Controller;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.CustomFtpClient;

import java.net.URL;
import java.nio.file.Path;


public abstract class CustomApp extends Application {
    private final String startFxmlFilename;
    private final String title;
    private final int[] borders;
    public Path chosenDir;
    protected Stage stage;

    private ObservableList<Controller> controllers = FXCollections.observableArrayList();
    private ObservableList<Scene> scenes = FXCollections.observableArrayList();
    private ObservableList<String> titles = FXCollections.observableArrayList();
    private ObservableList<int[]> borders_s = FXCollections.observableArrayList();
    private int depth = 0;

    public CustomFtpClient ftp;

    protected CustomApp(String startFxmlFilename, String title, int... borders) {
        super();

        this.startFxmlFilename = startFxmlFilename;
        this.title = title;
        this.borders = borders;
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        this.nextScene(startFxmlFilename, title, borders);
        show();
    }

    public void nextScene(String fxmlFilename, String title, int ...borders) throws Exception {
        URL resource = getClass().getResource(fxmlFilename);
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        System.out.println(fxmlLoader.getLocation());
        Scene scene = new Scene(fxmlLoader.load());
        Controller controller = fxmlLoader.getController();
        controller.setApp(this);

        scenes.add(scene);
        controllers.add(controller);

        int[] tmpBorders = new int[4];
        System.arraycopy(borders, 0, tmpBorders, 0, borders.length);

        switch (borders.length) {
            case (0):
                tmpBorders[0] = 300;

            case (1):
                tmpBorders[1] = 200;

            case (2):
                tmpBorders[2] = 1920;

            case (3):
                tmpBorders[3] = 1080;

            case (4):
                break;

            default:
                throw new AssertionError();
        }

        this.borders_s.add(tmpBorders);
        this.titles.add(title);

        this.stage.setTitle(title);
        this.setBoundary(tmpBorders[0], tmpBorders[1], tmpBorders[2], tmpBorders[3]);
        this.stage.setScene(scene);
        this.depth++;
    }

    public void prevScene() throws Exception {
        assert depth > 1;
        scenes.remove(depth -1);
        controllers.remove(depth - 1);
        titles.remove(depth - 1);
        borders_s.remove(depth - 1);
        depth--;

        controllers.get(depth - 1).setApp(this);

        this.stage.setScene(
                scenes.get(depth - 1));
        this.stage.setTitle(
                titles.get(depth - 1));
        int[] border = this.borders_s.get(depth - 1);
        this.setBoundary(
               border[0], border[1], border[2], border[3]);
    }

    public void setBoundary(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        this.stage.setMinWidth(minWidth);
        this.stage.setMinHeight(minHeight);
        this.stage.setMaxWidth(maxWidth);
        this.stage.setMaxHeight(maxHeight);
    }

    public void setPosition(double X, double Y) {
        this.stage.setX(X);
        this.stage.setY(Y);
    }

    public void setPositionToCentral() {
        setPosition(
                (int) (1920 - this.stage.getMinWidth()) >> 1,
                (int) (1080 - this.stage.getMinHeight()) >> 1
        );
    }

    protected abstract void show();
}
