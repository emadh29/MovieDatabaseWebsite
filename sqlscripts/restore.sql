SET FOREIGN_KEY_CHECKS=0;

use moviedb;
DROP TABLE creditcards;
DROP TABLE customers;
DROP TABLE employees;
DROP TABLE genres;
DROP TABLE genres_in_movies;
DROP TABLE movies;
DROP TABLE ratings;
DROP TABLE sales;
DROP TABLE stars;
DROP TABLE stars_in_movies;

CREATE TABLE creditcards AS SELECT * FROM xbackup_creditcards;
CREATE TABLE customers AS SELECT * FROM xbackup_customers;
CREATE TABLE employees AS SELECT * FROM xbackup_employees;
CREATE TABLE genres AS SELECT * FROM xbackup_genres;
CREATE TABLE genres_in_movies AS SELECT * FROM xbackup_genres_in_movies;
CREATE TABLE movies AS SELECT * FROM xbackup_movies;
CREATE TABLE ratings AS SELECT * FROM xbackup_ratings;
CREATE TABLE sales AS SELECT * FROM xbackup_sales;
CREATE TABLE stars AS SELECT * FROM xbackup_stars;
CREATE TABLE stars_in_movies AS SELECT * FROM xbackup_stars_in_movies;