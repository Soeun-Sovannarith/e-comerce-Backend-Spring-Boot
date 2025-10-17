-- PostgreSQL Database Setup Script for E-Commerce Project
-- Run this script in your PostgreSQL client (pgAdmin, psql, etc.)

-- 1. Create Database (run this first as postgres user)
CREATE DATABASE ecommerce_db;

-- 2. Connect to the database and create tables
-- Use: \c ecommerce_db (in psql) or switch to ecommerce_db database in your GUI tool

-- 3. Create Product Table
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

-- 4. Create Cart Item Table
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

-- 5. Create Orders Table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255),
    total_amount DECIMAL(10,2),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING',
    shipping_address TEXT
);

-- 6. Create Order Items Table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- 7. Create Payments Table
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

-- 8. Create Indexes for Better Performance
CREATE INDEX idx_cart_item_session_id ON cart_item(session_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_product_available ON product(available);



-- 10. Verify Data Insertion
SELECT 'Products inserted successfully. Total count: ' || COUNT(*) as message FROM product;

-- 11. Show all tables created
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- Setup Complete!
-- Your e-commerce database is ready to use with Spring Boot
