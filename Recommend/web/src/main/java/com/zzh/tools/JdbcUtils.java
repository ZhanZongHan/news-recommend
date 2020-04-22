package com.zzh.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcUtils {
    private static volatile Connection conn;

    private JdbcUtils() {}

    private static void initConn() {
        PropertiesUtils.init("jdbc.properties");
        String driver = PropertiesUtils.getValue("driver");
        String url = PropertiesUtils.getValue("url");
        String username = PropertiesUtils.getValue("username");
        String password = PropertiesUtils.getValue("password");

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            close();
        }
    }

    public static Connection getConn() {
        if (conn == null) {
            synchronized (JdbcUtils.class) {
                if (conn == null) {
                    initConn();
                }
            }
        }
        return conn;
    }

    public static void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
