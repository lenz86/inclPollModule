package sample;

import classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private ListView<String> list1;

    @FXML
    private TextArea log;

    @FXML
    private Button scanBtn;

    @FXML
    private Button startBtn;

    @FXML
    private Button clrSensorBtn;

    @FXML
    private Button stopBtn;

    @FXML
    private TableView<Incl> inclTable;

    @FXML
    private TableColumn<Incl, String> addressColumn;

    @FXML
    private TableColumn<Incl, String> factoryIdColumn;

    @FXML
    private TableColumn<Incl, String> xColumn;

    @FXML
    private TableColumn<Incl, String> yColumn;

    @FXML
    private ChoiceBox<String> comBox;

    @FXML
    private ProgressBar progBar;

    private ObservableList<String> portsBox = FXCollections.observableArrayList();
    private ObservableList<Incl> sensors = FXCollections.observableArrayList();
    private HashSet<SensorList> comPorts = new HashSet<>();
    private ArrayList<String> addresses = new ArrayList<>();
    private HashMap<Long, String> idFromDB;
    private SensorPoll sensorPoll;
    private SerialPort serialPort;


    @FXML
    void clearSensorClick(MouseEvent event) {
        list1.getItems().clear();
        inclTable.getItems().clear();
        DBWorker.clearDB();
    }

    @FXML
    void scanClick(MouseEvent event) {
        try {
            String port = comBox.getValue();
            if (serialPort == null) {
                serialPort = new SerialPort(port);
            }
            if (!serialPort.isOpened()) {
                serialPort.openPort();
                serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            }
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        new SensorScan().start();
    }

    @FXML
    void selectedItem(MouseEvent event) {
        if (inclTable.getItems().isEmpty()) {
            String port = list1.getSelectionModel().getSelectedItem();
            int pollingRate = 1000;
            SerialPort serialPort = new SerialPort(port);
            SensorLineInD7 sensorLine = new SensorLineInD7(serialPort, pollingRate, 1, 64, 1);
            SensorList sensorList = sensorLine.inclFactory(addresses, port);
            comPorts.add(sensorList);
            HashSet<Incl> sensorList2 = sensorList.getSensors();
            for (Incl incl : sensorList2) {
                sensors.add(incl);
            }
            inclTable.setItems(sensors);
            DBWorker.setSensors(sensorList2);
            idFromDB = DBWorker.getIdFromDB(sensorList2);
            for (Map.Entry<Long, String> longStringEntry : idFromDB.entrySet()) {
                for (Incl sensor : sensors) {
                    if (sensor.getFactoryID().equals(longStringEntry.getValue())) {
                        sensor.setIdFromDB(longStringEntry.getKey());
                    }
                }
            }
        }
    }

    @FXML
    void startBtnClick(MouseEvent event) throws InterruptedException {
        if (!(sensorPoll == null) && !sensorPoll.isInterrupted()) {
            sensorPoll.interrupt();
        }
        sensorPoll = new SensorPoll();
        sensorPoll.start();
    }

    @FXML
    void stopBtnClick(MouseEvent event) {
        sensorPoll.interrupt();
//        sensorPoll.interrupt();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stopBtn.disableProperty().set(false);
        addressColumn.setCellValueFactory(new PropertyValueFactory<Incl, String>("address"));
        factoryIdColumn.setCellValueFactory(new PropertyValueFactory<Incl, String>("factoryID"));
        xColumn.setCellValueFactory(new PropertyValueFactory<Incl, String>("axisX"));
        yColumn.setCellValueFactory(new PropertyValueFactory<Incl, String>("axisY"));
        getPorts();
    }

    public void getPorts() {
        portsBox.removeAll(portsBox);
        portsBox.addAll(SerialPortList.getPortNames());
        comBox.getItems().addAll(portsBox);
    }

    class SensorScan extends Thread {

        @Override
        public void run() {
            log.appendText("Search for sensors... \n");
            String port = comBox.getValue();
            int pollingRate = 100;
//            SerialPort serialPort = new SerialPort(port);
            SensorLineInD7 sensorLine = new SensorLineInD7(serialPort, pollingRate, 1, 64, 1);
            progBar.setProgress(0);
            sensorLine.start();
            for (int i = 0; i < 64; i++) {
                double t = (double) i / 64.0;
                progBar.setProgress(t);
                try {
                    Thread.sleep(pollingRate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (sensorLine.hasSensor()) {
                    addresses.add(sensorLine.getSensorAddress());
                    log.appendText("Sensor found! Address: " + addresses.get(addresses.size() - 1) + "\n");
                }
            }
            log.appendText("Search for sensors is over \n");
            if (addresses.size() > 0 && !list1.getItems().contains(port)) {
                list1.getItems().add(port);
            }
        }
    }

    class SensorPoll extends Thread {

        @Override
        public void run() {
            String port = list1.getSelectionModel().getSelectedItem();
            int pollingRate = 100;
//            serialPort = new SerialPort(port);
            SensorLineInD7 sensorLine = new SensorLineInD7(serialPort, pollingRate, 1, 64, 1);
            while (!this.isInterrupted()) {
                for (Incl sensor : sensors) {
                    String[] tmp = sensorLine.readValues(ProtocolVersion.VERSION_2_11, Integer.parseInt(sensor.getAddress()));
                    sensor.setAxisX(tmp[0]);
                    sensor.setAxisY(tmp[1]);
                    DBWorker.setValuesIntoDB(sensor.getIdFromDB(), sensor.getAxisX(), sensor.getAxisY());
                }
                inclTable.refresh();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    this.interrupt();
                    try {
                        serialPort.closePort();
                    } catch (SerialPortException ex) {
                        ex.printStackTrace();
                    }
                }
                System.out.println("Конец цикла опроса датчиков");
            }
        }
    }

}