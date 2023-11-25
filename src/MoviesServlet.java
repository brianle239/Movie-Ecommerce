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
import java.util.ArrayList;
import java.util.List;

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

        response.setContentType("application/json");

        int amt = 10;
        int offset = 0;

        String genreCondition = "";
        String titleCondition = "";
        String[] arr;
        String firstOrder = "rating Desc";
        String entireOrder = "rating Desc, title Desc";


        if (!request.getParameter("amt").equals("null")) {
            amt = Integer.parseInt(request.getParameter("amt"));
        }
        if (!request.getParameter("sort").equals("null")) {
            // SQL injections are prevented by the
            // Hard code check the strings

            arr = request.getParameter("sort").split(" ");
            if (arr[0].equals("t")) {
                if ((arr[1].equals("Desc") || arr[1].equals("Asc")) && ((arr[2].equals("Desc") || arr[2].equals("Asc")))) {
                    firstOrder = "title " + arr[1];
                    entireOrder = "title " + arr[1] + ", rating " + arr[2];
                }

            }
            else {
                if ((arr[1].equals("Desc") || arr[1].equals("Asc")) && ((arr[2].equals("Desc") || arr[2].equals("Asc")))) {
                    firstOrder = "rating " + arr[1];
                    entireOrder = "rating " + arr[1] + ", title " + arr[2];
                }
            }
        }
        if (!request.getParameter("offset").equals("null")) {
            offset = Integer.parseInt(request.getParameter("offset"));
        }
        if (!request.getParameter("genreId").equals("null")) {
            genreCondition = " and gm.genreId =" + request.getParameter("genreId");
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

        System.out.println(genreCondition);

        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {


            String query = "";
            PreparedStatement statement;
            if (!genreCondition.isEmpty() || !titleCondition.isEmpty()) {
                // This is browsing query

                List<Object> parameters = new ArrayList<>();
                StringBuilder whereClause = new StringBuilder();

                if (!"null".equals(request.getParameter("genreId"))) {
                    whereClause.append(" AND gm.genreId = ?");
                    parameters.add(Integer.parseInt(request.getParameter("genreId")));
                }

                String titleChar = request.getParameter("titleChar");
                if (titleChar != null && !"null".equals(titleChar)) {
                    if (!"*".equals(titleChar)) {
                        whereClause.append(" AND m.title LIKE ?");
                        parameters.add(titleChar + "%");
                    } else {
                        whereClause.append(" AND m.title RLIKE ?");
                        parameters.add("^[^a-zA-Z0-9]");
                    }
                }
                query = "SELECT\n" +
                        "    m.id as id,\n" +
                        "    m.title as title,\n" +
                        "    m.year as year,\n" +
                        "    m.director as director,\n" +
                        "    GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR '\t') as genres,\n" +
                        "    GROUP_CONCAT(DISTINCT g.id ORDER BY g.name ASC SEPARATOR '\t') AS genres_id,\n" +
                        "    GROUP_CONCAT(DISTINCT s.name ORDER BY (select count(sm.movieId) from stars_in_movies sm where sm.starId = s.id) Desc, s.name Asc SEPARATOR '\t') AS stars,\n" +
                        "    GROUP_CONCAT(DISTINCT s.id ORDER BY (select count(sm.movieId) from stars_in_movies sm where sm.starId = s.id) Desc, s.name Asc SEPARATOR '\t') AS stars_id,\n" +
                        "    mr.rating as rating,\n" +
                        "    mr.total_rows\n" +
                        "FROM (\n" +
                        "\tSELECT DISTINCT m.id as id, r.rating as rating, count(*) OVER() AS total_rows\n" +
                        "\tFROM movies m\n" +
                        "\tLEFT JOIN ratings r ON r.movieId = m.id\n" +
                        "\tLEFT JOIN genres_in_movies gm ON gm.movieId = m.id\n" +
                        "\tWHERE m.id is not null" + whereClause + "\n" +
                        "\tGROUP BY m.id\n" +
                        "\tORDER BY "+ firstOrder +"\n" +
                        "\tLIMIT ? OFFSET ?) mr, \n" +
                        "movies m, genres_in_movies gm, genres g, stars_in_movies sm, stars s\n" +
                        "WHERE mr.id = m.id and mr.id = gm.movieId and gm.genreId = g.id and mr.id = sm.movieId and s.id = sm.starId\n" +
                        "GROUP BY m.id\n" +
                        "ORDER BY " + entireOrder +" ;";

                statement = conn.prepareStatement(query);

                int paramIndex = 1;
                for (Object param : parameters) {
                    if (param instanceof String) {
                        statement.setString(paramIndex, (String) param);
                        paramIndex += 1;
                    } else if (param instanceof Integer) {
                        statement.setInt(paramIndex, (int) param);
                        paramIndex += 1;
                    }
                }
                statement.setInt(paramIndex++, amt);
                statement.setInt(paramIndex++, offset);

            }
            else {
                StringBuilder whereClause = new StringBuilder();
                List<Object> parameters = new ArrayList<>();
                if (!"null".equals(request.getParameter("movie_name"))) {
                    // MATCH (entry) AGAINST ('graduate michigan' IN BOOLEAN MODE);
                    whereClause.append(" AND MATCH (title) AGAINST (? IN BOOLEAN MODE)");
                    String temp_movie = "+" + request.getParameter("movie_name").trim().replace(" ", "* +") + "*";
                    parameters.add(temp_movie);
                }

                if (!request.getParameter("year").isEmpty() && !"null".equals(request.getParameter("year"))) {
                    whereClause.append(" AND m.year = ?");
                    parameters.add(request.getParameter("year").trim());
                }

                if (!"null".equals(request.getParameter("director"))) {
                    whereClause.append(" AND m.director LIKE ?");
                    parameters.add("%" + request.getParameter("director").trim() + "%");
                }

                if (!"null".equals(request.getParameter("star_name"))) {
                    whereClause.append(" AND s.name LIKE ?");
                    parameters.add("%" + request.getParameter("star_name").trim() + "%");
                }

                query = "SELECT\n" +
                        "    m.id as id,\n" +
                        "    m.title as title,\n" +
                        "    m.year as year,\n" +
                        "    m.director as director,\n" +
                        "    GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR '\t') as genres,\n" +
                        "    GROUP_CONCAT(DISTINCT g.id ORDER BY g.name ASC SEPARATOR '\t') AS genres_id,\n" +
                        "    GROUP_CONCAT(DISTINCT s.name ORDER BY (select count(sm.movieId) from stars_in_movies sm where sm.starId = s.id) Desc, s.name Asc SEPARATOR '\t') AS stars,\n" +
                        "    GROUP_CONCAT(DISTINCT s.id ORDER BY (select count(sm.movieId) from stars_in_movies sm where sm.starId = s.id) Desc, s.name Asc SEPARATOR '\t') AS stars_id,\n" +
                        "    mr.rating as rating,\n" +
                        "    mr.total_rows\n" +
                        "FROM (\n" +
                        "\tSELECT DISTINCT m.id as id, r.rating as rating, count(*) OVER() AS total_rows\n" +
                        "\tFROM movies m\n" +
                        "\tLEFT JOIN ratings r ON r.movieId = m.id\n" +
                        "\tLEFT JOIN stars_in_movies sm ON sm.movieId = m.id\n" +
                        "\tLEFT JOIN stars s ON sm.starId = s.id\n" +
                        "\tWHERE m.id is not null" +  whereClause + "\n" +
                        "\tGROUP BY m.id\n" +
                        "\tORDER BY " + firstOrder + "\n" +
                        "\tLIMIT ? OFFSET ?) mr, \n" +
                        "movies m, genres_in_movies gm, genres g, stars_in_movies sm, stars s\n" +
                        "WHERE mr.id = m.id and mr.id = gm.movieId and gm.genreId = g.id and mr.id = sm.movieId and s.id = sm.starId\n" +
                        "GROUP BY m.id\n" +
                        "ORDER BY " + entireOrder + ";";


                statement = conn.prepareStatement(query);
//                parameters.add(firstOrder);
                parameters.add(amt);
                parameters.add(offset);

//                parameters.add(entireOrder);

                for (int i = 0; i < parameters.size(); i++) {
                    if (parameters.get(i) instanceof String) {
                        statement.setString(i + 1, (String) parameters.get(i));
                    } else if (parameters.get(i) instanceof Integer) {
                        statement.setInt(i + 1, (Integer) parameters.get(i));
                    }

                }
                System.out.println("Param: " + parameters);

            }
            System.out.println(statement);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            // Iterate through each row of rs
            while (rs.next()) {
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