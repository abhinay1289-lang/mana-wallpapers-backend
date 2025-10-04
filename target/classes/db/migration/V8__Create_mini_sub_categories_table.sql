CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE mini_sub_categories (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  sub_category_id uuid,
  name varchar(255) NOT NULL
);

ALTER TABLE mini_sub_categories
ADD CONSTRAINT mini_sub_categories_sub_category_id_fkey
FOREIGN KEY sub_category_id REFERENCES sub_categories(id);

CREATE INDEX idx_mini_sub_categories_sub_category_id
  ON mini_sub_categories(sub_category_id);