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
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "GenreServlet", urlPatterns = "/api/genre")
public class BrowseServlet extends HttpServlet {
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

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT g.name, g.id from genres as g " +
                    "ORDER BY g.name ASC;";


            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            // Iterate through each row of rs
            while (rs.next()) {

                String name = rs.getString("name");
                String id = rs.getString("id");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", name);
                jsonObject.addProperty("id", id);

                jsonArray.add(jsonObject);
            }
            System.out.print("yes");

            query = "SELECT DISTINCT SUBSTRING(m.title, 1, 1) AS t\n" +
                    "FROM movies m\n" +
                    "ORDER BY t Asc;";
            statement = conn.prepareStatement(query);
            rs = statement.executeQuery();

            JsonArray jsonArrayTitle = new JsonArray();
            while (rs.next()) {

                String titleChar = rs.getString("t");
                if (!Character.isLetterOrDigit(titleChar.charAt(0))) {
                    continue;
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("titleChar", titleChar);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
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
