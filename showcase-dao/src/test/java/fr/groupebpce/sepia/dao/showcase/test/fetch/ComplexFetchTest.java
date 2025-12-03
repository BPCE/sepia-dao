package fr.groupebpce.sepia.dao.showcase.test.fetch;

import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.cache.ClubCache;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubService;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryDao;
import fr.groupebpce.sepia.dao.showcase.dao.player.PlayerCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.player.PlayerService;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import fr.groupebpce.sepia.dao.showcase.entity.Player;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static fr.groupebpce.sepia.criteria.fetching.Fetching.fetch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ComplexFetchTest {

    final PlayerService playerService;
    final ClubService clubService;
    final CountryDao countryDao;
    final ClubCache clubCache;

    @BeforeAll
    void loadCache() {
        countryDao.search(new CountryCriteria());
    }

    @Test
    void getRandomPlayerWithManyFetching() {
        Optional<Player> playerDb = playerService.searchOne(new PlayerCriteria()
                .clubIs("PSG")
                .fetch("club.stadium.address",
                        "club.stadium.address.city",
                        "club.players",
                        "country",
                        "club.stadium.address.country",
                        "club.players.country"
                )
        );

        assertThat(playerDb).isPresent();
        assertThat(playerDb.get().getClub()).isNotNull();
        assertThat(playerDb.get().getClub().getCode()).isEqualTo("PSG");
        assertThat(playerDb.get().getClub().getStadium()).isNotNull();
        assertThat(playerDb.get().getClub().getStadium().getAddress()).isNotNull();
        assertThat(playerDb.get().getClub().getStadium().getAddress().getCity()).isNotNull();
        assertThat(playerDb.get().getClub().getStadium().getAddress().getCity().getName()).isNotBlank();
        assertThat(playerDb.get().getClub().getPlayers()).isNotEmpty();
    }
    
    @Test
    void getRandomClubWithManyFetching() {
        Club clubDb = clubService.find("PSG",
                fetch("stadium.address",
                        "stadium.address.city",
                        "players",
                        "country",
                        "stadium.address.country",
                        "players.country"
                )
        );
        
        assertThat(clubDb).isNotNull();
        assertThat(clubDb.getCode()).isEqualTo("PSG");
        assertThat(clubDb.getStadium()).isNotNull();
        assertThat(clubDb.getStadium().getAddress()).isNotNull();
        assertThat(clubDb.getStadium().getAddress().getCity()).isNotNull();
        assertThat(clubDb.getStadium().getAddress().getCity().getName()).isNotBlank();
        assertThat(clubDb.getPlayers()).isNotEmpty();
    }

}
