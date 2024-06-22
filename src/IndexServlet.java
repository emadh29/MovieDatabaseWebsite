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

@WebServlet(name="IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet{
    private static final long serialVersionID = 8L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MySQLReadOnly");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement statement = conn.prepareStatement("SELECT name from genres")) {
                ResultSet rs = statement.executeQuery();
                JsonArray jsonArray = new JsonArray();

                while(rs.next()) {
                    String genre_name = rs.getString("name");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("genre_name", genre_name);
                    jsonArray.add(jsonObject);
                }
                rs.close();
                statement.close();

                request.getServletContext().log("getting " + jsonArray.size() + " results");
                out.write(jsonArray.toString());
                //System.out.println(jsonArray);

                response.setStatus(200);
            } catch(SQLException e) {
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
    }
}
