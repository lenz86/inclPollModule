package classes.restclient;

import classes.restclient.entity.InclinometrValue;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class RestTransferData {

    private Timestamp regTime;
    private InclinometrValue inclinometrValue;

    public RestTransferData(InclinometrValue inclinometrValue) {
        //set current time as timestamp
        this.regTime = Timestamp.valueOf(LocalDateTime.now());
        this.inclinometrValue = inclinometrValue;
        inclinometrValue.setDate(regTime);
    }

    public Timestamp getRegTime() {
        return regTime;
    }

    public void setRegTime(Timestamp regTime) {
        this.regTime = regTime;
    }

    public InclinometrValue getInclinometrValue() {
        return inclinometrValue;
    }

    public void setInclinometrValue(InclinometrValue inclinometrValue) {
        this.inclinometrValue = inclinometrValue;
    }
}
