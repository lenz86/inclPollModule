package classes;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonConverter {
    private static Logger log = Logger.getLogger(JsonConverter.class.getName());

    public static String objectToJsonString(Object pojo) {
        String jsonString = "";
        ObjectMapper mapper = new ObjectMapper();

        try {
            jsonString = mapper.writeValueAsString(pojo);
        } catch (JsonProcessingException e) {
            log.log(Level.WARNING, "EXCEPTION!: ", e);
        }
        return jsonString;
    }
}
