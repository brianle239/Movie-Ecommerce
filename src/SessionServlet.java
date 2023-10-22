import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

// Declaring a WebServlet called SessionServlet, which maps to url "/session"
@WebServlet(name = "SessionServlet", urlPatterns = "/session")
public class SessionServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String title = "Session Tracking Example";

        // Get a instance of current session on the request
        HttpSession session = request.getSession(true);

        String heading;

        // Retrieve data named "accessCount" from session, which count how many times the user requested before
        Integer accessCount = (Integer) session.getAttribute("accessCount");

        if (accessCount == null) {
            // Which means the user is never seen before
            accessCount = 0;
            heading = "Welcome, New-Comer";
        } else {
            // Which means the user has requested before, thus user information can be found in the session
            heading = "Welcome Back";
            accessCount++;
        }

        // Update the new accessCount to session, replacing the old value if existed
        session.setAttribute("accessCount", accessCount);

        // http://localhost:8080/cs122b-fall21-project2-session-example/Session?myname=Chen%20Li
        String myName = request.getParameter("myname");
        if (myName != null)
            out.println("Hey " + myName + "<br><br>");

    }
}