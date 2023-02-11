package dev.souvenirs.action;

import dev.souvenirs.entity.Producer;

/**
 * Интерфейс для описания абстрактных методов для изменения данных в БД
 * @version 1.0
 */
public interface ModificationAction {
    /**
     * Удаление заданного производителя и его сувениров
     * @param producer - производитель
     * @return результат удаления
     */
    boolean deleteProducerAndHisSouvenirs(Producer producer);
}
