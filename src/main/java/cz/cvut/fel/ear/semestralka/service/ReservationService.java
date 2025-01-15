package cz.cvut.fel.ear.semestralka.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cz.cvut.fel.ear.semestralka.dao.FacilityDao;
import cz.cvut.fel.ear.semestralka.dao.ReservationDao;
import cz.cvut.fel.ear.semestralka.dao.UserDao;
import cz.cvut.fel.ear.semestralka.dto.ReservationDto;
import cz.cvut.fel.ear.semestralka.exception.*;
import cz.cvut.fel.ear.semestralka.model.Facility;
import cz.cvut.fel.ear.semestralka.model.Membership;
import cz.cvut.fel.ear.semestralka.model.Reservation;
import cz.cvut.fel.ear.semestralka.model.User;
import cz.cvut.fel.ear.semestralka.model.enums.MembershipStatus;
import cz.cvut.fel.ear.semestralka.model.enums.ReservationStatus;
import cz.cvut.fel.ear.semestralka.model.enums.TypeOfMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationDao reservationDao;
    private final FacilityDao facilityDao;
    private final UserDao userDao;
    boolean isUpdate;

    @Transactional
    public ReservationDto addReservation(ReservationDto reservationDto) {
        isUpdate = false;
        validateReservation(reservationDto, isUpdate);

        Reservation reservation = mapToEntity(reservationDto);
        reservationDao.persist(reservation);

        return mapToDto(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationDto getReservationById(int reservationId) {
        Reservation reservation = reservationDao.find(reservationId);
        if (reservation == null) {
            throw new InvalidReservationException("Reservation not found with ID: " + reservationId);
        }
        return mapToDto(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUserId(int userId) {
        List<Reservation> reservations = reservationDao.findActiveReservationsByUserId(userId);
        return reservations.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        List<Reservation> reservations = reservationDao.findAll();
        return reservations.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public ReservationDto updateReservation(ReservationDto reservationDto) {
        isUpdate = true;
        Reservation existingReservation = reservationDao.find(reservationDto.getId());
        if (existingReservation == null) {
            throw new InvalidReservationException("Reservation not found with ID: " + reservationDto.getId());
        }

        Facility facility = facilityDao.find(reservationDto.getFacilityId());
        if (facility == null) {
            throw new FacilityException("Facility not found with ID: " + reservationDto.getFacilityId());
        }

        reservationDto.setUserId(existingReservation.getMembership().getUser().getUserId());

        validateReservation(reservationDto, isUpdate);

        existingReservation.setStart(reservationDto.getStart());
        existingReservation.setEnd(reservationDto.getEnd());
        existingReservation.setFacility(facility);
        existingReservation.setCapacity(reservationDto.getCapacity());

        reservationDao.update(existingReservation);
        return mapToDto(existingReservation);

    }

    @Transactional
    public void removeReservation(int reservationId) {
        Reservation reservation = reservationDao.find(reservationId);
        if (reservation == null) {
            throw new InvalidReservationException("Reservation not found with ID: " + reservationId);
        }
        reservationDao.remove(reservation);
    }

    @Transactional
    public void updateExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> expiredReservations = reservationDao.findActiveExpiredReservations(ReservationStatus.ACTIVE, now);

        for (Reservation reservation : expiredReservations) {
            reservation.setStatus(ReservationStatus.INACTIVE);
            reservationDao.update(reservation);
        }
    }

    private void validateReservation(ReservationDto reservationDto, boolean isUpdate) {
        User user = userDao.find(reservationDto.getUserId());
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + reservationDto.getUserId());
        }

        if(!user.getMembership().getStatus().equals(MembershipStatus.ACTIVE)){
            throw new InvalidReservationException("User does not have an active membership.");
        }

        Facility facility = facilityDao.find(reservationDto.getFacilityId());
        if (facility == null) {
            throw new FacilityException("Facility not found with ID: " + reservationDto.getFacilityId());
        }

        Membership membership = user.getMembership();
        if (membership == null || membership.getType() == null) {
            throw new InvalidReservationException("User does not have a valid membership.");
        }

        TypeOfMembership requiredMembershipType = facility.getRequiredMembershipType();
        if (requiredMembershipType == null) {
            throw new InvalidReservationException("Facility does not specify a required membership type.");
        }

        if (!membership.getType().canAccess(requiredMembershipType)) {
            throw new InvalidReservationException("User's membership level does not allow access to this facility.");
        }

        LocalDateTime start = reservationDto.getStart();
        LocalDateTime end = reservationDto.getEnd();
        int capacity = reservationDto.getCapacity();

        if (start == null || end == null) {
            throw new InvalidReservationException("Start and end times must be provided.");
        }

        if (!start.isBefore(end)) {
            throw new InvalidReservationException("Start time must be before end time.");
        }

        if (Duration.between(start, end).toHours() > 8) {
            throw new InvalidReservationException("Reservation duration cannot exceed 8 hours.");
        }

        if (start.isAfter(LocalDateTime.now().plusDays(7))) {
            throw new InvalidReservationException("Reservations can only be made up to 7 days in advance.");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new InvalidReservationException("Start time cannot be in the past.");
        }

        List<Reservation> userReservations = reservationDao.findActiveReservationsByUserId(user.getUserId());
        if (userReservations.size() >= 2 && !isUpdate) {
            throw new MaxReservationsExceededException("User cannot have more than 2 active reservations.");
        }

        Reservation existingReservation = reservationDto.getId() != null ? reservationDao.find(reservationDto.getId()) : null;

        List<Reservation> conflictingReservations = reservationDao.findReservationsByFacilityAndDateRange(
                reservationDto.getFacilityId(), start, end);

        if (existingReservation != null) {
            conflictingReservations.removeIf(reservation -> Objects.equals(reservation.getReservationId(), existingReservation.getReservationId()));
        }

        if (!conflictingReservations.isEmpty()) {
            throw new FacilityException("Facility is already reserved for the selected time range.");
        }

        if (capacity > facility.getCapacity()) {
            throw new InvalidReservationException("Capacity is less than reservation capacity");
        }

        if (!facility.isAvailable()) {
            throw new FacilityException("Facility is not available with ID: " + reservationDto.getFacilityId());
        }
    }

    private ReservationDto mapToDto(Reservation reservation) {
        return ReservationDto.builder()
                .id(reservation.getReservationId())
                .start(reservation.getStart())
                .end(reservation.getEnd())
                .capacity(reservation.getCapacity())
                .facilityId(reservation.getFacility().getFacilityId())
                .userId(reservation.getMembership().getUser().getUserId())
                .status(reservation.getStatus())
                .build();
    }

    private Reservation mapToEntity(ReservationDto reservationDto) {
        Facility facility = facilityDao.find(reservationDto.getFacilityId());
        User user = userDao.find(reservationDto.getUserId());
        ReservationStatus status = reservationDto.getStatus() != null ? reservationDto.getStatus() : ReservationStatus.ACTIVE;

        return Reservation.builder()
                .start(reservationDto.getStart())
                .end(reservationDto.getEnd())
                .capacity(reservationDto.getCapacity())
                .status(status)
                .facility(facility)
                .membership(user.getMembership())
                .build();
    }
}