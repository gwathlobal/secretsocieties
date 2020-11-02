package servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dto.user.EditUserInDTO;
import dto.user.EditUserOutDTO;
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
public class EditUserServlet extends HttpServlet {
    @Inject
    @PageGenerator
    private IPageGenerator pageGenerator;

    @Inject
    @AccountService
    private IAccountService accountService;

    @Inject
    @EmailService
    private IEmailService emailService;

    public static final String path = "/edituser";

    private static final String loggedVar = "logged";
    private static final String usernameVar = "username";
    private static final String errorReasonVar = "errorReason";

    private static final String errorReasonAttr = "errorReason";
    private static final String reqStatusAttr = "reqStatus";

    private static final String usernameParam = "username";
    private static final String usermailParam = "usermail";

    public static final String editUserIdParam = "edit_user_id";
    private static final String editUsernameParam = "edit_username";
    private static final String editUsermailParam = "edit_usermail";
    public static final String editEnabledParam = "edit_enabled";

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
        long editUserId;
        try {
            editUserId = Integer.parseInt(req.getParameter(editUserIdParam));
        }
        catch (NumberFormatException e) {
            PageHelper.setRespMessagePage(resp, pageGenerator, getServletContext(),
                    "Edit User",
                    "Error! Wrong user id!",
                    "/users",
                    "Back");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User editedUser = accountService.findDBUserById(editUserId);

        pageVariables.put(editUserIdParam, editUserId);
        pageVariables.put(editUsernameParam, editedUser.getLogin());
        pageVariables.put(editUsermailParam, editedUser.getEmail());
        pageVariables.put(editEnabledParam, (editedUser.getEnabled()) ? "true" : null);

        resp.getWriter().println(pageGenerator.getPage(getServletContext(),"edit_user.html", pageVariables));
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

        long userId;
        try {
            userId = Long.parseLong(req.getParameter(editUserIdParam));
        }
        catch (NumberFormatException e) {
            PageHelper.setRespMessagePage(resp, pageGenerator, getServletContext(),
                    "Edit User",
                    "Error! Wrong user id!",
                    "/users",
                    "Back");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String username = req.getParameter(editUsernameParam);
        String usermail = req.getParameter(editUsermailParam);
        boolean enabled = req.getParameter(editEnabledParam) != null;

        EditUserOutDTO result = accountService.doEditUser(new EditUserInDTO(userId, username, usermail, enabled));

        switch (result.result) {
            case EDIT_FAILED:
                req.setAttribute(errorReasonAttr, result.responseMsg);
                req.setAttribute(reqStatusAttr, result.servletCode);
                doGet(req, resp);
                return;
            case OK:
                PageHelper.setRespMessagePage(resp, pageGenerator, getServletContext(),
                        "Edit User",
                        result.responseMsg,
                        "/users",
                        "Back");
                resp.setStatus(HttpServletResponse.SC_OK);
                break;
        }
    }
}
