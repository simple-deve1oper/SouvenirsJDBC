package dev.souvenirs.action.impl;

import dev.souvenirs.action.ModificationAction;
import dev.souvenirs.dao.AdditionalDAO;
import dev.souvenirs.dao.SouvenirDAO;
import dev.souvenirs.dao.impl.CountryDAOImpl;
import dev.souvenirs.dao.impl.ProducerDAOImpl;
import dev.souvenirs.dao.impl.SouvenirDAOImpl;
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

/**
 * Тестирование класса ModificationActionImpl
 * @version 1.0
 */
public class ModificationActionImplTest {
    private final static Connection connection = DBConnection.getConnection();
    private final SouvenirDAO<Long, Souvenir> souvenirDAO = new SouvenirDAOImpl();
    private final AdditionalDAO<Long, Producer> producerDAO = new ProducerDAOImpl();
    private final ModificationAction modificationAction = new ModificationActionImpl(souvenirDAO, producerDAO);

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
        Long firstSouvenirId = souvenirDAO.create(souvenir);
        for (int i = 2; i <= 5; i++) {
            souvenir = new Souvenir("Тестовый сувенир" + i, producer, LocalDate.now(), decimal);
            souvenirDAO.create(souvenir);
        }
        List<Souvenir> souvenirs =
                souvenirDAO.getAllRequestedEntities("SELECT * FROM souvenirs WHERE producer_id=" + producerId);
        Assertions.assertFalse(souvenirs.isEmpty());
        Assertions.assertEquals(5, souvenirs.size());

        boolean flag = modificationAction.deleteProducerAndHisSouvenirs(producer);
        souvenirs =
                souvenirDAO.getAllRequestedEntities("SELECT * FROM souvenirs WHERE producer_id=" + producerId);
        Assertions.assertTrue(flag);
        Assertions.assertTrue(souvenirs.isEmpty());
        Assertions.assertEquals(0, souvenirs.size());

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
