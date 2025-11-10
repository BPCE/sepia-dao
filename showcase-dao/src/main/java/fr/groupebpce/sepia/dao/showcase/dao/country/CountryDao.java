package fr.groupebpce.sepia.dao.showcase.dao.country;

import fr.groupebpce.sepia.criteria.fetching.NoFetching;
import fr.groupebpce.sepia.dao.QueryContext;
import fr.groupebpce.sepia.dao.QueryDao;
import fr.groupebpce.sepia.dao.showcase.entity.Country;
import org.springframework.stereotype.Repository;

@Repository
public class CountryDao extends QueryDao<Country, CountryCriteria, NoFetching> {

    public CountryDao() {
        super(Country.class);
    }

    @Override
    protected void filter(QueryContext<CountryCriteria, NoFetching> context) {
        // nothing to filter
    }
}
