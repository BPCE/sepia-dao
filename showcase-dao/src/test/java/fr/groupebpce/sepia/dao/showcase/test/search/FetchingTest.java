package fr.groupebpce.sepia.dao.showcase.test.search;

import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubDao;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubFetching;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import lombok.RequiredArgsConstructor;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    final ClubDao clubDao;
    final CountryDao countryDao;

    @BeforeAll
    void loadCache() {
        countryDao.search(new CountryCriteria());
    }

    @Test
    void selectAllClubsWithStadium() {
        List<Club> clubs = clubDao.search(new ClubCriteria()
                .fetching(new ClubFetching().stadium(true)));
        displayWithStadium(clubs);
        assertThat(clubs).extracting("stadium").doesNotContainNull();
    }

    @Test
    void selectAllClubsWithStadiumAndPlayers_NotUsingTransaction() {
        List<Club> clubs = clubDao.search(new ClubCriteria()
                .fetching(new ClubFetching().stadium(true).players(true)));
        assertThrows(LazyInitializationException.class, () -> displayWithStadiumAndPlayers(clubs));
    }

    @Test
    @Transactional
    void selectAllClubsWithStadiumAndPlayers_UsingTransaction() {
        List<Club> clubs = clubDao.search(new ClubCriteria()
                .fetching(new ClubFetching().stadium(true).players(true)));
        displayWithStadiumAndPlayers(clubs);
        assertThat(clubs).extracting("stadium").doesNotContainNull();
    }


}
