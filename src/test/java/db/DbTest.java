package db;

import dao.IUsersDAO;
import dao.UsersDAO;
import models.user.User;
import models.helper.RoleSet;
import org.flywaydb.core.Flyway;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.HibernateSessionFactoryUtil;

import static org.junit.Assert.*;

public class DbTest {

    @BeforeClass
    public static void setUp() {
        SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();

        Flyway flyway = Flyway.configure().dataSource((String) sessionFactory.getProperties().get("connection.url"),
                (String) sessionFactory.getProperties().get("connection.username"),
                (String) sessionFactory.getProperties().get("connection.password")).baselineOnMigrate(true).load();
        flyway.migrate();
    }

    @AfterClass
    public static void tearDown() {
        HibernateSessionFactoryUtil.closeSessionFactory();
    }

    @Test
    public void testBasicDb() {
        User user1 = new User("user1", "password", "1@a.com", new RoleSet(false, true));
        User user2 = new User("user2", "password", "2@b.com", new RoleSet(false,true));
        IUsersDAO usersDAO = new UsersDAO();

        // test add
        usersDAO.add(user1);
        usersDAO.add(user2);

        // test non-unique add
        boolean wasException = false;
        try {
            usersDAO.add(user1);
        }
        catch (HibernateException e)
        {
            wasException = true;
        }
        assertTrue(wasException);

        // test get by id
        assertEquals(usersDAO.findUserById(user1.getId()), user1);
        assertEquals(usersDAO.findUserById(user2.getId()), user2);
        assertNotEquals(usersDAO.findUserById(user1.getId()), usersDAO.findUserById(user2.getId()));

        // test get all users
        for (var user: usersDAO.findAllUsers()) {
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

        // test update
        user1.setHashedPassword("new password");
        usersDAO.update(user1);
        assertEquals(user1, usersDAO.findUserById(user1.getId()));

        // test delete
        usersDAO.delete(user1);
        usersDAO.delete(user2);
        assertNull(usersDAO.findUserById(user1.getId()));
        assertNull(usersDAO.findUserById(user2.getId()));

    }
}
