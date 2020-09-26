package com.pksheldon4.sample.leaderselection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Scheduler {

    private LeaderSelectionProcessor leaderSelectionProcessor;

    Scheduler(LeaderSelectionProcessor leaderSelectionProcessor) {
        this.leaderSelectionProcessor = leaderSelectionProcessor;
    }

    @Scheduled(initialDelayString = "${leaderSelection.initialDelay}", fixedDelayString = "${leaderSelection.fixedDelay}")
    public void scheduledLeaderSelection() {
        try {
            leaderSelectionProcessor.processLeaderSelection();
        } catch (CannotAcquireLockException e) {
            log.warn("Here I am");
            e.printStackTrace();
        }
    }
}
