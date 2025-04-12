CREATE MATERIALIZED VIEW user_profile_view AS
SELECT 
    u.id,
    u.username,
    u.email,
    p.profile_picture,
    p.bio,
    p.location,
    (
        SELECT COUNT(*) 
        FROM friendships f 
        WHERE f.user_id = u.id 
        AND f.status = 'ACCEPTED'
    ) as friend_count,
    (
        SELECT COUNT(*) 
        FROM messages m 
        JOIN chats c ON m.chat_id = c.id 
        WHERE (c.user1_id = u.id OR c.user2_id = u.id) 
        AND m.sender_id != u.id 
        AND m.is_read = false
    ) as unread_messages,
    u.updated_at as last_active
FROM users u
LEFT JOIN profiles p ON u.id = p.user_id;

CREATE INDEX idx_user_profile_view_id ON user_profile_view (id);
CREATE INDEX idx_user_profile_view_username ON user_profile_view (username);
CREATE INDEX idx_user_profile_view_email ON user_profile_view (email);
CREATE INDEX idx_user_profile_view_last_active ON user_profile_view (last_active);

-- Function to refresh the materialized view
CREATE OR REPLACE FUNCTION refresh_user_profile_view()
RETURNS TRIGGER AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY user_profile_view;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Triggers to automatically refresh the materialized view
CREATE TRIGGER refresh_user_profile_view_after_user_update
AFTER UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION refresh_user_profile_view();

CREATE TRIGGER refresh_user_profile_view_after_profile_update
AFTER UPDATE ON profiles
FOR EACH ROW
EXECUTE FUNCTION refresh_user_profile_view();

CREATE TRIGGER refresh_user_profile_view_after_friendship_update
AFTER UPDATE ON friendships
FOR EACH ROW
EXECUTE FUNCTION refresh_user_profile_view();

CREATE TRIGGER refresh_user_profile_view_after_message_update
AFTER UPDATE ON messages
FOR EACH ROW
EXECUTE FUNCTION refresh_user_profile_view(); 