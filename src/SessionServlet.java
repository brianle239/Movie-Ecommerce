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
        //User user = (User) session.getAttribute("user");
        //String username;
        //if (user != null) {
            // "user" attribute found, you can work with it
         //    username = user.getUsername();
            // Rest of your code
        //} else {
     //        username = "testing else";
       // }
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());
        //responseJsonObject.addProperty("user", user.toString());
        // Retrieve data named "accessCount" from session, which count how many times the user requested before
        Integer accessCount = (Integer) session.getAttribute("accessCount");
        //responseJsonObject.addProperty("user", session.getAttribute("user").toString());
        //responseJsonObject.addProperty("user", username );

        if (accessCount == null) {
            accessCount = 0;
        } else {
            accessCount++;
        }

        // Update the new accessCount to session, replacing the old value if existed
        session.setAttribute("accessCount", accessCount);
        responseJsonObject.addProperty("accessCount", accessCount);
        responseJsonObject.addProperty("session", session.toString());


        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());

    }
}