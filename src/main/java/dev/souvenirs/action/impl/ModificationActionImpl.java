package dev.souvenirs.action.impl;

import dev.souvenirs.action.ModificationAction;
import dev.souvenirs.dao.AdditionalDAO;
import dev.souvenirs.dao.SouvenirDAO;
import dev.souvenirs.entity.Producer;
import dev.souvenirs.entity.Souvenir;

import java.util.Optional;

/**
 * Класс для реализации интерфейса по изменению данных в БД
 * @version 1.0
 */
public class ModificationActionImpl implements ModificationAction {
    private final SouvenirDAO<Long, Souvenir> souvenirDao;      // интерфейс DAO для сувениров
    private final AdditionalDAO<Long, Producer> producerDAO;    // интерфейс DAO для производителей

    /**
     * Конструктор для создания нового объекта типа ModificationActionImpl
     * @param souvenirDao - интерфейс DAO для сувениров
     * @param producerDAO - интерфейс DAO для производителей
     */
    public ModificationActionImpl(SouvenirDAO<Long, Souvenir> souvenirDao, AdditionalDAO<Long, Producer> producerDAO) {
        this.souvenirDao = souvenirDao;
        this.producerDAO = producerDAO;
    }

    @Override
    public boolean deleteProducerAndHisSouvenirs(Producer producer) {
        boolean flag = false;
        Optional<Producer> optionalProducer = producerDAO.read(producer.getId());
        if (optionalProducer.isPresent()) {
            if (producerDAO.checkRecord(producer)) {
                souvenirDao.deleteRecordsByProducer(producer);
                producerDAO.delete(producer.getId());
                flag = true;
            }
        }
        return flag;
    }
}
