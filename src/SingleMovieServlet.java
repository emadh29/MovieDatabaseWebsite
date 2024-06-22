import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;


    // Create a dataSource which registered in web.xml
    private DataSource dataSource;


    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MySQLReadOnly");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


        response.setContentType("application/json"); // Response mime type


        // Retrieve parameter id from url request.
        String id = request.getParameter("id");


        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();


        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource


            // Construct a query with parameter represented by "?"
            // Select director, genres, stars, and rating
            String query = "SELECT m.id, m.title, m.year, m.director, r.rating\n" +
                    "FROM movies AS m, ratings AS r\n" +
                    "WHERE m.id=r.movieId AND m.id=?\n" +
                    "ORDER BY r.rating DESC, m.title;";

            try (PreparedStatement statement = conn.prepareStatement(query)) {
                //System.out.println(statement);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, id);

                // Perform the query
                ResultSet rs = statement.executeQuery();

                JsonArray jsonArray = new JsonArray();

                // Iterate through each row of rs
                while (rs.next()) {
                    String movie_id = rs.getString("m.id");
                    String movie_title = rs.getString("m.title");
                    String movie_year = rs.getString("m.year");
                    String movie_director = rs.getString("m.director");
                    String movie_rating = rs.getString("r.rating");

                    String starQuery = "SELECT s.name, s.id, (SELECT COUNT(*) FROM stars_in_movies WHERE starId = s.id) AS total_num_movies " +
                            "FROM stars AS s " +
                            "JOIN stars_in_movies AS sm ON s.id = sm.starId " +
                            "WHERE sm.movieId = ?" +
                            "GROUP BY s.name, s.id " +
                            "ORDER BY total_num_movies DESC, s.name;";

                    PreparedStatement starStatement = conn.prepareStatement(starQuery);
                    starStatement.setString(1,movie_id);

                    ResultSet starRs = starStatement.executeQuery();

                    ArrayList<String> starsStr = new ArrayList<>();
                    ArrayList<String> starIds = new ArrayList<>();

                    while (starRs.next()) {
                        starsStr.add(starRs.getString("name"));
                        starIds.add(starRs.getString("id"));
                    }

                    starRs.close();
                    starStatement.close();


                    String genreQuery = "SELECT g.name, g.id " +
                            "FROM genres AS g, movies AS m, genres_in_movies AS gm " +
                            "WHERE m.id=gm.movieId AND gm.genreId=g.id AND m.id=?" +
                            "ORDER BY g.name;";

                    PreparedStatement genreStatement = conn.prepareStatement(genreQuery);
                    genreStatement.setString(1, movie_id);

                    ResultSet genreRs = genreStatement.executeQuery();

                    ArrayList<String> genreStr = new ArrayList<>();
                    ArrayList<String> genreIds = new ArrayList<>();

                    while (genreRs.next()) {
                        genreStr.add(genreRs.getString("name"));
                        genreIds.add(genreRs.getString("id"));
                    }

                    genreRs.close();
                    genreStatement.close();

                    // Create a JsonObject based on the data we retrieve from rs
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    jsonObject.addProperty("movie_stars", String.join(", ", starsStr));
                    jsonObject.addProperty("movie_star_ids", String.join(", ", starIds));
                    jsonObject.addProperty("movie_genres", String.join(", ", genreStr));
                    jsonObject.addProperty("movie_genre_ids", String.join(", ", genreIds));
                    jsonObject.addProperty("movie_rating", movie_rating);

                    jsonArray.add(jsonObject);
                }

                rs.close();
                statement.close();

                // Log to localhost log
                request.getServletContext().log("getting " + jsonArray.size() + " results");

                //System.out.println(jsonArray);
                // Write JSON string to output
                out.write(jsonArray.toString());
                // Set response status to 200 (OK)
                response.setStatus(200);
            } catch (SQLException e) {
                // Handle exceptions
                e.printStackTrace();
            }
        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());


            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }


        // Always remember to close db connection after usage. Here it's done by try-with-resources


    }


}
