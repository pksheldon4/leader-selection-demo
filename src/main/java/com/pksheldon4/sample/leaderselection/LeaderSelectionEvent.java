package com.pksheldon4.sample.leaderselection;

import lombok.Getter;

@Getter
public class LeaderSelectionEvent {

    private final String instanceId;
    private final boolean theLeader;

    public LeaderSelectionEvent(String instanceId, boolean theLeader) {
        this.theLeader = theLeader;
        this.instanceId = instanceId;
    }
}
