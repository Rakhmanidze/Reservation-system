package cz.cvut.fel.ear.semestralka.dao;

import cz.cvut.fel.ear.semestralka.model.Payment;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDao extends BaseDao<Payment> {
    public PaymentDao() {super(Payment.class);}
}
