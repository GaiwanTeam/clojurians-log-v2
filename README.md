# Clojurians Log v2

This is a v2 of [clojurians-log-app](https://github.com/clojureverse/clojurians-log-app) 

## Why v2?

I & the [Gaiwan team](https://gaiwan.co) help in maintaining the original
clojurians-app instance, and feel that it's quite possible to fix all the existing
issues in clojurians-log and keep maintaining it.

But the decision to make an independent v2 makes a lot of sense to me personally
to get a chance to completely re-architect the solution form all the lessons
we've learnt over the years. It also sounds much more exciting 🙈🙈

We will still be able to carry some of the independent modules over and
improvise on them, so it's not all waste :)

Some of the issues **I want to solve/implement** with v2:
- mobile responsive & usability (this is something I really really want personally)
- integrating real time search for entire logs (super excited for this)
- using newer slack api's (the current ones we used are deprecated)
- slack imports from the beginning of time
- idempotent incremental archive imports
- real-time logging (no more waiting till the end of the day for
archive to update)
- improve performance & reduce resource consumption so we can
run this on a much smaller instance
- better statistics reporting (automated stats email to admins/staff)
- open analytics 
- support for images and attachments
- fix all of the SEO issues (internal links showing up on google, better indexing, etc)
- more documentation to make it even more easier for folks to contribute 
- possibly automated email digests tracking specific keywords
- slack-bot custom commands
  - delete / anonymise a user
  - automated book & resource recommendations
  - thread saving
  - many more custom use cases could be supported using emojis & custom commands
- many more ideas!

## Why not a static site?

I really really love static site generation, but I think we could discuss a bit
more about this:

*Some pros of SSG*:
- Static site won't go down
- Fast page load times

*Some cons of SSG*:
- regenerating pages on delete events (these can go back upto a few weeks)
- regenerating pages on new reactions added 
- incremental generation is hard
- generating links (which date preceed and succeed #channel-foo on 2021-05-30
which have existing messages)
- changing rendering code / fixing a frontend bug means we need to re-render
everything, "selective rendering" of only last month would lead to divergence in
pages and subtle bugs
- we still need to maintain a server to actually fetch the slack events
real-time, so it's not "ops-free"
- the amount of archived data will only keep on increasing, which will keep
increasing the magnitude of all of the above issues
- On top of this, we've got a request by a member if they can opt-out from
having their messages archived. This wasn't a strict request from their end, but
it would be a nice to have feature to at least anonymize the messages if a user
says so (by using a slack command like /clojurians-log anonymize-me) I don't
want to dismiss the idea of static generation (especially as an add-on), but it
might be more work than expected.

A lot of the above features we want to implement is simply not possible with the
static site. This is why I am trying to create clojurians-log v2 as a dynamic
server rendered site and keeping in mind of all of the issues we're facing in
the way we log currently.

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

## Contribute

This project is currently undergoing major changes. Before you contribute please
create an issue to discuss your idea as things may change drastically and
rapidly over the next few weeks.

Please feel free to create issues to discuss if you have any cool ideas which we could
implement in the future, or let me know if you just want to have a chat about this :)
