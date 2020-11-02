package dao;

import models.user.User;
import org.hibernate.Session;
import utils.HibernateSessionFactoryUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class UsersDAO implements IUsersDAO {

    @Override
    public User findUserById(long id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    @Override
    public User findUserByLogin(String login) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteria = session.getCriteriaBuilder().createQuery(User.class);
            Root<User> root = criteria.from(User.class);
            // TODO: rewrite using JPA metamodel
            criteria.where(builder.equal( root.get("login"), login ) );
            return session.createQuery(criteria).uniqueResult();
        }
    }

    @Override
    public User findUserByEmail(String email) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteria = session.getCriteriaBuilder().createQuery(User.class);
            Root<User> root = criteria.from(User.class);
            // TODO: rewrite using JPA metamodel
            criteria.where(builder.equal( root.get("email"), email ) );
            return session.createQuery(criteria).uniqueResult();
        }
    }

    @Override
    public void add(User user) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(User user) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(User user) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.delete(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<User> findAllUsers() {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaQuery<User> criteria = session.getCriteriaBuilder().createQuery(User.class);
            criteria.from(User.class);
            return session.createQuery(criteria).getResultList();
        }
    }
}
