package controller;

import app.CustomApp;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FtpController extends Controller {
    public TreeView<String> tree;
    private final List<TreeItem<String>> cashed = new ArrayList<>();
    private static Image dirIcon;
    private static Image fileIcon;

    @Override
    protected void initialize() {

        try {
            File iconFile = new File(app.getClass().getResource("img/dir.svg").getFile());
            System.out.println(iconFile);

            InputStream io = new FileInputStream(iconFile);
            dirIcon = new Image(io);
            assert dirIcon.isError();
            System.out.println(dirIcon.isError());

            io = new FileInputStream(app.getClass().getResource("img/file.svg").getFile());
            fileIcon = new Image(io);
            assert fileIcon.isError();
            System.out.println(fileIcon.isError());
        } catch(IOException e) {}

        System.out.println(dirIcon.isError());
        FTPFile rootFTP = new FTPFile();
        rootFTP.setName("Files");
        TreeItem<String> root = new TreeItem<>(rootFTP.getName());
        tree.setRoot(root);
        crawl(root);

        tree.setOnMouseClicked(mouseEvent -> {
            TreeItem<String> item = tree.getSelectionModel().getSelectedItem();
            if (mouseEvent.getClickCount() >= 2 && item != tree.getRoot() && item != null) {
                if (item.isExpanded() && !cashed.contains(item)) {
                    crawl(item);
                }

                if (!item.isExpanded()) {
                    String name = item.getValue();
                    String value = getAbsolutePath(item);
                    System.out.println("Selected: " + value);
                    try {
                        if (!app.ftp.downloadFile(value, app.chosenDir + "/" + name)) {
                            System.out.println("> Загрузка файла с FTP-сервера ПРЕРВАНА");
                        } else {
                            System.out.println("> Загрузка файла с FTP-сервера завершена");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void crawl(TreeItem<String> root) {
        cashed.add(root);

        String rootLink;
        if (root == tree.getRoot()) {
            rootLink = "";
        } else {
            rootLink = getAbsolutePath(root);
        }
        System.out.println("crawling: " + rootLink);

        try {
            root.getChildren().addAll(
                    Arrays.stream(app.ftp.listFiles(rootLink))
                            .filter(x -> !x.isSymbolicLink())
                            .map(file -> {
                                TreeItem<String> result = new TreeItem<>(file.getName(), new ImageView((file.isDirectory()) ? dirIcon : fileIcon));
                                result.setExpanded(true);
                                return result;
                            })
                            .toList()
            );
            tree.refresh();
        } catch (IOException ignored) {
        }
    }

    private String getAbsolutePath(TreeItem<String> node) {
        StringBuilder rootLink = new StringBuilder(node.getValue());
        TreeItem<String> cur = node.getParent();
        while (cur != tree.getRoot()) {
            rootLink.insert(0, cur.getValue() + "/");
            cur = cur.getParent();
        }
        return rootLink.toString();
    }

}
