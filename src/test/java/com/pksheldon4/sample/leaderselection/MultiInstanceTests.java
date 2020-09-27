package com.pksheldon4.sample.leaderselection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MultiInstanceTests {

    /**
     * Manually start up the Applications on different ports, with different instanceIds
     * - Wait until one assumes leader
     * - Call /leader and assert on response
     * <p>
     *
     * @throws InterruptedException
     */
    @Test
    void test_StartTwoInstances_firstInstanceIsTheLeader() throws InterruptedException {


        SpringApplicationBuilder app1 = new SpringApplicationBuilder(LeaderSelectionDemoApplication.class)
            .properties("server.port=8881", "leaderSelection.instanceId=test-1")
            .profiles("test");
        ConfigurableApplicationContext app1Ctx = app1.run();

        SpringApplicationBuilder app2 = new SpringApplicationBuilder(LeaderSelectionDemoApplication.class)
            .properties("server.port=8882", "leaderSelection.instanceId=test-2")
            .profiles("test");
        ConfigurableApplicationContext app2Ctx = app2.run();

        Thread.sleep(3000);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> app1_LeaderResponse = restTemplate.getForEntity("http://localhost:8881/leader", String.class);
        ResponseEntity<String> app2_LeaderResponse = restTemplate.getForEntity("http://localhost:8882/leader", String.class);

        assertThat(app1_LeaderResponse.getBody()).isEqualTo("{leader: true}");
        assertThat(app2_LeaderResponse.getBody()).isEqualTo("{leader: false}");

        app1Ctx.close();
        app2Ctx.close();
    }

    /**
     * Manually start up the Applications on different ports, with different instanceIds
     * - Start first instance with a long initialDelay
     * - Wait until one assumes leader
     * - Call /leader and assert on response
     * <p>
     *
     * @throws InterruptedException
     */
    @Test
    void test_StartTwoInstances_firstInstanceHasLongInitialDelay_Instance2IsTheLeader() throws InterruptedException {

        SpringApplicationBuilder app1 = new SpringApplicationBuilder(LeaderSelectionDemoApplication.class)
            .logStartupInfo(false)
            .properties("LEADER_INITIAL_DELAY=10000")
            .properties("server.port=8883", "leaderSelection.instanceId=test-1")
            .profiles("test");
        ConfigurableApplicationContext app1Ctx = app1.run();

        SpringApplicationBuilder app2 = new SpringApplicationBuilder(LeaderSelectionDemoApplication.class)
            .properties("server.port=8884", "leaderSelection.instanceId=test-2")
            .profiles("test");
        ConfigurableApplicationContext app2Ctx = app2.run();

        Thread.sleep(3000);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> app1_LeaderResponse = restTemplate.getForEntity("http://localhost:8883/leader", String.class);
        ResponseEntity<String> app2_LeaderResponse = restTemplate.getForEntity("http://localhost:8884/leader", String.class);

        assertThat(app1_LeaderResponse.getBody()).isEqualTo("{leader: false}");
        assertThat(app2_LeaderResponse.getBody()).isEqualTo("{leader: true}");
        app1Ctx.close();
        app2Ctx.close();
    }
}
