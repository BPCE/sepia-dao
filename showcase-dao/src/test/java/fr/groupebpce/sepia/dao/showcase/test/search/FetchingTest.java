package fr.groupebpce.sepia.dao.showcase.test.search;

import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubDao;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubService;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import lombok.RequiredArgsConstructor;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static fr.groupebpce.sepia.criteria.fetching.Fetching.fetch;
import static fr.groupebpce.sepia.dao.showcase.utils.ClubDisplayer.displayWithStadium;
import static fr.groupebpce.sepia.dao.showcase.utils.ClubDisplayer.displayWithStadiumAndPlayers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FetchingTest {

    final ClubService clubService;
    final ClubDao clubDao;
    final CountryDao countryDao;

    @BeforeAll
    void loadCache() {
        countryDao.search(new CountryCriteria());
    }

    @Test
    void selectAllClubsWithStadium() {
        List<Club> clubs = clubService.search(new ClubCriteria()
                .fetching(fetch("stadium")));
        displayWithStadium(clubs);
        assertThat(clubs).extracting("stadium").doesNotContainNull();
    }

    @Test
    void selectAllClubsWithStadiumAndPlayers_NotUsingTransaction() {
        List<Club> clubs = clubDao.search(new ClubCriteria()
                .fetch("stadium", "players"));
        assertThrows(LazyInitializationException.class, () -> displayWithStadiumAndPlayers(clubs));
    }

    @Test
    void selectAllClubsWithStadiumAndPlayers_UsingTransaction() {
        List<Club> clubs = clubService.search(new ClubCriteria()
                .fetch("stadium", "players"));
        displayWithStadiumAndPlayers(clubs);
        assertThat(clubs).extracting("stadium").doesNotContainNull();
    }


}
