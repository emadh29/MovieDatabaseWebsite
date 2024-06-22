import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 8L;


    // Create a dataSource which registered in web.
    private DataSource dataSource;


    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MySQLReadWrite");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String first_name = request.getParameter("first_name");
        String last_name = request.getParameter("last_name");
        String cc_num = request.getParameter("cc_num");
        String exp_date = request.getParameter("exp_date");

        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT *\n" +
                    "FROM creditcards\n" +
                    "WHERE id=? AND firstName=? AND lastName=? AND expiration=?;\n";


            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, cc_num);
                statement.setString(2, first_name);
                statement.setString(3, last_name);
                statement.setString(4, exp_date);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    HttpSession session = request.getSession();
                    User user = (User) session.getAttribute("user");
                    int customerId = user.getId();

                    HashMap<String, MovieClass> previousItems = (HashMap<String, MovieClass>) session.getAttribute("previousItems");

                    for (Map.Entry<String, MovieClass> movie : previousItems.entrySet()) {
                        String sale = "INSERT INTO sales (customerId, movieId, saleDate, quantity)\n" +
                                "VALUES (?, ?, CURDATE(), ?);";

                        PreparedStatement sales_query = conn.prepareStatement(sale);

                        sales_query.setInt(1,customerId);
                        sales_query.setString(2,movie.getValue().movieId);
                        sales_query.setInt(3,movie.getValue().quantity);

                        sales_query.executeUpdate();

                        String salesIdQuery = "SELECT LAST_INSERT_ID() as saleId;";
                        PreparedStatement saleIdStatement = conn.prepareStatement(salesIdQuery);
                        ResultSet rs_saleId = saleIdStatement.executeQuery();
                        if (rs_saleId.next()) {
                            previousItems.get(movie.getKey()).sale_flag = rs_saleId.getInt("saleId");
                        }
                    }

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                } else {
                    responseJsonObject.addProperty("status", "fail");
                    request.getServletContext().log("Credit Card Payment failed");
                    responseJsonObject.addProperty("message", "Incorrect Payment Details");
                }
            }
        } catch (SQLException e) {
            responseJsonObject.addProperty("status", "error");
            responseJsonObject.addProperty("message", "Database error: " + e.getMessage());
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
