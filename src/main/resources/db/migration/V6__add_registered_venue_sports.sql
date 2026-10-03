CREATE TABLE IF NOT EXISTS venue_registered_sports (
    venue_id uuid NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    sport text NOT NULL,
    PRIMARY KEY (venue_id, sport)
);

CREATE INDEX IF NOT EXISTS idx_venue_registered_sports_sport ON venue_registered_sports (sport);
