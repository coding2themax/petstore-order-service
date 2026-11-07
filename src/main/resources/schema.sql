CREATE SCHEMA IF NOT EXISTS petstore;

CREATE TABLE IF NOT EXISTS petstore.pet_order (
    order_id VARCHAR(255) PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50),
    shipping_address_street VARCHAR(255),
    shipping_address_city VARCHAR(100),
    shipping_address_state VARCHAR(100),
    shipping_address_zip VARCHAR(20),
    total_amount DECIMAL(10, 2) NOT NULL,
    special_instructions TEXT
);

CREATE TABLE IF NOT EXISTS petstore.order_item (
    item_id SERIAL PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES petstore.pet_order(order_id) ON DELETE CASCADE
);

-- Horses kept at the store and their daily feeding schedules
CREATE TABLE IF NOT EXISTS petstore.horse (
    horse_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    breed VARCHAR(100),
    birth_date DATE,
    weight_kg DECIMAL(6, 2),
    stall_number VARCHAR(20),
    notes TEXT
);

CREATE TABLE IF NOT EXISTS petstore.feeding_schedule (
    schedule_id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL,
    feed_time TIME NOT NULL,
    feed_type VARCHAR(100) NOT NULL,
    quantity_kg DECIMAL(6, 2) NOT NULL CHECK (quantity_kg > 0),
    notes TEXT,
    FOREIGN KEY (horse_id) REFERENCES petstore.horse(horse_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_feeding_schedule_horse_id ON petstore.feeding_schedule(horse_id);
CREATE INDEX IF NOT EXISTS idx_feeding_schedule_feed_time ON petstore.feeding_schedule(feed_time);
