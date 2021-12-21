package utils;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Crawler implements Runnable {
    private final FTPClient ftp;
    TreeView<FTPFile> tree;

    public Crawler(FTPClient ftp, TreeView<FTPFile> tree) {
        this.ftp = ftp;
        this.tree = tree;
    }

    private void crawl(TreeItem<FTPFile> root) {
        String rootLink = root.getValue().getLink();
//        System.out.println("RootLink: " + rootLink);
        try {
            FTPFile[] files = ftp.listFiles(rootLink);
            FTPFile[] dirs = ftp.listDirectories(rootLink);
            root.getChildren().addAll(
                    Arrays.stream(files)
                            .filter(x -> !x.isSymbolicLink())
                            .map(TreeItem::new)
                            .toList()
            );

            List<TreeItem<FTPFile>> dirsStream =
                    Arrays.stream(dirs)
                            .filter(x -> !x.isSymbolicLink())
                            .map(x -> {
                                TreeItem<FTPFile> result = new TreeItem<>(x);
                                result.setExpanded(true);
                                return result;
                            }).toList();

            root.getChildren().addAll(dirsStream);

            for (TreeItem<FTPFile> item : dirsStream) {
                crawl(item);
                tree.refresh();
            }
        } catch (IOException ignored) {}
    }

    @Override
    public void run() {
        for (TreeItem<FTPFile> item: tree.getRoot().getChildren()) {
            if (item.getValue().isDirectory()) {
                crawl(item);
            }
        }
        System.out.println("Crawler fell asleep");
    }
}
