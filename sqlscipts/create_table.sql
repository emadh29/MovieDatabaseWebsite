CREATE DATABASE IF NOT EXISTS moviedb;
USE moviedb;

CREATE TABLE movies (
                        id varchar(10) PRIMARY KEY,
                        title varchar(100) DEFAULT 'N/A',
                        year integer NOT NULL,
                        director varchar(100) DEFAULT 'N/A'
);

CREATE TABLE stars(
                      id varchar(10) PRIMARY KEY,
                      name varchar(100) DEFAULT 'N/A',
                      birthYear varchar(5) DEFAULT 'N/A'
);

CREATE TABLE stars_in_movies(
                                starId varchar(10) NOT NULL,
                                movieId varchar(10) NOT NULL,
                                FOREIGN KEY (starId) REFERENCES stars(id),
                                FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE genres(
                       id int PRIMARY KEY AUTO_INCREMENT,
                       name varchar(32) DEFAULT 'N/A'
);

CREATE TABLE genres_in_movies(
                                 genreId int NOT NULL,
                                 movieId varchar(10) NOT NULL,
                                 FOREIGN KEY (genreId) REFERENCES genres(id),
                                 FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE creditcards(
                            id varchar(20) PRIMARY KEY,
                            firstName varchar(50) DEFAULT 'N/A',
                            lastName varchar(50) DEFAULT 'N/A',
                            expiration date NOT NULL
);

CREATE TABLE customers(
                          id int PRIMARY KEY AUTO_INCREMENT,
                          firstName varchar(50) DEFAULT 'N/A',
                          lastName varchar(50) DEFAULT 'N/A',
                          ccId varchar(20) NOT NULL,
                          address varchar(200) DEFAULT 'N/A',
                          email varchar(50) DEFAULT 'N/A',
                          password varchar(20) DEFAULT 'N/A',
                          FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

CREATE TABLE sales(
                      id int PRIMARY KEY AUTO_INCREMENT,
                      customerId int NOT NULL,
                      movieId varchar(10) NOT NULL,
                      saleDate date NOT NULL,
                      FOREIGN KEY (customerId) REFERENCES customers(id),
                      FOREIGN KEY (movieId) REFERENCES movies(id)
);


CREATE TABLE ratings(
                        movieId varchar(10) NOT NULL,
                        rating float NOT NULL,
                        numVotes int NOT NULL,
                        FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE employees (
                           email varchar(50) primary key,
                           password varchar(20) NOT NULL,
                           fullname varchar(100)
);

INSERT INTO employees VALUES ('classta@email.edu', 'classta', 'TA CS122B');