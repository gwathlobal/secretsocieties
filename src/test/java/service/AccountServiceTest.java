package service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import dao.IConfirmationTokenDAO;
import dao.IUsersDAO;
import dto.user.*;
import guice.ConfirmationTokenDAO;
import guice.UsersDAO;
import models.user.ConfirmationToken;
import models.user.User;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import utils.HibernateSessionFactoryUtil;
import utils.sessionstorage.ISessionStorage;
import utils.sessionstorage.MockSessionStorage;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;

public class AccountServiceTest {

    @Inject
    private IAccountService accountService;

    private static Injector injector;

    @BeforeClass
    public static void initSetUp() {
        SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();

        Flyway flyway = Flyway.configure().dataSource((String) sessionFactory.getProperties().get("connection.url"),
                (String) sessionFactory.getProperties().get("connection.username"),
                (String) sessionFactory.getProperties().get("connection.password")).baselineOnMigrate(true).load();
        flyway.migrate();

        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IUsersDAO.class).annotatedWith(UsersDAO.class).to(dao.UsersDAO.class);
                bind(IConfirmationTokenDAO.class).annotatedWith(ConfirmationTokenDAO.class).to(dao.ConfirmationTokenDAO.class);
                bind(IAccountService.class).annotatedWith(guice.AccountService.class).to(service.AccountService.class).in(Singleton.class);
            }
        });
    }

    @AfterClass
    public static void finalTearDown() {
        HibernateSessionFactoryUtil.closeSessionFactory();
    }

    @Before
    public void setUp()
    {
        accountService = injector.getInstance(AccountService.class);
    }

    @Test
    public void findDBUser() {
        User user = accountService.createUserWithHashedPass("login1", "password", "email1");
        accountService.saveDBUser(user);

        assertEquals(user,accountService.findDBUserById(user.getId()));
    }

    @Test
    public void saveDBUser() {
        User user = accountService.createUserWithHashedPass("login2", "password", "email2");
        accountService.saveDBUser(user);

        assertEquals(user,accountService.findDBUserById(user.getId()));
    }

    @Test
    public void deleteDBUser() {
        User user = accountService.createUserWithHashedPass("login3", "password", "email3");
        accountService.saveDBUser(user);
        accountService.deleteDBUser(user);
        assertNull(accountService.findDBUserById(user.getId()));
    }

    @Test
    public void updateDBUser() {
        User user = accountService.createUserWithHashedPass("login4", "password", "email4");
        accountService.saveDBUser(user);

        user.setHashedPassword(BCrypt.hashpw("new password", BCrypt.gensalt()));
        accountService.updateDBUser(user);
        assertEquals(user.getHashedPassword(), accountService.findDBUserById(user.getId()).getHashedPassword());
    }

    @Test
    public void findAllDBUsers() {
        User user1 = accountService.createUserWithHashedPass("login5", "password", "email5");
        User user2 = accountService.createUserWithHashedPass("login6", "password", "email6");
        accountService.saveDBUser(user1);
        accountService.saveDBUser(user2);

        for (var user: accountService.findAllDBUsers()) {
            if (user.getId() == user1.getId()) {
                assertEquals(user, user1);
            }
            else if (user.getId() == user2.getId()) {
                assertEquals(user, user2);
            }
            else {
                assertFalse(false);
            }
        }
    }

    @Test
    public void doLoginUser() {
        User user = accountService.createUserWithHashedPass("login7", "password", "email7");
        accountService.saveDBUser(user);

        LoginUserOutDTO resultFail1 = accountService.doLoginUser(new LoginUserInDTO("login7", "password", new MockSessionStorage()));

        assertTrue(resultFail1.servletCode == HttpServletResponse.SC_UNAUTHORIZED);

        user.setEnabled(true);
        accountService.updateDBUser(user);

        LoginUserOutDTO resultSuccess = accountService.doLoginUser(new LoginUserInDTO("login7", "password", new MockSessionStorage()));

        assertEquals(resultSuccess.servletCode, HttpServletResponse.SC_OK);

        LoginUserOutDTO resultFail2 = accountService.doLoginUser(new LoginUserInDTO("login8", "password", new MockSessionStorage()));

        assertTrue(resultFail2.servletCode == HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void doLogoutUser()
    {
        User user = accountService.createUserWithHashedPass("login9", "password", "email9");
        user.setEnabled(true);
        accountService.saveDBUser(user);

        ISessionStorage sessionStorage = new MockSessionStorage();
        LogoutUserOutDTO resultFail = accountService.doLogoutUser(new LogoutUserInDTO(sessionStorage));

        assertTrue(resultFail.result == LogoutUserOutDTO.ResultEnum.NO_USER_FOUND);

        LoginUserOutDTO resultSuccess1 = accountService.doLoginUser(new LoginUserInDTO("login9", "password", sessionStorage));

        assertEquals(resultSuccess1.servletCode, HttpServletResponse.SC_OK);

        LogoutUserOutDTO resultSuccess2 = accountService.doLogoutUser(new LogoutUserInDTO(sessionStorage));

        assertTrue(resultSuccess2.result == LogoutUserOutDTO.ResultEnum.OK);
        assertTrue(resultSuccess2.userLogin.equals("login9"));
        assertTrue(accountService.findLoginUserBySession(sessionStorage) == null);

    }

    @Test
    public void doRegisterUser()
    {
        IEmailService emailService = new MockEmailService();
        final String hostname = "localhost";
        RegisterUserOutDTO resultSuccess = accountService.doRegisterUser(new RegisterUserInDTO("login10", "password", "email10", emailService, hostname));
        assertTrue(resultSuccess.servletCode == HttpServletResponse.SC_OK);
        assertTrue(accountService.findDBUserByLogin("login10") != null);

        RegisterUserOutDTO resultFail1 = accountService.doRegisterUser(new RegisterUserInDTO("login10", "password", "email11", emailService, hostname));
        assertTrue(resultFail1.servletCode != HttpServletResponse.SC_OK);

        RegisterUserOutDTO resultFail2 = accountService.doRegisterUser(new RegisterUserInDTO("login11", "password", "email10", emailService, hostname));
        assertTrue(resultFail2.servletCode != HttpServletResponse.SC_OK);

        RegisterUserOutDTO resultFail3 = accountService.doRegisterUser(new RegisterUserInDTO("login11", "password", "", emailService, hostname));
        assertTrue(resultFail3.servletCode != HttpServletResponse.SC_OK);
    }

    @Test
    public void doConfirmRegisterUser()
    {
        IEmailService emailService = new MockEmailService();
        final String hostname = "localhost";
        final String username = "login11";
        RegisterUserOutDTO result = accountService.doRegisterUser(new RegisterUserInDTO(username, "password", "email11", emailService, hostname));
        User user = accountService.findDBUserByLogin(username);

        assertTrue(accountService.findDBUserByLogin(username) != null);

        ConfirmationToken token = accountService.findDBTokenByTokenStr(result.tokenStr);
        assertTrue(user.equals(token.getUser()));

        ConfirmRegOutDTO resultSuccess = accountService.doConfirmRegisterUser(new ConfirmRegInDTO(result.tokenStr,emailService,hostname));
        assertTrue(resultSuccess.result == ConfirmRegOutDTO.ResultEnum.OK);
    }

    @Test
    public void doRemoveUser()
    {
        User user = accountService.createUserWithHashedPass("login12", "password", "email12");
        accountService.saveDBUser(user);

        RemoveUserOutDTO resultSuccess = accountService.doRemoveUser(new RemoveUserInDTO(user.getId()));
        assertTrue(resultSuccess.result == RemoveUserOutDTO.ResultEnum.OK);
        assertNull(accountService.findDBUserByLogin("login12"));

        RemoveUserOutDTO resultFail = accountService.doRemoveUser(new RemoveUserInDTO(user.getId()));
        assertTrue(resultFail.result == RemoveUserOutDTO.ResultEnum.REMOVE_FAILED_NO_USER);
    }

    @Test
    public void doAddUser()
    {
        AddUserOutDTO resultSuccess = accountService.doAddUser(new AddUserInDTO("login13", "password", "email13"));
        assertTrue(resultSuccess.servletCode == HttpServletResponse.SC_OK);
        assertTrue(accountService.findDBUserByLogin("login13") != null);

        AddUserOutDTO resultFail1 = accountService.doAddUser(new AddUserInDTO("login13", "password", "email11"));
        assertTrue(resultFail1.servletCode != HttpServletResponse.SC_OK);

        AddUserOutDTO resultFail2 = accountService.doAddUser(new AddUserInDTO("login11", "password", "email13"));
        assertTrue(resultFail2.servletCode != HttpServletResponse.SC_OK);

        AddUserOutDTO resultFail3 = accountService.doAddUser(new AddUserInDTO("login11", "password", ""));
        assertTrue(resultFail3.servletCode != HttpServletResponse.SC_OK);
    }

    @Test
    public void doEditUser()
    {
        final String oldUsername = "login14";
        final String oldUsermail = "email14";
        AddUserOutDTO addSuccess = accountService.doAddUser(new AddUserInDTO(oldUsername, "password", oldUsermail));
        assertTrue(addSuccess.servletCode == HttpServletResponse.SC_OK);
        assertTrue(accountService.findDBUserByLogin(oldUsername) != null);

        long userId = accountService.findDBUserByLogin(oldUsername).getId();
        final String newUsername = "login15";
        final String newUsermail = "email15";
        EditUserOutDTO editSuccess1 = accountService.doEditUser(new EditUserInDTO(userId, oldUsername, oldUsermail, false));
        assertTrue(editSuccess1.result == EditUserOutDTO.ResultEnum.OK);

        EditUserOutDTO editSuccess2 = accountService.doEditUser(new EditUserInDTO(userId, newUsername, oldUsermail, false));
        assertTrue(editSuccess2.result == EditUserOutDTO.ResultEnum.OK);

        EditUserOutDTO editSuccess3 = accountService.doEditUser(new EditUserInDTO(userId, newUsername, newUsermail, false));
        assertTrue(editSuccess3.result == EditUserOutDTO.ResultEnum.OK);
        User user = accountService.findDBUserById(userId);
        assertTrue(user.getLogin().equals(newUsername));
        assertTrue(user.getEmail().equals(newUsermail));
        assertTrue(!user.getEnabled());
    }

}