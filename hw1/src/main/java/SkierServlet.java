import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import com.google.gson.Gson;

@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {

    private int URL_BY_DAY_SIZE = 8;
    private int URL_BY_RESORT_SIZE = 3;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String urlPath = request.getPathInfo();
        Map<String, String[]> body = request.getParameterMap();

        // check we have a URL!`
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            String jsonString = new Gson().toJson("missing paramterers");
            out.write(jsonString);
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (!isGetVerByDayOrPost(urlParts) || !validPostBody(body)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String jsonString = new Gson().toJson("Input Not Valid");
            out.write(jsonString);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            String jsonString = new Gson().toJson("1");
            out.write(jsonString);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String urlPath = request.getPathInfo();
        // check we have a URL!`
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            String jsonString = new Gson().toJson("missing paramterers");
            out.write(jsonString);
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (isGetVerByDayOrPost(urlParts)) {
            response.setStatus(HttpServletResponse.SC_OK);
            String jsonString = new Gson().toJson(1);
            out.write(jsonString);
        } else if (isGetVerByRes(urlParts)) {
            response.setStatus(HttpServletResponse.SC_OK);
            String jsonString = new Gson().toJson(1);
            out.write(jsonString);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    private boolean isGetVerByDayOrPost(String[] urlPath) {
        // urlPath  = "/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}"
        // http://localhost:8090/skiers/1/seasons/3/days/4/skiers/5
        // urlParts = [, 1, seasons, 2019, days, 1, skiers, 123]
        return urlPath.length == URL_BY_DAY_SIZE && urlPath[2].equals("seasons")
                && urlPath[4].equals("days")  && urlPath[6].equals("skiers");
    }

    private boolean isGetVerByRes(String[] urlPath) {
        // urlPath  = "/{skierID}/vertical"
        //http://localhost:8090/skiers/1/vertical
        return urlPath.length == URL_BY_RESORT_SIZE && urlPath[2].equals("vertical");
    }

    private boolean validPostBody(Map<String, String[]> body) {
        return body.containsKey("time") && body.containsKey("liftID");
    }

}
