DELIMITER //

CREATE PROCEDURE add_movie(
    IN p_title VARCHAR(100),
    IN p_year INT,
    IN p_director VARCHAR(100),
    IN p_rating FLOAT,
    IN p_star_name VARCHAR(100),
    IN p_star_birth_year VARCHAR(5),
    IN p_genre_name VARCHAR(32)
)
BEGIN
    DECLARE star_id VARCHAR(10);
    DECLARE genre_id INT;
    DECLARE movie_id VARCHAR(10);

    -- Check if the movie already exists
    SELECT id INTO movie_id
    FROM movies
    WHERE title = p_title AND year = p_year AND director = p_director
    LIMIT 1;

    IF movie_id IS NULL THEN
        -- Movie doesn't exist, proceed to add it
        SET movie_id = (SELECT MAX(CONCAT('tt', LPAD(
                CASE WHEN SUBSTRING(id, 3) REGEXP '^[0-9]+$'
                         THEN COALESCE(CAST(SUBSTRING(id, 3) AS UNSIGNED), 0) + 1
                     ELSE 1
                    END, 7, '0')))
                        FROM movies);

        -- Get or create star ID
        SELECT id INTO star_id
        FROM stars
        WHERE name = p_star_name
        LIMIT 1;

        IF star_id IS NULL THEN
            -- Star doesn't exist, create id

            SET star_id = (SELECT MAX(CONCAT('nm', LPAD(
                    CASE WHEN SUBSTRING(id, 3) REGEXP '^[0-9]+$'
                             THEN COALESCE(CAST(SUBSTRING(id, 3) AS UNSIGNED), 0) + 1
                         ELSE 1
                        END, 7, '0')))
                           FROM stars);
            INSERT INTO stars (id, name, birthYear) VALUES (star_id, p_star_name, p_star_birth_year);
        END IF;

        -- Get or create genre ID
        SELECT id INTO genre_id
        FROM genres
        WHERE name = p_genre_name
        LIMIT 1;

        IF genre_id IS NULL THEN
            -- Genre doesn't exist, create it
            SET genre_id = (SELECT MAX(id) + 1 as id FROM genres);
            INSERT INTO genres (id, name) VALUES (genre_id, p_genre_name);
        END IF;

        INSERT INTO movies (id, title, year, director) VALUES (movie_id, p_title, p_year, p_director);
        INSERT INTO ratings (movieId, rating, numVotes) VALUES (movie_id, p_rating, 0);

        -- Link star to the movie
        INSERT INTO stars_in_movies (starId, movieId) VALUES (star_id, movie_id);

        -- Link genre to the movie
        INSERT INTO genres_in_movies (genreId, movieId) VALUES (genre_id, movie_id);

        SELECT movie_id, star_id, genre_id;
    END IF;
END//

DELIMITER ;
