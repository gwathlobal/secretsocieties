package dao;

import models.user.ConfirmationToken;
import org.hibernate.Session;
import utils.HibernateSessionFactoryUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class ConfirmationTokenDAO implements IConfirmationTokenDAO {
    @Override
    public ConfirmationToken findTokenById(long id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session.get(ConfirmationToken.class, id);
        }
    }

    @Override
    public ConfirmationToken findTokenByTokenStr(String token) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ConfirmationToken> criteria = session.getCriteriaBuilder().createQuery(ConfirmationToken.class);
            Root<ConfirmationToken> root = criteria.from(ConfirmationToken.class);
            // TODO: rewrite using JPA metamodel
            criteria.where(builder.equal( root.get("token"), token ) );
            return session.createQuery(criteria).uniqueResult();
        }
    }

    @Override
    public void add(ConfirmationToken token) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(token);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(ConfirmationToken token) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(token);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(ConfirmationToken token) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.delete(token);
            session.getTransaction().commit();
        }
    }
}
