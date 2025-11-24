package fr.groupebpce.sepia.dao.showcase.test.search;

import fr.groupebpce.sepia.dao.showcase.dao.club.ClubService;
import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.cache.ClubCache;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import fr.groupebpce.sepia.dao.showcase.entity.Country;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;

import static fr.groupebpce.sepia.dao.showcase.utils.ClubDisplayer.display;
import static fr.groupebpce.sepia.dao.showcase.utils.ClubDisplayer.displayWithStadium;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilterTest {

    final ClubService clubService;
    final CountryDao countryDao;
    final ClubCache clubCache;
    List<Club> allClubsSortedByName;
    
    static final Comparator<Club> clubNameComparator = Comparator.comparing(Club::getName);

    @BeforeAll
    void loadCache() {
        countryDao.search(new CountryCriteria());
        allClubsSortedByName = clubService.search(new ClubCriteria());
        allClubsSortedByName.sort(clubNameComparator);
    }

    @Test
    void selectAllClubsWithStadiumCapacityBetween50000And80000() {
        List<Club> clubsFromDb = clubService.search(new ClubCriteria()
                .stadiumCapacityIn(50_000, 80_000)
                .fetch("stadium")
        );

        List<Club> clubsMem = clubCache.getClubs().stream()
                .filter(club -> 50_000 <= club.getStadium().getCapacity() && club.getStadium().getCapacity() <= 80_000)
                .toList();
        
        displayWithStadium(clubsFromDb);
        assertThat(clubsFromDb).isEqualTo(clubsMem);
    }

    @Test
    void selectAllFrenchClubs() {
        List<Club> clubsFromDb = clubService.search(new ClubCriteria()
                .countryIs(new Country().code("FRA"))
        );

        List<Club> clubsMem = clubCache.getClubs().stream()
                .filter(club -> "FRA".equals(club.getCountry().getCode()))
                .toList();
        
        display(clubsFromDb);
        assertThat(clubsFromDb).isEqualTo(clubsMem);
    }


}
