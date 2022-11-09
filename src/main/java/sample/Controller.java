package sample;

import classes.*;
import classes.dao.DBWorker;
import classes.restclient.Communication;
import classes.restclient.RestMessageSender;
import classes.restclient.RestTransferData;
import classes.restclient.entity.InclinometrValue;
import classes.websocket.ConnectWS;
import classes.websocket.WebSocketTransferData;
import classes.websocket.MyWebSocketClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import jssc.SerialPort;
import jssc.SerialPortList;
import org.springframework.context.ApplicationContext;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private ApplicationContext context;
    private ObservableList<String> portsBox = FXCollections.observableArrayList();
    private ObservableList<Incl> sensors = FXCollections.observableArrayList();
    private HashSet<SensorList> comPorts = new HashSet<>();
    private ArrayList<String> addresses = new ArrayList<>();
    private HashMap<Long, String> idFromDB;
    private SensorPoll sensorPoll;
    private SerialPort serialPort;
    private MyWebSocketClient myWebSocketClient;
    private static Logger logger = Logger.getLogger(Controller.class.getName());


    @FXML
    void clearSensorClick(MouseEvent event) {
        list1.getItems().clear();
        inclTable.getItems().clear();
        DBWorker.clearDB();
    }

    @FXML
    void scanClick(MouseEvent event) {

        /*CONNECT TO COM AND SEARCHING SENSORS*/
        String port = comBox.getValue();
        if (serialPort == null) {
            serialPort = new SerialPort(port);
        }
        if (!serialPort.isOpened()) {
            new SensorScan().start();
        }
    }


    @FXML
    void selectedItem(MouseEvent event) {
        if (inclTable.getItems().isEmpty()) {
            String port = list1.getSelectionModel().getSelectedItem();
            int pollingRate = 100;
            SerialPort serialPort = new SerialPort(port);
            SensorLineInD7 sensorLine = new SensorLineInD7(serialPort, pollingRate, 1, 64, 1);
            sensorLine.open();
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
            sensorLine.close();
        }
    }

    @FXML
    void startBtnClick(MouseEvent event) throws InterruptedException {
        if (!(sensorPoll == null) && !sensorPoll.isInterrupted()) {
            sensorPoll.interrupt();
        }
        /*CONNECT TO WEB SOCKET*/
        try {
            myWebSocketClient = ConnectWS.createClientConnection();
        } catch (ExecutionException | InterruptedException e) {
            logger.log(Level.WARNING, "UNCHECKED EXCEPTION!: ", e);
        }
        sensorPoll = new SensorPoll();
        sensorPoll.start();
    }

    @FXML
    void stopBtnClick(MouseEvent event) {
        sensorPoll.interrupt();
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
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.log(Level.WARNING, "UNCHECKED EXCEPTION!: ", e);
                }
            });

            log.appendText("Search for sensors... \n");
            String port = comBox.getValue();
            int pollingRate = 100;
            SensorLineInD7 sensorLine = new SensorLineInD7(serialPort, pollingRate, 1, 64, 1);
            sensorLine.open();
            progBar.setProgress(0);
            sensorLine.start();
            for (int i = 0; i < 64; i++) {
                double t = (double) i / 64.0;
                progBar.setProgress(t);
                try {
                    Thread.sleep(pollingRate);
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "Exception: ", e);
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
            sensorLine.close();
        }
    }

    class SensorPoll extends Thread {

        @Override
        public void run() {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.log(Level.WARNING, "UNCHECKED EXCEPTION!: ", e);
                }
            });

            String port = list1.getSelectionModel().getSelectedItem();
            RestMessageSender restMessageSender = new RestMessageSender(context.getBean("communication", Communication.class));
            int pollingRate = 100;
            int pollCount = 0;
            SensorLineInD7 sensorLine = new SensorLineInD7(serialPort, pollingRate, 1, 64, 1);
            sensorLine.open();
            while (!this.isInterrupted()) {
                for (Incl sensor : sensors) {
                    String[] tmp = sensorLine.readValues(ProtocolVersion.VERSION_2_11, Integer.parseInt(sensor.getAddress()));
                    sensor.setAxisX(tmp[0]);
                    sensor.setAxisY(tmp[1]);
                    //Write into DB and send data to server every 10th poll
                    if (pollCount == 10) {
                        String axisX = sensor.getAxisX();
                        String axisY = sensor.getAxisY();
                        Integer factoryId = Integer.parseInt(sensor.getFactoryID());
                        DBWorker.setValuesIntoDB(sensor.getIdFromDB(), axisX, axisY);
                        InclinometrValue inclinometrValue = new InclinometrValue(axisX, axisY, factoryId);
                        RestTransferData restTransferData = new RestTransferData(inclinometrValue);
                        restMessageSender.sendData(restTransferData);
                    }
                }
                if (pollCount < 10) {
                    pollCount++;
                } else {
                    pollCount = 0;
                }
                //Create data-object from 'sensors' array with current values
                WebSocketTransferData webSocketTransferData = new WebSocketTransferData(sensors.toArray());
                //convert to JSON
                String jsonMsg = JsonConverter.objectToJsonString(webSocketTransferData);
                //send to ws-server
                myWebSocketClient.send(jsonMsg);
                inclTable.refresh();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "Exception: ", e);
                    sensorLine.close();
                    this.interrupt();
                }
                System.out.println("Конец цикла опроса датчиков");
            }
        }
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}
