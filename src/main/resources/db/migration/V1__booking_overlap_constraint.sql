CREATE EXTENSION IF NOT EXISTS btree_gist;

DO $$
BEGIN
    IF to_regclass('public.bookings') IS NOT NULL AND to_regclass('public.playing_areas') IS NOT NULL THEN
        ALTER TABLE bookings ADD COLUMN IF NOT EXISTS playing_area_id uuid;
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_booking_playing_area') THEN
            ALTER TABLE bookings ADD CONSTRAINT fk_booking_playing_area
                FOREIGN KEY (playing_area_id) REFERENCES playing_areas(id);
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'bookings_playing_area_required') THEN
            ALTER TABLE bookings ADD CONSTRAINT bookings_playing_area_required
                CHECK (playing_area_id IS NOT NULL OR turf_id IS NOT NULL);
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'bookings_no_confirmed_overlap') THEN
            ALTER TABLE bookings ADD CONSTRAINT bookings_no_confirmed_overlap
                EXCLUDE USING gist (
                    playing_area_id WITH =,
                    tsrange((booking_date + start_time), (booking_date + end_time), '[)') WITH &&
                ) WHERE (booking_status = 'CONFIRMED' AND playing_area_id IS NOT NULL);
        END IF;
    END IF;
END $$;
