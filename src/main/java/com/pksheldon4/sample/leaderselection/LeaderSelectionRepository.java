package com.pksheldon4.sample.leaderselection;


import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;

public interface LeaderSelectionRepository extends Repository<LeaderSelection, Integer> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "2000")})
    @Query("select l from LeaderSelection l order by l.heartBeat desc")
    List<LeaderSelection> findLeader();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    LeaderSelection save(LeaderSelection entity);
}
