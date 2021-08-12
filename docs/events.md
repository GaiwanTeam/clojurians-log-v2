# Events

## Subtypes from full archive

For bulk imports the most common subtypes are the following:

```
$ src\sample_data> rg -I subtype | awk '{print $2}' | sed 's/,//' | sort | uniq -c | sort -n
      ...
      7 "bot_disable"
     12 "file_comment"
     12 "subtype?"
     24 "bot_remove"
     27 "channel_name"
     44 "slackbot_response"
     47 "bot_add"
    162 "tombstone"
    255 "pinned_item"
    373 "reply_broadcast"
    478 "channel_purpose"
    717 "channel_topic"
   2587 "me_message"
   4036 "thread_broadcast"
  23674 "bot_message"
  25018 "channel_leave"
 238742 "channel_join"
```

Socket mode / Real time events API generally has more subtypes.
