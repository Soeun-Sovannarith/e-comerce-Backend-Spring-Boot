-- Production database initialization script
-- This will be automatically executed when PostgreSQL container starts

-- Create the database if it doesn't exist
-- (This is handled by POSTGRES_DB environment variable in docker-compose)

-- You can add any additional database setup here
-- For example, creating additional schemas, users, or initial data

-- Example: Create a read-only user for monitoring
-- CREATE USER monitoring_user WITH PASSWORD 'monitoring_password';
-- GRANT CONNECT ON DATABASE ecommerce_db TO monitoring_user;
-- GRANT USAGE ON SCHEMA public TO monitoring_user;
-- GRANT SELECT ON ALL TABLES IN SCHEMA public TO monitoring_user;
