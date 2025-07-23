INSERT INTO users (email, login, name, birthday)
    VALUES ('mail@mail.eu', 'Login', 'Name', '2015-04-17');
INSERT INTO films (name, description, release_date, duration, age_rating)
    VALUES ('Film', 'Some film', '2015-04-17', 145, 1);
MERGE INTO genres (name) KEY(name) VALUES ('Комедия');
MERGE INTO genres (name) KEY(name) VALUES ('Драма');
MERGE INTO genres (name) KEY(name) VALUES ('Мультфильм');
MERGE INTO genres (name) KEY(name) VALUES ('Триллер');
MERGE INTO genres (name) KEY(name) VALUES ('Документальный');
MERGE INTO genres (name) KEY(name) VALUES ('Боевик');

MERGE INTO age_ratings (name) KEY(name) VALUES ('G');
MERGE INTO age_ratings (name) KEY(name) VALUES ('PG');
MERGE INTO age_ratings (name) KEY(name) VALUES ('PG-13');
MERGE INTO age_ratings (name) KEY(name) VALUES ('R');
MERGE INTO age_ratings (name) KEY(name) VALUES ('NC-17');