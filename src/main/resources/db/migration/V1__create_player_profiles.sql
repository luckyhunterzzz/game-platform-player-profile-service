CREATE TABLE player_profiles (
    id UUID primary key,
    user_id UUID NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    telegram_username VARCHAR(100),
    current_game_nickname VARCHAR(100),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

COMMENT ON TABLE player_profiles IS 'Stores business profile information for platform users';
COMMENT ON COLUMN player_profiles.id IS 'Internal unique identifier (Primary Key)';
COMMENT ON COLUMN player_profiles.user_id IS 'External user ID from Keycloak (Identity link)';
COMMENT ON COLUMN player_profiles.email IS 'User email address provided by Identity Provider';
COMMENT ON COLUMN player_profiles.first_name IS 'User first name';
COMMENT ON COLUMN player_profiles.last_name IS 'User last name';
COMMENT ON COLUMN player_profiles.telegram_username IS 'User handle in Telegram for coordination';
COMMENT ON COLUMN player_profiles.current_game_nickname IS 'Current active nickname used in the game';
COMMENT ON COLUMN player_profiles.status IS 'Current profile state: INCOMPLETE, COMPLETE, or SUSPENDED';
COMMENT ON COLUMN player_profiles.created_at IS 'Timestamp when the profile was first created';
COMMENT ON COLUMN player_profiles.updated_at IS 'Timestamp of the last profile update';