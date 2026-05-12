ALTER TABLE player_profile_heroes
    ADD COLUMN talent_level INTEGER NOT NULL DEFAULT 0;

ALTER TABLE player_profile_heroes
    ADD CONSTRAINT chk_player_profile_heroes_talent_level
        CHECK (talent_level BETWEEN 0 AND 25);

COMMENT ON COLUMN player_profile_heroes.talent_level IS 'Current talent level selected by the player from 0 to 25';
