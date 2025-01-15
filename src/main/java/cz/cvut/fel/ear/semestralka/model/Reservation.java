package cz.cvut.fel.ear.semestralka.model;

import cz.cvut.fel.ear.semestralka.model.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@Table(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(
                name = "Reservation.findActiveReservationsByUserId",
                query = "SELECT r FROM Reservation r JOIN r.membership.user u WHERE u.userId = :userId AND r.status = :status"
        ),
        @NamedQuery(
                name = "Reservation.findReservationsByFacilityAndDateRange",
                query = "SELECT r FROM Reservation r WHERE r.facility.facilityId = :facilityId AND " +
                        "((r.start < :end) AND (r.end > :start))"
        ),
        @NamedQuery(
                name = "Reservation.findActiveExpiredReservations",
                query = "SELECT r FROM Reservation r WHERE r.status = :status AND r.end < :currentTime"
        )
})

public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reservationId;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @ManyToOne
    @JoinColumn(name = "facilityId", nullable = true)
    private Facility facility;

    @ManyToOne
    @JoinColumn(name = "membershipId", nullable = false)
    private Membership membership;
}
