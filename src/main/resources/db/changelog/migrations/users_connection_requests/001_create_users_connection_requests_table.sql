CREATE TABLE users_connection_requests(
   id INT AUTO_INCREMENT,
   user_parent INT,
   user_children INT,
   is_email_verified BOOLEAN DEFAULT false,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (id, user_parent),
   FOREIGN KEY (user_parent) REFERENCES users (id) ON UPDATE RESTRICT ON DELETE CASCADE,
   FOREIGN KEY (user_children) REFERENCES users (id) ON UPDATE RESTRICT ON DELETE CASCADE
);