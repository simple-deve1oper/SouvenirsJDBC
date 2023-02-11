package dev.souvenirs.print;

import dev.souvenirs.dao.AdditionalDAO;
import dev.souvenirs.dao.SouvenirDAO;
import dev.souvenirs.entity.Country;
import dev.souvenirs.entity.Producer;
import dev.souvenirs.entity.Souvenir;
import dev.souvenirs.util.fromat.FormattingInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс для печати результатов
 * @version 1.0
 */
public class PrintingResult {
    private final SouvenirDAO<Long, Souvenir> souvenirDAO;      // интерфейс DAO для сувениров
    private final AdditionalDAO<Long, Producer> producerDAO;    // интерфейс DAO для производителей
    private final AdditionalDAO<Long, Country> countryDAO;      // интерфейс DAO для стран

    /**
     * Конструктор для создания нового объекта типа PrintingResult
     * @param souvenirDAO - интерфейс DAO для сувениров
     * @param producerDAO - интерфейс DAO для производителей
     * @param countryDAO - интерфейс DAO для стран
     */
    public PrintingResult(SouvenirDAO<Long, Souvenir> souvenirDAO, AdditionalDAO<Long, Producer> producerDAO,
                          AdditionalDAO<Long, Country> countryDAO) {
        this.souvenirDAO = souvenirDAO;
        this.producerDAO = producerDAO;
        this.countryDAO = countryDAO;
    }

    /**
     * Вывод информации о сувенирах заданного производителя
     * @param producer - производитель
     */
    public void souvenirsSpecifiedProducer(Producer producer) {
        System.out.println("---------------------------");
        System.out.println("Сувениры производителя '" + producer.getName() + "':");

        long producerId = producer.getId();
        Optional<Producer> optionalProducer = producerDAO.read(producerId);
        if (optionalProducer.isEmpty()) {
            System.out.println("---");
            System.out.println("Производителя с идентификатором " + producerId + " не найдено");
        } else if(producerDAO.checkRecord(producer)) {
            final String SQL_QUERY = "SELECT * FROM souvenirs WHERE producer_id=" + producerId;
            List<Souvenir> souvenirs = souvenirDAO.getAllRequestedEntities(SQL_QUERY);
            if (souvenirs.isEmpty()) {
                System.out.println("---");
                System.out.println("Список пуст");
            } else {
                souvenirs.forEach(ob -> System.out.println("---\n" + FormattingInfo.souvenirInfo(ob)));
            }
        } else {
            System.out.println("Ошибка!");
        }
        System.out.println("---------------------------");
    }
    /**
     * Вывод информации о сувенирах, произведенных в заданной стране
     * @param country - страна
     */
    public void souvenirsProducedInSpecifiedCountry(Country country) {
        System.out.println("---------------------------");
        System.out.println("Сувениры, произведенные в стране " + country.getName() + ":");

        Optional<Country> optionalCountry = countryDAO.read(country.getId());
        if (optionalCountry.isEmpty()) {
            System.out.println("Страны с идентификатором " + country.getId() + " не найдено");
        } else if(countryDAO.checkRecord(country)) {
            final String SQL_QUERY = "SELECT * FROM souvenirs";
            List<Souvenir> souvenirs = souvenirDAO.getAllRequestedEntities(SQL_QUERY);
            if (souvenirs.isEmpty()) {
                System.out.println("---");
                System.out.println("Список всех сувениров пуст");
            } else {
                souvenirs = souvenirs.stream()
                        .filter(ob -> ob.getProducer().getCountry().equals(country))
                        .collect(Collectors.toList());
                if (souvenirs.isEmpty()) {
                    System.out.println("---");
                    System.out.println("Список пуст");
                } else {
                    souvenirs.forEach(ob -> System.out.println("---\n" + FormattingInfo.souvenirInfo(ob)));
                }
            }
        }
        System.out.println("---------------------------");
    }
    /**
     * Вывод информации о производителях, чьи цены на сувениры меньше заданной
     * @param price - цена
     */
    public void producersWithPricesForSouvenirsLessThanSpecified(BigDecimal price) {
        System.out.println("---------------------------");
        System.out.println("Производители, у которых цены на сувениры меньше " + price + ":");

        final String SQL_QUERY = "SELECT * FROM souvenirs WHERE price<" + price;
        List<Souvenir> souvenirs = souvenirDAO.getAllRequestedEntities(SQL_QUERY);
        if (souvenirs.isEmpty()) {
            System.out.println("---");
            System.out.println("Список пуст");
        } else {
            Set<Producer> producers = souvenirs.stream()
                    .map(ob -> ob.getProducer())
                    .collect(Collectors.toSet());
            producers.forEach(ob -> System.out.println("---\n" + FormattingInfo.producerInfo(ob)));
        }
        System.out.println("---------------------------");
    }
    /**
     * Вывод информации о производителях заданного сувенира, произведенного в заданном году
     * @param name - наименование
     * @param year - год
     */
    public void producersSouvenirsProducedInSpecifiedYear(String name, int year) {
        System.out.println("---------------------------");
        System.out.println("Производители сувенира '" + name + "',  произведенного в " + year + " году:");

        final String SQL_QUERY = "SELECT * FROM souvenirs WHERE name='" + name + "'";
        List<Souvenir> souvenirs = souvenirDAO.getAllRequestedEntities(SQL_QUERY);
        if (souvenirs.isEmpty()) {
            System.out.println("---");
            System.out.println("Список сувениров пуст");
        } else {
            souvenirs = souvenirs.stream()
                    .filter(ob -> ob.getReleaseDate().getYear() == year)
                    .collect(Collectors.toList());
            if (souvenirs.isEmpty()) {
                System.out.println("---");
                System.out.println("Список пуст");
            } else {
                List<Producer> producers = souvenirs.stream()
                        .map(ob -> ob.getProducer())
                        .collect(Collectors.toList());
                producers.forEach(ob -> System.out.println("---\n" + FormattingInfo.producerInfo(ob)));
            }
        }
        System.out.println("---------------------------");
    }
}
