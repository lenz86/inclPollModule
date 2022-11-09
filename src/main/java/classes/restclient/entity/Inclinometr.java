package classes.restclient.entity;


/**
 * @author Alexandr Kursakov
 */


public class Inclinometr extends SensorEntity {

    private String version;

    private String port;

    private String address;


    public Inclinometr() {
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    @Override
    public String toString() {
        return "Inclinometr{" +
                "version='" + version + '\'' +
                ", port='" + port + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
