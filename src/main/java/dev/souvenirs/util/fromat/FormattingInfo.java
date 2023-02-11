package dev.souvenirs.util.fromat;

import dev.souvenirs.entity.Country;
import dev.souvenirs.entity.Producer;
import dev.souvenirs.entity.Souvenir;

/**
 * Класс для форматирования информации об объекте
 * @version 1.0
 */
public class FormattingInfo {
    /**
     * Форматирование объекта типа Souvenir
     * @param souvenir - объект типа Souvenir
     * @return - строка отформатированного объекта
     */
    public static String souvenirInfo(Souvenir souvenir) {
        StringBuilder sb = new StringBuilder();
        sb.append("Идентификатор: " + souvenir.getId() + "\n");
        sb.append("Наименование: " + souvenir.getName() + "\n");
        sb.append(producerInfo(souvenir.getProducer())
                .replaceAll("Идентификатор: [0-9]+\n", "")
                .replaceAll("Наименование", "Производитель") + "\n");
        sb.append("Дата выпуска: " + souvenir.getReleaseDate() + "\n");
        sb.append("Цена: " + souvenir.getPrice());
        return sb.toString();
    }
    /**
     * Форматирование объекта типа Producer
     * @param producer - объект типа Producer
     * @return - строка отформатированного объекта
     */
    public static String producerInfo(Producer producer) {
        StringBuilder sb = new StringBuilder();
        sb.append("Идентификатор: " + producer.getId() + "\n");
        sb.append("Наименование: " + producer.getName() + "\n");
        sb.append(countryInfo(producer.getCountry()).
                replaceAll("Идентификатор: [0-9]+\n", "")
                .replaceAll("Наименование", "Страна"));
        return sb.toString();
    }
    /**
     * Форматирование объекта типа Country
     * @param country - объект типа Country
     * @return - строка отформатированного объекта
     */
    public static String countryInfo(Country country) {
        StringBuilder sb = new StringBuilder();
        sb.append("Идентификатор: " + country.getId() + "\n");
        sb.append("Наименование: " + country.getName());
        return sb.toString();
    }
}
