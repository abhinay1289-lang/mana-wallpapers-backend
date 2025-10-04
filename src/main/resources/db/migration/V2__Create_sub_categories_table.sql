-- V2__Create_categories_table.sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE sub_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id UUID NOT NULL REFERENCES categories(id),
    name VARCHAR(255) NOT NULL
);

CREATE INDEX idx_sub_categories_category_id ON sub_categories(category_id);
