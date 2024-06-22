import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RunAllParsers {

    public static void main(String[] args) throws Exception {

        String dbtype = "mysql";
        String dbname = "MySQLReadWrite";
        String username = "mytestuser";
        String password = "My6$Password";
        DatabaseIds database_ids = new DatabaseIds();

        Class.forName("com.mysql.cj.jdbc.Driver");

        // Connect to the test database
        Connection conn = DriverManager.getConnection("jdbc:" + dbtype + ":///" + dbname + "?autoReconnect=true&useSSL=false",
                username, password);


        System.out.println("Inconsistency Report");
        StarParser star_parser = new StarParser();
        star_parser.runParser();
        MovieParser movie_parser = new MovieParser();
        movie_parser.runParser();
        CastParser cast_parser = new CastParser();
        cast_parser.runParser();

        System.out.println();
        System.out.println("Successfully parsed " + star_parser.total_parsed + " stars.");
        System.out.println("Successfully parsed " + movie_parser.total_parsed + " movies.");
        System.out.println("Successfully parsed " + cast_parser.total_parsed + " stars in movies.");
        System.out.println();

        //LOAD MOVIES
        /*
        String loadDataSQL = "LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/movies_data.csv' INTO TABLE movies FIELDS TERMINATED BY '|' LINES TERMINATED BY '\\n' (id, title,year,director)";
        Statement loadDataStmt = conn.createStatement();
        loadDataStmt.executeUpdate(loadDataSQL);
        System.out.println("Movie Data inserted into database.");
         */


        String insertMovies = "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt2 = conn.prepareStatement(insertMovies)) {
            conn.setAutoCommit(false);

            for (Movie movie : movie_parser.movies.values()) {
                String id = movie.getId();
                String title = movie.getTitle();
                int year = movie.getYear();
                String director = movie.getDirector();

                stmt2.setString(1, id);
                stmt2.setString(2, title);
                stmt2.setInt(3, year);
                stmt2.setString(4, director);

                stmt2.addBatch();
            }

            stmt2.executeBatch();
            conn.commit();
            System.out.println("Movies data inserted into the database.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        String insertrating = "INSERT INTO ratings (movieId, rating, numVotes) VALUES (?, ?, ?)";
        try (PreparedStatement stmt4 = conn.prepareStatement(insertrating)) {
            conn.setAutoCommit(false);

            for (Movie movie : movie_parser.movies.values()) {
                String id = movie.getId();
                float rating = -1;
                int numVotes = -1;

                stmt4.setString(1, id);
                stmt4.setString(2, String.valueOf(rating));
                stmt4.setInt(3, numVotes);
                stmt4.addBatch();
            }

            stmt4.executeBatch();
            conn.commit();
            System.out.println("Rating data inserted into the database.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        String insertStars = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertStars)) {
            conn.setAutoCommit(false);

            for (Star star : star_parser.stars.values()) {
                String starId = star.getId();
                String starName = star.getName();
                Integer birthYear = star.getBirthYear();
                //inserted_stars.put(starName, starId);

                stmt.setString(1, starId);
                stmt.setString(2, starName);
                if (birthYear != null) {
                    stmt.setInt(3, birthYear);
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }

                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
            System.out.println("Stars Data inserted into database.");
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }

        //is movie in movie parsers?
        //is star in star parsers?
        // get movie id, star id

        String insertStarsinMovies = "INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)";
        try (PreparedStatement smstmt = conn.prepareStatement(insertStarsinMovies)) {
            conn.setAutoCommit(false);

            for (Map.Entry<String, ArrayList<String>> entry : cast_parser.starIDAndMovieID.entrySet()) {
                String movieId = entry.getKey();

                // Check if the movie ID exists in movie_parser.movies
                if (!movie_parser.movies.containsKey(movieId)) {
                    continue; // Continue to the next movie
                }

                ArrayList<String> starNames = entry.getValue();
                for (String starName : starNames) {
                    // Check if the star exists in star_parser.stars
                    if (star_parser.stars.containsKey(starName)) {
                        Star star = star_parser.stars.get(starName);
                        smstmt.setString(1, star.getId());
                        smstmt.setString(2, movieId);
                        smstmt.addBatch();
                    }
                }
            }

            smstmt.executeBatch();
            conn.commit();
            System.out.println("Stars_in_Movies Data inserted into database.");
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }

        String insertGenres = "INSERT INTO genres (id, name) VALUES (?, ?)";
        try (PreparedStatement genre_stmt = conn.prepareStatement(insertGenres)) {
            conn.setAutoCommit(false);

            for (Movie movie : movie_parser.movies.values()) {
                for (Genre genre : movie.getGenres()) {
                    String genreName = genre.getName();

                    // Check if the genre exists in the genreTable
                    if (!(database_ids.genreTable.containsKey(genreName))) {
                        int genreId = genre.getId();
                        database_ids.genreTable.put(genreName, genreId);
                        genre_stmt.setInt(1, genreId);
                        genre_stmt.setString(2, genreName);
                        genre_stmt.addBatch();
                    }
                }
            }
            genre_stmt.executeBatch();
            conn.commit();
            System.out.println("Genres Data inserted into database.");
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }

        String insertGenresInMovies = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";
        try (PreparedStatement genre_in_movie_stmt = conn.prepareStatement(insertGenresInMovies)) {
            conn.setAutoCommit(false);

            for (Movie movie : movie_parser.movies.values()) {
                String movieId = movie.getId();
                for (Genre genre : movie.getGenres()) {
                    String genreName = genre.getName();
                    int genreId = genre.getId();
                    genre_in_movie_stmt.setInt(1, genreId);
                    genre_in_movie_stmt.setString(2, movieId);
                    genre_in_movie_stmt.addBatch();
                }
            }

            genre_in_movie_stmt.executeBatch();
            conn.commit();
            System.out.println("Genres_in_Movies Data inserted into database.");
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }

    }
}
