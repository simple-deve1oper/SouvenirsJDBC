package dev.souvenirs.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.StringJoiner;

/**
 * Класс для описания сувенира
 * @version 1.0
 */
public class Souvenir implements Cloneable, Serializable {
    private long id;                // идентификатор
    private String name;            // наименование
    private Producer producer;      // производитель
    private LocalDate releaseDate;  // дата выпуска
    private BigDecimal price;       // цена

    /**
     * Конструктор для создания нового объекта типа Souvenir
     * @param id - идентификатор
     * @param name - наименование
     * @param producer - производитель
     * @param releaseDate - дата выпуска
     * @param price - цена
     */
    public Souvenir(long id, String name, Producer producer, LocalDate releaseDate, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.producer = producer;
        this.releaseDate = releaseDate;
        this.price = price;
    }
    /**
     * Конструктор для создания нового объекта типа Souvenir
     * @param name - наименование
     * @param producer - производитель
     * @param releaseDate - дата выпуска
     * @param price - цена
     * @see Souvenir#Souvenir(long, String, Producer, LocalDate, BigDecimal) 
     */
    public Souvenir(String name, Producer producer, LocalDate releaseDate, BigDecimal price) {
        this(0, name, producer, releaseDate, price);
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
    public Producer getProducer() {
        return producer;
    }
    public void setProducer(Producer producer) {
        this.producer = producer;
    }
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public Souvenir clone() throws CloneNotSupportedException {
        Souvenir souvenir = (Souvenir) super.clone();
        souvenir.producer = producer.clone();
        return souvenir;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
       Souvenir souvenir = (Souvenir) obj;
        return (this.id == souvenir.id) &&
                (this.name == souvenir.name || (this.name != null && this.name.equals(souvenir.name))) &&
                (this.producer == souvenir.producer || (this.producer != null && this.producer.equals(souvenir.producer))) &&
                (this.releaseDate == souvenir.releaseDate ||
                        (this.releaseDate != null && this.releaseDate.equals(souvenir.releaseDate))) &&
                (this.price == souvenir.price || (this.price != null && this.price.equals(souvenir.price)));
    }

    @Override
    public int hashCode() {
        final int CODE = (int)(31 * 1 + id + (name == null ? 0 : name.hashCode()) +
                (producer == null ? 0 : producer.hashCode()) + (releaseDate == null ? 0 : releaseDate.hashCode()) +
                (price == null ? 0 : producer.hashCode()));
        return CODE;
    }

    @Override
    public String toString() {
        return new StringJoiner(",", this.getClass().getSimpleName() + "{", "}")
                .add("id=" + id).add("name=" + name).add("producer=" + producer)
                .add("releaseDate=" + releaseDate).add("price=" + price).toString();
    }
}
