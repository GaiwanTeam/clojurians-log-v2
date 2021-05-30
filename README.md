# Clojurians Log v2

## Development

First start postgres db:

``` sh
docker-compose up
```

then jack-in to a clojure repl and run `(go)` to start the local http server at http://localhost:8080

``` sh
$ clj
> (go)
```

To use psql on the docker postgres

``` sh
docker exec -it clojurians-log-v2_db_1 psql -U myuser clojurians_log
```
