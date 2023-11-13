import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

@WebServlet(name = "AddGenreServlet", urlPatterns = "/_dashboard/addGenre")
public class AddGenre extends HttpServlet {
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

        String genre = request.getParameter("name");

        try (Connection conn = dataSource.getConnection()) {
            CallableStatement statement = conn.prepareCall("{CALL add_genre(?,?)}");

            statement.setString(1, genre);
            statement.registerOutParameter(2, Types.VARCHAR);

            statement.execute();

            String status = statement.getString(2);
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