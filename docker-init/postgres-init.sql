-- Create first database and user
CREATE DATABASE app1_db;
CREATE USER app1_user WITH ENCRYPTED PASSWORD 'app1_secure_password';
GRANT ALL PRIVILEGES ON DATABASE app1_db TO app1_user;

-- Connect to app1_db and grant schema privileges
\c app1_db;
GRANT ALL ON SCHEMA public TO app1_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO app1_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO app1_user;

-- Create second database and user
\c postgres;
CREATE DATABASE circles_db;
CREATE USER circles_user WITH ENCRYPTED PASSWORD 'circles_secure_password';
GRANT ALL PRIVILEGES ON DATABASE circles_db TO circles_user;

-- Connect to circles_db and grant schema privileges
\c circles_db;
GRANT ALL ON SCHEMA public TO circles_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO circles_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO circles_user;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create circles table
CREATE TABLE IF NOT EXISTS circles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    circle_type VARCHAR(50) NOT NULL,
    invite_code VARCHAR(12) UNIQUE,
    privacy VARCHAR(20) NOT NULL,
    avatar_url VARCHAR(500),
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    settings JSONB,
    CONSTRAINT chk_circle_type CHECK (circle_type IN ('FAMILY', 'FRIENDS', 'WORK', 'HOBBY', 'COMMUNITY', 'OTHER')),
    CONSTRAINT chk_privacy CHECK (privacy IN ('PUBLIC', 'PRIVATE', 'INVITE_ONLY'))
);

-- Create circle_members table
CREATE TABLE IF NOT EXISTS circle_members (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    circle_id UUID NOT NULL,
    user_id UUID NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    user_avatar VARCHAR(500),
    role VARCHAR(20) NOT NULL,
    nickname VARCHAR(100),
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_circle FOREIGN KEY (circle_id) REFERENCES circles(id) ON DELETE CASCADE,
    CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'MEMBER', 'VIEWER')),
    CONSTRAINT uq_circle_user UNIQUE (circle_id, user_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_circles_created_by ON circles(created_by);
CREATE INDEX IF NOT EXISTS idx_circles_circle_type ON circles(circle_type);
CREATE INDEX IF NOT EXISTS idx_circles_privacy ON circles(privacy);
CREATE INDEX IF NOT EXISTS idx_circles_invite_code ON circles(invite_code);
CREATE INDEX IF NOT EXISTS idx_circle_members_circle_id ON circle_members(circle_id);
CREATE INDEX IF NOT EXISTS idx_circle_members_user_id ON circle_members(user_id);
CREATE INDEX IF NOT EXISTS idx_circle_members_role ON circle_members(role);

-- Create triggers for updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_circles_updated_at BEFORE UPDATE ON circles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_circle_members_updated_at BEFORE UPDATE ON circle_members
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data (optional, for development)
-- Uncomment if you want seed data
/*
INSERT INTO circles (id, name, description, circle_type, invite_code, privacy, created_by, settings)
VALUES 
    (uuid_generate_v4(), 'Family Circle', 'Our family group', 'FAMILY', 'FAM12345', 'PRIVATE', uuid_generate_v4(), '{"notifications": true}'),
    (uuid_generate_v4(), 'Work Team', 'Project team collaboration', 'WORK', 'WORK5678', 'INVITE_ONLY', uuid_generate_v4(), '{"theme": "dark"}');
*/

-- Grant permissions (if needed)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;

-- Return to postgres database
\c postgres;

-- Print success message
SELECT 'Databases and users created successfully!' AS status;