DROP TABLE IF EXISTS "schedule";
CREATE TABLE "schedule"
(
    id          BIGSERIAL NOT NULL,
    date        DATE      NOT NULL,
    hour        TEXT      NOT NULL,
    number_turn BIGINT    NOT NULL,
    PRIMARY KEY (id)
);