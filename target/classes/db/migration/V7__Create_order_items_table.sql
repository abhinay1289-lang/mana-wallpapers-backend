-- V7__Create_order_items_table.sql
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    wallpaper_id UUID NOT NULL REFERENCES wallpapers(id) ON DELETE CASCADE,
    price_cents INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_wallpaper_id ON order_items(wallpaper_id);