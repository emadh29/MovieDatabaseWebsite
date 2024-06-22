import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = {"/api/login", "/_dashboard/api/login"})
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 4L;


    // Create a dataSource which registered in web.
    private DataSource dataSource;


    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MySQLReadOnly");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String user_type = request.getParameter("user");

        /*
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
            RecaptchaVerify.verify(gRecaptchaResponse);
        } catch (Exception e) {
            out.println("<html>");
            out.println("<head><title>Error</title></head>");
            out.println("<body>");
            out.println("<p>recaptcha verification error</p>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");

            out.close();
            return;
        }
         */
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            String table_name = "";
            if (user_type.equals("employee")) {
                table_name = "employees";
            }
            else if (user_type.equals("customer")) {
                table_name = "customers";
            }

            String query = "SELECT * " + "FROM " + table_name + " WHERE email = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, username);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String encryptedPassword = resultSet.getString("password");
                    //boolean success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                    boolean success = password.equals(encryptedPassword);
                    // Login success:
                    if (success) {
                        // Login success:
                        if (user_type.equals("customer")) {
                            int customerID = resultSet.getInt("id");

                            // set this user into the session
                            request.getSession().setAttribute("user", new User(username, customerID));
                        }
                        else {
                            request.getSession().setAttribute("employee", new User(username));
                        }

                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("message", "success");
                    } else {
                        // Login fail (incorrect password)
                        responseJsonObject.addProperty("status", "fail");
                        responseJsonObject.addProperty("message", "incorrect login details");
                    }
                } else {
                    // Login fail
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Login failed");
                    // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                    responseJsonObject.addProperty("message", "incorrect login details");
                }
            }
        } catch (SQLException e) {
            responseJsonObject.addProperty("status", "error");
            responseJsonObject.addProperty("message", "Database error: " + e.getMessage());
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
