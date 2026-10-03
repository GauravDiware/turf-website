CREATE TABLE IF NOT EXISTS favorites (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    venue_id uuid NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    created_at timestamp NOT NULL DEFAULT now(),
    CONSTRAINT uk_favorite_user_venue UNIQUE (user_id, venue_id)
);

CREATE INDEX IF NOT EXISTS idx_favorite_user ON favorites(user_id);
