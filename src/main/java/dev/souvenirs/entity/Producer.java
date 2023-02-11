package dev.souvenirs.entity;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс для описания производителя
 * @version 1.0
 */
public class Producer implements Cloneable, Serializable {
    private long id;            // идентификатор
    private String name;        // наименование
    private Country country;    // страна

    /**
     * Конструктор для создания нового объекта типа Producer
     * @param id - идентификатор
     * @param name - наименование
     * @param country - страна
     */
    public Producer(long id, String name, Country country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }
    /**
     * Конструктор для создания нового объекта типа Producer
     * @param name - наименование
     * @param country - страна
     * @see Producer#Producer(long, String, Country)
     */
    public Producer(String name, Country country) {
        this(0, name, country);
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Country getCountry() {
        return country;
    }
    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public Producer clone() throws CloneNotSupportedException {
        Producer producer = (Producer) super.clone();
        producer.country = country.clone();
        return producer;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Producer producer = (Producer) obj;
        return (this.id == producer.id) &&
                (this.name == producer.name || (this.name != null && this.name.equals(producer.name))) &&
                (this.country == producer.country || (this.country != null && this.country.equals(producer.country)));
    }

    @Override
    public int hashCode() {
        final int CODE = (int)(31 * 1 + id + (name == null ? 0 : name.hashCode()) +
                (country == null ? 0 : country.hashCode()));
        return CODE;
    }

    @Override
    public String toString() {
        return new StringJoiner(",", this.getClass().getSimpleName() + "{", "}")
                .add("id=" + id).add("name=" + name).add("country=" + country).toString();
    }
}
