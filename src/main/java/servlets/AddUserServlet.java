package servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dto.user.AddUserInDTO;
import dto.user.AddUserOutDTO;
import dto.user.RegisterUserInDTO;
import dto.user.RegisterUserOutDTO;
import guice.AccountService;
import guice.EmailService;
import guice.PageGenerator;
import models.user.User;
import service.IAccountService;
import service.IEmailService;
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
public class AddUserServlet extends HttpServlet {
    @Inject
    @PageGenerator
    private IPageGenerator pageGenerator;

    @Inject
    @AccountService
    private IAccountService accountService;

    @Inject
    @EmailService
    private IEmailService emailService;

    public static final String path = "/adduser";

    private static final String loggedVar = "logged";
    private static final String usernameVar = "username";
    private static final String errorReasonVar = "errorReason";

    private static final String errorReasonAttr = "errorReason";
    private static final String reqStatusAttr = "reqStatus";

    private static final String usernameParam = "username";
    private static final String userpassParam = "userpass";
    private static final String usermailParam = "usermail";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        User loggedInUser = accountService.findLoginUserBySession(new HttpSessionStorage(req.getSession()));

        String reasonAttr = (String) req.getAttribute(errorReasonAttr);
        if (reasonAttr != null)
            pageVariables.put(errorReasonVar, reasonAttr);
        if (loggedInUser != null) {
            pageVariables.put(loggedVar, true);
            pageVariables.put(usernameVar, loggedInUser.getLogin());
        }

        resp.getWriter().println(pageGenerator.getPage(getServletContext(),"add_user.html", pageVariables));
        resp.setContentType("text/html;charset=utf-8");
        Integer reqStatus = (Integer) req.getAttribute(reqStatusAttr);
        if (reqStatus != null)
            resp.setStatus(reqStatus);
        else
            resp.setStatus(HttpServletResponse.SC_OK);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");

        String username = req.getParameter(usernameParam);
        String userpass = req.getParameter(userpassParam);
        String usermail = req.getParameter(usermailParam);

        AddUserOutDTO result = accountService.doAddUser(new AddUserInDTO(username, userpass, usermail));

        switch (result.result) {
            case REGISTER_FAILED:
                req.setAttribute(errorReasonAttr, result.responseMsg);
                req.setAttribute(reqStatusAttr, result.servletCode);
                doGet(req, resp);
                return;
            case OK:
                PageHelper.setRespMessagePage(resp, pageGenerator, getServletContext(),
                        "Add User",
                        result.responseMsg,
                        "/users",
                        "Back");
                resp.setStatus(HttpServletResponse.SC_OK);
                break;
        }
    }
}
