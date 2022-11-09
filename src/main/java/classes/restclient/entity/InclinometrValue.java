package classes.restclient.entity;


/**
 * @author Alexandr Kursakov
 */

public class InclinometrValue extends DeviceValue {


    private String axisX;

    private String axisY;

    private Integer factoryId;


    public InclinometrValue() {
    }

    public InclinometrValue(String axisX, String axisY, Integer factoryId) {
        this.axisX = axisX;
        this.axisY = axisY;
        this.factoryId = factoryId;
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

    public Integer getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Integer factoryId) {
        this.factoryId = factoryId;
    }


    @Override
    public String toString() {
        return "InclinometrValue{" +
                "axisX='" + axisX + '\'' +
                ", axisY='" + axisY + '\'' +
                ", factoryId=" + factoryId +
                '}';
    }
}
