package cz.cvut.fel.ear.semestralka.rest;

import cz.cvut.fel.ear.semestralka.dto.MembershipDto;
import cz.cvut.fel.ear.semestralka.dto.PaymentDto;
import cz.cvut.fel.ear.semestralka.dto.ReservationDto;
import cz.cvut.fel.ear.semestralka.dto.UserDto;
import cz.cvut.fel.ear.semestralka.exception.UserNotFoundException;
import cz.cvut.fel.ear.semestralka.service.MembershipService;
import cz.cvut.fel.ear.semestralka.service.PaymentService;
import cz.cvut.fel.ear.semestralka.service.ReservationService;
import cz.cvut.fel.ear.semestralka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ReservationService reservationService;
    private final MembershipService membershipService;
    private final PaymentService paymentService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile() {
       UserDto user = getUser();
       return ResponseEntity.ok(user);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateMyPhoneNumber(@RequestBody UserDto userDto) {
        UserDto user = getUser();
        user.setPhoneNumber(userDto.getPhoneNumber());
        UserDto updated = userService.updateUser(user);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/me/reservations")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        UserDto user = getUser();
        List<ReservationDto> reservations = reservationService.getReservationsByUserId(user.getId());
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/me/membership")
    public ResponseEntity<MembershipDto> getMembership() {
        UserDto user = getUser();
        MembershipDto userMembership = membershipService.getMembershipByUserId(user.getId());
        return ResponseEntity.ok(userMembership);
    }

    @GetMapping("/me/payments")
    public ResponseEntity<List<PaymentDto>> getPayments() {
        UserDto user = getUser();
        List<PaymentDto> paymentDtos = paymentService.getPaymentsForUser(user.getId());
        return ResponseEntity.ok(paymentDtos);
    }

    public UserDto getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserDto user = userService.getUserByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User not found: " + email);
        }
        return user;
    }
}
