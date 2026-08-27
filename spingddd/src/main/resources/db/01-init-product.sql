-- Product
DROP TABLE IF EXISTS product;
CREATE TABLE product (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  price NUMERIC(12, 2) NOT NULL DEFAULT 0.00
);
INSERT INTO
  product (name, price)
VALUES
  ('Điện thoại iPhone 15 Pro Max', 29990000.00),
  ('Tai nghe AirPods Pro 2', 5490000.00),
  ('Sạc dự phòng Anker 20000mAh', 850000.00);
-- // Inventory
DROP TABLE IF EXISTS inventory;
CREATE TABLE inventory (
  id SERIAL PRIMARY KEY,
  product_id INT NOT NULL UNIQUE,
  available_quantity INT NOT NULL DEFAULT 0,
  version INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_inventory_product_id ON inventory(product_id);
INSERT INTO
  inventory (product_id, available_quantity, version)
VALUES
  (1, 100, 1);
-- Order
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL DEFAULT 0,
  price DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
  amount DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);