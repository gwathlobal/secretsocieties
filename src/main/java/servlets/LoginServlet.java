package servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dto.user.LoginUserInDTO;
import dto.user.LoginUserOutDTO;
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
public class LoginServlet extends HttpServlet {

    @Inject
    @PageGenerator
    private IPageGenerator pageGenerator;

    @Inject
    @AccountService
    private IAccountService accountService;

    public static final String path = "/login";

    private static final String loggedVar = "logged";
    private static final String usernameVar = "username";
    private static final String errorReasonVar = "errorReason";

    private static final String errorReasonAttr = "errorReason";
    private static final String reqStatusAttr = "reqStatus";

    private static final String usernameParam = "username";
    private static final String userpassParam = "userpass";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //User loggedInUser = accountService.findLoginUserBySession(req.getSession().getId());
        User loggedInUser = (User) req.getSession().getAttribute("user");

        Map<String, Object> pageVariables = new HashMap<>();
        String reasonAttr = (String) req.getAttribute(errorReasonAttr);
        if (reasonAttr != null)
            pageVariables.put(errorReasonVar, reasonAttr);
        if (loggedInUser != null) {
            pageVariables.put(loggedVar, true);
            pageVariables.put(usernameVar, loggedInUser.getLogin());
        }
        resp.getWriter().println(pageGenerator.getPage(getServletContext(),"login.html", pageVariables));
        resp.setContentType("text/html;charset=utf-8");
        Integer reqStatus = (Integer) req.getAttribute(reqStatusAttr);
        if (reqStatus != null)
            resp.setStatus(reqStatus);
        else
            resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");

        String username = req.getParameter(usernameParam);
        String userpass = req.getParameter(userpassParam);

        LoginUserOutDTO result = accountService.doLoginUser(new LoginUserInDTO(username,userpass, new HttpSessionStorage(req.getSession())));

        if (result.servletCode != HttpServletResponse.SC_OK) {
            // TODO: learn if i need to use
            //  try (Writer writer = resp.getWriter()) {
            //     writer.write("string");
            //      writer.flush();
            //   }
            req.setAttribute(errorReasonAttr, result.responseMsg);
            req.setAttribute(reqStatusAttr, result.servletCode);
            doGet(req, resp);
            return;
        }

        resp.sendRedirect("/");

    }
}
