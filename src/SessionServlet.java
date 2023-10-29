import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

// Declaring a WebServlet called SessionServlet, which maps to url "/session"
@WebServlet(name = "SessionServlet", urlPatterns = "/api/session")
public class SessionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get a instance of current session on the request
        HttpSession session = request.getSession();
        String sessionId = session.getId();

        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        Integer accessCount = (Integer) session.getAttribute("accessCount");
        String history = (String) session.getAttribute("history");
        String single = (String) session.getAttribute("single");
        System.out.println("YES GET: " + history + ":");
        if (accessCount == null) {
            accessCount = 0;
        } else {
            accessCount++;
        }
        if (history == null || history.equals("null")) {
            history = "";
        }
        if (single == null || single.equals("null")) {
            single = "false";
        }


        session.setAttribute("accessCount", accessCount);
        responseJsonObject.addProperty("accessCount", accessCount);
        responseJsonObject.addProperty("history", history);

        responseJsonObject.addProperty("single", single);
        responseJsonObject.addProperty("session", session.toString());

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());

    }

    // add attributes to session
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        String h = (String) request.getParameter("history");
        try {
            if (h == null || h.equals("null") ) {
                String history = request.getParameter("history");
            }
            else {
                session.setAttribute("history", h);
            }
        }
        catch (Exception e) {
            System.out.println("No History");
        }
        try {
            if (!request.getParameter("single").equals("null")) {
                String single = request.getParameter("single");
                session.setAttribute("single", single);
            }
        }
        catch (Exception e) {
            System.out.println("No Single");
        }

    }
}