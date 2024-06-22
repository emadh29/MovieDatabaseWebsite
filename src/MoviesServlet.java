import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;




// Declaring a WebServlet called MoviesServlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    // Create a dataSource which registered in web.
    private DataSource dataSource;


    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


        response.setContentType("application/json"); // Response mime type


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();


        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();


            String query = "SELECT m.id, m.title, m.year, m.director, r.rating " +
                    "FROM movies AS m, ratings AS r " +
                    "WHERE m.id=r.movieId " +
                    "ORDER BY r.rating DESC, m.title LIMIT 20;";


            // Perform the query
            ResultSet rs = statement.executeQuery(query);

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
                        "WHERE sm.movieId = '" + movie_id + "' " +
                        "GROUP BY s.name, s.id " +
                        "ORDER BY total_num_movies DESC, s.name " +
                        "LIMIT 3;";

                Statement starStatement = conn.createStatement();
                ResultSet starRs = starStatement.executeQuery(starQuery);


                String starsStr = "";
                String starIds = "";

                while (starRs.next()) {
                    starsStr += starRs.getString("name") + ", ";
                    starIds += starRs.getString("id") + ", ";
                }

                starRs.close();
                starStatement.close();


                String genreQuery = "SELECT g.name, g.id " +
                        "FROM genres AS g, movies AS m, genres_in_movies AS gm " +
                        "WHERE m.id=gm.movieId AND gm.genreId=g.id AND m.id='" + movie_id + "' " +
                        "ORDER BY g.name LIMIT 3;";

                Statement genreStatement = conn.createStatement();
                ResultSet genreRs = genreStatement.executeQuery(genreQuery);


                String genreStr = "";
                String genreIds = "";

                while (genreRs.next()) {
                    genreStr += genreRs.getString("name") + ", ";
                    genreIds += genreRs.getString("id") + ", ";
                }

                genreRs.close();
                genreStatement.close();

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_stars", starsStr.substring(0, starsStr.length() - 2)); //Get rid of , and space
                jsonObject.addProperty("movie_star_ids", starIds.substring(0, starIds.length() - 2));
                jsonObject.addProperty("movie_genres", genreStr.substring(0, genreStr.length() - 2));
                jsonObject.addProperty("movie_genre_ids", genreIds.substring(0, genreIds.length() - 2));
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();


            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);


        } catch (Exception e) {


            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());


            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }


        // Always remember to close db connection after usage. Here it's done by try-with-resources


    }
}
