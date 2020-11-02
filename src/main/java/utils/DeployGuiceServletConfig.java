package utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import dao.IConfirmationTokenDAO;
import dao.IUsersDAO;
import filter.LogFilter;
import filter.SecurityFilter;
import guice.*;
import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import service.IAccountService;
import service.IEmailService;
import servlets.*;
import templater.IPageGenerator;

public class DeployGuiceServletConfig extends GuiceServletContextListener {

    public static class DeployModule extends ServletModule {

        @Override
        protected void configureServlets() {
            bind(IUsersDAO.class).annotatedWith(UsersDAO.class).to(dao.UsersDAO.class);
            bind(IConfirmationTokenDAO.class).annotatedWith(ConfirmationTokenDAO.class).to(dao.ConfirmationTokenDAO.class);
            bind(IAccountService.class).annotatedWith(AccountService.class).to(service.AccountService.class).in(Singleton.class);
            bind(IPageGenerator.class).annotatedWith(PageGenerator.class).to(templater.FreeMarkerGenerator.class).in(Singleton.class);
            bind(IEmailService.class).annotatedWith(EmailService.class).to(service.EmailService.class).in(Singleton.class);

            serve(IndexServlet.path).with(IndexServlet.class);
            serve(IndexServlet.path2).with(IndexServlet.class);
            serve(LoginServlet.path).with(LoginServlet.class);
            serve(RegisterServlet.path).with(RegisterServlet.class);
            serve(LogoutServlet.path).with(LogoutServlet.class);
            serve(ConfirmAccountServlet.path).with(ConfirmAccountServlet.class);
            serve(ProfileServlet.path).with(ProfileServlet.class);
            serve(UsersServlet.path).with(UsersServlet.class);
            serve(AddUserServlet.path).with(AddUserServlet.class);
            serve(EditUserServlet.path).with(EditUserServlet.class);

            filter("/*").through(LogFilter.class);
            filter("/*").through(SecurityFilter.class);

            SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();

            Flyway flyway = Flyway.configure().dataSource((String) sessionFactory.getProperties().get("connection.url"),
                    (String) sessionFactory.getProperties().get("connection.username"),
                    (String) sessionFactory.getProperties().get("connection.password")).baselineOnMigrate(true).load();
            flyway.migrate();
        }
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new DeployModule());
    }
}