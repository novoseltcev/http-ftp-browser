package controller;

import app.Browser;
import app.Notifier;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import utils.CustomFtpClient;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

public class MainController extends Controller {

    public TextField urlTextField;
    public Button openButton;

    public TextField ftpTextField;
    public Text chosenDirName;
    public Button downloadButton;
    public Button dirButton;
    public ChoiceBox<String> choiceProtocol;

    @Override
    protected void initialize() throws Exception {
        if (app.ftp != null && app.ftp.isAuthorized && app.chosenDir != null) {
            app.nextScene("ftp-view.fxml", app.ftp.getServer(), 500, 500, 1920, 1080);
        }
        openButton.setDisable(true);
        downloadButton.setDisable(true);
        choiceProtocol.setItems(
                FXCollections.observableArrayList("HTTP", "HTTPS")
        );

        initListeners();
    }

    private void initListeners() {
        urlTextField.textProperty().addListener(observable -> openButton.setDisable(urlTextField.getText().isEmpty() || choiceProtocol.getValue() == null));
        ftpTextField.textProperty().addListener(observable -> downloadButton.setDisable(ftpTextField.getText().isEmpty() || app.chosenDir == null));

        choiceProtocol.valueProperty().addListener(observable -> openButton.setDisable(urlTextField.getText().isEmpty() || choiceProtocol.getValue() == null));
    }

    private URL getURL(String preURL, String protocol) throws MalformedURLException {
        URL result;
        try {
            result = new URL(preURL);
        } catch (MalformedURLException e) {
            return new URL(protocol + "://" + preURL);
        }
        if (!Objects.equals(result.getProtocol(), protocol)) {
            throw new MalformedURLException();
        }
        return new URL(preURL);
    }

    public void openHtmlAction() throws Exception {
        try {
            URL serverURL = getURL(urlTextField.getText().toLowerCase(), choiceProtocol.getValue().toLowerCase());
            URLConnection connection = serverURL.openConnection();

            if (connection.getContentType() == null) {
                throw new MalformedURLException();
            }

            Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
            saveFile(Path.of("data", "loaded.html"), reader);
            reader.close();

            new Browser();

        } catch (MalformedURLException e) {
            new Notifier(e, Alert.AlertType.WARNING);
        }
    }

    public void downloadAction() throws Exception {
        URL serverURL = getURL(ftpTextField.getText().toLowerCase(), "ftp");
        System.out.println(serverURL.getHost() + " " + serverURL.getPort());
        app.ftp = CustomFtpClient.connect(serverURL);
        System.out.println("Connected to " + app.ftp.getServer());

        app.getStage().setTitle(serverURL.getHost());
        app.nextScene("login-view.fxml", "Login",350, 200, 350, 200);
    }

    public void openDirAction() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose directory");

        chooser.setInitialDirectory(new File("").getParentFile());
        File selectedFile = chooser.showDialog(app.getStage());
        System.out.println(selectedFile);
        if (selectedFile != null && selectedFile.isDirectory()) {
            app.chosenDir = Path.of(selectedFile.getAbsolutePath());
            chosenDirName.setText(app.chosenDir.toString());
        }
    }

    private void saveFile(Path path, Reader reader) throws IOException {
        path.getParent().toFile().mkdirs();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile()), StandardCharsets.UTF_8));
        reader.transferTo(out);
        out.close();
    }

    public void enterHtmlType() throws Exception {
        if (!openButton.isDisable())
            openHtmlAction();
    }

    public void enterFtpType() throws Exception {
        if (!downloadButton.isDisable())
            downloadAction();
    }
}
