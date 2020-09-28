package com.pksheldon4.sample.leaderselection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "leaderSelection.enabled", havingValue = "true", matchIfMissing = false)
public class LeaderSelectionProcessor {

    public final String leaderInstanceId;
    private final Integer heartBeatThreshold;
    private final LeaderSelectionRepository repository;
    private final ApplicationEventPublisher publisher;

    public LeaderSelectionProcessor(@Value("${leaderSelection.heartBeatThreshold:30000}") int heartBeatThreshold,
                                    @Value("${leaderSelection.instanceId:}") String instanceId,
                                    LeaderSelectionRepository repository,
                                    ApplicationEventPublisher publisher) {
        this.heartBeatThreshold = heartBeatThreshold;
        this.repository = repository;
        this.publisher = publisher;
        if (StringUtils.isEmpty(instanceId)) {
            this.leaderInstanceId = UUID.randomUUID().toString();
        } else {
            this.leaderInstanceId = instanceId;
        }
        log.info("@@@@@@@ Constructing LeaderSelectorProcessor - " + getShortInstanceId());
        log.info("heartBeatThreshold = " + this.heartBeatThreshold);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void processLeaderSelection() {
        List<LeaderSelection> leaderSelectionList = repository.findLeader();
        if (leaderSelectionList.isEmpty()) {
            updateLeaderSelection(new LeaderSelection());
        } else {
            LeaderSelection leaderSelection = leaderSelectionList.get(0);
            if (leaderInstanceId.equals(leaderSelection.getInstanceId())) {
                log.debug("####### I'm the Leader!! - {}", getShortInstanceId());
                updateLeaderSelection(leaderSelection);
            } else {
                long milliseconds = ChronoUnit.MILLIS.between(leaderSelection.getHeartBeat(), LocalDateTime.now());
                if (milliseconds > heartBeatThreshold) {
                    log.debug("####### I'm taking over as Leader!! - {}", getShortInstanceId());
                    //No heartbeat in last threshold milliseconds, taking over leadership
                    updateLeaderSelection(leaderSelection);
                } else {
                    log.debug("####### I'm NOT the Leader. - {}", getShortInstanceId());
                    publishLeaderSelectionEvent(new LeaderSelectionEvent(leaderInstanceId, false));
                }
            }
        }
    }

    private String getShortInstanceId() {
        if (leaderInstanceId == null || leaderInstanceId.length() < 8) {
            return leaderInstanceId;
        }
        return leaderInstanceId.substring(0, 8);
    }

    private void updateLeaderSelection(LeaderSelection leaderSelection) {
        leaderSelection.setInstanceId(leaderInstanceId);
        leaderSelection.setHeartBeat(LocalDateTime.now());
        repository.save(leaderSelection);
        publishLeaderSelectionEvent(new LeaderSelectionEvent(leaderInstanceId, true));
    }

    private void publishLeaderSelectionEvent(LeaderSelectionEvent event) {
        if (publisher != null) {
            publisher.publishEvent(event);
        }
    }

    @PreDestroy
    public void closingTheApplication() {
        log.warn("Destroying the LeaderSelectionProcessor - instanceId: {}", leaderInstanceId);
    }
}
