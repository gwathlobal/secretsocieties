package servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import guice.AccountService;
import guice.PageGenerator;
import models.user.User;
import service.IAccountService;
import templater.IPageGenerator;
import utils.sessionstorage.HttpSessionStorage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class IndexServlet extends HttpServlet {

    @Inject
    @PageGenerator
    private IPageGenerator pageGenerator;

    @Inject
    @AccountService
    private IAccountService accountService;

    public static final String path = "/";
    public static final String path2 = "/index";
    public static final String Name = "IndexServlet";

    private static final String menuVar = "menu";

    // TODO: remake everything using Spring MVC

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        User user = accountService.findLoginUserBySession(new HttpSessionStorage(req.getSession()));

        // include menu
        pageVariables.put(menuVar, PageHelper.generateMenuHtml(req, getServletContext(), pageGenerator, user));

        resp.getWriter().println(pageGenerator.getPage(getServletContext(), "index_s.html", pageVariables));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

    }
}
