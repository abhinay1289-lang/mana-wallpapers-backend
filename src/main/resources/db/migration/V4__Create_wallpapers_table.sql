-- V4__Create_wallpapers_table.sql
CREATE TABLE wallpapers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        title VARCHAR(255) NOT NULL,
        description TEXT,
        file_key VARCHAR(500) NOT NULL,
        thumbnail_key VARCHAR(500),
        price_cents INTEGER,
        currency VARCHAR(3) NOT NULL DEFAULT 'USD',
        is_free BOOLEAN NOT NULL DEFAULT FALSE,
        is_downloadable BOOLEAN NOT NULL DEFAULT TRUE,
        resolution VARCHAR(50) NOT NULL,
        format VARCHAR(10) NOT NULL,
        license_text TEXT,
        uploader_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
        category_id UUID NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
        sub_category_id UUID NOT NULL REFERENCES sub_categories(id) ON DELETE RESTRICT,
        mini_sub_category_id UUID NOT NULL REFERENCES mini_sub_categories(id) ON DELETE RESTRICT,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wallpaper_tags (
    wallpaper_id UUID NOT NULL REFERENCES wallpapers(id) ON DELETE CASCADE,
    tag VARCHAR(50) NOT NULL,
       PRIMARY KEY (wallpaper_id, tag)
);

CREATE INDEX idx_wallpapers_title ON wallpapers(title);
CREATE INDEX idx_wallpapers_category_id ON wallpapers(category_id);
CREATE INDEX idx_wallpapers_sub_category_id ON wallpapers(sub_category_id);
CREATE INDEX idx_wallpapers_mini_sub_category_id ON wallpapers(mini_sub_category_id);
CREATE INDEX idx_wallpapers_uploader_id ON wallpapers(uploader_id);
CREATE INDEX idx_wallpapers_is_free ON wallpapers(is_free);
CREATE INDEX idx_wallpapers_is_downloadable ON wallpapers(is_downloadable);
CREATE INDEX idx_wallpapers_created_at ON wallpapers(created_at);

CREATE TRIGGER update_wallpapers_updated_at
    BEFORE UPDATE ON wallpapers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();