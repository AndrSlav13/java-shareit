CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(100),
    description VARCHAR(400),
    available   BOOLEAN,
    owner_id    BIGINT,
    request_id  BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(100),
    requestor_id BIGINT,
    creationdate timestamp
);

CREATE TABLE IF NOT EXISTS bookings
(
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    booking_start timestamp,
    booking_end   timestamp,
    booker_id     BIGINT,
    booked_id     BIGINT,
    status        VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS comments
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    commentary  VARCHAR(100),
    author_name VARCHAR(100),
    created     timestamp,
    item_id     BIGINT
);