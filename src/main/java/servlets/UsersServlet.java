package servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dto.user.RemoveUserInDTO;
import dto.user.RemoveUserOutDTO;
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
import java.util.List;
import java.util.Map;

@Singleton
public class UsersServlet extends HttpServlet {

    @Inject
    @PageGenerator
    private IPageGenerator pageGenerator;

    @Inject
    @AccountService
    private IAccountService accountService;

    public static final String path = "/users";
    public static final String Name = "UsersServlet";

    private static final String menuVar = "menu";
    private static final String contentVar = "content";

    public static final String userIdVar = "id";
    public static final String commandVar = "command";
    public static final String commandRemoveVar = "remove";
    public static final String commandAddVar = "add";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        User user = accountService.findLoginUserBySession(new HttpSessionStorage(req.getSession()));
        List<User> users = accountService.findAllDBUsers();

        // include menu
        pageVariables.put(menuVar, PageHelper.generateMenuHtml(req, getServletContext(), pageGenerator, user));

        // include content
        pageVariables.put(contentVar, PageHelper.generateUserTableHtml(req, getServletContext(), pageGenerator, users));

        resp.getWriter().println(pageGenerator.getPage(getServletContext(), "users.html", pageVariables));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");

        String command = req.getParameter(commandVar);

        switch (command) {
            case commandAddVar:
                resp.sendRedirect(AddUserServlet.path);
                break;
            case commandRemoveVar:
                String idStr = req.getParameter("id");
                long id;
                try {
                    id = Integer.parseInt(idStr);
                }
                catch (NumberFormatException e)
                {
                    PageHelper.setRespMessagePage(resp, pageGenerator, getServletContext(),
                            "Users",
                            String.format("Invalid user id = %s!", idStr),
                            UsersServlet.path,
                            "Back");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                RemoveUserOutDTO result = accountService.doRemoveUser(new RemoveUserInDTO(id));

                switch (result.result) {
                    case OK:
                        PageHelper.setRespMessagePage(resp, pageGenerator, getServletContext(),
                                "Users",
                                result.responseMsg,
                                UsersServlet.path,
                                "Back");
                        resp.setStatus(result.servletCode);
                        return;
                    default:
                        resp.setStatus(result.servletCode);
                        return;
                }
        }
    }
}
