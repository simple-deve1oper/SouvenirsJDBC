package dev.souvenirs.entity;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс для описания страны
 * @version 1.0
 */
public class Country implements Cloneable, Serializable {
    private long id;        // идентификатор
    private String name;    // наименование

    /**
     * Конструктор для создания нового объекта типа Country
     * @param id - идентификатор
     * @param name - наименование
     */
    public Country(long id, String name) {
        this.id = id;
        this.name = name;
    }
    /**
     * Конструктор для создания нового объекта типа Country
     * @param name - наименование
     * @see Country#Country(long, String)
     */
    public Country(String name) {
        this(0, name);
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

    @Override
    public Country clone() throws CloneNotSupportedException {
        Country country = (Country) super.clone();
        return country;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Country country = (Country) obj;
        return (this.id == country.id) &&
                (this.name == country.name || (this.name != null && this.name.equals(country.name)));
    }

    @Override
    public int hashCode() {
        final int CODE = (int)(31 * 1 + id + (name == null ? 0 : name.hashCode()));
        return CODE;
    }

    @Override
    public String toString() {
        return new StringJoiner(",", this.getClass().getSimpleName() + "{", "}")
                .add("id=" + id).add("name=" + name).toString();
    }
}
