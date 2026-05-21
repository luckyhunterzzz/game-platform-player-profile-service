CREATE TABLE war_modes
(
    id UUID PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name_json JSONB NOT NULL,
    description_json JSONB NOT NULL,
    sort_order SMALLINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uq_war_modes_code UNIQUE (code)
);

INSERT INTO war_modes (id, code, name_json, description_json, sort_order, active, created_at, updated_at)
VALUES
    ('0f39dfc7-b8b4-46a0-92cf-4ed4e3e2f001', 'UNIVERSAL', '{"ru":"Универсальная","en":"Universal"}', '{"ru":"Команды для любого режима войны","en":"Teams for any war mode"}', 1, TRUE, NOW(), NOW()),
    ('0f39dfc7-b8b4-46a0-92cf-4ed4e3e2f002', 'SKYFIRE', '{"ru":"Небесное пламя","en":"Skyfire"}', '{"ru":"Атакующие драконы становятся сильнее","en":"Attacking dragons are more powerful"}', 2, TRUE, NOW(), NOW()),
    ('0f39dfc7-b8b4-46a0-92cf-4ed4e3e2f003', 'RUSH_ATTACK', '{"ru":"Стремительная атака","en":"Rush Attack"}', '{"ru":"Все герои: очень быстрая мана","en":"All heroes: very fast mana"}', 3, TRUE, NOW(), NOW()),
    ('0f39dfc7-b8b4-46a0-92cf-4ed4e3e2f004', 'WAR_EQUALIZER', '{"ru":"Боевое равенство","en":"War Equalizer"}', '{"ru":"Каждые 3 хода: все статус-эффекты снимаются","en":"Every 3 turns: status effects removed"}', 4, TRUE, NOW(), NOW()),
    ('0f39dfc7-b8b4-46a0-92cf-4ed4e3e2f005', 'ARROW_BARRAGE', '{"ru":"Град стрел","en":"Arrow Barrage"}', '{"ru":"При активации: атакующие теряют 25% текущего здоровья","en":"When activated: attackers lose 25% current HP"}', 5, TRUE, NOW(), NOW()),
    ('0f39dfc7-b8b4-46a0-92cf-4ed4e3e2f006', 'ATTACK_BOOST', '{"ru":"Бонус к атаке","en":"Attack Boost"}', '{"ru":"Нарастающий бонус атаки для защитников (неснимаемый)","en":"Scaling attack buff for defenders (undispellable)"}', 6, TRUE, NOW(), NOW()),
    ('0f39dfc7-b8b4-46a0-92cf-4ed4e3e2f007', 'UNDEAD_HORDE', '{"ru":"Орда зомби","en":"Undead Horde"}', '{"ru":"Каждые 5 ходов появляются скелеты-прислужники","en":"Every 5 turns: skeletal minions"}', 7, TRUE, NOW(), NOW()),
    ('0f39dfc7-b8b4-46a0-92cf-4ed4e3e2f008', 'BLOODY_BATTLE', '{"ru":"Кровавая война","en":"Bloody Battle"}', '{"ru":"Нельзя лечиться и воскрешаться","en":"No healing / revival"}', 8, TRUE, NOW(), NOW()),
    ('0f39dfc7-b8b4-46a0-92cf-4ed4e3e2f009', 'CLOVERFIELD', '{"ru":"Поле клевера","en":"Cloverfield"}', '{"ru":"Шанс применить особый навык дважды","en":"Chance to cast special skill twice"}', 9, TRUE, NOW(), NOW()),
    ('0f39dfc7-b8b4-46a0-92cf-4ed4e3e2f010', 'ANCIENT_TERROR', '{"ru":"Древний ужас","en":"Ancient Terror"}', '{"ru":"При активации: атакующие / защитники получают +10 / +20 Безумия","en":"When activated: attackers / defenders get +10 / +20 insanity"}', 10, TRUE, NOW(), NOW());

ALTER TABLE team_war_attack
    ADD COLUMN war_mode_id UUID;

UPDATE team_war_attack
SET war_mode_id = '0f39dfc7-b8b4-46a0-92cf-4ed4e3e2f001'
WHERE war_mode_id IS NULL;

ALTER TABLE team_war_attack
    ALTER COLUMN war_mode_id SET NOT NULL;

ALTER TABLE team_war_attack
    ADD CONSTRAINT fk_team_war_attack_war_mode
        FOREIGN KEY (war_mode_id) REFERENCES war_modes (id);

ALTER TABLE team_war_attack
    DROP CONSTRAINT uq_team_war_attack_profile_team_index;

ALTER TABLE team_war_attack
    ADD CONSTRAINT uq_team_war_attack_profile_mode_team_index
        UNIQUE (player_profile_id, war_mode_id, team_index);

ALTER TABLE team_war_attack_heroes
    DROP CONSTRAINT uq_team_war_attack_heroes_profile_hero;

CREATE INDEX idx_war_modes_sort_order
    ON war_modes (sort_order);

CREATE INDEX idx_team_war_attack_player_profile_mode_id
    ON team_war_attack (player_profile_id, war_mode_id);

COMMENT ON TABLE war_modes IS 'Stores available war modes including universal mode';
COMMENT ON COLUMN war_modes.name_json IS 'Localized mode names in JSON format';
COMMENT ON COLUMN war_modes.description_json IS 'Localized mode descriptions in JSON format';
