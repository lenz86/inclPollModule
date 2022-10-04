package classes;


import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SensorList {
    private String portName;
    private HashSet<Incl> sensors = new HashSet<>();

    public SensorList(String portName) {
        this.portName = portName;
    }

    public void addSensor(Incl incl) {
        sensors.add(incl);
    }

    public HashSet<Incl> getSensors() {
        return sensors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorList that = (SensorList) o;
        return Objects.equals(portName, that.portName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portName);
    }
}
