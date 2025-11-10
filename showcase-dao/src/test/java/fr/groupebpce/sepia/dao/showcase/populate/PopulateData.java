package fr.groupebpce.sepia.dao.showcase.populate;


import fr.groupebpce.sepia.dao.showcase.ShowcaseApplication;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(classes = {ShowcaseApplication.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestInstance(PER_CLASS) // allows @BeforeAll method not to be static
@Sql({"/sql/populate_data.sql"})
class PopulateData {
    
    @Test
    void testNothing() {
        
    }
    
}
