package dev.souvenirs.dao;

import dev.souvenirs.entity.Producer;

/**
 * Интерфейс для описания абстрактных методов для работы в БД с таблицей сувениров
 * @param <K> - тип первичного ключа
 * @param <T> - тип сущности
 * @version 1.0
 */
public interface SouvenirDAO<K, T> extends AdditionalDAO<K, T> {
    /**
     * Удаление сувениров определенного производителя
     * @param producer - производитель
     * @return - логический результат удаления сувениров
     */
    boolean deleteRecordsByProducer(Producer producer);
}
