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


@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        //String id = request.getParameter("id");

        // The log message can be found in localhost log
        //request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query ="SELECT\n" +
                    "\tm.id as id,\n" +
                    "    m.title as title,\n" +
                    "    m.year as year,\n" +
                    "    m.director as director,\n" +
                    "    GROUP_CONCAT(DISTINCT g.name ORDER BY g.id SEPARATOR ', ') as genres,\n" +
                    "    GROUP_CONCAT(DISTINCT g.id ORDER BY g.id SEPARATOR ',') AS genres_id,\n" +
                    "    GROUP_CONCAT(DISTINCT s.name ORDER BY s.id ASC SEPARATOR ',') AS stars,\n" +
                    "    GROUP_CONCAT(DISTINCT s.id ORDER BY s.id SEPARATOR ',') AS stars_id,\n" +
                    "    mr.Movie_Rating as rating\n" +
                    "FROM (\n" +
                    "\tSELECT m.id as id, r.rating as Movie_Rating\n" +
                    "\tFROM movies m, ratings r\n" +
                    "\tWHERE m.id = r.movieId\n" +
                    "\tORDER BY Movie_Rating DESC\n" +
                    "\tLIMIT 0, 20) mr, \n" +
                    "    movies m, genres_in_movies gm, genres g, stars_in_movies sm, stars s\n" +
                    "WHERE mr.id = m.id and mr.id = gm.movieId and gm.genreId = g.id and mr.id = sm.movieId and sm.starId = s.id\n" +
                    "GROUP BY m.id\n" +
                    "ORDER BY Movie_Rating DESC";



            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            //statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                // Will need genre Id later on so might as well store the ID

                String movieId = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String genres = rs.getString("genres");
                String genres_id = rs.getString("genres_id");
                String stars = rs.getString("stars");
                String stars_id = rs.getString("stars_id");
                String rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", title);
                jsonObject.addProperty("movie_year", year);
                jsonObject.addProperty("movie_director", director);
                jsonObject.addProperty("movie_genres", genres);
                jsonObject.addProperty("movie_genres_id", genres_id);
                jsonObject.addProperty("movie_stars", stars);
                jsonObject.addProperty("movie_stars_id", stars_id);
                jsonObject.addProperty("movie_rating", rating);



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
