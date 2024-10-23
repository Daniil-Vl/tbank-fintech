--changeset Daniil-Vl:1

CREATE TABLE places
(
    id   BIGSERIAL PRIMARY KEY NOT NULL,
    slug VARCHAR(255)          NOT NULL,
    name VARCHAR(255)          NOT NULL
)