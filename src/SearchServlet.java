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
import java.sql.*;

@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Use Http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        System.out.println(request.getParameter("title"));


        try (Connection conn = dataSource.getConnection()) {

            String movie_title_request = "%" + request.getParameter("title").replace(" ", "_") + "%";

            String movie_director_request = "%" + request.getParameter("director").replace(" ", "_") + "%";

            String movie_year_request = request.getParameter("year");
            String movie_star_request = "%" + request.getParameter("star").replace(" ", "_") + "%";

            String query = "SELECT m.id, m.title, m.year, m.director, r.rating\n" +
                    "FROM movies AS m\n" +
                    "INNER JOIN ratings AS r ON m.id = r.movieId\n" +
                    "INNER JOIN stars_in_movies AS sm ON m.id = sm.movieId  \n" +
                    "INNER JOIN stars AS s ON sm.starId = s.id\n" +
                    "WHERE (m.title LIKE ? OR ? = '') AND (m.director LIKE ? OR ? = '') AND (m.year = ? OR ? = '') AND (s.name LIKE ? OR ? = '') \n" +
                    "GROUP BY m.id, r.rating, m.title\n" +
                    "ORDER BY r.rating DESC, m.title " +
                    "LIMIT 20;";

            try(PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, movie_title_request);
                statement.setString(2, movie_title_request);
                statement.setString(3, movie_director_request);
                statement.setString(4, movie_director_request);
                statement.setString(5, movie_year_request);
                statement.setString(6, movie_year_request);
                statement.setString(7, movie_star_request);
                statement.setString(8, movie_star_request);

                //System.out.println(statement);

                // Execute the query and process the result
                ResultSet rs = statement.executeQuery();

                // Process the resultSet
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
                            "ORDER BY total_num_movies DESC, s.name;";

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
                            "ORDER BY g.name;";

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
                //System.out.println(jsonArray.toString());
                response.setStatus(200);

            } catch (SQLException e) {
                // Handle exceptions
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
