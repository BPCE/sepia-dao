package fr.groupebpce.sepia.dao.showcase.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "COUNTRY")
@Cacheable
@Getter
@Setter
public class Country {

    // ========== ATTRIBUTES ==========

    @Id
    @Column(name = "CODE", length = 3)
    private String code;

    @Column(name = "NAME", length = 50)
    private String name;

    // ========== Fluent Setters ==========

    public Country code(String code) {
        this.code = code;
        return this;
    }

    public Country name(String name) {
        this.name = name;
        return this;
    }

    // ========== Object ==========
    @Override
    public String toString() {
        return "Country{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(code, country.code);
    }
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

}
