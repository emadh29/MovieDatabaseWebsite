SET FOREIGN_KEY_CHECKS=0;

use moviedb;
DROP TABLE xbackup_creditcards;
DROP TABLE xbackup_customers;
DROP TABLE xbackup_employees;
DROP TABLE xbackup_genres;
DROP TABLE xbackup_genres_in_movies;
DROP TABLE xbackup_movies;
DROP TABLE xbackup_ratings;
DROP TABLE xbackup_sales;
DROP TABLE xbackup_stars;
DROP TABLE xbackup_stars_in_movies;

CREATE TABLE xbackup_creditcards AS SELECT * FROM creditcards;
CREATE TABLE xbackup_customers AS SELECT * FROM customers;
CREATE TABLE xbackup_employees AS SELECT * FROM employees;
CREATE TABLE xbackup_genres AS SELECT * FROM genres;
CREATE TABLE xbackup_genres_in_movies AS SELECT * FROM genres_in_movies;
CREATE TABLE xbackup_movies AS SELECT * FROM movies;
CREATE TABLE xbackup_ratings AS SELECT * FROM ratings;
CREATE TABLE xbackup_sales AS SELECT * FROM sales;
CREATE TABLE xbackup_stars AS SELECT * FROM stars;
CREATE TABLE xbackup_stars_in_movies AS SELECT * FROM stars_in_movies;