CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name text,
    email text UNIQUE,
    mobile text UNIQUE,
    password text,
    role varchar(32) NOT NULL DEFAULT 'CLIENT'
);

CREATE TABLE IF NOT EXISTS sports (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL UNIQUE,
    description text,
    icon_url text,
    active boolean NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS venues (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id uuid NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    name text NOT NULL,
    description text,
    venue_type text,
    address text NOT NULL,
    city text NOT NULL,
    state text,
    pincode varchar(10) NOT NULL,
    contact_number text,
    whatsapp_number text,
    email text,
    opening_time time,
    closing_time time,
    weekly_off text,
    status varchar(32) NOT NULL DEFAULT 'PENDING',
    created_at timestamp NOT NULL DEFAULT now(),
    updated_at timestamp
);

CREATE TABLE IF NOT EXISTS turfs (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    location text,
    price_per_hour double precision,
    sport_type text,
    description varchar(500),
    available boolean NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS playing_areas (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id uuid NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    sport_id uuid NOT NULL REFERENCES sports(id) ON DELETE RESTRICT,
    name text NOT NULL,
    description text,
    capacity integer,
    surface_type text,
    indoor_outdoor text,
    base_price numeric(12,2) NOT NULL,
    active boolean NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS bookings (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    turf_id uuid REFERENCES turfs(id) ON DELETE RESTRICT,
    playing_area_id uuid REFERENCES playing_areas(id) ON DELETE RESTRICT,
    booking_date date NOT NULL,
    start_time time NOT NULL,
    end_time time NOT NULL,
    total_price numeric(12,2) NOT NULL,
    booking_status varchar(32) NOT NULL,
    created_at timestamp NOT NULL DEFAULT now(),
    CONSTRAINT bookings_valid_time CHECK (start_time < end_time),
    CONSTRAINT bookings_resource_required CHECK (turf_id IS NOT NULL OR playing_area_id IS NOT NULL)
);

CREATE TABLE IF NOT EXISTS pricing_rules (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id uuid NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    playing_area_id uuid NOT NULL REFERENCES playing_areas(id) ON DELETE CASCADE,
    day_of_week varchar(16),
    specific_date date,
    start_time time NOT NULL,
    end_time time NOT NULL,
    price numeric(12,2) NOT NULL,
    rule_type varchar(32) NOT NULL,
    priority integer NOT NULL,
    active boolean NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS offers (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id uuid NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    coupon_code text NOT NULL UNIQUE,
    name text NOT NULL,
    discount_type varchar(32) NOT NULL,
    discount_value numeric(12,2) NOT NULL,
    start_date date,
    end_date date,
    start_time time,
    end_time time,
    minimum_booking_amount numeric(12,2),
    maximum_discount numeric(12,2),
    usage_limit integer,
    per_customer_limit integer,
    active boolean NOT NULL DEFAULT true
);

ALTER TABLE bookings ADD COLUMN IF NOT EXISTS playing_area_id uuid;

CREATE TABLE IF NOT EXISTS profiles (
    id uuid PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    avatar_url text,
    phone_verified boolean NOT NULL DEFAULT false,
    email_verified boolean NOT NULL DEFAULT false,
    created_at timestamp NOT NULL DEFAULT now(),
    updated_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS venue_registrations (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id uuid REFERENCES users(id) ON DELETE SET NULL,
    full_name text NOT NULL,
    email text NOT NULL,
    mobile text NOT NULL,
    venue_name text NOT NULL,
    address text NOT NULL,
    city text NOT NULL,
    state text,
    pincode varchar(6) NOT NULL,
    status varchar(32) NOT NULL DEFAULT 'PENDING',
    created_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS venue_courts (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id uuid NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    sport_id uuid REFERENCES sports(id) ON DELETE SET NULL,
    name text NOT NULL,
    description text,
    capacity integer,
    surface text,
    price_per_hour numeric(12,2) NOT NULL,
    active boolean NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS venue_availability (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    court_id uuid NOT NULL REFERENCES venue_courts(id) ON DELETE CASCADE,
    day_of_week varchar(16) NOT NULL,
    start_time time NOT NULL,
    end_time time NOT NULL,
    active boolean NOT NULL DEFAULT true,
    CONSTRAINT venue_availability_valid_time CHECK (start_time < end_time)
);

CREATE TABLE IF NOT EXISTS blocked_slots (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    court_id uuid NOT NULL REFERENCES venue_courts(id) ON DELETE CASCADE,
    blocked_date date NOT NULL,
    start_time time NOT NULL,
    end_time time NOT NULL,
    reason text,
    created_at timestamp NOT NULL DEFAULT now(),
    CONSTRAINT blocked_slots_valid_time CHECK (start_time < end_time)
);

CREATE TABLE IF NOT EXISTS contact_messages (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid REFERENCES users(id) ON DELETE SET NULL,
    name text NOT NULL,
    email text NOT NULL,
    message text NOT NULL,
    status varchar(32) NOT NULL DEFAULT 'OPEN',
    created_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS payments (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id uuid NOT NULL UNIQUE REFERENCES bookings(id) ON DELETE RESTRICT,
    amount numeric(12,2) NOT NULL CHECK (amount >= 0),
    currency varchar(3) NOT NULL DEFAULT 'INR',
    status varchar(32) NOT NULL DEFAULT 'PENDING',
    provider varchar(64) NOT NULL,
    provider_payment_id text UNIQUE,
    provider_order_id text UNIQUE,
    created_at timestamp NOT NULL DEFAULT now(),
    updated_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS offer_usage (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    offer_id uuid NOT NULL REFERENCES offers(id) ON DELETE RESTRICT,
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    booking_id uuid NOT NULL REFERENCES bookings(id) ON DELETE RESTRICT,
    discount_amount numeric(12,2) NOT NULL CHECK (discount_amount >= 0),
    used_at timestamp NOT NULL DEFAULT now(),
    CONSTRAINT uk_offer_user_booking UNIQUE (offer_id, user_id, booking_id)
);

CREATE TABLE IF NOT EXISTS memberships (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL UNIQUE,
    description text,
    price numeric(12,2) NOT NULL CHECK (price >= 0),
    duration_days integer NOT NULL CHECK (duration_days > 0),
    active boolean NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS venue_memberships (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id uuid NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    membership_id uuid NOT NULL REFERENCES memberships(id) ON DELETE RESTRICT,
    starts_on date NOT NULL,
    ends_on date NOT NULL,
    status varchar(32) NOT NULL DEFAULT 'ACTIVE',
    created_at timestamp NOT NULL DEFAULT now(),
    CONSTRAINT uk_venue_membership UNIQUE (venue_id, membership_id),
    CONSTRAINT venue_membership_valid_period CHECK (starts_on <= ends_on)
);

CREATE TABLE IF NOT EXISTS notifications (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title text NOT NULL,
    message text NOT NULL,
    notification_type varchar(64) NOT NULL,
    is_read boolean NOT NULL DEFAULT false,
    created_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS favorites (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    venue_id uuid NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    created_at timestamp NOT NULL DEFAULT now(),
    CONSTRAINT uk_favorite_user_venue UNIQUE (user_id, venue_id)
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_booking_playing_area') THEN
        ALTER TABLE bookings ADD CONSTRAINT fk_booking_playing_area
            FOREIGN KEY (playing_area_id) REFERENCES playing_areas(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'bookings_no_confirmed_overlap') THEN
        ALTER TABLE bookings ADD CONSTRAINT bookings_no_confirmed_overlap
            EXCLUDE USING gist (
                playing_area_id WITH =,
                tsrange((booking_date + start_time), (booking_date + end_time), '[)') WITH &&
            ) WHERE (booking_status = 'CONFIRMED' AND playing_area_id IS NOT NULL);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_registration_email ON venue_registrations(email);
CREATE INDEX IF NOT EXISTS idx_court_venue ON venue_courts(venue_id);
CREATE INDEX IF NOT EXISTS idx_availability_court_day ON venue_availability(court_id, day_of_week);
CREATE INDEX IF NOT EXISTS idx_blocked_court_date ON blocked_slots(court_id, blocked_date);
CREATE INDEX IF NOT EXISTS idx_contact_created ON contact_messages(created_at);
CREATE INDEX IF NOT EXISTS idx_payment_booking ON payments(booking_id);
CREATE INDEX IF NOT EXISTS idx_notification_user_read ON notifications(user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_favorite_user ON favorites(user_id);
