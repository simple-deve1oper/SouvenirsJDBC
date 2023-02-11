package dev.souvenirs.dao.impl;

import dev.souvenirs.dao.AdditionalDAO;
import dev.souvenirs.entity.Country;
import dev.souvenirs.util.db.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

/**
 * Тестирование класса AdditionalDAO со странами
 * @version 1.0
 */
public class CountryDAOImplTest {
    private final static Connection connection = DBConnection.getConnection();
    private final AdditionalDAO<Long, Country> countryDAO = new CountryDAOImpl();

    @Test
    @DisplayName("Создание, чтение и удаление записи в таблице")
    public void createAndReadAndDelete() {
        Country country = new Country("Тестовая страна");
        Long id = countryDAO.create(country);

        Optional<Country> optionalCountry = countryDAO.read(id);
        Assertions.assertTrue(optionalCountry.isPresent());
        country = optionalCountry.get();
        Assertions.assertEquals(id, country.getId());
        Assertions.assertEquals("Тестовая страна", country.getName());

        optionalCountry = countryDAO.delete(id);
        Assertions.assertTrue(optionalCountry.isPresent());
        country = optionalCountry.get();
        Assertions.assertEquals(id, country.getId());
        Assertions.assertEquals("Тестовая страна", country.getName());

        callingRequest("ALTER TABLE countries AUTO_INCREMENT=" + id);
    }

    @Test
    @DisplayName("Попытка создать такую же запись, которая существует в таблице")
    public void creatingAnAlreadyExistingRecord() {
        Country country = new Country("Другая тестовая страна");
        Long id = countryDAO.create(country);

        Long answer = countryDAO.create(country);
        Assertions.assertEquals(0, answer);

        countryDAO.delete(id);
        callingRequest("ALTER TABLE countries AUTO_INCREMENT=" + id);
    }

    @Test
    @DisplayName("Получить список всех записей из таблицы")
    public void getAllRecords() {
        final String SQL_QUERY = "SELECT * FROM countries";
        List<Country> list = countryDAO.getAllRequestedEntities(SQL_QUERY);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertTrue(list.size() > 0);
    }

    @Test
    @DisplayName("Проверка записи по наименованию, производителю и дате выпуска")
    public void checkRecordByName() {
        Country country = countryDAO.read(1L).get();
        boolean flag = countryDAO.checkRecord(country);
        Assertions.assertTrue(flag);
    }

    @AfterAll
    public static void completion() {
        DBConnection.closeConnection();
    }

    private static void callingRequest(String sql) {
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
