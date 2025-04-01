CREATE TABLE users
(
    id          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(32)  NOT NULL,
    email       VARCHAR(128) NOT NULL UNIQUE,
    password    VARCHAR(256) NOT NULL,
    dateOfBirth DATETIME     NULL,
    role        VARCHAR(32)  NOT NULL DEFAULT 'user'
)