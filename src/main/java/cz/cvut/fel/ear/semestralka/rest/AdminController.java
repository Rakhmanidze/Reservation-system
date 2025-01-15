package cz.cvut.fel.ear.semestralka.rest;

import cz.cvut.fel.ear.semestralka.dto.AdminDto;
import cz.cvut.fel.ear.semestralka.dto.MembershipDto;
import cz.cvut.fel.ear.semestralka.dto.PaymentDto;
import cz.cvut.fel.ear.semestralka.dto.UserDto;
import cz.cvut.fel.ear.semestralka.service.AdminService;
import cz.cvut.fel.ear.semestralka.service.PaymentService;
import cz.cvut.fel.ear.semestralka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final PaymentService paymentService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping
    public ResponseEntity<List<AdminDto>> getAllAdmins() {
        List<AdminDto> adminDtos = adminService.getAllAdmins();
        return ResponseEntity.ok(adminDtos);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        adminService.addUser(userDto);
        return ResponseEntity.status(201).build();
    }

    @PostMapping
    public ResponseEntity<UserDto> createAdmin(@RequestBody AdminDto adminSignupRequestDto) {
        adminService.addAdmin(adminSignupRequestDto);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable Integer id) {
        AdminDto adminDto = adminService.getAdminById(id);
        return ResponseEntity.ok(adminDto);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        UserDto updatedUser = userService.updateUser(userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("users/reservation/{id}")
    public ResponseEntity<Void> removeReservation(@PathVariable Integer id) {
        adminService.removeUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/membership")
    public ResponseEntity<MembershipDto> getMembership(@PathVariable("userId") Integer userId) {
        MembershipDto membershipDto = adminService.getUserMembership(userId);
        if (membershipDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(membershipDto);
    }

    @GetMapping("/users/{userId}/membership/payments")
    public ResponseEntity<List<PaymentDto>> getMembershipPayments(@PathVariable("userId") Integer userId) {
        MembershipDto membershipDto = adminService.getUserMembership(userId);
        if (membershipDto == null) {
            return ResponseEntity.notFound().build();
        }
        List<PaymentDto> paymentDtos = paymentService.getPaymentsForUser(userId);
        return ResponseEntity.ok(paymentDtos);
    }

    @PutMapping("/users/{userId}/membership/{membershipId}")
    public ResponseEntity<MembershipDto> updateUserMembership(@PathVariable("userId") Integer userId,
                                                              @PathVariable("membershipId") Integer membershipId,
                                                              @RequestBody MembershipDto membershipDto) {
        adminService.updateUserMembership(membershipId, membershipDto);
        return ResponseEntity.ok(membershipDto);
    }
}
