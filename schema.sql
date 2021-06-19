DROP table if exists channel CASCADE;
DROP table if exists "user" CASCADE;
DROP table if exists "reaction" CASCADE;
DROP table if exists "file" CASCADE;
DROP table if exists "message" CASCADE;

CREATE table if not exists channel (
 id integer primary key generated always as identity,
 slack_id text not null,
 "name" text not null
);

CREATE table if not exists "user" (
 id integer primary key generated always as identity,
 slack_id text not null,
 team_id text,
 "name" text not null,
 real_name text,
 real_name_normalized text,
 display_name text,
 display_name_normalized text,
 avatar_hash text,
 image_24 text,
 image_32 text,
 image_48 text,
 image_72 text,
 image_192 text,
 image_512 text,
 email text,
 first_name text,
 last_name text,
 title text,
 skype text,
 phone text,
 is_email_confirmed boolean default false,
 is_admin boolean default false,
 is_bot boolean default false,
 tz text,
 tz_offset integer default 0,
 tz_label text,
 deleted boolean default false,
 bot_id text
 );

CREATE table if not exists "file" (
 id integer primary key generated always as identity,
 slack_id text not null,
 channel_id integer references "channel",
 user_id integer references "user",
 ts text,
 created timestamptz,
 "timestamp" timestamptz,
 "name" text,
 title text,
 mode text,
 mimetype text,
 fieltype text,
 pretty_type text,
 is_external boolean default false,
 external_type text,
 size integer,
 url_private text,
 url_private_download text
);

CREATE table if not exists "message" (
 id integer primary key generated always as identity,
 channel_id integer references channel,
 user_id integer references "user",
 "text" text,
 ts text,
 parent integer references "message",
 deleted_ts text
);

CREATE table if not exists "reaction" (
 id integer primary key generated always as identity,
 user_id integer references "user",
 channel_id integer references "channel",
 message_id integer references "message",
 reaction text,
 url text
);
