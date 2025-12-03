-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_role CHECK (role IN ('USER', 'ADMIN', 'MANAGER'))
);

-- Create indices for faster lookups
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password, role, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at)
VALUES ('admin', 'admin@soloware.id', '$2a$10$pnx1UsCisEJrnd.Dzw1gw.DCT.BD4fgIhsEMr9WcoiMZPJ.BokCnC', 'ADMIN', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
