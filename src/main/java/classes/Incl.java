package classes;

import java.util.Objects;

public class Incl {
    private String axisX;
    private String axisY;
    private String name;
    private String comPort;
    private Long idFromDB;
    private String factoryID;
    private String version;
    private String address;

    public Incl(String name, String factoryID, String version, String comPort, String address) {
        this.name = name;
        this.factoryID = factoryID;
        this.version = version;
        this.comPort = comPort;
        this.address = address;
    }

    public String getAxisX() {
        return axisX;
    }

    public void setAxisX(String axisX) {
        this.axisX = axisX;
    }

    public String getAxisY() {
        return axisY;
    }

    public void setAxisY(String axisY) {
        this.axisY = axisY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFactoryID() {
        return factoryID;
    }

    public void setFactoryID(String factoryID) {
        this.factoryID = factoryID;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getIdFromDB() {
        return idFromDB;
    }

    public void setIdFromDB(Long idFromDB) {
        this.idFromDB = idFromDB;
    }

    public String getComPort() {
        return comPort;
    }

    public void setComPort(String comPort) {
        this.comPort = comPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Incl incl = (Incl) o;
        return factoryID.equals(incl.factoryID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factoryID);
    }

    @Override
    public String toString() {
        return "Incl{" +
                "axisX=" + axisX +
                ", axisY=" + axisY +
                ", name='" + name + '\'' +
                ", factoryID='" + factoryID + '\'' +
                ", version='" + version + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
