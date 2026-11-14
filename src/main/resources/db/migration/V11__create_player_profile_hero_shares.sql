CREATE TABLE player_profile_hero_shares (
    id UUID PRIMARY KEY,
    token UUID NOT NULL UNIQUE,
    player_profile_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_player_profile_hero_shares_profile
        FOREIGN KEY (player_profile_id) REFERENCES player_profiles (id) ON DELETE CASCADE
);

CREATE TABLE player_profile_hero_share_items (
    id UUID PRIMARY KEY,
    share_id UUID NOT NULL,
    hero_id BIGINT NOT NULL,
    power_grade VARCHAR(64) NOT NULL,
    talent_level INTEGER NOT NULL,
    costume_count INTEGER NOT NULL,
    item_order INTEGER NOT NULL,

    CONSTRAINT fk_player_profile_hero_share_items_share
        FOREIGN KEY (share_id) REFERENCES player_profile_hero_shares (id) ON DELETE CASCADE,
    CONSTRAINT chk_player_profile_hero_share_items_talent_level
        CHECK (talent_level >= 0),
    CONSTRAINT chk_player_profile_hero_share_items_costume_count
        CHECK (costume_count >= 0),
    CONSTRAINT uq_player_profile_hero_share_items_order
        UNIQUE (share_id, item_order)
);

CREATE INDEX idx_player_profile_hero_shares_profile_id
    ON player_profile_hero_shares (player_profile_id);

CREATE INDEX idx_player_profile_hero_share_items_share_id
    ON player_profile_hero_share_items (share_id);

COMMENT ON TABLE player_profile_hero_shares IS 'Immutable public snapshots of a player hero list';
COMMENT ON COLUMN player_profile_hero_shares.expires_at IS 'Expiration timestamp; NULL means the link has no expiration';
COMMENT ON TABLE player_profile_hero_share_items IS 'Hero progression copied into a public share snapshot';
