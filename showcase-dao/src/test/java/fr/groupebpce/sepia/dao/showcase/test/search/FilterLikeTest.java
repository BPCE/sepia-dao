package fr.groupebpce.sepia.dao.showcase.test.search;

import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.cache.ClubCache;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubDao;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubFetching;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static fr.groupebpce.sepia.dao.showcase.utils.ClubDisplayer.display;
import static fr.groupebpce.sepia.dao.showcase.utils.ClubDisplayer.displayWithStadium;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilterLikeTest {

    final ClubDao clubDao;
    final CountryDao countryDao;
    final ClubCache clubCache;
    
    @PersistenceContext
    private EntityManager em;
    
    @BeforeAll
    void loadCache() {
        countryDao.search(new CountryCriteria());
    }

    @Test
    void selectAllClubsWithNameContainingLl() {
        List<Club> clubsFromDb = clubDao.search(new ClubCriteria().nameLike("LL"));
        
        List<Club> clubsFromMem = clubCache.getClubs().stream()
                .filter(club -> club.getName().toUpperCase().contains("LL"))
                .toList();
        
        display(clubsFromDb);
        assertThat(clubsFromDb).isEqualTo(clubsFromMem);
    }

    @Test
    void selectAllClubsWithStadiumNameContainingNa() {
        List<Club> clubsFromDb = clubDao.search(new ClubCriteria()
                .stadiumNameLike("NA")
                .fetching(new ClubFetching().stadium(true))
        );

        List<Club> clubsFromMem = clubCache.getClubs().stream()
                .filter(club -> club.getStadium().getName().toUpperCase().contains("NA"))
                .toList();

        display(clubsFromDb);
        assertThat(clubsFromDb).isEqualTo(clubsFromMem);
    }


}
