package dev.souvenirs.dao.impl;

import dev.souvenirs.dao.AdditionalDAO;
import dev.souvenirs.entity.Country;
import dev.souvenirs.entity.Producer;
import dev.souvenirs.util.db.DBConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

/**
 * Тестирование класса AdditionalDAO с производителями
 * @version 1.0
 */
public class ProducerDAOImplTest {
    private final static Connection connection = DBConnection.getConnection();
    private final AdditionalDAO<Long, Producer> producerDAO = new ProducerDAOImpl();
    private final AdditionalDAO<Long, Country> countryDAO = new CountryDAOImpl();

    @Test
    @DisplayName("Создание, чтение и удаление записи в таблице")
    public void createAndReadAndDelete() {
        Optional<Country> optionalCountry = countryDAO.read(3L);
        Assertions.assertTrue(optionalCountry.isPresent());
        Country country = optionalCountry.get();

        Producer producer = new Producer("Тестовая организация", country);
        Long id = producerDAO.create(producer);

        Optional<Producer> optionalProducer = producerDAO.read(id);
        Assertions.assertTrue(optionalProducer.isPresent());
        producer = optionalProducer.get();
        Assertions.assertEquals(id, producer.getId());
        Assertions.assertEquals("Тестовая организация", producer.getName());

        optionalProducer = producerDAO.delete(id);
        Assertions.assertTrue(optionalProducer.isPresent());
        producer = optionalProducer.get();
        Assertions.assertEquals(id, producer.getId());
        Assertions.assertEquals("Тестовая организация", producer.getName());

        callingRequest("ALTER TABLE producers AUTO_INCREMENT=" + id);
    }

    @Test
    @DisplayName("Попытка создать такую же запись, которая существует в таблице")
    public void creatingAnAlreadyExistingRecord() {
        Optional<Country> optionalCountry = countryDAO.read(3L);
        Assertions.assertTrue(optionalCountry.isPresent());
        Country country = optionalCountry.get();

        Producer producer = new Producer("Тестовая организация", country);
        Long id = producerDAO.create(producer);

        Long answer = producerDAO.create(producer);
        Assertions.assertEquals(0, answer);

        producerDAO.delete(id);
        callingRequest("ALTER TABLE producers AUTO_INCREMENT=" + id);
    }

    @Test
    @DisplayName("Получить список всех записей из таблицы")
    public void getAllRecords() {
        final String SQL_QUERY = "SELECT * FROM producers";
        List<Producer> list = producerDAO.getAllRequestedEntities(SQL_QUERY);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertTrue(list.size() > 0);
    }

    @Test
    @DisplayName("Создать производителя с несуществующей страной")
    public void createProducerWithNonExistentCountry() {
        Country country = new Country(-99, "Неизвестная страна");
        Producer producer = new Producer("Тестовая организация", country);
        Long answer = producerDAO.create(producer);
        Assertions.assertEquals(-1L, answer);
    }

    @Test
    @DisplayName("Проверка записи по наименованию и стране")
    public void checkRecordByNameAndCountry() {
        Producer producer = producerDAO.read(1L).get();
        boolean flag = producerDAO.checkRecord(producer);
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
