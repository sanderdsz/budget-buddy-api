CREATE TABLE refresh_tokens(
     id VARCHAR(255),
     user_data_id INT,
     refresh_token VARCHAR(255),
     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     PRIMARY KEY (id, user_data_id),
     FOREIGN KEY (user_data_id) REFERENCES users_data (id)
)