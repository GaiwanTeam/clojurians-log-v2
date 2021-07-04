# Clojurians Log v2

## Development

First start postgres db:

``` sh
docker-compose up
```

then jack-in to a clojure repl and run `(go)` to start the local http server at http://localhost:8080

``` sh
$ clj -A:dev
> (go)
```

To use psql on the docker postgres

``` sh
docker exec -it clojurians-log-v2_db_1 psql -U myuser clojurians_log
```

To use pgcli instead

``` sh
pip install -U pgcli
make pgcli 
# if it asks for a pass enter "mypass"
```

To initialise schema, open pgcli and then load the schema file

``` sql
\i schema.sql
```

this can be done again to reset the database to a clean slate.

For populating the db from a slack archive, check `bulk_import.clj` file.
