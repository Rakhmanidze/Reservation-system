package cz.cvut.fel.ear.semestralka.service;

import cz.cvut.fel.ear.semestralka.dao.MembershipDao;
import cz.cvut.fel.ear.semestralka.dao.PaymentDao;
import cz.cvut.fel.ear.semestralka.dao.UserDao;
import cz.cvut.fel.ear.semestralka.dto.PaymentDto;
import cz.cvut.fel.ear.semestralka.exception.MembershipNotFoundException;
import cz.cvut.fel.ear.semestralka.exception.UserNotFoundException;
import cz.cvut.fel.ear.semestralka.model.Membership;
import cz.cvut.fel.ear.semestralka.model.Payment;
import cz.cvut.fel.ear.semestralka.model.User;
import cz.cvut.fel.ear.semestralka.model.enums.MembershipStatus;
import cz.cvut.fel.ear.semestralka.model.enums.TypeOfMembership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentDao paymentDao;

    @Mock
    private MembershipDao membershipDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addPayment_ShouldPersistPaymentAndUpdateMemberships() {
        PaymentDto paymentDto = new PaymentDto(1, 800, LocalDate.now(), List.of(1));
        Membership membership = new Membership();
        membership.setMembershipId(1);
        membership.setPayments(new ArrayList<>());
        membership.setType(TypeOfMembership.BASIC);
        membership.setStatus(MembershipStatus.DEACTIVATED);

        when(membershipDao.find(1)).thenReturn(membership);

        paymentService.addPayment(paymentDto);

        verify(paymentDao).persist(any(Payment.class));
        verify(membershipDao).update(membership);
        assertEquals(TypeOfMembership.ADVANCED, membership.getType());
        assertEquals(MembershipStatus.ACTIVE, membership.getStatus());
    }

    @Test
    void getAllPayments_ShouldReturnListOfPaymentDtos() {
        Payment payment = new Payment(1, 500, LocalDate.now(), Collections.emptyList());
        when(paymentDao.findAll()).thenReturn(List.of(payment));

        List<PaymentDto> result = paymentService.getAllPayments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(500, result.get(0).getAmount());
    }

    @Test
    void getPaymentsForUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userDao.find(1)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> paymentService.getPaymentsForUser(1));
    }

    @Test
    void addPayment_ShouldThrowIllegalArgumentException_WhenAmountIsZero() {
        PaymentDto paymentDto = new PaymentDto(1, 0, LocalDate.now(), List.of(1));

        assertThrows(IllegalArgumentException.class, () -> paymentService.addPayment(paymentDto));
    }

    @Test
    void addPayment_ShouldThrowMembershipNotFoundException_WhenMembershipIsInvalid() {
        PaymentDto paymentDto = new PaymentDto(1, 500, LocalDate.now(), List.of(1));

        when(membershipDao.find(1)).thenReturn(null);
        assertThrows(MembershipNotFoundException.class, () -> paymentService.addPayment(paymentDto));
    }

    @Test
    void checkAndUpdateMemberships_ShouldUpdateMembershipTypeAndStatusBasedOnPayments() {
        Payment payment = new Payment();
        Membership membership = new Membership();
        membership.setMembershipId(1);
        membership.setPayments(new ArrayList<>(List.of(payment)));
        payment.setAmount(800);
        payment.setMemberships(new ArrayList<>(List.of(membership)));

        when(membershipDao.find(1)).thenReturn(membership);

        paymentService.addPayment(new PaymentDto(1, 800, LocalDate.now(), List.of(1)));

        assertEquals(TypeOfMembership.ADVANCED, membership.getType());
        assertEquals(MembershipStatus.ACTIVE, membership.getStatus());
    }
}
