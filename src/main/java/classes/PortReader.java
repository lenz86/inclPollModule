package classes;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortReader implements SerialPortEventListener {
    private SerialPort serialPort;
    private ConcurrentLinkedQueue<String> data = new ConcurrentLinkedQueue<String>();
    private static Logger log = Logger.getLogger(PortReader.class.getName());

    public PortReader(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public String readData() {
        return data.poll();
    }

    public boolean hasData() {
        if (data.size() > 0) return true;
        return false;
    }

    public void serialEvent(SerialPortEvent event) {
        if (event.getEventValue() > 0) {
            try {
                //Получаем ответ от устройства, обрабатываем данные и т.д.
                data.offer(serialPort.readHexString(event.getEventValue()));
            } catch (SerialPortException ex) {
                log.log(Level.WARNING, "EXCEPTION!: ", ex);
            }
        }

    }


}

