-- V2__Create_categories_table.sql
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    parent_id UUID REFERENCES categories(id) ON DELETE CASCADE,
    slug VARCHAR(100) NOT NULL UNIQUE
);

CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_parent_id ON categories(parent_id);

-- Insert default categories
INSERT INTO categories (name, slug) VALUES 
('Abstract', 'abstract'),
('Nature', 'nature'),
('Technology', 'technology'),
('Space', 'space'),
('Minimalist', 'minimalist'),
('Artistic', 'artistic');