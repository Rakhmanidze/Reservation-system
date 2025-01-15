package cz.cvut.fel.ear.semestralka.dao;

import cz.cvut.fel.ear.semestralka.model.Reservation;
import cz.cvut.fel.ear.semestralka.model.enums.ReservationStatus;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Repository
public class ReservationDao extends BaseDao<Reservation> {
    public ReservationDao() {super(Reservation.class);}

    public List<Reservation> findActiveExpiredReservations(ReservationStatus status, LocalDateTime currentTime) {
        Objects.requireNonNull(status, "Status must not be null");
        Objects.requireNonNull(currentTime, "Current time must not be null");
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findActiveExpiredReservations", Reservation.class);
        query.setParameter("status", status);
        query.setParameter("currentTime", currentTime);
        return query.getResultList();
    }

    public List<Reservation> findActiveReservationsByUserId(int userId) {
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findActiveReservationsByUserId", Reservation.class);
        query.setParameter("userId", userId);
        query.setParameter("status", ReservationStatus.ACTIVE);
        return query.getResultList();
    }

    public List<Reservation> findReservationsByFacilityAndDateRange(int facilityId, LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(start, "Start date must not be null");
        Objects.requireNonNull(end, "End date must not be null");

        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findReservationsByFacilityAndDateRange", Reservation.class);
        query.setParameter("facilityId", facilityId);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

}
