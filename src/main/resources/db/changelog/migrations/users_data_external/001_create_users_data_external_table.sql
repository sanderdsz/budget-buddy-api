CREATE TABLE users_data_external(
   id INT AUTO_INCREMENT,
   user_id INT,
   provider_name VARCHAR(255),
   provider_id VARCHAR(255),
   provider_refresh_token VARCHAR(255),
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (id, user_id),
   FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE RESTRICT ON DELETE CASCADE
);