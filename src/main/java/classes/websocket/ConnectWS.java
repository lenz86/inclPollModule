package classes.websocket;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ConnectWS {

    private static Properties properties;
    private static String server;
    private static String port;
    private static MyWebSocketClient client;

    private ConnectWS() {

    }

    /*Create web-socket client and read properties from file*/
    public static MyWebSocketClient createClientConnection() throws ExecutionException, InterruptedException {
        properties = readProperties();
        server = properties.getProperty("server");
        port = properties.getProperty("port");
        client = new MyWebSocketClient();
        client.connect(server, port);
        return client;
    }

    /*Read settings for web-socket connection from .properties*/
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
