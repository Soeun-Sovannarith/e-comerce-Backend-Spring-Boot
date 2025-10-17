-- Updated PostgreSQL Database Setup Script for E-Commerce Project with Authentication
-- Run this script in your PostgreSQL client (pgAdmin, psql, etc.)

-- Create Database (if not exists)
CREATE DATABASE IF NOT EXISTS ecommerce_db;

-- Connect to the database and create tables
-- Use: \c ecommerce_db (in psql) or switch to ecommerce_db database in your GUI tool

-- 1. Create Users Table (Authentication)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    last_failed_login TIMESTAMP,
    locked_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create Product Table
CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(10,2),
    category VARCHAR(100),
    release_date DATE,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    quantity INTEGER NOT NULL DEFAULT 0,
    image_name VARCHAR(255),
    image_type VARCHAR(100),
    image_data BYTEA
);

-- 3. Create Cart Item Table
CREATE TABLE cart_item (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255),
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id),
    UNIQUE(session_id, product_id)
);

-- 4. Create Orders Table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255),
    total_amount DECIMAL(10,2),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING',
    shipping_address TEXT
);

-- 5. Create Order Items Table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- 6. Create Payments Table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT,
    amount DECIMAL(10,2),
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    card_last_four VARCHAR(4),
    cardholder_name VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- 7. Create Indexes for Better Performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_cart_item_session_id ON cart_item(session_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_product_available ON product(available);

-- 8. Insert Sample Product Data


-- 9. Insert Sample Users (password is 'password123' for all users - BCrypt hashed)
INSERT INTO users (username, email, password, role, enabled)
VALUES
    ('admin', 'admin@sovannarithshop.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_ADMIN', TRUE),
    ('testuser', 'user@sovannarithshop.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER', TRUE);

-- 10. Verify Setup
SELECT 'Products inserted successfully. Total count: ' || COUNT(*) as message FROM product;
SELECT 'Users inserted successfully. Total count: ' || COUNT(*) as message FROM users;

-- 11. Show all tables created
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- Setup Complete!
-- Your e-commerce database with authentication is ready to use with Spring Boot
