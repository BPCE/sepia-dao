package fr.groupebpce.sepia.dao.showcase.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "PLAYER")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Player {
	
	// ========== ATTRIBUTES ==========
	
	@Id
	@Column(name="ID", unique=true, nullable=false)
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PlayerSeqGen")
    @SequenceGenerator(name = "PlayerSeqGen", sequenceName = "PLAYER_SEQ", allocationSize = 1)
	private Long id;
	
	@Column(name="NAME", length=50, nullable=false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FK_COUNTRY", nullable=false)
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FK_CLUB", nullable=false)
	private Club club;
	
	@Temporal(TemporalType.DATE)
	@Column(name="BIRTHDATE")
	private Date birthdate;

    // ========== Object ==========

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country=" + country +
                ", club=" + club +
                ", birthdate=" + birthdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id) && Objects.equals(name, player.name) && Objects.equals(country, player.country) && Objects.equals(club, player.club) && Objects.equals(birthdate, player.birthdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, country, club, birthdate);
    }

    @PreUpdate
    public void updateBirthdate() {
        birthdate = new Date();
    }
	
}
