package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import model.exceptions.DBException;

public class MySQLConnection {
    private static Connection conn = null;

    private static Properties loadProperties() throws DBException {
        Properties props = new Properties();

        try (InputStream is = MySQLConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new DBException("db.properties not found in classpath");
            }

            props.load(is);
        } catch (Exception e) {
            throw new DBException("Could not load db.properties", e);
        }

        return props;
    }

    public static Connection getConnection() throws DBException {
        try {
            if (conn == null || conn.isClosed()) {
                Properties props = loadProperties();
                String url = props.getProperty("db.url");
                conn = DriverManager.getConnection(url, props);
            }

            return conn;
        } catch (Exception e) {
            throw new DBException("Could not get database connection", e);
        }
    }

    public static void closeConnection() throws DBException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            throw new DBException("Could not close database connection", e);
        }
    }
}