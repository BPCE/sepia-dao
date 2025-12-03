package fr.groupebpce.sepia.dao.showcase.test.search.one;

import fr.groupebpce.sepia.criteria.sorting.Sorting;
import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.cache.ClubCache;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubService;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.Optional;

import static fr.groupebpce.sepia.criteria.sorting.SortingDirectionEnum.DESC;
import static fr.groupebpce.sepia.dao.showcase.utils.ClubDisplayer.display;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SearchOneTest {

    final ClubService clubService;
    final CountryDao countryDao;
    final ClubCache clubCache;

    @BeforeAll
    void loadCache() {
        countryDao.search(new CountryCriteria());
    }

    @Test
    void searchClubWithBiggestStadium() {
        Optional<Club> clubDb = clubService.searchOne(new ClubCriteria()
                .orderBy(new Sorting("stadium.capacity", DESC))
        );
        Optional<Club> clubMem = clubCache.getClubs().stream()
                .max(Comparator.comparing(club1 -> club1.getStadium().getCapacity()));

        assertThat(clubDb).isPresent();
        assertThat(clubMem).isPresent();
        assertThat(clubDb).isEqualTo(clubMem);
        display(clubDb.get());
    }

}
