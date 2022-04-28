# Database

## Prod setup

```
sudo -u postgres psql
postgres=# create database clojurians_log;
CREATE DATABASE
postgres=# create user ox with encrypted password 'pass';
CREATE ROLE
postgres=# grant all privileges on database clojurians_log to ox;
GRANT
```

## Populating the db

On the server first we populate channel and member data using the slack api:

```
root@ark /srv/ox/clojurians-log-v2$ docker exec -it clojurians-log-v2 bash
root@ark:/src# rlwrap nc 0 50505
user=> (require '[clojurians-log.db.bulk-import :as bi])
nil
user=> (bi/channel-member-import)
{:imported-channels-count 849, :imported-members-count 22685}
```

Then to start importing of all messages from a dump

* unzip slack export dump somewhere eg: `/root/clojurians-log-data/$(date +%F)`
* mount `/root/clojurians-log-data/` directory to `/data` as read only bind in docker container
* from the repl run `(bi/messages-all "/data/2021-10-21")` to start the import process
