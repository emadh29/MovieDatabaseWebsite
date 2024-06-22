DELIMITER //
CREATE PROCEDURE add_star(
    IN star_name VARCHAR(100),
    IN star_birth_year INTEGER
)
BEGIN
    DECLARE star_id VARCHAR(10);

    -- Insert the star
    SET star_id = (SELECT MAX(CONCAT('nm', LPAD(
            CASE WHEN SUBSTRING(id, 3) REGEXP '^[0-9]+$'
                     THEN COALESCE(CAST(SUBSTRING(id, 3) AS UNSIGNED), 0) + 1
                 ELSE 1
                END, 7, '0'))) as id
                   FROM stars);
INSERT INTO stars (id, name, birthYear)
VALUES (star_id, star_name, star_birth_year);
SELECT star_id;

END//

DELIMITER ;