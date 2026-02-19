package fr.groupebpce.sepia.dao.showcase.test.find;

import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.country.CountryDao;
import fr.groupebpce.sepia.dao.showcase.dao.player.PlayerCriteria;
import fr.groupebpce.sepia.dao.showcase.dao.player.PlayerService;
import fr.groupebpce.sepia.dao.showcase.entity.Player;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LikeTest {

    final CountryDao countryDao;
    final PlayerService playerService;


    @BeforeAll
    void loadCache() {
        countryDao.search(new CountryCriteria());
    }

    @Test
    void search_player_withUnderscoreInItsName() {
        Optional<Player> player = playerService.searchOne(new PlayerCriteria().nameLike("_"));
        assertThat(player).isPresent();
        assertThat(player.get().getName()).contains("_");
    }

}
