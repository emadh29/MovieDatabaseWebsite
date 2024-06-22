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


@WebServlet(name = "FullTextSearchServlet", urlPatterns = "/api/fulltext")
public class FullTextSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 29L;


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

        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            String user_search = request.getParameter("query");
            JsonArray jsonArray = new JsonArray();

            if (user_search == null || user_search.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            String query = "SELECT id, title, year FROM movies\n" +
                    "WHERE MATCH(title) AGAINST (? IN BOOLEAN MODE) LIMIT 10;";

            String[] words = user_search.split(" ");
            String word_identifier = "";

            for (String word : words) {
                word_identifier += "+" + word + "* ";
            }

            try (PreparedStatement statement = conn.prepareStatement(query)) {

                statement.setString(1, word_identifier);

                System.out.println(statement.toString());
                System.out.println(statement);

                ResultSet rs = statement.executeQuery();

                // Iterate through each row of rs
                while (rs.next()) {
                    String movie_id = rs.getString("id");
                    String movie_title = rs.getString("title");
                    String movie_year = rs.getString("year");


                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("value", movie_title + " (" + movie_year + ")");

                    JsonObject additionalJsonObject = new JsonObject();
                    additionalJsonObject.addProperty("movie_id", movie_id);
                    jsonObject.add("data", additionalJsonObject);

                    jsonArray.add(jsonObject);
                }

                rs.close();
                statement.close();

                // Log to localhost log
                request.getServletContext().log("getting " + jsonArray.size() + " results");

                System.out.println(jsonArray.toString());
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
