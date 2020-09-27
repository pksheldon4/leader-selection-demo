# Leader Selection Demo
*Note: I'm calling this leader selection, instead of leader election, because there's no vote. Just a first in wins process.*

### Overview
When running multiple instances of an application, it is sometimes necessary/desired to ensure only a single instance is performing a certain task. In this example I use a database to maintain leadership but this could also be done using something like redis.


### Database
The data structure is simple. A single row in the table will identify the current leader and the last time a heart beat was received from that leader. The heart beat is required because a failed leader can't notify the other instances that it's failed.
```sql
create table if not exists leader_selection
(
    id          serial primary key,
    instance_id varchar(36) not null,
    heart_beat  timestamp   not null
);
```


### Implementation
The Scheduler is implemented using a Spring `@Scheduled` annotation. This uses two properties, `initialDelay` and `fixedDelay`. These control the first time the selection happens and how often it happens and can be tuned based on the application.  The `initialDelay` should be sufficient for the application to be fully initialized.

Each Process, when started, will assign itself a random UUID string. The value of this isn't meaningful, just that it's unique between instances. This value can also be specified as an Environment variable `leaderSelection.instanceId: xxxxx`, which is used for Testing. Care must be taken to ensure no 2 instances are using the same ID.
 
The Leader Selection process also requires a `heartBeatThreashold` property to indicate when non-leaders can assume the leader has stopped, and they should take over leadership.

Flow of leadership selection, based on the schedule.
```
    read leader_selection table
    if empty -> 
        become the leader by create a new leader_selection with this instance_id and heart_beat = now
        publish leaderSelectionEvent(isLeader=true)
    else ->
       if (currentLeader.instance_id = this instance_id) ->
          update leader_selection.heart_beat with current_timestamp
          publish leaderSelectionEvent(isLeader=true)
       else ->
          if ((now - currentLeader.heartbeat) > heartBeatThreshold) ->
             become the leader by updating leader_selection with this instance_id and heart_beat = now
             publish leaderSelectionEvent(isLeader=true)
          else ->
             publish leaderSelectionEvent(isLeader=false)

```

The specific process that is dependent on the leadership status should listen for the `LeaderSelectionEvent`, sent using Springs `ApplicationEventPublisher` and set a flag to know if it should/shouldNot be processing.
In this demo, a separate scheduled process is used to demonstrate this.

```java
public class SomeOtherProcessor {

    private boolean shouldProcess;

    @EventListener
    void handleReturnedEvent(LeaderSelectionEvent event) {
        this.shouldProcess = event.isTheLeader();
    }

    @Scheduled(...schedule parameters...)
    public void doSomething() {
        if (shouldProcess) {
            log.info("****** Some other Process - is being processed by instance");
        }
    }
}
```


### Running with Docker

1. Build the docker image using the built-in SpringBoot [Cloud Native Buildpack Support](https://spring.io/blog/2020/08/14/creating-efficient-docker-images-with-spring-boot-2-3)  using the following command `mvn spring-boot:build-image`
1. Start a postgres Database and 3 instances of the application using the included `docker-compose.yaml` file.

*Note: I use Mysql in the default profile because I have it running on my laptop. I had issues using a MySql docker image so I'm using postgres for the docker profile.*
 
