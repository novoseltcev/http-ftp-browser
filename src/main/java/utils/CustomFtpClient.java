package utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class CustomFtpClient extends FTPClient {
    private final String server;
    private final int port;
    public boolean isAuthorized = false;

    CustomFtpClient(URL url) {
        super();
        server = url.getHost();
        port = 21;
    }

    public String getServer() {
        return server;
    }

    public static CustomFtpClient connect(URL url) throws IOException {
        CustomFtpClient client = new CustomFtpClient(url);
        client.connect(client.server, client.port);
        client.enterLocalPassiveMode();
        int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            client.abort();
            client.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }
        return client;
    }

    public boolean login(String username, String password) throws IOException {
        if (!super.login(username, password)) {
            System.out.println("Failed auth");
            throw new IOException("Exception in login to FTP Server");
        }
        System.out.println("Successful auth");
        isAuthorized = true;
        return true;
    }

    public boolean downloadFile(String source, String destination) throws IOException {
        FileOutputStream out = new FileOutputStream(destination);
        return retrieveFile(source, out);
    }


    public void downloadSingleFile(String absoluteLocalDirectory, String relativeRemotePath) {
        if (!isConnected() || !isAvailable()) {
            System.out.println(">>>>> Соединение с FTP-сервером было закрыто или соединение неверно *********");
            return;
        }
//        if (StringUtils.isBlank(absoluteLocalDirectory) || StringUtils.isBlank(relativeRemotePath)) {
//            System.out.println (">>>>> Если при загрузке обнаружен локальный путь к хранилищу или путь к файлу ftp-сервера пуст, откажитесь ... *********");
//            return;
//        }
        try {
            FTPFile[] ftpFiles = listFiles(relativeRemotePath);
            FTPFile ftpFile = null;
            if (ftpFiles.length >= 1) {
                ftpFile = ftpFiles[0];
            }
            if (ftpFile != null && ftpFile.isFile()) {
                File localFile = new File(absoluteLocalDirectory, relativeRemotePath);
                if (!localFile.getParentFile().exists()) {
                    localFile.getParentFile().mkdirs();
                }
                OutputStream outputStream = new FileOutputStream(localFile);
                String workDir = relativeRemotePath.substring(0, relativeRemotePath.lastIndexOf("/"));
//                if (StringUtils.isBlank(workDir)) {
//                    workDir = "/";
//                }
                changeWorkingDirectory(workDir);
                retrieveFile(ftpFile.getName(), outputStream);

                outputStream.flush();
                outputStream.close();
                System.out.println(">>>>> Загрузка файла с FTP-сервера завершена *********" + ftpFile.getName());
            } else {
                System.out.println("Interrupted");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}