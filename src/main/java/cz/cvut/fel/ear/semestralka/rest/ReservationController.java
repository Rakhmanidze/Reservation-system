package cz.cvut.fel.ear.semestralka.rest;

import cz.cvut.fel.ear.semestralka.dto.ReservationDto;
import cz.cvut.fel.ear.semestralka.dto.UserDto;
import cz.cvut.fel.ear.semestralka.exception.InvalidReservationException;
import cz.cvut.fel.ear.semestralka.service.ReservationService;
import cz.cvut.fel.ear.semestralka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationDto reservationDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserDto userDto = userService.getUserByEmail(email);
        reservationDto.setUserId(userDto.getId());

        ReservationDto createdReservation = reservationService.addReservation(reservationDto);
        return ResponseEntity.status(201).body(createdReservation);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ReservationDto> createReservationForUser(@PathVariable Integer id, @RequestBody ReservationDto reservationDto) {
        reservationDto.setUserId(id);
        ReservationDto createdReservation = reservationService.addReservation(reservationDto);
        return ResponseEntity.status(201).body(createdReservation);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Integer id) {
        ReservationDto reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("all")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        List<ReservationDto> reservationDtos = reservationService.getAllReservations();
        return ResponseEntity.ok(reservationDtos);
    }

    @GetMapping("users/{id}")
    public ResponseEntity<List<ReservationDto>> getReservationsByUserId(@PathVariable Integer id) {
        List<ReservationDto> reservationsDto = reservationService.getReservationsByUserId(id);
        return ResponseEntity.ok(reservationsDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable Integer id, @RequestBody ReservationDto reservationDto) {
        reservationDto.setId(id);

        checkUserAuthorization(id);

        ReservationDto updatedReservation = reservationService.updateReservation(reservationDto);
        return ResponseEntity.ok(updatedReservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeReservation(@PathVariable Integer id) {
        checkUserAuthorization(id);

        reservationService.removeReservation(id);
        return ResponseEntity.noContent().build();
    }

    private void checkUserAuthorization(Integer reservationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserDto userDto = userService.getUserByEmail(email);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        ReservationDto reservation = reservationService.getReservationById(reservationId);

        if(!isAdmin && !reservation.getUserId().equals(userDto.getId())) {
            throw new InvalidReservationException("You can only modify or remove your own reservation");
        }
    }
}