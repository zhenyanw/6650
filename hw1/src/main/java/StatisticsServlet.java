import com.google.gson.Gson;
import service.pojo.EndPointStats;
import service.pojo.Statistic;
import service.dao.UserService;
import service.dao.IUserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


@WebServlet(name = "StatisticsServlet")
public class StatisticsServlet extends HttpServlet {
    IUserService IUserService = new UserService();
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String urlPath = request.getPathInfo();
        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            List<EndPointStats> statList = new ArrayList<>();
            try {
                statList = IUserService.getStatistics();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String jsonString = new Gson().toJson(statList);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonString);
            out.flush();
        }



    }
}
