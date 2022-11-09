package classes.restclient.entity;

/**
 * Base entity for all Sensor-associated classes
 *
 * @author Alexandr Kursakov
 */


public class SensorEntity extends DeviceEntity {

    private String projectName;

    public SensorEntity() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return "factoryId='" + super.getFactoryId() + '\'' +
                ", projectName='" + projectName + '\'';
    }
}
