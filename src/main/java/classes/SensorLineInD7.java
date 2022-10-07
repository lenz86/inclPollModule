package classes;

import classes.PackageHandler;
import classes.PortReader;
import classes.ProtocolVersion;
import classes.SensorList;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;


public class SensorLineInD7 extends Thread {
    private static final int[] PING_SENSOR = {0x7E, 0x9A, 0x03, 0x01, 0xE4, 0x7E};
    private static final int[] READ_VERSION = {0x7E, 0x9B, 0x0E, 0x01, 0x94, 0x7E};
    private static final int[] READ_VALUES_211 = {0x7E, 0x9B, 0x01, 0x01, 0x9B, 0x7E};
    private static final int[] READ_VALUES_210 = {0x7E, 0x9A, 0x01, 0x01, 0xFE, 0x7E};
    private static final int[] READ_FACTORY_ID = {0x7E, 0x9C, 0x0B, 0x01, 0x96, 0x7E};
    private static final int[] WRITE_NEW_LOG_NUMBER = {0x7E, 0x9C, 0x09, 0x01, 0x01, 0x96, 0x7E};
    private String portName;
    private SerialPort serialPort;
    private int pollingRate;
    private int startAddress;
    private int endAddress;
    private int commandType;
    private ConcurrentLinkedDeque<String> sensorScan = new ConcurrentLinkedDeque<>();
    private HashSet<String> sensorList = new HashSet<>();

    public SensorLineInD7(SerialPort serialPort, int pollingRate, int startAddress, int endAddress, int commandType) {
        this.portName = serialPort.getPortName();
        this.serialPort = serialPort;
        this.pollingRate = pollingRate;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.commandType = commandType;
    }

    @Override
    public void run() {
        switch (commandType) {
            case 1:
                pingSensors();
                break;
        }
    }

    public String getSensorAddress() {
        return sensorScan.pollLast();
    }

    public boolean hasSensor() {
        if (sensorScan.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void pingSensors() {
        try {
            PortReader portReader = new PortReader(serialPort);
            serialPort.addEventListener(portReader, SerialPort.MASK_RXCHAR);
            for (int address = startAddress; address <= endAddress; address++) {
                int[] command = PING_SENSOR;
                command[3] = address;
                serialPort.writeIntArray(command);
                Thread.sleep(pollingRate);
                if (portReader.hasData()) {
                    String data = portReader.readData();
                    if (data.equals("35")) {
                        sensorScan.offer(String.valueOf(address));
                        sensorList.add(String.valueOf(address));
                    } else System.out.println("Another data! " + data);  //Доделать
                }
            }
        } catch (SerialPortException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public SensorList inclFactory(ArrayList<String> addresses, String portName) {
        String factoryId;
        SensorList sensorList = new SensorList(portName);
        for (String address : addresses) {
            factoryId = readFactoryID(Integer.parseInt(address));
            sensorList.addSensor(new Incl("test", factoryId, "2.11", portName, address));
        }
        return sensorList;
    }

    public String readVersion(int address) {
        String version = "";
        try {
            PortReader portReader = new PortReader(serialPort);
            serialPort.addEventListener(portReader, SerialPort.MASK_RXCHAR);
            int[] command = READ_VERSION;
            command[3] = address;
            serialPort.writeIntArray(command);
            Thread.sleep(pollingRate);
            if (portReader.hasData()) {
                String data = portReader.readData();
                version = PackageHandler.pullVersion(data);                   //Отправлять в парсер пакетов
            }
            serialPort.removeEventListener();
        } catch (SerialPortException | InterruptedException e) {
            e.printStackTrace();
        }
        return version;
    }

    public String readFactoryID(int address) {
        String factoryID = "";
        try {
            PortReader portReader = new PortReader(serialPort);
            serialPort.addEventListener(portReader, SerialPort.MASK_RXCHAR);
            int[] command = READ_FACTORY_ID;
            command[3] = address;
            serialPort.writeIntArray(command);
            Thread.sleep(pollingRate);
            if (portReader.hasData()) {
                    String data = portReader.readData();
                factoryID = PackageHandler.pullFactoryId(data);                    //Отправлять в парсер пакетов
            }
            serialPort.removeEventListener();
        } catch (SerialPortException | InterruptedException e) {
            e.printStackTrace();
        }
        return factoryID;
    }

    public String[] readValues(ProtocolVersion version, int address) {
        String[] values = new String[2];
        try {
            PortReader portReader = new PortReader(serialPort);
            serialPort.addEventListener(portReader, SerialPort.MASK_RXCHAR);
            switch (version) {
                case VERSION_2_10:
                    int[] command1 = READ_VALUES_210;
                    command1[3] = address;
                    serialPort.writeIntArray(command1);
                    Thread.sleep(pollingRate);
                    break;
                case VERSION_2_11:
                    int[] command2 = READ_VALUES_211;
                    command2[3] = address;
                    serialPort.writeIntArray(command2);
                    Thread.sleep(pollingRate);
                    break;
            }
            if (portReader.hasData()) {
                String data = portReader.readData();
                values = PackageHandler.pullValues(data); //Отправлять в парсер пакетов
            }
            serialPort.removeEventListener();
        } catch (SerialPortException | InterruptedException e) {
            e.printStackTrace();
        }
        return values;
    }

    public static int getCheckSum(ArrayList<Integer> data) {
        int result = 0;
        for (int i = 1; i < data.size(); i++) {
            result ^= data.get(i);
        }
        return result;
    }

    public void close() {
        if (serialPort.isOpened()) {
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }

    public void open() {
        if (!serialPort.isOpened()) {
            try {
                serialPort.openPort();
                serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }


}
