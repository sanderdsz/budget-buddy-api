CREATE TABLE incomes(
    id INT AUTO_INCREMENT,
    user_id INT,
    value DECIMAL(15,2),
    income_type VARCHAR(255),
    date DATE,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, user_id),
    FOREIGN KEY (user_id) REFERENCES users (id)
)