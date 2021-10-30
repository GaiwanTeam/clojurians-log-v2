DROP index IF EXISTS message_channel_id_idx;
CREATE INDEX message_channel_id_idx ON message (channel_id);

DROP index IF EXISTS message_channel_id_created_at_idx;
CREATE INDEX message_channel_id_created_at_idx ON message (channel_id, created_at);
