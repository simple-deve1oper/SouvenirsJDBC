package dev.souvenirs.entity;

import dev.souvenirs.util.db.DBConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

/**
 * Тестирование класса Souvenir
 * @version 1.0
 */
public class SouvenirTest {
    private final Souvenir souvenir = new Souvenir(1, "Тетрадь",
            new Producer(1, "Тестовая организация", new Country(4, "Тестовая страна")),
            LocalDate.of(1999, 2, 10), new BigDecimal(100500));

    @Test
    @DisplayName("Сравнение объекта с константными значениями")
    public void comparisonWithConstants() {
        Producer producer = new Producer(1, "Тестовая организация", new Country(4, "Тестовая страна"));
        LocalDate date = LocalDate.of(1999, 2, 10);
        BigDecimal price = new BigDecimal(100500);
        Assertions.assertTrue(
                (souvenir.getId() == 1) && (souvenir.getName().equals("Тетрадь")) &&
                        (souvenir.getProducer().equals(producer)) && (souvenir.getReleaseDate().equals(date)) &&
                        (souvenir.getPrice().equals(price))
        );
    }

    @Test
    @DisplayName("Сравнение с идентичным объектом")
    public void comparisonWithAnIdenticalObject() {
        Souvenir copySouvenir = new Souvenir(1, "Тетрадь",
                new Producer(1, "Тестовая организация", new Country(4, "Тестовая страна")),
                LocalDate.of(1999, 2, 10), new BigDecimal(100500));
        Assertions.assertTrue(
                (souvenir.getId() == copySouvenir.getId()) &&
                        (souvenir.getName().equals(copySouvenir.getName())) &&
                        (souvenir.getProducer().equals(copySouvenir.getProducer())) &&
                        (souvenir.getReleaseDate().equals(copySouvenir.getReleaseDate())) &&
                        (souvenir.getPrice().equals(copySouvenir.getPrice()))
        );
        Assertions.assertTrue(souvenir.equals(copySouvenir));
    }

    @Test
    @DisplayName("Сравнение с объектом, у которого другие значения")
    public void comparisonWithAnotherObject() {
        Souvenir anotherSouvenir = new Souvenir(123, "Другой блокнот",
                new Producer(99, "Другая тестовая организация",
                        new Country(101, "Другая тестовая страна")), LocalDate.now(),
                new BigDecimal(12345.67));
        Assertions.assertFalse(
                (souvenir.getId() == anotherSouvenir.getId()) &&
                        (souvenir.getName().equals(anotherSouvenir.getName())) &&
                        (souvenir.getProducer().equals(anotherSouvenir.getProducer())) &&
                        (souvenir.getReleaseDate().equals(anotherSouvenir.getReleaseDate())) &&
                        (souvenir.getPrice().equals(anotherSouvenir.getPrice()))
        );
        Assertions.assertFalse(souvenir.equals(anotherSouvenir));
    }

    @Test
    @DisplayName("Сравнение с клонируемым объектом")
    public void comparisonWithClonedObject() {
        Souvenir cloneSouvenir = null;
        try {
            cloneSouvenir = souvenir.clone();
            Assertions.assertTrue(
                    (souvenir.getId() == cloneSouvenir.getId()) &&
                            (souvenir.getName().equals(cloneSouvenir.getName())) &&
                            (souvenir.getProducer().equals(cloneSouvenir.getProducer())) &&
                            (souvenir.getReleaseDate().equals(cloneSouvenir.getReleaseDate())) &&
                            (souvenir.getPrice().equals(cloneSouvenir.getPrice()))
            );
            Assertions.assertTrue(souvenir.equals(cloneSouvenir));
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
        final String SQL_GET_SOUVENIR_BY_ID = "SELECT * FROM souvenirs WHERE id=?";
        try (
                PreparedStatement statementSouvenir = connection.prepareStatement(SQL_GET_SOUVENIR_BY_ID);
                PreparedStatement statementProducer = connection.prepareStatement(SQL_GET_PRODUCER_BY_ID);
                PreparedStatement statementCountry = connection.prepareStatement(SQL_GET_COUNTRY_BY_ID)
        ) {
            statementSouvenir.setLong(1, 1L);
            ResultSet resultSetSouvenir = statementSouvenir.executeQuery();
            resultSetSouvenir.next();
            long souvenir_id = resultSetSouvenir.getLong("id");
            String souvenir_name = resultSetSouvenir.getString("name");
            long producer_id = resultSetSouvenir.getLong("producer_id");

            statementProducer.setLong(1, producer_id);
            ResultSet resultSetProducer = statementProducer.executeQuery();
            resultSetProducer.next();
            producer_id = resultSetProducer.getLong("id");
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

            LocalDate releaseDate = resultSetSouvenir.getDate("release_date").toLocalDate();
            BigDecimal price = resultSetSouvenir.getBigDecimal("price");

            Souvenir souvenirFromDb = new Souvenir(souvenir_id, souvenir_name, producerFromDB, releaseDate, price);

            Assertions.assertNotNull(souvenirFromDb);
            Assertions.assertNotNull(souvenirFromDb.getProducer());
            Assertions.assertNotNull(souvenirFromDb.getProducer().getCountry());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void closeConnection() {
        DBConnection.closeConnection();
    }
}
