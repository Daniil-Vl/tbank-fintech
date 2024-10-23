--changeset Daniil-Vl:2

CREATE TABLE events
(
    id       BIGSERIAL PRIMARY KEY NOT NULL,
    name     VARCHAR(300)          NOT NULL,
    date     DATE                  NOT NULL,
    slug     VARCHAR(255)          NOT NULL,
    place_id BIGINT                NOT NULL REFERENCES places (id) ON DELETE CASCADE
)