CREATE TABLE cats
(
    id          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ownerId     INT          NOT NULL REFERENCES user (id),
    name        VARCHAR(32)  NOT NULL,
    race        VARCHAR(32)  NOT NULL,
    gender      VARCHAR(32)  NOT NULL,
    dateOfBirth DATETIME     NOT NULL,
    imageUrl    VARCHAR(512) NOT NULL
);