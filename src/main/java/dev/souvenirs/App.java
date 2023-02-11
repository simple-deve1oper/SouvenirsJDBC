package dev.souvenirs;

import dev.souvenirs.action.ModificationAction;
import dev.souvenirs.action.impl.ModificationActionImpl;
import dev.souvenirs.entity.Country;
import dev.souvenirs.entity.Producer;
import dev.souvenirs.entity.Souvenir;
import dev.souvenirs.dao.AdditionalDAO;
import dev.souvenirs.dao.SouvenirDAO;
import dev.souvenirs.dao.impl.CountryDAOImpl;
import dev.souvenirs.dao.impl.ProducerDAOImpl;
import dev.souvenirs.dao.impl.SouvenirDAOImpl;
import dev.souvenirs.print.PrintingResult;

import java.math.BigDecimal;

/**
 * Класс для запуска приложения
 * @version 1.0
 */
public class App {
    public static void main(String[] args) {
        final SouvenirDAO<Long, Souvenir> souvenirDAO = new SouvenirDAOImpl();
        final AdditionalDAO<Long, Producer> producerDAO = new ProducerDAOImpl();
        final AdditionalDAO<Long, Country> countryDAO = new CountryDAOImpl();
        PrintingResult printingResult = new PrintingResult(souvenirDAO, producerDAO, countryDAO);

        Producer producer = producerDAO.read(2L).get();
        printingResult.souvenirsSpecifiedProducer(producer);
        System.out.println();

        Country country = countryDAO.read(4L).get();
        printingResult.souvenirsProducedInSpecifiedCountry(country);
        System.out.println();

        printingResult.producersWithPricesForSouvenirsLessThanSpecified(new BigDecimal(1200));
        System.out.println();

        printingResult.producersSouvenirsProducedInSpecifiedYear("Чайник", 2018);
        System.out.println();

        ModificationAction modificationAction = new ModificationActionImpl(souvenirDAO, producerDAO);
        boolean resultDelete = modificationAction.deleteProducerAndHisSouvenirs(producer);
        if (resultDelete) {
            System.out.println("Удаление производителя '" + producer.getName() + "' и его сувениров выполнено");
        } else {
            System.out.println("Удаление производителя '" + producer.getName() + "' не выполнено");
        }
        System.out.println();
        printingResult.souvenirsSpecifiedProducer(producer);
    }
}
