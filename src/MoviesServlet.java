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

        String genreCondition = "";
        String titleCondition = "";
        String[] arr;
        String firstOrder = "rating Desc";
        String entireOrder = "rating Desc, title Desc";
        String movie_name_cond = "";
        String year_cond = "";
        String director_cond = "";
        String star_name_cond = "";

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
        if (!request.getParameter("genreId").equals("null")) {
            genreCondition = "and gm.genreId =" + request.getParameter("genreId");
        }
        if (!request.getParameter("titleChar").equals("null")) {
            String c = request.getParameter("titleChar");
            if (!c.equals("*")) {
                titleCondition = " and m.title LIKE '" + c + "%'";
            }
            else {
                titleCondition = " and m.title RLIKE '^[^a-zA-Z0-9]'";
            }
        }
        if (!request.getParameter("movie_name").equals("null")) {
            movie_name_cond = " and m.title LIKE '%" + request.getParameter("movie_name") + "%'";
        }
        if (!request.getParameter("year").isEmpty() && !request.getParameter("year").equals("null")) {
            year_cond = " and m.year =" + request.getParameter("year");
        }
        if (!request.getParameter("director").equals("null")) {
            director_cond = " and m.director LIKE '%" + request.getParameter("director") + "%'";
        }
        if (!request.getParameter("star_name").equals("null")) {
            star_name_cond = " and s.name LIKE '%" + request.getParameter("star_name") + "%'";
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
            String query = "";
            PreparedStatement statement;
            if (!genreCondition.isEmpty() || !titleCondition.isEmpty()) {
                // This is browsing query

                query = "SELECT\n" +
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
                        "\tSELECT DISTINCT m.id as id, r.rating as rating, count(*) OVER() AS total_rows\n" +
                        "\tFROM movies m, ratings r, genres_in_movies gm\n" +
                        "\tWHERE m.id = r.movieId and m.id = gm.movieId " + genreCondition + titleCondition + "\n" +
                        "\tORDER BY " + firstOrder + "\n" +
                        "\tLIMIT ? OFFSET ?) mr, \n" +
                        "movies m, genres_in_movies gm, genres g, stars_in_movies sm, stars s\n" +
                        "WHERE mr.id = m.id and mr.id = gm.movieId and gm.genreId = g.id and mr.id = sm.movieId and s.id = sm.starId\n" +
                        "GROUP BY m.id\n" +
                        "ORDER BY " + entireOrder + ";";

                // Declare our statement
                statement = conn.prepareStatement(query);

                statement.setInt(1, amt);
                statement.setInt(2, offset);
            }
            else {
                query = "SELECT\n" +
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
                        "\tSELECT DISTINCT m.id as id, r.rating as rating, count(*) OVER() AS total_rows\n" +
                        "\tFROM movies m, ratings r, stars_in_movies sm, stars s\n" +
                        "\tWHERE m.id = r.movieId and m.id = sm.movieId and sm.starId = s.id" + movie_name_cond + director_cond + year_cond + star_name_cond+ "\n" +
                        "\tORDER BY " + firstOrder + "\n" +
                        "\tLIMIT ? OFFSET ?) mr, \n" +
                        "movies m, genres_in_movies gm, genres g, stars_in_movies sm, stars s\n" +
                        "WHERE mr.id = m.id and mr.id = gm.movieId and gm.genreId = g.id and mr.id = sm.movieId and s.id = sm.starId\n" +
                        "GROUP BY m.id\n" +
                        "ORDER BY " + entireOrder + ";";


                statement = conn.prepareStatement(query);
                statement.setInt(1, amt);
                statement.setInt(2, offset);
//                statement.setString(1, "L");
//                statement.setString(2, "");
//                statement.setString(3, "");

            }



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