CREATE TABLE IF NOT EXISTS user_data (
    user_id UUID PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    profile_picture VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL
);