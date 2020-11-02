package servlets;

import models.user.User;
import templater.IPageGenerator;
import utils.GsonHelper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PageHelper {
    public static final String titleVar = "title";
    public static final String messageVar = "message";
    public static final String backLinkVar = "back_link";
    public static final String backTxtVar = "back_txt";

    private static final String loggedVar = "logged";
    private static final String usernameVar = "username";
    private static final String roleSuperAdminVar = "roleSuperAdmin";

    public static void setRespMessagePage(HttpServletResponse resp, IPageGenerator pageGenerator, ServletContext servletContext,
                                          String title, String message, String backLink, String backTxt) throws IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        pageVariables.put(PageHelper.titleVar, title);
        pageVariables.put(PageHelper.messageVar, message);
        pageVariables.put(PageHelper.backLinkVar, backLink);
        pageVariables.put(PageHelper.backTxtVar, backTxt);

        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().println(pageGenerator.getPage(servletContext,"message.html", pageVariables));
    }

    public static String generateMenuHtml(HttpServletRequest req, ServletContext servletContext, IPageGenerator pageGenerator, User user) {
        Map<String, Object> pageVariables = new HashMap<>();

        if (user != null) {
            pageVariables.put(loggedVar, true);
            pageVariables.put(usernameVar, user.getLogin());
            if (user.getRoleSet().isSuperAdminRole())
                pageVariables.put(roleSuperAdminVar, user.getRoleSet().isSuperAdminRole());
        }
        return pageGenerator.getPage(servletContext, "menu.html", pageVariables);
    }

    public static String generateUserTableHtml(HttpServletRequest req, ServletContext servletContext, IPageGenerator pageGenerator, List<User> users) {
        Map<String, Object> pageVariables = new HashMap<>();

        StringBuffer table = new StringBuffer();

        for (User user : users) {
            table.append("<tr>");

            if (user.getRoleSet().isSuperAdminRole())
                table.append(String.format("<td>%s</td>", user.getId()));
            else
                table.append(String.format("<td><a href=\"/edituser?%s=%s\">%s</a></td>", EditUserServlet.editUserIdParam, user.getId(), user.getId()));

            table.append("<td>");
            table.append(user.getLogin());
            table.append("</td>");

            table.append("<td>");
            table.append((user.getEmail() != null) ? user.getEmail() : "" );
            table.append("</td>");

            table.append("<td>");
            table.append(user.getEnabled());
            table.append("</td>");

            table.append(String.format("<td>%s</td>", GsonHelper.getGson().toJson(user.getRoleSet())));

            if (user.getRoleSet().isSuperAdminRole())
                table.append("<td></td>");
            else
                table.append(String.format("<td><form method=\"post\">" +
                        "<input type=\"hidden\" name=\"%s\" value=\"%s\">" +
                        "<input type=\"hidden\" name=\"%s\" value=\"%s\">" +
                        "<input type=\"submit\" value=\"Remove\" />" +
                        "</form></td>",
                        UsersServlet.commandVar, UsersServlet.commandRemoveVar, UsersServlet.userIdVar, user.getId()));

            table.append("</tr>");
        }

        pageVariables.put("table", table.toString());

        return pageGenerator.getPage(servletContext, "user_table.html", pageVariables);
    }
}
