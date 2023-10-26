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
        int amt = 10;
        int offset = 0;
//        boolean order = true; // True when rating is first, False when title is first
        String[] arr;
        String firstOrder = "rating Desc";
        String entireOrder = "rating Desc, title Desc";
        if (!request.getParameter("amt").equals("null")) {
            amt = Integer.parseInt(request.getParameter("amt"));
        }
        if (!request.getParameter("sort").equals("null")) {
            arr = request.getParameter("sort").split(" ");
            if (arr[0].equals("t")) {
                firstOrder = "title " + arr[1];
                entireOrder = "title " + arr[1] + ", rating " + arr[2];
            }
            else {
                firstOrder = "rating " + arr[1];
                entireOrder = "rating " + arr[1] + ", title " + arr[2];
            }
        }
        if (!request.getParameter("offset").equals("null")) {
            offset = Integer.parseInt(request.getParameter("offset"));
        }




        // The log message can be found in localhost log
        //request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource
            // Construct a query with parameter represented by "?"
            // count(*) OVER() AS full_count
            String query ="SELECT\n" +
                    "    m.id as id,\n" +
                    "    m.title as title,\n" +
                    "    m.year as year,\n" +
                    "    m.director as director,\n" +
                    "    GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', ') as genres,\n" +
                    "    GROUP_CONCAT(DISTINCT g.id ORDER BY g.name ASC SEPARATOR ',') AS genres_id,\n" +
                    "    GROUP_CONCAT(DISTINCT s.name ORDER BY (select count(sm.movieId) from stars_in_movies sm where sm.starId = s.id) Desc, s.name Asc SEPARATOR ',') AS stars,\n" +
                    "    GROUP_CONCAT(DISTINCT s.id ORDER BY (select count(sm.movieId) from stars_in_movies sm where sm.starId = s.id) Desc, s.name Asc SEPARATOR ',') AS stars_id,\n" +
                    "    mr.rating as rating,\n" +
                    "    mr.total_rows\n" +
                    "FROM (\n" +
                    "\tSELECT m.id as id, r.rating as rating, count(*) OVER() AS total_rows\n" +
                    "\tFROM movies m, ratings r\n" +
                    "\tWHERE m.id = r.movieId\n" +
                    "\tORDER BY " + firstOrder + "\n" +
                    "\tLIMIT ? OFFSET ?) mr, \n" +
                    "movies m, genres_in_movies gm, genres g, stars_in_movies sm, stars s\n" +
                    "WHERE mr.id = m.id and mr.id = gm.movieId and gm.genreId = g.id and mr.id = sm.movieId and s.id = sm.starId\n" +
                    "GROUP BY m.id\n" +
                    "ORDER BY " + entireOrder + ";";



            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setInt(1, amt);
            statement.setInt(2, offset);

//            if (amt == null) {
//                statement.setString(1, "10");
//            }
//            else {
//                statement.setString(1, amt);
//
//
//            }

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
                String total_rows = rs.getString("total_rows");

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
                jsonObject.addProperty("total_rows", total_rows);



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