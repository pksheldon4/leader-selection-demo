package com.pksheldon4.sample.leaderselection;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "leader_selection")
@Data
@NoArgsConstructor
public class LeaderSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String instanceId;
    private LocalDateTime heartBeat;

    public LeaderSelection(String instanceId) {
        this.instanceId = instanceId;
        this.heartBeat = LocalDateTime.now();
    }

    public LeaderSelection(String instanceId, LocalDateTime heartBeat) {
        this.instanceId = instanceId;
        this.heartBeat = heartBeat;
    }
}
