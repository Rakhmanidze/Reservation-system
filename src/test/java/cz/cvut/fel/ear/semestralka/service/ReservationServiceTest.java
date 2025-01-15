package cz.cvut.fel.ear.semestralka.service;

import cz.cvut.fel.ear.semestralka.dao.FacilityDao;
import cz.cvut.fel.ear.semestralka.dao.ReservationDao;
import cz.cvut.fel.ear.semestralka.dao.UserDao;
import cz.cvut.fel.ear.semestralka.dto.ReservationDto;
import cz.cvut.fel.ear.semestralka.exception.FacilityException;
import cz.cvut.fel.ear.semestralka.exception.InvalidReservationException;
import cz.cvut.fel.ear.semestralka.exception.MaxReservationsExceededException;
import cz.cvut.fel.ear.semestralka.model.Facility;
import cz.cvut.fel.ear.semestralka.model.Membership;
import cz.cvut.fel.ear.semestralka.model.Reservation;
import cz.cvut.fel.ear.semestralka.model.User;
import cz.cvut.fel.ear.semestralka.model.enums.MembershipStatus;
import cz.cvut.fel.ear.semestralka.model.enums.ReservationStatus;
import cz.cvut.fel.ear.semestralka.model.enums.TypeOfMembership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationDao reservationDao;

    @Mock
    private FacilityDao facilityDao;

    @Mock
    private UserDao userDao;


    @InjectMocks
    private ReservationService reservationService;

    private User user;
    private Facility facility;
    private Membership membership;
    private final int facilityId = 1;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    public void setUp() {
        start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        end = start.plusHours(2);

        user = User.builder()
                .userId(1)
                .email("test@example.com")
                .firstName("Daniil")
                .lastName("Klykau")
                .build();

        membership = new Membership();
        membership.setMembershipId(1);
        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setType(TypeOfMembership.BASIC);
        membership.setUser(user);
        user.setMembership(membership);

        facility = new Facility();
        facility.setFacilityId(facilityId);
        facility.setName("Gym");
        facility.setRequiredMembershipType(TypeOfMembership.BASIC);
        facility.setAvailable(true);
    }

    @Test
    public void testAddReservation_Success() {
        when(userDao.find(user.getUserId())).thenReturn(user);
        when(facilityDao.find(facilityId)).thenReturn(facility);
        when(reservationDao.findActiveReservationsByUserId(user.getUserId())).thenReturn(Collections.emptyList());
        when(reservationDao.findReservationsByFacilityAndDateRange(eq(facilityId), any(), any())).thenReturn(Collections.emptyList());

        ReservationDto reservationDto = ReservationDto.builder()
                .userId(user.getUserId())
                .facilityId(facilityId)
                .start(start)
                .end(end)
                .build();

        assertDoesNotThrow(() -> reservationService.addReservation(reservationDto));

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationDao, times(1)).persist(captor.capture());

        Reservation createdReservation = captor.getValue();
        assertEquals(start, createdReservation.getStart());
        assertEquals(end, createdReservation.getEnd());
        assertEquals(ReservationStatus.ACTIVE, createdReservation.getStatus());
        assertEquals(facility, createdReservation.getFacility());
        assertEquals(membership, createdReservation.getMembership());
    }

    @Test
    public void testAddReservation_InsufficientMembershipLevel() {
        facility.setName("Pool");
        facility.setRequiredMembershipType(TypeOfMembership.ADVANCED);

        when(userDao.find(user.getUserId())).thenReturn(user);
        when(facilityDao.find(facilityId)).thenReturn(facility);

        ReservationDto reservationDto = ReservationDto.builder()
                .userId(user.getUserId())
                .facilityId(facilityId)
                .start(start)
                .end(end)
                .build();

        Exception exception = assertThrows(InvalidReservationException.class, () -> reservationService.addReservation(reservationDto));

        assertEquals("User's membership level does not allow access to this facility.", exception.getMessage());
        verify(reservationDao, never()).persist(any(Reservation.class));
    }

    @Test
    public void testAddReservation_ConflictingReservation() {
        Reservation existingReservation = new Reservation();
        existingReservation.setStart(start);
        existingReservation.setEnd(end);

        when(userDao.find(user.getUserId())).thenReturn(user);
        when(facilityDao.find(facilityId)).thenReturn(facility);
        when(reservationDao.findActiveReservationsByUserId(user.getUserId())).thenReturn(Collections.emptyList());

        when(reservationDao.findReservationsByFacilityAndDateRange(eq(facilityId), any(), any()))
                .thenReturn(Collections.emptyList());

        ReservationDto reservationDto = ReservationDto.builder()
                .userId(user.getUserId())
                .facilityId(facilityId)
                .start(start)
                .end(end)
                .build();

        reservationService.addReservation(reservationDto);

        when(reservationDao.findReservationsByFacilityAndDateRange(eq(facilityId), any(), any()))
                .thenReturn(List.of(existingReservation));

        ReservationDto newReservationDto = ReservationDto.builder()
                .userId(user.getUserId())
                .facilityId(facilityId)
                .start(start.plusHours(1))
                .end(end.plusHours(1))
                .build();

        Exception exception = assertThrows(FacilityException.class, () -> reservationService.addReservation(newReservationDto));
        assertEquals("Facility is already reserved for the selected time range.", exception.getMessage());

        verify(reservationDao, times(1)).persist(any(Reservation.class));
    }

    @Test
    public void testAddReservation_ExceedsActiveReservationsLimit() {
        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();

        when(userDao.find(user.getUserId())).thenReturn(user);
        when(facilityDao.find(facilityId)).thenReturn(facility);
        when(reservationDao.findActiveReservationsByUserId(user.getUserId())).thenReturn(List.of(reservation1, reservation2));

        ReservationDto reservationDto = ReservationDto.builder()
                .userId(user.getUserId())
                .facilityId(facilityId)
                .start(start)
                .end(end)
                .build();

        Exception exception = assertThrows(MaxReservationsExceededException.class, () -> reservationService.addReservation(reservationDto));

        assertEquals("User cannot have more than 2 active reservations.", exception.getMessage());
        verify(reservationDao, never()).persist(any(Reservation.class));
    }

    @Test
    public void testAddReservation_DateBeyond7Days() {
        start = LocalDateTime.now().plusDays(8).withHour(10);
        end = start.plusHours(2);

        when(userDao.find(user.getUserId())).thenReturn(user);
        when(facilityDao.find(facilityId)).thenReturn(facility);

        ReservationDto reservationDto = ReservationDto.builder()
                .userId(user.getUserId())
                .facilityId(facilityId)
                .start(start)
                .end(end)
                .build();

        Exception exception = assertThrows(InvalidReservationException.class, () -> reservationService.addReservation(reservationDto));

        assertEquals("Reservations can only be made up to 7 days in advance.", exception.getMessage());
        verify(reservationDao, never()).persist(any(Reservation.class));
    }

    @Test
    public void testAddReservation_DurationExceedsLimit() {
        end = start.plusHours(9);

        when(userDao.find(user.getUserId())).thenReturn(user);
        when(facilityDao.find(facilityId)).thenReturn(facility);
        // when(reservationDao.findActiveReservationsByUserId(user.getUserID())).thenReturn(Collections.emptyList());
        // when(reservationDao.findReservationsByFacilityAndDateRange(eq(facilityId), any(), any())).thenReturn(Collections.emptyList());

        ReservationDto reservationDto = ReservationDto.builder()
                .userId(user.getUserId())
                .facilityId(facilityId)
                .start(start)
                .end(end)
                .build();

        Exception exception = assertThrows(InvalidReservationException.class, () -> reservationService.addReservation(reservationDto));

        assertEquals("Reservation duration cannot exceed 8 hours.", exception.getMessage());
        verify(reservationDao, never()).persist(any(Reservation.class));
    }

    @Test
    public void testAddReservation_InThePast() {
        start = LocalDateTime.now().minusDays(1).withHour(10);
        end = start.plusHours(2);

        when(userDao.find(user.getUserId())).thenReturn(user);
        when(facilityDao.find(facilityId)).thenReturn(facility);

        ReservationDto reservationDto = ReservationDto.builder()
                .userId(user.getUserId())
                .facilityId(facilityId)
                .start(start)
                .end(end)
                .build();

        Exception exception = assertThrows(InvalidReservationException.class, () -> reservationService.addReservation(reservationDto));

        assertEquals("Start time cannot be in the past.", exception.getMessage());

        verify(reservationDao, never()).persist(any(Reservation.class));
    }

    @Test
    public void testGetReservationById() {
        int reservationId = 1;
        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setStart(start);
        reservation.setEnd(end);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setFacility(facility);
        reservation.setMembership(membership);

        when(reservationDao.find(reservationId)).thenReturn(reservation);

        ReservationDto fetchedReservation = reservationService.getReservationById(reservationId);

        assertNotNull(fetchedReservation);
        assertEquals(reservationId, fetchedReservation.getId());
        assertEquals(start, fetchedReservation.getStart());
        assertEquals(end, fetchedReservation.getEnd());
        assertEquals(ReservationStatus.ACTIVE, fetchedReservation.getStatus());
    }

    @Test
    public void testUpdateReservation() {
        int reservationId = 1;

        when(userDao.find(user.getUserId())).thenReturn(user);
        when(facilityDao.find(facilityId)).thenReturn(facility);

        Reservation existingReservation = new Reservation();
        existingReservation.setReservationId(reservationId);
        existingReservation.setStart(start);
        existingReservation.setEnd(end);
        existingReservation.setFacility(facility);
        existingReservation.setMembership(membership);
        existingReservation.setStatus(ReservationStatus.ACTIVE);

        when(reservationDao.find(reservationId)).thenReturn(existingReservation);
        when(reservationDao.findReservationsByFacilityAndDateRange(eq(facilityId), any(), any())).thenReturn(Collections.emptyList());

        LocalDateTime newStart = start.plusHours(1);
        LocalDateTime newEnd = end.plusHours(1);

        ReservationDto reservationDto = ReservationDto.builder()
                .id(reservationId)
                .userId(user.getUserId())
                .facilityId(facilityId)
                .start(newStart)
                .end(newEnd)
                .build();

        ReservationDto updatedReservation = reservationService.updateReservation(reservationDto);

        assertEquals(newStart, updatedReservation.getStart());
        assertEquals(newEnd, updatedReservation.getEnd());

        verify(reservationDao, times(1)).update(any(Reservation.class));
    }

    @Test
    public void testRemoveExistingReservation() {
        int reservationId = 1;
        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setMembership(membership);

        when(reservationDao.find(reservationId)).thenReturn(reservation);

        assertDoesNotThrow(() -> reservationService.removeReservation(reservationId));

        verify(reservationDao, times(1)).remove(reservation);
    }
}