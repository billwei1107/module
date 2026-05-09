CREATE TABLE meet_rooms (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    capacity INTEGER NOT NULL DEFAULT 0,
    equipment VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE meet_bookings (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL,
    title VARCHAR(160) NOT NULL,
    organizer_id VARCHAR(64),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'BOOKED',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_meet_bookings_room_time ON meet_bookings(room_id, start_time, end_time);

CREATE TABLE meet_meetings (
    id UUID PRIMARY KEY,
    booking_id UUID,
    subject VARCHAR(160) NOT NULL,
    organizer_id VARCHAR(64),
    agenda VARCHAR(2000),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE meet_attendees (
    id UUID PRIMARY KEY,
    meeting_id UUID NOT NULL,
    attendee_id VARCHAR(64) NOT NULL,
    attendee_name VARCHAR(100),
    email VARCHAR(160),
    response VARCHAR(20) NOT NULL DEFAULT 'INVITED',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_meet_attendees_meeting ON meet_attendees(meeting_id);

CREATE TABLE meet_minutes (
    id UUID PRIMARY KEY,
    meeting_id UUID NOT NULL,
    author_id VARCHAR(64),
    content VARCHAR(4000) NOT NULL,
    decisions VARCHAR(3000),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE meet_action_items (
    id UUID PRIMARY KEY,
    meeting_id UUID NOT NULL,
    minute_id UUID,
    description VARCHAR(500) NOT NULL,
    owner_id VARCHAR(64),
    due_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_meet_action_items_meeting ON meet_action_items(meeting_id);
