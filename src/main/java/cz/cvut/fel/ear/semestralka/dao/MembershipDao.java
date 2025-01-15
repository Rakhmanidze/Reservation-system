package cz.cvut.fel.ear.semestralka.dao;

import cz.cvut.fel.ear.semestralka.model.Membership;
import cz.cvut.fel.ear.semestralka.model.enums.MembershipStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MembershipDao extends BaseDao<Membership> {
    public MembershipDao() {super(Membership.class);}

    public List<Membership> findAllActive(MembershipStatus status) {
        return em.createNamedQuery("Membership.findAllActive", Membership.class)
                .setParameter("status", status)
                .getResultList();
    }
}
