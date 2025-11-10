package fr.groupebpce.sepia.dao.showcase.cache;

import fr.groupebpce.sepia.dao.showcase.entity.Club;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
public class ClubCache {

    @PersistenceContext
    private EntityManager em;

    private List<Club> clubs;

    @PostConstruct
    public void loadAllClubs() {
        clubs = em.createQuery("""
                from Club club
                left join fetch club.country
                left join fetch club.stadium stadium
                left join fetch stadium.address.country
                left join fetch stadium.address.city
                left join fetch club.players players
                left join fetch players.country
                """, Club.class).getResultList();
    }


}
