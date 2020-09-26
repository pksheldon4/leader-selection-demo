create table if not exists leader_selection
(
    id          serial primary key,
    instance_id varchar(36) not null,
    heart_beat  timestamp   not null
);
