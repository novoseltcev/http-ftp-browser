package controller;


import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

import java.io.IOException;

public class LoginController extends Controller {
    public Label errorLabel;
    public TextField userField;
    public PasswordField passwordField;

    @Override
    protected void initialize() {}

    public void login() {
        String username = userField.getText();
        String password = passwordField.getText();
       try {
           if (username.isEmpty()){
               username = "anonymous";
           }
           app.ftp.login(username, password);
           app.prevScene();
       } catch (IOException e) {
           errorLabel.setTextFill(Paint.valueOf("#d72121"));
           errorLabel.setText(e.getMessage());
       } catch (Exception e) {
           e.printStackTrace();
       }
    }
}

