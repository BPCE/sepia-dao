package fr.groupebpce.sepia.dao.showcase.test.exist;

import fr.groupebpce.sepia.dao.showcase.dao.club.ClubService;
import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.cache.ClubCache;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubCriteria;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import fr.groupebpce.sepia.dao.showcase.entity.Country;
import fr.groupebpce.sepia.dao.showcase.entity.Stadium;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExistsTest {

    final ClubService clubService;
    final ClubCache clubCache;

    @Test
    void notExist_ClubsWithNameContainingLl() {
        boolean dbExists = clubService.exists(new ClubCriteria().nameLike("LL"));

        boolean inMemExists = clubCache.getClubs().stream()
                .map(Club::getName)
                .anyMatch(s -> s.toUpperCase().contains("LL"));

        assertThat(dbExists).isTrue();
        assertThat(dbExists).isEqualTo(inMemExists);
    }

    @Test
    void notExist_ClubsWithStadiumNameContainingNa() {
        boolean dbExists = clubService.exists(new ClubCriteria().stadiumNameLike("NA"));

        boolean inMemExists = clubCache.getClubs().stream()
                .map(Club::getStadium)
                .map(Stadium::getName)
                .anyMatch(s -> s.toUpperCase().contains("NA"));
        
        assertThat(dbExists).isTrue();
        assertThat(dbExists).isEqualTo(inMemExists);
    }

    @Test
    void notExist_ClubsWithStadiumCapacityGreaterThan200000() {
        boolean dbNotExist = clubService.notExist(new ClubCriteria()
                .stadiumCapacityGreaterThan(200_000)
        );

        boolean inMemNotExist = clubCache.getClubs().stream()
                .map(Club::getStadium)
                .map(Stadium::getCapacity)
                .noneMatch(c -> 200_000 < c);
        
        assertThat(dbNotExist).isTrue();
        assertThat(dbNotExist).isEqualTo(inMemNotExist);
    }

    @Test
    void notExist_brazilianClub() {
        boolean dbNotExist = clubService.notExist(new ClubCriteria()
                .countryIs(new Country().code("BRA"))
        );

        boolean inMemNotExist = clubCache.getClubs().stream()
                .map(Club::getCountry)
                .map(Country::getCode)
                .noneMatch("BRA"::equals);

        assertThat(dbNotExist).isTrue();
        assertThat(dbNotExist).isEqualTo(inMemNotExist);
    }

}
