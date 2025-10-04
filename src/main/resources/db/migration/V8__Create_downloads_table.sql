-- V8__Create_downloads_table.sql
CREATE TABLE downloads (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    wallpaper_id UUID NOT NULL REFERENCES wallpapers(id) ON DELETE CASCADE,
    buyer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    ip_address VARCHAR(45),
    download_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_downloads_token ON downloads(token);
CREATE INDEX idx_downloads_buyer_id ON downloads(buyer_id);
CREATE INDEX idx_downloads_wallpaper_id ON downloads(wallpaper_id);
CREATE INDEX idx_downloads_order_id ON downloads(order_id);
CREATE INDEX idx_downloads_expires_at ON downloads(expires_at);