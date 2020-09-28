package com.pksheldon4.sample.leaderselection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/leader")
@ConditionalOnProperty(name = "leaderSelection.enabled", havingValue = "true", matchIfMissing = false)
public class LeaderSelectionController {

    private boolean iAmTheLEader;

    @EventListener
    void handleReturnedEvent(LeaderSelectionEvent event) {
        this.iAmTheLEader = event.isTheLeader();

    }

    @GetMapping
    public String amIYourLeader() {
        return iAmTheLEader ? "{leader: true}" : "{leader: false}";
    }

}
