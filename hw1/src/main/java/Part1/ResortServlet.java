package Part1;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet(name = "Part1.ResortServlet")
public class ResortServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String urlPath = request.getPathInfo();
        Map<String, String[]> body = request.getParameterMap();

        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            String jsonString = new Gson().toJson("missing paramterers");
            out.write(jsonString);
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (!isGetSeasonOrPostURL(urlParts) || !validPostBody(body)) {
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
        // get a list of ski resorts in the database
        // http://localhost:8090/resorts
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
            String jsonString = new Gson().toJson("Here are the list of ski resorts");
            out.write(jsonString);
            return;
        }
        String[] urlParts = urlPath.split("/");
        if (isGetSeasonOrPostURL(urlParts)) {
            response.setStatus(HttpServletResponse.SC_OK);
            String jsonString = new Gson().toJson("Here are the list of seasons for the resort");
            out.write(jsonString);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private boolean isGetSeasonOrPostURL(String[] urlPath) {
        //http://localhost:8090/resorts/1/seasons
        return urlPath.length == 3 && urlPath[2].equals("seasons");
    }

    private boolean validPostBody(Map<String, String[]> body) {
        return body.containsKey("year");
    }
}
