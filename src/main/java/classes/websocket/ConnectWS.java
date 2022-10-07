package classes.websocket;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ConnectWS {

    private static Properties properties;
    private static String server;
    private static String port;
    private static MyWebSocketClient client;

    public ConnectWS() {

    }

    public static MyWebSocketClient createClientConnection() throws ExecutionException, InterruptedException {
        properties = readProperties();
        server = properties.getProperty("server");
        port = properties.getProperty("port");
        client = new MyWebSocketClient();
        client.connect(server, port);
        return client;
    }


    private static Properties readProperties() {
        //get resource folder
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        //create app.properties path (/resource/app.properties)
        String appConfigPath = rootPath + "app.properties";
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
