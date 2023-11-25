import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "Autocomplete", urlPatterns = "/api/autocomplete")
public class Autocomplete extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        HashMap<String, JsonArray> autoHistory = ((HashMap<String, JsonArray> ) session.getAttribute("autoHistory"));
        JsonObject resObject = new JsonObject();

        String title = "";
        if (!"null".equals(request.getParameter("movie_name"))) {
            title = "+" + request.getParameter("movie_name").trim().replace(" ", "* +") + "*";
        }

        JsonArray autoResult = null;
        if (autoHistory != null) {
            autoResult = autoHistory.get(title);
        }

        if (autoResult != null) {
            resObject.addProperty("found", "true");
            resObject.add("result", autoResult);

            out.write(resObject.toString());
            response.setStatus(200);
            out.close();
        }
        else {
            // Get a connection from dataSource and let resource manager close the connection after usage.
            try (Connection conn = dataSource.getConnection()) {
                // Get a connection from dataSource

                // Construct a query with parameter represented by "?"
                String query = "SELECT m.title as title, m.id as id from movies as m " +
                        "WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE) LIMIT 10;";


                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, title);

                // Perform the query
                ResultSet rs = statement.executeQuery();

                JsonArray jsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {

                    String name = rs.getString("title");
                    String id = rs.getString("id");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    JsonObject dataJsonObject = new JsonObject();
                    dataJsonObject.addProperty("id", id);

                    jsonObject.addProperty("value", name);
                    jsonObject.add("data", dataJsonObject);

                    jsonArray.add(jsonObject);
                }

                rs.close();
                statement.close();
                if (autoHistory == null) {
                    HashMap<String, JsonArray> resHashMap = new HashMap<>();
                    resHashMap.put(title, jsonArray);
                    session.setAttribute("autoHistory", resHashMap);
                }
                else {
                    synchronized (autoHistory) {
                        autoHistory.put(title, jsonArray);
                        // Set automatically
                        // session.setAttribute("autoHistory", autoHistory);
                    }
                }

                resObject.addProperty("found", "false");
                resObject.add("result", jsonArray);
                // Write JSON string to output
                out.write(resObject.toString());
                // Set response status to 200 (OK)
                response.setStatus(200);
            }
            catch (Exception e) {
                // Write error message JSON object to output
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());

                // Log error to localhost log
                request.getServletContext().log("Error:", e);
                // Set response status to 500 (Internal Server Error)
                response.setStatus(500);
            }
            finally {
                out.close();
            }
        }
    }
}


