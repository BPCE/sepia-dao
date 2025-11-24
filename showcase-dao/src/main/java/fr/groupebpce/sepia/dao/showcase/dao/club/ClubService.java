package fr.groupebpce.sepia.dao.showcase.dao.club;

import fr.groupebpce.sepia.criteria.fetching.Fetching;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ClubService {
    
    private final ClubDao clubDao;
    
    public Club find(String id) {
        return clubDao.find(id);
    }
    
    public Club find(String id, Fetching fetching) {
        return clubDao.find(id, fetching);
    }

    public List<Club> search(ClubCriteria criteria) {
        return clubDao.search(criteria);
    }

    public Long count(ClubCriteria criteria) {
        return clubDao.count(criteria);
    }

    public boolean exists(ClubCriteria criteria) {
        return clubDao.exists(criteria);
    }

    public boolean notExist(ClubCriteria criteria) {
        return clubDao.notExist(criteria);
    }

    public Optional<Club> searchOne(ClubCriteria clubCriteria) {
        return clubDao.searchOne(clubCriteria);
    }
}
