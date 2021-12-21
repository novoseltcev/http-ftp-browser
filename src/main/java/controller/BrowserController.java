package controller;


import app.Notifier;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class BrowserController extends Controller {
    public WebView browser;

    @Override
    public void initialize() throws Exception {
        WebEngine engine = browser.getEngine();
        try {
            String html = loadHtml(Path.of("data", "loaded.html"));
            engine.loadContent(html);
            browser.setVisible(true);
        } catch (IOException e) {
            new Notifier(e, Alert.AlertType.ERROR);
            app.getStage().close();
        }
    }

    private String loadHtml(Path path) throws IOException {
        int b;

        path.getParent().toFile().mkdirs();

        InputStream in = new FileInputStream(path.toFile());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while ((b = in.read()) != -1) {
            baos.write(b);
        }

        in.close();
        return baos.toString(StandardCharsets.UTF_8);
    }
}
