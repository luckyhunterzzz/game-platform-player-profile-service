ALTER TABLE player_profile_heroes
    ADD COLUMN costume_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE player_profile_heroes
    ADD CONSTRAINT chk_player_profile_heroes_costume_count
        CHECK (costume_count >= 0);

COMMENT ON COLUMN player_profile_heroes.costume_count IS 'Number of costume versions owned by the player; the value can only increase';
