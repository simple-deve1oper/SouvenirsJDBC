package dev.souvenirs.dao.impl;

import dev.souvenirs.dao.AdditionalDAO;
import dev.souvenirs.dao.SouvenirDAO;
import dev.souvenirs.entity.Producer;
import dev.souvenirs.entity.Souvenir;
import dev.souvenirs.util.db.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс для реализации интерфейса по работе в БД с таблицей сувениров
 * @version 1.0
 */
public class SouvenirDAOImpl implements SouvenirDAO<Long, Souvenir> {
    // создание
    private final String SQL_CREATE = "INSERT INTO souvenirs(name, producer_id, release_date, price) VALUES(?, ?, ?, ?)";
    // чтение
    private final String SQL_READ_BY_ID = "SELECT * FROM souvenirs WHERE id=?";
    // удаление по идентификатору сувенира
    private final String SQL_DELETE_BY_ID = "DELETE FROM souvenirs WHERE id=?";
    // удаление по идентификатору производителя
    private final String SQL_DELETE_BY_PRODUCER_ID = "DELETE FROM souvenirs WHERE producer_id=?";

    // соединение с БД
    private final Connection connection = DBConnection.getConnection();
    // интерфейс DAO для производителей
    private final AdditionalDAO<Long, Producer> producerDAO = new ProducerDAOImpl();

    @Override
    public Long create(Souvenir entity) {
        String souvenir_name = entity.getName();
        long producer_id = entity.getProducer().getId();
        LocalDate souvenir_date = entity.getReleaseDate();
        if (checkRecord(entity)) {
            return 0L;
        }

        if (producerDAO.read(producer_id).isEmpty()) {
            return -1L;
        }

        try(PreparedStatement statement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            statement.setString(1, souvenir_name);
            statement.setLong(2, producer_id);
            statement.setDate(3, Date.valueOf(souvenir_date));
            statement.setBigDecimal(4, entity.getPrice());
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
    public Optional<Souvenir> read(Long id) {
        try(PreparedStatement statement = connection.prepareStatement(SQL_READ_BY_ID)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long souvenir_id = resultSet.getLong("id");
                String souvenir_name = resultSet.getString("name");
                long producer_id = resultSet.getLong("producer_id");
                Producer producer = producerDAO.read(producer_id).get();
                LocalDate souvenir_date = resultSet.getDate("release_date").toLocalDate();
                BigDecimal souvenir_price = resultSet.getBigDecimal("price");
                Souvenir souvenir = new Souvenir(souvenir_id, souvenir_name, producer, souvenir_date, souvenir_price);
                return resultSet.next() ? Optional.empty() : Optional.of(souvenir);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean checkRecord(Souvenir entity) {
        String name = entity.getName();
        long producerId = entity.getProducer().getId();
        LocalDate releaseDate = entity.getReleaseDate();
        final String SQL_READ_BY_NAME_AND_PRODUCER_ID_AND_RELEASE_DATE =
                "SELECT * FROM souvenirs WHERE name=? AND producer_id=? AND release_date=?";
        boolean flag = false;
        try(PreparedStatement statement = connection.prepareStatement(SQL_READ_BY_NAME_AND_PRODUCER_ID_AND_RELEASE_DATE)) {
            statement.setString(1, name);
            statement.setLong(2, producerId);
            statement.setDate(3, Date.valueOf(releaseDate));
            ResultSet resultSet = statement.executeQuery();
            flag = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public Optional<Souvenir> delete(Long id) {
        Optional<Souvenir> optionalSouvenir = read(id);
        if (optionalSouvenir.isPresent()) {
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
        return optionalSouvenir;
    }

    @Override
    public List<Souvenir> getAllRequestedEntities(String sql) {
        List<Souvenir> requestedEntities = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long souvenir_id = resultSet.getLong("id");
                String souvenir_name = resultSet.getString("name");
                long producer_id = resultSet.getLong("producer_id");
                Producer producer = producerDAO.read(producer_id).get();
                LocalDate souvenir_date = resultSet.getDate("release_date").toLocalDate();
                BigDecimal souvenir_price = resultSet.getBigDecimal("price");
                Souvenir souvenir = new Souvenir(souvenir_id, souvenir_name, producer, souvenir_date, souvenir_price);
                requestedEntities.add(souvenir);
            }
            return requestedEntities;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requestedEntities;
    }

    @Override
    public boolean deleteRecordsByProducer(Producer producer) {
        boolean flag = false;
        Optional<Producer> optionalProducer = producerDAO.read(producer.getId());
        if (optionalProducer.isPresent()) {
            if (producerDAO.checkRecord(producer)) {
                try(PreparedStatement statement = connection.prepareStatement(SQL_DELETE_BY_PRODUCER_ID)) {
                    connection.setAutoCommit(false);
                    statement.setLong(1, producer.getId());
                    int result = statement.executeUpdate();
                    if (result > 0) {
                        flag = true;
                        connection.commit();
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
            }
        }
        return flag;
    }
}
