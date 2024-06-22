# Search Feature Old Example
SELECT m.id, m.title, m.year, m.director, r.rating
FROM movies AS m
         JOIN ratings AS r ON m.id = r.movieId
         JOIN stars_in_movies AS sm ON m.id = sm.movieId
         JOIN stars AS s ON sm.starId = s.id
WHERE m.title LIKE '%Loma%' AND m.year='' AND m.director LIKE '%%' AND s.name LIKE '%%'
GROUP BY m.id, r.rating, m.title
ORDER BY r.rating DESC, m.title;

SELECT m.id, m.title, m.year, m.director, r.rating
FROM movies AS m, ratings AS r
WHERE m.id=r.movieId AND m.title LIKE 'L%'
ORDER BY r.rating DESC, m.title LIMIT 20;

SELECT m.id, m.title, m.year, m.director,
       GROUP_CONCAT(g.id ORDER BY g.id SEPARATOR ', ') AS genreId,
       GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') AS genreName,
       r.rating
FROM movies AS m
         INNER JOIN ratings AS r ON m.id = r.movieId
         INNER JOIN genres_in_movies AS gm ON m.id = gm.movieId
         INNER JOIN genres AS g ON gm.genreId = g.id
WHERE g.name LIKE 'Action%'
GROUP BY m.id, m.title, m.year, m.director, r.rating
ORDER BY r.rating DESC, m.title
    LIMIT 20;

SELECT
    id,
    title,
    year,
    director,
    genreId,
    genreName,
    rating
FROM (
    SELECT
    m.id,
    m.title,
    m.year,
    m.director,
    GROUP_CONCAT(g.id ORDER BY g.id SEPARATOR ', ') AS genreId,
    GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') AS genreName,
    r.rating
    FROM
    movies AS m
    INNER JOIN
    ratings AS r ON m.id = r.movieId
    INNER JOIN
    genres_in_movies AS gm ON m.id = gm.movieId
    INNER JOIN
    genres AS g ON gm.genreId = g.id
    GROUP BY
    m.id, m.title, m.year, m.director, r.rating
    ) AS subquery
WHERE
    FIND_IN_SET('Fantasy', genreName) > 0 OR genreName = 'Fantasy'
ORDER BY
    rating DESC, title;

SELECT
    *
FROM
    movies AS m
        INNER JOIN
    ratings AS r ON m.id = r.movieId
        INNER JOIN
    genres_in_movies AS gm ON m.id = gm.movieId
        INNER JOIN
    genres AS g ON gm.genreId = g.id
WHERE
        g.name = 'Fantasy'
ORDER BY
    r.rating DESC;

SELECT
    id,
    title,
    year,
    director,
    genreId,
    genreName,
    rating
FROM (
    SELECT
    m.id,
    m.title,
    m.year,
    m.director,
    GROUP_CONCAT(g.id ORDER BY g.id SEPARATOR ', ') AS genreId,
    GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') AS genreName,
    r.rating
    FROM
    movies AS m
    INNER JOIN ratings AS r ON m.id = r.movieId
    INNER JOIN genres_in_movies AS gm ON m.id = gm.movieId
    INNER JOIN genres AS g ON gm.genreId = g.id
    GROUP BY
    m.id, m.title, m.year, m.director, r.rating
    ) AS subquery
WHERE
    EXISTS (
    SELECT 1
    FROM genres_in_movies AS gm2
    INNER JOIN genres AS g2 ON gm2.genreId = g2.id
    WHERE
    subquery.id = gm2.movieId AND
    g2.name = 'Fantasy'
    )
ORDER BY
    rating DESC, title;

SELECT
    m.id,
    m.title,
    m.year,
    m.director,
    r.rating
FROM
    movies AS m
        INNER JOIN ratings AS r ON m.id = r.movieId
        INNER JOIN genres_in_movies AS gm ON m.id = gm.movieId
        INNER JOIN genres AS g ON gm.genreId = g.id
WHERE
        g.name = 'Reality-TV'
GROUP BY
    m.id, m.title, m.year, m.director, r.rating
ORDER BY
    r.rating DESC, m.title;

SELECT m.id, m.title, m.year, m.director, r.rating
FROM movies AS m, ratings AS r
WHERE m.id=r.movieId
ORDER BY r.rating DESC, m.title LIMIT 20 OFFSET 0;


SELECT m.id, m.title, m.year, m.director, r.rating, s.name
FROM movies AS m
         INNER JOIN ratings AS r ON m.id = r.movieId
         INNER JOIN stars_in_movies AS sm ON m.id = sm.movieId
         INNER JOIN stars AS s ON sm.starId = s.id
GROUP BY m.id, r.rating, m.title, s.name
ORDER BY r.rating DESC, m.title;

SELECT m.id, m.title, m.year, m.director, r.rating
FROM movies AS m, ratings AS r
WHERE m.id=r.movieId AND m.title LIKE 'Loma%' AND m.director LIKE 'Jason%' AND m.year='2004'
ORDER BY r.rating DESC, m.title;

SELECT m.id, m.title, m.year, m.director, r.rating
FROM movies AS m
         INNER JOIN ratings AS r ON m.id = r.movieId
         INNER JOIN stars_in_movies AS sm ON m.id = sm.movieId
         INNER JOIN stars AS s ON sm.starId = s.id
WHERE m.id=r.movieId AND m.title LIKE 'Loma%' AND m.director LIKE 'Jason%' AND m.year='2004' AND s.name LIKE 'Chris%'
GROUP BY m.id, r.rating, m.title
ORDER BY r.rating DESC, m.title;

SELECT
    m.id,
    m.title,
    m.year,
    m.director,
    r.rating
FROM movies AS m
         INNER JOIN ratings AS r ON m.id = r.movieId
         INNER JOIN genres_in_movies AS gm ON m.id = gm.movieId
         INNER JOIN genres AS g ON gm.genreId = g.id
WHERE
        g.name = 'Drama'
GROUP BY
    m.id, m.title, m.year, m.director, r.rating
ORDER BY
    r.rating DESC, m.title;


Select * from movies where title like '%mission%';