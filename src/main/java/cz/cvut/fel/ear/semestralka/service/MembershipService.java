package cz.cvut.fel.ear.semestralka.service;

import cz.cvut.fel.ear.semestralka.dao.MembershipDao;
import cz.cvut.fel.ear.semestralka.dao.UserDao;
import cz.cvut.fel.ear.semestralka.dto.MembershipDto;
import cz.cvut.fel.ear.semestralka.dto.UserDto;
import cz.cvut.fel.ear.semestralka.exception.MembershipNotFoundException;
import cz.cvut.fel.ear.semestralka.exception.UserNotFoundException;
import cz.cvut.fel.ear.semestralka.model.Membership;
import cz.cvut.fel.ear.semestralka.model.User;
import cz.cvut.fel.ear.semestralka.model.enums.MembershipStatus;
import cz.cvut.fel.ear.semestralka.model.enums.TypeOfMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {
    private final MembershipDao membershipDao;
    private final UserDao userDao;

    @Transactional
    public void createMembership(Integer userId) {
        User user = userDao.find(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        if (user.getMembership() != null) {
            throw new IllegalStateException("User already has a membership.");
        }

        Membership membership = mapToEntity();

        membership.setUser(user);
        user.setMembership(membership);
        membershipDao.persist(membership);
    }

    @Transactional
    public void updateMembership(Integer membershipId, MembershipDto membershipDto) {
        Membership membership = membershipDao.find(membershipId);
        if (membership == null) {
            throw new MembershipNotFoundException("Membership not found with ID: " + membershipId);
        }
        if (membershipDto.getType() != null) {
            membership.setType(membershipDto.getType());
        }

        if (membershipDto.getStatus() != null) {
            membership.setStatus(membershipDto.getStatus());
        }

        if (membershipDto.getStart() != null) {
            membership.setStart(membershipDto.getStart());
        }

        if (membershipDto.getEnd() != null) {
            membership.setEnd(membershipDto.getEnd());
        }

        membershipDao.update(membership);
    }

    @Transactional(readOnly = true)
    public MembershipDto getMembershipByUserId(Integer userId) {
        User user = userDao.find(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        Membership membership = user.getMembership();
        if (membership == null) {
           throw new MembershipNotFoundException("Membership not found with ID: " + userId);
        }
        return mapToDto(membership);
    }

    @Transactional
    public void updateExpiredMemberships() {
        LocalDate today = LocalDate.now();
        List<Membership> memberships = membershipDao.findAllActive(MembershipStatus.ACTIVE);

        memberships.forEach(membership -> {
            if (membership.getEnd().isBefore(today)) {
                membership.setStatus(MembershipStatus.DEACTIVATED);
                membershipDao.update(membership);
            }
        });
    }

    private Membership mapToEntity() {
        Membership membership = new Membership();
        membership.setType(TypeOfMembership.NONE);
        membership.setStatus(MembershipStatus.DEACTIVATED);
        membership.setStart(LocalDate.now());
        membership.setEnd(LocalDate.now().plusYears(1));
        return membership;
    }


    private MembershipDto mapToDto(Membership membership) {
        if (membership == null) {
            return null;
        }
        return MembershipDto.builder()
                .membershipId(membership.getMembershipId())
                .type(membership.getType())
                .status(membership.getStatus())
                .start(membership.getStart())
                .end(membership.getEnd())
                .build();
    }

}