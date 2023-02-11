package dev.souvenirs.entity;

import dev.souvenirs.util.db.DBConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Тестирование класса Producer
 * @version 1.0
 */
public class ProducerTest {
    private final Producer producer = new Producer(1, "Тестовая организация", new Country(4, "Тестовая страна"));

    @Test
    @DisplayName("Сравнение объекта с константными значениями")
    public void comparisonWithConstants() {
        Country country = new Country(4, "Тестовая страна");
        Assertions.assertTrue(
                (producer.getId() == 1) &&
                        (producer.getName().equals("Тестовая организация")) &&
                        (producer.getCountry().equals(country))
        );
    }

    @Test
    @DisplayName("Сравнение с идентичным объектом")
    public void comparisonWithAnIdenticalObject() {
        Producer copyProducer = new Producer(1, "Тестовая организация", new Country(4, "Тестовая страна"));
        Assertions.assertTrue(
                (producer.getId() == copyProducer.getId()) &&
                        (producer.getName().equals(copyProducer.getName())) &&
                        (producer.getCountry().equals(copyProducer.getCountry()))
        );
        Assertions.assertTrue(producer.equals(copyProducer));
    }

    @Test
    @DisplayName("Сравнение с объектом, у которого другие значения")
    public void comparisonWithAnotherObject() {
        Producer anotherProducer = new Producer(99, "Другая тестовая организация",
                new Country(101, "Другая тестовая страна"));
        Assertions.assertFalse(
                (producer.getId() == anotherProducer.getId()) &&
                        (producer.getName().equals(anotherProducer.getName())) &&
                        (producer.getCountry().equals(anotherProducer.getCountry()))
        );
        Assertions.assertFalse(producer.equals(anotherProducer));
    }

    @Test
    @DisplayName("Сравнение с клонируемым объектом")
    public void comparisonWithClonedObject() {
        Producer cloneProducer = null;
        try {
            cloneProducer = producer.clone();
            Assertions.assertTrue(
                    (producer.getId() == cloneProducer.getId()) &&
                            (producer.getName().equals(cloneProducer.getName())) &&
                            (producer.getCountry().equals(cloneProducer.getCountry()))
            );
            Assertions.assertTrue(producer.equals(cloneProducer));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Получение объекта из БД")
    public void getObjectFromDB() {
        Connection connection = DBConnection.getConnection();
        final String SQL_GET_COUNTRY_BY_ID = "SELECT * FROM countries WHERE id=?";
        final String SQL_GET_PRODUCER_BY_ID = "SELECT * FROM producers WHERE id=?";
        try (
                PreparedStatement statementProducer = connection.prepareStatement(SQL_GET_PRODUCER_BY_ID);
                PreparedStatement statementCountry = connection.prepareStatement(SQL_GET_COUNTRY_BY_ID)
        ) {
            statementProducer.setLong(1, 1L);
            ResultSet resultSetProducer = statementProducer.executeQuery();
            resultSetProducer.next();
            long producer_id = resultSetProducer.getLong("id");
            String name = resultSetProducer.getString("name");
            long country_id = resultSetProducer.getLong("country_id");
            Producer producerFromDB = null;

            statementCountry.setLong(1, country_id);
            ResultSet resultSetCountry = statementCountry.executeQuery();
            resultSetCountry.next();
            country_id = resultSetCountry.getLong("id");
            String country_name = resultSetCountry.getString("name");
            Country country = new Country(country_id, country_name);
            producerFromDB = new Producer(producer_id, name, country);

            Assertions.assertNotNull(producerFromDB);
            Assertions.assertNotNull(producerFromDB.getCountry());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void closeConnection() {
        DBConnection.closeConnection();
    }
}
