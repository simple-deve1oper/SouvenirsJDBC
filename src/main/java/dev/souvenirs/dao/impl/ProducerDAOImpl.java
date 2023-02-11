package dev.souvenirs.dao.impl;

import dev.souvenirs.dao.AdditionalDAO;
import dev.souvenirs.entity.Country;
import dev.souvenirs.entity.Producer;
import dev.souvenirs.util.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс для реализации интерфейса по расширенной работы в БД с производителями
 * @version 1.0
 */
public class ProducerDAOImpl implements AdditionalDAO<Long, Producer> {
    private final String SQL_CREATE = "INSERT INTO producers(name, country_id) VALUES(?, ?)";   // создание
    private final String SQL_READ_BY_ID = "SELECT * FROM producers WHERE id=?";                 // чтение
    private final String SQL_DELETE_BY_ID = "DELETE FROM producers WHERE id=?";                 // удаление

    private final Connection connection = DBConnection.getConnection();                         // соединение с БД
    private final AdditionalDAO<Long, Country> countryDAO = new CountryDAOImpl();               // интерфейс DAO для стран

    @Override
    public Long create(Producer entity) {
        String name = entity.getName();
        long countryId = entity.getCountry().getId();
        if (checkRecord(entity)) {
            return 0L;
        }

        if (countryDAO.read(countryId).isEmpty()) {
            return -1L;
        }

        try(PreparedStatement statement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            statement.setString(1, name);
            statement.setLong(2, countryId);
            int result = statement.executeUpdate();
            if (result > 0) {
                ResultSet resultSetGeneratedKey = statement.getGeneratedKeys();
                resultSetGeneratedKey.next();
                connection.commit();
                return resultSetGeneratedKey.getLong(1);
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1L;
    }

    @Override
    public Optional<Producer> read(Long id) {
        try(PreparedStatement statement = connection.prepareStatement(SQL_READ_BY_ID)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long producerId = resultSet.getLong("id");
                String producer_name = resultSet.getString("name");
                long countryId = resultSet.getLong("country_id");
                Country country = countryDAO.read(countryId).get();
                Producer producer = new Producer(producerId, producer_name, country);
                return resultSet.next() ? Optional.empty() : Optional.of(producer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean checkRecord(Producer entity) {
        String name = entity.getName();
        long countryId = entity.getCountry().getId();
        final String SQL_READ_BY_NAME_AND_COUNTRY_ID = "SELECT * FROM producers WHERE name=? AND country_id=?";
        boolean flag = false;
        try(PreparedStatement statement = connection.prepareStatement(SQL_READ_BY_NAME_AND_COUNTRY_ID)) {
            statement.setString(1, name);
            statement.setLong(2, countryId);
            ResultSet resultSet = statement.executeQuery();
            flag = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public Optional<Producer> delete(Long id) {
        Optional<Producer> optionalProducer = read(id);
        if (optionalProducer.isPresent()) {
            try(PreparedStatement statement = connection.prepareStatement(SQL_DELETE_BY_ID)) {
                connection.setAutoCommit(false);
                statement.setLong(1, id);
                int result = statement.executeUpdate();
                if (result <= 0) {
                    connection.rollback();
                    return Optional.empty();
                }
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return optionalProducer;
    }

    @Override
    public List<Producer> getAllRequestedEntities(String sql) {
        List<Producer> requestedEntities = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long producerId = resultSet.getLong("id");
                String producer_name = resultSet.getString("name");
                long countryId = resultSet.getLong("country_id");
                Country country = countryDAO.read(countryId).get();
                Producer producer = new Producer(producerId, producer_name, country);
                requestedEntities.add(producer);
            }
            return requestedEntities;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requestedEntities;
    }
}
