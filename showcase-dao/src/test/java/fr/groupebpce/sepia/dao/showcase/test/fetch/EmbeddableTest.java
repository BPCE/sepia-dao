package fr.groupebpce.sepia.dao.showcase.test.fetch;

import fr.groupebpce.sepia.criteria.fetching.Fetching;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubService;
import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static fr.groupebpce.sepia.criteria.fetching.Fetching.fetch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmbeddableTest {

    final ClubService clubService;

    @Test
    void canLoadEmbeddableUsingEntityGraph() {
        Club psg = clubService.find("PSG", 
                fetch("stadium.address.country", "stadium.address.city")
        );
        assertThat(psg).isNotNull();
        assertThat(psg.getStadium()).isNotNull();
        assertThat(psg.getStadium().getAddress()).isNotNull();
        assertThat(psg.getStadium().getAddress().getCity()).isNotNull();
        assertThat(psg.getStadium().getAddress().getCity().getName()).isNotNull();
    }

}
