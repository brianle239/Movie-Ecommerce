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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/addMovie")
public class AddMovie extends HttpServlet {
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
        JsonObject jsonObject = new JsonObject();

        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("starName");
        String birthYear = request.getParameter("birthYear");
        String genreName = request.getParameter("genre");

        try (Connection conn = dataSource.getConnection()) {
            CallableStatement statement = conn.prepareCall("{CALL add_movie(?,?,?,?,?,?,?)}");

            statement.setString(1, title);
            statement.setInt(2, Integer.parseInt(year));
            statement.setString(3, director);
            statement.setString(4, starName);
            if (birthYear != null && !birthYear.isEmpty()) {
                statement.setInt(5, Integer.parseInt(birthYear));
            } else {
                statement.setNull(5, Types.INTEGER);
            }
            statement.setString(6, genreName);

            statement.registerOutParameter(7, Types.VARCHAR);


            statement.execute();

            String status = statement.getString(7);
            jsonObject.addProperty("status", status);

        }
        catch (Exception e) {
            jsonObject.addProperty("errorMessage", "Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            response.getWriter().write(jsonObject.toString());
            response.getWriter().flush();
            response.getWriter().close();
        }
    }
}
