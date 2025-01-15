package cz.cvut.fel.ear.semestralka.service;

import cz.cvut.fel.ear.semestralka.dao.MembershipDao;
import cz.cvut.fel.ear.semestralka.dao.UserDao;
import cz.cvut.fel.ear.semestralka.dto.MembershipDto;
import cz.cvut.fel.ear.semestralka.exception.MembershipNotFoundException;
import cz.cvut.fel.ear.semestralka.exception.UserNotFoundException;
import cz.cvut.fel.ear.semestralka.model.Membership;
import cz.cvut.fel.ear.semestralka.model.User;
import cz.cvut.fel.ear.semestralka.model.enums.MembershipStatus;
import cz.cvut.fel.ear.semestralka.model.enums.TypeOfMembership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

    @Mock
    private MembershipDao membershipDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private MembershipService membershipService;

    private User user;
    private Membership membership;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");

        membership = new Membership();
        membership.setMembershipId(1);
        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setType(TypeOfMembership.BASIC);
        membership.setStart(LocalDate.now());
        membership.setEnd(LocalDate.now().plusYears(1));
        user.setMembership(membership);
    }

    @Test
    void createMembership_ShouldThrowException_IfUserNotFound() {
        when(userDao.find(1)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> membershipService.createMembership(1));
    }

    @Test
    void createMembership_ShouldCreateMembership_IfUserExistsWithoutMembership() {
        when(userDao.find(1)).thenReturn(user);
        user.setMembership(null);

        membershipService.createMembership(1);

        verify(membershipDao).persist(any(Membership.class));
    }

    @Test
    void updateMembership_ShouldUpdateMembership_IfMembershipExists() {
        when(membershipDao.find(1)).thenReturn(membership);

        MembershipDto dto = MembershipDto.builder()
                .type(TypeOfMembership.ADVANCED)
                .status(MembershipStatus.DEACTIVATED)
                .start(LocalDate.now()).
                end(LocalDate.now().plusDays(30))
                .build();

        membershipService.updateMembership(1, dto);

        assertEquals(TypeOfMembership.ADVANCED, membership.getType());
        assertEquals(MembershipStatus.DEACTIVATED, membership.getStatus());
        verify(membershipDao).update(membership);
    }

    @Test
    void getMembershipByUserId_ShouldThrowException_IfUserNotFound() {
        when(userDao.find(1)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> membershipService.getMembershipByUserId(1));
    }
}
