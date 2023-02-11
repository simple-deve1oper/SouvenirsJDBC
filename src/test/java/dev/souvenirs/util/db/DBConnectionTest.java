package dev.souvenirs.util.db;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

/**
 * Тестирование класса DBConnection
 * @version 1.0
 */
public class DBConnectionTest {
    @Test
    @DisplayName("Проверка подключения к БД")
    public void checkConnectionToDB() {
        Connection connection = DBConnection.getConnection();
        Assertions.assertNotNull(connection);
    }

    @AfterAll
    public static void closeConnection() {
        DBConnection.closeConnection();
    }
}
