package filter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import guice.AccountService;
import models.user.User;
import service.IAccountService;
import servlets.*;
import utils.sessionstorage.HttpSessionStorage;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class SecurityFilter implements Filter {

    private FilterConfig filterConfig;

    @Inject
    @AccountService
    private IAccountService accountService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // TODO: move list of pages into a separate class
        ArrayList<String> loginReqPages = new ArrayList<>(List.of(ProfileServlet.path, UsersServlet.path, AddUserServlet.path, EditUserServlet.path));
        ArrayList<String> roleSuperAdminReqPages = new ArrayList<>(List.of(UsersServlet.path, AddUserServlet.path, EditUserServlet.path));

        String servletPath = req.getServletPath();

        if (loginReqPages.contains(servletPath)) {
            User user = accountService.findLoginUserBySession(new HttpSessionStorage(req.getSession()));

            if (user == null) {
                resp.sendRedirect(LoginServlet.path);
                return;
            }

            if (roleSuperAdminReqPages.contains(servletPath) && !user.getRoleSet().isSuperAdminRole()) {
                resp.sendRedirect("/pagenotfound");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        filterConfig = null;
    }
}
