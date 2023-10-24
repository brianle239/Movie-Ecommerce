import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Declaring a WebServlet called ItemServlet, which maps to url "/items"
@WebServlet(name = "CartItemServlet", urlPatterns = "/api/cart")
public class CartItemServlet extends HttpServlet {

    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        HttpSession session = request.getSession();
        JsonObject responseJsonObject = new JsonObject();

        HashMap<String, Integer> cart = ((HashMap<String, Integer> ) session.getAttribute("cartItems"));


        if (cart == null) {
            cart = new HashMap<String, Integer>();
        }

        Gson gson = new Gson();
        JsonObject cartJson = gson.toJsonTree(cart).getAsJsonObject();
        responseJsonObject.add("cart", cartJson);

        response.getWriter().write(responseJsonObject.toString());
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        String movieId = request.getParameter("item");
        HttpSession session = request.getSession();
        try (Connection conn = dataSource.getConnection()) {

            String query = "SELECT title FROM movies WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, movieId);
            ResultSet rs = statement.executeQuery();
            while (rs.next())
            {
                String movieTitle = rs.getString("title");
                HashMap<String, Integer> cart = ((HashMap<String, Integer> ) session.getAttribute("cartItems"));
                if (cart == null) {
                    cart = new HashMap<String, Integer>();
                    cart.put(movieTitle, 1);;
                    session.setAttribute("cartItems", cart);
                } else {
                    // prevent corrupted states through sharing under multi-threads
                    // will only be executed by one thread at a time
                    synchronized (cart) {
                        cart.put(movieTitle, cart.getOrDefault(movieId, 0) + 1);
                    }
                }
                JsonObject responseJsonObject = new JsonObject();

                Gson gson = new Gson();
                JsonObject cartJson = gson.toJsonTree(cart).getAsJsonObject();

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "added item succesfully!");
                responseJsonObject.addProperty("name", session.getAttribute("user").toString());
                responseJsonObject.add("cart", cartJson);
                response.getWriter().write(responseJsonObject.toString());

            }




        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.getWriter().write(jsonObject.toString());

        }
        finally {
            response.getWriter().close();
        }






;
    }
}