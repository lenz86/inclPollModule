package classes;


import java.sql.*;

import java.util.HashMap;
import java.util.HashSet;

public class DBWorker {

    public static void setValuesIntoDB(Long sensorId, String axisX, String axisY) {
        String query = "INSERT INTO incl_values (sensor_id, axisx, axisy, date)" + "VALUES (?, ?, ?, ?)";
        DBConnection dbConnection = new DBConnection();
        java.util.Date utilDate = new java.util.Date();
        java.sql.Timestamp sqlTS = new java.sql.Timestamp(utilDate.getTime());
        try {
            PreparedStatement prSt = dbConnection.getConnection().prepareStatement(query);
            prSt.setLong(1, sensorId);
            prSt.setString(2, axisX);
            prSt.setString(3, axisY);
            prSt.setTimestamp(4, sqlTS);
            prSt.executeUpdate();
            prSt.close();
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setSensors(HashSet<Incl> sensors) {
        DBConnection dbConnection = new DBConnection();
        String selectAll = "SELECT * FROM incl";
        String insertSensor = "INSERT INTO incl (factory_id, port, address, version) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement prSt1 = dbConnection.getConnection().prepareStatement(selectAll,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = prSt1.executeQuery(selectAll);
            boolean hasInDB = false;
            for (Incl sensor : sensors) {
                while (resultSet.next()) {
                    //если в таблице уже есть датчик с таким заводским номером - устанавливаем в true флаг hasInDB
                    if (resultSet.getString("factory_id").equals(sensor.getFactoryID())) {
                        hasInDB = true;
                    }
                }
                //если флаг hasInDB false (т.е в БД нет датчика с таким зав.номером) - добавляем в БД датчик
                if (!hasInDB) {
                    PreparedStatement prSt2 = dbConnection.getConnection().prepareStatement(insertSensor);
                    prSt2.setString(1, sensor.getFactoryID());
                    prSt2.setString(2, sensor.getComPort());
                    prSt2.setString(3, sensor.getAddress());
                    prSt2.setString(4, sensor.getVersion());
                    prSt2.executeUpdate();
                    prSt2.close();
                }
                resultSet.first();
            }
            prSt1.close();
            resultSet.close();
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //получаем из БД ID датчиков с переданными заводскими номерами
    public static HashMap<Long, String> getIdFromDB(HashSet<Incl> sensors) {
        HashMap<Long, String> sensorId = new HashMap<>();
        DBConnection dbConnection = new DBConnection();
        String selectAll = "SELECT * FROM incl";
        try {
            PreparedStatement prSt1 = dbConnection.getConnection().prepareStatement(selectAll,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = prSt1.executeQuery(selectAll);
            for (Incl sensor : sensors) {
                while (resultSet.next()) {
                    if (resultSet.getString("factory_id").equals(sensor.getFactoryID())) {
                        sensorId.put(resultSet.getLong("id"), sensor.getFactoryID());
                    }
                }
                resultSet.first();
            }
            prSt1.close();
            resultSet.close();
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sensorId;
    }

    public static void clearDB() {
        DBConnection dbConnection = new DBConnection();
        String deleteAllFromValues = "DELETE FROM incl_values";
        String deleteAllFromIncl = "DELETE FROM incl";
        try {
            PreparedStatement prSt1 = dbConnection.getConnection().prepareStatement(deleteAllFromValues);
            prSt1.executeUpdate();
            PreparedStatement prSt2 = dbConnection.getConnection().prepareStatement(deleteAllFromIncl);
            prSt2.executeUpdate();
            prSt1.close();
            prSt2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
