package fr.groupebpce.sepia.dao.showcase.dao.player;

import fr.groupebpce.sepia.criteria.fetching.Fetching;
import fr.groupebpce.sepia.dao.showcase.entity.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class PlayerService {
    
    private final PlayerDao playerDao;
    
    public Player find(String id, Fetching fetching) {
        return playerDao.find(id, fetching);
    }

    public Optional<Player> searchOne(PlayerCriteria playerCriteria) {
        return playerDao.searchOne(playerCriteria);
    }
}
