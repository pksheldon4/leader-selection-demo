package com.pksheldon4.sample.leaderselection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class LeaderSelectionDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaderSelectionDemoApplication.class, args);
    }
}
