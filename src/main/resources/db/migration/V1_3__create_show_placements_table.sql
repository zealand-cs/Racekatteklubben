CREATE TABLE showPlacements
(
    showId        INT NOT NULL REFERENCES catShows (id),
    catId         INT NOT NULL REFERENCES cats (id),
    showPlacement INT NOT NULL
);