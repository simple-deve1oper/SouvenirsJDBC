package dev.souvenirs.dao.impl;

import dev.souvenirs.dao.AdditionalDAO;
import dev.souvenirs.entity.Country;
import dev.souvenirs.util.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс для реализации интерфейса по расширенной работы в БД со странами
 * @version 1.0
 */
public class CountryDAOImpl implements AdditionalDAO<Long, Country> {
    private final String SQL_CREATE = "INSERT INTO countries(name) VALUES(?)";  // создание
    private final String SQL_READ_BY_ID = "SELECT * FROM countries WHERE id=?"; // чтение
    private final String SQL_DELETE_BY_ID = "DELETE FROM countries WHERE id=?"; // удаление

    private final Connection connection = DBConnection.getConnection();         // соединение с БД

    @Override
    public Long create(Country entity) {
        if (checkRecord(entity)) {
            return 0L;
        }

        String name = entity.getName();
        try(PreparedStatement statement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            statement.setString(1, name);
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
    public Optional<Country> read(Long id) {
        try(PreparedStatement statement = connection.prepareStatement(SQL_READ_BY_ID)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long countryId = resultSet.getLong("id");
                String countryName = resultSet.getString("name");
                Country country = new Country(countryId, countryName);
                return resultSet.next() ? Optional.empty() : Optional.of(country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean checkRecord(Country entity) {
        String name = entity.getName();
        final String SQL_READ_BY_NAME = "SELECT * FROM countries WHERE name=?";
        boolean flag = false;
        try(PreparedStatement statement = connection.prepareStatement(SQL_READ_BY_NAME)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            flag = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public Optional<Country> delete(Long id) {
        Optional<Country> optionalCountry = read(id);
        if (optionalCountry.isPresent()) {
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
        return optionalCountry;
    }

    @Override
    public List<Country> getAllRequestedEntities(String sql) {
        List<Country> requestedEntities = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long countryId = resultSet.getLong("id");
                String countryName = resultSet.getString("name");
                Country country = new Country(countryId, countryName);
                requestedEntities.add(country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requestedEntities;
    }
}
