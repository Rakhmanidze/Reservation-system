package cz.cvut.fel.ear.semestralka.service;

import cz.cvut.fel.ear.semestralka.dao.AdminDao;
import cz.cvut.fel.ear.semestralka.dao.MembershipDao;
import cz.cvut.fel.ear.semestralka.dao.ReservationDao;
import cz.cvut.fel.ear.semestralka.dao.UserDao;
import cz.cvut.fel.ear.semestralka.dto.AdminDto;
import cz.cvut.fel.ear.semestralka.dto.MembershipDto;
import cz.cvut.fel.ear.semestralka.dto.UserDto;
import cz.cvut.fel.ear.semestralka.exception.AdminNotFoundException;
import cz.cvut.fel.ear.semestralka.exception.UserNotFoundException;
import cz.cvut.fel.ear.semestralka.model.Admin;
import cz.cvut.fel.ear.semestralka.model.Reservation;
import cz.cvut.fel.ear.semestralka.model.Membership;
import cz.cvut.fel.ear.semestralka.model.User;
import cz.cvut.fel.ear.semestralka.model.enums.MembershipStatus;
import cz.cvut.fel.ear.semestralka.model.enums.TypeOfMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminDao adminDao;
    private final ReservationDao reservationDao;
    private final MembershipDao membershipDao;
    private final UserService userService;
    private final MembershipService membershipService;
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void addUser(UserDto userDto) {
        User user = userService.userToEntity(userDto);

        userService.validateUserForRegistration(user);
        if (user.getRoomNumber() == null){
            throw new IllegalArgumentException("Room number is required.");
        }
        userDao.persist(user);
        membershipService.createMembership(user.getUserId());
    }

    @Transactional
    public void addAdmin(AdminDto adminSignupRequestDto) {
        if (adminSignupRequestDto.getRoomNumber() == null) {
            adminSignupRequestDto.setRoomNumber("NONE");
        }
        Admin admin = mapToEntity(adminSignupRequestDto);
        userService.validateUserForRegistration(admin);
        adminDao.persist(admin);
        membershipService.createMembership(admin.getUserId());
    }

    @Transactional
    public void removeUser(Integer userId) {
        User user = userDao.find(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        Membership membership = user.getMembership();
        if (membership != null) {
            List<Reservation> reservations = membership.getReservations();
            if (reservations != null && !reservations.isEmpty()) {
                for (Reservation reservation : reservations) {
                    reservationDao.remove(reservation);
                }
            }
            user.setMembership(null);
            membership.setUser(null);
            membershipDao.remove(membership);
        }
        userDao.remove(user);
    }

    @Transactional(readOnly = true)
    public AdminDto getAdminById(Integer adminId) {
        String email = adminDao.find(adminId).getEmail();
        Admin admin = adminDao.findByEmailCriteria(email);
        if (admin == null) {
            throw new AdminNotFoundException("Admin not found with ID: " + adminId);
        }
        return mapToDto(admin);
    }

    @Transactional(readOnly = true)
    public List<AdminDto> getAllAdmins() {
        List<Admin> admins = adminDao.findAll();
        return admins.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public void updateUserMembership(Integer membershipId, MembershipDto membershipDto) {
        membershipService.updateMembership(membershipId, membershipDto);
    }

    @Transactional
    public MembershipDto getUserMembership(Integer id){
        return membershipService.getMembershipByUserId(id);
    }


    private AdminDto mapToDto(Admin admin) {
        return AdminDto.builder()
                .id(admin.getUserId())
                .email(admin.getEmail())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .roomNumber(admin.getRoomNumber())
                .phoneNumber(admin.getPhoneNumber())
                .build();
    }

    private Admin mapToEntity(AdminDto adminSignupRequestDto) {
        return Admin.builder()
                .email(adminSignupRequestDto.getEmail())
                .firstName(adminSignupRequestDto.getFirstName())
                .lastName(adminSignupRequestDto.getLastName())
                .roomNumber(adminSignupRequestDto.getRoomNumber())
                .phoneNumber(adminSignupRequestDto.getPhoneNumber())
                .password(passwordEncoder.encode(adminSignupRequestDto.getPassword()))
                .build();
    }
}