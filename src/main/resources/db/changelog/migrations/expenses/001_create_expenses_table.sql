CREATE TABLE expenses(
    id INT AUTO_INCREMENT,
    user_id INT,
    value DECIMAL(15,2),
    expense_type VARCHAR(255),
    date DATE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, user_id),
    FOREIGN KEY (user_id) REFERENCES users (id)
)