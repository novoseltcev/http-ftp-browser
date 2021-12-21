package controller;


import app.CustomApp;
import javafx.stage.Stage;


public abstract class Controller {
    protected CustomApp app;

    public void setApp(CustomApp app) throws Exception {
        this.app = app;
        initialize();
    }

    protected abstract void initialize() throws Exception;
}
