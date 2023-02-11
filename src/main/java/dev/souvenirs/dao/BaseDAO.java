package dev.souvenirs.dao;

import java.util.Optional;

/**
 * Интерфейс для описания абстрактных методов для базовой работы в БД
 * @param <K> - тип первичного ключа
 * @param <T> - тип сущности
 * @version 1.0
 */
public interface BaseDAO<K, T> {
    /**
     * Создание записи в БД
     * @param entity - объект
     * @return - id созданной сущности;
     *  если объект уже существует, то возвращается 0;
     *  если произошла ошибка при создания, то возвращается -1
     */
    K create(T entity);
    /**
     * Чтение записи по идентификатору из БД
     * @param id - идентификатор
     * @return - класс-оболочка с полученной записью из БД
     */
    Optional<T> read(K id);
    /**
     * Проверка на существование записи в БД
     * @param entity - объект
     * @return - логический результат существования записи в БД
     */
    boolean checkRecord(T entity);
    /**
     * Удаление записи из БД по идентификатору
     * @param id - идентификатор
     * @return - класс-оболочка с удаленной записью из БД
     */
    Optional<T> delete(K id);
}