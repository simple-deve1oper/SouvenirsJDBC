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
 * Тестирование класса Country
 * @version 1.0
 */
public class CountryTest {
    private final Country country = new Country(1, "Тестовая страна");

    @Test
    @DisplayName("Сравнение объекта с константными значениями")
    public void comparisonWithConstants() {
        Assertions.assertTrue(
                (country.getId() == 1) && (country.getName().equals("Тестовая страна"))
        );
    }

    @Test
    @DisplayName("Сравнение с идентичным объектом")
    public void comparisonWithAnIdenticalObject() {
        Country copyCountry = new Country(1, "Тестовая страна");
        Assertions.assertTrue(
                (country.getId() == copyCountry.getId()) && (country.getName().equals(copyCountry.getName()))
        );
        Assertions.assertTrue(country.equals(copyCountry));
    }

    @Test
    @DisplayName("Сравнение с объектом, у которого другие значения")
    public void comparisonWithAnotherObject() {
        Country anotherCountry = new Country(2, "Другая тестовая страна");
        Assertions.assertFalse(
                (country.getId() == anotherCountry.getId()) &&
                        (country.getName().equals(anotherCountry.getName()))
        );
        Assertions.assertFalse(country.equals(anotherCountry));
    }

    @Test
    @DisplayName("Сравнение с клонируемым объектом")
    public void comparisonWithClonedObject() {
        Country cloneCountry = null;
        try {
            cloneCountry = country.clone();
            Assertions.assertTrue(
                    (country.getId() == cloneCountry.getId()) &&
                            (country.getName().equals(cloneCountry.getName()))
            );
            Assertions.assertTrue(country.equals(cloneCountry));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Получение объекта из БД")
    public void getObjectFromDB() {
        Connection connection = DBConnection.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM countries WHERE id=?")) {
            statement.setLong(1, 1L);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Country countryFromDB =
                    new Country(resultSet.getLong("id"), resultSet.getString("name"));
            Assertions.assertNotNull(countryFromDB);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void closeConnection() {
        DBConnection.closeConnection();
    }
}
