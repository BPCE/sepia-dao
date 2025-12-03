package fr.groupebpce.sepia.dao.showcase.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "CITY")
@Getter
@Setter
public class City {

    // ========== ATTRIBUTES ==========

    @Id
    @Column(name = "CODE", length = 3)
    private String code;

    @Column(name = "NAME", length = 50)
    private String name;

    // ========== Fluent Setters ==========

    public City code(String code) {
        this.code = code;
        return this;
    }

    public City name(String name) {
        this.name = name;
        return this;
    }

    // ========== Object ==========
    @Override
    public String toString() {
        return "City{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City other = (City) o;
        return Objects.equals(code, other.code);
    }
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

}
