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
