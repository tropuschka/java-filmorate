INSERT INTO users (email, login, name, birthday)
    VALUES ('mail@mail.eu', 'Login', 'Name', '2015-04-17');
INSERT INTO films (id, name, description, release_date, duration, age_rating)
    VALUES (NEXT VALUE FOR film_sequence, 'Film', 'Some film', '2015-04-17', 145, 1);
INSERT INTO genres (name, description)
    VALUES ('Comedy', 'Funny');
INSERT INTO age_ratings (name, description)
    VALUES ('G', 'General');
