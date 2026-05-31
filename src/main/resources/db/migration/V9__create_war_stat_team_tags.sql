CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE war_stat_team_tag
(
    id UUID PRIMARY KEY,
    scope_type VARCHAR(16) NOT NULL,
    player_profile_id UUID NULL,
    category VARCHAR(32) NOT NULL,
    code VARCHAR(64) NULL,
    name VARCHAR(50) NOT NULL,
    icon_key VARCHAR(128) NOT NULL,
    image_url TEXT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_war_stat_team_tag_profile
        FOREIGN KEY (player_profile_id) REFERENCES player_profiles (id) ON DELETE CASCADE,
    CONSTRAINT chk_war_stat_team_tag_scope
        CHECK (scope_type IN ('SYSTEM', 'CUSTOM')),
    CONSTRAINT chk_war_stat_team_tag_category
        CHECK (category IN ('WAR_MODE', 'ELEMENT', 'CUSTOM'))
);

CREATE TABLE war_stat_attack_team_tag_link
(
    id UUID PRIMARY KEY,
    team_id UUID NOT NULL,
    tag_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_war_stat_attack_team_tag_link_team
        FOREIGN KEY (team_id) REFERENCES war_stat_attack_team (id) ON DELETE CASCADE,
    CONSTRAINT fk_war_stat_attack_team_tag_link_tag
        FOREIGN KEY (tag_id) REFERENCES war_stat_team_tag (id) ON DELETE CASCADE,
    CONSTRAINT uq_war_stat_attack_team_tag_link
        UNIQUE (team_id, tag_id)
);

CREATE INDEX idx_war_stat_team_tag_profile_id
    ON war_stat_team_tag (player_profile_id);

CREATE UNIQUE INDEX uq_war_stat_team_tag_system_category_code
    ON war_stat_team_tag (category, code)
    WHERE scope_type = 'SYSTEM';

CREATE UNIQUE INDEX uq_war_stat_team_tag_custom_profile_name
    ON war_stat_team_tag (player_profile_id, LOWER(name))
    WHERE scope_type = 'CUSTOM';

INSERT INTO war_stat_team_tag (id, scope_type, player_profile_id, category, code, name, icon_key, image_url, created_at, updated_at)
SELECT gen_random_uuid(),
       'SYSTEM',
       NULL,
       'WAR_MODE',
       wm.code,
       COALESCE(wm.name_json::jsonb ->> 'en', wm.code),
       LOWER(wm.code),
       CASE UPPER(wm.code)
           WHEN 'UNIVERSAL' THEN '/heroes/elements/star/symbol_star_big_small.webp'
           WHEN 'SKYFIRE' THEN '/war-modes/skyfire.png'
           WHEN 'RUSH_ATTACK' THEN '/war-modes/rush_attack.png'
           WHEN 'WAR_EQUALIZER' THEN '/war-modes/war_equalizer.png'
           WHEN 'ARROW_BARRAGE' THEN '/war-modes/arrow_barrage.png'
           WHEN 'ATTACK_BOOST' THEN '/war-modes/attack_boost.png'
           WHEN 'UNDEAD_HORDE' THEN '/war-modes/undead_horde.png'
           WHEN 'BLOODY_BATTLE' THEN '/war-modes/bloody_battle.png'
           WHEN 'CLOVERFIELD' THEN '/war-modes/cloverfield.png'
           WHEN 'ANCIENT_TERROR' THEN '/war-modes/ancient_terror.png'
           ELSE NULL
       END,
       NOW(),
       NOW()
FROM war_modes wm
WHERE NOT EXISTS (
    SELECT 1
    FROM war_stat_team_tag existing
    WHERE existing.scope_type = 'SYSTEM'
      AND existing.category = 'WAR_MODE'
      AND existing.code = wm.code
);

INSERT INTO war_stat_team_tag (id, scope_type, player_profile_id, category, code, name, icon_key, image_url, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'SYSTEM', NULL, 'ELEMENT', 'FIRE', 'Fire', 'fire', '/heroes/elements/elements/herald_red.webp', NOW(), NOW()),
    (gen_random_uuid(), 'SYSTEM', NULL, 'ELEMENT', 'ICE', 'Ice', 'ice', '/heroes/elements/elements/herald_blue.webp', NOW(), NOW()),
    (gen_random_uuid(), 'SYSTEM', NULL, 'ELEMENT', 'NATURE', 'Nature', 'nature', '/heroes/elements/elements/herald_green.webp', NOW(), NOW()),
    (gen_random_uuid(), 'SYSTEM', NULL, 'ELEMENT', 'HOLY', 'Holy', 'holy', '/heroes/elements/elements/herald_yellow.webp', NOW(), NOW()),
    (gen_random_uuid(), 'SYSTEM', NULL, 'ELEMENT', 'DARK', 'Dark', 'dark', '/heroes/elements/elements/herald_purple.webp', NOW(), NOW())
ON CONFLICT DO NOTHING;

COMMENT ON TABLE war_stat_team_tag IS 'Stores system and custom tags for war statistic teams';
COMMENT ON TABLE war_stat_attack_team_tag_link IS 'Stores assigned tags for war statistic teams';
