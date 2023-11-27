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
        HashMap<String, Integer> priceId = ((HashMap<String, Integer> ) session.getAttribute("priceId"));
        HashMap<String, String> idCart = ((HashMap<String, String> ) session.getAttribute("idCart"));


        if (cart == null) {
            cart = new HashMap<String, Integer>();
            idCart = new HashMap<String,String>();
            priceId = new HashMap<String, Integer>();
        }


        Gson cartGson = new Gson();
        JsonObject cartJson = cartGson.toJsonTree(cart).getAsJsonObject();
        Gson idGson = new Gson();
        JsonObject idJson = idGson.toJsonTree(idCart).getAsJsonObject();
        String user = (String) session.getAttribute("user");

        Gson priceGson = new Gson();
        JsonObject priceJson = priceGson.toJsonTree(priceId).getAsJsonObject();

        responseJsonObject.add("cart", cartJson);
        responseJsonObject.add("idCart", idJson);
        responseJsonObject.add("priceId", priceJson);
        responseJsonObject.addProperty("user", user);
        responseJsonObject.addProperty("customerId", session.getAttribute("customerId").toString());


        response.getWriter().write(responseJsonObject.toString());
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String movieId = request.getParameter("item");
        String modify = request.getParameter("increase");
        HttpSession session = request.getSession();

        double randomDouble = Math.random();
        int randomInt = (int)(randomDouble * 35);


            try (Connection conn = dataSource.getConnection()) {

                String query = "SELECT title FROM movies WHERE id = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, movieId);
                ResultSet rs = statement.executeQuery();
                while (rs.next())
                {
                    String movieTitle = rs.getString("title");
                    HashMap<String, String> idCart = ((HashMap<String, String> ) session.getAttribute("idCart"));
                    HashMap<String, Integer> cart = ((HashMap<String, Integer> ) session.getAttribute("cartItems"));
                    HashMap<String, Integer> priceId = ((HashMap<String, Integer>) session.getAttribute("priceId"));

                    String remove = request.getParameter("remove");

                    if (cart == null) {
                        cart = new HashMap<String, Integer>();
                        cart.put(movieTitle, 1);;

                        idCart = new HashMap<String, String>();
                        idCart.put(movieTitle, movieId);

                        priceId = new HashMap<String, Integer>();
                        priceId.put(movieId, randomInt);


                        session.setAttribute("cartItems", cart);
                        session.setAttribute("idCart", idCart);
                        session.setAttribute("priceId", priceId);
                    } else {
                        // prevent corrupted states through sharing under multi-threads
                        // will only be executed by one thread at a time
                        synchronized (cart) {
                            if (remove.equalsIgnoreCase("true")){
                                cart.remove(movieTitle);
                                idCart.remove(movieTitle);
                            }
                            else{
                                if(modify.equalsIgnoreCase("true")){
                                    if(cart.getOrDefault(movieTitle, 0) == 0)
                                    {
                                        cart.put(movieTitle,  1);
                                        priceId.put(movieId, randomInt);

                                    }
                                    else {
                                        cart.put(movieTitle, cart.get(movieTitle) + 1);
                                    }

                                }
                                else {
                                    if(cart.get(movieTitle) == 0){
                                        cart.put(movieTitle, 0);
                                    } else{
                                        cart.put(movieTitle, cart.getOrDefault(movieTitle, 1) - 1);
                                    }


                                }

                        }

                            idCart.put(movieTitle, movieId);
                        }
                    }
                    JsonObject responseJsonObject = new JsonObject();

                    Gson cartGson = new Gson();
                    JsonObject cartJson = cartGson.toJsonTree(cart).getAsJsonObject();
                    Gson idGson = new Gson();
                    JsonObject idJson = idGson.toJsonTree(idCart).getAsJsonObject();

                    Gson priceGson = new Gson();
                    JsonObject priceJson = priceGson.toJsonTree(priceId).getAsJsonObject();

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "added item succesfully!");
                    //responseJsonObject.addProperty("name", session.getAttribute("user").toString());
                    responseJsonObject.add("cart", cartJson);
                    responseJsonObject.add("idCart", idJson);
                    responseJsonObject.add("priceId", priceJson);
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

    }
    }
