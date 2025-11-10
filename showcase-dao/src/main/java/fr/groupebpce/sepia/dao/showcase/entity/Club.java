package fr.groupebpce.sepia.dao.showcase.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "CLUB")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Club {

    // ========== ATTRIBUTES ==========

    @Id
    @Column(name = "CODE", length = 5, unique = true, nullable = false)
    private String code;

    @Column(name = "NAME", unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "FK_COUNTRY", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_STADIUM", nullable = false)
    private Stadium stadium;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<Player> players;

    // ========== Object ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Club club = (Club) o;
        return Objects.equals(code, club.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
