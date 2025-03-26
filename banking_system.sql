CREATE DATABASE banking_system;
-- drop database banking_system;
USE banking_system;

CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE accounts (
    account_id BIGINT PRIMARY KEY,  
    customer_id INT NOT NULL,
    account_type ENUM('Savings', 'Current', 'Fixed Deposit') DEFAULT 'Savings',
    balance DECIMAL(15, 2) DEFAULT 500.00,  -- Starting with minimum balance
    minimum_balance DECIMAL(15, 2) DEFAULT 500.00,
    status ENUM('Active', 'Inactive', 'Dormant') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,  -- Now properly handled in code
    description VARCHAR(255),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT UNIQUE,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,  
    role ENUM('Customer', 'Teller', 'Manager', 'Admin') DEFAULT 'Customer',
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);
CREATE INDEX idx_accounts_customer ON accounts(customer_id);
CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_users_username ON users(username);

INSERT INTO customers (name, email, phone, address)
VALUES ('John Doe', 'john.doe@example.com', '9876543210', '123 Main St, City'),
('Chinnu','chinnu@gmail.com','6302673244','No 1 yadaval street'),
('rishi','rishi@example.com','9876543210','123 MG Road, Mumbai'),
('Usha', 'usha@example.com', '8765432109', '456 Brigade Road, Bangalore'),
('Vandana', 'vandana@example.com', '7654321098', '789 Park Street, Kolkata');

INSERT INTO accounts (account_id, customer_id, account_type, balance)
VALUES (123456789, 1, 'Savings', 5000.00),(116428901,2,'Savings',40000.00),
(1001001001, 2, 'Savings', 15000.00),
(1001001002, 3, 'Current', 25000.00),
(1001001003, 4, 'Current', 50000.00);

INSERT INTO users (customer_id, username, password_hash, role)
VALUES (1, 'johndoe', '$2a$10$xJwL5v5zVZ5hJh5U5XQZ3', 'Customer'),
(2, 'deepu', '$2a$10$xJwL5v5zVZ5hJh5U5XQZ3', 'Customer'),
(3, 'nandhu', '$2a$', 'Customer'),
(4, 'amitp', '$2a', 'Admin');

INSERT INTO transactions (account_id, transaction_type, amount, balance_after, description)
VALUES 
(123456789, 'Deposit', 5000.00, 5000.00, 'Initial deposit'),
(123456789, 'Withdrawal', 1000.00, 4000.00, 'ATM withdrawal'),
(123456789, 'Deposit', 2000.00, 6000.00, 'Cash deposit'),
(1001001001, 'Deposit', 10000.00, 10000.00, 'Initial deposit'),
(1001001001, 'Deposit', 5000.00, 15000.00, 'Salary credit'),
(1001001002, 'Deposit', 20000.00, 20000.00, 'Initial deposit'),
(1001001002, 'Deposit', 5000.00, 25000.00, 'Gift from family'),
(1001001003, 'Deposit', 50000.00, 50000.00, 'Business account funding'),
(1001001001, 'Withdrawal', 2000.00, 13000.00, 'ATM withdrawal'),
(1001001002, 'Withdrawal', 5000.00, 20000.00, 'Online transfer');

ALTER TABLE transactions MODIFY balance_after DECIMAL(15, 2) NULL;

-- Option 2: Add a default value
ALTER TABLE transactions MODIFY balance_after DECIMAL(15, 2) DEFAULT 0.00;