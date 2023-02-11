package dev.souvenirs.dao;

import java.util.List;

/**
 * Интерфейс для описания абстрактных методов для расширенной работы в БД
 * @param <K> - тип первичного ключа
 * @param <T> - тип сущности
 * @version 1.0
 */
public interface AdditionalDAO<K, T> extends BaseDAO<K, T> {
    /**
     * Получение списка записей из БД по переданному запросу
     * @param sql - запрос
     * @return - список записей из БД
     */
    List<T> getAllRequestedEntities(String sql);
}
