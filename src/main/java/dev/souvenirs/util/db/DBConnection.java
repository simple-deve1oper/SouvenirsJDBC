package dev.souvenirs.util.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Класс Singleton для подключения к БД
 * @version 1.0
 */
public class DBConnection {
    private static Connection connection;   // соединение с БД

    static {
        initConnection();
    }

    /**
     * Закрытый конструктор по умолчанию
     */
    private DBConnection() {}

    /**
     * Получение соединения с БД
     * @return - соединение с БД
     */
    public static Connection getConnection() {
        if (connection == null) {
            initConnection();
        }
        return connection;
    }
    /**
     * Закрытие соединения с БД
     * @return - логический результат закрытия соединения с БД
     */
    public static boolean closeConnection() {
        boolean flag = false;
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                flag = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * Инициализация соединения к БД
     */
    private static void initConnection() {
        try(InputStream inputStream = DBConnection.class.getClassLoader().getResourceAsStream("DBInit.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
