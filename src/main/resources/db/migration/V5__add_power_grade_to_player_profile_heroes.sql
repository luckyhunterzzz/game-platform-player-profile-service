ALTER TABLE player_profile_heroes
    ADD COLUMN power_grade VARCHAR(64) NOT NULL DEFAULT 'FULLY_ASCENDED';

COMMENT ON COLUMN player_profile_heroes.power_grade IS 'Current hero power grade selected by the player';
