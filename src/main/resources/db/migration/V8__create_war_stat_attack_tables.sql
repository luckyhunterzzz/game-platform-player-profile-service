CREATE TABLE war_stat_attack_team
(
    id UUID PRIMARY KEY,
    player_profile_id UUID NOT NULL,
    name VARCHAR(128) NOT NULL,
    team_order INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_war_stat_attack_team_profile
        FOREIGN KEY (player_profile_id) REFERENCES player_profiles (id) ON DELETE CASCADE,
    CONSTRAINT uq_war_stat_attack_team_profile_order
        UNIQUE (player_profile_id, team_order),
    CONSTRAINT chk_war_stat_attack_team_order
        CHECK (team_order >= 1)
);

CREATE TABLE war_stat_attack_team_slot
(
    id UUID PRIMARY KEY,
    team_id UUID NOT NULL,
    slot SMALLINT NOT NULL,
    player_profile_hero_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_war_stat_attack_team_slot_team
        FOREIGN KEY (team_id) REFERENCES war_stat_attack_team (id) ON DELETE CASCADE,
    CONSTRAINT fk_war_stat_attack_team_slot_profile_hero
        FOREIGN KEY (player_profile_hero_id) REFERENCES player_profile_heroes (id) ON DELETE CASCADE,
    CONSTRAINT uq_war_stat_attack_team_slot_team_slot
        UNIQUE (team_id, slot),
    CONSTRAINT chk_war_stat_attack_team_slot
        CHECK (slot BETWEEN 1 AND 5)
);

CREATE TABLE war_stat_attack_record
(
    id UUID PRIMARY KEY,
    team_id UUID NOT NULL,
    war_mode_id UUID NOT NULL,
    result_type VARCHAR(64) NOT NULL,
    battle_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_war_stat_attack_record_team
        FOREIGN KEY (team_id) REFERENCES war_stat_attack_team (id) ON DELETE CASCADE,
    CONSTRAINT fk_war_stat_attack_record_mode
        FOREIGN KEY (war_mode_id) REFERENCES war_modes (id)
);

CREATE INDEX idx_war_stat_attack_team_profile_id
    ON war_stat_attack_team (player_profile_id);

COMMENT ON TABLE war_stat_attack_team IS 'Stores attack statistic teams for each player profile';
COMMENT ON TABLE war_stat_attack_team_slot IS 'Stores hero slots for attack statistic teams';
COMMENT ON TABLE war_stat_attack_record IS 'Stores battle result records for attack statistic teams';
