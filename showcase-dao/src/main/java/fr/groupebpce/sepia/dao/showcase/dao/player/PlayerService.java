package fr.groupebpce.sepia.dao.showcase.dao.player;

import fr.groupebpce.sepia.criteria.fetching.Fetching;
import fr.groupebpce.sepia.dao.showcase.dao.club.ClubCriteria;
import fr.groupebpce.sepia.dao.showcase.entity.Club;
import fr.groupebpce.sepia.dao.showcase.entity.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class PlayerService {
    
    private final PlayerDao playerDao;
    
    public Player find(String id) {
        return playerDao.find(id);
    }
    
    public Player find(String id, Fetching fetching) {
        return playerDao.find(id, fetching);
    }

    public List<Player> search(PlayerCriteria criteria) {
        return playerDao.search(criteria);
    }

    public Long count(PlayerCriteria criteria) {
        return playerDao.count(criteria);
    }

    public boolean exists(PlayerCriteria criteria) {
        return playerDao.exists(criteria);
    }

    public boolean notExist(PlayerCriteria criteria) {
        return playerDao.notExist(criteria);
    }

    public Optional<Player> searchOne(PlayerCriteria playerCriteria) {
        return playerDao.searchOne(playerCriteria);
    }
}
