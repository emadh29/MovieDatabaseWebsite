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
import java.util.ArrayList;

// Declaring a WebServlet called ResultServlet, which maps to url "/api/result"
@WebServlet(name = "ResultServlet", urlPatterns = "/api/result")
public class ResultServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MySQLReadOnly");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private String join(String[] strings) {
        StringBuilder result = new StringBuilder();

        for (String str : strings) {
            if (str != null && !str.isEmpty()) {
                if (result.length() > 0) {
                    result.append(" AND ");
                }
                result.append(str);
            }
        }

        return result.toString();
    }

    private String returnQuery(HttpServletRequest request) {
        String title = request.getParameter("title");
        String director = request.getParameter("director");
        String year = request.getParameter("year");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        String order = request.getParameter("sort");

        int page = Integer.parseInt(request.getParameter("page"));
        int entries = Integer.parseInt(request.getParameter("entries"));

        String title_query="";
        String director_query="";
        String year_query="";
        String order_query = "";

        switch (order) {
            case "rating_desc_title_asc":
                order_query = "ORDER BY r.rating DESC, m.title ASC\n";
                break;
            case "rating_desc_title_desc":
                order_query = "ORDER BY r.rating DESC, m.title DESC\n";
                break;
            case "rating_asc_title_asc":
                order_query = "ORDER BY r.rating ASC, m.title ASC\n";
                break;
            case "rating_asc_title_desc":
                order_query = "ORDER BY r.rating ASC, m.title DESC\n";
                break;
            case "title_asc_rating_desc":
                order_query = "ORDER BY m.title ASC, r.rating DESC\n";
                break;
            case "title_asc_rating_asc":
                order_query = "ORDER BY m.title ASC, r.rating ASC\n";
                break;
            case "title_desc_rating_desc":
                order_query = "ORDER BY m.title DESC, r.rating DESC\n";
                break;
            case "title_desc_rating_asc":
                order_query = "ORDER BY m.title DESC, r.rating ASC\n";
                break;
            default:
                order_query = "ORDER BY r.rating DESC, m.title ASC\n";
                break;
        }

        String final_query= "";

        if (genre != null && !genre.isEmpty()) {
            final_query = "SELECT\n" +
                    "    m.id,\n" +
                    "    m.title,\n" +
                    "    m.year,\n" +
                    "    m.director,\n" +
                    "    r.rating\n" +
                    "FROM movies AS m\n" +
                    "        INNER JOIN ratings AS r ON m.id = r.movieId\n" +
                    "        INNER JOIN genres_in_movies AS gm ON m.id = gm.movieId\n" +
                    "        INNER JOIN genres AS g ON gm.genreId = g.id\n" +
                    "WHERE\n" +
                    "        g.name = '" + genre + "'\n" +
                    "GROUP BY\n" +
                    "    m.id, m.title, m.year, m.director, r.rating\n" + order_query;
        } else {

            if (title != null && !title.isEmpty()) {
                //title = title.replace(" ", "_");

                if (title.length() == 1)
                {
                    if(title.equals("*"))
                        title_query = "(NOT (m.title REGEXP '^[[:alnum:]]'))";
                    else if(isNumeric(title))
                        title_query = "(m.title LIKE '" + title + "%' OR m.title LIKE '% " + title + "%')";
                    else
                        title_query = "(m.title LIKE '" + title + "%')";
                }
                else {
                    //title_query = "(m.title LIKE '" + title + "%' OR m.title LIKE '% " + title +
                    //title_query = "(m.title LIKE '%" + title + "%')";
                    //System.out.println(title);
                    String[] words = title.split(" ");
                    String word_identifier = "";

                    for (String word : words) {
                        word_identifier += "+" + word + "* ";
                    }
                    title_query = "MATCH(m.title) AGAINST('" + word_identifier + "' IN BOOLEAN MODE)";

                }
            }

            if (year != null && !year.isEmpty())
                year_query = "m.year='" + year + "'";

            if (director != null && !director.isEmpty()) {
                director = director.replace(" ", "_");
                //director_query = "(m.director LIKE '" + director + "%' OR m.director LIKE '% " + director + "%')";
                director_query = "(m.director LIKE '%" + director + "%')";
            }

            if (star != null && !star.isEmpty()) {
                star = star.replace(" ", "_");
                //String star_query = "(s.name LIKE '" + star + "%' OR s.name LIKE '% " + star + "%')";
                String star_query = "(s.name LIKE '%" + star + "%')";

                String[] movie_queries = {title_query, director_query, year_query, star_query};
                String movie_query_str = join(movie_queries);

                final_query = "SELECT m.id, m.title, m.year, m.director, r.rating\n" +
                        "FROM movies AS m\n" +
                        "INNER JOIN ratings AS r ON m.id = r.movieId\n" +
                        "INNER JOIN stars_in_movies AS sm ON m.id = sm.movieId\n" +
                        "INNER JOIN stars AS s ON sm.starId = s.id\n" +
                        "WHERE m.id=r.movieId AND " + movie_query_str + "\n" +
                        "GROUP BY m.id, r.rating, m.title\n" + order_query;
            }
            else {
                String[] movie_queries = {title_query,director_query,year_query};
                String movie_query_str = join(movie_queries);

                final_query = movie_query_str.isEmpty() ?
                        "SELECT m.id, m.title, m.year, m.director, r.rating\n" +
                                "FROM movies AS m, ratings AS r\n" +
                                "WHERE m.id=r.movieId\n" + order_query :
                        "SELECT m.id, m.title, m.year, m.director, r.rating\n" +
                                "FROM movies AS m, ratings AS r\n" +
                                "WHERE m.id=r.movieId AND " + movie_query_str + "\n" + order_query;
            }
        }

        final_query += "LIMIT " + entries + " OFFSET " + ((page-1) * entries) + ";";
        return final_query;
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
            try (PreparedStatement statement = conn.prepareStatement(returnQuery(request))) {
                System.out.println(statement);
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

                    //System.out.println("INFO: " + movie_id + " " + movie_title + " " + movie_year + " " + movie_director + " " + movie_rating);

                    String starQuery = "SELECT s.name, s.id, (SELECT COUNT(*) FROM stars_in_movies WHERE starId = s.id) AS total_num_movies " +
                            "FROM stars AS s " +
                            "JOIN stars_in_movies AS sm ON s.id = sm.starId " +
                            "WHERE sm.movieId = ?" +
                            "GROUP BY s.name, s.id " +
                            "ORDER BY total_num_movies DESC, s.name " +
                            "LIMIT 3;";

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
                            "ORDER BY g.name LIMIT 3;";

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

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources
    }
}
