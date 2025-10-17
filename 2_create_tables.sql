-- Step 2: Create Tables and Insert Data
-- Run this AFTER creating the database and connecting to ecommerce_db

-- Create Product Table
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
-- Step 1: Create Database (Run this first)
-- Create Cart Item Table
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

-- Create Orders Table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255),
    total_amount DECIMAL(10,2),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING',
    shipping_address TEXT
);

-- Create Order Items Table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Create Payments Table
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

-- Create Indexes for Better Performance
CREATE INDEX idx_cart_item_session_id ON cart_item(session_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_product_available ON product(available);

-- Insert Sample Product Data
INSERT INTO product (available, category, description, name, price, quantity, release_date)
VALUES
    (TRUE, 'Electronics', 'Smartwatch with fitness tracking and GPS', 'Apple Watch Series 9', 399.99, 40, '2025-02-01'),
    (TRUE, 'Electronics', 'High-performance Android smartphone', 'Samsung Galaxy S25', 899.99, 60, '2025-01-20'),
    (TRUE, 'Books', 'Bestselling fantasy novel with hardcover edition', 'The Winds of Winter', 29.99, 200, '2025-04-10'),
    (FALSE, 'Appliances', 'Air conditioner 12000 BTU, energy saving mode', 'LG Smart AC', 499.99, 0, '2024-07-15'),
    (TRUE, 'Gaming', 'VR headset with advanced motion tracking', 'Meta Quest 4', 499.00, 25, '2025-03-12'),
    (TRUE, 'Electronics', 'Mac Mini with M4 chip', 'Mac Mini M4', 600.00, 25, '2025-06-12');

-- Verify Setup
SELECT 'Products inserted successfully. Total count: ' || COUNT(*) as message FROM product;

-- Show all tables created
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- Setup Complete!
-- Execute this separately in your PostgreSQL client

CREATE DATABASE ecommerce_db;
