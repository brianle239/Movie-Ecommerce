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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieId = request.getParameter("item");
        HttpSession session = request.getSession();

        HashMap<String, Integer> cart = ((HashMap<String, Integer> ) session.getAttribute("cartItems"));

        if (cart == null) {
            cart = new HashMap<String, Integer>();
            cart.put(movieId, 1);;
            session.setAttribute("cartItems", cart);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (cart) {
                cart.put(movieId, cart.getOrDefault(movieId, 0) + 1);
            }
        }

        JsonObject responseJsonObject = new JsonObject();

        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
        }

        responseJsonObject.addProperty("status", "success");
        responseJsonObject.addProperty("message", "added item succesfully!");
        responseJsonObject.addProperty("name", session.getAttribute("user").toString());
        responseJsonObject.addProperty("cart", cart.toString());



        response.getWriter().write(responseJsonObject.toString());
    }
}