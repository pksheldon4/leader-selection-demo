package com.pksheldon4.sample.leaderselection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SomeOtherProcessor {

    private boolean shouldProcess;

    private String instanceId = "SomeOtherProcessor";

    public SomeOtherProcessor(@Value("${someOtherProcessor.shouldProcess:true}") boolean shouldProcess) {
        this.shouldProcess = shouldProcess;
        log.info("################### SomeOtherProcessor - shouldProcess: {}", shouldProcess);
    }


    @EventListener
    void handleReturnedEvent(LeaderSelectionEvent event) {
        this.shouldProcess = event.isTheLeader();
        this.instanceId = event.getInstanceId();
    }

    @Scheduled(initialDelayString = "${someOtherProcessor.initialDelay:0}",
        fixedDelayString = "${someOtherProcessor.fixedDelay:4000}")
    public void doSomething() {
        if (shouldProcess) {
            int endIx = instanceId.length() > 8 ? 8 : instanceId.length();
            log.info("****** Some other Process - is being processed by Application {}", instanceId.substring(0, endIx));
        }
    }
}
