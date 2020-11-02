package servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dto.user.ConfirmRegInDTO;
import dto.user.ConfirmRegOutDTO;
import guice.AccountService;
import guice.EmailService;
import guice.PageGenerator;
import service.IAccountService;
import service.IEmailService;
import templater.IPageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class ConfirmAccountServlet extends HttpServlet {

    @Inject
    @PageGenerator
    private IPageGenerator pageGenerator;

    @Inject
    @AccountService
    private IAccountService accountService;

    @Inject
    @EmailService
    private IEmailService emailService;

    public static final String path = "/confirm-account";

    public static final String tokenParam = "token";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String tokenStr = req.getParameter(tokenParam);

        ConfirmRegOutDTO result = accountService.doConfirmRegisterUser(new ConfirmRegInDTO(tokenStr,emailService,req.getHeader("Host")));

        switch (result.result) {
            case REGISTER_FAILED:
                PageHelper.setRespMessagePage(resp, pageGenerator, getServletContext(),
                        "Registration",
                        result.responseMsg,
                        "/index",
                        "Back");

                resp.setStatus(result.servletCode);
                return;
            case OK:
                PageHelper.setRespMessagePage(resp, pageGenerator, getServletContext(),
                        "Registration",
                        result.responseMsg,
                        "/login",
                        "Go to Login");
                resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
