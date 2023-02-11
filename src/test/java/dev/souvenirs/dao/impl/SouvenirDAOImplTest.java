package dev.souvenirs.dao.impl;

import dev.souvenirs.dao.AdditionalDAO;
import dev.souvenirs.dao.SouvenirDAO;
import dev.souvenirs.entity.Country;
import dev.souvenirs.entity.Producer;
import dev.souvenirs.entity.Souvenir;
import dev.souvenirs.util.db.DBConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Тестирование класса SouvenirDAO
 * @version 1.0
 */
public class SouvenirDAOImplTest {
    private final static Connection connection = DBConnection.getConnection();
    private final SouvenirDAO<Long, Souvenir> souvenirDAO = new SouvenirDAOImpl();
    private final AdditionalDAO<Long, Producer> producerDAO = new ProducerDAOImpl();

    @Test
    @DisplayName("Создание, чтение и удаление записи в таблице")
    public void createAndReadAndDelete() {
        Optional<Producer> optionalProducer = producerDAO.read(2L);
        Assertions.assertTrue(optionalProducer.isPresent());
        Producer producer = optionalProducer.get();

        Souvenir souvenir = new Souvenir("Тестовый сувенир", producer,
                LocalDate.of(2020, 1, 1), new BigDecimal(34.56));
        Long id = souvenirDAO.create(souvenir);

        Optional<Souvenir> optionalSouvenir = souvenirDAO.read(id);
        Assertions.assertTrue(optionalSouvenir.isPresent());
        souvenir = optionalSouvenir.get();
        Assertions.assertEquals(id, souvenir.getId());
        Assertions.assertEquals("Тестовый сувенир", souvenir.getName());

        optionalSouvenir = souvenirDAO.delete(id);
        Assertions.assertTrue(optionalSouvenir.isPresent());
        souvenir = optionalSouvenir.get();
        Assertions.assertEquals(id, souvenir.getId());
        Assertions.assertEquals("Тестовый сувенир", souvenir.getName());

        callingRequest("ALTER TABLE souvenirs AUTO_INCREMENT=" + id);
    }

    @Test
    @DisplayName("Попытка создать такую же запись, которая существует в таблице")
    public void creatingAnAlreadyExistingRecord() {
        Optional<Producer> optionalProducer = producerDAO.read(2L);
        Assertions.assertTrue(optionalProducer.isPresent());
        Producer producer = optionalProducer.get();

        Souvenir souvenir = new Souvenir("Тестовый сувенир", producer,
                LocalDate.of(2020, 1, 1), new BigDecimal(34.56));
        Long id = souvenirDAO.create(souvenir);

        Long answer = souvenirDAO.create(souvenir);
        Assertions.assertEquals(0, answer);

        souvenirDAO.delete(id);
        callingRequest("ALTER TABLE souvenirs AUTO_INCREMENT=" + id);
    }

    @Test
    @DisplayName("Получить список всех записей из таблицы")
    public void getAllRecords() {
        final String SQL_QUERY = "SELECT * FROM souvenirs";
        List<Souvenir> list = souvenirDAO.getAllRequestedEntities(SQL_QUERY);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertTrue(list.size() > 0);
    }

    @Test
    @DisplayName("Создать производителя с несуществующим производителем")
    public void createProducerWithNonExistentProducer() {
        Country country = new Country(-101, "Тестовая страна");
        Producer producer = new Producer(-99, "Тестовая организация", country);
        Souvenir souvenir = new Souvenir("Тестовый сувенир", producer,
                LocalDate.of(2020, 1, 1), new BigDecimal(100500));
        Long answer = souvenirDAO.create(souvenir);
        Assertions.assertEquals(-1L, answer);
    }

    @Test
    @DisplayName("Проверка записи по наименованию и стране")
    public void checkRecordByNameAndProducerAndReleaseDate() {
        Souvenir souvenir = souvenirDAO.read(1L).get();
        boolean flag = souvenirDAO.checkRecord(souvenir);
        Assertions.assertTrue(flag);
    }

    @Test
    @DisplayName("Удаление производителя и его сувениров")
    public void deleteProducersAndHisSouvenirs() {
        final AdditionalDAO<Long, Country> countryDAO = new CountryDAOImpl();
        Country country = countryDAO.read(1L).get();
        Producer producer = new Producer("Тестовый производитель1234", country);
        Long producerId = producerDAO.create(producer);
        producer = producerDAO.read(producerId).get();

        BigDecimal decimal = new BigDecimal(101);
        Souvenir souvenir = new Souvenir("Тестовый сувенир1", producer, LocalDate.now(), decimal);
        Long firstSouvenirId = souvenirDAO.create(souvenir);;
        for (int i = 2; i <= 5; i++) {
            souvenir = new Souvenir("Тестовый сувенир" + i, producer, LocalDate.now(), decimal);
            souvenirDAO.create(souvenir);
        }
        List<Souvenir> souvenirs =
                souvenirDAO.getAllRequestedEntities("SELECT * FROM souvenirs WHERE producer_id=" + producerId);
        Assertions.assertFalse(souvenirs.isEmpty());
        Assertions.assertEquals(5, souvenirs.size());

        boolean flag = souvenirDAO.deleteRecordsByProducer(producer);
        souvenirs =
                souvenirDAO.getAllRequestedEntities("SELECT * FROM souvenirs WHERE producer_id=" + producerId);
        Assertions.assertTrue(flag);
        Assertions.assertTrue(souvenirs.isEmpty());
        Assertions.assertEquals(0, souvenirs.size());

        producerDAO.delete(producerId);

        callingRequest("ALTER TABLE producers AUTO_INCREMENT=" + producerId);
        callingRequest("ALTER TABLE souvenirs AUTO_INCREMENT=" + firstSouvenirId);
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
