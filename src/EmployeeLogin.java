import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called ItemServlet, which maps to url "/items"
@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/_dashboard/login")
public class EmployeeLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT email, fullname, password FROM employees WHERE email = ?";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);

            ResultSet rs = statement.executeQuery();
            JsonObject jsonObject = new JsonObject();

            boolean success = false;

            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                String fullname = rs.getString("fullname");
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                if(success){
                    jsonObject.addProperty("status", "success");
                    jsonObject.addProperty("message", "Logged in successfully!");

                    request.getSession().setAttribute("employee", fullname);
                    request.getSession().setAttribute("user", username);

                    response.setStatus(200);
                }
                else {
                    jsonObject.addProperty("status", "fail");
                    jsonObject.addProperty("message", "Invalid email or password!");
                    jsonObject.addProperty("input", username + " " + password);
                }

            } else {
                // User not found
                jsonObject.addProperty("status", "fail");
                jsonObject.addProperty("message", "User not found");
                jsonObject.addProperty("input", username + " " + password);
            }

            rs.close();
            statement.close();
            //jsonObject.addProperty("sessionId" , request.getSession().getId());
            response.getWriter().write(jsonObject.toString());

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("Login failed. Enter the correct credentials", e.getMessage());
            response.getWriter().write(jsonObject.toString());;
            request.getServletContext().log("Error:", e);
            response.setStatus(500);

        } finally {
            response.getWriter();
        }
    }
}


