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
public class ProfileServlet extends HttpServlet {

    @Inject
    @PageGenerator
    private IPageGenerator pageGenerator;

    @Inject
    @AccountService
    private IAccountService accountService;
    public static final String path = "/profile";

    private static final String menuVar = "menu";
    private static final String usernameVar = "username";
    private static final String emailVar = "email";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        User user = accountService.findLoginUserBySession(new HttpSessionStorage(req.getSession()));

        // include menu
        pageVariables.put(menuVar, PageHelper.generateMenuHtml(req, getServletContext(), pageGenerator, user));

        if (user != null) {
            pageVariables.put(usernameVar, (user.getLogin() != null) ? user.getLogin() : "");
            pageVariables.put(emailVar, (user.getEmail() != null ? user.getEmail() : ""));
        }

        resp.getWriter().println(pageGenerator.getPage(getServletContext(), "profile.html", pageVariables));

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
