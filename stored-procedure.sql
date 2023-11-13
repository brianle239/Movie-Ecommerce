use moviedb;

DELIMITER $$

CREATE PROCEDURE add_movie(IN _t VARCHAR(255), IN _y YEAR, IN _d VARCHAR(255), IN _sName VARCHAR(255), 
	IN _birthYear INT, IN _gName VARCHAR(255), 
    OUT message VARCHAR(255))
    
BEGIN

    DECLARE _genreId VARCHAR(10);
    DECLARE _starId VARCHAR(10);
    DECLARE _movieId VARCHAR(10);
    DECLARE _newId INT;
    DECLARE duplicate_movie INT DEFAULT 0;
    DECLARE max_movie INT;
    DECLARE max_star_id INT;
    

    SELECT id INTO _genreId FROM genres WHERE name = _gName;
    IF _genreId IS NULL THEN
        SELECT MAX(id)+1 INTO _newId FROM (SELECT CAST(id AS UNSIGNED) AS id FROM genres) t;
        SET _genreId = CAST(_newId AS CHAR);
        INSERT INTO genres(id, name) VALUES (_genreId, _gName);
    END IF;
    

	IF _birthYear IS NOT NULL THEN
        SELECT id INTO _starId FROM stars WHERE name = _sName AND birthYear = _birthYear;
    ELSE
        SELECT id INTO _starId FROM stars WHERE name = _sName AND birthYear IS NULL;
    END IF;
    
    IF _starId IS NULL THEN
    
        SELECT MAX(CAST(SUBSTRING(id FROM 3) AS UNSIGNED)) INTO max_star_id FROM stars WHERE id LIKE 'nm%';
        SET _starId = CONCAT('nm', LPAD(max_star_id + 1, 7, '0'));
        INSERT INTO stars(id, name, birthYear) VALUES (_starId, _sName, _birthYear);
    END IF;
    
    SELECT COUNT(*) INTO duplicate_movie FROM movies WHERE title = _t AND year = _y AND director = _d;
    IF duplicate_movie > 0 THEN
        SET message = 'Duplicate movie found. No new movie added.';
    ELSE
            
		SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) INTO max_movie FROM movies WHERE id LIKE 'tt%';
		SET _movieId = CONCAT('tt', LPAD(max_movie + 1, 7, '0'));
        
        INSERT INTO movies(id, title, year, director) VALUES (_movieId, _t, _y, _d);
        
        -- Link movie with star and genre
        INSERT INTO genres_in_movies(genreId, movieId) VALUES (_genreId, _movieId);
        INSERT INTO stars_in_movies(starId, movieId) VALUES (_starId, _movieId);
        
     
		SET message = CONCAT('Genre:', _genreId, ' Star:', _starId, ' Movie:', _movieId);

    END IF;
END$$


CREATE PROCEDURE add_star(IN _sName VARCHAR(100), IN _birthYear INT, OUT message VARCHAR(255))
BEGIN
    DECLARE _starId VARCHAR(10);
    DECLARE max_star_id INT;
    
    SELECT MAX(CAST(SUBSTRING(id FROM 3) AS UNSIGNED)) INTO max_star_id FROM stars WHERE id LIKE 'nm%';
    SET _starId = CONCAT('nm', LPAD(max_star_id + 1, 7, '0'));
    
    INSERT INTO stars(id, name, birthYear) VALUES (_starId, _sName, _birthYear);
    SET message = CONCAT('Star id:', _starId);
END$$

CREATE PROCEDURE add_genre(IN _gName VARCHAR(100), OUT message VARCHAR(255))
BEGIN
    DECLARE _genreId VARCHAR(10);
    DECLARE _newId INT;
    
	SELECT MAX(id)+1 INTO _newId FROM (SELECT CAST(id AS UNSIGNED) AS id FROM genres) t;
	SET _genreId = CAST(_newId AS CHAR);
	INSERT INTO genres(id, name) VALUES (_genreId, _gName);
    
    SET message = CONCAT('genre id:', _genreId);
END$$

DELIMITER ;