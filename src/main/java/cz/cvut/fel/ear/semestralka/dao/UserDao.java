package cz.cvut.fel.ear.semestralka.dao;

import cz.cvut.fel.ear.semestralka.model.User;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class UserDao extends BaseDao<User> {
    public UserDao() {super(User.class);}

    public boolean existsByEmail(String email) {
        Objects.requireNonNull(email, "Email must not be null");
        TypedQuery<Long> query = em.createNamedQuery("User.countByEmail", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    public User findByEmail(String email) {
        Objects.requireNonNull(email, "Email must not be null");
        TypedQuery<User> query = em.createNamedQuery("User.findByEmail", User.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
