package cz.cvut.fel.ear.semestralka.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@Table(name = "admin")
@NoArgsConstructor
@AllArgsConstructor

public class Admin extends User {

    @ManyToMany
    @JoinTable(
            name = "admin_facilities",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private List<Facility> facilities;

    @ManyToMany
    @JoinTable(
            name = "admin_memberships",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "membership_id")
    )
    private List<Membership> memberships;
}
