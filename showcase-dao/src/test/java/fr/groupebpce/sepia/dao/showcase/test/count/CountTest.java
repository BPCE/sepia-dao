package fr.groupebpce.sepia.dao.showcase.test.count;

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
class CountTest {

    final ClubService clubService;
    final ClubCache clubCache;

    @Test
    void countAllClubsWithNameContainingLl() {
        long dbCount = clubService.count(new ClubCriteria().nameLike("LL"));
        
        long inMemCount = clubCache.getClubs().stream()
                .map(Club::getName)
                .filter(s -> s.toUpperCase().contains("LL"))
                .count();

        assertThat(dbCount).isEqualTo(inMemCount);
        System.out.printf("Found %s clubs%n", dbCount);
    }

    @Test
    void countAllClubsWithStadiumNameContainingNa() {
        Long dbCount = clubService.count(new ClubCriteria().stadiumNameLike("NA"));

        long inMemCount = clubCache.getClubs().stream()
                .map(Club::getStadium)
                .map(Stadium::getName)
                .filter(s -> s.toUpperCase().contains("NA"))
                .count();
        
        
        assertThat(dbCount).isEqualTo(inMemCount);
        System.out.printf("Found %s clubs%n", dbCount);
    }

    @Test
    void countAllClubsWithStadiumCapacityBetween50000And80000() {
        Long dbCount = clubService.count(new ClubCriteria()
                .stadiumCapacityIn(50_000, 80_000)
        );

        long inMemCount = clubCache.getClubs().stream()
                .map(Club::getStadium)
                .map(Stadium::getCapacity)
                .filter(c -> 50_000 <= c && c <= 80_000)
                .count();
        
        assertThat(dbCount).isEqualTo(inMemCount);
        System.out.printf("Found %s clubs%n", dbCount);
    }

    @Test
    void countAllFrenchClubs() {
        Long dbCount = clubService.count(new ClubCriteria()
                .countryIs(new Country().code("FRA"))
        );

        long inMemCount = clubCache.getClubs().stream()
                .map(Club::getCountry)
                .map(Country::getCode)
                .filter("FRA"::equals)
                .count();
        
        assertThat(dbCount).isEqualTo(inMemCount);
        System.out.printf("Found %s clubs%n", dbCount);
    }


}
