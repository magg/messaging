CREATE KEYSPACE IF NOT EXISTS messaging WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TYPE messaging.user(
id bigint,
contact_list set<bigint>,
chats set<bigint>
);

CREATE TABLE messaging.users(
id bigint,
contact_list set<bigint>,
chats set<bigint>,
PRIMARY KEY(id));

CREATE TABLE messaging.chats(
id bigint,
participant_ids set<bigint>,
PRIMARY KEY(id));

CREATE TABLE messaging.messages(
id uuid,
time bigint,
message text,
source_user_id bigint,
destination_user_id bigint,
chat_id bigint,
PRIMARY KEY((chat_id), time))
WITH CLUSTERING ORDER BY (time DESC);