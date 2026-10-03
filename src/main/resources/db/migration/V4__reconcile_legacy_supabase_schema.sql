-- Upgrade the earlier public Supabase venue/booking schema to the backend's
-- owner-based model without discarding the existing venue and booking data.

ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
UPDATE users
SET role = CASE upper(role)
    WHEN 'ADMIN' THEN 'ADMIN'
    WHEN 'VENUE_OWNER' THEN 'VENUE_OWNER'
    WHEN 'OWNER' THEN 'VENUE_OWNER'
    WHEN 'CUSTOMER' THEN 'CUSTOMER'
    ELSE 'CLIENT'
END;
ALTER TABLE users ADD CONSTRAINT users_role_check
    CHECK (role IN ('ADMIN', 'CLIENT', 'CUSTOMER', 'VENUE_OWNER'));

ALTER TABLE venues ADD COLUMN IF NOT EXISTS owner_id uuid;
ALTER TABLE venues ADD COLUMN IF NOT EXISTS venue_type text;
ALTER TABLE venues ADD COLUMN IF NOT EXISTS state text;
ALTER TABLE venues ADD COLUMN IF NOT EXISTS pincode varchar(10);
ALTER TABLE venues ADD COLUMN IF NOT EXISTS contact_number text;
ALTER TABLE venues ADD COLUMN IF NOT EXISTS whatsapp_number text;
ALTER TABLE venues ADD COLUMN IF NOT EXISTS email text;
ALTER TABLE venues ADD COLUMN IF NOT EXISTS opening_time time;
ALTER TABLE venues ADD COLUMN IF NOT EXISTS closing_time time;
ALTER TABLE venues ADD COLUMN IF NOT EXISTS weekly_off text;
ALTER TABLE venues ADD COLUMN IF NOT EXISTS status varchar(32);
ALTER TABLE venues ADD COLUMN IF NOT EXISTS updated_at timestamp;

INSERT INTO users (id, full_name, email, mobile, password, role)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Migrated venue owner',
    'legacy-venue-owner@bookmyslot.local',
    '0000000000',
    'MIGRATED_ACCOUNT_DO_NOT_USE_FOR_LOGIN',
    'VENUE_OWNER'
)
ON CONFLICT DO NOTHING;

UPDATE venues
SET owner_id = COALESCE(owner_id, (SELECT id FROM users WHERE email = 'legacy-venue-owner@bookmyslot.local')),
    pincode = COALESCE(NULLIF(pincode, ''), '000000'),
    status = COALESCE(NULLIF(status, ''), 'ACTIVE'),
    updated_at = COALESCE(updated_at, created_at, now());

ALTER TABLE venues ALTER COLUMN owner_id SET NOT NULL;
ALTER TABLE venues ALTER COLUMN pincode SET NOT NULL;
ALTER TABLE venues ALTER COLUMN status SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_venues_owner') THEN
        ALTER TABLE venues ADD CONSTRAINT fk_venues_owner
            FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE RESTRICT;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_venue_owner ON venues(owner_id);

ALTER TABLE turfs ADD COLUMN IF NOT EXISTS legacy_venue_id uuid;
CREATE UNIQUE INDEX IF NOT EXISTS uk_turfs_legacy_venue ON turfs(legacy_venue_id) WHERE legacy_venue_id IS NOT NULL;

INSERT INTO turfs (name, location, price_per_hour, sport_type, description, available, legacy_venue_id)
SELECT v.name, v.address, 0, COALESCE(v.venue_type, 'MIGRATED'), v.description, true, v.id
FROM venues v
WHERE NOT EXISTS (SELECT 1 FROM turfs t WHERE t.legacy_venue_id = v.id);

ALTER TABLE bookings ADD COLUMN IF NOT EXISTS user_id uuid;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS turf_id uuid;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS playing_area_id uuid;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS start_time time;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS end_time time;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS booking_status varchar(32);

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'bookings' AND column_name = 'customer_email') THEN
        INSERT INTO users (id, full_name, email, password, role)
        SELECT gen_random_uuid(), COALESCE(NULLIF(b.customer_name, ''), 'Migrated customer'), lower(b.customer_email), 'MIGRATED_ACCOUNT_DO_NOT_USE_FOR_LOGIN', 'CLIENT'
        FROM bookings b
        WHERE b.customer_email IS NOT NULL AND b.customer_email <> ''
        ON CONFLICT (email) DO NOTHING;

        EXECUTE '
            UPDATE bookings b
            SET user_id = COALESCE(b.user_id, u.id)
            FROM users u
            WHERE lower(u.email) = lower(b.customer_email)';
    END IF;

    INSERT INTO users (id, full_name, email, password, role)
    SELECT gen_random_uuid(), 'Migrated customer', 'legacy-customer-' || b.id || '@bookmyslot.local', 'MIGRATED_ACCOUNT_DO_NOT_USE_FOR_LOGIN', 'CLIENT'
    FROM bookings b
    WHERE b.user_id IS NULL
    ON CONFLICT (email) DO NOTHING;

    UPDATE bookings b
    SET user_id = u.id
    FROM users u
    WHERE b.user_id IS NULL
      AND u.email = 'legacy-customer-' || b.id || '@bookmyslot.local';

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'bookings' AND column_name = 'venue_id') THEN
        EXECUTE '
            UPDATE bookings b
            SET turf_id = COALESCE(b.turf_id, t.id)
            FROM turfs t
            WHERE t.legacy_venue_id = b.venue_id';
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'bookings' AND column_name = 'time_slot') THEN
        EXECUTE $sql$
            UPDATE bookings
            SET start_time = COALESCE(start_time, to_timestamp(trim(split_part(time_slot, ''-'', 1)), ''FMHH12:MI AM'')::time),
                end_time = COALESCE(end_time, to_timestamp(trim(split_part(time_slot, ''-'', 2)), ''FMHH12:MI AM'')::time)
            WHERE time_slot IS NOT NULL
        $sql$;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'bookings' AND column_name = 'status') THEN
        EXECUTE 'UPDATE bookings SET booking_status = COALESCE(booking_status, upper(status), ''CONFIRMED'')';
    ELSE
        UPDATE bookings SET booking_status = COALESCE(booking_status, 'CONFIRMED');
    END IF;
END $$;

ALTER TABLE bookings ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE bookings ALTER COLUMN turf_id DROP NOT NULL;
ALTER TABLE bookings ALTER COLUMN start_time SET NOT NULL;
ALTER TABLE bookings ALTER COLUMN end_time SET NOT NULL;
ALTER TABLE bookings ALTER COLUMN booking_status SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_bookings_user') THEN
        ALTER TABLE bookings ADD CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_bookings_turf') THEN
        ALTER TABLE bookings ADD CONSTRAINT fk_bookings_turf FOREIGN KEY (turf_id) REFERENCES turfs(id) ON DELETE RESTRICT;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_booking_playing_area') THEN
        ALTER TABLE bookings ADD CONSTRAINT fk_booking_playing_area FOREIGN KEY (playing_area_id) REFERENCES playing_areas(id) ON DELETE RESTRICT;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'bookings_resource_required') THEN
        ALTER TABLE bookings ADD CONSTRAINT bookings_resource_required CHECK (turf_id IS NOT NULL OR playing_area_id IS NOT NULL);
    END IF;
END $$;
