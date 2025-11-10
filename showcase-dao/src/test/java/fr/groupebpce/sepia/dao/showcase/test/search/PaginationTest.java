package fr.groupebpce.sepia.dao.showcase.test.search;

import fr.groupebpce.sepia.criteria.paging.Paging;
import fr.groupebpce.sepia.criteria.sorting.Sorting;
import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubDao;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;

import static fr.groupebpce.sepia.dao.showcase.utils.ClubDisplayer.display;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaginationTest {

    final ClubDao clubDao;
    final CountryDao countryDao;
    List<Club> allClubsSortedByName;
    
    static final Comparator<Club> clubNameComparator = Comparator.comparing(Club::getName);

    @BeforeAll
    void loadCache() {
        countryDao.search(new CountryCriteria());
        allClubsSortedByName = clubDao.search(new ClubCriteria());
        allClubsSortedByName.sort(clubNameComparator);
    }

    @Test
    void selectAllClubs() {
        List<Club> clubs = clubDao.search(new ClubCriteria());
        display(clubs);
        assertThat(clubs).size().isEqualTo(19);
    }

    @Test
    void selectAllClubsSortedByName() {
        List<Club> clubs = clubDao.search(new ClubCriteria()
                .orderBy(new Sorting().property("name").ascending())
        );
        display(clubs);
        assertThat(clubs).isEqualTo(allClubsSortedByName);
    }

    @Test
    void selectFirstFiveClubsSortedByName() {
        List<Club> clubs = clubDao.search(new ClubCriteria()
                .orderBy(new Sorting().property("name").ascending())
                .paging(new Paging().pageNumber(1).pageSize(5))
        );
        display(clubs);
        assertThat(clubs).isEqualTo(allClubsSortedByName.subList(0, 5));
    }

    @Test
    void selectNextFiveClubsSortedByName() {
        List<Club> clubs = clubDao.search(new ClubCriteria()
                .orderBy(new Sorting().property("name").ascending())
                .paging(new Paging().pageNumber(2).pageSize(5))
        );
        display(clubs);
        assertThat(clubs).isEqualTo(allClubsSortedByName.subList(5, 10));
    }


}
