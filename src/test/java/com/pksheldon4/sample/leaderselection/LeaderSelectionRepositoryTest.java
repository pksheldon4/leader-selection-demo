package com.pksheldon4.sample.leaderselection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Slf4j
@ActiveProfiles("test")
class LeaderSelectionRepositoryTest {

    public static final String INSTANCE_ID_1 = "instance_id_1";

    @Autowired
    LeaderSelectionRepository repository;


    @Test
    @Sql(statements = {"insert into leader_selection (instance_id, heart_beat) values ('instance_id_1', current_timestamp)"})
    void findLeader_WithData_returnsList() {
        List<LeaderSelection> leaderSelections = repository.findLeader();
        assertFalse(leaderSelections.isEmpty());
        LeaderSelection leaderSelection = leaderSelections.get(0);
        assertThat(leaderSelection.getInstanceId()).isEqualTo(INSTANCE_ID_1);
    }

    @Test
    void save() {
        LeaderSelection leaderSelection = new LeaderSelection(INSTANCE_ID_1);
        LeaderSelection result = repository.save(leaderSelection);
        assertThat(result).isNotNull();
        assertThat(result.getInstanceId()).isEqualTo(INSTANCE_ID_1);
    }

    @Test
    void findLeader_WithEmptyDatabase_returnsEmptyList() {
        List<LeaderSelection> leaderSelections = repository.findLeader();
        assertTrue(leaderSelections.isEmpty());
    }

}