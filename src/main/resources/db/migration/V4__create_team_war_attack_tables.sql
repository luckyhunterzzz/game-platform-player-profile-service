CREATE TABLE team_war_attack
(
    id UUID PRIMARY KEY,
    player_profile_id UUID NOT NULL,
    team_index SMALLINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_team_war_attack_profile
        FOREIGN KEY (player_profile_id) REFERENCES player_profiles (id),
    CONSTRAINT uq_team_war_attack_profile_team_index
        UNIQUE (player_profile_id, team_index),
    CONSTRAINT chk_team_war_attack_team_index
        CHECK (team_index BETWEEN 1 AND 6)
);

CREATE TABLE team_war_attack_heroes
(
    id UUID PRIMARY KEY,
    team_war_attack_id UUID NOT NULL,
    player_profile_hero_id UUID NOT NULL,
    slot SMALLINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_team_war_attack_heroes_team
        FOREIGN KEY (team_war_attack_id) REFERENCES team_war_attack (id) ON DELETE CASCADE,
    CONSTRAINT fk_team_war_attack_heroes_profile_hero
        FOREIGN KEY (player_profile_hero_id) REFERENCES player_profile_heroes (id) ON DELETE CASCADE,
    CONSTRAINT uq_team_war_attack_heroes_team_slot
        UNIQUE (team_war_attack_id, slot),
    CONSTRAINT uq_team_war_attack_heroes_profile_hero
        UNIQUE (player_profile_hero_id),
    CONSTRAINT chk_team_war_attack_heroes_slot
        CHECK (slot BETWEEN 1 AND 5)
);

CREATE INDEX idx_team_war_attack_player_profile_id
    ON team_war_attack (player_profile_id);

CREATE INDEX idx_team_war_attack_heroes_team_war_attack_id
    ON team_war_attack_heroes (team_war_attack_id);

COMMENT ON TABLE team_war_attack IS 'Stores six war attack teams for each player profile';
COMMENT ON COLUMN team_war_attack.team_index IS 'Team position from 1 to 6 within player profile';
COMMENT ON TABLE team_war_attack_heroes IS 'Stores hero assignments for war attack team slots';
COMMENT ON COLUMN team_war_attack_heroes.slot IS 'Slot position from 1 to 5 within war attack team';
