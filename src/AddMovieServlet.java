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
import java.sql.PreparedStatement;
import java.sql.*;
import java.sql.ResultSet;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/add-movie")
public class AddMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 14L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MySQLReadWrite");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String title = request.getParameter("title");
        int year = Integer.parseInt(request.getParameter("year"));
        String director = request.getParameter("director");
        float rating = Float.parseFloat(request.getParameter("rating"));
        String star_name = request.getParameter("star");
        String birth_year = request.getParameter("star_birth_year");
        String genre = request.getParameter("genre");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            CallableStatement add_movie = conn.prepareCall("{call add_movie(?, ?, ?, ?, ?, ?, ?)}");

            add_movie.setString(1, title);
            add_movie.setInt(2, year);
            add_movie.setString(3, director);
            add_movie.setFloat(4, rating);
            add_movie.setString(5, star_name);

            if (birth_year == null || birth_year.isEmpty()) {
                add_movie.setString(6, "N/A");
            } else {
                add_movie.setString(6, birth_year);
            }

            add_movie.setString(7, genre);

            System.out.println(add_movie);

            boolean Results = false;
            try {
                Results = add_movie.execute();
                System.out.println(Results);

                System.out.println("Stored procedure executed successfully");
            } catch (SQLException e) {
                System.out.println("Error executing stored procedure: " + e.getMessage());
                e.printStackTrace();
            }

            JsonObject json = new JsonObject();

            if (!Results) {
                json.addProperty("message", "failure");
            } else {
                ResultSet rs = add_movie.getResultSet();

                // Iterate through each row of rs
                while (rs.next()) {
                    String movie_id = rs.getString("movie_id");
                    String star_id = rs.getString("star_id");
                    int genre_id = rs.getInt("genre_id");

                    // Create a JsonObject based on the data we retrieve from rs
                    json.addProperty("movie_id", movie_id);
                    json.addProperty("star_id", star_id);
                    json.addProperty("genre_id", genre_id);
                    json.addProperty("message", "success");

                }
                rs.close();
            }

            out.write(json.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);

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
