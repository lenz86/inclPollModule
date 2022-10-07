package classes;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {

    public static String objectToJsonString(Object pojo) {
        String jsonString = "";
        ObjectMapper mapper = new ObjectMapper();

        try {
            jsonString = mapper.writeValueAsString(pojo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
