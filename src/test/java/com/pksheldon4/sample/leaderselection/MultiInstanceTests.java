package com.pksheldon4.sample.leaderselection;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiInstanceTests {

    /**
     *
     * Manually start up the Applications with different profiles
     *   - Wait until one assumes leader
     *   - Call /leader and assert on response
     *
     * NOTE: Had to make sure the first app to shut down didn't close the db
     *   - added '&DB_CLOSE_ON_EXIT=FALSE' to application-test.yaml
     *
     * NOTE: Had to fix typo in '/leader' response
     *   - used to return '{leader; false}' (note the ';')
     *
     * @throws InterruptedException
     */
    @Test
    void test() throws InterruptedException {

        SpringApplicationBuilder app1 =
                new SpringApplicationBuilder( LeaderSelectionDemoApplication.class )
                    .profiles( "test", "instance-1" );
        app1.run();

        SpringApplicationBuilder app2 =
                new SpringApplicationBuilder( LeaderSelectionDemoApplication.class )
                        .profiles( "test", "instance-2" );
        app2.run();

        Thread.sleep( 5000 );

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> app1_LeaderResponse = restTemplate.getForEntity( "http://localhost:8881/leader", String.class );
        ResponseEntity<String> app2_LeaderResponse = restTemplate.getForEntity( "http://localhost:8882/leader", String.class );

        assertThat( app1_LeaderResponse.getBody() ).isEqualTo( "{leader: false}" );
        assertThat( app2_LeaderResponse.getBody() ).isEqualTo( "{leader: true}" );

    }

}
