package fr.groupebpce.sepia.dao.showcase.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "STADIUM")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Stadium {

    // ========== ATTRIBUTES ==========

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CAPACITY")
    private Integer capacity;
    
    @Embedded
    private Address address;

    // ========== Object ==========
    @Override
    public String toString() {
        return "Stadium{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stadium stadium = (Stadium) o;
        return Objects.equals(id, stadium.id) && Objects.equals(name, stadium.name) && Objects.equals(capacity, stadium.capacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, capacity);
    }

}
