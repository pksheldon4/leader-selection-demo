package com.pksheldon4.sample.leaderselection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class LeaderSelectionProcessorTest {

    public static final String TEST_INSTANCE_ID = "test";
    @Mock
    private LeaderSelectionRepository repository;

    @Captor
    private ArgumentCaptor<LeaderSelection> leaderSelectionCaptor;

    private LeaderSelectionProcessor leaderSelectionProcessor;

    @BeforeEach
    public void setUp() {
        leaderSelectionProcessor = new LeaderSelectionProcessor(1000, TEST_INSTANCE_ID, repository, null);
    }

    @Test
    void whenLeaderSelectionTableIsEmpty_becomeTheLeader() {

        when(repository.findLeader()).thenReturn(Collections.emptyList());
        leaderSelectionProcessor.processLeaderSelection();
        verify(repository).save(leaderSelectionCaptor.capture());
        assertThat(leaderSelectionCaptor.getValue().getInstanceId()).isEqualTo(TEST_INSTANCE_ID);

    }

    @Test
    void whenLeaderSelection_alreadyTheLeader_updateTheTimestamp() {

        LeaderSelection currentLeader = new LeaderSelection(TEST_INSTANCE_ID);
        LocalDateTime TEST_TIME = currentLeader.getHeartBeat();

        when(repository.findLeader()).thenReturn(Collections.singletonList(currentLeader));
        leaderSelectionProcessor.processLeaderSelection();
        verify(repository).save(leaderSelectionCaptor.capture());
        assertThat(leaderSelectionCaptor.getValue().getInstanceId()).isEqualTo(TEST_INSTANCE_ID);
        assertThat(leaderSelectionCaptor.getValue().getHeartBeat()).isAfter(TEST_TIME);

    }

    @Test
    void whenLeaderSelection_isNotTheLeaderButThresholdHasPassed_becomeTheLeader() {

        LocalDateTime TEST_TIME = LocalDateTime.now().minus(Duration.ofMinutes(5));
        LeaderSelection currentLeader = new LeaderSelection("SomeOtherInstnce", TEST_TIME);

        when(repository.findLeader()).thenReturn(Collections.singletonList(currentLeader));
        leaderSelectionProcessor.processLeaderSelection();
        verify(repository).save(leaderSelectionCaptor.capture());
        assertThat(leaderSelectionCaptor.getValue().getInstanceId()).isEqualTo(TEST_INSTANCE_ID);
        assertThat(leaderSelectionCaptor.getValue().getHeartBeat()).isAfter(TEST_TIME);

    }

    @Test
    void whenLeaderSelection_isNotTheLeader_doNothing() {

        LeaderSelection currentLeader = new LeaderSelection("SomeOtherInstnce");

        when(repository.findLeader()).thenReturn(Collections.singletonList(currentLeader));
        leaderSelectionProcessor.processLeaderSelection();
        verify(repository, never()).save(isA(LeaderSelection.class));


    }
}