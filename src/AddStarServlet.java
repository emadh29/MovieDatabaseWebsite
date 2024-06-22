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

@WebServlet(name = "AddStarServlet", urlPatterns = "/_dashboard/api/add-star")
public class AddStarServlet extends HttpServlet {
    private static final long serialVersionUID = 13L;

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
        String name = request.getParameter("name");

        // The log message can be found in localhost log
        request.getServletContext().log("getting name: " + name);

        Integer birth_year = null;
        String birthYear_Str = request.getParameter("birth_year");
        if (birthYear_Str != null && !birthYear_Str.isEmpty()) {
            birth_year = Integer.parseInt(birthYear_Str);
        }

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            CallableStatement add_star = conn.prepareCall("{call add_star(?, ?)}");


            add_star.setString(1, name);

            if (birth_year == null) {
                add_star.setNull(2, java.sql.Types.INTEGER);
            } else {
                add_star.setInt(2, birth_year);
            }

            boolean Results = add_star.execute();

            JsonObject json = new JsonObject();

            if (!Results) {
                json.addProperty("message", "failure");
            } else {
                ResultSet rs = add_star.getResultSet();

                // Iterate through each row of rs
                while (rs.next()) {
                    String starId = rs.getString("star_id");

                    // Create a JsonObject based on the data we retrieve from rs
                    json.addProperty("star_id", starId);

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
