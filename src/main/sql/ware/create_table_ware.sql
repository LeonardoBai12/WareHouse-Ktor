CREATE TABLE IF NOT EXISTS ware (
    uuid UUID PRIMARY KEY,
    user_id UUID REFERENCES user_data(user_id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255),
    description TEXT,
    weight_per_unit DOUBLE PRECISION,
    weight_unit VARCHAR(50),
    available_quantity DOUBLE PRECISION DEFAULT 0.0,
    quantity_unit VARCHAR(50),
    ware_location VARCHAR(255) NOT NULL,
    timestamp TIMESTAMPTZ DEFAULT NOW() NOT NULL
);