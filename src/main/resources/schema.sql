CREATE SCHEMA IF NOT EXISTS petstore;

CREATE TABLE IF NOT EXISTS petstore.pet_order (
    order_id VARCHAR(255) PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
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