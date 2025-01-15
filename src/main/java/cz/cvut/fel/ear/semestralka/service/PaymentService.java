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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentDao paymentDao;
    private final MembershipDao membershipDao;
    private final UserDao userDao;

    @Transactional
    public void addPayment(PaymentDto paymentDto) {
        validatePaymentDto(paymentDto);
        Payment payment = mapToEntity(paymentDto);
        paymentDao.persist(payment);
        checkAndUpdateMemberships(payment);
    }

    @Transactional
    public List<PaymentDto> getAllPayments() {
        List<Payment> payments = paymentDao.findAll();
        return payments.stream()
                .map(this::mapToDto)
                .toList();
    }

    private Membership findMembershipById(Integer id) {
        Membership membership =  membershipDao.find(id);
        if (membership == null) {
            throw new MembershipNotFoundException("Membership not found with ID: " + id);
        }
        return membership;
    }

    private void validatePaymentDto(PaymentDto paymentDto) {
        if (paymentDto.getAmount() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        if (paymentDto.getDate() == null) {
            throw new IllegalArgumentException("Payment date cannot be null.");
        }
        if (paymentDto.getMembershipIds() == null || paymentDto.getMembershipIds().isEmpty()) {
            throw new IllegalArgumentException("Membership IDs must be provided and cannot be empty.");
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsForUser(Integer userId) {
        User user = userDao.find(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        Membership membership = user.getMembership();
        if (membership == null) {
            return Collections.emptyList();
        }

        List<Payment> payments = membership.getPayments();
        if (payments == null || payments.isEmpty()) {
            return Collections.emptyList();
        }

        return payments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private void checkAndUpdateMemberships(Payment payment) {
        for (Membership membership : payment.getMemberships()) {
            double totalPayments = membership.getPayments().stream()
                    .mapToDouble(Payment::getAmount)
                    .sum();
            if (totalPayments >= 800) {
                membership.setType(TypeOfMembership.ADVANCED);
                membership.setStatus(MembershipStatus.ACTIVE);
            } else if (totalPayments >= 500) {
                membership.setType(TypeOfMembership.BASIC);
                membership.setStatus(MembershipStatus.ACTIVE);
            } else {
                continue;
            }
            membershipDao.update(membership);
        }
    }

    public PaymentDto mapToDto(Payment payment) {
        List<Integer> membershipIds = payment.getMemberships().stream()
                .map(Membership::getMembershipId)
                .toList();

        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .date(payment.getDate())
                .membershipIds(membershipIds)
                .build();
    }

    public Payment mapToEntity(PaymentDto paymentDto) {
        List<Membership> memberships = Optional.ofNullable(paymentDto.getMembershipIds())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::findMembershipById)
                .collect(Collectors.toList());
        paymentDto.setDate(LocalDate.now());
        Payment payment = Payment.builder()
                .amount(paymentDto.getAmount())
                .date(paymentDto.getDate())
                .memberships(memberships)
                .build();
        memberships.forEach(membership -> membership.getPayments().add(payment));

        return payment;
    }
}