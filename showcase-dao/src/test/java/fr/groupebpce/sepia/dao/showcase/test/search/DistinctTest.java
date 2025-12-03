package fr.groupebpce.sepia.dao.showcase.test.search;

import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.cache.ClubCache;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubService;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import fr.groupebpce.sepia.dao.showcase.entity.Country;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static fr.groupebpce.sepia.dao.showcase.utils.ClubDisplayer.displayWithPlayers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DistinctTest {

    final ClubService clubService;
    final CountryDao countryDao;
    final ClubCache clubCache;

    @BeforeAll
    void loadCache() {
        countryDao.search(new CountryCriteria());
    }

    @Test
    void selectAllClubsWithPlayers() {
        List<Club> clubsDb = clubService.search(new ClubCriteria()
                .hasPlayerOfNationality(new Country().code("FRA"))
                .fetch("stadium", "players")
        );
        List<Club> clubsMem = clubCache.getClubs().stream()
                .filter(club -> club.getPlayers().stream().anyMatch(player -> "FRA".equals(player.getCountry().getCode())))
                .toList();


        assertThat(clubsDb)
                .hasSize(1)
                .isEqualTo(clubsMem);
        displayWithPlayers(clubsDb);
    }


}
