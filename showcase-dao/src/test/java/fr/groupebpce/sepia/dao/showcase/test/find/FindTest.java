package fr.groupebpce.sepia.dao.showcase.test.find;

import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubDao;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubFetching;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static fr.groupebpce.sepia.dao.showcase.utils.ClubDisplayer.display;
import static fr.groupebpce.sepia.dao.showcase.utils.ClubDisplayer.displayWithStadiumAndPlayers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FindTest {

    final ClubDao clubDao;
    final CountryDao countryDao;
    

    @BeforeAll
    void loadCache() {
        countryDao.search(new CountryCriteria());
    }

    @Test
    @Transactional
    void findPsg() {
        Club club = clubDao.find("PSG");
        display(club);
        assertThat(club).isNotNull();
        assertThat(club.getCode()).isEqualTo("PSG");
    }

    @Test
    @Transactional
    void findPsgWithPlayers() {
        Club club = clubDao.find("PSG", new ClubFetching().stadium(true).players(true));
        assertThat(club.getStadium()).isNotNull();
        assertThat(club.getStadium().getName()).isNotEmpty();
        assertThat(club.getPlayers())
                .isNotEmpty()
                .hasSizeGreaterThan(0);
        displayWithStadiumAndPlayers(club);
    }


}
