package cz.cvut.fel.ear.semestralka.model;

import cz.cvut.fel.ear.semestralka.model.enums.MembershipStatus;
import cz.cvut.fel.ear.semestralka.model.enums.TypeOfMembership;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "membership")
@NamedQueries({
        @NamedQuery(name = "Membership.findAllActive",
                query = "SELECT m FROM Membership m WHERE m.status = :status")
})
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Integer membershipId;

    @Column(name = "start_date")
    private LocalDate start;

    @Column(name = "end_date")
    private LocalDate end;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MembershipStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeOfMembership type;

    @ManyToMany
    @JoinTable(
            name = "membership_payments",
            joinColumns = @JoinColumn(name = "membership_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_id")
    )
    @OrderBy("amount DESC")
    private List<Payment> payments;

    @OneToOne(mappedBy = "membership")
    private User user;

    @ManyToMany(mappedBy = "memberships")
    private List<Admin> admins;

    @OneToMany(mappedBy = "membership")
    private List<Reservation> reservations;
}