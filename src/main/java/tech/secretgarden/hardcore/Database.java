package tech.secretgarden.hardcore;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database {
    /*
private final String HOST = "";
private final int PORT = 3306;
private final String DATABASE = "";
private final String USERNAME = "";
private final String PASSWORD = "";
 */

    static ArrayList<String> list = Hardcore.configList;
    public static ComboPooledDataSource pool;

    public static void connect() throws SQLException {

        try {
            pool = new ComboPooledDataSource();
            pool.setDriverClass("com.mysql.jdbc.Driver");
            pool.setJdbcUrl("jdbc:mysql://" + list.get(0) + ":" + list.get(1) + "/" + list.get(2) + "?useSSL=true");
            pool.setUser(list.get(3));
            pool.setPassword(list.get(4));
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected() {
        return pool != null;
    }

    public ComboPooledDataSource getPool() {
        return pool;
    }

    public static void disconnect() {
        if (isConnected()) {
            pool.close();
        }
    }
}