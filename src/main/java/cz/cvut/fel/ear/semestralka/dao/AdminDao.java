package cz.cvut.fel.ear.semestralka.dao;

import cz.cvut.fel.ear.semestralka.model.Admin;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class AdminDao extends BaseDao<Admin> {
    public AdminDao() {super(Admin.class);}

    public Admin findByEmailCriteria(String email) {
        Objects.requireNonNull(email, "Email must not be null");
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Admin> cq = cb.createQuery(Admin.class);
            Root<Admin> root = cq.from(Admin.class);
            Predicate emailCondition = cb.equal(root.get("email"), email);
            cq.select(root).where(emailCondition);
            return em.createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }
}
