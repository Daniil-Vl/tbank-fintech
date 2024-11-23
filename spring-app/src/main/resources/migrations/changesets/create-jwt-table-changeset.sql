--changeset Daniil-Vl:4

CREATE TABLE jwt
(
    id      BIGSERIAL PRIMARY KEY NOT NULL,
    token   TEXT                  NOT NULL,
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    revoked BOOLEAN               NOT NULL
)

-- rollback DROP TABLE jwt