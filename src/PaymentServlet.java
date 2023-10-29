import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;


@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet  extends HttpServlet {
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
        // get payment information
        String fName = request.getParameter("firstName");
        String lName = request.getParameter("lastName");
        String cc = request.getParameter("cardNumber");
        String exp = request.getParameter("expiry");

        HttpSession session = request.getSession();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * from creditcards WHERE id= ? AND firstName= ? AND lastName= ? AND expiration= ?";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cc);
            statement.setString(2, fName);
            statement.setString(3, lName);
            statement.setString(4, exp);

            ResultSet rs = statement.executeQuery();

            JsonObject jsonObject = new JsonObject();

            if (rs.next()) {
                HashMap<String, Integer> cart = ((HashMap<String, Integer> ) session.getAttribute("cartItems"));
                HashMap<String, String> idCart = ((HashMap<String, String> ) session.getAttribute("idCart"));

                if(cart == null || cart.toString().equals("{}")) {
                    // cart is empty
                    jsonObject.addProperty("status", "fail");
                    jsonObject.addProperty("message", "Your cart is empty!");
                }
                else {
                    // payment is successful

                    query = "INSERT INTO sales(customerId, movieId, saleDate) VALUES (?, ?, CURDATE())";
                    PreparedStatement statement2 = conn.prepareStatement(query);
                    String customerId = session.getAttribute("customerId").toString();
                    for (String key : cart.keySet()) {
                        String movieId = idCart.get(key);

                        statement2.setString(1, customerId);
                        statement2.setString(2, movieId);

                        statement2.executeUpdate();  // Insert for each movie in the cart
                    }

                    statement2.close();

                    jsonObject.addProperty("status", "success");
                    jsonObject.addProperty("message", "successful payment!");
                    jsonObject.addProperty("customerId", session.getAttribute("customerId").toString());
                }

            } else {
                // card information not found
                jsonObject.addProperty("status", "fail");
                jsonObject.addProperty("message", "Invalid payment information!");

            }

            rs.close();
            statement.close();


            response.getWriter().write(jsonObject.toString());

        } catch (Exception e) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.getWriter().write(jsonObject.toString());;

            request.getServletContext().log("Error:", e);
            response.setStatus(500);

        } finally {
            response.getWriter();
        }
    }
}