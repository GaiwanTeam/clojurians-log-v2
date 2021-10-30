DROP INDEX IF EXISTS message_channel_id_idx;
CREATE INDEX message_channel_id_idx ON message (channel_id);

DROP INDEX IF EXISTS message_channel_id_created_at_idx;
CREATE INDEX message_channel_id_created_at_idx ON message (channel_id, created_at);

DROP INDEX IF EXISTS message_search_idx;
CREATE INDEX message_search_idx ON message USING GIN (to_tsvector('english', text));
