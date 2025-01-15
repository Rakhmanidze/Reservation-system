package cz.cvut.fel.ear.semestralka.model;

import cz.cvut.fel.ear.semestralka.model.enums.TypeOfMembership;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "facility")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_id")
    private Integer facilityId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_membership_type", nullable = false)
    private TypeOfMembership requiredMembershipType;

    @ManyToMany(mappedBy = "facilities")
    private List<Admin> admins;
}
