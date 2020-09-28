package com.pksheldon4.sample.leaderselection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "leaderSelection.enabled", havingValue = "true", matchIfMissing = false)
public class LeaderSelectionScheduler {

    private LeaderSelectionProcessor leaderSelectionProcessor;

    LeaderSelectionScheduler(LeaderSelectionProcessor leaderSelectionProcessor,
                             @Value("${leaderSelection.instanceId:}") String instanceId,
                             @Value("${leaderSelection.fixedDelay:}") String fixedDelay,
                             @Value("${leaderSelection.initialDelay:}") String initialDelay) {
        log.info("LeaderSelectionScheduler: instanceId = " + instanceId);
        log.info("LeaderSelectionScheduler: fixedDelay = " + fixedDelay);
        log.info("LeaderSelectionScheduler: initialDelay = " + initialDelay);
        this.leaderSelectionProcessor = leaderSelectionProcessor;
    }

    @Scheduled(initialDelayString = "${leaderSelection.initialDelay}", fixedDelayString = "${leaderSelection.fixedDelay}")
    public void scheduledLeaderSelection() {
        try {
            leaderSelectionProcessor.processLeaderSelection();
        } catch (CannotAcquireLockException e) {
            log.warn("### Unable to acquire lock. Will try again next time");
        } catch (InvalidDataAccessResourceUsageException t) {
            log.warn("### Exception ignored as it's thrown during shutdown");
        }
    }
}
