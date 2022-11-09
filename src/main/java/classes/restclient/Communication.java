package classes.restclient;

import classes.restclient.entity.InclinometrValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import sample.Main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class Communication {

    private RestTemplate restTemplate;
    private static Properties properties = readProperties();
    private static final String SERVER = properties.getProperty("server");
    private static final String PORT = properties.getProperty("port");
    private static final String REST_API_URL = "http://" + SERVER + ":" + PORT + "/api/";

    @Autowired
    public Communication(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*Read settings for connection to app-server from .properties*/
    private static Properties readProperties() {
        //get app.properties as Stream
        InputStream stream = Main.class.getResourceAsStream("/app.properties");
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public boolean saveInclinometrValue(InclinometrValue value) {
        String URL = REST_API_URL + "sensors/" + value.getFactoryId() + "/values";
        System.out.println(URL);
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(URL, value, String.class);
        return responseEntity.getStatusCode().equals(HttpStatus.OK);
    }
}
