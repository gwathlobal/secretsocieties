package servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dto.user.LogoutUserInDTO;
import dto.user.LogoutUserOutDTO;
import guice.AccountService;
import guice.PageGenerator;
import service.IAccountService;
import templater.IPageGenerator;
import utils.sessionstorage.HttpSessionStorage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class LogoutServlet extends HttpServlet {

    @Inject
    @PageGenerator
    private IPageGenerator pageGenerator;

    @Inject
    @AccountService
    private IAccountService accountService;

    public static final String path = "/logout";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        LogoutUserOutDTO result = accountService.doLogoutUser(new LogoutUserInDTO(new HttpSessionStorage(req.getSession())));

        if (result.result == LogoutUserOutDTO.ResultEnum.OK) {
            PageHelper.setRespMessagePage(resp, pageGenerator, getServletContext(),
                    "Logout",
                    String.format("Logout successful, %s!", result.userLogin),
                    "/index",
                    "Back");
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else
            resp.sendRedirect("/");
    }

}
