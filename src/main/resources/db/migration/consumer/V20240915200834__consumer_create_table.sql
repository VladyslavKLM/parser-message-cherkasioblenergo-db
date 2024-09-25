DROP TABLE IF EXISTS "consumer";
CREATE TABLE "consumer"
(
    id              BIGSERIAL    NOT NULL,
    chat_id         BIGINT       NOT NULL UNIQUE,
    username        VARCHAR(100) NOT NULL UNIQUE,
    number_turn     BIGINT,
    role            VARCHAR(100) NOT NULL DEFAULT 'USER',
    PRIMARY KEY (id)
);