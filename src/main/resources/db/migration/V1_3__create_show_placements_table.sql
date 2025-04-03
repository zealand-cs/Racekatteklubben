CREATE TABLE showPlacements
(
    showId        INT NOT NULL REFERENCES cat_shows (id),
    catId         INT NOT NULL REFERENCES cats (id),
    showPlacement INT NOT NULL
);