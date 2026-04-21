CREATE TABLE player_profile_heroes (
    id UUID PRIMARY KEY,
    player_profile_id UUID NOT NULL,
    hero_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_player_profile_heroes_profile
        FOREIGN KEY (player_profile_id) REFERENCES player_profiles (id)
);

CREATE INDEX idx_player_profile_heroes_profile_id
    ON player_profile_heroes (player_profile_id);

COMMENT ON TABLE player_profile_heroes IS 'Stores hero entries added to player profiles';
COMMENT ON COLUMN player_profile_heroes.id IS 'Internal unique identifier (Primary Key)';
COMMENT ON COLUMN player_profile_heroes.player_profile_id IS 'Reference to player profile';
COMMENT ON COLUMN player_profile_heroes.hero_id IS 'Hero identifier from main-service catalog';
COMMENT ON COLUMN player_profile_heroes.created_at IS 'Timestamp when hero entry was added to profile';
