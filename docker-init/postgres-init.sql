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
CREATE DATABASE app2_db;
CREATE USER app2_user WITH ENCRYPTED PASSWORD 'app2_secure_password';
GRANT ALL PRIVILEGES ON DATABASE app2_db TO app2_user;

-- Connect to app2_db and grant schema privileges
\c app2_db;
GRANT ALL ON SCHEMA public TO app2_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO app2_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO app2_user;

-- Return to postgres database
\c postgres;

-- Print success message
SELECT 'Databases and users created successfully!' AS status;