package cz.cvut.fel.ear.semestralka.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "payment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "amount")
    private int amount;

    @Column(name = "date")
    private LocalDate date;

    @ManyToMany(mappedBy = "payments")
    private List<Membership> memberships;
}
