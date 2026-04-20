ALTER TABLE player_profiles
    ADD COLUMN vk_username VARCHAR(100),
    ADD COLUMN discord_username VARCHAR(100);

COMMENT ON COLUMN player_profiles.vk_username IS 'User handle in VK for coordination';
COMMENT ON COLUMN player_profiles.discord_username IS 'User handle in Discord for coordination';
