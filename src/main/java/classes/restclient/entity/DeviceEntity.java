package classes.restclient.entity;



import java.io.Serializable;

/**
 * Base entity for all Device-associated classes
 *
 * @author Alexandr Kursakov
 *
 */


public class DeviceEntity implements Serializable {


    private Integer factoryId;

    public DeviceEntity() {
    }

    public Integer getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Integer factoryId) {
        this.factoryId = factoryId;
    }

    public boolean isNew() {
        return this.factoryId == null;
    }


    @Override
    public String toString() {
        return "DeviceEntity{" +
                "factoryId=" + factoryId +
                '}';
    }
}
