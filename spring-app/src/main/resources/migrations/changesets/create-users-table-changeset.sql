--changeset Daniil-Vl:3

CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY NOT NULL,
    username     VARCHAR(255)          NOT NULL,
    password VARCHAR(255)          NOT NULL,
    role     VARCHAR(255)          NOT NULL
)

-- rollback DROP TABLE users