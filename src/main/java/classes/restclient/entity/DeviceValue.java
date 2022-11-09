package classes.restclient.entity;

import java.sql.Timestamp;

/**
 * Base entity for all Value-associated classes
 *
 * @author Alexandr Kursakov
 *
 */

public class DeviceValue {


    private Long id;

    private Timestamp date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
