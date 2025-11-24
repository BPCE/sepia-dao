package fr.groupebpce.sepia.dao.showcase.test.exceptions;

import fr.groupebpce.sepia.dao.showcase.dao.club.ClubService;
import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import fr.groupebpce.sepia.dao.showcase.cache.ClubCache;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import fr.groupebpce.sepia.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EntityNotFoundExceptionTest {

    final ClubService clubService
            ;
    final ClubCache clubCache;
    
    @Test
    void findClubNotExisting() {
        assertThrows(EntityNotFoundException.class, () -> clubService.find("XYZ"));
        assertThat(
                clubCache.getClubs().stream()
                        .map(Club::getCode)
                        .anyMatch("XYZ"::equals)
        ).isFalse();
        
    }


}
